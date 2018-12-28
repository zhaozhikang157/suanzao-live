<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title id="liveTopic"></title>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/fools.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/animate.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/fools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/vconsole.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
</head>
<body>
    <div class="wraper">
      <div class="seaipublic seaione on"></div>
      <div class="seaipublic seaitwo">
            <h3 class="foolsTitle animated bounceInDown"style="animation-delay:.7s;">【邀请函】我们结婚了</h3>
            <p class="animated bounceInLeft" style="animation-delay:1.5s">你是被整蛊的第<span class="identification" id="pagenumb"></span>人</p>
            <p class="animated bounceInRight"style="animation-delay:2s">但我知道</p>
            <p class="animated zoomInUp" style="animation-delay:2s">我只能<span class="identification">骗</span>到爱我的人</p>
            <div class="foolsewm">
              <div class="follsew"></div>
              <div class="ewfoolpic"><img src="/web/res/image/ewmsizepic.jpg"></div>
            </div>
      </div>
    </div>
    <div class="foocation"></div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<style>
  /*#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}*/
</style>
<script>
  var titleStr ='${titleStr}';
  var sumPeople='${sumPeople}';
  $(function(){
    $("#pagenumb").html(sumPeople);
    $(".foolsTitle").html(titleStr);
      // 通过下面这个API隐藏右上角按钮
     WeixinJSBridge.call('hideOptionMenu')
  })
</script>
</html>
