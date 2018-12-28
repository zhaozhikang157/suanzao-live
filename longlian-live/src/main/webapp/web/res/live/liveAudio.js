//var vConsole = new VConsole(); //  手机调试模式
//注意这里, 引入的 SDK 文件不一样的话, 你可能需要使用 SDK.Chatroom.getInstance 来调用接口
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

var uuidLens = 0;
// 记录当前正在播放语音的uuid
var _thisAudioUuid = '';
//原课程ID
var _oriCourseId ="";
$(document).ready(function(){
    $('#sayslist').html('');

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
    if(oriCourseId){
        _oriCourseId = oriCourseId;
    }else{
        _oriCourseId = courseId;
    }
    $.getJSON("/weixin/live/getCourse.user?courseId=" + courseId, function (data) {
        if (data.code == '000000') {
            iosCache = false;
            data = data.data;
            //课程结束时间，如果不为空课程已结束
            endTime = data.course.endTime;
            TeacherName = data.teacherName;
            TeacherPhoto = data.teacherPhoto;
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
            gags = data.gagUserId.split(",");
            liveUserConut = data.userCount;
            qrCode = data.qrCode ;
            if(data.course.appId == _userId){
                $('.clickLiveRoom').remove();
            }
            if(qrCode=="") {
                $.getJSON("/qrCode/getWechatOfficialQrCode.user?courseId=" + courseId, function (result) {
                    if (result.code == '000000') {
                        qrCode = result.data;
                    }
                })
            };
            if(courseId.toString().length >= 15){
                TeacherFrom = oriAppId;
                $('.reward').remove();
                if(endTime){
                    $('.quession').css('right','.4rem');
                }else{
                    $('.barrage').css('right','.4rem');
                }
            }else{
                TeacherFrom = data.course.appId;
            }

            admins = data.managerId.split(",");
            admins.push(TeacherFrom+'');

            //判断登录用户是老师还是学生
            if(TeacherFrom == _userId){    //老师
                $('#quiz').remove();
                $('.presay').hide();
                $('#send').hide().css('right','0.45rem');
                $('.reward').remove();
                $('.closeLiveBtn').show();
                $('.invitation_flexd').show();
                $('.clickFollow').remove();
            }else{  //学生
                $('.send_audio_btn').remove();
                $('.send_text_icon').remove();
                $('.send_img').remove();
                $('#say').css('width','8.8rem').attr("placeholder","说点什么...");
                $('.questions_btn').css({'position':'absolute','left':'0.3rem','bottom':'0.6rem','marginLeft':'0'});
                $('.addadmin').remove();
                $('.deleteadmin').remove();
                $('.invitation_flexd').remove();
                $('.publicPopups').remove();
                $('.closeLiveBtn').remove();
                $('.reward').show();
                $('.clickFollow').show();
                $('.tab_box').append($('.quiz_list_btn'));
                $('.quiz_list_btn').eq(1).remove();
                $('.tab_div').append($('.quiz_list_ul'));
                $('.quiz_list_ul').eq(1).remove();
                ////是否扫描二维码
                //是否扫描二维码
                //&&!sessionStorage.getItem("qrCode")
                //&&data.qrCode


                var oNow = new Date();

                var oSs = parseInt((liveStartTime-oNow.getTime())/1000);
                //开始时间，已
                if(oSs<=0 || _isEnd == '1'){
                    $(".timerbox").html("").hide();
                    $('.kfewm').css('height','13.7rem');
                } else {
                    var isNotShow = sessionStorage.getItem('ewm'+courseId+'_'+_userId);
                    //没有点击我知道了
                    if (isNotShow != '1' && data.isFollowThirdOfficial!='0') {
                        $(".ewmbox").addClass("on");
                    }
                }
                clearTimeout(timers);
                $('.imgsrc').attr('src',qrCode);
                tick();
                timers = setInterval(tick,1000);


            }


            $('.send_message').show();
            //设置弹幕
            var flag = localStorage.getItem("barrage_" + courseId);
            if (flag) {
                barrageChecked = flag;
            }
            if (barrageChecked == "0") {
                $('.barrage').toggleClass('barrage_active');
                $('.barrage_list_box').toggle();
            }
            if (endTime) {
                $("#sayslistOneData").hide();
                $('.closeLiveBtn').remove();
                $('.publicPopups').remove();
                $('.live_user_conut').html(liveUserConut+'人');
                //结束时间 直播已结束看回放
                playback();
                $('.live_status').html('<em></em> 直播已结束');
                $(".quession").show().css("bottom","0.6rem");
                if(courseId.toString().length >= 15){
                    $('.barrage').css('right','2.95rem');
                }else{
                    $(".barrage").css("right","5.7rem");
                }
                $(".barrage_box").css("bottom","3rem");
                $(".barrage,.reward,.invitation_flexd").css("bottom","0.6rem");
                $(".bd").css("padding-bottom","0.95rem");
                $(".back_active_positions").css("bottom","0.6rem")
            } else {
                $("#sayslistOneData").show();
                //学生设置老师输入状态
                if (TeacherFrom != _userId && data.teacherInputing == "1") {
                    receTeacherInputing("老师正在输入中...");
                }
                // 直播状态
                var statrTime_inow = serverTime - liveStartTime;
                var liveSetinter = null;
                if(statrTime_inow >= 0){
                    $('.live_status').addClass('live_status_red').html('<em></em> 直播中');
                }else{
                    $('.live_status').html('<em></em> 直播未开始');
                    var endlive = parseInt((-statrTime_inow)/1000);
                    liveSetinter = setInterval(function(){
                        endlive--;
                        if(endlive <= 0){
                            $('.live_status').addClass('live_status_red').html('<em></em> 直播中');
                            clearInterval(liveSetinter);
                        }
                    },1000);
                }
                $('.live_user_conut').html(liveUserConut+'人');
            }
            //课件
            courselist = data.courseWare;
            coursewares(courselist);
            var kjInit = true;
            var ChatRoomSwiperIndex = BaseFun.GetStorage('ChatRoomSwiperIndex'+courseId+'_'+_userId);
            ChatRoomSwiperIndex = ChatRoomSwiperIndex != null ? ChatRoomSwiperIndex : 0;
            ChatRoomSwiperIndex = ChatRoomSwiperIndex != null ? ChatRoomSwiperIndex : 0;
            courseware = new Swiper('#toppic', {
                pagination: '.toppic_pag',
                paginationType: 'fraction',
                initialSlide:parseInt(ChatRoomSwiperIndex),
                paginationClickable: true,
                autoplay:0,
                autoplayDisableOnInteraction:false,
                observer:true,
                preventLinksPropagation : true,
                observeParents:true,
                onSlideChangeEnd: function(swiper){
                    if(!kjInit){
                        BaseFun.SetStorage('ChatRoomSwiperIndex'+courseId+'_'+_userId,swiper.activeIndex);
                        if(_userId == TeacherFrom){
                            TeacherKjSwiper(swiper.activeIndex);
                        }
                    }
                    kjInit = false;
                }
            });
            _step++;
            //加载完了,如果是已经结束，就不加载了
            if (_isEnd == '0') {
                if (_step >= 2) {
                    loadDone()
                }
            }
        }  else {
            alert(data.message);
        }
    });

    //获取更多历史消息
    //$('#sayslist').on('click','#megMore',function () {
    //    mp3Inow = false;
    //    historyMsgs();
    //});
    var fig = true;
    $('#getQR').click(function(){
        if(fig){
            $.getJSON("/course/getQrAddress.user?courseId=" + courseId+'&roomId='+roomId, function (data) {
                if (data.code == '000000') {
                    var src = data.data;
                    $('.imgsrc').attr('src',src);
                    fig = false;
                }else{
                    alert('获取二维码失败！')
                }
            });
        }
    });
});

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
//************************************云信公共sdk函数***********************************//
/**正在直播**/
function onChatroomConnect(chatroom) {
    console.log('进入聊天室', chatroom);
    arrMp3url = [];
    console.log('测试数组',arrMp3url);
    //设置获取历史记录时间
    startTime = chatroom.member.enterTime;

    //绑定发送消息事件,先解后绑，避免重复绑定
    $('#send').off('click', say);
    $('#send').on('click', say);
    //绑定发送提问事件,先解后绑，避免重复绑定
    $('#quiz').off('click', quiz);
    $('#quiz').on('click', quiz);


    _step++
    //加载完了
    if (_step >= 2 && !isChatRoomReconnect) {
        loadDone()
    }

};
//聊天弹幕添加管理员和禁言
$('.barrage_list_box').on('click','.head_img',function(event){
    adminsClick($(this),event);
    event.stopPropagation();
});
$('.barrage_quiz_common').on('click','.p_headImg',function(event){
    adminsClick($(this),event);
    event.stopPropagation();
});
$('#sayslist').on('click','.headImg img',function(event){
    if($(this).parent().parent().attr('id') != 'sayslistOneData'){
        adminsClick($(this),event);
    }
    event.stopPropagation();
});
/**
 * 加载完了
 */
function loadDone() {
    //获取聊天室历史记录100
    historyMsgs();


}
var isCFollow = isCourseFollow;
function adminsClick($this,event){
    $('.gagbodyx').show();
    //自己是管理员
    if(admins.indexOf(_userId)!==-1&& endTime==''){
        fromId = $this.parent().parent().attr('from');
        fromIdName = $this.parent().parent().attr('name');
        $('.gaginfo>span').eq(1).html('ID:'+fromId);
        $('.gagname').html($this.parent().parent().attr('name'));
        $('#gagimg').attr('src',$this.parent().parent().attr('img'));
        var result = ZAjaxRes({url: "/user/liveRoomFollowCount.user?userId="+fromId,type: "GET"});
        $('.gaginfo>span').eq(0).html('关注:'+result.data.userFollowCount);
        //排查管理人员
        if(admins.indexOf(fromId)===-1){
            $('#gagbox').show();
            //判断 禁言 解除禁言
            if(gags.indexOf(fromId)===-1){
                //禁言
                $('.gagbtn').html('<div id="gag_jy" onclick="gagBtn($(this))" style="width: 100%;">禁言</div>');
                if(_userId == TeacherFrom){
                    $('.gagbtn').html('<div id="gag_jy" onclick="gagBtn($(this))">禁言</div><div id="gag_tj" onclick="gagBtn($(this))">添加为管理员</div>');
                }
            }else{
                //解除禁言
                $('.gagbtn').html('<div id="gag_jcjy" class="on" onclick="gagBtn($(this))" style="width: 100%;">解除禁言</div>');
                if(_userId == TeacherFrom){
                    $('.gagbtn').html('<div id="gag_jcjy" class="on" onclick="gagBtn($(this))">解除禁言</div><div id="gag_tj" class="no" onclick="gagBtn($(this))">添加为管理员</div>');
                }
            }
        }else{
            if(fromId == TeacherFrom){
                if(_userId == TeacherFrom){
                    $('.gagbodyx').hide();
                    $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
                }else{
                    if(courseId.toString().length >= 15){
                        if(isCFollow){
                            var html = '<i class="on"></i>已关注';
                            var htmlId = 'gag_ygz';
                        }else{
                            var html = '<i></i>关注Ta';
                            var htmlId = 'gag_gz';
                        }
                    }else{
                        if($('.clickLiveRoom').hasClass('on')){
                            var html = '<i class="on"></i>已关注';
                            var htmlId = 'gag_ygz';
                        }else{
                            var html = '<i></i>关注Ta';
                            var htmlId = 'gag_gz';
                        }
                    }
                    $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_jrzbj" class="" onclick="gagBtn($(this),'+roomId+')">进入直播间</div>');
                }
            }else{
                if(fromId==_userId){
                    $('.gagbodyx').hide();
                    $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
                }else{
                    if(_userId == TeacherFrom){
                        $('.gagbtn').html('<div id="gag_jy" class="no" onclick="gagBtn($(this))">禁言</div><div id="gag_jcgly" class="on" onclick="gagBtn($(this))">解除管理员</div>');
                    }else{
                        $('.gagbtn').html('<div id="gag_gb" class="on" onclick="gagBtn($(this))" style="width: 100%;font-size: .75rem;">对方为管理员无法禁言</div>');
                    }
                }
            }
            $('#gagbox').show();
        }
    }else{
        //学生点击只显示头像
        fromId = $this.parent().parent().attr('from');
        $('.gaginfo>span').eq(1).html('ID:'+fromId);
        $('.gagname').html($this.parent().parent().attr('name'));
        $('#gagimg').attr('src',$this.parent().parent().attr('img'));
        var result = ZAjaxRes({url: "/user/liveRoomFollowCount.user?userId="+fromId,type: "GET"});
        $('.gaginfo>span').eq(0).html('关注:'+result.data.userFollowCount);
        if(fromId==TeacherFrom){//老师
            if(_userId != fromId){
                if(courseId.toString().length >= 15){
                    if(isCFollow){
                        var html = '<i class="on"></i>已关注';
                        var htmlId = 'gag_ygz';
                    }else{
                        var html = '<i></i>关注Ta';
                        var htmlId = 'gag_gz';
                    }
                }else{
                    if($('.clickLiveRoom').hasClass('on')){
                        var html = '<i class="on"></i>已关注';
                        var htmlId = 'gag_ygz';
                    }else{
                        var html = '<i></i>关注Ta';
                        var htmlId = 'gag_gz';
                    }
                }
                $('.gagbtn').html('<div id="'+htmlId+'" class="" onclick="gagBtn($(this))">'+html+'</div><div id="gag_jrzbj" class="" onclick="gagBtn($(this),'+roomId+')">进入直播间</div>');
            }else{
                $('.gagbodyx').hide();
                $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
            }
        }else{
            $('.gagbodyx').hide();
            $('.gagbtn').html('<div id="gag_gb" class="" onclick="gagBtn($(this))" style="width: 100%;">我知道了</div>');
        }
        $('#gagbox').show();
    }
};
//弹框操作按钮
function gagBtn(_this,_roomId){
    var _this = _this.attr('id');
    var _liveRoomId = '';
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
            isCFollow = true;
            follow($('.clickLiveRoom'), 0);
            $('#gagbox').hide();
            break;
        case 'gag_ygz': //已关注
            isCFollow = false;
            follow($('.clickLiveRoom'), 1);
            $('#gagbox').hide();
            break;
        case 'gag_tj': //添加管理员
            setAdmin();
            $('#gagbox').hide();
            break;
        case 'gag_jcgly': //接触管理员
            deleteAdmin();
            $('#gagbox').hide();
            break;
        case 'gag_jrzbj':
            if(oriRoomId == ''){
                _liveRoomId = roomId;
            }else{
                _liveRoomId = oriRoomId;
            }
            sessionStorage.setItem("need-refresh", true);
            window.location.href = '/weixin/liveRoom?id='+_liveRoomId;
            break;
        default:
            orewardIco = ''
            break;
    }
}
//老师点击撤回
$('#sayslist').on('click','.cancelmsg',function(e){
    var _thisParent = $(this).parent().parent().parent().parent();
    var msgClientId = _thisParent.attr('uuid');
    cancelMsg(_thisParent,msgClientId);
    e.stopPropagation();
});
//老师撤回消息
function cancelMsg(_thisParent,msgid){
    BaseFun.Dialog.Config({
        title: '提示',
        text : '撤回该消息?',
        cancel:'取消',
        confirm:'确认',
        callback:function(index) {
            if(index == 1){
                _thisParent.prev('.divDates').remove();
                _thisParent.remove();
                $.getJSON("/msgCancel/cancelMsgOption.user?chatRoomId="+_chatRoomId + "&courseId=" + courseId+"&msgClientId="+msgid, function (data) {
                    pop({'content':'撤回成功','width':'6.5rem'});
                    if(msgid == _thisAudioUuid){
                        $('.back_active_positions').removeClass('fadeInLeft').addClass('fadeOutLeft');
                        $('#audio')[0].pause();
                    }
                });
            }
        }
    });
};

