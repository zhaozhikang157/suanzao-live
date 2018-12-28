var days=new  Array ("日", "一", "二", "三", "四", "五", "六");  
function showDT() {  
  var currentDT = new Date();  
  var y,m,date,day,hs,ms,ss,theDateStr;  
  y = currentDT.getFullYear(); //四位整数表示的年份  
  if(y<1900) y = y+1900; 
  m = currentDT.getMonth()+1; //月  
  if(m<10) m = '0' + m;
  date = currentDT.getDate(); //日  
  hs = currentDT.getHours(); //时 
  if(hs<10) hs = '0' + hs; 
  ms = currentDT.getMinutes(); //分
  if(ms<10) ms = '0' + ms;  
  ss = currentDT.getSeconds(); //秒
  if(ss<10) ss = '0' + ss;  
  theDateStr = y+"/"+  m +"/"+date+"/"+" "+hs+":"+ms+":"+ss;  
  $(".timeclick").text(theDateStr)
  window.setTimeout( showDT, 1000);  
}  

$(".okclick").mouseover(function(){
	$(".dem").show();
});
$(".okclick").mouseleave(function(){
	$(".dem").hide();
})


