var share_title = "";
var share_desc = "";
var share_link = "";
var imgUrl = "";
var inviAppId = 0;
//分享的内容
var shareRoomId = 0;
var shareCourseId = 0;
$(function () {
    if (isWeiXin()) {
        var url = location.href.split('#')[0];
        var result = ZAjaxRes({url: "/weixin/getJSAPITicketSignature", type: "GET", param: {url: url}});
        if (result.code == "000000") {
            var data = result.data;
            wx.config({
                debug: false,
                appId: data.appId,
                timestamp: data.timestamp,
                nonceStr: data.nonceStr,
                signature: data.signature,
                jsApiList: [
                    'checkJsApi',
                    'onMenuShareTimeline',
                    'onMenuShareAppMessage',
                    'onMenuShareQQ',
                    'onMenuShareWeibo',
                    'chooseImage',
                    'previewImage',
                    'uploadImage',
                    'getLocalImgData',
                    'startRecord',
                    'stopRecord',
                    'onVoiceRecordEnd',
                    'uploadVoice',
                    'downloadVoice',
                    'playVoice',
                    'hideMenuItems'
                ]
            });
        }
    }
});
function symbolDecodeWtCommon(ff) {
    var ffData = ff;
    ffData = ffData.replace(/(\&lt;)/g, "\<");
    ffData = ffData.replace(/(\&gt;)/g, "\>");
    ffData = ffData.replace(/(\&quot;)/g, "\"");
    ffData = ffData.replace(/(\&\#39;)/g, "\'");
    ffData = ffData.replace(/(\&amp;)/g, "&");

    // ffData = ffData.replace(/(['"])/g, '\$1');

    return ffData;
};
function symbolDecode(ff) {
    var ffData = ff;
    ffData = ffData.replace(/(\&lt;)/g, "\<");
    ffData = ffData.replace(/(\&gt;)/g, "\>");
    ffData = ffData.replace(/(\&quot;)/g, "\"");
    ffData = ffData.replace(/(\&\#39;)/g, "\'");
    ffData = ffData.replace(/(\&amp;)/g, "&");
    return ffData;
};
var para = {};
/**
 * 微信分享H5
 */
function share_h5(param) {
    //if (!isWeiXin()) return;
    para = {
        invitationCode: "",
        systemType: "",
        roomIdOrCouseId: "",
        photoUrl: "",
        isSeries: "",
        seriesid: 0,
        channel: 0,
    };
    if (param) {
        if (param.invitationCode)para.invitationCode = param.invitationCode;
        if (param.systemType)para.systemType = param.systemType;
        if (param.liveRoomId)para.roomIdOrCouseId = param.liveRoomId;
        if (param.photoUrl)para.photoUrl = param.photoUrl;
        if (param.isSeries)para.isSeries = param.isSeries;
        if (param.seriesid)para.seriesid = param.seriesid;
        if (param.channel)para.channel = param.channel;
        if (param.systemType == 'LIVE_ROOM_INVI_CARD' || param.systemType == 'LIVE_ROOM') {
            shareRoomId = param.liveRoomId;
        } else if (param.systemType == 'COURSE_INVI_CARD' || param.systemType == 'COURSE') {
            shareCourseId = param.liveRoomId;
        }
    }
    var result = ZAjaxRes({url: "/share", type: "GET", param: para});
    if (result.code == "000000") {
        var data = result.data;
        share_title = data.title;
        share_desc = data.content;
        share_link = data.shareAddress;
        inviAppId = data.inviAppId;
        var url_index = location.href.split('com')[0] + "com";
        imgUrl = url_index + "/web/res/image/suanzao.jpg";
        if (data.photoUrl != '' && data.photoUrl != undefined) {
            imgUrl = data.photoUrl;
        }
        if (param) {
            if (param.systemType == "LIVE_ROOM_INVI_CARD" || param.systemType == 'COURSE_INVI_CARD') {
                imgUrl = param.photoUrl;
            }
        }
        if (param && (param.seriesid == '1333'
            || param.liveRoomId == 1334
            || param.liveRoomId == 1335
            || param.liveRoomId == 1336
            || param.liveRoomId == 1337)) {
            share_title = '【健人直播】2017全国健身健美公开赛高清版';
            share_desc = '满屏都是美臀啊，比基尼美女和肌肉男现场走秀，我的身体怕是扛不住了。。。';
            share_link = 'http://suanzao.llkeji.com/weixin/course/fitnessLive?1=1';
            imgUrl = "https://longlian-live.oss-cn-hangzhou.aliyuncs.com/upload/2017/06/38fa775c195a408fabf64e844353fc71.png";
        }
        console.log("share_link=" + share_link);
        console.log("imgUrl=" + imgUrl);
        console.log("share_title=" + share_title);
        console.log("share_desc=" + share_desc);
        //alert("2share_desc="+share_desc +":" + share_title +":"+imgUrl );
        //重新设置分享
        wx.ready(function () {
            onMenuShareTimeline();//朋友圈
            onMenuShareAppMessage();//微信
            onMenuShareQQ();
            onMenuShareWeibo();
            if (window.wxReadCallback) {
                window.wxReadCallback();
            }
        });

        wx.error(function (res) {
            if ("config:invalid signature" == res.errMsg) return;
            alert(res.errMsg);
        });

    }
}

function insertShareRecord(wechatShareType) {
    //alert(wechatShareType +":"+ para.systemType +":"+imgUrl +":"+ inviAppId  +":"+ imgUrl +":"+ shareRoomId +":"+ shareCourseId)
    var shareRecord = {
        courseId: shareCourseId,
        roomId: shareRoomId,
        status: '0',
        inviAppId: inviAppId,
        wechatShareType: wechatShareType,
        imgUrl: imgUrl,
        systemType: para.systemType
    }
    var result = ZAjaxJsonRes({url: "/shareRecord/insertRecord.user", type: "POST", param: shareRecord});
}


//初始化右上角隐藏
function initWeixinHideMenu() {
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', hideMenu, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', hideMenu);
            document.attachEvent('onWeixinJSBridgeReady', hideMenu);
        }
    } else {
        hideMenu();
    }
}
function hideMenu() {
    WeixinJSBridge.call('hideOptionMenu');
}
//初始化右上角隐藏
function initWeixinShowMenu() {
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', showMenu, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', showMenu);
            document.attachEvent('onWeixinJSBridgeReady', showMenu);
        }
    } else {
        showMenu();
    }
}

