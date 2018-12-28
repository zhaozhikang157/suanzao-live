<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>创建系列单节课</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/lCalendar.css"/>
    <script src="/web/res/js/jquery.min.js"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/lCalendar.js"></script>
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
    </style>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="creatPopbox">
</div>
<div class="repeatBox">
    <div class="repeatCont">
        <h3>提示</h3>
        <p>您好，您的直播间有正在直播的</br>课程，建议结束直播课程后开课。</p>
        <div class="repeatBtn overClass">结束并创建新课程</div>
        <div class="repeatBtn iNooe">我知道了</div>
    </div>
</div>
<div id="wrapper" style="overflow: auto; height:100%">
    <div id="cont_box"style="padding-bottom: 0.7rem">
        <div class="newpop_wrap"style="top: 30%;">
            <p>课程名称最多不超过30个字</p>
        </div>
        <div class="found_SingleCourse_1" >
            <!-- 课程封面 -->
            <div class="coverPic">
                <div class="coverUpload">
                    <img src=""class="pic filePic" id="img2">
                    <span class="coverTap"></span>
                </div>
            </div>
            <!-- 课程名称 -->
            <div class="SingleCourse_4">
                <div class="list_tit">
                    <span class="tit">课程名称</span>
                </div>
                <!-- 标题文字输入 -->
                <div class="newerrallBox">
                    <textarea id="liveTopic" class="newsizenewText" placeholder="请输入课程名称"maxlength="30"></textarea>
                    <div class="textarea-count">
                        <span class="textareaInput">0</span>/<span class="textareaTotal">30</span>
                    </div>
                </div>
                <!-- 开课时间 -->
                <div class="list_tit">
                    <span class="tit">开课时间</span>
                    <input id="appDateTime" type="text" class="text" readonly name="appDateTime"  onfocus="this.blur()" placeholder="请选择开始时间" data-lcalendar="2018-01-11,2020-12-31"/>
                </div>
            </div>

            <!-- 横竖屏 -->
            <ul class="liveForm" id="liveWay" style="margin-top: .425rem">
                <li>
                    <p liveWay="3">竖屏直播</p>
                    <img src="/web/res/image/spzbpic.png" alt=""/>
                    <span></span>
                </li>
                <li>
                    <p liveWay="2">横屏直播</p>
                    <img src="/web/res/image/hpzbpic.png" alt=""/>
                    <span></span>
                </li>
                <li>
                    <p liveWay="1">语音直播</p>
                    <img src="/web/res/image/yyzbpic.png" alt=""/>
                    <span></span>
                </li>
            </ul>

            <!-- 表单验证错误提示 -->
            <p class="errorMessages" style="padding-left:.5rem;"></p>

            <!-- 确定完成按钮 -->
            <div class="sub_btn">
                <input class="found_S1 bgcol_c80"  disabled name="" type="button" value="完成">
            </div>
        </div>
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
    var peor;
    var shareOK;
    var seriesCourseId = '${seriesid}';
    var liveWayone = "";//直播形式
    var calendardatetime = new lCalendar();
    calendardatetime.init({
        'trigger': '#appDateTime',
        'type': 'datetime'
    });
    function p(s) {
        return s < 10 ? '0' + s: s;
    }
    var myDate = new Date();
    //获取当前年
    var year=myDate.getFullYear();
    //获取当前月
    var mt=myDate.getMonth()+1;
    //获取当前日
    var d=myDate.getDate();
    var h=myDate.getHours();       //获取当前小时数(0-23)
    var m=myDate.getMinutes();     //获取当前分钟数(0-59)
    var s=myDate.getSeconds();     //获取当前秒数(0-59)
    var now=year+'-'+p(mt)+"-"+p(d)+" "+p(h)+':'+p(m);
    $("#appDateTime").val(now);
    var roomId = '${roomId}';
    var coverssAddress = "";
    $(function () {
        $.getJSON("/series/getLastCourseType.user?seriesid=" + seriesCourseId, function (data) {
            if (data.code == '000000') {

                if(data.data=='1'){
                    $('#liveWay li').eq(2).find('p,span').addClass('on');
                }else if(data.data=='2'){
                    $('#liveWay li').eq(1).find('p,span').addClass('on');
                }else if(data.data=='3'){
                    $('#liveWay li').eq(0).find('p,span').addClass('on');
                }
                liveWayone = data.data;
            }
        });

        $(".iNooe").click(function(){
            $(".repeatBox").hide();
        })
        //点击完成提交创建单节课
        $('.found_S1').click(function () {
            $('.errorMessages:visible').html("");
            //标题不能有表情符
            if(isEmojiCharacter(liveTopic)){
                alert("课程主题暂不支持表情输入");
                return false;
            }
            if (roomId == "") {
                $('.errorMessages:visible').html("你还没有直播间！");
                return;
            }

            //是否重复课程
            var sameCourseId= "0";
            var result = ZAjaxJsonRes({url: "/course/isSameCoureForLiving.user", type: "GET"});
            if (result.code == "000000") {
                sameCourseId=result.data.sameCourseId;
                if(sameCourseId=="1"){
                    $(".repeatBox").show();
                    $(".overClass").click(function(){
                        if (flag) {
                            flag = false;
                            var params = param();
                            params.liveType = '0';
                            params.seriesCourseId = seriesCourseId;
                            params.isCloseId = 1;
                            $('.found_S1').prop('disabled', true);
                            $('.found_S1').addClass('bgcol_c80');
                            //ajax  提交后台
                            $.post("/course/createCourse.user", params, function (result) {
                                if (result.code == "000000") {
                                    $(".repeatBox").hide();
                                    window.location.href = "/weixin/courseInfo?id=" + result.data.courseId + "&shareOK&seriesid=" + seriesCourseId;
                                } else if(result.code == "100055"){
                                    $(".creatPopbox").show().html(result.message);
                                    setTimeout(function(){$(".creatPopbox").hide()},2000);
                                    $('.found_S1').removeClass('bgcol_c80');
                                    $('.found_S1').prop('disabled', false);
                                    flag = true;
                                    return;
                                } else{
                                    flag = true;
                                    $('.found_S1').removeClass('bgcol_c80');
                                    $('.found_S1').prop('disabled', false);
                                    return;
                                }
                            });
                        }
                    })
                }else{
                    if (flag) {
                        flag = false;
                        var params = param();
                        params.liveType = '0';
                        params.seriesCourseId = seriesCourseId;

                        $('.found_S1').prop('disabled', true);
                        $('.found_S1').addClass('bgcol_c80');
                        //ajax  提交后台
                        $.post("/course/createCourse.user", params, function (result) {
                            if (result.code == "000000") {
                                $(".repeatBox").hide();
                                window.location.href = "/weixin/courseInfo?id=" + result.data.courseId + "&shareOK&seriesid=" + seriesCourseId;
                            } else if(result.code == "100055"){
                                $(".creatPopbox").show().html(result.message);
                                setTimeout(function(){$(".creatPopbox").hide()},2000);
                                $('.found_S1').removeClass('bgcol_c80');
                                $('.found_S1').prop('disabled', false);
                                flag = true;
                                return;
                            }else{
                                flag = true;
                                $('.found_S1').removeClass('bgcol_c80');
                                $('.found_S1').prop('disabled', false);
                            }
                        });
                    }
                }
            }else{
                sys_pop(result);
            }
        });
        //验证必填
        $('#appDateTime,#liveTopic').on("input", function () {
            Required();
        });
        $('#btn').click(function(){
            setTimeout(function(){
                Required();
            },300);
        });
        //横竖屏语音
        $(".liveForm li").click(function(){
            $(this).find("p,span").addClass("on").parents().siblings().find("p,span").removeClass("on");
            Required();
        });
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
            };
            if(lenInput>=30){
                $(".newpop_wrap").show();
                setTimeout(function(){
                    $(".newpop_wrap").hide();
                },2000)
            }
        });
        $(".classNamebox").click(function(){
            //start--解决ios部分高版本，输入拼音，无法显示问题
            var  val = $('.sizenewText').val();
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
            var startTime = $("#appDateTime").val();//开课时间
            var liveWay = $('#liveWay li p.on').attr('liveWay');//直播形式
            var json = {
                "coverssAddress": coverssAddress,
                "liveTopic": liveTopic,
                "startTime": startTime,
                "liveWay": liveWay,
                "roomId": roomId,
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
    });

    //分销比例
    var result = ZAjaxJsonRes({url: "/course/getCourseDivideScale", type: "GET"});
    if (result.code == "000000") {
        if (result.data.length > 0){
            $.each(result.data, function (index, item) {
                if (index == 0) {
                    $("#divideScale").append('<li class="on" key = "'+item.key+'">'+item.value+'</li>');
                } else {
                    $("#divideScale").append('<li key = "'+item.key+'">'+item.value+'</li>');
                }
            });
        }
    } else {
        sys_pop(result);
    }
    $("#createSeriesCourse").click(function(){
        window.location.href="/weixin/createSeriesCourse?roomId="+roomId
    });
    $(".newtexterroy").bind("click",function(e){
        var target  = $(e.target);
        if(target.closest(".errallBox").length == 0){
            $(".newtexterroy").hide();
            $('.sizenewText').val($(".must").html());
            $(".textareaInput").text($('.sizenewText').val().length);
        }
    });
</script>

</html>
