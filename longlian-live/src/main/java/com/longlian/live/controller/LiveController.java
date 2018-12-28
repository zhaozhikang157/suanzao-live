package com.longlian.live.controller;

import cn.jpush.api.utils.StringUtils;
import com.github.pagehelper.StringUtil;
import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import com.huaxin.util.weixin.type.WechatQRCodeType;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.cmd.CmdExecuter;
import com.longlian.live.util.cmd.FFConverAudioCommand;
import com.longlian.live.util.cmd.FFMpegAudioCommand;
import com.longlian.live.util.cmd.ICommand;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.live.util.yunxin.YunXinUtil;
import com.longlian.live.util.yunxin.YunxinChatRoomUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播窗口相关方法
 * Created by lh on 2017-02-14.
 */
@Controller
@RequestMapping(value = "/")
public class LiveController {
    private static Logger log = LoggerFactory.getLogger(LiveController.class);
    @Autowired
    private YunxinChatRoomUtil yunxinChatRoomUtil;
    @Autowired
    CourseService courseService;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StudyRecordDetailService studyRecordDetailService;
    @Autowired
    EndLiveService endLiveService;
    @Autowired
    H5Controller h5Controller;
    @Autowired
    CourseManagerService courseManagerService;
    @Autowired
    AppUserCommonService appUserCommonService;
    @Autowired
    LiveService liveService;
    @Autowired
    VisitCourseRecordService visitCourseRecordService;
    @Autowired
    GagService gagService;
    @Autowired
    UserFollowWechatOfficialService userFollowWechatOfficialService;
    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    ThirdPayController thirdPayController;
    @Autowired
    MsgCancelService msgCancelService;
    @Autowired
    StudyRecordService studyRecordService;
    @Autowired
    SsoUtil ssoUtil;
    @Autowired
    EquipmentService equipmentService;
    @Autowired
    RoomFuncService roomFuncService;
    @Autowired
    WechatOfficialService wechatOfficialService;
    @Autowired
    LiveChannelService qiNiuliveChannelService;
    @Autowired
    AppUserService userService;
    @Autowired
    CourseRelayService courseRelayService;

    @Value("${publicConcern.url}")
    private String publicConcern;

    private String  ffmpegpath="/usr/local/bin/ffmpeg";