/**
 * 选择并上传附件
 */
var weiXinServiceId = [];   //微信中上传图片的serviceId
var localUrl = [];  //微信上传图片用于回显的localId
function wechatUploadPic() {
	weiXinServiceId = [];
	localUrl = [];
    var images = {
        localId: [],
        serverId: []
    };
    wx.chooseImage({
        success: function (res) {
            images.localId = res.localIds;
            localUrl = images.localId;  //android 和 ios 可以直接使用
            if (images.localId.length == 0) {
                alert('请先使用 chooseImage 接口选择图片');
                return;
            }
            var i = 0, length = images.localId.length;
            images.serverId = [];
            function uploadPic() {
                wx.uploadImage({
                    localId: images.localId[i],
                    isShowProgressTips: 1,
                    success: function (res) {
                        weiXinServiceId.push(res.serverId);
                        i++;
                        if (i < length) {
							//$('#Loading').show();
                            uploadPic();
                        }
                        if(parseInt(i) == parseInt(length)){
							//$('#Loading').hide();
                            wxUploadPic();
                           // var result = ZAjaxJsonRes({url: "/course/test", param:weiXinServiceId ,  type: "POST"});
                        }
                    },
                    fail: function (res) {
                        alert(JSON.stringify(res) + "ddd");
                    }
                });
            }
            uploadPic();
        }
    });
}
function showMenu() {
    WeixinJSBridge.call('showOptionMenu');
}

