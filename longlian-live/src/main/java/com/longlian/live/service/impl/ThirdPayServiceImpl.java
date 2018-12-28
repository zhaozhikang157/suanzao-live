package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.pay.WeiXinOrder;
import com.huaxin.util.weixin.type.WechatTradeStateType;
import com.longlian.dto.AccountAddDelReturn;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.CourseRelayMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Administrator on 2016/6/16.
 */
@Service("thirdPayService")
public class ThirdPayServiceImpl implements ThirdPayService {

    private static Logger log = LoggerFactory.getLogger(ThirdPayServiceImpl.class);

    private final Long unit = 1024l;


    @Value("${website}")
    private String website;
    @Autowired
    WeiXinService weiXinService;
    @Autowired
    AlipayService alipayService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    OrdersService ordersService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    AccountService accountService;
    @Autowired
    LlAccountService llAccountService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    CourseService courseService;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    IosPayTypeService iosPayTypeService;
    @Autowired
    UserRewardTypeService userRewardTypeService;
    @Autowired
    UserRewardRecordService userRewardRecordService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    InviCodeItemService inviCodeItemService;
    @Autowired
    InviCodeService inviCodeService;
    @Autowired
    DataChargeLevelService dataChargeLevelService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    DataChargeRecordService dataChargeRecordService;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    RelayIncomeService relayIncomeService;
    @Autowired
    CourseRelayMapper courseRelayMapper;