    /**
     * 跳转到直播窗口页面
     * @param request
     * @param courseId
     * @return isShare 分享的时候 , 如果该课程是免费,则直接进入直播室 "1"-为分享
     * @throws Exception
     */
    @RequestMapping(value = "weixin/index.user")
    @ApiOperation(value = "跳转到直播窗口页面", httpMethod = "GET", notes = "跳转到直播窗口页面")
    public ModelAndView live(HttpServletRequest request , HttpServletResponse response  ,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                             String invitationAppId , String isShare, Long seriesid) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);

        ModelAndView mv = new ModelAndView("func/weixin/live/index");
        //直播详情自动跳转过来的，需要处理购买流程,购买
        if("1".equals(isShare)){
            //需要进行支付处理
            ThirdPayDto dto = new ThirdPayDto();
            dto.setCourseId(courseId);
            if(seriesid != null && seriesid > 0) {dto.setCourseId(seriesid);}
            dto.setPayType(PayType.learn_coin_pay.getValue()); //学币支付
            dto.setIsBuy("1"); //支付
            if((token.getId()+"").equals(invitationAppId)) invitationAppId = "0";
            dto.setInvitationAppId(Long.valueOf(invitationAppId));
            try {
                ActResultDto resultDto = thirdPayController.thirdPay(request,dto);
                if("000000".equals(resultDto.getCode()) || "100008".equals(resultDto.getCode())){
                    log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享免费课,进入直播间,处理购买课程成功!");
                }else{
                    //分享处理报名记录失败
                    log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享课程失败,失败code="+resultDto.getCode());
                }
            }catch (Exception e){
                log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享课程失败,失败e="+e);
            }
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            course=courseService.getRelayCourse(courseId);
        }
        //如果已经删除，则跳到提示已删除页面
        if ("1".equals(course.getIsDelete())) {
            return h5Controller.notFound(course.getLiveTopic());
        }
        //判断是否已经加入课程了
        boolean isJoin = joinCourseRecordService.isJoinCourse(token.getId(), course);
        int isSuperAdmin = userService.findSystemAdminByUserId(token.getId());
        if(isSuperAdmin > 0){
            isJoin=true;
        }
        //没有报名,也不是老师自己,也不是试看,跳转到详情页面
        if (!isJoin && course.getAppId() != token.getId() && course.getTrySeeTime() <= 0) {
            response.sendRedirect("/weixin/courseInfo?id=" + courseId);
            return null;
        }

        //加入的才写入学习人数，写入课程学习记录

        if (isJoin && isSuperAdmin <= 0) {
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setAppId(token.getId());
            studyRecord.setCourseId(courseId);
            studyRecord.setCreateTime(new Date());
            studyRecord.setVirtualUser(false);
            studyRecord.setSeriesCourseId(course.getSeriesCourseId());
            redisUtil.lpush(RedisKey.ll_study_record_wait2db, JsonUtil.toJsonString(studyRecord));

        }

        getOutOfRoom(request, courseId, "0");

        //进入聊天室,没有关注需要自动关注
        /*boolean bo = userFollowService.isFollowRoom(course.getRoomId(),token.getId());
        if(!bo){
            userFollowService.follow(token.getId(), course.getRoomId());
        }*/

        //如果是竖屏，则跳转到竖屏界面
        if ("1".equals(course.getIsVerticalScreen())) {
            mv = new ModelAndView("func/weixin/live/vertical");
        }
        //如果是语音直播，则跳转到语音直播界面
        if ("1".equals(course.getLiveWay())) {
            mv = new ModelAndView("func/weixin/live/voice");
        }

        boolean isCourseFollow= userFollowService.isFollowRoom(course.getRoomId(), token.getId());//判断直播间是否关注过
        //传给前台参数
        String isFollow = token.getIsFollowLlWechat(); //0-没有 1-已关注
        mv.addObject("courseId",courseId);
        mv.addObject("isFollow",isFollow);
        mv.addObject("isCourseFollow",isCourseFollow);
        mv.addObject("userName" , token.getName());
        mv.addObject("userId" , token.getId());
        mv.addObject("photo" , token.getPhoto());
        mv.addObject("chatRoomId" ,course.getChatRoomId());
        mv.addObject("roomId" ,course.getRoomId());
        mv.addObject("liveTopic",course==null?"":course.getLiveTopic());
        mv.addObject("invitationAppId",invitationAppId == null ? "":invitationAppId);
        mv.addObject("sourcesId",course.getSeriesCourseId());
        mv.addObject("isEnd",course.getEndTime() != null ? "1" : "0");
        //如果已加入，则试看为0
        mv.addObject("trySeeTime",isJoin ? 0 : course.getTrySeeTime());
        mv.addObject("isVerticalScreen" , course.getIsVerticalScreen()); //0-横屏 1-竖屏

        //竖屏处理封面图片， 将封面图片改为高斯模糊
        if ("1".equals(course.getIsVerticalScreen()) && !StringUtils.isEmpty(course.getVerticalCoverssAddress())) {
            course.setCoverssAddress(course.getVerticalCoverssAddress());
        }
        //没有，则给一张默认图片
        if (StringUtils.isEmpty(course.getCoverssAddress())) {
            course.setCoverssAddress("/web/res/image/course_default.png");
        }
        mv.addObject("coverssAddress", course.getCoverssAddress());

        //取直播地址

        Course courseL = courseService.getCourseFromRedis(courseId);
        Course oriCourse = null;
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            courseL=courseService.getRelayCourse(courseId);
            CourseRelayDto cDto=courseRelayService.queryById(courseId);
            oriCourse = courseService.getCourseFromRedis(cDto.getOriCourseId());
            mv.addObject("oriCourseId",cDto.getOriCourseId());
            Map tmpMap = this.courseService.findCourseInfoById(cDto.getOriCourseId());
            mv.addObject("oriRoomId",tmpMap.get("roomId"));
            mv.addObject("oriAppId",cDto.getOriAppId());
            isCourseFollow= userFollowService.isFollowRoom(oriCourse.getRoomId(), token.getId());//判断直播间是否关注过
            mv.addObject("isCourseFollow",isCourseFollow);
        }else{
            oriCourse = new Course();
            oriCourse.setId(courseL.getId());
            oriCourse.setVideoAddress(courseL.getVideoAddress());
            oriCourse.setIsRecorded(courseL.getIsRecorded());
        }
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(oriCourse);
        String playAddr1 = "";
        String playAddr3 = "";
        //根据取得的直播通道,引出播放地址
        if (liveChannel != null) {
            playAddr1 = liveChannel.getPlayAddr1();
            playAddr3 = liveChannel.getPlayAddr3();
        }
        //将m3u8地址给jsp页面
        if (course.getEndTime() != null) {
            mv.addObject("playAddress",oriCourse.getVideoAddress());
        } else {
            mv.addObject("playAddress",playAddr3);
        }
        mv.addObject("isRecorded", course.getIsRecorded());
        //设置云信聊天室相关1
        Map chatRoom = new HashMap();
        //云信appkey
        mv.addObject("appKey", YunXinUtil.appKey);
        //chatRoom.put("appSecret", YunXinUtil.appSecret);
        //根据云信accid和云信聊天室ID，取得聊天室的地址(是个数组)
        String yunxinToken = token.getYunxinToken();
        //如果发现自己还没有云信token，则应该去创建一个yuntoken
        if (StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = appUserCommonService.createYunxinUser(token.getId() , token.getName() , token.getPhoto());
            if ("-1".equals(yunxinToken)) {
                response.sendRedirect("/weixin/courseInfo?id=" + courseId);
                return null;
            }
        }
        if (course.getEndTime() != null) {
            //直播已结束
            mv.addObject("roomAddr",  "[]");
        } else {
            //直播未结束
            mv.addObject("roomAddr", JsonUtil.toJson(liveRoomService.getRoomAddress(String.valueOf(token.getId()) , String.valueOf(course.getChatRoomId()))));
        }
        mv.addObject("yunxinToken", yunxinToken);

        return  mv;
    }

    @RequestMapping(value="/live/queryIsFollow",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "判断是否关注直播间", httpMethod = "GET", notes = "判断是否关注直播间")
    public ActResultDto queryIsFollow(String token,String roomId){
        ActResultDto result = new ActResultDto();
        if (token == null || roomId==null) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            AppUser appUser = SpringMVCIsLoginInterceptor.getUserTokenModelByToken(token);
            if(appUser==null){
                result.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
                result.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            }else{
                boolean isFollow= userFollowService.isFollowRoom(Long.parseLong(roomId), appUser.getId());//判断直播间是否关注过
                result.setData(isFollow);
            }

        }
        return result;
    }

    @RequestMapping(value="/live/insertEquipmentInfo",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "添加设备信息", httpMethod = "GET", notes = "添加设备信息")
    public ActResultDto insertEquipmentInfo(String token,String equipmentId,String equipmentType){
        ActResultDto result = new ActResultDto();
        if (token == null || equipmentId==null || equipmentType==null ) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        }else{
            AppUser appUser = SpringMVCIsLoginInterceptor.getUserTokenModelByToken(token);
            if(appUser==null){
                result.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
                result.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            }else{
                //判断是否存在  如果存在 更新
                Equipment equipment=equipmentService.selectByEquipmentId(equipmentId);

                Equipment newEquipment=new Equipment();
                newEquipment.setAppId(appUser.getId());
                newEquipment.setName(appUser.getName());
                newEquipment.setEquipmentId(equipmentId);
                newEquipment.setEquipmentType(equipmentType);
                newEquipment.setCreateTime(new Date());
                if(equipment==null){
                    equipmentService.insertEquipment(newEquipment);
                }else{
                    equipmentService.updateByEquipmentId(newEquipment);
                }
            }

        }
        return result;
    }

    /**
     * 跳转到直播窗口页面
     * @param request
     * @param courseId
     * @return isShare 分享的时候 , 如果该课程是免费,则直接进入直播室 "1"-为分享
     * @throws Exception
     */
    @RequestMapping(value = "weixin/voicelistening.user")
    @ApiOperation(value = "跳转到直播窗口页面", httpMethod = "GET", notes = "跳转到直播窗口页面")
    public ModelAndView voicelistening(HttpServletRequest request , HttpServletResponse response  ,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                             String invitationAppId , String isShare, Long seriesid) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);

        ModelAndView mv = new ModelAndView("/func/weixin/live/voice_listening");
        //直播详情自动跳转过来的，需要处理购买流程,购买
        if("1".equals(isShare)){
            //需要进行支付处理
            ThirdPayDto dto = new ThirdPayDto();
            dto.setCourseId(courseId);
            if(seriesid != null && seriesid > 0) {dto.setCourseId(seriesid);}
            dto.setPayType(PayType.learn_coin_pay.getValue()); //学币支付
            dto.setIsBuy("1"); //支付
            if((token.getId()+"").equals(invitationAppId)) invitationAppId = "0";
            dto.setInvitationAppId(Long.valueOf(invitationAppId));
            try {
                ActResultDto resultDto = thirdPayController.thirdPay(request,dto);
                if("000000".equals(resultDto.getCode()) || "100008".equals(resultDto.getCode())){
                    log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享免费课,进入直播间,处理购买课程成功!");
                }else{
                    //分享处理报名记录失败
                    log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享课程失败,失败code="+resultDto.getCode());
                }
            }catch (Exception e){
                log.info("登录人:"+token.getId()+" 分享人:"+invitationAppId+"分享课程失败,失败e="+e);
            }
        }
        Course course = courseService.getCourseFromRedis(courseId);

        //如果已经删除，则跳到提示已删除页面
        if ("1".equals(course.getIsDelete())) {
            return h5Controller.notFound(course.getLiveTopic());
        }
        //判断是否已经加入课程了
        boolean isJoin = joinCourseRecordService.isJoinCourse(token.getId() ,   course);
        //没有报名,也不是老师自己,也不是试看,跳转到详情页面
        if (!isJoin && course.getAppId() != token.getId() && course.getTrySeeTime() <= 0) {
            response.sendRedirect("/weixin/courseInfo?id=" + courseId);
            return null;
        }
        //加入的才写入学习人数，写入课程学习记录
        if (isJoin) {
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setAppId(token.getId());
            studyRecord.setCourseId(courseId);
            studyRecord.setCreateTime(new Date());
            studyRecord.setVirtualUser(false);
            studyRecord.setSeriesCourseId(course.getSeriesCourseId());
            redisUtil.lpush(RedisKey.ll_study_record_wait2db, JsonUtil.toJsonString(studyRecord));

        }
        getOutOfRoom(  request ,  courseId ,"0");


        //进入聊天室,没有关注需要自动关注
        /*boolean bo = userFollowService.isFollowRoom(course.getRoomId(),token.getId());
        if(!bo){
            userFollowService.follow(token.getId(), course.getRoomId());
        }*/

        //如果是语音直播，则跳转到语音直播界面
