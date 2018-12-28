<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>全部关注人</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>

</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style="min-height: 35rem;overflow: auto">
    <div id="cont_box" style="padding-bottom: 0;">
        <!--全部关注人 -->
        <div class="concerned">
            <p class="tadyconcer" id="todayCount">新增关注:0人</p>
            <ul class="tadyul" id="today">
            </ul>
        </div>
        <p class="tadyconcer" id="yesterdayCount">历史关注人数:0人</p>
        <ul class="tadyul" id="yesterday">
        </ul>
        <div class="pullUpLabel">上拉加载更多</div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    /*下拉加载*/
    $(function(){
        Load();
        /*直播 精选 下拉加载*/
        $('#wrapper').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if(scrollTop + windowHeight >= scrollHeight-100){
                Load();
            }
        });
    });
    var oBok = true;
    var offSet = 0,createTime = "";
    function Load(){
        if(oBok){
            var param = {"roomId": '${id}',"offSet":offSet,"createTime":createTime};
            var result = ZAjaxRes({ url: "/userFollow/getCountUserFollow",param:param,  type: "GET" });
            if (result.code == "000000") {
                var yesterdayList = result.ext;
                if(offSet==0){
                    var followList = result.data.addUserFollowList;
                    $("#todayCount").text("新增关注:" + followList.length + "人");
                    $("#yesterdayCount").text("历史关注人数:" + result.data.historyUserFollowListNum + "人");
                    //新增
                    if (followList.length > 0) {
                        $.each(followList, function (index, item) {
                            createTime =item.createTime ;
                            var div = "<li><div class='concerpic' style='background: url(" + item.photo + ") no-repeat;background-size:100% 100% ;'></div>" +
                                    "<p class='conerpag'> <span class='concername'>" + item.name + "</span><span class='concertime'>关注时间：<i>" +
                                    item.createTime + "</i></span></p></li>";
                            $("#today").append(div);
                        });
                    }
                }
                //历史
                if (yesterdayList.length > 0) {
                    $.each(yesterdayList, function (index, item) {
                        var div = "<li><div class='concerpic' style='background: url(" + item.photo + ") no-repeat;background-size:100% 100% ;'></div>" +
                                "<p class='conerpag'> <span class='concername'>" + item.name + "</span><span class='concertime'>关注时间：<i>" +
                                item.createTime + "</i></span></p></li>";
                        $("#yesterday").append(div);
                    });
                    offSet += yesterdayList.length;

                }else if(yesterdayList.length ==0){
                    oBok=false;
                    $('.pullUpLabel').text('没有更多内容了！');
                }
            }else {
                $('.pullUpLabel').text('没有更多内容了！');
            }
        }
    };
</script>
</html>