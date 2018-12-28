/**
 * Created by DHY on 2018/6/12.
 */
var chatroom = null;
var roomId = '';
//直播间创建者
var TeacherFrom = '';
//服务器当前时间
var serverTime = '';
//直播开始时间
var liveStartTime = '';
//获取消息时间
var startTime = '';
//语音播放地址数组
var arrMp3url = [];
//语音下标
var iNow = 0;
//图片消息列表
var imgList = [];
//课件
var courselist = [];
//直播地址
var videoUrl = '';
//结束时间
var endTime = '';

//聊天室消息不重复开关
var vOk = true;
//直播类型 0-视频直播 1-语音直播
var liveWay;
//获取课件开关
var fig2 = true;
//消息开关
var msgOk = true;
//历史链接开关
var msgLS = true;
//直播状态
var Connected = 1;
//管理员账号
var admins = [];
//禁言人员的账号
var gags = [];
//要禁言的id
var fromId = '';
//老师撤回了的消息
var msgCancelIds = [];
// 老师头像
var TeacherPhoto = '';
//老师名字
var TeacherName = '';
//进入直播间名单
var userEnterList = [];
// 在线人数
var liveUserConut = '';
// 是否在播放语音
var isPlayAudio = false;
//记录初始title
var documentTitle = $(document).attr("title");
//上一条消息时间
var prevMsgTimes = 0;
//拉取第一条消息的时间
var historyOneMsgTime = 0;
//记录语音位置
var mp3Inow = true;
//旦暮开关,默认是开着的
var barrageChecked = "1";
//弹幕"缓存"
var barrages = new Array(3);
//判断有没有barrages缓存，因为barrages是固定大小的数组不能用lenght判断
var hasBrrage = false;
//提问"缓存"
var tiwens = new Array(3);
//判断有没有tiwens缓存，因为tiwens是固定大小的数组不能用lenght判断
var hasTiwens = false;
//正在输入清理定时器
var inputingTimeout = null;
// 记录是否可以滚动
var isScrollswitch = true;
//  记录语音播放过的记录
var audioPlayEndArr = null;
// 下拉加载历史数据开关
var histDataNo = true;

//初始弹幕跟提问列表
var swiperBarrage = null;

//分步执行，记录步数
var _step = 0 ;

// 记录是否是重新连接
var isChatRoomReconnect = false;

var qrCode = "";