//        if ("1".equals(course.getLiveWay())) {
//            mv = new ModelAndView("func/weixin/live/voice_listening");
//        }
        //传给前台参数
        String isFollow = token.getIsFollowLlWechat(); //0-没有 1-已关注
        mv.addObject("courseId",courseId);
        mv.addObject("isFollow",isFollow);
        mv.addObject("userName" , token.getName());
        mv.addObject("userId" , token.getId());
        mv.addObject("photo" , token.getPhoto());
        mv.addObject("chatRoomId" ,course.getChatRoomId());
        mv.addObject("liveTopic",course==null?"":course.getLiveTopic());
        mv.addObject("invitationAppId",invitationAppId == null ? "":invitationAppId);
        mv.addObject("sourcesId",course.getSeriesCourseId());
        mv.addObject("isEnd",course.getEndTime() != null ? "1" : "0");
        //如果已加入，则试看为0
        mv.addObject("trySeeTime",isJoin ? 0 : course.getTrySeeTime());
        mv.addObject("isVerticalScreen" , course.getIsVerticalScreen()); //0-横屏 1-竖屏

        //没有，则给一张默认图片
        if (StringUtils.isEmpty(course.getCoverssAddress())) {
            course.setCoverssAddress("/web/res/image/course_default.png");
        }
        mv.addObject("coverssAddress",course.getCoverssAddress() );

        mv.addObject("isRecorded", course.getIsRecorded());
        //设置云信聊天室相关1
        Map chatRoom = new HashMap();
        //云信appkey
        mv.addObject("appKey", YunXinUtil.appKey);
        //chatRoom.put("appSecret", YunXinUtil.appSecret);
        //根据云信accid和云信聊天室ID，取得聊天室的地址(是个数组)
        String yunxinToken = token.getYunxinToken();
        //如果发现自己还没有云信token，则应该去创建一个yuntoken
        if (StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = appUserCommonService.createYunxinUser(token.getId() , token.getName() , token.getPhoto());
            if ("-1".equals(yunxinToken)) {
                response.sendRedirect("/weixin/courseInfo?id=" + courseId);
                return null;
            }
        }
        if (course.getEndTime() != null) {
            //直播已结束
            mv.addObject("roomAddr",  "[]");
        } else {
            //直播未结束
            mv.addObject("roomAddr", JsonUtil.toJson(liveRoomService.getRoomAddress(String.valueOf(token.getId()) , String.valueOf(course.getChatRoomId()))));
        }
        mv.addObject("yunxinToken", yunxinToken);

        return  mv;
    }


    @RequestMapping(value = "teacher/live/getCourseUsers.user")
    @ResponseBody
    @ApiOperation(value = "取得课程相关信息-处理延时未返回数据问题", httpMethod = "GET", notes = "处理延时未返回数据问题")
    public ActResultDto getCourseUsers(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception {
        ActResultDto res = new ActResultDto();
        log.info("处理延时未返回数据问题 接收参数courseId："+courseId);
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(course!=null){
            log.info("处理延时未返回数据问题 参数SeriesCourseId："+course.getSeriesCourseId());
        }else{
            log.info("获取课程为空");
        }
        Map map = new HashMap();

        if (course.getSeriesCourseId() >0) {
            List<Map> showUsers = liveService.getShowUsers(course.getId(), true);
            if(showUsers==null||showUsers.size()==0){
                map.put("userCount",0);
            }else {
                map.put("userCount", studyRecordService.getCountByCourseId(course.getId()));
            }
            map.put("showUsers", showUsers);
        } else {
            List<Map> showUsers = liveService.getShowUsers(course.getId(), false);
            if(showUsers==null||showUsers.size()==0){
                map.put("userCount",0);
            }else {
                map.put("userCount", joinCourseRecordService.getCountByCourseId(course.getId()));
            }
            map.put("showUsers", showUsers);

        }
        res.setData(map);
        return res;
    }


        /**
         * 取得课程相关信息
         * @param request
         * @param courseId
         * @return
         * @throws Exception
         */
    @RequestMapping(value = "weixin/live/getCourse.user")
    @ResponseBody
    @ApiOperation(value = "取得课程相关信息", httpMethod = "GET", notes = "取得课程相关信息")
    public ActResultDto getCourse4Web(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }

        long idL = 0;  //原始课id
        Course course ;
        if(courseId!=0 && String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelay = courseRelayService.queryById(courseId);
            idL=courseRelay.getOriCourseId();
            course=courseService.getRelayCourse(courseId);
//            //如果是语音课，用原老师的appid
//            if("1".equals(course.getLiveWay())){
//                course.setAppId(courseRelay.getOriAppId());
//            }
        }else{
            idL = Long.valueOf(courseId);
            course = courseService.getCourseFromRedis(idL);
        }

        Map result = new HashMap();
        //取直播地址
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        String playAddr1 = "";
        String playAddr3 = "";
        //根据取得的直播通道,引出播放地址
        if (liveChannel != null) {
            playAddr1 = liveChannel.getPlayAddr1();
            playAddr3 = liveChannel.getPlayAddr3();
        }
        course.setHlsLiveAddress(playAddr3);
        course.setLiveAddress(playAddr1);

        //如果是系列课程,把系列课程详情查询出来
        if (course.getSeriesCourseId() > 0 ) {
            Course seriesCourse =  courseService.getCourseFromRedis(course.getSeriesCourseId());
            result.put("seriesCourse", seriesCourse);
            course.setChargeAmt(seriesCourse.getChargeAmt());
            //是否支持邀请码，取系列课壳的字段值
            course.setIsInviteCode(seriesCourse.getIsInviteCode());
        }

        result.put("course", course);
        //获取简介图片列表
        List<CourseImg> courseImgList = courseService.getCourseImgList(idL);
        result.put("courseImgList",courseImgList);
        //查出课程课件
        result.put("courseWare", courseService.getCourseWare(idL));

        result.put("coverssAddress", course.getCoverssAddress());
        //是否在连接中，默认为连接中
        result.put("isConnected", "1");

        //从redis中获取相关的课程,课件页码（老师现在翻到多少页了）
        String classIndex = redisUtil.get(RedisKey.ll_course_class_index + idL);
        if(StringUtils.isEmpty(classIndex)){
            classIndex = "0";
        }
        //课件默认打开页码
        result.put("courseClassIndex", classIndex);
        //查询直播间头部信息（直播间人数、老师收益、直播默认显示人的头像）
        liveService.setLiveInfo(result, course);
        //查询设置的管理员
        String str="";
        if( "1".equals(course.getLiveWay())){
            str = courseManagerService.findAllManagerRealByCourseId(idL);
        }else{
            str = courseManagerService.findAllManagerRealByCourseId(courseId);
        }

        result.put("managerId",str);
        result.put("msgCancelId","");
        //设置的禁言ID
        result.put("gagUserId",gagService.findUserIdByCourseId(courseId));
        //查询老师相关信息
        Map teacher = appUserCommonService.getUserInfoFromRedis(course.getAppId());
        if (teacher != null) {
            result.put("teacherName",teacher.get("name"));
            result.put("teacherPhoto",teacher.get("photo"));
        } else {
            result.put("teacherName","");
            result.put("teacherPhoto","");
        }
        //系列课的单节课是不是从详情页面进入的
        if (course.getSeriesCourseId() != 0 ) {
            //课程id修改为实际ID（转播课ID就是转播课ID）
            visitCourseRecordService.insertRecord(token.getId(), courseId, 0l, "0", course.getSeriesCourseId() , "0" );
        }
        //生成需要关注的二维码图片
        result.putAll(userFollowWechatOfficialService.getUserFollowInfoByCourse(course.getId(),token.getId() , course.getRoomId() , course.getAppId(),course.getLiveWay()));;
        result.put("loginMobile",token.getMobile());
        //如果直播 还没结束，则需要记录直播间是从微信端进入的
        if(course.getEndTime() == null){
            //写入客户最后一次登录的端口，说明用户是app端
            redisUtil.hset(RedisKey.course_user_client_type + course.getId() , String.valueOf(token.getId()), "0");
            redisUtil.expire(RedisKey.course_user_client_type + course.getId() , 60 * 60 * 48);
        }
        //查询老师是否在正输入状态
        String teacherInputing = redisUtil.get(RedisKey.course_teacher_inputing + course.getChatRoomId());
        if (StringUtils.isEmpty(teacherInputing)) {
            teacherInputing = "0";
        }
        result.put("teacherInputing",teacherInputing);
        result.put("serverTime", System.currentTimeMillis());
        return ActResultDto.success().setData(result);
    }

    @Autowired
    MixLiveService mixLiveService;

    @RequestMapping(value = "live/getOutOfRoom.user")
    @ResponseBody
    @ApiOperation(value = "退出直播间", httpMethod = "GET", notes = "退出直播间")
    public ActResultDto getOutOfRoom(HttpServletRequest request ,Long courseId , String isConnecting) {
        //判断参数
        if (courseId == null || courseId ==0) {
            return ActResultDto.fail(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }

        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        redisUtil.srem(RedisKey.join_room_user + courseId , String.valueOf(token.getId()));
        //学生写状态
        Course course =courseService.getCourseFromRedis(courseId);
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            course=courseService.getRelayCourse(courseId);
        }
        if (course.getAppId() != token.getId()) {
            LiveConnectRequest connect = new LiveConnectRequest();
            connect.setTeacher(course.getAppId());
            connect.setStudent(token.getId());
            connect.setApplyUser(token.getId());
            connect.setCourseId(courseId);
            connect.setStudentStatus("0");

            redisUtil.lpush(RedisKey.write_student_connect_online_status ,JsonUtil.toJson(connect) );
        }
        //如果是正在连麦的
        //断开正在连麦的
        if ("1".equals(isConnecting)) {
            LiveConnectRequest liveConnectRequest = new LiveConnectRequest();
            liveConnectRequest.setStudent(token.getId());
            liveConnectRequest.setTeacher(course.getAppId());
            liveConnectRequest.setCourseId(courseId);
            liveConnectRequest.setCloseTime(new Date());
            liveConnectRequest.setStatus(ConnectStatus.disconnected.getValue());
            mixLiveService.closeConnectRequest(liveConnectRequest);
        }


        if(course.getAppId() == token.getId()){//判断是老师本人
            Map msg = new HashMap();
            msg.put("type", YunxinCustomMsgType.TEARCHER_OUT_OF_ROOM.getType());
            Map val = new HashMap();
            val.put("value", course.getAppId());
            msg.put("data", val);
            log.info(JsonUtil.toJson(msg));
            yunxinChatRoomUtil.sendMsg(String.valueOf(course.getChatRoomId()), String.valueOf(course.getAppId()), "100", JsonUtil.toJson(msg));
        }
        return ActResultDto.success();
    }

    @Autowired
    private MobileVersionService mobileVersionService;
    /**
     * 取得课程相关信息
     * @param request
     * @param courseId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "live/getCourse4App.user")
    @ResponseBody
    @ApiOperation(value = "取得课程相关信息", httpMethod = "GET", notes = "取得课程相关信息")
    public ActResultDto getCourse4App(HttpServletRequest request ,Long courseId) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        int isSuperAdmin = userService.findSystemAdminByUserId(token.getId());
        //取直播地址
        LiveChannel liveChannel = qiNiuliveChannelService.getCourseLiveAddr(course);
        boolean isJoin = joinCourseRecordService.isJoinCourse(token.getId(), course);
        if(isSuperAdmin <= 0) {
            //判断是否报名,且不是老师自己,且不是试看
            if (!isJoin && course.getAppId() != token.getId() && course.getTrySeeTime() <= 0) {
                res.setCode(ReturnMessageType.NO_JOIN_COURSE.getCode());
                res.setMessage(ReturnMessageType.NO_JOIN_COURSE.getMessage());
                return res;
            }
        } else {
            isJoin = true;
        }
        if (isJoin) {
            //前端要求，TrySeeTime买过了，将试看时间改为0传给前台，表示自己是购买用户，如果不为0，则说明是试看用户
            course.setTrySeeTime(0l);
            //学习记录,加入的才写入学习人数
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setAppId(token.getId());
            studyRecord.setCourseId(courseId);
            studyRecord.setCreateTime(new Date());
            studyRecord.setVirtualUser(false);
            studyRecord.setSeriesCourseId(course.getSeriesCourseId());
            redisUtil.lpush(RedisKey.ll_study_record_wait2db, JsonUtil.toJsonString(studyRecord));

            String v = request.getParameter("v");
            //根据版本，v1>1.6.1，判断是否写入联麦相关信息1.6.1之前是不能联麦的
            if (Utility.isGreaterThan(v, "1.6.1")) {
                redisUtil.sadd(RedisKey.join_room_user + courseId, String.valueOf(token.getId()));
                //写联麦的状态
                LiveConnectRequest connect = new LiveConnectRequest();
                connect.setTeacher(course.getAppId());
                connect.setStudent(token.getId());
                connect.setApplyUser(token.getId());
                connect.setCourseId(courseId);
                connect.setStudentStatus("1");

                redisUtil.lpush(RedisKey.write_student_connect_online_status, JsonUtil.toJson(connect));
            }
        }
        String playAddr1 = "";
        String playAddr3 = "";
        //根据取得的直播通道,引出播放地址
        if (liveChannel != null) {
            playAddr1 = liveChannel.getPlayAddr1();
            playAddr3 = liveChannel.getPlayAddr3();
        }
        //m3u8地址
        course.setHlsLiveAddress(playAddr3);
        //rtmp地址
        course.setLiveAddress(playAddr1);
        Map result = new HashMap();
        Map r = new HashMap();
        result.put("id",course.getId());
        result.put("appId",course.getAppId());
        result.put("roomId",course.getRoomId());
        result.put("startTime",course.getStartTime());
        result.put("liveTopic",course.getLiveTopic());
        result.put("liveWay",course.getLiveWay());
        result.put("trySeeTime",course.getTrySeeTime());
        result.put("isRecorded",course.getIsRecorded());
        result.put("clearTime",course.getCleanScreenTime());
        //直播间是否有连麦权限
        boolean hasConnectAuth = roomFuncService.isRoomFunc(course.getRoomId() , FuncCode.live_connect.getValue());
        result.put("hasConnectAuth",hasConnectAuth ? "1" : "0");
        //直播是否加水印
        boolean liveWatermark = roomFuncService.isRoomFunc(course.getRoomId() , FuncCode.live_watermark.getValue());
        result.put("liveWatermark",liveWatermark ? "1":"0");
        //老师是否允许学生发送连麦请求
        result.put("isCanConnect",course.getCanConnect());


        //竖屏处理封面图片， 将封面图片改为高斯莫负
        if ("1".equals(course.getIsVerticalScreen()) && !StringUtils.isEmpty(course.getVerticalCoverssAddress())) {
            course.setCoverssAddress(course.getVerticalCoverssAddress());
        }

        result.put("coverssAddress",course.getCoverssAddress());
        result.put("liveAddress",course.getLiveAddress());
        result.put("chatRoomId",course.getChatRoomId());
        result.put("videoAddress",course.getVideoAddress() == null ? "rec" : course.getVideoAddress());
        //从redis中获取相关的课程,课件页码（老师现在翻到多少页了）
        String classIndex = redisUtil.get(RedisKey.ll_course_class_index + course.getId());
        if(StringUtils.isEmpty(classIndex)){
            classIndex = "0";
        }
        result.put("courseClassIndex",classIndex);
        r.put("course", result);
        //课件
        r.put("courseWare", courseService.getCourseWare(courseId));
        liveService.setLiveInfo(r, course);
        //管理员
        String str = courseManagerService.findAllManagerRealByCourseId(courseId);
        r.put("managerId",str);
        r.put("msgCancelId","");
        //老师相关信息
        Map teacher = appUserCommonService.getUserInfoFromRedis(course.getAppId());
        if (teacher != null) {
            r.put("teacherName",teacher.get("name"));
            r.put("teacherPhoto",teacher.get("photo"));
        } else {
            r.put("teacherName","");
            r.put("teacherPhoto","");
        }
        r.put("gagUserId",gagService.findUserIdByCourseId(courseId));
        r.put("serverTime", System.currentTimeMillis());


        //系列课的单节课是不是从详情页面进入的,把所属的系列课插入相关学习记录(所属系列课人数数量加1)
        if (course.getSeriesCourseId() != 0 ) {
            //课程id修改为实际ID（转播课ID就是转播课ID）
            visitCourseRecordService.insertRecord(token.getId(), courseId, 0l, "0", course.getSeriesCourseId(), "0");
        }
        if(course.getEndTime() == null){
            //写入客户最后一次登录的端口，说明用户是app端
            redisUtil.hset(RedisKey.course_user_client_type + course.getId() , String.valueOf(token.getId()), "1");
            redisUtil.expire(RedisKey.course_user_client_type + course.getId() , 60 * 60 * 48);
        }
        String teacherInputing = redisUtil.get(RedisKey.course_teacher_inputing + course.getChatRoomId());
        if (StringUtils.isEmpty(teacherInputing)) {
            teacherInputing = "0";
        }
        //查询老师是否在正输入状态
        result.put("teacherInputing",teacherInputing);
        //从redis中获取相关的课程,课件页码（老师现在翻到多少页了）写重了
        String index = redisUtil.get(RedisKey.ll_course_class_index + courseId);
        if(StringUtils.isNotEmpty(index)){
            r.put("classIndex",index);
        }else{
            r.put("classIndex",0);
        }
        return ActResultDto.success().setData(r);
    }


    /**
     * 直播流连接情况
     * @param request
     * @return
     */
    @RequestMapping(value = "live/isConnected.user")
    @ResponseBody
    @ApiOperation(value = "直播流连接情况", httpMethod = "GET", notes = "直播流连接情况")
    public ActResultDto isConnected(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception {
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0 ) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        String result  = redisUtil.get(RedisKey.ll_course_live_connected  + courseId );
        Course course = courseService.getCourseFromRedis(courseId);
        //如果不是直播、或者直播已结束
        if (course != null && (course.getEndTime() != null || "1".equals(course.getIsRecorded()))) {
            return res.setData("1");
        }


        //未连接状态
        if (StringUtils.isEmpty(result)) {
            result = "0";
        }
        return res.setData(result);
    }

    /**
     * 直播流混流结果通知
     * @param request
     * @return
     */
    @RequestMapping(value = "live/mixResultNotify")
    @ResponseBody
    @ApiOperation(value = "直播流混流结果通知", httpMethod = "GET", notes = "直播流混流结果通知")
    public ActResultDto mixResultNotify(HttpServletRequest request ) throws Exception {
        return ActResultDto.success();
    }
    /**
     * 直播流混流结果通知
     * @param request
     * @return
     */
    @RequestMapping(value = "live/mixCanUseNotify")
    @ResponseBody
    @ApiOperation(value = "直播流混流结果通知", httpMethod = "GET", notes = "直播流混流结果通知")
    public ActResultDto mixCanUseNotify(HttpServletRequest request ) throws Exception {
        return ActResultDto.success();
    }

    /**
     * 直播流jianhuang通知
     * @return
     */
    @RequestMapping(value = "live/yellowNotify", method = RequestMethod.POST, consumes = "text/plain")
    @ResponseBody
    @ApiOperation(value = "直播流鉴黄通知", httpMethod = "POST", notes = "直播流鉴黄通知")
    public ActResultDto yellowNotify(@ApiParam(required = true,name = "鉴黄内容",value = "鉴黄内容")String str) throws Exception {
        if(Utility.isNullorEmpty(str)){
            return ActResultDto.success();
        }
        redisUtil.lpush(RedisKey.ll_live_img_shot_wait2db,  new String(Base64Utils.decodeFromString(str)));
        return ActResultDto.success();
    }
