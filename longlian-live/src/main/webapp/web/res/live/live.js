//var vConsole = new VConsole(); //  手机调试模式
//注意这里, 引入的 SDK 文件不一样的话, 你可能需要使用 SDK.Chatroom.getInstance 来调用接口
var chatroom = null;

var roomId = '';
//直播间创建者
var TeacherFrom = '';
//获取消息时间
var startTime = '';
//课件
var courselist = [];
//直播地址
var videoUrl = '';
//结束时间
var endTime = '';
//开始时间
var startTimer = '';
//系统时间
var serverTime = '';

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
var Connected = 0;
//管理员账号
var admins = [];
//禁言人员的账号
var gags = [];
//要禁言的id
var fromId = '';
//下标
var courseClassIndex = '';
var hisOk = true;
var boxH = 0;
var timers = null;
var timers2 = null;
//清屏时间
var cleanScreenTime = '';
//计算时间差
var megTime = '0';
//if(!Pc){
//    ovload();
//}
var nshow = 1;
//0 是横屏 1是竖屏
var isVerticalScreen = '';
//1是录播的课
var isRecorded = '0';

//课程价格
var chargeAmt = 0;
//手机号
var loginMobile = '';
//是否有邀请码
var isInviteCode = '';
//二维码图片路径
var qrCode="";

var mustShareTime = 0 ;

