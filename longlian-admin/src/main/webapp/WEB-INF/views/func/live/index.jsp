<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0"/>
    <title></title>
 <script type="text/javascript">
 
 </script>
</head>
<body>
 <div id="contentDiv">
<div id="users"></div>
  <div id="says" style="width:500px;height:600px"></div>
  <input type="text" value="" name="say" id="say"/>
  <input type="button"  value="发送" onclick="say()"/>
</div>

<script src="/web/res/live/NIM_Web_SDK_v3.3.0.js"></script>
 <script src='/web/res/live/NIM_Web_NIM_v3.3.0.js'></script> 
<script src='/web/res/live/NIM_Web_Chatroom_v3.3.0.js'></script>
<script src='/web/res/live/demo.js'></script>
<script src="/web/res/js/zepto.min.js"></script>
</body>
</html>


