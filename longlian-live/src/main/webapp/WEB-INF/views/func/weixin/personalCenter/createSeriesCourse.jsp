<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>创建系列课</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/lCalendar.css"/>
    <script src="/web/res/js/jquery.min.js"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <style>
        .dw {
            transform: translate(0px, -350px) !important;
        }
        input[type=radio] {
            width: 0.5rem;
            height: 0.5rem;
            margin-right: 6px;
            border-radius: 0.5rem
        }
        .paymentBox{
            padding: 0;
        }
</style>
</head>

<body>
<div id="wrapper" style=" overflow: auto; -webkit-overflow-scrolling: touch;">
    <div id="cont_box"style="padding-bottom: 0.95rem">
        <div class="newpop_wrap" style="top: 30%;">
            <p>课程名称最多不超过30个字</p>
        </div>

        <ul class="createCategories">
            <li id="createSeriesSingleCourse"><a>创建单节课</a></li>
            <li id="createSeriesCourse"><a class="on">创建系列课</a></li>
        </ul>
    	<div class="found_SingleCourse_2">
            <div class="coverPic">
                <div class="coverUpload">
                    <img src=""class="pic filePic" id="img2">
                    <span class="coverTap"></span>
                </div>
            </div>
        	<div class="SingleCourse_4">
            	<div class="list_tit">
                	<span class="tit">课程名称</span>
                </div>
                <div class="newerrallBox">
                    <textarea  id="liveTopic"  class="newsizenewText" placeholder="请输入课程名称"maxlength="30"></textarea>
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
            </div>
            <div class="paymentBox">
                <div class="options">
                    <i style="margin-left: .5rem">选择付费类型</i>
                    <em>保存后不能更改</em>
                </div>
                <div class="freeofcharge">
                    <ul class="frecharBox">
                        <li>免费课<span class="on"></span></li>
                        <li>付费课<span></span></li>
                    </ul>
                    <ul class="sfBox">
                        <li>收费金额 (元)<input id="chargeAmt" type="number" placeholder="请输入金额" onKeyUp="amount(this)" validate="money" value="0.00" class="newMuch"></li>
                        <li>设置分销奖励<p class="newdistribution"></p></li>
                        <li class="fourdistribution">学生所获分销比例 (%)<input type="number" value="0" placeholder="请输入1-99之间的整数" class="distribution"></li>
                    </ul>
                </div>
            </div>
            <!-- 表单验证错误提示 -->
            <p class="errorMessages" style="padding-left:.5rem;"></p>
            <div class="sub_btn">
                <input class="found_S1 bgcol_c80"  disabled="disabled"  name="" style="width:16rem;" type="button" value="完成">
            </div>
        </div>
    </div>
	<div class="tujq">
        <div class="toBar">
        <button id="btn" type="button">使用</button>
        <button id="btn2" style="float:left;" type="button">取消</button>
        </div>
        <div class="img_content">
            <img src=""  id="img" />
        </div>
        <!--裁剪图片框。宽高为定义裁剪出的图片大小-->
        <canvas id="canvas"></canvas>
    </div>
    <input id="file" type="file" name="file"  onChange="selectFileImage(this);" accept="image/*"/>
