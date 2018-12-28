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
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<title>${liveTopic}</title>
	<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/live.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/video-js.min.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
	<script type="text/javascript" src="/web/res/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="/web/res/js/jquery.cookie.js"></script>
	<script src="/web/res/js/access-meta.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
	<script src='/web/res/js/filter.js?nd=<%=current_version%>'></script>
	<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
	<%--<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>--%>
	<script src='/web/res/js/fastclick.js?nd=<%=current_version%>'></script>
	<script type="text/javascript">
		var courseId = '${courseId}';
        var _userId = '${userId}';
        var _userName = '${userName}';
        var _photo = '${photo}';
		var isFollow = '${isFollow}';
		var invitationAppId = '${invitationAppId}';
		var sourcesId = '${sourcesId}';
        var _playAddress = '${playAddress}';
        var _coverssAddress = '${coverssAddress}';
        //试看时间
        var _trySeeTime = '${trySeeTime}';
		var _appKey = '${appKey}';
        var _roomAddr = ${roomAddr};
        var _yunxinToken = '${yunxinToken}';
        var _chatRoomId = '${chatRoomId}';
        var _isEnd = '${isEnd}';
		var isVerticalScreen = '${isVerticalScreen}'; //0-横屏 1-竖屏
		var _isRecorded = '${isRecorded}'; // 1是录播课
		var isCourseFollow =${isCourseFollow};//是否关注直播间

//        function Torem(){
//			var width = document.documentElement.clientWidth*20/375;
//			var ohtml = document.getElementsByTagName("html")[0];
//			ohtml.style.fontSize = width + "px";
//			winHeight = document.documentElement.clientHeight;
//			winWidth = document.documentElement.clientWidth;
//			if(winWidth>winHeight){
//				$('.reward2').hide();
//			}else{
//				$('.reward2').show();
//			}
//		};
//		Torem();
	</script>
</head>
<body  onkeydown="keySay();">
<div id="content_box">
<!-- 课程购买-->
<div class="paymentbox rechartbox" style="display: none">
	<div class="rechartpop">
		<img class="Jujubepic" src="/web/res/image/Jujube.png" alt=""/>

		<p id="confirm_pay"></p>

		<div class="choice">
			<span class="cancel">取消</span>
			<span class="purchaseTap" onclick="pay()" id="pay">确定</span>
		</div>
	</div>
</div>
<%--邀请码--%>
<div class="newinvitationBox">
	<div class="invitationInput">
		<h2>课程邀请码</h2>
		<div class="midInput"><input type="tel" class="courseInput"  maxlength="16"  placeholder="请输入课程邀请码"><p class="invBtn"></p></div>
		<p class="Taptxt"></p>
		<ul class="invitationUl">
			<li class="newcance">取消</li>
			<li class="newcha">确认</li>
		</ul>
	</div>
</div>
<%--身份验证--%>
<div class="useryz">
	<div class="sfyzbox">
		<h2>身份验证</h2>
		<ul class="room_inf">
			<li>
				<input placeholder="请输入手机号" validate="mobile" class="text mobile" name="mobile" type="text">
			</li>
			<li>
				<input placeholder="请输入验证码" validate="verification" class="text code" name="code" type="text">
				<div class="submitYzm">获取验证码</div>
			</li>
		</ul>
		<div class="errorMessages zzcan"></div>
		<p>
			<input type="button" class="rushe" value="取消" onclick="closeRegister();">
			<input type="button" class="clios bgcol_c80" value="确定">
		</p>
	</div>
</div>
<!-- 关注二维码 -->
<div class="ewmbox">
	<img class="sicx imgsrc"  alt="" style="position: absolute;z-index:1111;opacity: 0;width: 12.5rem;height: 12.5rem;margin-left: -6rem;left: 50%;top:4.3rem;"/>
	<div class="kfewm">
    	<div class="kfewm_img"></div>
        <div class="timerbox"><strong>距离开播时间</strong><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>天</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>时</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>分</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>秒</span></div>
		<div class="wxpic">
			<img  class="imgsrc" alt=""/>
			<i>长按关注将获得开课提醒</i>
		</div>
        <div class="qdBtn">我知道了</div>
	</div>
