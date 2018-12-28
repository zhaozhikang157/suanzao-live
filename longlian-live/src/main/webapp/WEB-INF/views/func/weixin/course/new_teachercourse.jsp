<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>   
    <title id="liveTopic">正在加载...</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/pullToRefresh.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-2.1.0.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <style>
        body{
            overflow: visible;
            -webkit-overflow-scrolling: touch;
        }
        .livebroadcast{
            position: relative;
            width: 100%;
            max-width: 18.75rem;
            height: 100%;
            margin: 0 auto;
        }
        .support{
            background: none;
        }
    </style>
</head>
<body>
<!--正在直播 -->
<div class="settzzc">
    <div class="sessbox">
        <img src="/web/res/image/pic_heiban.png" alt=""/>
        <a href="http://a.app.qq.com/o/simple.jsp?pkgname=com.llkj.liveshow">下载手机app</a>
    </div>
</div>
<div class="publicPopups" style="display: none;">
    <div class="publicPopupsbox">
        <p class="publicPopupstitle">提示</p>
        <p class="publicPopupscont">流量不足,暂时无法开课<br>请及时充值</p>
        <ul class="publicPopupsul">
            <li class="no">取消</li>
            <li class="yes">充值</li>
        </ul>
    </div>
</div>
<div class="livebroadcast" >
    <div id="cont_box" style="height: 100%;background: none">
        <div class="sicree" style="padding-bottom: 2.4rem;">
            <div class="livevideo yupic" id="COVERSS_ADDRESS" style="background-size:cover; background-position:center center;">
                <div class="bjyBox">
                    <div class="bjyBoxs">
                        <div class="bhbox">
                            <p class="invite">分享赚钱</p>
                        </div>
                        <span class="bjy"></span>
                    </div>
                </div>
                <div class="ygbox">
                    <p></p>
                    <p></p>
                </div>
                <div class="playback_icon"><img src="/web/res/image/playback_icon.png" alt=""/></div>
            </div>
            <ul class="livetitle" style="display:none;">
                <li><a class="on">课程详情</a></li>
                <div class="lin"></div>
                <li><a>课程评价</a></li>
            </ul>
            <div class="inforallbox bd" id="tabBox1-bd">
                <div class="coursenew">
                    <div class="con">
                        <div style="margin-bottom: .25rem">
                            <div class="videotitlebox">
                                <div class="videotitlebox_bd">
                                    <div class="videomidbox">
                                        <p class="videotitle">正在加载...</p>
                                        <span class="usernub offree">免费</span>
                                    </div>
                                    <span class="opentimes">正在加载...</span>
                                </div>
                            </div>
                            <ul class="subnavigation">
                                <li><a>想学 <span id="visitCount">0</span></a></li>
                                <div class="partition"></div>
                                <li><a>学习 <span  id="studyCount">0</span></a></li>
                                <%--<li><a>评论 <span  id="commentCount">0</span></a></li>--%>
                            </ul>
                        </div>
                        <%--<p class="pucmode">--%>
                        <%--<span>老师介绍</span>--%>
                        <%--</p>--%>
                        <div class="teacherxq" style="margin-bottom: 0">
                            <div class="teacherzl">
                                <div class="teacherlog"></div>
                                <div class="thname">
                                    <p class="topteacherbox"><i></i></p>
                                    <%--<p class="btomteacherbox">正在加载...</p>--%>
                                </div>
                                <span class="newMore"></span>
                            </div>
                        </div>
                        <ul class="teacherlistdetails">
                            <li>
                                <i id="studyAllCount">0</i>
                                <span>学生数</span>
                            </li>
                            <div class="partition"></div>
                            <li>
                                <i id="teachCourseCount">0</i>
                                <span>单节课</span>
                            </li>
                            <div class="partition"></div>
                            <li>
                                <i id="sequence"></i>
                                <span>系列课</span>
                            </li>
                        </ul>
                        <p class="pucmode tladdress">
                            <span>推流链接</span>
                        </p>
                        <div class="logbox tladdress">
                            <div class="pushFlow">暂无链接</div>
                        </div>
                        <p class="pucmode distribution">
                            <span>收费类型</span>
                        </p>
                        <div class="logbox distribution"style="margin-top: 0">
                            <div class="distribution_items">
                                <span class="distribution_m">学生分销比例：<i>0%</i></span>
                                <div class="partition"></div>
                                <span class="distribution_m">学生分销金额(元)：<i>0</i></span>
                            </div>
                        </div>
                        <p class="pucmode">
                            <span>课程简介</span>
                        </p>

                        <div class="logbox">
                            <div class="livetext">正在加载...</div>
                            <div id="courseImgList"></div>
                        </div>
                        <p class="pucmode pucmodekj" id="courseWare" style="display: none;">
                            <span>课件图片</span>
                        </p>
                        <div id="courseWaresList"></div>
                        <%--<p class="pucmode nextpum" id="crowdtitle"><span class="crowdtitle">适宜人群</span></p>--%>
                        <%--<div class="logbox">--%>
                        <%--<i class="crowd"></i>--%>
                        <%--</div>--%>
                    </div>
                </div>
                <div id="commentList" style="display:none; margin-top:.55rem; padding-left:.5rem; background:#FFF;">
                    <div class="con"style="min-height:20rem; overflow:hidden;">

                    </div>
                </div>
            </div>
            <div class="pullUpLabel" style="display: none;">上拉加载数据</div>

            <div class="support">
                酸枣在线提供技术支持<a href="tel:400-116-9269">联系客服</a>
            </div>
        </div>
    </div>