// 记录当前正在播放语音的uuid
var _thisAudioUuid = '';
//存储时长
var durList = [];
// 总时长
var durNum = 0;
var playOK = true;
var offSet = 1;
// 当前播放时长
var Drag = 0;
//audio 当前时间
var currentTime='';
var mintime = 1;
//时间定时器
var brieftimer;
//进度条定时器
var briefbar;
//存储uuid
var uuidList = [];
//课件图片地址
var imgToppicSrc = [];
var Ios = false;
if(navigator.userAgent.toLowerCase().indexOf('iphone')!=-1){
    Ios = true;
}
$(function(){
    //获取聊天室基本信息
    ZAjaxRes({url: "/weixin/live/getCourse.user?courseId=" + courseId , type: "GET",
        callback:function(result) {
        if (result.code == '000000') {
            data = result.data;
            TeacherFrom = data.course.appId;
            TeacherName = data.teacherName;
            TeacherPhoto = data.teacherPhoto;
            $('.brief_title>em').html(TeacherName);
            $('.brief_title>span').html(data.course.liveTopic);
            roomId = data.course.roomId;
            liveWay =data.course.liveWay;
            //课程开始时间
            liveStartTime = data.course.startTime;
            //系统时间
            serverTime = data.serverTime;
            audioPlayEndArr = BaseFun.GetStorage('audioPlayEndArr'+courseId+'_'+_userId);
            //试看时间
            trySeeTime = data.course.trySeeTime;
            Connected = data.isConnected;
            admins = data.managerId.split(",");
            admins.push(TeacherFrom+'');
            gags = data.gagUserId.split(",");
            liveUserConut = data.userCount;
            qrCode = data.qrCode ;
            //课程结束时间，如果不为空课程已结束
            endTime = data.course.endTime;
            //课件
            courselist = data.courseWare;
            var html_banner = ''
            if (data.courseWare.length == 0) {
                html_banner = '<div class="swiper-slide"  onclick="coursewareClickImg(this)"><img id="firstImg" src="' + data.course.verticalCoverssAddress + '"/></div>'
            } else {
                for(var i = 0;i < data.courseWare.length;i++){
                    var picsrc = data.courseWare[i].address;
                    imgToppicSrc.push(picsrc);
                    html_banner += '<div class="swiper-slide"  onclick="coursewareClickImg(this)"><img id="firstImg" src="' + picsrc + '"/></div>'
                }
            }
            $('#brief_toppic>.swiper-wrapper').append(html_banner);

            $(".teacherlog").css({
                "background-image": "url(" + data.teacherPhoto + ")",
                "background-size": "100% 100%",
                "background-position": "center center",
                "background-repeat": "no-repeat"
            });
            $(".topteacherbox i").html(data.teacherName+"的直播间");
            //课程介绍图片
            var courseImgList = data.courseImgList;
            var courseRemark = data.course.remark;
            //console.log(courseRemark)
            if(courseImgList<=0){
                $("#courseImgList").hide();
                if(courseRemark == ""){courseRemark = '暂无简介'};
            }
            $(".livetext").html(replaceTeturn2Br(courseRemark));
            if(courseRemark == ""){
                $(".livetext").hide();
            };
            $.each(courseImgList, function (i, n) {
                if(n.address==''){
                    $("#courseImgList").append('<div class="imglist"><p>'+n.content+'</p></div>');
                }else{
                    $("#courseImgList").append('<div class="imglist"><img src="' + n.address + '"><p>'+n.content+'</p></div>');
                }
            });
            _step++;
        }  else {
            alert(data.message);
        }
        }});
    Load();
    var ChatRoomSwiperIndex = BaseFun.GetStorage('ChatRoomSwiperIndex'+courseId+'_'+_userId);
    ChatRoomSwiperIndex = ChatRoomSwiperIndex != null ? ChatRoomSwiperIndex : 0;
    var banner = new Swiper('#brief_toppic', {
        spaceBetween: 30,
        pagination: '.toppic_pag',
        paginationType: 'fraction',
        initialSlide:parseInt(ChatRoomSwiperIndex),
        observer:true,
        observeParents:true,
        autoplay:3000,
        autoplayDisableOnInteraction : false,
        loop : true,
        onSlideChangeEnd: function(swiper){
            var bg = $('#brief_toppic>.swiper-wrapper>.swiper-slide').eq(swiper.activeIndex).find('img').attr('src')
            $('.courseware_bg').css('background-image','url('+bg+')');
        }
    });
    // 播放
    $('#topic-audio-play').click(function(){
        if(!$(this).hasClass('btn-pause')){
            if(mintime-1 !=durNum ) {
                $(this).addClass('btn-pause');
                playAudioGo();
            }
        }else{
            $(this).removeClass('btn-pause');
            clearInterval(briefbar);
            clearInterval(brieftimer);
            $('#audio')[0].pause();
        }

    });
    // 打开课件
    $('.btn_course_list').click(function(){
        $('.course_portal_container').show();
    })
    // 关闭课件列表
    $('.course_close').click(function(){
        $('.course_portal_container').hide();
    })
    //点击空白区域关闭提问列表和弹幕列表
    $('.course_portal_container').on('click',function(e){
        if(this == e.target){
            $(this).hide();
        }
    });
    //切换上课模式
    $('.btn_tradition_list').click(function(){
        window.location.href = '/weixin/index.user?courseId='+ courseId +'&invitationAppId=';
    })
    // 拖拽进度条
    $('.brief_box').on('touchstart','.audioBar',function(e){
        var _this = $(this);
        var oBar = _this.find('em');
        var oTouch = e.originalEvent.targetTouches[0];
        var disX = oTouch.pageX-oBar.width();
        function fnMove(e){
            Drag = 0;
            var oTouch = e.originalEvent.targetTouches[0];
            var l = oTouch.pageX-disX;
            if(l<0){
                l = 0;
            }else if(l>_this.width()){
                l = _this.width();
                //$('#audio')[0].stop();
            }
            oBar.css('width',parseInt(l/_this.width()*100)+'%').attr('data-width',parseInt(l/_this.width()*100));
        }
        function fnEnd(){
            $(document).off('touchmove',fnMove);
            $(document).off('touchend',fnEnd);
            var _this_w = parseInt(oBar.attr('data-width')); //滑动到位置
            var current =  parseInt(durNum*(_this_w/100));//滑动到当前时间
            var listOnelen = 0;
            for(var i = 0; i < durList.length ; i++){
                Drag += durList[i];
                listOnelen = durList[i];
                if(current == durNum){
                    clearInterval(brieftimer);
                    clearInterval(briefbar);
                    return;
                }
                if(Drag > current){
                    iNow = i;
                    clearInterval(brieftimer);
                    clearInterval(briefbar);
                    break;
                }
            }
            mintime = current;   // 设置播放时间
            var Surplus = Drag-current; // 当前audio播放时间剩余秒
            currentTime = listOnelen-Surplus// 设置audio播放时间
            playAudioGo();
        }
        $(document).on('touchmove',fnMove);
        $(document).on('touchend',fnEnd);
        e.stopPropagation();

    });
})
//点击课件放大图片
function coursewareClickImg(imgurl){
    var imgsrc = $(imgurl).children().attr('src');
    if(imgsrc != undefined){
        wx.previewImage({
            current: imgsrc,
            urls: imgToppicSrc
        });
    }
}
//请求数据
function Load(){
    if(!playOK){return false;}
    var result = ZAjaxRes({url: "/weixin/live/getHistoryMsg.user", type: "GET", param:{courseId: courseId, offSet: offSet}});
    if (result.code == "000000") {
        var data = result.data;
        for(var i=0; i<data.length; i++){
            if(data[i].msgType == "AUDIO"){
                var attach = JSON.parse(data[i].attach);
                var mp3Url = attach.url;
                var uuid = data[i].msgidClient;
                var dur = Math.round(attach.dur / 1000);
                durList.push(dur);
                arrMp3url.push(mp3Url);
                uuidList.push(uuid)
            }
        };
        for(var i = 0;i < durList.length;i++){
            durNum += durList[i];
        }
        $('.audio_times div').eq(1).html(sec_to_time(durNum));
        console.log(sec_to_time(durNum))
        offSet++;
    }else if(result.code == '000110'){
        playOK = false;
    }
};
//语音播放结束
$('#audio')[0].addEventListener('ended',audioEnden, false);
function audioEnden(){
    _thisAudioUuid = '';
    if ($('#audio')[0].ended){
        if (iNow < arrMp3url.length - 1) {
            iNow++;
            clearInterval(brieftimer);
            clearInterval(briefbar);
            playAudioGo(iNow);
        } else {
            isPlayAudio = false;
            iNow = 0;
            mintime = 1;
            $('#topic-audio-play').removeClass('btn-pause');
            $('.audioBar em').width('.1%').attr('data-width','0');
            $('.audio_times > div').eq(0).html('00:00:00');
            console.log('结束了')
        }
    }
};
//当浏览器可以播放音频时
$('#audio')[0].addEventListener('playing', function () {
    if(arrMp3url.length > 0){
        isPlayLoading = false;
        //if(Ios){
        //    setTimeout(function(){
        //        brieftimer = setInterval("CountDown()", 1000);
        //        briefbar = setInterval("audiogo();", 500); //设置定时器
        //    },100);
        //}else{
            brieftimer = setInterval("CountDown()", 1000);
            briefbar = setInterval("audiogo();", 500); //设置定时器
        //}

    }
}, false);
//暂停
$('#audio')[0].addEventListener('pause', function (){
        console.log('暂停了')
        clearInterval(brieftimer);
        clearInterval(briefbar);
}, false);
var hh,mm,ss,length;
//极简模式总时长
function CountDown() {
    if(mintime-1 ==durNum ){
        clearInterval(brieftimer);
        $('#topic-audio-play').removeClass('btn-pause');
        return;
    }
    if (mintime >= 0) {
        hh = parseInt(mintime/3600);
        if(hh<10) hh = "0" + hh;
        mm = parseInt((mintime-hh*3600)/60);
        if(mm<10) mm = "0" + mm;
        ss = parseInt((mintime-hh*3600)%60);
        if(ss<10) ss = "0" + ss;
        length =  hh + ":" + mm + ":" + ss;
        //if(parseInt(durNum/3600) >0)length = hh + ":" + mm + ":" + ss;
        //else length =  mm + ":" + ss;
        if(mintime>0){
            $('.audio_times div').eq(0).html( length);
        }else{
            $('.audio_times div').eq(0).html( length);
        }
        ++mintime;
    }
}

