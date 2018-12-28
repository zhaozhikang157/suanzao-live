<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html  style="background:#f0f0f0;">
<head>
	<meta charset="UTF-8">
	<meta name="Keywords" content=""/>
	<meta name="Description" content=""/>
	<title>课程编辑</title>
	<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/lCalendar.css?nd=<%=current_version%>"/>
	<script src="/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
	<script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
	<script src="/web/res/js/lCalendar.js?nd=<%=current_version%>"></script>
	<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
	<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
	<style>
		.introductionBox{
			padding-bottom:1.5rem;
			border: 1px solid #e1e1e1;
			box-sizing: border-box;
		}
		.introductionBox #remark{
			border: none;
			width: 100%;
			height: 3.5rem;
			font-size: 0.7rem;
			line-height: 1rem;
			overflow: hidden;
			padding-bottom: .75rem;
		}
		.newBackfileBox{
			display: none;
		}
		#InvitedImg{
			width: 100%;
			height: 100%;
			display: none;
			position: absolute;
			left: 0;
			z-index: 1;
			background-color:#171717;
			background-repeat:  no-repeat;
			background-size: 100% auto;
			background-position: center;
		}
		.textarea-count{
			bottom: .6rem;
		}
	</style>
</head>
<body>
<div id="wrapper" style=" overflow-x:hidden; -webkit-overflow-scrolling: touch;">
	<div class="newAggregate">
		<div id="InvitedImg"></div>
		<input type="file"  id="file3" name="file"  onchange="dads2(this)" style="display: none;" accept=".png,.gif,.jpg"/>
		<ul class="newAggregateul">
			<li>操作</li>
			<li id="previewImg">预览</li>
			<li id="updataImgFile">更换图片</li>
			<li id="delInvited">删除</li>
			<li class="newAggregatedete">取消</li>
		</ul>
	</div>
	<div class="creatPopall">
		<div class="creatPopbox">
		</div>
	</div>
	<div id="cont_box" style="padding-bottom: 0.7rem;position: relative">
		<div class="newpop_wrap newpop_wrap1">
			<p>课程名称最多不超过30个字</p>
		</div>
		<div class="newpopsize">
			<p>文字简介最多不超过800个字</p>
		</div>
		<div class="newpop_wrap newpop_wrap2">
			<p>课程简介最多不超过800个字</p>
		</div>

		<!--编辑-->
		<div class="SingleCourse_2" style="margin-top:0; padding-left:0;border: none">
			<div class="cover" style="border-bottom:none;">
				<div class="pic_1">
					<img class="pic" id="img2" src=""/>
					<p class="pic_ts">封面图</p>
				</div>
			</div>
		</div>
		<%--课程名称--%>
		<div class="SingleCourse_4">
			<div class="list_tit">
				<span class="tit">课程名称</span>
			</div>
			<%--标题--%>
			<div class="newerrallBox">
				<textarea id="liveTopic"validate="emoji" class="newsizenewText" placeholder="请输入课程名称"maxlength="30"></textarea>
				<div class="textarea-count">
					<span class="textareaInput">0</span>/<span class="textareaTotal">30</span>
				</div>
			</div>
			<%--分类--%>
			<div class="list_tit">
				<span class="tit">分类</span>
			</div>
			<select class="newselectBox">

			</select>
			<%--转播课程--%>
			<div class="list_tit relaySet">
				<span class="tit">转播类型<em>保存后不能更改</em></span>
			</div>
			<div class="freeofcharge relaySet">
				<ul class="frecharBox">
					<li id="relayOK">允许所有用户转播<p class="newdistribution"></p></li>
					<li style="display: none">转播价格 (元)<input id="chargeAmt" style="background: #fff;" onKeyUp="amount(this)" type="number" maxlength="5" placeholder="不输入即为免费转播" validate="money" value="0.00" class="newMuch"></li>
					<li style="display: none" class="fourdistribution">转播人所获分成比例 (%)<input type="tel" style="background: #fff;" value="0" placeholder="请输入0-100之间的整数" value="0" id="separateInto" class="distribution"></li>
				</ul>
			</div>
			<!-- 开课时间 -->
			<div class="list_tit">
				<span class="tit">开课时间</span>
			</div>
			<input id="appDateTime"  type="text" class="text" style="background:white url('/web/res/image/more.png') no-repeat center center;background-position: right .75rem center; background-size: .35rem .65rem;" readonly name="appDateTime"  onfocus="this.blur()" placeholder="请选择开始时间" data-lcalendar="2017-01-11,2020-12-31"/>
			<div class="tryTimes">
				<div class="list_tit">
					<span class="tit">设置试看时间</span>
				</div>
				<input id="addLook"  type="text" class="text" trySeeTime=0 readonly name="addLook"  onfocus="this.blur()" style="background:white url('/web/res/image/more.png') no-repeat center center;background-position: right .75rem center; background-size: .35rem .65rem;" placeholder="请设置试看时间" />
			</div>
			<!-- 上传课件 -->
			<span class="zbzt">上传图片课件 <i class="neitle">( 建议上传16:9 )</i></span>
			<div class="SingleCourse_2 ssds">
				<div class="courseware">
					<div class="add_imgs">
						<input class="btn" id="addBtn" name="" type="button">
					</div>
				</div>
			</div>
			<!-- 课程简介-->
			<div class="list_tit">
				<span class="tit">课程简介</span>
			</div>
			<div class="introductionBox">
				<textarea maxlength="800" validate="emoji" class="text" id="remark" placeholder="请输入课程简介" name="" ></textarea>
				<div class="textarea-count">
					<span class="textareaInputnew">0</span>/<span class="textareaTotal">800</span>
				</div>
			</div>
		</div>

		<!-- 课程详情列表 -->
		<div id="detailsList">

		</div>
		<ul class="changeUl">
			<li id="wxUploadPic">上传图片</li>
			<li id="wxUploadTxt">上传文字</li>
		</ul>
		<p class="errorMessages" style="padding-left:.5rem;"></p>
		<%--邀请卡背景--%>
		<div class="SingleCourse_4 newBackfileBox">
			<div class="list_tit">
				<span class="tit">邀请卡背景</span>
			</div>
			<p class="newbackFile">
				<i>请上传750*1334像素的图片</i>
				<img src="/web/res/image/newfileok.png">
			</p>
		</div>
		<!-- 保存按钮 -->
		<div class="sub_btn">
			<input class="found_S1"  name="" type="button" value="保存">
		</div>
		<!-- 图片截取 -->
		<div class="tujq">
			<div class="toBar">
				<button id="btn" type="button">使用</button>
				<button id="btn2" style="float:left;" type="button">取消</button>
			</div>
			<div class="img_content">
				<img src=""  id="img" />
			</div>
			<!--裁剪图片框。宽高为定义裁剪出的图片大小-->
			<canvas width="800" height="450" id="canvas"></canvas>
		</div>
		<input id="file" type="file" name="file"  onChange="selectFileImage(this);" accept="image/*"/>
	</div>
