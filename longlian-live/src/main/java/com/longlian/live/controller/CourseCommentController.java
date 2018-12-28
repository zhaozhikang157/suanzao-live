package com.longlian.live.controller;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseCommentService;
import com.longlian.model.CourseComment;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by pangchao on 2017/2/24.
 */
@RequestMapping("/courseComment")
@Controller
public class CourseCommentController {
    private static Logger log = LoggerFactory.getLogger(CourseCommentController.class);

    @Autowired
    CourseCommentService courseCommentService;

    /**
     * 课程评价
     */
    @RequestMapping("/evaluateCourse.user")
    @ResponseBody
    @ApiOperation(value = "课程评价", httpMethod = "GET", notes = "课程评价")
    public ActResultDto evaluateCourse(HttpServletRequest request,
                                       @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                       @ApiParam(required = true,name = "评论内容",value = "评论内容")String content) {
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (courseId == null || StringUtils.isEmpty(content)) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            CourseComment comment = new CourseComment();
            comment.setCourseId(courseId);
            comment.setAppId(token.getId());
            comment.setContent(content);
            try {
                courseCommentService.insert(comment);
            } catch (Exception e) {
                e.printStackTrace();
                ac.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            }
        }
        return ac;
    }

    /**
     * 课程的全部评价（courseId）
     */
    @RequestMapping("/getCommentListByCourseId")
    @ResponseBody
    @ApiOperation(value = "获取课程评价", httpMethod = "GET", notes = "获取课程评价")
    public ActResultDto getCommentListByCourseId(
            @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
            Integer offset, Integer pageSize) {
        ActResultDto ac = new ActResultDto();
        if (courseId == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            List<Map> list = courseCommentService.getCommentListByCourseId(courseId, offset, pageSize);
            if (list != null && list.size() > 0) {
                ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
                ac.setData(list);
            } else {
                ac.setCode(ReturnMessageType.NO_DATA.getCode());
                ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
            }
        }
        return ac;
    }

    /**
     * 课程的全部评价（courseId）
     */
    @RequestMapping("/getCommentListByCourseId.user")
    @ResponseBody
    @ApiOperation(value = "获取课程评价", httpMethod = "GET", notes = "获取课程评价")
    public ActResultDto getCommentListByCourseIdUser(@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                                     Integer offset, Integer pageSize) {
        return getCommentListByCourseId(courseId, offset, pageSize);
    }

    /**
     * 课程的评价总数（courseId）
     */
    @RequestMapping("/getCommentSumByCourseId.user")
    @ResponseBody
    @ApiOperation(value = "课程的评价总数", httpMethod = "GET", notes = "课程的评价总数")
    public ActResultDto getCommentSumByCourseId(@ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId) {
        ActResultDto ac = new ActResultDto();
        if (courseId == null) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            long count = courseCommentService.getCommentSumByCourseId(courseId);
            ac.setData(count);
        }

        return ac;
    }

    /**
     * 课程评价(系列课或者单节课)
     */
    @RequestMapping("/evaluateServiesCourse.user")
    @ResponseBody
    @ApiOperation(value = "课程评价(系列课或者单节课)", httpMethod = "GET", notes = "课程评价(系列课或者单节课)")
    public ActResultDto evaluateServiesCourse(HttpServletRequest request,
                                              @ApiParam(required = true,name = "系列课ID",value = "系列课ID")Long seriesId,
                                              @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                              @ApiParam(required = true,name = "评论内容",value = "评论内容")String content) {
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (courseId == null && StringUtils.isEmpty(seriesId)) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return ac;
        }
        if (StringUtils.isEmpty(content)) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            CourseComment comment = new CourseComment();

            if (!Utility.isNullorEmpty(seriesId)) {
                comment.setSeriesCourseId(seriesId);
            }
            comment.setCourseId(courseId);
            comment.setAppId(token.getId());
            comment.setContent(content);
            try {
                courseCommentService.insert(comment);
            } catch (Exception e) {
                e.printStackTrace();
                ac.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            }
        }
        return ac;
    }

    /**
     * 系列课的全部评价
     */
    @RequestMapping("/getCommentListByServiesCourseId")
    @ResponseBody
    @ApiOperation(value = "系列课的全部评价", httpMethod = "GET", notes = "系列课的全部评价")
    public ActResultDto getCommentListByServiesCourseId(@ApiParam(required = true,name = "系列课ID",value = "系列课ID")Long seriesId,
                                                        Integer offset, Integer pageSize) {
        ActResultDto ac = new ActResultDto();
        List<Map> list = courseCommentService.getCommentListByServiesCourseIdPage(seriesId, offset, pageSize);
        if (list != null && list.size() > 0) {
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            ac.setData(list);
        } else {
            ac.setCode(ReturnMessageType.NO_DATA.getCode());
            ac.setMessage(ReturnMessageType.NO_DATA.getMessage());
        }
        return ac;
    }

    /**
     * 系列课的全部评价
     */
    @RequestMapping("/getCommentListByServiesCourseId.user")
    @ResponseBody
    @ApiOperation(value = "系列课的全部评价", httpMethod = "GET", notes = "系列课的全部评价")
    public ActResultDto getCommentListByServiesCourseIdUser(@ApiParam(required = true,name = "系列课ID",value = "系列课ID")Long seriesId, Integer offset, Integer pageSize) {
        return getCommentListByServiesCourseId(seriesId, offset, pageSize);
    }
}
