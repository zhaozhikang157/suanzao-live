package com.longlian.live.controller;

import com.huaxin.util.DateUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.ShareConst;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.MsgType;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/2/12.
 */
@RequestMapping("/teacher")
@Controller
public class TeacherController {
    private static Logger log = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    AppUserService appUserService;
    @Autowired
    CourseService courseService;
    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    RedisUtil redisUtil;
    @Value("${website}")
    private String website;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    RoomFuncService roomFuncService;
    @Autowired
    AccountService accountService;

    @Autowired
    LlAccountService llAccountService;
    @Autowired
    CourseRelayService courseRelayService;
    /**
     * 老师端登录
     * @param mobile
     * @param passWord
     * @return
     */
    @RequestMapping("/loginIn")
    @ResponseBody
    @ApiOperation(value = "老师端登录", httpMethod = "GET", notes = "老师端登录")
    public ActResultDto loginIn(@ApiParam(required =true, name = "手机号", value = "手机号") String mobile ,
                                @ApiParam(required =true, name = "密码", value = "密码") String passWord,
                                @ApiParam(required =true, name = "登陆代码", value = "登陆代码") String machineCode,HttpServletRequest request, HttpServletResponse response)throws Exception{
        ActResultDto result = new ActResultDto();
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(passWord)){
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        AppUser appUser = new AppUser();
        appUser.setMobile(mobile);
        appUser.setPassword(passWord);
        String version = request.getParameter("v");
        String cannotUpdateVersion = request.getParameter("cannotUpdateVersion");
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);

        result = appUserService.loginIn(appUser,"teacherLogin" );
        if(!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(result.getCode())){
            return  result;
        }
        AppUserIdentity identity = (AppUserIdentity) result.getData();
        String key = String.valueOf(identity.getId());
        String oldMachineCode = redisUtil.hget(RedisKey.ll_jpush_user_code_key, key);

        if (!Utility.isNullorEmpty(machineCode) && !"h5".equals(machineCode) && !machineCode.equals(oldMachineCode)) {
            if (redisUtil.hexists(RedisKey.ll_jpush_user_code_key, key)) {
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(oldMachineCode)) {
                    String count = "您账号在其它手机上登录，请注意账号安全！";
                    Map map1 = new HashMap();
                    map1.put("NotificationType",  MsgType.SYS_OTHER_PLACE_LOGIN.getTypeStr());
                    JPushLonglian.sendPushNotificationByCode(oldMachineCode, count, map1);
                }
                redisUtil.hdel(RedisKey.ll_jpush_user_code_key, key);
            }
            Map<String, String> map = redisUtil.hmgetAll(RedisKey.ll_jpush_user_code_key);
            if (map != null) {
                Set<String> userIds = map.keySet();
                String old = "";
                for (String k : userIds) {
                    String val = map.get(k);
                    if (machineCode.equals(val)) {
                        old = k;
                        break;
                    }
                }
                redisUtil.hdel(RedisKey.ll_jpush_user_code_key, old);
            }
        }
        if (!Utility.isNullorEmpty(machineCode)) {
            String androidOrIos = "";
            if(userAgent !=null) {
                androidOrIos = userAgent.getCustomerType();
            }
            redisUtil.hset(RedisKey.ll_jpush_user_code_key, key, machineCode);
            UserMachineInfo info = new UserMachineInfo();
            info.setConnotUpdateVersion(cannotUpdateVersion);
            info.setLoginTime(new Date());
            info.setMachineCode(machineCode);
            info.setUserId(Long.parseLong(key));
            info.setVersion(version);
            info.setMachineType(androidOrIos);

            redisUtil.lpush(RedisKey.ll_user_machine_info_wait2db  , JsonUtil.toJson(info));
        }


