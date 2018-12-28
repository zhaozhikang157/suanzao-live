<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>重置密码</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrapper" style="background: white">
  <div class="topReset"><img src="/web/res/image/Determine@2x.png" alt=""/>验证码已发送至<span class="resetmoble"></span>，请查收</div>
  <div id="cont_box">
     <p class="resetPasswordtitle on">请输入手机号</p>
     <div class="inputBox">
       <input type="tel" class="restInputold" placeholder="请输入手机号"maxlength="11" value="${mobile}" style="display: none"/>
       <p><img src="/web/res/image/resate.png"><input type="tel" class="restInput" placeholder="请输入手机号"maxlength="11" value="${mobile}"/><i class="closerese"></i></p>
     </div>
     <p class="restBtnsize on">点击获取验证码</p>
  </div>
</div>
<script>
  var restVal,restValold;
  var mobile = '${mobile}';
  var num=true;
  $(function(){
    if(mobile==""){
      $(".resetPasswordtitle").css("font-size","1rem");
      $(".restBtnsize").removeClass("on");
    }
  })
  $(".restBtnsize").click(function(){
    if($(".restInput").val()==""){
      return;
    };
    restValold=$(".restInputold").val();
    restVal=$(".restInput").val();
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(restVal)) {
      pop({"content": "请输入正确的手机号" , "status": "error", time: '2500'});
      return;
    }
    if(num){
      num=false;
      if(restValold!=restVal){
        BaseFun.Dialog.Config({
          title: '提示',
          text : '您已绑定手机号，是否要更换绑定?',
          cancel:'取消',
          confirm:'确认',
          callback:function(index) {
            if(index == 1){
              var result = ZAjaxJsonRes({url: "/forget/personalCenterSendVerificationCode.user?mobile="+restVal, type: "GET"});
              if (result.code == "000000") {
                $(".topReset").slideDown();
                $(".resetmoble").html(restVal.substring(0,3)+"****"+restVal.substring(7,11));
                setTimeout(function(){
                  $(".topReset").slideUp();
                },2000);
                setTimeout(function(){
                  window.location.href="/weixin/verificationCode?mobile="+restVal;
                  num=true;
                },3000);
              }else if(result.code == "000012"){
                pop({"content": "该手机号已被绑定" , "status": "error", time: '2500'});
                num=true;
              }else if(result.code == "000006"){
                pop({"content": "请输入正确的手机号" , "status": "error", time: '2500'});
                num=true;
              }
            }else if(index==0){
              num=true;
            }
          }
        });
      }else{
        var result = ZAjaxJsonRes({url: "/forget/personalCenterSendVerificationCode.user?mobile="+restVal, type: "GET"});
        if (result.code == "000000") {
          $(".topReset").slideDown();
          $(".resetmoble").html(restVal.substring(0,3)+"****"+restVal.substring(7,11));
          setTimeout(function(){
            $(".topReset").slideUp();
          },2000);
          setTimeout(function(){
            window.location.href="/weixin/verificationCode?mobile="+restVal;
            num=true;
          },3000);
        }else if(result.code == "000012"){
          pop({"content": "该手机号已被绑定" , "status": "error", time: '2500'});
          num=true;
        }else if(result.code == "000006"){
          pop({"content": "请输入正确的手机号" , "status": "error", time: '2500'});
          num=true;
        }
      }
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
        $(".closerese").hide();
        $(".restBtnsize").removeClass("on");

      }
  })
  $(".closerese").click(function(){
    $(".restInput").val("");
    $(".restBtnsize").removeClass("on");
    $(".resetPasswordtitle").css("font-size","1rem");
    $(this).hide();
  });

</script>
</body>
</html>