</div>
<!-- 分享 -->
<div class="sharBox" >
    <div class="sharbox"style="display: none">
        <div class="mask" style="display: none;height:auto;">
            <div class="bsf"><img src="/web/res/image/covers.png"></div>
            <p>请点击右上角</p>

            <p>通过【发送给朋友】</p>

            <p>邀请好友参与</p>
        </div>
        <div class="sharingBox">
            <div class="sharing" >
                <h1 class="contitle">课程创建成功分享到</h1>
                <div class="sharing_box">
                    <p class="wx" onclick="statisticsBtn({'button':'005','referer':'005006','courseId':${courseId}})">微信</p>
                    <p class="pyq"onclick="statisticsBtn({'button':'006','referer':'006006','courseId':${courseId}})">朋友圈</p>
                    <p class="yqk"onclick="statisticsBtn({'button':'007','referer':'007004','courseId':${courseId}})">邀请卡</p>
                </div>
                <div class="sharing_btn">取消</div>
            </div>
        </div>
    </div>
</div>

<!-- 开课 -->
<div class="newtapBox" >
    <div class="newtap newtaps">
        <p class="relayBtn" data-relay="true">
            <img src="/web/res/image/icon_relay@1.png" />
            <span>设置转播</span>
        </p>
        <p class="editBtn">编辑课程</p>
        <p class="buyclassbtns enterLiveroom">下载APP</p>
    </div>
