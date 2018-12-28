<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="wechatOfficialRoom">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>公众号审核</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/default/datepicker.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/my97/skin/whyGreen/datepicker.css"/>
    <script src="${ctx}/web/res/angular/angular.min.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.min.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        var id = ${id};
        $(function(){
            //1. 简单写法：
            $("#form1").validation({icon:true});
        });
        function getNowFormatDate() {
            var date = new Date();
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                    + " " + date.getHours() + seperator2 + date.getMinutes()
                    + seperator2 + date.getSeconds();
            return currentdate;
        }
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/wechatOfficial/app.js"></script>
    <style>
        .titles{
            width: 250px;
            text-align: right;
        }
        .much{
            display: inline-block;
        }
        .texry{
            position: absolute;
            left: 252px;
            top: 0;
            width: 250px;
            height: 66px;
            font-size: 10px;
            margin-left: 4px;
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
    
        <div class="form-group" style="position:relative;">
            <label class="col-sm-3 control-label">负责人：</label>
            <div class="col-sm-6">
                <select class="form-control" ng-model="wechatOfficialRoom.manager"  check-type="required" id="managerId" ng-options="item.id  as item.name for item in managerList " >
                <option value="">请选择</option>
                </select>
            </div>
        </div>
        <div class="form-group" >
            <label class="col-sm-3 control-label">有效期：</label>
            <div class="col-sm-3" id="startTimeInput">
                <input type="text" class="form-control" id="freeDate" name="freeDate"autocomplete="off" placeholder=""
                       onclick="WdatePicker({skin:'twoer',charset:'gb2312',isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:getNowFormatDate(),maxDate:'2088-03-10 20:59:30'})"
                       onchange="setDate(this);" check-type="required"/>

            </div>
        </div>
    </form>
</div>
<%--<div class="footerBtn">--%>
    <%--<button type="button" class="btn btn-danger" style="margin-right: 6px"  onclick="toCheck(-1)">--%>
        <%--<i class=" glyphicon glyphicon-remove"></i> 不通过--%>
    <%--</button>--%>
    <%--<button type="button" class="btn btn-success passBtn"  onclick="toCheck(1)">--%>
        <%--<i class=" glyphicon glyphicon-ok"></i> 通过--%>
    <%--</button>--%>
<%--</div>--%>
</body>
</html>