</div>
<%--充值--%>
<div class="rechargeBox">
	<div class="rechargeCont">
		<p class="contitle">充值<span class="conboxclose"></span></p>
		<div id="rechargeList">
			<div class="swiper-wrapper" id="rech">

			</div>
		</div>
		<p class="rechargeBtn" onclick="surePay()" id="surePay">确认充值</p>
		<div class="rechargeFoter">
			<span id="payBalance"></span>
			<span id="balanceText"></span>
			<span></span>
		</div>
	</div>
</div>
<%--打赏排行榜--%>
<div class="contributbox" style="display: none;">
	<div class="contributcon">
		<p class="contitle contitles">本节课贡献榜<span class="conboxclose"></span></p>
		<div class="contlistlength">
			<ul class="contlistbox">

			</ul>
		</div>
		<%--<div class="pullUpLabel">上拉加载更多</div>--%>
	</div>
</div>
<!-- 播放器 -->
<div class="video">
	<div class="Downloading"><img src="/web/res/image/Downloading.png" alt=""/><span>网络有点问题哦~</span></div>
	<div class="BeginPlay" style="display:none;"><img src="/web/res/image/play3.png"></div>
	<video id="video" class="video-js" poster="${coverssAddress}" style="background-image: url(${coverssAddress});background-size: 100% 100%;"  x5-playsinline="" playsinline="" webkit-playsinline=""  preload="auto">
		<%--<source src="${playAddress}"  type="application/x-mpegURL">--%>
	</video>
	<%--水印--%>
	<div class="watermark"></div>
	<div class="AndroidMb"></div>
        <!-- 视频试看 -->
        <%--<div class="trial" style="display:block;" onclick="coursePaying()"><span></span>购买后观看完整视频</div>--%>
        <!-- 邀请码观看 -->
        <%--<div class="invitationCode" style="display:none;">邀请码观看</div>--%>
    <!-- 试看结束 -->
    <div class="trialEnd" style="display:none;">
   		<h2>试看结束,购买后观看完整视频</h2>
        <div class="purchase" onclick="coursePaying()"></div>
        <p class="invitationCode">邀请码观看</p>
    </div>
    <!-- 视频空间 -->
	<div class="videoKj">
		<!-- 视频暂停 -->
		<div class="testPause"><img src="/web/res/image/video_ico_2b.png"></div>
        <!-- 视频开始 -->
		<div class="BeginPlay2"><img src="/web/res/image/video_ico_1b.png"></div>
		<!-- 全屏播放 -->
		<div class="full_screen"><img src="/web/res/image/video_ico_3b.png"></div>
		<!-- 视频时间 -->
		<div class="video_timer">00:00:00</div>
		<!-- 播放进度条 -->
		<div class="pro_box">
			<div class="buffer"></div>
			<div class="pro" style="width:0px;"></div>
			<div class="bar" style="left:0px;">
				<span></span><i></i><em></em>
			</div>
		</div>
		<div class="video_timer2">00:00:00</div>
	</div>
	<%--播放进度--%>

	<div class="pro_box_hide">
		<div class="buffer_hide"></div>
		<div class="pro_hide"></div>
	</div>
    <!-- 视频回放 -->
    <!--div class="videoPlayback"><img src="/web/res/image/replay.png"></div-->
    <!-- 播放进度2 -->
    <div class="pro_box2">
		<div class="buffer2"></div>
    	<div class="pro2" style="width:0px;"></div>
    </div>