var  isFollowThirdOfficial = "0";
//分步执行，记录步数
var _step = 0 ;
//进入直播间的人名
var userEnterList = [];
//进入直播间开关
var userEnterRight = null;
var openorcClose= true;// 监听推流状态
$(document).ready(function(){
    FastClick.attach(document.body);
    $('#sayslist').html('<li id="megMore"></li>');
    $('body').append("<input type='hidden' id='contentTypeNum'>");// 监听推流状态

    //没结束
    if (_isEnd == '0') {
        //配置聊天室信息
        chatroom = SDK.Chatroom.getInstance({
            appKey: _appKey,
            account: _userId,
            token: _yunxinToken,
            chatroomId: _chatRoomId,
            chatroomAddresses: _roomAddr,
            onconnect: onChatroomConnect,
            onerror: onChatroomError,
            onwillreconnect: onChatroomWillReconnect,
            ondisconnect: onChatroomDisconnect,
            onmsgs: onChatroomMsgs
        });
    } else {
        _step++;
    }

    // 记录url 到首页显示
    var liveRecording = {
        url:window.location.pathname+window.location.search,
        id:courseId
    }
    sessionStorage.setItem("liveRecording",JSON.stringify(liveRecording));
    //获取聊天室基本信息
    $.getJSON("/weixin/live/getCourse.user?courseId=" + courseId, function (data) {
        if (data.code == '000000') {
            iosCache = false;
            console.log(data);
            data = data.data;
            loginMobile = data.loginMobile;
            chargeAmt = data.course.chargeAmt;
            isInviteCode = data.course.isInviteCode;
            TeacherFrom = data.course.appId;
            roomId = data.course.roomId;
            liveWay = data.course.liveWay;
            isVerticalScreen = data.course.isVerticalScreen;
            isRecorded = data.course.isRecorded;
            courseClassIndex = data.courseClassIndex;
            admins = data.managerId.split(",");
            admins.push(TeacherFrom+'');
            mustShareTime =  data.course.mustShareTime;

            cleanScreenTime = data.course.cleanScreenTime;
            if(data.course.cleanScreenTime==''){
                cleanScreenTime = 0;
            }

            //如果是老师
            if(TeacherFrom == _userId){
                $('.reward2,.clickFollows').hide();
            }
            qrCode = data.qrCode ;
            gags = data.gagUserId.split(",");

            //课程结束时间，如果不为空课程已结束
            endTime = data.course.endTime;
            //课程开始时间
            startTimer = data.course.startTime;
            //系统时间
            serverTime = data.serverTime;

            var thisSbtime = data.serverTime-startTimer;
            if(isRecorded=='1'){
                clearTimeout(sTimer);
                var sTimer = setInterval(function(){
                    serverTime+=1000;
                },1000);
            }
            if(TeacherFrom == _userId){ //如果是老师
                $('#adv').remove();
            }
            //广告链接
            if(data.course.adAddress){
                //$('#adAddresss').attr('href',data.course.adAddress);
                $('#adAddresss').show();
                $('#adAddresss').on('click',function(){
                    $('.more_btn_pop_bg').hide();
                    window.location.href = data.course.adAddress;
                });
            }
            //如果是iOS
            if(ios){
                $('#video').css({'object-fit':'cover'});
            }
            if (endTime) {
                var tryseeTimes = BaseFun.GetSession('trysee_Time'+_chatRoomId);
                if(tryseeTimes != null){
                    $('.BeginPlay').remove();
                    $('.videoKj').remove();
                    $('.test_play_content').remove();
                    $('.trialEnd').show();
                    if(sourcesId>"0"){
                        $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                    }
                    BaseFun.RemoveSession('trysee_Time'+_chatRoomId);
                }
                if(liveWay == 0){//如果是视频直播
                    if (_playAddress == '') {
                        $('#sayslist').append('<li><h5>视频正在录制中，请稍后再看回放！</h5></li>');
                        pop({"content": "视频正在录制中，请稍后" ,width:"10rem" ,"status": "error", time: '2000'});
                    }else if(_playAddress == 'novideo'){
                        $('#sayslist').append('<li><h5>该课程授课期间未直播</h5></li>');
                        pop({"content": "该课程授课期间未直播" , "status": "error", time: '2000'});
                    }
                    var videoPlayOnOff = true;
                    $('#video').on('touchstart',function(){
                        if($('.BeginPlay').is(":hidden")){
                            clearTimeout(timerKj);
                            $('.videoKj').css('opacity','1');
                            $('.background_bottom_a').show();
                            $('.pro_box2').hide();
                            timerKj = setTimeout(function(){
                                $('.videoKj').css('opacity','0');
                                $('.background_bottom_a').hide();
                                $('.pro_box2').show();
                            },3000);
                        }
                    });
                    //回放的试看
                    if(_trySeeTime>0&&admins.indexOf(_userId)==-1){
                        if(localStorage.getItem("trySeeTime"+_chatRoomId)) {
                            if (isInviteCode == '0') {
                                $('.invitationCode').remove();
                            }
                            $('.purchase').html(chargeAmt + '学币购买');
                            $('.reward2,.send_message,.videoKj,.more_btn,.background_bottom_a,.BeginPlay,.Downloading').remove();
                            $('.trialEnd').show();
                            if(sourcesId>"0"){
                                $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                            }
                            //oV.ontimeupdate=function(){
                            //    if(oV.currentTime>_trySeeTime){
                            //        $('.test_play_content').remove();
                            //        $('.trialEnd').show();
                            //        //视频暂停
                            //        ztOK = false;
                            //        testPause();
                            //        $('#video').attr('src','');
                            //        $('.BeginPlay').remove();
                            //        $('.videoKj').remove();
                            //        BaseFun.SetSession('trysee_Time'+_chatRoomId,'true');
                            //        window.location.reload();
                            //    }
                            //};
                        }else{
                            localStorage.setItem("trySeeTime"+_chatRoomId,true);
                            if(isInviteCode=='0'){
                                $('.invitationCode').remove();
                            }
                            $('.purchase').html(chargeAmt+'学币购买');
                            $('.trial,.invitationCode').show();
                            $('#mesGift,#mesNews,.videoKj,.background_bottom_a').remove();
                            $('.test_play_content').show();
                            $('.trial_span').html('可试看'+_trySeeTime+'秒').css('color','#fff');
                            clearTimeout(trySee1);
                            var trySee1 = setInterval(function(){
                                _trySeeTime--;
                                $('.trial_span').html(_trySeeTime+'s').css('color','#d53c3e');
                                if(_trySeeTime=='0'){
                                    clearTimeout(trySee1);
                                    $('.test_play_content').remove();
                                    $('.trialEnd').show();
                                    if(sourcesId>"0"){
                                        $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                                    }
                                    //视频暂停
                                    ztOK = false;
                                    testPause();
                                    $('#video').attr('src','');
                                    $('.BeginPlay').remove();
                                    window.location.reload();
                                }
                            },1000);
                        }
                    }
                }
                //结束时间 直播已结束看回放
                $(".reward2,.more_btn").show();
                playback();
            } else {
                $(".send_message,.reward2,.more_btn").show();
                if(liveWay == 0){//如果是视频直播
                    //判断不是正常试看结束
                    var tryseeTimes = BaseFun.GetSession('trysee_Time'+_chatRoomId);
                    if(tryseeTimes != null){
                        $('.Downloading,.BeginPlay').remove();
                        $('.videoKj').remove();
                        $('.test_play_content').remove();
                        $('.trialEnd').show();
                        if(sourcesId>"0"){
                            $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                        }
                        BaseFun.RemoveSession('trysee_Time'+_chatRoomId);
                    }
                    //试看倒计时
                    if(_trySeeTime>0&&admins.indexOf(_userId)==-1){
                        if(isInviteCode=='0'){
                            $('.invitationCode').remove();
                        }
                        $('.purchase').html(chargeAmt+'学币购买');
                        //试看时不显示
                        $('.videoKj,.reward2,.send_message').remove();
                        //试看过后
                        if(localStorage.getItem("trySeeTime"+_chatRoomId)){
                            $('#video').attr('src','');
                            $('.test_play_content,.more_btn').remove();
                            $('.trialEnd').show();
                            if(sourcesId>"0"){
                                $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                            }
                            //视频暂停
                            ztOK = false;
                            testPause();
                            $('.BeginPlay').remove();
                        }else{//第一次试看
                            var thisSktime = _trySeeTime;
                            localStorage.setItem("trySeeTime"+_chatRoomId,true);
                            clearTimeout(trySee);
                            $('.test_play_content').show();
                            var trySee = setInterval(function(){
                                _trySeeTime--;
                                $('.trial_span').html(_trySeeTime+'s');
                                if(_trySeeTime=='0'){
                                    clearTimeout(trySee);
                                    $('.test_play_content').remove();
                                    $('.trialEnd').show();
                                    if(sourcesId>"0"){
                                        $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                                    }
                                    //视频暂停
                                    ztOK = false;
                                    testPause();
                                    $('#video').attr('src','');
                                    $('.BeginPlay').remove();
                                    window.location.reload();
                                }
                            },1000);

                            if(isRecorded == "1"){
                                oV.ontimeupdate=function(){
                                    if(oV.currentTime>parseFloat(thisSktime)+(thisSbtime/1000)){
                                        $('.test_play_content').remove();
                                        $('.trialEnd').show();
                                        if(sourcesId>"0"){
                                            $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                                        }
                                        //视频暂停
                                        ztOK = false;
                                        testPause();
                                        $('#video').attr('src','');
                                        $('.Downloading,.BeginPlay').remove();
                                        $('.videoKj').remove();
                                        BaseFun.SetSession('trysee_Time'+_chatRoomId,'true');
                                        window.location.reload();
                                    }
                                };
                            }
                        }
                    }
                    $('.pro_box,.video_timer,.video_timer2,.testPause,.BeginPlay2').remove();
                    $('.pro_box2').remove();
                    if(isRecorded!='1' && Android){
                        $('.videoKj').css('opacity','1');
                    }
                    //配置视频播放地址
                    videoUrl = data.course.hlsLiveAddress;
                    //为空时加载
                    if (!_playAddress) {
                        if (videoUrl =='') {
                            $('#sayslist').append('<li><h5>老师视频直播还未开始！</h5></li>');
                        }else{
                            $('#video').attr('src', videoUrl);
                        }
                    }
                }

                isFollowThirdOfficial = data.isFollowThirdOfficial;
                //是否扫描二维码
                if(data.isFollowThirdOfficial=='1'&&data.qrCode&&!sessionStorage.getItem("qrCode")){
                    var oNows = new Date();
                    var oSs = parseInt((startTimer-oNows.getTime())/1000);
                    if(oSs<=0){
                        $(".ewmbox").remove();
                    }else{
                        clearTimeout(timers);
                        $('.imgsrc').attr('src',data.qrCode);
                        tick();
                        timers = setInterval(tick,1000);
                        $(".ewmbox").addClass("on");
                        var timerst = null;
                        clearTimeout(timerst);
                        timerst = setTimeout(function(){
                            clearTimeout(timers);
                            $(".ewmbox").removeClass("on");
                        },60000);
                    }
                }
            }
            if (data.qrCode) {
                try{
                    $('.imgsrc').attr('src',data.qrCode);
                    console.log(data.qrCode);
                } catch (e){}
            }

            //课件
            courselist = data.courseWare;
            coursewares(courselist);
            courseware.slideTo(courseClassIndex);
            //学生禁止滑动课件
            /*if(endTime==''){
                courseware.detachEvents();
            }*/
            _step++;
            //加载完了
            if (_isEnd == '0') {
                if (_step >= 2) {
                    loadDone()
                }
            }
        } else {
            alert(data.message);
        }
    });


    $('#bd_box').scroll(function(){
        var scrollTop = $(this).scrollTop();
        //var boxH = $('#bd_box').outerHeight();
        //var boxT = $('#bd_box').offset().top;
        if(scrollTop < 10 &&endTime == ''){
            //var ulH = $('#sayslist').outerHeight();
            if(hisOk){
                hisOk =false;
                boxH = $('#sayslist').outerHeight();
                historyMsgs();
            }
        }
    });

});

