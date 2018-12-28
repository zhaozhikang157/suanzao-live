<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>我的课程收益</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body style="-webkit-overflow-scrolling:touch; ">
<div id="wrapper"style=" overflow: hidden; height:100%;background: white">
    <div id="cont_box" style="height:100%;background: white;padding-bottom: 0;">
        <div id="newlivStyle"style="overflow-y:scroll;height: 100%;">
            <div class="allAmount">

            </div>
        </div>

    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0, pageSize = 20;
    $(function(){
        Load();
        /*直播 精选 下拉加载*/
        $('#newlivStyle').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('.allAmount').height();
            if(scrollTop + windowHeight >= scrollHeight-500){
                Load();
            }
        });
    });
    var oBok = true;
    function Load() {
        if (oBok) {
            var result = ZAjaxRes({url: "/liveRoom/courseIncomeDetailsPage.user?offset="+offset+ "&pageSize="+pageSize});
            if (result.code == "000000") {
                var html = '';
                var liveway ="";
                var img ="";
                $.each(result.data, function (i, item) {
                    if(item.live_way=="1"){
                        liveway="livewaya"
                    }else if(item.live_way=="0"){
                        liveway="livewayv"
                    }else{
                        liveway=""
                    };
                    if(item.IsRelay=="0"){
                        img="";
                    }else{
                        img = "<img src='/web/res/image/zbke.png'>"
                    }
                    html += '<div class="newPubicsize claList" onclick="findCourseInfo('+item.courseId+')">' +
                            '<i class="clasPic" style="background:url('+item.address+') no-repeat center center; background-size:cover;" ><em class="'+liveway+'"></em></i>' +
                            '<div class="claRightbox">' +
                            '<div class="reversionTi">' +
                            '<i class="earTitle">'+img+item.topic+'</i>' +
                            '<p class="llM">'+item.amount+'枣币</p>' +
                            '</div>' +
                            '<i class="earbot"><span>想学习人数:<em class="xcmoney">'+item.visitCount+'</em></span> <span>付费人数:<em class="fumoney">'+item.joinCount+'</em></span></i>' +
                            '</div>'+
                            '</div>';
                });
                offset = parseInt(result.data.length)+offset;
                $(".allAmount").append(html);
            }else{
                if(result.data.length<1){
                    oBok = false;
                    if(offset=="0"){
                        $(".allAmount").append('<div class="noData"></div>');
                    }
                    return;
                }
            }
        }
    }

    function findCourseInfo(courseId){
        window.location.href = "/weixin/courseRevenuedetails?courseId="+courseId;
    }

</script>
</html>
