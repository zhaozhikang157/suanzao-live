<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: white">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>全部学生</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
</head>
<body>
    <div class="allmember">
        <%--<p class="members" style="">--%>
          <%--<span style="width: 2rem;height: 2rem;background:url(/web/res/image/pyq.png) no-repeat center center;background-size: 100% 100%"></span>--%>
          <%--<span>张瑞</span>--%>
        <%--</p>--%>
    </div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script>
    var courseId = '${courseId}';
    var result = ZAjaxRes({url:"/joinCourseRecord/getCourseRecordList.user?id=" + courseId  , type:"GET" });
    if(result.code=="000000"){
        $.each(result.data, function(i, n){
            $(".allmember").append("<p class='members'> " +
                    "<span style='width: 2rem;height: 2rem;background:url("+ n.photo+") no-repeat center center;background-size: 100% 100%'></span>" +
                    "<span>"+ n.name+"</span>" +
                    " </p>");
        });
    }
</script>
</html>
