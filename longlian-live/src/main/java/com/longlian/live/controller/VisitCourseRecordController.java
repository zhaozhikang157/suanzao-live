package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.VisitCourseRecordService;
import com.longlian.token.AppUserIdentity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2017/2/24.
 */
@RequestMapping("/visitCourseRecord")
@Controller
public class VisitCourseRecordController {
    private static Logger log = LoggerFactory.getLogger(VisitCourseRecordController.class);

    @Autowired
    VisitCourseRecordService visitCourseRecordService;

    /**
     * 访问记录
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping("/insertRecord.user")
    @ResponseBody
    @ApiOperation(value = "访问记录", httpMethod = "GET", notes = "访问记录")
    public ActResultDto insertRecord(HttpServletRequest request ,
                                     @ApiParam(required =true, name = "课程ID", value = "课程ID")  Long courseId,
                                     @ApiParam(required =true, name = "用户ID", value = "用户ID")  Long inviAppId ,
                                     @ApiParam(required =true, name = "类型", value = "类型")  String type){
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        return visitCourseRecordService.insertRecord(token.getId(),courseId , inviAppId , type);
    }
}
