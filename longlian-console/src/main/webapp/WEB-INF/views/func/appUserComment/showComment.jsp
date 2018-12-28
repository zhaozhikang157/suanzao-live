<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="appUserComment">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>查看审核意见</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <script src="${ctx}/web/res/angular/angular.js"></script>
  <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.js"></script>
  <script>
    var id = ${id};
    $(function(){
      //1. 简单写法：
      $("#form1").validation({icon:true});
    });
  </script>
  <script src="${requestContext.contextPath}/web/res/js/func/appUserComment/showCommentApp.js"></script>
</head>
<body>
<div  style="margin-top:10px; ">
  <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">

    <div>
      <label class="col-sm-3 control-label">待审核意见:</label>
      <div class="col-sm-5"  style="display: inline-block;">
        <span id="remarks0"></span>
      </div>
    </div>
    <div>
      <label  class="col-sm-3 control-label">审核中意见:</label>
      <div class="col-sm-5"  style="display: inline-block;">
        <span id="remarks1"></span>
      </div>
    </div>
  </form>
</div>
</body>
</html>

