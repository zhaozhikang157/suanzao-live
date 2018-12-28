<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>${courseTypeName}</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" href="/web/res/css/pullToRefresh.css?nd=<%=current_version%>"/>
</head>
<body>
<!-- 主体部分 -->
<div id="wrapper">
    <div id="cont_box">
        <div id="lecturer_main">
            <!-- 正在直播 -->
            <div class="liveList">
                <h3><a onclick="toMoreClass(0)" class="more"><img
                        src="/web/res/image/more.png"></a><strong>正在·直播</strong></h3>
                <ul id="liveingList">
                </ul>
            </div>
            <!-- 直播·预告 -->
            <div class="liveList">
                <h3><a onclick="toMoreClass(1)" class="more"><img
                        src="/web/res/image/more.png"></a><strong>直播·预告</strong></h3>
                <ul id="prevueList">
                </ul>
            </div>
            <!-- 精选·推荐 -->
            <div class="liveList">
                <h3><strong>往期·回顾</strong></h3>
                <ul id="selected">
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pullToRefresh.js?nd=<%=current_version%>" type="text/javascript"></script>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script>
    var courseTypeId = '${courseTypeId}';
    var liveVoiceDesc = "语音";
    //数据初始化
    $(function () {
        var param = {index: -1, "courseTypeId": courseTypeId};
        var result = ZAjaxRes({url: "/homeV2", type: "GET", param: param});
        if (result.code == "000000") {
            var prevueCourseList = result.data.prevueCourseList;//预约
            var liveingList = result.data.liveingList;//直播
            var commendList = result.data.commendList;//精选
            //直播中的
            $.each(liveingList, function (i, n) {
                var liveWayDesc = "视频";
                var free = "免费";
                var freeClass = "free";
                if (n.liveWay == '1') liveWayDesc = liveVoiceDesc;
                if (n.chargeAmt > 0) {
                    free = "￥" + n.chargeAmt;
                    freeClass = "";
                }
                var li = $('<li>');
                $("#liveingList").append(li);
                //
                var div = $('<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:100% 100%;" ><span>' + liveWayDesc + '</span></div>');
                li.append(div);
                li.append('<p><span class="number">' + n.studyCount + '人感兴趣</span><strong class="title">' + n.liveTopic + '</strong></p>' +
                        '<p><strong class="money ' + freeClass + '">' + free + '</strong></p>');
                div.click(function () {
                    toCourseInfo(n.id);
                });
            });

            //预约
            $.each(prevueCourseList, function (i, n) {
                var liveWayDesc = "视频";
                var free = "免费";
                var freeClass = "free";
                if (n.liveWay == '1') liveWayDesc = liveVoiceDesc;
                if (n.chargeAmt > 0) {
                    free = "￥" + n.chargeAmt;
                    freeClass = "";
                }
                var li = $('<li>');
                $("#prevueList").append(li);
                //n.coverssAddress
                var div = $('<div class="img" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:100% 100%;" ><span>' + liveWayDesc + '</span></div>');
                li.append(div);
                li.append('<p><span class="number">' + n.studyCount + '人感兴趣</span><strong class="title">' + n.liveTopic + '</strong></p>' +
                        '<p><span class="time">' + n.startTime + '</span><strong class="money ' + freeClass + '">' + free + '</strong></p>');
                div.click(function () {
                    toCourseInfo(n.id);
                });
            });
            //精选推荐
            $.each(commendList, function (i, n) {
                addLive(n);
            });
            wrapper.refresh();
        }

        share_h5();//分享
    });

    /*直播 精选 下拉加载*/
    refresher.init({
        id: "wrapper",
        pullUpAction: Load
    });
    var oBok = true;
    function Load() {
        var search_val = $('#searchFn').val();
        if (oBok) {
            var index = $("#selected").find("li").length;
            var param = {index: index, "courseTypeId": courseTypeId};
            var result = ZAjaxRes({url: "/homeV2", type: "GET", param: param});
            if (result.code == "000000") {
                var commendList = result.data.commendList;//推荐
                $.each(commendList, function (i, n) {
                    addLive(n);
                });
                if (commendList.length == 0) {
                    $('.pullUpLabel').text('没有更多内容了！');
                    $('.pullUp').removeClass('loading');
                    //如果没有了 oBok 设false
                    oBok = false;
                }
            }
            wrapper.refresh();
            /****   ---DOM  改变后重新计数高度 --- ****/
        }
        else {
            if (!oBok) {
                $('.pullUpLabel').text('没有更多内容了！');
                $('.pullUp').removeClass('loading')
            }
        }
    }
    /**
     * 添加精选推荐视频
     * @param n
     */
    function addLive(n) {
        var liveWayDesc = "视频"
        var free = "免费";
        var freeClass = "free";
        if (n.liveWay == '1') liveWayDesc = liveVoiceDesc;
        if (n.chargeAmt > 0) {
            free = "￥" + n.chargeAmt;
            freeClass = "";
        }
        var el, li, i;
        el = document.querySelector("#selected");
        li = document.createElement('li');
        li.innerHTML = '<div class="img" onclick="javascript:toCourseInfo(' + n.id + ')" style="background:url(' + n.coverssAddress + ') no-repeat center center; background-size:100% 100%;"><span>' + liveWayDesc + '</span></div>\
                    <p><span class="number">' + n.studyCount + '人感兴趣</span><strong class="title">' + n.liveTopic + '</strong></p>\
                    <p><strong class="money ' + freeClass + '">' + free + '</strong></p>'
        el.appendChild(li, el.childNodes[0]);
    }

    /**
     *  跳转至课程详情页面
     * @param id
     */
    function toCourseInfo(id) {
        window.location.href = "/weixin/courseInfo?id=" + id;
    }
   //去更多课程页面
    function toMoreClass(liveStatus){
        window.location.href = "/weixin/moreClass?liveStatus=" + liveStatus +"&courseTypeId="+courseTypeId;
    }

</script>
</html>