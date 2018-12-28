<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title id="liveTopic">正在加载...</title>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <style>
    .distribution{
      margin-bottom: 0;
    }
  </style>
</head>
<body>
<div class="topBoxcon">
  <div class="topBoxcontap">
    <h2>提示</h2>
    <p>您好，您直播内容涉及到不良内容，已被用户投诉，如有疑问请咨询官方客服，<a href="tel:400-116-9269">400-116-9269</a></p>
    <div class="tapCloseindex">好的</div>
  </div>
</div>
<!-- 课程详情 -->
<%--<div class="guide" >--%>
  <%--<div class="guiTeacbox" style="top:14.3rem">--%>

  <%--</div>--%>
<%--</div>--%>
<div class="paymentbox rechartbox" style="display: none;">
  <div class="rechartpop" style="display: none">
    <img class="Jujubepic" src="/web/res/image/Jujube.png" alt=""/>
    <p id="confirm_pay">您确认要支付1学币购买该课程?</p>
    <div class="choice">
      <span class="cancel">取消</span>
      <span class="purchase">确定</span>
    </div>
  </div>
  <div class="purchasesuccess" style="display: none;">
    <img class="purchasesuccesspic" src="/web/res/image/purchasesuccess.png" alt=""/>
    <i class="purchasesuccessBtn">购买成功</i>
  </div>
</div>
<!--正在直播 -->
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
<div class="livebroadcast" id="wrapper"style=" overflow: hidden; -webkit-overflow-scrolling: touch;">
  <div id="cont_box" class="new_index" style="height: 100%;padding-bottom: 0;overflow-y: scroll">
    <input type="hidden" class="numVal">
    <div class="sicree" style="padding-bottom: 2.4rem;">
    <div class="livevideo yupic" id="COVERSS_ADDRESS" style="background-size:cover; background-position:center center;">
      <div class="bjyBox">
        <div class="bjyBoxs">
          <div class="bhbox">
            <p class="invite">分享赚钱</p>
          </div>
          <span class="bjy"></span>
        </div>
      </div>
      <div class="ygbox">
        <p></p>
        <p></p>
      </div>
    </div>

    <ul class="livetitle" style="display:none;">
      <li><a class="on">课程详情</a></li>
      <div class="lin"></div>
      <li><a>课程评价</a></li>
    </ul>
    <div class="inforallbox bd" id="tabBox1-bd">
      <div class="coursenew">
        <div class="con">
          <div class="togleshow">
            <div style="margin-bottom: 10px">
              <div class="videotitlebox">
                <div class="videotitlebox_bd">
                    <div class="videomidbox">
                      <p class="videotitle">加载中.....</p>
                      <span class="usernub">免费</span>
                    </div>
                    <span class="opentimes newopentimes">
                      <p>
                        <em class="kaike">加载中.....</em>
                      </p>
                    </span>
                </div>
              </div>
              <ul class="subnavigation">
                <li><a>想学 <span id="visitCount">0</span></a></li>
                <div class="partition"></div>
                <li><a>学习 <span  id="studyCount">0</span></a></li>
              </ul>
            </div>
            <div class="teacherxq">
              <div class="teacherzl">
                <div class="teacherlog newteacherlog"  id= "teacherlog"></div>
                <div class="thname new_thname">
                  <p class="newtopteacherbox">加载中.....</p>
                  <p class="more"></p>
                </div>
              </div>
            </div>
            <ul class="teacherlistdetails" style="margin-bottom:10px">
              <li>
                <i id="studyAllCount"></i>
                <span>学生数</span>
              </li>
              <div class="partition"></div>
              <li>
                <i id="teachCourseCount"></i>
                <span>单节课</span>
              </li>
              <div class="partition"></div>
              <li>
                <i id="sequence"></i>
                <span>系列课</span>
              </li>
            </ul>
            <p class="pucmode distribution">
              <span>收费类型</span>
            </p>
            <div class="logbox distribution" style="margin-bottom: 10px">
              <div class="distribution_items">
                <span class="distribution_m">学生分销比例：<i>0%</i></span>
                <div class="partition"></div>
                <span class="distribution_m">学生分销金额(元)：<i>0</i></span>
              </div>
            </div>
            <ul class="newNavs">
              <li class="introductionNew on">课程简介</li>
              <div class="introductionLine"></div>
              <li class="introductionClass">课程列表</li>
            </ul>
            <div class="testHeight"></div>

          <p class="pucmode"id="pucmodeUp"style="margin-top: 0">
              <span>课程简介</span>
            </p>
            <div class="logbox">
              <div class="livetext"></div>
              <div id="courseImgList">

              </div>
            </div>
          </div>
          <p class="pucmode"id="classHref" style="margin-top:10px">
            <span>课程列表</span>
          </p>
          <ul class="sequenceClass publicUlstyle" id="sequenceClass">

          </ul>

        </div>
      </div>
    </div>
    <div class="support">
      酸枣在线提供技术支持<a href="tel:400-116-9269">联系客服</a>
    </div>
    </div>
  </div>
