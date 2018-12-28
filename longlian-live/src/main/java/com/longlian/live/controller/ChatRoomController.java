package com.longlian.live.controller;

import cn.jpush.api.utils.StringUtils;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.ChatRoomMsgService;
import com.longlian.live.service.CourseService;
import com.longlian.live.util.yunxin.CheckSumBuilder;
import com.longlian.live.util.yunxin.YunXinUtil;
import com.longlian.model.Course;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liuhan on 2017-02-17.
 */
@Controller
@RequestMapping(value = "/chatRoom")
public class ChatRoomController{
    private static Logger log = LoggerFactory.getLogger(ChatRoomController.class);
    @Autowired
    private ChatRoomMsgService chatRoomMsgService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    CourseService courseService;

    /**
     * 学生提问的问题
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/findQuestry.user")
    @ResponseBody
    @ApiOperation(value = "学生提问的问题", httpMethod = "GET", notes = "学生提问的问题")
    public ActResultDto findQuestryPage(HttpServletRequest request ,
                                        @ApiParam(required = true,name = "课程ID",value = "课程ID") Long courseId ,
                                        @ApiParam(required = true,name = "消息ID",value = "消息ID") Long msgId ,
                                        Long pageSize){
        ActResultDto resultDto = new ActResultDto();
//        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(courseId == null || courseId < 0){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(course == null ){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        return chatRoomMsgService.findQuestry(courseId, msgId, pageSize);
    }

    /**
     * 所有文本自定义消息
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/findAllMsg.user")
    @ResponseBody
    @ApiOperation(value = "所有文本自定义消息", httpMethod = "GET", notes = "所有文本自定义消息")
    public ActResultDto findAllMsgPage(HttpServletRequest request ,
                                       @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId ,
                                       @ApiParam(required = true,name = "消息ID",value = "消息ID")Long msgId , Long pageSize){
        ActResultDto resultDto = new ActResultDto();
//        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(courseId == null || courseId < 0){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(course == null){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        return chatRoomMsgService.findAllMsg(courseId, course.getAppId(), msgId, pageSize);
    }

    /**
     * 老师和打赏消息消息列表
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/findTeacherMsg.user")
    @ResponseBody
    @ApiOperation(value = "老师和打赏消息消息列表", httpMethod = "GET", notes = "老师和打赏消息消息列表")
    public ActResultDto findTeacherMsg(HttpServletRequest request , @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId , Integer offset , Integer pageSize){
        ActResultDto resultDto = new ActResultDto();
//        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(courseId == null || courseId < 0){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(course == null){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        return chatRoomMsgService.findTeacherMsgPage(courseId, course.getAppId(), offset, pageSize);
    }

    /**
     * 回放 -- 最后三条弹幕
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/findLastThreeMsg.user")
    @ResponseBody
    @ApiOperation(value = "回放 -- 最后三条弹幕", httpMethod = "GET", notes = "回放 -- 最后三条弹幕")
    public ActResultDto findLastThreeMsg(HttpServletRequest request , Long courseId){
        ActResultDto resultDto = new ActResultDto();
//        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(courseId == null || courseId < 0){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if(course == null){
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        return chatRoomMsgService.findLastThreeMsg(courseId,course.getAppId());
    }

    /**
     * 查询老师的语音,图片,文本,禁言(解除)提醒,管理员操作(提醒) , 学生禁言(解除)提醒 , 打赏信息,提问
     * @param request
     * @param courseId
     * @param msgId
     * @param pageSize
     * @return
     */
    @RequestMapping("/findMsgByCourseId.user")
    @ResponseBody
    @ApiOperation(value = "查询消息", httpMethod = "GET", notes = "查询消息")
    public ActResultDto findMsgByCourseId(HttpServletRequest request ,
                                          @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId ,
                                          @ApiParam(required = true,name = "消息ID",value = "消息ID")Long msgId , Long pageSize) {
        ActResultDto resultDto = new ActResultDto();
//        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (courseId == null || courseId < 0) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(courseId);
        if (course == null) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        if(msgId == null || msgId < 1) msgId = 0l;
        if(pageSize == null || pageSize < 1) pageSize = 10l;
        return chatRoomMsgService.findMsgByCourseId(course, msgId , pageSize);
    }
}
