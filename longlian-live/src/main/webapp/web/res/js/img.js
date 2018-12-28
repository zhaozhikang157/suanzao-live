$(function(){
	//点击空白区域关闭
	$('#wrapper').on('click',function(e){
	  var _con = $('input[type=number],textarea');   // 设置目标区域
	  if(!_con.is(e.target) && _con.has(e.target).length === 0){
		$('input[type=number],textarea').blur();
		$('#cont_box').removeClass('contBoxminHeight');
	  }
	});
	//输入框遮挡问题
	$('input[type=number]').click(function(){
		var target = this;
		if(navigator.userAgent.toLowerCase().indexOf('iphone')!=-1){
			return;
		}else{
			$('#cont_box').addClass('contBoxminHeight');
			// 使用定时器是为了让输入框上滑时更加自然
			setTimeout(function(){
				target.scrollIntoView(true);
			},300);
		};
	});
	//输入框遮挡问题
	$('textarea').click(function(){
		if(	$(".sizenewText").attr("class","sizenewText")
		){
			return;
		}
		if(navigator.userAgent.toLowerCase().indexOf('iphone')!=-1){
			return;
		}else{
			var target = this;
			// 使用定时器是为了让输入框上滑时更加自然
			setTimeout(function(){
				target.scrollIntoView(true);
			},300);
		};
	});
});

//canvas高度
var canvas=document.getElementById("canvas");
	window.onresize=resizeCanvas;
	function resizeCanvas(){
		canvas.width=window.innerWidth-4;
		canvas.height=canvas.width*9/16;
	}
resizeCanvas();
//是否是课件图片
	var CoursewareOk = false;
	var _index = '';
	//上传课件图片
	$("#addBtn").unbind("click").on("click", function () {
        $("#file").click();
		CoursewareOk = true;
    });
	//编辑替换
	$('.add_imgs').unbind("click").on('click','.picss',function(){
		_index = $(this).index();
		console.log(_index);
		$("#file").click();
		CoursewareOk = true;
	})
	//上传封面
	$("#img2").unbind("click").on("click", function () {
        $("#file").click();
		CoursewareOk = false;
    });
	//删除图片
    function colsses(that) {
        var index = $(that).parent('.picss').index();
        $(that).parent('.picss').remove();
		if(index == 0){
			coursePhoto.splice(index,1);
		}else{
			coursePhoto.splice(index,1);
		}
		console.log(coursePhoto);
		return false;
    };
	window.addEventListener("load",function(){
		var $canvas = document.querySelector("canvas");		
		var myCrop;
		require(["jquery", 'hammer', 'tomPlugin', "tomLib", 'hammer.fake', 'hammer.showtouch'], function($, hammer, plugin, T) {
			var opts = {
					cropWidth: $canvas.width,
					cropHeight: $canvas.height
			},
			previewStyle = {
				x: 0,
				y: 0,
				scale: 1,
				rotate: 0,
				ratio: 1
			},
			transform = T.prefixStyle("transform"),
			myCrop = T.cropImage({
				bindFile: $('#file'),
				enableRatio: false, //是否启用高清,高清得到的图片会比较大
				canvas: $canvas, //放一个canvas对象
				cropWidth: opts.cropWidth, //剪切大小
				cropHeight: opts.cropHeight,
				bindPreview: $("#img"), //绑定一个预览的img标签
				useHammer: true, //是否使用hammer手势，否的话将不支持缩放
				oninit: function() {

				},
				onLoad: function(data) {
					//用户每次选择图片后执行回调
					resetUserOpts();
					previewStyle.ratio = data.ratio;
					$("#img").css({
						width: data.width,
						height: data.height
					}).css(transform, 'scale(' + 1 / previewStyle.ratio + ')');
					myCrop.setCropStyle(previewStyle);
				}
			});

			function resetUserOpts() {
				$("canvas").hammer('reset');
				previewStyle = {
					scale: 1,
					x: 0,
					y: 0,
					rotate: 0
				};
			};
			$("canvas").hammer({
				gestureCb: function(o) {
					//每次缩放拖拽的回调
					//console.log(previewStyle);
					$.extend(previewStyle, o);
					previewStyle.rotate = 0;
					if(previewStyle.scale<1||previewStyle.ratio<1){
						previewStyle.scale=1;
						previewStyle.ratio=1;
					}
					var disS = previewStyle.scale/previewStyle.ratio;
					var imgW = window.innerWidth;
					var disX  = imgW*disS-imgW;
					
					var imgH = $('#img').height();
					var disY = imgH-imgW*9/16;
					
					if(disS==1){
						//横向限制
						if(previewStyle.x!=0){
							previewStyle.x=0;
						}
						//纵向限制
						if(previewStyle.y>disY/2){
							previewStyle.y=disY/2
						}else if(-previewStyle.y>disY/2){
							previewStyle.y=-disY/2
						}
					}else if(disS>1){
						if(previewStyle.x-disX/2>0){
							previewStyle.x=disX/2-4;
						}else if(-previewStyle.x>disX/2){
							previewStyle.x = -disX/2+4;
						}
						//纵向限制
					}
					$("#img").css(transform, "translate3d(" + previewStyle.x + 'px,' + previewStyle.y + "px,0) rotate(" + previewStyle.rotate + "deg) scale(" + (previewStyle.scale / previewStyle.ratio) + ")")
				}
			});
			var timer = null;
			$("#btn").on("click", function() {
				clearInterval(timer);
				myCrop.setCropStyle(previewStyle)
				var src = myCrop.getCropFile({});
				$(this).html('上传中');
				timer = setTimeout(function(){
					if(!CoursewareOk){//封面图片
						var result = ZAjaxRes({url: "/file/baseUrlConvert", type: "POST", param: {baseUrl:src}});
						if(result.code == '000000'){
							$("#img2").attr("src", result.data);
							coverssAddress = result.data;
							$("#btn").html('使用');
						}
					}else{//课件图片
						var result = ZAjaxRes({url: "/file/baseUrlConvert", type: "POST", param: {baseUrl:src}});
						if (result.code == '000000') {
							if(_index === ''){
								coursePhoto.push(result.data);
								$('#addBtn').before('<div class="picss">\
									<div class="pic" id="pic1"  style="background:url(' +result.data+ ') no-repeat center center; background-size:cover;"></div>\
									<i class="colsses" onclick="colsses(this)"></i>\
								</div>');
							}else{
								coursePhoto.splice(_index,1,result.data);
								$('.add_imgs .picss').eq(_index).find('.pic').attr("src", result.data);
								_index = '';
							}
							$("#btn").html('使用');
						}
					}
					$('.tujq').hide();
				},30);	
			});
			$("#btn2").on("click", function() {
				clearInterval(timer);
				$('.tujq').hide();
				$("#btn").html("使用");
			});
		});
	},false);


