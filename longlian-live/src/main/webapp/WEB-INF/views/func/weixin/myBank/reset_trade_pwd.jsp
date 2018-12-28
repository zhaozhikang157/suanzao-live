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
    <link rel="stylesheet" type="text/css" href="/web/res/css/myYention.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrap" style="background: white;overflow-x: hidden" >
    <div class="iscrollBox">
        <div class="contactBox">
            <form class="Pwd-myform" action="">
                <p class="pwNum dealPwdBox">
                    <span>原交易密码</span>
                    <input type="password" id="password" class="dealPwdNum1" name="pwdNum" maxlength="6"
                           placeholder="请输入六位密码" autocomplete="off" />
                </p>

                <p class="pwNum dealPwdBox">
                    <span>新交易密码</span>
                    <input type="password" id="newPassword" class="dealPwdNum2" maxlength="6" name="pwdNum"
                           maxlength="16" placeholder="请输入六位新密码" autocomplete="off"/>
                </p>

                <p class="pwNum dealPwdBox">
                    <span>确认新密码</span>
                    <input type="password" id="sureNewPassword" class="dealPwdNum3" maxlength="6" name="pwdNum"
                           maxlength="16" placeholder="请确认六位新密码" autocomplete="off"/>
                </p>

                <p class="loginBtnBox btnstyle"><a class="submitBtn" href="javascript:void(0)"
                                                   onclick="commit();"><span>确认提交</span></a></p>

                <a class="forgetPwd" onclick="forgetPwd()">忘记交易密码</a>
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
    var reg=/^\d{6}$/;
    function commit() {
        var password = $("#password").val().trim(); //原交易密码
        var newPassword = $("#newPassword").val().trim();   //新交易密码
        var sureNewPassword = $("#sureNewPassword").val().trim();   //确认新的交易密码
        if(!reg.test(password)||!reg.test(newPassword)||!reg.test(sureNewPassword)){
            pop({"content": "只能输入六位数字!", "status": "error", time: '2500'});
            return;
        }
        if (password == "" || newPassword == "" || sureNewPassword == "") {
            pop({"content": "交易密码不能为空格!", "status": "error", time: '2500'});
            return;
        }
        if (sureNewPassword != newPassword) {
            pop({"content": "两次交易密码不一致，请重新输入!", "status": "error", time: '2500'});
            return;
        }
        var checkOldPassword = ZAjaxRes({url: "/account/checkTradePwd.user?password=" + password, type: "POST"});
        if (checkOldPassword.code != '000000') {
            pop({"content": "原交易密码输入错误，请重新输入!", "status": "error", time: '2500'});
            return;
        }
        var result = ZAjaxRes({url: "/account/resetTradePwd.user?password=" + newPassword, type: "POST"});
        if (result.code == '000000') {
            sys_pop(result);
            if("payCourse" == type){
                window.setTimeout(window.location.href = "/weixin/courseInfo?id="+courseId, 1000);
            }else{
                if(type == "5"){    //跳转到我的收益(新版)
                    setTimeout(function () {window.location.href = "/weixin/new_learncoinAccount";}, 1000);
                }else{
                    setTimeout(function () {window.location.href = "/weixin/myYention";}, 1000);
                }
            }
        }
    }
    function forgetPwd() {
        window.location.href = "/account/sendCode.user?type="+type+"&courseId="+courseId;
    }

</script>
</html>


