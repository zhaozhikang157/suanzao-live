$(document).ready(function(){
	//屏幕自适应
	function adjustWidthHeight() {
		//加载时适应浏览器高度
		var width = $(window).outerWidth(); 
		var height = $(window).outerHeight();
		var topHeight =$("#header").outerHeight();
		$('#main,#sidebar,#main_conetnt').css('height', height - topHeight);
	};
	adjustWidthHeight();
	$(window).resize(function() {
		//改变窗体大小时适应浏览器高度
		adjustWidthHeight();
	});
	
	
	
});