var setInow = true;
//设置管理员
function setAdmin(){
    if(gags.indexOf(fromId)===-1){
        if(!setInow){return false;}
        setInow = false;
        //alert('添加管理员成功')
        $.getJSON("/courseManager/createManagerReal.user?userId="+fromId + "&courseId=" + courseId, function (data) {
            var content = {
                type: 13,
                data: {
                    id:fromId,
                    name:fromIdName,
                    value: fromId
                }
            };
            var msg = chatroom.sendCustomMsg({
                content: JSON.stringify(content),
                done: sendChatroomMsgDone
            });
            $('#gagbox').hide();
            setInow = true;
        });
    }
};
var setInows = true;
//解除管理员
function deleteAdmin(){
    //alert('解除管理员成功')
    //console.log(fromId , courseId);
    if(!setInows){return false;}
    setInows = false;
    $.getJSON("/courseManager/delManagerRealById.user?userId="+fromId + "&courseId=" + courseId, function (data) {
        var content = {
            type: 14,
            data: {
                id:fromId,
                name:fromIdName,
                value: fromId
            }
        };
        var msg = chatroom.sendCustomMsg({
            content: JSON.stringify(content),
            done: sendChatroomMsgDone
        });
        $('#gagbox').hide();
        setInows = true;
    });
};

//设置禁言
function Gag(){
    if(admins.indexOf(fromId)===-1){
        chatroom.markChatroomGaglist({
            account: fromId,
            isAdd: true,
            done: markChatroomGaglistDone
        });
    }else{
        $('.Gagbox').show();
    }
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
    isChatRoomReconnect = true;
    $('.audioloadIcon').parent().parent().parent().parent().remove();

};
function onChatroomDisconnect(error) {
    // 此时说明 `SDK` 处于断开状态, 开发者此时应该根据错误码提示相应的错误信息, 并且跳转到登录页面
    console.log('连接断开', error);
    $('.audioloadIcon').parent().parent().parent().parent().remove();
    if (error) {
        switch (error.code) {
            // 账号或者密码错误, 请跳转到登录页面并提示错误
            case 302:
                break;
            // 被踢, 请提示错误后跳转到登录页面
            case 'kicked':
                if (error.reason === "managerKick") {
                    pop({"content": "你已被管理员移出" ,width:"8rem", time: '1000'});
                } else if (error.reason === "blacked") {
                    pop({"content": "你已被管理员拉入黑名单，不能进入" ,width:"13rem", time: '1000'});
                } else if (error.reason === "chatroomClosed") {
                    pop({"content": "直播间已关闭" ,width:"8rem", time: '1000'});
                }
                window.location.href = "/weixin/courseInfo?id=" + courseId;
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
    console.log('收到聊天室消息', msgs);
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
        inputCancel();
        if(_userId == TeacherFrom){
            $('#send').hide();
            $('.questions_btn,#send_img').show();
        }
        msgOk = false;
    } else {
        if(text == ''){
            pop({"content": "请输入内容" ,width:"7rem",  time: '1000'});
        }
    }
};

//按回车发送
function keySay(){
    if (event.keyCode==13)  //回车键的键值为13
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
        inputCancel();
        msgOk = false;
    }else {
        pop({"content": "请输入内容" ,width:"7rem",  time: '1000'});
    }
};
var teacherInputMsg = true;
function inputEnter(){
    if(teacherInputMsg){
        var content = {
            type: 19,
            data: {
                value: '老师正在输入中...'
            }
        };
        var inputEnterMsg = chatroom.sendCustomMsg({
            content: JSON.stringify(content),
            done: sendChatroomMsgDone
        });
        teacherInputMsg = false;
    }else{
        return false;
    }
};
function inputCancel(){
    var content = {
        type: 20,
        data: {
            value: '老师取消输入...'
        }
    };
    var inputEnterMsg = chatroom.sendCustomMsg({
        content: JSON.stringify(content),
        done: sendChatroomMsgDone
    });
}
//  老师滑动课件
function TeacherKjSwiper(index){
    var content = {
        type: 12,
        data: {
            value: index
        }
    };
    var inputEnterMsg = chatroom.sendCustomMsg({
        content: JSON.stringify(content),
        done: sendChatroomMsgDone
    });
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
        if(msg.type == 'text' ||  (msg.content && JSON.parse(msg.content).type == "5")){
            $('#say').val('').css('height','0.8rem');
        }
    } else {
        if ( $(".pop_wrap").length > 0 ) {
            $(".pop_wrap").remove();
        }
        if(error.message=="您已被禁言"){
            pop({"content": error.message ,width:"7rem",time: '1000'});
        }else{
            pop({"content": error.message ,time: '1000'});
        }
    }
};

var ajaxSwitch = true;
//获取历史消息记录
function historyMsgs(msgId) {
    //   从云信获取历史消息
    //chatroom.getHistoryMsgs({
    //    timetag: startTime,
    //    limit: 100,
    //    done: getHistoryMsgsDone
    //});
    if(!ajaxSwitch){return;}
    ajaxSwitch = false;
    //    从自己后台获取历史消息
    var result = ZAjaxRes({url: "/chatRoom/findMsgByCourseId.user",type: "GET", param:{courseId: _oriCourseId, pageSize: 20,msgId:msgId || 0}});
    if(result.code == '000000'){
        var msgListHtml = [];
        for(var i = 0; i<result.data.length; i++){
            msgListHtml.unshift(HistaddMsg(result.data[i]));
        };
        $('#sayslist').prepend(msgListHtml.join(''));
        for(var j = 0;j < $('#sayslist>li[data-type=AUDIO]').length;j++){
            $('#sayslist>li[data-type=AUDIO]').eq(j).attr('uuidLen',j);
            console.log(uuidLens = j);
        }
        if(mp3Inow){
            if(_userId != TeacherFrom && arrMp3url.length){
                iNow = arrMp3url.length - 1;
                //if(Android){
                //    ImageLoadEnd(1);
                //}else{
                var mainContainer = document.querySelectorAll('.Audio')[iNow];
                if($('.messageBox>p>.resendIcon').eq(iNow).css('display') == 'none'){   //判断最后一条语音是否播放过
                    HtmlScrollBottom();
                }else{
                    BaseFun.Dialog.Pop({
                        title: '正在直播中',
                        text : '是否收听最新一条语音',
                        cancel:'一会再说',
                        confirm:'自动播放',
                        callback:function(index){
                            if(index == 1){
                                ImageLoadEnd(1);
                            }else{
                                HtmlScrollBottom();
                            }
                        }
                    });
                }
                //}
                mp3Inow = false;
            }else{
                ImageLoadEnd();
            }
        };

        if(result.data.length < 20 ){
            $('#sayslist').prepend('<li  class="megLeft clearfix" id="sayslistOneData"><div class="headImg"><img src="/web/res/image/newaudio/default@2x.png"></div>\
				<div class="messageMain">\
				<div class="messageName">酸枣在线</div>\
				<div class="messageBox Text"><p>欢迎进入酸枣语音直播间！</p></div>\
				</div></li>');
            histDataNo = false;
        }
        ajaxSwitch = true;
    }else{

        if(result.code == '000110' && $('#sayslistOneData').length == 0){
            $('#sayslist').prepend('<li  class="megLeft clearfix" id="sayslistOneData"><div class="headImg"><img src="/web/res/image/newaudio/default@2x.png"></div>\
                    <div class="messageMain">\
                    <div class="messageName">酸枣在线</div>\
                    <div class="messageBox Text"><p>欢迎进入酸枣语音直播间！</p></div>\
                    </div></li>');
            ImageLoadEnd(3);
        }
        ajaxSwitch = false;
    }
};


