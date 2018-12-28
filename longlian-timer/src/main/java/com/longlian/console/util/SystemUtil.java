package com.longlian.console.util;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.huaxin.util.constant.Const;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huaxin.util.DateUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.token.ConsoleUserIdentity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
 * Created by syl on 2016/4/13.
 */
@Component("systemUtilLonglian")
public class SystemUtil {
    private static Logger log = LoggerFactory.getLogger(SystemUtil.class);
    public static String clientType[]={"ios","android","weixin"};
    @Autowired
    RedisUtil redisUtil;
    /**
     * 获取当前登录用户对象模型
     * @param request
     * @return
     */
    public ConsoleUserIdentity getUserTokenModel(HttpServletRequest request  ){
        Cookie[] cookies = request.getCookies();
        String authToken = "";
        if(cookies != null){
            for (Cookie cookie : cookies){
                String token =  cookie.getName();
                if(token.equals("LONGLIAN-JWT")){
                    authToken = cookie.getValue();
                    break;
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
    public ConsoleUserIdentity getUserTokenModelByToken(String authToken){
        ConsoleUserIdentity userIdentity = null;
        String oldAuthToken = authToken;
        if(!StringUtils.isEmpty(authToken)  && authToken.startsWith(CecurityConst.OAUTH_TOKEN_PREFIX)){
            authToken = authToken.substring(CecurityConst.OAUTH_TOKEN_PREFIX.length() );
            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(authToken).getBody();
                String subject = claims.getSubject();
                if(!Utility.isNullorEmpty(subject)){
                    //读取redis
                    Map<String ,String > map =   redisUtil.hmgetAll( RedisKey.user_manage_login_prefix + subject);
                    if(map != null && !map.isEmpty()){
                        //System.out.println("subject:" + subject);
                        userIdentity = new ConsoleUserIdentity();
                        userIdentity.setId(Utility.parseLong(subject));
                        userIdentity.setAccount(map.get("account"));
                        userIdentity.setName(map.get("name"));
                        userIdentity.setEmployeeType(map.get("employeeType"));
                        userIdentity.setToken(map.get("token"));
                        userIdentity.setYunxinToken(map.get("yunxinToken"));
                        
                        //不同的地方登录，前后登录的token变了，返回null;
                        if (!oldAuthToken.equals(userIdentity.getToken())) {
                            return null;
                        }
                        redisUtil.expire(RedisKey.user_manage_login_prefix + subject, RedisKey.user_login_valid_time);
                    }
                }
            } catch (final SignatureException e) {
            	return null;
                // throw new ServletException("Invalid token.");
            }catch (ParseException e) {
                e.printStackTrace();
                log.error(e.toString());
                return null;
            }

        }
        return userIdentity;
    }
    
    public String createToken(long userid) {
       return  CecurityConst.OAUTH_TOKEN_PREFIX + Jwts.builder().setSubject(userid +"").claim(
                "roles", userid).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256,
                "secretkey").compact();
    }

    /**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     * @param request
     * @return ip
     */
   public static String getLocalIp(HttpServletRequest request) {
	   String ip = request.getHeader("X-Forwarded-For");
       if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
           //多次反向代理后会有多个ip值，第一个ip才是真实ip
           int index = ip.indexOf(",");
           if(index != -1){
               return ip.substring(0,index);
           }else{
               return ip;
           }
       }
       ip = request.getHeader("X-Real-IP");
       if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
           return ip;
       }
       return request.getRemoteAddr();
   }




}

