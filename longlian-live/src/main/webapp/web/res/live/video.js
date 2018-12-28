//var vConsole = new VConsole();
//安卓或者ios
var Pc = null;
var Android = null;
var ios = null;
var liveVideo = false;
//  电脑端播放视频
if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
    //pc
    Pc = true;
    //如果是直播页面live = 1
    if(GetQueryString('live') != 1){
        liveVideo = true;
    }else{
        liveVideo = false;
        $('#mesShare').remove();
        $('.tabBox .bd').css('width','103%');
    }
    $(document.body).css({'position': 'relative','maxWidth': '534px','margin': '0 auto'});
    $('.messageIco1').css('width','16.75rem');
    $('#video source').attr('src','https:'+$('#video source').attr('src').split(':')[1]);
    $('.BeginPlay').remove();
    $('.videoKj').remove();
    $('.yqks').hide();
    $('.Recharge').remove();
    $('.balance').css('margin-left','.7rem')
    $('.more_btn,.reward2').css('position','absolute');
    $('.video_html5_api').css('object-fit','initial');
    if(typeof (isVerticalScreen) != undefined){
        if(isVerticalScreen == '1'){
            $('#video').css('width','100%');
            $('.video_top').css({'width':'100%','boxSizing':'border-box'});
        }else{

            $('.AndroidMb').css('max-height','420px');
            $('.live_box').css('top', $('.AndroidMb').height()+'px');
        }
    }
    var myPlayer = videojs('video',{
        "controls": true,
        "autoplay": true,
        "preload": "auto",
        "loop": true,
        controlBar:liveVideo
    });
    myPlayer.play();
    if(isVerticalScreen == 0)
        $('.video-js .vjs-control-bar').css({'opacity':1,'zIndex':1111115 ,bottom:'.5rem'})
    else
        $('.video-js .vjs-control-bar').css({'opacity':1,'zIndex':1111115 })
}else{
    $('.more_btn,.reward2').css('position','fixed');
}
if(navigator.userAgent.toLowerCase().indexOf('iphone')!=-1){
    //ios
    ios = true;
    $('.AndroidMb').hide();
}else{
    //安卓
    Android = true;

}
$(".contributionList").click(function(){
    $(".contributbox").show();
    getUserRewardSort();
    //statisticsBtn({'button':'015','referer':'','courseId':${courseId}});
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
//****************************播放跟着动 横竖屏通用******************************//
var oV = document.querySelector('#video');
var oProBox = document.querySelector('.pro_box');
var oPro = document.querySelector('.pro');
var oBar = document.querySelector('.bar');
var oTimer = document.querySelector('.video_timer');
var oTimer2 = document.querySelector('.video_timer2');
var videoKj = document.querySelector('.videoKj');
var oProBox2 = document.querySelector('.pro_box2');
var oPro2 = document.querySelector('.pro2');
var pro_box_hide = document.querySelector('.pro_box_hide');
var timerKj = null;
var timerV = null;
var ztOK = true;
//视频缓冲进度
function getBuffered() {
    //console.log('获取当前缓冲进度');
    var buffered = oV.buffered, loaded;
    if (buffered.length) {
        // 获取当前缓冲进度
        loaded = 100 * buffered.end(0) / oV.duration;
        // 渲染缓冲条的样式
        $('.buffer_hide,.buffer,.buffer2').css("width", loaded + "%");
        //console.log('获取当前缓冲进度',loaded+3);
    }
    setTimeout(getBuffered, 50);
}
//播放跟着动*******播放跟着动
oV.ontimeupdate=function(){
    var scale = oV.currentTime/oV.duration;
    var buffer_hide = $('.buffer_hide').width();  //获取预加载长度
    var pro_hide = $('.pro_hide').width();    //获取当前播放长度
    if(_trySeeTime == 0){
        if(oV.currentTime != 0 && endTime){
            localStorage.setItem("currentTime"+_chatRoomId,oV.currentTime);
        }
    }
    if(_trySeeTime != 0 && TeacherFrom != _userId){
        if(isVerticalScreen ==1){  //判断横竖屏回放试看
            if(oV.currentTime>_trySeeTime){
                $('.trial,div.invitationCode').hide();
                $('.trialEnd').show();
                //视频暂停
                ztOK = false;
                testPause();
                $('#video').attr('src','');
                $('.BeginPlay').remove();
                $('.videoKj').remove();
                return;
            }
        }else{
            if(oV.currentTime>_trySeeTime){
                $('.test_play_content').remove();
                $('.trialEnd').show();
                //视频暂停
                ztOK = false;
                testPause();
                $('#video').attr('src','');
                $('.BeginPlay').remove();
                $('.videoKj').remove();
                BaseFun.SetSession('trysee_Time'+_chatRoomId,'true');
                window.location.reload();
            }
        }
    }

    if(scale>0){
        if(isVerticalScreen == '1'){
            oTimer.innerHTML = formatTime(oV.currentTime,0);
            oTimer2.innerHTML = formatTime(oV.duration,1);
        }else{
            oTimer.innerHTML = formatTime(oV.currentTime,0);
            oTimer2.innerHTML = formatTime(oV.duration,1);
            //oTimer.innerHTML = formatTime(oV.currentTime)+'/'+ formatTime(oV.duration);
            oPro2.style.width = oProBox2.offsetWidth*scale+'px';
        }
    } else {

    }
    oPro.style.width = oProBox.offsetWidth*scale+'px';
    oBar.style.left = scale*(oProBox.offsetWidth-oBar.offsetWidth)+'px';
    $('.pro_hide').width(pro_box_hide.offsetWidth*scale+'px');
    //    判读加载中
    if(endTime){
        console.log(buffer_hide, pro_hide)
        if(Android){
            return;
        }else{
            if(buffer_hide<= pro_hide && parseInt(pro_box_hide.offsetWidth)>buffer_hide+2){
                $('.Downloading').show();
                console.log('加载中.....');
                timerV = setTimeout(function(){
                    if($('.Downloading').is(":visible")){
                        console.log('网不好了')
                        $('.Downloading span').show();
                    }
                },20000)
            }else{
                clearTimeout(timerV);
                $('.Downloading,.Downloading span').hide();
            }
        }
    }
};
//拖动播放*******自定义播放器控件
videoKj.addEventListener('touchstart',function(ev){
    clearTimeout(timerKj);
    var oTouch = ev.targetTouches[0];
    var disX = oTouch.pageX-oBar.offsetLeft;
    function fnMove(ev){
        var oTouch = ev.targetTouches[0];
        var l = oTouch.pageX-disX;
        if(l<0){
            l = 0;
        }else if(l>oProBox.offsetWidth-oBar.offsetWidth){
            l = oProBox.offsetWidth-oBar.offsetWidth;
        }
        oBar.style.left = l+'px';
        oPro.style.width = l+'px';
        var scale = l/(oProBox.offsetWidth-oBar.offsetWidth);
        oV.currentTime = scale*oV.duration;
        $('.pro_box .bar span').show();  //当拖拽时小圆点样式改变
        testPlay();
    }
    function fnEnd(){
        document.removeEventListener('touchmove',fnMove,false);
        document.removeEventListener('touchend',fnEnd,false);
        $('.pro_box .bar span').hide(); //取消拖拽时小圆点样式改变
        if(endTime){
            clearTimeout(timerKj);
            timerKj = setTimeout(function(){
                $('.videoKj').css('opacity','0');
                $('.pro_box2').show();
                $('.background_bottom_a').hide();
            },3000);
        }
    }
    document.addEventListener('touchmove',fnMove,false);
    document.addEventListener('touchend',fnEnd,false);
    //ev.preventDefault();
},false);

//点击快进*******自定义播放器控件
oProBox.onclick=function(ev){
    var scale = (ev.clientX-oProBox.offsetLeft)/oProBox.offsetWidth;
    oBar.style.left = scale*(oProBox.offsetWidth-oBar.offsetWidth)+'px';
    oPro.style.width = scale*oProBox.offsetWidth+'px';
    oV.currentTime = scale*oV.duration;
    testPlay();
};
window.onresize = function(){
    var content_box = document.getElementById('content_box');
    content_box.style.width = window.innerWidth + "px";
    content_box.style.height = window.innerHeight + "px";
}
//设置全屏
function screenVideo(){
    oV.style.width = screen.width + "px";
    oV.style.height = screen.height + "px";
};
//
function screenVideo2(){
    oV.style.width = 18.75 + "rem";
    oV.style.height = 10.5 + "rem";
};

//暂停
function testPause(){
    oV.pause();
    videoing = false;
};
//重新加载
function ovload(){
    if(!Pc){
        oV.load();
    }
    videoing = false;
};
//画面置顶
function moveToTop(){
    oV.style["object-position"]= "0px 0px";
};
//画面居中
function moveToCenter(){
    oV.style["object-position"]= "";
};
//竖屏播放
function portrait(){
    oV.setAttribute("x5-video-orientation", "portrait");
}
//横屏屏播放
function landscape(){
    oV.setAttribute("x5-video-orientation", "landscape");
}
var hh,mm,ss,length;
//时间转换
function formatTime(second,last) {
    hh = parseInt(second/3600);
    if(hh<10) hh = "0" + hh;
    mm = parseInt((second-hh*3600)/60);
    if(mm<10) mm = "0" + mm;
    ss = parseInt((second-hh*3600)%60);
    if(ss<10) ss = "0" + ss;
    length = '';
    if(last == 0 &&parseInt(oV.duration/3600) >0)length = hh + ":" + mm + ":" + ss;
    else if(last == 1 &&hh >0)length = hh + ":" + mm + ":" + ss;
    else length =  mm + ":" + ss;
    if(second>0){
        return length;
    }else{
        return "00:00";
    }
}
//获取url中的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = window.location.search.substr(1).match(reg);
    if (r!=null) return unescape(r[2]); return null;
}
//x5退出全屏
$('#video')[0].addEventListener('x5videoexitfullscreen', function (){
    if(isVerticalScreen == '1'){//竖屏
        $('.video_top').css('top','.5rem');
        $('.clickFollow').css('top','3rem');
        $('.trial').css('top','2.9rem');
        $('.BeginPlay').show();
        $('.BeginPlay2').show();
    }else{// 横屏
        $('.trial').css('top','0.65rem');
        screenVideo2();
        moveToCenter();
        Torem();
        $('.live_box').show();
        $('.BeginPlay').show();
        $('.BeginPlay2').show();
    }
}, false);

//x5进入全屏
$('#video')[0].addEventListener('x5videoenterfullscreen', function (){
    if(isVerticalScreen == '1'){//竖屏
        portrait();
        moveToTop();
        $('.video_top').css('top','2.5rem');
        $('.clickFollow').css('top','5rem');
        $('.trial').css('top','4.9rem');
        $('.BeginPlay').hide();
        $('.BeginPlay2').hide();
        $('.testPause').show();
    }else{//横屏
        $('.trial').css('top','2.65rem');
        portrait();
        screenVideo();
        moveToTop();
        Torem();
        $('.BeginPlay').hide();
        $('.BeginPlay2').hide();
        $('.testPause').show();
    }
}, false);

//视频直播失效；
$('#video')[0].addEventListener('error', function (){
    if(ios){

        //pop({"content": "视频加载中！" , "status": "error", time: '1000'});
    }
    console.log(oV.readyState,'视频直播失效');
    if(oV.readyState == 1){
        ovload();
    }else if(oV.readyState == 2){
        testPause();
    }
}, false);

//暂停
$('#video')[0].addEventListener('pause', function (){
    if(ios&&ztOK){
        console.log(oV.readyState,'暂停了')
        if(endTime){
            $('.Downloading').show();
        }

        testPlay();
    }
}, false);
//播放
$('#video')[0].addEventListener('playing', function (){
    console.log(oV.readyState,'播放......')
    //$('.Downloading,.Downloading span').hide();
    //setTimeout(function(){
    //    $('.Downloading,.Downloading span').hide();
    //},15000);
}, false);
//视频开始加载
var timeupV = null;
oV.addEventListener("timeupdate", function() {
    clearTimeout(timeupV)
    if($('.BeginPlay').is(':hidden')){
        if(oV.buffered.length == 1){
            $('.Downloading').hide();
        }else if(oV.buffered.length == 0){
            $('.Downloading').show();
        }
        timeupV = setTimeout(function(){
            testPause();
        },5000);
    }


}, false);
//视频播放结束
$('#video')[0].addEventListener('ended',function(){
    if(isRecorded=='1'&&!endTime){
        //直播结束了
        ztOK = false;
        goBack();
    }else if(endTime){
        ztOK = false;
        localStorage.setItem("currentTime"+_chatRoomId,'00');
        if(Android){
            window.location.reload();
        }else{
            setTimeout(function(){
                clearTimeout(timerV);
                $('.Downloading,.Downloading span').hide();
                testPause();
                $('.BeginPlay>img').attr('src','/web/res/image/playback.png');   //如果是回放 改变播放按钮样式
                $('.BeginPlay').show();
            },50)
        }
        return;
    }
}, false);

//横屏播放
$('.full_screen').click(function(){
    Full();
});
function Full (){
    oV.webkitEnterFullScreen();
    return false;
}
var videoing = false;
//开始播放
function testPlay(cTimet){
    console.log(Connected,'Connected-----------------------------')
    if(Connected == 1){
        if(ios&&!endTime){
            //非直播更新
            $('.videoKj').css('opacity','1');
            ovload();
        }
        oV.play();
        videoing = true;
        var osTrue = true;
        if(cTimet){
            if(cTimet>oV.duration){
                if(isRecorded=='1'){
                    //直播结束了
                    ztOK = false;
                    testPause();
                    $('#video').attr("src",'');
                }
            }
            function getCurTime(){
                oV.currentTime = cTimet;
                //$('#video').attr('poster',coverssAddress);
                oV.removeEventListener("playing", getCurTime,false);
            };
            oV.addEventListener("playing", getCurTime,false);
            // 播放结束时触发
        }
        if(ios){
            $('.BeginPlay').hide();
        }
        $('.BeginPlay2').hide();
        $('.testPause').show();
        $('.replay').hide();
    }else if(courseId.toString().length >= '15'){ // 如果是转播课
        if(ios&&!endTime){
            //非直播更新
            $('.videoKj').css('opacity','1');
            ovload();
        }
        oV.play();
        videoing = true;
        var osTrue = true;
        if(cTimet){
            if(cTimet>oV.duration){
                if(isRecorded=='1'){
                    //直播结束了
                    ztOK = false;
                    testPause();
                    $('#video').attr("src",'');
                }
            }
            function getCurTime(){
                oV.currentTime = cTimet;
                //$('#video').attr('poster',coverssAddress);
                oV.removeEventListener("playing", getCurTime,false);
            };
            oV.addEventListener("playing", getCurTime,false);
            // 播放结束时触发
        }
        if(ios){
            $('.BeginPlay').hide();
        }
        $('.BeginPlay2').hide();
        $('.testPause').show();
        $('.replay').hide();
    }
};

function downConnectStatus(Connected) {
    if(Connected==1){
        var cTimet;
        if(_trySeeTime == 0){
            if(localStorage.getItem("currentTime"+_chatRoomId)){
                cTimet = localStorage.getItem("currentTime"+_chatRoomId);
            }
        }
        if(endTime){
            clearTimeout(timerKj);
            timerKj = setTimeout(function(){
                $('.videoKj').css('opacity','0');
                $('.pro_box2').show();
            },3000);
            $('.videoKj').css('opacity','1');
            $('.pro_box2').hide();
            var now = parseInt(cTimet) * 1000 * 1000;
            if (!now) {
                now = 0 ;
            }
            //isFollowThirdOfficial != "0"没有关注
            if( TeacherFrom != _userId &&  mustShareTime > 0 && (!localStorage.getItem("pass" + courseId) || isFollowThirdOfficial != "0")){
                //不让托动
                $('.videoKj').css('opacity','0');
                stopTime = maxTime - now;
                var stopInterval = setTimeout("stopPlayAndShow()",parseInt(stopTime) / 1000);
            }

        }else{
            if(isRecorded=='1'){
                var RecordedTime = serverTime - startTimer;
                if(RecordedTime<0){
                    pop({"content": "直播还未开始！" , "status": "error", time: '1000'});
                    return ;
                }else{
                    cTimet = parseInt(RecordedTime/1000);
                    $('.videoKj').remove();
                }
            }else{
                $('.videoKj').css('opacity','1');
            }
        }
        testPlay(cTimet);

        // if(!localStorage.getItem("pass" + courseId)){
        //     //不让托动
        //     $('.videoKj').remove();
        // }

    }else{
        pop({"content": "老师正在赶来的路上！" , "status": "error", time: '1000'});
        return false;
    }
}

//加载完成后
$(function(){
    //开始直播
    $('.BeginPlay').click(function(){
        if(ios){
            getBuffered(); //视频加载缓冲条
        }
        var maxTime = mustShareTime * 1000 * 1000;
        var stopTime = maxTime;
        var timestamp = Date.parse(new Date());
        //录播预告禁止播放
        if(isRecorded == 1){
            if(timestamp <= startTimer){
                pop({"content": "老师正在来的路上!" , "status": "error", time: '1000'});
                return;
            }
        }
        //更新下
        ovload();
        console.log(isRecorded,'---------------isRecorded----------------------')
        if (_isEnd == '0') {
            if(isRecorded == 0) {
                $.getJSON("/live/isConnected.user?courseId=" + courseId, function (data) {
                    if (data.code == '000000') {
                        if(courseId.toString().length >= '15'){ // 如果是转播课
                            downConnectStatus(1);
                        }else{
                            downConnectStatus(data.data);
                        }
                    } else {
                        pop({"content": "加载错误，请重新加载！", "status": "error", time: '1000'});
                        return false;
                    }
                });
            }else {
                downConnectStatus(1);
            }
        } else {
            downConnectStatus(1);
        }
        if(isVerticalScreen == '1' && courselist.length){
            setTimeout(function(){
                $('.courseware').show();
            },2000)
        }
        $('.reward_box_bg').show();
    });
    //暂停后播放
    $('.BeginPlay2').click(function(){
        testPlay();
    });
    //回放
    $('.videoPlayback').click(function(){
        oV.currentTime = 0;
        ovload();
        testPlay();
        $('.videoPlayback').hide();
    });

    //暂停
    $('.testPause').click(function(){
        ztOK = false;
        testPause();
        setTimeout(function(){
            ztOK = true;
        },1000)
        $(this).hide();
        $('.BeginPlay2').show();
    });
});
function stopPlayAndShow() {
    testPause();
    window.location.href  = "/weixin/courseInfo?id=" + courseId  +"&isMustShare=1";
}

//****************************注册 打赏 购买 分享 横竖屏通用*********************************//
var balanceTwo=0;
/*充值*/
//点击充值
var isMobile = '0'; // 0:没有手机号 1:有手机号
var balance = 0;
var payId = 0;
var payAmount = 0;
var offset = 0;
//--打赏排行榜--

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
});
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
//		getUserRewardSort();
    /*直播 精选 下拉加载*/
    $('.contlistlength').scroll(function(){
        var scrollTop = $(this).scrollTop();
        var windowHeight = $(this).height();
        var scrollHeight = $('.contlistbox').height();
        if( scrollTop>0){
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
    var pageSize = 1;
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
                '<span style="width:8rem;overflow: hidden;text-overflow: ellipsis;white-space:nowrap; height: 1rem;line-height: 1rem" class="conusetitle">' + obj.name + '</span>'+
                '<span class="contriBution">贡献值:' + obj.amount + '学币</span>'+
                '</p>'+
                '</li>';
            $(".contlistbox").append(li);
        });
    }else{
        oBok = false;
        if(offset == 0){
            $('.contlistbox').append('<div class="noData"></div>')
        }
    }
}


