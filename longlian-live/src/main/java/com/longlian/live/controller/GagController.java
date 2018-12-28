package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CourseManagerService;
import com.longlian.live.service.GagService;
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

/**
 * Created by admin on 2017/8/7.
 */
@RequestMapping("/gag")
@Controller
public class GagController {
    private static Logger log = LoggerFactory.getLogger(GagController.class);

    @Autowired
    GagService gayService;
    @Autowired
    CourseManagerService courseManagerService;

    /**
     * 设置用户禁言
     * @param request
     * @param userId
     * @param courseId
     * @retur
     */
    @RequestMapping("/setGay.user")
    @ResponseBody
    @ApiOperation(value = "设置用户禁言", httpMethod = "GET", notes = "设置用户禁言")
    public ActResultDto setGay(HttpServletRequest request,
                               @ApiParam(required = true,name = "用户ID",value = "用户ID")Long userId,
                               @ApiParam(required = true,name = "课程ID",value = "课程ID")Long courseId ,
                               @ApiParam(required = true,name = "操作人ID",value = "操作人ID")Long optId) {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (userId > 0 && courseId > 0 && optId > 0) {
            Boolean bo = true;
            if(!String.valueOf(token.getId()).equals(optId.toString())){
                bo = courseManagerService.isCourseManager(courseId,optId,"");
            }
            if(bo){
                return gayService.setGag(userId, courseId, optId);
            }else {
                resultDto.setCode(ReturnMessageType.NO_JURISDICTION_GAG.getCode());
                resultDto.setMessage(ReturnMessageType.NO_JURISDICTION_GAG.getMessage());
            }
        } else {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }
        return resultDto;
    }

    /**
     * 取消用户禁言
     * @param request
     * @param userId
     * @param courseId
     * @return
     */
    @RequestMapping("/delGay.user")
    @ResponseBody
    public ActResultDto delGay(HttpServletRequest request, Long userId, Long courseId,Long optId) {
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (userId > 0 && courseId > 0 && optId > 0) {
            Boolean bo = true;
            if(!String.valueOf(token.getId()).equals(optId.toString())){
                bo = courseManagerService.isCourseManager(courseId,optId,"");
            }
            if(bo){
                return gayService.delGag(userId, courseId);
            }else {
                resultDto.setCode(ReturnMessageType.NO_JURISDICTION_DEL_GAG.getCode());
                resultDto.setMessage(ReturnMessageType.NO_JURISDICTION_DEL_GAG.getMessage());
            }
        } else {
            resultDto.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
        }
        return resultDto;
    }
}
