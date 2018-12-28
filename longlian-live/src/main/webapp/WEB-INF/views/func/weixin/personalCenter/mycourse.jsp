<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>我的课程</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
</head>
<body id="box" style="position:relative;">
<!-- 主体部分 -->
<div id="wrapper" style="overflow: auto; height:100%; -webkit-overflow-scrolling: touch;">
  <div id="cont_box" class="new_index" style="padding-bottom: 0;">
      <div class="myFcoursebox">
        <div class="myFcoursetab_box">
          <span class="classcourse active">我的课程</span>
          <span class="hostory">观看历史</span>
          <span class="buyclasscoure">购买的课程</span>
        </div>
          <span class="noSpan"></span>
        <div id="myFcourseSwiper" class="myFcourseSwiper">
          <div class="swiper-wrapper">
            <div class="swiper-slide mycourse-list">
              <p class="noTitle">未开播课程</p>
              <ul class="publicUlstyle noopenbroadcast isnoclass">

              </ul>
              <%--<div class="segmenting livesegmenting" style="display: block"></div>--%>
              <p class="noTitle">已开播课程</p>
              <ul class="publicUlstyle noopenbroadcast isnoopenclass">

              </ul>
                <div class="pullUpLabel mycourLable" style="display: none">
                    <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div>
                </div>
            </div>
            <div class="swiper-slide myhostory-list">
                <ul class="publicUlstyle noopenbroadcast">

                </ul>
                <div class="pullUpLabel myhostLable" style="display: none">
                    <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div>
                </div>
            </div>
            <div class="swiper-slide mybuyclasscoure-list">
              <ul class="publicUlstyle noopenbroadcast">

              </ul>
                <div class="pullUpLabel mybuyLable" style="display: none">
                    <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div>
                </div>
            </div>
          </div>
        </div>
      </div>
  </div>
</div>
</body>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<style>
  #wrapper{overflow: hidden}
  #cont_box{overflow: hidden;height:100%}
  #myFcourseSwiper{overflow: hidden;box-sizing: border-box;}
  .swiper-slide{overflow-y: scroll}
  .myFcoursetab_box{position: absolute}
  /*.swiper-wrapper{overflow: hidden}*/
  .noSpan{position: fixed;top:2.025rem;left: 0;padding-right: 0.75rem; transform:translate3d(0,0,0);
        width: 100%;
        height: 2rem;
      display: none;
        line-height: 2rem;
        color: #333;
        font-size: 0.75rem;
        border-bottom: 1px solid #eee;
      z-index: 11199;
        background: #fff;
        text-indent: 0.75rem;
  }
    .pullUpLabel{
        margin: 1rem auto;
    }
</style>
<script>
//          var vConsole = new VConsole();
var userType = '${userType}';
if(userType != '1'){
    $('.classcourse').remove();
    $('.mycourse-list').remove();
    $(".hostory").addClass("active");
    $(".noSpan").hide();
}
  var myFcourseSwiper = new Swiper('#myFcourseSwiper', {
    direction : 'horizontal',
    autoplay:0,
    observer:true,
    observeParents:true,
    resistanceRatio : 0,
      onSlideChangeStart: function(swiper){
        if(swiper.activeIndex == '0'){
            if(userType==1&&$(".hostory").text()=="观看历史"){
                if($(".isnoclass li").length<1&&$('.isnoopenclass li').length<1);
                $(".noSpan").hide();
            }else{
                $('.noSpan').show();
            }
            if(userType != '1'){
                $(".noSpan").hide();
            }

        }else{
            $('.noSpan').hide()
        }
      $('.myFcoursetab_box span').removeClass('active');
      $('.myFcoursetab_box span').eq(swiper.activeIndex).addClass('active');
    }
  });

//  初始每个列表数据
var listInit = {
  'mycourse':{
    'name' : 'mycourse',
    'ajaxInow' : true,
    'offset' : 0
  },
  'history':{
    'name' : 'history',
    'ajaxInow' : true,
    'offset' : 0
  },
  'purchase':{
    'name' : 'purchase',
    'ajaxInow' : true,
    'offset' : 0
  }
};
var listNum = {
    'mycourse':{offset:0},
    'history':{offset:0},
    'purchase':{offset:0}
}

