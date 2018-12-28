<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>我的打赏收益</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body style="-webkit-overflow-scrolling:touch; ">
<div id="wrapper"style=" overflow: hidden; height:100%;background: white">
    <div id="cont_box" style="height:100%">
        <div id="newlivStyle"style="overflow-y:scroll;padding-bottom: 0;height: 100%;">
            <div class="myrewardbox">

            </div>
        </div>

    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    //0视频直播 1语音直播
    var offset = 0;
    var pictype="video";
    $(function () {
        Load();
        /*直播 精选 下拉加载*/
        $('#newlivStyle').scroll(function () {
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('.myrewardbox').height();
            if (scrollTop + windowHeight >= scrollHeight - 500) {
                Load();
            }
        });
    });
    var oBok = true;
    function Load() {
        if (oBok) {
            var result = ZAjaxRes({url: "/userRewardRecord/findRewInfoPage.user?offset=" + offset});
            if (result.code == "000000") {
                var html = '';
                var liveway ="";
                var img ="";
                $.each(result.data, function (i, item) {
                    if(item.liveWay=="1"){
                        liveway="livewaya"
                    }else if(item.liveWay=="0"){
                        liveway="livewayv"
                    }else{
                        liveway=""
                    };
                    if(item.courseId.length>=15){
                        img = "<img src='/web/res/image/zbke.png'>"
                    }else{
                        img="";
                    }
                    html +=
                            '<div class="newPubicsize claList" onclick="findRewardbyCouseId('+item.courseId+')">' +
                            '<i class="clasPic" style="background:url('+ item.address +') no-repeat center center; background-size:cover;" ><em class="'+liveway+'"></em></i>' +
                            '<div class="claRightbox">' +
                            '<div class="reversionTi">' +
                            '<i class="earTitle">'+img+item.topic+'</i>' +
                            '<p class="llM">'+item.totalAmount+'枣币</p>' +
                            '</div>' +
                            '<em class="xcmoney">打赏人数:'+item.courseCount+'</em>' +
                            '</div>'+
                            '</div>';
                });
                offset = parseInt(result.data.length) + offset;
                $(".myrewardbox").append(html);
            } else {
                if (result.data.length < 1) {
                    oBok = false;
                    if(offset=="0"){
                        $(".myrewardbox").append('<div class="noData"></div>');
                    }
                    return;
                }
            }
        }
    }

    function findRewardbyCouseId(courseId){
        window.location.href = "/weixin/rewardFordetails?courseId="+courseId;
    };

</script>
</html>
