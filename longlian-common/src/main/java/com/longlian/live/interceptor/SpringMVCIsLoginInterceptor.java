package com.longlian.live.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.huaxin.util.ObjectUtil;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.CountService;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份URL拦截.user
 * Created by lh on 2016/5/25.
 */
public class SpringMVCIsLoginInterceptor implements HandlerInterceptor {
    private static Logger log = LoggerFactory.getLogger(SpringMVCIsLoginInterceptor.class);
    @Autowired
    CountService countService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String ss = request.getRequestURI();
        String queryStr = request.getQueryString();
        String method = request.getMethod();
        String toUrl = ss + "?" + queryStr;
        //判断是否是需要拦截的url
        //从redis里 取出身份信息
        AppUserIdentity identify =  this.getUserTokenModel(request);
        if (identify == null ) {
            UserAgent ua = UserAgentUtil.getUserAgentCustomer(request);
            if (ua != null && (UserAgentUtil.ios.equals(ua.getCustomerType()) || UserAgentUtil.android.equals(ua.getCustomerType()))) {
                output(response);
            } else {
                if(ResourceHttpRequestHandler.class == handler.getClass()) {
                    if ("GET".equals(method)) {
                        response.sendRedirect("/weixin/toLogin?toUrl=" + URLEncoder.encode(toUrl));
                    } else {
                        response.sendRedirect("/weixin/toLogin");
                    }

                }else {
                    HandlerMethod handlerMethod = (HandlerMethod)handler;
                    Class cls = handlerMethod.getMethod().getReturnType();
                    if(ModelAndView.class == cls || String.class == cls){
                        if (ss.startsWith("/pc") || ss.startsWith("/inviCode") || ss.startsWith("/pcCourse")) {
                            response.sendRedirect("/pc/login");
                        } else {
                            if ("GET".equals(method)) {
                                response.sendRedirect("/weixin/toLogin?toUrl=" + URLEncoder.encode(toUrl));
                            } else {
                                response.sendRedirect("/weixin/toLogin");
                            }
                        }
                    }else {
                        output(response);
                    }
                }
            }
            return false;

        } else{
            countService.activeUserCount(identify.getId());
            //此方法，转到SpringMVCIsLoginInterceptor.getUserTokenModel里面，方便以后取
            //request.setAttribute(CecurityConst.REQUEST_USER_ATTR, token);
        }
        return true;
    }
    /**
     * 输出
     * @param response
     */
    public void output(HttpServletResponse response) {
        
        ActResultDto dto = new ActResultDto();
        dto.setCode(ReturnMessageType.ERROR_403.getCode());
        dto.setMessage(ReturnMessageType.ERROR_403.getMessage());
        response.setCharacterEncoding("UTF-8");  
        response.setContentType("application/json; charset=utf-8");  
        PrintWriter out = null;  
        try {  
            out = response.getWriter();  
            out.append(JSON.toJSONString(dto));  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (out != null) {  
                out.close();  
            }  
        }  
    }
    /**
     * 获取当前登录用户对象模型
     * @param request
     * @return
     */
    public static AppUserIdentity getUserTokenModel(HttpServletRequest request ){
        //如果request存在直接返回appuser,避免重复构建
        AppUserIdentity appUser = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (appUser != null) {
            return appUser;
        }

        String authToken = request.getParameter("token");
        if (StringUtils.isEmpty(authToken)) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies){
                    String token =  cookie.getName();
                    if(token.equals("token")){
                        authToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        appUser = getUserTokenModelByToken(authToken);
        if (appUser != null) {
            request.setAttribute(CecurityConst.REQUEST_USER_ATTR, appUser);
        }
        return appUser;
    }
    
    
    /**
     * 获取当前登录用户对象模型
     * @param authToken
     * @return
     */
    public static AppUserIdentity getUserTokenModelByToken(String authToken ){
        AppUserIdentity userIdentity = null;
        String oldAuthToken = authToken;
        String key = RedisKey.ll_live_weixin_login_prefix;
        //微信端登录
        if(!StringUtils.isEmpty(authToken) && authToken.startsWith(CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX)){
            authToken = authToken.substring(CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX.length() );
        } else if (!StringUtils.isEmpty(authToken) && authToken.startsWith(CecurityConst.LL_LIVE_TEACHER_OAUTH_TOKEN_PREFIX)) {
            authToken = authToken.substring(CecurityConst.LL_LIVE_TEACHER_OAUTH_TOKEN_PREFIX.length() );
            key = RedisKey.ll_live_teacher_app_login_prefix;
        } else if (!StringUtils.isEmpty(authToken) && authToken.startsWith(CecurityConst.LL_LIVE_TEACHER_WEB_OAUTH_TOKEN_PREFIX)) {
            authToken = authToken.substring(CecurityConst.LL_LIVE_TEACHER_WEB_OAUTH_TOKEN_PREFIX.length() );
            key = RedisKey.ll_live_teacher_web_login_prefix;
        }

        if (!StringUtils.isEmpty(authToken) ) {
            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(authToken).getBody();
                String subject = claims.getSubject();
                if(!Utility.isNullorEmpty(subject)){
                    //读取redis
                    Map<String ,String > map =   RedisUtil.getRedisUtil().hmgetAll(key + subject);
                    if(map != null && !map.isEmpty()){
                        try {
                            userIdentity = (AppUserIdentity)ObjectUtil.mapToObject(map, AppUserIdentity.class);
                            if (userIdentity != null) {
                                //不同的地方登录，前后登录的token变了，返回null;
                                if (!oldAuthToken.equals(userIdentity.getToken())) {
                                    return null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("身份信息转化失败:{}",map);
                            log.error("身份信息转化失败",e);
                        }
                    }
                    RedisUtil.getRedisUtil().expire(key + subject, RedisKey.ll_live_app_user_login_valid_time);
                }
            } catch (final SignatureException e) {
                log.error("token:{}",authToken);
                log.error("token转化失败",e);
                return null;
            }
        }
        return userIdentity;
    }


    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
    }

    public static void main(String[] args) {
        String authToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNTkwNSIsInJvbGVzIjoxNTkwNSwiaWF0IjoxNDk3NTk2MjY5fQ.uxwL0F2Oqo5dB8JtTusNggwZROg5V7zO70hqAXE7SRU";
        final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(authToken).getBody();
        String subject = claims.getSubject();
    }
}