var videoUrl = '';
var useryzInow = window.sessionStorage.getItem('useryz'+_chatRoomId);
if(useryzInow != null && !ios){
    videoUrl = $('#video').attr('src');
    $('#video').attr('src','');
    $(".useryz").show();
    window.sessionStorage.removeItem('useryz'+_chatRoomId);
};


/*确认充值*/
function surePay(){
    console.log(payAmount);
    console.log(payId);
    //验证手机号
    if(isMobile != '1'){
        if(!ios){
            window.sessionStorage.setItem('useryz'+_chatRoomId,'true');
            window.location.reload();
        }else{
            $(".useryz").show();
        }
        return;
    }
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
            return result.data.xb;
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
                //如果是试看支付成功，应该刷新页面
                if (isTrySeeFlag) {
                    var result = {
                        code: "000000",
                        message: "购买成功"
                    };
                    sys_pop(result);
                    setTimeout(function () {
                        //购买刷新
                        location.reload();
                    }, 1000);
                } else {
                    var result = {
                        code: "000000",
                        message: "充值成功"
                    };
                    sys_pop(result);
                    var balance = getBalance();
                    $("#payBalance").text("余额:"+balance);
                }
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
    $('.errorMessages:visible').html('');
    var obj = $('.useryz .text');
    var obj2 = $('.clios');
    valT(obj, obj2);
    var mobile = $('.mobile').val();
    if(mobile==""){
        $('.errorMessages').html("");
    }
});
//点击发送验证码
$(".submitYzm").click(function () {
    //验证手机号
    $('.errorMessages:visible').html('');
    var num = inputTest($('.mobile'));
    var mobile = $('.mobile').val();
    var result = ZAjaxJsonRes({url: "/aboutUs/getApplySms?mobile=" + mobile, type: "GET"});
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
});
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
        //coursePaying();
        location.reload();
    } else {
        $('.errorMessages:visible').html(result.message);
    }
}
/*关闭验证手机号弹窗*/
function closeRegister() {
    $(".useryz").hide();
    $(".room_inf li input").val("");
    $('.errorMessages:visible').html("");
    if(!ios){
        window.location.reload();
    }
}
/*
 * 邀请码验证
 * */
