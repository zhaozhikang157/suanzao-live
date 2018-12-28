<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>课程评论</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/pullToRefresh.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pullToRefresh.js?nd=<%=current_version%>"></script>
</head>
<body>
<div id="wrapper">
    <div id="cont_box">
        <!--课程评价-->
        <p class="pucmode">
            共<span id="courseCount">0</span>条评论
        </p>

        <div class="pjbox" id="courseComment">
            <%--<div class="pjxq">--%>
            <%--<img src="/web/res/image/01.png"/>--%>
            <%--<p class="pjright">--%>
            <%--<i class="pagename">阿狸</i>--%>
            <%--<i class="pagetime">2017-2-12 15:23发布</i>--%>
            <%--<span class="pagetext">--%>
            <%--是的粉红色灯光粉红色的功夫搞的苏菲公司会发生的功夫撒的规范化的事故发生--%>
            <%--</span>--%>
            <%--</p>--%>
            <%--</div>--%>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var courseId = '${courseId}';
    var pageNum = 1, pageSize = 10;
    var result = ZAjaxRes({url: "/courseComment/getCommentSumByCourseId?courseId=" + courseId, type: "GET"});
    if (result.code == "000000") {
        $("#courseCount").text(result.data);
    }
    refresher.init({
        id: "wrapper",
        pullUpAction: Load
    });
    var oBok = true;
    $(function () {
        Load();
    });
    function Load() {
        if (oBok) {
            var result = ZAjaxRes({
                url: "/courseComment/getCommentListByCourseId?courseId=" + courseId + "&pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: "GET"
            });
            
            if (result.code == "000000") {
                var recordList = result.data;//推荐
                if (recordList.length > 0) {
                    $.each(recordList, function (i, n) {
                        $("#courseComment").append("<div class='pjxq'> <img src=" + n.photo + ">" +
                                "<p class='pjright'><i class='pagename'>" + n.name + "</i>" +
                                "<i class='pagetime'>" + n.createTime + "发布</i>" +
                                "<span class='pagetext'>" + n.content + "</span></p></div>");
                    });
                    pageNum++;
                } else {
                    $('.pullUpLabel').text('没有更多内容了！');
                    $('.pullUp').removeClass('loading');
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
</script>
</html>