</div>
<%--设置转播--%>
<div class="relay_box">
    <div class="relay_list">
        <div class="freeofcharge">
            <span class="tit">转播类型
            <em>保存后不能更改转播价格和分成比例</em></span>
            <ul class="frecharBox">
                <li id="relayOK">允许所有用户转播<p class="newdistribution"></p></li>
                <li style="display: none">转播价格 (元)<input id="chargeAmt" type="number" maxlength="5" onKeyUp="amount(this)" placeholder="不输入即为免费转播" validate="money" value="0" class="newMuch"></li>
                <li style="display: none" class="fourdistribution">转播人所获分成比例 (%)<input type="tel" value="0" placeholder="请输入0-100之间的整数" value="0" id="separateInto" class="distribution"></li>
            </ul>
        </div>
        <div class="relay_btn">
            <p class="relay_c">取消</p>
            <p class="relay_ok">确定</p>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script>
    $(function(){
        /*直播 精选 下拉加载*/
        $('#cont_box').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if(scrollTop + windowHeight >= scrollHeight-1){
                Load();
            }
            if (scrollTop>380){
                $(".invite").css("left","10rem");
            }else{
                $(".invite").css("left","0");
            }

        });
        if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
            $(".livebroadcast").css("transform","translate(0,0)");

        }
    });
    //调取分享蒙层
    setTimeout(function(){
        var location = window.location.href;
        if(location.indexOf("shareOK") > 0){
            $(".sharbox").show();
            $(".contitle").show();
        }
    },30);
    function toDou(n){
        return n>=10?n:'0'+n;
    };
    function countDown(oS,id){
        oS = oS/1000;
        var timer = setInterval(function(){
            oS--;
            d = parseInt(oS / 86400);
            //天
            h=parseInt((oS%86400)/3600);
            //分
            m=parseInt(((oS%86400)%3600)/60);
            //秒
            s=parseInt(((oS%86400)%3600)%60);
            $(id).html('距离开播还有：'+d+' 天 '+toDou(h)+' 小时 '+toDou(m)+' 分 '+toDou(s)+' 秒');

            if(d<1){
                $(id).html('距离开播还有：'+toDou(h)+' 小时 '+toDou(m)+' 分 '+toDou(s)+' 秒');
            }

            if(oS<0){
                //倒计时结束
                clearInterval(timer);
                window.location.reload();
                $(".sharbox").hide();
                return;
            }
        },1000);
    };
    var courseId = "${courseId}";//课程ID
    var seriesid = "${seriesid}";//课程ID
	var imgList = [];
    var liveTimeStatus;
    var setstange ;
    var isRelay; //是否设置转播
    var relayCharge; // 转播金额
    var relayScale ; //转播比例
    var isOpened; //是否开启过转播
    var chargeAmt;
    var relayOff = false;
    var isMyCourse = '';
    $(function () {
        var result = ZAjaxRes({url: "/course/getCourseInfo?id=" + courseId+"&seriesid="+seriesid, type: "GET"});
        if (result.code == "000000") {
            var data = result.data;
            var course = data.course;
            var liveway = course.liveWay;  // 0-视频直播 1-语音直播
            var isWechatOfficial=result.data.isWechatOfficial;
            classId = course.appId;
            var startTime=course.proTimeH5; //距离开课的时间戳
            var distribution = data.course.distribution;
            var stuDisAmount = data.course.stuDisAmount;
            var isPay = data.course.isPay;
            isRelay = data.course.isRelay;
            relayCharge = data.course.relayCharge;
            relayScale = data.course.relayScale;
            chargeAmt = data.course.chargeAmt;
            isOpened = data.course.isOpened;
            if(isRelay == '1'){
                relayOff = true;
            }
            $("#liveTopic").text(course.liveTopic);//课程标题
            $(".opentimes").text("开课时间：" + course.startTime);
            $('#COVERSS_ADDRESS').css('background-image','url('+course.coverssAddress+')'   );
            liveTimeStatus = course.liveTimeStatus;//直播时间状态 0-预告 1-直播中 2-结束的
            var liveTimeStatusDesc = "";
                $(".editBtn").removeClass("on");
            if(liveway=="1"){
                $(".tladdress").hide();
                $(".buyclassbtns ").html("进入直播间");
                $(".buyclassbtns").click(function () {
                    $(".settzzc").hide();
                    if(isWechatOfficial=="0"){
                        window.location.href = "/weixin/index.user?courseId=" + courseId;
                    }else{
                        $(".publicPopups").show();
                    }
                })
            };
            if(startTime>0){
                countDown(startTime, "#startTime");
            };
            if(isPay == 1 && distribution != ''){
                $('.distribution_m').eq(0).children('i').html(distribution+'%');
                $('.distribution_m').eq(1).children('i').html(stuDisAmount);
            }else if(isPay == 0){
                $('.distribution').hide();
            }
            setstange = courseId+classId;
            var getOnce = BaseFun.GetStorage("onceInto");
//            设置转播
            if(liveTimeStatus == '1' || liveTimeStatus == '0'){
                if(seriesid == '' || seriesid == '0'){
                    $('.relayBtn').show();
                }
            }
            if(liveTimeStatus == '1' && liveway == '1'){
                liveTimeStatusDesc = "<p class='inplay'><img src='/web/res/image/playnow.png'><span class='newTexts'>开始直播</span></p>";
            } else if (liveTimeStatus == '0' && liveway == '1') {
                $(".editBtn").removeClass("on");
                liveTimeStatusDesc = "<p class='broadcastPic'><img src='/web/res/image/broadcastPic.png'><span class='newTexts'>直播未开始</span></p><p id='startTime'></p>";
            } else if (liveTimeStatus == '2') {
                liveTimeStatusDesc = "<p class='lookBack'><img src='/web/res/image/playnow.png'><span class='newTexts'>开始播放</span></p>";
//                $(".editBtn").addClass("on");
//                $(".editBtn").unbind("click");
//                if(liveway == '0'){
                    if(seriesid == '0'|| seriesid == ''){
                        $('.editBtn').html('设置转播').attr('data-relay','true');
                    }
//                }
                $('.playback_icon').show();
            }
            $(".ygbox").html(liveTimeStatusDesc);

            if(getOnce==setstange && liveway == '1'&&liveTimeStatus=="1"){
               $(".newTexts").html("进入直播");
            }
            var endTime = course.endTime;//结束时间
            if (endTime) {
                $(".ygbox").on('click','.lookBack',function () {
                    window.location.href = "/weixin/index.user?courseId=" + courseId;
                });
            }
            //点击直播封面图
            $(".ygbox").click(function () {
                BaseFun.SetStorage('onceInto',setstange);
                if(liveway == '0'&& isWechatOfficial == "1"){
                    $(".publicPopups").show();
                }else if(liveway == '1'){
                    window.location.href = "/weixin/index.user?courseId=" + courseId + "&invitationAppId="+'${invitationAppId}';
                }
            });
            if(result.data.course.id.toString().length >= 15){
                if(seriesid == '' || seriesid == '0'){
                    isMyCourse = '<i class="relay_icon"></i>';
                }
                $('.newtapBox,.tladdress,.distribution').remove();
            }else{
                isMyCourse= "";
            }
            $(".videotitle").html(isMyCourse+course.liveTopic);//标题
            $("#visitCount").html(course.visitCount);
            $("#studyCount").html(course.studyCount);
            $("#commentCount").html(data.commentCount);
            $("#teachCourseCount").html(data.teachCourseCount);
            $("#studyAllCount").html(data.studyAllCount);
            $(".topteacherbox i").html(course.userName+"的直播间");
            $(".btomteacherbox").html(course.roomRemark);
            $("#sequence").html(data.seriesCourseCount);
            $(".teacherlog").attr("style", "background: url(" + course.photo + ") no-repeat center center; background-size:100% 100% ")
            $(".teacherzl").click(function () {
                window.location.href = "/weixin/liveRoom?id=" + course.roomId;
            });
            if(seriesid !=0)
            {
                $(".crowd").html('');
                $("#crowdtitle").hide();
            }else{
                $(".crowd").html(replaceTeturn2Br(course.targetUsers) == "" ? "学生" : replaceTeturn2Br(course.targetUsers));
            }
            var CHARGE_AMT = course.chargeAmt;
            if (CHARGE_AMT > 0) {
                $(".usernub").html('<i class="weight">'+CHARGE_AMT+'</i>' + " 学币");
                $(".invite").html("分享赚钱");
                if(result.data.course.id.toString().length >= 15){
                    $(".invite").html("邀请好友");
                }
            }
            if (CHARGE_AMT =="0.00") {
                $(".invite").html("邀请好友");
                $(".usernub").addClass("on");
            }
            var isJoin = data.isJoin;//支付状态
            var mobile = result.ext;//是否已设置手机号
            $(".buyclassbtns").attr("mobile", mobile);
            $(".buyclassbtns").attr("isJoin", isJoin);
            $(".buyclassbtns").attr("chargeAmt", CHARGE_AMT);



//            if (endTime != '') {
//                $(".buyclassbtns").html("进入直播间/回放").addClass('jumpCourse');
//            } else {
//                $(".buyclassbtns").html("下载APP").addClass('xiazaiAPP');
//            }
            //课件展示
            /* var courseWaresList = data.courseWaresList;
             $.each(courseWaresList, function (i, n) {
             $("#courseWaresList").append("<img src=" + n.address + " alt=''/>");
             });*/
                //课件展示
                var courseWaresList = data.courseWaresList;
                if(courseWaresList.length>0){
                    $("#courseWare").show();
                    $.each(courseWaresList, function (i, n) {
                        $("#courseWaresList").append("<img src=" + n.address + " alt=''/>");
						imgList.push(n.address);
                    });
                }else{
                    $("#courseWare").hide();
                    $("#courseWaresList").hide();
                }
                if (data.myLiveRoom == 1) {
                    $(".follow").remove();
                    //如果进入自己的直播间没有关注功能
                }
                $("#user_follow").text(data.followNum);//获取点赞数量
            var pushFlow = data.pushAddress;
            if(pushFlow == ""){pushFlow = '暂无链接'};
            $(".pushFlow").html(replaceTeturn2Br(pushFlow))
            //课程介绍图片
            var courseRemark = course.remark;
            var courseImgList = data.courseImgList;
            if(courseImgList<=0){
                $("#courseImgList").hide()
                if(courseRemark == ""){courseRemark = '暂无简介'};
            }
            $(".livetext").html(replaceTeturn2Br(courseRemark));
            if(courseRemark == ""){
                $(".livetext").hide();
            };
            $.each(courseImgList, function (i, n) {
				if(n.address==''){
					$("#courseImgList").append('<div class="imglist"><p>'+n.content+'</p></div>');
				}else{
					$("#courseImgList").append('<div class="imglist"><img src="' + n.address + '"><p>'+n.content+'</p></div>');
				}
            });
        }
//        setTimeout(function(){
//            var bd = document.getElementById("tabBox1-bd");
//            bd.parentNode.style.height = bd.children[0].children[0].offsetHeight+"px";
//        },300);
        share_h5({systemType: 'COURSE', liveRoomId: courseId , seriesid : seriesid});//分享
    });

