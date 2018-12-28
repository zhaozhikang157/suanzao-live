<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #f7f7f7">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>转播收益</title>
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
    <div class="myRevenue">
      <span class="revenueAllmony">转播总收益(枣币)</span>
      <span id="revenuebalance">0.00</span>
    </div>
    <div id="myRevbox" style="overflow-y: scroll;">
      <div class="allAmount" style="background: white">

      </div>
    </div>
  </div>
</div>
</body>
<script>
  var offset = 0;
  $(function () {
    Load();
    /*直播 精选 下拉加载*/
    $('#myRevbox').scroll(function () {
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('.allAmount').height();
      if (scrollTop + windowHeight >= scrollHeight - 10) {
        Load();
      }
    });
  });

  var oBok = true;
  function Load() {
    if (oBok) {
      var result = ZAjaxRes({url: "/liveRoom/income/relay/course/list.user?offset="+offset});
      if (result.code == "000000") {
        var myRevenue = result.data.list;
        $("#revenuebalance").text((result.data.total).toFixed(2));
        var html = '';
        var liveway ="";
        $.each(myRevenue, function (i,item) {
          if(item.liveWay=="1"){
            liveway="livewaya"
          }else if(item.liveWay=="0"){
            liveway="livewayv"
          }else{
            liveway=""
          }
          html += '<div class="newPubicsize claList" onclick="findCourseInfo('+item.ID+')">' +
                  '<i class="clasPic" style="background:url('+item.COVERSS_ADDRESS+') no-repeat center center; background-size:cover;" ><em class="'+liveway+'"></em></i>' +
                  '<div class="claRightbox">' +
                  '<div class="reversionTi">' +
                  '<i class="earTitle">'+item.LIVE_TOPIC+'</i>' +
                  '</div>' +
                  '<i class="earbot"><span>总收益:<em class="xcmoney">'+item.INCOME_TOTAL+'</em>枣币</span> <span>转播人数:<em class="fumoney">'+item.RELAYER_CNT+'</em></span></i>' +
                  '</div>'+
                  '</div>';
        });
        offset = parseInt(result.data.list.length)+offset;
        $(".allAmount").append(html);
      }else{
        if(result.data.list.length<1){
          oBok = false;
          if(offset=="0"){
            $(".allAmount").append('<div class="noData"></div>');
          }
          return;
        }
      }
    }
  }

  function findCourseInfo(courseId){
    window.location.href = "/weixin/detailsReturn?courseId="+courseId+"&zb=0";
  }

</script>
</html>
