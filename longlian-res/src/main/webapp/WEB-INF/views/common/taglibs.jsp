<%@page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jstl/functions" %>
<c:set var="now" value="<%=new java.util.Date()%>" />
<%
	String staticResource = "";
	if (staticResource.startsWith("/")) {
		staticResource = request.getContextPath() + staticResource;
	}
	pageContext.setAttribute("ctx", request.getContextPath());
	pageContext.setAttribute("staticResource", staticResource);
%>