</div>
<!-- 视频直播聊天box -->
<div class="live_box">
	<!-- 内容区域 -->
	<div class="tabBox">
		<!-- 发送消息 -->
		<div class="send_message">
			<div class="send_message_input">
            	<div class="sayBox">
                	<div class="preinput" id="pre"></div>
                	<textarea placeholder="说点什么吧"  name="say" id="say" cols="" rows=""></textarea>
                </div>
				<input type="button" id="send"  value="发送"/>
				<input type="button" id="quiz"  value="提问"/>
			</div>
		</div>
		<div class="test_play">
			<div class="test_play_content">
				<span class="trial_span"></span>购买后观看完整视频
				<!-- 视频试看 -->
				<div class="trial" onclick="coursePaying()">购买</div>
				<!-- 邀请码观看 -->
				<div class="invitationCode">邀请码</div>
			</div>
			<div class="test_tab_check">
				<span class="yqks" onclick="statisticsBtn({'button':'007','referer':'007002','courseId':${courseId}})"><img src="/web/res/image/-The-invitation-card_icon.png" alt=""/>邀请卡</span><span class="contributionList"><img src="/web/res/image/Contribution-to-the-list_icon.png" alt=""/>贡献榜</span><span class="clickFollow"><img src="/web/res/image/3Focus-on_icon.png" alt=""/>关注公众号</span>
			</div>
		</div>
		<div class="bd" id="bd_box">
			<!-- 聊天消息 -->
			<div class="says">
				<ul id="sayslist">
					<li id="megMore"></li>
				</ul>
			</div>
		</div>
	</div>
    <!-- 打赏榜单 -->
    <%--<div class="sizsAllbox">--%>
	<%--<p class="menus"><img src="/web/res/image/sdg.png"><span>菜单</span></p>--%>
	<%--<ul class="menuBox">--%>
		<%--<li class="coursewareBtn"><img src="/web/res/image/kjpic.png"><span>课 &nbsp;&nbsp;件</span></li>--%>
		<%--<li class="contributionList"><img src="/web/res/image/phbpic1.png"><span>贡献榜</span></li>--%>
        <%--<li class="clearScreen" style="display: none;"><img src="/web/res/image/cls@2x.png"><span>清 &nbsp;&nbsp;屏</span></li>--%>
		<%--<li class="rightBtns"><img src="/web/res/image/yhcpic.png"></li>--%>
	<%--</ul>--%>
<%--</div>--%>
</div>
<!-- 视频课件 -->
<div class="courseware">
	<div id="toppic">
		<ul class="swiper-wrapper">

		</ul>
		<div class="swiper-pagination toppic_pag" style=" padding:.3rem 0; color:#FFF;  background:url(/web/res/image/ico_bj.png) no-repeat center center; background-size:16%;"></div>
	</div>
</div>
<!-- 同层播放器BUG修复 -->
<div style="display:none;">
	<canvas id="test"/>
</div>
<!-- 二维码消息 -->
<!--div class="txMegs"><p>关注 <span id="getQR">「酸枣在线」</span> 公众号，接收关注老师的新课提醒，以及已购课程开播前10分钟提醒;</p>
	<div class="gbtxmegs"></div>
</div-->
<!--进入直播间 -->
<div class="barrage_box">
	<div class="user_right ">
			<em></em>
	</div>
</div>
<!-- 打赏box -->
<div class="reward_box" >
	<h6></h6>
	<div id="rewardList">
		<div class="swiper-wrapper">
			<%--<div class="swiper-slide on">--%>
                    <%--<img src="/web/res/image/reward_ico2.png">--%>
                    <%--<span>0.1学币</span>--%>
                <%--</div>--%>
		</div>
	</div>
	<div class="reward_bottom">
		<!-- 充值 -->
		<div class="Recharge" onclick="rexb()">充值<img class="triangle" src="/web/res/image/triangle.png"></div>
		<!-- 余额 -->
		<div class="balance"><span id="balance">0</span> 学币</div>
		<!-- 打赏 -->
		<div class="aReward">打赏</div>
	</div>
</div>

<!-- 禁言 -->
<div id='gagbox'>
	<div class="gagbody">
		<div class="gagbodyx"><img src="/web/res/image/dealPwdClose.jpg"></div>
		<div class="gagimg"><img id="gagimg" src="/web/res/image/gagbodyico.png"></div>
		<div class="gagname"></div>
		<div class="gaginfo"><span>关注:0</span><span>ID:0</span></div>
		<div class="gagbtn">
			<%--&lt;%&ndash;老师点击学生&ndash;%&gt;--%>
			<%--<div id="gag_jx" class="on">禁言</div>--%>
			<%--<div id="gag_tj" class="no">添加为管理员</div>--%>
			<%--&lt;%&ndash;学生点击老师&ndash;%&gt;--%>
			<%--<div id="gag_gz" class=""><i></i>关注Ta</div>--%>
			<%--<div id="gag_gb" class="">我知道了</div>--%>
		</div>
	</div>
