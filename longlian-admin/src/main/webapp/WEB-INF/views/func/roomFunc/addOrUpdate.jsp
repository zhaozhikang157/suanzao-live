<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="func">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/jquery/jquery.js"></script>
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
    <script src="${requestContext.contextPath}/web/res/js/func/roomFunc/funcApp.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label for="funcName" class="col-sm-3 control-label">功能名:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="funcName"
                       ng-model="func.funcName" autocomplete="off" placeholder="" maxlength="10" check-type="required"  onblur="repeatName()" onclick="getName()"id="funcName">
            </div>
        </div>
          <div class="form-group">
              <label for="funcCode" class="col-sm-3 control-label">功能代码:</label>
              <div class="col-sm-5">
                  <input type="text" class="form-control"  name="funcCode"
                         ng-model="func.funcCode" autocomplete="off" placeholder=""  check-type="required"  onblur="repeatName()" onclick="getName()"id="funcCode">
              </div>
          </div>
          <div class="form-group">
              <label for="funcDisc" class="col-sm-3 control-label">功能说明:</label>
              <div class="col-sm-5">
                  <input type="text" class="form-control"  name="funcDisc"
                         ng-model="func.funcDisc" autocomplete="off" placeholder="" maxlength="10" check-type="required"  onblur="repeatName()" onclick="getName()"id="funcDisc">
              </div>
          </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">状态：</label>
            <div class="col-sm-3" style="padding-top: 7px">
                <input type="radio" name="status" ng-model="func.status" value="0">未启用
                <input type="radio" name="status" ng-model="func.status" value="1">启用
        </div>
        </div>
    </form>
</div>
<script>
    $(function(){
        $("input[name='status'][value='0']").prop('checked', true);//radio默认选中
    })
</script>
</body>
</html>
