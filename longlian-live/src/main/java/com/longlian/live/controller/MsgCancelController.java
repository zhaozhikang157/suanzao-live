package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseService;
import com.longlian.live.service.MsgCancelService;
import com.longlian.model.Course;
import com.longlian.model.MsgCancel;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by admin on 2017/12/5.
 * <p>
 * 聊天室消息撤回
 */
@RequestMapping("/msgCancel")
@Controller
public class MsgCancelController {
    private static Logger log = LoggerFactory.getLogger(MsgCancelController.class);

    @Autowired
    MsgCancelService msgCancelService;
    @Autowired
    CourseService courseService;

    /**
     * 撤回消息操作
     *
     * @return
     */
    @RequestMapping("/cancelMsgOption.user")
    @ResponseBody
    @ApiOperation(value = "撤回消息操作", httpMethod = "POST", notes = "撤回消息操作")
    public ActResultDto cancelMsgOption(HttpServletRequest request, MsgCancel msgCancel) {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto resultDto = new ActResultDto();
        if (msgCancel.getCourseId() == null || msgCancel.getCourseId() < 1) {
            resultDto.setCode(ReturnMessageType.NO_COURSE.getCode());
            resultDto.setMessage(ReturnMessageType.NO_COURSE.getMessage());
            return resultDto;
        }
        Course course = courseService.getCourseFromRedis(msgCancel.getCourseId());
        msgCancel.setOptId(token.getId());
        return msgCancelService.insertMsgCancel(msgCancel,course);
    }
}
