package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.SystemCofigConst;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.*;
import com.longlian.model.ChatRoomMsg;
import com.longlian.model.Course;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/22.
 */
@Controller
@RequestMapping(value = "/")
public class LiveMsgController {
    private static Logger log = LoggerFactory.getLogger(LiveMsgController.class);

    @Autowired
    CourseService courseService;
    @Autowired
    JoinCourseRecordService joinCourseRecordService;
    @Autowired
    ChatRoomMsgService chatRoomMsgService;
    @Autowired
    CourseRelayService courseRelayService;

    @Autowired
    AppUserService userService;

    @Autowired
    CourseManagerService courseManagerService;

    /**
     * 取得历史消息 weixin android
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "weixin/live/getHistoryMsg.user")
    @ResponseBody
    @ApiOperation(value = "取得历史消息", httpMethod = "GET", notes = "取得历史消息")
    public ActResultDto getHistoryMsg(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId , Long relayCourseId, Integer offSet) {
        ActResultDto actResultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        Course relayCourse = null;
        //如果转播课ID有值，则当前为转播课，下面的判断要根据转播课进行区分
        if(relayCourseId != null && relayCourseId >= SystemCofigConst.RELAY_COURSE_ID_SIZE){
            relayCourse = courseService.getCourseFromRedis(relayCourseId);
        }

        long cId = course.getId();
        if(String.valueOf(courseId).length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
            CourseRelayDto courseRelayDto =courseRelayService.queryById(courseId);
            cId = courseRelayDto.getOriCourseId();
            course = courseService.getCourseFromRedis(courseRelayDto.getOriCourseId());
        }else{
            //判断是否报名,且不是老师自己
            boolean isJoin = joinCourseRecordService.isJoinCourse(token.getId() , course);
            //如果是转播课，则根据转播课判断是否报名
            if(relayCourse != null){
                isJoin = joinCourseRecordService.isJoinCourse(token.getId() , relayCourse);
            }
            //判断是否是课程管理员
            boolean isCourseManager = courseManagerService.isCourseManager(courseId, token.getId(), "");
            //如果是转播课，则根据转播课判断是否是管理员
            if(relayCourse != null){
                isCourseManager = courseManagerService.isCourseManager(relayCourseId, token.getId(), "");
            }
            //当前课的老师APPID
            long curCourseTeacherAppId = course.getAppId();
            if(relayCourse != null){
                curCourseTeacherAppId = relayCourse.getAppId();
            }
            if (!isJoin
                    && curCourseTeacherAppId != token.getId()
                    && course.getTrySeeTime() <= 0
                    && userService.findSystemAdminByUserId(token.getId()) <= 0
                    && !isCourseManager) {
                res.setCode(ReturnMessageType.NO_JOIN_COURSE.getCode());
                res.setMessage(ReturnMessageType.NO_JOIN_COURSE.getMessage());
                return res;
            }
        }

        List<Map> list = chatRoomMsgService.getHistoryMsg(cId,offSet);
        if(list!=null && list.size()>0){
            actResultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            actResultDto.setData(list);
        }else {
            actResultDto.setCode(ReturnMessageType.NO_DATA.getCode());
            actResultDto.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return actResultDto;
    }

    /**
     * 取得历史消息分页
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "weixin/live/getHistoryMsgPage.user")
    @ResponseBody
    @ApiOperation(value = "取得历史消息分页", httpMethod = "GET", notes = "取得历史消息分页")
    public ActResultDto getHistoryMsgPage(HttpServletRequest request,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                          Integer offSet,ChatRoomMsg chatRoomMsg) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        //判断是否报名,且不是老师自己
        if (!joinCourseRecordService.isJoinCourse(token.getId() , course) && course.getAppId() != token.getId() && course.getTrySeeTime() <= 0) {
            res.setCode(ReturnMessageType.NO_JOIN_COURSE.getCode());
            res.setMessage(ReturnMessageType.NO_JOIN_COURSE.getMessage());
            return res;
        }

        List<Map> result = chatRoomMsgService.getHistoryMsgPage(courseId, offSet, chatRoomMsg);
        if(result!=null && result.size()>0){
            res.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            res.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            res.setData(result);
        }else {
            res.setCode(ReturnMessageType.NO_DATA.getCode());
            res.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return res;
    }

    /**
     * 取得历史消息分页 IOS
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(value = "weixin/live/getChatRoomMsgPage.user")
    @ResponseBody
    @ApiOperation(value = "取得历史消息分页IOS", httpMethod = "GET", notes = "取得历史消息分页IOS")
    public ActResultDto getChatRoomMsgPage(HttpServletRequest request,@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                           Integer offSet,ChatRoomMsg chatRoomMsg) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        long tId = token.getId();
        ActResultDto res = new ActResultDto();
        //判断参数
        if (courseId == null || courseId ==0 ) {
            res.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            res.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return res;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        //判断是否报名,且不是老师自己
        if (!joinCourseRecordService.isJoinCourse(tId, course) && course.getAppId() != tId && course.getTrySeeTime() <= 0) {
            res.setCode(ReturnMessageType.NO_JOIN_COURSE.getCode());
            res.setMessage(ReturnMessageType.NO_JOIN_COURSE.getMessage());
            return res;
        }
        chatRoomMsg.setFromAccount(tId);
        List<Map> result = chatRoomMsgService.getChatRoomMsgPage(courseId, offSet, chatRoomMsg);
        if(result!=null && result.size()>0){
            res.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            res.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            res.setData(result);
        }else {
            res.setCode(ReturnMessageType.NO_DATA.getCode());
            res.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return res;
    }
}
