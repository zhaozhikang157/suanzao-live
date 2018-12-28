<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>打赏收益详情</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style=" overflow: hidden; height:100%;background: white">
    <div id="cont_box"  style="overflow-y:auto;padding-bottom: 0;height: 100%;">
        <div class="detailsbox">
            <ul class="detailsList">
            </ul>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0;
    var courseId = '${courseId}';
    var pageSize =20;
    $(function () {
        Load();
        /*直播 精选 下拉加载*/
        $('#cont_box').scroll(function () {
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('.detailsbox').height();
            if (scrollTop + windowHeight >= scrollHeight - 10) {
                Load();
            }
        });
    });
    var oBok = true;
    function Load() {
        if (oBok) {
            var index = $(".mycombxo").find("dl").length;
            var result = ZAjaxRes({url: "/userRewardRecord/findRewInfoPageByCourseId.user?offset=" + offset + "&courseId="+courseId+"&pageSize="+pageSize});
            if (result.code == "000000") {
                var html = '';
                $.each(result.data, function (i, item) {
                    html += '<li> <p> ' +
                            '<i class="deatilsTitle">'+item.userName+'</i> ' +
                            '<i class="rewradMoney">打赏'+item.amount+'枣币</i> </p> ' +
                            '<p class="deatilsTime">'+item.createTime+'</p> ' +
                            '</li>';
                });
                offset = parseInt(result.data.length) + offset;
                $(".detailsList").append(html);
            } else {
                if (result.data.length < 1) {
//                    $('.pullUpLabel').show();
//                    $('.pullUpLabel').text('加载已经完毕！');
                    //如果没有了 oBok 设false
                    oBok = false;
                    if(offset=="0"){
                        $(".detailsList").append('<div class="noData"></div>');
                    }
                    return;
                }
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        } else {
            if (!oBok) {
//                $('.pullUpLabel').show();
//                $('.pullUpLabel').text('加载已经完毕！');
            }
        }
    }

</script>
</html>
