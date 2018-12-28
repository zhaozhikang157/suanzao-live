package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppUserCommentService;
import com.longlian.model.AppUserComment;
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
import java.util.Date;

/**
 * Created by admin on 2017/2/12.
 */
@RequestMapping("/comment")
@Controller
public class AppUserCommentController {
    private static Logger log = LoggerFactory.getLogger(AppUserCommentController.class);

    @Autowired
    AppUserCommentService appUserCommentService;

    @RequestMapping("/createComment.user")
    @ResponseBody
    @ApiOperation(value = "课程评论", httpMethod = "GET", notes = "课程评论")
    public ActResultDto createComment(HttpServletRequest request,
                                      @ApiParam(required = true,name = "评论内容")String comment) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if (StringUtils.isEmpty(comment)) {
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return resultDto;
        }
        try {
            AppUserComment appUserComment = new AppUserComment();
            appUserComment.setCreateTime(new Date());
            appUserComment.setAppId(token.getId());
            appUserComment.setMobile(token.getMobile());
            appUserComment.setHandStatus("0");
            appUserComment.setRemarks(comment);
            appUserComment.setStatus("0");
            appUserCommentService.createAppUserComment(appUserComment);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
            return resultDto;
        } catch (Exception e) {
            resultDto.setCode(ReturnMessageType.APP_USER_COMMENT_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.APP_USER_COMMENT_ERROR.getMessage());
            return resultDto;
        }
    }

    /**
     * 课程举报
     */
    @RequestMapping("/reportCourse.user")
    @ResponseBody
    @ApiOperation(value = "课程举报", httpMethod = "GET", notes = "课程举报")
    public ActResultDto reportCourse(HttpServletRequest request,
                                     @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId,
                                     @ApiParam(required = true,name = "举报内容",value = "举报内容")String content) {
        ActResultDto ac = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (courseId == null || StringUtils.isEmpty(content)) {
            ac.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
        } else {
            AppUserComment appUserComment = new AppUserComment();
            appUserComment.setAppId(token.getId());
            appUserComment.setMobile(token.getMobile());
            appUserComment.setRemarks(content);
            appUserComment.setCourseId(courseId);
            try {
                appUserCommentService.insert(appUserComment);
            } catch (Exception e) {
                e.printStackTrace();
                ac.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
                ac.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
            }
        }
        return ac;
    }

}
