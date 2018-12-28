package com.longlian.live.util;

import java.net.InetAddress;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by syl on 2017/2/9.
 */
@Component("systemUtil")
public class SystemUtil {

    /**
     * jwt 生成Token
     * @param userid
     * @return
     */
    public static  String createToken(long userid , String machineType) {
        //学生的话，用默认的前缀
        String pre = CecurityConst.LL_LIVE_OAUTH_TOKEN_PREFIX;
        //用老师的前缀
        if ("teacherLogin".equals(machineType)) {
            //龙链直播老师登录端
            pre = CecurityConst.LL_LIVE_TEACHER_OAUTH_TOKEN_PREFIX;
        } else if ("teacherLogin_web".equals(machineType)) {
            //龙链直播老师web登录端
            pre = CecurityConst.LL_LIVE_TEACHER_WEB_OAUTH_TOKEN_PREFIX;
        }
        return  pre + Jwts.builder().setSubject(userid +"").claim(
                "roles", userid).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256,
                "secretkey").compact();
    }

    /**
     * 获取分享地址
     * @param invitationAppId
     * @param systemType
     * @return
     */
    public static  String getShareAddress( String invitationAppId , String systemType ,String isSeries , Long seriesid,
                                           String roomIdOrCourseId , String shareInviCardUrl , String channel){
        String website = CustomizedPropertyConfigurer.getContextProperty("website");
        //APP移动端
        if("APP".equals(systemType)){
            return website+"?return_url="+ website + "/weixin/?invitationAppId=" + invitationAppId;
        }else if("COURSE".equals(systemType)){//视频播放
            return website+"?return_url="+ website + "/weixin/courseInfo/?invitationAppId=" + invitationAppId + "&id=" + roomIdOrCourseId +"&fromType=2" +
                    "&isSeries="+isSeries + "&seriesid="+seriesid + "&channel="+channel;
        }else if("LIVE_ROOM_INVI_CARD".equals(systemType)) {    //直播间邀请卡
            return website+"?return_url="+ website + "/weixin/liveRoom?invitationAppId="+invitationAppId+"&fromType=2" + "&id="+roomIdOrCourseId;
//            return website + "/weixin/shareInviCard?invitationAppId="+invitationAppId+"&roomIdOrCourseId="+roomIdOrCourseId+"&shareInviCardUrl="+shareInviCardUrl;
        }else if("COURSE_INVI_CARD".equals(systemType)){ //课件邀请卡
            return website+"?return_url="+ website + "/weixin/courseInfo?id=" + roomIdOrCourseId + "&invitationAppId=" + invitationAppId +
                    "&fromType=1"+"&seriesid="+seriesid+"&isSeries="+isSeries+"&channel="+channel;
//            return website + "/weixin/shareInviCard?invitationAppId="+invitationAppId+"&roomIdOrCourseId="+roomIdOrCourseId+"&shareInviCardUrl="+shareInviCardUrl+"&fromType=1";
        }else if("CREATR_LIVE_ROOM".equals(systemType)){    //邀请创建直播间
            return website+"?return_url="+ website + "/weixin/liveRoom?sourseId=" + 2 + "&invitationAppId="+invitationAppId +"&fromType=2";
        }else if("LIVE_ROOM".equals(systemType)){    //邀请创建直播间
            return website+"?return_url="+ website + "/weixin/liveRoom?invitationAppId="+invitationAppId +"&fromType=2&id="+roomIdOrCourseId;
        }else {
            return website + "/weixin/?invitationAppId=" + invitationAppId;
        }
    }
    public static String processQuotationMarks(String content){
        String regex = "\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        String reCT=content;
        while(matcher.find()){
            String itemMatch = "“" + matcher.group(1) + "”";
            reCT=reCT.replace("\""+matcher.group(1)+"\"", itemMatch);
        }
        return reCT;
    }
    
    public static boolean isTestEnv(HttpServletRequest request) {
    	try {
			String serverName = request.getServerName();
			String address= InetAddress.getByName(serverName).getHostAddress();
			
			if("api.longlianwang.com".equals(serverName) && "112.126.90.93".equals(address)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
     	return false;
    }
}
