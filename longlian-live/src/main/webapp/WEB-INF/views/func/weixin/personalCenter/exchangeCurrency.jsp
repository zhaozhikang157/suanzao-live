<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>兑换学币</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;background: #f4f6f9" onload="doInit()">
<div id="wrapper"style="overflow: auto; height:100%;max-width: 18.75rem;margin: 0 auto">
  <div id="cont_box">
      <div class="exchangeBox">
        <p>兑换金额<span>(1枣币=1学币)</span></p>
        <p id="money_xb_box">购买该课程，还需兑换<em id="money_xb"></em>枣币为学币</p>
        <p><i>￥</i><input type="number" class="exinputVal" oninput="clearNoNum($(this))"></p>
        <p>当前全部金额为<em class="indexAllmoney"></em>，<em class="allExchange">全部兑换</em></p>
        <p class="exchangeBtn on">兑换</p>
      </div>
  </div>
</div>
</body>
<script>
  var rise = true;
  var attach = getQueryString('attach');
  function doInit(){
    var result = ZAjaxRes({url: "/llaccount/getAccountBalance.user", type: "get"});
    if (result.code == "000000") {
      $(".indexAllmoney").text(result.data.zb);
    }
    if(attach != null && attach != ""){
      $('#money_xb').html(attach);
    }else{
      $('#money_xb_box').hide();
    }
  }
  $(".allExchange").click(function(){
      var allExchange = $(".indexAllmoney").text();
        $(".exinputVal").val(allExchange);
    var assignment =  $(".exinputVal").val();
    if(assignment == null || assignment == ''|| assignment == "0"|| assignment == "0.0"||assignment == "0.00"){
      $(".exchangeBtn").addClass("on")
    }else{
      $(".exchangeBtn").removeClass("on")
    }
  })
  $(".exchangeBtn").click(function(){
    if(rise){
      if( $(".exchangeBtn").hasClass("on")){
        return;
      }else{
        var allExchange = $(".indexAllmoney").text();
        var exputVal = $(".exinputVal").val();
        if(Number(exputVal)>Number(allExchange)){
          pop({"content": "输入金额超过枣币余额" , "status": "error", time: '2000'});
          return;
        }
        BaseFun.Dialog.Config({
          title: '提示',
          text : '您将要兑换'+exputVal+'枣币为学币?',
          cancel:'取消',
          confirm:'确定',
          callback:function(index) {
            if(index == 1){
              var result = ZAjaxRes({url: "/account/transforZb2Xb.user?amount="+exputVal, type: "get"});
              if (result.code == "000000") {
                pop1({"content": "兑换成功" , "status": "normal", time: '2000'});
                rise =false;
                setTimeout(function(){
                  window.location.href=document.referrer;
                },3000);
              }else{
                pop1({"content": "兑换失败" , "status": "error", time: '2000'});
              }
            }
          }
        });
      }
    }
  });


  function clearNoNum(that) {
    var value = that.val();
    if (value == null || value == ''|| value == "0"|| value == "0.0") {
      $(".exchangeBtn").addClass("on");
      return false;
    }
    if (!isNaN(value)&& value.length>0 ) {
      var exyz = /^(([1-9]\d*)|([0-9]\d*\.\d?[1-9]{1}))$/;
      if (exyz.test(value)) {
        $(".exchangeBtn").removeClass("on");
        return false;
      } else {
        var numindex = parseInt(value.indexOf("."), 10);
        if (numindex == 0) {
          $(".exchangeBtn").addClass("on");
          return false;
        }
        var head = value.substring(0, numindex);
        var bottom = value.substring(numindex, numindex + 3);
        var fianlNum = head + bottom;
        that.val(fianlNum);
      }
    }else{
      $(".exchangeBtn").addClass("on");
    }
  }
  function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
  }
</script>
</html>
