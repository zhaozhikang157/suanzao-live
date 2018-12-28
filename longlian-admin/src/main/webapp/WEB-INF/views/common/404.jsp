<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<%response.setStatus(200);%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/WEB-INF/views/common/meta.jsp"%>
    <title>404 - 页面不存在</title>
	<%@include file="/WEB-INF/views/include/header.jsp" %>
	<script type="text/javascript">
$(function() {
	$('#targetContentDiv').height($(window).innerHeight() - 150);
})
    </script>
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
	background:url(../staticResource/images/framelayout/404.png) no-repeat center center;
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
		<p>您访问的页面不存在,建议您</p>
		<ul>
		  <li><a class="btn btn-primary" href="javascript:void(0);" onclick="history.back()">返回</a></li>
		  <li><a class="btn" href="${ctx}/j_spring_security_logout">注销</a></li>
		</ul>
      </div>
	</div>

  </body>

</html>