//************************************云信公共sdk函数***********************************//
/**正在直播**/
function onChatroomConnect(chatroom) {
    console.log('进入聊天室', chatroom);
    if (!videoing) {
        ovload();
    } else {
        oV.play();
    }
    arrMp3url = [];
    //设置获取历史记录时间
    startTime = chatroom.member.enterTime;
    $('#sayslist').html('<li id="megMore"></li>');

    //绑定发送消息事件,先解后绑，避免重复绑定
    $('#send').off('click', say);
    $('#send').on('click', say);
    //绑定发送提问事件,先解后绑，避免重复绑定
    $('#quiz').off('click', quiz);
    $('#quiz').on('click', quiz);

    if (!videoing) {
        $.getJSON("/live/isConnected.user?courseId=" + courseId, function (data) {
            console.log('获取状态',data.data);
            if(nshow==1){
                $('.BeginPlay').show();
                nshow++;
            }
            if (data.code == '000000'){
                if(msgLS){
                    Connected = data.data;
                }
            }
        });

        _step++
        //加载完了
        if (_step >= 2) {
            loadDone()
        }
    }
};

/**
 * 加载完了
 */
function loadDone() {
    //获取聊天室历史记录100
    historyMsgs();
    setTimeout(function(){
        $('#sayslist').parents('.bd').scrollTop($('#sayslist').outerHeight()-$('#bd_box').outerHeight()-5);
    },300);

    //如果我是管理员 加清屏
    if(admins.indexOf(_userId)!=-1){
        $('.clearScreens').show();
    }
    //清屏
    $('.clearScreens').click(function(){
        $('.more_btn_pop_bg').hide();
        if(admins.indexOf(_userId)!=-1){
            $.getJSON("/course/clearScreen.user?courseId=" + courseId, function (data) {
                if (data.code == '000000') {

                }else{
                    alert(data.message);
                }
            });
        }
    });

}

//管理员禁言
$('#sayslist').on('click','.headImg',function(){
    console.log(admins);
    $('.gagbodyx').show();
    //自己是管理员
    if(admins.indexOf(_userId)!=-1){
        fromId = $(this).parent('li').attr('from');
        $('.gaginfo>span').eq(1).html('ID:'+fromId);
        $('.gagname').html($(this).parent('li').attr('name'));
        $('#gagimg').attr('src',$(this).parent('li').attr('img'));
        var result = ZAjaxRes({url: "/user/liveRoomFollowCount.user?userId="+fromId,type: "GET"});
        $('.gaginfo>span').eq(0).html('关注:'+result.data.userFollowCount);
        //排查管理人员
        if(admins.indexOf(fromId)==-1){
            $('#gagbox').show();
            //判断 禁言 解除禁言
            if(gags.indexOf(fromId)==-1){
                //禁言
                $('.gagbtn').html('<div id="gag_jy" onclick="gagBtn($(this))" style="width: 100%;">禁言</div>');
            }else{
                //解除禁言
                $('.gagbtn').html('<div id="gag_jcjy" class="on" onclick="gagBtn($(this))" style="width: 100%;">解除禁言</div>');
            }
        }else{
            if(fromId==TeacherFrom){//老师
                if($('.clickFollows').hasClass('on')){
                    var html = '<i class="on"></i>已关注';
                    var htmlId = 'gag_ygz';
                }else{
                    var html = '<i></i>关注Ta';
                    var htmlId = 'gag_gz';
                }
                $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_jrzbj" class="" onclick="gagBtn($(this))">进入直播间</div>');
                if(!ios){
                    $('.gagbody').css('margin','-2rem 0 0 -7.5rem');
                }
                $('#gagbox').show();
            }else{//其他管理员
                if(fromId==_userId){
                    $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
                    $('.gagbodyx').hide();
                }else{
                    $('.gagbtn').html('<div id="gag_gb" class="on" onclick="gagBtn($(this))" style="width: 100%;font-size: .75rem;">对方为管理员无法禁言</div>');
                }
                if(!ios){
                    $('.gagbody').css('margin','-2rem 0 0 -7.5rem');
                }
                $('#gagbox').show();
            }
        }
    }else{
        //学生点击只显示头像
        fromId = $(this).parent('li').attr('from');
        $('.gaginfo>span').eq(1).html('ID:'+fromId);
        $('.gagname').html($(this).parent('li').attr('name'));
        $('#gagimg').attr('src',$(this).parent('li').attr('img'));
        var result = ZAjaxRes({url: "/user/liveRoomFollowCount.user?userId="+fromId,type: "GET"});
        $('.gaginfo>span').eq(0).html('关注:'+result.data.userFollowCount);
        if(fromId==TeacherFrom){//老师
            if($('.clickFollows').hasClass('on')){
                var html = '<i class="on"></i>已关注';
                var htmlId = 'gag_ygz';
            }else{
                var html = '<i></i>关注Ta';
                var htmlId = 'gag_gz';
            }
            $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_jrzbj" class="" onclick="gagBtn($(this))">进入直播间</div>');
         }else{
            $('.gagbodyx').hide();
            $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
        }
        if(!ios){
            $('.gagbody').css('margin','-2rem 0 0 -7.5rem');
        }
        $('#gagbox').show();
    }
    if(endTime){
        $('.Gagbox').hide();
    }
});
//弹框操作按钮
function gagBtn(_this){
    var _this = _this.attr('id');
    switch (_this) {
        case 'gag_gb': //关闭弹框
            $('#gagbox').hide();
            break;
        case 'gag_jy': //禁言
            Gag();
            $('#gagbox').hide();
            break;
        case 'gag_jcjy': //解除禁言
            cancel();
            $('#gagbox').hide();
            break;
        case 'gag_gz': //关注Ta
            follow($('.clickFollows'), 0);
            $('#gagbox').hide();
            break;
        case 'gag_ygz': //已关注
            follow($('.clickFollows'), 1);
            $('#gagbox').hide();
            break;
        case 'gag_jrzbj':
            window.location.href = '/weixin/liveRoom?id='+roomId;
            break;
        default:
            orewardIco = ''
            break;
    }
}
//设置禁言
function Gag(){
    chatroom.markChatroomGaglist({
        account: fromId,
        isAdd: true,
        done: markChatroomGaglistDone
    });
    return false;
}

