<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
  <%@include file="/WEB-INF/views/include/header.jsp" %>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>tab</title>
  <script type="text/javascript" src="${ctx}/web/res/js/tabs.js"></script>
  <script>
    function doInit(){
      $.addTab("tabs","tabs-content",{url:contextPath+"/ordersController/toCheckRecordByAuditStatusPass",title:"已通过",active:true});
      $.addTab("tabs","tabs-content",{url:contextPath+"/ordersController/toCheckRecordByAuditStatusNoPass",title:"未通过",active:false});
    }

  </script>
</head>
<body onload="doInit()"  style="overflow:hidden">
<div id="test">
  <div id="tabs" class="base_layout_top"></div>
  <div id="tabs-content" class="base_layout_center"></div>
</div>
</body>
</html>