var invitationCode =  window.sessionStorage.getItem('invitationCode');
if(invitationCode != null){
    $(".newinvitationBox").show();
    window.sessionStorage.removeItem('invitationCode');
}
$(".invitationCode").click(function(){
    if(isVerticalScreen == '1'){
        $(".newinvitationBox").show();
    }else{
        window.sessionStorage.setItem('invitationCode','true');
        window.location.reload();
    }
});
var lenInput;
var titNum;
$(".courseInput").on("input",function(){
    lenInput = $(this).val().length;
    titNum = $.trim($('.courseInput').val());
    if(lenInput>0){
        $(".newcha").addClass("on");
        $(".invBtn").show();
    }else{
        $(".newcha").removeClass("on");
        $(".invBtn").hide();
        $(".Taptxt").text("");
    }
});

$(".newcha").click(function(){
    if($(".newcha").hasClass("on")){
        if(lenInput<16){
            $(".Taptxt").text("请输入正确的邀请码");
            return;
        }else{
            var param = {payType: "16", password: "", amount: "", courseId: courseId, deviceNo: "", isBuy: '1',ext:titNum};
            var result = ZAjaxRes({url: "/thirdPay/pay.user", type: "POST",param:param });
            if (result.code == "000000") {//支付成功或者已经支付
                $(".courseInput").val("");
                $(".Taptxt").text("兑换成功");
                $(".newinvitationBox").hide();
                setTimeout(window.location.reload(), 1500)
            } else {
                $(".Taptxt").text(result.message);
            }
        }
    }else{
        return;
    }
})
$(".invBtn").click(function(){
    $(".courseInput").val("");
    $(".invBtn").hide();
    $(".newcha").removeClass("on");
    $(".Taptxt").text("");
});
$(".invitationCoud").click(function(){
    $(".newinvitationBox").show();
});
$(".newcance").click(function(){
    $(".newinvitationBox").hide();
    $(".courseInput").val("");
    $(".newcha").removeClass("on");
    $(".Taptxt").text("");
    $(".invBtn").hide();
})