/**
 * 图片转base64
 * @param {Object} img
 */
function img_base64(img,width,height){
	var canvas = document.createElement("canvas");
	canvas.width = width;
	canvas.height = height;
	var c = canvas.getContext("2d");
	c.drawImage(img,0,0,width,height);
	var dataUrl = canvas.toDataURL("image/jpeg",0.3 );
	return dataUrl;
}

/**
 * base64转blob
 * @param {Object} base64
 */
function img_blob(base64){
	var dataURI = base64; //base64 字符串
	var mimeString =  dataURI.split(',')[0].split(':')[1].split(';')[0]; // mime类型
	var byteString = atob(dataURI.split(',')[1]); //base64 解码
	var arrayBuffer = new ArrayBuffer(byteString.length); //创建缓冲数组
	var intArray = new Uint8Array(arrayBuffer); //创建视图
	for (i = 0; i < byteString.length; i += 1) {
	     intArray[i] = byteString.charCodeAt(i);
	}
	var blob = new Blob([intArray], { type:  mimeString }); //转成blob
	return blob;
}

/**
 * 获取本地图片路径
 * @param {Object} selector
 */
function getFileUrl(selector){
	var img_url; 
	if(navigator.userAgent.indexOf("MSIE")>=1){
		img_url = selector.value; 
	}else{
		img_url = window.URL.createObjectURL(selector.files.item(0)); 
	}
	return img_url;
}

/**
 * 图片加载
 * @param {Object} img_src
 * @param {Object} callback
 */
function loadImg(img_src,callback){
	var img = new Image();
	img.src = img_src;
	img.onload = function(){
		callback(img);
	};
}

