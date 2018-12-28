<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="systemPara">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>资源</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        });
        $.fn.validation.defaults = {
            validRules: [
                {
                    name: 'required', validate: function (value) {
                    return ($.trim(value) == '');
                }, defaultMsg: '请输入内容。'
                },
                {
                    name: 'sixNumberPoint', validate: function (value) {
                    if (value.trim() == "") {
                        return false;
                    }
                    return (!/^\d+(\.\d{1,6})?$/.test(value));
                }, defaultMsg: '请输入有效的数字（六位小数点）。'
                }
            ],
            reqmark: true,
            callback: null,  //function(obj,params){};
            icon: false      //=icon=true 表示显示图标，默认不显示
        };
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/systemParaController/app.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <input type="hidden" class="form-control" name="id" ng-model="shopFloor.id" autocomplete="off" placeholder="">

        <div class="form-group">
            <label class="col-sm-3 control-label">名称：</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" name="name" ng-model="systemPara.name" autocomplete="off"
                       placeholder="" check-type="required">
            </div>
        </div>
        <div class="form-group">
            <label for="code" class="col-sm-3 control-label">代码:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="code" name="code" ng-model="systemPara.code"
                       autocomplete="off" placeholder="" check-type="required">
            </div>
        </div>

        <div class="form-group">
            <label for="value" class="col-sm-3 control-label">参数值:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="value" name="value" ng-model="systemPara.value"
                       autocomplete="off" placeholder="" check-type="required">
            </div>
        </div>
        <div class="form-group">
            <label for="describe" class="col-sm-3 control-label">描述:</label>

            <div class="col-sm-7">
                <textarea  class="form-control" id="describe" name="describe" ng-model="systemPara.describe"
                       autocomplete="off" placeholder="" rows="7">
                    </textarea>
            </div>
        </div>

    </form>
</div>
</body>
</html>

