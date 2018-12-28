<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#fff;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content="" />
    <meta name="Description" content="" />
    <title>${liveTopic}</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/liveAudio.css?nd=<%=current_version%>" />
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/animate.css?nd=<%=current_version%>"/>
    <script type="text/javascript" src="/web/res/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="/web/res/js/jquery.cookie.js"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src='/web/res/js/filter.js?nd=<%=current_version%>'></script>
    <%--<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>--%>
    <script type="text/javascript">
        var courseId = '${courseId}';
        var oriCourseId = '${oriCourseId}';
        var oriAppId = '${oriAppId}';
        var oriRoomId = '${oriRoomId}';
        var isFollow = '${isFollow}';
        var _userId = '${userId}';
        var _userName = '${userName}';
        var _photo = '${photo}';
        var sourcesId = '${sourcesId}';
        var _coverssAddress = '${coverssAddress}';
        var _trySeeTime = '${trySeeTime}';
        var _appKey = '${appKey}';
        var _roomAddr = ${roomAddr};
        var _yunxinToken = '${yunxinToken}';
        var _chatRoomId = '${chatRoomId}';
        var _isEnd = '${isEnd}';
        var playEndAndLoadEnd = 0 ;
        var isCourseFollow =${isCourseFollow};//是否关注直播间
        $(function () {
            var u = navigator.userAgent;
            var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            var isPageHide = false;
//            if(isIOS){
//                window.addEventListener('pageshow', function () {
//                    if (isPageHide) {
//                        window.location.reload();
//                    }
//                });
//                window.addEventListener('pagehide', function () {
//                    isPageHide = true;
//                });
//            }else{
                var needRefresh = sessionStorage.getItem("need-refresh");
                if(needRefresh){
                    sessionStorage.removeItem("need-refresh");
                    location.reload();
                }
//            }
        });
    </script>
</head>
<body>
<div class="LoadingBox" style="display:block;">
    <div class="newloading">
        <div class="rect1"></div>
        <div class="rect2"></div>
        <div class="rect3"></div>
        <div class="rect4"></div>
        <div class="rect5"></div>
        <div class="rect6"></div>
    </div>
</div>
<div id="content_box">
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

<!-- 语音直播聊天box -->
<div class="live_box">
    <!-- 语音直播课件 -->
    <div class="live_video ">
        <!-- 设置 -->
        <!--<div class="setup"><img src="/web/res/image/setup2.png"></div>-->
        <div class="video_box  ">
            <div id="toppic">
                <ul class="swiper-wrapper">

                </ul>
                <div class="swiper-pagination toppic_pag" style="font-size:0.6rem;border-radius:5px; left:0.3rem;bottom:0.3rem; color:#FFF; background:rgba(0,0,0,0.48);width:1.8rem;height: 1.05rem;line-height: 1.05rem;"></div>
            </div>
        </div>
        <!--直播人数 直播状态-->
        <div class="live_userconut_box">
            <!-- 打赏榜单 -->
            <div class="contributionList"></div>
            <span class="live_user_conut"></span>
            <span class="live_status"></span>
            <span class="live_kj_toggle">收起</span>
        </div>
    </div>
    <!-- 内容区域 -->
    <div class="tabBox">
        <div class="bd  Transition_1s" id="bd_box">
            <!-- 聊天消息 -->
            <div class="says">
                <audio id="audio" preload="auto"  autoplay="autoplay"></audio>
                <audio id="audioNext" preload="auto"></audio>
                <ul id="sayslist">
                    <li id="megMore"></li>
                    <li style="display: none"  class="megLeft clearfix" id="sayslistOneData">
                        <div class="headImg"><img src="/web/res/image/newaudio/default@2x.png"></div>
                        <div class="messageMain">
                             <div class="messageBox Text"><p>欢迎您进入直播间!</p></div>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="zhanweiDiv" style="height:500px"></div>
        </div>
    </div>

    <!-- 发送消息 -->
    <div class="send_message">
        <!--老师发送语音和输入文字切换-->
        <div class="send_text_icon"></div>
        <div class="send_audio_icon"></div>
        <%--<div class="sound_bomb_close"></div>--%>
        <div class="send_message_input fontsize0">
            <span class="send_audio_btn">点击发送语音</span>
            <div class="presay"><textarea  name="say" id="say" placeholder="跟学生说点什么..."></textarea></div>
            <input type="button" id="send"  value="发送" />
            <input type="button" id="quiz"  value="提问"/>
            <!--查看提问-->
            <span class="questions_btn"></span>
            <!--发送图片-->
            <span id="send_img" class="send_img"></span>
        </div>
    </div>
    <div class="height_zw" style="height:11.3rem;display:none"></div>

    <!--老师录音弹框-->
    <div class="sound_bomb_box">
        <div class="sound_bomb_box_bg"></div>
        <div class="sound_center">
            <div class="sound_title_time">
                <div class="start_text">点击开始录音</div>
                <div class="audio_tiems">
                    <div class="loader-inner line-scale-pulse-out"><div></div><div></div><div></div><div></div></div>
                    <div class="times"><span class="time">01s</span>/ 60s</div>
                    <div class="loader-inner line-scale-pulse-out"><div></div><div></div><div></div><div></div></div>
                </div>
                <span class="sound_bomb_close"></span>
            </div>
            <div class="sound_btn_box sound_start"></div>
            <div class="sound_operation">
                <div class="sound_start_text"><i class="round"></i>单击模式</div>
                <div class="sound_suspend_text">点击停止录音</div>
                <div class="sound_end_text"><span class="cancel">取消</span></div>
            </div>
        </div>
    </div>
    <!--左侧定位-->
    <div class="back_active_positions animated ">点此回到播放位置</div>
