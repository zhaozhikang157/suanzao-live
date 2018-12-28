package com.longlian.console.util;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.CourseDto;
import com.longlian.token.ConsoleUserIdentity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * Created by syl on 2016/4/13.
 */
@Component("systemUtilLonglian")
public class SystemUtil {
    private static Logger log = LoggerFactory.getLogger(SystemUtil.class);
    public static String clientType[]={"ios","android","weixin"};
    @Autowired
    RedisUtil redisUtil;
    public static String secondsToHoursMin(long mseconds){
        String timeStr="";
      // long secon= mseconds/1000;  //毫秒转s
        BigDecimal round=new BigDecimal("60");
        BigDecimal secon = new BigDecimal(mseconds).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        if (secon.compareTo(round)>0) {
            BigDecimal[] bigDecimals = secon.divideAndRemainder(round);
            BigDecimal second =bigDecimals[1]; //secon % 60;  //商
            BigDecimal min = bigDecimals[0];//secon / 60;   //余数
            timeStr = min.intValue() + "分" + second + "秒";
            if (min.compareTo(round)>0) {
                min=secon.divide(round,2,BigDecimal.ROUND_HALF_UP).divideAndRemainder(round)[1];
               // min = (secon / 60) % 60;
               BigDecimal hour=secon.divide(round,2,BigDecimal.ROUND_HALF_UP).divideAndRemainder(round)[0];
               // long hour = (secon / 60) / 60;
                timeStr = hour.intValue() + "小时" + min.intValue() + "分" + second + "秒";
                if (hour.compareTo(new BigDecimal("24")) > 0) {
                    hour=secon.divide(round,2,BigDecimal.ROUND_HALF_UP).divide(round,2,BigDecimal.ROUND_HALF_UP).divideAndRemainder(new BigDecimal("24"))[1];
                  //  hour = ((secon / 60) / 60) % 24;
                    BigDecimal day=secon.divide(round,2,BigDecimal.ROUND_HALF_UP).divide(round,2,BigDecimal.ROUND_HALF_UP).divideAndRemainder(new BigDecimal("24"))[0];
                    //long day = (((secon / 60) / 60) / 24);
                    timeStr = day.intValue() + "天" + hour.intValue() + "小时" + min.intValue() + "分" + second + "秒";
                }
            }
        }else{
            timeStr=secon+"秒";
        }
        return timeStr;
    }
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

    public static List<Map> convertListBean2ListMap(List<CourseDto> beanList) throws Exception {
        List<Map> mapList = new ArrayList<Map>();
        for(int i=0, n=beanList.size(); i<n; i++){
           // CourseDto bean = beanList.get(i);
             Object bean = beanList.get(i);
            Map map = convertBean2Map(bean);
            mapList.add(map);
        }
        return mapList;
    }
    public static Map convertBean2Map(Object bean) throws Exception {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0, n = propertyDescriptors.length; i <n ; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                  /*  if ("startTime".equals(propertyName)) {
                        returnMap.put(propertyName, DateUtil.format((Date) result));
                    } else if ("endTime".equals(propertyName)) {
                        returnMap.put(propertyName, DateUtil.format((Date) result));
                    } else if ("status".equals(propertyName)) {
                        returnMap.put(propertyName, ("0".equals(result + "")) ? "上线" : "下线");
                    } else if ("isSeriesCourse".equals(propertyName)) {
                        returnMap.put(propertyName, ("1".equals(result + "")) ? "序列课" : "单节课");
                    }else{*/
                        returnMap.put(propertyName, result);
                    //}
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }


}

