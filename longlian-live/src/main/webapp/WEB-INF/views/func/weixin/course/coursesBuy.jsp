<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>已购买课程</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrapper" style="height:100%; background:#FFF;-webkit-overflow-scrolling: touch;overflow: hidden">
    <div id="cont_box"class="new_index" style="padding-bottom:0;">
        <!--已购买课程-->
        <ul id="Admission" style="overflow-y: scroll"></ul>
        <div class="pullUpLabel" style="border-top: none;display: none ">上拉加载更多</div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var isPageHide = false;
    if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)){
        window.addEventListener("pageshow", function (e) {
            if (isPageHide) {
                window.location.reload();
            }
        },false);
        window.addEventListener('pagehide', function () {
            isPageHide = true;
        },false);
    };
    var liveVoiceDesc = "audioIco";
    var pageNum = 1, pageSize = 10;
    var oBok = true;
    $(function () {
        Load();
        $('#Admission').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#Admission').find('li').height()* $('#Admission').find('li').length-windowHeight;
            if(scrollTop + windowHeight >= scrollHeight-1){
                Load(2);
            }
        });
    });
    function Load(isOne) {
        if (oBok) {
            var result = ZAjaxRes({
                url: "/joinCourseRecord/getMyBuyCourseListV3.user?pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: "GET"
            });
            if (result.code == "000000") {
                var recordList = result.data;//推荐
                var listHtml = '';
                if (recordList.length > 0) {
                    $.each(recordList,function(index,obj){
                        var time = obj.dateStr;
                        if(isOne==2){
                            var lastP= $("#Admission em.laseP:last").text();
                            if(time!=lastP){
                                listHtml+='<em class="laseP">'+time+'</em>'
                            }
                        }else{
                            listHtml+='<em class="laseP">'+time+'</em>'
                        }
                        var list = obj.dataList
                        $.each(list,function(k,n){
                            var liveWayDesc = "videoIco";
                            var free = "免费";
                            var hed="";
                            var freeClass = "free";
                            var allimg= "";
                            var relayIcon = '';
                            //直播状态
                            var liveingpic="playbacks";
                            if(n.liveTimeStatus=="0"){
                                liveingpic="backlookout"
                            }else if(n.liveTimeStatus=="1"){
                                liveingpic="liveingpic"
                            }if(n.liveTimeStatus=="2"){
                                liveingpic="playbacks"
                            }
                            if (n.liveWay == ('语音直播')){
                                liveWayDesc = "audioIco"
                            }else{
                                liveWayDesc = "videoIco"
                            }
                            if (n.chargeAmt > 0) {
                                free ="<span class='tapfontWei'>"+n.chargeAmt+"</span>"+"学币";
                                freeClass = "";
                            }
                            var isSeriesCourse= n.isSeriesCourse;
                            var count = n.visitCount;
                            var iscourBox = "";
                            if(isSeriesCourse==1){
                                hed="hed"
                                iscourBox= "系列课：已更新"+ n.updatedCount+"节";
                                if(n.updatedCount=="0"){
                                    liveingpic=hed;
                                }
                                allimg ="allimg"
                            }else{
                                iscourBox="主播："+ n.name+"";
                                allimg =""
                            }
                            if(n.id.toString().length >= 15 ){
                                relayIcon = '<i class="relay_icon"></i>';
                            }
                            listHtml += '<li onclick="courseInfo('+n.id+','+n.isSeriesCourse+')"><div class="img" style="background: url('+ n.coverssAddress + ') no-repeat center center; background-size:cover;"><em class="projection"><i class="'+liveingpic+'"></i><span class="playNumber">' + count + '人</span><i class="'+liveWayDesc+hed+'"></i></em></div><p class="titDiv"><strong class="title">' +relayIcon+ n.liveTopic + '</strong></p>' +
                                    '<p><span class="name '+allimg+'">'+iscourBox+'</span></p>' +
                                    '<p><span class="time">' + n.startTime + '</span><strong class="money '+freeClass+'">' + free + '</strong></p></li>';
                        })
                    })
                        $("#Admission").append(listHtml);
                        if(recordList.length<10){
                            $('.pullUpLabel').show();
                            $('.pullUpLabel').text('加载已经完毕！');
                        }
                    pageNum++;
                } else {
					$('.pullUpLabel').show();
					$('.pullUpLabel').text('加载已经完毕！');
                    oBok = false;
                }
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        }
        else {
            if (!oBok) {
                $('.pullUpLabel').text('没有更多内容了！');
            }
        }
    };
   	function courseInfo(courseId,isSeriesCourse) {
        if(isSeriesCourse==1){
            window.location.href = "/weixin/courseInfo?isFrom=1&id=" + courseId+"&isSeries="+1;
        }else{
            window.location.href = "/weixin/courseInfo?isFrom=1&id=" + courseId ;
        }
    };
</script>
</html>
