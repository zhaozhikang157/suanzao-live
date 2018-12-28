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
    <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>
</head>
<body>
<div class="newinvitationBox">
    <div class="invitationInput">
        <h2>课程邀请码</h2>
        <div class="midInput"><input type="tel" maxlength="16" class="courseInput" placeholder="请输入课程邀请码"><p class="invBtn"></p></div>
        <p class="Taptxt"></p>
        <ul class="invitationUl">
            <li class="newcance">取消</li>
            <li class="newcha">确认</li>
        </ul>
    </div>
</div>
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
<div class="exchange">
    兑换成功
</div>
<%--<ul class="newNavs_1">--%>
    <%--<li class="introductionNew_1 on">简介</li>--%>
    <%--<div class="introductionLine_1"></div>--%>
    <%--<li class="introductionClass_1">课程</li>--%>
<%--</ul>--%>
<!-- 课程详情 -->
<div class="paymentbox rechartbox" style="display: none;">
    <div class="rechartpop" style="display: none">
        <img class="Jujubepic" src="/web/res/image/Jujube.png" alt=""/>
        <p id="confirm_pay">您确认要支付1学币购买该课程?</p>
        <div class="choice">
            <span class="cancel">取消</span>
            <span class="purchase">确定</span>
        </div>
    </div>
    <%--<div class="purchasesuccess" style="display: none;">--%>
    <%--<img class="purchasesuccesspic" src="/web/res/image/purchasesuccess.png" alt=""/>--%>
    <%--<i class="purchasesuccessBtn">购买成功</i>--%>
    <%--</div>--%>
</div>
<div class="livebroadcast" id="wrapper" style=" overflow: hidden; -webkit-overflow-scrolling: touch;">
    <div id="cont_box" class="new_index" style="height: 100%;padding-bottom: 0;overflow-y: scroll">
        <input type="hidden" class="numVal">
        <div class="sicree" style="padding-bottom: 2.4rem;">
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
            </div>
            <div class="inforallbox bd" >
                <div class="coursenew">
                    <div class="con">
                        <div class="heightBox">
                            <div style="margin-bottom: 10px">
                                <div class="videotitlebox">
                                    <div class="videotitlebox_bd">
                                        <div class="videomidbox">
                                            <p class="videotitle"></p>
                                            <span class="usernub">免费</span>
                                        </div>
                                      <span class="opentimes">
                                        <em class="kaike">加载中.....</em>
                                      </span>
                                    </div>
                                </div>
                                <ul class="subnavigation">
                                    <li><a>想学 <span id="visitCount">0</span></a></li>
                                    <div class="partition"></div>
                                    <li><a>学习 <span id="studyCount">0</span></a></li>
                                </ul>
                            </div>
                            <div class="teacherxq" onclick="enters()">
                                <div class="teacherzl">
                                    <div class="teacherlog newteacherlog" id="teacherlog"></div>
                                    <div class="thname new_thname">
                                        <p class="newtopteacherbox"></p>
                                    </div>
                                    <span class="newMore"></span>
                                </div>
                            </div>
                            <ul class="teacherlistdetails" style="margin-bottom:10px;margin-top: 1px;">
                                <li>
                                    <i id="studyAllCount"></i>
                                    <span>学生数</span>
                                </li>
                                <div class="partition"></div>
                                <li>
                                    <i id="teachCourseCount"></i>
                                    <span>单节课</span>
                                </li>
                                <div class="partition"></div>
                                <li>
                                    <i id="sequence"></i>
                                    <span>系列课</span>
                                </li>
                            </ul>
                            <ul class="newNavs">
                                <li class="introductionNew on">课程简介</li>
                                <div class="introductionLine"></div>
                                <li class="introductionClass">课程列表</li>
                            </ul>
                            <div class="testHeight"></div>
                            <p class="pucmode pucmodeUp" id="pucmodeUp" style="margin-top: 0">
                                <span>课程简介</span>
                            </p>
                            <div class="logbox" >
                                <div class="livetext">加载中.....</div>
                                <div id="courseImgList">

                                </div>
                            </div>
                        </div>

                        <p class="pucmode" id="classHref" style="margin-top: 10px">
                            <span>课程列表</span>
                        </p>
                        <ul class="sequenceClass publicUlstyle" id="sequenceClass">

                        </ul>
                    </div>
                </div>
                <%--评价--%>
                <div id="commentList" style="display:none; margin-top:.55rem; padding-left:20px; background:#FFF;">
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
<div class="sharBox">
    <div class="sharbox" style="display: none">
        <div class="mask" style="display: none">
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
        <p class="buyclassbtns"><a class="buyOk">购买系列课</a></p>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<script src="/web/res/js/base.js"></script>
