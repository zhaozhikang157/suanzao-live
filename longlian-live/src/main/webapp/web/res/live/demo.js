
var chatRoomId = "1";

var socket = null;
var TeacherFrom = '';

$(document).ready(function(){
      socket = io('http://localhost:9092/' + chatRoomId + "?token=" + token + "&uuid=" + uuid);

    socket.on('connect', function(){
        console.log("已连接上");
    });
    socket.on('message', function(data){
        console.log("收到消息:" , data);
        sendChatroomMsgDone(null, data);
    });
    socket.on('system', function(data){
        console.log("收到系统消息:" , data);
    });
    socket.on('disconnect', function(){});
});

//发送聊天室消息
function say() {
    var date = new Date();
    //发普通送的消息
    var text =$.trim($('#say').val());
    var jsonObject = {'@class': 'com.longlian.model.IMMessage',
        "type": "text",
        "text": text,
        "fromClientType":"WEB",
        msgTimestamp:date.getTime(),
        msgidClient:getUUID()
    };
    socket.json.send(jsonObject);
};
function getUUID() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
   // s[8] = s[13] = s[18] = s[23] = "-";

    return s.join("");
}
//发送聊天室消息
function sendChatroomMsgDone(error, msg) {
	msgOk = true;
    if (!error) {
        //会话UI
        addMsg(msg, 1);
    } else {
        alert(error.message);
    }
    //初始化输入框
    $('#say').val('');
};


//************************************公共函数***********************************//
//补零
function toDou(n){
	return n>=10?n:'0'+n;
};

//时间戳转时间
function getLocalTime(time){
	var oDate=new Date();
	oDate.setTime(time);
	return oDate.getFullYear()+'年'+toDou(oDate.getMonth()+1)+'月'+toDou(oDate.getDate())+'日 '+toDou(oDate.getHours())+':'+toDou(oDate.getMinutes())+':'+toDou(oDate.getSeconds());
};


// 直播创建会话UI函数
function addMsg(json, node) {
    var type = json.type; //收到消息类型
    var fromNick = json.fromNick; //呢称
    var fromAvatar = json.fromAvatar || '/web/res/image/01.png'; //头像
    var from = json.from; // 账号

    //是否是老师发的消息
    if (from == TeacherFrom) {
        var isTeacher = 'Teacher';
    }else{
		var isTeacher = '';
	}

	//是否是自己发的消息
	if (from == uuid) {
    	var direction = 'megRight';
    }else{
		var direction = 'megLeft';
	}

    var oLi = '';
    switch (type) {
        //文本消息///////////////
        case 'text':
            var text = json.text; //文本消息内容
            oLi = ('<li class="' + direction + '">\
                <div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName">' + fromNick + '</div>\
					<div class="messageBox Text ' + isTeacher + '"><p>' + escapeHtml(text) + '</p></div>\
				</div>\
            </li>');
            break;

        //图片消息///////////////
        case 'image':
            var imgsrc = json.file.url;
             oLi = ('<li class="' + direction + '">\
				<div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName">' + fromNick + '</div>\
					<div class="messageBox Image ' + isTeacher + '"><p><img src="' + imgsrc + '"></p></div>\
				</div>\
			</li>');
            break;

        //语音消息///////////////
        case 'audio':
            var mp3Url = json.file.mp3Url;
            var dur = Math.ceil(json.file.dur / 1000)
            if (node) {
                arrMp3url.push(mp3Url);
            } else {
                arrMp3url.unshift(mp3Url);
            }
            var rem = dur / 4;

            oLi = ('<li class="' + direction + '">\
				<div class="headImg"><img src="' + fromAvatar + '"></div>\
				<div class="messageMain">\
					<div class="messageName ">' + fromNick + '</div>\
					<div class="messageBox Audio ' + isTeacher + '"><p style="width:' + rem + 'rem;"><span class="audioIco"></span><span class="aud">' + dur + '”</span></p></div>\
				</div>\
			</li>');
            if ($('#audio')[0].ended) {
                iNow = arrMp3url.length - 1;
                audio(iNow);
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
                oLi = ('<li class="' + direction + '">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
					<div class="messageName ">' + fromNick + '</div>\
						<div class="messageBox Custom ' + isTeacher + '"><p>' + escapeHtml(content.data.value) + '</p><span class="customBg"></span></div>\
					</div>\
				</li>');
            } else if (content.type == 6) {
                //老师回答
                oLi = ('<li class="' + direction + '">\
					<div class="headImg"><img src="' + fromAvatar + '"></div>\
					<div class="messageMain">\
					<div class="messageName ">' + fromNick + '</div>\
						<div class="messageBox Answer ' + isTeacher + '"><p><i>@' + content.data.nick + '</i>' + escapeHtml(content.data.value) + '</p><span class="answerBg"></span></div>\
					</div>\
				</li>');
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
						$('#CuPlayerVideo_video').removeAttr ('controls');
						testPause();
						msgLS = false;
					}else if(msgLS){
						$('#CuPlayerVideo_video').removeAttr ('controls');
						testPause();
						msgLS = false;
					}
				}
			} else if(content.type == 11){
				oLi = ('<li class="trter"><h5><span>' + fromNick + '：向老师打赏了'+content.data.value+'个学币</span></h5></li>')
			}
            break;

        //通知类消息///////////////
        case 'notification':
			var notype = json.attach.type;
			var timer = parseInt(json.time);
			var date = getLocalTime(timer).substring(5,17);
			if (notype == 'memberEnter' && from == TeacherFrom) {
				//当有人（老师）进入聊天室时
					oLi =('<li class="trter"><h5>"'+json.attach.toNick + '"老师进入聊天室!' + date + '</h5></li>');
			} else if (notype == 'memberExit' && from == TeacherFrom) {
				//当有人（老师）退出聊天室时
				oLi =('<li class="trter"><h5>"'+json.attach.toNick + '"老师退出聊天室!' + date + '</h5></li>');
			} else if (notype == 'gagMember') {
				///当有人被加入禁言名单
				$('#sayslist').append('<li><h5>"' + json.attach.toNick + '" 被禁言</h5></li>');
			} else if (notype == 'ungagMember') {
				//当有人被移除禁言名单时
				$('#sayslist').append('<li><h5>"' + json.attach.toNick + '" 被解除禁言</h5></li>');
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


//输入框
$(function(){
	$('#say').off('click',asmk);
	$('#say').on('click',asmk);
	function asmk(){
		var target = this;
		// 使用定时器是为了让输入框上滑时更加自然
		setTimeout(function(){
			target.scrollIntoView(true);
		},300);
	}
	//**************屏幕自适应
	function autoWH() {
		//加载时适应浏览器高度
		var width = $(window).outerWidth();
		var height = $(window).outerHeight();
		var topHeight = $('.live_box').position().top;
		$('.live_box,.tabBox,#bd_box').css('height', height - topHeight);
	}
	autoWH();
	$(window).resize(function() {
		//改变窗体大小时适应浏览器高度
		Torem();
		autoWH();
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
