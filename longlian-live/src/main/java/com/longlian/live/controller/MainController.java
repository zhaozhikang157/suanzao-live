package com.longlian.live.controller;

import com.huaxin.util.*;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.live.util.jpush.JPushLonglian;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.live.util.yunxin.YunxinUserUtil;
import com.longlian.model.*;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.MsgType;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by syl on 2017/2/8.
 */
@Controller
@RequestMapping(value = "/")
public class MainController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AdvertisingDisplayService advertisingDisplayService;

    @Autowired
    CourseTypeService courseTypeService;

    @Autowired
    CourseService courseService;
    @Autowired
    AppUserService appUserService;

    @Autowired
    YunxinUserUtil yunxinUserUtil;
    @Autowired
    AppMsgService appMsgService;
    @Autowired
    MobileVersionService versionService;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    LiveService liveService;
    @Autowired
    RoomFuncService roomFuncService;

    @Autowired
    WeixinUtil weixinUtil;
    /**
     * 用户登录
     *
     * @param user
     * @param response
     * @param machineCode
     * @param machineType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "loginIn")
    @ResponseBody
    @ApiOperation(value = "用户登录", httpMethod = "GET", notes = "用户登录")
    public ActResultDto loginIn(AppUser user, HttpServletResponse response, String machineCode, String machineType) throws Exception {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(machineCode)) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        result = appUserService.loginIn(user, machineType);
        if (!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(result.getCode())) {
            return result;
        }
        AppUserIdentity identity = (AppUserIdentity) result.getData();
        String key = identity.getId() + "";
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
        if (StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = yunxinUserUtil.refreshToken(String.valueOf(identity.getId()));
            appUserService.updateYunxinToken(identity.getId(), yunxinToken);
        }
        map.put("yunxinToken", yunxinToken);
        map.put("userType", identity.getUserType());
        result.setData(map);
        return result;
    }

    /**
     * 微信用户登录/注册
     *
     * @param weixinAppUser
     * @param response
     * @param machineCode
     * @param machineType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "loginIn4Wechat")
    @ResponseBody
    @ApiOperation(value = "微信用户登录/注册", httpMethod = "GET", notes = "微信用户登录/注册")
    public ActResultDto loginIn4Wechat( WeixinAppUser weixinAppUser,HttpServletRequest request, HttpServletResponse response, String machineCode, String machineType) throws Exception {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(machineCode) && StringUtils.isEmpty(weixinAppUser.getUnionid())) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        machineType = "teacherLogin";//统一改成老师登录
        result = appUserService.weixinLogin(weixinAppUser, 0 , machineType);
        if (!ReturnMessageType.CODE_MESSAGE_TRUE.getCode().equals(result.getCode())) {
            return result;
        }

        AppUserIdentity identity = (AppUserIdentity) result.getData();
        String key = String.valueOf(identity.getId());
        String oldMachineCode = redisUtil.hget(RedisKey.ll_jpush_user_code_key, key);
        String version = request.getParameter("v");
        String cannotUpdateVersion = request.getParameter("cannotUpdateVersion");
        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
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
        if (StringUtils.isEmpty(yunxinToken)) {
            yunxinToken = yunxinUserUtil.refreshToken(String.valueOf(identity.getId()));
            appUserService.updateYunxinToken(identity.getId(), yunxinToken);
        }
        map.put("yunxinToken", yunxinToken);
        map.put("userType", identity.getUserType());
        map.put("balance",identity.getBalance().toString());
        result.setData(map);
        return  result;
    }

    /**
     * 小程序  扩展
     * @param request
     * @param response
     * @param machineCode
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "loginIn4Xcx")
    @ResponseBody
    @ApiOperation(value = "微信小程序登录/注册", httpMethod = "GET", notes = "微信用户登录/注册")
    public ActResultDto loginIn4Xcx( HttpServletRequest request, HttpServletResponse response, String machineCode, String code ,String encryptedData , String iv) throws Exception {
        ActResultDto result = new ActResultDto();
        if (StringUtils.isEmpty(code)) {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        WeixinAppUser weixinAppUser = weixinUtil.getUserAppUserXCX(code , encryptedData ,  iv);
        result.setData(weixinAppUser);
        return result;
    }

    /**
     * 首页
     *
     * @param response
     * @param index    精选.推荐 分页索引 默认为-1
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "home")
    @ResponseBody
    @ApiOperation(value = "首页", httpMethod = "GET", notes = "首页")
    public ActResultDto home(HttpServletRequest request , HttpServletResponse response,
                             @ApiParam(required = true,name = "索引",value = "索引")String index) throws Exception {
        ActResultDto result = new ActResultDto();
        Map data = new HashMap();
        int index_ = -1;
        if (!Utility.isNullorEmpty(index)) index_ = Utility.parseInt(index);
        if (index_ <= -1) {
            //广告
            List advertisingDisplays = advertisingDisplayService.getList("0");
            data.put("advertisingList", advertisingDisplays);//广告 (redis缓存)
            //课程类型
//            List<CourseType> courseTypeList = courseTypeService.getCourseType4Redis();
//            data.put("courseTypeList", courseTypeList);//课程分类 (redis缓存)
            index_ = 0;
        }
        //预告
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(token != null){
            result.setExt(appMsgService.getIsAppMsg(token.getId()));
        }
        List<Map> prevueCourseList = courseService.getPrevueLive4Home(index_);
        result.setData(prevueCourseList);
        data.put("prevueCourseList", prevueCourseList);
        result.setData(data);
        return result;
    }


    /**
     * 首页
     *
     * @param response
     * @param index    精选.推荐 分页索引 默认为-1
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "homeV2")
    @ResponseBody
    @ApiOperation(value = "首页", httpMethod = "GET", notes = "首页")
    public ActResultDto homeV2(HttpServletRequest request , HttpServletResponse response, @ApiParam(required = true,name = "索引",value = "索引")String index) throws Exception {
        ActResultDto result = new ActResultDto();
        Map data = new HashMap();
        int index_ = -1;
        if (!Utility.isNullorEmpty(index)) index_ = Utility.parseInt(index);
        if (index_ <= -1) {
            //广告
            List advertisingDisplays = advertisingDisplayService.getList("0");
            data.put("advertisingList", advertisingDisplays);//广告 (redis缓存)
            //课程类型
//            List<CourseType> courseTypeList = courseTypeService.getCourseType4Redis();
//            data.put("courseTypeList", courseTypeList);//课程分类 (redis缓存)
            index_ = 0;
        }
        //预告
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(token != null){
            result.setExt(appMsgService.getIsAppMsg(token.getId()));
        }

        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        List<Map> prevueCourseList = courseService.getPrevueLive4HomeV2(index_, isHaveRecord);
        Boolean bo = versionService.compareVersion(request.getParameter("v"));
        if(bo){
            if(prevueCourseList.size() == 0){
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
                result.setData(data);
                return result;
            }
        }
        data.put("prevueCourseList", prevueCourseList);
        result.setData(data);
        return result;
    }

    /**
     * 精选。推荐
     */
    @RequestMapping(value = "getCommend4Home")
    @ResponseBody
    @ApiOperation(value = " 精选。推荐", httpMethod = "GET", notes = " 精选。推荐")
    public ActResultDto getCommend4Home(Integer commendOffset , HttpServletRequest request) throws Exception {
        if ( commendOffset == null) {
            commendOffset = 0 ;
        }
        ActResultDto result = new ActResultDto();
        DataGridPage page = new DataGridPage();
        page.setOffset(commendOffset);
        List<Map> commendList = courseService.getCommend4HomePage(page);
        Boolean bo = versionService.compareVersion(request.getParameter("v"));
        if(commendList.size()>0) {
            for (Map map : commendList) {
                map.put("chargeAmt", map.get("chargeAmt").toString());
                if (map.get("startTime") != null) {
                    map.put("startTimeStr", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
                }  else {
                    map.put("startTimeStr","");
                }
            }
        }else{
            if(bo){
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
                return result;
            }
        }
        result.setData(commendList);
        return result;
    }

    /**
     * 精选。推荐
     */
    @RequestMapping(value = "getCommend4HomeV2")
    @ResponseBody
    @ApiOperation(value = " 精选。推荐", httpMethod = "GET", notes = " 精选。推荐")
    public ActResultDto getCommend4HomeV2(Integer commendOffset , HttpServletRequest request) throws Exception {
        if ( commendOffset == null) {
            commendOffset = 0 ;
        }
        ActResultDto result = new ActResultDto();
        DataGridPage page = new DataGridPage();
        page.setOffset(commendOffset);
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        List<Map> commendList = courseService.getCommend4HomePageV2(page, isHaveRecord);
        Boolean bo = versionService.compareVersion(request.getParameter("v"));
        if(commendList.size()>0) {
            for (Map map : commendList) {
                map.put("chargeAmt", map.get("chargeAmt").toString());
                if (map.get("startTime") != null) {
                    map.put("startTimeStr", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
                }  else {
                    map.put("startTimeStr","");
                }
            }
        }else{
            if(bo){
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
                return result;
            }
        }
        result.setData(commendList);
        return result;
    }

    /**
     * 精选。推荐
     */
    @RequestMapping(value = "getCommend4HomeV2.user")
    @ResponseBody
    @ApiOperation(value = " 精选。推荐", httpMethod = "GET", notes = " 精选。推荐")
    public ActResultDto getCommend4HomeV2User(Integer commendOffset , HttpServletRequest request) throws Exception {
        return  getCommend4HomeV2(commendOffset,request) ;
    }


    /**
     * 正在直播分页
     */
    @RequestMapping(value = "getLiveing4Home")
    @ResponseBody
    @ApiOperation(value = "正在直播分页", httpMethod = "GET", notes = "正在直播分页")
    public ActResultDto getLiveing4Home(Integer liveingOffset , HttpServletRequest request) throws Exception {
        ActResultDto result = new ActResultDto();
        List<Map> prevueCourseList = courseService.getLiveing4Home(liveingOffset);
        for(Map map : prevueCourseList){
            map.put("chargeAmt", map.get("chargeAmt").toString());
            if (map.get("startTime") != null) {
                map.put("startTimeStr", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
            }

            else {
                map.put("startTimeStr","");
            }
//            String date_time_fmt2_str = "yyyy-MM-dd HH:mm";
//            SimpleDateFormat date_time_fmt3 = new SimpleDateFormat(date_time_fmt2_str);
//            Timestamp startTime = (Timestamp) map.get("startTime");
//            Date d = new Date(startTime.getTime());
//            String startTimeStr = Utility.getDateTimeStr(d, date_time_fmt3);
//            map.put("startTimeStr", startTimeStr);
        }
        if (prevueCourseList != null && prevueCourseList.size() > 0) {
            result.setData(prevueCourseList);
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
            result.setCode(ReturnMessageType.NO_DATA.getCode());
        }
        return result;
    }




    /**
     * 正在直播分页
     */
    @RequestMapping(value = "getLiveing4HomeV2")
    @ResponseBody
    @ApiOperation(value = "正在直播分页", httpMethod = "GET", notes = "正在直播分页")
    public ActResultDto getLiveing4HomeV2(Integer liveingOffset , HttpServletRequest request) throws Exception {
        ActResultDto result = new ActResultDto();
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        List<Map> prevueCourseList = courseService.getLiveing4HomeV2(liveingOffset, isHaveRecord);
        for(Map map : prevueCourseList){
            map.put("chargeAmt", map.get("chargeAmt").toString());
            if (map.get("startTime") != null) {
                map.put("startTimeStr", map.get("startTime").toString().substring(0, map.get("startTime").toString().length() - 5));
            }
            else {
                map.put("startTimeStr","");
            }
        }
        if (prevueCourseList != null && prevueCourseList.size() > 0) {
            result.setData(prevueCourseList);
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        } else {
            Boolean bo = versionService.compareVersion(request.getParameter("v"));
            if(bo){
                result.setCode(ReturnMessageType.NO_DATA.getCode());
                result.setMessage(ReturnMessageType.NO_DATA.getMessage());
                return result;
            }
        }
        result.setData(prevueCourseList);
        return result;
    }

    /**
     * 正在直播分页
     */
    @RequestMapping(value = "getLiveing4HomeV2.user")
    @ResponseBody
    @ApiOperation(value = "正在直播分页", httpMethod = "GET", notes = "正在直播分页")
    public ActResultDto getLiveing4HomeV2User(Integer liveingOffset , HttpServletRequest re ) throws Exception {
        return getLiveing4HomeV2(liveingOffset, re);
    }
    /**
     * 首页
     *
     * @param response
     * @param index    精选.推荐 分页索引 默认为-1
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "homeV2.user")
    @ResponseBody
    @ApiOperation(value = "首页", httpMethod = "GET", notes = "首页")
    public ActResultDto homeV2User(HttpServletRequest request , HttpServletResponse response, @ApiParam(required = true,name = "索引",value = "索引")String index) throws Exception {
        return this.homeV2(request, response, index);
    }

    /**
     * 是否打开qr功能
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "isOpenQR")
    @ResponseBody
    @ApiOperation(value = "是否打开qr功能", httpMethod = "GET", notes = "是否打开qr功能")
    public ActResultDto isOpenQR(HttpServletRequest request) throws Exception {
        String isOpen =  redisUtil.get(RedisKey.ll_is_open_qr);
        if (StringUtils.isNotEmpty(isOpen) && "1".equals(isOpen)) {
           return ActResultDto.success().setData("1");
        }
        return ActResultDto.success().setData("0");
    }
    @RequestMapping(value = "getAllUses")
    @ResponseBody
    public ActResultDto getAllUses(HttpServletRequest request,Long courseId) throws Exception {
        ActResultDto result = new ActResultDto();
        Set<String> allUsers =  liveService.getAllUses(courseId);
        result.setData(allUsers);
       return  result;
    }


    /**
     * 新版首页--精彩推荐页面
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wonderfulrecommendation")
    @ResponseBody
    @ApiOperation(value = "新版首页--精彩推荐页面", httpMethod = "GET", notes = "新版首页--精彩推荐页面")
    public ActResultDto  wonderfulrecommendation(HttpServletRequest request) throws Exception {
        ActResultDto result = courseService.wonderfulrecommendation();
        UserAgent ua =  UserAgentUtil.getUserAgentCustomer(request);
        if (ua!=null&&UserAgentUtil.ios.equals(ua.getCustomerType())) {
          Map map =   (Map)result.getData();
            if(map!=null&& map.get("courseWeeklySelection")!=null){
                List<Map> ms =  (List<Map>)map.get("courseWeeklySelection");
                for(Map m:ms){
                    if(m.get("liveWay")!=null){
                        if("0".equals(m.get("liveWay").toString())){
                            m.put("liveWay","1");
                        }else{
                            m.put("liveWay","0");
                        }
                    }
                }
            }
        }
        //预告
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(token != null){
            result.setExt(appMsgService.getIsAppMsg(token.getId()));
        }else{
            result.setExt(0);
        }
        return result;
    }

    /**
     * 新版首页--精选课程分页
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findCourseAllSelection")
    @ResponseBody
    @ApiOperation(value = "新版首页--精选课程分页", httpMethod = "GET", notes = "新版首页--精选课程分页")
    public ActResultDto findCourseAllSelection( @ApiParam(required = true,name = "偏移量",value = "偏移量")Integer offset,
                                               Integer pageSize) throws Exception {
        return courseService.findCourseAllSelection(offset,pageSize);
    }

    /**
     * 新版首页--精彩推荐页面-1.6.4
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wonderfulrecommendationV164")
    @ResponseBody
    public ActResultDto  wonderfulrecommendationV164(HttpServletRequest request) throws Exception {
        ActResultDto result = courseService.wonderfulrecommendationV164();
        //预告
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(token != null){
            result.setExt(appMsgService.getIsAppMsg(token.getId()));
        }else{
            result.setExt(0);
        }
        return result;
    }

    /**
     * 获取课程状态
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/getCourseStatus")
    @ResponseBody
    public ActResultDto getCourseStatus(HttpServletRequest request,
                                        @ApiParam(required = true,name = "课程id",value = "课程id") long id){
        ActResultDto dto = new ActResultDto();
        if(id <= 0){
            dto.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            dto.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return dto;
        }
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if(identity != null){
            dto = courseService.getCourseStatus(id);
        }else{
            dto.setCode(ReturnMessageType.ERROR_403.getCode());
            dto.setMessage(ReturnMessageType.ERROR_403.getMessage());
        }
        return dto;
    }
}
