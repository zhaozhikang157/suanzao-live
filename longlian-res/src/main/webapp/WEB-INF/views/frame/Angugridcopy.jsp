
<div class="GridContainer default-layout" style="top:40px;">
    <div id="toolbar">
        <button class="btn btn-success btn-sm" onclick="add()" >
            <i class="glyphicon glyphicon-plus"></i> 添加用户
        </button>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100]"
           data-show-refresh="true"
           data-show-columns="true" ></table>
</div>
</body>
</html>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<html ng-app="app">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome= 1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="/web/res/bootstrap/css/bootstrap.css">
    <link href="/web/res/bootstrap-table/dist/bootstrap-table.min.css" rel="stylesheet" type="text/css">
    <script src="/web/res/jquery/jquery.js"></script>
    <script src="/web/res/bootstrap/js/bootstrap.js"></script>
    <script src="/web/res/angular/angular.js"></script>
    <script src="/web/res/angular-ui-router.js"></script>

    <script type="text/javascript" src="/web/res/bootstrap-table/dist/bootstrap-table.min.js" ></script>
    <script type="text/javascript" src="/web/res/bootstrap-table/dist/locale/bootstrap-table-zh-CN.js" ></script>
    <script src="/web/res/controller/bsTable.js"></script>
    <script src="/web/res/tools.js"></script>
    <script type="text/javascript" >

    </script>
</head>

<body class="container-fluid" ng-controller="MainController" onload="do3();">
<br />
<!-- Workspaces nav -->
<ul class="nav nav-tabs">
    <li role="presentation" ng-repeat="wk in workspaces" ng-class="{active: currentWorkspace==wk}" ng-click="changeCurrentWorkspace(wk)"><a href="#">{{wk.name}}</a></li>
</ul>

<!-- Workspaces containers -->
<div class="workspaceContainer" ng-repeat="wk in workspaces" ng-show="currentWorkspace==wk">

    <table data-options="wk.tableOptions" bs-table-control> </table>
</div>

<div id="aaaa">ssss</div>
<script src="/web/res/controller/app.js"></script>
</body>
</html>
