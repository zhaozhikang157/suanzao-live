//注意这里, 引入的 SDK 文件不一样的话, 你可能需要使用 SDK.Chatroom.getInstance 来调用接口
var chatroom = null;
var appKeyStr = '';
var roomAddr = null;
var chatRoomId = '';
var yunxinToken = '';
var accid = '';
var TeacherFrom='';

var startTime ='';
//语音播放地址数组
var arrMp3url = [];
var iNow = 0;
//直播类型 0-视频直播 1-语音直播
var liveWay;
var videoUrl;
$(function(){
	//获取聊天室信息
    $.getJSON("/course/getCourseByVirtualUser?courseId=" + courseId +"&userId=" + userId + "&isInRoom=" + isInRoom
        , function(data){
            if (data.code == '000000') {
                data = data.data;
				console.log(data);
                appKeyStr = data.chatRoom.appKey;

                yunxinToken = data.chatRoom.yunxinToken;
                chatRoomId = data.chatRoom.chatRoomId;
                roomAddr = data.chatRoom.roomAddr;
				TeacherFrom = data.course.appId;
				endTime = data.course.endTime;
				liveWay =data.course.liveWay;
				if(endTime != ''){
					//结束时间 直播已结束看回放
					playback();
					alert('直播已结束');
				}else{
					chatroom = SDK.Chatroom.getInstance({
						appKey: appKeyStr,
						account: userId,
						token: yunxinToken,
						chatroomId: chatRoomId,
						chatroomAddresses: roomAddr,
						onconnect:function(msg){
							//进入聊天室
							console.log('进入聊天室', msg);
							arrMp3url = [];
							//设置获取历史记录时间
							startTime=msg.member.enterTime;
							//获取聊天室历史记录100条
							//historyMsgs();
							//获取成员列表
							//getMember();
						},
						onerror: onChatroomError,
						onwillreconnect: onChatroomWillReconnect,
						ondisconnect: onChatroomDisconnect,
						// 消息
						onmsgs: onChatroomMsgs
                	});
				}              
            }else if(data.code == '000107'){
                alert(data.message);	
            }
	});

});


/**正在直播**/
function onChatroomWillReconnect(obj) {
    // 此时说明 `SDK` 已经断开连接, 请开发者在界面上提示用户连接已断开, 而且正在重新建立连接
    console.log('即将重连', obj);
}
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
			if(error.reason==="managerKick"){
				alert('你已被管理员移出');   
			}else if(error.reason==="blacked"){
				alert('你已被管理员拉入黑名单，不能再进入'); 
			}else if(error.reason==="chatroomClosed"){
				alert("直播间已关闭")
			}	
            break;
        default:
            break;
        }
    }
}
function onChatroomError(error, obj) {
    console.log('发生错误', error, obj);
}


