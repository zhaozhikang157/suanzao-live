package com.longlian.live.frontcontroller;

import com.huaxin.util.MessageClient;
import com.huaxin.util.StringUtil;
import com.huaxin.util.constant.RedisKey;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ChatRoomMsgDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.CourseService;
import com.longlian.live.third.service.ChatMsgRemote;
import com.longlian.model.course.CourseCard;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 指定老师邀请卡
 */
@Controller
@RequestMapping("/courseCard")
public class ApiCourseCardController {
    private static Logger log = LoggerFactory.getLogger(ApiCourseCardController.class);
    @Autowired
    CourseService courseService;

    /**
     * 是否允许传模板
     * @param courseId
     * @return
     */
    @RequestMapping("/isPermit")
    @ResponseBody
    @ApiOperation(value = "当前课程是否可生成邀请卡", httpMethod = "GET", notes = "当前课程是否可生成邀请卡")
    public ActResultDto getIsPermit(@RequestParam(required = true) Long courseId){
        ActResultDto resultDto = new ActResultDto();
        try {
            int result = courseService.findCourseIsExist(courseId);
            CourseCard card = courseService.findCardUrlByCourseId(courseId);
            resultDto.setData(result);
            resultDto.setExt(card);
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询当前课程是否允许根据模板生成邀请卡异常:"+e.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        }
        return resultDto;
    }
    /**
     * 是否允许传模板,android专用
     * @param courseId
     * @return
     */
    @RequestMapping("/isPermitForApp")
    @ResponseBody
    @ApiOperation(value = "当前课程是否可生成邀请卡", httpMethod = "GET", notes = "当前课程是否可生成邀请卡")
    public ActResultDto getPermitForApp(@RequestParam(required = true) Long courseId){
        ActResultDto resultDto = new ActResultDto();
        try {
            int result = courseService.findCourseIsExist(courseId);
            CourseCard card = courseService.findCardUrlByCourseId(courseId);
            resultDto.setData(result);
            resultDto.setExt("");
            if(card != null) {
                String modelUrl = card.getModelUrl();
                if(StringUtils.isNotBlank(modelUrl)){
                    resultDto.setAttach(card.getCardUrl());
                } else {
                    resultDto.setAttach("");
                }
            } else {
                resultDto.setAttach("");
            }
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询当前课程是否允许根据模板生成邀请卡异常app:"+e.getMessage());
            resultDto.setCode(ReturnMessageType.CODE_MESSAGE_ERROR.getCode());
            resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_ERROR.getMessage());
        }
        return resultDto;
    }
    /**
     * 前端邀请卡预览
     * @param request
     * @param courseCard
     * @return
     */
    @RequestMapping(value = "/getPreviewCourseCard")
    @ResponseBody
    public ActResultDto getPreviewCourseCard(HttpServletRequest request,CourseCard courseCard){
        ActResultDto ac = new ActResultDto();
        if(StringUtils.isBlank(courseCard.getModelUrl()) || courseCard.getCourseId() == null || courseCard.getCourseId().longValue() == 0L){
            ac.setData(null);
            ac.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return ac;
        }
        try {
            String url = courseService.doPreviewCourseCard(courseCard, request);
            ac.setData(url);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("预览邀请卡异常："+e.getMessage());
            ac.setCode(ReturnMessageType.SERVER_ERROR_RETY.getCode());
            ac.setMessage(ReturnMessageType.SERVER_ERROR_RETY.getMessage());
        }
        return ac;
    }

    /**
     * 删除邀请卡
     * @param request
     * @param courseCard
     * @return
     */
    @RequestMapping(value = "/delCourseCard")
    @ResponseBody
    public ActResultDto delCourseCard(HttpServletRequest request,CourseCard courseCard){
        ActResultDto ac = new ActResultDto();
        if(courseCard.getCourseId().longValue() == 0L){
            ac.setData(null);
            ac.setCode(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getCode());
            ac.setMessage(ReturnMessageType.CODE_PARAM_VALUE_ERROR.getMessage());
            return ac;
        }
        try {
            int result = courseService.deleteCourseCard(courseCard, request);
            ac.setData(result);
            ac.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
            ac.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ac.setCode(ReturnMessageType.SERVER_ERROR_RETY.getCode());
            ac.setMessage(ReturnMessageType.SERVER_ERROR_RETY.getMessage());
        }
        return ac;
    }
}