//解决试看支付完没有刷新问题
var isTrySeeFlag = false;

//支付
function pay(){
    $("#pay").attr("disabled",true);
    var sId = 0;
    if(sourcesId > 0){
        sId = sourcesId;
    }else{
        sId = courseId;
    }
    if(loginMobile == '' || loginMobile == null ){
        $(".useryz").show();//请填写手机号！
        $(".rechartbox").hide();
        return;
    }

}
//购买课程按钮 -- 弹支付提示
function coursePaying(){
    var sId = 0;
    if(sourcesId > 0){
        sId = sourcesId;
    }else{
        sId = courseId;
    }
    if(loginMobile == '' || loginMobile == null ){
        $(".useryz").show();//请填写手机号！
        $(".rechartbox").hide();
        return;
    }
    var param = {payType: "07", password: "", amount: chargeAmt, courseId: sId, deviceNo: "", isBuy: '0' ,invitationAppId: invitationAppId};
    var result = paying(param);
    var attach = result.attach;
    if(result.code == '100025'){
        BaseFun.Dialog.Config2({
            title: '提示',
            text : '是否购买该课程',
            cancel:'取消',
            confirm:'确定',
            close:false,
            callback:function(index) {
                if(index == 1){
                    var param = {payType: "07", password: "", amount: chargeAmt, courseId: sId, deviceNo: "", isBuy: '1' ,invitationAppId: invitationAppId};
                    var result = paying(param);
                    if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                        $(".trialEnd").hide();
                        pop1({"content": "购买成功" , "status": "normal", time: '1500'});
                        setTimeout(function () {
                            //购买刷新
                            localStorage.removeItem("trySeeTime"+_chatRoomId)
                            location.reload();
                        }, 2000);
                    }
                }
            }
        });
    }else if(result.code == '100012'){
        BaseFun.Dialog.Config2({
            title: '提示',
            text : '学币不足，有枣币可兑换',
            cancel:'去兑换',
            confirm:'不用，直接买',
            close:true,
            callback:function(index) {
                if(index == 0){
                    window.location.href="/weixin/exchangeCurrency.user?attach="+attach;
                }else if(index == 1){
                    var param = {payType: "14", password: "", amount: "", courseId: sId, deviceNo: "", isBuy: "1", invitationAppId: invitationAppId};
                    var result = paying(param);
                    isTrySeeFlag = true;
                    onBridgeReady(result);
                }
            }
        });
    }else if (result.code == '100002') {//不足 -- 微信支付
        BaseFun.Dialog.Config2({
            title: '提示',
            text : '支付' + chargeAmt + '学币购买该课程',
            cancel:'取消',
            confirm:'确定',
            close:false,
            callback:function(index) {
                if(index == 1){
                    var param = {payType: "14", password: "", amount: "", courseId: sId, deviceNo: "", isBuy: "1", invitationAppId: invitationAppId};
                    var result = paying(param);
                    isTrySeeFlag = true;
                    onBridgeReady(result);
                }
            }
        });

    }
}


