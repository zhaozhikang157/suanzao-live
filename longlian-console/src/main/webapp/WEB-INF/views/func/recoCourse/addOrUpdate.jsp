<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="recoCourse">
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
    <script src="${requestContext.contextPath}/web/res/js/func/recoCourse/app.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <input type="hidden" class="form-control" name="id" ng-model="recoCourse.courseId" autocomplete="off" >
        <div class="form-group">
            <label for="courseId" class="col-sm-3 control-label">课程id:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="courseId"
                       ng-model="recoCourse.courseId" autocomplete="off" placeholder="" maxlength="10" check-type="required"  onblur="repeatName()" id="courseId">
            </div>
        </div>
        <div class="form-group" style="position:relative;">
            <label class="col-sm-3 control-label">推荐位置：</label>
            <div class="col-sm-6">
                <select class="form-control" ng-model="recoCourse.recoPos"  check-type="required" id="recoPos" ng-options="item.value  as item.text for item in posList " >
                    <option value="">请选择</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="sort" class="col-sm-3 control-label">排序:</label>
            <div class="col-sm-7">
                <input type="text" class="form-control"  name="sort"
                       ng-model="recoCourse.sort" autocomplete="off" placeholder="" maxlength="10" check-type="required"  onblur="repeatName()"  id="sort">
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
