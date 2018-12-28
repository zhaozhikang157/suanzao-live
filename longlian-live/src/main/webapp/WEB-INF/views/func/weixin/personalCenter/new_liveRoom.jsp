<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title id="liveName">正在加载...</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="topBoxcon">
    <div class="topBoxcontap">
        <h2>提示</h2>
        <p>您好，您直播内容涉及到不良内容，已被用户投诉，如有疑问请咨询官方客服，<a href="tel:400-116-9269">400-116-9269</a></p>
        <div class="tapCloseindex">好的</div>
    </div>
</div>
<div class="guide">
    <div class="guiLivebox">

    </div>
</div>
<div class="guideNew">
    <div class="guiLivebox1">

    </div>
</div>
<div class="paymentmethod" style="display: none">
    <div class="mask">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>

        <p>通过【发送给朋友】</p>

        <p>邀请好友参与</p>
    </div>
</div>
<div id="wrapper" style="overflow:hidden;max-width: 18.75rem;margin: 0 auto">
    <div id="cont_box" style="padding-bottom: 0">
        <div class="prompting">
            <p>
                <span class="promptingClosebtn"></span>
                <i class="promptingUser"style="background: url(/web/res/image/newaudio/default@2x.png) no-repeat center center;background-size: 100% 100%"></i>
                <i class="promptingName">酸枣在线</i>
            </p>
            <i class="promptingBtn">关注公众号</i>
        </div>
        <div class="liveroomtop" id="liveroomtop"
             style="background: url(/web/res/image/ccpic.png) no-repeat center center;background-size:cover">
            <div class="liveroomzc">
                <div class="room_setup" id="roomSetup"><a><img src="/web/res/image/setup.png" id="photo"></a></div>
                <dl class="details">
                    <dt id="backstagebox"
                        style="background: url(/web/res/image/photo.jpg) no-repeat center center;background-size: 100% 100%"></dt>
                    <dd>
                        <span class="poBox"><p class="detailsname" id="detailsname"></p><i class="gzf"></i></span>

                        <p class="gznumber"><i class="follows"></i><i class="tapsId newtapsId"></i></p>
                    </dd>
                </dl>
            </div>
        </div>
        <div class="btnBox">
            <p class="newcouse" id="newcouse">创建单节课</p>
            <p id="newseries">创建系列课</p>
        </div>
        <ul class="livenav">
            <li CourseType="singleClass" class="on">单节课</li>
            <li CourseType="seriesOflessons">系列课</li>
            <li CourseType="">分享</li>
        </ul>
        <div id="lecturer_main" class="lecturer_main">
            <div class="swiper-wrapper">
                <div class="swiper-slide mycourse-list" id="first">
                    <ul id="prevueList"class="publicUlstyle">

                    </ul>
                </div>
                <div class="swiper-slide mycourse-list" id="secret">
                    <ul id="seriesList" class="publicUlstyle">

                    </ul>
                </div>
                <div class="swiper-slide" >
                    <div class="sharing">
                        <p class="wx" onclick="statisticsBtn({'button':'005','referer':'005001','roomId':${id}})">微信</p>

                        <p class="pyq"onclick="statisticsBtn({'button':'006','referer':'006001','roomId':${id}})">朋友圈</p>

                        <p class="yqk" id="yqk"onclick="statisticsBtn({'button':'007','referer':'007001','roomId':${id}})">邀请卡</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="ewmbox">
    <img class="sicx imgsrc"  alt="" style="position: absolute;z-index:1111;opacity: 0;width: 12.5rem;height: 12.5rem;margin-left: -6rem;left: 50%;top:4.3rem;"/>
    <div class="kfewm">
        <div class="kfewm_img"></div>
        <div class="wxpic">
            <img  class="imgsrc" alt=""/>
            <i style="font-size:0.65rem;">长按关注将获得开课提醒</i>
        </div>
        <div class="qdBtn">我知道了</div>
    </div>
</div>
<%--更多--%>
<div class="more_box">
    <div class="more_list">
        <div class="freeofcharge">
            <span class="more_tit"></span>
            <ul class="more_btn_list">
                <li>下架课程<span class="down_coures_btn"></span></li>
            </ul>
        </div>
        <div class="more_btn">
            <p class="more_c">取消</p>
        </div>
    </div>
