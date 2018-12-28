package com.longlian.third.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.longlian.live.service.CountService;
import com.longlian.token.WechatOfficialIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.huaxin.util.ObjectUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.ReturnMessageType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

/**
 * 身份URL拦截
 * Created by lh on 2016/5/25.
 */
public class SpringMVCInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String ss = request.getRequestURI();
        //判断是否是需要拦截的url
        //从redis里 取出身份信息
        WechatOfficialIdentity identify =  this.getUserTokenModel(request);
        if (identify == null ) {
            output(response);
            return false;
        } else{
            request.setAttribute(CecurityConst.REQUEST_USER_ATTR, identify);
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
    public static WechatOfficialIdentity getUserTokenModel(HttpServletRequest request ){
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

        return getUserTokenModelByToken(authToken);
    }


    /**
     * 获取当前登录用户对象模型
     * @param authToken
     * @return
     */
    public static WechatOfficialIdentity getUserTokenModelByToken(String authToken ){
        WechatOfficialIdentity userIdentity = null;
        String oldAuthToken = authToken;
        if(!StringUtils.isEmpty(authToken) && authToken.startsWith(CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX)){
            authToken = authToken.substring(CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX.length() );
            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(authToken).getBody();
                String subject = claims.getSubject();
                if(!Utility.isNullorEmpty(subject)){
                    //读取redis
                    Map<String ,String > map =   RedisUtil.getRedisUtil().hmgetAll(RedisKey.ll_live_third_login_prefix + subject);
                    if(map != null && !map.isEmpty()){
                        try {
                            userIdentity = (WechatOfficialIdentity)ObjectUtil.mapToObject(map, WechatOfficialIdentity.class);
                            if (userIdentity != null) {
                                //不同的地方登录，前后登录的token变了，返回null;
                                if (!oldAuthToken.equals(userIdentity.getToken())) {
                                    return null;
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    RedisUtil.getRedisUtil().expire(RedisKey.ll_live_third_login_prefix + subject, RedisKey.ll_live_app_user_login_valid_time);
                }
            } catch (final SignatureException e) {
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

}