//        String key = identity.getId() + "";
        response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        Map map = new HashMap();
        map.put("level", identity.getLevel());
        map.put("id", identity.getId());
        map.put("roomFunc","");
        LiveRoom room = liveRoomService.findByAppId(identity.getId());
        if (room != null) {
            redisUtil.del(RedisKey.ll_room_fun + room.getId());
            map.put("roomId", room.getId());
            map.put("roomFunc",roomFuncService.findRoomFunc(room.getId()));
        } else {
            map.put("roomId", "");
        }

        map.put("mobile", identity.getMobile());
        map.put("name", identity.getName());
        map.put("gender", identity.getGender());
        map.put("birthday", identity.getBirthday() == null ? "" : DateUtil.format(identity.getBirthday(), "yyyy-MM-dd"));
        map.put("photo", identity.getPhoto());
        map.put("city", identity.getCity());
        map.put("token", identity.getToken());
        //如果云信的用户token为空时，则更新数据库
        String yunxinToken = identity.getYunxinToken();
        if (org.apache.commons.lang3.StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = yunxinUserUtil.refreshToken(String.valueOf(identity.getId()));
            appUserService.updateYunxinToken(identity.getId(), yunxinToken);
        }
        map.put("yunxinToken", yunxinToken);
        map.put("userType", identity.getUserType());
        LlAccount accountByUserId = llAccountService.getAccountByUserId(identity.getId());
        if(accountByUserId!=null){
            map.put("balance", accountByUserId.getBalance().toString());
        }
        result.setData(map);
        return result;
    }

    /**
     * 发送验证码
     * @return
     */
    @RequestMapping("/sendMobileCode")
    @ResponseBody
    @ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
    public ActResultDto sendMobileCode(@ApiParam(required =true, name = "手机号", value = "手机号")  String mobile){
        return appUserService.teacherSendCode(mobile);
    }

    /**
     * 设置新密码
     * @param mobile
     * @param code
     * @return
     */
    @RequestMapping("/setNewPassword")
    @ResponseBody
    @ApiOperation(value = "设置新密码", httpMethod = "GET", notes = "设置新密码")
    public ActResultDto setNewPassword(@ApiParam(required =true, name = "手机号", value = "手机号") String mobile ,
                                       @ApiParam(required =true, name = "验证码", value = "验证码") String code ,
                                       @ApiParam(required =true, name = "新密码", value = "新密码") String password){
        Boolean codeStatus = appUserService.verificationCode(mobile, code, RedisKey.ll_live_mobile_register_sms);
        if(codeStatus){
            return appUserService.setPassword(mobile,password);
        }
        ActResultDto resultDto = new ActResultDto();
        resultDto.setCode(ReturnMessageType.CODE_UPDATE_FALSE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_UPDATE_FALSE.getMessage());
        return resultDto;
    }

    /**
     * 老师所有直播间信息(未开播的课程信息)
     * @return
     */
    @RequestMapping("/noLiveInfoPage.user")
    @ResponseBody
    @ApiOperation(value = "老师所有直播间信息", httpMethod = "GET", notes = "老师所有直播间信息")
    public ActResultDto noLiveInfoPage(@ApiParam(required =true, name = "课程分页偏移量", value = "课程分页偏移量")  Integer offset,
                                       @ApiParam(required =true, name = "每页数量", value = "每页数量")   Integer pageSize ,
                                       HttpServletRequest request){
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (offset == null ) {
            offset = 0;
        }
        return courseService.noLiveInfoPage(offset, pageSize, request);
    }

    /**
     * 该老师所有的直播信息
     * @param noLearnOffset    :未开课偏移量
     * @param alreadyLearnOffset    : 历史开播偏移量
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("/liveInfoPage.user")
    @ResponseBody
    @ApiOperation(value = "该老师所有的直播信息", httpMethod = "GET", notes = "该老师所有的直播信息")
    public ActResultDto getAllLiveInfoPage( @ApiParam(required =true, name = "未开课偏移量", value = "未开课偏移量")   Integer noLearnOffset,
                                            @ApiParam(required =true, name = "每页数量", value = "每页数量")  Integer pageSize,
                                            @ApiParam(required =true, name = "历史开播偏移量", value = "历史开播偏移量")  Integer alreadyLearnOffset,
                                           HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (alreadyLearnOffset == null ) {
            alreadyLearnOffset = 0 ;
        }
        return courseService.getAllLiveInfoPage(noLearnOffset, pageSize, token.getId(), alreadyLearnOffset);
    }

    /**
     * 退出登录
     * @param request\
     * @return
     */
    @RequestMapping("/loginOut.user")
    @ResponseBody
    @ApiOperation(value = "退出登录", httpMethod = "GET", notes = "退出登录")
    public ActResultDto loginOut(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token != null) {
            //学生微信退出登录
            if ("0".equals(token.getIsTeacherClient())) {
                redisUtil.del(RedisKey.ll_live_weixin_login_prefix + token.getId());
                //老师端退出登录
            } else if ("2".equals(token.getIsTeacherClient())) {
                redisUtil.del(RedisKey.ll_live_teacher_web_login_prefix + token.getId());
            } else {
                redisUtil.del(RedisKey.ll_live_teacher_app_login_prefix + token.getId());
                redisUtil.hdel(RedisKey.ll_jpush_user_code_key, String.valueOf(token.getId()));
            }
        }
        ActResultDto result = new ActResultDto();
        result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        return result;
    }

    /**
     * 老师:分享
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/getShareAddress.user")
    @ResponseBody
    @ApiOperation(value = "老师:分享", httpMethod = "GET", notes = "老师:分享")
    public ActResultDto getShareAddress(HttpServletRequest request ,@ApiParam(required =true, name = "ID", value = "ID")  Long courseId ){
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息;
        ActResultDto resultDto = new ActResultDto();
        Map m = new HashMap();
        //直播间分享
        if(courseId==null || courseId == 0){
            m.put("shareTitle", ShareConst.share_title);
            m.put("shareContent",ShareConst.share_content);
            resultDto.setData(m);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }
        //课程分享
        Course course = courseService.getCourse(courseId);
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
            course=courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
        }
        if(course!=null){
            m.put("coverssAddress",course.getCoverssAddress());
            m.put("shareTitle", course.getLiveTopic());
            m.put("shareContent",course.getRemark());
            /*if(StringUtils.isEmpty(course.getRemark())){
                m.put("shareContent", ShareConst.defult_share_content);
            }*/
            LiveRoom liveRoom = liveRoomService.findById(course.getRoomId());
            if(liveRoom!=null && liveRoom.getRemark()!= null && !"".equals(String.valueOf(liveRoom.getRemark())) ){
                m.put("shareContent", liveRoom.getRemark());
            }
        }else{
            m.put("coverssAddress","");
            m.put("shareTitle", ShareConst.share_title);
            m.put("shareContent", ShareConst.share_content);
        }
        Long seriesid = 0l;
        if(course.getIsSeriesCourse().equals("1")){
            seriesid = course.getId();
        }else{
            seriesid = course.getSeriesCourseId();
        }
        String shareAddress = website+"?return_url="+website + "/weixin/courseInfo?id=" + course.getId() + "&invitationAppId=" + token.getId() +
                "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+course.getIsSeriesCourse();
        m.put("inviCardArress",course.getCoverssAddress());
        m.put("shareAddress", shareAddress);
        resultDto.setData(m);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 老师:分享
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/getShareAddress")
    @ResponseBody
    @ApiOperation(value = "老师:分享", httpMethod = "GET", notes = "老师:分享")
    public ActResultDto getShareAddressNoLogin(HttpServletRequest request ,@ApiParam(required =true, name = "ID", value = "ID")  Long courseId ){
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息;
        ActResultDto resultDto = new ActResultDto();
        Map m = new HashMap();
        //直播间分享
        if(courseId==null || courseId == 0){
            m.put("shareTitle", ShareConst.share_title);
            m.put("shareContent",ShareConst.share_content);
            resultDto.setData(m);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        }
        //课程分享
        Course course = courseService.getCourse(courseId);
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto=courseRelayService.queryById(courseId);
            course=courseRelayService.queryByAppidAndOriCourseId(courseRelayDto.getAppId(),String.valueOf(courseRelayDto.getOriCourseId()));
        }
        if(course!=null){
            m.put("coverssAddress",course.getCoverssAddress());
            m.put("shareTitle", course.getLiveTopic());
            m.put("shareContent",course.getRemark());
            if(StringUtils.isEmpty(course.getRemark())){
                m.put("shareContent", ShareConst.defult_share_content);
            }
        }else{
            m.put("coverssAddress","");
            m.put("shareTitle", ShareConst.share_title);
            m.put("shareContent", ShareConst.share_content);
        }
        Long seriesid = 0l;
        if(course.getIsSeriesCourse().equals("1")){
            seriesid = course.getId();
        }else{
            seriesid = course.getSeriesCourseId();
        }
        String shareAddress = "";
        if(token == null){
            shareAddress = website+"?return_url="+website + "/weixin/courseInfo?id=" + course.getId() + "&invitationAppId=" + 0 +
                    "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+course.getIsSeriesCourse();
        }else{
            shareAddress = website+"?return_url="+website + "/weixin/courseInfo?id=" + course.getId() + "&invitationAppId=" + token.getId() +
                    "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+course.getIsSeriesCourse();
        }
        m.put("inviCardArress",course.getCoverssAddress());
        m.put("shareAddress", shareAddress);
        resultDto.setData(m);
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }

    /**
     * 获取版权界面
     * @return
     */
    @RequestMapping(value = "/userAgreement")
    @ResponseBody
    @ApiOperation(value = "获取版权界面", httpMethod = "GET", notes = "获取版权界面")
    public ModelAndView userAgreement(){
        ModelAndView view = new ModelAndView("func/weixin/userAgreement");
        return view;
    }

    /**
     * 未开播,和正在开播的(两条) 以及 历史直播的(5条)
     * @param request
     * @return
     */
    @RequestMapping(value = "/teacherHome.user")
    @ResponseBody
    @ApiOperation(value = "未开播,和正在开播的(两条) 以及 历史直播的(5条)", httpMethod = "GET", notes = "未开播,和正在开播的(两条) 以及 历史直播的(5条)")
    public ActResultDto getAllCourses(HttpServletRequest request){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return courseService.getAllCourses(token.getId());
    }

    /**
     * 更多
     * @param request
     * @param type
     * @param pageSize
     * @param offset
     * @return
     */
    @RequestMapping(value = "/getMoreCourse.user")
    @ResponseBody
    @ApiOperation(value = "更多", httpMethod = "GET", notes = "更多")
    public ActResultDto getMoreCourse(HttpServletRequest request ,
                                      @ApiParam(required =true, name = "类型", value = "类型")  String type ,
                                      @ApiParam(required =true, name = "每页数量", value = "每页数量")  Integer pageSize ,
                                      @ApiParam(required =true, name = "偏移量", value = "偏移量")  Integer offset){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(pageSize == null) pageSize = 10;
        if(offset == null) offset = 0;
        return courseService.getMoreCoursePage(token.getId(), type, pageSize, offset);
    }

    /**
     * app端获取课程数据
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/findCourseInfoById")
    @ResponseBody
    @ApiOperation(value = "app端获取课程数据", httpMethod = "GET", notes = "app端获取课程数据")
    public ActResultDto findCourseInfoById (@ApiParam(required =true, name = "ID", value = "ID") Long courseId){
        ActResultDto resultDto = new ActResultDto();
        if(courseId == null || courseId == 0){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Map map = courseService.findCourseInfoById(courseId);
        if(map == null){
            resultDto.setMessage(ReturnMessageType.NOT_FIND_COURSE.getMessage());
            resultDto.setCode(ReturnMessageType.NOT_FIND_COURSE.getCode());
            return resultDto;
        }
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        resultDto.setData(map);
        return resultDto;
    }


    /**
     * 绑定手机号发送验证码
     * @param mobile
     * @return
     */
    @RequestMapping("/bindingSendCode")
    @ResponseBody
    @ApiOperation(value = "绑定手机号发送验证码", httpMethod = "GET", notes = "绑定手机号发送验证码")
    public ActResultDto bindingSendCode(@ApiParam(required =true, name = "手机号", value = "手机号") String mobile){
        return appUserService.getApplySms(mobile,RedisKey.ll_binding_mobile,"1");
    }


    /*
     * 绑定手机号
     * @param request
     * @return
     */
    @RequestMapping("/bindingMobile.user")
    @ResponseBody
    @ApiOperation(value = "绑定手机号", httpMethod = "GET", notes = "绑定手机号")
    public ActResultDto bindingMobile(HttpServletRequest request ,
                                      @ApiParam(required =true, name = "手机号", value = "手机号") String mobile ,
                                      @ApiParam(required =true, name = "验证码", value = "验证码") String code ,
                                      @ApiParam(required =true, name = "密码", value = "密码") String password){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return appUserService.bindingMobile(token.getId(), mobile, code, password);
    }

    /**
     * app 分享微信和朋友圈 -- 需要登录
     * @return
     */
    @RequestMapping("/newShare.user")
    @ResponseBody
    @ApiOperation(value = " app 分享微信和朋友圈 -- 需要登录", httpMethod = "GET", notes = " app 分享微信和朋友圈 -- 需要登录")
    public ActResultDto newShare(HttpServletRequest request ,
                                 @ApiParam(required =true, name = "课程ID", value = "课程ID")  Long courseId ,
                                 @ApiParam(required =true, name = "直播间ID", value = "直播间ID") Long roomId ,
                                 @ApiParam(required =true, name = "地址", value = "地址")  String inviCardAddress){
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        Map map = new HashMap();
        String shareAddress = "";
        map.put("shareTitle", ShareConst.share_title);
        map.put("shareContent", ShareConst.share_content);
        if(courseId==null || courseId == 0) {
            //直播间分享
            LiveRoom liveRoom = liveRoomService.findById(roomId);
            if(StringUtils.isEmpty(inviCardAddress)){
                try {
                    ActResultDto resultDto = appUserService.drawInvitationCard("", request, 0l, roomId);
                    Map card = (Map)resultDto.getData();
                    if(card != null){
                        map.put("inviCardAddress", card.get("url"));
                        shareAddress = website + "/weixin/liveRoom?invitationAppId="+token.getId()+"&fromType="+2+"&id="+liveRoom.getId();
                        map.put("shareAddress", shareAddress);
                    }
                }catch (Exception e){
                    log.info("分享直播间失败:"+e);
                    result.setCode(ReturnMessageType.APP_SHARE_ERROR.getCode());
                    result.setMessage(ReturnMessageType.APP_SHARE_ERROR.getMessage());
                    return result;
                }
            }else{
                shareAddress = website + "/weixin/liveRoom?invitationAppId="+token.getId()+"&fromType="+2+"&id="+liveRoom.getId();
                map.put("shareAddress", shareAddress);
                map.put("inviCardAddress", inviCardAddress);
            }
        }else{
            //课程分享
            Course course = courseService.getCourse(courseId);
            if(course != null){
                map.put("shareTitle", course.getLiveTopic());
                map.put("shareContent", course.getRemark());
                if(StringUtils.isEmpty(course.getRemark())){
                    map.put("shareContent", ShareConst.defult_share_content);
                }
            }
            Long seriesid = 0l;
            if(course.getIsSeriesCourse().equals("1")){
                seriesid = course.getId();
            }else{
                seriesid = course.getSeriesCourseId();
            }
            shareAddress = website + "/weixin/courseInfo?id=" + course.getId() +
                    "&invitationAppId=" + token.getId() + "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+course.getIsSeriesCourse();
            map.put("shareAddress", shareAddress);
            map.put("inviCardAddress", course.getCoverssAddress() +  "?x-oss-process=style/share");
        }
        result.setData(map);
        return result;
    }

    /**
     * app 分享微信和朋友圈 -- 游客登录
     * @return
     */
    @RequestMapping("/newShare")
    @ResponseBody
    @ApiOperation(value = " app 分享微信和朋友圈 -- 游客登录", httpMethod = "GET", notes = " app 分享微信和朋友圈 -- 游客登录")
    public ActResultDto newShareNoLogin(HttpServletRequest request ,
                                         Long courseId ,
                                       Long roomId ,
                                          String inviCardAddress){
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息;
        long loginId = 0;
        if(token != null){
            loginId = token.getId();
        }
        ActResultDto result = new ActResultDto();
        Map map = new HashMap();
        String shareAddress = "";
        map.put("shareTitle", ShareConst.share_title);
        map.put("shareContent", ShareConst.share_content);

        if (courseId == null && roomId == null) {
            return ActResultDto.fail(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }

        if(courseId==null || courseId == 0) {
            //直播间分享
            LiveRoom liveRoom = liveRoomService.findById(roomId);
            if(liveRoom==null){
                liveRoom=new LiveRoom();
            }else{
                map.put("shareTitle", liveRoom.getName());
                map.put("shareContent", liveRoom.getRemark());
            }
            if(StringUtils.isEmpty(inviCardAddress)){
                try {
                    ActResultDto resultDto = appUserService.drawInvitationCard("", request, 0l, roomId);
                    Map card = (Map)resultDto.getData();
                    if(card != null){
                        map.put("inviCardAddress", card.get("url"));
                        shareAddress = website + "/weixin/liveRoom?invitationAppId="+loginId+"&fromType="+2+"&id="+liveRoom.getId();
                        map.put("shareAddress", shareAddress);
                        map.put("shareTitle", liveRoom.getName());
                        map.put("shareContent", liveRoom.getRemark());
                    }
                }catch (Exception e){
                    log.info("分享直播间失败:"+e);
                    result.setCode(ReturnMessageType.APP_SHARE_ERROR.getCode());
                    result.setMessage(ReturnMessageType.APP_SHARE_ERROR.getMessage());
                    return result;
                }
            }else{
                shareAddress = website + "/weixin/liveRoom?invitationAppId="+loginId+"&fromType="+2+"&id="+liveRoom.getId();
                map.put("shareAddress", shareAddress);
                map.put("inviCardAddress", inviCardAddress);
            }
        }else{
            //课程分享
            Course course = courseService.getCourse(courseId);
            if(course != null){
                map.put("shareTitle", course.getLiveTopic());
                map.put("shareContent", ShareConst.defult_share_content);
                LiveRoom liveRoom = liveRoomService.findById(course.getRoomId());
                if(liveRoom!=null){
                    map.put("shareContent", liveRoom.getRemark());
                }
            }
            Long seriesid = 0l;
            if(course.getIsSeriesCourse().equals("1")){
                seriesid = course.getId();
            }else{
                seriesid = course.getSeriesCourseId();
            }
            shareAddress = website + "/weixin/courseInfo?id=" + course.getId() +
                    "&invitationAppId=" + loginId + "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+course.getIsSeriesCourse();
            map.put("shareAddress", shareAddress);
            map.put("inviCardAddress", course.getCoverssAddress() +  "?x-oss-process=style/share");
        }
        result.setData(map);
        return result;
    }

    /**
     * 发送验证码
     * @return
     */
    @RequestMapping("/loginSendCode")
    @ResponseBody
    @ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
    public ActResultDto loginSendCode(@ApiParam(required =true, name = "手机号", value = "手机号")  String mobile){
        return appUserService.getApplySms(mobile,RedisKey.ll_login_mobile_code,"2");
    }

    /**
     * 老师端登录 - 手机号登录
     * @param mobile
     * @param code
     * @return
     */
    @RequestMapping("/loginByMobile")
    @ResponseBody
    @ApiOperation(value = "老师端登录 - 手机号登录", httpMethod = "GET", notes = "老师端登录 - 手机号登录")
    public ActResultDto loginByMobile(
            @ApiParam(required =true, name = "手机号", value = "手机号") String mobile ,
            @ApiParam(required =true, name = "验证码", value = "验证码")  String code,
            @ApiParam(required =true, name = "密码", value = "密码")  String machineCode,
            HttpServletRequest request, HttpServletResponse response)throws Exception {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        AppUser appUser = new AppUser();
        appUser.setMobile(mobile);
        appUser.setPassword(code);
        String version = request.getParameter("v");
        String cannotUpdateVersion = request.getParameter("cannotUpdateVersion");
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
        result = appUserService.loginInByMobile(appUser, "teacherLogin");
        if (!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(result.getCode())) {
            return result;
        }
        AppUserIdentity identity = (AppUserIdentity) result.getData();
        String key = String.valueOf(identity.getId());
        String oldMachineCode = redisUtil.hget(RedisKey.ll_jpush_user_code_key, key);
        if (!Utility.isNullorEmpty(machineCode) && !"h5".equals(machineCode) && !machineCode.equals(oldMachineCode)) {
            if (redisUtil.hexists(RedisKey.ll_jpush_user_code_key, key)) {
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(oldMachineCode)) {
                    String count = "您账号在其它手机上登录，请注意账号安全！";
                    Map map1 = new HashMap();
                    map1.put("NotificationType", MsgType.SYS_OTHER_PLACE_LOGIN.getTypeStr());
                    JPushLonglian.sendPushNotificationByCode(oldMachineCode, count, map1);
                }
                redisUtil.hdel(RedisKey.ll_jpush_user_code_key, key);
            }
            Map<String, String> map = redisUtil.hmgetAll(RedisKey.ll_jpush_user_code_key);
            if (map != null) {
                Set<String> userIds = map.keySet();
                String old = "";
                for (String k : userIds) {
                    String val = map.get(k);
                    if (machineCode.equals(val)) {
                        old = k;
                        break;
                    }
                }
                redisUtil.hdel(RedisKey.ll_jpush_user_code_key, old);
            }
        }
        if (!Utility.isNullorEmpty(machineCode)) {
            String androidOrIos = "";
            if (userAgent != null) {
                androidOrIos = userAgent.getCustomerType();
            }
            redisUtil.hset(RedisKey.ll_jpush_user_code_key, key, machineCode);
            UserMachineInfo info = new UserMachineInfo();
            info.setConnotUpdateVersion(cannotUpdateVersion);
            info.setLoginTime(new Date());
            info.setMachineCode(machineCode);
            info.setUserId(Long.parseLong(key));
            info.setVersion(version);
            info.setMachineType(androidOrIos);
            redisUtil.lpush(RedisKey.ll_user_machine_info_wait2db, JsonUtil.toJson(info));
        }
        response.setHeader("SET-COOKIE", "token=" + identity.getToken() + ";path=/; HttpOnly");
        Map map = new HashMap();
        map.put("level", identity.getLevel());
        map.put("id", identity.getId());
        map.put("mobile", identity.getMobile());
        map.put("name", identity.getName());
        map.put("gender", identity.getGender());
        map.put("birthday", identity.getBirthday() == null ? "" : DateUtil.format(identity.getBirthday(), "yyyy-MM-dd"));
        map.put("photo", identity.getPhoto());
        map.put("city", identity.getCity());
        map.put("token", identity.getToken());
        map.put("roomFunc","");
        LiveRoom room = liveRoomService.findByAppId(identity.getId());
        if (room != null) {
            redisUtil.del(RedisKey.ll_room_fun + room.getId());
            map.put("roomId", room.getId());
            map.put("roomFunc",roomFuncService.findRoomFunc(room.getId()));
        } else {
            map.put("roomId", "");
        }

        //如果云信的用户token为空时，则更新数据库
        String yunxinToken = identity.getYunxinToken();
        if (org.apache.commons.lang3.StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = yunxinUserUtil.refreshToken(String.valueOf(identity.getId()));
            appUserService.updateYunxinToken(identity.getId(), yunxinToken);
        }
        map.put("yunxinToken", yunxinToken);
        map.put("userType", identity.getUserType());
        LlAccount accountByUserId = llAccountService.getAccountByUserId(identity.getId());
        if(accountByUserId!=null){
            map.put("balance", accountByUserId.getBalance().toString());
        }
        result.setData(map);
        return result;
    }
}
