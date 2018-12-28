<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="mRes">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>地区管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        })
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/catalog/app.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label class="col-sm-2 control-label">名称:</label>
            <div class="col-sm-5">
                <input type="hidden" class="form-control" name="id" ng-model="mRes.id" autocomplete="off"
                       placeholder="">
                <input type="text" class="form-control" name="name" ng-model="mRes.name" autocomplete="off"
                       placeholder="请输入菜单名称" check-type="required" maxlength="10">
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">上级菜单:</label>
            <div class="col-sm-5">
                <select class="form-control" ng-model="mRes.parentId" ng-options="item.id as item.name for item in parentMenu" check-type="select_required" >
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-2 control-label">路径:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="name" ng-model="mRes.url" autocomplete="off"
                       placeholder="请输入路径" check-type="selectRequired">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-2 control-label">状态:</label>
            <div class="col-sm-6">
                <div class="checkbox">
                    <label>
                        <input type="radio" value="0" name="status" ng-model="mRes.status"/> &nbsp;启用
                    </label>
                    <label>
                        <input type="radio" value="1" name="status" ng-model="mRes.status"/> &nbsp;禁用
                    </label>
                </div>
            </div>
        </div>
    </form>
</div>
</body>
</html>
