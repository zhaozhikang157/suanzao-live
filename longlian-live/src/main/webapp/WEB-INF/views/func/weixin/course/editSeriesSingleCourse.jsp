<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html  style="background:#f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>系列单节课程编辑</title>
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
		.textarea-count{
			bottom: .6rem;
		}
	</style>
</head>
<body>
<div id="wrapper" style=" overflow-x:hidden; -webkit-overflow-scrolling: touch;">
	<div class="creatPopall">
		<div class="creatPopbox">
		</div>
	</div>
    <div id="cont_box" style="padding-bottom:0.7rem;position: relative">
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
				<!-- 开始时间 -->
				<div class="list_tit">
					<span class="tit">开始时间</span>
					<input id="appDateTime" type="text" class="text" style="border:none;" readonly name="appDateTime"  onfocus="this.blur()" placeholder="请选择开始时间" data-lcalendar="2017-01-11,2020-12-31"/>
				</div>
				<div class="list_tit tryTimes">
					<span class="tit">设置试看时间</span>
					<input id="addLook" type="text" class="text" style="border:none;" readonly name="addLook"  onfocus="this.blur()" placeholder="请设置试看时间" />
				</div>
				<!-- 上传课件 -->
				<span class="zbzt" style="padding-left: 0">上传图片课件 <i class="neitle">( 建议上传16:9 )</i></span>
				<div class="SingleCourse_2 ssds">
					<div class="courseware">
						<div class="add_imgs">
							<input class="btn" id="addBtn" name="" type="button">
						</div>
					</div>
				</div>
				<!-- 课程详情 -->
				<div class="list_tit">
					<span class="tit">课程简介</span>
				</div>
				<div class="introductionBox" style="border-top: 1px solid #ccc">
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
<%--<!-- 上传中请稍后 -->--%>
<%--<div class="Loading" style="display:none;">--%>
	<%--<img src="/web/res/image/Loading.gif">--%>
    <%--<p>正在上传</p>--%>
<%--</div>--%>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/img.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/require.js"></script>
<script src="/web/res/js/main.js"></script>
<script>
var JQuery = $.noConflict();
var courseId = '${courseId}';
var seriesid = '${seriesid}';
var coursePhoto = [];
var coverssAddress = "";
//开课时间
var calendardatetime = new lCalendar();
calendardatetime.init({
	'trigger': '#appDateTime',
	'type': 'datetime'
});
//试看时长
var calendartime = new lCalendar();
calendartime.init({
	'trigger': '#addLook',
	'type': 'time'
});
//图文混排
var courseContent = [];
$(function(){
	//获取原始数据
    if (courseId != "") {
        var result = ZAjaxRes({url: "/course/getCourseInfo?id=" + courseId, type: "GET"});
        if (result.code == "000000") {
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
			if( result.data.course.seriesAmt==0.00||result.data.course.liveWay=='1'){
				$('.tryTimes').hide();
			}
			//试看时间
			if(result.data.course.trySeeTime>0){
				$("#addLook").attr('trySeeTime',result.data.course.trySeeTime);
				var trySeeT = parseInt(result.data.course.trySeeTime/60)+'分'+(result.data.course.trySeeTime%60)+'秒';
				$("#addLook").val(trySeeT);
			}
			//课件
            var courseWaresList = result.data.courseWaresList;
            transformation(courseWaresList);
            if (courseWaresList.length > 0) {
                $.each(courseWaresList, function (i, n) {
                    var html = "";
                    html += '<div class="picss">';
                    html += '<img class="pic" src="' + n.address + '"/>'
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
												<dd>\
													<textarea maxlength="800"oninput="cheange($(this))" class="datext" placeholder="请输入文字简介">'+n.content+'</textarea>\
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
													<textarea maxlength="800"oninput="cheange($(this))" class="datext" placeholder="请输入图片简介">'+n.content+'</textarea>\
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

				console.log(courseContent);
				var params = {
					"id": courseId,
					"coverssAddress": coverssAddress,
					"liveTopic": liveTopic,
					"startTime": startTime,
					"trySeeTime":trySeeTime,
					"remark": remark,
					"coursePhoto": coursePhotos,
					"courseContent":JSON.stringify(courseContent)
				};
				console.log(params);

				var result = ZAjaxRes({url: "/course/uploadCourse.user", type: "POST", param: params});
				if (result.code == "000000") {
					setTimeout(function(){
						window.location.href = "/weixin/teachercourse?id=" + courseId+"&seriesid="+seriesid;
					},2000)
				}else if(result.code == "100056"){
					$(".creatPopall").show();
					$(".creatPopbox").show().html(result.message);
					setTimeout(function(){$(".creatPopall,.creatPopbox").hide()},2000);
					flag = true;
					return;
				} else {
					alert(result.message);
				}
			}
			flag = true;
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
								<dd>\
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
$(".newtexterroy").bind("click",function(e){
	var target  = $(e.target);
	if(target.closest(".errallBox").length == 0){
		$(".newtexterroy").hide();
		$('.newsizenewText').val($(".must").html());
		$(".textareaInput").text($('.newsizenewText').val().length);
	}
});
</script>
</body>
</html>
