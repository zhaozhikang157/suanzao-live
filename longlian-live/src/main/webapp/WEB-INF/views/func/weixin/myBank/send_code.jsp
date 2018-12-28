<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <%@include file="/WEB-INF/views/common/meta.jsp" %>
    <title>${tilteName}</title>
    <link rel="icon" href="${requestContext.contextPath}/favicon.ico" type="image/x-icon"/>
    <link rel="shortcut icon" href="${requestContext.contextPath}/favicon.ico" type="image/x-icon"/>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimal-ui"/>
    <meta name="format-detection" content="telephone=no"/>
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="yes"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/reset.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/myYention.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
    <style>
        .cont_dealReset div{
            width: 94%;
            margin: 0 auto;
            color: #333;
        }
        .cont_dealReset p{
            width: 94%;
        }
    </style>
</head>
<body>
<div id="wrap" style="background: white">
    <div class="iscrollBox">
        <div class="contactBox cont_dealReset">
            <form>
                <div>验证码会发送至${mobile}</div>
                <p class="authCode">
                    <input type="number" name="authCode" placeholder="请输入验证码" autocomplete="off" id="code" />
                    <input type="button" id="downNum" class="downNum" value="获取验证码"/>
                    <span class="verification"></span>
                </p>
                <p class="loginBtnBox btnstyle"><a class="submitBtn" onclick="checkCode()"><span>确定</span></a></p>
            </form>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var type = '${type}';
    var courseId = '${courseId}';
    var isProxy = '${isProxy}';
    var i=1,
            wait=60;
    function time(obj) {
        if (wait == 0) {
            obj.removeAttribute("disabled");
            obj.className = "downNum";
            obj.value="获取验证码";
            wait = 60;
        } else {
            obj.setAttribute("disabled", true);
            obj.value="" + wait + "s后重新获取";
            obj.className = "downNum on";
            wait--;
            setTimeout(function() {
                time(obj)
            },1000)
        }
    }
    document.getElementById("downNum").onclick=function(){
        time(this);
        var result = ZAjaxRes({url: "/account/sendCheckCode.user", type: "GET"});
        if(result.code == '000000'){
        }
    }

    function checkCode(){
        var code = $("#code").val();
        if(code==''){
            pop({"content":"请输入验证码!","status":"error",time:'2500'});
            return;
        }
        var result = ZAjaxRes({url: "/account/checkCode.user?code="+code, type: "POST"});
        if(result.code=='000000'){
            window.location.href="/weixin/toSetPwd?type="+type+"&courseId="+courseId+"&isProxy="+isProxy;
        }else{
            sys_pop(result);
        }
    }
</script>
</html>


