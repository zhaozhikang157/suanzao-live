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
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/vconsole.min.js?nd=<%=current_version%>"></script>
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
<div class="invitationImage" style="display:none;">
    <div class="inverImagebox"><img id="roomCard" src="/web/res/image/roomCard_4.jpg" alt=""/></div>
</div>
<div class="newinvitationBox">
    <div class="invitationInput">
        <h2>课程邀请码</h2>
        <div class="midInput"><input type="tel" class="courseInput"  maxlength="16"  placeholder="请输入课程邀请码"><p class="invBtn"></p></div>
        <p class="Taptxt"></p>
        <ul class="invitationUl">
            <li class="newcance">取消</li>
            <li class="newcha">确认</li>
        </ul>
    </div>
</div>
<!-- 课程详情 -->
<%--<div class="paymentbox rechartbox" style="display: none;">--%>
    <%--<div class="rechartpop" style="display: none">--%>
        <%--<img class="Jujubepic" src="/web/res/image/Jujube.png" alt=""/>--%>

        <%--<p id="confirm_pay">您确认要支付1学币购买该课程?</p>--%>

        <%--<div class="choice">--%>
            <%--<span class="cancel">取消</span>--%>
            <%--<span class="purchase">确定</span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--&lt;%&ndash;<div class="purchasesuccess" style="display: none;">&ndash;%&gt;--%>
        <%--&lt;%&ndash;<img class="purchasesuccesspic" src="/web/res/image/purchasesuccess.png" alt=""/>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<i class="purchasesuccessBtn">购买成功</i>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
<%--</div>--%>
<div class="exchange">
    兑换成功
</div>
<div class="weChatbox">
    <div class="weCont">
          <p>点击菜单“<span>发送给朋友</span>”</p>
          <p>分享到任意微信好友/群</p>
          <p>即可观看</p>
        <img src="/web/res/image/jtpic.png" alt=""/>
    </div>
</div>
<div class="weChatNumberbox">
    <div class="weNumbercont">
        <h1>分享成功</h1>
        <p id="tip">还有<span id="nowCount">1</span>次，请继续分享</p>
        <div id="iKnow" style="display: none" class="weBtn">我知道了</div>
    </div>
</div>
<%--<div class="paymentmethod" >--%>
<%----%>
<%--</div>--%>
<!--正在直播 -->
<div class="useryz">
    <div class="sfyzbox">
        <h2>身份验证</h2>
        <ul class="room_inf">
            <li>
                <input placeholder="请输入手机号" validate="mobile" class="text mobile" name="mobile" type="text">
            </li>
            <li>
                <input placeholder="请输入验证码" validate="verification" class="text code" name="code" type="text">

                <div class="submitYzm">获取验证码</div>
            </li>
        </ul>
        <div class="errorMessages zzcan"></div>
        <p>
            <input type="button" class="rushe" value="取消" onclick="closeRegister();">
            <input type="button" class="clios bgcol_c80" value="确定">
        </p>
    </div>
