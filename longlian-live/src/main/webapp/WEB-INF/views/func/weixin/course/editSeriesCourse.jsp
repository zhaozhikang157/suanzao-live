<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html  style="background:#f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>系列课程编辑</title>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/lCalendar.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
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
		#InvitedImg {
			width: 100%;
			height: 100%;
			display: none;
			position: absolute;
			left: 0;
			z-index: 1;
			background-color: #171717;
			background-repeat: no-repeat;
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

        <div class="SingleCourse_4">
       	 	<!--标题-->
            <div class="list_tit">
				<div class="list_tit">
					<span class="tit">课程名称</span>
				</div>
				<div class="newerrallBox">
					<textarea id="liveTopic" validate="emoji" class="newsizenewText" placeholder="请输入课程名称"maxlength="30"></textarea>
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
				<div class="list_tit">
					<span class="tit">转播类型</span>
				</div>
				<div class="freeofcharge">
					<ul class="frecharBox" style="padding-left: 0;">
						<li id="relayOK">允许所有用户转播<p class="newdistribution"></p></li>
						<li style="display: none">转播价格 (元)<input id="chargeAmt" type="number" onKeyUp="amount(this)" maxlength="5" placeholder="输入0即为免费转播" validate="money" value="0.00" class="newMuch"></li>
						<li style="display: none" class="fourdistribution">转播人所获分成比例 (%)<input type="tel" value="0" placeholder="请输入0-100之间的整数" value="0" id="separateInto" class="distribution"></li>
					</ul>
				</div>
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
		<div class="SingleCourse_4  newBackfileBox">
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
<!-- 上传中请稍后 -->
<%--<div class="Loading" style="display:none;">--%>
	<%--<img src="/web/res/image/Loading.png">--%>
    <%--<p>正在上传</p>--%>
