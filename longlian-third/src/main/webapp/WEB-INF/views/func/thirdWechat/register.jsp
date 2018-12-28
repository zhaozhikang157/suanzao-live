<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>登记信息</title>
		<link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
		<link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/public.css"/>
	</head>
	<body>
		<div class="wrapp">
			<div class="taptitle"></div>
			<div class="registercont">
				<p class="registertitle">请务必正确填写以下信息，方便我方客服对您提供一对一的支持和帮助</p>
				<div class="dbbox">
					<div class="wx wxnum">
						<label>微信号</label>
						<input type="text" id="" value="" autocomplete="off" placeholder="请填写微信号"/>
					</div>
					<span class="sign">*</span>
				</div>
				<div class="dbbox">
					<div class="wx wxnum">
						<label>手机号</label>
						<input type="text" id="" value=""autocomplete="off" placeholder="请填写手机号"/>
					</div>
					<span class="sign">*</span>
				</div>
				<p class="nextbtn">
					下一步
				</p>	
			</div>

			
		</div>
	</body>
</html>
