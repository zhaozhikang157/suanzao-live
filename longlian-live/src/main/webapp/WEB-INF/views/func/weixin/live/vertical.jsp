<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#fff;">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="author" content="monicaqin">
	<meta name="format-detection" content="telephone=no" />
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<title>${liveTopic}</title>
	<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/liveVertical.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/video-js.min.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
	<script type="text/javascript" src="/web/res/js/jquery-1.7.2.js"></script>
	<script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/access-meta.js?nd=<%=current_version%>"></script>
	<script src='/web/res/js/filter.js?nd=<%=current_version%>'></script>
	<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
	<%--<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>--%>
	<script type="text/javascript">
		var courseId = '${courseId}';
		var isFollow = '${isFollow}';
        var _userId = '${userId}';
        var _userName = '${userName}';
        var _photo = '${photo}';
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
//		};
//		Torem();
//		$(window).resize(function() {
//			//改变窗体大小时适应浏览器高度
//			Torem();
//		});

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
<div class="bindingtap" style="display: none">
	<div class="bindbox">
		<div class="bindTitle">关闭直播间</div>
		<p class="bindtext">请问是否确认关闭直播间？</p>
		<ul class="bingthure">
			<li class="no">否</li>
			<li class="yes">是</li>
		</ul>
	</div>
</div>

<!-- 分享 -->
<div class="sharBox">
	<div class="sharbox"style="display: none">
		<div class="mask" style="display: none;height:auto;">
			<div class="bsf"><img src="/web/res/image/covers.png"></div>
			<p>请点击右上角</p>

			<p>通过【发送给朋友】</p>

			<p>邀请好友参与</p>
		</div>
		<div class="sharing" >
			<div class="sharing_box">
				<p class="wx"  onclick="statisticsBtn({'button':'005','referer':'005002','courseId':${courseId}})">微信</p>
				<p class="pyq" onclick="statisticsBtn({'button':'006','referer':'006002','courseId':${courseId}})">朋友圈</p>
				<p class="yqk" onclick="statisticsBtn({'button':'007','referer':'007002','courseId':${courseId}})">邀请卡</p>
			</div>
			<div class="sharing_btn">取消</div>
		</div>
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
<!-- 直播顶部 -->
<div class="video_top">
	<div style="overflow:hidden;">
	<!-- 老师信息 -->
    <div class="TeacherInfo">
    	<dl class="TeacherInfodl">
        	<dt class="TeacherImg" ><img src="/web/res/image/01.png"></dt>
            <dd>
            	<span class="TeacherName"></span>
            	<span class="TeacherCurrency"></span>
            </dd>
			<%--<span class="clickFollow"><img src="/web/res/image/photo_ricon.png" alt=""/></span>--%>
        </dl>
		<div class="clickFollowsS">

		</div>
    </div>
    <!-- 在线人数 -->
    <div class="Online_population">
    	<div id='population'>
			<div class="popuscrll">
				<div class="swiper-wrapper newpopulation">

				</div>
			</div>
        </div>
        <div id="Number">0人</div>
    </div>
    </div>
	<%--视频邀请码盒子--%>
	<div class="trialBox">
		<!-- 视频试看 -->
		<div class="trial" style="display:none;" onclick="coursePaying()"><span></span>购买后观看完整视频</div>
		<!-- 邀请码观看 -->
		<div class="invitationCode" style="display:none;left: 0;bottom:0">邀请码观看</div>
	</div>
    <!-- 打赏按钮 -->
    <div class="contributionList"></div>
	<%--水印--%>
	<div class="watermark" style="left: 0.75rem;top: 2.5rem;"></div>
</div>
<!-- 播放器 -->
<div class="videoBody">
	<div class="Downloading"><img src="/web/res/image/Downloading.png" alt=""/><span>网络有点问题哦~</span></div>
	<div class="BeginPlay" style="display:none;"><img src="/web/res/image/play3.png"></div>
	<video id="video" class="video-js"   preload="auto"  playsinline="true" webkit-playsinline="true" x-webkit-airplay="true" x5-video-ignore-metadata="true" style="background-image: url(${coverssAddress});background-size: cover;background-position: center;"   x5-video-player-type="h5" x5-video-player-fullscreen="true" >
		<%--<source src="${playAddress}"  type="application/x-mpegURL">--%>
	</video>
    <div class="AndroidMb"></div>
    <!-- 试看结束 -->
		<div class="trialEnd" style="display:none;" >
			<h2>试看结束,购买后观看完整视频</h2>
			<div class="purchase" onclick="coursePaying()"></div>
			<p class="invitationCode">邀请码观看</p>
		</div>
	<!-- 视频控件 -->
	<div class="videoKj" style="display:block;opacity:0">
		<!-- 视频暂停 -->
		<div class="testPause"><img src="/web/res/image/video_ico_2b.png"></div>
        <!-- 视频开始 -->
		<div class="BeginPlay2"><img src="/web/res/image/video_ico_1b.png"></div>
		<!-- 全屏播放 -->
		<!--div class="full_screen"><img src="/web/res/image/video_ico_3.png"></div-->
		<!-- 视频时间 -->
		<div class="video_timer">00:00:00</div>
		<!-- 播放进度条 -->
		<div class="pro_box">
			<div class="buffer"></div>
			<div class="pro"></div>
			<div class="bar"><span></span><i></i><em></em></div>
		</div>
        <div class="video_timer2">00:00:00</div>
	</div>
	<div class="pro_box_hide">
		<div class="buffer_hide"></div>
		<div class="pro_hide"></div>
	</div>
	<div class="background_bottom_a"></div>
     <!-- 视频回放 -->
    <!--div class="videoPlayback"><img src="/web/res/image/replay.png"></div-->