</div>
<%--<div id='gagbox'>--%>
	<%--<div class="gagbody">--%>
    	<%--<div class="gagbodyx"><img src="/web/res/image/dealPwdClose.jpg"></div>--%>
        <%--<dl>--%>
        	<%--<dt><img id="gagimg" src="/web/res/image/gagbodyico.png"></dt>--%>
            <%--<dd>--%>
            	<%--<p><strong id="gagname">此课程禁言</strong></p>--%>
                <%--<p id="gagid">ID:132656</p>--%>
            <%--</dd>--%>
        <%--</dl>--%>
        <%--<h5>--%>
        	<%--<div class="Gag" onClick="Gag()">禁言</div><div class="RemoveGag" onClick="cancel()">解除禁言</div>--%>
            <%--<div class="Gagbox">--%>
            	<%--<div class="Gagnot">禁言</div>--%>
                <%--<div class="gagh6">对方为管理员无法禁言</div>--%>
            <%--</div>--%>

        <%--</h5>--%>
	<%--</div>--%>
<%--</div>--%>
<%--下载App--%>
<div class="downApp">
	<div class="downLeftbox">
		<span class="downClose"></span>
		<p class="downLogo"></p>
		<p>下载酸枣App，观看超清视频</p>
	</div>
	<a class="downloadd"><span class="changea">立即使用</span></a>
</div>
<!--点击关注-->
<%--<div class="clickFollow" >关注公众号</div>--%>
<%--关注二维码--%>
<div class="new_ewmbox" style="padding-top:10.5rem;">
	<img class="sicx imgsrc"  alt="" style="position: absolute;z-index:1111;opacity: 0;width: 12.5rem;height: 12.5rem;margin-left: -6rem;left: 50%;top:11.8rem;"/>
	<div class="kfewm">
		<div class="kfewm_img"></div>
		<h1 style="font-size: 0.75rem;color: #333;margin-top: 1rem;text-align: center">实时课程提醒</h1>
		<div class="wxpic">
			<img  class="imgsrc" alt=""/>
			<i>长按关注将获得开课提醒</i>
		</div>
		<div class="qdBtn">我知道了</div>
	</div>
</div>
<%--<div class="contributionList"></div>--%>
<%--<div class="coursewareBtn">课件</div>--%>
<!-- 打赏 -->
<div class="reward2" id="adv"></div>
<!--更多-->
<div class="more_btn"></div>
<!-- 广告链接 -->
<%--<a style="display:none;" id="adAddress"></a>--%>

<div class="more_btn_pop_bg">
	<div class="more_btn_pop_box">
		<div class="more_btn_pop_list">
			<span><img id="shar_btn_p" src="/web/res/image/1share_icon.png" alt=""/><br>分享</span>
			<span class="coursewareBtn"><img src="/web/res/image/The-courseware_icon.png" alt=""/><br>课件</span>
			<span class="clearScreens" style="display:none"><img src="/web/res/image/Clear-the-screen_icon.png" alt=""/><br>清屏</span>
			<span id="adAddresss" style="display:none"><img src="/web/res/image/link_icon.png" alt=""/><br>链接</span>
			<span class="downloadds"><img src="/web/res/image/download_icon.png" alt=""/><br>下载App</span>
			<span class="clickFollows"><br><i>关注</i></span>
		</div>
		<div class="more_btn_pop_close">取消</div>
	</div>
</div>