</div>
<div class="livebroadcast" >
    <div id="cont_box" >
        <div class="sicree" >
            <div class="livevideo yupic" id="COVERSS_ADDRESS"
                 style="background-size:cover; background-position:center center;">
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
                                        <p class="videotitle"></p>
                                        <span class="usernub">免费</span>
                                    </div>
                                    <p class="openTimebox">
                                        <span class="opentimes"></span>
                                        <em class="signback"></em>
                                    </p>
                                </div>
                            </div>
                            <ul class="subnavigation">
                                <li><a>想学 <span id="visitCount">0</span></a></li>
                                <div class="partition"></div>
                                <li><a>学习 <span id="studyCount">0</span></a></li>
                                <%--<li><a>评论 <span id="commentCount">0</span></a></li>--%>
                            </ul>
                        </div>
                        <div class="teacherxq" onclick="enters()">
                            <div class="teacherzl">
                                <div class="teacherlog"></div>
                                <div class="thname">
                                    <p class="topteacherbox"><i></i></p>
                                    <%--<p class="btomteacherbox"></p>--%>
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
                        <p class="pucmode">
                            <span>课程简介</span>
                        </p>

                        <div class="logbox">
                            <div class="livetext"></div>
                            <div id="courseImgList"></div>
                        </div>
                        <%--<p class="pucmode" id="courseWare" style="display: none;">--%>
                        <%--<span>课件图片</span>--%>

                        <%--<div id="courseWaresList"></div>--%>
                        <%--</p>--%>
                        <%--<p class="pucmode">--%>
                        <%--<span>老师介绍</span>--%>
                        <%--</p>--%>
                        <%--<p class="pucmode nextpum" id="crowdtitle"><span class="crowdtitle">适宜人群</span></p>--%>
                        <%--<div class="logbox">--%>
                        <%--<i class="crowd"></i>--%>
                        <%--</div>--%>
                    </div>
                </div>

                <div id="commentList" style="display:none; margin-top:.55rem; padding-left:.5rem; background:#FFF;">
                    <div class="con" style="min-height:20rem; overflow:hidden;">
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
<div class="sharBox">
    <div class="sharbox" style="display: none">
        <div class="mask" style="display: none;height:auto;">
            <div class="bsf"><img src="/web/res/image/covers.png"></div>
            <p>请点击右上角</p>

            <p>通过【发送给朋友】</p>

            <p>邀请好友参与</p>
        </div>
        <div class="sharing">
            <div class="sharing_box">
                <p class="wx" onclick="statisticsBtn({'button':'005','referer':'005002','courseId':${courseId}})">微信</p>

                <p class="pyq" onclick="statisticsBtn({'button':'006','referer':'006002','courseId':${courseId}})">
                    朋友圈</p>

                <p class="yqk" onclick="statisticsBtn({'button':'007','referer':'007002','courseId':${courseId}})">
                    邀请卡</p>
            </div>
            <div class="sharing_btn">取消</div>
        </div>
    </div>
</div>


<!-- 买课 关注 开课 -->
<div class="newtapBox">
    <div class="newtap">
            <div class="invitationCoud">邀请码</div>
            <p class="buyclassbtns"></p>
    </div>
</div>
<!-- 发表评价 -->
<%--<div class="evaluateBox" style="display:none;">--%>
    <%--<input id="evaluateTxt" name="" type="text" placeholder="">--%>
    <%--<input id="evaluateBtn" name="" type="button" value="评价">--%>
