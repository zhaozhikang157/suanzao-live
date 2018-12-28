<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="recoCourse">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>公众号审核</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        var id = ${id};
        $(function(){
            //1. 简单写法：
            $("#form1").validation({icon:true});
        });

    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/recoCourse/app.js"></script>
    <style>
        .titles{
            width: 250px;
            text-align: right;
        }
        .much{
            display: inline-block;
        }
        .texry{
            width: 250px;
            height: 70px;
            font-size: 10px;
            resize: none;
            font-size: 14px;
        }
        .footerBtn{
            clear: both;
            text-align: center;
            margin-top: 80px;
        }
    </style>
</head>
<body>
<div  style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">
        <input type="hidden" class="form-control"  ng-model="recoCourse.courseId" autocomplete="off" value="${id}" id="courseId">
        <div class="form-group" >
            <label class="col-sm-3 control-label"><span style="color: red"></span>推荐位置：</label>
            <div class="col-sm-6">
                <select ng-model="recoPos" id="recoPos" check-type="required">
                    <option value="">--请选择--</option>
                    <option ng-repeat="item in posList" value="{{item.value}}">{{item.text}}</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label  class="col-sm-3 control-label">排序:</label>
            <div class="col-sm-6">
                <input type="text" class="form-control"  ng-model="recoCourse.sort" autocomplete="off" placeholder="" maxlength="10" check-type="required"  id="sort">
            </div>
        </div>
    </form>
</div>
<div class="footerBtn">
    <button type="button" class="btn btn-success passBtn"  onclick="toCheck()">
        <i class=" glyphicon glyphicon-ok"></i> 通过
    </button>
</div>
</body>
</html>
