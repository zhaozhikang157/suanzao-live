<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>个人中心</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
  <script type="text/javascript">
    var fromWeixinMsg = "${fromWeixinMsg}";
  </script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="topBoxcon">
  <div class="topBoxcontap">
    <h2>提示</h2>
    <p>您好，您直播内容涉及到不良内容，已被用户投诉，如有疑问请咨询官方客服，<a href="tel:400-116-9269">400-116-9269</a></p>
    <div class="tapCloseindex">好的</div>
  </div>
</div>
<div class="userSname">
    <div class="newuserBox">
          <div class="texraeBox">
              <textarea class="newUs" type="text"maxlength="12" placeholder="最多12个字符，建议减少特殊字符。"></textarea>
              <div class="textarea-count">
                <span class="textareaInput" style="font-size: .6rem;">0</span>/<span class="textareaTotal" style="font-size: .6rem;">12</span>
              </div>
          </div>
          <ul class="neBjd">
            <li class="nOk">取消</li>
            <li class="yOk">确认</li>
          </ul>
    </div>
</div>
<div id="wrapper"style="overflow: auto; height:100%;max-width: 18.75rem;margin: 0 auto">
  <div id="cont_box">
    <div class="topbox">
      <%--<p class="topboxBackgroud"></p>--%>
      <div class="topallbox">
        <div class="avatar">
          <div style="background:url(/web/res/image/02.png) no-repeat;background-size: contain;background-position: center center;" class="pic" id="img"></div>

        </div>
        <div class="username">
          <div class="newAddnameBox"><p class="teachername"></p><i class="updateNname"></i></div>
          <p class="botomBox"><i class="tapsId"></i></p>
          <p class="botomBox botoUp"><i class="topusernum"><span class="newtopusernum"></span>人关注</i><span class="fenge"></span><i class="sistime">系统消息<i class="appMsg"></i></i></p>
        </div>
      </div>
    </div>
    <ul class="new_division">
      <li id="liveRoomid">

      </li>
      <p class="divisions"></p>
      <li id="liveRoomBack">
        <a href="javascript:;">
          <img src='/web/res/image/Set-the-background_icon.png'>
          <span>直播间后台</span>
        </a>
      </li>
    </ul>
    <div class="division">
      <div class="xzbox">
        <img src="/web/res/image/Focus-on-the-studio_icon.png">
        <a href="/weixin/report">
          <p>我关注的直播间</p>
          <span></span>
        </a>
      </div>
      <div class="xzbox">
        <img src="/web/res/image/Purchase-of-course_icon.png">
        <a href="/weixin/courseBuy" style="border-bottom: none">
          <p>我购买的课程</p>
          <span></span>
        </a>
      </div>
    </div>
    <div class="division">
      <div class="xzbox">
        <img src="/web/res/image/earnings_icon.png">
        <a href="/weixin/new_learncoinAccount">
          <p>我的收益</p>
          <span></span>
        </a>
      </div>
      <div class="xzbox">
        <img src="/web/res/image/My-account_icon.png">
        <a href="/weixin/myYention" id="myaccount" style="border-bottom: none">
          <p>我的账户</p>
          <span></span>
        </a>
      </div>
      <div class="xzbox flooeshow">
        <img src="/web/res/image/Traffic-top-up_icon.png">
        <a href="/weixin/flowRecharge" style="border-bottom: none" >
          <p>流量充值</p>
          <span></span>
        </a>
      </div>
    </div>
    <div class="division" >
      <div class="xzbox">
        <img src="/web/res/image/The-invitation-card_icon.png">
        <a href="javascript:void(0);"id="yqk">
          <p>邀请卡</p>
          <span></span>
        </a>
      </div>
      <div class="xzbox"id="systemMessage">
        <img src="/web/res/image/Set-up-the_icon.png">
        <a onclick="jumpSet()" style="border-bottom: none">
          <p class="firstd"><i>设置</i> <%--<i id="appMsg" class="ju"></i>--%></p>
          <span></span>
        </a>
      </div>
    </div>
  </div>
</div>
<!-- 尾部 -->
<div class="footerbox">
  <footer class="footer">
    <a class="home" href="/weixin">首页</a>
    <div class="creatliv"></div>
    <a class="my on" href="/weixin/personalCenter">我的</a>
  </footer>
