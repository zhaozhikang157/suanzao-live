<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="WechatOfficial">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/jquery/jquery.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        })
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/third/messageTempletApp.js"></script>

</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label class="col-sm-3 control-label">预约提醒ID:</label>
            <input type="hidden" class="form-control" name="id" ng-model="WechatOfficial.id" autocomplete="off"   >
            <div class="col-sm-5">
               <input type="text" class="form-control" name="reservePreReminderId" ng-model="WechatOfficial.reserveReminderId" autocomplete="off" maxlength=""
                        check-type="select_required repeatName"  id="reserveReminderId">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">预约提醒ID:（微信提前10分钟提醒）</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="reservePreReminderId" ng-model="WechatOfficial.reservePreReminderId" autocomplete="off" maxlength=""
                       check-type="select_required repeatName"   id="reservePreReminderId">
            </div>
        </div>
    </form>
</div>
</body>
</html>