</div>

<!-- 分享 -->
<div class="sharBox" >
<div class="sharbox"style="display: none">
  <div class="mask"style="display: none;height:auto;">
    <div class="bsf"><img src="/web/res/image/covers.png"></div>
    <p>请点击右上角</p>

    <p>通过【发送给朋友】</p>

    <p>邀请好友参与</p>
  </div>
  <div class="sharingBox">
    <div class="sharing">
      <h1 class="contitle">课程创建成功分享到</h1>
      <div class="sharing_box">
        <p class="wx" onclick="statisticsBtn({'button':'005','referer':'005006','courseId':${courseId}})">微信</p>
        <p class="pyq"onclick="statisticsBtn({'button':'006','referer':'006006','courseId':${courseId}})">朋友圈</p>
        <p class="yqk"onclick="statisticsBtn({'button':'007','referer':'007004','courseId':${courseId}})">邀请卡</p>
      </div>
      <div class="sharing_btn">取消</div>
    </div>
  </div>
</div>
</div>
<div class="newtapBox">
  <div class="newtap">
    <p class="relayBtn" data-relay="true" style="display: block;">
      <img src="/web/res/image/icon_relay@1.png" />
      <span>设置转播</span>
    </p>
    <p class="buyclassbtns" style="border-right: 1px solid #CCCCCC;border-top: 1px solid #CCCCCC;background: rgba(255, 255, 255, 1);color: rgba(51, 51, 51, 1);">编辑系列课</p>
    <p class="newgreabtn">创建单节课</p>
  </div>
</div>

