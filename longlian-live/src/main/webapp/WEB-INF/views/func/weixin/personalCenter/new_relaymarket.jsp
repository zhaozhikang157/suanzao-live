<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title id="liveName">转播市场</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/relaymarket.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-2.1.0.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;" id="scrollId">
  <header>
    <div class="search_box">
      <div class="searchInput" style="display: -webkit-box;-webkit-box-align: center;-webkit-box-pack: justify;position: relative">
        <form action="javascript:void(0);" style="display: block;-webkit-box-flex: 1;">
          <input type="search" id="searchFn" maxlength="30" placeholder="搜索单节课/系列课"  style="border:none;" value="">
        </form>
        <span class="searchcloseBtn" data-search="0">取消</span>
        <span class="searclose_index"></span>
      </div>
    </div>
    <div class="sort_box">
      <ul>
        <li class="on">综合排序</li>
        <li>最新</li>
        <li>转播价格<i class=""></i></li>
        <li>分成比例<i class=""></i></li>
      </ul>
    </div>
  </header>
<div class="content">
  <div id="conScroll">
  </div>
  <div class="pullUpLabel selectedLabel"> <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div></div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script>
  var sortIndex = 0; // 记录点击某种排序
  var scIndex = 1; // 记录转播价格/分成比例 正序倒序
  $(function(){
    if($('#searchFn').val() != ''){
      sortLoad(0,1,0,$('#searchFn').val());
    }else{
      sortLoad(0,1,0,'');
    }
    $('.content').scrollTop(0);
  })
<%--排序--%>
$('.sort_box').on('click','li',function(){
  $(this).addClass('on').siblings().removeClass('on');
  var index = $(this).index();
  sortIndex = index;
  $('.content').scrollTop(0)
  if(index == 0){ //综合排序
    $('.sort_box li').children('i').removeClass('down up');
  }else if(index == 1){  //最新
    $('.sort_box li').children('i').removeClass('down up');
    scIndex = 1;
  }else if(index == 2){  //转播价格
    $('.sort_box li').eq(3).children('i').removeClass('down up');
    if($(this).hasClass('on')&&$(this).children('i')){
      if($(this).children('i').hasClass('down')||$(this).children('i').hasClass('')){
        $(this).children('i').addClass('up').removeClass('down');
        scIndex = 0;
      }else{
        $(this).children('i').addClass('down').removeClass('up');
        scIndex = 1;
      }
    }
  }else{ //分成比例
    $('.sort_box li').eq(2).children('i').removeClass('down up');
    if($(this).hasClass('on')&&$(this).children('i')){
      if($(this).children('i').hasClass('down')||$(this).children('i').hasClass('')){
        $(this).children('i').addClass('up').removeClass('down');
        scIndex = 0;
      }else{
        $(this).children('i').addClass('down').removeClass('up');
        scIndex = 1;
      }
    }
  }
  $('#conScroll').html('');
  sortLoad(index,scIndex,0,'');
})
//  搜索
$("#searchFn").focus(function(){
  if($('.sort_box').is(':visible')){
    $('#conScroll').html('');
  }
  $(".searchcloseBtn").show();
  $('.sort_box').hide();
  $('header').height('2.5rem');
  $('.content').css('top','2.7rem');
});

$("#searchFn").on('input',function(){
  if( $(this).val() != ''){
    $('.searchcloseBtn').html('搜索').attr('data-search','1').css('color','#d53c3e');
    $(".searclose_index").show();
  }else{
    $('.searchcloseBtn').html('取消').attr('data-search','0').css('color','#282828');
  }
})
  document.onkeyup = function (e) {//按键信息对象以函数参数的形式传递进来了，就是那个e
    var code = e.charCode || e.keyCode;  //取出按键信息中的按键代码(大部分浏览器通过keyCode属性获取按键代码，但少部分浏览器使用的却是charCode)
    if (code == 13) {
      if($('#searchFn').val() != ''){
        $("#conScroll").empty();
        sortLoad(0,0,0,$('#searchFn').val());
        $("#searchFn").blur();
      }
    }
  }

