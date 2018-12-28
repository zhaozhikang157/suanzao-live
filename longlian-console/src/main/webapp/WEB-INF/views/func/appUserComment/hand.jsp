<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="appUserComment">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>反馈处理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
        var handStatus =${handStatus};       //处理状态
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        });
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/appUserComment/app.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label class="col-sm-3 control-label">处理状态:</label>

            <div class="col-sm-7">
                <select class="form-control" ng-model="appUserComment.handStatus"
                        ng-options="item.value  as item.text for item in handStatus"
                        check-type="select_required">
                    <option value="">--请选择--</option>
                </select>
            </div>
        </div>


        <div class="form-group">
            <label for="liveTopic" class="col-sm-3 control-label">直播主题:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="liveTopic" name="liveTopic"
                       ng-model="appUserComment.liveTopic"
                       autocomplete="off" readonly>
            </div>
        </div>

        <div class="form-group">
            <label for="handRemarks" class="col-sm-3 control-label">课程介绍:</label>

            <div class="col-sm-5">
                <textarea class="form-control" style="resize: none;height: 100px" id="courseRemark" name="courseRemark"
                          autocomplete="off"
                          ng-model="appUserComment.courseRemark" readonly></textarea>
            </div>
        </div>

        <div class="form-group">
            <label for="handRemarks" class="col-sm-3 control-label">处理内容:</label>

            <div class="col-sm-5">
                <textarea class="form-control" style="resize: none;height: 100px" id="handRemarks" name="handRemarks"
                          autocomplete="off"
                          ng-model="appUserComment.handRemarks" placeholder="限80字" maxlength="80"></textarea>
            </div>
        </div>

    </form>
</div>
</body>
</html>