</div>
<%--上传头像--%>
<div class="lazy_tip" id="lazy_tip"><span>1%</span><br>	载入中......</div>
<div class="lazy_cover"></div>
<div class="resource_lazy hide"></div>
<div class="pic_edit">
  <h2 style="color:#4eaf7a;">可双指旋转、双指缩放</h2>
  <div id="clipArea"></div>
  <input type="file" id="file" style="opacity: 0;position: fixed;bottom: -100px">
  <div class="updataImg">
    <input type="button" id="clipBtn" value="使用"/>
    <input type="button" id="closeBtn" value="取消"/>
  </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery-2.1.0.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<%--上传头像--%>
<script src="/web/res/uploadimg/sonic.js"></script>
<script src="/web/res/uploadimg/comm.js"></script>
<script src="/web/res/uploadimg/hammer.js"></script>
<script src="/web/res/uploadimg/iscroll-zoom.js"></script>
<script src="/web/res/uploadimg/jquery.photoClip.js?v=1"></script>
<%--<script src="/web/res/js/uploadImage.js?nd=<%=current_version%>"></script>--%>
<%--<script src="/web/res/js/exif.js?nd=<%=current_version%>"></script>--%>
<script>
  var hammer = '';
  var currentIndex = 0;
  var body_width = $('body').width();
  var body_height = $('body').height();
  $("#clipArea").photoClip({
    width: body_width,
    height: body_width,
    file: "#file",
    ok: "#clipBtn",
    loadStart: function () {
      console.log("照片读取中");
      $('.pic_edit').show()
      $('.lazy_tip span').text('');
      $('.lazy_cover,.lazy_tip').show();
    },
    loadComplete: function () {
      console.log("照片读取完成");
      $('.lazy_cover,.lazy_tip').hide();
    },
    clipFinish: function (dataURL) {
      phototUpload(dataURL)
    }
  });

  /*获取文件拓展名*/
  function getFileExt(str) {
    var d = /\.[^\.]+$/.exec(str);
    return d;
  }
  //图片上传结束
  $(function () {
    $('#img').on('click', function () {
      //图片上传按钮
      $('#file').click();
    });
    $('#closeBtn').on('click',function(){
      $('.pic_edit').hide();
    })
  })