    /**
     * 微信支付 H5网页支付
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinH5Pay (AppUserIdentity appUserIdentity , String ip  , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id = CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");//微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //购买课程的
            course = courseService.getCourseFromRedis(thirdPayDto.getCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getChargeAmt();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId() , "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }
        //创建订单
        Orders orders = ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin_h5.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0, thirdPayDto.getPayTypeId(), "微信端网页购买学币");
        orders.setIosPayType(thirdPayDto.getIosPayType());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("龙链网页微信支付");
        wei.setOpenid(appUserIdentity.getOpenid());
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(amount + "");
        resultDto =  weiXinService.unifiedorderH5(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        if(map_data == null){
            resultDto.setCode(ReturnMessageType.weixin_h5_pay_openid_not_match.getCode());
            resultDto.setCode(resultDto.getMessage());
            return  resultDto;
        }
        String packagecDesc = map_data.get("package");
        String prepayid = packagecDesc.split("=")[1];
        orders.setTranNo(prepayid);
        ordersService.create(orders);
        resultDto.setExt(orders.getId());
        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理报名记录
            JoinCourseRecord joinCourseRecord = handlerBuyCourseJoinCourseRecord(appUserIdentity, thirdPayDto, resultDto , course);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }
        return  resultDto;
    }
    /**
     * --微信支付 H5网页支付
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinH5PayBuyReplay(AppUserIdentity appUserIdentity, String ip, ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id = CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");//微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        CourseRelayDto courseRelayDto = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //转播信息
            courseRelayDto = courseRelayService.queryById(thirdPayDto.getCourseId());
            if(courseRelayDto == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            course = courseService.getCourse(courseRelayDto.getOriCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getChargeAmt();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId() , "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }

        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理报名记录
            CourseRelayDto courseRelayDtoVo = courseRelayMapper.queryByAppidAndOriCourseId(courseRelayDto.getAppId(), String.valueOf(courseRelayDto.getOriCourseId()));
            handlerBuyCourseJoinCourseRecord(appUserIdentity, thirdPayDto, resultDto , courseRelayDtoVo);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }

        //创建订单
        Orders orders = ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin_h5.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0, thirdPayDto.getPayTypeId(), "微信端网页购买学币");
        orders.setIosPayType(thirdPayDto.getIosPayType());
        orders.setIosPayTypeId(courseRelayDto.getId());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("龙链网页微信支付");
        wei.setOpenid(appUserIdentity.getOpenid());
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(amount + "");
        resultDto =  weiXinService.unifiedRelayorderH5(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        if(map_data == null){
            resultDto.setCode(ReturnMessageType.weixin_h5_pay_openid_not_match.getCode());
            resultDto.setCode(resultDto.getMessage());
            return  resultDto;
        }
        String packagecDesc = map_data.get("package");
        String prepayid = packagecDesc.split("=")[1];
        orders.setTranNo(prepayid);
        orders.setCourseType(1);//设置课程类型为购买转播课
        ordersService.create(orders);
        resultDto.setExt(orders.getId());
        return  resultDto;
    }

    /**
     * 微信支付 转播课程 H5网页支付
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinH5PayReplay (AppUserIdentity appUserIdentity , String ip  , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id = CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");//微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //购买课程的
            course = courseService.getCourseFromRedis(thirdPayDto.getCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getRelayCharge();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId() , "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }
        //创建订单
        Orders orders = ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin_h5.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0, thirdPayDto.getPayTypeId(), "微信端网页购买学币",1);
        orders.setIosPayType(thirdPayDto.getIosPayType());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("龙链网页微信支付");
        wei.setOpenid(appUserIdentity.getOpenid());
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(amount + "");
        resultDto =  weiXinService.unifiedorderH5(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        if(map_data == null){
            resultDto.setCode(ReturnMessageType.weixin_h5_pay_openid_not_match.getCode());
            resultDto.setMessage(resultDto.getMessage());
            return  resultDto;
        }
        String packagecDesc = map_data.get("package");
        String prepayid = packagecDesc.split("=")[1];
        orders.setTranNo(prepayid);
        ordersService.create(orders);
        resultDto.setExt(orders.getId());
        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理转播记录
            handlerBuyCourseRelayRecord(appUserIdentity, thirdPayDto, resultDto , course);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }
        return  resultDto;
    }

    /**
     * app微信支付
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinPay (AppUserIdentity appUserIdentity , String ip  , ThirdPayDto thirdPayDto) throws Exception {
        System.out.println("weixinPay微信支付>>>>>>>>>>"+thirdPayDto.getIsBuy()+","+thirdPayDto.getInvitationAppId()+","+thirdPayDto.getPayType());
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id= CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");//app微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //购买课程的
            course = courseService.getCourseFromRedis(thirdPayDto.getCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getChargeAmt();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId(), "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }

        //创建订单
        Orders orders =  ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0,thirdPayDto.getPayTypeId(), "酸枣APP微信端购买学币");
        orders.setIosPayType(thirdPayDto.getIosPayType());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("微信APP支付");
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(String.valueOf(amount.doubleValue()));
        resultDto =  weiXinService.unifiedorder(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())) {
        	resultDto.setCode(ReturnMessageType.weixin_pay_fail.getCode());
            resultDto.setCode(ReturnMessageType.weixin_pay_fail.getMessage());
            return  resultDto;
        }
        //log.error("调取微信支付生成预支付单返回信息：【"+String.valueOf(amount.intValue())+",】"+ JsonUtil.toJsonString(resultDto));
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        String prepayid = map_data.get("prepayid");
        orders.setTranNo(prepayid);
        ordersService.create(orders);
        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理报名记录
            JoinCourseRecord joinCourseRecord = handlerBuyCourseJoinCourseRecord(appUserIdentity, thirdPayDto, resultDto , course);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }
        return  resultDto;
    }

    /**
     * app微信支付（转播购买）
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinPayRelayBuy(AppUserIdentity appUserIdentity, String ip, ThirdPayDto thirdPayDto) throws Exception {
        System.out.println("weixinPay微信支付>>>>>>>>>>"+thirdPayDto.getIsBuy()+","+thirdPayDto.getInvitationAppId()+","+thirdPayDto.getPayType());
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id= CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");//app微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        CourseRelayDto courseRelayDto = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //转播课程信息
            courseRelayDto = courseRelayService.queryById(thirdPayDto.getCourseId());
            //courseRelayDto = courseRelayService.getCourseRelayFromRedis(thirdPayDto.getCourseId());
            if(courseRelayDto == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            //原课程表
            course = courseService.getCourse(courseRelayDto.getOriCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getChargeAmt();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId(), "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }


        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理报名记录
            CourseRelayDto courseRelayDtoVo = courseRelayMapper.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
            JoinCourseRecord joinCourseRecord = handlerBuyCourseJoinCourseRecord(appUserIdentity, thirdPayDto, resultDto, courseRelayDtoVo);
            //报名记录后是否需要把报名id设置到订单中===========？
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }

        //创建订单
        Orders orders =  ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0,thirdPayDto.getPayTypeId(), "酸枣APP微信端购买学币");
        orders.setIosPayType(thirdPayDto.getIosPayType());
        orders.setIosPayTypeId(courseRelayDto.getId());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("微信APP支付");
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee("0.1");
        resultDto =  weiXinService.unifiedRelayorder(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        //log.error("调取微信支付生成预支付单返回信息：【"+String.valueOf(amount.intValue())+",】"+ JsonUtil.toJsonString(resultDto));
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        String prepayid = map_data.get("prepayid");
        orders.setTranNo(prepayid);
        orders.setCourseType(1);//订单类型为转播课购买
        ordersService.create(orders);

        return  resultDto;
    }

    public static void main(String args[]){
        ActResultDto resultDto = new ActResultDto();
        resultDto.setCode("0000");
        resultDto.setMessage("aa");
        Map map = new HashMap();
        map.put("appid", "1111");
        map.put("noncestr", "dddd");
        map.put("prepayid", "123");
        map.put("partnerid", "456");
        map.put("package", "Sign=WXPay");
        resultDto.setData(map);
        System.out.println(JsonUtil.toJsonString(resultDto));
    }

    /**
     * app微信支付  转播课程
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto weixinPayRelay (AppUserIdentity appUserIdentity , String ip  , ThirdPayDto thirdPayDto) throws Exception {
        log.info("weixinPay微信支付>>>>>>>>>>"+thirdPayDto.getIsBuy()+","+thirdPayDto.getInvitationAppId()+","+thirdPayDto.getPayType());
        ActResultDto resultDto = new ActResultDto();
        String weixin_mch_id= CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");//app微信商户号
        BigDecimal amount = new BigDecimal(0);
        //购买课程的
        Course course = null;
        if("1".equals(thirdPayDto.getIosPayType())){
            //购买课程的
            course = courseService.getCourse(thirdPayDto.getCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return resultDto;
            }
            thirdPayDto.setPayTypeId(course.getId());
            amount = course.getRelayCharge();
        }else{
            //充值的
            IosPayType iosPayType = iosPayTypeService.findPayInfoById(thirdPayDto.getPayTypeId(), "1");
            if(iosPayType == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
                return  resultDto;
            }
            amount = iosPayType.getAmount();
        }

        //创建订单
        Orders orders =  ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.weixin.getValue(), appUserIdentity.getId(), weixin_mch_id, amount, "", 0,thirdPayDto.getPayTypeId(), "酸枣APP微信端购买学币");
        orders.setIosPayType(thirdPayDto.getIosPayType());
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("微信APP支付");
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(String.valueOf(amount.doubleValue()));
        resultDto =  weiXinService.unifiedorder(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())) {
        	resultDto.setCode(ReturnMessageType.weixin_pay_fail.getCode());
            resultDto.setCode(ReturnMessageType.weixin_pay_fail.getMessage());
            return  resultDto;
        }
        //log.error("调取微信支付生成预支付单返回信息：【"+String.valueOf(amount.intValue())+",】"+ JsonUtil.toJsonString(resultDto));
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        String prepayid = map_data.get("prepayid");
        orders.setTranNo(prepayid);
        orders.setCourseType(1);
        ordersService.create(orders);
        if("1".equals(thirdPayDto.getIosPayType())){//购买课程
            //处理转播记录
            resultDto = handlerBuyCourseRelayRecord(appUserIdentity, thirdPayDto, resultDto, course);
            if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode())
                    || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(resultDto.getCode()) && "return".equals(resultDto.getExt()))
                    ) return  resultDto;

        }
        return  resultDto;
    }


    /**
     * 处理报名记录 --学币购买的
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @param joinType  类型'0-正常 1-场控人员2-管理员，详见JoinCourseRecordType';
     * @return
     * @throws Exception
     */
    @Override
    public  JoinCourseRecord handlerJoinCourseRecord(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId)throws  Exception{
        if(roomId <= 0){
            Course course = courseService.getCourseFromRedis(thirdPayDto.getCourseId());
            if(course == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return  null;
            }else{
                BigDecimal chargeAmt =  course.getChargeAmt() == null ? new BigDecimal(0) :   course.getChargeAmt();
                thirdPayDto.setAmount(chargeAmt);
                roomId = course.getRoomId();
            }
        }
        return  joinCourseRecordService.handlerJoinCourseRecord(appUserIdentity , thirdPayDto ,resultDto ,joinType, roomId);
    }
    /**
     * 处理报名记录 --学币购买的 购买转播课
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @param joinType  类型'0-正常 1-场控人员2-管理员，详见JoinCourseRecordType';
     * @return
     * @throws Exception
     */
    @Override
    public  JoinCourseRecord handlerJoinCourseRecordRelay(AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto ,    ActResultDto resultDto , String  joinType , long roomId)throws  Exception{
        if(roomId <= 0){
            CourseRelayDto courseRelayDto = courseRelayService.getCourseRelayFromRedis(thirdPayDto.getCourseId());
            CourseRelayDto courseRelayDtoAndLiveRoom = courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
            //原课程表
            Course course = courseService.getCourse(courseRelayDto.getOriCourseId());
            if(courseRelayDto == null || courseRelayDtoAndLiveRoom == null){
                resultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                resultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return  null;
            }else{
                BigDecimal chargeAmt =  course.getChargeAmt() == null ? new BigDecimal(0) :   course.getChargeAmt();
                thirdPayDto.setAmount(chargeAmt);
                roomId = courseRelayDtoAndLiveRoom.getRoomId();
            }
        }
        return  joinCourseRecordService.handlerJoinCourseRecordRelay(appUserIdentity , thirdPayDto ,resultDto ,joinType, roomId);
    }