//图片生成best64
function selectFileImage(fileObj) { 
var file = fileObj.files['0']; 
//图片方向角 added by lzk 
var Orientation = null; 
if (file) { 
	//console.log("正在上传,请稍后...");
	$('.tujq').show(); 
	var rFilter = /^(image\/jpeg|image\/png)$/i; // 检查图片格式 
	if (!rFilter.test(file.type)) { 
		//showMyTips("请选择jpeg、png格式的图片", false);
		alert('请选择jpeg、png格式的图片') ;
		$('.tujq').hide();
		return; 
	} 
	// var URL = URL || webkitURL; 
	//获取照片方向角属性，用户旋转控制 
	EXIF.getData(file, function() { 
	   // alert(EXIF.pretty(this)); 
		EXIF.getAllTags(this);  
		//alert(EXIF.getTag(this, 'Orientation'));  
		Orientation = EXIF.getTag(this, 'Orientation'); 
		//return; 
	}); 

	var oReader = new FileReader(); 
	oReader.onload = function(e) { 
		//var blob = URL.createObjectURL(file); 
		//_compress(blob, file, basePath); 
		var image = new Image(); 
		image.src = e.target.result;
		image.onload = function() { 
			var expectWidth = this.naturalWidth; 
			var expectHeight = this.naturalHeight; 
			 
		 
			expectWidth = window.innerWidth-4; 
			expectHeight = expectWidth * this.naturalHeight / this.naturalWidth; 
			
			var canvas = document.createElement("canvas"); 
			var ctx = canvas.getContext("2d"); 
			canvas.width = expectWidth; 
			canvas.height = expectHeight; 
			ctx.drawImage(this, 0, 0, expectWidth, expectHeight); 
			var base64 = null;
			//修复ios 
			if(navigator.userAgent.match(/iphone/i)) { 
				//alert(expectWidth + ',' + expectHeight); 
				//如果方向角不为1，都需要进行旋转 added by lzk 
				if(Orientation != "" && Orientation != 1){ 
					//alert('旋转处理'); 
					switch(Orientation){ 
						case 6://需要顺时针（向左）90度旋转 
							//alert('需要顺时针（向左）90度旋转'); 
							rotateImg(this,'left',canvas); 
							break; 
						case 8://需要逆时针（向右）90度旋转 
							//alert('需要顺时针（向右）90度旋转'); 
							rotateImg(this,'right',canvas); 
							break; 
						case 3://需要180度旋转 
							//alert('需要180度旋转'); 
							rotateImg(this,'right',canvas);//转两次 
							rotateImg(this,'right',canvas); 
							break; 
					}        
				} 
				base64 = canvas.toDataURL("image/jpeg", 0.3); 
			}else if (navigator.userAgent.match(/Android/i)) {// 修复android 
				base64 = canvas.toDataURL("image/jpeg", 0.3); 
				
			}else{ 
				//alert(Orientation); 
				if(Orientation != "" && Orientation != 1){ 
					//alert('旋转处理'); 
					switch(Orientation){ 
						case 6://需要顺时针（向左）90度旋转 
							//alert('需要顺时针（向左）90度旋转'); 
							rotateImg(this,'left',canvas); 
							break; 
						case 8://需要逆时针（向右）90度旋转 
							//alert('需要顺时针（向右）90度旋转'); 
							rotateImg(this,'right',canvas); 
							break; 
						case 3://需要180度旋转 
							//alert('需要180度旋转'); 
							rotateImg(this,'right',canvas);//转两次 
							rotateImg(this,'right',canvas); 
							break; 
					}        
				} 
				base64 = canvas.toDataURL("image/jpeg", 0.3); 
			} 
			//
			$("#img").attr("src", base64);
		};
		
	}; 
	oReader.readAsDataURL(file); 
} 
} 

//对图片旋转处理 added by lzk www.bcty365.com 
function rotateImg(img, direction,canvas) {   
	//alert(img); 
	//最小与最大旋转方向，图片旋转4次后回到原方向   
	var min_step = 0;   
	var max_step = 3;   
	//var img = document.getElementById(pid);   
	if (img == null)return;   
	//img的高度和宽度不能在img元素隐藏后获取，否则会出错   
	var height = img.height;   
	var width = img.width;   
	//var step = img.getAttribute('step');   
	var step = 2;   
	if (step == null) {   
		step = min_step;   
	}   
	if (direction == 'right') {   
		step++;   
		//旋转到原位置，即超过最大值   
		step > max_step && (step = min_step);   
	} else {   
		step--;   
		step < min_step && (step = max_step);   
	}   
	//img.setAttribute('step', step);   
	/*var canvas = document.getElementById('pic_' + pid);   
	if (canvas == null) {   
		img.style.display = 'none';   
		canvas = document.createElement('canvas');   
		canvas.setAttribute('id', 'pic_' + pid);   
		img.parentNode.appendChild(canvas);   
	}  */ 
	//旋转角度以弧度值为参数   
	var degree = step * 90 * Math.PI / 180;   
	var ctx = canvas.getContext('2d');   
	switch (step) {   
		case 0:   
			canvas.width = width;   
			canvas.height = height;   
			ctx.drawImage(img, 0, 0);   
			break;   
		case 1:   
			canvas.width = height;   
			canvas.height = width;   
			ctx.rotate(degree);   
			ctx.drawImage(img, 0, -height);   
			break;   
		case 2:   
			canvas.width = width;   
			canvas.height = height;   
			ctx.rotate(degree);   
			ctx.drawImage(img, -width, -height);   
			break;   
		case 3:   
			canvas.width = height;   
			canvas.height = width;   
			ctx.rotate(degree);   
			ctx.drawImage(img, -width, 0);   
			break;   
	}   
} 


  