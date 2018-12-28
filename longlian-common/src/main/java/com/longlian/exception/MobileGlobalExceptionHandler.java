package com.longlian.exception;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huaxin.util.MessageClient;
import com.huaxin.util.UserAgentUtil;
import com.huaxin.util.dto.UserAgent;
import com.huaxin.util.spring.SpringContextUtil;
import com.longlian.live.interceptor.SpringMVCIsLoginInterceptor;
import com.longlian.token.AppUserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.huaxin.exception.GlobalExceptionHandler;
import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.longlian.type.ReturnMessageType;

public class MobileGlobalExceptionHandler extends GlobalExceptionHandler {

	private Logger logg = LoggerFactory.getLogger(MobileGlobalExceptionHandler.class);



	private static Map<String , String > ips = new HashMap<>();
	static {
		ips.put("10.28.117.218","59.110.41.211");
		ips.put("10.51.123.174","112.126.95.74");
		ips.put("10.25.132.178","60.205.171.214");
		ips.put("10.51.121.207","112.126.90.93");
		ips.put("10.25.135.117","60.205.169.16");
		ips.put("123.57.224.112","123.57.224.112");
		ips.put("60.205.169.16","60.205.169.16");
	}

	public static void sendMsg(String tip , String errorKey) {
		if (mobile.length > 0 ) {
			try{
				MessageClient messageClient = (MessageClient)SpringContextUtil.getBeanByName("messageClient");
				String content = tip + "错误码："+errorKey+"【酸枣在线】";
				for (String mob : mobile) {
					messageClient.sendMessage(mob, content);
				}
			} catch (Exception ex1) {

			}
		}
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		//String ticket = CookieUtil.getValue(request, Constant.Ticket);

		String info = getExceptionAllinformation(ex);

		String errorKey = UUID.randomUUID().toString();

		logg.error("info=\r\n{}" , info);
		logg.error("系统错误:" , ex);
		ex.printStackTrace();
		if(ex.getStackTrace() != null && ex.getStackTrace().length > 0 && ex.getStackTrace()[0].getClassName().startsWith("org.apache.catalina")||ex.getStackTrace()[0].getClassName().startsWith("org.apache.tomcat")){
			return null;
		}

		StringBuilder sb = new StringBuilder("");
		for (String key : request.getParameterMap().keySet()) {
			sb.append(key + "=" + request.getParameter(key) + "</br>");
		}
		AppUserIdentity identity = SpringMVCIsLoginInterceptor.getUserTokenModel(request);
		if (identity != null) {
			sb.append("登录人=" + identity.getName() + "</br>");
			sb.append("登录ID=" + identity.getId() + "</br>");
			sb.append("登录手机号=" + identity.getMobile() + "</br>");
		}

		UserAgent ua = UserAgentUtil.getUserAgentCustomer(request);
		if (ua != null) {
			sb.append("登录客户端=" + ua.getCustomerType() + "</br>");
		}
		Enumeration<String> headers= request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerKey=headers.nextElement();
			sb.append(headerKey + "=" + request.getHeader(headerKey) + "</br>");
		}
		sb.append("errorKey=" + errorKey+ "</br>");
		String loggStr = sb.toString().replace("</br>" , "\r\n");
		logg.error("错误相关信息:{}" , loggStr);

		if (receive != null && receive.length > 0) {
			EmailUtil.getInstance().send(IPUtil.getLocalIp4()+"-系统错误"+request.getServletPath(),
					"url=" +request.getServletPath()+"<br/>参数:<br/>tocken=<br/>"+ sb.toString() + "<br/><hr/>" + info, receive);
		}
		System.out.println(sb.toString());
		if (mobile.length > 0 ) {
			try{
				MessageClient messageClient = (MessageClient)SpringContextUtil.getBeanByName("messageClient");
				String content = "错误码："+errorKey+",	服务器内网IP："+ request.getLocalAddr() + "外网IP" + ips.get(request.getLocalAddr())+"【酸枣在线】";
				for (String mob : mobile) {
					messageClient.sendMessage(mob, content);
				}
			} catch (Exception ex1) {

			}
		}

		ResponseBody body = ((HandlerMethod)handler).getMethodAnnotation(ResponseBody.class);
		if (body == null) {
			ModelAndView view = new ModelAndView("common/500");
			return view;
		} else {
			Map<String, Object> map = new HashMap();
			map.put("code", ReturnMessageType.ERROR_500.getCode());
			map.put("message", ReturnMessageType.ERROR_500.getMessage());
			map.put("data", ex.getMessage());
			FastJsonJsonView json = new FastJsonJsonView();
			json.setAttributesMap(map);
			ModelAndView view = new ModelAndView(json);
			return view;
		}

	}

}