</div>
<!-- 视频直播聊天box -->
<div class="live_box">
	<!-- 内容区域 -->
	<div class="tabBox">
		<!-- 发送消息 -->
		<div class="send_message">
        	<ul class="messageIco1">
            	<li id="mesClose"><img src="/web/res/image/stopicon@3x.png"></li>
				<%--<li style="display:none;" id="clearScreen"><img src="/web/res/image/hcleanup@3x.png"></li>--%>
				<li id="spmore" style="display: none"><img src="/web/res/image/spmor.png"></li>
                <li id="mesCourseware" class="mesCoursewares"><img src="/web/res/image/courseware@3x.png"></li>
                <%--<li style="display:none;" id="adAddress"><a href="#"><img src="/web/res/image/The-shopping-cart_icon.png"></a></li>--%>
				<li id="dowLoad"><img src="/web/res/image/downApp.png"></li>
				<li id="mesShare"><img src="/web/res/image/share@3x.png"></li>
				<li id="mesGift"><img src="/web/res/image/liwu2.png"></li>
                <li id="mesNews"><img src="/web/res/image/conversation@3x.png"></li>
            </ul>
		</div>
        <div class="send_message_input">
            <input type="text" value="" placeholder="说点什么吧" name="say" id="say" />
            <input type="button" id="send"  value="发送"/>
        </div>
        <div class="GetInto" id="GetInto"><div>老师进入直播间</div><span></span></div>
		<div class="bd" id="bd_box">
			<!-- 聊天消息 -->
			<div class="says">
				<ul id="sayslist">
					<li id="megMore"></li>
				</ul>
			</div>
		</div>
	</div>
</div>
<!-- 视频课件 -->
<div class="courseware">
	<div id="toppic">
		<ul class="swiper-wrapper">

		</ul>
		<div id='tipClose' class="swiper-pagination toppic_pag"></div>
	</div>
	<div class="courseware_toggle"><img src="/web/res/image/courseware_toggle.png"></div>
</div>
<!-- 同层播放器BUG修复 -->
<div style="display:none;">
	<canvas id="test"/>
</div>

<div class="reward_box_bg"></div>
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
		<div class="Recharge" onclick="rexb()">充值 <img class="triangle" src="/web/res/image/triangle.png"></div>

		<!-- 余额 -->
		<div class="balance"><span id="balance">0</span> 学币</div>
		<!-- 打赏 -->
		<button class="aReward">打赏</button>
	</div>
</div>
<!--点击关注-->
<div class="clickFollow" >关注公众号</div>
<!-- 禁言 -->
<div id='gagbox'>
	<div class="gagbody">
    	<div class="gagbodyx"><img src="/web/res/image/dealPwdClose.jpg"></div>
		<div class="gagimg"><img id="gagimg" src="/web/res/image/gagbodyico.png"></div>
		<div class="gagname">我是爱学习的学生</div>
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
<%--下载App--%>
<div class="downApp">
	<div class="downLeftbox">
		<span class="downClose"></span>
		<p class="downLogo"></p>
		<p>下载酸枣App，观看超清视频</p>
	</div>
	<a class="downloadd"><span class="changea">立即使用</span></a>
</div>
<%--关注二维码--%>
<div class="new_ewmbox">
	<img class="sicx imgsrc"  alt="" style="position: absolute;z-index:1111;opacity: 0;width: 12.5rem;height: 12.5rem;margin-left: -6rem;left: 50%;top:4.3rem;"/>
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
<!-- 更多学生 -->
<div class="usersbox">
	<h3><span><img src="/web/res/image/czcloseX.png"></span>受教学生</h3>
    <div id='usersBox'>
        <ul>
           
        </ul>
    </div>