//云信历史消息处理
function getHistoryMsgsDone(error, obj) {
    var msg = obj.msgs;
    if (!error) {
        $.each(msg, function () {
            //当前时间
            startTime = this.time;
            //会话UI
            addMsg(this, 0);

        });
        if( mp3Inow){
            if(_userId != TeacherFrom && arrMp3url.length){
                iNow = arrMp3url.length - 1;
                if(Android){
                    audio(iNow);
                }else{
                    scrollToLocation(iNow);
                }
                mp3Inow = false;
            }else{
                ImageLoadEnd();
            }
        }
        if (msg.length < 100) {
            $('#megMore').html('<h6 class="disabled"></h6>').hide();
            $('#sayslistOneData').html('<div class="headImg"><img src="/web/res/image/newaudio/default@2x.png"></div>\
				<div class="messageMain">\
				<div class="messageName">酸枣在线</div>\
				<div class="messageBox Text"><p>欢迎您进入直播间!</p></div>\
				</div>').show();
            return;
        }else {
            $('#megMore').html('<h6>点击查看更多历史消息</h6>');
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

//时间戳转时间
function getLocalTime(time){
    var oDate=new Date();
    var bDate = new Date(oDate.getFullYear()+'/'+(oDate.getMonth()+1)+'/'+oDate.getDate()+' 0:0:0').getTime()/1000;
    var msgTimes = new Date(time);
    var conutTimes = msgTimes.getTime()/1000 - bDate;
    if(conutTimes >= 0){
        if(conutTimes <= 43200){
            return '上午 '+toDou(msgTimes.getHours())+':'+toDou(msgTimes.getMinutes());
        }else{
            return '下午 '+toDou(msgTimes.getHours()-12)+':'+toDou(msgTimes.getMinutes());
        }
    }else{
        var thisTimes = new Date(msgTimes.getFullYear()+'-'+(msgTimes.getMonth()+1)+'-'+msgTimes.getDate()+' 0:0:0').getTime()/1000;
        if(msgTimes.getTime()/1000 - thisTimes <= 43200){
            return msgTimes.getFullYear()+'年'+toDou(msgTimes.getMonth()+1)+'月'+toDou(msgTimes.getDate())+'日 上午 '+toDou(msgTimes.getHours())+':'+toDou(msgTimes.getMinutes());
        }else{
            return msgTimes.getFullYear()+'年'+toDou(msgTimes.getMonth()+1)+'月'+toDou(msgTimes.getDate())+'日 下午 '+toDou(msgTimes.getHours()-12)+':'+toDou(msgTimes.getMinutes());
        }
    }
};

//时间戳转1987-10-10 10:33:33
function DateTimeSetDate(time){
    var oDate=new Date(time);
    return oDate.getFullYear()+'-'+toDou(oDate.getMonth()+1)+'-'+toDou(oDate.getDate())+' '+toDou(oDate.getHours())+':'+toDou(oDate.getMinutes())+':'+toDou(oDate.getSeconds())
};

$(function(){
    //document.addEventListener("visibilitychange", function(){
    //    if(document.hidden) {
    //       console.log('页面被挂起');
    //    }
    //    else {
    //       console.log('页面呼出'+iNow);
    //    }
    //});

    FastClick.attach(document.body);
    //点击开始录音
    var itme = 0;
    var itmeDown = 6;
    var itmes = null;
    var itmesDown = null;
    var localIds = null;
    var sendAudioInow = false;


    //语音顺序自动播放
    $('#sayslist').on('click', '#sayslist .audioIco', function () {
        var _index = $(this).index('.audioIco');
        mp3Inow = true;
        if (iNow == _index) {
            //自己在加载的时候，不让点
            if (isPlayLoading || $(this).hasClass('loading')) {
                return;
            }
            //end状态，或者重来没加载过
            if ($('#audio')[0].ended || !isLoaded) {
                audio();
            } else if ($('#audio')[0].paused) {
                reAudio();
            } else {

                $(this).removeClass('playIn loading');
                $('#audio')[0].pause();
            }
        } else {
            iNow = _index;
            audio();
        }
    });



    ajax_quiz_list_ul();
    ajax_barrage_list_ul();
    swiperBarrage = new Swiper('#swiperBarrage', {
        direction : 'horizontal',
        autoplay:0,
        observer:true,
        observeParents:true,
        onSlideChangeEnd: function(swiper){
            $('.tab_box span').removeClass('active');
            $('.tab_box span').eq(swiper.activeIndex).addClass('active');
            $('.tab_div div').removeClass('on');
            $('.tab_div div').eq(swiper.activeIndex).addClass('on');
        }
    });

    // 点击打开学生提问列表
    $('.questions_btn,.quession ').on('click',function(){
        swiperBarrage.slideTo(0);
        if(_userId == TeacherFrom){
            $('.barrage_quiz_common .tab_div').animate({scrollTop:0},20);
            $('.barrage_quiz_common_box').show();
            $('.quiz_list_btn').addClass('active');
            $('.barrage_list_btn').removeClass('active');
            $('.quiz_list_ul').addClass('on');
            $('.barrage_list_ul').removeClass('on');
            //ajax_quiz_list_ul();
        }else{
            $('.barrage_quiz_common .tab_div').animate({scrollTop:0},20);
            $('.barrage_quiz_common_box').show();
            $('.quiz_list_btn').removeClass('active');
            $('.barrage_list_btn').addClass('active');
            $('.quiz_list_ul').removeClass('on');
            $('.barrage_list_ul').addClass('on');
            //ajax_barrage_list_ul();
        }
        $('.tabBox').css({'overflow-y':'hidden'});
    });
    $('.barrage_list_box').on('click','.barrage_quiz_item',function(){
        if(_userId == TeacherFrom){
            swiperBarrage.slideTo(0);
        }else{
            swiperBarrage.slideTo(1);
        }
        $('.barrage_quiz_common .quiz_list_ul').animate({scrollTop:0},20);
        $('.barrage_quiz_common_box').show();
        $('.quiz_list_btn').addClass('active');
        $('.barrage_list_btn').removeClass('active');
        $('.quiz_list_ul').addClass('on');
        $('.barrage_list_ul').removeClass('on');
        //ajax_quiz_list_ul();
        $('.tabBox').css({'overflow-y':'hidden'});
    });

    //点击打开弹幕列表
    $('.barrage_list_box').on('click','.barrage_dm_item',function(){
        if(_userId == TeacherFrom){
            swiperBarrage.slideTo(1);
        }else{
            swiperBarrage.slideTo(0);
        }
        $('.barrage_quiz_common .barrage_list_ul').animate({scrollTop:0},20);
        $('.barrage_quiz_common_box').show();
        $('.quiz_list_btn').removeClass('active');
        $('.barrage_list_btn').addClass('active');
        $('.quiz_list_ul').removeClass('on');
        $('.barrage_list_ul').addClass('on');
        //ajax_barrage_list_ul();\
        $('.tabBox').css({'overflow-y':'hidden'});
    });

    var quizScrollTop = 0;
    var barrageScrollTop = 0;
    // 提问列表弹幕列表切换
    $('.barrage_quiz_common_box .tab_box span ').on('click',function(){
        $('.barrage_quiz_common_box .tab_box span').removeClass('active');
        $(this).addClass('active');
        if($(this).hasClass('quiz_list_btn')){
            //if($('.quiz_list_ul').children('.div_list').length < 1){
            //    ajax_quiz_list_ul();
            //}
            if(_userId == TeacherFrom){
                swiperBarrage.slideTo(0);
            }else{
                swiperBarrage.slideTo(1);
            }
            $('.quiz_list_ul').addClass('on');
            $('.barrage_list_ul').removeClass('on');
            $('.barrage_quiz_common .tab_div').animate({scrollTop:quizScrollTop},20);
        }else if($(this).hasClass('barrage_list_btn')){
            //if($('.barrage_list_ul').children('.div_list').length < 1){
            //    ajax_barrage_list_ul();
            //}
            if(_userId == TeacherFrom){
                swiperBarrage.slideTo(1);
            }else{
                swiperBarrage.slideTo(0);
            }
            $('.quiz_list_ul').removeClass('on');
            $('.barrage_list_ul').addClass('on');
            $('.barrage_quiz_common .tab_div').animate({scrollTop:barrageScrollTop},20);
        }
    });

    $('.barrage_quiz_common .quiz_list_ul').scroll(function(){
        var thisScrollTop = $(this).scrollTop();
        var thisScrollHeight = $(this).height();
        var bottomScrollTop = $(this).children().eq(0).outerHeight()*$(this).children().length;
        quizScrollTop = thisScrollTop;
        if(thisScrollTop+thisScrollHeight >= bottomScrollTop){
            var msgId = $('.quiz_list_ul .div_list:last-child').attr('data-id');
            if(msgId != ''){
                ajax_quiz_list_ul(msgId);
            }
        }
    });
    $('.barrage_quiz_common .barrage_list_ul').scroll(function(){
        var thisScrollTop = $(this).scrollTop();
        var thisScrollHeight = $(this).height();
        var bottomScrollTop = $(this).children().eq(0).outerHeight()*$(this).children().length;
        barrageScrollTop = thisScrollTop;
        if(thisScrollTop+thisScrollHeight >= bottomScrollTop){
            var msgId = $('.barrage_list_ul .div_list:last-child').attr('data-id');
            console.log(msgId)
            if(msgId != undefined){
                ajax_barrage_list_ul(msgId);
            }
        }
    });


    // 发送提问列表请求
    function ajax_quiz_list_ul(msgid){
        var msgId = msgid || 0;
        var param = {msgId: msgId, pageSize: 20};
        var result = ZAjaxRes({url: "/chatRoom/findQuestry.user?courseId=" + _oriCourseId, type: "POST", param:param});
        if (result.code != "000000") {
            if($('.quiz_list_ul').children('.div_list').length == 0){
                $('.quiz_list_ul').html('<div class="noData"></div>');
            }
        }else{
            var html = message_list_update(result.data , tiwens);
            if(result.data.length > 0 && $('.barrage_list_ul').find('.noData').length == 1){
                $('.barrage_list_ul').find('.noData').remove();
            }
            $('.quiz_list_ul').append(html);
        }
    };
    // 发送弹幕列表请求
    function ajax_barrage_list_ul(msgid){
        var msgId = msgid || 0;
        var param = {"msgId": msgId, "pageSize": 20};
        var result = ZAjaxRes({url: "/chatRoom/findAllMsg.user?courseId=" + _oriCourseId, param:param});
        if (result.code != "000000") {
            if($('.barrage_list_ul').children('.div_list').length == 0){
                $('.barrage_list_ul').html('<div class="noData"></div>');
            }
        }else{
            var html = message_list_update(result.data, barrages);
            if(result.data.length > 0 && $('.barrage_list_ul').find('.noData').length == 1){
                $('.barrage_list_ul').find('.noData').remove();
            }
            $('.barrage_list_ul').append(html);
        }
    };

    //用于生成uuid
    function guid() {
        function S4() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        }
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    };

    //点击空白区域关闭提问列表和弹幕列表
    $('.barrage_quiz_common_box').on('click',function(e){
        if(this == e.target){
            $(this).hide();
            $('.tabBox').css({'overflow-y':'scroll'});
        }
    });

    //老师发送图片
    var images = {
        localId: [],
        serverId: []
    };
    var img_upload_id = 0;
    var uploadImage = function() {
        if (images.localId.length == 0) {
            return;
        }
        var i = 0,
            length = images.localId.length;
        images.serverId = [];
        function upload() {
            var uuid = guid();
            var oLi = ('<li class="megLeft" id="'+uuid+'" uuid="'+uuid+'" localid="'+images.localId[i]+'">\
									<div class="headImg"><img src="'+TeacherPhoto+'"></div>\
									<div class="messageMain">\
										<div class="messageName"><em>老师</em> <i class="yd"></i> '+TeacherName+'</div>\
										<div class="messageBox Teacher Image"><p><img style="max-width: 7rem" imgurl="" src="'+images.localId[i]+'"><em class="audioloadIcon"></em></p></div>\
									</div>\
								</li>');
            $('#sayslist').append(oLi);
            ImageLoadEnd();
            wx.uploadImage({
                localId: images.localId[i],
                isShowProgressTips:0,
                success: function(res) {
                    i++;
                    img_upload_id++;
                    images.serverId.push(res.serverId);
                    console.log(JSON.stringify(res)+'微信图片服务器信息');
                    var result = ZAjaxRes({url: "/live/getUrl.user?serviceId=" + res.serverId+'&roomId='+_chatRoomId+'&type=1&uuid='+uuid});
                    if(result.code == "000000"){

                    }else{
                        $('#'+uuid).remove();
                    }
                    if (i < length) {
                        upload();
                    }
                    teacherInputMsg = true;
                    inputCancel();
                },
                fail: function(res) {
                    pop({conter:"图片上传微信失败"});
                    teacherInputMsg = true;
                    inputCancel();
                    $('#'+uuid).remove();
                }
            });
        }
        upload();
    };

    $('#send_img').on('click',function() {
        inputEnter();
        wx.chooseImage({
            success: function(res) {
                console.log(JSON.stringify(res)+'微信图片本地信息');
                images.localId = res.localIds;
                uploadImage();
            },
            fail: function(res) {
                pop({conter:JSON.stringify(res)});
                teacherInputMsg = true;
                inputCancel();
            },
            cancel:function (res) {
                teacherInputMsg = true;
                inputCancel();
            }
        });
    });
    //老师录音跟文字输入切换
    $('.send_audio_icon').on('click',function(){
        $(this).hide();
        $('.send_text_icon').show();
        $('#send').hide();
        $('.questions_btn').show();
        $('.send_img').show();
        $('.sound_bomb_box_bg').show();
        $('.sound_bomb_box,.height_zw').show();
        $('.invitation_flexd,.barrage,.closeLiveBtn,.barrage_list_box,.Group1').hide();
        $('.barrage_box').css('bottom','16.15rem');
        $('.invitation_flexd').css('bottom','14.3rem');
        $('.barrage').css('bottom','14.3rem');
        $('#say').val('');

    });
    $('.send_text_icon').on('click',function(){
        $(this).hide();
        $('.send_audio_icon').show();
        $('.send_audio_btn').hide();
        $('.presay').show();
        //$('#say').focus();
        if(itme != 0){
            cancelAudio();
        }else{
            $('.sound_btn_box').removeClass('sound_end').removeClass('sound_suspend').addClass('sound_start');
            $('.start_text').show();
            $('.audio_tiems').hide();
            $('.sound_start_text').show();
            $('.sound_suspend_text').hide();
            $('.sound_end_text').hide();
            $('.sound_bomb_box').hide();
            $('.height_zw').hide();
            $('.send_message').show();
            $('.barrage_box').css('bottom', '4.85rem');
            $('.invitation_flexd').css('bottom', '3rem');
            $('.barrage').css('bottom', '3rem');
            $('.sound_center').removeClass('fadeInUp animated');
            $('.reward').show();
            $('.invitation_flexd,.barrage,.closeLiveBtn,.barrage_list_box,.Group1').show();

        }
    });
    // 弹出语音录制界面
    $('.send_audio_btn').on('click',function(){
        $(this).hide();
        $('.barrage_box').css('bottom','16.15rem');
        $('.invitation_flexd').css('bottom','14.3rem');
        $('.barrage').css('bottom','14.3rem');
        $('.sound_bomb_box_bg').show();
        $('.sound_bomb_box,.send_text_icon').show();
        $('.height_zw,.presay').show();
        $('.barrage,.closeLiveBtn,.barrage_list_box,.Group1').hide();
        $('.invitation_flexd').hide();
        $('.reward').hide();
        $('.tabBox').animate({
            scrollTop:  $('.says').height()
        }, 500);
        //$('.sound_center').addClass('fadeInUp animated');
    });
    //正在录音
    var recording = 0;
    //var stopRecording = false;
    var recordObject =   {
        success: function(){
            $('.start_text').hide();
            $('.audio_tiems').show().find('.line-scale-pulse-out').find('div').removeClass('animationEnd');
            //可停止
            recording = 2;
            clearInterval(itmes);
            clearInterval(itmesDown);
            itmes = setInterval(function(){
                itme++;
                if(itme >= 60) {
                    clearInterval(itmes);
                    // $('.sound_bomb_close').show();
                    // $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_end');
                    // $('.start_text').hide();
                    // $('.audio_tiems').show().find('.line-scale-pulse-out').find('div').addClass('animationEnd');
                    // $('.sound_start_text').hide();
                    // $('.sound_suspend_text').hide();
                    // $('.sound_end_text').show();
                    // recording = 4;
                }
                if(itme == 54){ //录音到54秒 开始5秒倒计时
                    itmesDown = setInterval(function(){
                        itmeDown--;
                        if(itme >= 54 ){
                            $('.times').html(toDou(itmeDown)+'秒后自动发送，并自动录下一条').css('font-size','.65rem');
                        }
                        if(itmeDown == 0){
                            clearInterval(itmesDown);
                        }
                    },1000);
                }
                if(itme <= 54 ){
                    $('.time').html(toDou(itme)+'s');
                }
                //解决ios60秒问题
                if((itme == 59 && ios) || (itme == 60 && Android) ){
                    teacherInputMsg = true;
                    inputCancel();
                    wx.stopRecord({
                        success: function (res) {
                            localIds = res.localId;
                            clearInterval(itmes);
                            clearInterval(itmesDown);
                            if(ios){
                                itme = 60;
                            }
                            itmeDown = 6;
                            $('.audio_tiems .times').html('<span class="time">01s</span>/ 60s').css('font-size','.75rem');
                            setTimeout(function(){
                                //自动录音
                                $('.sound_btn_box').click();
                            },1000);
                            uploadVoice(1);
                        },
                        fail: function (res) {
                            //pop({content:JSON.stringify(res)});
                        }
                    });
                }
            },1000);

        },
        fail: function(res) {
            if (res.errMsg == 'startRecord:recording'|| res.errMsg == 'startRecord:fail') {
                wx.stopRecord({
                    success: function (res) {
                        wx.startRecord(recordObject);
                    },
                    fail: function (res) {
                        console.log(JSON.stringify(res));
                    }
                });
            }
        },
        cancel: function () {
            pop({content:'用户拒绝授权录音'});
            teacherInputMsg = true;
            inputCancel();
            recording = 0;
            itme = 0;
            $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_start');
            $('.start_text').show();
            $('.audio_tiems').hide().find('.line-scale-pulse-out').find('div').removeClass('animationEnd');
            $('.sound_start_text').show();
            $('.sound_suspend_text').hide();
            $('.sound_end_text').hide();
            $('.time').html(toDou(itme)+'s');
        }
    }
    $('.sound_btn_box').on('click',function(){

        if(recording == 0 || recording == 1){ //进入录音模式
            //$(this).hasClass('sound_start')
            //正在录音
            $('#sayslist .audioIco').removeClass('playIn');
            $('#audio')[0].pause();
            recording = 1;
            inputEnter();
            //$('.sound_bomb_close').hide();
            $('.sound_btn_box').removeClass('sound_start').addClass('sound_suspend');

            $('.sound_start_text').hide();
            $('.sound_suspend_text').show();
            $('.sound_end_text').hide();
            wx.startRecord(recordObject);
        }else if( recording == 2 || recording == 3){ // 停止录音
            //$(this).hasClass('sound_suspend')
            //停止
            recording = 3;
            teacherInputMsg = true;
            inputCancel();
            try {
                wx.stopRecord({
                    success: function (res) {
                        console.log(JSON.stringify(res)+'stop');
                        $('.sound_bomb_close').show();
                        clearInterval(itmes);
                        clearInterval(itmesDown);
                        itmeDown = 6;
                        if(itme >= 2){
                            localIds = res.localId;
                            $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_end');
                            $('.start_text').hide();
                            $('.audio_tiems').show().find('.line-scale-pulse-out').find('div').addClass('animationEnd');
                            $('.sound_start_text').hide();
                            $('.sound_suspend_text').hide();
                            $('.sound_end_text').show();
                            recording = 4;
                        }else{
                            pop({content:'录音时间小于2s',width:"8rem"});
                            itme = 0;
                            $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_start');
                            $('.start_text').show();
                            $('.audio_tiems').hide().find('.line-scale-pulse-out').find('div').removeClass('animationEnd');
                            $('.sound_start_text').show();
                            $('.sound_suspend_text').hide();
                            $('.sound_end_text').hide();
                            recording = 0;
                        }
                    },
                    fail: function (res) {
                        if (res.errMsg == 'stopRecord:tooshort') {
                            pop({content:'录音时间小于2s',width:"8rem"});
                        }
                        console.log(JSON.stringify(res));
                        //pop({content:JSON.stringify(res)});
                        itme = 0;
                        $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_start');
                        $('.start_text').show();
                        $('.audio_tiems').hide().find('.line-scale-pulse-out').find('div').removeClass('animationEnd');
                        $('.sound_start_text').show();
                        $('.sound_suspend_text').hide();
                        $('.sound_end_text').hide();
                        recording = 0;
                    }
                });
            } catch (e) {
                console.log(e);
                itme = 0;
                $('.sound_btn_box').removeClass('sound_suspend').addClass('sound_start');
                $('.start_text').show();
                $('.audio_tiems').hide().find('.line-scale-pulse-out').find('div').removeClass('animationEnd');
                $('.sound_start_text').show();
                $('.sound_suspend_text').hide();
                $('.sound_end_text').hide();
                recording = 0;
                console.log(44);
            }

        }else if( recording == 4){ // 发送录音
            clearInterval(itmesDown);
            clearInterval(itmes);
            itmeDown = 6;
            $('.invitation_flexd,.barrage,.closeLiveBtn,.Group1').hide();
            $('.audio_tiems .times').html('<span class="time">01s</span>/ 60s').css('font-size','.75rem');
            //$(this).hasClass('sound_end')
            //没在发送中
            // if (recording == 5) {
            //     return ;
            // }
            //停止
            //recording = 5;
            uploadVoice()
        }
    });

    var retryUploadVoice = 0;

    // 发送录音到服务器并发送消息
    function uploadVoice(auto){
        $('.sound_btn_box').removeClass('sound_end').removeClass('sound_suspend').addClass('sound_start');
        $('.start_text').show();
        $('.audio_tiems').hide();
        $('.sound_start_text').show();
        $('.sound_suspend_text').hide();
        $('.sound_end_text').hide();

        if (!auto) {
            auto = "";
        }

        if(itme == 60){
            var andS = (1+"'")+'00"';
        }else{
            var andS = (itme)+'"';
        }

        var rem = ((itme-2)*((13.9-3.5)/(180-2))+3.5).toFixed(3);
        var uuid = guid();
        var audiooLi = ('<li id="'+uuid+'" uuid="'+uuid+'"  class="megLeft">\
				<div class="headImg"><img src="' + TeacherPhoto + '"></div>\
				<div class="messageMain">\
					<div class="messageName "><em>老师</em><i class="yd"></i>  ' + TeacherName + '</div>\
					<div class="messageBox Audio Teacher loadingStatus"><em class="loader"><em class="loading-3"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></em></em><p style="width:' + rem + 'rem;"><span class="audioIco"></span><span class="audioBar"><em><i></i></em></span><span time="'+andS+'" class="aud">' + andS + '</span><em class="audioloadIcon"></em></p></div>\
				</div>\
			</li>');
        $('#sayslist').append(audiooLi);
        HtmlScrollBottom();
        itme = 0;
        $('.time').html(toDou(itme)+'s');
        recording = 0;
        if(sendAudioInow){
            //改成发送中
            return ;
            //pop({content:'<em class="audioLoadEm"></em>发送中...',width:'6.45rem',height:'1.95rem',bottom:"4rem",time:10000000});

        }
        sendAudioInow = true;

        var uploadObject = {
            localId: localIds, // 需要上传的音频的本地ID，由stopRecord接口获得
            isShowProgressTips: 0, // 默认为1，显示进度提示
            success: function (res) {
                //把录音在微信服务器上的id（res.serverId）发送到自己的服务器供下载。
                console.log(JSON.stringify(res)+'语音微信服务器返回信息');
                //$('.pop_wrap').remove();
                ZAjaxRes({url: "/live/getAudioUrl.user?serviceId=" + res.serverId+'&roomId='+_chatRoomId+'&type=2&uuid='+uuid + "&auto=" + auto + "&isIosWeixin=" + (ios ? "1" : "0")
                    , callback:function () {
                        console.log("上传成功");
                    }
                    , async:true
                });
                sendAudioInow = false;
                //recording = 0;
            },
            fail: function(res) {
                //重试1次
                if (retryUploadVoice < 1) {
                    retryUploadVoice = retryUploadVoice + 1;
                    wx.uploadVoice(uploadObject);
                    return ;
                }
                //alert(JSON.stringify(res));
                sendAudioInow = false;
                // recording = 0;
            }
        };

        wx.uploadVoice(uploadObject);
    }


    //关闭录音
    //$('.sound_bomb_close').on('click',function(){
    //    if(itme != 0){
    //        cancelAudio();
    //    }else{
    //        $('.sound_bomb_box').hide();
    //        $('.height_zw').hide();
    //        $('.sound_center').removeClass('fadeInUp animated');
    //        $('.send_message').show();
    //        $('.barrage').show();
    //        $('.invitation_flexd').show();
    //        $('.reward').show();
    //        $('.barrage_box').css('bottom','4.85rem');
    //        $('.invitation_flexd').css('bottom','3rem');
    //        $('.barrage').css('bottom','3rem');
    //    }
    //
    //});

    $('.sound_bomb_box_bg,.sound_bomb_close').on('click',function(e){
        if(itme != 0 &&$('.sound_bomb_box').is(':visible')){
            cancelAudio();
        }else{
            clearInterval(itmesDown);
            clearInterval(itmes);
            itme = 0;
            itmeDown = 6;
            $('.audio_tiems .times').html('<span class="time">01s</span>/ 60s').css('font-size','.75rem');
            $('.sound_btn_box').removeClass('sound_end').removeClass('sound_suspend').addClass('sound_start');
            $('.start_text').show();
            $('.audio_tiems').hide();
            $('.sound_start_text').show();
            $('.sound_suspend_text').hide();
            $('.sound_end_text').hide();
            $('.time').html(toDou(itme)+'s');
            recording = 0;
            $('.sound_bomb_box').hide();
            $('.height_zw').hide();
            $('.send_message').show();
            $('.barrage_box').css('bottom', '4.85rem');
            $('.invitation_flexd').css('bottom', '3rem');
            $('.barrage').css('bottom', '3rem');
            $('.sound_center').removeClass('fadeInUp animated');
            $('.reward').show();
            //$('.sound_bomb_close').hide();
            $('.send_audio_icon').show();
            $('.send_text_icon').hide();
            $('.invitation_flexd,.barrage,.closeLiveBtn,.barrage_list_box,.Group1').show();
        }
    })
    //取消录完的音
    $('.cancel').on('click',function(){
        BaseFun.Dialog.Config({
            title: '提示',
            text : '确定取消录音吗？',
            cancel:'否',
            confirm:'是',
            callback:function(index){
                if(index == 1){
                    clearInterval(itmesDown);
                    clearInterval(itmes);
                    itme = 0;
                    itmeDown = 6;
                    $('.audio_tiems .times').html('<span class="time">01s</span>/ 60s').css('font-size','.75rem');
                    $('.sound_btn_box').removeClass('sound_end').addClass('sound_start');
                    $('.start_text').show();
                    $('.audio_tiems').hide();
                    $('.sound_start_text').show();
                    $('.sound_suspend_text').hide();
                    $('.sound_end_text').hide();
                    $('.time').html(toDou(itme)+'s');
                    recording = 0;
                }
            }
        });
    });
    function cancelAudio(){
        BaseFun.Dialog.Config({
            title: '正在录制中',
            text : '确定取消录音吗？',
            cancel:'取消',
            confirm:'确定',
            callback:function(index){
                if(index == 1){
                    clearInterval(itmesDown);
                    clearInterval(itmes);
                    itme = 0;
                    itmeDown = 6;
                    $('.audio_tiems .times').html('<span class="time">01s</span>/ 60s').css('font-size','.75rem');
                    $('.sound_btn_box').removeClass('sound_end').removeClass('sound_suspend').addClass('sound_start');
                    $('.start_text').show();
                    $('.audio_tiems').hide();
                    $('.sound_start_text').show();
                    $('.sound_suspend_text').hide();
                    $('.sound_end_text').hide();
                    $('.time').html(toDou(itme)+'s');
                    recording = 0;
                    $('.sound_bomb_box').hide();
                    $('.height_zw').hide();
                    $('.send_message').show();
                    $('.barrage_box').css('bottom', '4.85rem');
                    $('.invitation_flexd').css('bottom', '3rem');
                    $('.barrage').css('bottom', '3rem');
                    $('.sound_center').removeClass('fadeInUp animated');
                    $('.reward').show();
                    //$('.sound_bomb_close').hide();
                    $('.send_audio_icon').show();
                    $('.send_text_icon').hide();
                    $('.invitation_flexd,.barrage,.closeLiveBtn,.barrage_list_box,.Group1').show();
                    wx.stopRecord();
                }
            }
        });
    }
    $('#say').on('keyup',function(){
        if(_userId == TeacherFrom){
            inputEnter();
        }
    });
    $('.presay').on('click',function(){
        $('#say').focus();
    })
    $('#say').on('focus',function(){
        HtmlScrollBottom();
        if(TeacherFrom == _userId){
            //$('.sound_bomb_close').hide();
            $('.send_audio_icon').show();
            $('.send_text_icon').hide();
            $('.height_zw,.sound_bomb_box').hide();
            $('.barrage_box').css('bottom', '4.85rem');
            $('.invitation_flexd').css('bottom', '3rem');
            $('.barrage').css('bottom', '3rem');
        }

        //$('.barrage').hide();
        //$('.invitation_flexd').hide();
        //$('.reward').hide();
    });
    $('#say').on('input propertychange',function(){
        if($(this).val() != '' && TeacherFrom == _userId){
            $('#send').show();
            $('.questions_btn,.send_img').hide();
        }else if($(this).val() == '' && TeacherFrom == _userId){
            $('#send').hide();
            $('.questions_btn,.send_img').show();
        }
    })
    $('#say').on('blur',function(){
        if(_userId == TeacherFrom) {
            teacherInputMsg = true;
            inputCancel();
        }
        //$('.barrage').show();
        //$('.invitation_flexd').show();
        //$('.reward').show();
    });

    //	关闭直播间
    $('.closeLiveBtn').on('click',function(){
        $('.publicPopups').show();
    });
    $('.publicPopups').on('click',function(e){
        if(this == e.target){
            $(this).hide();
        }
    });
    $('.publicPopups .dialog-cancel').on('click',function(e){
        if(this == e.target){
            $('.publicPopups').hide();
        }
    });
    $('.dialog-confirm').on('click',function(){
        var result = ZAjaxRes({url:"/weixin/live/endLive.user", type: "POST",param:{courseId:courseId} });
        if(result.code == "000000"){
            window.location.href = "/weixin/courseInfo?id=" + courseId;
        }else{
            console.log(result)
        }
    });

    $('.barrage').on('click',function(){
        $(this).toggleClass('barrage_active');
        $('.barrage_list_box').toggle();

        if (barrageChecked == "1" ) {
            barrageChecked = "0";
        } else {
            barrageChecked = "1";
        }

        localStorage.setItem("barrage_" + courseId , barrageChecked);
    });
    $('.clickFollow').on('click',function(){
        $('.ewmbox').addClass("on");
        $('.imgsrc').attr('src',qrCode);
    })
    $('.ewmbox').on('click',function(e){
        if(this == e.target){
            $(this).removeClass("on");
        }
    });
    $('.ewmbox .qdBtn').on('click',function(){
        $('.ewmbox').removeClass("on");
        sessionStorage.setItem('ewm'+courseId+'_'+_userId , "1");
    })
});
/**
 * 更新 提问弹框跟弹幕数据列表
 * @param resultData
 * @param recDataCache 收到数据的缓存，防止数据库的数没加载到导致数据丢
 * @returns {string}
 */
function message_list_update(resultData , recDataCache){
    var data = resultData;
    var html = '';

    if (data && data.length > 0 ) {
        for(var i=0; i<data.length; i++){
            if($('.div_list[uuid='+data[i].msgidClient+']').length == 0){
                html += ('<div class="div_list" data-id="'+data[i].id+'" from="'+data[i].fromAccount+'" name="'+data[i].fromNick+'" img="'+data[i].fromAvator+'">\
					<div class="div_img">\
						<img class="p_headImg" src="'+ data[i].fromAvator +'" alt=""/>\
						</div>\
						<div class="div_text">\
						<p class="name">'+data[i].fromNick+'</p>\
						<p class="times">'+data[i].fromExt+'</p>\
					<p class="text">'+data[i].attach+'</p>\
					</div>\
		        </div>');
            }

            //for (var j = 0 ;j < recDataCache.length ;j++) {
            //    if (recDataCache[j] && recDataCache[j].idClient == data[i].msgidClient) {
            //        //说明有了，给一个有了的标志，下面会去循环
            //        recDataCache[j].isHas = true;
            //    }
            //}
        }
    }
    ////加载没有的
    //for(var i=0; i<recDataCache.length; i++){
    //    //为空，或者上面已经有了
    //    console.log(recDataCache[i]);
    //    if (!recDataCache[i] || recDataCache[i].isHas) continue;
    //    html = ('<div class="div_list" data-id="'+recDataCache[i].idClient+'" from="'+recDataCache[i].from+'" name="'+recDataCache[i].fromNick+'" img="'+recDataCache[i].fromAvatar+'">\
    //			<div class="div_img">\
    //				<img class="p_headImg" src="'+ recDataCache[i].fromAvatar +'" alt=""/>\
    //				</div>\
    //				<div class="div_text">\
    //				<p class="name">'+recDataCache[i].fromNick+'</p>\
    //				<p class="times">'+formatDateTime(recDataCache[i].time)+'</p>\
    //			<p class="text">'+recDataCache[i].text+'</p>\
    //			</div>\
    //        </div>') + html;
    //
    //    recDataCache[i].isHas = false;
    //}

    return html;
};
function formatDateTime(inputTime) {
    var date = new Date(inputTime);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;
};

//语音播放函数
var slideTimes = null;
var isPlayLoading = false;
//是否加载过了
var isLoaded = false;
function audio() {
    if(arrMp3url.length){
        isPlayAudio = true;
        isPlayLoading = true;
        isLoaded = true;
        $('#sayslist .audioIco').removeClass('playIn loading');
        $('#sayslist .loader').hide();
        $('#sayslist .audioBar').css('opacity','0');
        $('#sayslist .audioBar i').css('opacity','0');
        $('#sayslist .audioIco').eq(iNow).addClass('loading');
        $('#sayslist .loader').eq(iNow).show();
        $('#sayslist .resendIcon').eq(iNow).hide();
        $('#audio')[0].src = arrMp3url[iNow];
        if(arrMp3url[iNow+1]){$('#audioNext')[0].src = arrMp3url[iNow+1];}
        $('#audio')[0].loop = false;
        $('#audio')[0].ended = true;
        $('#audio')[0].play();
        scrollToLocation(iNow);
        //音频准备就绪
        var ThisAudioUUID = $('#sayslist .audio_uuid').eq(iNow).attr('uuid');
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
            BaseFun.SetStorage("playHistory_" + courseId , iNow);
        }
    }else{
        if(_userId != TeacherFrom){
            $('#audio')[0].src = 'http://filemedia.llkeji.com/default/000.m4a';
            $('#audio')[0].loop = false;
            $('#audio')[0].ended = true;
            $('#audio')[0].play();
        }
    }

    $('#audio')[0].addEventListener('canplay', function () {
        if(arrMp3url.length > 0){
            $('#sayslist .audioIco').eq(iNow).removeClass('loading');
            isPlayLoading = false;
            $('#sayslist .loader').hide();
            $('#sayslist .audioIco').eq(iNow).addClass('playEnd playIn');
            $('#sayslist .audioBar').eq(iNow).css('opacity','1');
            $('#sayslist .audioBar i').eq(iNow).css('opacity','1');
            audioBarSlide(this)
        }
    }, false);

    //音频加载失败
    $('#audio')[0].addEventListener('error', function () {
        _thisAudioUuid = '';
        $('#audio')[0].load();
        isPlayLoading = false;
    }, false);
    //音频播放结束
    $('#audio')[0].addEventListener('ended', function () {
        _thisAudioUuid = '';
    }, false);

};


function reAudio() {
    $('#audio')[0].play();
    $('#sayslist .audioIco').eq(iNow).addClass('playEnd playIn');
    $('#sayslist .audioBar').eq(iNow).css('opacity','1');
    ///audioBarSlide($('#audio')[0]);
};

var slideTimes = null;
function audioBarSlide(_this){
    var duration = 0;
    if(ios){
        duration = parseInt($('#sayslist .aud').eq(iNow).attr('time'));
    }else{
        duration = _this.duration;
    }
    clearInterval(slideTimes);
    $('#sayslist .audioBar').eq(iNow).children('em').css('width',_this.currentTime/duration*100+'%');
    slideTimes = setInterval(function(){
        $('#sayslist .audioBar').eq(iNow).children('em').css('width',_this.currentTime/duration*100+'%');
        if(_this.currentTime > 0 && duration > 0  && Math.ceil(_this.currentTime) >= duration){
            clearInterval(slideTimes);
        }
    },500)
}

//  语音进度条拖动
$('#sayslist').on('touchstart','.audioBar',function(e){
    var _this = $(this);
    var oBar = _this.find('em');
    var oTouch = e.originalEvent.targetTouches[0];
    var disX = oTouch.pageX-oBar.width();
    function fnMove(e){
        oBar.children('i').css({
            width:'.6rem',
            height:'.6rem',
            right:'-.3rem',
            marginTop:'-.3rem'
        })
        var oTouch = e.originalEvent.targetTouches[0];
        var l = oTouch.pageX-disX;
        if(l<0){
            l = 0;
        }else if(l>_this.width()){
            l = _this.width();
            $('#audio')[0].stop();
        }
        oBar.css('width',l/_this.width()*100+'%');
        var scale = l/_this.width();
        if(ios){
            var Thisduration = parseInt($('#sayslist .aud').eq(iNow).attr('time'));
        }else{
            var Thisduration = $('#audio')[0].duration;
        }
        $('#audio')[0].currentTime = scale*Thisduration;
        if($('#audio')[0].paused){
            $('#audio')[0].play();
        }
    }
    function fnEnd(){
        oBar.children('i').css({
            width:'.4rem',
            height:'.4rem',
            right:'-.2rem',
            marginTop:'-.2rem'
        })
        $(document).off('touchmove',fnMove);
        $(document).off('touchend',fnEnd);
    }
    $(document).on('touchmove',fnMove);
    $(document).on('touchend',fnEnd);
    e.stopPropagation();
});


$('.back_active_positions').on('click',function(){
    isScrollswitch = false;
    scrollToLocation(iNow);
    var isScrollswitchTimes = null;
    clearTimeout(isScrollswitchTimes);
    isScrollswitchTimes = setTimeout(function(){
        isScrollswitch = true;
    },500)
    //$(this).removeClass('fadeInLeft').addClass('fadeOutLeft');
})

//语音播放结束
$('#audio')[0].addEventListener('ended',audioEnden, false);
function audioEnden(){
    _thisAudioUuid = '';
    if ($('#audio')[0].ended){
        if (iNow < arrMp3url.length - 1) {
            iNow++;
            audio(iNow);
        } else {
            $('#sayslist .audioIco').removeClass('playIn');
            $('.back_active_positions').removeClass('fadeInLeft').addClass('fadeOutLeft');
            isPlayAudio = false;
        }
    }
    $('#sayslist .audioBar').css('opacity','0');
    $('#sayslist .audioBar i').css('opacity','0');
};




//语音跟随滚动方法
function scrollToLocation(iNow) {
    var mainContainer = document.querySelectorAll('.Audio')[iNow];
    var mainContainerHeight = mainContainer.parentNode.parentNode.offsetTop;
    //动画效果
    $('.tabBox').animate({
        scrollTop: mainContainerHeight-$('.live_video').height()
    }, 500);//2秒滑动到指定位置
    $('.back_active_positions').removeClass('fadeInLeft').addClass('fadeOutLeft');
}


//获取课件
function coursewares(arr) {
    var LiveKjSwitch = BaseFun.GetStorage('LiveKjSwitch'+courseId+'_'+_userId);
    if (arr.length == 0) {
        if (endTime) {
            $('#toppic>ul').append('<li class="swiper-slide" onclick="coursewareClickImg(this)"><img id="firstImg"   src="' + _coverssAddress + '"/></li>');
        } else{
            $('#toppic>ul').append('<li class="swiper-slide" onclick="coursewareClickImg(this)"><img  id="firstImg"  src="' + _coverssAddress + '"/></li>');
        }
    } else {
        $.each(arr, function () {
            var picsrc = this.address;
            $('#toppic>ul').append('<li class="swiper-slide" onclick="coursewareClickImg(this)"><img  id="firstImg"  src="' + picsrc + '"/></li>');
        });
    }
    if(LiveKjSwitch != null){
        if(LiveKjSwitch == '0'){
            $('.live_kj_toggle').addClass('off').html('展开');

            $('.video_box').hide();
            $('.live_video').css('height','2.025rem');
            $('.clickLiveRoom').css({'top':'7rem','opacity':'1'});
            $(".Group1").css({'top':'4.5rem','opacity':'1'});
            //$('#bd_box').css('padding-top','2.025rem');

            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','2.65rem');
                if(endTime){
                    $(".Group1").css('top','3rem');
                }else{
                    $(".Group1").css('top','5rem');
                }
                $(".clickLiveRoom").hide();
            }else{
                $('.clickFollow').addClass('clickFollow_active');
            }
        }else{
            $('.live_kj_toggle').removeClass('off').html('收起');
            $('.video_box').show();
            $('.live_video').css('height','12.525rem');
            $('.clickLiveRoom').css({'top':'5rem'});
            $(".Group1").css({'top':'2.5rem'});
            //$('#bd_box').css('padding-top','12.525rem');
            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','0.67rem');
                if(endTime){
                    $(".Group1").css('top','1rem');
                }else{
                    $(".Group1").css('top','3rem');
                }
                $(".clickLiveRoom").hide();
            }else{
                $('.clickFollow').removeClass('clickFollow_active');
            }
        }
    }else{
        if(arr.length == 0){
            $('.live_kj_toggle').addClass('off').html('展开');
            $('.video_box').hide();
            $('.live_video').css('height','2.025rem');
            $('.clickLiveRoom').css({'top':'7rem','opacity':'1'});
            $(".Group1").css({'top':'4.5rem','opacity':'1'});
            //$('#bd_box').css('padding-top','2.025rem');
            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','2.65rem');
                if(endTime){
                    $(".Group1").css('top','3rem');
                }else{
                    $(".Group1").css('top','5rem');
                }
                $(".clickLiveRoom").hide();
            }else{
                $('.clickFollow').addClass('clickFollow_active');
            }
        }else{
            $('.live_kj_toggle').removeClass('off').html('收起');
            $('.video_box').show();
            $('.live_video').css('height','12.525rem');
            $('.clickLiveRoom').css({'top':'5rem'});
            $(".Group1").css({'top':'2.5rem'});
            //$('#bd_box').css('padding-top','12.525rem');
            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','0.67rem');
                if(endTime){
                    $(".Group1").css('top','1rem');
                }else{
                    $(".Group1").css('top','3rem');
                }
                $(".clickLiveRoom").hide();
            }else{
                $('.clickFollow').removeClass('clickFollow_active');
            }
        }
    }
};
$(document).ready(function(){
    //  获取最新的三条弹幕
    $.getJSON("/chatRoom/findLastThreeMsg.user?courseId=" + _oriCourseId, function (data) {
        if(data.code == "000000"){
            for(var i=0; i<data.data.length; i++){
                if(data.data[i].customMsgType == 5){  // 提问
                    $('.barrage_list_box').prepend('<div class="item" from ="' + data.data[i].fromAccount + '" name="' + data.data[i].fromNick + '" img="' + data.data[i].fromAvator + '">\
						<div class="barrage_item barrage_quiz_item">\
							<p class="text"><em class="barrage_quiz_iconn"></em>' + escapeHtml(data.data[i].attach) + '</p>\
							<div class="head_img">\
								<img src="' + data.data[i].fromAvator + '"/>\
							</div>\
						</div>\
					</div>');
                }else{  // 弹幕
                    $('.barrage_list_box').prepend('<div class="item" from ="' + data.data[i].fromAccount + '" name="' + data.data[i].fromNick + '" img="' + data.data[i].fromAvator + '">\
						<div class="barrage_item barrage_dm_item">\
							<p class="text">' + escapeHtml(data.data[i].attach) + '</p>\
							<div class="head_img">\
								<img src="' + data.data[i].fromAvator + '"/>\
							</div>\
						</div>\
					</div>');
                }
            }
        }else{
            //pop({content:data.message});
        }
    });

})



