<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#fff;">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="author" content="monicaqin">
	<meta name="format-detection" content="telephone=no" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/live.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
	<script type="text/javascript" src="/web/res/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="/web/res/js/jquery.cookie.js"></script>
	<script type="text/javascript" src="/web/res/socket.io/socket.io.js"></script>
	<script type="text/javascript" src="/web/res/socket.io/moment.min.js"></script>
	<script src='/web/res/js/filter.js?nd=<%=current_version%>'></script>
	<script type="text/javascript">
        function Torem(){
            var width = document.documentElement.clientWidth*20/375;
            var ohtml = document.getElementsByTagName("html")[0];
            ohtml.style.fontSize = width + "px";
        };
        Torem();
        var token = "${token}";
        var uuid = '${uuid}';
	</script>
</head>
<body>

<!-- 视频直播聊天box -->
<div class="live_box">
	<!-- 内容区域 -->
	<div class="tabBox">
		<!-- 发送消息 -->
		<div class="send_message">
			<div class="send_message_input">
				<input type="text" value="" name="say" id="say"/>
				<input type="button" id="send"  value="发送" onclick="say()"/>
				<input type="button" id="quiz"  value="提问"/>
			</div>
		</div>
		<div class="bd" id="bd_box">
			<!-- 聊天消息 -->
			<div class="says">

				<audio id="audio" autoplay></audio>
				<ul id="sayslist">
					<li id="megMore"></li>
				</ul>
			</div>
		</div>
	</div>
</div>





</body>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src='/web/res/live/demo.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/tools.js?nd=<%=current_version%>'></script>

<script src='/web/res/js/pay.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pop.js?nd=<%=current_version%>'></script>
<script>

</script>
</html>
