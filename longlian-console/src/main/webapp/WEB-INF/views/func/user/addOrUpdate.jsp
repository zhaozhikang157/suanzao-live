<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="mUser">
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
    <script src="${requestContext.contextPath}/web/res/js/func/user/app.js"></script>
    <script>
        var userName = "";
        var oldName = "";
        var mUserName = "";
        $.fn.validation.defaults = {
            validRules : [
                {name: 'repeatName', validate: function(userName) {
                    if(oldName==userName){
                        return ($.trim(userName) == userName);
                    }
                }, defaultMsg: '用户名已存在,请重新输入。'},
                {name: 'select_required', validate: function(value) {
                    if(value == "?"){
                        return true;
                    }
                    return ($.trim(value) == '');

                }, defaultMsg: '请输入内容。'},
                {
                    name: 'mobile', validate: function (value) {
                    if (value != "" && value != null) {
                        return (!/^0?(13[0-9]|15[0-9]|17[01678]|18[0-9]|14[57])[0-9]{8}$/.test(value));
                    }
                }, defaultMsg: '请输入正确的手机号。'
                },
                {
                    name: 'mail', validate: function (value) {
                    if (value != "" && value != null) {
                        return (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(value));
                    }
                }, defaultMsg: '请输入邮箱地址。'
                },
                {name: 'positivIntege', validate: function(value) {
                    if(value.trim() == ""){
                        return false;
                    }
                    return (!/^[1-9][0-9]*$/.test(value));}, defaultMsg: '请输入正整数。'},
            ],
            reqmark:true,
            callback:null,  //function(obj,params){};
            icon:false      //=icon=true 表示显示图标，默认不显示
        };
        function repeatName(){
            userName = $("#userId").val();
            if(mUserName!=userName){
                if(!userName==""||!userName==null){
                    var param = "userId="+userName+"&id="+id;
                    var obj = tools.requestRs("/user/findUserId", param, 'post');
                    if(!obj.success){       //提交用户名重复
                        oldName = obj.data;
                    }
                }
            }
        }

        function getName(){
            mUserName = $("#userId").val();
            document.getElementById('userId').onclick=null;
        }
    </script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label class="col-sm-3 control-label">用户名:</label>
            <input type="hidden" class="form-control" name="id" ng-model="mUser.id" autocomplete="off">
            <div class="col-sm-5">
                <input type="text" class="form-control" name="userId" ng-model="mUser.userId" autocomplete="off" maxlength="10"
                       placeholder="请输入用户名" check-type="select_required repeatName" onblur="repeatName()" onclick="getName()" id="userId">
            </div>
        </div>

        <div class="form-group">
            <label for="name" class="col-sm-3 control-label">姓名:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="name" name="name"
                       ng-model="mUser.name" autocomplete="off" placeholder="" maxlength="10">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">角色:</label>
            <div class="col-sm-5">
                <select class="form-control" ng-model="mUser.resId" ng-options="item.id as item.name for item in role" check-type="select_required" >
                    <option value="">--请选择--</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">性别:</label>
            <div class="col-sm-5">
                <div class="checkbox">
                    <label>
                        <input type="radio" value="0" name="areaType" ng-model="mUser.gender"/> &nbsp;男
                    </label>
                    <label>
                        <input type="radio" value="1" name="areaType" ng-model="mUser.gender"/> &nbsp;女
                    </label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label for="tel" class="col-sm-3 control-label">联系方式:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="tel" name="tel"
                       ng-model="mUser.tel" autocomplete="off" check-type="mobile">
            </div>
        </div>

        <div class="form-group">
            <label for="email" class="col-sm-3 control-label">常用邮箱:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="email" name="email"
                       ng-model="mUser.email" autocomplete="off" check-type="mail" >
            </div>
        </div>

        <div class="form-group">
            <label for="qq" class="col-sm-3 control-label">QQ:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="qq" name="qq"
                       ng-model="mUser.qq" autocomplete="off" check-type="positivIntege" >
            </div>
        </div>

    </form>
</div>
</body>
</html>
