package com.longlian.live.service.impl;

import com.huaxin.util.DateUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.MsgConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.dao.OrdersMapper;
import com.longlian.live.service.*;
import com.longlian.live.util.OrderUtil;
import com.longlian.live.util.SystemParaRedisUtil;
import com.longlian.model.*;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by syl on 2016/8/15.
 */
@Service("ordersService")
public class OrdersServiceImpl implements OrdersService {

    private static Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Value("${website}")
    private String website;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    LlAccountService llAccountService;
    @Autowired
    SystemParaRedisUtil systemParaRedisUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    CourseService courseService;
    @Autowired
    UserDistributionService userDistributionService;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    CountService countService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    IosPayTypeService iosPayTypeService;
    @Autowired
    DataChargeRecordService dataChargeRecordService;

    @Autowired
    DataUseService dataUseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    DataChargeLevelService dataChargeLevelService;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    RelayIncomeService relayIncomeService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    /**
     * 创建订单
     *
     * @param orders
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Orders orders) {
        orders.setCreateTime(new Date());
        if(Utility.isNullorEmpty(orders.getIosPayType())) orders.setIosPayType("0");
        ordersMapper.create(orders);
    }

    /**
     * 统一支付接口
     * 订单第三方支付
     *
     * @param orderNo 订单编号
     * @param payType 支付类型  枚举类PayType
     * @param payType tranNo  第三方支付流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders thirdPay(String orderNo, String payType, String tranNo)throws  Exception{
        Orders orders = getOrderByOrderNo(orderNo);
        if (orders != null && ("0".equals(orders.getOptStatus()) )) {
            //执行更新
            if (!Utility.isNullorEmpty(tranNo)) orders.setTranNo(tranNo);
            //手续费
            BigDecimal appPayPercent = new BigDecimal(0);
            BigDecimal minCharge = new BigDecimal(0);//最少手续费
            if (PayType.alipay.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppAliPayPercent();
            } else if (PayType.weixin.getValue().equals(payType) ) {
                appPayPercent = systemParaRedisUtil.getAppWeixinPayPercent();
            }  else if ( PayType.weixin_h5.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppWeixinH5PayPercent();
            }else if (PayType.unionpay.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppUnionPayPercent();
                minCharge = systemParaRedisUtil.getAPPUnionPayMinCharge();
            }
            BigDecimal ziJinChiCharge = Utility.parseUseTwoPointNumMoney(orders.getRealAmount().multiply(appPayPercent));
            if (PayType.unionpay.getValue().equals(payType) && ziJinChiCharge.compareTo(minCharge) < 0) {
                ziJinChiCharge = minCharge;
            }
            orders.setCharge(ziJinChiCharge);
            orders.setChargePercent(appPayPercent);
            orders.setLlCharge(new BigDecimal(0));
            orders.setLlChargePercent(new BigDecimal(0));
            IosPayType iosPayType = null;
            if(OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) && !"1".equals(orders.getIosPayType())) {//充值学币
                if(PayType.ios.getValue().equals(orders.getBankType())){
                    iosPayType = iosPayTypeService.findPayInfoById(orders.getIosPayTypeId(), "0");
                }else{
                    iosPayType = iosPayTypeService.findPayInfoById(orders.getIosPayTypeId(), "1");
                }
                orders.setLlCharge(orders.getRealAmount().subtract(iosPayType.getLlReallyAmount()));
                orders.setLlChargePercent(new BigDecimal(0));
            }
            //更新订单
            int count = updateOrderSuccess(orders);
            if (count > 0) {
                if(orders.getCourseType()==1){//转播课
                    log.info("转播课回调开始-------------");
                    if(OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) &&  !"1".equals(orders.getIosPayType())) {//充值学币
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(iosPayType.getUserReallyAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.default_value.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        //提醒学员购买成功
                        String cact = website + "/weixin/myYention";
                        sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, accountTrack.getAmount().toString()), cact);

                    }else if(OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) &&  "1".equals(orders.getIosPayType())){//充值学币 + 购课
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(orders.getRealAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.buy_replay.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        if ("1".equals(orders.getIosPayType())) {//购买课程的
                            //先锁住账号金额
                            LlAccount queryAccount = llAccountService.getIdRowLockByAccountId(orders.getAppId());
                            /*JoinCourseRecord joinCourseRecord =  joinCourseRecordService.getByAppIdAndCourseId(orders.getAppId(), orders.getIosPayTypeId());//根据用户Id和课程Id获取购买记录
                            //更新报名成功
                            joinCourseRecordService.updateSignUpStatus(joinCourseRecord.getId() , "1" ,joinCourseRecord.getInvitationUserId());//直接更新支付成名*/

                            //查询课程
                            Course course=courseService.getCourse(orders.getIosPayTypeId());
                            //添加记录到转播表
                            ActResultDto actResultDto=courseRelayService.createRelayCourse(orders.getAppId(), String.valueOf(course.getId()),
                                    String.valueOf(course.getRelayCharge()), String.valueOf(course.getRelayScale()));
                            log.info("------------添加转播信息："+actResultDto.getMessage());
                            CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(orders.getAppId(),String.valueOf(course.getId()));