</div>
</body>
<style>
    .kfewm{
        width: 15.5rem;
        height: 14rem;
        background: white;
        border-radius: 14px;
    }
    .wxpic{
        width: 11rem;
        height: 8.8rem;
        margin: 0 auto;
        display: -webkit-box;
        -webkit-box-align: center;
        -webkit-box-pack: center;
        -webkit-box-orient: vertical;
        font-size: 0.7rem;
        color: #6f6f6f;
        background: white;
    }
    .wxpic img{
        display: block;
        width: 6rem;
        height: 6rem;
        margin-bottom: 0.5rem;
    }
    .kfewm_img{ width:5.825rem; height:4.7rem; margin:-2.1rem auto 0 auto; background:url(/web/res/image/kfewm_img.png) no-repeat center center; background-size:5.825rem 4.7rem;}
    .qdBtn{ width:100%; height:2.4rem; font-size:.75rem; color:#007aff; text-align:center; line-height:2.4rem; border-top:1px #e0e0e0 solid;}
    .publicUlstyle li p .title em{
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        line-height: .9rem;
    }
</style>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<script>
    var id = '${id}';//直播室ID
    var isTearcher = '${isTeacher}';//是不是老师自己1-是 0-不是
    var liveVoiceDesc = "audioIco";
    var tId = ${tId};
    var followAppId = 0;
    var appId = 0;
    var CourseType = 'singleClass';
    var isFollowThirdOfficial = "";
    var qrCode = "";
    var isThird = "";
    var courseId = "";
    var isFollow=${isFollow};
    var seriesId = '';
    var isUserNow = false;

    share_h5({systemType: 'LIVE_ROOM', liveRoomId: id, seriesid: seriesId, channel: "",return_url:'true'});//分享
    //数据列表组件
    var newlistInit = {
        'prevueList':{
            'name' : 'prevueList',
            'ajaxInow' : true,
            'pageNum' :1
        },
        'seriesList':{
            'name' : 'seriesList',
            'ajaxInow' : true,
            'offset' :0
        }
    };

    /*直播 精选 下拉加载*/
    $("#first").scroll(function(){
        listScrolls($(this),newlistInit.prevueList);
    });
    $("#secret").scroll(function(){
        listScrolls($(this),newlistInit.seriesList);
    });
    function listScrolls(_this,json) {
        var thisScrollTop = _this.scrollTop();
        var thisScrollHeight = _this.height();
        var bottomScrollTop = _this.find('.publicUlstyle').height();
        //下拉到底部加载
        if (thisScrollTop + thisScrollHeight+10 >= bottomScrollTop && json.ajaxInow) {
            Load(json);
        }
    };

    Load(newlistInit.prevueList);
    Load(newlistInit.seriesList);


    //首页主体内容
    newindex = new Swiper('#lecturer_main', {
        direction : 'horizontal',
        autoplay:0,
        observer:true,
        observeParents:true,
        resistanceRatio : 0,
        iOSEdgeSwipeDetection : true,
        iOSEdgeSwipeThreshold : 20,
        touchReleaseOnEdges:true,
        onSlideChangeStart: function(swiper){
            $('.livenav li').removeClass('on');
            $('.livenav li').eq(swiper.activeIndex).addClass('on');
        }
    });
    $("#photo").on('click', function () {
        if (tId==appId){
            window.location.href = "/weixin/liveRoomSet?id=" + id;
        };
    });
	var singleOK = true;
	var seriesOK = true;
    var name_grable = "";

    function Load(json) {
        switch (json.name) {
            //单节课
            case 'prevueList':
                if(!json.ajaxInow){return};
                newlistInit.prevueList.ajaxInow = false;
                var result = ZAjaxRes({url: "/user/getCourseList?roomId="+id, type: "GET",param:{pageSize:10,pageNum:json.pageNum}});
                if (result.code == "000000" || result.code == "000110") {
                    if(json.pageNum==1){
                        //直播间数据回显
                        var liveRoom = result.data.liveRoom;
                        var appUser = result.data.appUser;
                        var liveAppUser = result.data.liveAppUser;
                        followAppId = liveAppUser.id;
                        appId = appUser.id;
                        if(followAppId!=appId){
                            $("#roomSetup").hide();
                        }else{
                            isUserNow = true;
                        }
                        $("#detailsname").text(liveRoom.name);

                        $("#liveroomtop").attr("style", "background:url(" + result.data.liveRoom.coverssAddress + ") no-repeat center center;background-size:cover;");
                        var name = "";
                        var photo = "";
                        //不是当前用户的直播间
                        if (appUser.id != liveRoom.appId) {
                            $(".btnBox").remove();
                            $(".gzf").css("display",'inline-block');
                            if(isFollow){
                                $(".gzf").addClass("on");
                            }else{
                                $(".gzf").removeClass("on");
                            }
                            tId = liveRoom.appId;
                            name = liveAppUser.name;
                            photo = liveAppUser.photo;
                            $(".lecturer_main").css("top","9.34rem");
                            $(".mycourse-list").css("padding-bottom","9.3rem")
                            $("#liveName").html(name);
                            $("#backstagebox").attr("style", "background:url(" + liveAppUser.photo + ") no-repeat center center;background-size: 100% 100%");
                        } else {
                            $(".lecturer_main").css("top","12.32rem");
                            $(".mycourse-list").css("padding-bottom","12.3rem")
                            $("#liveName").html("我的直播间");
                            name = appUser.name;
                            photo = appUser.photo;
                            $("#backstagebox").attr("style", "background:url(" + appUser.photo + ") no-repeat center center;background-size: 100% 100%");
                        }
                        //第三方
                        isFollowThirdOfficial = result.data.isFollowResult.isFollowThirdOfficial; //是否关注
                        qrCode = result.data.isFollowResult.qrCode;//二维码
                        isThird = result.data.isFollowResult.isThird; //是否是第三方
                        if(qrCode=="") {
                            $.getJSON("/qrCode/getWechatOfficialQrCode.user?courseId=" + courseId, function (result) {
                                if (result.code == '000000') {
                                    qrCode = result.data;
                                }
                            })
                        };
                        if(isFollowThirdOfficial == "1" ){
                            //第三方
                            if(isThird){
                                $('.promptingUser').css('background','url('+photo+') no-repeat center center;');
                                $('.promptingName').html(name);
                            }
                            $('.imgsrc').attr('src',qrCode);
                            $(".prompting").show();
                            $('.promptingBtn').on('click',function(){
                                $('.ewmbox').css('display','-webkit-box');
                            })
                        }else{
                            $(".prompting").hide();
                        }

                        $('.ewmbox').on('click',function(e){
                            if(this == e.target){
                                $(this).hide();
                            }
                        });
                        $('.qdBtn').on('click',function(){
                            $('.ewmbox').hide();
                        });
                        name_grable = name;
                        var followNum = result.data.followNum;//直播间关注数
                        $(".follows").text(followNum + "人关注");
                        $(".tapsId").html('直播间ID:'+id);
                    }
                    var courseList = result.data.courseList;
                    if(result.code == "000110" || !courseList  || courseList.length == 0 ) {
                        if ($('#prevueList li').length < 1 && $('#prevueList .noData').length < 1) {
                            $('#prevueList').append('<div class="noData"></div>');
                        }
                        newlistInit.prevueList.ajaxInow = false;
                    } else {
                        var size = courseList.length;
                        $('#prevueList').append(pubicHtml(courseList , 0));
                        if(size<10){
                            newlistInit.prevueList.ajaxInow = false;
                        }else{
                            newlistInit.prevueList.pageNum++;
                            newlistInit.prevueList.ajaxInow = true;
                        }
                    }
                } else{
                    newlistInit.prevueList.ajaxInow = true;
                }
                break;
            //系列课
            case 'seriesList':
                if(!json.ajaxInow){return};
                newlistInit.seriesList.ajaxInow = false;
                var result = ZAjaxRes({url: "/user/getSeriesCourseList.user?roomId="+id, type: "GET",param:{pageSize:10,offset:json.offset}});
                if (result.code == "000000") {
                    var liveingList = result.data;//直播
                    var size = liveingList.length;
                    $('#seriesList').append(pubicHtml(liveingList , 1));
                    if(size<10){
                        newlistInit.seriesList.ajaxInow = false;
                    }else{
                        newlistInit.seriesList.offset=$('#seriesList li').length;
                        newlistInit.seriesList.ajaxInow = true;
                    }
                }else if(result.code == "000110"){
                    if($('#seriesList li').length < 1 && $('#seriesList .noData').length < 1){
                        $('#seriesList').append('<div class="noData"></div>');
                    }
                    newlistInit.seriesList.ajaxInow = false;
                }else{
                    newlistInit.seriesList.ajaxInow = true;
                }
                break;
                default:
                return false;
        }
    };

    //  公共渲染lists列表数据转换成html格式
    function pubicHtml(mycourse,type){
        var arrHtml = [];
        $.each(mycourse, function (i, n) {
            var hed="";
            var liveWayDesc = "videoIco";
            var free = "免费";
            var freeClass = "free";
            if (n.liveWay == ('语音直播')) liveWayDesc = liveVoiceDesc;
            if (n.chargeAmt > 0) {
                free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
                freeClass = "";
            }
            //直播状态
            var liveingpic="playbacks";
            if(n.liveTimeStatus=="0"){
                liveingpic="backlookout"
            }else if(n.liveTimeStatus=="1"){
                liveingpic="liveingpic"
            }if(n.liveTimeStatus=="2"){
                liveingpic="playbacks"
            }
            var ohtml ="";
            var starTime="";
            var isMyCourse = '';
            var isSeriesC = '0'
            if(n.isSeriesCourse==1){
                //直播状态
                var liveingpic="playbacks";
                if(n.liveTimeStatus=="0"){
                    liveingpic="playbacks"
                }else if(n.liveTimeStatus=="1"){
                    liveingpic="playbacks"
                }if(n.liveTimeStatus=="2"){
                    liveingpic="liveingpic"
                }
                isSeriesC = 1;
                ohtml='系列课 | 已更新'+n.updatedCount+'节';
                starTime= n.startTimeStr;
                if(n.updatedCount==0){
                    hed="hed"
                }
            }else{
                ohtml='主讲：'+ n.userName+'';
                starTime= n.startTime;
            }
            var mhtml = '<span class="project_more" data-id="'+ n.id +'" data-SeriesCourse="'+ isSeriesC +'"></span>'; // 更多操作
            if(!isUserNow){
                mhtml = '';
            }
            if( n.id .toString().length>=15){
                isMyCourse = '<i class="relay_icon"></i>';
            }else{
                isMyCourse="";
            }
            arrHtml.push('<li c_id=' + n.id + ' type='+type+'>' +
                    '<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:cover;" >' +
                    '<em class="projection"><i class="'+liveingpic+hed+'"></i><span class="playNumber">' +  n.joinCount + '人</span><span class="'+liveWayDesc+hed+'"></span></em></div>'+
                    '<p><strong class="title"><em>'+isMyCourse + n.liveTopic + '</em></strong>'+mhtml+'</p>' +
                    '<p><span class="name">'+ohtml+'</span></p>' +
                    '<p><span class="time">'+ starTime+'</span><strong class="moneys '+freeClass+'">' + free + '</strong></p>' +
                    '</li>');
        });
        return arrHtml.join('');
    };

    $('.publicUlstyle').on('click','.project_more',function(){
        $('.more_btn_list>li').eq(0).attr({'data-isSC':$(this).attr('data-SeriesCourse'),'data-id':$(this).attr('data-id'),'data-index':$(this).parents('li').index()});
        $('.more_tit').html($(this).prev('.title').children('em').html());
        $('.more_box').show();
    })
    $('.publicUlstyle').on('click','.publicUlstyle li',function (e){
        var cId = $(this).attr("c_id"); //单节课或者系列课ID
        var type = $(this).attr("type"); // 0:单节课 1:系列课
        if ($(e.target).closest('.project_more').length !== 0 ){
            return;
        }
        if(type == 0){
            window.location.href = "/weixin/courseInfo?id="+cId;
        }if(type == 1){
            if (appId != tId) {
                window.location.href = "/weixin/courseInfo?id=" + cId +"&isSeries=1";
            }else {
                window.location.href = "/weixin/teacherSeries?seriesid=" + cId;
            }
        }
    })
    $('.more_btn_list li').on('click',function(){
        var result;
        var isSC = $(this).attr('data-isSC');
        var id = $(this).attr('data-id');
        var _index = $(this).attr('data-index');
        BaseFun.Dialog.Config2({
            title: '提示',
            text : '是否下架此课程，下架后不可恢复',
            cancel:'取消',
            confirm:'确定',
            close:false,
            callback:function(index) {
                if(index == 1){
                    if(isSC == 0){
                        result = ZAjaxRes({url: "/course/setCourseDown.user?id="+id, type: "GET"});
                    }else{
                        result = ZAjaxRes({url: "/course/closeSeries.user?courseId="+id, type: "GET"});
                    }
                    if(result.code == '000000'){
                        if(isSC == 0){
                            $('#first .publicUlstyle li').eq(_index).hide();
                        }else{
                            $('#secret .publicUlstyle li').eq(_index).hide();
                        }
                        pop1({"content": "下架成功" , "status": "normal", time: '2500'});
                    }else{
                        pop({"content": result.message , "status": "normal", time: '2500'});
                    }
                }
                $('.more_box').hide();
            }
        });

    })
    $(".more_box").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".freeofcharge").length == 0) {
            $('.more_box').hide();
        }
    });

    $('.livenav li').on('click',function(){
        $('.livenav li').removeClass('on');
        $(this).addClass('on');
        newindex.slideTo($(this).index());
    });

    $(".wx,.pyq").click(function (event) {
        event.stopPropagation();
        $(".paymentmethod").show();
    })

    $(".wx").on("touchstart",function(){
        $(this).addClass("wx1");
        $(".paymentmethod").show();
        return false;
    })
    $(".wx").on("touchend",function(){
        $(this).removeClass("wx1")
    })
    $(".pyq").on("touchstart",function(){
        $(this).addClass("pyq1");
        $(".paymentmethod").show();
        return false;
    })
    $(".pyq").on("touchend",function(){
        $(this).removeClass("pyq1")
    })
    $(".yqk").on("touchstart",function(){
        $(this).addClass("yqk1");
        return false;
    })
    $(".yqk").on("touchend",function(){
        $(this).removeClass("yqk1");
        if(appId == followAppId){
            window.location.href = "/weixin/inviCard?roomId=" + id + "&appId=" + followAppId;
        }else{
            window.location.href = "/weixin/inviCard?roomId=" + id + "&appId=" + followAppId + "&type=1";
        }
    })

    $(".mask").click(function () {
        $(".paymentmethod").hide();
    })
    var result = ZAjaxJsonRes({url: "/liveRoom/isForbiddenRoom.user", type: "GET"});
    $("#newcouse").on('click', function () {
        if( result.code=="100057"){
            $(".topBoxcon").show();
            return;
        }
        $(".guide").hide();
        $(".guideNew").hide();
        statisticsBtn({'button':'001','referer':'001003','roomId':${id}})
        window.location.href = "/weixin/createSingleCourse?id=" + id;
    });
	$("#newseries").on('click',function(){
        if( result.code=="100057"){
            $(".topBoxcon").show();
            return;
        }
        $(".guide").hide();
        $(".guideNew").hide();
        statisticsBtn({'button':'001','referer':'001004','roomId':${id}})
        window.location.href = "/weixin/createSeriesCourse?roomId=" + id;
	})
    $(".guide").click(function(){
        $(this).remove();
        $(".guideNew").show();
    })
    $(".guideNew").click(function(){
        $(this).remove();
    })
    if(handlerNavigationVisitRecord("liveRoom" ,"<%=sytem_new_version%>" , "<%=longlian_live_user_web_navigation_sign%>")){
        $(".guide").show();
    }
    $(".tapCloseindex").click(function(){
        $(".topBoxcon").hide();
    });
    if(isTearcher==0){
        $(".guide,.guideNew").hide();
    }
    $(".promptingClosebtn").click(function(){
        $(".prompting").hide()
    });
    $(".poBox").on('click','.gzf',function(){
        if ($(this).hasClass("on")) {
            follow(this, 1);
        } else {
            follow(this, 0);
        }
    });
    function follow(obj, isFollow) {
        if (isFollow == 0) {
            var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + id, type: "GET"});
            if (result.code == "000000" || result.code == "000111") {
                $(obj).addClass("on");
                $(obj).find("i").html("已关注");
            }
        } else {
            var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + id, type: "GET"});
            if (result.code == "000000") {
                $(obj).removeClass("on");
                $(obj).find("i").html("关注")
            }
        }
    }

</script>
</html>
