<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>设置登录密码</title>
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
    <p class="resetPasswordtitle">请设置登录密码</p>
    <div class="inputBox">
      <p><img src="/web/res/image/password@2x.png"><input type="password" class="restInput newrestInput" id="restInput" placeholder="8-20位数字、字母组合"maxlength="20"/><i class="closerese newcloserese"></i><i class="eyes" onclick="hideShowPsw()"></i></p>
    </div>
    <p class="restBtnsize">确定</p>
  </div>
</div>
<script>
  var restVal;
  var mobile = '${mobile}';
  $(".restBtnsize").click(function(){
    if($(".restInput").val()==""){
      return;
    }else if(restVal.length<8){
      pop({"content": "请输入8-20位新密码" , "status": "error", time: '2500'});
      return;
    };
    restVal=$(".restInput").val();
    var myreg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$/;
    if (!myreg.test(restVal)) {
      pop({"content": "请输入正确格式的新密码" , "status": "error", time: '2500'});
      return;
    }
    var result = ZAjaxJsonRes({url: "/forget/personalCenterSetNewPassword.user?mobile="+mobile+"&password="+restVal, type: "GET"});
    if (result.code == "000000") {
      setTimeout(function(){
        window.location.href = "/weixin/setUp?mobile="+mobile+ "&shareOK";
      },2000);
    }else if(result.code == "000002"){
      pop({"content": "修改失败" , "status": "error", time: '2500'});
    }
  })
  $(".restInput").on("input",function(){
    restVal = $(".restInput").val();
    if(restVal.length>0){
      $(".closerese").show();
      $(".restBtnsize").addClass("on");
      $(".resetPasswordtitle").css("font-size",".7rem");
    }else{
      $(".resetPasswordtitle").css("font-size","1rem");
      $(".restBtnsize").removeClass("on");
      $(".closerese").hide();
    }
  })
  $(".closerese").click(function(){
    $(".restBtnsize").removeClass("on");
    $(".restInput").val("");
    $(".resetPasswordtitle").css("font-size","1rem");
    $(this).hide();
  });




  // 这里使用最原始的js语法实现，可对应换成jquery语法进行逻辑控制但不支持IE
  var restInput = document.getElementById("restInput");
  //隐藏text block，显示password block
  function hideShowPsw(){
    $(".newrestInput").focus();
    if (restInput.type == "password") {
      restInput.type = "text";
     $(".eyes").addClass("on");
    }else {
      restInput.type = "password";
      $(".eyes").removeClass("on");
    }
  }

</script>
</body>
</html>