<%--</div>--%>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<script>
    var seriesid = '${seriesid}';
    var isSeries = 0;
    var imgList = [];
    var isInviteCode;
    var trySeeTime = ${course.trySeeTime};
    var isManager = "0";
    var isFollowThirdOfficial = "${isFollowThirdOfficial}";
    //var vConsole = new VConsole();
    var liveTimeStatus="";
    $(function () {
        /*直播 精选 下拉加载*/
        document.querySelector('body').onscroll = function () {
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
//            if (scrollTop + windowHeight >= scrollHeight - 1) {
//                Load();
//            }
            if (scrollTop>380){
                $(".invite").css("left","10rem");
            }else{
                $(".invite").css("left","0");
            }
        };
    });
    var courseId = ${courseId};//课程ID
    var channel = "${channel}";//渠道ID
    var liveRoomId = 0;
    var appId = 0;
    var maxIndex  = 3;
    var nowIndex = 0 ;
    var isMustShare = '${isMustShare}';
    var isMyCourse = '';
    $(function () {
        if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
            $(".livebroadcast").css("transform","translate(0,0)");
        }
         ZAjaxRes({url: "/course/getCourseInfo?id=" + courseId + "&seriesid=" + seriesid, type: "GET",
            callback:function(result) {
                if (result.code == "000000") {
                    var data = result.data;
                    var course = data.course;
                    trySeeTime =course.trySeeTime;
                    var isJoin = data.isJoin;//支付状态
                    isManager = data.isManager;
                    isInviteCode =course.isInviteCode;
                    appId = course.appId;
                    liveRoomId = course.roomId;
                    if(liveRoomId == '1195'){
                        $(".support").hide();
                    }
                    if(isInviteCode==1){
                        $(".invitationCoud").show();
                    }
                    if(result.data.course.id.toString().length >= 15){

                        if(seriesid == '' || seriesid == '0'){
                            isMyCourse = '<i class="relay_icon"></i>';
                        }
                        $(".invitationCoud").hide();
                    }else{
                        isMyCourse=""
                    }
                    isSeries = course.isSeriesCourse;  //1-是系列课0-是单节课
                    var startTime = course.proTimeH5; //距离开课的时间戳
                    var liveTimeStatusDesc;
                    $("#liveTopic").text(course.liveTopic);//课程标题
                    $(".opentimes").text("开课时间：" + course.startTime);
                    $('#COVERSS_ADDRESS').css('background-image', 'url(' + course.coverssAddress + ')');
                         liveTimeStatus = course.liveTimeStatus;//直播时间状态 0-预告 1-直播中 2-结束的
                    if(isManager == '0' && isJoin==''&&trySeeTime>0&& course.chargeAmt!='0.00'&&liveTimeStatus=="0"){
                        liveTimeStatusDesc = "<p class='broadcastPic'><img src='/web/res/image/broadcastPic.png'>进入试看</p><p id='startTime'></p>";
                    }else if(isManager == '0' && isJoin==''&&trySeeTime>0&& course.chargeAmt!='0.00'&&liveTimeStatus!="0"){
                        liveTimeStatusDesc = "<p class='broadcastPic'><img src='/web/res/image/broadcastPic.png'>进入试看</p>";
                    }else{
                        liveTimeStatusDesc = "<p class='inplay'><img src='/web/res/image/playnow.png'>进入直播</p>";
                        if (liveTimeStatus == '0') {
                            liveTimeStatusDesc = "<p class='broadcastPic'><img src='/web/res/image/broadcastPic.png'>直播未开始</p><p id='startTime'></p>";
                        } else if (liveTimeStatus == '2') {
                            liveTimeStatusDesc = "<p class='lookBack'><img src='/web/res/image/playnow.png'>开始播放</p>";
                            $('.playback_icon').show();
                        }
                    }
                    $(".ygbox").html(liveTimeStatusDesc);
                    if(startTime>0){
                        countDown(startTime, "#startTime");
                    };
                    $(".videotitle").html(isMyCourse+course.liveTopic);
                    $("#visitCount").html(course.visitCount);
                    $("#studyCount").html(course.studyCount);
                    $("#commentCount").html(data.commentCount);
                    $("#teachCourseCount").html(data.teachCourseCount);
                    $("#studyAllCount").html(data.studyAllCount);
                    $(".topteacherbox i").html(course.userName+"的直播间");
                    $(".btomteacherbox").html(course.roomRemark);
                    $("#sequence").html(data.seriesCourseCount);
                    $(".teacherlog").css({
                        "background": "url(" + course.photo + ") no-repeat center center;",
                        "background-size": "100% 100%"
                    });
                    $(".teacherlog").click(function () {
                        window.location.href = "/weixin/liveRoom?id=" + course.roomId;
                    });
                    if (seriesid != 0) {
                        $(".crowd").html('');
                        $("#crowdtitle").hide();
                    } else {
                        $(".crowd").html(replaceTeturn2Br(course.targetUsers) == "" ? "学生" : replaceTeturn2Br(course.targetUsers));
                    }
                    var divideScaleDesc = course.divideScaleDesc;
                    var CHARGE_AMT = course.chargeAmt;
                    if (CHARGE_AMT > 0) {
                        if(course.distribution == ''||course.distribution==null){
                            $(".usernub").html('<i class="weight">'+CHARGE_AMT+'</i>' + " 学币");
                            $(".invite").html("分享赚钱");
                        }else{
                            $(".usernub").html('<i class="weight">'+CHARGE_AMT+'</i>' + " 学币");
//                            $(".invite").html("分享赚￥"+course.stuDisAmount);
                            $(".invite").html("分享赚钱");
                        }

                    }
                    if (CHARGE_AMT =="0.00") {
                        $(".usernub").addClass("on");
                        $(".buyclassbtns").hide();
                        $(".invitationCoud").hide();
                        $("#cont_box").css("padding-bottom","0");
                        $(".invite").html("邀请好友");
                    }

                    var mobile = result.ext;//是否已设置手机号
                    var payDesc = "购买课程：" + CHARGE_AMT + "学币";
                    //支付状态 状态0-报名中 1-成功 2-失败 空-没有保密鞥
                    if (isJoin == '1') {
                        payDesc = "进入直播间";
                        $(".newtapBox").hide();
                        $("#cont_box").css("padding-bottom","0");
                        $(".invitationCoud").hide();
                    }
                    $(".buyclassbtns").html(payDesc);
                    $(".buyclassbtns").attr("mobile", mobile);
                    $(".buyclassbtns").attr("isJoin", isJoin);
                    $(".buyclassbtns").attr("chargeAmt", CHARGE_AMT);
                    $(".buyclassbtns").click(function () {

                        coursePaying(this);
                    });
                    //课件展示
                    var courseWaresList = data.courseWaresList;
                    if (courseWaresList.length > 0) {
                        $("#courseWare").show();
                        $.each(courseWaresList, function (i, n) {
                            $("#courseWaresList").append("<img src=" + n.address + " alt=''/>");
                            imgList.push(n.address);
                        });
                    } else {
                        $("#courseWare").hide();
                    }
                    if (data.myLiveRoom == 1) {
                        $(".follow").remove();//如果进入自己的直播间没有关注功能
                    } else {
                        //判断直播室是否关注过
                        if (data.isFollow) {
                            $(".openTimebox").find(".signback").addClass("on");
                        } else {
                            $(".openTimebox").find(".signback").removeClass("on");
                        }
                    }
                    $("#user_follow").text(data.followNum);//获取点赞数量
                    //课程介绍图片
                    var courseImgList = data.courseImgList;
                    var courseRemark = course.remark;
                    if(courseImgList<=0){
                        $("#courseImgList").hide();
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
                            $("#courseImgList").append('<div class="imglist"><img src="' + n.showAddress + '"><p>'+n.content+'</p></div>');
                        }
                    });
                }
            }});

        share_h5({systemType: 'COURSE', liveRoomId: courseId, seriesid: seriesid, channel: channel,return_url:'true'});//分享
    });
    //Tab切换
    var judge = false;
    var clickCommentCoumt = 0;


    /*下拉加载*/
    var oBok = true;
    function Load() {
        if (!judge) {
            $('.pullUpLabel').hide();
            return;
        }
        if (oBok) {
           // addCommentList();
            var bd = document.getElementById("tabBox1-bd");
            bd.parentNode.style.height = bd.children[1].children[0].offsetHeight + "px";
        }else{
            if(offset!="0"){
                $('.pullUpLabel').text('数据加载完毕');
            }else{
                $('.pullUpLabel').hide();
            }
        }
    }
    var pageSize = 5;
    /**
     *评论列表
     */
    var offset = 0;
    function addCommentList() {
        var result = ZAjaxRes({
            url: "/courseComment/getCommentListByCourseId?courseId=" + courseId + "&offset=" + offset + "&pageSize=" + pageSize,
            type: "GET"
        });
        if (result.code == '000000') {
            var recordList = result.data;
            if (recordList.length > 0) {
                offset = offset + recordList.length;
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
            } else {
                if (offset == '0') {
                    commentList.append('<div class="noData"></div>');
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
    }
    ;
    function enters() {
        window.location.href = "/weixin/liveRoom?id=" + liveRoomId;
    }
    //点击分享
    $(".bjy").click(function () {
        $(".invite").show();
        statisticsBtn({'button': '004', 'referer': '004001', 'courseId':${courseId}})
    });
    $(".wx,.pyq").click(function () {
        $(".mask").show();
    })
    $('.sharing_btn').click(function () {
        $('.sharbox').hide();
        $(".mask").hide();
    });

    $(".sharbox").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".sharing").length == 0) {
            $(".sharbox").hide();
            $(".mask").hide();
        }
    });


    $("#evaluateBtn").click(function () {

        var content = $("#evaluateTxt").val();
        var commentCount = parseInt($("#commentCount").html());
        if ($.trim(content) == "" || content == null) {
            alert("评价不能为空！")
            return false;
        }
        var result = ZAjaxJsonRes({
            url: "/courseComment/evaluateServiesCourse.user?courseId=" + courseId + "&seriesId=" + seriesid + "&content=" + content,
            type: "post"
        });
        $("#commentCount").html(commentCount + 1);
        var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
        if (result.code == "000000") {
            $('#commentList .con').prepend(' <div class="pjbox">' +
                    ' <div class="pjxq">' +
                    ' <img src="' + result.data.headPhoto + '">' +
                    ' <p class="pjright">' +
                    ' <i class="pagename">' + result.data.userName + '</i>' +
                    ' <i class="pagetime">' + getNowFormatDate() + '</i>' +
                    ' <span class="pagetext">' + content + '</span>' +
                    ' </p>' +
                    '</div>' +
                    '</div>');
            $("#evaluateTxt").val("");
//            var bd = document.getElementById("tabBox1-bd");
//            bd.parentNode.style.height = bd.children[1].children[0].offsetHeight + "px";
        }
        var pjlength = $("#commentList .con .pjbox").length;
        if (pjlength > 0) {
            $(".noData").remove();
        }
    });


    function getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                + " " + date.getHours() + seperator2 + date.getMinutes();
        return currentdate;
    }
    var Pc = null;
    var weixinresult = "";
    var courseMoney = 0;
    function coursePaying(obj , flag) {
        var mobile = $(obj).attr("mobile");
        var isJoin = $(obj).attr("isJoin");//1-支付完成  0-未支付或者未成功
        var chargeAmt = $(obj).attr("chargeAmt");
        if (mobile == "" && chargeAmt > 0) {
            if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
                Pc = true;
                window.location.href="/weixin/toLogin";
            }
            pop({"content": "请填写手机号" , "status": "normal", time: '2000'});
            $(".useryz").addClass("on");//请填写手机号！
            return "";
        }
        courseMoney = chargeAmt;
        if (isJoin == '1') {

            if(liveTimeStatus==2){
                window.location.href = "/weixin/index.user?courseId=" + courseId;
            }else{
                window.location.href = "/weixin/index.user?courseId=" + courseId+"&live=1";
            }
        } else {
            var referer = "008001";
            //flag如果是封面图点过来的
            if (flag) {
                referer = "008002";
            }
            statisticsBtn({'button': '008', 'referer': referer, 'courseId':${courseId},'clientType':'weixin'})
            var tempCourseId = courseId;
            if (seriesid > 0) tempCourseId = seriesid;
            var param = {payType: "07", password: "", amount: courseMoney, courseId: tempCourseId, deviceNo: "", isBuy: '0'};
            var result = paying(param);
            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                $(".purchase").attr("pay", "1");
                setTimeout(function () {
                    //购买刷新
                    if(liveTimeStatus==2){
                        window.location.href = "/weixin/index.user?courseId=" + courseId;
                    }else{
                        window.location.href = "/weixin/index.user?courseId=" + courseId+"&live=1";
                    }
                }, 2000);
            }else if (result.code == '100025') {//足够
                //可以支付弹出确认按钮
                BaseFun.Dialog.Config2({
                    title: '提示',
                    text : '您将支付' + courseMoney + '学币购买该课程',
                    cancel:'取消',
                    confirm:'确定',
                    close:false,
                    callback:function(index) {
                        if(index == 1){
                            var param = {payType: "07", password: "", amount: courseMoney, courseId: tempCourseId, deviceNo: "", isBuy: '1' ,invitationAppId:'${invitationAppId}'};
                            var result = paying(param);
                            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                                pop1({"content": "购买成功" , "status": "normal", time: '1500'});
                                $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                                $(".purchase").attr("pay", "1");
                                setTimeout(function () {
                                    //购买刷新
                                    if(liveTimeStatus==2){
                                        window.location.href = "/weixin/index.user?courseId=" + courseId;
                                    }else{
                                        window.location.href = "/weixin/index.user?courseId=" + courseId+"&live=1";
                                    }
                                }, 2000);
                            }
                        }
                    }
                });
            } else if (result.code == '100002') {//不足
                //直接支付
                BaseFun.Dialog.Config2({
                    title: '提示',
                    text : '您将支付' + courseMoney + '学币购买该课程',
                    cancel:'取消',
                    confirm:'确定',
                    close:false,
                    callback:function(index) {
                        if(index == 1){
                            var param = {payType: "14", password: "", amount: "", courseId: tempCourseId, deviceNo: "", isBuy: "1",invitationAppId:'${invitationAppId}'};
                            var result = paying(param);
                            isTrySeeFlag = true;
                            onBridgeReady(result);
                            $(".purchase").attr("pay", "0");
                        }
                    }
                });
            }else if(result.code == '100012'){ //枣币可以兑换
                var attch= result.attach
                BaseFun.Dialog.Config2({
                    title: '提示',
                    text : '学币不足，有枣币可兑换',
                    cancel:'去兑换',
                    confirm:'不用，直接买',
                    close:true,
                    callback:function(index) {
                        if(index == 0){
                            window.location.href="/weixin/exchangeCurrency.user?attach="+attch;
                        }else if(index == 1){
                            var param = {payType: "14", password: "", amount: "", courseId: tempCourseId, deviceNo: "", isBuy: "1",invitationAppId:'${invitationAppId}'};
                            var result = paying(param);
                            isTrySeeFlag = true;
                            onBridgeReady(result);
                        }
                    }
                });
            }
        }
    }
    //验证步骤
    $('.useryz .text').on("keyup change", function () {
        var obj = $('.useryz .text');
        var obj2 = $('.clios')
        valT(obj, obj2);
    });
    //点击发送验证码
    $(".submitYzm").click(function () {
        //验证手机号
        $('.errorMessages:visible').html('');
        var num = inputTest($('.mobile'));
        var mobile = $('.mobile').val();
        mobile = $.trim(mobile);
        var result = ZAjaxJsonRes({url: "/aboutUs/getApplySms.user?mobile=" + mobile, type: "GET"});
        if (result.code == "000004") {
            $('.errorMessages:visible').html("请输入手机号");
            return;
        }
        if (result.code == "000006") {
            $('.errorMessages:visible').html("请输入正确的手机号");
            return;
        }
        if (result.code != "000000") {
            $('.errorMessages:visible').html("请输入手机号");
            return;
        }
        if (num && result.code == "000000") {
            //请求发送成功后
            $(".submitYzm").addClass('not_pointer');
            $('.submitYzm').html('发送成功');

            var timer = null;
            timer = setTimeout(function () {
                //60秒重新发送
                var oT = 60;
                timer = setInterval(function () {
                    oT--;
                    $('.submitYzm').html(oT + '秒后重发');
                    if (oT == 0) {
                        clearInterval(timer);
                        $('.submitYzm').html('获取验证码');
                        $(".submitYzm").removeClass('not_pointer');
                    }
                }, 1000);
            }, 500);
        }
    });
    $(".clios").click(function () {
        registerMobile();
        $(".room_inf li input").val("");
    });
    /**
     * 注册手机号
     */
    function registerMobile() {
        var mobile = $("input[name='mobile']").val();
        var param = {mobile: mobile, checkCode: $("input[name='code']").val()};
        var result = ZAjaxRes({url: "/user/registerMobile.user", type: "POST", param: param});
        if (result.code == "000000") {
            sys_pop(result);
            $(".useryz").removeClass("on");//请填写手机号！
            $(".buyclassbtns").attr("mobile", mobile);//设计点击按钮可以购买
            coursePaying($(".buyclassbtns")[0]);
        } else {
            $('.errorMessages:visible').html(result.message);
        }
    }
    function closeRegister() {
        $(".useryz").removeClass("on");//请填写手机号！
        $('.errorMessages:visible').html("");
    }

    function getShareImage() {
        var result = ZAjaxRes({
            url: "/user/invitationCard.user?roomId=" + liveRoomId + "&courseId=" + courseId + "&appId=" + appId,
            type: "GET"
        });
        if (result.code != '000000') {
            sys_pop(result);
            return;
        }
        return result.data.url;
    }

    //关注和取消关注
    $(".signback").click(function () {
        if ($(this).hasClass("on")) {
            follow(this, 1);
            pop1({"content": "取消关注成功" , "status": "normal", time: '2500'});
        } else {
            follow(this, 0);
            pop1({"content": "关注成功" , "status": "normal", time: '2500'});
        }
    })
    function follow(obj, isFollow) {
        if (isFollow == 0) {
            var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + liveRoomId, type: "GET"});
            if (result.code == "000000" || result.code == "000111") {
                $(obj).addClass("on");
                $(obj).find(".follows").html("已关注")
            }
        } else {
            var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + liveRoomId, type: "GET"});
            if (result.code == "000000") {
                $(obj).removeClass("on");
                $(obj).find(".follows").html("关注")
            }
        }
    }
    //点击直播封面图
    $(".ygbox").click(function () {
        statisticsBtn({'button': '025', 'courseId':${courseId},'clientType':'weixin'})
        if(trySeeTime>0 && isManager == '0'){
            window.location.href = "/weixin/index.user?courseId=" + courseId + "&invitationAppId="+'${invitationAppId}'+"&live=1";
        }else{
            coursePaying($(".buyclassbtns")[0] , true);
        }
    });
    //去开课
    $("#openCourse").click(function () {
        statisticsBtn({'button': '001', 'referer': '001003', 'courseId':${courseId}})
        window.location.href = "/weixin/liveRoom?sourseId=1";
    });

    $(".closebtn").click(function () {
        $(".paymentbox").hide();
        $(".choice").find("i").removeClass("on");
        $("#inputBoxContainer input").val("");
        $(".mmbox").hide();
        $(".buyclassbtns").html("加入课程：" + courseMoney + "学币");
    })

    $(".reports").click(function () {
        $(".ewmbox").addClass("on");
    })
    $(".ewmbox").on("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".sicx,.kfewm").length == 0) {
            $(".ewmbox").removeClass("on");
        }
    });

    //appId : 该课程老师的ID
    $(".yqk").click(function () {
        window.location.href = "/weixin/inviCard?courseId=" + getQueryString('id') + "&appId=" + appId + "&type=1&seriesid="+getQueryString('seriesid');
    })


    $(".bjyBox").click(function () {
        window.location.href = "/weixin/inviCard?courseId=" + getQueryString('id') + "&appId=" + appId + "&type=1&seriesid="+getQueryString('seriesid');
    });
    //    充值取消
    $(".cancel").click(function () {
        $(".rechartbox").hide();
    });
    $(".purchasesuccessBtn").click(function () {
        $(".rechartbox").hide();
    });
    //    充值确认
    $(".purchase").click(function () {
        if ($(this).attr("learn_coin_enough") == '1') {
            window.location.href = "/weixin/recharge";
            return;
        }
        var tempCourseId = courseId;
        if (seriesid > 0) tempCourseId = seriesid ;
        var status = $(this).attr("pay");
        if (status == '0') {
            var param = {payType: "14", password: "", amount: '', courseId: tempCourseId, deviceNo: "", isBuy: "1", invitationAppId: '${invitationAppId}'};
            var result = paying(param);
            onBridgeReady(result);
        } else {
            var param = {payType: "07", password: "", amount: courseMoney, courseId: tempCourseId, deviceNo: "", isBuy: '1', invitationAppId: '${invitationAppId}'};
            var result = paying(param);
            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                $(".rechartbox").hide();
                $(".buyclassbtns").hide();
                $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                if(liveTimeStatus==2){
                    window.location.href = "/weixin/index.user?courseId=" + courseId;
                }else{
                    window.location.href = "/weixin/index.user?courseId=" + courseId+"&live=1";
                }
            }
        }
    });
    //避免遮挡输入框
    $('#evaluateTxt').off('click', asmk);
    $('#evaluateTxt').on('click', asmk);
    function asmk() {
        var target = this;
        // 使用定时器是为了让输入框上滑时更加自然
        setTimeout(function () {
            target.scrollIntoView(true);
        }, 300);
    }
    //课件图片点击放大
    $(function () {
        $('#courseWaresList').on('click', '#courseWaresList  img', function () {
            var imgsrc = $(this).attr('src');
            wx.previewImage({
                current: imgsrc,
                urls: imgList
            });
        });
    });

    /**
     *微信支付
     */
    function onBridgeReady(result) {
        if (!result) return;
        var data = result.data;
        var orderId = result.ext;
        WeixinJSBridge.invoke(
                'getBrandWCPayRequest', {
                    "appId": data.appId,     //公众号名称，由商户传入
                    "timeStamp": data.timeStamp,         //时间戳，自1970年以来的秒数
                    "nonceStr": data.nonceStr, //随机串
                    "package": data.package,
                    "signType": data.signType,         //微信签名方式:
                    "paySign": data.paySign    //微信签名
                },
                function (res) {
                    $(".paymentbox").hide();
                    $("#inputBoxContainer input").val("");
                    $(".mmbox").hide();
                    $(".buyclassbtns").html("购买课：" + courseMoney + "学币");
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        $(".buyclassbtns").html("进入直播室");
                        $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                        var result = {
                            code: "000000",
                            message: "支付成功",
                            "successUrl": "/weixin/index.user?courseId=" + courseId
                        };
                        pop1({"content": "支付成功" , "status": "normal", time: '2500'});
                    } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                        var result = {code: "000000", message: "取消支付"};
                        var param = {orderId: orderId};
                        //cancelThirdPay(param);
                        pop1({"content": "取消支付" , "status": "error", time: '2500'});
                    } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                        var param = {orderId: orderId};
                        //cancelThirdPay(param);
                    }
                    // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。         吗没呢，                                ，
                }
        );
    }

