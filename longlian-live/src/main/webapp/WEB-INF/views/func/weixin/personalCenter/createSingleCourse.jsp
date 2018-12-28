<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>创建单节课</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/lCalendar.css"/>
    <script src="/web/res/js/jquery.min.js"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/lCalendar.js?nd=<%=current_version%>"></script>
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
        .must{
            width: 11rem;
        }
        .paymentBox{
            padding: 0;
        }
    </style>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="repeatBox">
    <div class="repeatCont">
        <h3>提示</h3>
        <p>您好，您的直播间有正在直播的</br>课程，建议结束直播课程后开课。</p>
        <div class="repeatBtn overClass">结束并创建新课程</div>
        <div class="repeatBtn iNooe">我知道了</div>
    </div>
</div>
<div class="creatPopbox">
</div>
<div id="wrapper" style="overflow: auto; height:100%;">
    <div id="cont_box" style="padding-bottom: 3.9rem;position: relative">
        <div class="newpop_wrap" style="top: 8rem;">
            <p>课程名称最多不超过30个字</p>
        </div>
        <!--创建单节课  -->
        <ul class="createCategories">
            <li id="createSeriesSingleCourse"><a class="on">创建单节课</a></li>
            <li id="createSeriesCourse"><a>创建系列课</a></li>
        </ul>
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
                <%--分类--%>
                <div class="list_tit">
                    <span class="tit">分类</span>
                </div>
                <select class="newselectBox">

                </select>
                <!-- 开课时间 -->
                <div class="list_tit">
                    <span class="tit">开课时间</span>
                </div>
                <input id="appDateTime" type="text" class="text" readonly name="appDateTime"  onfocus="this.blur()" placeholder="请选择开始时间"
                       data-lcalendar="2018-01-11,2020-12-31"/>
            </div>

            <!-- 分享比例 -->
            <div class="newpaymentBox">
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
                        <li>收费金额 (元)<input id="chargeAmt" type="number"placeholder="请输入金额" validate="money" onKeyUp="amount(this)" value="0.00" class="newMuch"></li>
                        <li>设置分销奖励<p class="newdistribution"></p></li>
                        <li class="fourdistribution">学生所获分销比例 (%)<input type="number" value="" placeholder="请输入1-99之间的整数" class="distribution"></li>
                    </ul>
                </div>
            </div>

            <!-- 横竖屏 -->
            <ul class="liveForm" id="liveWay">
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
                <input class="found_S1 bgcol_c80 new_bgco"  disabled name="" type="button" value="完成">
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
    var sizeNum = "";