var share_result = {code: "100000"};
wx.ready(function(){
    wx.hideMenuItems({
        menuList: [
            "menuItem:share:qq",
            "menuItem:share:weiboApp",
            "menuItem:favorite",
            "menuItem:share:facebook",
            "menuItem:share:QZone",
            "menuItem:editTag",
            "menuItem:delete",
            //"menuItem:copyUrl",
            "menuItem:originPage",
            "menuItem:readMode",
            "menuItem:openWithQQBrowser",
            "menuItem:openWithSafari",
            "menuItem:share:email",
            "menuItem:share:brand"
        ] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
    });

// 2. 分享接口
// 2.1 监听“分享给朋友”，按钮点击、自定义分享内容及分享结果接口
    wx.onMenuShareAppMessage({
        title: share_title,
        desc: share_desc,
        link: share_link + "&wechatShareType=friend",
        imgUrl: imgUrl,
        withShareTicket: true,
        trigger: function (res) {
            //alert('用户点击发送给朋友');
            if (window.shareTriggerDone) {
                window.shareTriggerDone(res);
            }
        },
        success: function (res) {
            //alert("ddddd")
            //  share_result["message"] = "已分享";
            //sys_pop(share_result);
            insertShareRecord('friend');
            if (window.shareAppMessageDone) {
                window.shareAppMessageDone(res);
            }
        },
        cancel: function (res) {
            //share_result["message"] = "已取消分享";
            // sys_pop(share_result);
        },
        fail: function (res) {
            alert(JSON.stringify(res));
        }
    });

// 2.2 监听“分享到朋友圈”按钮点击、自定义分享内容及分享结果接口
    wx.onMenuShareTimeline({
        title: share_title,
        link: share_link + "&wechatShareType=circle_of_friend",
        imgUrl: imgUrl,
        trigger: function (res) {
            //alert('用户点击分享到朋友圈');
        },
        success: function (res) {
            shareMoments();
            insertShareRecord(circle_of_friend);
        },
        cancel: function (res) {
            // share_result["message"] = "已取消分享";
            //sys_pop(share_result);
        },
        fail: function (res) {
            alert(JSON.stringify(res));
        }
    });
})
// 2.3 监听“分享到QQ”按钮点击、自定义分享内容及分享结果接口
function onMenuShareQQ() {
    wx.onMenuShareQQ({
        title: share_title,
        desc: share_desc,
        link: share_link + "&wechatShareType=qq",
        imgUrl: imgUrl,
        trigger: function (res) {
            // alert('用户点击分享到QQ');
        },
        complete: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            //share_result["message"] = "已分享";
            //sys_pop(share_result);
            insertShareRecord('qq');

        },
        cancel: function (res) {
            share_result["message"] = "已取消分享";
            sys_pop(share_result);
        },
        fail: function (res) {
            alert(JSON.stringify(res));
        }
    });
    //alert('已注册获取“分享到 QQ”状态事件');
}

// 2.4 监听“分享到微博”按钮点击、自定义分享内容及分享结果接口
function onMenuShareWeibo() {
    wx.onMenuShareWeibo({
        title: share_title,
        desc: share_desc,
        link: share_link + "&wechatShareType=weibo",
        imgUrl: imgUrl,
        trigger: function (res) {
            //alert('用户点击分享到微博');
        },
        complete: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            share_result["message"] = "已分享";
            sys_pop(result);
            insertShareRecord('weibo');
        },
        cancel: function (res) {
            share_result["message"] = "已取消分享";
            sys_pop(result);
        },
        fail: function (res) {
            alert(JSON.stringify(res));
        }
    });
    //alert('已注册获取“分享到微博”状态事件');
}

// 5.2 图片预览
function previewImage() {
    wx.previewImage({
        current: share_pic_address,
        urls: [
            share_pic_address
        ]
    });
};


/**
 * 分享朋友圈有奖
 */
function shareMoments() {
    var result = ZAjaxJsonRes({url: "/weixin/shareMoment.user", type: "GET"});
    if (result.code == "000000") {

    } else {

    }
}
function checkJsApi() {
    wx.checkJsApi({
        jsApiList: [
            'getNetworkType',
            'previewImage'
        ],
        success: function (res) {
            alert(JSON.stringify(res));
        }
    });
}

/**
 * 微信执行config后执行此方法
 */
