<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>招募</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
</head>
<body onload="load()">
<div class="paymentmethod" style="display: none">
    <div class="mask">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>

        <p>通过【发送给朋友】</p>

        <p>邀请好友参与</p>
    </div>
</div>
<div id="wrapper">
    <div id="cont_box" style="padding-bottom:0">
        <!--福利 -->
        <div class="recruittop">

        </div>
        <div class="recruitbotm">
            <a href="/weixin/livereward" class="lqzbjl"></a>
            <a href="/weixin/fans" class="lqfsjl"></a>
            <a href="/weixin/rules" class="lqtgjl"></a>
        </div>
    </div>
</div>
</body>
<script>
    function load() {
        setTimeout(function () {
            var myscroll = new iScroll("wrapper");
        }, 500);
    }
    function goBack() {
        history.back(-1);
    }
    //    分享
    var flug = 0;
    $(".wxhy,.friends").click(function () {
        $(".paymentmethod").show();
        var para = {
            systemType: "CREATR_LIVE_ROOM",
        };
        if (flug < 2) {
            share_h5(para);
        }
        flug++;
    });
    $(".mask").click(function () {
        $(".paymentmethod").hide();
    })

</script>
</html>
