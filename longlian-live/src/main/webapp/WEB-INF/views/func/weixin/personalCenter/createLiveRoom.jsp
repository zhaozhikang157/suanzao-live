<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#FFF;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>申请入驻</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.min.js"></script>
    <script src="/web/res/js/form.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<!--创建直播间  -->
<div class="found_room">
    <!-- 基本信息 -->
    <ul class="room_inf">
		<li>
        	<span>姓名：</span>
            <input placeholder="请输入真实姓名进行实名认证" validate="fullname" class="text" name="realName" id="realName" maxlength="20" type="text">
        </li>
        <li>
        	<span>手机号：</span>
            <input placeholder="请输入手机号" validate="mobile" class="text mobile" name="" id="mobile" maxlength="11" type="text">
        </li>
        <li>
        	<span>验证码：</span>
            <input placeholder="请输入验证码" validate="verification" class="text code" id="code" name="" maxlength="6" type="text">

            <div class="submitYzm">获取验证码</div>
        </li>
        <li>
        	<span>密码：</span>
            <input placeholder="请设置登录密码" validate="password" class="text" name="" id="password" maxlength="20" type="text">
        </li>
        <li>
        	<span>证件号：</span>
            <input placeholder="请输入公民二代身份证号码" validate="identity" class="text" name="cardNo" id="cardNo" maxlength="20" type="text">
        </li>
    </ul>
    <!-- 直播间资料 -->
    <div class="room_data">
        <!-- 简介 -->
        <div class="room_introduction">
            <h4>直播间介绍：</h4>
            <textarea maxlength="200" class="text" placeholder="写点什么..." name="" id="remark" cols="30" rows="10"></textarea>
        </div>
        <!-- 表单验证错误提示 -->
    	<p class="errorMessages"></p>
    </div>
    

    <div class="sub_btn">
        <input class="found_room_sub bgcol_c80" disabled="disabled" name="" type="button" value="提交审核">
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/emoji.js?nd=<%=current_version%>"></script>
<script>
    var id = '${id}';
    var fileCount = 0;
    var frontNum = 0, rearNum = 0;
    $(function () {
        $('.found_room .text').on("keyup", function () {
            var obj = $('.found_room .text');
            var obj2 = $('.found_room_sub');
            valT(obj, obj2, fileCount);
        });
        //点击发送验证码
        $(".submitYzm").click(function () {
            //验证手机号
            $('.errorMessages:visible').html('');
            var num = inputTest($('.mobile'));
            if (num) {
				var mobile = $('#mobile').val();
        		ZAjaxJsonRes({url: "/user/getApplySms.user?mobile=" + mobile, type: "GET"});
                //请求发送成功后
                $(".submitYzm").addClass('not_pointer');
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
                            $(".submitYzm").removeClass('not_pointer');
                        }
                    }, 1000);
                }, 500);
            }
        });

        if (id != '' && parseInt(id)>0) {
            var result = ZAjaxJsonRes({url: "/liveRoom/getLiveRoomById.user?liveRoomId=" + id, type: "GET"});
            if (result.code == "000000") {
                idCardFront = result.data.appUser.idCardFront;
                idCardRear = result.data.appUser.idCardRear;
                $("#mobile").val(result.data.appUser.mobile);
                $("#remark").text(result.data.liveRoom.remark);
            }
        } else {
            var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
            if (result.code == "000000") {
                $("#mobile").val(result.data.mobile);
            }
        }
        //创建直播间提交审核
        $('.found_room_sub').on('click', function () {
            $('.errorMessages:visible').html('');
            //正则验证
            var num = true;
            $('.room_inf input').each(function (i) {
                if (!inputTest($(this))) {
                    $(this).css('color', '#C80000');
                    num = false;
                    return false;
                } else {
                    $(this).css('color', '#107184');
                }
            });
            if (!num) return;
            var flag = true;
            if (flag) {
                flag = false;
                if(id=="" || parseInt(id)==0){
                    id="";
                }
                var mobile = $('#mobile').val();
                var remark = $('#remark').val();
                var realName = $('#realName').val();
                var cardNo = $('#cardNo').val();
                if(isEmojiCharacter(remark)){
                    alert("不支持emoji表情输入");
                    return;
                }
                var password = $('#password').val();
                var code = $('#code').val();
                var param = {
                    "id": id,
                    "mobile": mobile,
                    "remark": remark,
                    "realName": realName,
                    "cardNo": cardNo,
                    "password": password,
                    "code": code
                };
                var result = ZAjaxRes({url: "/liveRoom/apply.user", type: "POST", param: param});
                if (result.code == "000000") {
                    window.location.href = "/weixin/personalCenter";
                    return;
                } else {
                    alert(result.message);
                }
                flag = true;
            }

        });
    });

    if(navigator.userAgent.toLowerCase().indexOf('iphone')== -1){
        $("#remark").css("font-size",".4rem")
    };
</script>
</html>
