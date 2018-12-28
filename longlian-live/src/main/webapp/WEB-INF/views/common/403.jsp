<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #eeeeee">
<head>
	<meta charset="UTF-8">
	<meta name="Keywords" content=""/>
	<meta name="Description" content=""/>
	<title>403 - 无权访问资源</title>
	<script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
	<link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
	<link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
	<style type="text/css">
		#ContentDiv {
			width:400px;
			height:300px;
			position: absolute;
			left:50%;
			top:50%;
			margin-left:-200px;
			margin-top:-150px;
			background-color: #fff;
			text-align: center;
			font-family: "微软雅黑";
			vertical-align: middle;
		}

		#ContentDiv h1 {
			width:100%;
			height:120px;
		}

		#ContentDiv p {
			font-size: 24px;
			font-weight:normal;
			line-height: 1.25;
			padding: 10px;
			color:#666;
		}

		#ContentDiv li {
			display: inline;
			list-style: none outside none;
		}
		.btn {
			border: none;
			background: none;
			box-shadow: none;
			color: #fff;
			box-shadow: none;
		}
		a.btn:hover {
			color: #fff;
		}
		.btn:link,
		.btn:hover,
		.btn:active {
			background-color: #0090e6;
		}

		button.btn,
		span.btn,
		a.btn,input.btn{
			padding: 3px 5px;
			width:60px;
			text-align: center;
			font-size: 12px;
			background-color: #45b7f9;
			background: transparent linear-gradient(to bottom, #45b7f9 0%, #0399f0 100%) repeat scroll 0px 0px;
			color: #fff;
			border-radius: 3px;
			text-decoration: none;
			border: 1px solid #0882c9;
			text-shadow: none;
			margin-right: 10px;
			margin-left: 2px;
		}
		button.btn:focus,
		input.btn:focus,
		span.btn:focus,
		a.btn:focus,
		button.btn:hover,
		a.btn:hover,
		input.btn:hover,
		span.btn:hover {
			background: transparent linear-gradient(to bottom, #16a1f1 0%, #0983cb 100%) repeat scroll 0px 0px;
			color: #fff;
		}

		button.btn.disabled:hover,
		span.btn.disabled:hover,
		a.btn.disabled:hover,
		input.btn.disabled:hover,
		button.btn[disabled]:hover,
		span.btn[disabled]:hover,
		a.btn[disabled]:hover,
		input.btn[disabled]:hover
		{
			background-image: none;
			color: inherit;
		}
	</style>
</head>
<body>


	<div id="ContentDiv">
	  <div class="containers">
		<h1></h1>
		<p>您没有权限访问该资源</p>
      </div>
      <div style="clear: both;"></div>
	</div>

</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/TouchSlide.1.1.js"></script>
<script type="text/javascript">
    $(function() {
        // $('#targetContentDiv').height($(window).innerHeight() - 150);
    })
</script>
</html>