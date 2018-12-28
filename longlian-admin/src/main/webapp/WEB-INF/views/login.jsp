<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="./common/taglibs.jsp" %>
<script src="${ctxStatic}/js/jquery-1.11.1.min.js"></script>
<script src="${ctxStatic }/js/plugins/layer/layer.min.js"></script>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>酸枣直播后台管理系统登陆</title>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jBox.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jbox-customer.css">
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/369login.css"/>
    <style>
        .page-bg{width:100%;height:100%;background-color:#000;background-position:center;background-repeat:no-repeat;position:absolute;z-index:-1;top:0;left:0}
    </style>
</head>
<body onload="reA()">
<div class="wrap">
    <div class="box">
        <div class="cont">
            <div class="tap">
                <p class="logo"></p>
            </div>
            <form method="post" class="myform" id="myform" name="myform" novalidate="novalidate">
                <p class="userp">
                    <input type="text" class="username" placeholder="账号" autocomplete="off"
                           placeholder="请输入用户名" name="account"/>
                    <span class="userpic"><img src="${requestContext.contextPath}/web/res/style/img/userpic.png"></span>
                </p>

                <p class="pwdp">
                    <input type="password" class="userpwd" placeholder="密码" autocomplete="off"
                           name="password" id="password"/>
                    <span class="pwdpic"><img src="${requestContext.contextPath}/web/res/style/img/pwdpic.png"></span>
                </p>

                <p class="yamp">
                    <input type="text" class="yam" placeholder="验证码" autocomplete="off"
                           id="verifCode" name="verifCode"/>
                    <span class="nexttext" onclick="reA()"><img id="codeimg" src=""/></span>
                </p>
                <%--<p class="forget">--%>
                <%--<a href="" >忘记密码</a>--%>
                <%--</p>--%>
                <p>
                    <input onclick="sub()" class="btn" type="button" value="登录"/>
                </p>
            </form>
        </div>
        <div class="bottombox">

        </div>
    </div>
    <div class="page-bg"></div>
</div>
</body>
<script type="text/javascript">
    $(function(){
        $(".page-bg").css('background-image', "url('${ctxStatic}/common/img/login/loginbg.jpg')");
    });
</script>
<script type="text/javascript" src="${requestContext.contextPath}/web/res/js/jboxTools.js"></script>
<script type="text/javascript" src="${requestContext.contextPath}/web/res/jbox/js/jbox.js"></script>
<script src="${requestContext.contextPath}/web/res/js/frame/login.js"></script>
<script type="text/javascript">
    function reA() {
        $("#codeimg").attr("src", "/vcodeimg?r=" + Math.random());
    }

    document.onkeydown = function (event) {
        var e = event || window.event || arguments.callee.caller.arguments[0];

        if (e && e.keyCode == 13) { // enter 键
            sub();
        }
    };
</script>
</html>