$(function(){
    Load(listInit.mycourse);
    Load(listInit.history);
    Load(listInit.purchase);
    $('.noSpan').hide();

    $('.myFcoursetab_box span').on('click',function(){
        $('.myFcoursetab_box span').removeClass('active');
        $(this).addClass('active');
        myFcourseSwiper.slideTo($(this).index());
    });

    //   我的课程
    $('.mycourse-list').scroll(function(){
      listScroll($(this),listInit.mycourse);
    });

    //    观看历史
    $('.myhostory-list').scroll(function(){
      listScroll($(this),listInit.history);
    });

    //   购买的课程
    $('.mybuyclasscoure-list').scroll(function(){
      listScroll($(this),listInit.purchase);
    });
    function listScroll(_this,json) {
        var thisScrollTop = _this.scrollTop();
        var thisScrollHeight = _this.height();
        if(json.name == 'mycourse'){
          var pHeight = _this.find('.noTitle').eq(0).height()*2;
          var bottomScrollTop = _this.find('.publicUlstyle').eq(0).height()+_this.find('.publicUlstyle').eq(1).height()+pHeight;

            if($('.noTitle').length > 1){
                var spanTop = $('.noTitle').eq(1).position().top;
            }else{
                var spanTop = $('.noTitle').eq(0).position().top;
            }

            var spanHeight = $('.noTitle').eq(0).height();
            if($('.noTitle').eq(0).position().top >= 0){
                $('.noSpan').hide();
            }else{
                $('.noSpan').show();
            }
            if(spanTop-spanHeight <= 0){
                if(spanTop <= 0 && $('.noTitle').length > 1){
                    $('.noSpan').html($('.noTitle').eq(1).html());
                }else{
                    $('.noSpan').html($('.noTitle').eq(0).html());
                }
            }else{
                $('.noSpan').html($('.noTitle').eq(0).html())
            }
        }else{
          var bottomScrollTop = _this.find('.publicUlstyle ').height();
        }
        //下拉到底部加载
        if (thisScrollTop + thisScrollHeight+300 >= bottomScrollTop && json.ajaxInow) {
          Load(json);
        }
    };
});

