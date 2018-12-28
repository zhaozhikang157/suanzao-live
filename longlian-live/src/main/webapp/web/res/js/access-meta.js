function reset() {
	var scale = 1 / window.devicePixelRatio;
	document.write('<meta name="viewport" content="width=device-width,initial-scale=' + scale + ',minimum-scale=' + scale + ',maximum-scale=' + scale + ',user-scalable=no" /><meta content="yes" name="apple-mobile-web-app-capable"><meta content="black" name="apple-mobile-web-app-status-bar-style"><meta content="telephone=no" name="format-detection"><meta http-equiv="expires" content="-1"/><meta http-equiv="Cache-Control" content="no-cache"/><meta http-equiv="Pragma" content="no-cache"/>');
	var width = document.documentElement.clientWidth*20/375;
	var ohtml = document.getElementsByTagName("html")[0];
	ohtml.style.fontSize = width + "px";
	if(IsPC()){
		if(width>30){
			width =30;
			ohtml.style.fontSize = width + "px";
		}
	}
};
reset();
window.onresize=function(){
	var width = document.documentElement.clientWidth*20/375;
	var ohtml = document.getElementsByTagName("html")[0];
	ohtml.style.fontSize = width + "px";
	if(IsPC()){
		if(width>30){
			width =30;
			ohtml.style.fontSize = width + "px";
		}
	}
};

function IsPC() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone",
                "SymbianOS", "Windows Phone",
                "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            flag = false;
            break;
        }
    }
    return flag;
}