//收到的聊天室消息
function onChatroomMsgs(msgs) {
    console.log('收到聊天室消息', msgs);
	if(msgs.type){
		var arr = [];
		arr.push(msgs);
	}else{
		arr = msgs;
	}
	console.log(arr);
	
   	$.each(arr, function () {
		console.log(this);
			var fromNick = this.fromNick; //呢称
			var fromAvatar = this.fromAvatar; //头像
			!fromAvatar?fromAvatar ='/web/res/style/img/01.png':fromAvatar = fromAvatar; //设置默认头像
			var from = this.from; // 账号
			if(from == TeacherFrom){
				var teacher ='teacher';
			}else{
				var teacher ='';
			}
			var type = this.type; //收到消息类型
			switch (type) {
				//文本消息///////////////
				case 'text':
					var text = this.text; //文本消息内容
					$('#sayslist').append('<li class="message'+teacher+'">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p>'+text+'</p>\
						</div>\
					</li>');
					break;
					
				//图片消息///////////////
				case 'image':
					var imgsrc = this.file.url;
					$('#sayslist').append('<li class="message'+teacher+'">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p><img src="'+imgsrc+'"></p>\
						</div>\
					</li>');
					break;
					
				//语音消息///////////////
				case 'audio': 
					var mp3Url = this.file.mp3Url;
					var dur = Math.ceil(this.file.dur/1000)
					arrMp3url.push(mp3Url);
					var px = dur*5;
					 $('#megMore').after('<li class="message teacher audio">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p style="width:'+px+'px;"></p>\
							<span class="aud">'+dur+'”</span>\
						</div>\
					</li>');
					if($('#audio')[0].ended){
						iNow = arrMp3url.length-1;
						audio(iNow);
					}
					break;
					
				//自定义消息///////////////
				case 'custom':
					var content = JSON.parse(this.content);
					if(content.type == 5){
						//学生提问
						$('#sayslist').append('<li class="message">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p>'+content.data.value+'</p>\
							</div>\
							<span class="custom">问</span>\
						</li>');
					}else if(content.type == 6){
						//老师回答
						$('#sayslist').append('<li class="message teacher">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p><i>@'+content.data.nick+'</i>'+content.data.value+'</p>\
							</div>\
						</li>');
					}else if(content.type == 7){
						// //推流地址改变
						// videoUrl = content.data.liveAddress;
						// video(videoUrl);
					}
					break;
					
				//通知类消息///////////////
				case 'notification':
					var notype = this.attach.type;
					if(notype == 'memberEnter'){
						
					}else if(notype == 'memberEnter'){
						
					}
				
				 break;
				default:
					break;
			 }
	});
	$('#sayslist').parents('.says').scrollTop($('#sayslist').height());
};