                            //添加收益表记录
                            RelayIncome relayIncome = new RelayIncome();
                            relayIncome.setOriAppId(course.getAppId());//原老师（用户）ID
                            relayIncome.setCharge(course.getRelayCharge());//金额（转播费用、分成金额）
                            relayIncome.setPayAppId(orders.getAppId());//付费人ID（转播者或购买者）
                            relayIncome.setRelayScale(new BigDecimal(course.getRelayScale()));//转播比例
                            relayIncome.setType("1");//类型：1 转播 2 分成
                            relayIncome.setRelCourseId(courseRelayDto.getId());//转播课ID
                            relayIncome.setOriCourseId(course.getId());//原课程ID
                            relayIncomeService.insert(relayIncome);

                            //创建订单
                            String merId= orders.getAppId() + "";//商户号
                            Orders courseOrders =  setOrders(OrderType.RELAY_COURSE.getValue(), PayType.learn_coin_pay.getValue(), orders.getAppId(), merId, orders.getRealAmount(), "", courseRelayDto.getId() , orders.getIosPayTypeId(), "",1);
                            courseOrders.setSuccessTime(new Date());
                            courseOrders.setOptStatus("1");
                            create(courseOrders);

                            //扣除学币购买课程
                            LlAccountTrack accountTrack2 = new LlAccountTrack();
                            accountTrack2.setAmount(orders.getRealAmount());
                            accountTrack2.setOrderId(courseOrders.getId());
                            accountTrack2.setReturnMoneyLevel(LlAccountFromType.buy_replay.getValue());
                            accountTrack2.setCourseType(1);
                            llAccountService.delAccountBalance(courseOrders.getAppId(), courseOrders.getRealAmount(), accountTrack2, queryAccount);
                            relayCourse(courseOrders, courseRelayDto.getId(), courseRelayDto.getOriCourseId());// 处理返钱 购买课程
                            //购买课成功处理redis joinCourseRecord
                            redisUtil.hset(RedisKey.ll_live_join_relay_course_record_pre + courseRelayDto.getOriCourseId(), courseRelayDto.getAppId() + "", "1");
                        }
                        //提醒学员购买成功
                        String cact = website + "/weixin/myYention";
                        sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, accountTrack.getAmount().toString()), cact);
                    } else if(OrderType.recharge.getValue().equals(orders.getOrderType())){
                        //钱包充值
                    }else{
                        //其他待定
                    }
                }else{
                    if(OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) &&  !"1".equals(orders.getIosPayType())) {//充值学币
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(iosPayType.getUserReallyAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.default_value.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        //提醒学员购买成功
                        String cact = website + "/weixin/myYention";
                        sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, accountTrack.getAmount().toString()), cact);

                    }else if(OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) &&  "1".equals(orders.getIosPayType())){//充值学币 + 购课
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(orders.getRealAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.default_value.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        if ("1".equals(orders.getIosPayType())) {//购买课程的
                            //先锁住账号金额
                            LlAccount queryAccount = llAccountService.getIdRowLockByAccountId(orders.getAppId());
                            JoinCourseRecord joinCourseRecord =  joinCourseRecordService.getByAppIdAndCourseId(orders.getAppId(), orders.getIosPayTypeId());//根据用户Id和课程Id获取购买记录
                            //更新报名成功
                            joinCourseRecordService.updateSignUpStatus(joinCourseRecord.getId() , "1" ,joinCourseRecord.getInvitationUserId());//直接更新支付成名
                            //创建订单
                            String merId= orders.getAppId() + "";//商户号
                            Orders courseOrders =  setOrders(OrderType.buyCourse.getValue(), PayType.learn_coin_pay.getValue(), orders.getAppId(), merId, orders.getRealAmount(), "", joinCourseRecord.getId() , orders.getIosPayTypeId(), "");
                            courseOrders.setSuccessTime(new Date());
                            courseOrders.setOptStatus("1");
                            create(courseOrders);
                            //扣除学币购买课程
                            LlAccountTrack accountTrack2 = new LlAccountTrack();
                            accountTrack2.setAmount(orders.getRealAmount());
                            accountTrack2.setOrderId(courseOrders.getId());
                            accountTrack2.setReturnMoneyLevel(LlAccountFromType.buy_course.getValue());
                            llAccountService.delAccountBalance(courseOrders.getAppId(), courseOrders.getRealAmount(), accountTrack2, queryAccount);
                            handlerBuyCourse(courseOrders, joinCourseRecord.getInvitationUserId());// 处理返钱 购买课程
                            //购买课成功处理redis joinCourseRecord
                            redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
                        }
                        //提醒学员购买成功
                        String cact = website + "/weixin/myYention";
                        sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, accountTrack.getAmount().toString()), cact);
                    } else if(OrderType.recharge.getValue().equals(orders.getOrderType())){
                        //钱包充值
                    }else{
                        //其他待定
                    }
                }
            }
        }
        return orders;
    }
    /**
     * 统一支付接口(转播课购买)
     * 订单--第三方支付
     *@author qym
     * @param orderNo 订单编号
     * @param payType 支付类型  枚举类PayType
     * @param payType tranNo  第三方支付流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders thirdPayRelay(String orderNo, String payType, String tranNo) throws Exception {
        Orders orders = getOrderByOrderNo(orderNo);
        if (orders != null && ("0".equals(orders.getOptStatus()) )) {
            //执行更新
            if (!Utility.isNullorEmpty(tranNo)) orders.setTranNo(tranNo);
            //手续费
            BigDecimal appPayPercent = new BigDecimal(0);
            BigDecimal minCharge = new BigDecimal(0);//最少手续费
            if (PayType.alipay.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppAliPayPercent();
            } else if (PayType.weixin.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppWeixinPayPercent();
            } else if (PayType.weixin_h5.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppWeixinH5PayPercent();
            } else if (PayType.unionpay.getValue().equals(payType)) {
                appPayPercent = systemParaRedisUtil.getAppUnionPayPercent();
                minCharge = systemParaRedisUtil.getAPPUnionPayMinCharge();
            }
            BigDecimal ziJinChiCharge = Utility.parseUseTwoPointNumMoney(orders.getRealAmount().multiply(appPayPercent));
            if (PayType.unionpay.getValue().equals(payType) && ziJinChiCharge.compareTo(minCharge) < 0) {
                ziJinChiCharge = minCharge;
            }
            orders.setCharge(ziJinChiCharge);
            orders.setChargePercent(appPayPercent);
            orders.setLlCharge(new BigDecimal(0));
            orders.setLlChargePercent(new BigDecimal(0));
            IosPayType iosPayType = null;
            if (OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) && !"1".equals(orders.getIosPayType())) {//充值学币
                if (PayType.ios.getValue().equals(orders.getBankType())) {
                    iosPayType = iosPayTypeService.findPayInfoById(orders.getIosPayTypeId(), "0");
                } else {
                    iosPayType = iosPayTypeService.findPayInfoById(orders.getIosPayTypeId(), "1");
                }
                orders.setLlCharge(orders.getRealAmount().subtract(iosPayType.getLlReallyAmount()));
                orders.setLlChargePercent(new BigDecimal(0));
            }
            //更新订单
            int count = updateOrderSuccess(orders);
            if (count > 0) {
                if (orders.getCourseType() == 1) {//购买转播课
                    //添加收益表记录（appId为转播人id）
                    //CourseRelayDto courseRelayDt1 = courseRelayService.queryById(courseRelayDto.getId());
                    //根据转播课id获取价格
                    CourseRelayDto courseRelayDt1 = courseRelayService.queryById(orders.getIosPayTypeId());
                    //原课程表
                    Course course = courseService.getCourse(courseRelayDt1.getOriCourseId());
                    RelayIncome relayIncome = new RelayIncome();
                    //BigDecimal charge1 = new BigDecimal(charge).setScale(2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal CHARGE_AMT = new BigDecimal(courseRelayDt1.getRelayScale());
                    BigDecimal teacherScaleB =  new BigDecimal(100-courseRelayDt1.getRelayScale());//给老师的比例数字   CUSTOM_DISTRIBUTION%
                    BigDecimal teacherScaleBB =  teacherScaleB.divide(BigDecimal.valueOf(100));//10/100=0.1
                    BigDecimal teacherScaleAMT = course.getChargeAmt().multiply(teacherScaleBB);//1*0.1=0.1
                    relayIncome.setOriAppId(courseRelayDt1.getOriAppId());//原老师（用户）ID
                    relayIncome.setCharge(teacherScaleAMT);//金额（分成金额）
                    relayIncome.setPayAppId(orders.getAppId());//付费人ID（转播者或购买者）
                    relayIncome.setRelayScale(new BigDecimal(100-courseRelayDt1.getRelayScale()));//转播比例
                    relayIncome.setType("2");//类型：1 转播 2 分成
                    relayIncome.setRelCourseId(courseRelayDt1.getId());//转播课ID
                    relayIncome.setOriCourseId(courseRelayDt1.getOriCourseId());//原课程ID
                    relayIncomeService.insert(relayIncome);
                    if (OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) && !"1".equals(orders.getIosPayType())) {//充值学币
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(iosPayType.getUserReallyAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.default_value.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        //提醒学员购买成功
                        String cact = website + "/weixin/myYention";
                        sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getJoinCourseId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, accountTrack.getAmount().toString()), cact);

                    } else if (OrderType.recharge_learn_coinpay.getValue().equals(orders.getOrderType()) && "1".equals(orders.getIosPayType())) {//充值学币 + 购课
                        LlAccountTrack accountTrack = new LlAccountTrack();
                        accountTrack.setFormAccountId(0);
                        accountTrack.setAmount(orders.getRealAmount());
                        accountTrack.setReturnMoneyLevel(LlAccountFromType.replay_profit.getValue());
                        accountTrack.setOrderId(orders.getId());
                        llAccountService.addAccountBalance(orders.getAppId(), accountTrack.getAmount(), accountTrack);
                        if ("1".equals(orders.getIosPayType())) {//购买课程的
                            //先锁住账号金额
                            LlAccount queryAccount = llAccountService.getIdRowLockByAccountId(orders.getAppId());
                            JoinCourseRecord joinCourseRecord = joinCourseRecordService.getByAppIdAndCourseId(orders.getAppId(), orders.getIosPayTypeId());//根据用户Id和课程Id获取购买记录
                            //更新报名成功
                            joinCourseRecordService.updateSignUpStatus(joinCourseRecord.getId(), "1", joinCourseRecord.getInvitationUserId());//直接更新支付成名
                            //创建订单
                            String merId = orders.getAppId() + "";//商户号
                            Orders courseOrders = setOrders(OrderType.buyCourse.getValue(), PayType.learn_coin_pay.getValue(), orders.getAppId(), merId, orders.getRealAmount(), "", joinCourseRecord.getId(), orders.getIosPayTypeId(), "");
                            courseOrders.setSuccessTime(new Date());
                            courseOrders.setOptStatus("1");
                            courseOrders.setCourseType(1);
                            create(courseOrders);
                            //扣除学币购买课程
                            LlAccountTrack accountTrack2 = new LlAccountTrack();
                            accountTrack2.setAmount(orders.getRealAmount());
                            accountTrack2.setOrderId(courseOrders.getId());
                            accountTrack2.setReturnMoneyLevel(LlAccountFromType.buy_course.getValue());
                            llAccountService.delAccountBalance(courseOrders.getAppId(), courseOrders.getRealAmount(), accountTrack2, queryAccount);
                            //handlerBuyCourse(courseOrders, joinCourseRecord.getInvitationUserId());// 处理返钱 购买课程
                            handlerBuyRelayCourse(courseOrders , courseRelayDt1.getId());// 处理返钱 购买课程
                            //购买课成功处理redis joinCourseRecord
                            redisUtil.hset(RedisKey.ll_live_join_course_record_pre + joinCourseRecord.getCourseId(), joinCourseRecord.getAppId() + "", "1");
                        }
                        //提醒学员购买成功
                        //String cact = website + "/weixin/myYention";
                        //sendMsgService.sendMsg(orders.getAppId(), MsgType.LEARD_COINPAY_RECHARGE.getType(), orders.getId(), MsgConst.replace(MsgConst.LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT, accountTrack.getAmount().toString()), cact);
                        //变更账户发送消息
                        handlerBuyRelayCourse(orders,0L);
                        //添加购买课程记录
                        joinCourseRecordService.addJoinUser2Redis(courseRelayDt1.getId(), orders.getAppId(), new Date());
                    } else if (OrderType.recharge.getValue().equals(orders.getOrderType())) {
                        //钱包充值
                    } else {
                        //其他待定
                    }
                }
            }
        }
        return orders;
    }


    public int updateOrderSuccess(Orders orders) {
        orders.setOptStatus("1");
        orders.setSuccessTime(new Date());
        return ordersMapper.updateRechargeById(orders);
    }

    /**
     * 发送消息和自动关注
     * @param orders
     * @param course
     */
    @Override
    public void sendMsgAndFollow2(Orders orders , Course course , String cAct){
        //自动关注老师
//        userFollowService.follow(orders.getAppId(), course.getRoomId());
        //处理报名付费成功人数接口，等待刘涵写入redis
        joinCourseRecordService.addJoinUser2Redis(course.getId(), orders.getAppId(), new Date());
        //提醒学员
        sendMsgService.sendMsg(orders.getAppId(), MsgType.BUY_COURSE_SUCCESS.getType(), course.getId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, course.getLiveTopic(), orders.getRealAmount().toString()), cAct);
        //提醒老师
        cAct = website + "/weixin/new_learncoinAccount";
        if(orders.getAmount()!=null&&orders.getAmount().compareTo(new BigDecimal(0))>0){
            sendMsgService.sendMsg(course.getAppId(), MsgType.PAY_REMIND.getType(), course.getId() ,MsgConst.NEW_PAY_REMIND_TEACHER_CONTENT , cAct);
        }
    }

    /**
     * 发送消息和自动关注
     * @param orders
     * @param course
     */
    @Override
    public void sendMsgAndFollow(Orders orders , Course course , String cAct){
        //自动关注老师
//        userFollowService.follow(orders.getAppId(), course.getRoomId());
        //处理报名付费成功人数接口，等待刘涵写入redis
        joinCourseRecordService.addJoinUser2Redis(course.getId(), orders.getAppId(), new Date());
        //提醒学员
        sendMsgService.sendMsg(orders.getAppId(), MsgType.BUY_COURSE_SUCCESS.getType(), course.getId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, course.getLiveTopic(), orders.getRealAmount().toString()), cAct);
        //提醒老师
        cAct = website + "/weixin/new_learncoinAccount";
        if(orders.getAmount()!=null&&orders.getAmount().compareTo(new BigDecimal(0))>0){
            sendMsgService.sendMsg(course.getAppId(), MsgType.PAY_REMIND.getType(), course.getId() ,MsgConst.NEW_PAY_REMIND_TEACHER_CONTENT , cAct);
        }
    }

    /**
     * 发送消息和自动关注
     * @param orders
     * @param course
     */
    @Override
    public void sendRelayMsg(Orders orders , Course course , String cAct){
        //处理报名付费成功人数接口，等待刘涵写入redis
        /*joinCourseRecordService.addJoinUser2Redis(course.getId(), orders.getAppId(), new Date());*/

        //发送公众号消息
        CourseRelayDto courseRelayDto=courseRelayService.queryByAppidAndOriCourseId(orders.getAppId(), String.valueOf(course.getId()));
        CourseDto courseDto=new CourseDto();
        courseDto.setLiveTopic(courseRelayDto.getLiveTopic());
        courseDto.setId(courseRelayDto.getId());
        courseDto.setIsRelay(1);
        courseDto.setRoomId(courseRelayDto.getRoomId());
        courseDto.setRemark(courseRelayDto.getRemark());
        courseDto.setCoverssAddress(courseRelayDto.getCoverssAddress());
        courseDto.setLiveTopic(courseRelayDto.getLiveTopic());
        courseDto.setRelayCourseType(1);
        courseDto.setStartTime(courseRelayDto.getStartTime());
        AppUser teach = appUserService.getById(courseRelayDto.getAppId());
        courseDto.setAppUserName(teach.getName());
        wechatOfficialService.getSendWechatTemplateMessageSaveRedis(courseDto);

        //提醒学员
        LiveRoom liveRoom=liveRoomService.findByAppId(orders.getAppId());
        cAct = website + "/weixin/liveRoom?id="+liveRoom.getId();
        sendMsgService.sendMsg(orders.getAppId(), MsgType.NEW_COURSE_TEACHER.getType(), course.getId(), MsgConst.replace(MsgConst.RELAY_REMIND_STUDENT_CONTENT, course.getLiveTopic()), cAct);
        //粉丝消息
        Set<String> follows=userFollowService.getFollowUser(liveRoom.getId());
        AppUser user = appUserService.getById(orders.getAppId());
        for(String userId:follows){
            sendMsgService.sendMsg(Long.parseLong(userId), MsgType.NEW_COURSE_STUDENT.getType(), course.getId()
                    , MsgConst.replace(MsgConst.FOLLOW_TEACHER_RELAY_COURSE_SUCCESS, user.getName())
                    , cAct);
        }
        //提醒老师
        cAct = website + "/weixin/new_learncoinAccount";
        if(orders.getAmount()!=null&&orders.getAmount().compareTo(new BigDecimal(0))>0){
            sendMsgService.sendMsg(course.getAppId(), MsgType.RELAY_COURSES_REMIND.getType(), course.getId() ,
                    MsgConst.replace(MsgConst.RELAY_REMIND_TEACHER_CONTENT, course.getLiveTopic()), cAct);
        }
    }

    /**
     * 购买转播课发送消息
     * @param orders
     * @param course
     * @param
     */
    @Override
    public void sendMsgRelayCoinPay(Orders orders, Course course,RelayIncome relayIncome,CourseRelayDto courseRelayDto) {
        //自动关注老师
//        userFollowService.follow(orders.getAppId(), course.getRoomId());
        //处理报名付费成功人数接口，等待刘涵写入redis
        joinCourseRecordService.addJoinUser2Redis(courseRelayDto.getId(), orders.getAppId(), new Date());
        //提醒学员
        String cActStu = website + "/weixin/courseInfo?id="+courseRelayDto.getId();
        sendMsgService.sendMsg(orders.getAppId(), MsgType.BUY_COURSE_SUCCESS.getType(), courseRelayDto.getId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, course.getLiveTopic(), orders.getRealAmount().toString()), cActStu);
        //提醒转播老师
        String cActTea = website + "/weixin/new_learncoinAccount";
        if(orders.getAmount()!=null&&orders.getAmount().compareTo(new BigDecimal(0))>0){
            sendMsgService.sendMsg(courseRelayDto.getAppId(), MsgType.PAY_REMIND.getType(), courseRelayDto.getId() ,MsgConst.NEW_PAY_REMIND_TEACHER_CONTENT , cActTea);
        }
        //提醒原创老师
        if(relayIncome.getCharge()!=null && relayIncome.getCharge().compareTo(new BigDecimal(0))>0){
            sendMsgService.sendMsg(relayIncome.getOriAppId(), MsgType.BUY_RELAY_COURSES.getType(), relayIncome.getOriCourseId() ,MsgConst.replace(MsgConst.DIVIDE_INTO_ACCOUNT, course.getLiveTopic()) , cActTea);

        }


    }

    /**
     * 处理购买课程
     * @param orders
     * @param invitationAppId  分销分用户ID
     *
     */
    @Override
    public  void handlerBuyCourse(Orders orders , Long invitationAppId){
        /*返钱*/
        //查课程报名记录
        long courseId = joinCourseRecordService.getCourseIdById(orders.getJoinCourseId());
        if(courseId > 0){
            //查询课程
            Course course = courseService.getCourse(courseId);
            String DIVIDE_SCALE = course.getDivideScale();//获取分销比例
            String CUSTOM_DISTRIBUTION = course.getCustomDistribution();//获取自定义分销比例
            BigDecimal CHARGE_AMT = course.getChargeAmt();//金额
            //获取分销人
            //有邀请人，且邀请人不是老师,且不是人
            if(invitationAppId != null &&  invitationAppId > 0 && invitationAppId != course.getAppId() && invitationAppId != orders.getAppId() ){
                String DIVIDE_SCALE_DESC = systemParaRedisUtil.getCourseDivideScaleByValue(DIVIDE_SCALE);
                if(!Utility.isNullorEmpty(DIVIDE_SCALE_DESC)&&Utility.isNullorEmpty(CUSTOM_DISTRIBUTION)){
                    String studyScale = DIVIDE_SCALE_DESC.split(":")[1];//学生的比例
                    BigDecimal studyScaleB =  new BigDecimal(studyScale);
                    BigDecimal studyScaleBB =  studyScaleB.divide(BigDecimal.valueOf(10));
                    BigDecimal studyScaleAMT = CHARGE_AMT.multiply(studyScaleBB);
                    studyScaleAMT = Utility.parseUseTwoPointNumMoney(studyScaleAMT);
                    CHARGE_AMT = CHARGE_AMT.subtract(studyScaleAMT);
                    //给分销人钱
                    AccountTrack accountTrack = new AccountTrack();
                    accountTrack.setFormAccountId(orders.getAppId());
                    accountTrack.setAmount(studyScaleAMT);
                    accountTrack.setReturnMoneyLevel(AccountFromType.distribution.getValue());
                    accountTrack.setOrderId(orders.getId());
                    accountService.addAccountBalance(invitationAppId, studyScaleAMT, accountTrack);
                    //提醒分销人
                    String cact = website + "/weixin/new_learncoinAccount";
                    sendMsgService.sendMsg(invitationAppId, MsgType.INVITATION_REWARD_CONTENT.getType(), course.getId(), MsgConst.replace(MsgConst.INVITATION_REWARD_CONTENT, course.getLiveTopic(),studyScaleAMT.toString()), cact);
                }else if(!Utility.isNullorEmpty(CUSTOM_DISTRIBUTION)){//增加自定义分销比例
                    BigDecimal studyScaleB =  new BigDecimal(CUSTOM_DISTRIBUTION);//给学生的比例数字   CUSTOM_DISTRIBUTION%
                    BigDecimal studyScaleBB =  studyScaleB.divide(BigDecimal.valueOf(100));//10/100=0.1
                    BigDecimal studyScaleAMT = CHARGE_AMT.multiply(studyScaleBB);//1*0.1=0.1
                    studyScaleAMT = Utility.parseUseTwoPointNumMoney(studyScaleAMT);
                    CHARGE_AMT = CHARGE_AMT.subtract(studyScaleAMT);//1-0.1=0.9
                    //给分销人钱
                    AccountTrack accountTrack = new AccountTrack();
                    accountTrack.setFormAccountId(orders.getAppId());
                    accountTrack.setAmount(studyScaleAMT);//0.1
                    accountTrack.setReturnMoneyLevel(AccountFromType.distribution.getValue());
                    accountTrack.setOrderId(orders.getId());
                    accountService.addAccountBalance(invitationAppId, studyScaleAMT, accountTrack);
                    //提醒分销人
                    String cact = website + "/weixin/new_learncoinAccount";
                    sendMsgService.sendMsg(invitationAppId, MsgType.INVITATION_REWARD_CONTENT.getType(), course.getId(), MsgConst.replace(MsgConst.INVITATION_REWARD_CONTENT, course.getLiveTopic(),studyScaleAMT.toString()), cact);
                }
            }
            //给老师钱
            AccountTrack accountTrack = new AccountTrack();
            accountTrack.setFormAccountId(orders.getAppId());
            accountTrack.setAmount(CHARGE_AMT);
            accountTrack.setReturnMoneyLevel(AccountFromType.default_value.getValue());
            accountTrack.setOrderId(orders.getId());
            accountService.addAccountBalance(course.getAppId(), CHARGE_AMT, accountTrack);
            //发送消息和自动关注
            String cAct = website + "/weixin/courseInfo?id="+course.getId();
            sendMsgAndFollow(orders, course, cAct);
            //不是系列课，记录有付费的课程
            if (!"1".equals(course.getIsSeriesCourse()))
              countService.newPayCourseCount(course.getId());
            //记录有付费的用户&金额
            countService.payUserCount(orders.getAppId() , CHARGE_AMT);
        }
    }

    /**
     * 处理转播课程
     * @param orders
     * @param invitationAppId
     * @auth zhikang.zhao
     *
     */
    @Override
    public  void relayCourse(Orders orders , Long invitationAppId,long courseId){
        /*返钱*/
        //查询课程
        Course course = courseService.getCourse(courseId);
        BigDecimal CHARGE_AMT = course.getRelayCharge();//金额
        //给老师钱
        AccountTrack accountTrack = new AccountTrack();
        accountTrack.setFormAccountId(orders.getAppId());
        accountTrack.setAmount(CHARGE_AMT);
        accountTrack.setReturnMoneyLevel(AccountFromType.buy_relay.getValue());
        accountTrack.setOrderId(orders.getId());
        accountTrack.setCourseType(1);
        accountService.addAccountBalance(course.getAppId(), CHARGE_AMT, accountTrack);
        //发送消息和自动关注
        String cAct = website + "/weixin/courseInfo?id="+course.getId();
        sendRelayMsg(orders, course, cAct);
        /*//不是系列课，记录有付费的课程
        if (!"1".equals(course.getIsSeriesCourse()))
            countService.newPayCourseCount(course.getId());
        //记录有付费的用户&金额
        countService.payUserCount(orders.getAppId() , CHARGE_AMT);*/
    }


    /**
     * 处理购买课程
     * @param orders
     * @param
     *
     */
    @Override
    public  void handlerRelayCourse(Orders orders , Long relayCourseId,long courseId,RelayIncome relayIncome){
        /*返钱*/
        //查询课程
        Course course = courseService.getCourse(courseId);
        //转播信息
        CourseRelayDto courseRelayDto = courseRelayService.queryById(relayCourseId);
        BigDecimal CHARGE_AMT = course.getRelayCharge();//金额
        //给老师钱
        AccountTrack accountTrack = new AccountTrack();
        accountTrack.setFormAccountId(orders.getAppId());
        accountTrack.setAmount(CHARGE_AMT);
        accountTrack.setReturnMoneyLevel(AccountFromType.buy_relay.getValue());
        accountTrack.setOrderId(orders.getId());
        accountTrack.setCourseType(1);
        accountService.addAccountBalance(course.getAppId(), CHARGE_AMT, accountTrack);
        //发送消息和自动关注
        String cAct = website + "/weixin/courseInfo?id="+course.getId();
        //sendMsgRelayCoinPay(orders, course, cAct);
        sendMsgRelayCoinPay(orders, course,relayIncome,courseRelayDto);
        /*//不是系列课，记录有付费的课程
        if (!"1".equals(course.getIsSeriesCourse()))
            countService.newPayCourseCount(course.getId());
        //记录有付费的用户&金额
        countService.payUserCount(orders.getAppId() , CHARGE_AMT);*/
    }
    /**
     * --购买转播课程
     * @param orders
     * @param invitationAppId
     * @author qym
     */
    @Override
    public void handlerBuyRelayCourse(Orders orders, Long invitationAppId) {
        /*返钱*/
        //查课程报名记录
        long courseId = joinCourseRecordService.getCourseIdById(orders.getJoinCourseId());
        if(courseId > 0){

            //根据转播课id获取价格
            CourseRelayDto courseRelayDt = courseRelayService.queryById(courseId);
            //查询课程
            Course course1 = courseService.getCourse(courseRelayDt.getOriCourseId());
            String DIVIDE_SCALE = String.valueOf(courseRelayDt.getRelayScale());//获取分销比例
            //String CUSTOM_DISTRIBUTION = courseRelayDt.getCustomDistribution();//获取自定义分销比例
            BigDecimal CHARGE_AMT = course1.getChargeAmt();//金额
            //获取分销人
            //有邀请人，且邀请人不是老师,且不是人

            //String DIVIDE_SCALE_DESC = systemParaRedisUtil.getCourseDivideScaleByValue(DIVIDE_SCALE);
            String cAct = website + "/weixin/new_learncoinAccount";
            //String studyScale = DIVIDE_SCALE_DESC.split(":")[1];//学生的比例
            BigDecimal relayScaleB =  new BigDecimal(courseRelayDt.getRelayScale());
            BigDecimal relayScaleBB =  relayScaleB.divide(BigDecimal.valueOf(100));
            BigDecimal relayScaleAMT = CHARGE_AMT.multiply(relayScaleBB);
            relayScaleAMT = Utility.parseUseTwoPointNumMoney(relayScaleAMT);
            //CHARGE_AMT = CHARGE_AMT.subtract(relayScaleAMT);
            //给转播人钱
            if(relayScaleAMT.compareTo(new BigDecimal(0))>0){
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setFormAccountId(orders.getAppId());
                accountTrack.setAmount(relayScaleAMT);
                accountTrack.setReturnMoneyLevel(AccountFromType.default_value.getValue());
                accountTrack.setOrderId(orders.getId());
                accountService.addAccountBalance(courseRelayDt.getAppId(), relayScaleAMT, accountTrack);
                //提醒转播人
                sendMsgService.sendMsg(courseRelayDt.getAppId(), MsgType.PAY_REMIND.getType(), courseRelayDt.getId(),MsgConst.NEW_PAY_REMIND_TEACHER_CONTENT, cAct);
            }

            //给老师钱
            BigDecimal teacherScaleB =  new BigDecimal(100-courseRelayDt.getRelayScale());//给老师的比例数字   CUSTOM_DISTRIBUTION%
            BigDecimal teacherScaleBB =  teacherScaleB.divide(BigDecimal.valueOf(100));//10/100=0.1
            BigDecimal teacherScaleAMT = CHARGE_AMT.multiply(teacherScaleBB);//1*0.1=0.1
            if(teacherScaleAMT.compareTo(new BigDecimal(0))>0){
                AccountTrack accountTrack = new AccountTrack();
                accountTrack.setFormAccountId(orders.getAppId());
                accountTrack.setAmount(teacherScaleAMT);
                accountTrack.setReturnMoneyLevel(AccountFromType.relay_yield.getValue());
                accountTrack.setOrderId(orders.getId());
                accountService.addAccountBalance(courseRelayDt.getOriAppId(), teacherScaleAMT, accountTrack);
                //提醒老师
                sendMsgService.sendMsg(courseRelayDt.getOriAppId(), MsgType.BUY_RELAY_COURSES.getType(), courseRelayDt.getOriCourseId() ,MsgConst.replace(MsgConst.DIVIDE_INTO_ACCOUNT, course1.getLiveTopic()), cAct);//MsgConst.NEW_PAY_RELAY_TEACHER_HALF
            }
            //发送消息
            //String cAct = website + "/weixin/courseInfo?id="+courseRelayDt.getOriAppId();
            //sendMsgAndFollow(orders, courseRelayDt, cAct);

            if(orders.getAmount()!=null&&orders.getAmount().compareTo(new BigDecimal(0))>0){
                //提醒学员
                String cActStu = website + "/weixin/courseInfo?id="+courseRelayDt.getId();
                sendMsgService.sendMsg(orders.getAppId(), MsgType.BUY_COURSE_SUCCESS.getType(), courseRelayDt.getId(), MsgConst.replace(MsgConst.PAY_REMIND_STUDENT_CONTENT, course1.getLiveTopic(), orders.getRealAmount().toString()), cActStu);
            }
            //不是系列课，记录有付费的课程
            if (!"1".equals(courseRelayDt.getIsSeriesCourse()))
                countService.newPayCourseCount(courseRelayDt.getId());
            //记录有付费的用户&金额
            countService.payUserCount(orders.getAppId() , CHARGE_AMT);
            //判添加用户购买课程
            joinCourseRecordService.addJoinUser2Redis(courseRelayDt.getId(), orders.getAppId(), new Date());
        }
    }



    /**
     * 根据Id获取订单
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Orders getOrderById(long id) {
        return ordersMapper.getOrderById(id);
    }

    /**
     * 根据订单编号
     *
     * @param orderNo 订单编号
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Orders getOrderByOrderNo(String orderNo) {
        return ordersMapper.getOrderByOrderNo(orderNo);
    }

    /**
     * 根据报名记录Id 获取订单信息
     * @param joinRecordId
     * @return
     */
    @Override
    public Orders getOrderByJoinCourseRecordId(long joinRecordId) {
        List<Orders> list = ordersMapper.getOrderByJoinCourseRecordId(joinRecordId);
        if(list.size() == 0 ) return null;
        return list.get(0) ;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map> getTopUpRecord(Long appId, long lastId) {
        List<Map> list = ordersMapper.getTopUpRecord(appId, lastId);
        for (Map m : list) {
            m.put("createTime", DateUtil.format((Date) m.get("createTime")));
        }
        return list;
    }

    /**
     * 取消订单 --失败
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelById(long id) {
        return ordersMapper.cancelById(id);
    }


    /**
     * @param orderType 订单类型
     * @param payType 支付类型
     * @param appId appID
     * @param merId 商户号
     * @param tranNo 第三方订单编号
     * @param joinCourseId 购买课程记录表Id
     * @param iosPayTypeId 购买学币支付类型表ID
     * @param amount 支付金额
     * @return
     */
    @Override
    public  Orders setOrders(String orderType , String payType , long appId , String merId , BigDecimal amount , String tranNo ,long joinCourseId ,long iosPayTypeId, String remark){
        //创建订单
        Orders orders = new Orders();
        orders.setOrderType(orderType);
        orders.setAppId(appId);
        orders.setBankType(payType);
        orders.setOrderNo(OrderUtil.getOrderNumber(appId));
        orders.setTranNo(tranNo);
        orders.setAmount(amount);
        orders.setRealAmount(amount);
        orders.setOptStatus("0");
        orders.setMerId(merId);
        orders.setJoinCourseId(joinCourseId);
        orders.setIosPayTypeId(iosPayTypeId);
        orders.setRemark(remark);
        return orders;
    }

    @Override
    public Orders setOrders(String orderType , String payType , long appId , String merId , BigDecimal amount , String tranNo ,long joinCourseId ,long iosPayTypeId, String remark,int courseType) {
        //创建订单
        Orders orders = new Orders();
        orders.setOrderType(orderType);
        orders.setAppId(appId);
        orders.setBankType(payType);
        orders.setOrderNo(OrderUtil.getOrderNumber(appId));
        orders.setTranNo(tranNo);
        orders.setAmount(amount);
        orders.setRealAmount(amount);
        orders.setOptStatus("0");
        orders.setMerId(merId);
        orders.setJoinCourseId(joinCourseId);
        orders.setIosPayTypeId(iosPayTypeId);
        orders.setRemark(remark);
        orders.setCourseType(courseType);
        return orders;
    }

    /**
     *
     * @param orderType 订单类型
     * @param payType   支付类型
     * @param appId     appId
     * @param merId     商户号
     * @param amount    支付金额
     * @param tranNo    第三方订单号
     * @param remark    支付描述
     * @return
     */
    @Override
    public Orders setBuyFlowOrder(String orderType, String payType, long appId, String merId, BigDecimal amount, String tranNo, String remark) {
        //创建订单
        Orders orders = new Orders();
        orders.setOrderType(orderType);
        orders.setAppId(appId);
        orders.setBankType(payType);
        orders.setOrderNo(OrderUtil.getOrderNumber(appId));
        orders.setTranNo(tranNo);
        orders.setAmount(amount);
        orders.setRealAmount(amount);
        orders.setOptStatus("0");
        orders.setMerId(merId);
        orders.setRemark(remark);
        return orders;
    }

    /**
     * 购买流量微信支付回调
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Override
    public Orders updateBuyFlow(String orderNo, String payType) throws Exception {
        Orders orders = getOrderByOrderNo(orderNo);
        if (orders != null && ("0".equals(orders.getOptStatus()) )) {
            //手续费
            BigDecimal appPayPercent = systemParaRedisUtil.getAppWeixinH5PayPercent();
            BigDecimal ziJinChiCharge = Utility.parseUseTwoPointNumMoney(orders.getRealAmount().multiply(appPayPercent));
            orders.setCharge(ziJinChiCharge);
            orders.setChargePercent(appPayPercent);
            orders.setLlCharge(new BigDecimal(0));
            orders.setLlChargePercent(new BigDecimal(0));
            //更新订单
            int count = updateOrderSuccess(orders);
            if (count > 0) {
                //提醒充值流量成功
                DataChargeRecord record = dataChargeRecordService.getModelByOrderId(orders.getId());
                record.setStatus("1");
                dataChargeRecordService.updateStatus(record);
                dataUseService.useData(record.getRoomId() , 0L);
                if(record!=null){
                    DataChargeLevel dataChargeLevel = dataChargeLevelService.findModelById(record.getLevelId());
                    if(dataChargeLevel != null){
                        String text = "";
                        if(record.getInvalidRealDate()!=null){
                            text = DateUtil.format(record.getInvalidRealDate(),"yyyy-MM-dd");
                        }else{
                            text = "永久有效";
                        }
                        try {
                            String cact = website + "/weixin/flowRecharge";
                            sendMsgService.sendMsg(orders.getAppId(), MsgType.BUY_FLOW.getType(), orders.getId(),
                                    MsgConst.replace(MsgConst.BUY_FLOW_CONTENT,
                                            dataChargeLevel.getPrefPrice().toString() ,//充值的金额
                                            dataChargeLevel.getAmount() + "G" ,         //充值的流量
                                            text), //有效期
                                    cact);
                        }catch (Exception e){
                            log.info(orders.getAppId()+"购买流量级别为"+dataChargeLevel.getId()+",发送通知失败!");
                        }
                    }
                }
            }
        }
        return orders;
    }

    @Override
    public Orders updateBuyFlowError(String orderNo, String payType) throws Exception {
        Orders orders = getOrderByOrderNo(orderNo);
        DataChargeRecord record = dataChargeRecordService.getModelByOrderId(orders.getId());
        record.setStatus("1");
        record.setOrderTime(new Date());
        dataChargeRecordService.updateStatus(record);
        return null;
    }
}
