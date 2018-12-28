<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<%
    String website = CustomizedPropertyConfigurer.getContextProperty("website");
    String loginBackcall = CustomizedPropertyConfigurer.getContextProperty("wechat.web.loginBackcall.url");
    String web_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.web.appid");
    String redirect_uri = URLEncoder.encode(website + loginBackcall);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>PC微信公众号</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/public.css"/>
</head>

<body onload="load()">
<div class="wrapp">
    <div class="zzcbox" style="display: none;">
        <div id="login_container" class="nodebox" style="color: white"></div>
    </div>
    <%--<div class="taptitle"></div>--%>
    <form name="form1" id="form1s" action="${requestContext.contextPath}/wechat/insertWechatOfficialRoom.user"
          method="post">
        <div class="registercont">
            <p class="registertitle">请务必正确填写以下信息，方便我方客服对您提供一对一的支持和帮助</p>

        </div>
        <div class="information">
            <p class="informationTitle">服务号信息填写</p>

            <div class="line">
                <label>服务号名称</label>
                <input type="text" id="nickName" name="nickName" value="" readOnly="true"/>
            </div>
            <div class="line">
                <label>服务号App ID</label>
                <input type="text" id="appid" name="wechatId" value="" readOnly="true"/>
            </div>
            <div class="line">
                <label></label>
                <input class="binding" type="button" value="点击绑定直播间"/><i class="tapscap">(绑定直播间后，以下信息将被自动获取)</i>
            </div>
            <div class="line">
                <label>直播间名称</label>
                <input type="text" id="liveName" name="liveName" value="${liveName}" readOnly="true"/>
            </div>
            <div class="line">
                <label>直播间ID</label>
                <input type="hidden" id="liveId" name="liveId" value="${liveId}" readOnly="true"/>
                <input type="text" id="liveRoomNo" name="liveRoomNo" value="${liveRoomNo}" readOnly="true"/>

            </div>
            <div class="dbbox">
                <label class="wxtitle">微信号</label>
                <div class="wx wxnum">
                    <%--<label>微信号</label>--%>
                    <input type="text" id="weixinhao" name="contactWechat" value="" autocomplete="off"
                           placeholder="请填写微信号"/>
                </div>
                <span class="sign">*</span>
            </div>
            <div class="dbbox">
                <label class="wxtitle">手机号</label>
                <div class="wx wxnum">
                    <%--<label>手机号</label>--%>
                    <input type="text" id="phone" name="contactMobile" value="" autocomplete="off"
                           placeholder="请填写手机号"/>
                </div>
                <span class="sign">*</span>
            </div>
            <p class="informationTitle">服务号信息填写<a class="helptap"
                                                  onclick="window.open('https://mp.weixin.qq.com/s?__biz=MzIyNjQ0ODMxNQ==&mid=100000355&idx=1&sn=6f19a9d381cf0442fc7638c1c4a3c5cf&chksm=687104b75f068da105c5744b4364ce02cadfe4b2cc9b6525aa8f39302114b86e84b555925fe2&mpshare=1&scene=1&srcid=04010p8hX5z5937rCLzb5aNJ#rd','test')"
                                                  style="cursor: pointer;">点击获取模块ID教程-如何添加模块信息</a></p>
            <%--<div class="line">
                <label>板块消息所在行业</label>
                <input type="text" id="" value="" />
            </div>--%>
            <div class="line" style="display: none;" id="reserveReminderRe">
                <label>开课提醒ID</label>
                <input type="text" id="reserveReminderId" name="reserveReminderId" value=""/>
            </div>

            <div class="line" style="display: none;" id="reserveReminderRe3">
                <label>上课提醒ID</label>
                <input type="text" id="reservePreReminderId" name="reservePreReminderId" value=""/>
            </div>
            <div class="line" style="display: none;" id="reserveReminderRe02">
                <div class="leftcont"></div>
                <p class="classReminder">开通直播间通知时间设置</p>

                <p class="details">服务号对接成功后,会给新关注服务号的粉丝推送一条<span class="importants">“开通粉丝专属直播间”</span>的图文消息通知，该条图文消息不会占用
                    每个月的4次群发图文的次数，粉丝点击这条图文消息就会自动关注你的直播间，后续直播间才有权限给粉丝推送新话题
                    开播通知消息。</p>

                <%--   <p class="classReminder">请选择推送通知的时间</p>

                   <div class="choice">
                       <input type="radio" name="only" id="male" value=""/><label for="male">关注后立即推送 </label>

                       <p class="ftitle">(推荐服务号主要用于直播间通知推送的用户选择此项，立即推送能提升关注直播间的转化率)</p>
                   </div>
                   <div class="choice">
                       <input type="radio" name="only" id="choice" value=""/>
                       <label for="choice">关注一分钟后再推送</label>
                       <p class="ftitle">
                           (推荐服务号有其他更重要的流程或人物希望新粉丝完成的用户选择此项；延迟推送后就不会响服务号新粉丝主流程的转化;“开通粉丝专属直播间”消息将会在关注1分钟之后才会推送)</p>
                   </div>--%>
            </div>
            <div class="nextbtn">
                保存
            </div>
            <div style="clear: both;"></div>
        </div>
    </form>
</div>

</body>

<script src="http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script>
    function load() {
        var url = "/wechat/getWechatOfficial.user";
        var json = tools.requestRs(url);
        if (json.success) {
            $("#nickName").val(json.data.nickName);
            $("#appid").val(json.data.appid);
            $("#weixinhao").val(json.data.contactWechat);
            $("#phone").val(json.data.contactMobile);
            $("#reserveReminderId").val(json.data.reserveReminderId);
            $("#reservePreReminderId").val(json.data.reservePreReminderId);
            if (json.data.serviceType == '2') {
                $("#reserveReminderRe").show();
                $("#reserveReminderRe02").show();
                $("#reserveReminderRe3").show();
            }
        }
    }

    $(".nextbtn").click(function () {
        var weixinhao = $("#weixinhao").val();
        var liveId = $("#liveId").val();
        if (!liveId) {
            alert("请绑定直播间！");
            return;
        }
        $.ajax({
            url: "/wechat/isBindedRoom.user?liveId=" + liveId,
            dataType: "json",
            type: "GET",
            async: false,
            success: function (result) {
                if (result.data) {
                    alert("该直播间已经绑定过公众号了，如需重新绑定，请先解绑原有纪录！");
                    return;
                } else {
                    $("#form1s").submit();
                }
            }
        });
    });

    $(".binding").click(function () {
        $(".zzcbox").show();
        var obj = new WxLogin({
            id: "login_container",
            appid: "<%=web_appid%>",
            scope: "snsapi_login",
            //  redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
            redirect_uri: "<%=redirect_uri%>",
            state: "state",
            style: "black",
            href: ""

        });
    })
    $(document).bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".notepic,.binding").length == 0) {
            $(".zzcbox").hide();
        }
    });


</script>
</html>