</div>
</body>
<script src="/web/res/js/jquery.min.js"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/img.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/require.js"></script>
<script src="/web/res/js/main.js"></script>
<script>
    var flag = true;
    var roomId = '${roomId}';
    var coverssAddress = "";
	var sizeNum = "";
    $(function () {
        var orderSort="";
        var result = ZAjaxRes({url: "/courseType/getCourseType  ", type: "get"});
        var orderOption = result.data;
        if (result.code == "000000") {
            $.each(orderOption,function(i,n){
                orderSort+="<option value='"+n.id+"'>"+n.name+"</option>"
            });
            $(".newselectBox").append(orderSort).val(orderOption[2].id)
        }
        yanzheng();
        //提交 验证表单
        $('.found_S1').click(function () {
			$('.errorMessages:visible').html("");
            var distribution = $('.distribution').val(),
                        money = $('#chargeAmt').val();
			//验证金额
            if(money<1 && money!=='0.00'){
                pop({"content": "请输入正确金额 (1-10000)", "status": "error", time: '2500'});
				  return;
            }else{
                if(money>10000){
//                    $('.errorMessages:visible').html("金额为1 ~ 10000!");
                    pop({"content": "请输入正确金额 (1-10000)", "status": "error", time: '2500'});
                    return;
                }
            }
            if($(".newdistribution").hasClass("on")){
                // 验证分销金额
                if(distribution < 1 || distribution =='0'|| distribution ==''){
                    pop({"content": "请输入正确分销金额 (1-99)", "status": "error", time: '2500'});
                    return;
                }else{
                    if(distribution>99){
                        pop({"content": "请输入正确分销金额 (1-99)", "status": "error", time: '2500'});
                        return;
                    }
                }
            }
			//标题不能有表情符
			if(isEmojiCharacter(liveTopic)){
				alert("课程主题暂不支持表情输入");
				return false;
			}
			
            if (roomId == "") {
				$('.errorMessages:visible').html("你还没有直播间！");
                return;
            }
            var orderSortval = $(".newselectBox option:selected").val();
            if (flag) {
                flag = false;
				var params = param();
				params.isSeriesCourse = 1;
				params.liveType = '0';
                params.customDistribution = distribution;
                params.courseType = orderSortval;
				$('.found_S1').addClass('bgcol_c80');
        		$('.found_S1').prop('disabled', true);
				
				//ajax  提交后台
                $.post("/series/createSeriesCourse.user", params, function (result) {
                    if (result.code == "000000") {
                        if(sizeNum==1){
                            localStorage.setItem("newdistribution", 1); //是否分销  1为设置分销状态
                        }else{
                            localStorage.setItem("newdistribution", 0); //是否分销  1为设置分销状态
                        }
                       window.location.href="/weixin/teacherSeries?seriesid="+ result.data+"&shareOK";
                    } else {
                        flag = true;
                        $('.found_S1').removeClass('bgcol_c80');
                        $('.found_S1').prop('disabled', false);
                        return;
                    }
                });
			}

        });

        //收费免费
        $(".frecharBox li").click(function(){
            $(this).find("span").addClass("on").parent().siblings().find("span").removeClass("on");
            if($(this).index()==1){
                $(".sfBox").show();
                $('#chargeAmt').val('');
                $(".fourdistribution").hide();
                var newdistributionNum = localStorage.getItem("newdistribution");  //判断老师上次创建课程设置分销
                if(newdistributionNum == '1'){
                    $('.newdistribution').addClass('on');
                    $(".fourdistribution").show();
                    $(".distribution").val("");
                }else{
                    $('.newdistribution').removeClass('on');
                    $(".fourdistribution").hide();
                }
                yanzheng();
            }else{
                $(".sfBox").hide();
                $('#chargeAmt').val('0.00');
                $(".newdistribution").removeClass("on");
                $(".distribution").val("0");
            }
            yanzheng();
//            Required();
        });
        //设置分销比例
        $(".fourdistribution").click(function(){
            $(".distribution").focus();
        });
        $(".newdistribution").click(function(){
            if($(this).hasClass("on")){
                $(this).removeClass("on");
                $(".fourdistribution").css("display","none");
                $(".distribution").val("0");
                sizeNum = 0;
            }else{
                $(this).addClass("on");
                $(".fourdistribution").show();
                $(".distribution").val("");
                sizeNum = 1
            }
            yanzheng();
        })
		//标题
        var titNum;
        var lenInput = $('.newsizenewText').val().length;
        $(".newsizenewText").on("input",function(){
            lenInput = $(this).val().length;
            titNum = $.trim($('.newsizenewText').val());
            if(lenInput>=0 && lenInput<=30){
                $('.textareaInput').html(lenInput);
                $(".newBcbtn").addClass("on");
            };
            if(titNum==""){
                $(".newBcbtn").removeClass("on");
                return;
            }
            if(lenInput==0){
                $(".newBcbtn").removeClass("on");
            }
            if(lenInput>=30){
                $(".newpop_wrap").show();
                setTimeout(function(){
                    $(".newpop_wrap").hide();
                },2000)
            }
        });
        $(".classNamebox").click(function(){
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
            $(".newtexterroy").show();
        });
        $(".newBcbtn").click(function(){
            if($(this).hasClass("on")){
                $(".newtexterroy").hide();
                $(".must").text(titNum);
            }else{
                return;
            }

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
			var distribution = $('.distribution').val();//分销比例
			var chargeAmt = $("#chargeAmt").val();//价格

			var json = {
				"coverssAddress": coverssAddress,
				"liveTopic": liveTopic,
				"chargeAmt": chargeAmt,
				"roomId": roomId,
                "distribution":distribution
			};
			return json;
		};
		//必填验证函数
		function Required(){
			var params = param();
			var bOk = true;
			$.each(params,function(name,value){
                console.log(name,value)
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
        function yanzheng(){
            Required();
            if($(".frecharBox li:last >span").hasClass("on")){
                $('#chargeAmt').on("input", function () {
                    Required();
                });
                if($(".newdistribution").hasClass("on")){
                    $('#chargeAmt,.distribution').on("input", function () {
                        Required();
                    });
                };
            }else{
                $('#liveTopic').on("input", function () {
                    Required();
                });
            }
        }

		$('#btn').click(function(){
			setTimeout(function(){
				Required();
			},300);
		});
		
    });
	$("#createSeriesSingleCourse").click(function(){
        window.location.href="/weixin/createSingleCourse?id="+roomId
    });
    $('#chargeAmt').off('click',asmk);
    $('#chargeAmt').on('click',asmk);
    function asmk(){
        var target = this;
        // 使用定时器是为了让输入框上滑时更加自然
        setTimeout(function(){
            target.scrollIntoView(true);
        },300);
    }
    $(".newtexterroy").bind("click",function(e){
        var target  = $(e.target);
        if(target.closest(".errallBox").length == 0){
            $(".newtexterroy").hide();
            $('.newsizenewText').val($(".must").html());
            $(".textareaInput").text($('.newsizenewText').val().length);
        }
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

</html>