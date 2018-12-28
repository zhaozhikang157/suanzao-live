<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #f0f0f0;">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>直播设置</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
    <script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
</head>
<body>
<div class="paymentmethod" style="display: none">
    <div class="mask">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>
        <p>通过【发送给朋友】</p>
        <p>邀请好友参与</p>
    </div>
</div>
<div class="ewmbox">
    <img src="/web/res/image/custom_service.jpg" alt="" style="position: absolute;z-index:1111;opacity: 0;padding-bottom: 100px"/>
    <div class="kfewm">
        <h3>我是您的专属产品顾问</h3>

        <div class="wxpic">
            <img src="/web/res/image/custom_service.jpg" alt=""/>
            <i>长按</i><i>二维码保存图片</i>
        </div>
    </div>
</div>

<!--正在直播 -->
<div class="livebroadcast">
    <p class="pucmode">
        <span>老师介绍</span>
    </p>

    <div class="teacherxq">
        <div class="teacherzl">
            <div class="teacherlog"
                 style="background: url(/web/res/image/01.png) no-repeat; background-size:100% 100% ;"></div>
            <div class="thname">
                <p class="topteacherbox"><i id="userName"></i>
                    <%--<span class="cw">设计总监</span></p>--%>

                <p class="btomteacherbox"><span>关注:<span id="user_follow">0</span></span>丨<span id="courseCount">课程:0</span></p>
            </div>
        </div>
        <div class="follow">
            <span class="gzbtn">关注</span>
        </div>
    </div>
    <%--<p class="pucmode">--%>
        <%--分销达人榜 <span class="moretap" id="distributionMaster">更多</span>--%>
    <%--</p>--%>

    <%--<div class="swipbox">--%>
        <%--<div class="fxdrb" id="lecturer_box">--%>
            <%--<ul class="swiper-wrapper">--%>
                <%--&lt;%&ndash;<li class="swiper-slide"&ndash;%&gt;--%>
                    <%--&lt;%&ndash;style="background: url(/web/res/image/01.png) no-repeat center center;background-size:100% 100% ;"></li>&ndash;%&gt;--%>
            <%--</ul>--%>
            <%--<!--<p class="rightbtn"></p>-->--%>
        <%--</div>--%>
    <%--</div>--%>
    <p class="pucmode allpep">
        <span>全部学生</span>
        <a class="mor">更多</a>
    </p>

    <div class="templatespoop" id="memberList">
        <%--<div class="exhibition">--%>
        <%--<span class="exhibitionpic"--%>
        <%--style="background:url(/web/res/image/01.png) no-repeat center center;background-size: 100%;"></span>--%>
        <%--<i>张锐</i>--%>
        <%--</div>--%>
    </div>
    <p class="pucmode">
        <span>操作</span>
    </p>

    <div class="templatespoop">
        <a class="introductions">
            直播介绍
        </a>
        <a class="consultation">
            咨询
        </a>
        <a class="evaluations">
            课程评价
        </a>
        <a class="report">
            举报
        </a>
    </div>
    <p class="pucmode">
       <span>微信分销</span>
    </p>

    <div class="templatespoop fotte">
        <p class="wx" onclick="statisticsBtn({'button':'005','referer':'005003','courseId':${courseId}})">
            微信
        </p>
         <p class="yqk"onclick="statisticsBtn({'button':'007','referer':'007003','courseId':${courseId}})">
            邀请卡
        </p>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script>
    var courseId = '${courseId}';
    var roomId = 0;
    var isFollow = 0;
    var appId = 0;
    $(".textIcon").click(function () {
        $(".livetext").toggleClass("on");
    });
    /*金牌讲师*/
