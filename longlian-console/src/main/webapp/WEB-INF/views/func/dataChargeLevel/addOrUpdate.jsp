<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="dataChargeLevel">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/dataChargeLevel/app.js?aa=4"></script>
    <script>
        var id = ${id};
        $(function () {
            $("#form").validation({icon: true});
        });
    </script>
    <style>

    </style>
</head>
<body>
<div  style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from"  id="form" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label class="col-sm-4 control-label">流量(GB)：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="amount"  name="amount"  ng-model="dataChargeLevel.amount" autocomplete="off" placeholder=""  check-type="required" >
                正整数
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">原价(元)：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="origPrice"  ng-model="dataChargeLevel.origPrice" autocomplete="off" placeholder=""  check-type="required" >
            </div>
    </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">优惠价(元)：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="origPrice"  ng-model="dataChargeLevel.prefPrice" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-4 control-label">有效期：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="invalidDate"  name="invalidDate"  ng-model="dataChargeLevel.invalidDate" autocomplete="off" placeholder=""  check-type="required" >
                0代表永久
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">有效期单位：</label>
            <div class="col-sm-3">
                    <select name="invalidDateUnit"  class="form-control" id="invalidDateUnit" ng-model="dataChargeLevel.invalidDateUnit">
                    <option value="0">天</option>
                    <option value="1">月</option>
                    <option value="2">年</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">是否是热门：</label>
            <div class="col-sm-3" style="padding-top: 7px">
                <input type="radio" name="isHot" ng-model="dataChargeLevel.isHot" value="0">不是
                <input type="radio" name="isHot" ng-model="dataChargeLevel.isHot" value="1">是
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">是否是零售：</label>
            <div class="col-sm-3" style="padding-top: 7px">
                <input type="radio" name="isRetail" ng-model="dataChargeLevel.isRetail" value="0">不是
                <input type="radio" name="isRetail" ng-model="dataChargeLevel.isRetail" value="1">是
            </div>
        </div>
        <div class="form-group muchs" style="display: none">
            <label class="col-sm-4 control-label">单价：</label>
            <div class="col-sm-3">
                <input type="text"  class="form-control"  name="unitPrice"  ng-model="dataChargeLevel.unitPrice" autocomplete="off" placeholder="">
            </div>
        </div>
    </form>
</div>
</body>
<script>
    $(function(){
        var options = document.getElementById('invalidDateUnit').children;
        options[1].selected=true;
        $("input[name='isRetail'][value='0']").prop('checked', true);//radio默认选中
        $("input[name='isHot'][value='0']").prop('checked', true);//radio默认选中
        $("input[name='isRetail']").change(function(){
            var muchs =  $("input[name='isRetail']:checked").val();
            if(muchs==1){
                $(".muchs").show();
            }else{
                $(".muchs").hide();
            }
        })
    })
</script>
</html>