<!-- 分享 -->
<div class="sharBox">
	<div class="sharbox" style="display: none;">
		<div class="mask" style="display: none;height:auto;padding-top:7.5rem">
			<%--<div class="bsf"><img src="/web/res/image/covers.png"></div>--%>
			<p style="margin-top: 30%">请点击右上角</p>
			<p>通过【发送给朋友】</p>

			<p>邀请好友参与</p>
		</div>
		<div class="sharing">
			<div class="sharing_box">
				<p class="wx" onclick="statisticsBtn({'button':'005','referer':'005002','courseId':${courseId}})">微信</p>

				<p class="pyq" onclick="statisticsBtn({'button':'006','referer':'006002','courseId':${courseId}})">
					朋友圈</p>

				<p class="yqk" onclick="statisticsBtn({'button':'007','referer':'007002','courseId':${courseId}})">
					邀请卡</p>
			</div>
			<div class="sharing_btn">取消</div>
		</div>
	</div>
</div>
</div>
</body>
<script>
	//动态绑定video src   <source src="${playAddress}"  type="application/x-mpegURL">
	var src = '${playAddress}';
	var suffix = src.replace(/.m3u8/ig, '');
	var sourceDom = '';
	if(_isEnd == 0 && _isRecorded == 0){
		sourceDom = $("<source src=\""+ suffix +"@480p.m3u8\"  type=\"application/x-mpegURL\">")
		console.log(suffix+'@480p.m3u8')
		$("#video").append(sourceDom);
	}else{
		sourceDom = $("<source src=\""+ src +"\"  type=\"application/x-mpegURL\">")
		$("#video").append(sourceDom);
	}
	$('#video source').attr('src','https:'+$('#video source').attr('src').split(':')[1]);
</script>
<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js?nd=<%=current_version%>'></script>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/video.min.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/videojs-contrib-hls.js?nd=<%=current_version%>'></script>
<script src='/web/res/live/video.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pop.js?nd=<%=current_version%>'></script>
<script src='/web/res/live/live.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/tools.js?nd=<%=current_version%>'></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/weixin.js?nd=<%=current_version%>'></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/pay.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/form.js?nd=<%=current_version%>'></script>
<style>
	#video_html5_api{width: 100%;object-fit:initial}
	.barrage_box{

		width: 8.4rem;
		position: fixed;
		bottom:7.25rem;
		right: 0.5rem;
		z-index: 11116;
		transform: translateX(8.4rem);
	}
	.user_right{
		background: rgba(0, 0, 0, 0.5);
		height: 1.6rem;
		padding:0 0.7rem;
		box-sizing: border-box;
		line-height: 1.6rem;
		position: absolute;
		right: 0rem;
		top: 0rem;
		font-size:0.6rem;
		color: #ffffff;
		z-index: 111999;
		display: none;
		border-radius: 5px;
		transition: 1s;
	}
	._slideInRight{
		opacity: 1;
		transform:translateX(-8.4rem);
	}
	._fadeOut{
		opacity: 0;
		transform:translateX(-8.4rem);
	}
	._left{
		transform:translateX(0);
	}
</style>
<script>
	var timestamp=new Date().getTime();
	share_h5({systemType: 'COURSE', liveRoomId: '${courseId}'});//分享
	//多行输入
	var textarea = document.getElementById('say');
	var pre = document.getElementById('pre');
	textarea.onchange = textarea.onpropertychange = textarea.oninput = function() {
		var texp = textarea.value.split(/\r?\n/).length;
		pre.textContent = textarea.value;
		textarea.style.height = (pre.offsetHeight/20+(texp-1)*0.9) + 'rem';
	};

	/*课件*/
	var courseware = new Swiper('#toppic', {
		pagination: '.toppic_pag',
		paginationType: 'fraction',
		paginationClickable: true,
		autoplay:0,
		autoplayDisableOnInteraction:false,
		observer:true,
		observeParents:true
	});

