<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="liveRoom">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>审核处理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <style>
        #form1 input {
            border: none;
        }

        .tappic img {
            display: block;
            width: 105px;
            height: 98px;
        }

        .tappic {
            float: left;
        }

        .picboxs {
            margin-top: 20px;
        }
    </style>
    <script>
        var id = ${id};
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        });
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/liveRoom/app.js"></script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label class="col-sm-3 control-label">聊天室名称:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="name" readonly name="name" ng-model="liveRoom.name"
                       autocomplete="off">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">用户名:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="appName" readonly name="appName" ng-model="liveRoom.appName"
                       autocomplete="off">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">手机号:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="mobile" readonly name="mobile" ng-model="liveRoom.mobile"
                       autocomplete="off">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">微信号:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="weixinNum" readonly name="weixinNum" ng-model="liveRoom.weixinNum"
                       autocomplete="off">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">处理状态:</label>

            <div class="col-sm-7">
                <select class="form-control" id="status" name="status" ng-model="liveRoom.status"
                        check-type="select_required">
                    <option value="">--请选择--</option>
                    <option value="1">通过</option>
                    <option value="2">不通过</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">审核备注:</label>

            <div class="col-sm-7">
                <textarea class="form-control" style="resize: none;height: 100px" name="authRemark" autocomplete="off"
                          ng-model="liveRoom.authRemark" placeholder="限80字" maxlength="80"></textarea>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">身份证正反面:</label>

            <div class="col-sm-7">
            </div>
        </div>
    </form>

    <div class="form-group picboxs">
    <div class="col-sm-6 tappic" style="padding-left: 166px"><img src="" id="idCardFront" alt=""/></div>
    <div class="col-sm-6 tappic"><img src="" id="idCardRear" alt=""/></div>
    </div>
</div>
</body>
</html>