function Load(json) {
    switch(json.name){
        case 'mycourse':
            listNum.mycourse.offset = json.offset;
            if(!json.ajaxInow){return};
            listInit.mycourse.ajaxInow = false;
            var result = ZAjaxRes({url: "/course/getMyLiveCourseList.user", type: "post",param:{liveOffset:json.offset}});
            if (result.code == "000000") {
                var nolivedCourseList = result.data.nolivedCourseList;
                var havelivedCourseList = result.data.havelivedCourseList;
                $('.mycourse-list .publicUlstyle ').eq(0).html('');
                $('.mycourse-list .publicUlstyle ').eq(0).append(listsHtmlRendering(nolivedCourseList));
                $('.mycourse-list .publicUlstyle ').eq(1).append(listsHtmlRendering(havelivedCourseList));
                if($('.mycourse-list .publicUlstyle ').eq(0).find('li').length < 1 && $('.noTitle').eq(0).text() == '未开播课程'){
                    $('.noTitle').eq(0).remove();
                    $('.mycourse-list .livesegmenting').remove();
                }
                if($('.mycourse-list .publicUlstyle ').eq(1).find('li').length < 1 && $('.noTitle').eq(1).text() == '已开播课程'){
                    $('.noTitle').eq(1).remove();
                    $(".isnoopenclass").hide();
                    $('.mycourse-list .livesegmenting').remove();
                }
                $('.noSpan').html($('.noTitle').eq(0).html()).show();
                if( havelivedCourseList < 10){
                    if($('.isnoopenclass li').length>5){
                        $(".mycourLable").show();
                    }
                }
                if(nolivedCourseList<1){
                    $(".isnoclass").hide();
                }else{
                    $(".isnoclass").show();
                }
                if(nolivedCourseList < 10 && havelivedCourseList < 10){
                  listInit.mycourse.ajaxInow = false;
                }else{
                  listInit.mycourse.offset = $('.mycourse-list .publicUlstyle ').eq(1).find('li').length;
                  listInit.mycourse.ajaxInow = true;
//                    if($('.isnoopenclass li').length>10){
//                        $(".mycourLable").show();
//                    }
                    if(listNum.mycourse.offset == listInit.mycourse.offset){
                        $(".mybuyLable").show();
                    }
                }
            }else if(result.code == "000110"){
                if($('.mycourse-list li').length < 1 && $('.mycourse-list .noData').length < 1){
                    $('.classcourse').remove();
                    $('.mycourse-list').remove();
                    $(".hostory").addClass("active");
                    myFcourseSwiper.updateSlidesSize();
                }
                listInit.mycourse.ajaxInow = false;
            }else{
                listInit.mycourse.ajaxInow = true;
            }
            break;
        case 'history':
            listNum.history.offset = json.offset;
            if(!json.ajaxInow){return};
            listInit.history.ajaxInow = false;
            var result = ZAjaxRes({url: "/course/getMyHistoryCourseList.user", type: "post",param:{historyOffset:json.offset}});
            if (result.code == "000000") {
              var mycourse = result.data;
              var size = mycourse.length;
              $('.myhostory-list .publicUlstyle ').append(listsHtmlRendering(mycourse));
              if(size<10){
                listInit.history.ajaxInow = false;
//                  if($('.myhostory-list li').length>5){
//                      $(".myhostLable").show();
//                  }
                  if(listNum.history.offset == listInit.history.offset){
                      $(".mybuyLable").show();
                  }
              }else{
                listInit.history.offset = $('.myhostory-list li').length;
                listInit.history.ajaxInow = true;
//                  if($('.myhostory-list li').length>10){
//                      $(".myhostLable").show();
//                  }
                  if(listNum.history.offset == listInit.history.offset){
                      $(".mybuyLable").show();
                  }
              }
            }else if(result.code == "000110"){
              if($('.myhostory-list li').length < 1 && $('.myhostory-list .noData').length < 1){
                $('.myhostory-list .publicUlstyle').css("background","#f0f1f4").append('<div class="noData"></div>');
              }
              listInit.history.ajaxInow = false;
            }else{
              listInit.history.ajaxInow = true;
            }
            break;
        case 'purchase':
            listNum.purchase.offset = json.offset;
            if(!json.ajaxInow){return};
            listInit.purchase.ajaxInow = false;
            var result = ZAjaxRes({url: "/course/getMyBuyFreeCourseList.user", type: "post",param:{buyOffset:json.offset}});
            if (result.code == "000000") {
              var mycourse = result.data;
              var size = mycourse.length;
              $('.mybuyclasscoure-list .publicUlstyle ').append(listsHtmlRendering(mycourse));
              if(size<10){
                listInit.purchase.ajaxInow = false;
//                  if($('.mybuyclasscoure-list li').length>5){
//                      $(".mybuyLable").show();
//                  }
//                  if(listNum.purchase.offset == listInit.purchase.offset){
//                      $(".mybuyLable").show();
//                  }
              }else{
                listInit.purchase.offset = $('.mybuyclasscoure-list li').length;
                listInit.purchase.ajaxInow = true;
//                  if($('.mybuyclasscoure-list li').length>10){
//                      $(".mybuyLable").show();
//                  }
                  if(listNum.purchase.offset == listInit.purchase.offset){
                      $(".mybuyLable").show();
                  }
              }
            }else if(result.code == "000110"){
              if($('.mybuyclasscoure-list li').length < 1 && $('.mybuyclasscoure-list .noData').length < 1){
                 $('.mybuyclasscoure-list .publicUlstyle').css("background","#f0f1f4").append('<div class="noData"></div>');
              }
              listInit.purchase.ajaxInow = false;
            }else{
              listInit.purchase.ajaxInow = true;
            }
            break;
        default:
         return false;
    }
};

//  公共渲染lists列表数据转换成html格式
function listsHtmlRendering(mycourse){
      var arrHtml = [];
      $.each(mycourse, function (i, n) {
          //是否是系列课
          var hed="";
          var isSeriesCourse = n.isSeriesCourse;
          if(isSeriesCourse == '0'){
            var oHtml = '主讲：'+n.name;
          }else{
            hed="hed";
            var oHtml = "<span class='series'></span>"+'系列课 | 已更新'+n.updatedCount+'节'
          }
          //转播课
          var relay_icon="";
          if((n.id).toString().length>=15){
              relay_icon = '<i class="relay_icon"></i>';
          }else{
              relay_icon ="";
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
          if(n.liveTimeStatus=="1"){
            liveingpic='backlookout';
          }else if(n.liveTimeStatus=="2"){
              liveingpic='liveingpic';
          }else if(n.liveTimeStatus=="0"){
            liveingpic='playbacks';
          }
          arrHtml.push('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'"><div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
                  '<em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">'+ n.studyCount+'人</span>' +
                  '<i class='+liveWayDesc+'></i></em></div>' +
                  '<p> <strong class="title">'+relay_icon+ n.liveTopic+'</strong></p>' +
                  '<p><span class="name">'+oHtml+'</span></p><p><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong>' +
                  '</p></li>');
      });
      return arrHtml.join('');
};

// 点击每个Lists下的li做相应的跳转
  $('.swiper-wrapper').on('click','.swiper-wrapper li',function () {
    var id = $(this).attr('data-id');
    var isSeriesCourse = $(this).attr('data-SeriesCourse');
    toCourseInfo(id,isSeriesCourse);
  });

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
