<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content="" />
    <meta name="Description" content="" />
    <title>龙链在线教育首页</title>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/live.css" />
	<script type="text/javascript">
    	var courseId = '${courseId}';
    </script>
</head>
<body>
<!-- 语音直播 -->
<!--div class="video_box">
    <div id="toppic2">
        <ul class="swiper-wrapper">									
            
        </ul>
    </div>
</div-->

<div class="item nteraction" style="display:block;">
	<div class="cylist">
    	<ul id="cylist">
        </ul>
    </div>
    <div class="says">
        <audio id="audio"  autoplay></audio>
        <ul id="sayslist">
            <li id="megMore"><h6>查看更多历史记录</h6></li>
            
        </ul>
    </div>
    <div class="qj">
    	<!--input name="" id="allgag" type="button" value="全部禁言" style="padding-left: 3px">
        <input name="" id="allcancel" type="button" value="全部解除禁言" style="padding-left: 3px"-->
        <input name="" id="truncation" type="button" value="掐流" style="padding-left: 3px;display: none">
        <input name="" id="resume" type="button" value="恢复直播流" style="padding-left: 3px;display: none">
    </div>
</div>
           
</body>
<script src="/web/res/js/jquery-1.7.2.js"></script>
<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js"></script>
<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js'></script>
<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js'></script>
<script src='/web/res/js/func/course/voice.js'></script>

</html>