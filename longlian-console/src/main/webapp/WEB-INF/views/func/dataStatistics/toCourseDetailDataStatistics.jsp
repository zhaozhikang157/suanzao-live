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
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
</head>

<script src="${ctx}/web/res/js/func/dataStatistics/courseDetailPageStatistics.js"></script>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 class="courseTitle">
        课程访问统计:
    </h2>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <label>端口：</label>
            <select class="form-control" id="portType" name="portType" style="display: inline-block;width: auto">
                <option value="0" selected="">全部</option>
                <option value="1">iOS</option>
                <option value="2">Android</option>
                <option value="3">微信</option>
            </select>
            <label>页面：</label>
            <select class="form-control" id="pageUrl" name="pageUrl"
                    style="display: inline-block;width: auto">
                <option value="0" selected>课程详情</option>
                <option value="1">直播间</option>
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
                   placeholder="" style="width: 120px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 120px;display: inline-block;">
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
            <div class="totalVisite"></div>

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
    <div class="visitDialog" style="">
        <p class="close" style="padding-right: 10px;padding-top: 10px;" onclick="closev()">X</p>
        <p style="height: 30px;border-bottom: 1px #bdb0b0 solid;    font-size: 15px;font-weight: bold;margin-top: 10px;">访问人数来源统计：</p>
        <table class="vt">
            <th class="vp">
                <td class="vp">课程名称：</td><td class="liveTopic-p"></td>
                <td class="vp">统计时间：</td><td class="rangeDate-p"></td>
                <td class="vp">访问人数：</td><td class="vount-p"></td>
            </th>
        </table>
        <table class="vt">
            <tr><td class="vp">微信</td><td class="vp">Android</td><td class="vp">ios</td></tr>
            <tr><td class="weixinCountp"></td><td class="androidCountp"></td><td class="iosCountp"></td></tr>
        </table>
      </div>
    </div>
    <style>
        .table_list_box{
            table-layout:fixed !important;
        }
    </style>
</div>
<%--<div class="totalVisitCount" style="display: none;">
   <div style="height: 25px;"> <p style="float: left;">总访问量统计:</p> <p class="close">X</p></div>--%>
<%--  <div class="stay">
      <div class="subStay1">日停留时长:<label id="dayStay">0</label></div>
      <div class="subStay1">周停留时长:<label id="weekStay">0</label></div>
      <div class="subStay1">月停留时长:<label id="monthStay">0</label></div>
  </div>
  <div class="stay">
      <div>日访问人数:<label id="dayPcount">0</label></div>
      <div>周访问人数:<label id="weekPcount">0</label></div>
      <div>月访问人数:<label id="monthPcount">0</label></div>
  </div>
  <div class="stay">
      <div>日访问次数:<label id="dayVisitCount">0</label></div>
      <div>周访问次数:<label id="weekVisitCount">0</label></div>
      <div>月访问次数:<label id="monthVisitCount">0</label></div>
  </div>--%>
<%--</div>--%>
<script>

</script>
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