</script>
<script>
    function toDou(n) {
        return n >= 10 ? n : '0' + n;
    }
    ;
    function countDown(oS, id) {
        oS = oS / 1000;
        var timer = setInterval(function () {
            oS--;
            d = parseInt(oS / 86400);
            //天
            h = parseInt((oS % 86400) / 3600);
            //分
            m = parseInt(((oS % 86400) % 3600) / 60);
            //秒
            s = parseInt(((oS % 86400) % 3600) % 60);
            $(id).html('距离开播还有：' + d + ' 天 ' + toDou(h) + ' 小时 ' + toDou(m) + ' 分 ' + toDou(s) + ' 秒');
            if (d < 1) {
                $(id).html('距离开播还有：' + toDou(h) + ' 小时 ' + toDou(m) + ' 分 ' + toDou(s) + ' 秒');
            }
            if (oS <= 0) {
                //倒计时结束
                clearInterval(timer);
                window.location.reload();
                return;
            }
        }, 1000);

    };
/*
* 邀请码验证
* */
    var lenInput;
    var titNum;
    $(".courseInput").on("input",function(){
        lenInput = $(this).val().length;
        titNum = $.trim($('.courseInput').val());
        if(lenInput>0){
            $(".newcha").addClass("on");
            $(".invBtn").show();
        }else{
            $(".newcha").removeClass("on");
            $(".invBtn").hide();
            $(".Taptxt").text("");
        }
    });

    $(".newcha").click(function(){
        if($(".newcha").hasClass("on")){
           if(lenInput<16){
               $(".Taptxt").text("请输入正确的邀请码");
               return;
           }else{
               var param = {payType: "16", password: "", amount: "", courseId: courseId, deviceNo: "", isBuy: '1',ext:titNum};
               var result = ZAjaxRes({url: "/thirdPay/pay.user", type: "POST",param:param });
               if (result.code == "000000") {//支付成功或者已经支付
                   $(".newtapBox").hide();
                   $(".courseInput").val("");
                   $(".Taptxt").text("");
                   $(".newinvitationBox").hide();
                   $(".exchange").show();
                   setTimeout(function(){
                       $(".exchange").hide();
                       if(liveTimeStatus==2){
                           window.location.href = "/weixin/index.user?courseId=" + courseId;
                       }else{
                           window.location.href = "/weixin/index.user?courseId=" + courseId+"&live=1";
                       }
                   },2000);
               } else {
                   $(".Taptxt").text(result.message);
               }
           }
        }else{
            return;
        }
    })
    $(".invBtn").click(function(){
        $(".courseInput").val("");
        $(".invBtn").hide();
        $(".newcha").removeClass("on");
        $(".Taptxt").text("");
    });
    $(".invitationCoud").click(function(){
        $(".newinvitationBox").show();
    });
    $(".newcance").click(function(){
        $(".newinvitationBox").hide();
        $(".courseInput").val("");
        $(".newcha").removeClass("on");
        $(".Taptxt").text("");
        $(".invBtn").hide();
    });
    $(".weBtn").click(function(){
        $(".weChatNumberbox").hide();
        $(".weChatbox").hide();
        $(".invitationImage").show();
    });
    $(document).bind("click",function(e){
        var target  = $(e.target);
        if(target.closest(".inverImagebox").length == 0){
            $(".invitationImage").hide();
        }
    });
    function getQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }
</script>
<style>
    /*#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}*/
</style>
</html>
