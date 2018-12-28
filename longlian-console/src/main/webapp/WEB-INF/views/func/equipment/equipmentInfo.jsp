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

<script src="${ctx}/web/res/js/func/equipment/equipment.js"></script>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
  <h2>
    设备ID统计:
  </h2>
  <div id="toolbar">

    <form class="form-horizontal" id="form1" name="form1">
      <label>时间：</label>
      <input type="text" class="Wdate form-control" id="beginTime"
             onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
             style="width: 120px;display: inline-block;"/>
      至<input type="text" class="Wdate form-control" id="endTime"
              onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
              style="width: 120px;display: inline-block;"/>
      <label>用户昵称：</label>
      <input type="text" class="form-control" name="name" autocomplete="off" placeholder="用户昵称"
             placeholder="" style="width: 220px;display: inline-block;">
      <label>设备类型：</label>
        <select class="form-control" id="equipmentType" name="equipmentType"
                style="display: inline-block;width: auto">
            <option value="">全部</option>
            <option value="IDFA">IDFA</option>
            <option value="IMEI">IMEI</option>
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