// 直播创建会话UI函数
function addMsg(json, node) {
    var type = json.type; //收到消息类型
    var fromNick = json.fromNick; //呢称
    var fromAvatar = json.fromAvatar || '/web/res/image/01.png'; //头像
    var from = json.from; // 账号

    var cancelHtml = '';
    if(_userId == TeacherFrom){
        var cancelHtml = '<e class="cancelmsg">撤回</e>';
    }
    //console.log(json)
    //是否是老师发的消息
    if (from == TeacherFrom) {
        var isTeacher = 'Teacher';
    }else{
        var isTeacher = '';
    }

    //是否是自己发的消息
    //if (from == _userId) {
    //	var direction = 'megRight';
    //}else{
    var direction = 'megLeft';
    //}

    var oLi = '';
    switch (type) {
        //文本消息///////////////
        case 'text':
            var text = json.text; //文本消息内容
            if(from == TeacherFrom) {  //  老师文本消息
                oLi = ('<li  from ="' + from + '" uuid="'+json.idClient+'" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
						<div class="messageName"><em>老师</em> <i class="yd"></i> ' + fromNick + '</div>\
						<div class="messageBox Text ' + isTeacher + '"><p>' + escapeHtml(text) + ''+cancelHtml+'</p></div>\
					</div>\
				</li>');
            }else{     // 学生弹幕消息
                if(node){
                    oLi = ('<div class="item" from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '">\
						<div class="barrage_item barrage_dm_item">\
							<p class="text">' + escapeHtml(text) + '</p>\
							<div class="head_img">\
								<img src="' + fromAvatar + '"/>\
							</div>\
						</div>\
					</div>');

                    ////3个对象，先进先出队列，每一个对象向前移动一个位置，最后一个进入缓存
                    //for (var i = 0 ;i < barrages.length - 1; i++) {
                    //    barrages[i] = barrages[i + 1];
                    //}
                    //json.text = escapeHtml(text);
                    //barrages[barrages.length-1] = json;
                    ////判断有没有barrages缓存，因为barrages是固定大小的数组不能用lenght判断
                    //hasBrrage = true;

                    if($('.barrage_list_ul').find('.noData').length){
                        $('.barrage_list_ul').find('.noData').remove();
                    }

                    $('.barrage_list_ul').prepend('<div uuid="'+json.idClient+'" class="div_list"  from="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'">\
                                <div class="div_img">\
                                    <img class="p_headImg" src="'+ fromAvatar +'" alt=""/>\
                                    </div>\
                                    <div class="div_text">\
                                    <p class="name">'+fromNick+'</p>\
                                    <p class="times">'+DateTimeSetDate(json.time)+'</p>\
                                <p class="text">'+escapeHtml(text)+'</p>\
                                </div>\
                            </div>').scrollTop(0);

                }
            }
            break;

        //图片消息///////////////
        case 'image':
            var imgsrc = json.file.url;
            if($('#'+json.idClient).length == 1 ){
                $('#'+json.idClient).find('.audioloadIcon').remove();
                $('#'+json.idClient).find('.Image').find('img').attr('imgurl',imgsrc);
                $('#'+json.idClient).find('.Image').find('p').append(cancelHtml);
            }else{
                oLi = ('<li from ="' + from + '" uuid="'+json.idClient+'" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
						<div class="messageName"><em>老师</em> <i class="yd"></i> ' + fromNick + '</div>\
						<div class="messageBox Image ' + isTeacher + '"><p><img imgurl="'+imgsrc+'" src="' + imgsrc + '?x-oss-process=style/chatMsgImg">'+cancelHtml+'</p></div>\
					</div>\
				</li>');
            }
            if (node) {
                imgList.push(imgsrc);
            } else {
                imgList.unshift(imgsrc);
            }
            break;

        //语音消息///////////////
        case 'audio':
            var mp3Url = json.file.mp3Url;
            var dur = Math.round(json.file.dur / 1000);
            var andS = dur/60;
            var resendInow = '';
            if (node) {
                arrMp3url.push(mp3Url);
            } else {
                arrMp3url.unshift(mp3Url);
            }
            var rem = ((dur-2)*((13.9-3.5)/(180-2))+3.5).toFixed(3);
            if(andS >= 1){
                andS = parseInt(andS) +"'"+toDou((dur%60))+'"';
            }else{
                andS = dur+'"'
            }
            if(!json.file.resend && _userId != TeacherFrom){
                if(audioPlayEndArr != null){
                    if(audioPlayEndArr.audioArr.indexOf(json.idClient) === -1){
                        resendInow = '<b class="resendIcon"></b>';
                    }else{
                        resendInow = '<b class="resendIcon hide"></b>';
                    }
                }else{
                    resendInow = '<b class="resendIcon"></b>';
                }
            }
            if($('#'+json.idClient).length == 1 ){
                $('#'+json.idClient).find('.audioloadIcon').remove();
                $('#'+json.idClient).find('.Audio').removeClass('loadingStatus').find('p').append(cancelHtml);
            }else{
                oLi = ('<li data-type="AUDIO" uuidlen="'+ (uuidLens+1) +'" uuid="'+json.idClient+'" from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix audio_uuid">\
                    <div class="headImg"><img src="' + fromAvatar + '"></div>\
                    <div class="messageMain">\
                        <div class="messageName "><em>老师</em> <i class="yd"></i> ' + fromNick + '</div>\
                        <div class="messageBox Audio ' + isTeacher + '"><em class="loader"><em class="loading-3"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></em></em><p style="width:' + rem + 'rem;"><span class="audioIco"></span><span class="audioBar"><em><i></i></em></span><span time="'+dur+'" class="aud">' + andS + '</span>'+resendInow+''+cancelHtml+'</p></div>\
                    </div>\
                </li>');
            }
            break;

        //自定义消息///////////////
        case 'custom':
            if (!json.content) {
                return;
            }
            var content = JSON.parse(json.content);
            if (content.type == 5) {
                //学生提问
                //oLi = ('<li  from ="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'" class="' + direction + ' clearfix">\
                //	<div class="headImg"><img src="' + fromAvatar + '"></div>\
                //	<div class="messageMain">\
                //	<div class="messageName ">' + fromNick + '</div>\
                //		<div class="messageBox Custom ' + isTeacher + '"><p>' + escapeHtml(content.data.value) + '</p><span class="customBg"></span></div>\
                //	</div>\
                //</li>');
                if(node){
                    oLi = ('<div class="item" from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '">\
						<div class="barrage_item barrage_quiz_item">\
							<p class="text"><em class="barrage_quiz_iconn"></em>' + escapeHtml(content.data.value) + '</p>\
							<div class="head_img">\
								<img src="' + fromAvatar + '"/>\
							</div>\
						</div>\
					</div>');

                    ////3个对象，先进先出队列，每一个对象向前移动一个位置，最后一个进入缓存
                    //for (var i = 0 ;i < tiwens.length - 1; i++) {
                    //    tiwens[i] = tiwens[i + 1];
                    //}
                    //json.text = escapeHtml(content.data.value);
                    //tiwens[tiwens.length-1] = json;
                    ////判断有没有tiwens缓存，因为tiwens是固定大小的数组不能用lenght判断
                    //hasTiwens = true;

                    if($('.quiz_list_ul').find('.noData').length){
                        $('.quiz_list_ul').find('.noData').remove();
                    }
                    console.log(json)
                    $('.quiz_list_ul').prepend('<div uuid="'+json.idClient+'" class="div_list"  from="'+from+'" name="'+fromNick+'" img="'+fromAvatar+'">\
                                    <div class="div_img">\
                                        <img class="p_headImg" src="'+ fromAvatar +'" alt=""/>\
                                        </div>\
                                        <div class="div_text">\
                                        <p class="name">'+fromNick+'</p>\
                                        <p class="times">'+DateTimeSetDate(json.time)+'</p>\
                                    <p class="text">'+escapeHtml(content.data.value)+'</p>\
                                    </div>\
                                </div>').scrollTop(0);

                }
            } else if (content.type == 6) {
                //老师回答
                //oLi = ('<li class="' + direction + '">\
                //	<div class="headImg"><img src="' + fromAvatar + '"></div>\
                //	<div class="messageMain">\
                //	<div class="messageName ">' + fromNick + '</div>\
                //		<div class="messageBox Answer ' + isTeacher + '"><p><i>@' + content.data.nick + '</i>' + escapeHtml(content.data.value) + '</p><span class="answerBg"></span></div>\
                //	</div>\
                //</li>');
            } else if (content.type == 7) {
                if (node) {
                    //推流地址改变
                    videoUrl = content.data.hlsLiveAddress;
                    $('#CuPlayerVideo_video').attr('src', videoUrl);
                    $('#sayslist').append('<li><h5>老师直播开始了！请点击视频开始！</h5></li>');
                }
            } else if(content.type == 8) {
                oLi = ('<li><h5>视频直播已链接！</h5></li>');
                if(Android){
                    if(node){
                        $('#CuPlayerVideo_video').attr('controls','controls');
                        testPlay();
                        oV.load();
                        msgLS = false;
                    }else if(msgLS){
                        $('#CuPlayerVideo_video').attr('controls','controls');
                        testPlay();
                        oV.load();
                        msgLS = false;
                    }
                }
            } else if(content.type == 9){
                oLi = ('<li><h5>视频直播已断开！</h5></li>');
                if(Android){
                    if(node){
                        $('#CuPlayerVideo_video').removeAttr('controls');
                        testPause();
                        msgLS = false;
                    }else if(msgLS){
                        $('#CuPlayerVideo_video').removeAttr('controls');
                        testPause();
                        msgLS = false;
                    }
                }
            } else if(content.type == 11){
                var IDS = '0';
                if(content.data.ids){
                    IDS = (content.data.ids).toString();
                }

                var t_oLi = ('<li class="trter"><h5><span><img src="'+fromAvatar+'"></span><span><em>'+ subStringMax(fromNick,6) + '</em><em>送了一个'+getRewardIcon(IDS)+'</span><i class="number_x1"><img src="/web/res/image/newaudio/number@2x.png"></i></h5></li>')
                if(node){
                    if(historyOneMsgTime > 0){
                        if(((json.time - historyOneMsgTime)/1000) > 60){
                            $('#sayslist').append('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }
                    historyOneMsgTime = json.time;
                    $('#sayslist').append(t_oLi);
                    HtmlScrollBottom();
                }else{
                    $('#sayslistOneData').after(t_oLi);
                    if(prevMsgTimes > 0 ){
                        if(((prevMsgTimes - json.time)/1000) > 60){
                            $('#sayslistOneData').after('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }else{
                        historyOneMsgTime = json.time;
                    }
                    prevMsgTimes = json.time;
                }
            }else if(content.type == 12){
                //学生收到老师滑动课件
                if(node && _userId != TeacherFrom){
                    courseware.slideTo(content.data.value);
                    BaseFun.SetStorage('ChatRoomSwiperIndex'+courseId+'_'+_userId,content.data.value)
                }
            }else if(content.type == 16){//在线人次
                if(node){
                    $('.live_user_conut').html(content.data.value+'人');
                }
            }else if(content.type == 19 && _userId  != TeacherFrom){
                if(node){
                    //2分钟清理,老师输入状态
                    receTeacherInputing(content.data.value);
                }
            }else if(content.type == 20 && _userId  != TeacherFrom){
                if(node){
                    receTeacherDisInputing();
                }
            }else if(content.type == 21){ // 老师撤回消息
                var cancelMsgId = content.data.msgidClient;
                if(_userId != TeacherFrom && _thisAudioUuid == cancelMsgId){
                    pop({'content':'您正在收听的语音已被撤回'});
                }
                if($('#sayslist li[uuid='+cancelMsgId+']').find('.messageMain').find('.messageBox').hasClass('Audio')){
                    var uuidNow = $('#sayslist li[uuid='+cancelMsgId+']').attr('uuidlen');
                    $('#audio')[0].pause();
                    arrMp3url.splice(uuidNow,1);
                    if(arrMp3url.length >1){
                        $('.back_active_positions').removeClass('fadeInLeft').addClass('fadeOutLeft');
                        $('#audio')[0].load();
                        iNow = uuidNow;
                        audio();
                    }
                }
                $('#sayslist li[uuid='+cancelMsgId+']').prev('.divDates').remove();
                $('#sayslist li[uuid='+cancelMsgId+']').remove();

            }else if(content.type == 13){//添加管理员
                var s_oLi = ('<li class="div_center"><h5 class="gagMember_h5">'+content.data.name+'已被提升为管理员</h5></li>')
                if(node){
                    admins.push(content.data.value);
                    if(historyOneMsgTime > 0){
                        if(((json.time - historyOneMsgTime)/1000) > 60){
                            $('#sayslist').append('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }
                    historyOneMsgTime = json.time;
                    $('#sayslist').append(s_oLi);
                    HtmlScrollBottom();
                }else{
                    if(prevMsgTimes > 0 ){
                        if(((prevMsgTimes - json.time)/1000) > 60){
                            $('#sayslistOneData').after('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }else{
                        historyOneMsgTime = json.time;
                    }
                    $('#sayslistOneData').after(s_oLi);
                    prevMsgTimes = json.time;
                }
            }else if(content.type == 14){//解除管理员
                var s_oLi = ('<li class="div_center"><h5 class="gagMember_h5">'+content.data.name+'已被解除管理员身份</h5></li>')
                if(node){
                    removeByValue(admins,content.data.value);
                    if(historyOneMsgTime > 0){
                        if(((json.time - historyOneMsgTime)/1000) > 60){
                            $('#sayslist').append('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }
                    historyOneMsgTime = json.time;
                    $('#sayslist').append(s_oLi);
                    HtmlScrollBottom();
                }else{
                    if(prevMsgTimes > 0 ){
                        if(((prevMsgTimes - json.time)/1000) > 60){
                            $('#sayslistOneData').after('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                        }
                    }else{
                        historyOneMsgTime = json.time;
                    }
                    $('#sayslistOneData').after(s_oLi);
                    prevMsgTimes = json.time;
                }
            }
            break;

        //通知类消息///////////////
        case 'notification':
            var notype = json.attach.type;
            var timer = parseInt(json.time);
            var date = getLocalTime(timer);

            //if (notype == 'memberEnter' && from == TeacherFrom) {
            //	//当有人（老师）进入聊天室时
            //	//oLi =('<li class="trter"><h5>"'+json.attach.toNick + '"老师进入聊天室!' + date + '</h5></li>');
            //} else if (notype == 'memberExit' && from == TeacherFrom) {
            //	//当有人（老师）退出聊天室时
            //	//oLi =('<li class="trter"><h5>"'+json.attach.toNick + '"老师退出聊天室!' + date + '</h5></li>');
            if (node && notype == 'memberEnter'){
                if (from == _userId) {
                    userEnterList.push('您');   //  往用户进入数组中添加数据
                }else{
                    userEnterList.push(subStringMax(json.attach.fromNick,5));   //  往用户进入数组中添加数据
                }
            } else if (notype == 'gagMember') {
                ///当有人被加入禁言名单
                oLi = ('<li class="div_center"><h5 class="gagMember_h5">' + json.attach.toNick + '已被禁言</h5></li>');
                if(node){
                    gags.push(json.attach.to[0]);
                }
            } else if (notype == 'ungagMember') {
                //当有人被移除禁言名单时
                oLi = ('<li class="div_center"><h5 class="gagMember_h5">' + json.attach.toNick + '已被解除禁言</h5></li>');
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
            if (from == TeacherFrom || type == 'notification') {
                if(historyOneMsgTime > 0){
                    if(((json.time - historyOneMsgTime)/1000) > 60){
                        $('#sayslist').append('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                    }
                }
                historyOneMsgTime = json.time;
                $('#sayslist').append(oLi);
                if(type == 'image'){
                    ImageLoadEnd();
                }else{
                    HtmlScrollBottom();
                }
                if($('#audio')[0].ended && _userId != TeacherFrom && type == 'audio'){
                    mp3Inow = true;
                    iNow = arrMp3url.length -1;
                    audio(iNow);
                }
            }else{
                $('.barrage_list_box').append(oLi);
                if($('.barrage_list_box').children().length > 3){
                    $('.barrage_list_box').children().eq(0).remove();
                }
            }
        } else {
            if (from == TeacherFrom || type == 'notification') {
                if(prevMsgTimes > 0 ){
                    if(((prevMsgTimes - json.time)/1000) > 60){
                        $('#sayslistOneData').after('<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(json.time)+'</h5></li>');
                    }
                }else{
                    historyOneMsgTime = json.time;
                }
                $('#sayslistOneData').after(oLi);
                prevMsgTimes = json.time;

            }else{
                $('.barrage_list_box').prepend(oLi);
                if($('.barrage_list_box').children().length > 3){
                    $('.barrage_list_box').children().eq(0).remove();
                }
            }
        }
    }
};


function HistaddMsg(json) {
    var type = json.msgType; //收到消息类型
    var fromNick = json.fromNick; //呢称
    var fromAvatar = json.fromAvator || '/web/res/image/01.png'; //头像
    var from = json.fromAccount; // 账号
    console.log(json);
    var isTeacher = 'Teacher';
    var direction = 'megLeft';
    var cancelHtml = '';
    if(_userId == TeacherFrom){
        var cancelHtml = '<e class="cancelmsg">撤回</e>';
    }
    var oLi = '';
    switch (type) {
        //文本消息///////////////
        case 'TEXT':
            var text = json.attach; //文本消息内容
            oLi = ('<li msgid="'+json.id+'" uuid="'+json.msgidClient+'"  from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
						<div class="messageName"><em>老师</em> <i class="yd"></i>  ' + fromNick + '</div>\
						<div class="messageBox Text ' + isTeacher + '"><p>' + escapeHtml(text) + ''+cancelHtml+'</p></div>\
					</div>\
				</li>');
            break;

        //图片消息///////////////
        case 'PICTURE':
            var imgsrc = JSON.parse(json.attach).url;
            oLi = ('<li msgid="'+json.id+'" uuid="'+json.msgidClient+'" from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
						<div class="messageName"><em>老师</em> <i class="yd"></i> ' + fromNick + '</div>\
						<div class="messageBox Image ' + isTeacher + '"><p><img imgurl="'+imgsrc+'" src="' + imgsrc + '?x-oss-process=style/chatMsgImg">'+cancelHtml+'</p></div>\
					</div>\
				</li>');
            imgList.unshift(imgsrc);
            break;

        //语音消息///////////////
        case 'AUDIO':
            var jsonAudio = JSON.parse(json.attach);
            var mp3Url = jsonAudio.url;
            if (mp3Url.indexOf(".m4a") <= 0) {
                mp3Url += '?audioTrans&type=mp3';
            }
            var dur = Math.round(jsonAudio.dur / 1000);
            var andS = dur/60;
            var resendInow = '';
            arrMp3url.unshift(mp3Url);
            iNow++;
            var rem = ((dur-2)*((13.9-3.5)/(180-2))+3.5).toFixed(3);
            if(andS >= 1){
                andS = parseInt(andS) +"'"+toDou((dur%60))+'"';
            }else{
                andS = dur+'"'
            }
            if(_userId != TeacherFrom){
                if(audioPlayEndArr != null){
                    if(audioPlayEndArr.audioArr.indexOf(json.msgidClient) === -1){
                        resendInow = '<b class="resendIcon"></b>';
                    }else{
                        resendInow = '<b class="resendIcon hide"></b>';
                    }
                }else{
                    resendInow = '<b class="resendIcon"></b>';
                }
            }
            oLi = ('<li data-type="AUDIO" msgid="'+json.id+'" uuid="'+json.msgidClient+'" from ="' + from + '" name="' + fromNick + '" img="' + fromAvatar + '"  class="' + direction + ' clearfix audio_uuid">\
				<div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName "><em>老师</em><i class="yd"></i>  ' + fromNick + '</div>\
					<div class="messageBox Audio ' + isTeacher + '"><em class="loader"><em class="loading-3"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></em></em><p style="width:' + rem + 'rem;"><span class="audioIco"></span><span class="audioBar"><em><i></i></em></span><span time="'+dur+'" class="aud">' + andS + '</span>'+resendInow+''+cancelHtml+'</p></div>\
				</div>\
			</li>');
            break;

        //自定义消息///////////////
        case 'CUSTOM':
            var content = JSON.parse(json.attach);
            if(content.type == 11){
                var IDS = '0';
                if(content.data.ids){
                    IDS = (content.data.ids).toString();
                }

                oLi = ('<li msgid="'+json.id+'" class="trter"><h5><span><img src="'+fromAvatar+'"></span><span><em>'+ subStringMax(fromNick,6) + '</em><em>送了一个'+getRewardIcon(IDS)+'</span><i class="number_x1"><img src="/web/res/image/newaudio/number@2x.png"></i></h5></li>')

            }else if(content.type == 13){//添加管理员
                oLi = ('<li msgid="'+json.id+'" class="div_center"><h5 class="gagMember_h5">'+content.data.name+'已被提升为管理员</h5></li>');

            }else if(content.type == 14){//解除管理员
                oLi = ('<li msgid="'+json.id+'" class="div_center"><h5 class="gagMember_h5">'+content.data.name+'已被解除管理员身份</h5></li>')
            }
            break;

        //通知类消息///////////////
        case 'notification':
            var notype = json.attach.type;
            var timer = parseInt(json.time);
            var date = getLocalTime(timer);
            if (notype == 'gagMember') {
                ///当有人被加入禁言名单
                oLi = ('<li msgid="'+json.id+'" class="div_center"><h5 class="gagMember_h5">' + json.attach.toNick + '已被禁言</h5></li>');
            } else if (notype == 'ungagMember') {
                //当有人被移除禁言名单时
                oLi = ('<li msgid="'+json.id+'" class="div_center"><h5 class="gagMember_h5">' + json.attach.toNick + '已被解除禁言</h5></li>');
            }
            break;
        default:
            break;
    }
    //消息时间
    if(oLi!=''){
        if(prevMsgTimes > 0 ){
            if(((prevMsgTimes - json.msgTimestamp)/1000) > 60){
                oLi = oLi+'<li class="div_center divDates"><h5 class="gagMember_h5">'+getLocalTime(prevMsgTimes)+'</h5></li>';
            }
        }else{
            historyOneMsgTime = json.msgTimestamp;
        }
        prevMsgTimes = json.msgTimestamp;
        return oLi;
    }
};


function HtmlScrollBottom(){
    $('.tabBox').animate({
        scrollTop:  $('.says').height()
    }, 500);
};

function ImageLoadEnd(status){
    var load_img; // 定时器
    var isLoad = true; // 控制变量
// 判断图片加载状况，加载完成后回调
    isImgLoad(function(){
        if(status == 1){
            audio(iNow);
        }else if(status == 2){
            scrollToLocation(iNow);
        }else if(status == 3){
            audio();
        }else{
            HtmlScrollBottom();
        }
    });
// 判断图片加载的函数
    function isImgLoad(callback){
        // 查找所有封面图，迭代处理
        $('.Image img').each(function(){
            // 找到为0就将isLoad设为false，并退出each
            if(this.height === 0){
                isLoad = false;
                return false;
            }
        });
        // 为true，没有发现为0的。加载完毕
        if(isLoad){
            clearTimeout(load_img); // 清除定时器
            // 回调函数
            callback();
            // 为false，因为找到了没有加载完成的图，将调用定时器递归
        }else{
            isLoad = true;
            load_img = setTimeout(function(){
                isImgLoad(callback); // 递归扫描
            },200); // 我这里设置的是500毫秒就扫描一次，可以自己调整
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
setInterval(function(){
    if(userEnterList.length){
        $('.user_right').show().removeClass('_fadeOut _left').addClass('_slideInRight');
        $('.user_right em').html(userEnterList[0]);
        userEnterList.shift();
        setTimeout(function(){
            $('.user_right').addClass('_fadeOut').removeClass('_slideInRight');
            setTimeout(function(){
                $('.user_right').hide().addClass('_left');
            },1000)
        },1000);
    }
},2000);
var scrollTimeout = null;
$('.tabBox').on('scroll',function(){
    var scrollTop = $(this).scrollTop();
    var scrollHeight = $(this).height();
    if(endTime == '' && scrollTop == 0 && histDataNo){
        mp3Inow = false;
        if($('#sayslistOneData').length == 0){
            var msgId = $('#sayslist').children().eq(0).attr('msgid');
            historyMsgs(msgId);
        }
    }
    if(isScrollswitch && isPlayAudio && $('#sayslist .Audio').eq(iNow).length > 0 ){
        var iNowDivTop = $('#sayslist .Audio').eq(iNow).offset().top;
        var iNowDivHeight = $('#sayslist .audio_uuid').eq(iNow).height();
        clearTimeout(scrollTimeout);
        scrollTimeout =  setTimeout(function(){
            if( iNowDivTop + iNowDivHeight - $('.live_video').height() <= 0 || scrollHeight <= iNowDivTop - $('.live_video').height() - $('.send_message').height()){
                $('.back_active_positions').show();
                if(!$('.back_active_positions').hasClass('fadeInLeft')){
                    var times = null;
                    clearTimeout(times);
                    times = setTimeout(function(){
                        $('.back_active_positions').removeClass('fadeOutLeft').addClass('fadeInLeft');
                    },200)
                }
            }else{
                if(!$('.back_active_positions').hasClass('fadeOutLeft')){
                    var timesT = null;
                    clearTimeout(timesT);
                    timesT = setTimeout(function(){
                        $('.back_active_positions').removeClass('fadeInLeft').addClass('fadeOutLeft');
                    },200);
                }
            }
        },200)

    }
    $('.zhanweiDiv').hide();
});


/*********************回放页面函数********************/
function playback() {
    $('.send_message').remove();
    $('#sayslist').html('<li class="megLeft clearfix" >\
		<div class="headImg"><img src="/web/res/image/newaudio/default@2x.png"></div>\
		<div class="messageMain">\
		<div class="messageName">酸枣在线</div>\
		<div class="messageBox Text"><p>欢迎进入酸枣语音直播间！</p></div>\
		</div></li>');
    var offSet = 1;
    var playOK = true;
    Load();

    iNow = 0;
    var iNowStr = BaseFun.GetStorage("playHistory_" + courseId);
    if (iNowStr) {
        try{
            iNow = parseInt(iNowStr);
        } catch(e){};
        //长度达不到已播放的
        if (arrMp3url.length <= iNow) {
            iNow = 0 ;
        }
    }

    if (arrMp3url.length > 0) {
        var resendIcon = false;
        $('.Audio>p>.resendIcon').each(function(index){
            if($('.messageBox>p>.resendIcon').eq(index).css('display') != 'none'){
                resendIcon = true;
            }
        })
        if(resendIcon == true){
            BaseFun.Dialog.Pop({
                title: '直播已结束',
                text : '您有语音未收听哦',
                cancel:'一会再说',
                confirm:'自动播放',
                callback:function(index){
                    if(index == 1){
                        audio(iNow);
                    }
                }
            });
        }
    }


    //上拉加载
    $('.tabBox').scroll(function(){
        var scrollTop = $(this).scrollTop();
        var windowHeight = $(this).height();
        var scrollHeight = $('.says').height();
        if(scrollTop + windowHeight >= scrollHeight-30){
            Load();
        }
        $('.zhanweiDiv').hide();
    });

    //请求数据
    function Load(){
        if(!playOK){return false;}
        var relayCourseId='';
        if(oriCourseId == ''){
            relayCourseId = '';
        }else{
            relayCourseId = courseId;
        }
        var result = ZAjaxRes({url: "/weixin/live/getHistoryMsg.user", type: "GET", param:{courseId: _oriCourseId,relayCourseId: relayCourseId, offSet: offSet}});
        console.log(result);
        if (result.code == "000000") {
            var data = result.data;
            var dataArr = [];
            for(var i=0; i<data.length; i++){
                dataArr.push(playbackMsg(data[i]));
            };
            $('#sayslist').append(dataArr.join(''));
            offSet++;
        }else if(result.code == '000110'){
            playOK = false;
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
    //if (from == _userId) {
    //	var direction = 'megRight';
    //	//老师自己看回放
    //	if (from == TeacherFrom) {
    //		direction = 'megLeft';
    //	}
    //}else{
    var direction = 'megLeft';
    //}
    var h_Oli = '';
    if (type == 'TEXT') {//普通消息
        var text = json.attach; //消息内容
        if(from == TeacherFrom) {  //  老师文本消息
            h_Oli = ('<li from ="' + json.fromAccount + '" name="' + json.fromNick + '" img="' + json.fromAvator + '"   class="' + direction + ' clearfix">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
						<div class="messageName"><em>老师</em> <i class="yd"></i>  ' + fromNick + '</div>\
						<div class="messageBox Text ' + isTeacher + '"><p>' + escapeHtml(text) + '</p></div>\
					</div>\
				</li>');
        }
    } else if (type == 'CUSTOM') {//自定义消息
        var attach = JSON.parse(json.attach);
        var text = attach.data.value; //消息内容
        var nick = attach.data.nick;//@人
        if (attach.type == 5) {
            //$('#sayslist').append('<li class="' + direction + '">\
            //			<div class="headImg"><img src="' + fromAvatar + '"></div>\
            //			<div class="messageMain">\
            //			<div class="messageName ">' + fromNick + '</div>\
            //				<div class="messageBox Custom ' + isTeacher + '"><p>' + escapeHtml(text) + '</p><span class="customBg"></span></div>\
            //			</div>\
            //		</li>');
        } else if (attach.type == 6) {
            //$('#sayslist').append('<li class="' + direction + '">\
            //			<div class="headImg"><img src="' + fromAvatar + '"></div>\
            //			<div class="messageMain">\
            //			<div class="messageName ">' + fromNick + '</div>\
            //				<div class="messageBox Answer ' + isTeacher + '"><p><i>@' + nick + '</i>' + escapeHtml(text) + '</p><span class="answerBg"></span></div>\
            //			</div>\
            //		</li>');
        } else if(attach.type == 11){
            if(attach.data.ids){
                var IDS = (attach.data.ids).toString();
            }else{
                var IDS = '0';
            }
            switch (IDS) {
                //打赏消息///////////////
                case '1':
                    orewardIco = '掌声</em></span><span><img src="/web/res/image/newaudio/clapping@2x.png">'
                    break;
                case '2':
                    orewardIco = '小星星</em></span><span><img src="/web/res/image/newaudio/star@2x.png">'
                    break;
                case '3':
                    orewardIco = '么么哒</em></span><span><img src="/web/res/image/newaudio/kiss@2x.png">'
                    break;
                case '4':
                    orewardIco = '学士帽</em></span><span><img src="/web/res/image/newaudio/doctor@2x.png">'
                    break;
                default:
                    orewardIco = ''
                    break;
            }
            h_Oli = ('<li class="trter"><h5><span><img src="'+fromAvatar+'"></span><span><em>'+ subStringMax(fromNick,6) + '</em><em>送了一个'+orewardIco+'</span><i class="number_x1"><img src="/web/res/image/newaudio/number@2x.png"></i></h5></li>');
        }
    } else if (type == 'PICTURE') {
        var attach = JSON.parse(json.attach);
        var picsrc = attach.url;
        imgList.push(picsrc);
        h_Oli = ('<li from ="' + json.fromAccount + '" name="' + json.fromNick + '" img="' + json.fromAvator + '"  class="' + direction + '">\
						<div class="headImg"><img src="' + fromAvatar + '"></div>\
						<div class="messageMain">\
							<div class="messageName"><em>老师</em><i class="yd"></i> ' + fromNick + '</div>\
							<div class="messageBox Image ' + isTeacher + '"><p><img imgurl="'+picsrc+'" src="' + picsrc + '"></p></div>\
						</div>\
					</li>');
    } else if (type == "AUDIO") {
        var attach = JSON.parse(json.attach);
        var mp3Url = attach.url;
        if (mp3Url.indexOf(".m4a") <= 0) {
            mp3Url += '?audioTrans&type=mp3';
        }
        var dur = Math.round(attach.dur / 1000);
        var andS = dur/60;
        var resendInow = '';
        arrMp3url.push(mp3Url);
        var rem = ((dur-2)*((13.9-3.5)/(180-2))+3.5).toFixed(3);
        if(andS >= 1){
            andS = parseInt(andS) +"'"+toDou((dur%60))+'"';
        }else{
            andS = dur+'"'
        }
        if(_userId != TeacherFrom){
            if(audioPlayEndArr != null){
                if(audioPlayEndArr.audioArr.indexOf(json.msgidClient) === -1){
                    resendInow = '<b class="resendIcon"></b>';
                }else{
                    resendInow = '<b class="resendIcon hide"></b>';
                }
            }else{
                resendInow = '<b class="resendIcon"></b>';
            }
        }
        h_Oli = ('<li uuid="'+json.msgidClient+'" from ="' + json.fromAccount + '" name="' + json.fromNick + '" img="' + json.fromAvator + '"  class="' + direction + ' audio_uuid">\
				<div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName "><em>老师</em> <i class="yd"></i>  ' + fromNick + '</div>\
					<div class="messageBox Audio ' + isTeacher + '"><em class="loader"><em class="loading-3"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></em></em><p style="width:' + rem + 'rem;"><span class="audioIco"></span><span class="audioBar"><em><i></i></em></span><span time="'+dur+'" class="aud">' + andS + '</span>'+resendInow+'</p></div>\
				</div>\
			</li>');
    }
    return  h_Oli;
};

//输入框
$(function(){
    var winHeight = $(window).height();
    if(Android){
        $('#content_box').css({
            position:'absolute',
            height:winHeight,
            bottom:0
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
                },500);
            }
        }
        if(Android){
            setTimeout(function(){
                window.scrollTo(0,winHeight);
            },500);
        }
    }
    //**************屏幕自适应
    //function autoWH() {
    //	//加载时适应浏览器高度
    //	var width = $(window).outerWidth();
    //	var height = $(window).outerHeight();
    //	var topHeight = $('.live_box').position().top;
    //	$('.live_box,.tabBox,#bd_box').css('height', height - topHeight);
    //}
    //autoWH();
    //$(window).resize(function() {
    //	//改变窗体大小时适应浏览器高度
    //	Torem();
    //	autoWH();
    //});
    //课件收起展开
    $('.live_kj_toggle').on('click',function(){
        if($(this).hasClass('off')){
            $("#firstImg").show();
            $(this).removeClass('off').html('收起');
            $('.video_box').show().addClass('animated fadeInDown').removeClass('fadeOutUp');
            $('.live_video').css('height','12.525rem').addClass('Transition_1s');
            $('.clickLiveRoom').css({'top':'5rem'});
            $(".Group1").css({'top':'2.5rem'});
            $('.tabBox').addClass('Transition_1s');
            BaseFun.SetStorage('LiveKjSwitch'+courseId+'_'+_userId,1);
            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','0.67rem');
                if(endTime){
                    $(".Group1").css('top','1rem');
                }else{
                    $(".Group1").css('top','3rem');
                }
                $(".clickLiveRoom").hide();
            }else{
                $('.clickFollow').removeClass('clickFollow_active');
            }
        }else{
            $(this).addClass('off').html('展开');
            $('.video_box').addClass('animated fadeOutUp').removeClass('fadeInDown');
            $('.live_video').css('height','2.025rem').addClass('Transition_1s');
            $('.clickLiveRoom').css({'top':'7rem','opacity':'1'});
            $(".Group1").css({'top':'4.5rem','opacity':'1'});
            $('.tabBox').addClass('Transition_1s');
            BaseFun.SetStorage('LiveKjSwitch'+courseId+'_'+_userId,0);
            if(_userId == TeacherFrom){
                $('.closeLiveBtn').css('top','2.65rem');
                if(endTime){
                    $(".Group1").css('top','3rem');
                }else{
                    $(".Group1").css('top','5rem');
                }
                $(".clickLiveRoom").hide();
            }else{

                $('.clickFollow').addClass('clickFollow_active');
            }
        }
    });
    //   切换极简模式
    //$('.clickBrief').on('click',function(){
    //    window.location.href = '/weixin/voicelistening.user?courseId='+ courseId +'&invitationAppId=';
    //})
    //   关注直播间
    $('.clickLiveRoom').on('click',function(){
        if ($(this).hasClass("on")) {
            isCFollow = false;
            follow(this, 1);
        } else {
            isCFollow = true;
            follow(this, 0);
        }
    })
});

function follow(obj, isFollow) {
    var _liveRoomId = '';
    if(oriRoomId == '' ){
        _liveRoomId = roomId;
    }else{
        _liveRoomId = oriRoomId;
    }
    if (isFollow == 0) {
        var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + _liveRoomId, type: "GET"});
        if (result.code == "000000" || result.code == "000111") {
            $(obj).addClass("on");
            pop1({"content": "已关注" ,width:"6rem", "status": "normal", time: '800'});
        }
    } else {
        var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + _liveRoomId, type: "GET"});
        if (result.code == "000000") {
            $(obj).removeClass("on");
            pop1({"content": "取消关注"  , width:"6rem", "status": "normal", time: '800'});
        }
    }
}
//返回顶部
$(function() {
    var timerBack = null;
    $('.tabBox').scroll(function(){
        clearTimeout(timerBack);
        if ($('.tabBox').scrollTop()>=300){
            $("#back_box").stop(true,true).fadeIn(500);
            timerBack = setTimeout(function(){
                $("#back_box").stop(true,true).fadeOut(500);
            },3000);
        }
    });
    $("#back-to-top").click(function(){
        $('.tabBox').animate({scrollTop:0},500);
        return false;
    });
    $("#back-to-bottom").click(function(){
        $('.tabBox').animate({scrollTop:$('#sayslist').height()},500);
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
$('.reward').on('click',function(e){
    $('.reward_box').show();
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
        spaceBetween: -2
    });
    e.stopPropagation();
});
//点击打赏
var timers = null;
var aRewardNo = true;
$('.aReward').click(function () {
    if(aRewardNo == true){
        $(this).addClass('aRewardNo');
        aRewardNo = false;
        statisticsBtn({'button':'009','referer':'','courseId':courseId})
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
        //if(parseFloat(amount) > parseFloat(balance)){
        //rexb();
        //$("#balanceText").text("您的余额不足，请充值。");
        //}
        var data  = userReward2(userRewardTypeId , courseId);//打赏 pay.js
        if(data){
            $("#balance").html(data.afterBalance);
            if(_isEnd == '0'){
                rewardMsg(amount,userRewardTypeId);
            } else {
                var now = new Date().getTime();
                var t_oLi = ('<li class="trter"><h5><span><img src="'+_photo+'"></span><span><em>'+ subStringMax(_userName,6) + '</em><em>送了一个'+getRewardIcon(userRewardTypeId)+'</span><i class="number_x1"><img src="/web/res/image/newaudio/number@2x.png"></i></h5></li>')
                if (historyOneMsgTime > 0) {
                    if (((now - historyOneMsgTime) / 1000) > 60) {
                        $('#sayslist').append('<li class="div_center divDates"><h5 class="gagMember_h5">' + getLocalTime(now) + '</h5></li>');
                    }
                }
                historyOneMsgTime = now;
                $('#sayslist').append(t_oLi);
                $('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
            }
        }
        setTimeout(function(){
            $('.aReward').removeClass('aRewardNo');
            aRewardNo = true;
        },500);
    }

});
function getRewardIcon(Ids) {
    switch (Ids) {
        //打赏消息///////////////
        case '1':
            orewardIco = '掌声</em></span><span><img src="/web/res/image/newaudio/clapping@2x.png">'
            break;
        case '2':
            orewardIco = '小星星</em></span><span><img src="/web/res/image/newaudio/star@2x.png">'
            break;
        case '3':
            orewardIco = '么么哒</em></span><span><img src="/web/res/image/newaudio/kiss@2x.png">'
            break;
        case '4':
            orewardIco = '学士帽</em></span><span><img src="/web/res/image/newaudio/doctor@2x.png">'
            break;
        default:
            orewardIco = ''
            break;
    }
    return orewardIco;
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
};
//点击空白区域关闭
$(document).click(function(e){
    var _con = $('.reward_box,.send_message');   // 设置目标区域
    if(!_con.is(e.target) && _con.has(e.target).length === 0){
        $('.reward_box').hide();

    }
});
//打赏切换
$('#rewardList').on('click','#rewardList .swiper-slide',function(){
    $('#rewardList .swiper-slide').removeClass('on');
    var userRewardTypeId = $(this).attr("userRewardTypeId")
    statisticsBtn({'button':'010','referer':'','courseId':courseId,'rewardId':userRewardTypeId});
    $(this).addClass('on');
});


//跳转到贡献榜
//$('.contributionList').click(function(){
//	window.location.href = "/weixin/contributionList?courseId=" + courseId
//});

//图片列表
$(function(){
    $('#sayslist').on('click', '#sayslist .Image img', function () {
        var imgsrc = $(this).attr('imgurl');
        if(imgsrc != undefined){
            wx.previewImage({
                current: imgsrc,
                urls: imgList
            });
        }
    });
});



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
    $(".rechargeBox").show();
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

/**
 *
 * 学生收到消息 ， 设置老师状态
 * @param value
 */
function receTeacherInputing(value) {
    $(document).attr("title",value);
    //2分钟清理,老师输入状态
    inputingTimeout = setTimeout(function () {
        $(document).attr("title",documentTitle);
    } , 3 * 1000 * 60);
}
function receTeacherDisInputing() {
    $(document).attr("title",documentTitle);
    clearTimeout(inputingTimeout);
}

//倒计时
function tick(){
    var oNow = new Date();

    var oS = parseInt((liveStartTime-oNow.getTime())/1000);


    if(oS<=0){
        $(".timerbox").html("");
        return
    }
    //5.通过时间差，求天，小时，分钟，秒数
    var s = parseInt(oS % 60);
    var m = parseInt(oS / (60) % 60);
    var h = parseInt(oS / (60 * 60) % 24);
    var d = parseInt(oS / (24 * 60 * 60));
    //6.把得到的时间，给元素的内容。
    var str = toDou(d) + toDou(h) + toDou(m) + toDou(s);
    for(var i=0;i<str.length;i++){
        $('.timerbox img').eq(i).attr('src','/web/res/image/'+str.charAt(i)+'.png');
    }
};



