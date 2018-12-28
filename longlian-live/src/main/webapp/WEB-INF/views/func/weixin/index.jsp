<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>${indexTitle}</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/home.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script>
        var isShareUrl = sessionStorage.getItem('isShareUrl');
        var return_url = window.location.search;
        function getQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]); return null;
        }
        if(isShareUrl == null && getQueryString('return_url') != ""&& getQueryString('return_url') != null){
            $('head').append('<a id="return_url" href="'+ return_url.substr(12,return_url.length) +'"></a>');
            sessionStorage.setItem('isShareUrl', 'true');
            $('#return_url').click();
        }
    </script>
</head>
<body>
<%-- 头部 --%>
<header>
    <div class="search_box">
        <div class="searchInput" style="display: -webkit-box;-webkit-box-align: center;-webkit-box-pack: justify;position: relative">
            <input type="button" id="searchFn" autocomplete="off" style="border:none;" value="搜索单节课/系列课/直播间" />
            <span class="live_room_open"><img src="/web/res/image/live_gif.png" alt=""/></span>
        </div>
        <nav class="nav">
            <div class="swiper-container" id="nav">
                <div class="swiper-wrapper">
                    <div class="swiper-slide on" data-onclick = 'true'>
                        <a href="javascript:;">推荐</a></div>
                </div>
            </div>
        </nav>
    </div>
</header>
<%-- 内容 --%>
<div class="content">
    <div class="content_lsit " >
        <div class="content_itme">
            <%--banner--%>
            <div class="content_banner">
                <div class="swiper-container" id="banner">
                    <div class="swiper-wrapper">
                    </div>
                    <!-- Add Pagination -->
                    <div class="swiper-pagination"></div>
                </div>
            </div>
            <%--banner end--%>
            <%--导航分类--%>
            <ul class="partitionType">
                <li class="mycourse">我的课程</li>
                <li class="courfree">免费专区</li>
                <li class="rankingList">排行榜</li></ul>
            <%--导航分类 end--%>
            <%-- 每周精选 --%>
            <div class="recommend_list">
                <div class="recommend_head">
                    <h3>
                        <strong>每周精选</strong>
                        <em class="selectionMoer" id="recommendMoer">更多</em></h3>
                </div>
                <div class="recommend_itmes">
                    <ul id="selected">

                    </ul>
                </div>
            </div>
            <%-- 每周精选 end --%>
            <%-- 正在直播 --%>
            <div class="recommend_list hide" id="recommend_list_live">
                <div class="recommend_head">
                    <h3>
                        <strong>正在直播</strong>
                        <em class="selectionMoer" id="liveMoer">更多</em></h3>
                </div>
                <div class="recommend_itmes">
                    <ul id="live" class="publicUlstyle">

                    </ul>
                    <%--<div class="swiper-container" id="live">--%>
                        <%--<div class="swiper-wrapper">--%>
                        <%--</div>--%>
                    <%--</div>--%>
                </div>
            </div>
            <%-- 正在直播 end --%>
            <%-- 直播预告 --%>
            <div class="recommend_list">
                <div class="recommend_head">
                    <h3>
                        <strong>直播预告</strong>
                        <em class="selectionMoer" id="prevueListMoer">更多</em></h3>
                </div>
                <div class="recommend_itmes">
                    <ul id="prevueList" class="publicUlstyle">

                    </ul>
                </div>
            </div>
            <%-- 直播预告 end --%>
            <%-- 精选课程 --%>
            <div class="recommend_list">
                <div class="recommend_head">
                    <h3>
                        <strong>精选课程</strong></h3>
                </div>
                <div class="recommend_itmes">
                    <ul id="playback" class="publicUlstyle">
                    </ul>
                </div>
            </div>
            <%-- 精选课程 end --%>
        </div>
    </div>
</div>
<%-- 尾部 --%>
<footer class="footer">
    <a class="home on" href="/weixin">首页</a>
    <div class="creatliv" onClick="statisticsBtn({'button':'001','referer':'001001'})"></div>
    <a class="my" href="/weixin/personalCenter">我的</a>
</footer>

