package com.longlian.console.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.longlian.live.util.SystemLogUtil;
import com.longlian.live.util.log.LogRequestInfo;
import com.longlian.live.util.log.RequestInfoContext;
import com.longlian.token.AppUserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.longlian.console.util.SystemUtil;
import com.longlian.model.MRes;
import com.longlian.model.MUser;
import com.longlian.console.service.ConsoleUserService;
import com.longlian.token.ConsoleUserIdentity;


/**
 * 身份URL拦截
 * Created by syl on 2016/4/13.
 */
public class SpringMVCInterceptor implements HandlerInterceptor {
    @Value("${NOT_CHECK_CONTROLLER_URL:/loginIn}")
    private String url ="/loginIn";//不需要校验认证URL


    @Autowired
    private SystemUtil systemUtilLonglian;
 
    @Autowired
    ConsoleUserService consoleUserService;
    public final static Map urls = new HashMap();
    static {
       

    }
    /**
     * 设置日志拦截 请求对象
     * @param request
     */
    public static void setLoginRequestInfo(HttpServletRequest request){
        LogRequestInfo info = new LogRequestInfo();
        info.setRequest(request.getParameterMap());
        ConsoleUserIdentity userIdentity = (ConsoleUserIdentity)request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        if(userIdentity != null){
            info.setId(userIdentity.getId());
            info.setUserName(userIdentity.getName());
            info.setUserId(userIdentity.getId());
        }
        RequestInfoContext.setRequestInfo(info);
    }
//    @Autowired
//    RedisUtil redisUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestUri =   request.getRequestURI();
        //组装线程请求信息 --日志
        setLoginRequestInfo(request);

//        //判断是否是需要拦截的url
//    	//从redis里 取出身份信息
        ConsoleUserIdentity identify = null;
        identify = systemUtilLonglian.getUserTokenModel(request);
    	///是否已登录
    	if (identify == null &&  !requestUri.equals("/doLoginIn")) {
    		response.sendRedirect("/");
            return false;
        } else {
            String resIds = consoleUserService.getUserRes(identify.getId() , "menu");

            //是管理员
            if (!MUser.isAdmin(identify.getAccount())) {
                resIds = Utility.null2Empty(resIds);

              /*  Set<String> keys = urls.keySet();
                String val = "";
                for (String k : keys) {
                    if (requestUri.contains(k)) {
                        val = urls.get(k).toString();
                        break;
                    }
                }*/
                MRes mres = new MRes();
                mres.setUrl(requestUri);
                List<MRes> list = null;
                        //mResService.findResourceIdByUrl(mres);
                String val = "";
                if(list != null && list.size() > 0){
                	val = list.get(0).getId()+"";
                }
                if (!"".equals(val) && !Utility.findById(resIds, val)) {
                    response.sendRedirect("/func/common/403");
                    return false;
                }
            }
        }
        //将身份信息存到request里面方便后面 controller使用
    	request.setAttribute(CecurityConst.REQUEST_USER_ATTR, identify);
//        //组装线程请求信息 --日志
//        SystemLogUtil.setLoginRequestInfo(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {


    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        //RequestInfoContext.clear();
    }
}
