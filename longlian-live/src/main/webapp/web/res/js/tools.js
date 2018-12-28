/**
 * aAjax 封装
 * @param config
 * @returns {*}
 * @constructor
 */
function ZAjaxJsonRes(config ){
	if (!config.param) config.param = {};
	var jsonObj = null;
	var type = (config.type == "POST" ? "POST" : "GET");
	var jsonObj ;
	$.ajax({
		type: type,
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		url: config.url,
		data:(type == "GET" || type == "get") ?  "":JSON.stringify(config.param),
		async: (config.async ? config.async  :false ),
		success: function (data) {
			jsonObj = data;
			if (config.callback) {
				config.callback(data);
			}
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			var response = XMLHttpRequest.response;
			jsonObj = eval("(" + response + ")");
		}
	});
	return jsonObj;
}

/**
 * aAjax 封装
 * @param config
 * @returns {*}
 * @constructor
 */
function ZAjaxRes(config ){
	if (!config.param) config.param = {};
	var jsonObj = null;
	var type = (config.type == "POST" ? "POST" : "GET");
	var jsonObj ;
	$.ajax({
		type: type,
	//	contentType: "application/x-www-form-urlencoded",
		dataType: "json",
		url: config.url,
		data: config.param,
		async: (config.async ? config.async  :false ),
		success: function (data) {
			jsonObj = data;
			if (config.callback) {
				config.callback(data , config.param);
			}
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			var response = XMLHttpRequest.response;
			jsonObj = eval("(" + response + ")");
		}
	});
	return jsonObj;
}

/**
 * 根据状态判断跳转页面
 * @param code
 * @param isLoginOrVip 是否登录 0-未登录 1-已登录 2-是VIP登录
 * @param isThirdUserLogin //是否第三方登录
 * @param invitationCode 邀请码
 * @param type  点击类型
 */
function to_href_no_login(code , isLoginOrVip , isThirdUserLogin , type ,invitationCode){
	var invitationCodePara = "";
	if(invitationCode) invitationCodePara = invitationCode
	if(isLoginOrVip == 0){//
		if(isThirdUserLogin){//第三方系统已登录
			window.location.href = "/weixin/toBindMobile?invitationCode=" + invitationCodePara;//需要绑定手机号
		}else{
			window.location.href = "/weixin/toLogin?invitationCode=" + invitationCodePara;//跳转登录
		}
	}else  if(isLoginOrVip == 1 || isLoginOrVip == 2) {//appuser已登录且是会员
		if (type && type == 'my_photo') {
			window.location.href = "/weixin/toEditUser";//编辑用户界面
		} else if (type == 'my_moneybag') {
			window.location.href = "/weixin/toMoneybay";//钱包
		} else if (type == 'recharge') {
			window.location.href = "/weixin/payRecord";//充值记录
		} else if (type == 'vip') {
			window.location.href = "/weixin/vip";//VIP充值
		} else if (type == 'my_team') {
			window.location.href = "/weixin/myteam";//我的团队
			//window.location.href = "/weixin/uploadApp";//下载APP
		} else if (type == 'message') {
			window.location.href = "/weixin/message";//消息
			//window.location.href = "/weixin/uploadApp";//下载APP
		} else if (type == 'setting') {
			window.location.href = "/weixin/setting";//设置
		} else if (type == 'share') {
			window.location.href = "/weixin/share";//分享
		} else if (type == 'update_invitation') {
			window.location.href = "/weixin/	changeInvitationCode.user";//更换邀请人
		} else if (type == 'agent') {
			window.location.href = "/weixin/agent";//代理
		}else {
			if (isLoginOrVip == 1) {
				if (type == 'update_invitation') {
					window.location.href = "/weixin/	changeInvitationCode.user";//更换邀请人
				} else {
					window.location.href = "/weixin/vip";//VIP充值
				}
			}
		}
	}
}


/**
 * 是否是微信客户端
 * @returns {boolean}
 */
function isWeiXin(){
	var ua = window.navigator.userAgent.toLowerCase();
	if(ua.match(/micromessenger/i) == 'micromessenger'){
		return true;
	}else{
		return false;
	}
}





