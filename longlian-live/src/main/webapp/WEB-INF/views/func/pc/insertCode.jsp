<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="inviCode">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>生成邀请码</title>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/css/pop.css">
    <script src="${ctx}/web/res/pc/angular/angular.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/pc/angular/angular-messages.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/inviCode/app.js"></script>
    <script src="${ctx}/web/res/pc/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/pc/my97/xdate.dev.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/pop.js"></script>
    <script>
        $(function () {
            $("#form1").validation({icon: true});
        });
    </script>
    <style>
        #form1 label{
            font-weight: normal;
            font-size: 18px;
        }
        .alone{
            margin-right: 14px;
        }
    </style>
</head>
<body>
<div style="margin-top:30px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group form-group-lg">
            <label class="col-sm-3 control-label">课程类型：</label>
            <div class="col-sm-5">
                <div class="checkbox">
                    <button type="button" class="btn btn-primary alone" ng-click="selectSingles()">单节课</button>
                    <button type="button" class="btn btn-default toger"  ng-click="selectSeries()">系列课</button>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">课程名称：</label>
            <div class="col-sm-2">
                <select ng-model="courseId" ng-options="data.LIVE_TOPIC for data in model" class="form-control">
                    <option value="0">请选择课程</option>
                    </option>
                </select>
            </div>

        </div>

        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label">邀请码数量：</label>

            <div class="col-sm-2">
                <input type="text" class="form-control" id="remark" name="remark" ng-model="inviCode.codeCount"
                       autocomplete="off" placeholder="请输入邀请码数量" check-type="positivIntege" maxlength="4">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"></label>
            <p class="col-sm-6" style="color: #999999">单批次邀请码数量不得超过1000</p>
        </div>

        <div class="form-group form-horizontal">
            <label class="col-sm-3 control-label">有效期：</label>

            <div class="col-sm-6 form-inline">
                <input type="text" class="form-control col-sm-2" id="startTime" name="startTime"
                       ng-model="inviCode.startTime | date:'yyyy-MM-dd HH:mm:ss'"
                       autocomplete="off" placeholder="请选择日期"
                       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'endTime\')}',skin:'twoer',charset:'gb2312'})"
                       onchange="setStartDate(this);"/> <label class="col-sm-1 control-label" style="text-align: center;line-height: 36px;padding: 0;width: 40px">至</label>
                <input type="text" class="form-control col-sm-2" id="endTime" name="endTime"
                       ng-model="inviCode.endTime | date:'yyyy-MM-dd HH:mm:ss'"
                       autocomplete="off" placeholder="请选择日期"
                       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startTime\')}',skin:'twoer',charset:'gb2312'})"
                       onchange="setEndDate(this);"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"></label>
            <p class="col-sm-3" style="color: #999999">有效期未选择则视为永久有效</p>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">使用须知：</label>

            <div class="col-sm-3">
                <textarea type="text" class="form-control" ng-model="inviCode.remark" autocomplete="off"
                       placeholder=" 1.一个邀请码只能使用一次&#10 2.邀请码不可折为现金.&#10 3.请在有效期内使用。" maxlength="500" style="resize: none;height: 100px"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"></label>
            <p class="col-sm-3">
                <button ng-click="sub()" id="sub" class="btn btn-primary">提交</button>
            </p>
        </div>
    </form>
</div>
</body>
</html>
</div>
</body>
<script>
    $(".alone").click(function(){
        $(this).removeClass('btn-default').addClass('btn-primary');
        $('.toger').removeClass('btn-primary').addClass('btn-default');
    })
    $(".toger").click(function(){
        $(this).removeClass('btn-default').addClass('btn-primary');
        $('.alone').removeClass('btn-primary').addClass('btn-default');
    })
</script>
</html>
