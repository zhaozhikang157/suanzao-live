<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>用户购买课程订单</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/dataTable/dataTables.min.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
    <script src="${ctx}/web/res/dataTable/dataTables.min.js" />
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/echarts.common.min.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/orders/courseOrderData.js"></script>
    <script src="${ctx}/web/res/js/func/orders/courseOrdersList.js"></script>
    <style>
        #leftbox{
            width: 460px;
            height: 300px;
            padding: 20px;
            float: left;
            background: white !important;
        }
    </style>
</head>

<body>
<div style="  width: 62%; float: left; margin-left: 71px;">
    <p><pr style="font-size:21px;font-weight:bold;">总体概况</pr> <pr style="margin-left: 201px;"><select class="selectDate"><option selected value="-1">全部</option><option value="0">本日</option><option value="1">本周</option><option value="2">本月</option><option value="3">本年</option></select></pr>
    </p>
    <div class="dataContent" style="height: 72px;margin-top: 21px;">
        <div style="float: left;height: 72px;width: 135px;     padding-left: 21px;   background: #d6d3d3">
            <pr>单节课总收入：</pr><pv style="font-size:21px;font-weight:bold;" class="singleCourseIncome"></pv>
        </div>
        <div style="float: left;height: 72px;width: 135px;margin-left: 79px;     padding-left: 5px;    background: #d6d3d3">
            <pr>序列课总收入：</pr><pv style="font-size:21px;font-weight:bold;" class="seriesCourseIncome"></pv>
        </div>
    </div>
    <div id="leftbox">
            txt
    </div>

</div>
<div class="container" style="width:86%;float: left;">
   <%-- <div id="H1">--%>
        <h2>
            用户购课明细
        </h2>
       <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
                <label class="control-label">课程名称：</label>
                <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
                <label class="control-label">购买人：</label>
                <input type="text" class="form-control" name="uname" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
                <label class="control-label">电话：</label>
                <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">

                <label class="control-label">开课时间：</label>
                <input type="text" class="Wdate form-control" id="cstartTime" name="cstartTime"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="cendTime" name="cendTime"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
            <p></p>
                <label class="control-label">购买时间：</label>
                <input type="text" class="Wdate form-control" id="startTime" name="startTime"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="endTime" name="endTime"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <button type="button" class="btn btn-info" onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
               <%-- <button type="button" class="btn btn-success" onclick="importExcel();">
                    <i class=" glyphicon glyphicon-export"></i> 导出
                </button>--%>
        </form>
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
              style="width: 2000px;">
       </table>
</div>
  <%--  <script>
        var firstDate = new Date();
        firstDate.setDate(1);
        var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
        var endDate = new Date(firstDate);
        endDate.setMonth(firstDate.getMonth() + 1);
        endDate.setDate(0);
        var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
        $("#startTime").val(first)  //获取当月第一天
        $("#endTime").val(lastDay);//默认时间为 当月 最后一天
    </script>--%>
<%--</div>--%>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">

</html>
