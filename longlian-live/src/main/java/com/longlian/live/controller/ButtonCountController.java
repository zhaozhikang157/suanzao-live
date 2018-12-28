package com.longlian.live.controller;

import com.alibaba.dubbo.common.json.JSON;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.dto.ButtonCount;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lh on 2017/6/21.
 */
@Controller
@RequestMapping("/button")
public class ButtonCountController {
    private static Logger log = LoggerFactory.getLogger(ButtonCountController.class);
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/count")
    @ResponseBody
    @ApiOperation(value = "按钮监控", httpMethod = "GET", notes = "按钮监控")
    public ActResultDto count(HttpServletRequest  request , ButtonCount count){
        ActResultDto resultDto = new ActResultDto();
        AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);

        UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(request);
        if (token != null) {
            count.setUserId(token.getId());
        }
        if (userAgent != null) {
            count.setClientType(userAgent.getCustomerType());
        }
        count.setCreateTime(new Date());
        log.info("请求记录：{}" , JsonUtil.toJson(count));
        redisUtil.lpush(RedisKey.ll_button_visit_record_wait2db , JsonUtil.toJson(count));
        resultDto.setCode(ReturnMessageType.CODE_MESSAGE_TRUE.getCode());
        resultDto.setMessage(ReturnMessageType.CODE_MESSAGE_TRUE.getMessage());
        return resultDto;
    }
}