</div>

<!-- 弹幕 提问弹出框 -->
<div class="barrage_quiz_common_box">
    <div class="barrage_quiz_common">
        <div class="tab_box">
            <span class="quiz_list_btn">问题</span>
            <span class="barrage_list_btn active">弹幕</span>
        </div>
        <div id="swiperBarrage">
            <div class="tab_div swiper-wrapper">
                <div class="item quiz_list_ul swiper-slide">

                </div>
                <div class="item barrage_list_ul swiper-slide">

                </div>
            </div>
        </div>
    </div>
</div>
<!--弹幕框-->
<div class="barrage_box">
    <!--进入直播间提示-->
    <div class="user_right ">
        <em></em> 进入直播间
    </div>
    <!-- 弹幕列表 -->
    <div class="barrage_list_box"></div>
</div>
<!-- 右侧弹幕 开关 -->
<div class="barrage"></div>
<!-- 打赏 -->
<div class="reward" ></div>
<!--点击关注-->
<div class="clickFollow">关注公众号</div>
<%--下载--%>
<div class="Group1"></div>
<%--下载--%>
<div class="clickLiveRoom"></div>
<!--切换极简模式-->
<%--<div class="clickBrief"></div>--%>
<!-- 老师邀请-->
<div class="invitation_flexd"></div>
<%--提问弹幕按钮--%>
<div class="quession"></div>
<!-- 右侧爬楼
<div class="back_box">
    <p id="back-to-top"></p>
    <p id="back-to-bottom"></p>
</div>-->
<!-- 确认提示框 -->
<div class="publicPopups" style="display: none">
    <div class="publicPopupsbox">
        <p class="publicPopupstitle">提示</p>
        <p class="publicPopupscont">是否为您结束本次直播?</p>
        <ul class="publicPopupsul">
            <li class="dialog-cancel">取消</li>
            <li class="dialog-confirm">确认</li>
        </ul>
    </div>
</div>

<%--下载App--%>
<div class="downApp">
    <div class="downLeftbox">
        <span class="downClose"></span>
        <p class="downLogo"></p>
        <p>下载酸枣App，收听无损音频</p>
    </div>
    <a class="downloadd"><span class="changea">立即使用</span></a>
