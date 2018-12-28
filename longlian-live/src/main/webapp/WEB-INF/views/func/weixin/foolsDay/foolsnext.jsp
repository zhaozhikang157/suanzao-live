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
</head>
<body>
<div class="paymentmethod" style="display: none">
  <div class="mask">
    <div class="bsf"><img src="/web/res/image/covers.png"></div>
    <p>请点击右上角</p>

    <p>通过【发送给朋友】</p>

    <p>邀请好友参与</p>
  </div>
</div>
<div class="wraper">
  <div class="seaipublic seaione on"></div>
  <div class="seaipublic seaifour">
    <img src="/web/res/image/justmap.png" class="seaifourimg">
    <h3 class="foolsTitle foolsTitlesecret"><input type="text" maxlength="28" class="foolsText" placeholder="请自拟整蛊标题"></h3>
    <div class="foolBtn"><img src="/web/res/image/foolbtn.png" alt=""/></div>
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
  var uuid ='${uuid}'
  var foolsText="今年起，五一确定恢复七天长假"
  function weixinCallback() {
    wx.ready(function () {
      onMenuShareTimeline();//朋友圈
      onMenuShareAppMessage();//微信
      onMenuShareQQ();
      onMenuShareWeibo();
      if (window.wxReadCallback) {
        window.wxReadCallback();
      }
    });

    wx.error(function (res) {
      if ("config:invalid signature" == res.errMsg) return;
      alert(res.errMsg);
    });

  };
  $(".foolBtn").click(function(){
    shareBtn()
  })
  $(".mask").click(function () {
    $(".paymentmethod").hide();
  })
  var url_index1 = location.href.split('com')[0] + "com";
  var foolstext1 = encodeURIComponent(foolsText);
  share_link="http://suanzao.llkeji.com/weixin/toFoolsDayPage?titleStr="+foolstext1+"&uuid="+uuid;
  imgUrl = url_index1 + "/web/res/image/witepic.png";
  share_desc="今年起，五一确定恢复七天长假";
  share_title="今年起，五一确定恢复七天长假";

  function shareBtn() {
    foolsText =$(".foolsText").val();
    if(foolsText==""||foolsText.length<1){
      pop({"content": "请填写1-28位字符" , "status": "error", time: '2000'});
      return;
    }

    $(".paymentmethod").show();
    var foolstext = encodeURIComponent(foolsText);
    var url_index1 = location.href.split('com')[0] + "com";
    share_link="http://suanzao.llkeji.com/weixin/toFoolsDayPage?titleStr="+foolstext+"&uuid="+uuid;
    imgUrl =url_index1 + "/web/res/image/witepic.png";
    share_desc=foolsText;
    share_title=foolsText;
    wx.ready(function () {
      onMenuShareTimeline();//朋友圈
      onMenuShareAppMessage();//微信
      onMenuShareQQ();
      onMenuShareWeibo();
      if (window.wxReadCallback) {
        window.wxReadCallback();
      }
    });
    wx.error(function (res) {
      if ("config:invalid signature" == res.errMsg) return;
      alert(res.errMsg);
    });
    var result = ZAjaxRes({url: "/weixin/foolsDay", type: "get"});
  }

</script>
</html>
