package com.longlian.live.controller;

import com.huaxin.util.*;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.ActResultDto;
import com.longlian.live.constant.Const;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.live.service.*;
import com.longlian.model.LiveRoom;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/10.
 */
@Controller
@RequestMapping(value = "/user")
public class AppUserController {
    private static Logger log = LoggerFactory.getLogger(AppUserController.class);
    @Autowired
    AppUserService appUserService;
    @Autowired
    MessageClient messageClient;
    @Autowired
    LiveRoomService liveRoomService;
    @Autowired
    AppMsgService appMsgService;
    @Value("${website}")
    private String website;
    @Value("${dockPublic.url}")
    private String dockPublic;
    @Autowired
    MobileVersionService versionService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 修改用户姓名
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/updateUser.user", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "修改用户姓名", httpMethod = "POST", notes = "修改用户姓名")
    public ActResultDto updateUser(HttpServletRequest request, String name) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            result = appUserService.updateUser(token.getId(), name);
        }
        //清空redis
        String key=RedisKey.user_info_key+token.getId();
        if(redisUtil.exists(key)){
            redisUtil.del(key);
        }
        return result;
    }

    /**
     * 取得个人信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/userInfo.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取个人信息", httpMethod = "GET", notes = "获取个人信息")
    public ActResultDto userInfo(HttpServletRequest request, @RequestParam(required = false) String isCenter) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            Map map = null;
            if (Utility.isNullorEmpty(isCenter)) {
                map = appUserService.getUserInfo(token.getId());
            } else {
                map = appUserService.updateFetchUserInfo(token.getId());
            }
            result.setData(map);
        }
        return result;
    }

    @RequestMapping(value = "/liveRoomFollowCount.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取用户直播间关注数量", httpMethod = "GET", notes = "获取用户直播间关注数量")
    public ActResultDto userLiveRoomFollowCount(HttpServletRequest request, long userId) {
        ActResultDto result = new ActResultDto();
        Map<String, Object> map = new HashMap<>();
        map.put("userFollowCount", appUserService.userLiveRoomFollowCount(userId));
        result.setData(map);
        return result;
    }


    /**
     * 发送申请验证码
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/getApplySms", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "发送申请验证码", httpMethod = "GET", notes = "发送申请验证码")
    public ActResultDto getApplySms(@ApiParam(required = true,name = "手机号",value = "手机号")String mobile) {
        return appUserService.getApplySms(mobile, RedisKey.ll_live_mobile_register_sms , "1");
    }

    /**`
     * 微信端报名注册手机号
     *
     * @param mobile
     * @param checkCode
     * @return
     */
    @RequestMapping(value = "/registerMobile.user")
    @ResponseBody
    @ApiOperation(value = "微信报名注册手机号", httpMethod = "GET", notes = "微信报名注册手机号")
    public ActResultDto registerMobile(HttpServletRequest request,
                                       @ApiParam(required = true,name = "手机号",value = "手机号")String mobile,
                                       @ApiParam(required = true,name = "验证码",value = "验证码")String checkCode) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return appUserService.registerMobile(token, mobile, checkCode);
    }


    /**
     * 个人邀请卡
     *
     * @param temp      模板编号
     * @param courseId  课程ID    (课程ID和直播间ID,必须传一个)
     * @param roomId    直播间ID
     * @param request
     * @return
     */
    @RequestMapping("/invitationCard.user")
    @ResponseBody
    @ApiOperation(value = "邀请卡", httpMethod = "GET", notes = "邀请卡")
    public ActResultDto drawInvitationCard(String temp, HttpServletRequest request,
                                          Long courseId, Long roomId) {
        return appUserService.drawInvitationCard(temp, request, courseId, roomId);
    }

    /**
     * 个人邀请卡
     *
     * @param temp      模板编号
     * @param courseId  课程ID    (课程ID和直播间ID,必须传一个)
     * @param roomId    直播间ID
     * @param request
     * @return
     */
    @RequestMapping("/invitationCard")
    @ResponseBody
    @ApiOperation(value = "邀请卡", httpMethod = "GET", notes = "邀请卡")
    public ActResultDto drawInvitationCardNoLogin(String temp, HttpServletRequest request,
                                           Long courseId, Long roomId) {
        return appUserService.drawInvitationCard(temp, request, courseId, roomId);
    }

    /**
     * 关注的直播间
     */
    @RequestMapping("/followRoom.user")
    @ResponseBody
    @ApiOperation(value = "关注的直播间", httpMethod = "GET", notes = "关注的直播间")
    public ActResultDto followRoom(HttpServletRequest request) {
        ActResultDto result = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            result = liveRoomService.getFollowRoom(token.getId());
        }
        return result;
    }

    /**
     * 本人的全部单节课程
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCourseList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "本人的全部单节课程", httpMethod = "GET", notes = "本人的全部单节课程")
    public ActResultDto getCourseList(HttpServletRequest request, Integer pageNum, Integer pageSize,
                                          @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId, HttpServletResponse response) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (roomId == null || identity == null) {
            result.setCode(ReturnMessageType.NO_LOGIN.getCode());
            result.setMessage(ReturnMessageType.NO_LOGIN.getMessage());
        } else {
            result = appUserService.getCourseListByAppId(identity.getId(), pageNum, pageSize, roomId);
        }
        return result;
    }

    /**
     * 本人的全部单节课程
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCourseList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "本人的全部单节课程", httpMethod = "GET", notes = "本人的全部单节课程")
    public ActResultDto getCourseListUser(HttpServletRequest request, Integer pageNum, Integer pageSize,
                                          @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId,
                                          HttpServletResponse response) throws Exception {
        return this.getCourseList( request,  pageNum,  pageSize,  roomId,  response);
    }

    /**
     * 本人的全部系列课程
     * isQueryRelay 是否查询转播课
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSeriesCourseList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "本人的全部系列课程", httpMethod = "GET", notes = "本人的全部系列课程")
    public ActResultDto getSeriesCourseList(HttpServletRequest request, Integer offset, Integer pageSize,
                                            @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId,
                                            HttpServletResponse response,Integer isQueryRelay) throws Exception {
        ActResultDto result = new ActResultDto();
        AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
        if (pageSize == null) {
            pageSize = 10;
        }
        if (roomId == null) {
            if (identity != null) {
              LiveRoom lr =   liveRoomService.findByAppId(identity.getId());
              roomId = lr.getId();
            }
        }
        long loginAppId = 0;
        boolean isHaveRecord = versionService.isHaveRecordedCourse(request);
        if(identity != null){
            loginAppId = identity.getId();
        }
        List<Map> list = appUserService.getSeriesListByAppId(loginAppId,offset, pageSize, roomId , isHaveRecord,isQueryRelay);
        if(list!=null && list.size()>0){
            result.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            result.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            result.setData(list);
        }else{
            result.setCode(ReturnMessageType.NO_DATA.getCode());
            result.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return result;
    }



    /**
     * 本人的全部系列课程
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSeriesCourseList.user", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "本人的全部系列课程", httpMethod = "GET", notes = "本人的全部系列课程")
    public ActResultDto getSeriesCourseListUser(HttpServletRequest request, Integer offset, Integer pageSize,
                                                @ApiParam(required = true,name = "直播间ID",value = "直播间ID")Long roomId, HttpServletResponse response) throws Exception {
        return getSeriesCourseList( request,  offset,  pageSize,  roomId,  response,1);
    }

    /**
     * 福利页面接口
     */
    @RequestMapping(value = "/getWelfareIndex", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "福利页面接口", httpMethod = "GET", notes = "福利页面接口")
    public ActResultDto getWelfareIndex() throws Exception {
        ActResultDto result = new ActResultDto();
        String address = website + "/weixin/welfareIndex";
        result.setData(address);
        return result;
    }
    
    /**
     * 公众号对接
     */
    @RequestMapping(value = "/dockPublic", method = RequestMethod.GET)
    @ApiOperation(value = "公众号对接", httpMethod = "GET", notes = "公众号对接")
    public String dockPublic() throws Exception {
        return "redirect:"+ dockPublic;
    }
}
