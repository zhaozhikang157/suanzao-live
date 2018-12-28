package com.longlian.res.interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.StringUtil;
import com.huaxin.util.ObjectUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.token.ConsoleUserIdentity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

/**
 * 身份URL拦截
 * Created by lh on 2016/5/25.
 */
public class SpringMVCInterceptor implements HandlerInterceptor {

    @Autowired
    RedisUtil redisUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        ConsoleUserIdentity identify = null;
        String accessToken = request.getParameter("accessToken");
        //根据accessToken判断是否能登录
        if (!StringUtil.isEmpty(accessToken)) {
           String userId = redisUtil.get(RedisKey.user_res_access + accessToken);
           redisUtil.del(RedisKey.user_res_access + accessToken);
           
           if (!StringUtil.isEmpty(userId)) {
               identify =  getUserByKey(userId);
               response.setHeader("SET-COOKIE", "RES-JWT=" + identify.getToken() + "; HttpOnly");
           }
        } else {
            //判断是否是需要拦截的url
             identify =  this.getUserTokenModel(request);
        }
        
        if (identify == null ) {
            response.sendRedirect("/");
            return false;
        } 
        request.setAttribute(CecurityConst.REQUEST_USER_ATTR, identify);
        return true;
    }
     
    /**
     * 获取当前登录用户对象模型
     * @param request
     * @param redisKeyPrefix 存储用户的redisKey前缀
     * @return
     */
    public static ConsoleUserIdentity getUserTokenModel(HttpServletRequest request ){
        
        String authToken = "";
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                String token =  cookie.getName();
                if(token.equals("RES-JWT")){
                    authToken = cookie.getValue();
                    break;
                }
            }
        }
        return getUserTokenModelByToken(authToken);
    }
    
    public static ConsoleUserIdentity getUserByKey(String userId) {
        ConsoleUserIdentity userIdentity = null;
      //读取redis
        Map<String ,String > map =   RedisUtil.getRedisUtil().hmgetAll( RedisKey.user_login_res_prefix + userId);
        if(map != null && !map.isEmpty()){
            //System.out.println("subject:" + subject);
            userIdentity = new ConsoleUserIdentity();
            userIdentity.setId(Long.parseLong(userId));
            userIdentity.setAccount(map.get("account"));
            userIdentity.setName(map.get("name"));
            userIdentity.setEmployeeType(map.get("employeeType"));
            userIdentity.setToken(map.get("token"));
            RedisUtil.getRedisUtil().expire(RedisKey.user_app_login_prefix + userId, RedisKey.user_login_valid_time);
        }
        return userIdentity;
    }
    
    /**
     * 获取当前登录用户对象模型
     * @param authToken
     * @return
     */
    public static ConsoleUserIdentity getUserTokenModelByToken(String authToken ){
        ConsoleUserIdentity userIdentity = null;
        String oldAuthToken = authToken;
        if(!StringUtils.isEmpty(authToken) && authToken.startsWith(CecurityConst.OAUTH_TOKEN_PREFIX)){
            authToken = authToken.substring(CecurityConst.OAUTH_TOKEN_PREFIX.length() );
            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(authToken).getBody();
                String subject = claims.getSubject();
                if(!Utility.isNullorEmpty(subject)){
                    userIdentity  = getUserByKey(subject);
                   //不同的地方登录，前后登录的token变了，返回null;
                    if (!oldAuthToken.equals(userIdentity.getToken())) {
                        return null;
                    }
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