<%--</div>--%>
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
var seriesid = '${seriesid}';
var coverssAddress = "";
var modelUrl = ''; //模版url
var isUptmp = false; //是否生成邀请码
var orderSortval ="";//下拉选中
var isRelay; //是否设置转播
var relayCharge; // 转播金额
var relayScale ; //转播比例
var isOpened; //是否开启过转播
var chargeAmt;
//图文混排
var courseContent = [];
$(function(){
	var orderSort="";
	var result = ZAjaxRes({url: "/courseType/getCourseType  ", type: "get"});
	var orderOption = result.data;
	if (result.code == "000000") {
		$.each(orderOption,function(i,n){
			orderSort+="<option value='"+n.id+"'>"+n.name+"</option>"
		});
		$(".newselectBox").append(orderSort)
	}
	//获取原始数据
    if (seriesid != "") {
         var result = ZAjaxRes({url: "/series/getSeriesCourseInfo?seriesid=" + seriesid, type: "GET"});
        if (result.code == "000000") {
			console.log(result.data)
			//分类
			for( var i=0;i<$(".newselectBox option").length;i++){
				if($(".newselectBox option").eq(i).val()==result.data.seriesCourse.courseType){
					$(".newselectBox option").eq(i).attr("selected","selected")
				}
			}
			//标题
            $("#liveTopic").val(result.data.seriesCourse.liveTopic);
			$(".must").text(result.data.seriesCourse.liveTopic);
			$('.textareaInput').text((result.data.seriesCourse.liveTopic).length);
			//课程详情
            $("#remark").text(result.data.seriesCourse.remark);
			$('.textareaInputnew').text((result.data.seriesCourse.remark).length)
			//封面图
            coverssAddress = result.data.seriesCourse.coverssAddress;
            $("#img2").attr("src",coverssAddress);
			////图文混排
            var courseImgList = result.data.courseImgList;
			isRelay = result.data.seriesCourse.isRelay;
			relayCharge = result.data.seriesCourse.relayCharge;
			relayScale = result.data.seriesCourse.relayScale;
			isOpened = result.data.seriesCourse.isOpened;
			chargeAmt = result.data.seriesCourse.chargeAmt;
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
			console.log(courseImgList);
			if (courseImgList.length > 0) {
				$.each(courseImgList, function (i, n) {
					if(n.address==''){
						var json = {"img":"", "content":n.content};
						courseContent.push(json);
						var html = '<div class="detailsBox">\
											<dl>\
												<dd class="noBpctext">\
													<textarea maxlength="800" oninput="cheange($(this))" class="datext" placeholder="请输入文字简介">'+n.content+'</textarea>\
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
												<dt><img src="' + n.address + '"/></dt>\
												<dd>\
													<textarea maxlength="800" oninput="cheange($(this))" class="datext" placeholder="请输入图片简介">'+n.content+'</textarea>\
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
		var orderSortval = $(".newselectBox option:selected").val();
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
			var newdistribution = $('.newdistribution').hasClass('on');
			var isRelay = 0;
			if(newdistribution){
				isRelay = 1;
			}
            //end--
            var remark = $("#remark").val();
			
			console.log(courseContent);
            var params = {
                "id": seriesid,
				"coverssAddress": coverssAddress,
                "liveTopic": liveTopic,
                "remark": remark,
				"modelUrl":modelUrl,
				"courseContent":JSON.stringify(courseContent),
				"courseType": orderSortval,
				"isRelay":isRelay,
				"relayCharge":relayMoney,
				"relayScale":separateInto
            };
			console.log(params);
		
            var result = ZAjaxRes({url: "/series/updateSeriesCourse.user", type: "POST", param: params});
            if (result.code == "000000") {
				setTimeout(function(){
					window.location.href = "/weixin/teacherSeries?seriesid="+seriesid;
				},2000)
            } else {
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
//		var relayMoney = $("#chargeAmt").val();//转播价格
        if (!liveTopic && valOld) {
            liveTopic = valOld;
        }
        //end--
		var json = {
			"coverssAddress": coverssAddress,
			"liveTopic": liveTopic,
//			"relayCharge":relayMoney
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
	$('#liveTopic').on("input", function () {
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
	$(".classNamebox").click(function(){
		$(".newtexterroy").show();
		if($('.newsizenewText').val().length>0){
			$(".newBcbtn").addClass("on");
		}
        //start--解决ios部分高版本，输入拼音，无法显示问题
        var  val = $('.newsizenewText').val();
        var valOld = $(".must").text();
        if (!val && valOld) {
            $('.newsizenewText').val(valOld);
            $(".textareaInput").text($('.newsizenewText').val().length);
            titNum = valOld;
            $(".newBcbtn").addClass("on");
        }
        //end--
	});
	$(".newBcbtn").click(function(){
		if($(this).hasClass("on")){
			$(".newtexterroy").hide();
			$(".must").text(titNum);
		}else{
			return;
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
									<textarea maxlength="800" oninput="cheange($(this))" class="datext" placeholder="请输入文字简介"></textarea>\
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
									<textarea maxlength="800" oninput="cheange($(this))" class="datext" placeholder="请输入图片简介"></textarea>\
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
function cheange(that){
	if(that.val().length>=800){
		$(".newpopsize").show();
		setTimeout(function(){
			$(".newpopsize").hide();
		},2000)
	}
}
$('.datext,#remark').off('click',asmk);
$('.datext,#remark').on('click',asmk);
function asmk(){
	var target = this;
	// 使用定时器是为了让输入框上滑时更加自然
	setTimeout(function(){
		target.scrollIntoView(true);
	},300);
};

$(".newbackFile").click(function(){
	var newBackImg = $('.newbackFile ').hasClass('on');
	if(newBackImg == ''){
		$('#file3').click();
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
	var result = ZAjaxRes({url: "/courseCard/isPermit?courseId=" + seriesid, type: "GET"});
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
		var result = ZAjaxRes({url: "/courseCard/getPreviewCourseCard",param:{modelUrl:url,courseId:seriesid}, type: "post"});
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
				var result = ZAjaxRes({url: "/courseCard/delCourseCard", type: "GET", param:{courseId: seriesid, modelUrl:modelUrl }});
				if (result.code == "000000") {
					modelUrl = '';
					pop1({"content": "删除成功" , "status": "normal", time: '2000'});
					$(".newbackFile").removeClass("on").find("i").text("请上传750*1334像素的图片").next().hide();
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
