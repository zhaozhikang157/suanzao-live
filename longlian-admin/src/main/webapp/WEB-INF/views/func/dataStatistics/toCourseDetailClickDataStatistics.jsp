<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/dataTable/dataTables.min.css"/>
    <script src="${ctx}/web/res/dataTable/dataTables.min.js" />
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
</head>
<script>
    $(document).ready(function(){
        doInit();
    });
</script>
<script src="${ctx}/web/res/js/func/dataStatistics/courseDetailClickPageStatistics.js"></script>
<body onload="doInit();">
<div class="container" style="width: 86%;float: left;">
    <h2>
        课程点击数统计:
    </h2>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">

            <label>端口：</label>
            <select class="form-control" id="portType" name="portType"
                    style="display: inline-block;width: auto">
                <option value="0" selected>全部</option>
                <option value="1">iOS</option>
                <option value="2">Android</option>
                <option value="3">微信</option>
            </select>
            <label>访问日期：</label>
            <input type="text" class="Wdate form-control" id="beginDate"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endDate\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endDate"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginDate\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>课程名称：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 220px;display: inline-block;">


            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-success" onclick="importExcel();">
                <i class=" glyphicon glyphicon-export"></i> 导出
            </button>
        </form>
        <div>
            <label style="float: left;">时间：</label>
            <select class="form-control" id="dateRange" name="dateRange"  style="display: inline-block;width: auto;float: left;">
                <option value="0" selected>日</option>
                <option value="1">周</option>
                <option value="2">月</option>
            </select>
            <div class="totalVisite" style=" float: left;margin-right:124px;font-size: 20px;"></div>
        </div>
    </div>
    <table id="table-bootstrap"
           border="0" cellpadding="4" cellspacing="0"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered"
           style="width: 1400px;">
    </table>
    <style>
        .table_list_box{
            table-layout:fixed !important;
        }
    </style>
</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
<%--<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#beginTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>--%>
</html>
