/**
 * Created by pangchao on 2018/6/15.
 */
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
            //onMenuShareTimeline();//朋友圈
            //onMenuShareAppMessage();//微信
            //onMenuShareQQ();
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
            "menuItem:share:brand",
            "menuItem:share:appMessage"
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


