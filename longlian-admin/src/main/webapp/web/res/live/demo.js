 
//注意这里, 引入的 SDK 文件不一样的话, 你可能需要使用 SDK.Chatroom.getInstance 来调用接口
var chatroom = SDK.Chatroom.getInstance({
	 appKey: 'fca0cbcc9d19fa53756ea8d085bdeaec',
	    account: 'test1',
	    token: 'b6beb5d1314d2fd40fe6dc32902fb405',
    chatroomId: '6529211',
    chatroomAddresses: [
        'dgphy9.netease.im:9093'
        //,
        //'dgphy3.netease.im:8081'
    ],
    onconnect: onChatroomConnect,
    onerror: onChatroomError,
    onwillreconnect: onChatroomWillReconnect,
    ondisconnect: onChatroomDisconnect,
    // 消息
    onmsgs: onChatroomMsgs
});
function onChatroomConnect(chatroom) {
    console.log('进入聊天室', chatroom);
}
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
            break;
        default:
            break;
        }
    }
}
function onChatroomError(error, obj) {
    console.log('发生错误', error, obj);
}
function onChatroomMsgs(msgs) {
    console.log('收到聊天室消息', msgs);
    
    for (var i = 0 ;i < msgs.length ;i++) {
    	var msg = msgs[i];
    	 if (msg.type == 'notification' && msg.attach.type == 'memberEnter') {
          	 document.getElementById("users").innerHTML +=  msg.from + ",";
          }
           
    	 if (msg.type == 'text' ) {
           	document.getElementById("says").innerHTML +=   msg.from + "：" + msg.text + "</br>";
         }
    }
}

function say() {
	var text1 = document.getElementById("say").value;
	document.getElementById("says").innerHTML +=  "test1" + "：" + text1 + "</br>";
	var msg = chatroom.sendText({
	    text: text1,
	    done: sendChatroomMsgDone
	});
	console.log('正在发送聊天室text消息, id=' + msg.idClient);
}

function sendChatroomMsgDone(error, msg) {
    console.log('发送聊天室' + msg.type + '消息' + (!error?'成功':'失败') + ', id=' + msg.idClient, error, msg);
}