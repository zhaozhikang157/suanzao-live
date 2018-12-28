<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="coursePrivateCardDto">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程邀请卡设置</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/upload.css"/>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-process.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-validate.js"></script>
    <script src="${ctx}/web/res/fileupload/upload.js"></script>
    <script src="${ctx}/web/res/js/func/coursePrivateCard/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            $("#form1").validation({icon: true});
        });
        function myCallBack(data) {
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $("#src").show();
                $scope.setPicAddress(data.data.url);
                $('.Img').attr("src", data.data.url);
            } else {
                jbox_notice({content: "选择图片失败", autoClose: 2000, className: "error"});
            }
        }
    </script>
</head>

<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%"
          ng-controller="task">

        <div class="form-group">
            <label for="userId" class="col-sm-3 control-label">课程id:</label>

            <div class="col-sm-6">
                <input type="hidden" class="form-control" id="id" name="id" ng-model="coursePrivateCardDto.id"/>
                <input type="text" class="form-control" id="courseId" name="courseId" ng-model="coursePrivateCardDto.courseId"
                       ng-blur="getCourseAndTeacherInfo()"  autocomplete="off" placeholder="输入要设置的课程id" check-type="required" maxlength="20">
            </div>
        </div>
        <div class="form-group ">
            <label class="col-sm-3 control-label">课程名称:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="liveTopic" name="userId" ng-model="coursePrivateCardDto.liveTopic"
                       autocomplete="off" placeholder="" readonly check-type="required" maxlength="20">
            </div>
        </div>
        <div class="form-group">
            <label for="userId" class="col-sm-3 control-label">老师id:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="userId" name="userId" ng-model="coursePrivateCardDto.userId"
                       autocomplete="off" placeholder="" readonly check-type="required" maxlength="20">
            </div>
        </div>
        <div class="form-group ">
            <label class="col-sm-3 control-label">老师名称:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="appUserName" name="appUserName" ng-model="coursePrivateCardDto.appUserName"
                       autocomplete="off" placeholder="" readonly maxlength="20">
            </div>
        </div>

    </form>
</div>
</body>
</html>
