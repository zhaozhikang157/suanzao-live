<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="bankCard">
<head>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>银行卡详情</title>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/style/css/appUser/appUserStyle.css"/>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/bank/bankCardAndAppUser.js"></script>
</head>
<body ng-controller="task">
<div style="width:97%;margin: 0 auto;margin-top:10px">
    <h2 style="margin: 15px 0;">
        银行卡查看
    </h2>
    <form class="form-horizontal form-inline" id="form1" name="form1">
        <div class="appUserInfoList">
            <p><img src="{{bankCard.url}}" title="会员头像" alt="暂无会员头像"/></p>

            <p><label class="control-label">姓名：</label><span>{{bankCard.aname}}</span></p>

            <p><label class="control-label">钱包余额：</label><span>${balance}</span></p>
        </div>
        <table class="tableBox" border="" cellspacing="" cellpadding="">
            <thead>
            <tr align="center">
                <th>姓名</th>
                <th>手机号</th>
                <th>银行名称</th>
                <th>银行卡号</th>
                <th>身份证号</th>
            </tr>
            </thead>
            <tbody>
            <tr align="center" ng-repeat="bs in bankCard.bankCards">
                <td>{{bs.NAME}}</td>
                <td>{{bs.MOBILE}}</td>
                <td>{{bs.BANK_NAME}}</td>
                <td>{{bs.CARD_NO}}</td>
                <td>{{bs.ID_CARD}}</td>
            </tr>
            </tbody>
        </table>
    </form>
</div>
</body>
</html>
