<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="followRewardRule">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>关注金额规则</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/upload.css"/>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/followRewardRule/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            $("#form1").validation({icon: true});
        });
    </script>
</head>
<body>
<div  style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label class="col-sm-3 control-label">名称：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="name"  ng-model="followRewardRule.name" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">人数:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="menCount"  ng-model="followRewardRule.menCount" autocomplete="off" placeholder=""  check-type="required positivIntege" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">奖励金额:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="amount"  ng-model="followRewardRule.amount" autocomplete="off" placeholder=""  check-type="required twoNumberPoint" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">备注:</label>

            <div class="col-sm-7">
                <textarea class="form-control" style="resize: none" name="remark" autocomplete="off"
                          ng-model="followRewardRule.remark" placeholder="限80字" maxlength="80"></textarea>
            </div>
        </div>
    </form>
</div>
</body>
</html>