    /**
     * 处理报名记录 - 第三方购买课程
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @author qym
     * @return
     * @throws Exception
     */
    public  JoinCourseRecord handlerBuyCourseJoinCourseRecord(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto ,  ActResultDto resultDto ,Course course )throws  Exception{
        System.out.println("处理报名记录 - 第三方购买课程>>>>>>>>>>");
        log.debug("处理报名记录 - 第三方购买课程>>>>>>>>>>");
        //处理报名记录
        // JoinCourseRecord joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseId(appUserIdentity.getId(), thirdPayDto.getCourseId());
        JoinCourseRecord joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseIdByRedis(appUserIdentity.getId(), thirdPayDto.getCourseId(), true);
        if(joinCourseRecord == null || "-1".equals(joinCourseRecord.getSignUpStatus())){
            joinCourseRecord = new JoinCourseRecord();
            joinCourseRecord.setAppId(appUserIdentity.getId());
            joinCourseRecord.setCourseId(thirdPayDto.getCourseId());
            //课程查找直播间ID
            joinCourseRecord.setRoomId(course.getRoomId());
            joinCourseRecord.setIsFree("0");
            //判断是否是第一次报名
            joinCourseRecord.setIsFirst("1");
            joinCourseRecord.setSignUpStatus("0");
            joinCourseRecord.setInvitationUserId(thirdPayDto.getInvitationAppId());
            boolean isFirst = joinCourseRecordService.getFirstJoinByAppId(appUserIdentity.getId());
            if(isFirst)  joinCourseRecord.setIsFirst("0");
            //创建报名记录
            joinCourseRecordService.insert(joinCourseRecord);
            redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "0");
        }else {
            if("0".equals(joinCourseRecord.getSignUpStatus())){     //提示已经支付中...
                joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseId(appUserIdentity.getId(), thirdPayDto.getCourseId());
                if(  PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())){//微信支付
                    //先去微信查询是否失败
                    WeiXinOrder wx_order = new WeiXinOrder();
                    Orders orders = ordersService.getOrderByJoinCourseRecordId(joinCourseRecord.getId());
                    if(orders != null){
                        //微信直接购买课程
                        wx_order.setOut_trade_no(orders.getOrderNo());
                        resultDto = weiXinService.orderquery(wx_order);
                        if(resultDto.getData() != null){
                            Map map = (Map)resultDto.getData();
                            String trade_state = map.get("trade_state").toString();
                            if(WechatTradeStateType.SUCCESS.getValue().equals(trade_state)){
                                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                                resultDto.setExt("return");
                                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
                                return  joinCourseRecord;
                            }
                            if(WechatTradeStateType.USERPAYING.getValue().equals(trade_state)){
                                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAIDING.getCode());
                                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAIDING.getMessage());
                                resultDto.setExt("return");
                                return  joinCourseRecord;
                            }
                        }
                    }
                }
            }else  if("1".equals(joinCourseRecord.getSignUpStatus())){
                //提示已经支付
                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                resultDto.setExt("return");
                return  joinCourseRecord;
            }else if("2".equals(joinCourseRecord.getSignUpStatus())){
                joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseId(appUserIdentity.getId(), thirdPayDto.getCourseId());
                joinCourseRecordService.updateSignUpStatus(joinCourseRecord.getId() , "0" ,thirdPayDto.getInvitationAppId());//直接更新支付中
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "0");
            }
        }
        return  joinCourseRecord;
    }

    /**
     * 处理 转播 功能
     * @param appUserIdentity
     * @param thirdPayDto
     * @param resultDto
     * @return
     * @throws Exception
     */
    public  ActResultDto handlerBuyCourseRelayRecord(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto ,  ActResultDto resultDto ,Course course )throws  Exception{
        System.out.println("处理报名记录 - 第三方购买课程>>>>>>>>>>");
        log.debug("处理转播记录 - 第三方转播课程>>>>>>>>>>");

        CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(appUserIdentity.getId(), String.valueOf(course.getId()));

        if(courseRelayDto == null ){
            return resultDto;
        }else {
            if(  PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())){//微信支付
                //先去微信查询是否失败
                WeiXinOrder wx_order = new WeiXinOrder();
                Orders orders = ordersService.getOrderByJoinCourseRecordId(courseRelayDto.getId());
                if(orders != null){
                    //微信直接购买课程
                    wx_order.setOut_trade_no(orders.getOrderNo());
                    resultDto = weiXinService.orderquery(wx_order);
                    if(resultDto.getData() != null){
                        Map map = (Map)resultDto.getData();
                        String trade_state = map.get("trade_state").toString();
                        if(WechatTradeStateType.SUCCESS.getValue().equals(trade_state)){
                            resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                            resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                            resultDto.setExt("return");
                            redisUtil.hset(RedisKey.ll_live_join_relay_course_record_pre + course.getId(), appUserIdentity.getId() + "", "1");
                            return  resultDto;
                        }
                        if(WechatTradeStateType.USERPAYING.getValue().equals(trade_state)){
                            resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAIDING.getCode());
                            resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAIDING.getMessage());
                            resultDto.setExt("return");
                            return  resultDto;
                        }
                    }
                }
            }
            /*else  if("1".equals(joinCourseRecord.getSignUpStatus())){
                //提示已经支付
                resultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                resultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                resultDto.setExt("return");
                return  joinCourseRecord;
            }else if("2".equals(joinCourseRecord.getSignUpStatus())){
                joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseId(appUserIdentity.getId(), thirdPayDto.getCourseId());
                joinCourseRecordService.updateSignUpStatus(joinCourseRecord.getId() , "0" ,thirdPayDto.getInvitationAppId());//直接更新支付中
                redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "0");
            }*/
        }
        return  resultDto;
    }


    @Override
    public  ActResultDto  thirdPay(HttpServletRequest request , String thirdOrderNo) throws Exception{
        ActResultDto resultDto = new ActResultDto();
        //先去微信查询是否失败
        WeiXinOrder wx_order = new WeiXinOrder();
        //微信直接购买课程
        wx_order.setOut_trade_no(thirdOrderNo);
        resultDto = weiXinService.orderquery(wx_order);
        return  resultDto;
    }

    /**
     * 发送消息
     * @param appUserIdentity
     * @param joinCourseRecord
     *//**
    public  void sendMessage(AppUserIdentity appUserIdentity , JoinCourseRecord  joinCourseRecord  , BigDecimal  bigDecimal){
        Course course = courseService.getCourse(joinCourseRecord.getCourseId());
        //将报名的人写入redis
        joinCourseRecordService.addJoinUser2Redis(joinCourseRecord.getCourseId(), joinCourseRecord.getAppId(), new Date());
        //提醒学员
        sendMsgService.sendMsg(joinCourseRecord.getAppId(), MsgType.BUY_COURSE_SUCCESS.getType(), joinCourseRecord.getId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, course.getLiveTopic(), bigDecimal.toString()), "");
        //提醒老师,免费不用发送
        //sendMsgService.sendMsg(course.getAppId(), MsgType.PAY_REMIND.getType(), joinCourseRecord.getId() ,MsgConst.NEW_PAY_REMIND_TEACHER_CONTENT , "");
        //自动关注老师
        userFollowService.follow(appUserIdentity.getId(), course.getRoomId());
    } */

    /**
     * 钱包 学币  支付
     * @param appUserIdentity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto moneybagLearnCoinPay (AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        if(thirdPayDto.getCourseId() <= 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        Course course = courseService.getCourse(thirdPayDto.getCourseId());
        if(course == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        BigDecimal chargeAmt =  course.getChargeAmt() == null ? new BigDecimal(0) :   course.getChargeAmt();
        thirdPayDto.setAmount(chargeAmt);
        //判断钱包是否足够
        LlAccount account = null;
        Account zbAccount = null;
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) > 0 ) {
            //判断钱包是否足够
            account = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            if (account == null) {
                actResultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
                return actResultDto;
            }
            if (thirdPayDto.getAmount().compareTo(account.getBalance()) > 0) {
                //处理并发
                String signUpStatusStr = redisUtil.hget(RedisKey.ll_live_join_course_record_pre + course.getId(), appUserIdentity.getId() + "");
                if( "1".equals(signUpStatusStr)){
                    //提示已经支付
                    actResultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                    actResultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                    return  actResultDto;
                }
                //如果学币不足，则看 学币+枣币 够不够
                zbAccount = accountService.getAccountByAppId(appUserIdentity.getId());
                BigDecimal pay = zbAccount.getBalance().add(account.getBalance());
                if(pay.compareTo(thirdPayDto.getAmount()) >= 0){
                    BigDecimal needToTransfor = thirdPayDto.getAmount().subtract(account.getBalance());
                    actResultDto.setAttach(needToTransfor);
                    actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getCode());
                    actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getMessage());
                    return actResultDto;
                }
                actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
                actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
                return actResultDto;
            } else {
                if (!"1".equals(thirdPayDto.getIsBuy())) {//不是购买
                    actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getCode());
                    actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getMessage());
                    actResultDto.setData(account);
                    return actResultDto;
                }
            }
        }
        //处理报名记录
        JoinCourseRecord joinCourseRecord = handlerJoinCourseRecord(appUserIdentity, thirdPayDto, actResultDto ,JoinCourseRecordType.general.getValue() ,  course.getRoomId());
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                ) return  actResultDto;
        //创建订单
        String merId= appUserIdentity.getId() + "";//商户号
        Orders orders =   ordersService.setOrders(OrderType.buyCourse.getValue(), PayType.learn_coin_pay.getValue(), appUserIdentity.getId(), merId, thirdPayDto.getAmount(), "", joinCourseRecord.getId(), thirdPayDto.getPayTypeId(), "");
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        orders.setCourseType(1);//购买转播课程
        ordersService.create(orders);
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) <= 0 ){
            String cAct = website + "/weixin/courseInfo?id="+course.getId();
            ordersService.sendMsgAndFollow2(orders ,course , cAct); //发送短信 -- 钱包购买课程
            return actResultDto;
        }
        //扣除钱包余额
        LlAccountTrack accountTrack = new LlAccountTrack();
        accountTrack.setAmount(thirdPayDto.getAmount());
        accountTrack.setOrderId(orders.getId());
        accountTrack.setReturnMoneyLevel(LlAccountFromType.buy_course.getValue());
        llAccountService.delAccountBalance(appUserIdentity.getId(), thirdPayDto.getAmount(), accountTrack, account);
        ordersService.handlerBuyCourse(orders , thirdPayDto.getInvitationAppId());// 处理返钱 购买课程
        actResultDto.setExt(orders);
        if ("1".equals(thirdPayDto.getIsBuy())) {//购买课程则返回学币余额
            LlAccount accounts = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            BigDecimal balance = accounts.getBalance();
            String remark = "0";
            if(balance != null) {
                remark =  balance.toString();
            }
            accounts.setRemark(remark);
            actResultDto.setData(accounts);
        }
        return  actResultDto;
    }


    /**
     * 钱包 学币  支付  课程转播
     * @param appUserIdentity
     * @return
     * @throws Exception
     * @auth zhikang.zhao
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto moneybagLearnCoinPayByRelay (AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        if(thirdPayDto.getCourseId() <= 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        Course course = courseService.getCourse(thirdPayDto.getCourseId());
        if(course == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        BigDecimal chargeAmt =  course.getRelayCharge() == null ? new BigDecimal(0) :   course.getRelayCharge();
        thirdPayDto.setAmount(chargeAmt);
        //判断钱包是否足够
        LlAccount account = null;
        Account zbAccount = null;
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) > 0 ) {
            //判断钱包是否足够
            account = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            if (account == null) {
                actResultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
                return actResultDto;
            }
            if (thirdPayDto.getAmount().compareTo(account.getBalance()) > 0) {
                //处理并发
                String signUpStatusStr = redisUtil.hget(RedisKey.ll_live_join_relay_course_record_pre + course.getId(), appUserIdentity.getId() + "");
                if( "1".equals(signUpStatusStr)){
                    //提示已经支付
                    actResultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                    actResultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                    return  actResultDto;
                }
                //如果学币不足，则看 学币+枣币 够不够
                zbAccount = accountService.getAccountByAppId(appUserIdentity.getId());
                BigDecimal pay = zbAccount.getBalance().add(account.getBalance());
                if(pay.compareTo(thirdPayDto.getAmount()) >= 0){
                    BigDecimal needToTransfor = thirdPayDto.getAmount().subtract(account.getBalance());
                    actResultDto.setAttach(needToTransfor);
                    actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getCode());
                    actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getMessage());
                    return actResultDto;
                }
                actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
                actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
                return actResultDto;
            } else {
                if (!"1".equals(thirdPayDto.getIsBuy())) {//不是购买
                    actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getCode());
                    actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getMessage());
                    actResultDto.setData(account);
                    return actResultDto;
                }
            }
        }
        /* //处理报名记录
        JoinCourseRecord joinCourseRecord = handlerJoinCourseRecord(appUserIdentity, thirdPayDto, actResultDto, JoinCourseRecordType.general.getValue(), course.getRoomId());*/

        //添加记录到转播表
        ActResultDto actResult=courseRelayService.createRelayCourse(appUserIdentity.getId(), String.valueOf(course.getId()),
                String.valueOf(course.getRelayCharge()), String.valueOf(course.getRelayScale()));
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResult.getCode())){
            return  actResult;
        }

        CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(appUserIdentity.getId(), String.valueOf(course.getId()));

        //添加收益表记录
        RelayIncome relayIncome = new RelayIncome();
        relayIncome.setOriAppId(course.getAppId());//原老师（用户）ID
        relayIncome.setCharge(course.getRelayCharge());//金额（转播费用、分成金额）
        relayIncome.setPayAppId(appUserIdentity.getId());//付费人ID（转播者或购买者）
        relayIncome.setRelayScale(new BigDecimal(course.getRelayScale()));//转播比例
        relayIncome.setType("1");//类型：1 转播 2 分成
        relayIncome.setRelCourseId(courseRelayDto.getId());//转播课ID
        relayIncome.setOriCourseId(course.getId());//原课程ID
        relayIncomeService.insert(relayIncome);

        redisUtil.hset(RedisKey.ll_live_join_relay_course_record_pre + course.getId(), appUserIdentity.getId() + "", "1");


        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                ) return  actResultDto;
        //创建订单
        String merId= appUserIdentity.getId() + "";//商户号
        Orders orders =   ordersService.setOrders(OrderType.RELAY_COURSE.getValue(), PayType.learn_coin_pay.getValue(), appUserIdentity.getId(), merId, thirdPayDto.getAmount(), "", courseRelayDto.getId(), thirdPayDto.getPayTypeId(), "",1);
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        ordersService.create(orders);
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) <= 0 ){
            String cAct = website + "/weixin/courseInfo?id="+course.getId();
            ordersService.sendRelayMsg(orders, course ,cAct); //发送短信 -- 钱包转播课程
            return actResultDto;
        }
        //扣除钱包余额
        LlAccountTrack accountTrack = new LlAccountTrack();
        accountTrack.setAmount(thirdPayDto.getAmount());
        accountTrack.setOrderId(orders.getId());
        accountTrack.setReturnMoneyLevel(LlAccountFromType.buy_replay.getValue());
        accountTrack.setCourseType(1);
        llAccountService.delAccountBalance(appUserIdentity.getId(), thirdPayDto.getAmount(), accountTrack, account);
        ordersService.relayCourse(orders, thirdPayDto.getCourseId(), course.getId());// 处理返钱 转播课程
        actResultDto.setExt(orders);
        if ("1".equals(thirdPayDto.getIsBuy())) {//购买课程则返回学币余额
            LlAccount accounts = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            BigDecimal balance = accounts.getBalance();
            String remark = "0";
            if(balance != null) {
                remark =  balance.toString();
            }
            accounts.setRemark(remark);
            actResultDto.setData(accounts);
        }
        return  actResultDto;
    }

    /**
     * --钱包 学币 支付 购买转播课
     * @param appUserIdentity
     * @param thirdPayDto
     * @return
     * @author qym
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto moneybagRelayCoinPay(AppUserIdentity appUserIdentity, ThirdPayDto thirdPayDto,Course course) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        //转播信息
        CourseRelayDto courseRelayDto = courseRelayService.queryById(thirdPayDto.getCourseId());
        if(courseRelayDto == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        //转播表和原课程信息
        CourseRelayDto courseRelayDtoAndLiveRoom = courseRelayMapper.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
        if(null == courseRelayDtoAndLiveRoom){
            actResultDto.setCode(ReturnMessageType.NO_LIVE_ROOM.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_LIVE_ROOM.getMessage());
            return actResultDto;
        }
        //原课程
        BigDecimal chargeAmt =  course.getChargeAmt() == null ? new BigDecimal(0) :   course.getChargeAmt();
        thirdPayDto.setAmount(chargeAmt);
        //判断钱包是否足够
        LlAccount account = null;
        Account zbAccount = null;
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) > 0 ) {
            //判断钱包是否足够
            account = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            if (account == null) {
                actResultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
                return actResultDto;
            }
            if (thirdPayDto.getAmount().compareTo(account.getBalance()) > 0) {
                //处理并发
                String signUpStatusStr = redisUtil.hget(RedisKey.ll_live_join_course_record_pre + thirdPayDto.getCourseId(), appUserIdentity.getId() + "");
                if( "1".equals(signUpStatusStr)){
                    //提示已经支付
                    actResultDto.setCode(ReturnMessageType.COURSE_ORDER_PAID.getCode());
                    actResultDto.setMessage(ReturnMessageType.COURSE_ORDER_PAID.getMessage());
                    return  actResultDto;
                }
                //如果学币不足，则看 学币+枣币 够不够
                zbAccount = accountService.getAccountByAppId(appUserIdentity.getId());
                BigDecimal pay = zbAccount.getBalance().add(account.getBalance());
                if(pay.compareTo(thirdPayDto.getAmount()) >= 0){
                    BigDecimal needToTransfor = thirdPayDto.getAmount().subtract(account.getBalance());
                    actResultDto.setAttach(needToTransfor);
                    actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getCode());
                    actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_TRANSFOR.getMessage());
                    return actResultDto;
                }
                actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
                actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
                return actResultDto;
            } else {
                if (!"1".equals(thirdPayDto.getIsBuy())) {//不是购买
                    actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getCode());
                    actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_SUPPORT.getMessage());
                    actResultDto.setData(account);
                    return actResultDto;
                }
            }
        }

        //处理报名记录
        JoinCourseRecord joinCourseRecord = handlerJoinCourseRecordRelay(appUserIdentity, thirdPayDto, actResultDto ,JoinCourseRecordType.general.getValue() ,  courseRelayDtoAndLiveRoom.getRoomId());
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                ) return  actResultDto;

        //添加收益表记录
        //CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(appUserIdentity.getId(), String.valueOf(course.getId()));
        RelayIncome relayIncome = new RelayIncome();
        //BigDecimal charge1 = new BigDecimal(charge).setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal CHARGE_AMT = new BigDecimal(courseRelayDto.getRelayScale());
        BigDecimal teacherScaleB =  new BigDecimal(100-courseRelayDto.getRelayScale());//给老师的比例数字   CUSTOM_DISTRIBUTION%
        BigDecimal teacherScaleBB =  teacherScaleB.divide(BigDecimal.valueOf(100));//10/100=0.1
        BigDecimal teacherScaleAMT = course.getChargeAmt().multiply(teacherScaleBB);//1*0.1=0.1
        relayIncome.setOriAppId(courseRelayDto.getOriAppId());//原老师（用户）ID
        relayIncome.setCharge(teacherScaleAMT);//金额（分成金额）
        relayIncome.setPayAppId(appUserIdentity.getId());//付费人ID（转播者或购买者）
        relayIncome.setRelayScale(new BigDecimal(100-courseRelayDto.getRelayScale()));//转播比例
        relayIncome.setType("2");//类型：1 转播 2 分成
        relayIncome.setRelCourseId(courseRelayDto.getId());//转播课ID
        relayIncome.setOriCourseId(courseRelayDto.getOriCourseId());//原课程ID
        relayIncomeService.insert(relayIncome);
        //

        //创建订单
        String merId= appUserIdentity.getId() + "";//商户号
        Orders orders =   ordersService.setOrders(OrderType.buyCourse.getValue(), PayType.learn_coin_pay.getValue(), appUserIdentity.getId(), merId, thirdPayDto.getAmount(), "", joinCourseRecord.getId(), thirdPayDto.getPayTypeId(), "");
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        orders.setCourseType(1);
        ordersService.create(orders);
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) <= 0 ){
            ordersService.sendMsgRelayCoinPay(orders ,course,relayIncome,courseRelayDto); //发送短信 -- 钱包购买课程
            return actResultDto;
        }
        //扣除钱包余额
        LlAccountTrack accountTrack = new LlAccountTrack();
        accountTrack.setAmount(thirdPayDto.getAmount());
        accountTrack.setOrderId(orders.getId());
        accountTrack.setReturnMoneyLevel(LlAccountFromType.buy_course.getValue());
        llAccountService.delAccountBalance(appUserIdentity.getId(), thirdPayDto.getAmount(), accountTrack, account);
        ordersService.handlerBuyRelayCourse(orders , thirdPayDto.getInvitationAppId());// 处理返钱 购买课程
        actResultDto.setExt(orders);
        if ("1".equals(thirdPayDto.getIsBuy())) {//购买课程则返回学币余额
            LlAccount accounts = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
            BigDecimal balance = accounts.getBalance();
            String remark = "0";
            if(balance != null) {
                remark =  balance.toString();
            }
            accounts.setRemark(remark);
            actResultDto.setData(accounts);
        }
        return  actResultDto;
    }


    /**
     * 邀请码  支付
     * @param appUserIdentity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto InviCodePay (AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        if(thirdPayDto.getCourseId() <= 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        //判断邀请码是否为空
        if(Utility.isNullorEmpty(thirdPayDto.getExt())){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ERROR.getMessage());
            return actResultDto;
        }
        //判断邀请码是否有效
        Long l = redisUtil.scard(RedisKey.ll_invi_code + thirdPayDto.getCourseId());
        boolean isExtis =  redisUtil.sismember(RedisKey.ll_invi_code + thirdPayDto.getCourseId(), thirdPayDto.getExt());
        if(!isExtis){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ERROR.getMessage());
            return actResultDto;
        }
        //判断是否被使用过，先从数据库查吧，后面课存到reids
        Map map = inviCodeItemService.getItemInfoByInviCodeAndcourseId(thirdPayDto.getExt(), thirdPayDto.getCourseId());
        if(map == null ){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ALREADY_USE.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ALREADY_USE.getMessage());
            return actResultDto;
        }
        if(Long.valueOf(map.get("userAppId").toString())>0){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ALREADY_USE.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ALREADY_USE.getMessage());
            return actResultDto;
        }
        if(map.get("startTime")!=null){
            Date startTime = (Date)map.get("startTime");
            if(startTime.compareTo(new Date()) > 0){
                actResultDto.setCode(ReturnMessageType.INVI_CODE_NOT_USE.getCode());
                actResultDto.setMessage(ReturnMessageType.INVI_CODE_NOT_USE.getMessage());
                return actResultDto;
            }
        }
        if(map.get("endTime")!=null){
            Date endTime = (Date)map.get("endTime");
            if(endTime.compareTo(new Date())< 0 ){
                actResultDto.setCode(ReturnMessageType.NOT_IN_USE_TIME.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_IN_USE_TIME.getMessage());
                return actResultDto;
            }
        }
        Course course = courseService.getCourse(thirdPayDto.getCourseId());
        if(course == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        BigDecimal chargeAmt =  course.getChargeAmt() == null ? new BigDecimal(0) :   course.getChargeAmt();
        thirdPayDto.setAmount(chargeAmt);
        //处理报名记录
        JoinCourseRecord joinCourseRecord = handlerJoinCourseRecord(appUserIdentity, thirdPayDto, actResultDto ,JoinCourseRecordType.general.getValue() ,  course.getRoomId());
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                ) return  actResultDto;
        //创建订单
        String merId= appUserIdentity.getId() + "";//商户号
        Orders orders =   ordersService.setOrders(OrderType.RELAY_COURSE.getValue(), PayType.invi_code.getValue(), appUserIdentity.getId(), merId, thirdPayDto.getAmount(), "", joinCourseRecord.getId(), thirdPayDto.getPayTypeId(), "");
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        ordersService.create(orders);
        String cAct = website + "/weixin/courseInfo?id="+course.getId();
        ordersService.sendMsgAndFollow(orders, course, cAct); //发送短信
        //更新邀请码使用数据
        inviCodeItemService.updateUseAppId(appUserIdentity.getId(), (Long) map.get("id"), new Date(), (Long) map.get("inviCodeId"));
        //去除未抢的邀请码
        redisUtil.lrem(RedisKey.ll_get_no_invi_code_course + map.get("inviCodeId"), map.get("id").toString());
        actResultDto.setExt(orders);
        return  actResultDto;
    }

    /**
     * 邀请码  转播课程支付
     * @param appUserIdentity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto InviCodePayRelay (AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        if(thirdPayDto.getCourseId() <= 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        //判断邀请码是否为空
        if(Utility.isNullorEmpty(thirdPayDto.getExt())){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ERROR.getMessage());
            return actResultDto;
        }
        //判断邀请码是否有效
        Long l = redisUtil.scard(RedisKey.ll_invi_code + thirdPayDto.getCourseId());
        boolean isExtis =  redisUtil.sismember(RedisKey.ll_invi_code + thirdPayDto.getCourseId(), thirdPayDto.getExt());
        if(!isExtis){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ERROR.getMessage());
            return actResultDto;
        }
        //判断是否被使用过，先从数据库查吧，后面课存到reids
        Map map = inviCodeItemService.getItemInfoByInviCodeAndcourseId(thirdPayDto.getExt(), thirdPayDto.getCourseId());
        if(map == null ){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ALREADY_USE.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ALREADY_USE.getMessage());
            return actResultDto;
        }
        if(Long.valueOf(map.get("userAppId").toString())>0){
            actResultDto.setCode(ReturnMessageType.INVI_CODE_ALREADY_USE.getCode());
            actResultDto.setMessage(ReturnMessageType.INVI_CODE_ALREADY_USE.getMessage());
            return actResultDto;
        }
        if(map.get("startTime")!=null){
            Date startTime = (Date)map.get("startTime");
            if(startTime.compareTo(new Date()) > 0){
                actResultDto.setCode(ReturnMessageType.INVI_CODE_NOT_USE.getCode());
                actResultDto.setMessage(ReturnMessageType.INVI_CODE_NOT_USE.getMessage());
                return actResultDto;
            }
        }
        if(map.get("endTime")!=null){
            Date endTime = (Date)map.get("endTime");
            if(endTime.compareTo(new Date())< 0 ){
                actResultDto.setCode(ReturnMessageType.NOT_IN_USE_TIME.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_IN_USE_TIME.getMessage());
                return actResultDto;
            }
        }
        Course course = courseService.getCourse(thirdPayDto.getCourseId());
        if(course == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        BigDecimal chargeAmt =  course.getRelayCharge() == null ? new BigDecimal(0) :   course.getRelayCharge();
        thirdPayDto.setAmount(chargeAmt);
        //处理报名记录
//        JoinCourseRecord joinCourseRecord = handlerJoinCourseRecord(appUserIdentity, thirdPayDto, actResultDto ,JoinCourseRecordType.general.getValue() ,  course.getRoomId());
        redisUtil.hset(RedisKey.ll_live_join_relay_course_record_pre + course.getId(), appUserIdentity.getId() + "", "1");

        CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(appUserIdentity.getId(), String.valueOf(course.getId()));
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                || ( ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                || ( ReturnMessageType.COURSE_ORDER_PAID.getCode().equals(actResultDto.getCode()) && "return".equals(actResultDto.getExt()))
                ) return  actResultDto;
        //创建订单
        String merId= appUserIdentity.getId() + "";//商户号
        Orders orders =   ordersService.setOrders(OrderType.buyCourse.getValue(), PayType.invi_code.getValue(), appUserIdentity.getId(), merId, thirdPayDto.getAmount(), "", courseRelayDto.getId(), thirdPayDto.getPayTypeId(), "",1);
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        ordersService.create(orders);
        String cAct = website + "/weixin/courseInfo?id="+course.getId();
        ordersService.sendRelayMsg(orders, course, cAct); //发送短信


        //更新邀请码使用数据
        inviCodeItemService.updateUseAppId(appUserIdentity.getId(), (Long) map.get("id"), new Date(), (Long) map.get("inviCodeId"));
        //去除未抢的邀请码
        redisUtil.lrem(RedisKey.ll_get_no_invi_code_course + map.get("inviCodeId"), map.get("id").toString());
        actResultDto.setExt(orders);
        return  actResultDto;
    }

    /**
     * IOS内购支付  充值 学币
     * @param appUserIdentity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto iosPay4Recharge (AppUserIdentity appUserIdentity ,ThirdPayDto thirdPayDto ) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        AppUser appUser369 = appUserService.getById(appUserIdentity.getId());
        IosPayType iosPayType = iosPayTypeService.findIosPayInfoByIosCommodityId(thirdPayDto.getIosCommodityId());
        if(iosPayType == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getCode());
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_SUPPORT.getMessage());
            return  actResultDto;
        }
        BigDecimal amount = iosPayType.getAmount();
        BigDecimal llReallyAmount = iosPayType.getLlReallyAmount(); //龙链平台实收
        BigDecimal userReallyAmount = iosPayType.getUserReallyAmount();
        //创建订单
        String merId= CustomizedPropertyConfigurer.getContextProperty("ios_pay_merId");//商户号
        Orders orders =  ordersService.setOrders(OrderType.recharge_learn_coinpay.getValue(), PayType.ios.getValue(), appUser369.getId(), merId, amount, thirdPayDto.getIosOrderId(), 0, thirdPayDto.getPayTypeId(),
                "IOS内购支付,商品名称：" + iosPayType.getIosCommodityName() + ";商品ID:" + iosPayType.getIosCommodityId());
        orders.setSuccessTime(new Date());
        orders.setOptStatus("1");
        if (orders.getRealAmount().compareTo(new BigDecimal(0)) < 0){
            actResultDto.setCode(ReturnMessageType.IOS_PAY_FRAIL.getCode());
            actResultDto.setMessage(ReturnMessageType.IOS_PAY_FRAIL.getMessage());
            return actResultDto;
        }
        //计算手续费和实收
        if(orders.getRealAmount().compareTo(llReallyAmount) < 0){
            actResultDto.setCode(ReturnMessageType.IOS_PAY_FRAIL.getCode());
            actResultDto.setMessage(ReturnMessageType.IOS_PAY_FRAIL.getMessage());
            return actResultDto;
        }
        orders.setCharge(orders.getRealAmount().subtract(llReallyAmount));
        orders.setChargePercent(new BigDecimal(0));
        orders.setLlCharge(orders.getRealAmount().subtract(userReallyAmount));
        orders.setLlChargePercent(new BigDecimal(0));
        //创建订单
        ordersService.create(orders);
        //用户学币账号处理
        LlAccountTrack accountTrack = new LlAccountTrack();
        accountTrack.setFormAccountId(orders.getAppId());
        accountTrack.setAmount(userReallyAmount);
        accountTrack.setReturnMoneyLevel(LlAccountFromType.default_value.getValue());
        accountTrack.setOrderId(orders.getId());
        AccountAddDelReturn accountAddDelReturn = llAccountService.addAccountBalance(appUserIdentity.getId(), userReallyAmount, accountTrack);
        actResultDto.setData(accountAddDelReturn);
        //发送消息
        sendMsgService.sendMsg(appUserIdentity.getId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, userReallyAmount.toString()), "");
        actResultDto.setExt(orders);
        return  actResultDto;
    }



    /**
     * 取消订单
     * @param appUserIdentity
     * @param orderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto cancelThirdPay(AppUserIdentity appUserIdentity, long orderId) {
        // Orders orders = ordersService.getOrderById(orderId);
        //if(orders != null){
        //购买课程
       /* if(OrderType.buyCourse.getValue().equals(orders.getOrderType())){
            //处理报名记录为失败
            joinCourseRecordService.updateSignUpStatus(orders.getJoinCourseId() , "2");
        }*/
        //处理订单取消
        //   ordersService.cancelById(orderId);
        // }
        return null;
    }



    /**
     * 用户打赏
     * @param appUserIdentity
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto userRewardPay (AppUserIdentity appUserIdentity , ThirdPayDto thirdPayDto) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        if(thirdPayDto.getCount() == null )thirdPayDto.setCount(1);
        if(thirdPayDto.getCourseId() <= 0){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        String courseId = String.valueOf(thirdPayDto.getCourseId());
        Course course;
        if(courseId != null && courseId.length() >= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            course=courseService.getRelayCourse(Long.parseLong(courseId));
        }else{
            course = courseService.getCourseFromRedis(Long.valueOf(courseId));
        }
        if(course == null){
            actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
            return actResultDto;
        }
        if(thirdPayDto.getPayTypeId() <= 0){
            actResultDto.setCode(ReturnMessageType.USER_REWARD_TYPE_NOT_NULL.getCode());
            actResultDto.setMessage(ReturnMessageType.USER_REWARD_TYPE_NOT_NULL.getMessage());
            return actResultDto;
        }
        UserRewardType userRewardType = userRewardTypeService.getById(thirdPayDto.getPayTypeId());
        thirdPayDto.setAmount(userRewardType.getAmount().multiply(new BigDecimal(thirdPayDto.getCount())));
        //判断钱包是否足够
        LlAccount account = llAccountService.getIdRowLockByAccountId(appUserIdentity.getId());
        if(account == null ){
            actResultDto.setCode(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getCode());
            actResultDto.setMessage(ReturnMessageType.NOT_EXISTS_MONEYBAG_CODE.getMessage());
            return actResultDto;
        }
        if(thirdPayDto.getAmount().compareTo(account.getBalance()) > 0 ) {
            actResultDto.setCode(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getCode());
            actResultDto.setMessage(ReturnMessageType.MONEYBAG_BALANCE_NOT_ENOUGN.getMessage());
            return actResultDto;
        }
        if(thirdPayDto.getAmount().compareTo(new BigDecimal(0)) > 0 ){
            //创建打赏记录
            UserRewardRecord userRewardRecord = new UserRewardRecord();
            userRewardRecord.setAmount(thirdPayDto.getAmount());
            userRewardRecord.setSingleAmount(userRewardType.getAmount());
            userRewardRecord.setRewardCount(thirdPayDto.getCount());
            userRewardRecord.setAppId(appUserIdentity.getId());
            userRewardRecord.setRewardRewardId(course.getAppId());
            userRewardRecord.setCourseId(thirdPayDto.getCourseId());
            userRewardRecordService.insert(userRewardRecord);
            //扣除账号余额
            LlAccountTrack accountTrack = new LlAccountTrack();
            accountTrack.setAmount(thirdPayDto.getAmount());
            accountTrack.setOrderId(userRewardRecord.getId());
            accountTrack.setReturnMoneyLevel(LlAccountFromType.a_reward.getValue());
            AccountAddDelReturn accountAddDelReturn = llAccountService.delAccountBalance(appUserIdentity.getId(), thirdPayDto.getAmount(), accountTrack, account);
            actResultDto.setData(accountAddDelReturn);
            //给老师钱
            AccountTrack accountTrack2 = new AccountTrack();
            accountTrack2.setFormAccountId(appUserIdentity.getId());
            accountTrack2.setAmount(thirdPayDto.getAmount());
            accountTrack2.setReturnMoneyLevel(AccountFromType.user_reward.getValue());
            accountTrack2.setOrderId(userRewardRecord.getId());
            accountService.addAccountBalance(course.getAppId(), thirdPayDto.getAmount(), accountTrack2);

            //该人的所有枣币
            redisUtil.lpush(RedisKey.ll_course_user_income_queue, course.getAppId() + "," + course.getId() + "," + course.getChatRoomId());
            //聊天室人员
            Map sendMsg = new HashMap();
            sendMsg.put("courseId",  String.valueOf(course.getId() ));
            sendMsg.put("userId", String.valueOf(appUserIdentity.getId() ));
            sendMsg.put("amout", thirdPayDto.getAmount());
            //系列课里的单节课
            if (course.getSeriesCourseId() > 0 ) {
                redisUtil.lpush(RedisKey.ll_course_visit_user_deal , JsonUtil.toJson(sendMsg));
            } else  {
                redisUtil.lpush(RedisKey.ll_course_join_user_deal , JsonUtil.toJson(sendMsg));
            }

        }
        return  actResultDto;
    }

    /**
     * 充值流量
     * @param appUserIdentity
     * @param ip
     * @param levelId
     * @return
     * @throws Exception
     */
    @Override
    public ActResultDto weixinH5BuyFlow(AppUserIdentity appUserIdentity, String ip, Long levelId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        if(levelId == null || levelId < 1){
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getCode());
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getMessage());
            return  resultDto;
        }
        String weixin_mch_id = CustomizedPropertyConfigurer.getContextProperty("wechat.mch_id");//微信商户号
        DataChargeLevel dataChargeLevel = dataChargeLevelService.findModelById(levelId);
        if(dataChargeLevel == null){
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getMessage());
            return  resultDto;
        }
        BigDecimal prefPrice = dataChargeLevel.getPrefPrice();
        //创建订单
        Orders orders = ordersService.setBuyFlowOrder(OrderType.recharge_data.getValue(), PayType.weixin_H5_buy_flow.getValue(), appUserIdentity.getId(),
                weixin_mch_id, prefPrice, "", "微信端充值流量");
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("龙链网页微信支付");
        wei.setOpenid(appUserIdentity.getOpenid());
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(prefPrice + "");
        resultDto =  weiXinService.buyFlowOrderH5(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        if(map_data == null){
            resultDto.setCode(ReturnMessageType.weixin_h5_pay_openid_not_match.getCode());
            resultDto.setMessage(resultDto.getMessage());
            return  resultDto;
        }
        String packagecDesc = map_data.get("package");
        String prepayid = packagecDesc.split("=")[1];
        orders.setTranNo(prepayid);
        ordersService.create(orders);
        //创建充值流量记录
        LiveRoom liveRoom = liveRoomService.findByAppId(appUserIdentity.getId());
        if(liveRoom == null){
            resultDto.setCode(ReturnMessageType.NO_LIVE_ROOM.getCode());
            resultDto.setMessage(ReturnMessageType.NO_LIVE_ROOM.getMessage());
            return resultDto;
        }
        DataChargeRecord record = new DataChargeRecord();
        record.setRoomId(liveRoom.getId());
        record.setTotalAmount(dataChargeLevel.getAmount()*unit*unit*unit);
        record.setBalAmount(dataChargeLevel.getAmount()*unit*unit*unit);
        record.setInvalidDate(dataChargeLevel.getInvalidDate());
        record.setInvalidDateUnit(dataChargeLevel.getInvalidDateUnit());
        record.setUsedAmount(0l);
        record.setStatus("-2");
        record.setOrderId(orders.getId());
        record.setUserId(appUserIdentity.getId());
        record.setUseOriginAmount(0l);
        record.setIsPlatformGift("0");
        record.setLevelId(levelId);
        if("0".equals(dataChargeLevel.getInvalidDate().toString())){
            record.setInvalidRealDate(null);
        } else {
            if("0".equals(dataChargeLevel.getInvalidDateUnit())){       //天
                Date afterTime = DateUtil.getDayHourAfter(new Date(),dataChargeLevel.getInvalidDate() * 24);
                record.setInvalidRealDate(afterTime);
            }else if("1".equals(dataChargeLevel.getInvalidDateUnit())){ //月
                Date afterTime = DateUtil.getDayHourAfter(new Date(), dataChargeLevel.getInvalidDate() * 24 * 30);
                record.setInvalidRealDate(afterTime);
            }else if("2".equals(dataChargeLevel.getInvalidDateUnit())){ //年
                Date afterTime = DateUtil.getDayHourAfter(new Date(),dataChargeLevel.getInvalidDate() * 24 * 30 * 365);
                record.setInvalidRealDate(afterTime);
            }
        }
        record.setOrderTime(new Date());
        dataChargeRecordService.insert(record);
        resultDto.setExt(orders.getId());
        return  resultDto;
    }

    /**
     * app微信支付
     * @param appUserIdentity
     * @param ip
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActResultDto buyFlowApp (AppUserIdentity appUserIdentity , String ip  , Long levelId) throws Exception {
        ActResultDto resultDto = new ActResultDto();
        if(levelId == null || levelId < 1){
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getCode());
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getMessage());
            return  resultDto;
        }
        DataChargeLevel dataChargeLevel = dataChargeLevelService.findModelById(levelId);
        if(dataChargeLevel == null){
            resultDto.setCode(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.GET_DATA_CHANGE_LEVEL_ERROR.getMessage());
            return  resultDto;
        }
        String weixin_mch_id= CustomizedPropertyConfigurer.getContextProperty("app.wechat.mch_id");//app微信商户号
        BigDecimal prefPrice = dataChargeLevel.getPrefPrice();
        //创建订单
        Orders orders = ordersService.setBuyFlowOrder(OrderType.recharge_data.getValue(), PayType.weixin_buy_flow.getValue(), appUserIdentity.getId(),
                weixin_mch_id, prefPrice, "", "酸枣APP充值流量");
        WeiXinOrder wei = new WeiXinOrder();
        wei.setDes("微信APP支付");
        wei.setOut_trade_no(orders.getOrderNo());
        wei.setTotal_fee(prefPrice + "");
        resultDto =  weiXinService.buyFlowOrderApp(wei, ip);  //向微信服务器发送请求，获取预支付订单号
        TreeMap<String, String> map_data = (TreeMap<String, String>) resultDto.getData();
        String prepayid = map_data.get("prepayid");
        orders.setTranNo(prepayid);
        ordersService.create(orders);
        //创建充值流量记录
        LiveRoom liveRoom = liveRoomService.findByAppId(appUserIdentity.getId());
        if(liveRoom == null){
            resultDto.setCode(ReturnMessageType.NO_LIVE_ROOM.getCode());
            resultDto.setMessage(ReturnMessageType.NO_LIVE_ROOM.getMessage());
            return resultDto;
        }
        DataChargeRecord record = new DataChargeRecord();
        record.setRoomId(liveRoom.getId());
        record.setTotalAmount(dataChargeLevel.getAmount()*unit*unit*unit);
        record.setBalAmount(dataChargeLevel.getAmount()*unit*unit*unit);
        record.setInvalidDate(dataChargeLevel.getInvalidDate());
        record.setInvalidDateUnit(dataChargeLevel.getInvalidDateUnit());
        record.setUsedAmount(0l);
        record.setStatus("-2");
        record.setOrderId(orders.getId());
        record.setUserId(appUserIdentity.getId());
        record.setUseOriginAmount(0l);
        record.setIsPlatformGift("0");
        record.setLevelId(levelId);
        if("0".equals(dataChargeLevel.getInvalidDate().toString())){
            record.setInvalidRealDate(null);
        } else {
            if("0".equals(dataChargeLevel.getInvalidDateUnit())){       //天
                Date afterTime = DateUtil.getDayHourAfter(new Date(),dataChargeLevel.getInvalidDate() * 24);
                record.setInvalidRealDate(afterTime);
            }else if("1".equals(dataChargeLevel.getInvalidDateUnit())){ //月
                Date afterTime = DateUtil.getDayHourAfter(new Date(), dataChargeLevel.getInvalidDate() * 24 * 30);
                record.setInvalidRealDate(afterTime);
            }else if("2".equals(dataChargeLevel.getInvalidDateUnit())){ //年
                Date afterTime = DateUtil.getDayHourAfter(new Date(),dataChargeLevel.getInvalidDate() * 24 * 30 * 365);
                record.setInvalidRealDate(afterTime);
            }
        }
        record.setOrderTime(new Date());
        dataChargeRecordService.insert(record);
        resultDto.setExt(orders.getId());
        return  resultDto;
    }

}
