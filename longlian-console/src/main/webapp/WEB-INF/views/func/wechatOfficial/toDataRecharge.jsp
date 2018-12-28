<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="wechatOfficialRoom">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>流量充值</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
  <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
  <script src="${ctx}/web/res/angular/angular.js"></script>
  <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.js"></script>
  <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
  <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
    var id = ${id};
    var liveId = ${liveId};
    $(function(){
      //1. 简单写法：
      $("#form1").validation({icon:true});
    });
  </script>
  <script src="${requestContext.contextPath}/web/res/js/func/wechatOfficial/app.js"></script>
  <style>
    .titles{
      width: 250px;
      text-align: right;
    }
    .much{
        display: inline-block;
    }
    .texry{
      position: absolute;
      left: 252px;
      top: 0;
      width: 250px;
      height: 66px;
      font-size: 10px;
      margin-left: 4px;
      resize: none;
      font-size: 14px;
    }
    .footerBtn{
      clear: both;
      text-align: center;
      margin-top: 80px;
    }
  </style>
</head>
<body>
<div  style="margin-top:10px; ">
  <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">
    <%--<div class="form-group">
      <div class="centers">
          <label class="control-label titles">课程编号：</label>
          <span>{{course.id}}</span>
      </div>
    </div>
    <div class="form-group">
      <div class="centers">
        <label  class="control-label titles">课程名称：</label>
        <span class="much">{{course.liveTopic}}</span>
      </div>
    </div>
    --%>
      <div class="form-group">
        <label for="totalAmount" class="col-sm-3 control-label">流量(GB)：</label>
        <div class="col-sm-6">
          <input type="text" class="form-control" id="totalAmount" name="totalAmount" ng-model="dataChargeRecord.totalAmount"
                 autocomplete="off" placeholder="" check-type="required" maxlength="20">（正整数）
        </div>
      </div>
      <div class="form-group">
        <label for="invalidDate" class="col-sm-3 control-label">失效时长*：</label>
        <div class="col-sm-6">
          <input type="text" class="form-control" id="invalidDate" name="invalidDate" ng-model="dataChargeRecord.invalidDate"
                 autocomplete="off" placeholder="" check-type="required" maxlength="20">（天)(0代表永久)
        </div>
      </div>

  </form>
</div>
<div class="footerBtn">
  <button type="button" class="btn btn-success passBtn"  onclick="dataCharge()">
    <i class=" glyphicon glyphicon-ok"></i> 确定
  </button>
</div>
</body>
</html>
