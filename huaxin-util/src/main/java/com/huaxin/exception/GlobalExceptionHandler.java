package com.huaxin.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.huaxin.util.ActResult;
import com.huaxin.util.EmailUtil;
import com.huaxin.util.IPUtil;
import com.huaxin.util.Utility;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;



public class GlobalExceptionHandler implements HandlerExceptionResolver {

	private Logger logg = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	public String[] receive;

	public static String[] mobile = new String[]{};

	public void setMobile(String[] mobile) {
		GlobalExceptionHandler.mobile = mobile;
	}

	public void setReceive(String[] receive) {
		this.receive = receive;
	}

	public static String getExceptionAllinformation(Exception ex) {
		String sOut = ex.getMessage();
		StackTraceElement[] trace = ex.getStackTrace();
		for (StackTraceElement s : trace) {
			sOut += "\t " + s + "\r\n";
		}
		return sOut;
	}

   public static void sendEmail(Exception e , String tip) {
	   sendEmail( tip , GlobalExceptionHandler.getExceptionAllinformation(e));
   }


	public static void sendEmail(String tip , String content) {
		String receive = CustomizedPropertyConfigurer.getContextProperty("exception.receive");
		if (!Utility.isNullorEmpty(receive)) {
			String[] res = receive.split(",");
			EmailUtil.getInstance().send(IPUtil.getLocalIp4()+"-" + tip , content, res);
		}
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		//String ticket = CookieUtil.getValue(request, Constant.Ticket);

		String info = getExceptionAllinformation(ex);

		logg.error("tocken=\r\n" + info);
			logg.info("tocken");
		ex.printStackTrace();
		if(ex.getStackTrace()[0].getClassName().startsWith("org.apache.catalina")||ex.getStackTrace()[0].getClassName().startsWith("org.apache.tomcat")){
			return null;
		}
		if (receive != null && receive.length > 0) {
			StringBuilder sb = new StringBuilder("");
			for (String key : request.getParameterMap().keySet()) {
				sb.append(key + "=" + request.getParameter(key) + "</br>");
			}
			Enumeration<String> headers= request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String headerKey=headers.nextElement();
				sb.append(headerKey + "=" + request.getHeader(headerKey) + "</br>");
			}
			EmailUtil.getInstance().send(IPUtil.getLocalIp4()+"-系统错误"+request.getServletPath(),
					"url=" +request.getServletPath()+"<br/>参数:<br/>tocken=<br/>"+ sb.toString() + "<br/><hr/>" + info, receive);
			}
			ResponseBody body = ((HandlerMethod)handler).getMethodAnnotation(ResponseBody.class);
			if (body == null) {
					ModelAndView view = new ModelAndView("common/500");
					return view;
			} else {
					Map<String , Object> map = new HashMap();
					map.put("success",false);
					map.put("msg",ex.getMessage());
					FastJsonJsonView json = new FastJsonJsonView();
					json.setAttributesMap(map);
					ModelAndView view = new ModelAndView(json);
					return view;
			}

	}
}
