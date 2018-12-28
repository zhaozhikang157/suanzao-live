<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>免费专区</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
</head>
<body id="box" style="position:relative;">
<!-- 主体部分 -->
<div id="wrapper" style="overflow: hidden; height:100%; -webkit-overflow-scrolling: touch;">
    <div id="cont_box"class="new_index" style="background: white;padding-bottom: 0;height: 100%">
        <ul class="freeNav">
          <li class="on">综合排序</li>
          <li>人气</li>
          <li>开课时间<em><i class="uptriangle"></i><i class="downtriangle"></i></em></li>
        </ul>
      <div class="freeContbox" style="padding-top: 2.05rem">
          <ul class="publicUlstyle"id="comprehensive" style="display: block;">
          </ul>
          <ul class="publicUlstyle"id="popuLaritys">
          </ul>
          <ul class="publicUlstyle"id="sortingTime">
          </ul>
      </div>
    </div>
</div>
</body>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<%--<style>--%>
  <%--#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}--%>
<%--</style>--%>
<%--<script src="/web/res/js/vconsole.min.js"></script>--%>
<script>
//  var vConsole = new VConsole();
  var listInit = {
    'comprehensive':{
      'name' : 'comprehensive',
      'ajaxInow' : true,
      'offset' :0,
    },
    'popuLaritys':{
      'name' : 'popuLaritys',
      'ajaxInow' : true,
      'offset' :0,
    },
    'sortingTime':{
      'name' : 'sortingTime',
      'ajaxInow' : true,
      'offset' :0,
      'sc':1,
    }
  };

  loading(listInit.comprehensive);
  loading(listInit.popuLaritys);

  //   综合排序
  $('#comprehensive').scroll(function(){
    if(!listInit.comprehensive.ajaxInow){return;}
    listScroll($(this),listInit.comprehensive);
  });

  //  人气
  $('#popuLaritys').scroll(function(){
    if(!listInit.popuLaritys.ajaxInow){return;}
    listScroll($(this),listInit.popuLaritys);
  });

  // 开课时间
  $('#sortingTime').scroll(function(){
    if(!listInit.sortingTime.ajaxInow){return;}
    listScroll($(this),listInit.sortingTime);
  });

  $(".freeNav li").click(function(){
    $(this).addClass("on").siblings().removeClass("on");
    var index = $(this).index();
    $(".freeContbox>ul").eq(index).show().siblings().hide();
    if(index == 0){
      $(".freeNav li").eq(2).find("em").removeClass("on off")
      if($('#comprehensive li').length){return}
      listInit.comprehensive.offset = 0;
      listScroll($(this),listInit.comprehensive);
    }else if(index == 1){
      $(".freeNav li").eq(2).find("em").removeClass("on off")
      if($('#popuLaritys li').length){return}
      listInit.popuLaritys.offset = 0;
      listScroll($(this),listInit.popuLaritys);
    }else if(index == 2){
      listInit.sortingTime.offset = 0;
      $("#sortingTime").html('');
      if(listInit.sortingTime.sc){
          listInit.sortingTime.sc = 0;
          $(this).find("em").addClass("on").removeClass("off");
      }else{
          listInit.sortingTime.sc = 1;
          $(this).find("em").removeClass("on").addClass("off");
      }
      listScroll($(this),listInit.sortingTime);
    }
  });

  function listScroll(_this,json) {
    var thisScrollTop = _this.scrollTop();
    var thisScrollHeight = _this.height()+$(".freeNav").height();
    var bottomScrollTop = _this.find('li').height()*_this.find('li').length-thisScrollHeight;
    //下拉到底部加载
    if (thisScrollTop + thisScrollHeight > bottomScrollTop && json.ajaxInow) {
      loading(json);
    }
//    console.log(thisScrollTop,thisScrollHeight,bottomScrollTop)
  };


  function loading(json) {
    switch(json.name){
      case 'comprehensive':
        if(!json.ajaxInow){return};
        listInit.comprehensive.ajaxInow = false;
        var result = ZAjaxRes({url: "/course/findFreeAdmissionCoursePage", type: "POST",param:{offset:json.offset,sort:0,pageSize:10}});
        if (result.code == "000000") {
          var mycourse = result.data;
          var size = mycourse.length;
          $('#comprehensive').append(htmllist(mycourse));

          listInit.comprehensive.offset=$('#comprehensive li').length;
          listInit.comprehensive.ajaxInow = true;

        }else if(result.code == "000110"){
          if($('#comprehensive li').length < 1){
            $('#comprehensive').append('<div class="noData"></div>');
          }
          listInit.comprehensive.ajaxInow = false;
        }else{
          listInit.comprehensive.ajaxInow = true;
        }
        break;
      case 'popuLaritys':
        if(!json.ajaxInow){return};
        listInit.popuLaritys.ajaxInow = false;
        var result = ZAjaxRes({url: "/course/findFreeAdmissionCoursePage", type: "POST",param:{offset:json.offset,sort:1,pageSize:10}});
        if (result.code == "000000") {
          var mycourse = result.data;
          var size = mycourse.length;
          $('#popuLaritys').append(htmllist(mycourse));

          listInit.popuLaritys.offset=$('#popuLaritys li').length;
          listInit.popuLaritys.ajaxInow = true;

        }else if(result.code == "000110"){
          if($('#popuLaritys').length < 1 && $('#popuLaritys .noData').length < 1){
            $('#popuLaritys').append('<div class="noData"></div>');
          }
          listInit.popuLaritys.ajaxInow = false;
        }else{
          listInit.popuLaritys.ajaxInow = true;
        }
        break;
      case 'sortingTime':
        if(!json.ajaxInow){return};
        listInit.sortingTime.ajaxInow = false;
        var result = ZAjaxRes({url: "/course/findFreeAdmissionCoursePage", type: "POST",param:{offset:json.offset,sort:2,pageSize:10,sc:json.sc}});
        if (result.code == "000000") {
          var mycourse = result.data;
          var size = mycourse.length;
          $('#sortingTime').append(htmllist(mycourse));

          listInit.sortingTime.offset=$('#sortingTime li').length;
          listInit.sortingTime.ajaxInow = true;

        }else if(result.code == "000110"){
          if($('#sortingTime li').length < 1 && $('#sortingTime .noData').length < 1){
            $('#sortingTime').append('<div class="noData"></div>');
          }
          listInit.sortingTime.ajaxInow = false;
        }else{
          listInit.sortingTime.ajaxInow = true;
        }
        break;
      default:
        return false;
    }
  };

