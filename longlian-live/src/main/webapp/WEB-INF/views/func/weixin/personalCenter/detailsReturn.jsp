<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #f7f7f7">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title class="zb"></title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrapper"style="height:100%;-webkit-overflow-scrolling: touch;overflow: hidden">
  <div id="cont_box"style="padding-bottom:0;background:#FFF; height:100%">
    <div class="detailsBanner">
        <div class="detailsTite"></div>
    </div>
    <div class="detailsCont">
        <div class="detailsDetali">
            <div class="topDer">
              <p>
                <i class="zbsy">0.00</i>
                <i>转播收益(枣币)</i>
              </p>
              <span class="detailL"></span>
              <p>
                <i class="fcsy">0.00</i>
                <i>分成收益(枣币)</i>
              </p>
            </div>
            <div class="topDer bottDer">
              <p>
                <i class="zbrs">0.00</i>
                <i>转播人数</i>
              </p>
              <span class="detailL"></span>
              <p>
                <i class="zbrbl">0</i>
                <i>转播人所获分成比例</i>
              </p>
            </div>
        </div>
    </div>

    <div id="myDetals" style="overflow-y: scroll;">
      <div class="allAmount" style="background: white">

      </div>
    </div>
  </div>
</div>
</body>
<script>
  var offset = 0;
  var relaier= getQueryString('courseId');
  var zb=getQueryString("zb");
  var jsonUrl= "";
  $(function () {
    Load();
    /*直播 精选 下拉加载*/
    $('#myDetals').scroll(function () {
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('.allAmount').height();
      if (scrollTop + windowHeight >= scrollHeight - 10) {
        Load();
      }
    });
  });

  var oBok = true;
  var details= ""
  function Load() {
    if (oBok) {
      if(zb=="1"){
        $(".zb").text("今日转播收益详情");
        jsonUrl ="/llAccountTrack/income/today/relay/relaier/list.user"
      }else{
        $(".zb").text("转播收益详情")
        jsonUrl ="/llAccountTrack/income/relay/relaier/list.user"
      }
      var result = ZAjaxRes({url: jsonUrl+"?offset="+offset+"&courseId="+relaier});
      if (result.code == "000000") {
        console.log(result)
        $(".zbsy").text(result.data.TOTAL_RELAY);
        $(".fcsy").text(result.data.TOTAL_PROFIT);
        $(".zbrs").text(result.data.relayerCnt);
        $(".zbrbl").text(result.data.course.relayScale+'%');
        $(".detailsTite").text(result.data.course.liveTopic);
        $(".detailsBanner").css({"background":"url('"+result.data.course.coverssAddress+"') no-repeat center center","background-size":"cover"});
          details = result.data.list;
        var html = '';
        $.each(details, function (i, item) {
          html += '<div class="newPubicsize claList">' +
                  '<i class="usdPic" style="background:url('+item.USER_PHOTO+') no-repeat center center; background-size:cover;" ></i>' +
                  '<div class="claRightbox">' +
                  '<div class="reversionTi">' +
                  '<i class="earTitle">'+item.USER_NAME+'</i>' +
                  '<p class="llM">'+item.TOTAL+'枣币</p>' +
                  '</div>' +
                  '<i class="earbot"><span><em class="xcmoney newxcmo">'+item.createTime+'</em></span> <span>付费人数:<em class="fumoney">'+item.PAY_COUNT+'</em></span></i>' +
                  '</div>'+
                  '</div>';
        });
        $(".allAmount").append(html);
        offset = $(".allAmount>.claList").length;
      }else{
        if(details.length<1){
          oBok = false;
          if(offset=="0"){
            $(".allAmount").append('<div class="noData"></div>');
          }
          return;
        }
      }
    }
  }

// 暂时不用
// function findCourseInfo(courseId){
//    window.location.href = "/weixin/courseRevenuedetails?courseId="+courseId;
//  }

</script>
</html>
