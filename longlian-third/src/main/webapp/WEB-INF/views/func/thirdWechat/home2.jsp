<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<head>
    <title>微信号关注</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/public.css"/>
</head>

<body>
<div class="default-main-layout indextop">
    <p><span class="userewm">${contactWechat}</span><span class="closeewm" onclick="quit()">退出</span></p>
</div>
<div class="default-main-layout indexleft">
    <ul class="indexnav">
        <li>
            <a class="on" href="javascript:void(0)" onclick="setFrame('${requestContext.contextPath}/wechat/error')">欢迎</a>
        </li>
        <%-- <   li>
           <a href="javascript:void(0)" onclick="setFrame('/wechat/bindinglive')">任务邀请卡设置</a>
         </li>--%>
        <li>
            <a href="javascript:void(0)">帮助</a>
        </li>
    </ul>
</div>
<div  class="default-main-layout indexcon">
    <iframe name="myiframe" id="indexiframe" class="indexiframe" frameborder=0 src="${requestContext.contextPath}/wechat/error"></iframe>
</div>
</body>
<script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
<script>
    function quit()
    {
        window.location.href="${requestContext.contextPath}/wechat/index";
    }
    function setFrame(url){
        $("#indexiframe")[0].src = url;
    }
    $(".indexnav li a").click(function(){
        $(this).addClass("on").parent().siblings().find("a").removeClass("on");
        var url = $(this).attr("item_url");
        if(url){
            setFrame(url);
        }
    })
</script>
</html>
