<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #f7f7f7">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>我的收益</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;" onload="doInit()">
<div id="wrapper"style=" height:100%;max-width: 18.75rem;margin: 0 auto">
  <div id="cont_box">
    <div class="publicPopups" style="display: none">
      <div class="publicPopupsbox">
        <p class="publicPopupstitle">提示</p>
        <p class="publicPopupscont">流量不足,暂时无法提现</br>请及时充值</p>
        <ul class="publicPopupsul">
          <li class="no">取消</li>
          <li class="yes">充值</li>
        </ul>
      </div>
    </div>
  <div class="learncoinTitle">
    <span class="alllearMoney">总收益(枣币)</span>
    <span id="balance">0.00</span>
    <%--<p>今日收益<span class="todyTake">0.00</span></p>--%>
    <div class="myMoeybtn" onclick="cash()">
      提现
    </div>
  </div>
  <div class="learPubicbox">
    <div class="learPubiccont"onclick="todyTake()">
      <div class="nextCont">
        <span><img src="/web/res/image/to.png">今日收益</span>
        <p class="rightlearBox">
          <em class="todyTake"></em>
          <i></i>
        </p>
      </div>
    </div>
  </div>
  <div class="learPubicbox">
     <div class="learPubiccont"onclick="mylive()">
       <div class="nextCont">
         <span><img src="/web/res/image/courseget.png">课程收益</span>
         <p class="rightlearBox">
           <em id="mylivmonry"></em>
           <i></i>
         </p>
       </div>
     </div>
    <div class="learPubiccont" onclick="dis()">
      <div class="nextCont">
        <span><img src="/web/res/image/fxget.png">分销收益</span>
        <p class="rightlearBox">
          <em id="fxsymoney"></em>
          <i></i>
        </p>
      </div>
    </div>
    <div class="learPubiccont"onclick="reward()">
      <div class="nextCont">
        <span><img src="/web/res/image/payget.png">打赏收益</span>
        <p class="rightlearBox">
          <em id="rewardmoney"></em>
          <i></i>
        </p>
      </div>
    </div>
    <div class="learPubiccont"onclick="zbsy()">
      <div class="nextCont">
        <span><img src="/web/res/image/zbsy.png">转播收益</span>
        <p class="rightlearBox">
          <em id="zbsydmoney"></em>
          <i></i>
        </p>
      </div>
    </div>
  </div>
  <div class="learPubicbox border_none" >
        <div class="learPubiccont"onclick="tradetails()">
          <div class="nextCont">
            <span><img src="/web/res/image/jyget.png">收益明细</span><i></i>
          </div>
        </div>
      <div class="learPubiccont">
        <div class="nextCont"onclick="updatefuc()" >
          <span><img src="/web/res/image/changeget.png">更换交易密码</span><i></i>
        </div>
      </div>
  </div>
    <div class="learPubicbox border_none" >
        <div class="learPubiccont"onclick="dhzb()">
          <div class="nextCont">
            <span><img src="/web/res/image/dhxb.png">兑换学币</span><i></i>
          </div>
        </div>
      <div class="learPubiccont">
        <div class="nextCont"onclick="dhmx()" >
          <span><img src="/web/res/image/dhmx.png">兑换明细</span><i></i>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
<script>
  function doInit(){
    var result = ZAjaxRes({url: "/accountTrack/findTodayWalletNew.user"});
    if(result.code == '000000'){
      $("#platmoney").text((result.data.plaAmount).toFixed(2));//平台收益
      $("#fxsymoney").text((result.data.disAmount).toFixed(2));//分销收益
      $("#mylivmonry").text((result.data.livAmount).toFixed(2));//课程收益
      $("#proxymoney").text((result.data.proAmount).toFixed(2));
      $("#rewardmoney").text((result.data.rewAmount).toFixed(2));//打赏收益
      $("#balance").text((result.data.balance).toFixed(2));//总收益
      $(".todyTake").text((result.data.todayAmount).toFixed(2));//今日收益
      $("#zbsydmoney").text((result.data.relayAmount).toFixed(2));//转播收益
    } else{
      sys_pop(result);
    }
  }

  //直播间收益
  function mylive(){
    window.location.href="/weixin/myLiveIncome?mylivmoney="+$("#mylivmonry").text();
  }

  //分销收益
  function dis(){
    window.location.href="/weixin/disIncome?disIncome="+$("#fxsymoney").text();
  }
  //转播收益
  function zbsy(){
    window.location.href="/weixin/myRevenue";
  }
  //平台收益
  function plat(){
    window.location.href="/weixin/platIncome?platIncome="+$("#platmoney").text();
  }
  //我的打赏收益
  function reward(){
    window.location.href="/weixin/reward";
  }
  //更换交易密码
  function updatefuc(){
    window.location.href="/account/findUserAccount.user?type=5";
  }
  //交易明细
  function tradetails(){
    window.location.href="/weixin/toMoneyList";
  }
  //兑换明细
  function dhmx(){
    window.location.href="/weixin/detailsFor";
  }
  //兑换学币
  function dhzb(){
    window.location.href="/weixin/exchangeCurrency.user";
  }
  function cash(){
    var result = ZAjaxRes({url: "/liveRoom/findFlow.user"});
    if(result.code == '000000' && result.data != '1'){
      $(".publicPopups").show();
      return;
    }
    var result = ZAjaxRes({url: "/accountTrack/getAccount.user"});
    if(result.code == '000000'){
      var isProxy = result.data;// 0:没有代理 1:代理
      window.location.href="/weixin/carryCash.user?type=cash&isProxy="+isProxy;
    }else{
      sys_pop(result);
    }
  }
  $(".no").click(function(){
    $(".publicPopups").hide();
  })
  $(".yes").click(function(){
    $(".publicPopups").hide();
    window.location.href="/weixin/flowRecharge";
  })
  //代理收益
  function proxy(){
    window.location.href="/weixin/proxyIncome?proxyIncome="+$("#proxymoney").text();
  }
//  今日收益
  function todyTake(){
    window.location.href="/weixin/earningsDay.user";
  }
</script>
</html>