//取消禁言
function cancel(){
    chatroom.markChatroomGaglist({
        account: fromId,
        isAdd: false,
        done: markChatroomGaglistDone
    });
}
//删除指定数组里的元素
function removeByValue(arr, val) {
    for(var i=0; i<arr.length; i++) {
        if(arr[i] == val) {
            arr.splice(i, 1);
            break;
        }
    }
};

function markChatroomGaglistDone(error, obj) {
    console.log('添加聊天室禁言名单' + (!error?'成功':'失败'), error, obj.member);
    if(!error){
        if(obj.member.valid){
            //alert('禁言成功')
            $.getJSON("/gag/setGay.user?courseId="+courseId+"&userId="+fromId+"&optId="+_userId, function (data) {

            });

        }else{
            //alert('解除禁言成功')
            $.getJSON("/gag/delGay.user?courseId="+courseId+"&userId="+fromId+"&optId="+_userId, function (data) {

            });
        }

    }else{
        console.log(error.message)
    }
    $('#gagbox').hide();
}

function onChatroomWillReconnect(obj) {
    // 此时说明 `SDK` 已经断开连接, 请开发者在界面上提示用户连接已断开, 而且正在重新建立连接
    console.log('即将重连', obj);
};
function onChatroomDisconnect(error) {
    // 此时说明 `SDK` 处于断开状态, 开发者此时应该根据错误码提示相应的错误信息, 并且跳转到登录页面
    console.log('连接断开', error);
    if (error) {
        switch (error.code) {
            // 账号或者密码错误, 请跳转到登录页面并提示错误
            case 302:
                break;
            // 被踢, 请提示错误后跳转到登录页面
            case 'kicked':
                if (error.reason === "managerKick") {
                    pop({"content": "你已被管理员移出" ,width:"8rem", "status": "error", time: '1000'});
                } else if (error.reason === "blacked") {
                    pop({"content": "你已被管理员拉入黑名单，不能进入" ,width:"13rem", "status": "error", time: '1000'});
                } else if (error.reason === "chatroomClosed") {
                    localStorage.setItem("currentTime"+_chatRoomId,'00');
                    pop({"content": "直播间已关闭" ,width:"8rem", "status": "error", time: '1000'});
                }
                setTimeout(function(){
                    window.location.href = "/weixin/courseInfo?id=" + courseId;
                },1000);
                break;
            default:
                break;
        }
    }
};
function onChatroomError(error, obj) {
    console.log('发生错误', error, obj);
};
//收到的聊天室消息
function onChatroomMsgs(msgs) {
    //console.log('收到聊天室消息', msgs);
    //会话UI
    $.each(msgs, function () {
        addMsg(this, 1);
    });
};
//发送聊天室消息
function say() {
    //发普通送的消息
    var text =$.trim($('#say').val());
    if (text&&msgOk) {
        text = filter(text);
        //发普通文本消息
        var msg = chatroom.sendText({
            text: text,
            done: sendChatroomMsgDone
        });
        msgOk = false;
        //$('#say').focus();
    } else {
        return false;
    }
};
//按回车发送
function keySay(){
    if (event.keyCode==13  && Pc)  //回车键的键值为13
        document.getElementById("send").click(); //调用登录按钮的登录事件
}
//提问
function quiz(){
    //发送提问的消息
    var text = $.trim($('#say').val());
    if (text&&msgOk) {
        text = filter(text);
        //发提问消息
        var content = {
            type: 5,
            data: {
                value: text
            }
        };
        var msg = chatroom.sendCustomMsg({
            content: JSON.stringify(content),
            done: sendChatroomMsgDone
        });
        msgOk = false;
    }else {
        return false;
    }
};
//打赏消息
function rewardMsg(number,userRewardTypeId){
    //发送打赏消息
    var text = number;
    if (text) {

        var content = {
            type: 11,
            data: {
                value: text,
                ids : userRewardTypeId,
            }
        };
        var msg = chatroom.sendCustomMsg({
            content: JSON.stringify(content),
            done: sendChatroomMsgDone
        });
        msgOk = false;
    }else {
        return false;
    }

};

//发送聊天室消息
function sendChatroomMsgDone(error, msg) {
    msgOk = true;
    if (!error) {
        //会话UI
        addMsg(msg, 1);
    } else {
        pop({"content": error.message , "status": "error", time: '1000'});
    }
    //初始化输入框
    $('#say').val('').css('height','.9rem');
};

//获取历史消息记录
function historyMsgs() {
    chatroom.getHistoryMsgs({
        timetag: startTime,
        limit: 100,
        done: getHistoryMsgsDone
    });
};
//历史消息
function getHistoryMsgsDone(error, obj) {
    var msg = obj.msgs;
    if (!error) {
        $.each(msg, function () {
            //当前时间
            startTime = this.time;

            if(startTime>cleanScreenTime){
                //console.log(this);
                //会话UI
                addMsg(this, 0,'isShow');
            }
        });
        hisOk = true;
        var boxH2 = $('#sayslist').outerHeight();
        //console.log(boxH,boxH2);
        $('#sayslist').parents('.bd').scrollTop(boxH2-boxH+15);
        var oLiLength =  $("#sayslist li").length;
        if (msg.length == 0&&oLiLength>5) {
            $('#megMore').html('<h6 class="disabled">没有更多历史消息了！</h6>').show();
            return;
        }else {
            $('#megMore').html('<h6></h6>').hide();
        }
    } else {
        console.log(error.message)
    }
};

//************************************公共函数***********************************//
//补零
function toDou(n){
    return n>=10?n+'':'0'+n;
};

//倒计时
function tick(){
    var oNow = new Date();
    var oS = parseInt((startTimer-oNow.getTime())/1000);
    if(oS<=0){
        $(".ewmbox").removeClass("on");
        return
    }
    //5.通过时间差，求天，小时，分钟，秒数
    var d = parseInt(oS/86400);
    var h = parseInt((oS%86400)/3600);
    var m = parseInt(((oS%86400)%3600)/60);
    var s = parseInt(((oS%86400)%3600)%60);;
    //6.把得到的时间，给元素的内容。
    var str = toDou(d) + toDou(h) + toDou(m) + toDou(s);
    //console.log(str)
    for(var i=0;i<str.length;i++){
        $('.timerbox img').eq(i).attr('src','/web/res/image/'+str.charAt(i)+'.png');
    }
};