<script>
    var seriesid = ${courseId};
    var invitationAppId = '${invitationAppId}';
    var oBoks = true;
    var roomId = "";
    var offset = 0, pageSize = 10;
    var isPay = 2;
    var isInviteCode;
    var seriesAmt ="";
    var isManager = "0";
    var CHARGE_AMT = 0;
    var isClick = false;
    var isMyCourse="";
    var trys ="";
    var isJoin= "";
    $(function () {
        share_h5({systemType: 'COURSE', liveRoomId: seriesid, isSeries: 1, seriesid: 0});//分享
        /*直播 精选 下拉加载*/
        var newNavs= $(".newNavs").offset().top;//浮动标题距离
        var classHrefheight = $("#classHref").height();
        $('#cont_box').scroll(function () {
            var courseTopone = $("#classHref").offset().top; //课程标题距顶高度
            var pucmodeUp = $('#pucmodeUp').offset().top;
            var offsetTop = $(this).offset().top;
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if (scrollTop + windowHeight >= scrollHeight - 1) {
                serisoList();
            }
            if (scrollTop>380){
                $(".invite").css("left","10rem");
            }else{
                $(".invite").css("left","0");
            }

            if(scrollTop+5>newNavs){
                $(".newNavs").css({position:"fixed",top:0});
                $('.testHeight').css('height','2rem');
                $(".bjyBox").css("top","2.1rem");
            }else{
                $(".newNavs").css("position","static");
                $('.testHeight').css('height','');
                $(".bjyBox").css("top",".5rem");
            }
            if(!isClick){
                if($('#sequenceClass>li').length <=6 && $('.numVal').val() == '1'){
                    $(".introductionNew").addClass("on").siblings().removeClass("on");
                }else{
                    if(courseTopone <= $('.newNavs').height()){
                        $(".introductionClass").addClass("on").siblings().removeClass("on");
                    }else{
                        $(".introductionNew").addClass("on").siblings().removeClass("on");
                    }
                }
            }else{
                setTimeout(function(){
                    isClick = false;
                },30);
                return;
            }
        });

        if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
            $(".livebroadcast").css("transform","translate(0,0)");

        }
        $(".introductionNew").click(function(){
            isClick = true;
            $(".numVal").val("0");
            $("#cont_box").scrollTop( newNavs);
            $(".introductionNew").addClass("on").siblings().removeClass("on");

            return false;
        })
        $(".introductionClass").click(function(){
            isClick = true;
            $(".numVal").val("1");
            var boxDiv = $("#cont_box");
            var a1 = $('#COVERSS_ADDRESS').outerHeight(),a2 = $('.heightBox').outerHeight();
            var num = a1+a2+20;
            boxDiv.scrollTop( num-$('.newNavs').height());
            $(".introductionClass").addClass("on").siblings().removeClass("on");
            return false;
        });



        $(function () {
            var result = ZAjaxRes({url: "/series/getSeriesCourseInfo?seriesid=" + seriesid, type: "GET"});
            if (result.code == "000000") {
                var data = result.data;
                var seriesCourse = data.seriesCourse;
                isInviteCode =seriesCourse.isInviteCode;
                appId = seriesCourse.appId;
                roomId = seriesCourse.roomId;
                if(roomId == '1195'){
                    $(".support").hide();
                }
                if(isInviteCode==1){
                    $(".invitationCoud").show();
                }
                if(seriesCourse.id.toString().length >= 15){
                    isMyCourse = '<i class="relay_icon"></i>';
                    $(".invitationCoud").hide();
                }else{
                    isMyCourse=""
                }
                $('#COVERSS_ADDRESS').css('background-image', 'url(' + seriesCourse.coverssAddress + ')');
                $(".videotitle").html(isMyCourse+seriesCourse.liveTopic);
                $("#liveTopic").text(seriesCourse.liveTopic);//课程标题
                $("#visitCount").html(seriesCourse.visitCount);
                $("#studyCount").html(seriesCourse.studyCount);
                $("#commentCount").html(data.commentCount);
                $(".newtopteacherbox").html(seriesCourse.userName + "的直播间");
                $("#studyAllCount").html(data.studyAllCount);
                $("#teachCourseCount").html(data.teachCourseCount);
                $("#sequence").html(data.seriesCourseCount);
                $(".muchclass").html(seriesCourse.chargeAmt + "学币");
                seriesAmt = seriesCourse.chargeAmt;
                $(".kaike").html("已更新" + data.alreadyCourseCount + "节课");
                $("#crowdtitle").html(replaceTeturn2Br(seriesCourse.targetUsers));
                $("#teacherlog").css({
                    "background": "url(" + seriesCourse.photo + ") no-repeat center center",
                    "background-size": "100% 100%"
                });
                CHARGE_AMT = seriesCourse.chargeAmt;
                if (CHARGE_AMT > 0) {
                    if(seriesCourse.distribution == ''||seriesCourse.distribution==null){
                        $(".usernub").html('<i class="weight">'+CHARGE_AMT+'</i>' + " 学币");
                        $(".invite").html("分享赚钱");
                    }else{
                        $(".usernub").html('<i class="weight">'+CHARGE_AMT+'</i>' + " 学币");
//                        $(".invite").html("分享赚￥"+course.stuDisAmount);
                        $(".invite").html("分享赚钱");
                    }
                }
                if (CHARGE_AMT =="0.00") {
                    $(".buyclassbtns").hide();
                    $(".invitationCoud").hide();
                    $(".usernub").addClass("on");
                    $("#cont_box").css("padding-bottom","0");
                    $(".invite").html("邀请好友");
                }
                    isJoin = data.isJoin;//支付状态
                isPay = isJoin;
                var payDesc = "购买系列课：" + CHARGE_AMT + "学币";
                //支付状态 状态0-报名中 1-成功 2-失败 空-没有保密鞥
                if (isJoin == '1') {
//                    payDesc = "进入课程";
                    $(".newtapBox").hide();
                    $(".invitationCoud").hide();
                }
                var mobile = result.ext;//是否已设置手机号
                $(".buyclassbtns").attr("mobile", mobile);
                $(".buyclassbtns a").html(payDesc);
                $(".buyclassbtns").attr("isJoin", isJoin);
                $(".buyclassbtns").attr("chargeAmt", CHARGE_AMT);
                $(".buyclassbtns").click(function () {
                    coursePaying(this);
                });
                //课程介绍图片
                var remark = seriesCourse.remark;
                var courseImgList = data.courseImgList;
                if(courseImgList<=0){
                    $("#courseImgList").hide();
                    if(remark == ""){remark = '暂无简介';}
                }
                $(".livetext").html(replaceTeturn2Br(remark));
                if(remark == ""){
                    $(".livetext").hide();
                    $("#courseImgList").css("margin-top","0");
                };
                $.each(courseImgList, function (i, n) {
                    if(n.address==''){
                        $("#courseImgList").append('<div class="imglist"><p>'+n.content+'</p></div>');
                    }else{
                        $("#courseImgList").append('<div class="imglist"><img src="' + n.address + '"><p>'+n.content+'</p></div>');
                    }
                });
                if (data.myLiveRoom == 1) {
                    $(".follow").remove();//如果进入自己的直播间没有关注功能
                } else {
                    //判断直播室是否关注过
                    if (data.isFollow) {
                        $(".soursize").find(".signback").addClass("on");
                        $(".soursize").find(".follows").html("已关注")
                    } else {
                        $(".soursize").find(".signback").removeClass("on");
                        $(".soursize").find(".follows").html("关注")
                    }
                }
            }
            serisoList();
        });
    });

    function serisoList(){
        if(!oBoks){return}
        oBoks=true;
        if (oBoks) {
            var result = ZAjaxRes({
                url: "/series/getMyCourseList?offset=" + offset + "&pageSize=" + pageSize + "&seriesid=" + seriesid,
                type: "GET"
            });
            if (result.code == "000000") {
                var courseList = result.data;
                if (courseList.length > 0) {
                    $.each(courseList, function (i, n) {
                        var liveWayDesc = "videoIco";
                        var free = "免费";
                        if (n.liveWay == ('语音直播')) liveWayDesc = "audioIco";
                        var free = "免费";
                        var freeClass = "free";
                        if (n.chargeAmt > 0) {
                            free = n.chargeAmt + "学币";
                            freeClass = "";
                        }
                        var count = n.visitCount;
                        var trySeeTime = n.trySeeTime;
                        var li = $('<li c_id=' + n.id + ' s_id=' + seriesid + '>');
                        $("#sequenceClass").append(li);
                        if(trySeeTime>0&&isJoin!="1"){
                            trys='<span class="tescolor">进入试看</span>';
                        }else{
                            trys=""
                        }

                        var liveTimeStatus = n.liveTimeStatus;//直播时间状态 1-预告 2-直播中 0-结束的
                        var liveTimeStatusDesc = "liveingpic";
                        if (liveTimeStatus == '0') {
                            liveTimeStatusDesc = "playbacks";
                        } else if (liveTimeStatus == '1') {
                            liveTimeStatusDesc = "backlookout";
                        }else if(liveTimeStatus == '2'){
                            liveTimeStatusDesc = "liveingpic";
                        }
                        isManager = n.isManager;
                        var div = '<div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
                                '<em class="projection"><i class='+liveTimeStatusDesc+'></i><span class="playNumber">'+ count+'人气</span>' +
                                '<i class='+liveWayDesc+'></i></em></div><p><strong class="title">'+ n.liveTopic+'</strong></p>' +
                                '<p><span class="name">主讲:'+n.userName+'</span></p><p><span class="time">'+  n.startTime+'</span>'+trys+'</p>'
                        li.append(div);
                        li.click(function () {
                            if(n.trySeeTime>0)
                            {
                                window.location.href = "/weixin/index.user?courseId=" + n.id + "&invitationAppId="+invitationAppId;
                            }else
                                courseInfo($(this));
                        })
                    });
                    offset = $("#sequenceClass li").length;
                } else {
                    oBoks = false;
                }
            }else{
                oBoks = false;
                if(offset==0){
                    $("#sequenceClass").append("<div class='textTaps'>暂无课程</div>");
                }
            }
        }
    }



    var courseMoney = 0;
    //购买系列课
    function coursePaying(obj) {
        var mobile = $(obj).attr("mobile");
        var isJoin = $(obj).attr("isJoin");//1-支付完成  0-未支付或者未成功
        var chargeAmt = $(obj).attr("chargeAmt");
        if (mobile == "" && chargeAmt > 0) {
            $(".useryz").addClass("on");//请填写手机号！
            return "";
        }
        courseMoney = chargeAmt;

        if (isJoin == '1') {
            //window.location.href = "/weixin/live/index.user?courseId=" + courseId;
            $(".buyOk").attr("href", "#sequenceClass");
            window.location.reload();
        } else {
            var referer = "008001";
            statisticsBtn({'button': '008', 'referer': referer, 'courseId':${courseId},'clientType':'weixin'});
            var param = {
                payType: "07",
                password: "",
                amount: courseMoney,
                courseId: seriesid,
                deviceNo: "",
                isBuy: '0'
            };
            var result = paying(param);
            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                $(".buyclassbtns").attr("isJoin", '1').hide();//1-支付完成  0-未支付或者未成功
                $(".purchase").attr("pay", "1");
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
                            var param = {payType: "07", password: "", amount: courseMoney, courseId: seriesid, deviceNo: "", isBuy: '1',invitationAppId:window.invitationAppId };
                            var result = paying(param);
                            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                                pop1({"content": "购买成功" , "status": "normal", time: '1500'});
                                $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                                $(".purchase").attr("pay", "1");
                                window.location.reload(true);
                            }
                        }
                    }
                });
            } else if (result.code == '100002') {//不足
                //可以支付弹出确认按钮
                BaseFun.Dialog.Config2({
                    title: '提示',
                    text : '您将支付' + courseMoney + '学币购买该课程',
                    cancel:'取消',
                    confirm:'确定',
                    close:false,
                    callback:function(index) {
                        if(index == 1){
                            var param = {payType: "14", password: "", amount: "", courseId: seriesid, deviceNo: "", isBuy: "1",invitationAppId:window.invitationAppId};
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
                            var param = {payType: "14", password: "", amount: "", courseId: seriesid, deviceNo: "", isBuy: "1",invitationAppId: '${invitationAppId}'};
                            var result = paying(param);
                            isTrySeeFlag = true;
                            onBridgeReady(result);
                        }
                    }
                });
            }
        }
    }
    //    充值确认
    $(".purchase").click(function () {
        if ($(this).attr("learn_coin_enough") == '1') {
            window.location.href = "/weixin/recharge";
            return;
        }
        var status = $(this).attr("pay");
        if (status == '0') {
            //直接微信支付
            var param = {
                payType: "14",
                password: "",
                amount: '',
                courseId: seriesid,
                deviceNo: "",
                isBuy: "1",
                invitationAppId: '${invitationAppId}'
            };
            var result = paying(param);
            onBridgeReady(result);
        } else {
            var param = {
                payType: "07",
                password: "",
                amount: courseMoney,
                courseId: seriesid,
                deviceNo: "",
                isBuy: '1',
                invitationAppId: invitationAppId
            };
            var result = paying(param);
            if (result.code == '000000' || result.code == "100008") {//购买成功、已经购买
                $(".rechartbox").hide();
//                $(".purchasesuccess").show();
                pop1({"content": "购买成功" , "status": "normal", time: '2500'});
                $(".buyclassbtns").hide();
                $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                isPay = '1';
                ///window.location.href = "/weixin/live/index.user?courseId=" + courseId;
                setTimeout(function () {
//                    $(".purchasesuccess").hide();
                    $(".rechartbox").hide();
                    //购买刷新
                    window.location.reload();
                }, 2000);
            }
        }
    });

    function courseInfo(obj) {
        courseId = $(obj).attr("c_id");
        seriesid = $(obj).attr("s_id");
        if(isPay == '1'){
            window.location.href = "/weixin/index.user?courseId=" + courseId;
        }else{
            window.location.href = "/weixin/courseInfo?id=" + courseId + "&seriesid=" + seriesid + "&invitationAppId=" + invitationAppId;
        }
    }
    ;
    /*    /!**
     *评论列表
     *!/
     var offset = 0;
     function addCommentList() {
     var result = ZAjaxRes({
     url: "/courseComment/getCommentListByServiesCourseId?seriesId=" + seriesid + "&offset=" + offset + "&pageSize=" + pageSize,
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
     $("#commentList .con").append('<div class="noData"></div>');
     $('.pullUpLabel').hide();
     }
     oBok = false;
     }
     } else {
     if (offset == '0') {
     $("#commentList .con").append('<div class="noData"></div>');
     $('.pullUpLabel').hide();
     } else {
     $('.pullUpLabel').text('数据加载完毕');
     }
     oBok = false;
     }
     }
     ;*/


    //  关注和取消关注
    $("#guanzhu").click(function () {
        if ($(this).find(".signback").hasClass("on")) {
            follow(this, 1);
            pop1({"content": "取消关注成功" , "status": "normal", time: '2500'});
        } else {
            follow(this, 0);
            pop1({"content": "关注成功" , "status": "normal", time: '2500'});
        }
    })

    $(".soursize").click(function () {
        if ($(this).find(".signback").hasClass("on")) {
            follow(this, 1);
        } else {
            follow(this, 0);
        }
    })
    function follow(obj, isFollow) {
        if (isFollow == 0) {
            var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + roomId, type: "GET"});
            if (result.code == "000000" || result.code == "000111") {
                $(obj).find(".signback").addClass("on");
                $(obj).find(".follows").html("已关注")
            }
        } else {
            var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + roomId, type: "GET"});
            if (result.code == "000000") {
                $(obj).find(".signback").removeClass("on");
                $(obj).find(".follows").html("关注")
            }
        }
    }

    $(".greabtn").click(function () {
        window.location.href = "/weixin/createSerieSingleCourse?seriesid=" + seriesid + "&roomId=" + roomId;
    })


    //点击分享
    $(".bjy").click(function () {
        statisticsBtn({'button': '004', 'referer': '004001', 'courseId':${courseId}})
        $(".invite").show();
    });
    $(".mask").click(function () {
        $(".paymentmethod").hide();
    })

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
            url: "/courseComment/evaluateServiesCourse.user?courseId=" + seriesid + "&content=" + content,
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

    //验证步骤
    $('.useryz .text').on("keyup change", function () {
        var obj = $('.useryz .text');
        var obj2 = $('.clios')
        valT(obj, obj2);
    });
    //  //点击直播封面图
    //  $(".ygbox").click(function () {
    //    coursePaying($(".buyclassbtns")[0]);
    //  });
    //去开课
    $("#openCourse").click(function () {
        statisticsBtn({'button': '001', 'referer': '001004', 'courseId':${courseId}})
        window.location.href = "/weixin/liveRoom?sourseId=1";
    });

    $(".closebtn").click(function () {
        $(".paymentbox").hide();
        $(".choice").find("i").removeClass("on");
        $("#inputBoxContainer input").val("");
        $(".mmbox").hide();
        $(".buyclassbtns a").html("加入课程：" + courseMoney + "学币");
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
        window.location.href = "/weixin/inviCard?courseId=" + seriesid + "&appId=" + appId + "&type=1&seriesid="+seriesid;
    });
    $(".bjy,.invite").click(function () {
        window.location.href = "/weixin/inviCard?courseId=" + seriesid + "&appId=" + appId + "&type=1&seriesid="+seriesid;
    });
    //    充值取消
    $(".cancel").click(function () {
        $(".rechartbox").hide();
    });
    $(".purchasesuccessBtn").click(function () {
        $(".rechartbox").hide();
    });

    //    window.onload = function () {
    //        var bd = document.getElementById("tabBox1-bd");
    //        bd.parentNode.style.height = bd.children[0].children[0].offsetHeight + "px";
    //    };
    function enters() {
        window.location.href = "/weixin/liveRoom?id=" + roomId;
    }

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
                    $(".buyclassbtns a").html("购买课：" + courseMoney + "学币");
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        $(".buyclassbtns a").html("进入课程");
                        $(".buyclassbtns").attr("isJoin", '1');//1-支付完成  0-未支付或者未成功
                        isPay = '1';
                        var result = {
                            code: "000000",
                            message: "支付成功",
                            //购买刷新
                            "successUrl": "/weixin/teacherSeries?seriesid=" + seriesid

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
        var result = ZAjaxJsonRes({url: "/user/getApplySms.user?mobile=" + mobile, type: "GET"});
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
                var param= {payType: "16", courseId: seriesid, deviceNo: "", isBuy: '1',ext:titNum};
                var result = ZAjaxRes({url: "/thirdPay/pay.user", type: "POST",param:param });
                if (result.code == "000000") {//支付成功或者已经支付
                    $(".newtapBox").hide();
                    $(".newinvitationBox").hide();
                    $(".courseInput").val("");
                    $(".Taptxt").text("");
                    $(".exchange").show();
                    setTimeout(function(){
                        $(".exchange").hide();
                    },2000)
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
        $(".newcha").removeClass("on");
        $(".courseInput").val("");
        $(".Taptxt").text("");
        $(".invBtn").hide();
    })
</script>
</html>
