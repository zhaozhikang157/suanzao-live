<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>酸枣在线</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/home.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
</head>
<body>
<%-- 头部 --%>
<header style="height: 2.5rem;">
    <div class="search_box">
        <div class="searchInput" style="display: -webkit-box;-webkit-box-align: center;-webkit-box-pack: justify;position: relative">
            <form action="javascript:void(0);" style="display: block;-webkit-box-flex: 1;">
                <input type="search" id="searchFn" maxlength="30" placeholder="搜索单节课/系列课/直播间"  style="border:none;" value="" oninput="searchValue(this)">
            </form>
            <span class="searchcloseBtn" data-search="0">取消</span>
            <span class="searclose_index"></span>
        </div>
    </div>
</header>
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
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var navNum = 1; // 设置nav被点击坐标
    var errorMessage = true; // 设置开关
    <%--页面初始化--%>
    $(function(){
        $("#searchFn").focus(function(){
            searchVal= $('#searchFn').val();
            $(".searchcloseBtn").show();
        });
        $(".searclose_index").click(function(){
            $('#searchFn').val("");
            $('.searchcloseBtn').html('取消').attr('data-search','0').css('color','#282828');
            $(".seachPublicclass").show();
            $(".othercourses").empty();
            $(".searchAllbox").hide();
            $(this).hide();
            $(".indexzdata").hide()
        })
        var searchText = BaseFun.GetSession('searchVal');
        if(searchText != '' && searchText != undefined && searchText != null){
            $('#searchFn').val(searchText);
            $('.searchcloseBtn').html('搜索').attr('data-search','1').css('color','#d53c3e');
            $(".searchAllbox").show();
            $('#search_lecturer').show();//搜索内容区
            if($("#searchseries li").length<1){
                $(".coursesX").hide();
            }
            if($("#searchLive li").length<1){
                $(".liveRooms").hide();
            }
            if($("#othercourses li").length<1){
                $(".coursesD").hide();
            }
            searchFn();
        }else{
            BaseFun.RemoveSession('searchVal');
        }
        //   检测ios返回
        var isPageHide = false;
        window.addEventListener("pageshow", function (e) {
            if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)){
                if (isPageHide) {
                    BaseFun.RemoveSession('searchVal');
                    var IndexUrlSearchVal = window.location.search;
                    if(IndexUrlSearchVal.indexOf('searchVal') != -1){
                        window.location.reload();
                    }
                }
            };
        },false);
        window.addEventListener('pagehide', function () {
            isPageHide = true;
        },false);
        $(".searchcloseBtn").click(function(){
            if($(this).attr('data-search') == 0){
                BaseFun.RemoveSession('searchVal');
                window.location.href = '/weixin';
            }else if($(this).attr('data-search') == 1){
                $(".othercourses").empty();
                searchFn();
            }
        });
        $(document).on('touchend',function(){
            $("#searchFn").blur();
        });
        $("header").on('touchstart',function(event){
            event.stopPropagation();
        });
        document.onkeyup = function (e) {//按键信息对象以函数参数的形式传递进来了，就是那个e
            var code = e.charCode || e.keyCode;  //取出按键信息中的按键代码(大部分浏览器通过keyCode属性获取按键代码，但少部分浏览器使用的却是charCode)
            if (code == 13) {
                if($('#searchFn').val() != ''){
                    $(".othercourses").empty();
                    searchFn();
                    $("#searchFn").blur();
                }

            }
        }
        //单节课更多
        $(".oneclassTitle").click(function(){
            $(".coursesX,.liveRooms").show();
            if($(".oneclassTitle").hasClass("on")){
                $(this).removeClass("on").html("更多");
                var liHeight =$("#othercourses li").height()*3+$(".znoTitle").height();
                $(".coursesD").css({height:liHeight,overflow:"hidden",margin_bottom:".25rem" })
                $(".searchAllbox").show();
                if($("#searchseries li").length<1){
                    $(".coursesX").hide();
                }
                if($("#searchLive li").length<1){
                    $(".liveRooms").hide();
                }
                if($("#othercourses li").length<1){
                    $(".coursesD").hide();
                }
            }else{
                $(".coursesX,.liveRooms").hide();
                $(this).addClass("on").html("收起");
                $(".coursesD").css({height:"100%",overflow:"auto",margin_bottom:"0"});

                loading(listInit.mycourse);
                listInit.mycourse.opens=true;
            }
        })
        $(".serrorclass").click(function(){
//        $(this).hide();
            $(".coursesD,.liveRooms").show();
            if($(".serrorclass").hasClass("on")){
                $(this).removeClass("on").html("更多");
                var liHeight =$("#searchseries li").height()*3+$(".xTitle").height();
                $(".coursesX").css({height:liHeight,overflow:"hidden",margin_bottom:".25rem" })
                $(".searchAllbox").show();
                if($("#searchseries li").length<1){
                    $(".coursesX").hide();
                }
                if($("#searchLive li").length<1){
                    $(".liveRooms").hide();
                }
                if($("#othercourses li").length<1){
                    $(".coursesD").hide();
                }
            }else{
                $(".coursesD,.liveRooms").hide();
                $(this).addClass("on").html("收起");
                $(".coursesX").css({height:"100%",overflow:"auto",margin_bottom:"0"})
                loading(listInit.history);
                listInit.history.opens=true;
            }
        })
        $(".seachliveBox").click(function(){
            $(".coursesD,.coursesX").show();
            if($(".seachliveBox").hasClass("on")){
                $(this).removeClass("on").html("更多");
                var liHeight =$("#searchLive li").height()*3+$(".liveTitle").height();
                $(".liveRooms").css({height:liHeight,overflow:"hidden" })
                $(".searchAllbox").show();
                if($("#searchseries li").length<1){
                    $(".coursesX").hide();
                }
                if($("#searchLive li").length<1){
                    $(".liveRooms").hide();
                }
                if($("#othercourses li").length<1){
                    $(".coursesD").hide();
                }
            }else{
                $(".coursesD,.coursesX").hide();
                $(this).addClass("on").html("收起");
                $(".liveRooms").css({height:"100%",overflow:"auto",margin_bottom:"0"})
                loading(listInit.purchase);
                listInit.purchase.opens=true;
            }
        })
        //   单节课
        $('.coursesD').scroll(function(){
            if(!listInit.mycourse.opens){return;}
            listScroll($(this),listInit.mycourse);
        });
        //  系列课
        $('.coursesX').scroll(function(){
            if(!listInit.history.opens){return;}
            listScroll($(this),listInit.history);
        });
        // 直播间
        $('.liveRooms').scroll(function(){
            if(!listInit.purchase.opens){return;}
            listScroll($(this),listInit.purchase);
        });
        // 点击每个Lists下的li做相应的跳转
        $('.publicUlstyle').on('click','.publicUlstyle li',function () {
            var id = $(this).attr('data-id');
            var isSeriesCourse = $(this).attr('data-SeriesCourse');
            toCourseInfo(id,isSeriesCourse);
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
    /*搜索讲师Fn*/
    var search_lecturer;
    var flag = 0;
    function searchValue(_this){
        if( _this.value != ''){
            $('.searchcloseBtn').html('搜索').attr('data-search','1').css('color','#d53c3e');
            $(".searclose_index").show();
        }else{
            $('.searchcloseBtn').html('取消').attr('data-search','0').css('color','#282828');
        }
    }
    function searchFn() {
        flag = 0;
        var searchVal = $('#searchFn').val();
        BaseFun.SetSession('searchVal',searchVal);
        search_lecturer = $('#search_lecturer');//搜索内容区
        if (searchVal.length>0) {
            var param = {name:searchVal,type:0};
            //讲师默认内容和搜索内容切换
            var result = ZAjaxRes({url: "/liveRoom/findCourseAndLiveRoomByCondition",type:"POST",param:param});
            if (result.code == '000000') {
                var coursesD=result.data.coursesD;//单节课
                var coursesX=result.data. coursesX;//系列课
                var liveRooms=result.data.liveRooms;//直播间
                if(coursesD.length<4){
                    $(".oneclassTitle").hide();
                }else{
                    $(".oneclassTitle").show();
                }
                if(coursesX.length<4){
                    $(".serrorclass").hide();
                }else{
                    $(".serrorclass").show();
                }
                if(liveRooms.length<4){
                    $(".seachliveBox").hide();
                }else{
                    $(".seachliveBox").show();
                }
                //单节课
                $.each(coursesD,function(i,n){
                    if(i>2){
                        return;
                    }
                    //直播类型
                    var liveWayDesc = "";
                    if (n.liveWay == '1'){
                        liveWayDesc = "audioIco";
                    }else if(n.liveWay == '0') {
                        liveWayDesc = "videoIco";
                    }
                    //转播课
                    var relay_icon="";
                    if(n.isRelay=="1"){
                        relay_icon = '<i class="relay_icon"></i>';
                    }else{
                        relay_icon ="";
                    }
                    //开播状态
                    var liveingpic="playbacks";
                    if(n.statusStr=="0"){
                        liveingpic='playbacks';
                    }else if(n.statusStr=="1"){
                        liveingpic='backlookout';
                    }else if(n.statusStr=="2"){
                        liveingpic='liveingpic';
                    }
                    //课程金额
                    var free = "免费";
                    var freeClass = "free";
                    if (n.chargeAmt > 0) {
                        free = "<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
                        freeClass = "";
                    }
                    var isSeriesCourse= n.isSeriesCourse
                    var userTitle = (n.topic).toString();
                    var reg = new RegExp("(" + searchVal + ")", "ig");
                    var str = userTitle;
                    var newstr = str.replace(reg, '<i class=gulesColor>$1</i>');

                    var li = $('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'">');
                    $("#othercourses").append(li);
                    li.append('<div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center;background-size:cover;">' +
                            '<em class="projection">' +
                            '<i class="'+liveingpic+'"></i><span class="playNumber">' + n.joinCount + '人</span><i class="'+liveWayDesc+'"></i></em>' +
                            '</div>' +
                            '<p><strong class="title">'+relay_icon+newstr+'</strong></p><p><span class="name">主讲：'+n.userName+'</span></p>' +
                            '<p><span class="time">'+n.startTimeStr+'</span><strong class="money '+freeClass+'"><span class="tapfontWei">'+free+'</span></strong></p>');

                })
                if(coursesD.length>=1){
                    $(".coursesD").show();
                }else{
                    $(".coursesD").hide();
                }
                //系列课
                var html = '';
                $.each(coursesX, function (i, item) {
                    if(i>2){
                        return;
                    }
                    //是否是系列课
                    var isSeriesCourse = item.isSeriesCourse;
                    //课程金额
                    var free = "免费";
                    var freeClass = "free";
                    var hed="";
                    //转播课
                    var relay_icon="";
                    if(item.isRelay=="1"){
                        relay_icon = '<i class="relay_icon"></i>';
                    }else{
                        relay_icon ="";
                    }
                    if (item.chargeAmt > 0) {
                        free = "<span class='tapfontWei'>"+item.chargeAmt+"</span>"+"学币";
                        freeClass = "";
                        hed="hed"
                    }
                    if(isSeriesCourse == '0'){
                        var oHtml = '主讲：'+item.userName;
                    }else{
                        var oHtml = "<span class='series'></span>"+'系列课 | 已更新'+item.updatedCount+'节'
                    }
                    //转播课
                    var relay_icon="";
                    if(item.isRelay=="1"){
                        relay_icon = '<i class="relay_icon"></i>';
                    }else{
                        relay_icon ="";
                    }
                    //开播状态
                    var liveingpic="playbacks";
                    if(item.statusStr=="0"){
                        liveingpic='playbacks';
                    }else if(item.statusStr=="1"){
                        liveingpic='backlookout';
                    }else if(item.statusStr=="2"){
                        liveingpic='liveingpic';
                    }
                    var userTitle = (item.topic).toString();
                    var reg = new RegExp("(" + searchVal + ")", "ig");
                    var str = userTitle;
                    var newstr = str.replace(reg, '<i class=gulesColor>$1</i>');
                    var startCount = item.joinCount  + '人气';
                    html += '<li data-id="'+item.id+'" data-SeriesCourse="'+isSeriesCourse+'">' +
                            '<div class="img" style="background:url('+item.coverssAddress+') no-repeat center center; background-size:cover;">' +
                            '<em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">'+startCount+'</span><i class="'+hed+'"></i></em>' +
                            '</div>' +
                            ' <p><strong class="title">'+relay_icon+newstr+'</strong></p>' +
                            '<p><span class="name">'+oHtml+'</span></p>' +
                            '<p><span class="time">'+item.startTimeStr+'</span><strong class="money '+freeClass+'"><span class="tapfontWei">'+free+'</span></strong></p>' ;
                })
                $("#searchseries").append(html);
                if(coursesX.length>=1){
                    $(".coursesX").show();
                }else{
                    $(".coursesX").hide();
                }
                //直播间
                $.each(liveRooms, function (i, item) {
                    if(i>2){
                        return;
                    }
                    var userTitle = (item.name).toString();
                    var reg = new RegExp("(" + searchVal + ")", "ig");
                    var str = userTitle;
                    var newstr = str.replace(reg, '<i class=gulesColor>$1</i>');
                    var li = $('<li onclick="liveRom('+ item.id+')">');
                    $("#searchLive").append(li);
                    li.append(' <div class="liveUserpic" style="background:url('+item.coverssAddress+') no-repeat center center; background-size:cover;"></div>' +
                            '<p><span class="liceUsertitle">'+newstr+'</span><i class="searchFloow">'+item.followNum+'人关注</i></p>')

                })

                if(liveRooms.length>=1){
                    $(".liveRooms").show();
                }else{
                    $(".liveRooms").hide();
                }
            }
            if(coursesD.length==0&&coursesX.length==0&&liveRooms.length==0){
                $(".indexzdata").show();
                $(".searchAllbox").hide();
            }else{
                $(".indexzdata").hide();
            }
            search_lecturer.show();
            listInit = {
                'mycourse':{
                    'name' : 'coursesD',
                    'ajaxInow' : true,
                    'offset' :3,
                    'username':"searchVal",
                    'opens':false
                },
                'history':{
                    'name' : 'coursesX',
                    'ajaxInow' : true,
                    'offset' : 3,
                    'username':"searchVal",
                    'opens':false
                },
                'purchase':{
                    'name' : 'liveRooms',
                    'ajaxInow' : true,
                    'offset' : 3,
                    'username':"searchVal",
                    'opens':false
                }
            };
        }else{
            $(".searclose_index").hide();
            $(".searchAllbox").hide();
        }
    }
    var searchVal;
    function loading(json) {
        searchVal = $('#searchFn').val();
        switch(json.name){
            case 'coursesD':
                $(".coursesX,.liveRooms").hide();
                $(".coursesD").css({height:"100%",overflow:"auto"})
                if(!json.ajaxInow){return};
                listInit.mycourse.ajaxInow = false;
                var result = ZAjaxRes({url: "/liveRoom/findCourseByLiveTopic", type: "POST",param:{offset:json.offset,name:searchVal,isSeriesCourse:0,pageSize:10}});
                if (result.code == "000000") {
                    var mycourse = result.data;
                    var size = mycourse.length;
                    $('#othercourses').append(listsHtmlRendering(mycourse));

                    listInit.mycourse.offset= $('#othercourses li').length;
                    listInit.mycourse.ajaxInow = true;

                }else if(result.code == "000110"){
                    if($('#othercourses li').length < 1){
                        $('#othercourses').append('<div class="noData"></div>');
                    }
                    listInit.mycourse.ajaxInow = false;
                }else{
                    listInit.mycourse.ajaxInow = true;
                }
                break;
            case 'coursesX':
                if(!json.ajaxInow){return};
                $(".coursesD,.liveRooms").hide();
                $(".coursesX").css({height:"100%",overflow:"auto"})
                listInit.history.ajaxInow = false;
                var result = ZAjaxRes({url: "/liveRoom/findCourseByLiveTopic", type: "POST",param:{offset:json.offset,name:searchVal,isSeriesCourse:1,pageSize:10}});
                if (result.code == "000000") {
                    var mycourse = result.data;
                    var size = mycourse.length;
                    $('#searchseries').append(listsHtmlRendering(mycourse));

                    listInit.history.offset=$('#searchseries li').length;
                    listInit.history.ajaxInow = true;

                }else if(result.code == "000110"){
                    if($('#searchseries').length < 1 && $('#searchseries .noData').length < 1){
                        $('#searchseries').append('<div class="noData"></div>');
                    }
                    listInit.history.ajaxInow = false;
                }else{
                    listInit.history.ajaxInow = true;
                }
                break;
            case 'liveRooms':
                if(!json.ajaxInow){return};
                $(".liveRooms").css({height:"100%",overflow:"auto"})
                $(".coursesD,.coursesX").hide();
                listInit.purchase.ajaxInow = false;
                var result = ZAjaxRes({url: "/liveRoom/findLiveRoomByNamePage", type: "POST",param:{offset:json.offset,name:searchVal,pageSize:10}});
                if (result.code == "000000") {
                    var mycourse = result.data;
                    var size = mycourse.length;
                    $('#searchLive').append(puBlicsearchLive(mycourse));

                    listInit.purchase.offset= $('#searchLive li').length;
                    listInit.purchase.ajaxInow = true;

                }else if(result.code == "000110"){
                    if($('#searchLive li').length < 1 && $('#searchLive .noData').length < 1){
                        $('#searchLive').append('<div class="noData"></div>');
                    }
                    listInit.purchase.ajaxInow = false;
                }else{
                    listInit.purchase.ajaxInow = true;
                }
                break;
            default:
                return false;
        }
    };
    //  公共渲染lists列表数据转换成html格式
    function listsHtmlRendering(mycourse){
        var arrHtml = [];
        $.each(mycourse, function (i, n) {
            //是否是系列课
            var hed="";
            var isSeriesCourse = n.isSeriesCourse;
            if(isSeriesCourse == '0'){
                var oHtml = '主讲：'+n.userName;
            }else{
                hed="hed";
                var oHtml = "<span class='series'></span>"+'系列课 | 已更新'+n.updatedCount+'节'
            }

            var userTitle = (n.topic).toString();
            var searchVal= $('#searchFn').val()
            var spanId = '';
            var reg = new RegExp("(" + searchVal + ")", "ig");
            var str = userTitle;
            var spanId = str.replace(reg, '<i class=gulesColor>$1</i>');

            //直播类型
            var liveWayDesc = "";
            if (n.liveWay == '1'){
                liveWayDesc = "audioIco";
            }else if(n.liveWay == '0') {
                liveWayDesc = "videoIco";
            }
            //转播课
            var relay_icon="";
            if(n.isRelay=="1"){
                relay_icon = '<i class="relay_icon"></i>';
            }else{
                relay_icon ="";
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
                liveingpic='playbacks';
            }else if(n.statusStr=="1"){
                liveingpic="backlookout"
            }else if(n.statusStr=="2"){
                liveingpic='liveingpic';
            }
            arrHtml.push('<li data-id="'+n.id+'" data-SeriesCourse="'+isSeriesCourse+'"><div class="img" style="background:url('+ n.coverssAddress+') no-repeat center center;background-size:cover;">' +
                    '<em class="projection">' +
                    '<i class="'+liveingpic+'"></i><span class="playNumber">' + n.joinCount + '人</span><i class="'+liveWayDesc+'"></i></em>' +
                    '</div>' +
                    '<p><strong class="title">'+relay_icon+spanId+'</strong></p><p><span class="name">'+oHtml+'</span></p>' +
                    '<p><span class="time">'+n.startTimeStr+'</span><strong class="money '+freeClass+'">' + free + '</strong></p></li>');
        });
        return arrHtml.join('');
    };
    //直播间
    function puBlicsearchLive(mycourse){
        var arrHtml = [];
        $.each(mycourse, function (i,n) {
            var userTitle = (n.name).toString();
            var searchVal= $('#searchFn').val()
            var spanId = '';
            var reg = new RegExp("(" + searchVal + ")", "ig");
            var str = userTitle;
            var spanId = str.replace(reg, '<i class=gulesColor>$1</i>');
            var li = $('<li onclick="liveRom('+ n.id+')">');
            $("#searchLive").append(li);
            li.append(' <div class="liveUserpic" style="background:url('+n.coverssAddress+') no-repeat center center; background-size:cover;"></div>' +
                    '<p><span class="liceUsertitle">'+spanId+'</span><i class="searchFloow">'+n.followNum+'人关注</i></p>')

        });
        return arrHtml.join('');
    };
    // 直播间跳转
    function liveRom(id){
        window.location.href = "/weixin/liveRoom?id=" + id;
    }
</script>
</html>
