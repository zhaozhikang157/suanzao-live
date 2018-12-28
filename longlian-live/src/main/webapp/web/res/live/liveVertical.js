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
//开始时间
var startTimer = '';
//结束时间
var endTime = '';
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
//四个头像
var showUsers = null;
var showUserids = null;
var userCount = null;
//头像是否在滚动中
var showTier = true;
var _index = 0;
//老师的appId
var teacherId = 0;
var timers = null;
var timers2 = null;
var showtimer = null;
//清屏时间
var cleanScreenTime = '';
var nshow = 1;
//0 是横屏 1是竖屏
var isVerticalScreen = '';
//1是录播的课
var isRecorded = '0'
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
var openorcClose= true;// 监听推流状态
//ovload();
$(document).ready(function(){
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
            console.log('基本信息',data);
            iosCache = false;
            data = data.data;
            loginMobile = data.loginMobile;
            chargeAmt = data.course.chargeAmt;
            isInviteCode = data.course.isInviteCode;
            teacherId = data.course.appId;
            TeacherFrom = data.course.appId;
            roomId = data.course.roomId;
            liveWay =data.course.liveWay;
            isVerticalScreen = data.course.isVerticalScreen;
            isRecorded = data.course.isRecorded;
            admins = data.managerId.split(",");
            admins.push(TeacherFrom+'');
            mustShareTime =  data.course.mustShareTime;
            cleanScreenTime = data.course.cleanScreenTime;
            if(data.course.cleanScreenTime==''){cleanScreenTime = 0;}
            gags = data.gagUserId.split(",");
            courseClassIndex = data.courseClassIndex;
            //头像滚动
            userCount = data.userCount;
            showUsers = data.showUsers;
            //showUserids = chunk(data.showUserids,20);
            //创建头像div
            userCountFn(userCount);
            //头像滚动
            //showUseridsFn(n);
            showUsersFn(showUsers);
            qrCode = data.qrCode ;
            //如果是老师
            console.log(teacherId == _userId)
            if(teacherId == _userId){
                $('.clickFollow').show();
                $('#mesGift').remove();
                //$('.contributionList').css('top','2.5rem');
                $(".clickFollowsS ").hide();
            }else{
                $('.clickFollow').show();
            }
            $('#Number').html(userCount+'人');
            $('#CourseTitle>p').html('课程标题：'+data.course.liveTopic);
            $('.TeacherCurrency').html(data.incomeAmt+'学币');
            $('.TeacherName').html(data.teacherName)
            $('.TeacherImg>img').attr('src',data.teacherPhoto);
            $(".TeacherInfo").attr('from',teacherId);
            $(".TeacherInfo").attr('name',data.teacherName);
            $(".TeacherInfo").attr('img',data.teacherPhoto);
            //课程结束时间，如果不为空课程已结束
            endTime = data.course.endTime;
            //课程开始时间
            startTimer = data.course.startTime;
            //系统时间
            serverTime = data.serverTime;
            if(isRecorded=='1'){
                clearTimeout(sTimer);
                var sTimer = setTimeout(function(){
                    serverTime+=1000;
                },1000);
            }
            //广告链接
            if(data.course.adAddress){
                $(".mesCoursewares").hide();
                $("#spmore,.newlj").show();
                $("#adAddress").click(function(){
                    window.location.href=""+data.course.adAddress+""
                })
            }
            if (endTime) {
                if(liveWay == 0){//如果是视频直播
                    if (_playAddress == '') {
                        $('#sayslist').append('<li>提示：视频正在录制中，请稍后再看回放！</li>');
                        pop({"content": "视频正在录制中，请稍后" ,width:"10rem" ,"status": "error", time: '2000'});
                    }else if(_playAddress == 'novideo'){
                        $('#sayslist').append('<li>提示：该课程授课期间未直播</li>');
                        pop({"content": "该课程授课期间未直播" , "status": "error", time: '2000'});
                    }
                    var videoPlayOnOff = true;
                    $('.videoBody,.reward_box_bg').click(function(){
                        if(ios){
                            getBuffered(); //视频加载缓冲条
                        }
                        if($('.BeginPlay').is(":hidden")){
                            clearTimeout(timerKj);
                            $('.videoKj').css('opacity','1');
                            $('.background_bottom_a').show();
                            timerKj = setTimeout(function(){
                                $('.videoKj').css('opacity','0');
                                $('.background_bottom_a').hide();
                            },3000);
                        }
                    });
                    //回放的试看
                    if(_trySeeTime>0&&admins.indexOf(_userId)==-1){
                        if(localStorage.getItem("trySeeTime"+_chatRoomId)){
                            if(isInviteCode=='0'){
                                $('.invitationCode').remove();
                            }
                            $('.purchase').html(chargeAmt+'学币购买');
                            $('#mesGift,#mesNews,.videoKj,.background_bottom_a,.BeginPlay,.Downloading').remove();
                            $('.trialEnd').show();
                            if(sourcesId>"0"){
                                $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                            }
                        }else{
                            localStorage.setItem("trySeeTime"+_chatRoomId,true);
                            if(isInviteCode=='0'){
                                $('.invitationCode').remove();
                            }
                            $('.purchase').html(chargeAmt+'学币购买');
                            $('.trial,.invitationCode').show();
                            $('#mesGift,#mesNews,.videoKj,.background_bottom_a').remove();
                            $('.test_play_content').show();
                            $('.trial span').html('可试看'+_trySeeTime+'秒').css('color','#fff');
                            clearTimeout(trySee1);
                            var trySee1 = setInterval(function(){
                                _trySeeTime--;
                                $('.trial span').html(_trySeeTime+'s').css('color','#d53c3e');
                                if(_trySeeTime=='0'){
                                    clearTimeout(trySee1);
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
                $("#mesGift").show();
                playback();
                showUsersFn(showUsers);
            } else {
                $("#mesNews,#mesGift").show();
                if(liveWay == 0){//如果是视频直播
                    //试看倒计时
                    if(_trySeeTime>0&&admins.indexOf(_userId)==-1){
                        $("#mesNews").css("display","none");
                        if(isInviteCode=='0'){
                            $('.invitationCode').remove();
                        }
                        $('.purchase').html(chargeAmt+'学币购买');
                        //试看时不显示
                        $('.videoKj,#mesGift,#mesNews').remove();
                        //试看过后
                        if(localStorage.getItem("trySeeTime"+_chatRoomId)){
                            $('.trial,div.invitationCode').hide();
                            $('.trialEnd').show();
                            if(sourcesId>"0"){
                                $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                            }
                            //视频暂停
                            if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
                                myPlayer.pause();
                            }
                            ztOK = false;
                            testPause();
                            $('.BeginPlay').remove();
                        }else{//第一次试看
                            localStorage.setItem("trySeeTime"+_chatRoomId,true);
                            clearTimeout(trySee);
                            $('.trial,.invitationCode').show();
                            var trySee = setInterval(function(){
                                _trySeeTime--;
                                $('.trial span').html(_trySeeTime+'s');
                                if(_trySeeTime=='0'){
                                    clearTimeout(trySee);
                                    $('.trial,div.invitationCode').hide();
                                    $('.trialEnd').show();
                                    if(sourcesId>"0"){
                                        $(".trialEnd h2").text("试看结束,购买系列课后观看完整视频")
                                    }
                                    //视频暂停
                                    if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
                                        myPlayer.pause();
                                    }
                                    ztOK = false;
                                    testPause();
                                    $('#video').attr('src','');
                                    $('.BeginPlay').remove();
                                }
                            },1000);
                        }
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
                if(data.isFollowThirdOfficial!='0'&&data.qrCode&&!sessionStorage.getItem("qrCode")){
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
            _step++
            //加载完了
            if (_isEnd == '0') {
                if (_step >= 2) {
                    loadDone()
                }
            }
        } else {
            pop({"content": data.message, "status": "error", time: '1000'});
        }
    });
});
$('.clickFollow').on('click',function(){
    if(Android){
        var result = ZAjaxRes({url: "/publicConcern",type: "get"});
        var success= result.success;
        var ermUrl = result.data;
        if(success){
           window.location=ermUrl
        }
    }else{
        $('.new_ewmbox').addClass("on");
        $('.imgsrc').attr('src',qrCode);
    }
})
$(".qdBtn").click(function () {
    $(".new_ewmbox").removeClass("on");
})
$('.new_ewmbox').on('click',function(e){
    if(this == e.target){
        $(this).removeClass("on");
    }
});
$('.live_box').on('click',function(){
    if(endTime){   //如果结束时间为true
        clearTimeout(timerKj);
        if($('.BeginPlay').css('display') == 'none'){
            $('.videoKj').css('opacity','1');
            $('.background_bottom_a').show();
        }
        $('.pro_box2').hide();
        timerKj = setTimeout(function(){
            $('.videoKj').css('opacity','0');
            $('.pro_box2').show();
            $('.background_bottom_a').hide();
        },3000);
    }
})
//************************************云信公共sdk函数***********************************//
/**正在直播**/
function onChatroomConnect(chatroom) {
    console.log('进入聊天室', chatroom);
    if (!videoing) {
        ovload();
    } else {
        oV.play();
    }
    //设置获取历史记录时间
    startTime = chatroom.member.enterTime;
    $('#sayslist').html('<li id="megMore"></li>');
    $('#GetInto>div').html('你进入了课堂');
    $('#GetInto').stop().animate({left:'.5rem'}).delay(3000).animate({left:'-15rem'});
    $('#sayslist').append('<li class="megs">酸枣提倡绿色健康的直播课程，同时对直播内容24小时在线监测。严禁传播各种违法、违规、暴力、涉黄等不良信息，违者将被立即封停。</li>');
    //绑定发送消息事件,先解后绑，避免重复绑定
    $('#send').off('click', say);
    $('#send').on('click', say);
    if (!videoing) {
        $.getJSON("/live/isConnected.user?courseId=" + courseId, function (data) {
            //console.log('获取状态',data);
            if(nshow==1){
                $('.BeginPlay').show();
                nshow++;
            }
            if (data.code == '000000') {
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
//管理员禁言
$('#sayslist,#usersBox,.newpopulation,.TeacherInfo').on('click','#sayslist li span,#usersBox ul li img,.on,.TeacherInfodl',function(){
    $('.gagbodyx').show();
    if(admins.indexOf(_userId)!=-1&&endTime==""){ //自己是管理员
        fromId = $(this).parent('li').attr('from');
        $('.gaginfo>span').eq(1).html('ID:'+fromId);
        $('.gagname').html($(this).attr('name'));
        $('#gagimg').attr('src',$(this).attr('img'));
        //$('.gagbtn').html('<div id="gag_jy" onclick="gagBtn($(this))">禁言</div><div id="gag_tj" onclick="gagBtn($(this))">添加为管理员</div>');
        if($(this).hasClass("on")){
            fromId = $(this).attr('from');
            //if(fromId==_userId){return};
            $('.gaginfo>span').eq(1).html('ID:'+fromId);
            $('.gagname').html($(this).attr('name'));
            $('#gagimg').attr('src',$(this).attr('img'));
            //$('.gagbtn').html('<div id="gag_jy" onclick="gagBtn($(this))">禁言</div><div id="gag_tj" onclick="gagBtn($(this))">添加为管理员</div>');
        }
        if($(this).hasClass("TeacherInfodl")){
            fromId = $(this).parent().attr('from');
            //if(fromId==_userId){return};
            $('.gaginfo>span').eq(1).html('ID:'+fromId);
            $('.gagname').html($(this).parent().attr('name'));
            $('#gagimg').attr('src',$(this).parent().attr('img'));
        }
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
                if($('.clickFollowsS').hasClass('clk')){
                    var html = '<i class="on"></i>已关注';
                    var htmlId = 'gag_ygz';
                }else{
                    var html = '<i></i>关注Ta';
                    var htmlId = 'gag_gz';
                }
                $('.gagbodyx').hide();
                $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_gb" class="" onclick="gagBtn($(this))">我知道了</div>');
                $('#gagbox').show();
            }else{//其他管理员
                if(fromId==_userId){
                    $('.gagbodyx').hide();
                    $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
                }else{
                    $('.gagbtn').html('<div id="gag_gb" class="on" onclick="gagBtn($(this))" style="width: 100%;font-size: .75rem;">对方为管理员无法禁言</div>');
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
        if(fromId==TeacherFrom){
            if($('.clickFollowsS').hasClass('clk')){
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
        if($(this).hasClass("on")){
            fromId = $(this).attr('from');
            //if(fromId==_userId){return};
            $('.gaginfo>span').eq(1).html('ID:'+fromId);
            $('.gagname').html($(this).attr('name'));
            $('#gagimg').attr('src',$(this).attr('img'));
            $('.gagbodyx').hide();
            $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
        }
        if($(this).hasClass("TeacherInfodl")){
            fromId = $(this).parent().attr('from');
            //if(fromId==_userId){return};
            $('.gaginfo>span').eq(1).html('ID:'+fromId);
            $('.gagname').html($(this).parent().attr('name'));
            $('#gagimg').attr('src',$(this).parent().attr('img'));
            if(_userId==TeacherFrom){//老师
                $('.gagbodyx').hide();
                $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
            }else{
                if($('.clickFollowsS').hasClass('clk')){
                    var html = '<i class="on"></i>已关注';
                    var htmlId = 'gag_ygz';
                }else{
                    var html = '<i></i>关注Ta';
                    var htmlId = 'gag_gz';
                }
                $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_jrzbj" class="" onclick="gagBtn($(this))">进入直播间</div>');
            }
        }
        var result = ZAjaxRes({url: "/user/liveRoomFollowCount.user?userId="+fromId,type: "GET"});
        $('.gaginfo>span').eq(0).html('关注:'+result.data.userFollowCount);
        $('#gagbox').show();
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
            follow($('.clickFollowsS'), 0);
            $('#gagbox').hide();
            break;
        case 'gag_ygz': //已关注
            follow($('.clickFollowsS'), 1);
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
/**
 * 加载完了
 */
function loadDone() {
    //如果我是管理员 加清屏
    if(admins.indexOf(_userId)!=-1){
        $("#mesCourseware").hide();
        $('#clearScreen,#spmore').show();
    }else {
        $("#clearScreen").hide();
    }
    //清屏
    $('#clearScreen').click(function(){
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
                    pop({"content": "直播已结束" ,width:"8rem", "status": "error", time: '1000'});
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
        $('.send_message_input').css({zIndex:'-1',opacity:0});
        //$('#say').focus();
    } else {
        if(text == ''){
            pop({'content':'请输入内容','width':'8rem'});
        }
        return false;
    }
};
//按回车发送
function keySay(){
    if (event.keyCode==13 && Pc)  //回车键的键值为13
        document.getElementById("send").click(); //调用登录按钮的登录事件
}
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
    $('#say').val('');
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
            //会话UI
            addMsg(this, 0 ,'isShow');
        });
        if (msg.length < 100) {
            $('#megMore').html('<h6 class="disabled">没有更多历史消息了！</h6>');
            return;
        }else {
            $('#megMore').html('<h6>查看更多历史记录！</h6>');
        }
    } else {
        console.log(error.message)
    }
};
//获取聊天室成员信息
function showUseridsFn(arr){
    chatroom.getChatroomMembersInfo({
        accounts: arr,
        done: getChatroomMembersInfoDone
    });
}
function getChatroomMembersInfoDone(erorr, obj) {
    console.log(obj);
    $.each(obj.members, function (i,n) {
        $('#population .swiper-slide').eq(i+_index*20).find('img').attr('src',n.avatar);
    });
}
//************************************公共函数***********************************//
//显示头像
function showUsersFn(arr){
    $('#population .swiper-wrapper').html('');
    $.each(arr, function (i,n) {
        var photo = this.photo;
        var index = this.index;
        if(index){
            $('#population .swiper-wrapper').append('<div class="swiper-slide on" from ="'+this.id+'"img="'+photo+'" name="'+this.name+'"><img src="'+photo+'"></div>');
        }else{
            $('#population .swiper-wrapper').append('<div class="swiper-slide on"  from ="'+this.id+'"img="'+photo+'" name="'+this.name+'"><img src="'+photo+'"></div>');
        }
    });
};
//创建头像
function userCountFn(str){
    if(str>100){
        str = 100;
    }
    if(str<5){
       $(".Online_population").attr("dir","rtl")
    }else{
        $(".Online_population").attr("dir","ltr")
    }
    $('#population .swiper-wrapper').html('');
    for(var i=0;i<str;i++){
        $('#population .swiper-wrapper').append('<div class="swiper-slide on"><img src="/web/res/image/01.png"></div>');
    }
}
//补零
function toDou(n){
    return n>=10?n+'':'0'+n;
};
//切割数组
function chunk(arr,size){
    var arr1= [];
    for(var i=0;i<arr.length;i=i+size){
        var arr2 =arr;
        arr1.push(arr2.slice(i,i+size));
    }
    return arr1;
}
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
//获取课件
function coursewares(arr) {
    if (arr.length == 0) {
        if(liveWay == 0){
           // $('.txMegs').css('top','10.5rem')
        }
    } else {
        $.each(arr, function () {
            var picsrc = this.address;
            $('#toppic>ul').append('<li onclick="coursewareClickImg(this)" class="swiper-slide"><img src="' + picsrc + '"/></li>');
        });
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
    if(imgsrc != undefined){
        wx.previewImage({
            current: imgsrc,
            urls: imgToppicSrc
        });
    }
}
// 直播创建会话UI函数
function addMsg(json, node ,isShow) {
    var type = json.type; //收到消息类型
    var fromNick = json.fromNick; //呢称
    var fromAvatar = json.fromAvatar || '/web/res/image/01.png'; //头像
    var from = json.from; // 账号
    var nameColor = ""; //老师学生用户名颜色
    //console.log(json)
    //是否是老师发的消息
    if (from == TeacherFrom) {
        var isTeacher = 'Teacher';
        nameColor = "teacherNmaeColor";
    }else if((admins.indexOf(from)!=-1)){
        var isTeacher = 'Teacher';
        nameColor = "teacherNmaeColor";
    }else{
        var isTeacher = '';
        nameColor = "nameColor";
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
            var text = json.text; //文本消息内容
            oLi = ('<div></div><li  from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" ><span class = "'+nameColor+'">'+ fromNick +' : </span>'+ escapeHtml(text)+ '</li>');
            break;
        //自定义消息///////////////
        case 'custom':
            if (!json.content) {
                return;
            }
            var content = JSON.parse(json.content);
            if (content.type == 7) {
                if (node) {
                    //推流地址改变
                    videoUrl = content.data.hlsLiveAddress;
                    $('#video').attr('src', videoUrl);
                    $('#sayslist').append('<div></div><li class="megs">提示：老师直播开始了！</li>');
                }
            } else if(content.type == 8) {
                $('#contentTypeNum').val(content.type);
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
                    testPlay();
                }
            } else if(content.type == 9){
                $('#contentTypeNum').val(content.type);
                if(node){
                    if(Connected == 1){
                        var contentTypeNum = $('#contentTypeNum').val();
                        if(contentTypeNum == ''){
                            console.log('当前网络不佳');
                        }
                    }
                    Connected = 0;
                    msgLS = false;
                    if(Android){
                        testPause();
                    }
                }else if(msgLS){
                    Connected = 0;
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
                        orewardIco = '掌声<em><img src="/web/res/image/orewardIco1.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '2':
                        orewardIco = '小星星<em><img src="/web/res/image/orewardIco2.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '3':
                        orewardIco = '么么哒<em><img src="/web/res/image/orewardIco3.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '4':
                        orewardIco = '学士帽<em><img src="/web/res/image/orewardIco4.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    default:
                        orewardIco = ''
                        break;
                }
                oLi = ('<div></div><li class="trter"><strong style="display:block;font-weight: normal;"><span class="'+nameColor+'">'+fromNick+'</span></br>送了1个'+orewardIco+' </strong></li>')
            }else if(content.type == 12){//控制课件
                if(node&&courselist.length > 0){
                    setTimeout(function(){
                        courseware.slideTo(content.data.value,1000,false);
                    },10000);
                }
            }else if(content.type == 13){//添加管理员
                if(node){
                    oLi = ('<li class="state" style="color: #ffd972;"><span>'+content.data.name+'</span>已被提升为管理员</li>')
                    admins.push(content.data.value);
                    if (content.data.value == _userId) {
                        $('#clearScreen,#spmore').show();
                        $("#mesCourseware").hide();

                    }
                }
            }else if(content.type == 14){//解除管理员
                if(node){
                    removeByValue(admins,content.data.value);
                    if((admins.indexOf(content.data.value)==-1)){
                        nameColor="nameColor"
                    }
                    oLi = ('<li class="state" style="color: #ffd972;"><span>'+content.data.name+'</span>已被解除管理员身份</li>')
                    if (content.data.value == _userId) {
                        $('#clearScreen,#spmore').hide();
                        $("#mesCourseware").show();
                    }
                }
            }else if(content.type == 15){//显示四个头像
                if(node){
                    var Users =eval(content.data.users);
                    showUsersFn(Users);
                }
            }else if(content.type == 16){//直播人数改变
                if(node){
                    $('#Number').html(content.data.value+'人');
                }
            }else if(content.type == 17){//老师收益改变
                if(node){
                    $('.TeacherCurrency').html(content.data.value+'学币');
                }
            }else if(content.type == 18){//清屏
                if(node){
                    clearInterval(timers2);
                    $('#sayslist').html('<li id="megMore"><li class="megs">酸枣提倡绿色健康的直播课程，同时对直播内容24小时在线监测。严禁传播各种违法、违规、暴力、涉黄等不良信息，违者将被立即封停。</li></li>');
                    // $('#sayslist').append('<li class="megs"></li>');
                    pop({"content": "管理员已对不良信息进行处理" ,width:"12rem", "status": "error", time: '1000'});
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
            if (notype == 'memberEnter') {
                if(from == TeacherFrom){
                    $('#GetInto>div').html('老师进入了课堂');
                    $('#GetInto').stop().animate({left:'.5rem'}).delay(3000).animate({left:'-15rem'});
                    //进来不是管理人员且不是自己时,提示进入课堂
                }else if(from != _userId && admins.indexOf(from)==-1){
                    $('#GetInto>div').html(json.attach.toNick+'进入了课堂');
                    $('#GetInto').stop().animate({left:'.5rem'}).delay(3000).animate({left:'-15rem'});
                }
            }
            if (notype == 'gagMember') {
                ///当有人被加入禁言名单
                $('#sayslist').append('<div></div><li class="state" style="color: #4af0ff"><span>' + json.attach.toNick + '</span>已被禁言</li>');
                $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
                if(node){
                    gags.push(json.attach.to[0]);
                }
            }
            if (notype == 'ungagMember') {
                //当有人被移除禁言名单时
                $('#sayslist').append('<div></div><li class="state"style="color: #4af0ff"><span>' + json.attach.toNick + '</span>已被解除禁言</li>');
                $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
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
            $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
        } else {
            $('#megMore').after(oLi);
        }
    }
};
/*********************回放页面函数********************/
function playback() {
    $('#bd_box').css('height','4.9rem');
    $('#mesNews').remove();
    $('#sayslist').html('');
    $('.BeginPlay').show();
    Connected = 1;
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
        if(Pc){
            $("#mesShare").hide();
        }
        if(!playOK){return false;}
        var result = ZAjaxRes({url: "/weixin/live/getHistoryMsg.user", type: "GET", param:{courseId: courseId, offSet: offSet}});
        console.log(result);
        if (result.code == "000000") {
            var data = result.data;
            $.each(data, function (i,json) {
                // playbackMsg(json)
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
        var isTeacher = 'Teacher';
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
        $('#sayslist').append('<li>'+ fromNick +' : '+ escapeHtml(text)+ '</li>');
    } else if (type == 'CUSTOM') {//自定义消息
        var attach = JSON.parse(json.attach);
        var text = attach.data.value; //消息内容

        if(attach.type == 11){
            if(attach.data.ids){
                var IDS = (attach.data.ids).toString();
            }else{
                var IDS = '0';
            }
            switch (IDS) {
                //打赏消息///////////////
                case '1':
                    orewardIco = '掌声<em><img src="/web/res/image/orewardIco1.png"></em><em><img src="/web/res/image/x1.png"></em>'
                    break;
                case '2':
                    orewardIco = '小星星<em><img src="/web/res/image/orewardIco2.png"></em><em><img src="/web/res/image/x1.png"></em>'
                    break;
                case '3':
                    orewardIco = '么么哒<em><img src="/web/res/image/orewardIco3.png"></em><em><img src="/web/res/image/x1.png"></em>'
                    break;
                case '4':
                    orewardIco = '学士帽<em><img src="/web/res/image/orewardIco4.png"></em><em><img src="/web/res/image/x1.png"></em>'
                    break;
                default:
                    orewardIco = ''
                    break;
            }
            $('#sayslist').append('<li class="trter"><strong style="display:block;font-weight: normal;">'+fromNick+'</br>送了1个'+orewardIco+'</strong></li>');
        }
    }
};

//输入框
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
    if(Pc){
        $('#mesNews,#say').off('click',asmk);
        $('#mesNews,#say').on('click',asmk);
    }else{
        $('#mesNews,#say').off('touchstart',asmk);
        $('#mesNews,#say').on('touchstart',asmk);
    }
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
    };
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
$('#mesGift').click(function(){
    $('.reward_box,.reward_box_bg').show();
    $('.reward_box h6').html('');
    var  data  = getUserRewardType();//获取打赏类型,pay.js
    console.log(data);
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
if(!Pc){
    var n = 0;
    $('.aReward').on('touchstart',function () {
        n++;
        aRewrdFun()
    });
}else{
    $('.aReward').on('click',function () {
        aRewrdFun()
    });
}
var aRewardNo = true;
function aRewrdFun(){
    if(aRewardNo == true){
        $('.aReward').addClass('aRewardNo');
        aRewardNo = false;
        statisticsBtn({'button':'009','referer':'','courseId':courseId })
        if(balanceTwo==0){
            rexb()
        }
        var userRewardType = $('#rewardList .on');
        var userRewardTypeId = userRewardType.attr("userRewardTypeId")
        var amount = userRewardType.attr("amount");//金额
        if(!userRewardTypeId) {
            alert("请选择要打赏的金额!");
            return;
        }
        var balance = $("#balance").html();
        var data  = userReward2(userRewardTypeId , courseId);//打赏 pay.js
        if(data){
            $("#balance").html(data.afterBalance);
            if(_isEnd == '1'){

                switch (userRewardTypeId) {
                    //打赏消息///////////////
                    case '1':
                        orewardIco = '掌声<em><img src="/web/res/image/orewardIco1.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '2':
                        orewardIco = '小星星<em><img src="/web/res/image/orewardIco2.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '3':
                        orewardIco = '么么哒<em><img src="/web/res/image/orewardIco3.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    case '4':
                        orewardIco = '学士帽<em><img src="/web/res/image/orewardIco4.png"></em><em><img src="/web/res/image/x1.png"></em>'
                        break;
                    default:
                        orewardIco = ''
                        break;
                }
                $('#sayslist').append('<li class="trter"><strong style="display:block;font-weight: normal;">'+_userName+'<br/>送了1个'+orewardIco+'</strong></li>');
                $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
            }else{
                rewardMsg(amount,userRewardTypeId);
            }
        }
        setTimeout(function(){
            $('.aReward').removeClass('aRewardNo');
            aRewardNo = true;
        },500);
    }
}
/**
 * 用户打赏
 * @param userRewardTypeId 用户选择打赏类型Id
 * @param courseId 课程Id
 */
function userReward2(userRewardTypeId , courseId){
    var result = ZAjaxRes({url:"/thirdPay/userRewardPay.user", type: "POST",param:{payType:"07" , courseId:courseId , payTypeId:userRewardTypeId,count:"1"} });
    if(result.code == "000000"){
        return  result.data;
    }else if(result.code == "100002"){
        rexb();
        pop({"content": "您的余额不足，请充值!" , "status": "error", time: '1000'});
    }else{
        pop({"content": result.message , "status": "error", time: '1000'});
    }
}
//点击空白区域关闭
$(document).click(function(e){
    var _con = $('.reward_box,.send_message,.moGn');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.reward_box,.reward_box_bg').hide();
        $(".moGn ").removeClass("on");
    }
});
//修改内容
$('.reward_box_bg').click(function(){})
$(document).click(function(e){
    var _con = $('#sayslist li[from]');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.GagBox').remove().hide();
    }
});
//打赏切换
$('#rewardList').on('click','#rewardList .swiper-slide',function(){
    $('#rewardList .swiper-slide').removeClass('on');
    $(this).addClass('on');
    statisticsBtn({'button':'010','referer':''});
});
function rexb(){
    statisticsBtn({'button':'011','referer':''})
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
    $("#payBalance").text("余额:"+$("#balance").text());
    var rechargeList = new Swiper('#rechargeList', {
        slidesPerView: 4,
        paginationClickable: true,
        spaceBetween: 24
    });
}
//更多的人
var userOk = false;
var ids = '';
var iDsoffSet = 0;
var allUser = null;
$('#Number').click(function(){
    iDsoffSet = 0;
    userOk = false;
    ids = '';
    allUser = null;
    $('.usersbox').show().scrollTop(0);
    AllUsers();
    statisticsBtn({'button':'014','referer':'','courseId':courseId });
});
$('.usersbox h3 span').click(function(){
    $('.usersbox').hide();
    $('#usersBox ul').html('');
});
//点击空白隐藏课件
$(document).on('click',function(e){
    var _con = $('.usersbox,#Number,#gagbox');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.usersbox').hide();
        $('#usersBox ul').html('');
    }
});
//上拉加载
$('#usersBox').scroll(function(event){
    var scrollTop = $(this).scrollTop();
    var windowHeight = $(this).height();
    var scrollHeight = $('#usersBox ul').height();
    if(scrollTop + windowHeight >= scrollHeight-30){
        AllUsers();
    }
    event.preventDefault();
    return false;
});


//关注
$('.clickFollowsS').on('click',function(){
    if ($(this).hasClass("clk")) {
        follow(this, 1);
    } else {
        follow(this, 0);
    }
})

function follow(obj, isFollow) {
    if (isFollow == 0) {
        var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + roomId, type: "GET"});
        if (result.code == "000000" || result.code == "000111") {
            $(obj).addClass("clk");
            pop1({"content": "已关注" ,width:"6rem", "status": "normal", time: '800'});
        }
    } else {
        var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + roomId, type: "GET"});
        if (result.code == "000000") {
            $(obj).removeClass("clk");
            pop1({"content": "取消关注"  , width:"6rem", "status": "normal", time: '800'});
        }
    }
}
//请求数据
function AllUsers(){
    if(userOk){return false;}
    if(iDsoffSet == 0){
        $('#usersBox ul').html('');
    }
    var result = ZAjaxRes({url: "/live/getAllUsers.user", type: "POST", param:{courseId: courseId, ids:ids}});
    console.log(result);
    if (result.code == "000000") {
        console.log(result)
        if(iDsoffSet == 0){
            allUser = result.data.allUser;
        }
        var users = result.data.users;
        if(allUser.length!= 0){
            iDsoffSet++;
            ids = allUser[iDsoffSet-1];
            $.each(users, function (i,json) {
                $('#usersBox ul').append('<li from ="'+this.id+'" name="'+this.name+'" img="'+this.photo+'"><img src="'+this.photo+'"><span>'+this.name+'</span></li>');
            });
            if(iDsoffSet>allUser.length){
                userOk = true;
            }
        }else{
            $.each(users, function (i,json) {
                $('#usersBox ul').append('<li from ="'+this.id+'" name="'+this.name+'" img="'+this.photo+'"><img src="'+this.photo+'"><span>'+this.name+'</span></li>');
            });
            if($('#usersBox ul').find('li').length  == 0 && $('#usersBox .noData'.length == 0)){
                $('#usersBox ul').append('<div class="noData"></div>')
            }
            userOk = true;
        }
    }
};