<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <%
        String website = CustomizedPropertyConfigurer.getContextProperty("website");
        String loginBackcall_second = CustomizedPropertyConfigurer.getContextProperty("wechat.web.loginBackcall.second.url");
        String web_appid = CustomizedPropertyConfigurer.getContextProperty("wechat.web.appid");
        String redirect_uri = URLEncoder.encode(website + loginBackcall_second);
    %>

    <script type="text/javascript" src="${requestContext.contextPath}/web/res/jquery/jquery.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        <%--var id = ${id};--%>
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        })
    </script>
    <%--<script src="${requestContext.contextPath}/web/res/js/func/third/app.js"></script>--%>
    <style>
        .binding {
            margin-left: 15px
        }

        .zzcbox {
            display: block;
            position: absolute;
            z-index: 1;
            width: 304px;
            left: 50%;
            margin-left: -174px;
            background: white;
        }

        .footerBtn {
            text-align: center;
        }
    </style>
</head>
<body>
<div style="margin-top:10px; ">
    <div class="zzcbox" style="display: none;">
        <div id="login_container" class="nodebox" style="color: white"></div>
    </div>
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <input type="hidden" ng-model="WechatOfficialRoom.liveId" id="liveId" value="${liveId}" readOnly="true"/>
        <div class="form-group">
            <div class="line">
                <label class="col-sm-3 control-label"></label>
                <input class="binding" type="button" value="点击绑定直播间"/><i class="tapscap"><font style="color: #d43f3a">(绑定直播间后，以下信息将被自动获取)</font></i>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">直播间名称:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="liveName" ng-model="WechatOfficialRoom.liveName"
                       autocomplete="off" maxlength="" value="${liveName}" id="liveName" disabled="disabled">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">直播间ID:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="liveRoomNo" ng-model="WechatOfficialRoom.liveRoomNo"
                       autocomplete="off" maxlength="" value="${liveRoomNo}"
                       disabled="disabled" id="liveRoomNo">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">联系人手机号:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="contactMobile" ng-model="WechatOfficialRoom.contactMobile"
                       autocomplete="off" maxlength=""
                       id="contactMobile">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">联系微信号:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="contactWechat" ng-model="WechatOfficialRoom.contactWechat"
                       autocomplete="off" maxlength=""
                       id="contactWechat">
            </div>
        </div>
    </form>
</div>
<div class="footerBtn">
    <button type="button" class="btn btn-success passBtn" onclick="saveRoom()">
        <i class="glyphicon glyphicon-ok"></i> 保存
    </button>
</div>
<script src="http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script>
    $(".binding").click(function () {
        $(".zzcbox").show();
        var obj = new WxLogin({
            id: "login_container",
            appid: "<%=web_appid%>",
            scope: "snsapi_login",
            // redirect_uri: "http%3A%2F%2Fapi.longlianwang.com%2Fweixin%2FloginBackcall",
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

    function saveRoom() {
        var liveId = $("#liveId").val();
        var liveName = $("#liveName").val();
        var liveRoomNo = $("#liveRoomNo").val();
        var contactMobile = $("#contactMobile").val();
        var contactWechat = $("#contactWechat").val();
        if (liveName == "") {
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
                    alert("您已经绑定过该直播间了,如需重新绑定，请先解绑原有纪录！");
                    $(".footerBtn button").attr("disabled", true);   //让按钮失去点击事件
                    window.close();
                } else {
                    $.ajax({
                        type: "POST",
                        data: {liveId: liveId, liveName: liveName, liveRoomNo: liveRoomNo,contactMobile:contactMobile,contactWechat:contactWechat},
                        url: "/wechat/insertWechatOfficialRoom.user",
                        success: function (result) {
                            window.close();
                            window.opener.location.reload();
                        }
                    })
                }
            }
        });
    }
</script>
</body>
</html>
