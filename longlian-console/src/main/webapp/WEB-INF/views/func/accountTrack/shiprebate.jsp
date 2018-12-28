<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="rebate">
<head>
    <%@include file="/WEB-INF/views/include/header.jsp" %>

    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>收益记录</title>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/style/css/appUser/appUserStyle.css"/>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/dataTable/dataTables.min.css"/>

    <script>
        var id = ${id};
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/appUser/rebate.js"></script>
    <script src="${requestContext.contextPath}/web/res/dataTable/dataTables.min.js" />

    <script src="${requestContext.contextPath}/web/res/my97/xdate.dev.js"></script>
</head>
<body ng-controller="task">
<script src="${requestContext.contextPath}/web/res/my97/WdatePicker.js"></script>
<div style="width:97%;margin: 0 auto;margin-top:10px">
    <h2 style="margin: 15px 0;">
        收益记录
    </h2>
    <form class="form-horizontal form-inline" id="form1" name="form1">
        收益时间：
        <input type="text" class="Wdate form-control" id="beginTime"
               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
               style="width: 120px;display: inline-block;"/>
        至<input type="text" class="Wdate form-control" id="endTime"
                onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                style="width: 120px;display: inline-block;"/>
        <button type="button" class="btn btn-info" ng-click="toquery()">
            <i class=" glyphicon glyphicon-search"></i> 查询
        </button>
        <div class="appUserInfoList">
            <p><label  class="control-label">收益总金额：</label><span>{{rebate.rebateAmount}}元</span></p>
        </div>
        <table class="tableBox" border="" cellspacing="" cellpadding="">
            <thead><tr align="center"><th>订单编号</th><th>订单类型</th>
                <th>手机号</th><th>金额</th><th>时间</th></tr></thead>
            <tbody>
                <tr align="center" ng-repeat="bs in rebate.rebates">
                    <td>{{bs.orderNo}}</td> <td>{{bs.typeStr}}</td>
                    <td>{{bs.mobile}}</td><td>{{bs.amount}}</td>
                    <td>{{bs.createTime|date:'yyyy-MM-dd HH:mm:ss'}}</td>
                </tr>
              </tbody>
        </table>
    </form>
</div>
</body>
<script>
    function doQuery(){

    }


</script>
</html>
