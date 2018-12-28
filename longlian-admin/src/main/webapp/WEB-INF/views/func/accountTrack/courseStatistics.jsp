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
    <script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/echarts.common.min.js"></script>
    <style>
        #leftbox{
            width: 700px;
            height: 300px;
            padding: 20px;
            float: left;
            background: white !important;
        }
    </style>
</head>

<script src="${ctx}/web/res/js/func/accountTrack/courseStatistics.js"></script>
<script>
    $(document).ready(function() {
        doInit();
        getCourseDatas({"selectDate":$(".selectDate").val()});
    });
</script>
<body onload="doInit();">
    <div style="  width: 62%; float: left; margin-left: 20px;">
        <p><pr style="font-size:21px;font-weight:bold;">总体概况</pr> <pr style="margin-left: 782px;"><select class="selectDate"><option selected value="-1">全部</option><option value="0">本日</option><option value="1">本周</option><option value="2">本月</option><option value="3">本年</option></select></pr>
        </p>
        <div class="dataContent" style="height: 72px;margin-top: 20px;">
            <div style="float: left;height: 72px;width: 104px;  margin-left: 70px;   padding-left: 5px;   background: #d6d3d3">
                <pr>单节课总数量：</pr><pv style="font-size:21px;font-weight:bold;" class="singleNum"></pv>
            </div>
            <div style="float: left;height: 72px;width: 104px;margin-left: 150px;     padding-left: 5px;    background: #d6d3d3">
                <pr>序列课总数量：</pr><pv style="font-size:21px;font-weight:bold;" class="seriesNum"></pv>
            </div>
            <div style="float: left;height: 72px;width: 104px;  margin-left: 150px;    padding-left: 5px;   background: #d6d3d3">
                <pr>付费单节课总数量：</pr><pv style="font-size:21px;font-weight:bold;" class="singlePayNum"></pv>
            </div>
            <div style="float: left;height: 72px;width: 104px;margin-left: 150px;     padding-left: 5px;    background: #d6d3d3">
                <pr>付费序列课总数量：</pr><pv style="font-size:21px;font-weight:bold;" class="seriesPayNum"></pv>
            </div>
        </div>
        <div id="leftbox">
            txt
        </div>

    </div>
<div class="container" style="width:86%;float: left;">
    <h2>
        课程统计:
    </h2>
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label>开课时间：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label class="control-label">结束时间：</label>
            <input type="text" class="Wdate form-control" id="cebeginTime" name="cebeginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="ceendTime" name="ceendTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>课程名称：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 220px;display: inline-block;">

            <label>课程状态：</label>
            <select class="form-control" id="status" name="status"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">上线</option>
                <option value="1">下线</option>
            </select>
            <%--  <label>课程分类：</label>
              <select class="form-control" id="courseType" name="courseType"
                      style="display: inline-block;width: auto">
              </select>--%>
            <label>是否是系列课：</label>
            <select class="form-control" id="isSerier" name="isSerier"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">不是</option>
                <option value="1">是</option>
            </select>
            <label>收费类型：</label>
            <select class="form-control" id="isFree" name="isFree"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="1">收费</option>
                <option value="0">免费</option>
            </select>

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-success" onclick="importExcel();">
                <i class=" glyphicon glyphicon-export"></i> 导出
            </button>
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
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<%--<script src="${ctx}/web/res/my97/WdatePicker.js"></script>--%>
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
<%--<link rel="stylesheet" href="${ctx}/web/res/style/css/common/btable.css">--%>
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#beginTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