$(".searclose_index").click(function(){
  $('#searchFn').val("");
  $('.searchcloseBtn').html('取消').attr('data-search','0').css('color','#282828');
  $(this).hide();
  $('.noData').remove();
})
$(".searchcloseBtn").click(function(){
  if($(this).attr('data-search') == 0){
    $(this).hide();
    $('.sort_box').show();
    $('.noData').remove();
    $('header').height('4.1rem');
    $('.content').css('top','4.3rem');
    $('#conScroll').html('');
    $('#searchFn').val("");
    sortLoad(sortIndex,scIndex,0,'');
  }else if($(this).attr('data-search') == 1){
    $('.noData').remove();
    $('#conScroll').html('');
    sortLoad(0,0,0,$('#searchFn').val());
  }
});
//  点击进入详情
$('#conScroll').on('click','#toCourseInfo',function(){
  var isSeriesCourse = $(this).attr('data-isSeriesCourse');
  var id = $(this).attr('data-id');
  if(isSeriesCourse =='1'){
    window.location.href = "/weixin/courseInfo?id="+id+"&isSeries=1";
  }else{
    window.location.href = "/weixin/courseInfo?id=" + id;
  }
})
// 上拉加载
  var scrollEnd = true;
  $('.content').scroll(function(){
    var thisScrollTop = this.scrollTop;
    var thisScrollHeight = $(this).height();
    var bottomScrollTop = $(this).children('#conScroll').height();
    if(thisScrollTop + thisScrollHeight >= bottomScrollTop-10 ){
      if(scrollEnd == true){
        console.log('到底了');
        scrollEnd = false;
        if($('#searchFn').val() != ''){
          sortLoad(0,0,1,$('#searchFn').val());
        }else{
          sortLoad(sortIndex,scIndex,1,'');
        }
      }
    }
    setTimeout(function(){scrollEnd = true;},2000);
  })
//转播列表加载
  function sortLoad(a,b,c,d){ //c代表是否滚动条加载 0否1是
    var listLen = 0;
    if(c == 1){
      listLen = $('#conScroll>.sort_list').length;
    }
    var param = {
      pageSize:20,
      offset:listLen,
      sort:a,
      sc:b,
      courseName:d
    }
    var result = ZAjaxRes({url: "/course/queryCanBroadcastCoursePage.user", type: "GET",param:param});
    if(result.code == "000000"){
      var html = '';
      var data = result.data;
      if(data == ''){
        if($('#conScroll div').length < 1 && $('#conScroll .noData').length < 1){
          $('#conScroll').append('<div class="noData"></div>');
        }else{
          $('.pullUpLabel ').show();
        }
      }else{
        $('.noData').remove();
        $('.pullUpLabel ').hide();
      }
      var liveWayIco = '';
      var isstate = '';
      var teacherName = '';
      var courseRelayId = "";
      var relayCharge = '';
      for(var i = 0;i < data.length;i++){
        if(data[i].isSeriesCourse == 0){ // 是否系列课 0否1是
          if(data[i].liveWay == 0){ //视频
            liveWayIco = 'videoIco';
          }else if(data[i].liveWay == 1){ // 语音
            liveWayIco = 'audioIco';
          }
          teacherName = data[i].teacherName;
          //修改的直播状态
          if(data[i].statusStr == 0){
            isstate = 'playbacks';
          }else if(data[i].statusStr == 1){// 直播状态 0回放1直播
            isstate = 'backlookout';
          }else if(data[i].statusStr == 2){
            isstate = 'liveingpic';
          }
        }else{
          liveWayIco = '';
          teacherName = '<span class="series"></span>已更新'+ data[i].updatedCount+'节';
          if(data[i].updatedCount=="0"){
            isstate = ""
          }
        }




        if(data[i].courseRelayId != ''){
            courseRelayId = 'class="on">已转播';
        }else{
          if(data[i].isMyCourse == 1){
            courseRelayId = 'class="on">转播';
          }else{
            courseRelayId = ">转播";
          }
        }

        if(data[i].relayCharge != 0){
          relayCharge = '转播价格 <i>'+data[i].relayCharge+'学币</i>';
        }else{
          relayCharge = '免费转播';
        }
        html += '<div class="sort_list">'
                +'<div id="toCourseInfo" data-isSeriesCourse="'+data[i].isSeriesCourse+'" data-id="'+ data[i].id +'">'
                +'<div class="sort_img" style="background:url('+data[i].coverssAddress+') no-repeat center center; background-size:cover;">'
                +'<em class="projection">'
                +'<i class="'+ isstate +'"></i>'
                +'<span class="playNumber">'+data[i].visitCount+'人</span>'
                +'<span class="'+liveWayIco+'"></span>'
                +'</em></div>'
                +'<div class="sort_info">'
                +'<div class="sort_tlt">'+data[i].liveTopic+'</div>'
                +'<div class="sort_money">'+data[i].chargeAmt+'学币 <i>(分成比例<em>'+data[i].relayScale+'%</em>)</i></div>'
                +'<div class="sort_time"><span>'+data[i].startTimeStr+'</span><span>'+teacherName+'</span></div>'
                +'</div></div>'
                +'<div class="sort_btn">'
                +'<span onclick="payRelay(this)" data-id="'+data[i].id+'" data-relayCharge="'+data[i].relayCharge+'"'+courseRelayId+'</span>'
                +'<span>'+relayCharge+'</span>'
                +'</div></div>'
      }
      $('#conScroll').append(html);
    }
  }
