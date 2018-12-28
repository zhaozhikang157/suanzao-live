package com.longlian.live.controller;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.ProxyTeacherService;
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
import java.util.Map;

/**
 * Created by admin on 2017/5/8.
 */
@Controller
@RequestMapping(value = "/proxuUser")
public class ProxyUserController {
    private static Logger log = LoggerFactory.getLogger(ProxyUserController.class);
   
    @Autowired
    ProxyTeacherService proxyTeacherService;

    @RequestMapping("/getProxyUserByTeacherId")
    @ResponseBody
    @ApiOperation(value = "获取代理人ID", httpMethod = "GET", notes = "获取代理人ID")
    public ActResultDto getProxyUserByTeacherId(HttpServletRequest request,
                                                @ApiParam(required =true, name = "讲师ID", value = "讲师ID") Long teacherId){
       ActResultDto result = new ActResultDto();
        if(Utility.isNullorEmpty(teacherId) || teacherId==0)
        {
            result.setCode(ReturnMessageType.CODE_PARAM_RETURN.getCode());
            result.setMessage(ReturnMessageType.CODE_PARAM_RETURN.getMessage());
            return result;
        }
        Map map= proxyTeacherService.getProxyAppIdByTeacherId(teacherId);
        result.setData(map);
        return result;
    }
}
