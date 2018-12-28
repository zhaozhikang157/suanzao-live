package com.longlian.live.controller;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huaxin.util.DateUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.UUIDGenerator;
import com.huaxin.util.Utility;
import com.huaxin.util.XmlUtil;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.CourseBaseService;
import com.longlian.live.service.CourseManagerService;
import com.longlian.live.service.CourseRelayService;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.LiveRoomService;
import com.longlian.live.service.ThirdPayService;
import com.longlian.live.util.SystemUtil;
import com.longlian.live.util.ThirdPayUtil;
import com.longlian.live.util.log.Log;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.model.Course;
import com.longlian.model.LiveConnectRequest;
import com.longlian.model.LiveRoom;
import com.longlian.model.Orders;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.JoinCourseRecordType;
import com.longlian.type.LogTableType;
import com.longlian.type.LogType;
import com.longlian.type.PayType;
import com.longlian.type.ReturnMessageType;
import com.longlian.type.ThirdPayDto;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created by Administrator on 2016/8/13.
 */
@Controller
@RequestMapping("/thirdPay")
public class ThirdPayController {
    private static final Logger log = LoggerFactory.getLogger(ThirdPayController.class);
    @Autowired
    ThirdPayService thirdPayService;
    @Autowired
    AppUserService userService;
    @Autowired
    CourseManagerService courseManagerService;
    @Autowired
    CourseService courseService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CourseRelayService courseRelayService;
    @Autowired
    CourseBaseService courseBaseService;
    /**
     * 支付  购买课程
     * @param request
     * @param  thirdPayDto 第三方支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pay.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.learn_coinpay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "支付  购买课程", httpMethod = "POST", notes = "支付  购买课程")
    public ActResultDto thirdPay(HttpServletRequest request ,
                                 @ApiParam(required =true, name = "支付对象类对象", value = "支付对象类对象") ThirdPayDto thirdPayDto){
        if(thirdPayDto.getCourseId() > SystemCofigConst.RELAY_COURSE_ID_SIZE){
            return relayCointhirdPay(request,thirdPayDto);
        }
        ActResultDto actResultDto = null;
        try {
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
            Map mapP = new HashMap();
            String payTypeDesc = PayType.getNameByValue(thirdPayDto.getPayType());
            mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
            RequestInfoContext.getRequestInfo().setIsCreateLog(false);
            RequestInfoContext.getRequestInfo().setLogModel(mapP);
            String ip = IPUtil.getLocalIp(request);
            if (thirdPayDto.getInvitationAppId() == null) thirdPayDto.setInvitationAppId(0l);
            if (thirdPayDto.getCourseId() <= 0) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            //需要判断该课程是否存在
            Course course = courseService.getCourseFromRedis(thirdPayDto.getCourseId());
            if (course == null) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }

            //需要判断该课程是否删除或者下架
            if ("1".equals(course.getIsDelete()) || "1".equals(course.getStatus())) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            int isSuperAdmin = userService.findSystemAdminByUserId(token.getId());
            //判断是否是场控人员
            if (isSuperAdmin > 0 || courseManagerService.isCourseManager(thirdPayDto.getCourseId(), token.getId(), "")) {
                actResultDto = new ActResultDto();
                if(isSuperAdmin > 0){
                    thirdPayService.handlerJoinCourseRecord(token, thirdPayDto, actResultDto, JoinCourseRecordType.super_user.getValue(), 0);
                }else{
                    thirdPayService.handlerJoinCourseRecord(token, thirdPayDto, actResultDto, JoinCourseRecordType.contrl_user.getValue(), 0);
                }
                return actResultDto;
            }
            if (PayType.learn_coin_pay.getValue().equals(thirdPayDto.getPayType())) {
                actResultDto = new ActResultDto();
                RequestInfoContext.getRequestInfo().setIsCreateLog(true);
                actResultDto = thirdPayService.moneybagLearnCoinPay(token, thirdPayDto);
                if (actResultDto.getCode().equals(ReturnMessageType.CODE_MESSAGE_TRUE.getCode())) {
                    Object obj = actResultDto.getExt();
                    if (obj instanceof Orders) {
                        Orders orders = (Orders) obj;
                        if (orders != null) {
                            mapP.put("money", orders.getRealAmount());
                            RequestInfoContext.getRequestInfo().setTableId(orders.getId());
                            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
                            actResultDto.setMessage("支付成功");
                        }
                    }
                }
                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
                actResultDto.setExt(null);
                return actResultDto;
            } else if (PayType.weixin.getValue().equals(thirdPayDto.getPayType())) {//app微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinPay(token, ip, thirdPayDto);
            } else if (PayType.invi_code.getValue().equals(thirdPayDto.getPayType())) {//微信
                actResultDto = thirdPayService.InviCodePay(token, thirdPayDto);
            } else if (PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())) {//微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinH5Pay(token, ip, thirdPayDto);
            } else {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
                actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
            }
            if ("000000".equals(actResultDto.getCode())) {
                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("app+微信支付异常:"+e.getMessage());
        }
        return actResultDto;
    }

    /**
     * 支付  转播课程的费用
     * @param request
     * @param  thirdPayDto 第三方支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/PayByRelpay.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.learn_coinpay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "支付  购买课程", httpMethod = "POST", notes = "支付  购买课程")
    public ActResultDto thirdPayByRelpay(HttpServletRequest request ,
                                         @ApiParam(required =true, name = "支付对象类对象", value = "支付对象类对象") ThirdPayDto thirdPayDto){
        ActResultDto actResultDto = new ActResultDto();
        try {
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
            Map mapP = new HashMap();
            String payTypeDesc = PayType.getNameByValue(thirdPayDto.getPayType());
            mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
            RequestInfoContext.getRequestInfo().setIsCreateLog(false);
            RequestInfoContext.getRequestInfo().setLogModel(mapP);
            String ip = IPUtil.getLocalIp(request);
            if (thirdPayDto.getInvitationAppId() == null) thirdPayDto.setInvitationAppId(0l);
            if (thirdPayDto.getCourseId() <= 0) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            //需要判断该课程是否存在
            Course course = courseService.getCourse(thirdPayDto.getCourseId());
            if (course == null) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }

            //需要判断该课程是否删除或者下架
            if ("1".equals(course.getIsDelete()) || "1".equals(course.getStatus())) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }

            //判断是否是自己的课
            if(course.getAppId()==token.getId()){
                actResultDto.setCode(ReturnMessageType.TEACHER_CAN_NOY_RELAY_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.TEACHER_CAN_NOY_RELAY_COURSE.getMessage());
                return actResultDto;
            }

            //判断课程是否可以转播
            if(course.getIsRelay()!=1){
                actResultDto.setCode(ReturnMessageType.NOT_RELAY_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.NOT_RELAY_COURSE.getMessage());
                return actResultDto;
            }

            //判断是否添加过
            if(courseRelayService.isTransmitted(token.getId(), String.valueOf(thirdPayDto.getCourseId()))){
                actResultDto.setCode(ReturnMessageType.RELAY_COURSE_TRANSMITTED.getCode());
                actResultDto.setMessage(ReturnMessageType.RELAY_COURSE_TRANSMITTED.getMessage());
                return actResultDto;
            }

            //判断是否有自己的个人直播间
            LiveRoom liveRoom = liveRoomService.findByAppId(token.getId());
            if(liveRoom==null){
                actResultDto.setCode(ReturnMessageType.NO_LIVE_ROOM.getCode());
                actResultDto.setMessage(ReturnMessageType.NO_LIVE_ROOM.getMessage());
                return actResultDto;
            }

            /*//不能转播系列课
            if("1".equals(course.getIsSeriesCourse())){
                actResultDto.setCode(ReturnMessageType.CAN_NOT_RELAY_SERIES_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.CAN_NOT_RELAY_SERIES_COURSE.getMessage());
                return actResultDto;
            }
            //不能转播语音课
            if("1".equals(course.getLiveWay())){
                actResultDto.setCode(ReturnMessageType.CAN_NOT_RELAY_VOICE_COURSE.getCode());
                actResultDto.setMessage(ReturnMessageType.CAN_NOT_RELAY_VOICE_COURSE.getMessage());
                return actResultDto;
            }*/
           /* //查询最近的一条转播记录
            CourseRelayDto relayDto = courseRelayService.queryByCreateTime(token.getId());
            //只能有一节转播课为 没有直播结束
            if(relayDto!=null && relayDto.getEndTime()==null){
                actResultDto.setCode(ReturnMessageType.WAIT_RELAY_END.getCode());
                actResultDto.setMessage(ReturnMessageType.WAIT_RELAY_END.getMessage());
                return actResultDto;
            }*/

