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
		<script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
		<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
		<link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>" />
		<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
	</head>
	<body style="background: white">
			<div class="contactBox">
				<form class="Pwd-myform" action="">
					<p class="telPhoneNum">
						<img src="/web/res/image/icon_user.png" alt="" />
						<input type="tel" name="telNum" maxlength="11" placeholder="请输入手机号码" autocomplete="off" />
					</p>
					<p class="pwNum">
						<img src="/web/res/image/icon_pwd.png" alt="" />
						<input type="password" class="pwdNums" name="pwdNum" maxlength="16" placeholder="请输入密码" autocomplete="off" />
					</p>
					<%--<p class="loginBtnBox btnstyle"><a class="loginBtn" href="javascript:void(0)"><span>立即登录</span></a></p>--%>
				</form>
			</div>
			<p class="btnstyle loginBtn">账号密码登录</p>
	</body>
	<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
	<script>
		var toUrl = '${toUrl}';
		$(function(){
			$(".loginBtn").click(function(){
				var mobile =  $("input[name='telNum']").val();
				var password =  $("input[name='pwdNum']").val();
				var param = {mobile:mobile , password:password , machineCode:"h5",machineType:"h5"};
				loginIn(param);
			});
		});
		function loginIn(param){
			var result = ZAjaxRes({url:"/weixin/login" , type:"POST"  ,param:param});
			if(result.code == "000000"){
				result["message"] = "登录成功" ;
				//if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
				//	window.history.go(-1);
				//}else{
                    result["successUrl"] = toUrl ? toUrl : "/weixin";
				//}
			}else{
				alert(result.message);
			}
			sys_pop(result);
		}
	</script>
</html>


