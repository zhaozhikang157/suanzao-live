<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>设置</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<div class="changepop">
    <p>更换的手机号不能和绑定的手机号一致</p>
</div>
<div id="wrapper">
    <div id="cont_box">
        <div class="changebox">
            <p class="changetype">
                <input placeholder="请输入新的手机号" validate="mobile" class="text mobile" name="" id="mobile" maxlength="11"
                       type="text">
            </p>

            <div class="verification">
                <p class="changeya">
                    <input placeholder="请输入验证码" validate="verification" class="text code" id="code" name=""
                           maxlength="6" type="text">
                </p>

                <div class="submitYzm">获取验证码</div>
            </div>
            <!-- 表单验证错误提示 -->
            <p class="errorMessages"></p>
            <button class="changebtn bgcol_c80" disabled="disabled">
                确定
            </button>
        </div>
    </div>
</div>
</body>
<script>
    var oldMobile = '${oldMobile}';
    $('.text').on("keyup", function () {
        var obj = $('.text');
        var obj2 = $('.changebtn');
        valT(obj, obj2);
    });
    //点击发送验证码
    $(".submitYzm").click(function () {
        //验证手机号
        $('.errorMessages:visible').html('');
        var num = inputTest($('.mobile'));
        if (num) {
            var mobile = $('#mobile').val();
            if (oldMobile == mobile) {
                $(".changepop").show();
                setTimeout(function(){
                    $(".changepop").hide();
                },2000);
                return;
            }
            var result = ZAjaxJsonRes({url: "/user/getApplySms.user?mobile=" + mobile, type: "GET"});
            if (result.code == '000000') {
                //请求发送成功后
                $(".submitYzm").addClass('not_pointers');
                $('.submitYzm').html('发送成功');
                var timer = null;
                timer = setTimeout(function () {
                    //60秒重新发送
                    var oT = 60;
                    timer = setInterval(function () {
                        oT--;
                        $('.submitYzm').html(oT + '秒后重发');
                        if (oT == 0) {
                            clearInterval(timer);
                            $('.submitYzm').html('获取验证码');
                            $(".submitYzm").removeClass('not_pointers');
                        }
                    }, 1000);
                }, 500);
            } else {
                pop({content: "更改成功!", status: "normal", time: '2500'});
            }
        }
    });

    $(".changebtn").click(function () {
        var mobile = $('#mobile').val();
        if (oldMobile == mobile) {
            $(".changepop").show();
            setTimeout(function(){
                $(".changepop").hide();
            },2000)
            return;
        }
        var num = true;
        $('.errorMessages:visible').html('');
        $('.changebox input').each(function (i) {
            if (!inputTest($(this))) {
                $(this).css('color', '#C80000');
                num = false;
                return false;
            } else {
                $(this).css('color', '#107184');
            }
        });
        var checkCode = $("#code").val().trim();
        var data = ZAjaxJsonRes({
            url: "/user/registerMobile.user?mobile=" + mobile + "&checkCode=" + checkCode,
            type: "GET"
        });
        sys_pop(data)
        if (data.code == '000000') {
            window.location.href = "/weixin/personalCenter";
        }
        if (!num) return;
    })
</script>
</html>