<!-- 搜索讲师box -->
<div id="search_lecturer" style="display:none">
    <div class="zdata indexzdata">没有找到相关内容</div>
    <div class="searchAllbox coursesD">
        <p class="noTitle znoTitle">单节课<i class="seachPublicclass oneclassTitle">更多</i></p>
        <ul class="publicUlstyle othercourses" id="othercourses">
        </ul>
    </div>
    <div class="searchAllbox coursesX">
        <p class="noTitle xTitle">系列课<i class="seachPublicclass serrorclass">更多</i></p>
        <ul class="publicUlstyle othercourses" id="searchseries">
        </ul>
    </div>
    <div class="searchAllbox liveRooms">
        <p class="noTitle liveTitle">直播间<i class="seachPublicclass seachliveBox">更多</i></p>
        <ul class="searchliveBoxul othercourses" id="searchLive">
        </ul>
    </div>
</div>
</body>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/pop.js?nd=<%=current_version%>'></script>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script>
    var navNum = 1; // 设置nav被点击坐标
    var errorMessage = true; // 设置开关
    var IndexUrlSearchVal = window.location.search; // 获取当前http地址
    <%--页面初始化--%>
    $(function(){

        var result = ZAjaxRes({url: "/wonderfulrecommendationV164", type: "post"});
        if(result.code == "000000"){
            advertisingList = result.data.advertisingList;/*banner*/
            courseWeeklySelection = result.data.courseWeeklySelection;/*每周精选*/
            beingroadcastive = result.data.beingroadcastive;/*正在直播*/
            pageSize=result.data.pageSize;
            advance = result.data.advance;/*直播预告*/
            courseAllSelection = result.data.courseAllSelection;/*精选课程*/
            courseTypes = result.data.courseTypes /*初始化导航*/
            // 初始化导航
            $.each(courseTypes, function (i, n) {
                var NavLi = '<div class="swiper-slide" data-onclick = "true" data-id="'+ n.id+'"><a href="javascript:;">'+ n.name +'</a></div>';
                $("#nav .swiper-wrapper").append(NavLi);
            });
            //广告
            $.each(advertisingList, function (i, n) {
                var advertType = n.advertType;
                var picAddress = n.picAddress;
                var openUrl="";
                var openId = n.courseId;
                var openisSeries = n.isSeriesCourse;
                if(advertType=="0"){
                    openUrl =""
                }else if(advertType=="1"){
                    openUrl =  n.openUrl;
                }else if(advertType=="2"){
                    if(openisSeries=="1"){
                        openUrl =  "/weixin/courseInfo?id="+openId+"&isSeries=1";
                    }else{
                        openUrl = "/weixin/courseInfo?id="+openId;
                    }
                }else if(advertType=="3"){
                    openUrl = "/weixin/liveRoom?id=" + openId;
                }
                var JLi = '<div class="swiper-slide">' +
                        '<a href="'+openUrl+'" ><img src="' + picAddress + '"/></a>' +
                        '</div>';
                $("#banner .swiper-wrapper").append(JLi);
            });
            //每周精选
            $.each(courseWeeklySelection,function(i,n){
                //是否是系列课
                var isSeriesCourse = n.isSeriesCourse;
                var hed=""
                if(isSeriesCourse == '0'){
                    var oHtml = '主讲：'+n.userName;
                }else{
                    hed="hed";
                    var oHtml = "<span class='series'></span>"+'已更新'+n.updatedCount+'节 | 主讲：'+n.userName;
                }
                //直播类型
                var liveWayDesc = "";
                if (n.liveWay == '1'){
                    liveWayDesc = "audioIco";
                }else if(n.liveWay == '0') {
                    liveWayDesc = "videoIco";
                }
                //正在直播
                var liveingpic="playbacks";
                if(n.statusStr=="0"){
                    liveingpic="playbacks"
                }else if(n.statusStr=="1"){
                    liveingpic="backlookout"
                }else if(n.statusStr=="2"){
                    liveingpic="liveingpic"
                }
                var li = $('<li>');
                $("#selected").append(li);
                li.append('<p class="recommendation" style="background:url('+ n.coverssAddress+') no-repeat center center;background-size:cover ">' +
                        '<em class="projection">' +
                        ' <i class="'+liveingpic+'"></i><span class="playNumber">' + n.joinCount + '人</span><i class="'+liveWayDesc+'"></i></em>' +
                        '</p>' +
                        '<p class="selectioncontBox"><span class="selectionTitle">'+ n.topic+'</span><em class="teacherselection">'+oHtml+'</em></p>');
                li.click(function () {
                    toCourseInfo(n.id,isSeriesCourse);
                });
            })
            //正在直播
            if(beingroadcastive.length == 0){
                console.log('当前没有正在直播课程');
                $('#recommend_list_live').addClass('hide');
            }else{
                $('#recommend_list_live').removeClass('hide');
                $.each(beingroadcastive, function (i, n) {
                    //直播预告少于3条隐藏更多
                    if(i > 2){
                        $('#liveMoer').removeClass('hide');
                    }else if(i<=2){
                        $('#liveMoer').addClass('hide');
                    }
                    //每次遍历3条数据
                    if (i >= 3) {
                        return;
                    }
                    //是否是系列课
                    var isSeriesCourse = n.isSeriesCourse;
                    var hed = ""
                    if (isSeriesCourse == '0') {
                        var oHtml = '主讲：' + n.userName;
                    } else {
                        hed = "hed";
                        var oHtml = "<span class='series'></span>" + '已更新' + n.updatedCount + '节 | 主讲：' + n.userName;
                    }
                    //直播类型
                    var liveWayDesc = "";
                    if (n.liveWay == '1') {
                        liveWayDesc = "audioIco";
                    } else if (n.liveWay == '0') {
                        liveWayDesc = "videoIco";
                    }
                    //课程金额
                    var free = "免费";
                    var freeClass = "free";
                    if (n.chargeAmt > 0) {
                        free = "<span class='tapfontWei'>" + n.chargeAmt + "</span>" + "学币";
                        freeClass = "";
                    }
                    //正在直播
                    var liveingpic = "playbacks";
                    if (n.statusStr == "0") {
                        liveingpic = "playbacks"
                    } else if (n.statusStr == "1") {
                        liveingpic = "backlookout"
                    } else if (n.statusStr == "2") {
                        liveingpic = "liveingpic"
                    }
                    var li = $('<li data-id="' + n.id + '" data-SeriesCourse="' + isSeriesCourse + '">');
                    $("#live").append(li);
                    li.append('<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:cover;">' +
                            '<em class="projection"><i class="' + liveingpic + '"></i><span class="playNumber">' + n.joinCount + '人</span>' +
                            '<i class=' + liveWayDesc + '></i></em></div>' +
                            '<p> <strong class="title">' + n.topic + '</strong></p>' +
                            '<p><span class="name">' + oHtml + '</span></p><p><span class="time">' + n.startTimeStr + '</span><strong class="money ' + freeClass + '">' + free + '</strong>' +
                            '</p>');
                    li.click(function () {
                        toCourseInfo(n.id, isSeriesCourse);
                    });
                    //每次遍历3条数据
//                    if(i == 3){
//                        return;
//                    }
//                    //是否是系列课
//                    var isSeriesCourse = n.isSeriesCourse;
//                    var hed=""
//                    if(isSeriesCourse == '0'){
//                        var oHtml = '主讲：'+n.userName;
//                    }else{
//                        hed="hed";
//                        var oHtml = "<span class='series'></span>"+'已更新'+n.updatedCount+'节 | 主讲：'+n.userName;
//                    }
//                    //直播类型
//                    var liveWayDesc = "";
//                    if (n.liveWay == '1'){
//                        liveWayDesc = "audioIco";
//                    }else if(n.liveWay == '0') {
//                        liveWayDesc = "videoIco";
//                    }
//                    //课程金额
//                    var free = "免费";
//                    var freeClass = "free";
//                    if (n.chargeAmt > 0) {
//                        free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
//                        freeClass = "";
//                    }
//                    //正在直播
//                    var liveingpic="playbacks";
//                    if(n.statusStr=="0"){
//                        liveingpic="playbacks"
//                    }else if(n.statusStr=="1"){
//                        liveingpic="backlookout"
//                    }else if(n.statusStr=="2"){
//                        liveingpic="liveingpic"
//                    }
//                    var li = $('<div class="swiper-slide">');
//                    $("#live .swiper-wrapper").append(li);
//                    li.append('<p class="recommendation" style="background:url('+ n.coverssAddress+') no-repeat center center;background-size:cover ">' +
//                            '<em class="projection">' +
//                            ' <i class="'+liveingpic+'"></i><span class="playNumber">' + n.joinCount + '人</span><i class="'+liveWayDesc+'"></i></em>' +
//                            '</p>' +
//                            '<p class="selectioncontBox"><span class="selectionTitle">'+ n.topic+'</span><em class="teacherselection">'+oHtml+'</em></p>'+
//                            '<p class="time_money"><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong>' +
//                            '</p>');
//                    li.click(function () {
//                        toCourseInfo(n.id,isSeriesCourse);
//                    });
                });
            }
            //直播预告
            if(advance.length == 0){
                console.log('当前没有正在直播课程');
                $('#recommend_list_live').addClass('hide');
            }else {
                $.each(advance, function (i, n) {
                    //直播预告少于3条隐藏更多
                    if (i > 2) {
                        $('#prevueListMoer').removeClass('hide');
                    } else if (i <= 2) {
                        $('#prevueListMoer').addClass('hide');
                    }
                    //每次遍历3条数据
                    if (i  >= 3) {
                        return;
                    }
                    //是否是系列课
                    var isSeriesCourse = n.isSeriesCourse;
                    var hed = ""
                    if (isSeriesCourse == '0') {
                        var oHtml = '主讲：' + n.userName;
                    } else {
                        hed = "hed";
                        var oHtml = "<span class='series'></span>" + '已更新' + n.updatedCount + '节 | 主讲：' + n.userName;
                    }
                    //直播类型
                    var liveWayDesc = "";
                    if (n.liveWay == '1') {
                        liveWayDesc = "audioIco";
                    } else if (n.liveWay == '0') {
                        liveWayDesc = "videoIco";
                    }
                    //课程金额
                    var free = "免费";
                    var freeClass = "free";
                    if (n.chargeAmt > 0) {
                        free = "<span class='tapfontWei'>" + n.chargeAmt + "</span>" + "学币";
                        freeClass = "";
                    }
                    //正在直播
                    var liveingpic = "playbacks";
                    if (n.statusStr == "0") {
                        liveingpic = "playbacks"
                    } else if (n.statusStr == "1") {
                        liveingpic = "backlookout"
                    } else if (n.statusStr == "2") {
                        liveingpic = "liveingpic"
                    }
                    var li = $('<li data-id="' + n.id + '" data-SeriesCourse="' + isSeriesCourse + '">');
                    $("#prevueList").append(li);
                    li.append('<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:cover;">' +
                            '<em class="projection"><i class="' + liveingpic + '"></i><span class="playNumber">' + n.joinCount + '人</span>' +
                            '<i class=' + liveWayDesc + '></i></em></div>' +
                            '<p> <strong class="title">' + n.topic + '</strong></p>' +
                            '<p><span class="name">' + oHtml + '</span></p><p><span class="time">' + n.startTimeStr + '</span><strong class="money ' + freeClass + '">' + free + '</strong>' +
                            '</p>');
                    li.click(function () {
                        toCourseInfo(n.id, isSeriesCourse);
                    });
                });
            }
            //精选课程
            $.each(courseAllSelection, function (i, n) {
                //是否是系列课
                var isSeriesCourse = n.isSeriesCourse;
                var hed=""
                if(isSeriesCourse == '0'){
                    var oHtml = '主讲：'+n.userName;
                }else{
                    hed="hed";
                    var oHtml = "<span class='series'></span>"+'已更新'+n.updatedCount+'节 | 主讲：'+n.userName;
                }
                //直播类型
                var liveWayDesc = "";
                if (n.liveWay == '1'){
                    liveWayDesc = "audioIco";
                }else if(n.liveWay == '0') {
                    liveWayDesc = "videoIco";
                }
                //课程金额
                var free = "免费";
                var freeClass = "free";
                if (n.chargeAmt > 0) {
                    free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
                    freeClass = "";
                }
                //正在直播
                var liveingpic="playbacks";
                if(n.statusStr=="0"){
                    liveingpic="playbacks"
                }else if(n.statusStr=="1"){
                    liveingpic="backlookout"
                }else if(n.statusStr=="2"){
                    liveingpic="liveingpic"
                }
                var li = $('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'">');
                $("#playback").append(li);
                li.append('<div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
                        '<em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">'+ n.joinCount+'人</span>' +
                        '<i class='+liveWayDesc+'></i></em></div>' +
                        '<p> <strong class="title">'+ n.topic+'</strong></p>' +
                        '<p><span class="name">'+oHtml+'</span></p><p><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong>' +
                        '</p>');
                li.click(function () {
                    toCourseInfo(n.id,isSeriesCourse);
                });
            });
        }
        <%-- 创建直播间 --%>
        var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
        var authStatus,liveRoomId;
        if (result.code == "000000") {
            liveRoomId = result.data.liveRoomId;
            authStatus = result.data.authStatus;
        }
        $(".creatliv").click(function(){
            $(".guide").hide();
            if (authStatus == -1) {
                //创建直播间
                window.location.href="${ctx}/weixin/createLiveRoom";
            }
            if (authStatus == 0) {
                alert("审核中");
            }
            if (authStatus == 2) {
                alert("审核失败");
            }
            if (authStatus == 1) {

                if(!/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)){
                    alert('PC端无法创建课程');
                }else{
                    var result = ZAjaxJsonRes({url: "/liveRoom/isForbiddenRoom.user", type: "GET"});
                    if( result.code=="100057"){
                        $(".topBoxcon").show();
                        return;
                    }
                    //成功
                    window.location.href="${ctx}/weixin/createSingleCourse?id="+liveRoomId;
                }
            }
        })
        <%-- 创建直播间end --%>
        <%-- 获取nav 个数--%>
        var navListNum = $('#nav .swiper-slide').length;
        for(var i = 0;i < navListNum-1;i++){
            var conHtml = '<div class="content_lsit class_fun_itme hide" onscroll="myScroll(this)"><ul class="publicUlstyle"></ul><div class="pullUpLabel selectedLabel" style=""> <div><img src="/web/res/image/smial.png"><i>我是有底线的</i></div></div></div>';
            $('.content').append(conHtml)
        }
        $("#searchFn").click(function(){
            BaseFun.RemoveSession('searchVal');
            window.location.href = '/weixin/search';
        });
        //   下拉加载精彩课程
        $('.content_lsit ').scroll(function(){
            var thisScrollTop = this.scrollTop;
            var thisScrollHeight = $('.content_lsit').eq(0).height();
            var bottomScrollTop = $('.content_lsit').eq(0).children('.content_itme').height();
            var playbacklen = $('#playback>li').length;
            if(thisScrollTop + thisScrollHeight >= bottomScrollTop-20 ){
                var result = ZAjaxRes({url: "/findCourseAllSelection", type: "post",param:{pageSize:20,offset:playbacklen}});
                $.each(result.data, function (i, n) {
                    //是否是系列课
                    var isSeriesCourse = n.isSeriesCourse;
                    var hed=""
                    if(isSeriesCourse == '0'){
                        var oHtml = '主讲：'+n.userName;
                    }else{
                        hed="hed";
                        var oHtml = "<span class='series'></span>"+'已更新'+n.updatedCount+'节 | 主讲：'+n.userName;
                    }
                    //直播类型
                    var liveWayDesc = "";
                    if (n.liveWay == '1'){
                        liveWayDesc = "audioIco";
                    }else if(n.liveWay == '0') {
                        liveWayDesc = "videoIco";
                    }
                    //课程金额
                    var free = "免费";
                    var freeClass = "free";
                    if (n.chargeAmt > 0) {
                        free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
                        freeClass = "";
                    }
                    //正在直播
                    var liveingpic="playbacks";
                    if(n.statusStr=="0"){
                        liveingpic="playbacks"
                    }else if(n.statusStr=="1"){
                        liveingpic="backlookout"
                    }else if(n.statusStr=="2"){
                        liveingpic="liveingpic"
                    }
                    var li = $('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'">');
                    $("#playback").append(li);
                    li.append('<div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center; background-size:cover;">' +
                            '<em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">'+ n.joinCount+'人</span>' +
                            '<i class='+liveWayDesc+'></i></em></div>' +
                            '<p> <strong class="title">'+ n.topic+'</strong></p>' +
                            '<p><span class="name">'+oHtml+'</span></p><p><span class="time">'+ n.startTimeStr+'</span><strong class="money '+freeClass+'">'+ free+'</strong>' +
                            '</p>');
                    li.click(function () {
                        toCourseInfo(n.id,isSeriesCourse);
                    });
                });
            }
        })
        // 点击进入直播间
        var liveRecording = JSON.parse(sessionStorage.getItem("liveRecording"));
        if(liveRecording == '' || liveRecording == null ){
            $('.live_room_open').hide();
        }else{
            $('.live_room_open').show();
        }
        $('.live_room_open').on('click',function(){
            var result = ZAjaxRes({url: "/getCourseStatus?id="+liveRecording.id, type: "GET"});
            if(result.code == '000000'){
                console.log(result)
                if(result.data.IS_DELETE == 0 && result.data.STATUS == 0){
                    window.location.href = liveRecording.url;
                }else{
                    pop({"content": "课程已下架" ,width:"10rem" ,"status": "error", time: '2000'});
                    $(this).hide();
                    sessionStorage.removeItem("liveRecording");
                }
            }
        })
        //   检测ios返回
        var isPageHide = false;
        window.addEventListener("pageshow", function (e) {
            if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)){
                if (isPageHide) {
                        window.location.reload();
                }
            };
        },false);
        window.addEventListener('pagehide', function () {
            isPageHide = true;
        },false);
    <%--导航--%>
    var nav = new Swiper('#nav', {
        slidesPerView: 'auto',
        freeModeMomentumRatio: 0.5,
        freeMode: true,
        observer: true,
        onSetTranslate: function(swiper,translate){
            var navListW = $('.on').width(),navListLen = $('#nav .swiper-slide').length; //获取一个导航宽度，导航个数
            var navList = (navListW*navListLen)-(navListW*7); //总长度减展示长度
            if(translate <= '-'+navList){
                $('nav').removeClass('nav');
            }else{
                $('nav').addClass('nav');
            }
        },
    });
    swiperWidth = nav.container[0].clientWidth
    maxTranslate = nav.maxTranslate();
    maxWidth = -maxTranslate + swiperWidth / 2
    $("nav .swiper-container").on('click', function(e) {
        e.preventDefault();
    })
    nav.on('tap', function(swiper, e) {
        slide = swiper.slides[swiper.clickedIndex]
        slideLeft = slide.offsetLeft
        slideWidth = slide.clientWidth
        slideCenter = slideLeft + slideWidth / 2
        // 被点击slide的中心点
        nav.setWrapperTransition(300)
        if (slideCenter < swiperWidth / 2) {
            nav.setWrapperTranslate(0)
        } else if (slideCenter > maxWidth) {
            nav.setWrapperTranslate(maxTranslate)
        } else {
            nowTlanslate = slideCenter - swiperWidth / 2
            nav.setWrapperTranslate(-nowTlanslate)
        }
        $('#nav .swiper-slide').eq(swiper.clickedIndex).addClass('on').siblings().removeClass('on');
        $('.content .content_lsit').addClass('hide').eq(swiper.clickedIndex).removeClass('hide');
        if(swiper.clickedIndex != 0) {
            navNum = swiper.clickedIndex;
        }
        if($('#nav .swiper-slide').eq(swiper.clickedIndex).attr('data-onclick') == 'true') {
            loadItems(swiper.clickedIndex);
        }else{
            console.log('已经加载过了')
        }
    })
    <%--导航 end--%>
    <%-- 首页banner --%>
    var banner = new Swiper('#banner', {
        spaceBetween: 30,
        pagination: '.swiper-pagination',
        observer:true,
        observeParents:true,
        autoplay:3000,
        autoplayDisableOnInteraction : false,
        loop : true,
    });
    <%-- 首页banner end --%>
    <%-- 正在直播 --%>
//    var live = new Swiper('#live', {
//        slidesPerView: '1.1',
//        centeredSlides: true,
//        observer:true,
//        observeParents:true,
//        spaceBetween: 30,
//    });
    <%-- 正在直播 end --%>
    <%--分类导航--%>
    $(".rankingList").click(function(){
        window.location.href="/weixin/rankingList"
    });
    $(".mycourse").click(function(){
        window.location.href="/weixin/mycourse"
    })
    $(".courfree").click(function(){
        window.location.href="/weixin/courseFree"
    });
    <%--分类导航 end--%>
    <%--每周精选更多--%>
    $(".selectionMoer").click(function(){
        if($(this).attr('id') == 'playbackMoer' || $(this).attr('id') == 'recommendMoer'){
            window.location.href="/weixin/weekSelection"
        }else if($(this).attr('id') == 'liveMoer'){
            window.location.href="/weixin/liveitmesinfo"
        }else if($(this).attr('id') == 'prevueListMoer'){
            window.location.href="/weixin/traileritmesinfo"
        }
    });
    })

    /*--------------------------------------------------------------*/
    /**
     *  跳转至课程详情页面
     * @param id
     */
    function toCourseInfo(id,isSeriesCourse) {
        if(isSeriesCourse =='1'){
            window.location.href = "/weixin/courseInfo?id="+id+"&isSeries=1";
        }else{
            window.location.href = "/weixin/courseInfo?id=" + id;
        }
    };
    function loadItems(index){
        var dataId = $('#nav .swiper-wrapper .swiper-slide').eq(index).attr('data-id');
        var offset = $('.content_lsit').eq(index).children('.publicUlstyle').children('li').length;
        var result = ZAjaxRes({url: "/course/getCoursesByType", type: "post",param:{pageSize:20,offset:offset,courseType:dataId}});
        if(result.code == '000000' ){
            $.each(result.data, function (i, n) {
                //是否是系列课
                var isSeriesCourse = n.isSeriesCourse;
                var hed = ""
                if (isSeriesCourse == '0') {
                    var oHtml = '主讲：' + n.userName;
                } else {
                    hed = "hed";
                    var oHtml = "<span class='series'></span>" + '已更新' + n.updatedCount + '节 | 主讲：' + n.userName;
                }
                //直播类型
                var liveWayDesc = "";
                if (n.liveWay == '1') {
                    liveWayDesc = "audioIco";
                } else if (n.liveWay == '0') {
                    liveWayDesc = "videoIco";
                }
                //课程金额
                var free = "免费";
                var freeClass = "free";
                if (n.chargeAmt > 0) {
                    free = "<span class='tapfontWei'>" + n.chargeAmt + "</span>" + "学币";
                    freeClass = "";
                }
                //正在直播
                var liveingpic = "playbacks";
                if (n.statusStr == "0") {
                    liveingpic = "playbacks"
                } else if (n.statusStr == "1") {
                    liveingpic = "backlookout"
                } else if (n.statusStr == "2") {
                    liveingpic = "liveingpic"
                }
                var li = $('<li data-id="' + n.id + '" data-SeriesCourse="' + isSeriesCourse + '">');
                $(".content_lsit").eq(index).children('.publicUlstyle').append(li);
                li.append('<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:cover;">' +
                        '<em class="projection"><i class="' + liveingpic + '"></i><span class="playNumber">' + n.joinCount + '人</span>' +
                        '<i class=' + liveWayDesc + '></i></em></div>' +
                        '<p> <strong class="title">' + n.topic + '</strong></p>' +
                        '<p><span class="name">' + oHtml + '</span></p><p><span class="time">' + n.startTimeStr + '</span><strong class="money ' + freeClass + '">' + free + '</strong>' +
                        '</p>');
                li.click(function () {
                    toCourseInfo(n.id, isSeriesCourse);
                });
            });
        }else if(result.code == '000110'){ //000110 没有更多数据
            var ulStyleLen = $(".content_lsit").eq(index).children('.publicUlstyle').children('li').length; //是否有子元素
            if(ulStyleLen == 0){
                var li = '<div class="noData"></div>';
                $(".content_lsit").eq(index).children('.publicUlstyle').append(li);
            }else{
                if(errorMessage == true){
                    $(".content_lsit").eq(index).children(".pullUpLabel").show();
                    errorMessage = false;
                }
            }
        }
        $('#nav .swiper-slide').eq(index).attr('data-onclick','false');

    }
    //上拉加载
    var timeoutRef;
    function myScroll(_this){
        if(timeoutRef){
            clearTimeout(timeoutRef);
        }
        timeoutRef = setTimeout( myScrollCallback(_this), 1000);
    }
    function myScrollCallback(_this){
        var thisScrollTop = _this.scrollTop;
        var thisScrollHeight = $('.content_lsit').eq(navNum).height();
        var bottomScrollTop = $('.content_lsit').eq(navNum).children('.publicUlstyle').height();
        if(thisScrollTop + thisScrollHeight >= bottomScrollTop-1 ){
            loadItems(navNum);
        }
    }
    function listScroll(_this,json) {
        var thisScrollTop = _this.scrollTop();
        var thisScrollHeight = _this.height();
        var bottomScrollTop = _this.find('.publicUlstyle').height()+$(".noTitle").height();
        //下拉到底部加载
        if (thisScrollTop + thisScrollHeight+10 >= bottomScrollTop && json.ajaxInow) {
            loading(json);
        }
    };
</script>
</html>