function htmllist(courseFree){
  var htmlArr = [];
  $.each(courseFree, function (i, n) {
    //是否是系列课
    var hed="";
    var isSeriesCourse = n.isSeriesCourse;
    if(isSeriesCourse == '0'){
      var oHtml = '主讲：'+n.userName;
    }else{
      hed="hed";
      var oHtml = "<span class='series'></span>"+'系列课 | 已更新'+n.updatedCount+'节'
    }
    //直播类型
    var liveWayDesc = "";
    if (n.liveWay == '1'){
      liveWayDesc = "audioIco";
    }else if(n.liveWay == '0') {
      liveWayDesc = "videoIco";
    }
    //课程金额
    var free = "免费";
    var freeClass = "free";
    if (n.chargeAmt > 0) {
      free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
      freeClass = "";
    }
    //正在直播
    var liveingpic="playbacks";
    if(n.statusStr=="0"){
      liveingpic='playbacks';
    }else if(n.statusStr=="1"){
      liveingpic='backlookout';
    }else if(n.statusStr=="2"){
      liveingpic='liveingpic';
    }
    htmlArr.push('<li data-id="'+n.id+'" data-isserie="'+ n.isSeriesCourse+'"><div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
            '<em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">'+ n.joinCount+'人</span>' +
            '<i class='+liveWayDesc+'></i></em></div>' +
            '<p> <strong class="title">'+ n.topic+'</strong></p>' +
            '<p><span class="name">'+oHtml+'</span></p><p><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong>' +
            '</p></li>');
  });
  return htmlArr.join('');
}
</script>
<script>
  $(".publicUlstyle").on("click",".publicUlstyle li",function(){
    var isSeriesCourse  = $(this).attr("data-isserie");
    var id = $(this).attr("data-id");
    toCourseInfo(id,isSeriesCourse)
  })
  /**
   *
   *  跳转至课程详情页面
   * @param id
   */
  function toCourseInfo(id,isSeriesCourse) {
    if(isSeriesCourse =='1'){
      window.location.href = "/weixin/courseInfo?id="+id+"&isSeries=1";
    }else{
      window.location.href = "/weixin/courseInfo?id=" + id;
    }
  };
</script>
</html>