</div>
<!-- 二维码消息 -->
<%--<div class="txMegs" style="top:10.5rem;"><p>关注 <span id="getQR">「酸枣在线」</span> 公众号，接收关注老师的新课提醒，以及已购课程开播前10分钟提醒;</p>--%>
    <%--<div class="gbtxmegs"></div>--%>
<%--</div>--%>
<!-- 打赏box -->
<div class="reward_box" >
    <h6></h6>
    <div id="rewardList">
        <div class="swiper-wrapper">

        </div>
    </div>
    <div class="reward_bottom">
        <!-- 充值 -->
        <div class="Recharge" onclick="rexb()">充值<img class="triangle" src="/web/res/image/triangle.png"></div>
        <!-- 余额 -->
        <div class="balance"><span id="balance">0</span> 学币</div>
        <!-- 打赏 -->
        <div class="aReward ">打赏</div>
    </div>
</div>
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
        	<%--<div class="Gag_red Gag" onClick="Gag()">禁言</div>--%>
            <%--<div class="Gag_red RemoveGag" onClick="cancel()">解除禁言</div>--%>
            <%--<div class="Gag_red addadmin" onClick="setAdmin()">添加管理员</div>--%>
            <%--<div class="Gag_red deleteadmin" onClick="deleteAdmin()">解除管理员</div>--%>
            <%--<div class="Gagbox">--%>
                <%--<div class="gagh6">对方为管理员无法禁言</div>--%>
            <%--</div>--%>
        <%--</h5>--%>
	<%--</div>--%>
<%--</div>--%>
<!-- 关闭直播间-->
<div class="closeLiveBtn"></div>
<!-- 关注二维码 -->
<div class="ewmbox">
    <img class="sicx imgsrc"  alt="" style="position: absolute;z-index:1111;opacity: 0;width: 12.5rem;height: 12.5rem;margin-left: -6rem;left: 50%;top:4.3rem;"/>
    <div class="kfewm">
        <div class="kfewm_img"></div>
        <div class="timerbox"><strong>距离开播时间</strong><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>天</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>时</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>分</span><img  src="/web/res/image/0.png"><img  src="/web/res/image/0.png"><span>秒</span></div>
        <div class="wxpic">
            <img  class="imgsrc" alt=""/>
            <i style="font-size:0.65rem;">长按关注将获得开课提醒</i>
        </div>
        <div class="qdBtn">我知道了</div>
    </div>
</div>
<!-- 分享 -->
<div class="sharBox">
    <div class="sharbox" style="display: none;z-index: 11199">
        <div class="mask" style="display: none;height:auto;">
            <div class="bsf"><img src="/web/res/image/covers.png"></div>
            <p>请点击右上角</p>

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


<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js?nd=<%=current_version%>'></script>
<script src="/web/res/js/fastclick.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/bscroll.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<%--<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>--%>
<script src='/web/res/live/liveAudio.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/tools.js?nd=<%=current_version%>'></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/weixin.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pay.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pop.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/form.js?nd=<%=current_version%>'></script>
<script>
    var timestamp=new Date().getTime();
    var balanceTwo=0;
    share_h5({systemType: 'COURSE', liveRoomId: '${courseId}'});//分享

