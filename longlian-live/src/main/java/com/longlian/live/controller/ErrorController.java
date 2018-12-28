package com.longlian.live.controller;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.dto.UserAgent;
import com.longlian.type.ReturnMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统错误统一处理类
 * @author lh
 *
 */
@Controller
@RequestMapping(value = "error")
public class ErrorController {
    private static Logger log = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping(value = "{code}")
    public ModelAndView error(@PathVariable("code") String code , HttpServletRequest request , HttpServletResponse response ) {
        UserAgent ua = UserAgentUtil.getUserAgentCustomer(request);

        if (ua != null && (UserAgentUtil.ios.equals(ua.getCustomerType()) || UserAgentUtil.android.equals(ua.getCustomerType()))) {
            Map<String , Object> map = new HashMap();
            map.put("code", ReturnMessageType.ERROR_500.getCode());
            map.put("message",ReturnMessageType.ERROR_500.getMessage());
            //map.put("data",ex.getMessage());
            FastJsonJsonView json = new FastJsonJsonView();
            json.setAttributesMap(map);
            ModelAndView view = new ModelAndView(json);
            return view;
        } else {
            int errorCode = 500;
            if ("000099".equals(code)) {
                errorCode = 404;
            } else if ("000100".equals(code)) {
                errorCode = 401;
            }else if ("000101".equals(code)) {
                errorCode = 403;
            }
            ModelAndView mv = new ModelAndView("common/" + errorCode  );
            return mv;
        }

    }
}