$(function(){
	Date.prototype.Format = function(formatStr)
	{
		var str = formatStr;
		var Week = ['日','一','二','三','四','五','六'];

		str=str.replace(/yyyy|YYYY/,this.getFullYear());
		str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));

		str=str.replace(/MM/,this.getMonth()>9?this.getMonth().toString():'0' + this.getMonth());
		str=str.replace(/M/g,this.getMonth());

		str=str.replace(/w|W/g,Week[this.getDay()]);

		str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());
		str=str.replace(/d|D/g,this.getDate());

		str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());
		str=str.replace(/h|H/g,this.getHours());
		str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());
		str=str.replace(/m/g,this.getMinutes());

		str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());
		str=str.replace(/s|S/g,this.getSeconds());

		return str;
	}
});


//日期转换
/**
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q) 可以用 1-2 个占位符
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * eg:
 * (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 * (new Date()).pattern("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04
 * (new Date()).pattern("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04
 * (new Date()).pattern("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04
 * (new Date()).pattern("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */
Date.prototype.pattern=function(fmt) {
	var o = {
		"M+" : this.getMonth()+1, //月份
		"d+" : this.getDate(), //日
		"h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时
		"H+" : this.getHours(), //小时
		"m+" : this.getMinutes(), //分
		"s+" : this.getSeconds(), //秒
		"q+" : Math.floor((this.getMonth()+3)/3), //季度
		"S" : this.getMilliseconds() //毫秒
	};
	var week = {
		"0" : "/u65e5",
		"1" : "/u4e00",
		"2" : "/u4e8c",
		"3" : "/u4e09",
		"4" : "/u56db",
		"5" : "/u4e94",
		"6" : "/u516d"
	};
	if(/(y+)/.test(fmt)){
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}
	if(/(E+)/.test(fmt)){
		fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);
	}
	for(var k in o){
		if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		}
	}
	return fmt;
};

/**
 * 转换日期字符串
 * @param time 时间戳
 * @param format  日期转换格式
 */
function getFormatDateTimeStr(time , format){
	var timeStr = "";
	if(time){
		var timeDate =new Date(parseInt(time,10));
		timeStr = new Date(timeDate).pattern(format);
	}else{
		//timeStr = new Date(new Date()).pattern(format);
	}
	return timeStr;
}

/**
 * 转换日期字符串
 * @param time 时间戳
 * @param format  日期转换格式
 */
function getFormatDateTimeStrByDate(timeDate , format){
	var timeStr = "";
	if(timeDate){
		timeStr = timeDate.pattern(format);
	}else{
		//timeStr = new Date(new Date()).pattern(format);
	}
	return timeStr;
}
/**
 * 获取有效的金额 --四舍五入 保留两位小数点
 * @param bigDecimal
 * @return
 */
function parseUseTwoPointNum(number) {
	var numberFloat = parseFloat(number);
	return 	Math.round(numberFloat*100)/100;
}

/**
 * 将\n \t换成换行
 * @param str
 * @returns {void|string|XML}
 */
function replaceTeturn2Br(str) {
	if(str){
		str =  str.replace(/\n/g,"<br/>").replace(/\t/g,"<br/>");
	}
	return str;
}
/**
 * 返回上以页面方法
 */
function goBack() {
	history.back();
}

//获取url中的参数
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) return unescape(r[2]); return null;
}
//埋点
function statisticsBtn(param){
	if(!param.button){
		return;
	}
	var abc = ZAjaxRes({url: "/button/count", type: "GET", param: param});
}

/**
 * 处理 导航访问页面
 * @param webPageSign 页面类型  index-代表 首页；personalCenter 个人中心  ;  liveRoom 我的直播间 teacherSeries老师系列课
 * @param longlian_live_user_web_navigation_sign  用户导航标记 1-已经使用过 0-不存在 空-不用处理的，或者是未登录的
 */
function handlerNavigationVisitRecord(webPageSign ,sytem_new_version , longlian_live_user_web_navigation_sign){
	var isExtis = true;
	var get_sytem_new_version = localStorage.getItem(webPageSign);
	if(sytem_new_version != get_sytem_new_version ){
		if("1" == longlian_live_user_web_navigation_sign){
			isExtis =  false;
		}else{
			isExtis = true;
		}
		localStorage.setItem(webPageSign,sytem_new_version);
	}else{
		//不显示
		isExtis =  false;
	}
	return isExtis;
}
