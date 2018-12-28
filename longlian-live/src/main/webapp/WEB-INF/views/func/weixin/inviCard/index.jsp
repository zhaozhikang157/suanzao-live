<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<meta charset="UTF-8">
<meta name="Keywords" content=""/>
<meta name="Description" content=""/>
<title>长按发送给朋友</title>
<script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
<link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
<script src="/web/res/js/jquery.min.js"></script>
<style>
    .customYQM{
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
    }
    .customYQM img{
        width: 100%;
        height: 100%;
    }
    .share_yqm{
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        z-index: 9899999999;
        display: none;
    }
    .intifoot{
        z-index: 1;
    }
</style>
</head>
<body onload="doInit()">
<div class="fezq">
    <span>分享赚<em class="mon"></em></span>
</div>
<div class="fezqTip"></div>
<div class="paymentmethod" style="display: none">
    <div class="mask" style="display: none">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>

        <p>通过【发送给朋友】</p>

        <p>邀请好友参与</p>
    </div>
</div>
<!--正在直播 -->
<div class="invitationbox">
    <div class="picbox">
        <div class="nvitationone">
            <img src="/web/res/image/yqk1.png">
        </div>
        <div class="nvitationone">
            <img src="/web/res/image/yqk2.png">
        </div>
        <div class="nvitationone">
            <img src="/web/res/image/yqk3.png">
        </div>
    </div>

    <ul class="intifoot" style="display: none">
        <li code="2"><img src="/web/res/image/bgTemp_2.png"><span>童年夜</span></li>
        <li code="3"><img src="/web/res/image/bgTemp_3.png"><span>年轻极简</span></li>
        <li code="1"><img src="/web/res/image/bgTemp_1.png"><span>浪漫粉</span></li>
    </ul>

</div>

<%--自定义邀请码--%>
<div class="customYQM" style="display: none;">
    <img src="" alt="" style=" width: 66%;position: absolute;top: 4rem;left: 0;right: 0; margin: 0 auto;"/>
</div>
<%--<div class="share_yqm">--%>
    <%--<img src="/web/res/image/share_yqk.png" alt=""/>--%>
<%--</div>--%>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script>
    var inviCardList = new Array();
    var roomId = '${roomId}';
    var courseId = '${courseId}'
    var inviCardUrl = '';
    var appId = '${appId}';
    var OnlyCode = '';
    if(getQueryString('seriesid') != '0' && getQueryString('seriesid') != ''&& getQueryString('seriesid') != 'null'){
        OnlyCode = getQueryString('seriesid');
    }else{
        OnlyCode = courseId;
    }
    var isPermit = ZAjaxRes({url: "/courseCard/isPermit?courseId=" + OnlyCode, type: "GET"});
    var courseCardAmt = ZAjaxRes({url: "/course/getCourseCardAmt.user?id=" + OnlyCode, type: "GET"});
    if(courseCardAmt.code == '000000'){
        console.log(courseCardAmt)
        if(courseCardAmt.data.value=="" || courseCardAmt.data.type=='teacher'){
            $(".fezq").hide();
        }else{
            $(".mon").text("￥"+courseCardAmt.data.value+"");
        }
    }else{
        $(".fezq").hide();
    }
    if(isPermit.code == '000000'){
        if(isPermit.ext.modelUrl != '' && isPermit.ext != '' ){
            $('.invitationbox').hide();
            $('.customYQM').show().children('img').attr('src',isPermit.ext.cardUrl)
        }
    }
    <%--if(courseCardAmt.code == '000000'){--%>
        <%--var data = courseCardAmt.data;--%>
        <%--if(data.type == "teacher"){--%>
            <%--$('.share_yqm').hide();--%>
        <%--}else{--%>
            <%--if(handlerNavigationVisitRecord("personalCenter" ,"<%=sytem_new_version%>" , "<%=longlian_live_user_web_navigation_sign%>")){--%>
                <%--$(".share_yqm").show();--%>
            <%--}--%>
        <%--}--%>
    <%--}--%>
    function doInit() {
        var result = ZAjaxRes({
            url: "/user/invitationCard.user?roomId=" + roomId + "&courseId=" + courseId + "&appId=" + appId,
            type: "GET"
        });
        if (result.code != '000000') {
            sys_pop(result);
            return;
        }
        var defultTemp = result.data.code;

        for(i=0;i<$(".intifoot li").length;i++){
            var inforFoot  =$(".intifoot li").eq(i).attr("code");
            if(defultTemp==inforFoot){
                $(".intifoot li").eq(i).find("span").addClass("on");
                $(".picbox>div").eq(i).find("img").attr("src",result.data.url);
                $(".picbox>div").eq(i).addClass("on");
            }
        }
        $(".intifoot").show();
        share_h5_init(result.data.url);
    }

    function share_h5_init(url) {
        var sys = "";
        var id = 0;
        if (roomId == '0') {
            sys = "COURSE_INVI_CARD";
            id = courseId;
        }
        if (courseId == '0') {
            sys = "LIVE_ROOM_INVI_CARD";
            id = roomId;
        }
        var param = {
            liveRoomId: id,
            systemType: sys,
            photoUrl: url
        }
        share_h5(param);//分享
    }

    $(".paymentmethod").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".dister,.contnt").length == 0) {
            $(".paymentmethod").hide();
            $(".mask").hide();
        }
    });
    $(".intifoot li").click(function () {
        $(".intifoot").hide();
        var index = $(this).index();
        $(this).find("span").addClass("on").parent().siblings().find("span").removeClass("on");
        $(".picbox>div").eq(index).show().siblings().hide();
        var temp = $(this).attr("code");
        console.log(temp)
        var result = ZAjaxRes({
            url: "/user/invitationCard.user?roomId=" + roomId + "&courseId=" + courseId + "&appId=" + appId + "&temp="+temp,
            type: "GET"
        });
        if (result.code != '000000') {
            sys_pop(result);
            return;
        }
        $(".picbox>div").eq(index).find("img").attr("src",result.data.url);
        $(".intifoot").show();
        share_h5_init(result.data.url);
    })
    $('.share_yqm').click(function(){
        $(this).hide();
    })
    $(".fezq").click(function(){
        $(".fezqTip").show();
    });
    $(".fezqTip").click(function(){
        $(this).hide();
    })
</script>
</html>


