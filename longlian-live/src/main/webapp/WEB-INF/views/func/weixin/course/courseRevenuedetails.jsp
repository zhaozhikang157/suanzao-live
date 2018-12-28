<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>课程收益详情</title>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style=" overflow: auto; height:100%;">
  <div id="cont_box" style="padding-bottom: 0">
    <div class="courdetails" style="min-height: 34rem;padding:0 0.75rem" id="buyCourseName">

    </div>
  </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
  var offset = 0;
  var courseId = '${courseId}';
  $(function(){
    Load();
    /*直播 精选 下拉加载*/
    $('#wrapper').scroll(function(){
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('#cont_box').height();
      if(scrollTop + windowHeight >= scrollHeight-1){
        Load();
      }
    });
  });
  var oBok = true;
  function Load() {
    if (oBok) {
      var result = ZAjaxRes({url: "/llAccountTrack/findMyCourseProfit.user?offset="+offset+"&courseId="+courseId});
      if (result.code == "000000") {
        var html = '';
        $.each(result.data, function (i, item) {
          html +='<div class="courdetailslist">' +
                  '<div><i class="courdTitle" id="userName">'+item.name+'</i>' +
                  '<p class="midcourd"><i class="courdTime" id="createTime">'+item.createTime+'</i>' +
                  '<i class="courdTime courdtype"></i>'+
                  '</p></div>'+
                  '<span id="amount"> +'+item.amount+' 枣币</span>' +
                  '</div>';
        });
        offset = parseInt(result.data.length)+offset;
        $("#buyCourseName").append(html);
      }else{
        sys_pop(result);
        if(result.data.length<1){
          $('.pullUpLabel').show();
          //如果没有了 oBok 设false
          oBok = false;
          return;
        }
      }
    } else {
      sys_pop(result);
      if (!oBok) {
        $('.pullUpLabel').show();
      }
    }
  }

</script>
</html>