//时间戳转时间
function getLocalTime(time){
    var oDate=new Date();
    oDate.setTime(time);
    return oDate.getFullYear()+'年'+toDou(oDate.getMonth()+1)+'月'+toDou(oDate.getDate())+'日 '+toDou(oDate.getHours())+':'+toDou(oDate.getMinutes())+':'+toDou(oDate.getSeconds());
};

//时间戳转时间
function getLocalTime2(time){
    var oDate=new Date();
    oDate.setTime(time);
    if(oDate.getHours()<12){
        return '上午 '+oDate.getHours()+':'+toDou(oDate.getMinutes());
    }else if(oDate.getHours()==12){
        return '中午 '+oDate.getHours()+':'+toDou(oDate.getMinutes());
    }else if(oDate.getHours()>12){
        return '下午 '+oDate.getHours()%12+':'+toDou(oDate.getMinutes());
    }
};


//获取课件
function coursewares(arr) {
    if (arr.length == 0) {
        if(liveWay == 0){//视频就隐藏
            $('.courseware').remove();
            //$('.txMegs').css('top','10.5rem')
        }else{//语音就默认一张
            $('#toppic>ul').append('<li onclick="coursewareClickImg(this)" class="swiper-slide"><img src="' + _coverssAddress + '"/></li>');
        }
    } else {
        $.each(arr, function () {
            var picsrc = this.address;
            $('#toppic>ul').append('<li onclick="coursewareClickImg(this)" class="swiper-slide"><img src="' + picsrc + '"/></li>');
        });
        $('.courseware').show();
    }
};

//点击课件放大图片
function coursewareClickImg(imgurl){
    var imgsrc = $(imgurl).children().attr('src');
    var imgToppicSrc = [];
    for(var i = 0;i < $(imgurl).parent().children("li").length;i++){
        var imgsrcL = $(imgurl).parent().children("li").eq(i).children().attr('src');
        imgToppicSrc.push(imgsrcL);
    }
    console.log(imgToppicSrc)
    if(imgsrc != undefined){
        wx.previewImage({
            current: imgsrc,
            urls: imgToppicSrc
        });
    }
}

