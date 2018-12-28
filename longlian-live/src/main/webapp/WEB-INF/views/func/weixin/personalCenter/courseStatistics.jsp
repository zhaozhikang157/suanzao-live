<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>统计详情</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>
<body>
<div class="detailss">
    <ul class="detatap">
        <li><a class="on" href="javascript:void(0);">数据总览</a></li>
        <div class="spots"></div>
        <li><a href="javascript:void(0);">渠道数据</a></li>
    </ul>
    <div class="detailtab">
        <div class="on">
            <div class="modebox">
                <p class="fksz">访客数据说明</p>

                <div class="explain">
                    <p id="visitCount">访客人数:0</p>

                    <p id="joinCount">付费人数:0</p>

                    <p id="studyCount">听课人数:0</p>
                </div>
            </div>
            <%--<div class="modebox">--%>
                <%--<p class="fksz">赞赏<span class="switch"></span></p>--%>
                <%--<ul class="appreciate">--%>
                    <%--<li id="pushPraiseCount">点赞人数:0<a href="javascript:void(0);"></a></li>--%>
                    <%--<div class="spots"></div>--%>
                    <%--<li id="pushPraise">获赞率:0%<a href="javascript:void(0);"></a></li>--%>
                <%--</ul>--%>
            <%--</div>--%>
            <div class="modebox">
                <p class="fksz">评论</p>
                <ul class="appreciate">
                    <li id="commentCount">评论人数:0<a href="javascript:void(0);"></a></li>
                    <div class="spots"></div>
                    <li id="comment">评论条数:0<a href="javascript:void(0);"></a></li>
                </ul>
            </div>
        </div>
        <div></div>
        <div>
            <div class="modebox">
                <p class="fksz">渠道数据说明</p>

                <div class="tableDiv">
                    <table border="" cellspacing="" cellpadding="">
                        <thead>
                        <tr align="center">
                            <td>来访渠道</td>
                            <td>人数</td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr align="center">
                            <td>正常访客</td>
                            <td id="normal"> 0</td>
                        </tr>

                        <tr align="center">
                            <td>邀请卡</td>
                            <td id="invitationCard">0</td>
                        </tr>

                        <tr align="center">
                            <td>分销人分享链接</td>
                            <td id="distributorsShareLinks">0</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var id = ${id};
    $(".detatap li").click(function () {
        var index = $(this).index();
        $(this).find("a").addClass("on").parent().siblings().find("a").removeClass("on");
        $(".detailtab>div").eq(index).addClass("on").siblings().removeClass("on");
    });

    var result = ZAjaxJsonRes({url: "/course/getCourseDetails.user?id=" + id, type: "GET"});
    if (result.code == "000000") {
        var data = result.data;
        $("#visitCount").text("访客人数:" + data.course.visitCount);
//        $("#visitCount").prepend("<img src='/web/res/image/fkrs.png'/>");
        $("#joinCount").text("付费人数:" + data.payNum);
//        $("#joinCount").prepend("<img src='/web/res/image/ffrs.png'/>");
        $("#studyCount").text("听课人数:" + data.studyMum);
//        $("#studyCount").prepend("<img src='/web/res/image/pic_tkrs.png'/>");
//        $("#pushPraiseCount").text("点赞人数:" + data.course.pushPraiseCount);
        $("#commentCount").text("评论人数:" + data.commentCount);
//        $("#pushPraise").text("获赞率:" + parseInt(data.course.pushPraiseCount * 100 / data.course.visitCount) + "%");
        $("#comment").text("评论条数:" + data.courseCommentNum);
        $("#normal").text(data.courseSourceMap.normal);
        $("#invitationCard").text(data.courseSourceMap.invitationCard);
        $("#distributorsShareLinks").text(data.courseSourceMap.distributorsShareLinks);
    }
</script>
</html>