/*wx.ready(function () {

 // onMenuShareAppMessage();//分享朋友
 //onMenuShareTimeline();//分享朋友圈
 //onMenuShareQQ();//分享QQ
 //onMenuShareWeibo();//分享微博


 // 1 判断当前版本是否支持指定 JS 接口，支持批量判断
 function checkJsApi() {
 wx.checkJsApi({
 jsApiList: [
 'getNetworkType',
 'previewImage'
 ],
 success: function (res) {
 alert(JSON.stringify(res));
 }
 });
 }


 // 3 智能接口
 var voice = {
 localId: '',
 serverId: ''
 };
 // 3.1 识别音频并返回识别结果
 document.querySelector('#translateVoice').onclick = function () {
 if (voice.localId == '') {
 alert('请先使用 startRecord 接口录制一段声音');
 return;
 }
 wx.translateVoice({
 localId: voice.localId,
 complete: function (res) {
 if (res.hasOwnProperty('translateResult')) {
 alert('识别结果：' + res.translateResult);
 } else {
 alert('无法识别');
 }
 }
 });
 };

 // 4 音频接口
 // 4.2 开始录音
 document.querySelector('#startRecord').onclick = function () {
 wx.startRecord({
 cancel: function () {
 alert('用户拒绝授权录音');
 }
 });
 };

 // 4.3 停止录音
 document.querySelector('#stopRecord').onclick = function () {
 wx.stopRecord({
 success: function (res) {
 voice.localId = res.localId;
 },
 fail: function (res) {
 alert(JSON.stringify(res));
 }
 });
 };

 // 4.4 监听录音自动停止
 wx.onVoiceRecordEnd({
 complete: function (res) {
 voice.localId = res.localId;
 alert('录音时间已超过一分钟');
 }
 });

 // 4.5 播放音频
 document.querySelector('#playVoice').onclick = function () {
 if (voice.localId == '') {
 alert('请先使用 startRecord 接口录制一段声音');
 return;
 }
 wx.playVoice({
 localId: voice.localId
 });
 };

 // 4.6 暂停播放音频
 document.querySelector('#pauseVoice').onclick = function () {
 wx.pauseVoice({
 localId: voice.localId
 });
 };

 // 4.7 停止播放音频
 document.querySelector('#stopVoice').onclick = function () {
 wx.stopVoice({
 localId: voice.localId
 });
 };

 // 4.8 监听录音播放停止
 wx.onVoicePlayEnd({
 complete: function (res) {
 alert('录音（' + res.localId + '）播放结束');
 }
 });

 // 4.8 上传语音
 document.querySelector('#uploadVoice').onclick = function () {
 if (voice.localId == '') {
 alert('请先使用 startRecord 接口录制一段声音');
 return;
 }
 wx.uploadVoice({
 localId: voice.localId,
 success: function (res) {
 alert('上传语音成功，serverId 为' + res.serverId);
 voice.serverId = res.serverId;
 }
 });
 };

 // 4.9 下载语音
 document.querySelector('#downloadVoice').onclick = function () {
 if (voice.serverId == '') {
 alert('请先使用 uploadVoice 上传声音');
 return;
 }
 wx.downloadVoice({
 serverId: voice.serverId,
 success: function (res) {
 alert('下载语音成功，localId 为' + res.localId);
 voice.localId = res.localId;
 }
 });
 };

 // 5 图片接口
 // 5.1 拍照、本地选图
 var images = {
 localId: [],
 serverId: []
 };
 document.querySelector('#chooseImage').onclick = function () {
 wx.chooseImage({
 success: function (res) {
 images.localId = res.localIds;
 alert('已选择 ' + res.localIds.length + ' 张图片');
 }
 });
 };

 // 5.2 图片预览
 document.querySelector('#previewImage').onclick = function () {
 wx.previewImage({
 current: address,
 urls: [
 address
 ]
 });
 };

 // 5.3 上传图片
 document.querySelector('#uploadImage').onclick = function () {
 if (images.localId.length == 0) {
 alert('请先使用 chooseImage 接口选择图片');
 return;
 }
 var i = 0, length = images.localId.length;
 images.serverId = [];
 function upload() {
 wx.uploadImage({
 localId: images.localId[i],
 success: function (res) {
 i++;
 alert('已上传：' + i + '/' + length);
 images.serverId.push(res.serverId);
 if (i < length) {
 upload();
 }
 },
 fail: function (res) {
 alert(JSON.stringify(res));
 }
 });
 }
 upload();
 };

 // 5.4 下载图片
 document.querySelector('#downloadImage').onclick = function () {
 if (images.serverId.length === 0) {
 alert('请先使用 uploadImage 上传图片');
 return;
 }
 var i = 0, length = images.serverId.length;
 images.localId = [];
 function download() {
 wx.downloadImage({
 serverId: images.serverId[i],
 success: function (res) {
 i++;
 alert('已下载：' + i + '/' + length);
 images.localId.push(res.localId);
 if (i < length) {
 download();
 }
 }
 });
 }
 download();
 };

 // 6 设备信息接口
 // 6.1 获取当前网络状态
 document.querySelector('#getNetworkType').onclick = function () {
 wx.getNetworkType({
 success: function (res) {
 alert(res.networkType);
 },
 fail: function (res) {
 alert(JSON.stringify(res));
 }
 });
 };

 // 8 界面操作接口
 // 8.1 隐藏右上角菜单
 document.querySelector('#hideOptionMenu').onclick = function () {
 wx.hideOptionMenu();
 };

 // 8.2 显示右上角菜单
 document.querySelector('#showOptionMenu').onclick = function () {
 wx.showOptionMenu();
 };

 // 8.3 批量隐藏菜单项
 document.querySelector('#hideMenuItems').onclick = function () {
 wx.hideMenuItems({
 menuList: [
 'menuItem:readMode', // 阅读模式
 'menuItem:share:timeline', // 分享到朋友圈
 'menuItem:copyUrl' // 复制链接
 ],
 success: function (res) {
 alert('已隐藏“阅读模式”，“分享到朋友圈”，“复制链接”等按钮');
 },
 fail: function (res) {
 alert(JSON.stringify(res));
 }
 });
 };

 // 8.4 批量显示菜单项
 document.querySelector('#showMenuItems').onclick = function () {
 wx.showMenuItems({
 menuList: [
 'menuItem:readMode', // 阅读模式
 'menuItem:share:timeline', // 分享到朋友圈
 'menuItem:copyUrl' // 复制链接
 ],
 success: function (res) {
 alert('已显示“阅读模式”，“分享到朋友圈”，“复制链接”等按钮');
 },
 fail: function (res) {
 alert(JSON.stringify(res));
 }
 });
 };

 // 8.5 隐藏所有非基本菜单项
 document.querySelector('#hideAllNonBaseMenuItem').onclick = function () {
 wx.hideAllNonBaseMenuItem({
 success: function () {
 alert('已隐藏所有非基本菜单项');
 }
 });
 };

 // 8.6 显示所有被隐藏的非基本菜单项
 document.querySelector('#showAllNonBaseMenuItem').onclick = function () {
 wx.showAllNonBaseMenuItem({
 success: function () {
 alert('已显示所有非基本菜单项');
 }
 });
 };

 // 8.7 关闭当前窗口
 document.querySelector('#closeWindow').onclick = function () {
 wx.closeWindow();
 };

 // 9 微信原生接口
 // 9.1.1 扫描二维码并返回结果
 document.querySelector('#scanQRCode0').onclick = function () {
 wx.scanQRCode({
 desc: 'scanQRCode desc'
 });
 };
 // 9.1.2 扫描二维码并返回结果
 document.querySelector('#scanQRCode1').onclick = function () {
 wx.scanQRCode({
 needResult: 1,
 desc: 'scanQRCode desc',
 success: function (res) {
 alert(JSON.stringify(res));
 }
 });
 };

 // 10 微信支付接口
 // 10.1 发起一个支付请求
 document.querySelector('#chooseWXPay').onclick = function () {
 wx.chooseWXPay({
 timestamp: 1414723227,
 nonceStr: 'noncestr',
 package: 'addition=action_id%3dgaby1234%26limit_pay%3d&bank_type=WX&body=innertest&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F120.204.206.246%2Fcgi-bin%2Fmmsupport-bin%2Fnotifypay&out_trade_no=1414723227818375338&partner=1900000109&spbill_create_ip=127.0.0.1&total_fee=1&sign=432B647FE95C7BF73BCD177CEECBEF8D',
 paySign: 'bd5b1933cda6e9548862944836a9b52e8c9a2b69'
 });
 };

 // 11.3  跳转微信商品页
 document.querySelector('#openProductSpecificView').onclick = function () {
 wx.openProductSpecificView({
 productId: 'pDF3iY0ptap-mIIPYnsM5n8VtCR0'
 });
 };

 // 12 微信卡券接口
 // 12.1 添加卡券
 document.querySelector('#addCard').onclick = function () {
 wx.addCard({
 cardList: [
 {
 cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
 cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"64e6a7cc85c6e84b726f2d1cbef1b36e9b0f9750"}'
 },
 {
 cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
 cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"64e6a7cc85c6e84b726f2d1cbef1b36e9b0f9750"}'
 }
 ],
 success: function (res) {
 alert('已添加卡券：' + JSON.stringify(res.cardList));
 }
 });
 };

 // 12.2 选择卡券
 document.querySelector('#chooseCard').onclick = function () {
 wx.chooseCard({
 cardSign: '97e9c5e58aab3bdf6fd6150e599d7e5806e5cb91',
 timestamp: 1417504553,
 nonceStr: 'k0hGdSXKZEj3Min5',
 success: function (res) {
 alert('已选择卡券：' + JSON.stringify(res.cardList));
 }
 });
 };

 // 12.3 查看卡券
 document.querySelector('#openCard').onclick = function () {
 alert('您没有该公众号的卡券无法打开卡券。');
 wx.openCard({
 cardList: [
 ]
 });
 };

 var shareData = {
 title: '方倍工作室 微信JS-SDK DEMO',
 desc: '微信JS-SDK,帮助第三方为用户提供更优质的移动web服务',
 link: 'http://www.cnblogs.com/txw1958/',
 imgUrl: 'http://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRt8Qia4lv7k3M9J1SKqKCImxJCt7j9rHYicKDI45jRPBxdzdyREWnk0ia0N5TMnMfth7SdxtzMvVgXg/0'
 };
 //wx.onMenuShareAppMessage(shareData);
 //wx.onMenuShareTimeline(shareData);
 });*/




