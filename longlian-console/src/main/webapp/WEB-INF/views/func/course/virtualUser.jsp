<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content="" />
    <meta name="Description" content="" />
    <title>${name}</title>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/live.css" />
	<script type="text/javascript">
    	var courseId = '${courseId}';
        var userId = '${userId}';
        var isInRoom = '${isInRoom}';
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
            <li id="megMore"><h6></h6></li>
            
        </ul>
    </div>
    <div>
        <input type="text" value="" name="say" id="say"/>
        <input type="button" id="send"  value="发送"/>
        <input type="button" id="quiz"  value="提问"/>
        <%--<input type="button" class="reward"  value="打赏"/>--%>
    </div>
    <!-- 打赏box -->
    <div class="reward_box" >
        <h5><span><img src="/web/res/image/reward_ico3.png"></span>请选择打赏金额</h5>
        <h6></h6>
        <div id="rewardList">
            <div class="swiper-wrapper">
                <%-- <div class="swiper-slide on">
                     <img src="/web/res/image/reward_ico2.png">
                     <span>0.1学币</span>
                 </div>--%>
            </div>
        </div>
        <div class="reward_bottom">
            <!-- 充值 -->
            <!--div class="Recharge">充值</div-->
            <!-- 余额 -->
            <div class="balance"><img src="/web/res/image/reward_ico1.png"><span id="balance">0</span></div>
            <!-- 打赏 -->
            <div class="aReward">打赏</div>
        </div>
    </div>
</div>
           
</body>
<script src="/web/res/js/jquery-1.7.2.js"></script>
<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js"></script>
<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js'></script>
<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js'></script>
<script src='/web/res/js/func/course/virtualUser.js'></script>

</html>