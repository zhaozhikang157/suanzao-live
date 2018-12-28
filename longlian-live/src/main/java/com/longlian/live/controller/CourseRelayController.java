package com.longlian.live.controller;

import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.CourseRelayService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/11.
 */
@Controller
@RequestMapping("courseRelay")
public class CourseRelayController {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private CourseRelayService courseRelayService;

    /**
     * 创建转播
     * @param request
     * @param oriCourseId  原课程id
     * @param relayCharge  转播价格
     * @param relayScale  转播收益比例
     * @return
     */
    @RequestMapping(value = "/createRelayCourse.user", method = RequestMethod.POST)
    @ResponseBody
    public ActResultDto createRelayCourse(HttpServletRequest request, String oriCourseId,String relayCharge,String relayScale){
        ActResultDto actResultDto = new ActResultDto();

        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (token==null) {
            actResultDto.setCode(ReturnMessageType.CODE_USERNAME_ISEXIST_TRUE.getCode());
            actResultDto.setMessage(ReturnMessageType.CODE_USERNAME_ISEXIST_TRUE.getMessage());
            return actResultDto;
        }
        if(oriCourseId==null || "".equals(oriCourseId)){
            actResultDto.setCode(ReturnMessageType.PARAMETER_NULL_ERROR.getCode());
            actResultDto.setMessage(ReturnMessageType.PARAMETER_NULL_ERROR.getMessage());
            return actResultDto;
        }
        actResultDto=courseRelayService.createRelayCourse(token.getId(),oriCourseId,relayCharge,relayScale);
        return actResultDto;
    }

}
