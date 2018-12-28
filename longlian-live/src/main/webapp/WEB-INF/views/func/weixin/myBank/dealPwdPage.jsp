<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>交易密码</title>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <meta name=”renderer” content=”webkit”>
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
</head>
<body>
<div id="wrap" style="background: white;overflow-x: hidden">
    <div class="iscrollBox">
        <div class="contactBox">
            <form class="Pwd-myform" action="">
                <p class="pwNum dealPwdBox">
                    <span>交易密码</span>
                    <input type="password" class="dealPwdNum1" maxlength="6" maxlength="6" id="pwd1"
                           placeholder="请输入密码"/>
                </p>

                <p class="pwNum dealPwdBox">
                    <span>确认新密码</span>
                    <input type="password" class="dealPwdNum2" maxlength="6" maxlength="6" id="pwd2"
                           placeholder="请确认新密码"/>
                </p>

                <p class="loginBtnBox btnstyle"><a onclick="setPwd()"><span>确认提交</span></a></p>
            </form>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var type = '${type}';
    var courseId = '${courseId}';
    var reg=/^\d{6}$/;
    var isProxy = '${isProxy}';
    function setPwd() {
        var firstPwd = $("#pwd1").val().trim();
        var secondPwd = $("#pwd2").val().trim();
        if(!reg.test(firstPwd)||!reg.test(secondPwd)){
            pop({"content": "只能输入六位数字!", "status": "error", time: '2500'});
            return;
        }
        if (firstPwd == null || firstPwd == '') {
            pop({"content": "请填写六位交易密码!", "status": "error", time: '2500'});
            return;
        }
        if (secondPwd == null || secondPwd == '') {
            pop({"content": "请填写六位交易密码!", "status": "error", time: '2500'});
            return;
        }
        if (firstPwd != secondPwd) {
            pop({"content": "两次密码输入不一致!", "status": "error", time: '2500'});
            return;
        }
        var result = ZAjaxJsonRes({url: "/account/setPwd.user?pwd=" + secondPwd});
        sys_pop(result)
        if (result.code == '000000') {
            if("payCourse" == type){
                window.setTimeout(window.location.href = "/weixin/courseInfo?id="+courseId, 2000);
            }else{
                if(type == '5'){
                    window.setTimeout(window.location.href = "/weixin/new_learncoinAccount", 2000);
                }else{
                    if(isProxy != "" ){
                        window.location.href="/weixin/carryCash.user?isProxy="+isProxy+"&type=cash";
                    }else{
                        window.setTimeout(window.location.href = "/weixin/myYention", 2000);
                    }
                }
            }
        }

    }
</script>
</html>
