<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="withdraw">
<head>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>提现记录</title>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/style/css/appUser/appUserStyle.css"/>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/appUser/withdraw.js"></script>
</head>
<body ng-controller="task">
<div style="width:97%;margin: 0 auto;margin-top:10px">
    <h2 style="margin: 15px 0;">
        提现记录
    </h2>
    <form class="form-horizontal form-inline" id="form1" name="form1">
        <div class="appUserInfoList">
            <p><label  class="control-label">提现总金额：</label><span>{{withdraw.sumAmount}}元</span></p>
            <p><label  class="control-label">手续费：</label><span>{{withdraw.sumCharge}}元</span></p>
        </div>
        <table class="tableBox" border="" cellspacing="" cellpadding="">
            <thead><tr align="center"><th>订单编号</th><th>银行名称</th><th>银行卡号</th><th>金额</th><th>手续费</th><th>时间</th></tr></thead>
            <tbody>
                <tr align="center" ng-repeat="bs in withdraw.maps">
                    <td>{{bs.orderNo}}</td><td>{{bs.bankName}}</td><td>{{bs.bankCardNo}}</td><td>{{bs.amount}}</td><td>{{bs.charge}}</td>
                    <td>{{bs.createTime|date:'yyyy-MM-dd HH:mm:ss'}}</td>
                </tr>
              </tbody>
        </table>
    </form>
</div>
</body>
</html>