//设置控件的年月份
//    function FormatDate() {
//    var date = new Date();
//    return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
//    }
//    var dataLcalendar =FormatDate() ;
//    $('#appDateTime').attr('data-lcalendar',dataLcalendar+",2018-12-31")
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
    var calendardatetime = new lCalendar();
    calendardatetime.init({
        'trigger': '#appDateTime',
        'type': 'datetime'
    });
    var roomId = '${roomId}';
    var coverssAddress = "";
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
        $(".iNooe").click(function(){
            $(".repeatBox").hide();
        })
        //点击完成提交创建单节课
        $('.found_S1').click(function () {
            var regex = /^[1-9][0-9]?$/;
            var distribution =$(".distribution").val();
            $('.errorMessages:visible').html("");
            //验证金额
            if($('#chargeAmt').val()<1&&$('#chargeAmt').val()!=='0.00'){
                pop({"content": "请输入正确金额 (1-10000)", "status": "error", time: '2500'});
                return;
            }else{
                if($('#chargeAmt').val()>10000){
                    pop({"content": "请输入正确金额 (1-10000)", "status": "error", time: '2500'});
                    return;
                }
            }
            // 验证分销金额
            if($(".newdistribution").hasClass("on")){
                if(distribution < 1 || distribution ==''||!regex.test(distribution) ){
                    pop({"content": "请输入正确分销金额 (1-99)", "status": "error", time: '2500'});
                    return;
                }else{
                    if(distribution>99||!regex.test(distribution)){
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
            //判断是否重复课程
            var params = param();
            var sameCourseId= "0";
            var result = ZAjaxJsonRes({url: "/course/isSameCoureForLiving.user", type: "GET"});
            if (result.code == "000000") {
                var orderSortval = $(".newselectBox option:selected").val();
                sameCourseId=result.data.sameCourseId;
                if(sameCourseId=="1"){
                    $(".repeatBox").show();
                    $(".overClass").click(function(){
                        if (flag) {
                            flag = false;
                            params.liveType = '0';
                            params.coursePlanCount = '0';
                            params.isSeriesCourse = '0';
                            params.updatedCount = '0';
                            params.isCloseId = 1;
                            params.customDistribution = distribution;
                            params.courseType = orderSortval;
                            $('.found_S1').prop('disabled', true);
                            $('.found_S1').addClass('bgcol_c80');
                            //ajax  提交后台
                            $.post("/course/createCourseNew.user", params, function (result) {
                                if (result.code == "000000") {
                                    $(".repeatBox").hide();
                                    if(sizeNum=="1"){
                                        localStorage.setItem("creatSer",1);//是否分销
                                    }else{
                                        localStorage.setItem("creatSer",0);//是否分销
                                    }
//                                    localStorage.setItem("liveWay",params.liveWay);   //直播类型
                                    window.location.href = "/weixin/courseInfo?id=" + result.data.courseId + "&shareOK";
                                } else if(result.code == "100055") {
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
                                    return;
                                }
                            });
                        }
                    })
                }else{
                    if (flag) {
                        flag = false;
                        params.liveType = '0';
                        params.coursePlanCount = '0';
                        params.isSeriesCourse = '0';
                        params.updatedCount = '0';
                        params.customDistribution = distribution;
                        params.courseType = orderSortval;
                        $('.found_S1').prop('disabled', true);
                        $('.found_S1').addClass('bgcol_c80');
                        //ajax  提交后台
                        $.post("/course/createCourseNew.user", params, function (result) {
                            if (result.code == "000000") {
                                $(".repeatBox").hide();
                                if(sizeNum==1){
                                    localStorage.setItem("creatSer",1);//是否分销
                                }else{
                                    localStorage.setItem("creatSer",0);//是否分销
                                }
//                                localStorage.setItem("liveWay",params.liveWay);   //直播类型   1为设置分销状态
                                window.location.href = "/weixin/courseInfo?id=" + result.data.courseId + "&shareOK";
                            }else if(result.code == "100055") {
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
                                return;
                            }
                        });
                    }
                }
            }else{
                sys_pop(result);
            }
        });
        //收费免费
        $(".frecharBox li").click(function(){
            $(this).find("span").addClass("on").parent().siblings().find("span").removeClass("on");
            if($(this).index()==1){
                $(".sfBox").show();
                $('#chargeAmt').val('');
                $(".fourdistribution").hide();
                var newdistributionNum = localStorage.getItem("creatSer");  //判断老师上次创建课程设置分销
                if(newdistributionNum == '1'){
                    $('.newdistribution').addClass('on');
                    $(".fourdistribution").show();
                }else{
                    $('.newdistribution').removeClass('on');
                    $(".fourdistribution").hide();
                }
                yanzheng();
            }else{
                $('#appDateTime').on("input", function () {
                    Required();
                });
                $(".sfBox").hide();
                $('#chargeAmt').val('0.00');
                $(".newdistribution").removeClass("on");
                $(".distribution").val("");
            }
            yanzheng();
//            Required();
        });
        yanzheng();
        //设置分销比例
        $(".fourdistribution").click(function(){
            $(".distribution").focus();
        })
        $(".newdistribution").click(function(){
            if($(this).hasClass("on")){
                $(this).removeClass("on");
                $(".fourdistribution").css("display","none");
                $(".distribution").val("");
                sizeNum = 0
            }else{
                $(this).addClass("on");
                $(".fourdistribution").show();
                sizeNum = 1
            }
            Required();
        })

        //横竖屏语音
        $(".liveForm li").click(function(){
            $(this).find("p,span").addClass("on").parent().siblings().find("p,span").removeClass("on");
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
            }
            if(lenInput>=30){
                $(".newpop_wrap").show();
                setTimeout(function(){
                    $(".newpop_wrap").hide();
                },2000)
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
            var chargeAmt = $("#chargeAmt").val();//价格
            var liveWay = $('#liveWay li p.on').attr('liveWay');//直播形式
            var distribution =$(".distribution").val();//分销
            var json = {
                "coverssAddress": coverssAddress,
                "liveTopic": liveTopic,
                "startTime": startTime,
                "chargeAmt": chargeAmt,
                "liveWay": liveWay,
                "roomId": roomId
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
                    $('#chargeAmt').on("input", function () {
                        Required();
                    });
                }
            }
            $('#liveTopic').on("input", function () {
                Required();
            });

        }

        $('#btn').click(function(){
            setTimeout(function(){
                Required();
            },300);
        });
    });

    $("#createSeriesCourse").click(function(){
        window.location.href="/weixin/createSeriesCourse?roomId="+roomId
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
