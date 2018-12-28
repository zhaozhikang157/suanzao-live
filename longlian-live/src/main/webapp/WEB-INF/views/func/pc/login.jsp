<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<%
    String website  = CustomizedPropertyConfigurer.getContextProperty("website");
    String loginBackcall  = CustomizedPropertyConfigurer.getContextProperty("wechat.web.loginBackcall.url");
    String web_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.web.appid");
    String redirect_uri = URLEncoder.encode(website + loginBackcall);
%>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>酸枣在线登录</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>
<script>
    var userStatus ='${userStatus}';
    var redirect_uri = "";
    if(userStatus !="" && userStatus=="0"){
        alert("您还不是老师，请下载“酸枣APP”或关注“酸枣在线”公众号完成直播间入驻再尝试登陆！");
        window.location.href='${ctx}/pc/login';
    }else{
        redirect_uri = "<%=redirect_uri%>";
    }
</script>
<body id="box" style="position:relative;">


<div  id="login_container"> 


</div>
</body>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script>
$(function(){
	//导航吸顶高度
    var obj = new WxLogin({
        id:"login_container",
        appid: "<%=web_appid%>",
        scope: "snsapi_login",
        //  redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
        redirect_uri: redirect_uri ,
        state: "state",
        style: "black",
        href: ""
    });
});

</script>
</html>
