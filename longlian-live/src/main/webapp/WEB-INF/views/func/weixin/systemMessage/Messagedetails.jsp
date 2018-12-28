<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #f0f0f0">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>系统消息详情</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
</head>
<body>
    <div class="messdetalis">
        <p id="content"> ${msg} </p>
        <span class="messdetalistime" id="sendTime"></span>
    </div>
    <%--<a class="kfiphon" href="tell:133333333333">联系客服</a>--%>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script>

</script>
</html>
