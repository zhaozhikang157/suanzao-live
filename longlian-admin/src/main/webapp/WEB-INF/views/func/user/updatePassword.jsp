<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>密码修改</title>
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
        var password="";
        var newPassword1 = "";
        var newPassword2 = "";
        $.fn.validation.defaults = {
            validRules : [
                {name: 'repeatName', validate: function(value) {    //判断新密码是否一致
                    if(newPassword1!=newPassword2 && newPassword1!="" &&newPassword2!=""){
                        return true;
                    }
                }, defaultMsg: '新密码输入不一致,请重新输入!'},
                {name: 'required', validate: function(value) {
                    return ($.trim(value) == '');
                }, defaultMsg: '请输入内容。'},
                {name: 'checkOldPassword', validate: function(value) {     //判断是否和旧密码相同
                    if(newPassword1==password && newPassword1!="" && password!=""){
                        return true;
                    }
                }, defaultMsg: '新密码和旧密码一致,请重新输入!'},
                {name: 'valLength', validate: function(value) {             //判断密码是否符合规则
                    if(!/^[^\s\u4e00-\u9fa5]{6,12}$/.test(value)){
                        return true;
                    }
                }, defaultMsg: '请输入6-12位不含中文,空格的字符!'},
            ],
            reqmark:true,
            callback:null,  //function(obj,params){};
            icon:false      //=icon=true 表示显示图标，默认不显示
        };

        function updatePassword(){
            if ($("form").valid(null,"error!")==false){
                return;
            }
            var param = "oldPassword="+password+"&newPassword="+newPassword1;
            var obj = tools.requestRs("/user/updatePassWord", param, 'post');
            if(!obj.success){
                jbox_notice({content: "旧密码与登录密码不一致,请重新输入旧密码", autoClose: 3000, className: "error"});
            }else{
                jbox_notice({content: "修改成功!", autoClose: 2000, className: "success"});
                setTimeout('myrefresh()',1000);
            }
        }
        function myrefresh() {
            $("#password").val("");
            $("#newPassword1").val("");
            $("#newPassword2").val("");
            window.location.reload();
        }

        function getPassWord(){
            password = $("#password").val();
            newPassword1 = $("#newPassword1").val();
            newPassword2 = $("#newPassword2").val();
        }
    </script>
</head>
<body>

<div class="container" style="width:86%;float: left;">
<div style="margin-top:10px;">
    <form class="form-horizontal form-horizontal-my-from" id="form1" style="width: 100%">
        <div class="form-group">
            <label  class="col-sm-5 control-label">请输入旧密码:</label>
            <div class="col-sm-3">
                <input type="password" class="form-control" name="password" id="password" placeholder="请输入登录密码"
                       autocomplete="off" placeholder="" check-type="required checkOldPassword" onblur="getPassWord()">
            </div>
        </div>

        <div class="form-group">
            <label  class="col-sm-5 control-label">请输入新密码:</label>
            <div class="col-sm-3">
                <input type="password" class="form-control" id="newPassword1" placeholder="请输入6-12位不含中文,空格的字符"
                       autocomplete="off" placeholder="" check-type="required repeatName checkOldPassword valLength" onblur="getPassWord()">
            </div>
        </div>

        <div class="form-group">
            <label  class="col-sm-5 control-label">请输入新密码:</label>
            <div class="col-sm-3">
                <input type="password" class="form-control" id="newPassword2" placeholder="请输入6-12位不含中文,空格的字符"
                       autocomplete="off" placeholder="" check-type="required repeatName checkOldPassword valLength" onblur="getPassWord()">
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-6 control-label">
                <button type="button" class="btn btn-info" onclick="updatePassword()">
                    <i class=" glyphicon"></i> 确定修改
                </button>
            </div>
        </div>

    </form>
</div>
    </div>
</body>
</html>