function historyMsgs(){
	chatroom.getHistoryMsgs({
		timetag: startTime,
		limit: 100,
		done: getHistoryMsgsDone
	});
};
function getHistoryMsgsDone(error, obj) {
    console.log('获取聊天室历史' + (!error?'成功':'失败'), error, obj.msgs);
	var msg = obj.msgs;	
	if(!error){
		
		$.each(msg, function() {
			//当前时间
			startTime = this.time;
			var fromNick = this.fromNick; //呢称
			var fromAvatar = this.fromAvatar; //头像
			!fromAvatar?fromAvatar ='/web/res/style/img/01.png':fromAvatar = fromAvatar; //设置默认头像
			var from = this.from; // 账号
			if(from == TeacherFrom){
				var teacher ='teacher';
			}else{
				var teacher ='';
			}
			var type = this.type; //收到消息类型
			switch (type) {
				//文本消息///////////////
				case 'text':
					var text = this.text; //文本消息内容
					$('#megMore').after('<li class="message'+teacher+'">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p>'+text+'</p>\
						</div>\
					</li>');
					break;
					
				//图片消息///////////////
				case 'image':
					var imgsrc = this.file.url;
					$('#megMore').after('<li class="message'+teacher+'">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p><img src="'+imgsrc+'"></p>\
						</div>\
					</li>');
					break;
					
				//语音消息///////////////
				case 'audio': 
					var mp3Url = this.file.mp3Url;
					var dur = Math.ceil(this.file.dur/1000)
					arrMp3url.push(mp3Url);
					var px = dur*5;
					 $('#megMore').after('<li class="message teacher audio">\
						<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
						<div class="message_box">\
							<p style="width:'+px+'px;"></p>\
							<span class="aud">'+dur+'”</span>\
						</div>\
					</li>');
					if($('#audio')[0].ended){
						iNow = arrMp3url.length-1;
						audio(iNow);
					}
					break;
					
				//自定义消息///////////////
				case 'custom':
					var content = JSON.parse(this.content);
					if(content.type == 5){
						//学生提问
						$('#megMore').after('<li class="message">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p>'+content.data.value+'</p>\
							</div>\
							<span class="custom">问</span>\
						</li>');
					}else if(content.type == 6){
						//老师回答
						$('#megMore').after('<li class="message teacher">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p><i>@'+content.data.nick+'</i>'+content.data.value+'</p>\
							</div>\
						</li>');
					}else if(content.type == 7){
						// //推流地址改变
						// videoUrl = content.data.liveAddress;
						// video(videoUrl);
					}
					break;
					
				//通知类消息///////////////
				case 'notification':
					var notype = this.attach.type;
					if(notype == 'memberEnter'){
						
					}else if(notype == 'memberEnter'){
						
					}
				
				 break;
				default:
					break;
			 }
		});
		if(msg.length==0){
			$('#megMore').hide();
			return;
		}
		$('#megMore').show();
	}else{
		console.log(error.message)
	}
};



//获取聊天室成员列表
function getMember(){
	chatroom.getChatroomMembers({
		guest: false,
		limit: 100,
		done: getChatroomMembersDone
	});
};

function getChatroomMembersDone(error, obj) {
    console.log('获取聊天室成员' + (!error?'成功':'失败'), error, obj.members);
	if(!error){
		var json = obj.members;
		$.each(json,function(){
			var avatar = this.avatar; //头像
			!avatar?avatar ='/web/res/style/img/01.png':avatar = avatar; //设置默认头像
			
			$('#cylist').append('<li id="'+this.account+'">\
				<div class="head_img"><img src="'+avatar+'">\
				<span>'+this.nick+'</span>\
				</div>\
			</li>');
		});
	}
};


$(function(){
	//语音顺序自动播放
	$('#sayslist').on('click','#sayslist>li.audio',function(){
		iNow = $(this).index('.audio');
		audio(iNow);	
	});
	//$('#megMore').click(function(){
		//historyMsgs();
	//});
	
	$('#sayslist').on('click','.gag',function(){
		var from = $(this).attr('from');
		Gag(from);
	});
	
	$('#sayslist').on('click','.cancel',function(){
		var from = $(this).attr('from');
		cancel(from);
	});
    $('#send').off('click', say);
    $('#send').on('click', say);
    //绑定发送提问事件,先解后绑，避免重复绑定
    $('#quiz').off('click', quiz);
    $('#quiz').on('click', quiz);

	
	
});
//消息开关
var msgOk = true;
//发送聊天室消息
function say() {
    //发普通送的消息
    var text =$.trim($('#say').val());
    if (text&&msgOk) {
        //发普通文本消息
        var msg = chatroom.sendText({
            text: text,
            done: sendChatroomMsgDone
        });
        msgOk = false;
    } else {
        return false;
    }
};
//发送聊天室消息
function sendChatroomMsgDone(error, msg) {
    msgOk = true;
    if (!error) {
        //会话UI
        onChatroomMsgs(msg);
    } else {
        alert(error.message);
    }
    //初始化输入框
    $('#say').val('');
};

//提问
function quiz(){
    //发送提问的消息
    var text = $.trim($('#say').val());
    if (text&&msgOk) {
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
function audio(){
	clearInterval(tiemr);
	$('#sayslist li.audio').removeClass('on');
	$('#sayslist li.audio').eq(iNow).addClass('on Play');
	$('#audio')[0].src = arrMp3url[iNow];
	$('#audio')[0].play();
	var tiemr = setInterval(function(){
		if($('#audio')[0].ended){
			clearInterval(tiemr);
			if(iNow<arrMp3url.length-1){
				iNow++;
				audio(iNow);
			}else{
				$('#sayslist li.audio').removeClass('on');
			}
	   }
   },10);
};

/*********************回放页面函数********************/
function playback() {
	$('#megMore').remove();
	$('.qj').remove();
    $.getJSON("/course/getHistoryMsg?courseId=" + courseId, function (data) {
		console.log(data+'上');
        if (data.code == '000000') {
            var data =data.data;
			console.log(data);
            if(data.length==0){
                $('#sayslist').append('<li><h5>没有消息记录</h5></li>');
            }
            $.each(data, function () {
                var fromAvatar = this.fromAvator || '/web/res/image/01.png'; //头像
                var type = this.msgType; //消息类型
                var fromNick = this.fromNick; //呢称
                var from = this.fromAccount; // 账号
                //消息者身份
                if (from == TeacherFrom) {
                    var identity = 'teacher';
                    var teacherImg = '';
                } else {
                    var identity = '';
                    var teacherImg = '';
                }

                if (type == 'TEXT') {//老师普通消息
                    var text = this.attach; //消息内容
                    $('#sayslist').append('<li class="message ' + identity + '">\
									<div class="head_img"><img src="' + fromAvatar + '">' + teacherImg + '<span>' + fromNick + '</span></div>\
									<div class="message_box">\
										<p>' + text + '</p>\
									</div>\
								</li>');
                } else if (type == 'CUSTOM') {//老师回答
                    var attach = JSON.parse(this.attach);
                    var text = attach.data.value; //消息内容
                    var nick = attach.data.nick;//@人
                    if (attach.type == 5) {
                        $('#sayslist').append('<li class="message ' + identity + '">\
									<div class="head_img"><img src="' + fromAvatar + '">' + teacherImg + '<span>' + fromNick + '</span></div>\
									<div class="message_box">\
										<p>' + text + '</p><span class="custom">问</span>\
									</div>\
								</li>');
                    } else if (attach.type == 6) {
                        $('#sayslist').append('<li class="message  ' + identity + '">\
									<div class="head_img"><img src="' + fromAvatar + '">' + teacherImg + '<span>' + fromNick + '</span></div>\
									<div class="message_box">\
										<p><i>@' + nick + '</i>' + text + '</p>\
									</div>\
								</li>');
                    }
                } else if (type == 'PICTURE') {
                    var attach = JSON.parse(this.attach);
                    var picsrc = attach.url;
                    $('#sayslist').append('<li class="message  ' + identity + '">\
									<div class="head_img"><img src="' + fromAvatar + '">' + teacherImg + '<span>' + fromNick + '</span></div>\
									<div class="message_box">\
										<p><img src="' + picsrc + '"></p>\
									</div>\
								</li>');
                } else if (type == "AUDIO") {
                    var attach = JSON.parse(this.attach);
                    var mp3Url = attach.url+'?audioTrans&type=mp3';
                    var dur = Math.ceil(attach.dur / 1000);
                    arrMp3url.push(mp3Url);
                    var rem = dur/4;
                    $('#sayslist').append('<li class="message  ' + identity + ' audio">\
									<div class="head_img"><img src="' + fromAvatar + '">' + teacherImg + '<span>' + fromNick + '</span></div>\
									<div class="message_box">\
										<p style="width:' + rem + 'rem;"></p>\
										<span class="aud">' + dur + '”</span>\
									</div>\
								</li>');
                }

            });

        }
    });
};



//打赏功能
//打赏显示隐藏
$(function(){
	$('.reward_box').hide();
	$('.reward_box').css({opacity:'1'})
	
	
	$('.reward').click(function(){
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
				'<img src="/web/res/image/reward_ico2.png">' +
				'<span>' + userReward.amount + '学币</span>' +
				'</div>';
			elem_obj.append(div);
		}
		$("#balance").html(accountAmount);
		/*打赏*/
		var rewardList = new Swiper('#rewardList', {
			slidesPerView: 4,
			paginationClickable: true,
			spaceBetween: 0
		});
	});
	//点击充值
	//$('.Recharge').click(function(){
	//	window.location.href = "/weixin/recharge";
	//})
	//点击打赏
	var timers = null;
	$('.aReward').click(function () {
		var userRewardType = $('#rewardList .on');
		var userRewardTypeId = userRewardType.attr("userRewardTypeId")
		var amount = userRewardType.attr("amount");//金额
		if(!userRewardTypeId) {
			alert("请选择要打赏的金额!");
			return;
		}
		var balance = $("#balance").html();
		if(parseFloat(amount) > parseFloat(balance)){
			$('.reward_box h6').html('您学币余额不足，请充值！').fadeIn();
			clearTimeout(timers);
			timers = setTimeout(function(){
				$('.reward_box h6').fadeOut();
			},2000);
			return;
		}
		var data  = userReward(userRewardTypeId , courseId);//打赏 pay.js
		if(data){
			$("#balance").html(data.afterBalance);
			$('.reward_box h6').html('打赏成功！').fadeIn();
			clearTimeout(timers);
			timers = setTimeout(function(){
				$('.reward_box h6').fadeOut();
			},2000);
			rewardMsg(data.data.amount);
		}
	})
	//关闭
	$('.reward_box>h5>span').click(function(){
		$('.reward_box').hide();
	})
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
		$(this).addClass('on');
	});

});