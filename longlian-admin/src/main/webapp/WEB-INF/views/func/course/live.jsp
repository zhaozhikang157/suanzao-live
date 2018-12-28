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
    <script type="text/javascript" src="//g.alicdn.com/de/prismplayer/1.5.6/prism-min.js"></script>
    
     <script type="text/javascript">
     	var courseId = '${courseId}';
     </script>
</head>
<body>
<div class="video_box">
    <div id="J_prismPlayer" class="prism-player"></div>

<script>
// 初始化播放器
function video(src){
	var player = new prismplayer({
		id: "J_prismPlayer", // 容器id
		source: src,// 视频地址
		autoplay: true,    //自动播放：否
		width: "100%",       // 播放器宽度
		height: "400px"      // 播放器高度
	});
	player.on("pause", function() {
		
	});
};
//var clickDom = document.getElementById("J_clickToPlay");
//	clickDom.addEventListener("click", function(e) {
//	// 调用播放器的play方法
//		player.play();
//	});
//	// 监听播放器的pause事件
	
    
</script>
</div>

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

        <!--input name="" id="allgag" type="button" value="全部禁言" >
        <input name="" id="allcancel" type="button" value="全部解除禁言" -->
        <input name="" id="truncation" type="button" value="掐流" >
        <input name="" id="resume" type="button" value="恢复直播流" >

    </div>
</div>
           
</body>
<script src="/web/res/js/jquery-1.7.2.js"></script>
<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js"></script>
<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js'></script>
<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js'></script>
<script src='/web/res/js/func/course/voice.js'></script>
<script>
	
	
</script>
</html>