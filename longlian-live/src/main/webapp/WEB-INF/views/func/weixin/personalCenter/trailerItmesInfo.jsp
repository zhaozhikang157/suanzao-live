<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>直播预告</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
<style>
  .newfreebox{
    background: white;
    margin-bottom: 0.5rem;
    box-sizing: border-box;
    overflow: hidden;
  }
body{
  -webkit-tap-highlight-color:#fff
}
  .publicUlstyle{
    margin-bottom: .5rem;
  }
</style>
</head>

<body style="background: white">
  <div id="cont_box"class="new_index" style="padding-bottom: 0;overflow-y: scroll;height: 100%;-webkit-overflow-scrolling: touch;">
    <div style="overflow: auto">
      <ul class="publicUlstyle" id="weekSkbox">
      </ul>
      <div class="pullUpLabel selectedLabel" style="display: none;"> <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div></div>
    </div>
  </div>
</body>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/bscroll.min.js?nd=<%=current_version%>"></script>
<style>

  .new_index{height: 100%}
  .freeContbox ul{overflow: auto}
  #cont_box{position: relative}
  .tapTitle{webkit-margin-before: 0em;
    -webkit-margin-after: 0em;
    -webkit-margin-start: 0px;
    -webkit-margin-end: 0px;
  }
</style>
<script>

  var i = 0;
  $(function(){
    A(0,10,0);
    if($("#weekSkbox li").length<10){
      A(0,10,1);
      if($("#prelookBox li").length<10){
        A(0,10,2);
      }
    }


    /*直播 精选 下拉加载*/
    $('#cont_box').scroll(function(){
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(window).height();
      var scrollHeight =$('.publicUlstyle').find("li").height()*$('.publicUlstyle li').length;
      //下拉到底部加载
      if(scrollTop + windowHeight >= scrollHeight+$(".tapSpan").height()){
          A($("#weekSkbox li").length,10,0);
      }
    });
  });
</script>
<script>
  function A(offset,pageSize,timeStr){
    var param = {offset: offset,pageSize:pageSize,timeStr:timeStr};
    var result = ZAjaxRes({url: "/course/findCommend4HomeV4Page", type: "post",param: param});
    if (result.code == "000000") {
      var courseFree = result.data;
      var size = courseFree.length;
      var aa =openSize(courseFree)
      if(timeStr=="0"){
        $("#weekSkbox").append(aa);
      }
//      点击绑定跳转
      $('.publicUlstyle').on('click','.publicUlstyle li',function () {
        var id = $(this).attr('data-id');
        var isSeriesCourse = $(this).attr('data-SeriesCourse');
        toCourseInfo(id,isSeriesCourse);
      });

      index++;
      multiple=false;
      if(size<10){
        oBok=false;
      }
    }else if(result.code =="000110"){
      $('.selectedLabel').show();
    }else{
      if(timeStr=="0"){
        i=1;
      }
    }
  }


  function openSize(courseFree){
    var weeksearr=[];
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
      weeksearr.push('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'"><div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
              '<em class="projection"><i class="backlookout"></i><span class="playNumber">'+ n.joinCount+'人气</span>' +
              '<i class='+liveWayDesc+'></i></em></div>' +
              '<p> <strong class="title">'+ n.topic+'</strong></p>' +
              '<p><span class="name">'+oHtml+'</span></p><p><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong></p></li>'
      )
    });
    return weeksearr.join('');
  }


</script>
<script>
  var oBok = true;
  var index= 0;
  var multiple =false;
  var sort= 0;
  function Load() {
    if(multiple){
      return;
    }
    multiple=true;
    if(oBok){

    }
  }
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
  $(".freeNav li").click(function(){
    $(this).addClass("on").siblings().removeClass("on");
    if($(this).index() == 2){
      if($(this).find("em").hasClass("on")){
        $(this).find("em").removeClass("on").addClass("off");
      }else{
        $(this).find("em").addClass("on").removeClass("off");
      }
    }else{
      $(".freeNav li").eq(2).find("em").removeClass("on off")
    }

  });
</script>
</html>