//    $(".buyclassbtns").click(function () {
//        if($(".buyclassbtns").hasClass("jumpCourse")){
//            window.location.href = "/weixin/index.user?courseId=" + courseId;
//        }else{
//            $(".settzzc").show();
//        }
//    });
    $(".buyclassbtns").click(function () {
            $(".settzzc").show();
    });
    //	转播
    $('#relayOK').click(function(){
        $(this).children('.newdistribution').toggleClass('on');
        if(isOpened == '1'){
            $('#chargeAmt').val(relayCharge);
            $('#separateInto').val(relayScale);
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
        console.log($('.weight').html());
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
    $('.relay_ok').click(function(){
            Required();
    })
//    取消转播
    $('.relay_c').click(function () {
        $('.relay_box').hide();
        $('.bjyBox').show();
        $('body').css('overflow',' visible');
        if(isOpened == '0'){
            if(isRelay == '0'){
                $('.newdistribution').removeClass('on');
                $('#relayOK').nextAll().hide();
            }
            $("#chargeAmt").val('');//转播价格
            $("#separateInto").val('');//转播人所获分成比例
        }else{
            if(isRelay == '0'){
                $('.newdistribution').removeClass('on');
                $('#relayOK').nextAll().hide();
            }
        }
    })
    $(".settzzc").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest("a").length == 0) {
            $(".settzzc").hide();
        }
    });
    $(".relay_box").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".freeofcharge,.relay_btn").length == 0) {
            $('.relay_box').hide();
            $('.bjyBox').show();
            $('body').css('overflow',' visible');
            $('.newdistribution').removeClass('on');
            $('#relayOK').nextAll().hide();
            if(isOpened == '0'){
                $("#chargeAmt").val('');//转播价格
                $("#separateInto").val('');//转播人所获分成比例
            }
        }
    });
    function Required(){
        var regex = /^100$|^(\d|[1-9]\d)$/;
        var relayMoney = $("#chargeAmt").val();//转播价格
        var separateInto = $("#separateInto").val();//转播人所获分成比例
        //验证金额
        if($('#relayOK .newdistribution').hasClass('on')) {
            if (relayMoney <= 0 && relayMoney !== '0'&&relayMoney != '') {
                pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
                return;
            } else {
                if (relayMoney > 10000) {
                    pop({"content": "请输入正确金额 (0-10000)", "status": "error", time: '2500'});
                    return;
                }
            }
            // 验证分成比例
            if ($(".newdistribution").hasClass("on") && separateInto!= '') {
                if(!/^(\+|-)?\d+$/.test( separateInto )){
                    pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
                    return;
                }
                if (separateInto < 0 && !regex.test(separateInto)&&relayMoney != '') {
                    pop({"content": "请输入正确分成比例 (0-100)", "status": "error", time: '2500'});
                    return;
                } else if (separateInto > 100 && !regex.test(separateInto)&&relayMoney != '') {
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
        var newdistribution = $('.newdistribution').hasClass('on');
        var isRelay = 0;
        if(newdistribution){
            isRelay = 1;
        }
        relayCharge = relayMoney;
        relayScale = separateInto;
        var params = {
            "courseId": courseId,
            "isRelay":isRelay,
            "relayCharge":relayMoney,
            "relayScale":separateInto
        };
        var result = ZAjaxRes({url: "/course/updateCourseRelayInfo.user", type: "GET", param: params});
        if(result.code == "000000"){
            $('.relay_box').hide();
            $('.bjyBox').show();
            $('body').css('overflow',' visible');
            if($('.newdistribution').hasClass('on')){
                relayOff = true;
                isRelay = 1;
                isOpened = 1;
            }else{
                relayOff = false;
            }
//            $('.newdistribution').removeClass('on');
//            $('#relayOK').nextAll().hide();
//            $("#chargeAmt").val('');//转播价格
//            $("#separateInto").val('');//转播人所获分成比例
            pop1({"content": "设置成功" , "status": "normal", time: '2000'});
        }else if(result.code == '10506'){
            $('.relay_box').hide();
            $('.bjyBox').show();
            $('body').css('overflow',' visible');
            if($('.newdistribution').hasClass('on')){
                relayOff = true;
            }else{
                relayOff = false;
            }
        }
    }
    //移到上面去了,根据课程状态判断
//    $(".ygbox").click(function(){
//        window.location.href = "/weixin/index.user?courseId=" + courseId;
//    })
    //去开课
//    $(".openclass").click(function () {
//        window.location.href = "/weixin/liveRoom?sourseId=1";
//    });


    //Tab切换
    var judge = false;
    var clickCommentCoumt = 0;
    /*TouchSlide( { slideCell:"#wrapper",titCell:".livetitle li a",
        endFun:function(i){ //高度自适应
            if(i==1){
                if(clickCommentCoumt <= 0) addCommentList();
                clickCommentCoumt= 1;
                judge = true;
                $('.newtap').hide();
                $('.evaluateBox').show();
                $('.pullUpLabel').hide();
                if(offset != '0'){
                    $('.pullUpLabel').show();
                }
                $(".support").hide();
            }else{
                judge = false;
                $('.newtap').show();
                if (liveTimeStatus == '2') {
                    $('.newtap').hide();
                    $("#cont_box").css("padding-bottom","0");
                }
                $('.evaluateBox').hide();
                $('.pullUpLabel').hide();
                $(".support").show();
            }
            var bd = document.getElementById("tabBox1-bd");
            bd.parentNode.style.height = bd.children[i].children[0].offsetHeight+"px";
            bd.parentNode.style.height = bd.children[0].children[0].offsetHeight+"px";

        }
    });*/
    /*下拉加载*/
    var oBok = true;
    function Load() {
        if (!judge) {
            $('.pullUpLabel').hide();
            return;
        }
        if (oBok) {
            addCommentList();
            var bd = document.getElementById("tabBox1-bd");
            bd.parentNode.style.height = bd.children[1].children[0].offsetHeight+"px";
        }else{
            if(offset!="0"){
                $('.pullUpLabel').text('数据加载完毕');
            }else{
                $('.pullUpLabel').hide();
            }
        }
    }
    var  pageSize = 5;
    /**
     *评论列表
     */
    var offset = 0;
    function addCommentList(){
        var result = ZAjaxRes({
            url: "/courseComment/getCommentListByCourseId?courseId=" + courseId  + "&offset=" + offset +   "&pageSize=" + pageSize,
            type: "GET"
        });
        if(result.code == '000000'){
            var  recordList = result.data;
            if (recordList.length > 0) {
                offset = recordList.length + offset;
                $.each(recordList, function (i, n) {
                    $("#commentList .con").append(' <div class="pjbox">' +
                            ' <div class="pjxq">' +
                            ' <img src="' + n.photo + '">' +
                            ' <p class="pjright">' +
                            ' <i class="pagename">' + n.name + '</i>' +
                            ' <i class="pagetime"> ' + n.createTime + '</i>' +
                            ' <span class="pagetext">' + n.content + '</span>' +
                            ' </p>' +
                            '</div>' +
                            '</div>');
                });
            }else {
                if (offset == '0') {
                    $("#commentList .con").append('<div class="noData"></div>');
                    $('.pullUpLabel').hide();
                }
                oBok = false;
            }
        }else {
            if (offset == '0') {
                $("#commentList .con").append('<div class="noData"></div>');
                $('.pullUpLabel').hide();
            }else{
                $('.pullUpLabel').text('数据加载完毕');
            }
            oBok = false;
        }
    };
    //流量充值
    $(".no").click(function(){
        $(".publicPopups").hide();
    })
    $(".yes").click(function(){
        $(".publicPopups").hide();
        window.location.href="/weixin/flowRecharge";
    })
    //点击分享
    $(".bjyBox").click(function () {
        statisticsBtn({'button':'004','referer':'004001','courseId':${courseId}})
        window.location.href = "/weixin/inviCard?courseId=" + courseId+"&seriesid="+seriesid;
    });
    $(".bjy").click(function(){
        $(".invite").show(1500);
    })
    $(".wx").on("touchstart",function(){
        $(this).addClass("wx1");
        $(".mask").show();
        share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
    })
    $(".wx").on("touchend",function(){
        $(this).removeClass("wx1")
    })
    $(".pyq").on("touchstart",function(){
        $(this).addClass("pyq1");
        $(".mask").show();
        share_h5({systemType: 'COURSE', liveRoomId: courseId});//分享
    })
    $(".pyq").on("touchend",function(){
        $(this).removeClass("pyq1")
    })
    $(".yqk").on("touchstart",function(){
        $(this).addClass("yqk1");
    })
    $(".yqk").on("touchend",function(){
        $(this).removeClass("yqk1");
        window.location.href = "/weixin/inviCard?courseId=" + courseId+"&seriesid="+seriesid;
    })
    $('.sharing_btn').click(function(){
        $('.sharbox').hide();
        $(".mask").hide();
    });
    $(".sharbox").bind("click",function(e){  //点击对象
        var target  = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if(target.closest(".sharing").length == 0){
            $(".sharbox").hide();
            $(".mask").hide();
        }
    });
//    $(".soursize").click(function () {
//        if ($(this).find(".signback").hasClass("on")) {
//            $(this).find(".signback").removeClass("on");
//            $(this).find(".follows").html("关注")
//        } else {
//            $(this).find(".signback").addClass("on");
//            $(this).find(".follows").html("已关注")
//        }
//    })

        //跳转编辑课程界面//设置转播
        $(".editBtn,.relayBtn").click(function(){
            if($(this).attr('data-relay') == 'true'){
                $('body').css('overflow',' hidden');
                $('.bjyBox').hide();
                if(isRelay == '1'){
                    if(relayOff == true){
                        $('.newdistribution').addClass('on');
                        $('#chargeAmt').val(relayCharge);
                        $('#separateInto').val(relayScale);
                        $('.relay_ok').removeClass('on');
                        if($('.weight').html() == undefined){
                            $('.fourdistribution').hide();
                        }
                    }else{
                        $('.newdistribution').removeClass('on');
                        $('#chargeAmt,#separateInto').val('');
                    }
                }
                if(isOpened == '1'){
                    if($('.newdistribution').hasClass('on')){
                        $('#relayOK').nextAll().show();
                        $('#chargeAmt,#separateInto').attr("disabled","disabled");
                    }else{
                        $('#relayOK').nextAll().hide();
                        $('#chargeAmt,#separateInto').attr("disabled","disabled");
                    }
                }else{
                    if($('.newdistribution').hasClass('on')){
                        $('#relayOK').nextAll().show();
                    }else{
                        $('#relayOK').nextAll().hide();
                    }
                }
                if(chargeAmt == '0.00'){
                    $('.fourdistribution').hide();
                }
                $('.relay_box').show();
            }else if(seriesid==0){
                window.location.href = "/weixin/evaluation?id=" + courseId;
            }else{
                window.location.href = "/weixin/editSeriesSingleCourse?courseId=" + courseId+"&seriesid="+seriesid;
            }
        })
//	window.onload=function(){
//		var bd = document.getElementById("tabBox1-bd");
//		bd.parentNode.style.height = bd.children[0].children[0].offsetHeight+"px";
//	};
    function amount(th){
        var temp = /^\d+\.?\d{0,2}$/;
        if (temp.test(th.value)) {
        } else {
            th.value = th.value.substr(0, th.value.length - 1);
            // return false;
        }
    }
//课件图片点击放大
$(function(){
	$('#courseWaresList').on('click', '#courseWaresList  img', function () {
		var imgsrc = $(this).attr('src');
		wx.previewImage({
			current: imgsrc,
			urls: imgList
		});
	});
    });
</script>
</html>