<%--设置转播--%>
<div class="relay_box">
  <div class="relay_list">
    <div class="freeofcharge">
            <span class="tit">转播类型
            <em>保存后不能更改转播价格和分成比例</em></span>
      <ul class="frecharBox">
        <li id="relayOK">允许所有用户转播<p class="newdistribution"></p></li>
        <li style="display: none">转播价格 (元)<input id="chargeAmt" type="number" maxlength="5" onKeyUp="amount(this)" placeholder="不输入即为免费转播" validate="money" value="0" class="newMuch"></li>
        <li style="display: none" class="fourdistribution">转播人所获分成比例 (%)<input type="tel" value="0" placeholder="请输入0-100之间的整数" value="0" id="separateInto" class="distribution"></li>
      </ul>
    </div>
    <div class="relay_btn">
      <p class="relay_c">取消</p>
      <p class="relay_ok">确定</p>
    </div>
  </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<script>
  var seriesid = ${seriesid};
  var oBok = true;
  var roomId ="";
  var offset = 0, pageSize = 10;
  var test = "<%=longlian_live_user_web_navigation_sign%>";
  var isClick = false;
  var isRelay; //是否设置转播
  var relayCharge; // 转播金额
  var relayScale ; //转播比例
  var isOpened; //是否开启过转播
  var chargeAmt;
  var relayOff = false;
  var isMyCourse ="";
  $(function(){
    share_h5({systemType: 'COURSE', liveRoomId: seriesid ,isSeries:1,seriesid: 0 });//分享
    serisoList();
    var newNavs= $(".newNavs").offset().top;//浮动标题距离
    /*直播 精选 下拉加载*/
    $('#cont_box').scroll(function(){
      var courseTopone = $("#classHref").offset().top; //课程标题距顶高度
      var pucmodeUp = $('#pucmodeUp').offset().top;
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('#cont_box').height();
      if(scrollTop + windowHeight >= scrollHeight-1){
        serisoList();
      }
      if (scrollTop>380){
        $(".invite").css("left","10rem");
      }else{
        $(".invite").css("left","0");
      }

      if(scrollTop+5>newNavs){
        $(".newNavs").css({position:"fixed",top:0});
        $('.testHeight').css('height','2rem');
        $(".bjyBox").css("top","2.1rem");
      }else{
        $(".newNavs").css("position","static");
        $('.testHeight').css('height','0');
        $(".bjyBox").css("top",".5rem");
      }

      if(!isClick){
        if($('#sequenceClass>li').length <=6 && $('.numVal').val() == '1'){
          $(".introductionNew").addClass("on").siblings().removeClass("on");
        }else{
          if(courseTopone <= $('.newNavs').height()){
            $(".introductionClass").addClass("on").siblings().removeClass("on");
          }else{
            $(".introductionNew").addClass("on").siblings().removeClass("on");
          }
        }
      }else{
        setTimeout(function(){
          isClick = false;
        },30);
        return;
      }
    });
    if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
      $(".livebroadcast").css("transform","translate(0,0)");
    }


    $(".introductionNew").click(function(){
      isClick = true;
      $(".numVal").val("0");
      $("#cont_box").scrollTop( newNavs);
      $(".introductionNew").addClass("on").siblings().removeClass("on");

      return false;
    })
    $(".introductionClass").click(function(){
      isClick = true;
      $(".numVal").val("1");
      var boxDiv = $("#cont_box");
      var a1 = $('.ygbox').outerHeight(),a2 = $('.togleshow').outerHeight(),a3 = $('.subnavigation').outerHeight(),a4 = $('.teacherxq').outerHeight(),a5 = $('#pucmodeUp').outerHeight(),a6 = $('.logbox').outerHeight(),a7 =$("#courseImgList").outerHeight(),a8=$(".newgreabtnbox").outerHeight(),a9 = $('.testHeight').outerHeight();
      var num = a1+a2+20;
      boxDiv.scrollTop( num-$('.newNavs').height());
      $(".introductionClass").addClass("on").siblings().removeClass("on");
      return false;
    })


    //调取分享蒙层
    setTimeout(function(){
      var location = window.location.href;
      if(location.indexOf("shareOK") > 0){
        $(".sharbox").show();
        $(".contitle").show();
      }
    },30);
    $(function () {
      var result = ZAjaxRes({url: "/series/getSeriesCourseInfo?seriesid=" + seriesid, type: "GET"});
      if (result.code == "000000") {
        var data = result.data;
        var seriesCourse = data.seriesCourse;
        appId = seriesCourse.appId;
        roomId = seriesCourse.roomId;
        var distribution = data.seriesCourse.distribution;
        var stuDisAmount = data.seriesCourse.stuDisAmount;
        var isPay = data.seriesCourse.isPay;
        isRelay = data.seriesCourse.isRelay;
        relayCharge = data.seriesCourse.relayCharge;
        relayScale = data.seriesCourse.relayScale;
        chargeAmt = data.seriesCourse.chargeAmt;
        isOpened = data.seriesCourse.isOpened;
        if(isRelay == '1'){
          relayOff = true;
        }
        if(seriesCourse.id.toString().length >= 15){
          isMyCourse = '<i class="relay_icon"></i>';
          $('.newtapBox,.tladdress,.distribution').remove();
        }else{
          isMyCourse= "";
        }
        $('#COVERSS_ADDRESS').css('background-image', 'url(' + seriesCourse.coverssAddress + ')');
        $(".videotitle").html(isMyCourse+seriesCourse.liveTopic);
        $("#liveTopic").text(seriesCourse.liveTopic);//课程标题
        $("#visitCount").html(seriesCourse.visitCount);
        $("#studyCount").html(seriesCourse.studyCount);
        $("#commentCount").html(data.commentCount);
        $(".newtopteacherbox").html(seriesCourse.userName + "的直播间");
        $("#studyAllCount").html(data.studyAllCount);
        $("#teachCourseCount").html(data.teachCourseCount);
        $("#sequence").html(data.seriesCourseCount);
        if(isPay == 1 && distribution != ''){
          $('.distribution_m').eq(0).children('i').html(distribution+'%');
          $('.distribution_m').eq(1).children('i').html(stuDisAmount);
        }else if(isPay == 0){
          $('.distribution').hide();
        }
        var CHARGE_AMT = seriesCourse.chargeAmt;
        if(CHARGE_AMT > 0){
          $(".usernub").html('<i class="weight">'+ CHARGE_AMT +'</i>' + " 学币");
          $(".invite").html("分享赚钱");
        }
        if (CHARGE_AMT =="0.00"){
          $(".invite").html("邀请好友");
          $(".usernub").addClass("on");
        }
        $(".kaike").html("已更新" + data.alreadyCourseCount + "节课");
        $(".shengyu").html("剩余未开课" + data.notCourseCount + "节");
        $("#teacherlog").css({"background": "url(" + seriesCourse.photo + ") no-repeat center center","background-size":"100% 100%"});
        $("#crowdtitle").html(replaceTeturn2Br(seriesCourse.targetUsers));
        //课程介绍图片
        var seriesRemark = seriesCourse.remark;
        var courseImgList = data.courseImgList;
        if(courseImgList<=0){
          $("#courseImgList").hide();
          if(seriesRemark == ""){seriesRemark = '暂无简介';}
        }
        $(".livetext").html(replaceTeturn2Br(seriesRemark));
        if(seriesRemark == ""){
          $(".livetext").hide();
        };
        $(".livetext").html(replaceTeturn2Br(seriesRemark));
       	$.each(courseImgList, function (i, n) {
			if(n.address==''){
				$("#courseImgList").append('<div class="imglist"><p>'+n.content+'</p></div>');
			}else{
				$("#courseImgList").append('<div class="imglist"><img src="' + n.address + '"><p>'+n.content+'</p></div>');
			}
		});
      }

    });
  });
  function courseInfo(obj) {
    courseId = $(obj).attr("c_id");
    seriesid = $(obj).attr("s_id");
    window.location.href = "/weixin/teachercourse?id=" + courseId+"&seriesid="+seriesid;
  };
  /*下拉加载*/
  var oBoks = true;
  function serisoList(){
    if(!oBoks){return}
    oBoks=true;
    if (oBoks) {
      var seriesList = $("#sequenceClass");
      var result = ZAjaxRes({
        url: "/series/getMyCourseList?offset=" + offset + "&pageSize=" + pageSize + "&seriesid=" + seriesid,
        type: "GET"
      });
      if (result.code == "000000") {
        var courseList = result.data;
        if (courseList.length > 0) {
          $.each(courseList, function (i, n) {
            var liveWayDesc = "videoIco";
            var free = "免费";
            if (n.liveWay == ('语音直播')) liveWayDesc = "audioIco";
            var free = "免费";
            var freeClass = "free";
            if (n.chargeAmt > 0) {
              free =  n.chargeAmt+"学币";
              freeClass = "";
            }
            var count = n.visitCount;

            var li =$('<li c_id=' + n.id + ' s_id=' +seriesid + '>');
            seriesList.append(li);

            var liveTimeStatus = n.liveTimeStatus;//直播时间状态 0-结束的 1-直播中 2-预告
            var liveTimeStatusDesc = "liveingpic";
            if (liveTimeStatus == '1') {
              liveTimeStatusDesc = "backlookout";
            } else if (liveTimeStatus == '0') {
              liveTimeStatusDesc = "playbacks";
            }else if(liveTimeStatus == '2'){
              liveTimeStatusDesc = "liveingpic";
            }
            var div = $('<div class="img" style="background: url('+ n.coverssAddress + ') no-repeat center center; background-size:cover;">' +
                    '<em class="projection"><i class='+liveTimeStatusDesc+'></i><span class="playNumber">'+ count + '人</span><i class="'+liveWayDesc+'"></i></em></div>');
            li.append(div);
            li.append('<p><strong class="title">' + n.liveTopic + '</strong></p>' +
                    '<p><span class="name">主讲:'+n.userName+'</span></p><p><span class="time">' + n.startTime + '</span></p>'+
                    '</li>'
            );
            li.click(function () {
              courseInfo($(this));
            })
          });
          offset = $("#sequenceClass li").length;
        } else {
          oBoks = false;
        }
      }else{
        oBoks = false;
        if(offset==0){
          $("#sequenceClass").append("<div class='textTaps'>暂无课程</div>");
        }
      }
    }
  }
  $('.relayBtn').click(function(){
      $('body').css('overflow',' hidden');
      $('.bjyBox').hide();
      if(isRelay == '1'){
        if(relayOff == true){
          $('.newdistribution').addClass('on');
          $('#chargeAmt').val(relayCharge);
          $('#separateInto').val(relayScale);
          $('.relay_ok').removeClass('on');
          if($('.weight').html() == undefined){
            $('.fourdistribution').hide();
          }
        }else{
          $('.newdistribution').removeClass('on');
          $('#chargeAmt,#separateInto').val('');
        }
      }
      if(isOpened == '1'){
        if($('.newdistribution').hasClass('on')){
          $('#relayOK').nextAll().show();
          $('#chargeAmt,#separateInto').attr("disabled","disabled");
        }else{
          $('#relayOK').nextAll().hide();
          $('#chargeAmt,#separateInto').attr("disabled","disabled");
        }
      }else{
        if($('.newdistribution').hasClass('on')){
          $('#relayOK').nextAll().show();
        }else{
          $('#relayOK').nextAll().hide();
        }
      }
      if(chargeAmt == '0.00'){
        $('.fourdistribution').hide();
      }
      $('.relay_box').show();
  })
  //	转播
  $('#relayOK').click(function(){
    $(this).children('.newdistribution').toggleClass('on');
    if(isOpened == '1'){
      $('#chargeAmt').val(relayCharge);
      $('#separateInto').val(relayScale);
      if($('.newdistribution').hasClass('on')){
        $(this).nextAll().show();
      }else{
        $(this).nextAll().hide();
      }
    }else{
      if($('.newdistribution').hasClass('on')){
        $(this).nextAll().show();
      }else{
        $(this).nextAll().hide();
      }
    }
    console.log($('.weight').html());
    if(chargeAmt == '0.00'){
      $('.fourdistribution').hide();
    }
    if($(this).children('.newdistribution').hasClass('on')&& isOpened!= '1'){
      $('#chargeAmt').val('');
      $('#separateInto').val('');
    }else if(isOpened!= '1'){
      $('#chargeAmt').val('0');
      $('#separateInto').val('0');
    }
  })
  $('.relay_ok').click(function(){
    Required();
  })
  //    取消转播
  $('.relay_c').click(function () {
    $('.relay_box').hide();
    $('.bjyBox').show();
    $('body').css('overflow',' visible');
    if(isOpened == '0'){
      if(isRelay == '0'){
        $('.newdistribution').removeClass('on');
        $('#relayOK').nextAll().hide();
      }
      $("#chargeAmt").val('');//转播价格
      $("#separateInto").val('');//转播人所获分成比例
    }else{
      if(isRelay == '0'){
        $('.newdistribution').removeClass('on');
        $('#relayOK').nextAll().hide();
      }
    }
  })
  $(".relay_box").bind("click", function (e) {  //点击对象
    var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
    if (target.closest(".freeofcharge,.relay_btn").length == 0) {
      $('.relay_box').hide();
      $('.bjyBox').show();
      $('body').css('overflow',' visible');
      $('.newdistribution').removeClass('on');
      $('#relayOK').nextAll().hide();
      if(isOpened == '0'){
        $("#chargeAmt").val('');//转播价格
        $("#separateInto").val('');//转播人所获分成比例
      }
    }
  });
  function Required(){
    var regex = /^100$|^(\d|[1-9]\d)$/;
    var relayMoney = $("#chargeAmt").val();//转播价格
    var separateInto = $("#separateInto").val();//转播人所获分成比例
    //验证金额
    if($('#relayOK .newdistribution').hasClass('on')) {
      if (relayMoney <= 0 && relayMoney !== '0'&&relayMoney != '') {
        pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
        return;
      } else {
        if (relayMoney > 10000) {
          pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
          return;
        }
      }
      // 验证分成比例
      if ($(".newdistribution").hasClass("on") && separateInto!= '') {
        if(!/^(\+|-)?\d+$/.test( separateInto )){
          pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
          return;
        }
        if (separateInto < 0 && !regex.test(separateInto)&&relayMoney != '') {
          pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
          return;
        } else if (separateInto > 100 && !regex.test(separateInto)&&relayMoney != '') {
          pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
          return;
        }
      }
    }

    if(relayMoney == ''){
      relayMoney = 0;
      $("#chargeAmt").val(relayMoney);
    }
    if(separateInto == ''){
      separateInto = 0;
      $("#separateInto").val(separateInto);
    }
    var newdistribution = $('.newdistribution').hasClass('on');
    var isRelay = 0;
    if(newdistribution){
      isRelay = 1;
    }
    relayCharge = relayMoney;
    relayScale = separateInto;
    var params = {
      "courseId": seriesid,
      "isRelay":isRelay,
      "relayCharge":relayMoney,
      "relayScale":separateInto
    };
    var result = ZAjaxRes({url: "/course/updateCourseRelayInfo.user", type: "GET", param: params});
    if(result.code == "000000"){
      $('.relay_box').hide();
      $('.bjyBox').show();
      $('body').css('overflow',' visible');
      if($('.newdistribution').hasClass('on')){
        relayOff = true;
        isRelay = 1;
        isOpened = 1;
      }else{
        relayOff = false;
      }
      pop1({"content": "设置成功" , "status": "normal", time: '2000'});
    }else if(result.code == '10506'){
      $('.relay_box').hide();
      $('.bjyBox').show();
      $('body').css('overflow',' visible');
      if($('.newdistribution').hasClass('on')){
        relayOff = true;
      }else{
        relayOff = false;
      }
    }
  }
  //点击分享
  $(".bjy").click(function () {
    statisticsBtn({'button':'004','referer':'004001'})
    $(".invite").show();
  });
  $(".wx").on("touchstart",function(){
    $(this).addClass("wx1");
    $(".mask").show();
    share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
  })
  $(".wx").on("touchend",function(){
    $(this).removeClass("wx1")
  })
  $(".pyq").on("touchstart",function(){
    $(this).addClass("pyq1");
    $(".mask").show();
    share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
  })
  $(".pyq").on("touchend",function(){
    $(this).removeClass("pyq1")
  })
  $(".yqk").on("touchstart",function(){
    $(this).addClass("yqk1");
  })
  $(".yqk").on("touchend",function(){
    $(this).removeClass("yqk1");

    window.location.href = "/weixin/inviCard?courseId=" + seriesid+"&appId="+appId + "&type=1";
  })
  $('.sharing_btn').click(function(){
    $('.sharbox').hide();
    $(".mask").hide();
  });
  $(".sharbox").bind("click",function(e){  //点击对象
    var target  = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
    if(target.closest(".sharing").length == 0){
      $(".sharbox").hide();
      $(".mask").hide();
    }
  });
  $(".bjyBox").click(function () {
    window.location.href = "/weixin/inviCard?courseId=" + seriesid+"&appId="+appId + "&type=1&seriesid="+seriesid;
  });

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

  //  //点击直播封面图
  //  $(".ygbox").click(function () {
  //    coursePaying($(".buyclassbtns")[0]);
  //  });
  //去开课
  $("#openCourse").click(function () {
    window.location.href = "/weixin/liveRoom?sourseId=1";
  });

  $(".closebtn").click(function () {
    $(".paymentbox").hide();
    $(".choice").find("i").removeClass("on");
    $("#inputBoxContainer input").val("");
    $(".mmbox").hide();
    $(".buyclassbtns").html("加入课程：" + courseMoney + "学币");
  })


  $(".reports").click(function(){
    $(".ewmbox").addClass("on");
  })
  $(".ewmbox").on("click", function (e) {  //点击对象
    var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
    if (target.closest(".sicx,.kfewm").length == 0) {
      $(".ewmbox").removeClass("on");
    }
  });
  //    充值取消
  $(".cancel").click(function(){
    $(".rechartbox").hide();
  });
  $(".purchasesuccessBtn").click(function(){
    $(".rechartbox").hide();
  });

//  window.onload=function(){
//    var bd = document.getElementById("tabBox1-bd");
//    bd.parentNode.style.height = bd.children[0].children[0].offsetHeight+"px";
//  };
  $(".teacherzl").click(function(){
    window.location.href = "/weixin/liveRoom?id="+roomId;
  })
  $(".newgreabtn").click(function(){
    var result = ZAjaxJsonRes({url: "/liveRoom/isForbiddenRoom.user", type: "GET"});
    if( result.code=="100057"){
      $(".topBoxcon").show();
      return;
    }
      window.location.href = "/weixin/createSerieSingleCourse?seriesid=" + seriesid + "&roomId=" + roomId;
  });
  $(".buyclassbtns").click(function(){
    window.location.href = "/weixin/editSeriesCourse?seriesid=" + seriesid;
  })
//  $(".guide").click(function(){
//    $(this).remove();
//  });
  $(".tapCloseindex").click(function(){
    $(".topBoxcon").hide();
  });
  if(handlerNavigationVisitRecord("teacherSeries" ,"<%=sytem_new_version%>" , "<%=longlian_live_user_web_navigation_sign%>")){
    $(".guide").show();
  }
</script>
</html>
