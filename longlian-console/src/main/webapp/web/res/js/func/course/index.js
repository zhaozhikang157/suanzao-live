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
var courselist = [];
//直播地址
var videoUrl = '';

$(function(){
	//获取聊天室信息
    $.getJSON("/course/getCourse?courseId=" + courseId
        , function(data){
            if (data.code == '000000') {
                data = data.data;
				console.log('调取接口',data);
                appKeyStr = data.chatRoom.appKey;
                accid = data.chatRoom.userId;
                yunxinToken = data.chatRoom.yunxinToken;
                chatRoomId = data.chatRoom.chatRoomId;
                roomAddr = data.chatRoom.roomAddr;
				TeacherFrom = data.course.appId;
				endTime = data.course.endtime;
				if(endTime){
					videoUrl = data.course.videoAddres;
					if (videoUrl) {
						alert('视频正在录制中，请稍后再看回放！')
					}
					$('#video').attr('src',videoUrl);	
				}else{
					videoUrl = data.course.hlsLiveAddress;
					$('#video').attr('src',videoUrl);
					chatroom = SDK.Chatroom.getInstance({
						appKey: appKeyStr,
						account: accid,
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
							historyMsgs();
							//绑定发送消息事件
							$('#send').bind('click',say);
						},
						onerror: onChatroomError,
						onwillreconnect: onChatroomWillReconnect,
						ondisconnect: onChatroomDisconnect,
						// 消息
						onmsgs: onChatroomMsgs
                	});
				}
				//课件
				courselist = data.courseWare;
				coursewares(courselist);                
            } else {
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
	msgs = msgs[0];
    var type = msgs.type; //收到消息类型
	var fromNick = msgs.fromNick; //呢称
	var fromAvatar = msgs.fromAvatar; //头像
	!fromAvatar?fromAvatar ='/web/res/image/01.png':fromAvatar = fromAvatar; //设置默认头像
	var from = msgs.from; // 账号
	if(from == TeacherFrom){//老师的消息
		if(type=='text'){//老师普通消息
			var text = msgs.text; //消息内容
			$('#sayslist').append('<li class="message teacher">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p>'+text+'</p>\
							</div>\
						</li>');		
		}else if(type=='custom'){//老师回答
			var text =JSON.parse(msgs.content).data.value; //消息内容
			var account = JSON.parse(msgs.content).data.account;//@人
			$('#sayslist').append('<li class="message teacher">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p><i>@'+account+'</i>'+text+'</p>\
							</div>\
						</li>');
		}else if(type=='image'){
			$('#sayslist').append('<li class="message teacher">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p><img src="'+msgs.file.url+'"></p>\
							</div>\
						</li>');
		}else if(type=='audio'){
			var mp3Url = msgs.file.mp3Url;
			var dur = Math.ceil(msgs.file.dur/1000)
			arrMp3url.push(mp3Url);
			var rem = dur/4;
			$('#sayslist').append('<li class="message teacher  audio">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p style="width:'+rem+'rem;"></p>\
								<span class="aud">'+dur+'”</span>\
							</div>\
						</li>');
			if($('#audio')[0].ended){
				iNow = arrMp3url.length-1;
				audio(iNow);
			}
			
		}
	}else{//学生的消息
		if(type=='text'){//学生普通消息
			var text = msgs.text; //消息内容
			$('#sayslist').append('<li class="message">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p>'+text+'</p>\
							</div>\
						</li>');		
		}else if(type=='custom'){//学生提问
			var text =JSON.parse(msgs.content).data.value; //消息内容
			$('#sayslist').append('<li class="message">\
							<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
							<div class="message_box">\
								<p>'+text+'</p><span class="custom">问</span>\
							</div>\
						</li>');
		}
	}
	$('#sayslist').parents('.bd').scrollTop($('#sayslist').height());
}



function historyMsgs(){
	chatroom.getHistoryMsgs({
		timetag: startTime,
		limit: 100,
		done: getHistoryMsgsDone
	});
}
function getHistoryMsgsDone(error, obj) {
    console.log('获取聊天室历史' + (!error?'成功':'失败'), error, obj.msgs);
	var msg = obj.msgs;	
	if(!error){
		if(msg.length==0){
			$('#megMore').hide();
			return;
		}
		$.each(msg, function() {
			//当前时间
			startTime = this.time;
			var fromAvatar = this.fromAvatar; //头像
			if(!fromAvatar){
				fromAvatar = '/web/res/image/01.png';
			}
			var type = this.type; //消息类型
			var fromNick = this.fromNick; //呢称
			var from = this.from; // 账号
			if(from == TeacherFrom){//老师的消息
				if(type=='text'){//老师普通消息
					var text = this.text; //消息内容
					$('#megMore').after('<li class="message teacher">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p>'+text+'</p>\
									</div>\
								</li>');		
				}else if(type=='custom'){//老师回答
					var text =JSON.parse(this.content).data.value; //消息内容
					var nick = JSON.parse(this.content).data.nick;//@人
					$('#megMore').after('<li class="message teacher">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p><i>@'+nick+'</i>'+text+'</p>\
									</div>\
								</li>');
				}else if(type=='image'){
					$('#megMore').after('<li class="message teacher">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p><img src="'+this.file.url+'"></p>\
									</div>\
								</li>');
				}else if(type=='audio'){
					var mp3Url = this.file.mp3Url;
					var dur = Math.ceil(this.file.dur/1000)
					arrMp3url.unshift(mp3Url);
					var rem = dur/4;
					$('#megMore').after('<li class="message teacher audio">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p style="width:'+rem+'rem;"></p>\
										<span class="aud">'+dur+'”</span>\
									</div>\
								</li>');
				}
			}else{
				if(type=="custom"){
					var text =JSON.parse(this.content).data.value; //消息内容
					$('#megMore').after('<li class="message">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p>'+text+'</p><span class="custom">问</span>\
									</div>\
								</li>');
				}else if(type=="text"){
					var text = this.text; //消息内容
					$('#megMore').after('<li class="message">\
									<div class="head_img"><img src="'+fromAvatar+'"><span>'+fromNick+'</span></div>\
									<div class="message_box">\
										<p>'+text+'</p>\
									</div>\
								</li>');
				}	
			}	
			
			$('#sayslist').parents('.bd').scrollTop(0);
		});
		$('#megMore').show();
	}else{
		$('#megMore').hide();
		console.log(error.message)
	}
}


$(function(){
	//语音顺序自动播放
	$('#sayslist').on('click','#sayslist>li.audio',function(){
		iNow = $(this).index('.audio');
		audio(iNow);	

	});
	$('#megMore').click(function(){
		historyMsgs();
	});
	
	
});
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

