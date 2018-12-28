<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: white">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>评论</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
</head>
<body>
<div class="complaint">
    <!--评论 -->
    <form method="post" onsubmit="return false">
        <div class="complbox">
            <textarea class="jbtext" maxlength="200" id="content" placeholder="请输入你要评论的内容（不支持emoji表情）"></textarea>
        </div>
        <p class="btnbox">
            <button class="tjbtn">确认提交</button>
            <button class="qxbtn">取消</button>
        </p>
    </form>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/filter.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/emoji.js?nd=<%=current_version%>"></script>
<script>
    var id = '${courseId}';
    var seriesid = '${seriesid}';
    $(".tjbtn").on('click', function () {
        var content =$("#content").val();
        if (content == "") {
            alert("评价内容不能为空");
            return;
        }

        if(isEmojiCharacter(content)){
            alert("不支持emoji表情输入");
            return;
        }
        var result = ZAjaxRes({url: "/courseComment/evaluateServiesCourse.user?courseId=" + id +"&seriesId="+seriesid+ "&content=" + filter(content), type: "GET"});
        if (result.code == "000000") {
            alert("评价成功");
            //setTimeout(function () {
            goBack();
          //  }, 1000);
        } else {
            alert("评价失败");
        }

    });

    $(".qxbtn").click(function () {
        window.location.href = "/weixin/directBroadcast?id=" + id;
    });
</script>
</html>