</script>
<script>
  var JQuery = $.noConflict();
  var liveRoomId = 0;
  var authStatus = -1;
  var uploadStatus = 0;
  var mobile = 0;
  var isCenter = 1;
  var wechat = 0; //0:不显示  1:显示
  var result = ZAjaxJsonRes({url: "/user/userInfo.user?isCenter=1", type: "GET"});
  if (result.code == "000000") {
    $(".teachername").text(result.data.userName);
//    $('#img').attr("src", result.data.headPhoto);
    $("#img").css({
      background:'url('+result.data.headPhoto +') no-repeat',
      backgroundSize:'cover ',
      backgroundPosition: 'center'
    })
//    $(".topbox").css({"background":'url("'+result.data.blurPhoto+'") no-repeat center center',"background-size": 'cover'});
    wechat = result.data.wechat;
    if(wechat==1){
      $(".flooeshow").css("display","-webkit-box");
      $('#myaccount').css('border-bottom','1px solid #e1e1e1');
    }
    liveRoomId = result.data.liveRoomId;
    authStatus = result.data.authStatus;
    mobile = result.data.mobile;
    var result1 = ZAjaxJsonRes({url: "/userFollow/getCountByRoomId.user?id=" + liveRoomId, type: "GET"});
    $(".newtopusernum").html(result1.data);
    $(".tapsId").html('ID:'+result.data.userId)
    //判断是否有未读消息
    if (result.data.isAppMsg > 0) {
//      $("#appMsg").addClass("ju");
      $(".appMsg,.ju").show();
    }
  }else {
    sys_pop(result);
  }
  if (authStatus == -1) {
    //创建直播间
    $("#liveRoomid").append("<a href=${ctx}/weixin/createLiveRoom><img src='/web/res/image/cerynew.png'><span>创建直播间</span></a>");
  }

  if (authStatus == 1) {
    //成功
    $("#liveRoomid").append("<a href=${ctx}/weixin/liveRoom?id=" + result.data.liveRoomId + "><img src='/web/res/image/My-studio_icon.png'><span>我的直播间</span></a>");
  }
  $(".creatliv").click(function(){

    statisticsBtn({'button':'001','referer':'001001'})
    if (authStatus == -1) {
      //创建直播间
      window.location.href="${ctx}/weixin/createLiveRoom";
    }
    if (authStatus == 0) {
      alert("审核中");
    }
    if (authStatus == 2) {
      alert("审核失败");
    }
    if (authStatus == 1) {
        var result = ZAjaxJsonRes({url: "/liveRoom/isForbiddenRoom.user", type: "GET"});
        if( result.code=="100057"){
            $(".topBoxcon").show();
            return;
        }
      //成功
      window.location.href="${ctx}/weixin/createSingleCourse?id="+liveRoomId;
    }
  })
  if(fromWeixinMsg=="1"){
    $(".footerbox").hide();
  }
  $("#liveRoomid").on('click', function () {
    $(".guide").hide();
    window.location.href = "${ctx}/weixin/liveRoom?id=" +  liveRoomId ;
  });
  $("#liveRoomBack").on('click', function () {
    if (authStatus == -1) {
      alert("请先创建直播间!");
    }else{
      window.location.href = "/weixin/liveBackstage.user?id=" +liveRoomId;
    }
  });

  $("#yqk").on('click', function () {
    statisticsBtn({'button':'002','referer':'002001','roomId':liveRoomId})
    if (authStatus == -1) {
      alert("请先创建直播间!");
    }else{
      window.location.href = "/weixin/inviCard?roomId=" +liveRoomId;
    }
  });
  //选择图片，马上预览
  function phototUpload(src) {
    $.ajax({
      type: "POST",
      dataType: "json",
      url: "/file/avatar.user",
      data:{"baseUrl":src},
      async: false,
      success: function (data) {
        console.log(data)
        if(data.code == '000000'){
          $('.pic_edit').hide();
          $("#img").css({
            background:'url('+data.data.photo +') no-repeat',
            backgroundSize:'cover',
            backgroundPosition: 'center'
          })
          $(".topbox").css({"background":'url("'+data.data.blurPhoto+'") no-repeat center center',"background-size": 'cover'});
          pop1({"content": "提交成功" , "status": "normal", time: '2500'});
        }
      },
      error: function (XMLHttpRequest, textStatus, errorThrown) {

      }
    });
  }

  function jumpSet(){
    window.location.href = "/weixin/setUp?mobile="+mobile;
  };
  $(".guide").click(function(){
    $(this).remove();
  });
  $(".tapCloseindex").click(function(){
    $(".topBoxcon").hide();
  });
  //
  $(".sistime").click(function(){
    window.location.href = "/weixin/messageType.user";
  });
  //编辑姓名
  $(".newAddnameBox").click(function(){
    $(".userSname").show();
    $('.newUs').val($(".teachername").text());
    var lenInput = $('.newUs').val().length;
    $('.textareaInput').html(lenInput);
    if(lenInput>0){
      $(".yOk").addClass("on");
    }else{
      $(".yOk").removeClass("on");
    }
  });
  var titNum = $.trim($('.teachername').text());
  var lenInput = $('.newUs').val().length;
  $('.textareaInput').html(lenInput);
  $(".newUs").on("input",function(){
    lenInput = $(this).val().length;
    titNum = $.trim($('.newUs').val());
    if(lenInput>=0 && lenInput<=12){
      $('.textareaInput').html(lenInput);
      $(".yOk").addClass("on");
    };
    if(titNum==""){
      $(".yOk").removeClass("on");
      return;
    }
    if(lenInput==0){
      $(".yOk").removeClass("on");
    }
  });
  $(".nOk").click(function(){
    $(".userSname").hide();
    $(".newUs").val();
  })
  $(".yOk").click(function(){
    if($(this).hasClass("on")){
      ZAjaxJsonRes({url: "/user/updateUser.user?name=" + titNum, type: "POST"});
      $(".userSname").hide();
      $(".teachername").text(titNum);
      pop1({"content": "修改成功" , "status": "normal", time: '2500'});
      setTimeout(function(){
        window.location.reload()
      },1000)

    }else{
      return;
    }
  });

















  if(handlerNavigationVisitRecord("personalCenter" ,"<%=sytem_new_version%>" , "<%=longlian_live_user_web_navigation_sign%>")){
    $(".guide").show();
  }
</script>

<script src="/web/res/js/ajaxfileupload.js"></script>
</html>