//    文本框自动
    $.fn.autoTextarea = function(options) {
        var defaults={
            maxHeight:null,//文本框是否自动撑高，默认：null，不自动撑高；如果自动撑高必须输入数值，该值作为文本框自动撑高的最大高度
            minHeight:$(this).height() //默认最小高度，也就是文本框最初的高度，当内容高度小于这个高度的时候，文本以这个高度显示
        };
        var opts = $.extend({},defaults,options);
        // 兼容pc
        var curURL = window.location.href;
        if (/AppleWebKit.*Mobile/i.test(navigator.userAgent) || (/MIDP|SymbianOS|NOKIA|SAMSUNG|LG|NEC|TCL|Alcatel|BIRD|DBTEL|Dopod|PHILIPS|HAIER|LENOVO|MOT-|Nokia|SonyEricsson|SIE-|Amoi|ZTE/.test(navigator.userAgent))) {
            //手机端浏览器

        } else {
            $('#content_box').css({
                maxWidth: '520px',
                position: 'relative',
                margin:' 0 auto',
                height: '100%',
                left:'0',
                right:'0',
                overflow:'hidden'
            })
            $('html').css('font-size','24px');
            $('.barrage,.reward,.barrage_box,.quession').css('position','absolute');
            $('.barrage_quiz_common_box').css({
                right:0,
                margin:'0 auto',
                maxWidth: '520px',
                overflow:'hidden'
            });
            $('.reward_box').css({
                right:0,
                margin:'0 auto',
                width:'520px'
            })
            $('.Recharge').remove();
            $('.balance').css('margin-left','.4rem');
        }
        return $(this).each(function() {
            $(this).bind("paste cut keydown keyup focus blur",function(){

                var height,style=this.style;
                this.style.height =  opts.minHeight + 'rem';
                var ohtml = document.getElementsByTagName("html")[0];
                var size = parseFloat(ohtml.style.fontSize);
                if ((this.scrollHeight/size).toFixed(2) > opts.minHeight) {
                    if (opts.maxHeight && (this.scrollHeight/size).toFixed(2) > opts.maxHeight) {
                        height = opts.maxHeight;
                        style.overflowY = 'scroll';
                    } else {
                        height = (this.scrollHeight/size).toFixed(2);
                        style.overflowY = 'hidden';
                    }
                    style.height = height  + 'rem';
                }
            });
        });
    };

    $("#say").autoTextarea({
        maxHeight:2.35,//文本框是否自动撑高，默认：null，不自动撑高；如果自动撑高必须输入数值，该值作为文本框自动撑高的最大高度
        minHeight:0.825//默认最小高度，也就是文本框最初的高度，当内容高度小于这个高度的时候，文本以这个高度显示
    });

    /*充值*/
    //点击充值
    var isMobile = '0'; // 0:没有手机号 1:有手机号
    var balance = 0;
    var payId = 0;
    var payAmount = 0;


    window.wxReadCallback = function() {
//        WeixinJSBridge.invoke('closeWindow',{},function(res){
//            wx.stopRecord({
//                success: function (res) {
//                },
//                fail: function (res) {
//                }
//            });
//        });
    }

    /*选择充值金额*/
    function size(that){
        that.addClass("on").find(".firstSpan").addClass("on");
        that.parent().siblings().find("p,.firstSpan").removeClass("on");
        var id = that.attr("typeId");
        var amount =that.attr("typeAmount");
        payId = id;
        payAmount = amount;
    }

    $(".conboxclose").click(function(){
        $(".rechargeBox").hide();
        $("#rech").empty();
        $("#balanceText").empty();
        payId = 0;
        payAmount = 0;
    })

    $(".rechargeBox").bind("click",function(e){  //点击对象
        var target  = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if(target.closest(".rechargeCont").length == 0){
            $(".rechargeBox").hide();
            $("#rech").empty();
            payId = 0;
            payAmount = 0;
        }
    });


    $(function(){
        if(isCourseFollow){
            $(".clickLiveRoom").addClass("on");
        }else{
            $(".clickLiveRoom").removeClass("on");
        }
//        $('.invitation_flexd').on('click',function(){
//            $('.sharbox').show();
//        })
        var starTime = BaseFun.GetStorage("starTime");
        var nowTime =new Date().getTime();
        if(nowTime-starTime>86400000||starTime==null){
            $(".downApp").css("display"," -webkit-box");
        }else{
            $(".downApp").hide();
        }
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
            var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
            if (target.closest(".sharing").length == 0) {
                $(".sharbox").hide();
                $(".mask").hide();
            }
        });
        $(".invitation_flexd").click(function () {
//            if (isSeries == 0) {
//                seriesid = courseId;
//            }
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
    });
    //下载App
    $(".downloadd,.Group1").click(function(){
        statisticsBtn({'button': '024'});
        sessionStorage.setItem("need-refresh", true);
        window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow" ;
    });
