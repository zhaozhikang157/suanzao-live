var conn = new WebIM.connection({
    https: WebIM.config.https,
    url: WebIM.config.xmppURL,
    isAutoLogin:true,
    isMultiLoginSessions: WebIM.config.isMultiLoginSessions
});

conn.listen({
    onOpened: function ( message ) {          //连接成功回调
        // 如果isAutoLogin设置为false，那么必须手动设置上线，否则无法收消息
        // 手动上线指的是调用conn.setPresence(); 如果conn初始化时已将isAutoLogin设置为true
        // 则无需调用conn.setPresence();   

		document.getElementById("loginDiv").style.display = 'none';
		document.getElementById("contentDiv").style.display = '';
		// 加入聊天室
	    conn.joinChatRoom({
	        roomId: myRoomId
	    });
	    
	    conn.queryRoomMember({
	        roomId: myRoomId,
	        success:function(members) {
	        	if (members && members.length > 0) {
	                var values = [];
	                var values_old = [];
	                for (var i = 0, l = members.length; i < l; i++) {
	                    var jid = members[i].jid,
	                        username = jid.substring(jid.indexOf('_') + 1).split('@')[0];
	                    values.push({ "id": username, "text": username });
	                    document.getElementById("users").innerHTML +=  username + ",";
	                    values_old.push(username);
	                }

	               
	            }
	        }
	    });
    },  
    onClosed: function ( message ) {},         //连接关闭回调
    onTextMessage: function ( message ) {
		
		document.getElementById("says").innerHTML +=  message.from + "：" + message.data + "</br>";
		
	},    //收到文本消息
    onEmojiMessage: function ( message ) {},   //收到表情消息
    onPictureMessage: function ( message ) {}, //收到图片消息
    onCmdMessage: function ( message ) {},     //收到命令消息
    onAudioMessage: function ( message ) {},   //收到音频消息
    onLocationMessage: function ( message ) {},//收到位置消息
    onFileMessage: function ( message ) {},    //收到文件消息
    onVideoMessage: function (message) {
        var node = document.getElementById('privateVideo');
        var option = {
            url: message.url,
            headers: {
              'Accept': 'audio/mp4'
            },
            onFileDownloadComplete: function (response) {
                var objectURL = WebIM.utils.parseDownloadResponse.call(conn, response);
                node.src = objectURL;
            },
            onFileDownloadError: function () {
                console.log('File down load error.')
            }
        };
        WebIM.utils.download.call(conn, option);
    },   //收到视频消息
    onPresence: function ( message ) {
    	if (message.type == 'joinChatRoomSuccess') {
    		document.getElementById("users").innerHTML +=  message.to + ",1";
    	}
    	
    },       //收到联系人订阅请求、处理群组、聊天室被踢解散等消息
    onRoster: function ( message ) {},         //处理好友申请
    onInviteMessage: function ( message ) {},  //处理群组邀请
    onOnline: function () {},                  //本机网络连接成功
    onOffline: function () {},                 //本机网络掉线
    onError: function ( message ) {},          //失败回调
    onBlacklistUpdate: function (list) {       //黑名单变动
        // 查询黑名单，将好友拉黑，将好友从黑名单移除都会回调这个函数，list则是黑名单现有的所有好友信息
        console.log(list);
    }
});

var userName = "";

function login() {
	  userName = document.getElementById("name").value;
	var password = document.getElementById("password").value;
	
	var options = { 
	  apiUrl: WebIM.config.apiURL,
	  user: userName,
	  pwd: password,
	  appKey: WebIM.config.appkey
	};
	conn.open(options);
	
}

function say() {
	var text = document.getElementById("say").value;
	document.getElementById("says").innerHTML +=  userName + "：" + text + "</br>";
	sendRoomText(text);
}

var sendRoomText = function (text) {
    var id = conn.getUniqueId();         // 生成本地消息id
    var msg = new WebIM.message('txt', id); // 创建文本消息
    var option = {
        msg: text,          // 消息内容
        to: myRoomId,               // 接收消息对象(聊天室id)
        roomType: true,
        chatType: 'chatRoom',
        success: function () {
            console.log('send room text success');
        },
        fail: function () {
            console.log('failed');
        }
    };
    msg.set(option);
    msg.setGroup('groupchat');
    conn.send(msg.body);
};