</div>
<%--更多功能--%>
	<ul class="moGn">
		<li class="newkj" id="mesCoursewaress">课件</li>
		<li class="newlj" id="adAddress">链接</li>
		<li class="newqp" id="clearScreen">清屏</li>
	</ul>
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
<script src='/web/res/live/liveVertical.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/tools.js?nd=<%=current_version%>'></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/weixin.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pay.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/form.js?nd=<%=current_version%>'></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script>
	var courseId = ${courseId};
	var timestamp=new Date().getTime();
	share_h5({systemType: 'COURSE', liveRoomId: '${courseId}'});//分享
//	如果是直播
	if(_isEnd == 0){
		$('.videoKj').remove();
	}
	$("#spmore").click(function(){
		$(".moGn").addClass("on")
	})
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
			$(".clickFollowsS").addClass("clk");
		}else{
			$(".clickFollowsS").removeClass("clk");
		}
		var starTime = BaseFun.GetStorage("starTime");
		var nowTime =new Date().getTime();
		if(nowTime-starTime>86400000||starTime==null){
			$(".downApp").css("display"," -webkit-box");
		}else{
			$(".downApp").hide();
		}
		//关闭二维码
		$(".qdBtn").click(function () {
			$(".ewmbox").removeClass("on");
			sessionStorage.setItem("qrCode",true);
			clearTimeout(timers);
		});
		//下载App
		$(".downloadd,#dowLoad").click(function(){
			statisticsBtn({'button': '024'});
			window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow" ;
		});
		//打开聊天框
		$('#mesNews').click(function(){
			$('.send_message_input').css({zIndex:'11111111',opacity:1});
			$('#say').focus();
		});
		//点击空白关闭聊天窗口
		$(document).on('click',function(e){
			var _con = $('.send_message_input,#mesNews');   // 设置目标区域
			if(!_con.is(e.target) && _con.has(e.target).length === 0){
				$('.send_message_input').css({zIndex:'-1',opacity:0});
				$('#say').blur();
			}
		});
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
		
		//显示课件
		$('#mesCourseware,#mesCoursewaress').click(function(){
			if(courselist.length == 0){
				pop({"content": "本次课程暂无课件" ,width:"8.5rem","status": "error", time: '1000'});
			}else{
				$(".moGn").removeClass("on");
				$('.courseware').show();	
			}
            statisticsBtn({'button':'012','referer':'','courseId':courseId });
		});
		
		//点击空白隐藏课件
		$(".videoBody").on('click',function(e){
			var _con = $('#mesCourseware');   // 设置目标区域
			if(!_con.is(e.target) && _con.has(e.target).length === 0){
				$('.courseware').hide();
			}
		});
		
		//隐藏课件
		$('.courseware_toggle,.videoBody').click(function(){
			$('.courseware').hide();
		});
	});
	

//点击分享
$("#mesShare").click(function () {
	$(".sharbox").show();
	statisticsBtn({'button':'004','referer':'004001','courseId':${courseId}})
});
	$(".wx").on("touchstart",function(){
		$(this).addClass("wx1");
		$(".mask").show();
		share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
	})
	$(".wx").on("touchend",function(){
		$(this).removeClass("wx1")
	})
	$(".pyq").on("touchstart",function(){
		$(this).addClass("pyq1");
		$(".mask").show();
		share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
	})
	$(".pyq").on("touchend",function(){
		$(this).removeClass("pyq1")
	})
$('.sharing_btn').click(function(){
	$('.sharbox').hide();
	$(".mask").hide();
});

$(".sharbox").bind("click",function(e){
	if(this == e.target){
		$(".sharbox").hide();
		$(".mask").hide();
	}
});
	$(".downClose").click(function(){
		$(".downApp").hide();
		BaseFun.SetStorage('starTime',timestamp);
	});
//appId : 该课程老师的ID 邀请卡
$(".yqk").click(function(){
	$('#video')[0].pause();
	$('.BeginPlay').show();
//	$('.sharBox').hide();
	window.location.href = "/weixin/inviCard?courseId=" + courseId +"&appId="+teacherId+"&seriesid="+sourcesId ;
})
	$(".yqk").on("touchstart",function(){
		$(this).addClass("yqk1");
	})
	$(".yqk").on("touchend",function(){
		$(this).removeClass("yqk1")
	})
//退出聊天室
$('#mesClose').click(function(){
	$(".bindingtap").show();
	statisticsBtn({'button':'013','referer':'','courseId':courseId });
	$(".yes").click(function(){
		$(".bindingtap").hide();
		window.location.href = "/weixin/courseInfo?id=" + courseId;
	})
});
//确定是否退出聊天室
$(".bingthure .no").click(function(){
	$(".bindingtap").hide();
});
	$(".cancel").click(function(){
		$(".rechartbox").hide();
		$("#pay").attr("disabled",false);
	})
	if(courseId.toString().length >= '15'){
		$(".invitationCode").remove();
	}
</script>
<%--<style>#__vconsole{top:0;z-index:111111199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}</style>--%>
</html>
