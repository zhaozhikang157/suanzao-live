<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>

<html class="indHtml">
	<head>
		<%@include file="/WEB-INF/views/common/meta.jsp" %>
		<title>酸枣在线</title>
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
		<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
		<link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>" />
		<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
		<script src="http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
	</head>
	<body>
			<div class="contactBox">
				<form class="Pwd-myform" action="">
					<p class="telPhoneNum">
						<img src="/web/res/image/icon_user.png" alt="" />
						<input type="text" name="telNum" maxlength="11" placeholder="请输入手机号码" autocomplete="off" />
					</p>
					<p class="pwNum">
						<img src="/web/res/image/icon_pwd.png" alt="" />
						<input type="password" class="pwdNums" name="pwdNum"  maxlength="16" placeholder="请输入密码" autocomplete="off" />
					</p>
					<%--<p class="loginBtnBox btnstyle"><a class="loginBtn" href="javascript:void(0)"><span>立即登录</span></a></p>--%>
				</form>
			</div>
			<p class="btnstyle loginBtn" onclick="toAuthor();">账号密码登录</p>

	<div id="login_container">

	</div>
	</body>
	<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>

	<script>
		function toAuthor(){
			window.location.href = "/wechat/goAuthor"
		}
		var obj = new WxLogin({
			id:"login_container",
			appid: "wx776cc6d32ad501b8",
			scope: "snsapi_login",
			redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
			state: "state",
			style: "black",
			href: ""
		});
	</script>
</html>


