<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html >
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>直播间后台</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>
<body>
<img class="excise" src="/web/res/image/gongzhonghao_qrcode.png" alt="" style="position: absolute;left: 0;bottom: 0;width:5.5rem;opacity:0;z-index: 111;"/>
  <div id="wrapper">
      <div id="cont_box" style="padding: 0">
          <dl class="backstagebox">
              <dt  id="backstagebox"  style="background: url(/web/res/image/timg.jpg) no-repeat center center;background-size:100% 100%"></dt>
              <dd>
                <i id="liveName"></i>
              </dd>
          </dl>
        <ul class="newbackstag">
          <li class="peole">
            <p class="peoplenum">0</p>
            <p>粉丝</p>
          </li>
          <p class="newbackstaglin"></p>
          <li class="todayAmount">
            <p class="redmoney">0.00</p>
            <p>今日收益</p>
          </li>
        </ul>
        <dl class="advertisement">
          <dt>
            <img src="/web/res/image/gongzhonghao_qrcode.png">
          </dt>
          <dd>长按关注「酸枣在线」公众号，此窗口将关闭。
            公众号能第一时间了解酸枣动态、最新活动；
            关注的老师开课通知、购买课程开播提醒等。</dd>
          <p class="backclose"></p>
        </dl>
        <ul class="new_liveBackdivision">
          <li class="navsetlive">
            <p><img src="/web/res/image/newSet-up-the_icon.png" alt=""/></p>
            <p>直播间设置</p>
          </li>
          <li class="navlive">
            <p><img src="/web/res/image/newMy-studio_icon.png" alt=""/></p>
            <p>我的课程</p>
          </li>
          <li class="zbjgly">
            <p><img src="/web/res/image/newAdministrator-setup_icon.png" alt=""/></p>
            <p>管理员设置</p>
          </li>
          <li class="navtj">
            <p><img src="/web/res/image/newSet-the-background_icon.png" alt=""/></p>
            <p>数据统计</p>
          </li>
          <li class="navyqk">
            <p><img src="/web/res/image/newThe-invitation-card_icon.png" alt=""/></p>
            <p>邀请卡</p>
          </li>
          <li class="navdj">
            <p><img src="/web/res/image/newWeChat_icon.png" alt=""/></p>
            <p>对接公众号</p>
          </li>
          <li class="newopencloseBtn">
            <span class="newSetuppicon"></span>
            <p class="newSetuppiconbox"></p>
            <p>课程消息推送</p>
          </li>

          <li class="navzbsc">
            <p><img src="/web/res/image/newThe-zbsc_icon.png" alt=""/></p>
            <p>转播市场</p>
          </li>
        </ul>
      </div>
  </div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>

  $('.backclose').click(function(){
    $(".advertisement").hide();
    $(".excise").hide();
  })

  var id = '${id}';
  var balance=0;
  var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
  if (result.code == "000000") {
    $("#backstagebox").attr("style", "background:url(" +  result.data.headPhoto+ ") no-repeat center center;background-size:100% 100% ;");
    if(result.data.isFollowLlWechat==1){
      $(".advertisement").hide();
    }else{
      $(".advertisement").show();
    }
  }
var messageFlag = "";
  //直播间信息
  var result = ZAjaxJsonRes({url: "/liveRoom/getLiveRoomById.user?liveRoomId=" + id, type: "GET"});
  if (result.code == "000000") {
    $("#liveName").text(result.data.liveRoom.name);
    //钱包余额
    balance = result.data.account;
    //今日收益
    todayAmount=result.data.todayAmount;
    $(".redmoney").text(todayAmount.toFixed(2));
    //获取关注数
    $(".peoplenum").text(result.data.followNum);
    messageFlag=result.data.liveRoom.messageFlag;
    if(messageFlag=='1'){
      $(".newSetuppicon,.newSetuppiconbox").removeClass('on');
    }else{
      $(".newSetuppicon,.newSetuppiconbox").addClass('on');
    }
  }
  //我的关注
  $("#userFollow").on('click', function () {
    window.location.href = "/weixin/userFollow?id=" + id;
  });

  //数据统计
  $(".navtj").on('click', function () {
    window.location.href = "/weixin/dataTotal.user?id=" + id;
  });
  //直播间收益
  $(".navsy").on('click', function () {
    window.location.href = "/weixin/myLiveIncome?mylivmoney=" + balance.toFixed(2);
  });
  //直播间设置
  $(".navsetlive").on('click', function () {
    window.location.href = "/weixin/liveRoomSet?id=" + id;
  });
  //邀请卡
  $(".navyqk").on('click', function () {
      statisticsBtn({'button':'002','referer':'002003','roomId':id})
      window.location.href = "/weixin/inviCard?roomId=" + id;
  });
  //我的直播间
  $(".navlive").on('click', function () {
     window.location.href = "/weixin/liveRoom?id=" + id;
  });
  //今日收益
  $(".todayAmount").on('click', function () {
      window.location.href = "/weixin/new_learncoinAccount?id="+id;
  });
  //粉丝
  $(".peole").on('click', function () {
      window.location.href = "/weixin/userFollow?id=" + id;
  });
  //使用教程
  $(".navsyjc").click(function () {
     window.location.href="http://www.suanzao.com.cn/p/list.html"
  });
  //对接公众号
  $(".navdj").click(function () {
      statisticsBtn({'button':'020','referer':'','roomId':id });
     window.location.href="/user/dockPublic";
  });
  //管理员设置
  $(".zbjgly").click(function(){
      statisticsBtn({'button':'019','referer':'','roomId':id});
      window.location.href="/weixin/managers";
  })
  //转播市场
  $(".navzbsc").click(function(){
      window.location.href="/weixin/relaymarket.user";
  })
  $(".newopencloseBtn").click(function(){
    if($(".newSetuppicon,.newSetuppiconbox").hasClass("on")){
      var result = ZAjaxRes({url:"/liveRoom/updateMessageFlag.user" , type:"POST"  ,param:{messageFlag:'1',id:id}});
      if(result.code == "000000") {
        $(".newSetuppicon,.newSetuppiconbox").removeClass("on");
      }
    }else{
      var result = ZAjaxRes({url:"/liveRoom/updateMessageFlag.user" , type:"POST"  ,param:{messageFlag:'0',id:id}});
      if(result.code == "000000") {
        $(".newSetuppicon,.newSetuppiconbox").addClass("on");
      }
    }
  })
  //咨询客服
//  $(".navzxkf").click(function () {
//    $(".ewmbox").addClass("on");
//  });
//  $(".ewmbox").bind("click", function (e) {  //点击对象
//    var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
//    if (target.closest(".kfewm").length == 0) {
//      $(".ewmbox").removeClass("on");
//    }
//  });

  //进入钱包-直播间收益
  $("#liveRoomIncome").click(function () {
    window.location.href = "/weixin/myLiveIncome?mylivmoney="+balance;
  });

</script>
</html>