//

//    /**
//     * 直播流jianhuang通知
//     * @return
//     */
//    @RequestMapping(value = "live/yellowNotify")
//    @ResponseBody
//    public ActResultDto yellowNotify(HttpServletRequest req ) throws Exception {
//        return ActResultDto.success();
//    }

    /**
     * 直播结束-老师调 用
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "teacher/live/endLive.user")
    @ResponseBody
    @ApiOperation(value = "直播结束", httpMethod = "GET", notes = "直播结束")
    public ActResultDto endLive(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception{
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }

        endLiveService.endLive(courseId , token.getId());
        String courseKey = RedisKey.ll_course + courseId;
        redisUtil.del(courseKey);
        SystemLogUtil.saveSystemLog(LogType.course_user_end.getType(), "0"
                , token.getId(), String.valueOf(token.getName())
                , String.valueOf(courseId), "课程：" + courseId + "已被老师关闭");
        return ActResultDto.success();
    }

    /**
     * 直播结束-老师调 用
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "weixin/live/endLive.user")
    @ResponseBody
    @ApiOperation(value = "直播结束-老师调用", httpMethod = "GET", notes = "直播结束-老师调用")
    public ActResultDto endWeixinLive(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception{
        return this.endLive(request ,courseId);
    }

    /**
     * 播放结束-学生调用
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "weixin/live/endPlay.user")
    @ResponseBody
    @ApiOperation(value = "直播结束-学生调用", httpMethod = "GET", notes = "直播结束-学生调用")
    public ActResultDto endPlay(HttpServletRequest request ,
                                @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                @ApiParam(required = true,name = "播放时段",value = "播放时段")Long studyEndPoint, String isEnd) throws Exception{
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        //判断参数
        if (studyEndPoint == null || studyEndPoint ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        //直播已结束
        if (course.getEndTime() != null) {
            StudyRecordDetail studyRecordDetail = new StudyRecordDetail();
            studyRecordDetail.setIsEnd(isEnd == null ? "0":isEnd);
            studyRecordDetail.setStudyEndPoint(studyEndPoint);
            studyRecordDetail.setAppId(token.getId());
            studyRecordDetail.setCourseId(courseId);
            //修改最新的一条记录，把结束点放上去
            studyRecordDetailService.updateStudyRecordDetailRec(studyRecordDetail);
        }
        //直播未结束，不处理
        return ActResultDto.success();
    }

    /**
     * 老师端取得课程的推流地址
     **/
    @RequestMapping(value = "teacher/live/getPushAddr.user")
    @ResponseBody
    @ApiOperation(value = "老师端取得课程的推流地址", httpMethod = "GET", notes = "老师端取得课程的推流地址")
    public ActResultDto getCoursePushAddr(
            @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
            @ApiParam(required = true,name = "设备模型",value = "设备模型")String machineModel,HttpServletRequest request) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto dto = courseService.getCoursePushAddr(courseId, token.getId(), machineModel);
        Map map = (Map)dto.getExt();
        String msgCancelId = "";
        try {
            if(courseId != null && courseId > 0){
                msgCancelId = msgCancelService.findMsgCancel(courseId);
            }
            map.put("msgCancelId", msgCancelId);
        }catch (Exception e){
            map.put("msgCancelId", msgCancelId);
        }
        return dto;

    }

    /**
     * 取得课程的推流地址
     **/
    @RequestMapping(value = "weixin/live/getPushAddr.user")
    @ResponseBody
    @ApiOperation(value = "取得课程的推流地址", httpMethod = "GET", notes = "取得课程的推流地址")
    public ActResultDto getCourseLiveAddr(@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) throws Exception {
        return courseService.getCourseLiveAddr(courseId);
    }
    /**
     * 取得更多用户列表
     **/
    @RequestMapping(value = "live/getUsers.user")
    @ResponseBody
    @ApiOperation(value = "取得更多用户列表", httpMethod = "GET", notes = "取得更多用户列表")
    public ActResultDto getUsers(@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,Integer offset , Integer pageSize) throws Exception {
        //判断参数
        ActResultDto res = new ActResultDto();
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        if (offset == null) {
            offset = 0;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        //第一页取19个
        if (offset == 0) {
            pageSize =  pageSize -1 ;
        }

        return liveService.getUsersByOffset(courseId, offset, pageSize);
    }

    /**
     * 取得更多用户列表
     **/
    @RequestMapping(value = "live/getAllUsers.user")
    @ResponseBody
    @ApiOperation(value = "取得更多用户列表", httpMethod = "GET", notes = "取得更多用户列表")
    public ActResultDto getAllUsers(HttpServletRequest request ,
                                    @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                    @ApiParam(required = true,name = "用户ids",value = "用户ids")String ids, Integer pageSize) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        //判断参数
        ActResultDto res = new ActResultDto();
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        return liveService.getAllUsers(courseId, ids, pageSize, token);
    }

    public static Map<String , String> imageContentType = new HashMap();
    static {
        imageContentType.put("image/bmp" , "bmp");
        imageContentType.put("image/gif" , "gif");
        imageContentType.put("image/jpeg" , "jpg");
        imageContentType.put("image/png" , "png");
        imageContentType.put("image/tiff" , "tiff");
        imageContentType.put("audio/amr", "amr");
    }

    public long getDur(String serviceId , byte[] bytes) {
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        File file = null;
        long ls = 0 ;
        try {

            File temp = new File("/temp");
            if (!temp.exists()) temp.mkdir();
            file = new File("/temp/" + serviceId);
            log.info("创建文件");
            output = new FileOutputStream(file);
            bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedOutput != null) {
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        log.info("读取时长：");
        if (file != null) {
            try {
                ls = (long)getAmrDuration(file) * 1000l;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (file != null) {
                    file.delete();
                }
            }
            log.info("时长：" + ls);
        }
        return ls;
    }
    /**
     * 得到amr的时长
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static int getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
                0, 0 };
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;

            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }

            duration += frameCount * 20;// 帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return (int)((duration/1000)+1);
    }
    /**
     * 微信发送图片，语音
     * @param request
     * @param serviceId
     * @return
     */
    @RequestMapping("live/getUrl.user")
    @ResponseBody
    @ApiOperation(value = "微信发送图片，语音", httpMethod = "GET", notes = "微信发送图片，语音")
    public ActResultDto getWeiXinImg(HttpServletRequest request , String serviceId , String roomId , String type , String uuid ) throws IOException {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);

        Map map = weixinUtil.getTemporaryMediaMapByMediaid(serviceId);
        if (map == null || map.get("object") == null) return ActResultDto.fail(ReturnMessageType.SEND_FILE_FAIL.getCode());

        byte[] bytes = (byte[]) map.get("object");
        String contentType = (String)map.get("contentType");
        log.info("返回类型：" + contentType);

        String content = Base64Utils.encodeToString(bytes);
        String url = yunxinChatRoomUtil.upload(content, token.getId(), roomId );

        if(StringUtils.isNotEmpty(url)){
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(url);


            Map body = new HashMap();
            body.put("name" , serviceId);
            body.put("url" , url);
            body.put("md5" , DigestUtils.md5DigestAsHex(bytes));
            body.put("size" ,bytes.length);
            //图片
            if ("1".equals(type)) {
                Image img = ImageIO.read(new ByteArrayInputStream(bytes));
                String ext = "jpg";

                if (contentType != null && imageContentType.get(contentType) != null) {
                    ext = imageContentType.get(contentType);
                }

                body.put("ext" , ext);
                try {
                    body.put("w" , img.getWidth(null));
                    body.put("h" ,  img.getHeight(null));
                } catch (Exception ex) {}
            } else  if ("2".equals(type)) {
//                            "dur":4551,        //语音持续时长ms
//                            "ext":"aac",        //语音消息格式，只能是aac格式
                body.put("dur" , getDur(  serviceId , bytes));
                body.put("ext" , "amr");
            } else {
                      //  "ext":"ttf",    //文件后缀类型
            }
            if(StringUtil.isNotEmpty(uuid)) {
                yunxinChatRoomUtil.sendMsgWithUUID(roomId ,String.valueOf(token.getId())  , type, JsonUtil.toJson(body) , uuid );
            } else {
                yunxinChatRoomUtil.sendMsg(roomId ,String.valueOf(token.getId())  , type, JsonUtil.toJson(body) );
            }

        }
        return resultDto;
    }

    /**
     * 微信发送图片，语音
     * @param request
     * @param serviceId
     * @return
     */
    @RequestMapping("live/getAudioUrl.user")
    @ResponseBody
    @ApiOperation(value = "微信发送图片，语音", httpMethod = "GET", notes = "微信发送图片，语音")
    public ActResultDto getWeiXinAudio(HttpServletRequest request , String serviceId , String roomId , String type , String uuid ) throws IOException {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);


        Map map = weixinUtil.getTemporaryMediaHeightMapByMediaid(serviceId);
        if (map == null || map.get("object") == null) return ActResultDto.fail(ReturnMessageType.SEND_FILE_FAIL.getCode());

        byte[] bytes = (byte[]) map.get("object");
        String contentType = (String)map.get("contentType");
        log.info("返回类型：" + contentType);

        String basePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        String fileName = UUIDGenerator.generate().toString();


        String date = DateFormatUtils.format(new Date() , "yyyy-MM-dd");

        File dir = new File(basePath + "audiotmp/"+ date);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String from = basePath  + "audiotmp/"+ date +"/" + fileName + ".speex";
        String mp3desc = basePath + "audiotmp/" + date +"/" + fileName + ".mp3";
        String m4adesc = basePath + "audiotmp/" + date +"/" + fileName + ".aac";
        File file =  getFileFromBytes(bytes,  from);
        file.createNewFile();
        //转化
        ICommand converDeal = new FFConverAudioCommand(from , mp3desc);
        CmdExecuter.exec(converDeal);
        //获取长度
        ICommand audioDeal = new FFMpegAudioCommand(mp3desc , m4adesc);
        CmdExecuter.exec(audioDeal);
        long dur = (Long)audioDeal.getMeta().get("dur");
        String auto = request.getParameter("auto");
        String isIosWeixin = request.getParameter("isIosWeixin");
        if ("1".equals(isIosWeixin) && "1".equals(auto)) {
            dur = 60000l;
        }

        byte[] output = getBytesFromFile(new File(m4adesc));

        String content = Base64Utils.encodeToString(output);
        String url = yunxinChatRoomUtil.upload(content, token.getId(), roomId );

        if(StringUtils.isNotEmpty(url)){
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            resultDto.setData(url);

            Map body = new HashMap();
            body.put("name" , serviceId);
            body.put("url" , url);
            body.put("md5" , DigestUtils.md5DigestAsHex(output));
            body.put("size" ,bytes.length);
            body.put("dur" , dur);//语音持续时长ms
            body.put("ext" , "aac");

            if(StringUtil.isNotEmpty(uuid)) {
                yunxinChatRoomUtil.sendMsgWithUUID(roomId ,String.valueOf(token.getId())  , type, JsonUtil.toJson(body) , uuid );
            } else {
                yunxinChatRoomUtil.sendMsg(roomId ,String.valueOf(token.getId())  , type, JsonUtil.toJson(body) );
            }

        }
        return resultDto;
    }

    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static byte[] getBytesFromFile(File f)  {
        if (f == null)  {
            return null;
        }
        try  {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e)  {
        }
        return null;
    }

    @RequestMapping(value = "wexin/getWechatOfficialQrCode.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取公众号二维码", httpMethod = "GET", notes = "获取公众号二维码")
    public ActResultDto appLyLiveConnect(HttpServletRequest request,Long courseId) throws Exception {
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Course course =   courseService.getCourse(courseId);
        String ll_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.appid");
        String wechatAppid = redisUtil.hget(RedisKey.ll_live_appid_use_authorizer_room_info, course.getRoomId() + "");
        String qrCodeType = "";
        if (!Utility.isNullorEmpty(wechatAppid) && !ll_appid.equals(wechatAppid) && course.getAppId() != token.getId()) {
            qrCodeType = WechatQRCodeType.third_wechat_live_room_or_course_param_pop_lose.getValue();
        } else {
            qrCodeType = WechatQRCodeType.third_wechat_or_course_param.getValue();
        }
        String qrCode = wechatOfficialService.getWechatOfficialQrCode(qrCodeType, course.getRoomId(), courseId, token.getId(), 0l);
        actResultDto.setData(qrCode);
        return actResultDto;
    }

    /**
     * 公众号对接
     */
    @RequestMapping(value = "/publicConcern", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "关注公众号", httpMethod = "GET", notes = "关注公众号")
    public ActResult publicConcern() throws Exception {
        ActResult ar = new ActResult();
        ar.setData(publicConcern);
        log.info("获取关注公众号URL:" + publicConcern);
        return ar;
    }

}