//    var lecturer = new Swiper('#lecturer_box', {
//        pagination: null,
//        slidesPerView: 6,
//        slidesPerColumn: 1,
//        slidesPerGroup: 2
//    });

    var result = ZAjaxRes({url: "/course/getTeacherSetByCourseId.user?courseId=" + courseId, type: "GET"});
    if (result.code == "000000") {
        var data = result.data;
        $(".teacherlog").attr("style", "background: url(" + data.courseMap.photo + ") no-repeat; background-size:100% 100% ;");
        $("#userName").text(data.courseMap.userName);
        $("#courseCount").text("课程" + data.teacherCourseCount);
        $.each(data.memberList, function (i, n) {
            $("#memberList").append(" <div class='exhibition'>" +
                    "<span class='exhibitionpic' style='background:url(" + n.photo + ") no-repeat center center;background-size: 100%;'></span>" +
                    "<i>" + n.name + "</i>" +
                    "</div> ");
        });
        roomId = data.courseMap.roomId;
        appId = data.courseMap.appId;
        if(data.myLiveRoom==1){
            $(".follow").remove();//自己不能关注自己
            $(".evaluations").remove();//自己不能评论
            $(".report").remove();//自己不能举报

        }else{
            if(data.isFollow==1){
                $(".gzbtn").addClass("on");
                $(".gzbtn").text("已关注");
            }else{
                $(".gzbtn").removeClass("on");
                $(".gzbtn").text("关注");
            }
        }
        $("#user_follow").text(data.followNum);


        //跳转到老师的直播间
        $(".teacherlog").on('click', function () {
            window.location.href="/weixin/liveRoom?id="+roomId ;
        });
    }

    function getUrl() {
        var result = ZAjaxRes({url: "/user/invitationCard.user?courseId=" + courseId, type: "GET"});
        return result.data.url;
    }

    $(".introductions").click(function () {
        window.location.href = "/weixin/courseInfo?id=" + courseId;
    });
    $(".consultation").click(function () {
        $(".ewmbox").addClass("on");
    });
    //课程评价
    $(".evaluations").click(function () {
        var isFreeSign  = isFree();
        if(isFreeSign==1){
            window.location.href = "/weixin/submitComment?id=" + courseId;
        }else{
            alert("课程购买后才可以评价");
        }
    });
    //举报
    $(".report").click(function () {
        window.location.href = "/weixin/complaint?id=" + courseId;
    });

    //判断课程是否购买或免费
    function isFree(){
        var result = ZAjaxRes({url: "/course/getCourseIsFreeSign?courseId=" + courseId, type: "GET"});
        return result.data.isFreeSign;
    }

    //微信
    $(".wx").click(function () {
        $(".paymentmethod").show();
        var param = {
            liveRoomId: courseId,
            systemType: "COURSE",
            photoUrl: getUrl()
        };
        share_h5(param);//分享

    });

    //邀请卡
    $(".yqk").click(function () {
        window.location.href = "/weixin/inviCard?courseId=" + courseId+"&appId="+appId;
    });


    $(".ewmbox").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".kfewm,.wxpic").length == 0) {
            $(".ewmbox").removeClass("on");
        }
    });

    //关注或取消关注
    var flag =true;
    $(".gzbtn").click(function () {
       if(flag){
           flag=false;
//           if (isFollow == 0) {
               if(!$(".gzbtn").hasClass("on")){
               var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + roomId, type: "GET"});
               if (result.code == "000000" ||result.code == "000111") {
                   $(".gzbtn").addClass("on");
                   $(".gzbtn").text("已关注");
                   isFollow=1;
                   var num = $("#user_follow").text();
                   $("#user_follow").text(parseInt(num)+1);
               }
           } else {
               var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + roomId, type: "GET"});
               if (result.code == "000000") {
                   $(".gzbtn").removeClass("on");
                   $(".gzbtn").text("关注");
                   isFollow=0;
                   var num = $("#user_follow").text();
                   $("#user_follow").text(parseInt(num)-1);
               }
           }
           flag = true;
       }
    });

    //更多的分销达人
//    $("#distributionMaster").on('click',function(){
//        window.location.href="/weixin/toShare?id="+courseId;
//    });

    $(".mor").click(function () {
        window.location.href = "/weixin/member?id=" + courseId;

    });
    $(".paymentmethod").on("click", function () {
            $(".paymentmethod").hide();
    });
</script>
</html>
