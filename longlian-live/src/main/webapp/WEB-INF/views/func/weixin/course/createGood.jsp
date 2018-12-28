<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>创建成功</title>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<div id="wrapper" style=" overflow-x:hidden; -webkit-overflow-scrolling: touch;">
  <div id="cont_box">
    <!--创建完成-->
    <div class="greatebox"></div>
    <div class="greatGoodbtn">
      <p class="curspan"></p>
      下载APP
    </div>
  </div>
</div>
</body>
<script>
  <%--var courseId = '${courseId}';--%>
  <%--var seriesCourseId = '${seriesCourseId}';--%>
  <%--share_h5({systemType: 'COURSE', liveRoomId: courseId , seriesid:seriesCourseId });//分享--%>
  $(".greatGoodbtn").click(function(){
        window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow"
  })
</script>
</html>
