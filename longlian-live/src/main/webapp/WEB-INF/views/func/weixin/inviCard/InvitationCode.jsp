<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<meta charset="UTF-8">
<meta name="Keywords" content=""/>
<meta name="Description" content=""/>
<title>邀请码</title>
<script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
<link rel="stylesheet" type="text/css" href="/web/res/css/code.css?nd=<%=current_version%>"/>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body onload="doInit()">
<!--邀请码 -->
<div class="invitationCode">
	<div class="CodeSuccess">
		<div class="titImg"><img  src="/web/res/image/code/pic_3.png"></div>
        <h3 style="margin-top:.6rem;">课程名称：${inviCodeInfo.liveTopic}</h3>
        <c:if test="${inviCodeInfo.startTime == '0'}">

        </c:if>
        <c:if test="${inviCodeInfo.startTime != '0'}">
            <h3>开课时间：${inviCodeInfo.startTime}</h3>
        </c:if>
        <p>价值：${inviCodeInfo.chargeAmt}元</p>
        <p style="margin-top:1rem;">有效期:</p>
        <p>${inviCodeInfo.time}</p>
        <input name="" type="button" class="codeBtn" value="立即使用" onclick="GrabInviCode('${inviCodeInfo.id}');">
        <h6>使用须知</h6>
        <ul>
        	<li>${inviCodeInfo.remark}</li>
        </ul>
    </div>
</div>
<div class="code_foot">- 酸枣在线出品 -</div>
</body>
<script>
    var inviCodeId = '${inviCodeInfo.inviCodeId}';
    var courseId = '${inviCodeInfo.courseId}';
    var itemId = '${inviCodeInfo.itemId}';
    function doInit(){
        var result = ZAjaxRes({url:"/weixin/useCode?inviCodeId="+inviCodeId+"&courseId="+courseId+"&itemId="+itemId});
        if(result.code != '000000'){
            if(result.code == '500'){
                $('.codeBtn').val("请重新操作");
            }else{
                $('.codeBtn').val(result.message);
            }
            $('.codeBtn').attr('disabled',"true");
            $('.codeBtn').addClass('not');
        }
    }
    function GrabInviCode(id){
        if(id){
            var result = ZAjaxRes({url:"/weixin/useCode?inviCodeId="+inviCodeId+"&courseId="+courseId+"&itemId="+itemId});
            if(result.code == '000000'){
                var isSeriesCourse = '${inviCodeInfo.isSeriesCourse}';//1-是系列课0-是单节课
                if(isSeriesCourse == '1'){
                    window.location.href =  "/weixin/courseInfo?id=" + ${inviCodeInfo.courseId}+ "&isSeries=" +1;
                }else{
                    window.location.href = "/weixin/index.user?courseId="+${inviCodeInfo.courseId};
                }
            }else{
                //sys_pop(result);
                if(result.code == '500'){
                    $('.codeBtn').val("请重新操作");
                }else{
                    $('.codeBtn').val(result.message);
                }
                $('.codeBtn').attr('disabled',"true");
                $('.codeBtn').addClass('not');
            }
        }
    }
</script>
</html>