//极简模式进度条
function audiogo(){
    var audioWidth = parseInt(mintime)/parseInt(durNum)*100;
    $('.control-bar .audioBar em').width( parseInt(audioWidth)+ "%");
    if($('.control-bar .audioBar em').width() == "100%"){
        clearInterval(briefbar);
    }
}
//语音播放函数
var isPlayLoading = false;
//是否加载过了
var isLoaded = false;
function playAudioGo(){
    if(arrMp3url.length){
        console.log(Drag)
        isPlayAudio = true;
        isPlayLoading = true;
        isLoaded = true;
        $('#audio')[0].src = arrMp3url[iNow];
        if(arrMp3url[iNow+1]){$('#audioNext')[0].src = arrMp3url[iNow+1];}
        $('#audio')[0].loop = false;
        $('#audio')[0].ended = true;
        if(currentTime!=''){
            $('#audio')[0].currentTime = currentTime;
        }
        $('#audio')[0].play();
        $('#topic-audio-play').addClass('btn-pause');
        //音频准备就绪
        var ThisAudioUUID = uuidList[iNow];
        _thisAudioUuid = ThisAudioUUID;
        audioPlayEndArr = BaseFun.GetStorage('audioPlayEndArr'+courseId+'_'+_userId);
        if(audioPlayEndArr != null ){
            if(audioPlayEndArr.audioArr.indexOf(ThisAudioUUID) === -1){
                audioPlayEndArr.audioArr.push(ThisAudioUUID);
                BaseFun.SetStorage('audioPlayEndArr'+courseId+'_'+_userId,audioPlayEndArr)
            }
        }else{
            BaseFun.SetStorage('audioPlayEndArr'+courseId+'_'+_userId,{audioArr:[ThisAudioUUID]})
        }
        //回放时，应该记录播放到什么地方了
        if (endTime) {
            BaseFun.SetStorage("playHistory_" + courseId , iNow,currentTime);
        }
        currentTime = '';
    }
}


/**
 * 时间秒数格式化
 * @param s 时间戳（单位：秒）
 * @returns {*} 格式化后的时分秒
 */
var sec_to_time = function(s) {
    var t;
    if(s > -1){
        var hour = Math.floor(s/3600);
        var min = Math.floor(s/60) % 60;
        var sec = s % 60;
        if(hour < 10){t = "0";}
        t += hour + ":";
        if(min < 10){t = "0";}
        t += min + ":";
        if(sec < 10){t += "0";}
        t += sec;
    }
    return t;
}
function enters() {
    window.location.href = "/weixin/liveRoom?id=" + roomId;
}