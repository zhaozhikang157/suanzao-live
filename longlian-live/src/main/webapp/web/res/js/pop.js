/*
   默认参数值
   status:可取值 warning，normal，error
   {
     status:"normal",
 width:'22.4rem',
 height:'12rem',
 content:"",
 title:'',
 time:5000
	 }
*/
function pop(option){
	var thisTop = option.top || '40%';
	var top = 'top:'+ thisTop;
	if(option.bottom){
		top = 'bottom:'+option.bottom;
	}
	var defaults={
		status:"normal",
		width:'10.9rem',
		height:'',
		title:'',
		content:"",
		time:'1500'
	}
	var opts=$.extend(defaults,option);
	var pophtml='<div class="pop_wrap '+opts.status+'" style="width:'+opts.width+';height:'+opts.height+';'+top+' ;margin-left:'+(-parseInt(opts.width)/2)+"rem"+';" ><h3 class="pop_title">'+opts.title+'</h3><div class="pop_con"><p class="poptext">'+opts.content+'</p></div></div>';
	$("body").append(pophtml);
	setTimeout(function(){
		$(".pop_wrap").remove();
	},opts.time)
}

function pop1(option){
	var defaults={
		status:"normal",
		width:'5.75rem',
		height:'',
		title:'',
		content:"",
		time:'3000'
	}
	var opts=$.extend(defaults,option);
	var pophtml='<div class="pop_wrap1 '+opts.status+'" style="width:'+opts.width+';height:'+opts.height+';" ><h3 class="pop_title1">'+opts.title+'</h3><div class="pop_con1"><div class="classpic"></div><p class="poptext1">'+opts.content+'</p></div></div>';
	$("body").append(pophtml);
	setTimeout(function(){
		$(".pop_wrap1").remove();
	},opts.time)
}

function pop2(option){
	var defaults={
		status:"loadboxpoc",
		width:'5.75rem',
		height:'',
		content:"加载中"
	}
	var opts=$.extend(defaults,option);
	if(opts.toggle == 'true'){
		var pophtml = '<div class="pop_wrap1  '+opts.status+'" style="width:'+opts.width+';height:'+opts.height+';" ><div class="pop_con1"><div class="loadIcon"></div><p class="poptext1">'+opts.content+'</p></div></div>'
		$("body").append(pophtml);
	}else if(opts.toggle == 'false'){
		$(".pop_wrap1").remove();
	}
}
/**
 * 系统默认提示
 * @param option  后台返回的对象
 */
function sys_pop(result) {
	 if (result.code == "000000") {
		 var message = result.message == null ? "操作成功" : result.message;
		 if(result.successUrl){
			 pop({"content": message ,width:"6rem", "status": "normal", time: '2500'});
			 var t = setTimeout("redirect_url('"+result.successUrl+"')",1000)
		 }else{
			 pop({"content": message , "status": "normal", time: '2500'});
		 }
	 }else if(result.code == "000101"){//未登录没有权限
		 window.location.href = "/weixin/toLogin";//跳转登录
	 }else{
		 pop({"content":result.message,"status":"error",time:'3000'});
	 }
}
/**
 *页面跳转
 * @param url
 */
function redirect_url(url){
	if(url == "return_back"){
		history.back(-1);
	}else{
		window.location.href = url;//跳转
	}
     }

function alerts(e){
	var u = navigator.userAgent;
	var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
	var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
	var alertsHmtl = '';
	if(isAndroid){
		if(isVerticalScreen == 0){
			alertsHmtl = '<div id="msg" style="width:11rem;position:fixed;z-index:999;top:53%;margin-top:-80px;left: 0; right: 0; margin: 0 auto;background:#fff;font-size:17px;color:#666;text-align:center;line-height:1.5rem;display:inline-block;border-radius:2px;border-radius:25px;"><div id="msg_top" style="padding:5px 25px 0px 20px;text-align:center;font-size:.7rem;font-weight:bold;">提示</div><div id="msg_cont" style="padding:0px 20px 48px;font-size:.65rem;border-bottom:1px #a9a9a9 solid;text-align:center;">'+e+'</div><div class="msg_close" id="msg_clear" style="display:inline-block;color:#3370db;padding:1px 15px;border-radius:2px;font-size:.7rem;cursor:pointer;font-weight:bold;">我知道了</div></div>';
		}else{
			alertsHmtl = '<div id="msg" style="width:11rem;position:fixed;z-index:999;top:41%;margin-top:-80px;left: 0; right: 0; margin: 0 auto;background:#fff;font-size:17px;color:#666;text-align:center;line-height:1.5rem;display:inline-block;border-radius:2px;border-radius:25px;"><div id="msg_top" style="padding:5px 25px 0px 20px;text-align:center;font-size:.7rem;font-weight:bold;">提示</div><div id="msg_cont" style="padding:0px 20px 48px;font-size:.65rem;border-bottom:1px #a9a9a9 solid;text-align:center;">'+e+'</div><div class="msg_close" id="msg_clear" style="display:inline-block;color:#3370db;padding:1px 15px;border-radius:2px;font-size:.7rem;cursor:pointer;font-weight:bold;">我知道了</div></div>';
		}
	}else if(isiOS){
		alertsHmtl = '<div id="msg" style="width:11rem;position:fixed;z-index:999;top:41%;margin-top:-80px;left: 0; right: 0; margin: 0 auto;background:#fff;font-size:17px;color:#666;text-align:center;line-height:1.5rem;display:inline-block;border-radius:2px;border-radius:25px;"><div id="msg_top" style="padding:5px 25px 0px 20px;text-align:center;font-size:.7rem;font-weight:bold;">提示</div><div id="msg_cont" style="padding:0px 20px 48px;font-size:.65rem;border-bottom:1px #a9a9a9 solid;text-align:center;">'+e+'</div><div class="msg_close" id="msg_clear" style="display:inline-block;color:#3370db;padding:1px 15px;border-radius:2px;font-size:.7rem;cursor:pointer;font-weight:bold;">我知道了</div></div>';
	}
	$("body").append(alertsHmtl);
	$(".msg_close").click(function (){
		$("#msg").remove();
	});
}