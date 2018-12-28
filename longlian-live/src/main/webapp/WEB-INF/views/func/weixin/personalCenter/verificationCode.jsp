<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>输入验证码</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrapper" style="background: white">
  <div id="cont_box">
    <p class="resetPasswordtitle">请输入验证码</p>
    <div class="verCod">
      <input type='tel' name=''class='now'maxlength="1"/>
      <input type='tel' name=''class='now'maxlength="1"/>
      <input type='tel' name=''class='now'maxlength="1"/>
      <input type='tel' name=''class='now'maxlength="1"/>
      <input type='tel' name=''class='now'maxlength="1"/>
      <input type='tel' name=''class='now'maxlength="1"/>
    </div>
    <input type="text" class="allVal" style="display: none"/>
    <p class="sizeTime"></p>
  </div>
</div>
<script>
  var timer = null;
  var strs=true;
  var mobile = '${mobile}';
    //60秒重新发送
    var oT = 60;
    timer = setInterval(function () {
      oT--;
      $('.sizeTime').html(oT+"秒");
      if (oT == 0) {
        clearInterval(timer);
        $('.sizeTime').addClass("on").html('重新获取');
      }
    }, 1000);



  $(".sizeTime").click(function(){
    if(strs){
      strs=false;
      if($(this).html()=="重新获取"){
      $(".now,.allVal").val("");
        var result = ZAjaxJsonRes({url: "/forget/personalCenterSendVerificationCode.user?mobile="+mobile, type: "GET"});
        if (result.code == "000000") {
          //60秒重新发送
          var oT = 60;
          timer = setInterval(function () {
            oT--;
            $('.sizeTime').removeClass("on").html(oT+"秒");
            if (oT == 0) {
              clearInterval(timer);
              $('.sizeTime').addClass("on").html('重新获取');
              strs=true;
            }
          }, 1000);
        }
      }else{
        $(".sizeTime").css("color","#aaaaaa");
        strs=true;
        return;
      }
    }

  })




  $('.now').keyup(function(){
    $(".resetPasswordtitle").css("font-size",".7rem");
    var erro =[];
    var allval;
    $(".verCod input[type='tel']").each(function(){
      var telval = $(this).val();
      erro.push($(this).val());
      var str=erro.join("");
      allval=$(".allVal").val(str);
      if(str==""&&telval==""){
        $(".resetPasswordtitle").css("font-size","1rem");
      }else{
        $(".resetPasswordtitle").css("font-size",".7rem");
      }
    });
    if( $(".allVal").val().length==6){
      var vallCode =  $(".allVal").val();
      var result = ZAjaxJsonRes({url: "/forget/verificationCode?mobile="+mobile + "&code=" + vallCode, type: "GET"});
      if (result.code == "000000") {
        window.location.href="/weixin/newpasswordsettings?mobile="+mobile;
      }else if(result.code == "000008"){
        pop({"content": "验证码错误" , "status": "error", time: '2500'});
      }else if(result.code == "000022"){
        pop({"content": "此手机号非该平台用户" , "status": "error", time: '2500'});
      }
    }
    if($(this).index()<5 && $(this).val()!="") {
      $(this).next('input').focus();
    }
    if($(this).val()==""){
      $(this).prev('input').focus();
    }
  });


</script>
</body>
</html>
