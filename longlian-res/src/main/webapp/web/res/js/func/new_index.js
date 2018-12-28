var days=new  Array ("日", "一", "二", "三", "四", "五", "六");
function showDT() {
	var currentDT = new Date();
	var y,m,date,day,hs,ms,ss,theDateStr;
	y = currentDT.getFullYear(); //四位整数表示的年份
	if(y<1900) y = y+1900;
	m = currentDT.getMonth()+1; //月
	if(m<10) m = '0' + m;
	date = currentDT.getDate(); //日
	if(date<10) date = '0' + date;
	
	day = currentDT.getDay(); //星期  
	if (day==1) {
		day="一"
	}else if (day==2) {
		day="二"
	}else if (day==3) {
		day="三"
	}else if (day==4) {
		day="四"
	}else if (day==5) {
		day="五"
	}else if (day==6) {
		day="六"
	}else if (day==0) {
		day="日"
	}
	
	hs = currentDT.getHours(); //时
	if(hs<10) hs = '0' + hs;
	ms = currentDT.getMinutes(); //分
	if(ms<10) ms = '0' + ms;
	ss = currentDT.getSeconds(); //秒
	if(ss<10) ss = '0' + ss;	
	theDateStr = '';
	theDateStr += '<span>'+y+'年'+m+'月'+date+'日</span><span>星期'+day+'</span><span>'+hs+':'+ms+':'+ss+'</span>';
	$(".timeclick").html(theDateStr)
	window.setTimeout( showDT, 1000);
}