function payRelay(_this){
  if($(_this).attr('class') == 'on'){
    return;
  }
  var courseId = $(_this).attr('data-id');
  var amount = $(_this).attr('data-relayCharge');
  var param = {payType: "07", password: "", amount: amount, courseId: courseId, deviceNo: "", isBuy: '0'};
  var result = PayByRelpay(param);
  if(result.code == '000000'){
    setTimeout(function () {
      payOk(result.data.roomId,_this);
    },500);
  }else if(result.code == '100012'){ //将枣币转换为学币
    var attch= result.attach
    BaseFun.Dialog.Config2({
      title: '提示',
      text : '学币不足，有枣币可兑换',
      cancel:'去兑换',
      confirm:'不用，直接买',
      close:true,
      callback:function(index) {
        if(index == 0){
          window.location.href="/weixin/exchangeCurrency.user?attach="+attch;
        }else if(index == 1){
          var param = {payType: "14", password: "", amount: "", courseId: courseId, deviceNo: "", isBuy: "1"};
          var result = PayByRelpay(param);
          isTrySeeFlag = true;
          onBridgeReady(result,_this);
        }
      }
    });
  }else if (result.code == '100002') {//不足
    //直接支付
    BaseFun.Dialog.Config2({
      title: '提示',
      text : '您将支付' + amount + '学币购买该课程',
      cancel:'取消',
      confirm:'确定',
      close:false,
      callback:function(index) {
        if(index == 1){
          var param = {payType: "14", password: "", amount: "", courseId: courseId, deviceNo: "", isBuy: "1"};
          var result = PayByRelpay(param);
          isTrySeeFlag = true;
          onBridgeReady(result,_this);
        }
      }
    });
  }else if (result.code == '100025') {//足够
    //可以支付弹出确认按钮
    BaseFun.Dialog.Config2({
      title: '提示',
      text : '确定要支付' + amount + '学币转播此课程吗',
      cancel:'取消',
      confirm:'确定',
      close:false,
      callback:function(index) {
        if(index == 1){
          var param = {payType: "07", password: "", amount: amount, courseId: courseId, deviceNo: "", isBuy: '1' };
          var result = PayByRelpay(param);
          if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
            setTimeout(function () {
              payOk(result.data.roomId,_this);
            },1000);
          }
        }
      }
    });
  }else if(result.code == '10505'){
    pop({width:"12.5rem","content": "需要等待上一节转播课直播结束" , "status": "normal", time: '2500'});
  }else if(result.code == '10507'){
    pop({"content": "当前存在正在直播的课程" , "status": "normal", time: '2500'});
  }else if(result.code == '000125'){
    pop({"content": "服务器异常，请稍后重试" , "status": "normal", time: '2500'});
  }
}
  function payOk(roomId,__this){
    BaseFun.Dialog.Config2({
      title: '提示',
      text : '转播成功，请稍后在直播间内查看',
      cancel:'继续转播',
      confirm:'查看',
      close:false,
      callback:function(index) {
        $(__this).addClass('on').html('已转播');
        if(index == 1){
          window.location.href = '/weixin/liveRoom?id='+roomId;
        }
      }
    })
  }
  /**
   *微信支付
   */
  function onBridgeReady(result,__this) {
    if (!result) return;
    var data = result.data;
    var orderId = result.ext;
    WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
              "appId": data.appId,     //公众号名称，由商户传入
              "timeStamp": data.timeStamp,         //时间戳，自1970年以来的秒数
              "nonceStr": data.nonceStr, //随机串
              "package": data.package,
              "signType": data.signType,         //微信签名方式:
              "paySign": data.paySign    //微信签名
            },
            function (res) {
              if (res.err_msg == "get_brand_wcpay_request:ok") {
                payOk(data,__this);
              } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                var result = {code: "000000", message: "取消支付"};
                var param = {orderId: orderId};
                //cancelThirdPay(param);
                pop1({"content": "取消支付" , "status": "error", time: '2500'});
              } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                var param = {orderId: orderId};
                //cancelThirdPay(param);
              }
              // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。         吗没呢，                                ，
            }
    );
  }
</script>
</html>
