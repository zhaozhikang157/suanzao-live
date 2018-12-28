<%@ page import="com.longlian.live.constant.Const" %>
<%@ page import="com.huaxin.util.UUIDGenerator" %>
<%@page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	response.setHeader("X-Frame-Options", "SAMEORIGIN");//禁用iframe
	pageContext.setAttribute("ctx", request.getContextPath());
 	String current_version = UUIDGenerator.generate();
	String sytem_new_version = "V1.4.3";//系统版本
	//用户导航标记 1-已经使用过 0-不存在 空-不用处理的，或者是未登录的
	String longlian_live_user_web_navigation_sign = "";
	if(request.getAttribute(Const.longlian_live_user_web_navigation_sign) != null){
		longlian_live_user_web_navigation_sign = request.getAttribute(Const.longlian_live_user_web_navigation_sign).toString();
	}
%>
