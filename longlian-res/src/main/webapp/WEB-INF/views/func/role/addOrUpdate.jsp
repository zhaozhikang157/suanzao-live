<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="mRes">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
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
    <script src="${requestContext.contextPath}/web/res/js/func/mRes/app.js"></script>
    <script>
        var newName = "";
        var oldName = "";
        var mName = "";
        $.fn.validation.defaults = {
            validRules : [
                {name: 'repeatName', validate: function(value) {
                    if(oldName==value){
                        return ($.trim(value) == value);
                    }
                }, defaultMsg: '该角色已存在,请重新输入。'},
                {name: 'select_required', validate: function(value) {
                    if(value == "?"){
                        return true;
                    }
                    return ($.trim(value) == '');
                }, defaultMsg: '请输入内容。'},
            ],
            reqmark:true,
            callback:null,  //function(obj,params){};
            icon:false      //=icon=true 表示显示图标，默认不显示
        };
        function repeatName(){
            newName = $("#roleName").val();
            if(mName!=newName){
                if(!newName==""||!newName==null){
                    var param = "roleName="+newName;
                    var obj = tools.requestRs("/mRes/findRoleName", param, 'post');
                    if(!obj.success){
                        oldName = obj.data;
                    }
                }
            }
        }

        function getName(){
            mName = $("#roleName").val();
            document.getElementById('roleName').onclick=null;
        }
    </script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label class="col-sm-3 control-label">角色:</label>
            <input type="hidden" class="form-control" name="id" ng-model="mRes.id" autocomplete="off">
            <div class="col-sm-5">
                <input type="text" class="form-control" name="name" ng-model="mRes.name" autocomplete="off" maxlength="10"
                       placeholder="请输入角色" check-type="select_required repeatName" onblur="repeatName()" onclick="getName()" id="roleName">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">状态:</label>
            <div class="col-sm-5">
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

        <div class="form-group">
            <label class="col-sm-3 control-label">角色描述:</label>
            <div class="col-sm-5">
                <textarea class="form-control" name="description" maxlength="30" style="resize:none"
                       ng-model="mRes.description" autocomplete="off"/>
            </div>
        </div>

    </form>
</div>
</body>
</html>
