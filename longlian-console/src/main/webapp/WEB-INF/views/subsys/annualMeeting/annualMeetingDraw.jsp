<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>年会抽奖注册</title>
    <script src="/web/res/annualMeetingDraw/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/annualMeetingDraw/css/initialize.css"/>
    <style>
        html, body {
            width: 100%;
            height: 100%;
            background: url(/web/res/annualMeetingDraw/images/LD_bj.png) repeat-y;
            background-size: 100%;
        }

        .LD_mobile {
            padding: 0 1.8rem;
            overflow: hidden;
            margin-top: 9rem;
        }

        .LD_mobile span {
            color: #FFF;
            float: left;
            width: 3rem;
            height: 1.25rem;
            font-size: .75rem;
            line-height: 1.25rem;
            text-align: center;
        }

        .LD_mobile input {
            width: 9rem;
            height: 1.25rem;
            border: 1px #d6a6a4 solid;
            background: none;
            color: #FFF;
            font-size: .7rem;
            text-indent: .5rem;
        }

        .errorMessages {
            color: #F00;
            line-height: 1rem;
            padding-left: 3rem;
        }

        .LD_tip {
            line-height: .75rem;
            color: #FFF;
            padding: 0 1.7rem 0 2.15rem;
            font-size: .55rem;
            margin-top: .5rem;
        }

        .LD_btn {
            margin-top: 2.5rem;
        }

        .LD_btn .LD_submit {
            display: block;
            width: 7.8rem;
            height: 2.05rem;
            background: url(/web/res/annualMeetingDraw/images/LD_btn1.png) no-repeat;
            background-size: 100%;
            border: none;
            margin: 0 auto;
            text-indent: -999999px;
        }

        .LD_btn .LD_recharge {
            display: block;
            width: 7.8rem;
            height: 2.05rem;
            background: url(/web/res/annualMeetingDraw/images/LD_btn2.png) no-repeat;
            background-size: 100%;
            border: none;
            margin: 0 auto;
            text-indent: -999999px;
            margin-top: 1rem;
        }
    </style>
</head>
<body>
<div class="LD_login">

    <div class="LD_mobile"><span>手机号</span><input class="loginMobile" name="loginMobile" type="text" maxlength="11">

        <p class="errorMessages"></p></div>
    <div class="LD_btn">
        <input class="LD_submit" name="" type="button" value="提交">
    </div>
</div>
</body>
<script src="/web/res/annualMeetingDraw/js/zepto.min.js"></script>
<script src="${requestContext.contextPath}/web/res/js/tools.js?"></script>
<script>
    //验证方法
    function inputTest(obj) {
        switch (obj.attr('name')) {
            case 'loginMobile': //手机号
                var userZ = /^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$/;
                if (!userZ.test(obj.val())) {
                    $('.errorMessages:visible').html('请输入正确的手机号!');
                    return false;
                }
                break;
        }
        return true;
    }
    ;
    $(document).ready(function () {
        var LD_success = localStorage.getItem("LD_success1");
        //提交
        $('.LD_submit').on('click', function () {
            $('.errorMessages:visible').html('');
            if (LD_success == 'true') {
                alert('您已提成功，请勿重复提交！');
                return false;
            } else if (!inputTest($('.loginMobile'))) {
                return false;
            } else {
                var phone = $(".loginMobile").val();
                $.ajax({
                    url: "/annualMeetingDraw/register",
                    dataType: "json",
                    type: "Post",
                    async: false,
                    data: {phone: phone},
                    success: function (result) {
                        if (result.success) {
                            localStorage.setItem("LD_success1", true);
                        }
                        alert(result.msg);
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    }
                });
            }
        });
    });
</script>
</html>
