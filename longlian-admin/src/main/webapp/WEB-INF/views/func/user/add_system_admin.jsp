<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="coursePrivateCardDto">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>管理员添加</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/upload.css"/>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
    <script src="${ctx}/web/res/fileupload/upload.js"></script>
    <script src="${ctx}/web/res/js/func/coursePrivateCard/app.js"></script>
    <script>
        function  getAdminUserInfo(t) {
            var userId = $(t).val();
            $.ajax({
                type: "POST",
                url: "/appUser/findUserByWhere?id=" + userId,
                success: function (data) {
                    if (data.code=='000000') {
                        $('#adminName').val(data.data.name);
                    } else {
                        jbox_notice({content: "查询失败", autoClose: 2000, className: "error"});
                    }
                }
            })
        }
        function submitSystem(){
            var adminId = $('#adminId').val();
            var adminName = $('#adminName').val();
            $.ajax({
                type: "POST",
                url: "/appUser/add_system_admin?adminId=" + adminId+"&adminName="+adminName,
                success: function (data) {
                    if (data.code=='000000') {
                        jbox_notice({content: "添加成功", autoClose: 2000, className: "success"});
                        window.location.href='/appUser/managerList';
                    } else {
                        jbox_notice({content: "查询失败", autoClose: 2000, className: "error"});
                    }
                }
            })
        }
    </script>
</head>

<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%">

        <div class="form-group">
            <label class="col-sm-3 control-label">管理员id:</label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="adminId" name="adminId"
                       onblur="getAdminUserInfo(this)"  autocomplete="off" placeholder="输入要设置的管理员id" check-type="required" maxlength="20">
            </div>
        </div>
        <div class="form-group ">
            <label class="col-sm-3 control-label">管理员名称:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="adminName" name="adminName" readonly/>
            </div>
        </div>
        <div class="form-group ">
            <label class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
                <button type="button" class="btn btn-success passBtn"  onclick="submitSystem()">
                    <i class=" glyphicon glyphicon-ok"></i>确定
                </button>
                <button type="button" class="btn btn-success passBtn"  onclick="javascript:history.go(-1);">
                    <i class=" glyphicon glyphicon-ok"></i>返回
                </button>
            </div>
        </div>
    </form>
</div>
</body>
</html>