</div>

<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/img.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/require.js"></script>
<script src="/web/res/js/main.js"></script>
<script>
	var JQuery = $.noConflict();
	var courseId = '${courseId}';
	var coursePhoto = [];
	var coverssAddress = "";
	var modelUrl = ''; //模版url
	var isUptmp = false; //是否生成邀请码
	var orderSortval ="";//下拉选中
	//开课时间
	var calendardatetime = new lCalendar();

	var relayCharge; // 转播金额
	var relayScale ; //转播比例
	var isRelay; //是否设置转播
	var isOpened; //是否开启过转播
	var chargeAmt;
	calendardatetime.init({
		'trigger': '#appDateTime',
		'type': 'datetime'
	});
	//试看时长
	var calendartime = new lCalendar();
	calendartime.init({
		'trigger': '#addLook',
		'type': 'time',
	});
	//图文混排
	var courseContent = [];
	$(function(){
		var orderSort="";
		var result = ZAjaxRes({url: "/courseType/getCourseType  ", type: "get"});
		var orderOption = result.data;
		console.log(orderOption)
		if (result.code == "000000") {
			$.each(orderOption,function(i,n){
				orderSort+="<option value='"+n.id+"'>"+n.name+"</option>"
			});
			$(".newselectBox").append(orderSort)
		}
		//获取原始数据
		if (courseId != "") {
			var result = ZAjaxRes({url: "/course/getCourseInfo?id=" + courseId, type: "GET"});
			if (result.code == "000000") {
				console.log(result.data)
				isRelay = result.data.course.isRelay;
				relayCharge = result.data.course.relayCharge;
				relayScale = result.data.course.relayScale;
				isOpened = result.data.course.isOpened;
				chargeAmt =result.data.course.chargeAmt;
				if(isOpened =='1' ){
					$('#chargeAmt,#separateInto').attr("disabled","disabled").css('color','#333');
				}
				if(isRelay == '1'){
					$('.newdistribution').addClass('on');
					$('#chargeAmt').val(relayCharge);
					$('#separateInto').val(relayScale);
					$('.relay_ok').removeClass('on');
					$('#relayOK').nextAll().show();
					if(chargeAmt == '0.00'){
						$('.fourdistribution').hide();
					}
				}
				//分类
				for( var i=0;i<$(".newselectBox option").length;i++){
					if($(".newselectBox option").eq(i).val()==result.data.course.courseType){
						$(".newselectBox option").eq(i).attr("selected","selected")
					}
				}
				//标题
				$("#liveTopic").val(result.data.course.liveTopic);
				$(".must").text(result.data.course.liveTopic);
				$('.textareaInput').text((result.data.course.liveTopic).length);
				//时间
				$("#appDateTime").val(result.data.course.startTime);
				//课程详情
				$("#remark").text(result.data.course.remark);
				$('.textareaInputnew').text((result.data.course.remark).length)
				//封面图
				coverssAddress = result.data.course.coverssAddress;
				$("#img2").attr("src",coverssAddress );
				if( result.data.course.chargeAmt==0.00||result.data.course.liveWay=='1'){
					$('.tryTimes').hide();
				}
				$('.fourdistribution').attr('data-chargeAmt',result.data.course.chargeAmt);
				//试看时间
				if(result.data.course.trySeeTime>0){
					$("#addLook").attr('trySeeTime',result.data.course.trySeeTime);
					var trySeeT = parseInt(result.data.course.trySeeTime/60)+'分'+(result.data.course.trySeeTime%60)+'秒';
					$("#addLook").val(trySeeT);
				}
//				if(result.data.course.liveWay == '1'){
//					$('.relaySet').hide();
//				}

				//试看时间

				//课件
				var courseWaresList = result.data.courseWaresList;
				transformation(courseWaresList);
				if (courseWaresList.length > 0) {
					$.each(courseWaresList, function (i, n) {
						var html = "";
						html += '<div class="picss">';
						html += '<div class="pic" style="background:url(' + n.address + ') no-repeat center center; background-size:cover;"></div>'
						html += '<i class="colsses" onclick="colsses(this)"></i>'
						html += "</div>";
						$("#addBtn").before(html);
					});
				}
				////图文混排
				var courseImgList = result.data.courseImgList;
				console.log(courseImgList);
				if (courseImgList.length > 0) {
					$.each(courseImgList, function (i, n) {
						if(n.address==''){
							var json = {"img":"", "content":n.content};
							courseContent.push(json);
							var html = '<div class="detailsBox">\
											<dl>\
												<dd class="noBpctext">\
													<textarea class="datext"oninput="cheange($(this))" maxlength="800" placeholder="请输入文字简介">'+n.content+'</textarea>\
												</dd>\
												<span class="newcloss" onclick="colssesWx(this)"></span>\
											</dl>\
											<p>\
												<em class="oPrev" onclick="oPrev(this)"></em>\
												<em class="oNext" onclick="oNext(this)"></em>\
											</p>\
										</div>'
							$('#detailsList').append(html);
						}else{
							var json = {"img":n.address, "content":n.content};
							courseContent.push(json);
							var html = '<div class="detailsBox">\
											<dl>\
												<dt><img src="'+n.address +'"></dt>\
												<dd>\
													<textarea class="datext"oninput="cheange($(this))" maxlength="800" placeholder="请输入图片简介">'+n.content+'</textarea>\
												</dd>\
												<span class="newcloss" onclick="colssesWx(this)"></span>\
											</dl>\
											<p>\
												<em class="oPrev" onclick="oPrev(this)"></em>\
												<em class="oNext" onclick="oNext(this)"></em>\
											</p>\
										</div>';
							$("#detailsList").append(html);
						}
					});
					console.log(courseContent);
				}
			}
		};
		//下拉框选中值
		$(".newselectBox").change(function(){
			orderSortval = $(".newselectBox option:selected").val();
		});

		//课件
		function transformation(courseWaresList) {
			for (var i = 0; i < courseWaresList.length; i++) {
				coursePhoto[i] = courseWaresList[i].address;
			}
		};
		var flag = true;

		$(".found_S1").on('click', function () {
			$('.errorMessages:visible').html("");
			$('.SingleCourse_3 input.text,.SingleCourse_3 textarea.text').each(function (i) {
				if (!inputTest($(this))) {
					alert("不支持emoji表情输入");
					flag = false;
					return;
				}
			});
			var regex = /^100$|^(\d|[1-9]\d)$/;
			var relayMoney = $("#chargeAmt").val();//转播价格
			var separateInto = $("#separateInto").val();//转播人所获分成比例
			//验证金额
			if($('#relayOK .newdistribution').hasClass('on')){
				if(relayMoney<=0&&relayMoney!=='0'&&relayMoney != ''){
					pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
					return;
				}else{
					if(relayMoney>10000){
						pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
						return;
					}
				}
				// 验证分成比例
				if($(".newdistribution").hasClass("on")){
					if(!/^(\+|-)?\d+$/.test( separateInto ) && separateInto!= ''){
						pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
						return;
					}
					if(separateInto < 0  && !regex.test(separateInto) && separateInto!= ''){
						pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
						return;
					}else if(separateInto>100 && !regex.test(separateInto) && separateInto!= ''){
						pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
						return;
					}
				}
			}

			if(relayMoney == ''){
				relayMoney = 0;
				$("#chargeAmt").val(relayMoney);
			}
			if(separateInto == ''){
				separateInto = 0;
				$("#separateInto").val(separateInto);
			}
			$.each(courseContent, function (i, n) {
				n.content = $('.datext').eq(i).val();
			});
			if (flag) {
				flag = false;
				var liveTopic = $("#liveTopic").val();
				//start--解决ios部分高版本，输入拼音，无法显示问题
				var valOld = $(".must").text();
				if (!liveTopic && valOld) {
					liveTopic = valOld;
				}
				//end--
				var startTime = $("#appDateTime").val();
				var remark = $("#remark").val();
				var trySeeTime = $("#addLook").attr('trySeeTime');
				var coursePhotos = coursePhoto.join(';');
				var newdistribution = $('.newdistribution').hasClass('on');
				var isRelay = 0;
				if(newdistribution){
					isRelay = 1;
				}

				console.log(courseContent);
				var params = {
					"id": courseId,
					"coverssAddress": coverssAddress,
					"liveTopic": liveTopic,
					"startTime": startTime,
					"remark": remark,
					"trySeeTime":trySeeTime,
					"coursePhoto": coursePhotos,
					"modelUrl":modelUrl,
					"courseContent":JSON.stringify(courseContent),
					"courseType": orderSortval,
					"isRelay":isRelay,
					"relayCharge":relayMoney,
					"relayScale":separateInto
				};
				console.log(params);
				var result = ZAjaxRes({url: "/course/uploadCourse.user", type: "POST", param: params});
				if (result.code == "000000") {
					setTimeout(function(){
						window.location.href = "/weixin/teachercourse?id=" + courseId;
					},2000)
				} else if (result.code == "100056") {
					$(".creatPopall").show();
					$(".creatPopbox").show().html(result.message);
					setTimeout(function(){$(".creatPopall,.creatPopbox").hide()},2000);
					flag = true;
					return;
				}else{
					alert(result.message);
				}
			}
			flag = true;
		});

		//	转播
		$('#relayOK').click(function(){
			$(this).children('.newdistribution').toggleClass('on');
			if(isOpened == '1'){
				$('#chargeAmt').val(relayCharge);
				$('#separateInto').val(relayScale);
				$('#chargeAmt,#separateInto').attr("disabled","disabled");
				if($('.newdistribution').hasClass('on')){
					$(this).nextAll().show();
				}else{
					$(this).nextAll().hide();
				}
			}else{
				if($('.newdistribution').hasClass('on')){
					$(this).nextAll().show();
				}else{
					$(this).nextAll().hide();
				}
			}
			if(chargeAmt == '0.00'){
				$('.fourdistribution').hide();
			}
			if($(this).children('.newdistribution').hasClass('on')&& isOpened!= '1'){
				$('#chargeAmt').val('');
				$('#separateInto').val('');
			}else if(isOpened!= '1'){
				$('#chargeAmt').val('0');
				$('#separateInto').val('0');
			}
//			$(this).nextAll().toggle();
//			if($('.fourdistribution').attr('data-chargeamt') == 0.00){
//				$('.fourdistribution').hide();
//			}
//			if($(this).children('.newdistribution').hasClass('on')){
//				$('#chargeAmt').val('');
//				$('#separateInto').val('');
//			}else{
//				$('#chargeAmt').val('0');
//				$('#separateInto').val('0');
//				Required();
//			}
		})
		//验证必填
		$('#chargeAmt,#separateInto').on("input", function () {
			Required();
		});
		// 获取数据
		function param(){
			var liveTopic = $.trim($("#liveTopic").val());//标题
			//start--解决ios部分高版本，输入拼音，无法显示问题
			var valOld = $(".must").text();
			if (!liveTopic && valOld) {
				liveTopic = valOld;
			}
			//end--
			var startTime = $("#appDateTime").val();//开课时间
			var relayMoney = $("#chargeAmt").val();//转播价格

			var json = {
				"coverssAddress": coverssAddress,
				"liveTopic": liveTopic,
				"startTime": startTime,
			};
			return json;
		};
		//必填验证函数
		function Required(){
			var params = param();
			var bOk = true;
			$.each(params,function(name,value){
				if(isEmptyPic(value)){
					bOk = false;
				}
			});
			if(bOk){
				$('.found_S1').removeClass('bgcol_c80');
				$('.found_S1').prop('disabled', false);
			}else{
				$('.found_S1').prop('disabled', true);
				$('.found_S1').addClass('bgcol_c80');
			}
		};
		//验证必填
		$('#appDateTime,#liveTopic').on("input", function () {
			Required();
		});

		//文字输入限制计数
		var titNum;
		var lenInput = $('.newsizenewText').val().length;
		$(".newsizenewText").on("input",function(){
			lenInput = $(this).val().length;
			titNum = $('.newsizenewText').val();
			if(lenInput>=0 && lenInput<=30){
				$('.textareaInput').html(lenInput);
				$(".newBcbtn").addClass("on");
			}
			if(lenInput==0){
				$(".newBcbtn").removeClass("on");
			};
			if(lenInput>=30){
				$(".newpop_wrap1").show();
				setTimeout(function(){
					$(".newpop_wrap1").hide();
				},2000)
			}
		});
		var introduction;
		var introinput = $('#remark').val().length;
		$("#remark").on("input",function(){
			introinput = $(this).val().length;
			introduction = $('#remark').val();
			if(introinput>=0 && introinput<=800){
				$('.textareaInputnew').html(introinput);
			}
			if(introinput>=800){
				$(".newpop_wrap2").show();
				setTimeout(function(){
					$(".newpop_wrap2").hide();
				},2000)
			}
		});
		//微信上传图片
		$('#wxUploadPic').click(function(){
			wechatUploadPic();
		});
		//文字
		$('#wxUploadTxt').click(function(){
			var json = {"img":'', "content":"","localId":""};
			courseContent.push(json);
			var oDiv = '<div class="detailsBox">\
							<dl>\
								<dd class="noBpctext">\
									<textarea class="datext"maxlength="800"oninput="cheange($(this))" placeholder="请输入文字简介"></textarea>\
								</dd>\
								<span class="newcloss" onclick="colssesWx(this)"></span>\
							</dl>\
							<p>\
								<em class="oPrev" onclick="oPrev(this)"></em>\
								<em class="oNext" onclick="oNext(this)"></em>\
							</p>\
						</div>'
			$('#detailsList').append(oDiv);
		});
	});


	//数组位置调换
	function change(arr,index,upDow){
		var temp;
		temp = arr[index];
		if(upDow=='up'){
			arr[index] = arr[index-1];
			arr[index-1] = temp;
		}else if(upDow=='dow'){
			arr[index] = arr[index+1];
			arr[index+1] = temp;
		}
		return arr;
	};
	//上移
	function oPrev(that){
		var _this = $(that).parents('.detailsBox');
		var index = _this.index();
		if(index==0){
			alert('到头了');
			return;
		}
		var oPrev = _this.prev('.detailsBox');
		_this.insertBefore(oPrev);
		change(courseContent,index,'up');
	};
	//下移动
	function oNext(that){
		var _this = $(that).parents('.detailsBox');
		var index = _this.index();
		if(index==($('.detailsBox').length-1)){
			alert('到底了');
			return;
		}
		var oNext = _this.next('.detailsBox');
		oNext.insertBefore(_this);
		change(courseContent,index,'dow');
	};
	//删除图片
	function colssesWx(that) {
		var index = $(that).parents('.detailsBox').index();
		$(that).parents('.detailsBox').remove();
		if(index == 0){
			courseContent.splice(index,1);
		}else{
			courseContent.splice(index,1);
		}
	}
	//上传图片回调函数
	function wxUploadPic(){
		$.each(localUrl, function (i, value) {
			var json = {"img":weiXinServiceId[i], "content":"","localId":value};
			courseContent.push(json);
			var html = '<div class="detailsBox">\
							<dl>\
								<dt><img src="' + value + '"/></dt>\
								<dd>\
									<textarea maxlength="800"oninput="cheange($(this))" class="datext" placeholder="请输入图片简介"></textarea>\
								</dd>\
								<span class="newcloss" onclick="colssesWx(this)"></span>\
							</dl>\
							<p>\
								<em class="oPrev" onclick="oPrev(this)"></em>\
								<em class="oNext" onclick="oNext(this)"></em>\
							</p>\
						</div>';
			$("#detailsList").append(html);
		});
	};
	$('.datext,#remark').off('click',asmk);
	$('.datext,#remark').on('click',asmk);
	function asmk(){
		var target = this;
		// 使用定时器是为了让输入框上滑时更加自然
		setTimeout(function(){
			target.scrollIntoView(true);
		},300);
	};
	function cheange(that){
		if(that.val().length>=800){
			$(".newpopsize").show();
			setTimeout(function(){
				$(".newpopsize").hide();
			},2000)
		}
	}
	$(".newbackFile").click(function(){
		var newBackImg = $('.newbackFile ').hasClass('on');
		if(newBackImg == ''){
			$('#file3').val('').click();
		}else{
			$(".newAggregate").show();
		}
	})
	$('#updataImgFile').click(function(){
		isUptmp = false;
		$('#file3').val('').click();
	})
	function dads2(input){
		var file = input.files[0];
		var reader = new FileReader();
		reader.readAsDataURL(file);
		ajaxFileUpload2();
		$('.newbackFile>i').html($(this).val());
	}
	function ajaxFileUpload2(){
		JQuery.ajaxFileUpload({
			url: '/file/upload',
			secureuri: false,
			async: false,
			fileElementId: 'file3',//file标签的id
			dataType: 'json',//返回数据的类型
			data: {},//一同上传的数据
			success: function (data, status) {
				if(data.code == '000000'){
					$(".newbackFile").addClass("on").find("i").text("上传成功").next().show();
					pop({"content": "上传成功！" , "status": "error", time: '1000'});
					modelUrl = data.data.url;
					$('#InvitedImg').attr('data-url',data.data.url);
					$('.newAggregate').hide();
				}
			},
			error: function (data, status, e) {
				console.log(e);

			}
		});
	}
	$(function(){
		var result = ZAjaxRes({url: "/courseCard/isPermit?courseId=" + courseId, type: "GET"});
		if(result.code == '000000' && result.data == 1){
			$('.newBackfileBox').show();
			if(result.ext.modelUrl != '' && result.ext.modelUrl != undefined ){
				isUptmp = true;
				modelUrl = result.ext.modelUrl;
				$('#InvitedImg').attr('data-url',result.ext.modelUrl).css('background-image','url('+result.ext.cardUrl+')');
				$(".newbackFile").addClass("on").find("i").text("上传成功").next().show();
			}
		}
	})
	$('#previewImg').on('click',function(){
		if(isUptmp){
			$('#InvitedImg').show();
		}else{
			var url = $('#InvitedImg').attr('data-url');
			var result = ZAjaxRes({url: "/courseCard/getPreviewCourseCard",param:{modelUrl:url,courseId:courseId}, type: "post"});
			if(result.code == '000000'){
				$('#InvitedImg').css('background-image','url('+result.data+')').show();
			}
		}
	})
	$('#InvitedImg').on('click',function(){
		$(this).hide()
	})
	$('#delInvited').on('click',function(){
		BaseFun.Dialog.Config({
			title: '提示',
			text : '确定要删除此背景吗',
			cancel:'取消',
			confirm:'确认',
			callback:function(index) {
				if(index == 1){
					var result = ZAjaxRes({url: "/courseCard/delCourseCard", type: "GET", param:{courseId: courseId, modelUrl:modelUrl }});
					if (result.code == "000000") {
						pop1({"content": "删除成功" , "status": "normal", time: '2000'});
						modelUrl = '';
						$(".newbackFile").removeClass("on").find("i").text("请上传750*1334像素的图片").next().hide();
						$('.newAggregate').hide();
					}
				}
			}
		});
	})
	$(".newAggregatedete").click(function(){
		$(".newAggregate").hide();
	});
	function amount(th){
		var temp = /^\d+\.?\d{0,2}$/;
		if (temp.test(th.value)) {
		} else {
			th.value = th.value.substr(0, th.value.length - 1);
			// return false;
		}
	}

</script>
<script src="/web/res/js/ajaxfileupload.js"></script>
</body>
</html>
