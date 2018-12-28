<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
 		<meta name="renderer" content="webkit">
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<%@include file="/WEB-INF/views/common/common.jsp" %>
	<%--	<link rel="stylesheet" href="${ctx}/web/res/style/css/common/btable.css">--%>
		<title>酸枣直播后台管理系统</title>
	</head>
	<body class="no-skin">
        <c:import url="/main/header"></c:import>
		<!-- /section:basics/navbar.layout -->
		<div class="main-container" id="main-container">
			<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
			</script>
			<c:import url="/main/menu"/>
			<!-- /section:basics/sidebar -->
			<div class="main-content" >
				<!-- #section:basics/content.breadcrumbs -->
				<div class="" id="breadcrumbs">

					<div id="contents" class="contents"></div>
				</div>
				<%--<iframe src="" id="myFrame"></iframe>--%>

			</div>
			<%--<%@include file="./footer.jsp" %>--%>
			<a href="javascript:void(0);" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
				<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
			</a>
		</div>
	</body>
</html>