            /*//转播正在直播的课，判断是否有正在直播的课程，有则不让转播
            String endTime=null;
            String startTime=null;
            if(course.getEndTime()!=null){
                endTime=DateUtil.format(course.getEndTime());
            }
            if(course.getStartTime()!=null){
                startTime=DateUtil.format(course.getStartTime());
            }
            if("2".equals(DateUtil.getStatusStr(startTime, endTime))){
                List<CourseRelayDto> relayDto = courseRelayService.queryByPlayingCourse(token.getId());
                if(relayDto!=null && relayDto.size()>0){
                    actResultDto.setCode(ReturnMessageType.PALYING_COURSE.getCode());
                    actResultDto.setMessage(ReturnMessageType.PALYING_COURSE.getMessage());
                    return actResultDto;
                }
            }*/
            /*int isSuperAdmin = userService.findSystemAdminByUserId(token.getId());
            //判断是否是场控人员
            if (isSuperAdmin > 0 || courseManagerService.isCourseManager(thirdPayDto.getCourseId(), token.getId(), "")) {
                actResultDto = new ActResultDto();
                thirdPayService.handlerJoinCourseRecord(token, thirdPayDto, actResultDto, JoinCourseRecordType.contrl_user.getValue(), 0);
                return actResultDto;
            }*/
            if (PayType.learn_coin_pay.getValue().equals(thirdPayDto.getPayType())) {//金币支付
                actResultDto = new ActResultDto();
                RequestInfoContext.getRequestInfo().setIsCreateLog(true);
                actResultDto = thirdPayService.moneybagLearnCoinPayByRelay(token, thirdPayDto);
                if(ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())){
                    CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(token.getId(), String.valueOf(course.getId()));
                    //添加转播云信token 语音课是原老师的云信
                   /* Course oriCourse = courseService.getCourse(thirdPayDto.getCourseId());
                    if(!"1".equals(oriCourse.getLiveWay())){  //语音课用原来的chatRoomId
                        courseBaseService.setRelayChatroomId(courseRelayDto,false);
                    }*/
                    courseBaseService.setRelayChatrooId(courseRelayDto,thirdPayDto.getCourseId()); //给每节课添加chatroomid
                    actResultDto.setData(courseRelayDto);
                }

