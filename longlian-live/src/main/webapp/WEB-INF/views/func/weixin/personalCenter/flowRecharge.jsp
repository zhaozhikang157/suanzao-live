<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>流量充值</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper"style="overflow: auto; height:100%;max-width: 18.75rem;margin: 0 auto;background: white">
  <div id="cont_box">
      <div class="rechgeTopbox">
        <p>剩余流量余额</p>
        <p id="banlanceAmunt"></p>
      </div>
      <ul class="rechgeMoney">
      </ul>
    <div class="validity">
      <div class="valleftBox">
        <p id="price">12</p>
        <span id="invalidTime"></span>
      </div>
     <span class="valdiBtn" onclick="buyNow()">
       立即购买
     </span>
    </div>
    <ul class="rechhul">
      <li><a href="/weixin/rechargeRecord">流量充值记录</a></li>
      <li><a href="/weixin/consumeRecord">流量消耗记录</a></li>
    </ul>
  </div>
</div>
</body>
<script>
//流量充值列
var result = ZAjaxJsonRes({url: "/dataChargeLevel/getDataChargeLevelList.user", type: "GET"});
if (result.code == "000000") {
	console.log(result.data);
  var banlanceAmount =result.ext;
  $("#banlanceAmunt").html(banlanceAmount);
	if (result.data.length > 0){
		$.each(result.data, function (index, item) {
          var on = '';

            var amount = item.amount;
            if (amount >= 1024) {
                amount = amount/1024;
                var amounts=amount.toString();
                amount =  parseInt(amounts)+"T";
            } else {
                amount= amount+"G";
            }
            if(item.chargeFlag==1){
                on = 'on'
                $(".rechgeMoney").append('<li class="'+on+'" invalidTime='+item.invalidTime+' id='+item.id+'><i>'+amount+'</i><i class="recmoney">'+item.prefPrice+'元</i> <img src="/web/res/image/hot@2x.png"></li>');
                $("#price").html(item.prefPrice+'元');
                $('#invalidTime').html('购买时生效，有效期'+item.invalidTime);
            }else if(item.chargeFlag==2){
                $(".rechgeMoney").append('<li invalidTime='+item.invalidTime+' id='+item.id+'><i>'+amount+'</i><i class="recmoney">'+item.prefPrice+'元</i><img src="/web/res/image/yellow@2x.png"></li>');
               
            }else if(item.chargeFlag==3) {
                    $(".rechgeMoney").append('<li invalidTime=' + item.invalidTime + ' id='+item.id+'><i>' + amount + '</i><i class="recmoney">' + item.prefPrice + '元</i>');
            }
    
            
		});
	}
} else {
	sys_pop(result);
}


   $('.rechgeMoney').on('click','.rechgeMoney li,',function(){
      $(this).addClass("on").siblings().removeClass("on");
       var recmoney = $(this).find(".recmoney").text();
      var invalidTime = $(this).attr('invalidTime');
     $("#price").html(recmoney);
     $('#invalidTime').html('购买时生效，有效期'+invalidTime);
   });


   function buyNow(){
    var id = $('.rechgeMoney').find('.on').attr('id');
     var param = {levelId: id};
     var result = ZAjaxRes({url: "/thirdPay/buyFlow.user", type: "POST", param: param});
     if (result.code == "000000" || result.code == "100008"|| result.code == "100025"  || result.code == "100002") {//支付成功或者已经支付
       onBridgeReady(result);
     } else {
       sys_pop(result);
     }
   }

   /**
    *微信支付
    */
   function onBridgeReady(result) {
     if (!result) return;
     var data = result.data;
     var orderId = result.ext;
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
                   "successUrl": "/weixin/flowRecharge"
                 };
                   pop1({"content": "支付成功" , "status": "normal", time: '2500'});
                   setTimeout(function(){window.location.reload(); }, 2000);
               } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                 var result = {code: "000000", message: "取消支付"};
                 var param = {orderId: orderId};
                 //cancelThirdPay(param);
                   pop1({"content": "取消支付" , "status": "error", time: '2500'});
               } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                 var param = {orderId: orderId};
                 //cancelThirdPay(param);
               }
               // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。         吗没呢，                                ，
             }
     );
   }
</script>
</html>