// 直播创建会话UI函数
function  addMsg(json, node,isShow) {
    var type = json.type; //收到消息类型
    var fromNick = json.fromNick; //呢称
    var fromAvatar = json.fromAvatar || '/web/res/image/01.png'; //头像
    var from = json.from; // 账号
    console.log(json)
    //是否是老师发的消息
    if (from == TeacherFrom) {
        var isTeacher = '<span>老师</span><i></i>';
    }else{
        var isTeacher = '';
    }

    //是否是自己发的消息
    if (from == _userId) {
        var direction = 'megRight';
    }else{
        var direction = 'megLeft';
    }

    var oLi = '';
    switch (type) {
        //文本消息///////////////
        case 'text':
            if (node) {
                if(json.time-megTime>60000){
                    var src = getLocalTime2(json.time);
                    $('#sayslist').append('<li class ="trter2"><h4>'+src+'</h4></li>');
                }
                megTime = json.time;
            }

            var text = json.text; //文本消息内容
            oLi = ('<li  from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + ' clearfix">\
                <div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName">'+ isTeacher + fromNick + '</div>\
					<div class="messageBox Text"><p>' + escapeHtml(text) + '</p></div>\
				</div>\
            </li>');
            break;

        //自定义消息///////////////
        case 'custom':
            if (!json.content) {
                return;
            }
            var content = JSON.parse(json.content);
            if (content.type == 5) {
                if (node) {
                    if(json.time-megTime>60000){
                        var src = getLocalTime2(json.time);
                        $('#sayslist').append('<li class ="trter2"><h4>'+src+'</h4></li>');
                    }
                    megTime = json.time;
                }
                megTime = json.time;
                //学生提问
                oLi = ('<li from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
					<div class="messageName">'+ isTeacher + fromNick + '</div>\
						<div class="messageBox Custom"><p><span class="customBg"></span>' + escapeHtml(content.data.value) + '</p></div>\
					</div>\
				</li>');
            } else if (content.type == 6) {
                //老师回答
                oLi = ('<li  class="' + direction + '">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
					<div class="messageName">'+ isTeacher + fromNick + '</div>\
						<div class="messageBox Answer"><p><i>@' + content.data.nick + '</i>' + escapeHtml(content.data.value) + '</p><span class="answerBg"></span></div>\
					</div>\
				</li>');
            } else if (content.type == 7) {
                if (node) {
                    //推流地址改变
                    videoUrl = content.data.hlsLiveAddress;
                    $('#video').attr('src', videoUrl);
                    $('#sayslist').append('<li class = "trter"><h5>老师直播开始了！请点击视频开始！</h5></li>');
                }
            } else if(content.type == 8) {
                //oLi = ('<li class = "trter"><h5>视频直播已链接！</h5></li>');
                //openorcClose = true;
                if(node){
                    Connected = 1;
                    msgLS = false;
                    $('.dialog-box').remove();
                    if(ios){
                        if(openorcClose == false){
                            testPlay();
                            openorcClose = true;
                        }
                    }else if(Pc){
                        window.location.reload();
                    }else{
                        testPlay();
                    }
                }else if(msgLS){
                    Connected = 1;
                    msgLS = false;
                    if(ios){
                        //ovload();
                    }else{
                        testPlay();
                    }
                }
            } else if(content.type == 9){

                //oLi = ('<li><h5>视频直播已断开！</h5></li>');
                if(node){
                    if(Connected == 1){
                        var contentTypeNum = $('#contentTypeNum').val();
                        if(contentTypeNum == ''){
                            //console.log('当前网络不佳');
                            //pop({"content": "当前网络不佳！" , "status": "error", time: '1000'});
                        }
                    }
                    Connected = 0;
                    msgLS = false;
                    if(Android){
                        testPause();
                    }
                }else if(msgLS){
                    //Connected = 0;
                    console.log('断');
                    msgLS = false;
                    if(Android){
                        testPause();
                    }
                }
            } else if(content.type == 11){
                if(content.data.ids){
                    var IDS = (content.data.ids).toString();
                }else{
                    var IDS = '0';
                }
                switch (IDS) {
                    //打赏消息///////////////
                    case '1':
                        var	orewardIco = '<img src="/web/res/image/orewardIco1.png">'
                        var orewardTxt = '掌声'
                        break;
                    case '2':
                        var	orewardIco = '<img src="/web/res/image/orewardIco2.png">'
                        var orewardTxt = '小星星'
                        break;
                    case '3':
                        var	orewardIco = '<img src="/web/res/image/orewardIco3.png">'
                        var orewardTxt = '么么哒'
                        break;
                    case '4':
                        var	orewardIco = '<img src="/web/res/image/orewardIco4.png">'
                        var orewardTxt = '学士帽'
                        break;
                    default:
                        var	orewardIco = ''
                        break;
                }
                if(IDS!='0'){
                    oLi = ('<li class="trters">\
							<div class="trbox">\
								<div class="trboxs">\
									<dl>\
										<dt><img src="'+ fromAvatar + '"></dt>\
										<dd>\
											<h3>'+fromNick+'</h3>\
											<h4>送了一个'+orewardTxt+'</h4>\
										</dd>\
									</dl>\
									<div class="trimg">'+orewardIco+'</div>\
								</div>\
								<div class="x1"><img src="/web/res/image/x1.png"></div>\
							</div>\
						</li>');
                }else{
                    oLi = '';
                }
            }else if(content.type == 12){//控制课件
                if(node&&courselist.length > 0){
                    setTimeout(function(){
                        courseware.slideTo(content.data.value,1000,false);
                    },10000);
                }
            }else if(content.type == 13){//添加管理员
                if(node){
                    oLi = ('<li class="trter"><h5>'+content.data.name+'已被提升为管理员</h5></li>');
                    admins.push(content.data.value);
                    if (content.data.value == _userId) {
                        $('.clearScreens').show();
                    }
                }
            }else if(content.type == 14){//解除管理员
                if(node){
                    oLi = ('<li class="trter"><h5>'+content.data.name+'已被解除管理员身份</h5></li>');
                    removeByValue(admins,content.data.value);
                    if (content.data.value == _userId) {
                        $('.clearScreens').hide();
                    }
                }
            }else if(content.type == 18){//清屏
                if(node){
                    clearInterval(timers2);
                    $('#sayslist').html('<li id="megMore"></li>');
                    pop({"content": "管理员已对不良信息进行处理！" ,width:"12rem", "status": "error", time: '1000'});
                    timers2 = setTimeout(function(){
                        $('.qpxx').hide();
                    },3000);
                    cleanScreenTime = json.time;
                }
            }else if(content.type == 24){
                if(isShow != 'isShow'){
                    $('#contentTypeNum').val(content.type);
                    openorcClose = false;
                    BaseFun.Dialog.Pop2({
                        title: '提示',
                        text : '老师正在赶来的路上',
                        confirm:'我知道了'
                    });
                    setTimeout(function(){
                        $('#contentTypeNum').val('');
                    },3000)
                }
            }
            break;

        //通知类消息///////////////
        case 'notification':
            var notype = json.attach.type;
            var timer = parseInt(json.time);
            var date = getLocalTime(timer).substring(5,17);
            //if (notype == 'memberExit' && node ) {
            //    pop({"content": "老师正在赶来的路上" , "status": "error", time: '3000'});
            //}
            if (notype == 'memberEnter' && node) {
                if (from == _userId) {
                    userEnterList.push('您');   //  往用户进入数组中添加数据
                }else{
                    userEnterList.push(subStringMax(json.attach.fromNick,5));   //  往用户进入数组中添加数据
                }
                if(userEnterRight == null){
                    userEnterRightFun();
                }
            }else if (notype == 'gagMember') {

                ///当有人被加入禁言名单
                $('#sayslist').append('<li class="trter"><h5>' + json.attach.toNick + '已被禁言</h5></li>');
                if(node){
                    gags.push(json.attach.to[0]);
                }
            } else if (notype == 'ungagMember') {
                //当有人被移除禁言名单时
                $('#sayslist').append('<li class="trter"><h5>' + json.attach.toNick + '已被解除禁言</h5></li>');
                if(node){
                    removeByValue(gags,json.attach.to[0]);
                }
            }
            break;
        default:
            break;
    }
    //消息时间
    if(oLi!=''){
        if (node) {
            $('#sayslist').append(oLi);
            if($('#sayslist').outerHeight()>$('#bd_box').outerHeight()){
                $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
            }else{
                $('#sayslist').parents('.bd').scrollTop(0);
            }
        } else {
            $('#megMore').after(oLi);
        }
    }
};

function subStringMax(str,max){
    var max = max || 5;
    var newStr = str;
    if(newStr.length > max){
        newStr = newStr.substring(0,max)+'...';
    }
    return newStr;
};

//自动查询进入用户名单-右侧滑动块
function userEnterRightFun(){
    userEnterRight = setInterval(function(){
        if(userEnterList.length){
            $('.user_right em').html(userEnterList[0]+'进入直播间');
            $('.user_right').show().removeClass('_fadeOut _left').addClass('_slideInRight');
            userEnterList.shift();
            setTimeout(function(){
                $('.user_right').addClass('_fadeOut').removeClass('_slideInRight');
                setTimeout(function(){
                    $('.user_right').hide().addClass('_left');
                },1000)
            },1000);
        }
    },2000);
}

/*********************回放页面函数********************/
function playback() {
    Connected = 1;
    $('.send_message').remove();
    $('#adv').css('bottom','1rem');
    $('.more_btn').css('bottom','1rem');
    $('#sayslist').html('');
    $('.BeginPlay').show();

    var offSet = 1;
    var playOK = true;
    Load();

    //上拉加载
    $('#bd_box').scroll(function(){
        var scrollTop = $(this).scrollTop();
        var windowHeight = $(this).height();
        var scrollHeight = $('.says').height();
        if(scrollTop + windowHeight >= scrollHeight-30){
            Load();
        }
    });

    //请求数据
    function Load(){
        if(!playOK){return false;}
        var result = ZAjaxRes({url: "/weixin/live/getHistoryMsg.user", type: "GET", param:{courseId: courseId, offSet: offSet}});
        console.log(result);
        if (result.code == "000000") {
            var data = result.data;
            $.each(data, function (i,json) {
                playbackMsg(json)
            });
            offSet++;
            if(data.length == 0){
                playOK = false;
            }
        }
    };
};


// 回放创建会话UI函数
function playbackMsg(json){

    var fromAvatar = json.fromAvator || '/web/res/image/01.png'; //头像
    var type = json.msgType; //消息类型
    var fromNick = json.fromNick; //呢称
    var from = json.fromAccount; // 账号

    //是否是老师发的消息
    if (from == TeacherFrom) {
        var isTeacher = '<span>老师</span> · ';
    }else{
        var isTeacher = '';
    }

    //是否是自己发的消息
    if (from == _userId) {
        var direction = 'megRight';
        //老师自己看回放
        if (from == TeacherFrom) {
            direction = 'megLeft';
        }
    }else{
        var direction = 'megLeft';
    }

    if (type == 'TEXT') {//普通消息
        var text = json.attach; //消息内容
        $('#sayslist').append('<li from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + '">\
						<div class="headImg"><img src="' + fromAvatar + '"></div>\
						<div class="messageMain">\
							<div class="messageName">'+ isTeacher + fromNick + '</div>\
							<div class="messageBox Text"><p>' + escapeHtml(text) + '</p></div>\
						</div>\
					</li>');
    } else if (type == 'CUSTOM') {//自定义消息
        var attach = JSON.parse(json.attach);
        var text = attach.data.value; //消息内容
        var nick = attach.data.nick;//@人
        if (attach.type == 5) {
            $('#sayslist').append('<li from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + '">\
						<div class="headImg"><img src="' + fromAvatar + '"></div>\
						<div class="messageMain">\
						<div class="messageName">'+ isTeacher + fromNick + '</div>\
							<div class="messageBox Custom"><p><span class="customBg"></span>' + escapeHtml(text) + '</p></div>\
						</div>\
					</li>');
        } else if (attach.type == 6) {
            $('#sayslist').append('<li from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + '">\
						<div class="headImg"><img src="' + fromAvatar + '"></div>\
						<div class="messageMain">\
						<div class="messageName">'+ isTeacher + fromNick + '</div>\
							<div class="messageBox Answer"><p><i>@' + nick + '</i>' + escapeHtml(text) + '</p><span class="answerBg"></span></div>\
						</div>\
					</li>');
        } else if(attach.type == 11){
            if(attach.data.ids){
                var IDS = (attach.data.ids).toString();
            }else{
                var IDS = '0';
            }
            switch (IDS) {
                //打赏消息///////////////
                case '1':
                    var	orewardIco = '<img src="/web/res/image/orewardIco1.png">'
                    var orewardTxt = '掌声'
                    break;
                case '2':
                    var	orewardIco = '<img src="/web/res/image/orewardIco2.png">'
                    var orewardTxt = '小星星'
                    break;
                case '3':
                    var	orewardIco = '<img src="/web/res/image/orewardIco3.png">'
                    var orewardTxt = '么么哒'
                    break;
                case '4':
                    var	orewardIco = '<img src="/web/res/image/orewardIco4.png">'
                    var orewardTxt = '学士帽'
                    break;
                default:
                    var	orewardIco = ''
                    break;
            }
            if(IDS!='0'){
                $('#sayslist').append('<li class="trters">\
							<div class="trbox">\
								<div class="trboxs">\
									<dl>\
										<dt><img src="'+ fromAvatar + '"></dt>\
										<dd>\
											<h3>'+fromNick+'</h3>\
											<h4>送了一个'+orewardTxt+'</h4>\
										</dd>\
									</dl>\
									<div class="trimg">'+orewardIco+'</div>\
								</div>\
								<div class="x1"><img src="/web/res/image/x1.png"></div>\
							</div>\
						</li>');
            }
        }
    }
};

$(function(){
    var winHeight = $(window).height();
    if(Android){
        $('#content_box').css({
            position:'absolute',
            height:winHeight,
            bottom:0,
            width:'100%'
        })
    }
    $('#say').off('click',asmk);
    $('#say').on('click',asmk);
    function asmk(){
        var str= navigator.userAgent.toLowerCase();
        var ver=str.match(/cpu iphone os (.*?) like mac os/);
        if(ios){
            var ver1 = ver[1].replace(/_/g,".");
            if(ver1.slice(0,4) != '11.2'){
                setTimeout(function(){
                    window.scrollTo(0,winHeight);
                },300);
            }
        }
        if(Android){
            setTimeout(function(){
                window.scrollTo(0,winHeight);
            },300);
        }
    }
    //**************屏幕自适应
    function autoWH() {
        //加载时适应浏览器高度
        var width = $(window).outerWidth();
        var height = $(window).outerHeight();
        var topHeight = $('.live_box').position().top;
        $('.live_box,.tabBox').css('height', height - topHeight);
        $('#bd_box').css('height', height - topHeight-$('.test_play').outerHeight());
    }
    autoWH();
    $(window).resize(function() {
        //改变窗体大小时适应浏览器高度
        autoWH();
    });
    $('#say').on('focus',function(){
        //setTimeout(function(){
        //    $('.reward2').hide();
        //    $('.more_btn').hide();
        //},500)
    });
    $('#say').on('blur',function(){
        //$('.reward2').show();
        //$('.more_btn').show();
    });
    $('.more_btn').on('click',function(){
        $('.more_btn_pop_bg').show();
    });

    $('.more_btn_pop_close').on('click',function(){
        $('.more_btn_pop_bg').hide();
    });


});
//返回顶部
$(function() {
    var timerBack = null;
    $('#bd_box').scroll(function(){
        clearTimeout(timerBack);
        if ($('#bd_box').scrollTop()>=300){
            $("#back_box").stop(true,true).fadeIn(500);
            timerBack = setTimeout(function(){
                $("#back_box").stop(true,true).fadeOut(500);
            },3000);
        }
    });
    $("#back-to-top").click(function(){
        $('#bd_box').animate({scrollTop:0},500);
        return false;
    });
    $("#back-to-bottom").click(function(){
        $('#bd_box').animate({scrollTop:$('#sayslist').height()},500);
        return false;
    });
});

//转义特殊字符
function escapeHtml(string) {
    var entityMap = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': '&quot;',
        "'": '&#39;',
        "/": '&#x2F;'
    };
    return String(string).replace(/[&<>"'\/]/g, function (s) {
        return entityMap[s];
    });
};

//打赏功能
//打赏显示隐藏
$(function(){
    $('.reward_box').hide();
    $('.reward_box').css({opacity:'1'})
});

$('.reward2').click(function(){
    $('.reward_box').show();
    $('.reward_box h6').html('');
    var  data  = getUserRewardType();//获取打赏类型,pay.js
    var accountAmount = data.accountAmount;
    var userRewardList = data.userRewardList;
    var elem_obj = $("#rewardList .swiper-wrapper");
    elem_obj.empty();
    for(var i = 0 ; i < userRewardList.length ; i ++ ){
        var userReward = userRewardList[i];
        var on = "";
        if(i == 0)on ="on";
        var div = '<div class="swiper-slide ' + on + '" userRewardTypeId="' + userReward.id + '" amount="' + userReward.amount + '">' +
            '<img src="'+userReward.picAddress+'">' +
            '<span>' + userReward.amount + '学币</span>' +
            '<strong>'+userReward.remark+'</strong>'
        '</div>';
        elem_obj.append(div);
    }
    $("#balance").html(accountAmount);
    balance = accountAmount;
    balanceTwo = accountAmount;
    /*打赏*/
    var rewardList = new Swiper('#rewardList', {
        slidesPerView: 4,
        paginationClickable: true,
        spaceBetween: 0
    });

    statisticsBtn({'button':'021','referer':'','courseId':courseId })
});
//点击打赏
var aRewardNo = true;
$('.aReward').click(function () {
    if(aRewardNo == true){
        $(this).addClass('aRewardNo');
        aRewardNo = false;
        if(balanceTwo==0){
            rexb()
        }
        var userRewardType = $('#rewardList .on');
        var userRewardTypeId = userRewardType.attr("userRewardTypeId")
        var amount = userRewardType.attr("amount");//金额
        if(!userRewardTypeId) {
            pop({"content": "请选择要打赏的金额!", "status": "error", time: '1000'});
            return;
        }
        var balance = $("#balance").html();
        //if(parseFloat(amount) > parseFloat(balance)){
        //rexb();
        //$("#balanceText").text("您的余额不足，请充值。");
        //}
        userReward2(userRewardTypeId , courseId , amount);//打赏 pay.js
        setTimeout(function(){
            $('.aReward').removeClass('aRewardNo');
            aRewardNo = true;
        },500);
    }
});

function userRewardCallback(result , param) {


    console.log(result);

    if(result.code == "000000"){
        data = result.data;
        var userRewardTypeId = param.payTypeId
        $("#balance").html(data.afterBalance);
        if(_isEnd == '1'){
            switch (userRewardTypeId) {
                //打赏消息///////////////
                case '1':
                    var	orewardIco = '<img src="/web/res/image/orewardIco1.png">'
                    var orewardTxt = '掌声'
                    break;
                case '2':
                    var	orewardIco = '<img src="/web/res/image/orewardIco2.png">'
                    var orewardTxt = '小星星'
                    break;
                case '3':
                    var	orewardIco = '<img src="/web/res/image/orewardIco3.png">'
                    var orewardTxt = '么么哒'
                    break;
                case '4':
                    var	orewardIco = '<img src="/web/res/image/orewardIco4.png">'
                    var orewardTxt = '学士帽'
                    break;
                default:
                    var	orewardIco = ''
                    break;
            }

            $('#sayslist').append('<li class="trters">\
							<div class="trbox">\
								<div class="trboxs">\
									<dl>\
										<dt><img src="'+ _photo + '"></dt>\
										<dd>\
											<h3>我</h3>\
											<h4>送了一个'+orewardTxt+'</h4>\
										</dd>\
									</dl>\
									<div class="trimg">'+orewardIco+'</div>\
								</div>\
								<div class="x1"><img src="/web/res/image/x1.png"></div>\
							</div>\
						</li>');
            $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
        }else{
            rewardMsg(param.amountAMT,userRewardTypeId);
        }
    }else if(result.code == "100002"){
        rexb();
        pop({"content": "您的余额不足，请充值!" , "status": "error", time: '1000'});
    }else{
        pop({"content": result.message , "status": "error", time: '1000'});
    }


}
/**
 * 用户打赏
 * @param userRewardTypeId 用户选择打赏类型Id
 * @param courseId 课程Id
 */
function userReward2(userRewardTypeId , courseId , amount){
    var result = ZAjaxRes({url:"/thirdPay/userRewardPay.user", type: "POST",param:{payType:"07" , courseId:courseId
        , payTypeId:userRewardTypeId,count:"1" , amountAMT:amount
    } , async:true
        , callback:userRewardCallback});

};
//点击空白区域关闭
$(document).click(function(e){
    var _con = $('.reward_box,.reward2');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.reward_box').hide();
    }
});
//点击空白区域关闭禁言
$(document).click(function(e){
    var _con = $('#sayslist li[from]');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.GagBox').remove().hide();
    }
});
$('.live_box').click(function(){
    $('.reward_box').hide();
})


//打赏切换
$('#rewardList').on('click','#rewardList .swiper-slide',function(){
    $('#rewardList .swiper-slide').removeClass('on');
    var userRewardTypeId = $(this).attr("userRewardTypeId")
    //statisticsBtn({'button':'010','referer':'','courseId':courseId,'rewardId':userRewardTypeId});
    $(this).addClass('on');
});
$('.clickFollow').on('click',function(){
    $('.new_ewmbox').addClass("on");
    console.log(qrCode);
    $('.imgsrc').attr('src',qrCode);
})
$('.clickFollows').on('click',function(){
    if ($(this).hasClass("on")) {
        follow(this, 1);
    } else {
        follow(this, 0);
    }
})

function follow(obj, isFollow) {
    if (isFollow == 0) {
        var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + roomId, type: "GET"});
        if (result.code == "000000" || result.code == "000111") {
            $(obj).addClass("on");
            $(obj).find("i").html("已关注");
        }
    } else {
        var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + roomId, type: "GET"});
        if (result.code == "000000") {
            $(obj).removeClass("on");
            $(obj).find("i").html("关注");
        }
    }
}