                if (actResultDto.getCode().equals(ReturnMessageType.CODE_MESSAGE_TRUE.getCode())) {
                    Object obj = actResultDto.getExt();
                    if (obj instanceof Orders) {
                        Orders orders = (Orders) obj;
                        if (orders != null) {
                            mapP.put("money", orders.getRealAmount());
                            RequestInfoContext.getRequestInfo().setTableId(orders.getId());
                            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
                            actResultDto.setMessage("支付成功");
                        }
                    }
                }
//                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
                actResultDto.setExt(null);

                return actResultDto;
            } else if (PayType.weixin.getValue().equals(thirdPayDto.getPayType())) {//app微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinPayRelay(token, ip, thirdPayDto);
            } else if (PayType.invi_code.getValue().equals(thirdPayDto.getPayType())) {//微信
                actResultDto = thirdPayService.InviCodePayRelay(token, thirdPayDto);
                CourseRelayDto courseRelayDto = courseRelayService.queryByAppidAndOriCourseId(token.getId(), String.valueOf(course.getId()));
                actResultDto.setData(courseRelayDto);
            } else if (PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())) {//微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinH5PayReplay(token, ip, thirdPayDto);
            } else {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
                actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
            }
            if ("000000".equals(actResultDto.getCode())) {
                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("app+微信支付异常:"+e.getMessage());
            actResultDto.setCode(ReturnMessageType.SERVER_ERROR_RETY.getCode());
            actResultDto.setMessage(ReturnMessageType.SERVER_ERROR_RETY.getMessage());
            return actResultDto;
        }
        return actResultDto;
    }
    /**
     * 支付  购买转播课程
     * @param request
     * @param  thirdPayDto 第三方支付
     * @return
     * @author qym
     * @throws Exception
     */
    //@RequestMapping(value = "/relayCoinPay.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.learn_coinpay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "支付  购买转播课程", httpMethod = "POST", notes = "支付  购买转播课程")
    public ActResultDto relayCointhirdPay(HttpServletRequest request ,
                                 @ApiParam(required =true, name = "支付对象类对象", value = "支付对象类对象") ThirdPayDto thirdPayDto){
        ActResultDto actResultDto = null;
        try {
            AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
            Map mapP = new HashMap();
            String payTypeDesc = PayType.getNameByValue(thirdPayDto.getPayType());
            mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
            RequestInfoContext.getRequestInfo().setIsCreateLog(false);
            RequestInfoContext.getRequestInfo().setLogModel(mapP);
            String ip = IPUtil.getLocalIp(request);
            if (thirdPayDto.getInvitationAppId() == null) thirdPayDto.setInvitationAppId(0l);
            if (thirdPayDto.getCourseId() <= 0) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            //转播表
            CourseRelayDto courseRelayDto = courseRelayService.queryById(thirdPayDto.getCourseId());
            if (courseRelayDto == null) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            //原课程表
            Course course = courseService.getCourse(courseRelayDto.getOriCourseId());
            if (course == null) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }

            //需要判断该课程是否删除或者下架
            if ("1".equals(course.getIsDelete()) || "1".equals(course.getStatus())) {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_COURSE_NO_NULL.getMessage());
                return actResultDto;
            }
            int isSuperAdmin = userService.findSystemAdminByUserId(token.getId());
            //判断是否是场控人员
            if (isSuperAdmin > 0 || courseManagerService.isCourseManager(course.getId(), token.getId(), "")) {
                actResultDto = new ActResultDto();
                ThirdPayDto thirdPayDtoOri = new ThirdPayDto();
                thirdPayDtoOri = thirdPayDto;
                thirdPayDtoOri.setCourseId(course.getId());
                if(isSuperAdmin > 0){
                    thirdPayService.handlerJoinCourseRecord(token, thirdPayDtoOri, actResultDto, JoinCourseRecordType.super_user.getValue(), 0);
                }else{
                    thirdPayService.handlerJoinCourseRecord(token, thirdPayDtoOri, actResultDto, JoinCourseRecordType.contrl_user.getValue(), 0);
                }
                return actResultDto;
            }
            if (PayType.learn_coin_pay.getValue().equals(thirdPayDto.getPayType())) {
                actResultDto = new ActResultDto();
                RequestInfoContext.getRequestInfo().setIsCreateLog(true);
                actResultDto = thirdPayService.moneybagRelayCoinPay(token, thirdPayDto,course);
                if (actResultDto.getCode().equals(ReturnMessageType.CODE_MESSAGE_TRUE.getCode())) {
                    Object obj = actResultDto.getExt();
                    if (obj instanceof Orders) {
                        Orders orders = (Orders) obj;
                        if (orders != null) {
                            mapP.put("money", orders.getRealAmount());
                            RequestInfoContext.getRequestInfo().setTableId(orders.getId());
                            RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
                            actResultDto.setMessage("支付成功");
                        }
                    }
                }
                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
                actResultDto.setExt(null);
                return actResultDto;
            } else if (PayType.weixin.getValue().equals(thirdPayDto.getPayType())) {//app微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinPayRelayBuy(token, ip, thirdPayDto);
            }else if (PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())) {//微信
                thirdPayDto.setIosPayType("1");
                actResultDto = thirdPayService.weixinH5PayBuyReplay(token, ip, thirdPayDto);
            } else {
                actResultDto = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
                actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
            }
            if ("000000".equals(actResultDto.getCode())) {
                this.addZaiXianUser(request, actResultDto, thirdPayDto, token, course);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("app+微信支付异常:" + e.getMessage());
        }
        return actResultDto;
    }

    /**
     * 根据商户订单号查询微信支付情况
     * @param request
     * @param thirdOrderNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/payQuery.user")
    @ResponseBody
    @ApiOperation(value = "根据商户订单号查询微信支付情况", httpMethod = "GET", notes = "根据商户订单号查询微信支付情况")
    public ActResultDto thirdPay(HttpServletRequest request ,
                                 @ApiParam(required =true, name = "商户订单号", value = "商户订单号")  String thirdOrderNo)throws  Exception{
        return  thirdPayService.thirdPay( request ,  thirdOrderNo);
    }

    /**
     * 支付  充值学币
     * @param request
     * @param  thirdPayDto 第三方支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rechargePay.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.third_pay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "支付  充值学币", httpMethod = "POST", notes = "支付  充值学币")
    public ActResultDto thirdRechargePay(HttpServletRequest request ,
                                         @ApiParam(required =true, name = "支付对象类对象", value = "支付对象类对象") ThirdPayDto thirdPayDto)throws  Exception{
        ActResultDto actResultDto = null;
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        Map mapP = new HashMap();
        String payTypeDesc = PayType.getNameByValue(thirdPayDto.getPayType());
        mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        String ip = IPUtil.getLocalIp(request);
        Map<String,Boolean> resultMap = null;
        if(PayType.ios.getValue().equals(thirdPayDto.getPayType())){
        	
        	// ios 支付验证
        	String iosTransactionId = thirdPayDto.getIosTransactionId();
        	actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.IOS_PAY_FRAIL.getCode());
            actResultDto.setMessage(ReturnMessageType.IOS_PAY_FRAIL.getMessage());
        	if(StringUtils.isBlank(iosTransactionId)) {
        		return actResultDto;
        	}else {
        		resultMap = ThirdPayUtil.doIosRequest(iosTransactionId);
        		boolean res = resultMap.get(ThirdPayUtil.RESULT);
        		if(!res) {
        			return actResultDto;
        		}
        	}
        	
            if(Utility.isNullorEmpty(thirdPayDto.getIosCommodityId())){
                actResultDto  = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_EXIST.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_EXIST.getMessage());
                return actResultDto;
            }
        }else{
            if(Utility.isNullorEmpty(thirdPayDto.getPayTypeId())){
                actResultDto  = new ActResultDto();
                actResultDto.setCode(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_EXIST.getCode());
                actResultDto.setMessage(ReturnMessageType.LEARN_COINPAY_PAY_TYPE_NO_EXIST.getMessage());
                return actResultDto;
            }
        }
        if(PayType.alipay.getValue().equals(thirdPayDto.getPayType())){
            // actResultDto = thirdPayService.aliPay(token , thirdPayDto);
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
            return actResultDto;
        }else if (PayType.weixin.getValue().equals(thirdPayDto.getPayType())){
            thirdPayDto.setIosPayType("0");
            actResultDto  = thirdPayService.weixinPay(token ,ip,thirdPayDto);
        }else if (PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())){
            thirdPayDto.setIosPayType("0");
            actResultDto = thirdPayService.weixinH5Pay(token, ip , thirdPayDto);
        }else if (PayType.abc.getValue().equals(thirdPayDto.getPayType())){
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
        }else if (PayType.moneybag.getValue().equals(thirdPayDto.getPayType())){
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
        }else if (PayType.unionpay.getValue().equals(thirdPayDto.getPayType())){
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
        }else if (PayType.ios.getValue().equals(thirdPayDto.getPayType())){
        	boolean test = SystemUtil.isTestEnv(request);
        	boolean isOnLine = resultMap.get(ThirdPayUtil.ISONLINE);
        	if(isOnLine || test) {// 如果是ios内购线上测试成功 或者 是测试环境才充值
        		actResultDto = thirdPayService.iosPay4Recharge(token,  thirdPayDto );
        	}else {
        		actResultDto = new ActResultDto();
        	}
        }else{
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
        }
        return actResultDto;
    }

    /**
     * 取消订单
     * @param request
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancelThirdPay.user" )
    @ResponseBody
    @ApiOperation(value = "取消订单", httpMethod = "GET", notes = "取消订单")
    public ActResultDto cancelThirdPay(HttpServletRequest request ,
                                       @ApiParam(required =true, name = "订单ID", value = "订单ID")  long orderId)throws  Exception{
        ActResultDto actResultDto = null;
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (orderId  <=0){
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_CANCEL_ORDER_ERRER.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_CANCEL_ORDER_ERRER.getMessage());
            return actResultDto;
        }
        thirdPayService.cancelThirdPay(token , orderId);
        return actResultDto;
    }

    /**
     * 用户打赏
     * @param request
     * @param  thirdPayDto 第三方支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userRewardPay.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.learn_coinpay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "用户打赏", httpMethod = "POST", notes = "用户打赏")
    public ActResultDto userRewardPay(HttpServletRequest request ,
                                      @ApiParam(required =true, name = "支付对象类对象", value = "支付对象类对象")  ThirdPayDto thirdPayDto)throws  Exception{
        ActResultDto actResultDto = null;
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        Map mapP = new HashMap();
        String payTypeDesc = PayType.getNameByValue(thirdPayDto.getPayType());
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        String ip = IPUtil.getLocalIp(request);
        if(PayType.learn_coin_pay.getValue().equals(thirdPayDto.getPayType())){
            actResultDto  = new ActResultDto();
            RequestInfoContext.getRequestInfo().setIsCreateLog(true);
            actResultDto = thirdPayService.userRewardPay(token, thirdPayDto);
            actResultDto.setExt(null);
            return actResultDto;
        }else{
            actResultDto  = new ActResultDto();
            actResultDto.setCode(ReturnMessageType.THIRD_PAY_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.THIRD_PAY_ERROR.getMessage());
        }
        return actResultDto;
    }

    /**
     * 企业付款
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/payment" ,  method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "企业付款", httpMethod = "GET", notes = "企业付款")
    public ActResultDto thirdRechargePay(HttpServletRequest request )throws  Exception{
        String xml =  getXml(request);
        String url="https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
      /*  String result =  HttpUtil.doPost(url, xml);

*/

        String jsonStr = doPost( url,  xml);
       /* System.out.println(xml);
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File("E://apiclient_cert.p12"));
        keyStore.load(instream, "1418478402".toCharArray());//杩欓噷鍐欏
        instream.close();
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "1418478402".toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf) .build();
        HttpPost httpost = new HttpPost(url); // 璁剧疆鍝嶅簲澶翠俊鎭?*/
      //  httpost.addHeader("Connection", "keep-alive");
       // httpost.addHeader("Accept", "*/*");
       // httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      //  httpost.addHeader("Host", "api.mch.weixin.qq.com");
      //  httpost.addHeader("X-Requested-With", "XMLHttpRequest");
      //  httpost.addHeader("Cache-Control", "max-age=0");
      //  httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
    /*    httpost.setEntity(new StringEntity(xml, "UTF-8"));
        CloseableHttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");*/
      //  EntityUtils.consume(entity);
        XmlUtil xmlutil = new XmlUtil(jsonStr);
        String return_code = xmlutil.getChildText("return_code");
        String result_code = xmlutil.getChildText("result_code");
        ActResultDto resultDto = new ActResultDto();
        resultDto.setData(jsonStr);
        return  resultDto;
    }



    public static String doPost(String url, String body) throws Exception {

        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File("E://apiclient_cert.p12"));
        keyStore.load(instream, "1418478402".toCharArray());//杩欓噷鍐欏
        instream.close();
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "1418478402".toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

     //   HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
       // CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

        CloseableHttpClient closeableHttpClient = HttpClients.custom() .setSSLSocketFactory(sslsf) .build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = null;
        String result = null;
        try {
            entity = new StringEntity(body, "utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse;
            httpResponse = closeableHttpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (Exception e) {
        }finally {
            if(closeableHttpClient != null){
                closeableHttpClient.close();
            }
        }
        return result;
    }


    public String getXml(HttpServletRequest request){

        String ip = IPUtil.getLocalIp(request);
        BigDecimal de = new BigDecimal(1);
        BigDecimal b = new BigDecimal(100);
        BigDecimal mony = de.multiply(b);

        String partnerKey=  "EF52S985TE3O6YP5974K3F7CWVD30T1D";
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("mch_appid", "wx4a1e117b08e36e0a");//bf16ff76b0128967860ec2aa97cd818d
        treeMap.put("mchid", "1418478402");
        treeMap.put("nonce_str", UUIDGenerator.generate());
        treeMap.put("partner_trade_no", "20171012165000002");  //商户订单号
        treeMap.put("openid", "oFL9AvwmmzmFSpNPKWZVXjpQjx3o");
        treeMap.put("check_name", "NO_CHECK");
/*        treeMap.put("re_user_name", "");*/
        treeMap.put("amount", mony.longValue() + "" );  //单位是分
        treeMap.put("desc","测试");
        treeMap.put("spbill_create_ip", ip);
        String sign =  getSignStr(treeMap, partnerKey);
        String signValue = DigestUtils.md5Hex(sign).toUpperCase();
        treeMap.put("sign", signValue);
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            xml.append("<" + entry.getKey() + ">")
                    .append("<![CDATA[" + entry.getValue() + "]]>").append("</" + entry.getKey() + ">");
            //xml.append("<" + entry.getKey() + ">").append(entry.getValue()).append("</" + entry.getKey() + ">\n");
        }
        xml.append("</xml>");
        return xml.toString();

    }

    /**
     *
     * @Title: sign
     * @Description: TODO(微信签名)
     * @param treeMap
     * @return
     * @return String    返回类型
     */
    public static String getSignStr(TreeMap<String, String> treeMap , String partnerKey){
        StringBuilder sb = new StringBuilder();
        for (String key : treeMap.keySet()) {
            sb.append(key).append("=").append(treeMap.get(key)).append("&");
        }
        sb.append("key=" + partnerKey);
        return sb.toString();
    }

    /**
     * 支付  充值流量 微信
     * @param request
     * @param levelId 充值流量级别ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/buyFlow.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.bug_flow, systemType = "0" ,deviceNo="{$2.deviceNo}")
    @ApiOperation(value = "支付  充值流量 微信", httpMethod = "POST", notes = "支付  充值流量 微信")
    public ActResultDto buyFlow(HttpServletRequest request ,
                                @ApiParam(required =true, name = "ID", value = "ID") Long levelId)throws  Exception{
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        Map mapP = new HashMap();
        String payTypeDesc = PayType.getNameByValue("17");
        mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        String ip = IPUtil.getLocalIp(request);
        //微信_H5充值
        return thirdPayService.weixinH5BuyFlow(token, ip, levelId);
    }

    /**
     * APP_ 充值流量
     * @param request 第三方支付
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/buyFlowApp.user" ,  method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "APP_ 充值流量", httpMethod = "POST", notes = "APP_ 充值流量")
    @Log(content="{logModel.payTypeDesc}，金额：{logModel.money},支付描述，{#.message}", type= LogType.third_pay, systemType = "0" ,deviceNo="{$2.deviceNo}")
    public ActResultDto thirdRechargePay(HttpServletRequest request ,
                                         @ApiParam(required =true, name = "ID", value = "ID") Long levelId)throws  Exception{
        ActResultDto actResultDto = null;
        AppUserIdentity token = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        RequestInfoContext.getRequestInfo().setTableType(LogTableType.order.getVal());
        Map mapP = new HashMap();
        String payTypeDesc = PayType.getNameByValue(LogTableType.order.getVal());
        mapP.put("payTypeDesc", "第三方支付:支付方式：" + payTypeDesc);
        RequestInfoContext.getRequestInfo().setIsCreateLog(false);
        RequestInfoContext.getRequestInfo().setLogModel(mapP);
        String ip = IPUtil.getLocalIp(request);
        return thirdPayService.buyFlowApp(token, ip, levelId);
    }

    private void addZaiXianUser(HttpServletRequest request,ActResultDto actResultDto,ThirdPayDto thirdPayDto,AppUserIdentity token, Course course){
        String v = request.getParameter("v");
        //根据版本，v1>1.6.1，判断是否写入联麦相关信息1.6.1之前是不能联麦的
        if (ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(actResultDto.getCode())
                && !PayType.weixin_h5.getValue().equals(thirdPayDto.getPayType())
                && Utility.isGreaterThan(v,"1.6.1" )) {
            redisUtil.sadd(RedisKey.join_room_user + thirdPayDto.getCourseId(), String.valueOf(token.getId()));
            //写联麦的状态
            LiveConnectRequest connect = new LiveConnectRequest();
            connect.setTeacher(course.getAppId());
            connect.setStudent(token.getId());
            connect.setApplyUser(token.getId());
            connect.setCourseId(thirdPayDto.getCourseId());
            connect.setStudentStatus("1");

            redisUtil.lpush(RedisKey.write_student_connect_online_status ,JsonUtil.toJson(connect) );
        }
    }
}
