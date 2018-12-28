<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>充值</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;" onload="Load()">
<div class="useryz">
    <div class="sfyzbox">
        <h2>身份验证</h2>
        <ul class="room_inf">
            <li>
                <input placeholder="请输入手机号" validate="mobile" class="text mobile" name="mobile" type="text">
            </li>
            <li>
                <input placeholder="请输入验证码" validate="verification" class="text code" name="code" type="text">
                <div class="submitYzm">获取验证码</div>
            </li>
        </ul>
        <div class="errorMessages zzcan"></div>
        <p>
            <input type="button" class="rushe" value="取消" onclick="closeRegister();">
            <input type="button" class="clios bgcol_c80" value="确定">
        </p>
    </div>
</div>
<div id="wrapper" style="background:#FFF; overflow: auto; height:100%;">
    <div id="cont_box" style="padding-bottom: 0">
        <!--我的直播间收益 -->
        <p class="rechartitle">
            <span>请选择充值金额</span>
        </p>
        <ul class="recharcon" style="min-height: 33rem">

        </ul>

    </div>
</div>
</body>

<script>
    var isMobile = '0'; // 0:没有手机号 1:有手机号
    function Load() {
        var result = ZAjaxRes({url: "/iosPayType/getIosPayInfo.user?type=1"});
        if (result.code == "000000") {
            isMobile = result.ext;
            var html = '';
            $.each(result.data, function (i, item) {
                html += '<li onclick="selectPayType($(this))" ' +
                        '  payTypeId="' + item.id +
                        '" payType="' + item.type +
                        '" reallyAmount="'+item.userReallyAmount+
                        '" amount="'+item.amount+'">' +
                        '<i>' + item.userReallyAmount + '学币</i>' +
                        '<p>' + item.amount + '元</p></li>'
            });
            $(".recharcon").append(html);
        }
    }


    function selectPayType(that){
        var id = that.attr("payTypeId");
        var type = that.attr("payType");
        var reallyAmount = that.attr("reallyAmount");
        var amount = that.attr("amount");
        //验证手机号
        if(isMobile != '1'){
            $(".useryz").show();
            return;
        }
        //微信支付
        var param = {payType: "14", amount: amount, payTypeId: id, deviceNo: ""};
        var result = rechargePay(param);
        onBridgeReady(result);
    }

    /**
     *微信支付
     */
    var orderId = 0;
    function onBridgeReady(result) {
        if (!result) return;
        var data = result.data;
        orderId = result.ext;
        WeixinJSBridge.invoke(
                'getBrandWCPayRequest', {
                    "appId": data.appId,     //公众号名称，由商户传入
                    "timeStamp": data.timeStamp,         //时间戳，自1970年以来的秒数
                    "nonceStr": data.nonceStr, //随机串
                    "package": data.package,
                    "signType": data.signType,         //微信签名方式:
                    "paySign": data.paySign    //微信签名
                },
                function (res) {
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        var result = {
                            code: "000000",
                            message: "支付成功",
                            "successUrl": "/weixin/myYention"
                        };
                        pop1({"content": "充值成功" , "status": "normal", time: '2500'});
                    } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                        var result = {code: "000000", message: "取消支付"};
                        var param = {orderId: orderId};
                        cancelThirdPay(param);
                        pop1({"content": "充值取消" , "status": "error", time: '2500'});
                    } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                        var param = {orderId: orderId};
                        cancelThirdPay(param);
                    }
                    // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。         吗没呢，                                ，
                }
        );
    }
    //验证步骤
    $('.useryz .text').on("keyup change", function () {
        var obj = $('.useryz .text');
        var obj2 = $('.clios')
        valT(obj, obj2);
    });
    //点击发送验证码
    $(".submitYzm").click(function () {
        //验证手机号
        $('.errorMessages:visible').html('');
        var num = inputTest($('.mobile'));
        var mobile = $('.mobile').val();
        var result = ZAjaxJsonRes({url: "/user/getApplySms.user?mobile=" + mobile, type: "GET"});
        if (result.code != "000000") {
            $('.errorMessages:visible').html("请输入手机号");
            return;
        }
        if (num && result.code == "000000") {
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
    $(".clios").click(function () {
        registerMobile();
        $(".room_inf li input").val("");
    });
    /**
     * 注册手机号
     */
    function registerMobile() {
        var mobile = $("input[name='mobile']").val();
        var param = {mobile: mobile, checkCode: $("input[name='code']").val()};
        var result = ZAjaxRes({url: "/user/registerMobile.user", type: "POST", param: param});
        if (result.code == "000000") {
            sys_pop(result);
            $(".useryz").removeClass("on");//请填写手机号！
            $(".buyclassbtns").attr("mobile", mobile);//设计点击按钮可以购买
            $(".useryz").hide();
            isMobile = '1';
        } else {
            $('.errorMessages:visible').html(result.message);
        }
    }
    function closeRegister() {
        $(".useryz").removeClass("on");//请填写手机号！
    }
</script>
</html>