$(".qdBtn").click(function () {
    $(".new_ewmbox").removeClass("on");
})
$('.new_ewmbox').on('click',function(e) {
    if (this == e.target) {
        $(this).removeClass("on");
    }
})
function rexb(){
    statisticsBtn({'button':'011','referer':'','courseId':courseId})
    $(".reward_box").hide();
    var result = ZAjaxRes({url: "/iosPayType/getIosPayInfo.user?type=1"});
    if (result.code == "000000") {
        $("#rech").empty();
        isMobile = result.ext;
        var html = '';
        $.each(result.data, function (i, item) {
            var d = '<div class="swiper-slide"> <p onclick="size($(this))" typeId="'+item.id+'" typeAmount="'+item.amount+'">';
            var w = '<span class="firstSpan">'+item.userReallyAmount+'学币</span>'
            if(i == "0"){
                d = '<div class="swiper-slide on"> <p class="on" onclick="size($(this))" typeId="'+item.id+'" typeAmount="'+item.amount+'">'
                w = '<span class="firstSpan on">'+item.userReallyAmount+'学币</span>'
                payId = item.id;
                payAmount = item.amount;
            }
            html += d + w +
                '<span>￥'+item.amount+'</span> ' +
                '</p> </div>';
        })
        $("#rech").append(html);
    }
    if(!Pc){
        $(".rechargeBox").show();
    }
    //alert($("#balance").text());
    $("#payBalance").text("余额:"+$("#balance").text());
    //if(balance == '0'){
    //	$("#balanceText").text("您的余额不足，请充值。");
    //}
    var rechargeList = new Swiper('#rechargeList', {
        slidesPerView: 4,
        paginationClickable: true,
        spaceBetween: 24
    });
}