$(function () {
	if(isCourseFollow){
		$(".clickFollows").addClass("on");
		$(".clickFollows").find("i").html("已关注");
	}else{
		$(".clickFollows").removeClass("on");
		$(".clickFollows").find("i").html("关注")
	}
	var starTime = BaseFun.GetStorage("starTime");
	var nowTime =new Date().getTime();
	if(nowTime-starTime>86400000||starTime==null){
		$(".downApp").css("display"," -webkit-box");
	}else{
		$(".downApp").hide();
	}

	//关闭禁言
	$('#gagbox').click(function(e){
	  var _con = $('.gagbody');   // 设置目标区域
	  if(!_con.is(e.target) && _con.has(e.target).length === 0){
		$('#gagbox').hide();
	  }
	});
	$('.gagbodyx').click(function(){
		$('#gagbox').hide();
	});
		
	//关闭二维码
	$(".qdBtn").click(function () {
		$(".ewmbox").removeClass("on");
		sessionStorage.setItem("qrCode",true);
		clearTimeout(timers);
	});
	//下载App
	$(".downloadd").click(function(){
		statisticsBtn({'button': '024'});
		window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow" ;
	});
	$(".downloadds").click(function(){
		$('.more_btn_pop_bg').hide();
		window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow" ;
	});
	//显示隐藏课件
	$('#say').focus(function(){
		$('.courseware').hide();
	});
	//关闭课件
	$(document).click(function(e){
	  var _con = $('.courseware,.sizsAllbox,.coursewareBtn');   // 设置目标区域
	  if(!_con.is(e.target) && _con.has(e.target).length === 0){
		$('.courseware').hide();
	  }
	});
	//显示课件
	$('.coursewareBtn').click(function(){
		$('.more_btn_pop_bg').hide();
		if(courselist.length == 0){
			pop({"content": "本次课程暂无课件" ,width:"8.5rem", "status": "error", time: '1000'});
		}else{
			$('.courseware').show();	
		}
		statisticsBtn({'button':'012','referer':'','courseId':courseId });
		return false;
	});	

	$('.more_btn_pop_bg').on('click',function(e){
		if(this == e.target){
			$(this).hide();
		}
	});
		$(".menus").click(function(){
		$(this).fadeOut(200);
		$(".menuBox").css("right","0");
	})
	$(".rightBtns").click(function(){
		$(".menuBox").css("right","-4rem");
		$(".menus").fadeIn(1500);
	});
});
	$(".cancel").click(function(){
		$(".rechartbox").hide();
		$("#pay").attr("disabled",false);
	});
	$(".downClose").click(function(){
		BaseFun.SetStorage('starTime',timestamp);
		$(".downApp").hide();

	});

	$('#shar_btn_p').on('click',function(){
		$('.more_btn_pop_bg').hide();
		$('.sharbox').show();
	});
	//点击分享
	$(".bjy").click(function () {
		$(".invite").show();
		statisticsBtn({'button': '004', 'referer': '004001', 'courseId':${courseId}})
	});
	$(".wx,.pyq").click(function () {
		$(".mask").show();
	})
	$('.sharing_btn').click(function () {
		$('.sharbox').hide();
		$(".mask").hide();
	});

	$(".sharbox").bind("click", function (e) {  //点击对象
		if (this == e.target) {
			$(".sharbox").hide();
			$(".mask").hide();
		}
	});
	$(".yqk,.yqks").click(function () {
//            if (isSeries == 0) {
//                seriesid = courseId;
//            }
//		$('.sharBox').hide();
		window.location.href = "/weixin/inviCard?courseId=" + courseId + "&appId=" + _userId + "&type=1&seriesid="+sourcesId;
	})

	//设置跳转页面
	$('.setup').click(function(){
		window.location.href="/weixin/directBroadcast?id="+courseId;
	});
	if(isFollow == '0'){
		//$('.txMegs').show();
	}else{
		//$('.txMegs').hide();
	}
	$("#getQR").click(function () {
		$(".ewmbox").addClass("on");
	});
	//        $('.gbtxmegs').click(function(){
	//            $('.txMegs').hide();
	//        });
	$('.ewmbox').on("click", function (e) {  //点击对象
		var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
		if (target.closest(".sicx,.kfewm,.getQR").length == 0) {
			$(".ewmbox").removeClass("on");
		}
	});
	//转播课隐藏邀请码
	if(courseId.toString().length >= '15'){
		$(".invitationCode").remove();
	}
</script>
<%--<style>#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}</style>--%>
</html>
