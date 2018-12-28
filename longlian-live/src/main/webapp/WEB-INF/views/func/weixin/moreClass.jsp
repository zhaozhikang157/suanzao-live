<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>更多</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" href="/web/res/css/pullToRefresh.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pullToRefresh.js?nd=<%=current_version%>"></script>
</head>
<body>
<!-- 主体部分 -->
<div id="wrapper">
    <div id="cont_box">
        <div id="lecturer_main">
            <!-- 正在直播 -->
            <div class="liveList">
                <ul id="liveingList">
                    <%--<li>--%>
                    <%--<div class='img' style='background:url(/web/res/image/t.png); background-size:cover;' ><span>语音</span></div>--%>
                    <%--<p><span class='number'>30人学习</span><strong class='title'>标题表是正常送将诶用</strong></p>--%>
                    <%--<p><strong class='money'>￥10.00</strong></p>--%>
                    <%--</li>--%>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var pageNum = 1, pageSize = 10;
    var liveVoiceDesc = "语音";
    var liveStatus = ${liveStatus};
    var study = "人感兴趣";
    var courseTypeId = '${courseTypeId}';
    $(function () {
        Load();
        share_h5();
    });
    /*下拉加载*/
    refresher.init({
        id: "wrapper",
        pullUpAction: Load
    });
    var oBok = true;
    function Load() {
        if (oBok) {
            $.ajax({
                url: "/course/getMoreLiveCourse?liveStatus=" + '${liveStatus}' + "&courseTypeId=" + courseTypeId + "&pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: 'GET',
                dataType: 'json',
                success: function (result) {
                    if (result.code == "000000") {
                        if (result.data.length > 0) {
                            pageNum++;
                            $.each(result.data, function (index, n) {
                                var num = n.visitCount;
                                var time = "";
                                if (liveStatus == 1) {
                                    time = "<span class='time'>" + n.startTime + "</span>";
                                }
                                var liveWayDesc = "视频";
                                var free = "免费";
                                if (n.liveWay == '1') liveWayDesc = liveVoiceDesc;
                                if (n.chargeAmt > 0) {
                                    free = "￥" + n.chargeAmt;
                                }
                                $("#liveingList").append("<li><div class='img'  onclick=toCourseInfo(" + n.id + ") style='background:url(" + n.coverssAddress + "); background-size:100% 100%;' ><span>"+liveWayDesc+"</span></div>" +
                                        "<p><span class='number'>" +num + "" + study + "</span><strong class='title'>" + n.liveTopic + "</strong></p>" +
                                        "<p>"+time+"<strong class='money'>" + free + "</strong></p> </li>")

                            });
                        } else {
                            $('.pullUpLabel').text('没有更多内容了！');
                            $('.pullUp').removeClass('loading');
                            oBok = false;
                        }
                    }
                }
            });
            wrapper.refresh();
        }
        else {
            $('.pullUpLabel').text('没有更多内容了！');
            $('.pullUp').removeClass('loading')
        }
    }

    /**
     *  跳转至课程详情页面
     * @param id
     */
    function toCourseInfo(id) {
        window.location.href = "/weixin/courseInfo?id=" + id;
    }
</script>
</html>
