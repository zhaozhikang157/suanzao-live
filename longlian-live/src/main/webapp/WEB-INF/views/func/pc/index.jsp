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
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <title>酸枣在线管理后台</title>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/pcIndex.css?nd=<%=current_version%>"/>

    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
</head>
<body onload="doOnload();">
    <div class="pcBox">
        <div class="pcheader"></div>
        <div class="pclogin" id="pclogin">
            <%--<div class="pccont">
                <h3>微信登录</h3>
                <img src="" alt=""/>
                <p>打开微信扫描二维码登录酸枣在线</p>
            </div>--%>
                <script>
                        //导航吸顶高度
                        var obj = new WxLogin({
                            id:"pclogin",
                            appid: "<%=web_appid%>",
                            scope: "snsapi_login",
                            //  redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
                            redirect_uri: "<%=redirect_uri%>"  ,
                            state: "state",
                            style: "black",
                            href: ""

                        });

                </script>
        </div>
        <div class="pcfooter">
            <p>北京龙链科技有限公司</p>
            <p>Copyright © 1998 - 2017 Suanzao.Com.Cn All Rights Reserved  粤ICP备15103069号-1</p>
        </div>
    </div>
</body>



<script>
    function doOnload(){
        /*//导航吸顶高度
        var obj = new WxLogin({s
            id:"pclogin",
            appid: "<%=web_appid%>",
            scope: "snsapi_login",
            //  redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
            redirect_uri: "<%=redirect_uri%>"  ,
            state: "state",
            style: "black",
            href: ""

        });*/
    }

</script>
</html>
