<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>平台收益明细</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body onload="Load()"style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style="background:#FFF; overflow: auto; height:100%;">
    <div id="cont_box" style="padding-bottom: 0">
        <!--分销收益 -->
        <p class="pucmode">
            总收益：<em class="getmoney">${platIncome}</em> 枣币
        </p>
        <div class="allAmount" style="min-height: 33rem">

        </div>
        <div class="pullUpLabel">上拉加载更多</div>
    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0;
    $(function(){
        /*直播 精选 下拉加载*/
        $('#wrapper').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if(scrollTop + windowHeight >= scrollHeight-1){
                Load();
            }
        });
    });
    var oBok = true;
    function Load() {
        if (oBok) {
            var index = $(".mycombxo").find("dl").length;
            var result = ZAjaxRes({url: "/accountTrack/platIncomeDetailsPage.user?offset="+offset});
            if (result.code == "000000") {
                var html = '';
                $.each(result.data, function (i, item) {
                    html +='<div class="mycombxo" id="selected">'+ '<dl> ' +
                            '<dd> ' + '<h4>'+item.type+'</h4> ' +
                            '<span class="buycy" >时间:'+getFormatDateTimeStr(item.createTime,'yyyy-MM-dd HH:mm:ss')+'</span> ' +
                            '</dd> ' + '</dl> ' +
                            '<p class="mobbox" id="amount">'+item.amount+'枣币</p> ' +
                            '</div>';
                });
                offset = parseInt(result.data.length)+offset;
                $(".allAmount").append(html);
            }else{
                if(result.data.length<1){
                    $('.pullUpLabel').show();
                    $('.pullUpLabel').text('加载已经完毕！');
                    //如果没有了 oBok 设false
                    oBok = false;
                    if(offset=="0"){
                        $(".allAmount").append('<div class="noData"></div>');
                    }
                    return;
                }
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        } else {
            if (!oBok) {
                $('.pullUpLabel').show();
                $('.pullUpLabel').text('加载已经完毕！');
            }
        }
    }

</script>
</html>