</script>
<%--打赏排行榜--%>
<script>
    var Android = null;
    var ios = null;
    if(navigator.userAgent.toLowerCase().indexOf('iphone')!=-1){
        //ios
        ios = true;
    }else{
        //安卓
        Android = true;
        var wechatInfo = navigator.userAgent.match(/TBS\/([\d\.]+)/i) ;
        if (wechatInfo && wechatInfo.length > 1 && wechatInfo[1] < "036849" ) {
            alert("请升级您的微信版本，或使用小窗模式") ;
        }
    }
    var courseId = ${courseId};
    var offset = 0;
    $(function(){
//        getUserRewardSort();
        /*直播 精选 下拉加载*/
        $('.contlistlength').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('.contlistbox').height();
            if( scrollTop >0){
                Load();
            }
        });
    });

    var oBok = true;
    function Load() {
        if (oBok) {
            getUserRewardSort();
            /****   ---DOM  改变后重新计数高度 --- ****/
        } else {
            oBok = false;
        }
    }

    function getUserRewardSort(){
        var offset = $(".contlistbox li").length;
        var result = ZAjaxRes({url: "/userRewardRecord/getUserRewardSort" ,type:"POST" ,param:{courseId : courseId ,offset  : offset } });
        if(result.code == "000000"){
            var data = result.data;
            $.each(result.data, function (i, item){
                var obj = data[i];
                var sort = offset + i + 1;
                var sortDesc = "";
                var temp = "";
                if(sort == 1){
                    sortDesc = "goldmedal";
                } else if(sort == 2){
                    sortDesc = "silvermedal";
                }else if(sort == 3){
                    sortDesc = "bronzemedal";
                }
                if(sort > 3){
                    temp = '<i class="goldmedal">' + sort + '</i>';
                }else{
                    temp =  ' <i class="goldmedal" style="background: url(/web/res/image/' +sortDesc + '.png) no-repeat center center;background-size: 100% 100%"></i>';
                }
                var li ='<li> '+
                        temp +
                        '<i class="conuespic" style="background: url(' + obj.photo + ') no-repeat center center;background-size: 100% 100%"></i>'+
                        '<p>'+
                        '<span style="width:8rem;overflow: hidden;text-overflow: ellipsis;white-space:nowrap;margin-bottom:0 "  class="conusetitle">' + obj.name + '</span>'+
                        '<span class="contriBution">贡献值:' + obj.amount + '学币</span>'+
                        '</p>'+
                        '</li>';
                $(".contlistbox").append(li);
            });
        }else{
            if(result.code=="000110"){
//            $('.pullUpLabel').show();
//            $('.pullUpLabel').text('加载已经完毕！');
                //如果没有了 oBok 设false
                oBok = false;
                if(offset=='0'){
                    $('.contlistbox').append('<div class="noData"></div>');
//                $('.pullUpLabel').hide();
                }
                return;
            }
        };
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
        $('.Gagbox').hide();
	});

    $(".contributionList").click(function(){
        $(".contributbox").show();
        var liveVideoH = $('.live_video ').height();
        if(liveVideoH > 200){
            $('.contributcon').css('top',$('.live_video').height()-$('.live_userconut_box').height());
        }else{
            $('.contributcon').height('22.8rem');
        }
        $('.contlistlength').animate({scrollTop:0},20)
        getUserRewardSort();
        statisticsBtn({'button':'015','referer':'','courseId':${courseId}})
    })
    $(".conboxclose").click(function(){
        $(".contributbox").hide();
        $(".contlistbox").empty();
        oBok = true;
    })


    $(".contributbox").bind("click",function(e){  //点击对象
        var target  = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if(target.closest(".contributcon").length == 0){
            $(".contributbox").hide();
            $(".contlistbox").empty();
            oBok = true;
        }
    });
    /*确认充值*/
    function surePay(){
        //验证手机号
        if(isMobile != '1'){
            $(".useryz").show();
            return;
        }
//        console.log(payAmount);
//        console.log(payId);
        var param = {payType: "14", amount: payAmount, payTypeId: payId, deviceNo: ""};
        var result = rechargePay(param);
        onBridgeReady(result);
    }
    function getBalance() {
        var result = ZAjaxRes({url: "/llaccount/getAccountBalance.user", type: "get"});
        if (result.code == "000000") {
            if(result.data==""){
                return "0.00";
            }else{
                return result.data;
            }
        }
    }
    /**
     *微信支付
     */
    var orderId = 0;
    function onBridgeReady(result) {
        if (!result) return;
        var data = result.data;
        orderId = result.ext;
        WeixinJSBridge.invoke(
                'getBrandWCPayRequest', {
                    "appId": data.appId,     //公众号名称，由商户传入
                    "timeStamp": data.timeStamp,         //时间戳，自1970年以来的秒数
                    "nonceStr": data.nonceStr, //随机串
                    "package": data.package,
                    "signType": data.signType,         //微信签名方式:
                    "paySign": data.paySign    //微信签名
                },
                function (res) {
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        var result = {
                            code: "000000",
                            message: "充值成功"
//							"successUrl": "return_back",
                            //                     "successUrl": "/weixin/index.user?courseId=" + courseId + "&invitationAppId="+invitationAppId
                        };
                        sys_pop(result);
                        var balance = getBalance();
                        $("#payBalance").text("余额:"+balance);
                    } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                        var result = {code: "000000", message: "取消充值"};
                        var param = {orderId: orderId};
                        cancelThirdPay(param);
                        sys_pop(result);
                    } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                        var param = {orderId: orderId};
                        cancelThirdPay(param);
                    }
                    // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。         吗没呢，                                ，
                }
        );
    }

    //验证步骤
    $('.useryz .text').on("keyup change", function () {
        var obj = $('.useryz .text');
        var obj2 = $('.clios')
        valT(obj, obj2);
    });
    //点击发送验证码
    $(".submitYzm").click(function () {
        //验证手机号
        $('.errorMessages:visible').html('');
        var num = inputTest($('.mobile'));
        var mobile = $('.mobile').val();
        var result = ZAjaxJsonRes({url: "/user/getApplySms.user?mobile=" + mobile, type: "GET"});
        if (result.code != "000000") {
            $('.errorMessages:visible').html("请输入手机号");
            return;
        }
        if (num && result.code == "000000") {
            //请求发送成功后
            $(".submitYzm").addClass('not_pointer');
            $('.submitYzm').html('发送成功');

            var timer = null;
            timer = setTimeout(function () {
                //60秒重新发送
                var oT = 60;
                timer = setInterval(function () {
                    oT--;
                    $('.submitYzm').html(oT + '秒后重发');
                    if (oT == 0) {
                        clearInterval(timer);
                        $('.submitYzm').html('获取验证码');
                        $(".submitYzm").removeClass('not_pointer');
                    }
                }, 1000);
            }, 500);
        }
    });
    $(".clios").click(function () {
        registerMobile();
        $(".room_inf li input").val("");
    });
    $(".downClose").click(function(){
        $(".downApp").hide();
        BaseFun.SetStorage('starTime',timestamp);
    })
    /**
     * 注册手机号
     */
    function registerMobile() {
        var mobile = $("input[name='mobile']").val();
        var param = {mobile: mobile, checkCode: $("input[name='code']").val()};
        var result = ZAjaxRes({url: "/user/registerMobile.user", type: "POST", param: param});
        if (result.code == "000000") {
            sys_pop(result);
            $(".buyclassbtns").attr("mobile", mobile);//设计点击按钮可以购买
            $(".useryz").hide();
            isMobile = '1';
        } else {
            $('.errorMessages:visible').html(result.message);
        }
    }
    /*关闭验证手机号弹窗*/
    function closeRegister() {
        $(".useryz").hide();
    }
    window.onload=function(){
        setTimeout(function(){
            $(".LoadingBox").hide();
        },300);
    };
</script>


<%--<style>#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}</style>--%>
</html>
