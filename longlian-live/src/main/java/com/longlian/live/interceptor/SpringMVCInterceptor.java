package com.longlian.live.interceptor;

import com.huaxin.util.IPUtil;
import com.huaxin.util.JsonUtil;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.constant.SystemCofigConst;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.weixin.ParamesAPI.WeixinAppUser;
import com.longlian.dto.CourseRelayDto;
import com.longlian.live.service.CountService;
import com.longlian.live.service.CourseRelayService;
import com.longlian.live.service.WeiXinService;
import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.live.util.weixin.LocalOauth2Url;
import com.longlian.live.util.weixin.WeixinUtil;
import com.longlian.token.AppUserIdentity;
import com.longlian.weixin.ParamesAPI.LocalOauth2Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份URL拦截
 * Created by lh on 2016/5/25.
 */
public class SpringMVCInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(SpringMVCInterceptor.class);

    @Autowired
    WeixinUtil weixinUtil;
    @Autowired
    CountService countService;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    WeiXinService weiXinService;
    @Autowired
    CourseRelayService courseRelayService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //组装线程请求信息 --日志
        SystemLogUtil.setLoginRequestInfo(request);
        //判断客户端来自于 syl
        HttpSession session= request.getSession();
        String userAgentStr = request.getHeader("USER-AGENT");
        String uri = request.getRequestURI();
        if( userAgentStr != null){
            userAgentStr = userAgentStr.toLowerCase();
            boolean exists = LocalOauth2Util.existsUrl(uri);
            UserAgent userAgent = UserAgentUtil.getUserAgentCustomer(userAgentStr);
            //System.out.println("userAgent=" + userAgentStr + "          uri============>" + uri + "--------" + exists);
            if(exists){
                String URL = getURL(request, 0);
                String code = request.getParameter("code");
                String state = request.getParameter("state");
                //用户已经登录
                AppUserIdentity token = SpringMVCIsLoginInterceptor.getUserTokenModel(request);//获取用户信息
                //System.out.println("sssssssstoken============>" + token );
                if(token == null){
                    if(userAgent != null && UserAgentUtil.weixin.equals(userAgent.getCustomerType())) {
                        if(!Utility.isNullorEmpty(code) && !Utility.isNullorEmpty(state)){
                            weixinSso(request, response, code, state);
                        }else{
                            response.addHeader("location", URL);
                            response.setStatus(302);
                            return false ;  //下版本测试（加上 return false;）
                        }
                    }
                }else{
                    countService.activeUserCount(token.getId());
                    //此方法，转到SpringMVCIsLoginInterceptor.getUserTokenModel里面，方便以后取
                    //request.setAttribute(CecurityConst.REQUEST_USER_ATTR, token);
                }

                if(!LocalOauth2Url.teacherSeries.equalsIgnoreCase(uri)
                        && !LocalOauth2Url.teacherSeries.equalsIgnoreCase(uri + "/")) {
                    weiXinService.handlerUserNavivationRecord( request ,  token , uri);
                }
                request.setAttribute(CecurityConst.USER_AGENT_ATTR, userAgent);
            }
        }
        long startTime = System.currentTimeMillis();
        request.setAttribute("_startTime", startTime);
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        long startTime = (Long)request.getAttribute("_startTime");

        request.removeAttribute("_startTime");
        long endTime = System.currentTimeMillis();
        String v = request.getParameter("v");
        UserAgent userAgent = (UserAgent)request.getAttribute(CecurityConst.USER_AGENT_ATTR);
        String type = null;
        if (userAgent != null) {
            type = userAgent.getCustomerType();
        }
        Long user = 0l;
        AppUserIdentity appUser = (AppUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if (appUser != null) {
            user = appUser.getId();
        }

        String uri = request.getRequestURI();
        //不记录
        if ("/error/000099".equals(uri)
                || "/version/check".equals(uri)
                || "/live/yellowNotify".equals(uri)
                || "/chatRoom/receiveMsg".equals(uri)) {
           return ;
        }
        //记录课程id 按照不同的请求地址记录
        long courseId = 0l ;
        String courseIdStr = null ;
        if("/course/getCourseInfo".equals(uri) || "/course/getCourseInfo.user".equals(uri) || "/weixin/courseInfo".equals(uri)){ //课程详情
            courseIdStr = request.getParameter("id");
            if(courseIdStr!=null && courseIdStr.length()>= SystemCofigConst.RELAY_COURSE_ID_LENTH){
                CourseRelayDto courseRelayDto=courseRelayService.queryById(Long.valueOf(courseIdStr));
                if(courseRelayDto == null){
                    courseIdStr = "0";
                }else{
                    courseIdStr=String.valueOf(courseRelayDto.getOriCourseId());
                }
            }
            courseId = Long.parseLong(courseIdStr != null ? courseIdStr : "0");
        }else if("/live/getCourse4App.user".equals(uri) || "/weixin/index.user".equals(uri)) {
            courseIdStr = request.getParameter("courseId");
            if(courseIdStr!=null && courseIdStr.length()>=SystemCofigConst.RELAY_COURSE_ID_LENTH){
                CourseRelayDto courseRelayDto=courseRelayService.queryById(Long.valueOf(courseIdStr));
                courseIdStr=String.valueOf(courseRelayDto.getOriCourseId());
            }
            courseId = Long.parseLong(courseIdStr != null ? courseIdStr : "0");
        }

        Map send = new HashMap();
        send.put("handleTime" , endTime - startTime);
        send.put("visitTime" , endTime);
        send.put("v" , v);
        send.put("userId", user);
        send.put("url" , request.getRequestURI());
        send.put("type" , type);
        send.put("ip" ,IPUtil.getClientAddress(request));
        send.put("courseId",courseId);
        send.put("clientType",request.getParameter("clientType"));
        if (modelAndView != null) {
            send.put("isPage" , "1");
        }
        redisUtil.lpush(RedisKey.ll_url_visit_record_wait2db, JsonUtil.toJson(send));
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                          Object handler, Exception ex) throws Exception {
        RequestInfoContext.clear();
    }

    /**
     * 微信点单SSO登录
     * @param code
     * @param state
     * @throws Exception
     */
    public  void weixinSso(HttpServletRequest request, HttpServletResponse response, String code, String state) throws Exception {
        if (!Utility.isNullorEmpty(code) && !Utility.isNullorEmpty(state)) {
            //获取微信信息
            //WeixinAppUser weixinAppUser = weixinUtil.getUserAppUser(code);//公众号授权获取
            WeixinAppUser weixinAppUser = weixinUtil.getUserAppUserContainIsFollow(code);//公众号授权获取且是否关注
                    if (weixinAppUser != null) {
               // System.out.println("openid-------------->" + weixinAppUser.getOpenid() + "---nickname>" + weixinAppUser.getNickname());
                request.setAttribute(CecurityConst.ll_live_weixin_app_user, weixinAppUser);
            } else {
                String URL = getURL(request , 1);
                response.addHeader("location", URL);
                response.setStatus(302);
            }
        }
    }
    public static String weixin = "/weixin/";//微信根目录
    public static String aprilFoolsDay = weixin + "toFoolsDayPage/";//使用愚人节活动分享页面
    public static String fatherDay = "/festival/doAnswer" ;//父亲节
    public static String fatherPage = "fathersDay/";
    public static String foolsDay = weixin + "foolsDay/";//记录人数
    /**
     * 获取路径
     * @param request
     * @return
     */
    public   String  getURL(HttpServletRequest request , int count){
        String uri = request.getRequestURI();
        String url =  request.getRequestURL().toString();
        String params = request.getQueryString() == null ? "" : request.getQueryString();
        if(!LocalOauth2Url.weixin.equals(uri) &&
                !LocalOauth2Url.courseInfo.equals(uri)  &&    !LocalOauth2Url.courseInfo.equals(uri +"/") && //课程界面
                !LocalOauth2Url.inviCard.equals(uri)  &&    !LocalOauth2Url.inviCard.equals(uri +"/")  && //课程邀请卡
                !LocalOauth2Url.shareInviCard.equals(uri)  &&    !LocalOauth2Url.shareInviCard.equals(uri +"/") &&  //我的邀请卡---分享的邀请卡（直播间）
                !LocalOauth2Url.liveRoom.equals(uri)  &&    !LocalOauth2Url.liveRoom.equals(uri +"/") && //直播间
                !LocalOauth2Url.auth2.equals(uri)  &&    !LocalOauth2Url.auth2.equals(uri +"/")  && //直播间
                !LocalOauth2Url.fitnessLive.equals(uri)  &&    !LocalOauth2Url.fitnessLive.equals(uri +"/") &&
                !LocalOauth2Url.createSingleCourse.equals(uri)  &&    !LocalOauth2Url.createSingleCourse.equals(uri +"/") &&
                 !LocalOauth2Url.personalCenter.equals(uri)  &&    !LocalOauth2Url.personalCenter.equals(uri +"/") &&
                !LocalOauth2Url.inviCode.equals(uri)  &&    !LocalOauth2Url.inviCode.equals(uri +"/") &&
                !LocalOauth2Url.useCode.equals(uri)  &&    !LocalOauth2Url.useCode.equals(uri +"/") &&
                !LocalOauth2Url.learncoinAccount.equals(uri)  &&    !LocalOauth2Url.learncoinAccount.equals(uri +"/")
                &&  !aprilFoolsDay.equals(uri)  &&    !aprilFoolsDay.equals(uri +"/")
                &&  !foolsDay.equals(uri)  &&    !foolsDay.equals(uri +"/")

                ){
            //特殊处理播放页面
            String basePath = request.getScheme()+"://"+request.getServerName();
            url = basePath +  LocalOauth2Url.weixin;
        }
        if(!Utility.isNullorEmpty(params)){
            if(count >=0){
                String paraArray[] = params.split("&");
                String param = "";
                for (int i = 0 ; i<paraArray.length ; i++){
                    String singleParam = paraArray[i];
                    String []singleArray = singleParam.split("=");
                    if(singleArray.length == 2 && !"code".equals(singleArray[0])){
                        if(Utility.isNullorEmpty(param)){
                            param =  singleArray[0] +"=" + singleArray[1];
                        }else{
                            param = param + "&" + singleArray[0] +"=" + singleArray[1];
                        }
                    }
                }
                params = param;
            }
            params = "?" + params ;
        }
        String redirect_uri = url + params;
        System.out.println("url-------->" + redirect_uri);
        String URL = weixinUtil.getThirdReplacePageRedirectUri(redirect_uri );
       // System.out.println("weixin-URL-------->" + URL);
        return URL;
    }

}
