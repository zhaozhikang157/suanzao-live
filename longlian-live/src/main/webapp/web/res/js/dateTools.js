/**
 * tools常用工具类
 */
var tools = {
		//判断是否为正整数
		isNum:function(num){
			 if((/^(\+|-)?\d+$/.test(num))&&num>=0){  
			        return true;  
			    }
			 return false;  
		},
		
	/***
	 * 请求后台交互，采用json对象 获取JSON对象
	 * url : 请求地址
	 * param: 传值参数
	 * async: 发送方式，同步（false）或异步（true）
	 * callback：响应处理函数，执行成功调用返回方法
	 */
	requestJsonRs: function (url, param,methodType, async, callback) {
		if (!param) param = {};
		var jsonObj = null;
		$.ajax({
			type: "POST",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			url: url,
			data: JSON.stringify(param),
			async: (async ? async : false),
			success: function (data) {
				jsonObj = data;
				if (callback) {
					callback(data);
				}
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				jsonObj = {code: 0,success:false ,data:"" , msg: "请求出错！"};
			}
		});
		return jsonObj;
	},

	/***
	 * 请求后台交互 , 普通字符串 获取JSON对象
	 * url : 请求地址
	 * param: 传值参数
	 * async: 发送方式，同步（false）或异步（true）
	 * callback：响应处理函数，执行成功调用返回方法
	 */
	requestRs: function (url, param,methodType, async, callback) {
		var methodTemp  = "get";
		if(methodType && methodType != null && methodType != ''){
			methodTemp = methodType;
		}
		if (!param) param = {};
		var jsonObj = null;
		$.ajax({
			type: methodTemp,
			url: url,
			data: param,
			dataType: "json",
			async: (async ? async : false),
			success: function (data) {
				jsonObj = data;
				if (callback) {
					callback(data);
				}

			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				jsonObj = {code: 0,success:false ,data:"" , msg: "请求出错！"};
			}
		});
		return jsonObj;
	},
	/**
	 * 把Json数据绑定到控件

	 json : json对象
	 filters : 过滤不需要绑定的控件
	 */
	bindJsonObj2Cntrl: function(json, filters) {
		for (var property in json) {
			if (filters) {
				if (Object.isString(filters) && filters.indexOf(",") > 0) {
					var filterArray = filters.split(",");
					if (!filterArray.contains(property)) {
						continue;
					}
				} else if (Object.isString(filters)) {
					var ancestor = $(filters);
					var elem = $(property);
					if (ancestor && elem && !Element.descendantOf(elem, ancestor)) {
						continue;
					}
				} else if (Object.isArray(filters)) {
					if (!filters.contains(property)) {
						continue;
					}
				} else if (Object.isElement(filters)) {
					var elem = $(property);
					if (elem && !Element.descendantOf(elem, ancestor)) {
						continue;
					}
				}
			}
			var value = json[property];
			var cntrlArray = document.getElementsByName(property);
			var cntrlCnt = cntrlArray.length;
			if (!cntrlArray || cntrlCnt < 1) {
				if (document.getElementById(property)) {
					cntrlArray = [document.getElementById(property)];
					cntrlCnt = 1;
				} else {
					continue;
				}
			}
			if (cntrlCnt == 1) {
				var cntrl = cntrlArray[0];
				if (cntrl.tagName.toLowerCase() == "input" && cntrl.type.toLowerCase() == "checkbox") {

					if (cntrl.value == value) {
						cntrl.checked = true;
					} else {
						cntrl.checked = false;
					}
				} else if (cntrl.tagName.toLowerCase() == "td"
					|| cntrl.tagName.toLowerCase() == "div"
					|| cntrl.tagName.toLowerCase() == "span") {
					cntrl.innerHTML = value;
				} else if (cntrl.tagName.toLowerCase() == 'select') {
					for (var i = 0; i < cntrl.childNodes.length; i++) {
						if (cntrl.childNodes[i].value == value) {
							cntrl.childNodes[i].selected = "selected";
							break;
						}
					}
				} else {
					cntrl.value = value == null ? "" : value;
				}
			} else {
				for (var i = 0; i < cntrlCnt; i++) {
					var cntrl = cntrlArray[i];
					if (cntrl.value == value) {
						cntrl.checked = true;
					} else {
						cntrl.checked = false;
					}
				}
			}
		}
	},
	/**
	 * str转为json
	 */
	strToJson: function (str) {
		var json = eval('(' + str + ')');
		return json;
	},
	/**
	 * 表单内控件转换为POST请求的JSON格式
	 * form : 表单对象
	 */
	formToJson: function (form, all) {
		var json = {};
		if (!all) {

			$(form).find("input[name][type=text][disabled!=disabled]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

			$(form).find("input[name][type=hidden][disabled!=disabled]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

			$(form).find("textarea[name][disabled!=disabled]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});
			$(form).find("select[name][disabled!=disabled]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});
			$(form).find("input[name][type=checkbox][disabled!=disabled]").each(function (i, obj) {
				if ($(obj).attr("checked")) {
					json[$(obj).attr("name")] = 1;
				} else {
					json[$(obj).attr("name")] = 0;
				}
			});

			$(form).find("input[name][type=radio][disabled!=disabled]:checked").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

			$(form).find("input[name][type=password][disabled!=disabled]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

		} else {
			$(form).find("input[name][type=text]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

			$(form).find("input[name][type=hidden]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});
			$(form).find("textarea[name]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});
			$(form).find("select[name]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});
			$(form).find("input[name][type=checkbox]").each(function (i, obj) {
				if ($(obj).attr("checked")) {
					json[$(obj).attr("name")] = 1;
				} else {
					json[$(obj).attr("name")] = 0;
				}
			});
			$(form).find("input[name][type=radio]:checked").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

			$(form).find("input[name][type=password]").each(function (i, obj) {
				json[$(obj).attr("name")] = $(obj).val();
			});

		}
		return json;
	},
	/**
	 * array中是否存在target元素
	 */
	findInSet: function (target, array1) {
		var sp = (array1 + "").split(",");
		for (var i = 0; i < sp.length; i++) {
			if ((sp[i] + "") == (target + "")) {
				return true;
			}
		}
		return false;
	},
	/**
	 * 检查是否存在中文
	 */
	existChinese: function (str) {
		for (var i = 0; i < str.length; i++) {
			if (str.charAt(i) >= '\u4e00' && str.charAt(i) <= '\u9fa5') {
				return true;
			}
		}
		return false;
	},

	/**
	 * 将字符串转化为Json
	 */
	string2JsonObj: function (text) {
		try {
			return eval("(" + text + ")");
		} catch (e) {
			return {};
		}
	},
	encode64: function (input) {
		input = strUnicode2Ansi(input);
		var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3 = "";
		var enc1, enc2, enc3, enc4 = "";
		var i = 0;

		do {
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output +
				keyStr.charAt(enc1) +
				keyStr.charAt(enc2) +
				keyStr.charAt(enc3) +
				keyStr.charAt(enc4);
			chr1 = chr2 = chr3 = "";
			enc1 = enc2 = enc3 = enc4 = "";
		} while (i < input.length);

		return output;
	},
	decode64: function (input) {
		var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3 = "";
		var enc1, enc2, enc3, enc4 = "";
		var i = 0;

		if (input.length % 4 != 0) {
			return "";
		}
		var base64test = /[^A-Za-z0-9\+\/\=]/g;
		if (base64test.exec(input)) {
			return "";
		}

		do {
			enc1 = keyStr.indexOf(input.charAt(i++));
			enc2 = keyStr.indexOf(input.charAt(i++));
			enc3 = keyStr.indexOf(input.charAt(i++));
			enc4 = keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output += String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output += String.fromCharCode(chr3);
			}

			chr1 = chr2 = chr3 = "";
			enc1 = enc2 = enc3 = enc4 = "";

		} while (i < input.length);

		return strAnsi2Unicode(output);
	},
	getEvent: function () { //同时兼容ie和ff的写法
		if (document.all)  return window.event;
		func = getEvent.caller;
		while (func != null) {
			var arg0 = func.arguments[0];
			if (arg0) {
				if ((arg0.constructor == Event || arg0.constructor == MouseEvent) ||

					(typeof(arg0) == "object" && arg0.preventDefault && arg0.stopPropagation)) {
					return arg0;
				}
			}
			func = func.caller;
		}
		return null;
	},
	getEventSrc: function () {
		var event = this.getEvent();
		return event.srcElement ? event.srcElement : event.target;
	},
	reloadParent: function () {
		try {
			xparent.datagrid.datagrid("reload");
		} catch (e) {
			try {
				xparent.location.reload();
			} catch (e) {

			}
		}
	},
	/**
	 * 加载用，将所有按钮置为不可用状态
	 */
	doLoading: function () {
		$(".btn").each(function (i, obj) {
			$(obj).attr("disabled", "disabled");
		});
	},
	/**
	 * 加载完成用，将所有按钮置为激活状态
	 */
	doFinished: function () {
		$(".btn").each(function (i, obj) {
			$(obj).removeAttr("disabled");
		});
	},

	/**
	 * 获取文件类型图标
	 * @param ext
	 * @return
	 */
	getFileTypeImg:function(ext){
		return "<img align=\"absMiddle\" src=\""+systemImagePath+"/filetype/"+ext.toLowerCase()+".gif"+"\" onerror=\"this.src='"+systemImagePath+"/filetype/defaut.gif'\"/>";
	},
	/**
	 * 获取文件项元素
	 * @return
	 */
	getAttachElement:function(attachModel,opts){
		var opts = opts;
		if(!opts){
			opts = {};
		}
		var downloadFunc = this.download;
		var ext = attachModel.ext;
		var render = "<div>";
	//	render += this.getFileTypeImg(ext);
		render += "&nbsp;"+attachModel.fileName+"";
		render += "</div>";

		var attachElement = $(render);
		var priv = attachModel.priv;//权限值

		var menuData = [];
		var img = $("<img src='"+systemImagePath+"/remove.png' style='margin-left:10px;cursor: pointer;vertical-align:middle;width:25px;height:25px;' />");
		attachElement.append(img);
		img.click(function deleteFunction(){
			opts.deleteEvent(attachModel,opts.params, attachElement);
		});
		return attachElement;
	},
	isImage:function(ext){
		ext = ext.toLowerCase();
		if(ext=="gif" || ext=="jpg" || ext=="png" || ext=="bmp"){
			return true;
		}
		return false;
	},
	isOffice:function(ext){
		ext = ext.toLowerCase();
		if(ext=="doc" || ext=="xls" || ext=="ppt" || ext=="docx"){
			return true;
		}
		return false;
	},
	download:function(url){
		if(!this._downloadFrame){
			this._downloadFrame = document.createElement("iframe");
			this._downloadFrame.style.display = "none";
			document.body.appendChild(this._downloadFrame);
		}
		this._downloadFrame.src = url;
	},
	 getHeight :function() {
		 return $(document).height();
	 }
	 
};



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

