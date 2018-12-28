<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程详情</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/course/courseDetails.css"/>
    <script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
    <script>
        var id = ${id};
    </script>
</head>
<body  >
<div class="wrap">
    <h3 class="detailstitle">
        单节课详情
    </h3>

    <div class="detailscont">
        <p><label>课程名称:</label><i>${course.course.liveTopic}</i></p>

        <p><label>封面图:</label><img id="fm_pic" src="${course.course.coverssAddress}"></p>

        <p><label>状态:</label><i id="status">${course.status}</i></p>

        <p><label>讲课老师:</label><i>${course.appUser.name}</i></p>

        <p><label>课程价格:</label><i>${course.course.chargeAmt}</i></p>
        <%--<p><label>视频时长:</label><i></i></p>--%>
        <%--<p><label>报名人数:</label><i>230人</i>(直播前)<span><i>直播观看人数:</i><i>112人</i></span></p>--%>
        <p><label>访问人数:</label><i>${course.course.visitCount}</i></p>

        <p><label>报名人数:</label><i>${course.course.joinCount}</i></p>

        <p><label>付费人数:</label><i >${course.course.joinCount}</i></p>
        <c:if test="${not empty course.liveChannel}">
            <p><label>推流地址:</label><i><span>${course.liveChannel.pushAddr}</span></i></p></c:if>
        <%--<p><label>观看时长:</label><i>10%<i></i>2人</i>20%<i>2人</i>50%<i>2人</i>80%<i>2人</i>100%<i>2人</i></p>--%>
        <p style="display:none"><label></label><i id="recoveryvalue"></i></p>
        <div>
            <label style="float: left;">课程介绍:</label>

            <p style="display: inline-block;">${course.course.remark}</p>
        </div>
        <div class="evaluatecont" id="courseComment">
            <label style="float: left;" >课程评价:</label>
            <c:forEach items="${course.commentList}" var="item">
            <div class="evaluate">
                <img src="${item.photo}"/>
                <div class="livemessage">
                    <i>${item.name}</i>
                    <i style="border-bottom: 1px solid #CCCCCC; ">${item.createTime}发布</i>
                    <i style="padding: 6px 0;"> ${item.content}</i>
                </div>
            </div>
            </c:forEach>

        </div>
        <div style="clear: both;"></div>
        <div class="tableDiv">
            <table border="" cellspacing="" cellpadding="">
                <thead>
                <tr align="center">
                    <td>来访渠道</td>
                    <td>人数</td>
                    <td>付费</td>
                </tr>
                </thead>
                <tbody>
                <tr align="center">
                    <td>正常访客</td>
                    <td>${course.courseSourceMap.normal}</td>
                    <td>${course.payCourseNumMap.normalPay}</td>
                </tr>
                <tr align="center">
                    <td>邀请卡</td>
                    <td>${course.courseSourceMap.invitationCard}</td>
                    <td>${course.payCourseNumMap.invitationCardPay}</td>
                </tr>
                <tr align="center">
                    <td>分销人分享链接</td>
                    <td>${course.courseSourceMap.shareLink}</td>
                    <td>${course.payCourseNumMap.shareLinkPay}</td>
                </tr>
                </tbody>
            </table>
        </div>
<c:if test="${course.status=='上线'}">
    <div class="btns" id="sales">
        下架此课
    </div>
</c:if>
        <c:if test="${course.course.endTime !=null }">
            <div class="btns" id="recovery">
                恢复此课程
            </div>
        </c:if>

    </div>
</div>
</body>
<script>
<c:if test="${course.status=='上线'}">
     $("#sales").click(function () {
         if ($("#status").text() == "下线") {
             alert("该课程已下线");
             return;
         }
         $.ajax({
             type: "GET",
             url: "/course/updateDown?id="+id,
             success: function (data) {
                 if(data.success){
                     jbox_notice({content: "下架成功", autoClose: 2000, className: "success"});
                     $("#status").text("下线");
                 }else{
                     jbox_notice({content: "下架失败", autoClose: 2000, className: "error"});
                 }
             }
         })
     });
</c:if>

$("#recovery").click(function () {
    if ($("#recoveryvalue").text() == "已恢复" || ${course.course.endTime==''}) {
        alert("该课程已恢复");
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/recoveryCourse?id="+id,
        success: function (data) {
            if(data.success){
                jbox_notice({content: "恢复成功", autoClose: 2000, className: "success"});
                $("#recoveryvalue").text("已恢复");
            }else{
                jbox_notice({content: "恢复失败", autoClose: 2000, className: "error"});
            }
        }
    })
});
</script>
</html>

