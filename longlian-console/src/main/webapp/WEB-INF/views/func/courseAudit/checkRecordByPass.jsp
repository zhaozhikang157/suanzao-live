<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>bootstrap table</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <link rel="stylesheet" href="${ctx}/web/res/my97/skin/default/datepicker.css"/>
  <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
  <link rel="stylesheet" type="text/css" href="${ctx}/web/res/my97/skin/whyGreen/datepicker.css"/>
  <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
  <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
  <script src="${ctx}/web/res/my97/calendar.js"></script>
  <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
  <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
  <script >
    function doInit(){
      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/courseAudit/getCheckRecordList?auditStatus=1&isGarbage=1",   //auditStatus：审核状态 0-待审核 1-通过 -1-不通过
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
          {
            field: 'id',
            title: 'ID',
            align: 'center',
            valign: 'middle'
          }, {
            field: 'startTime',
            title: '开课时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'endTime',
            title: '结束时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'liveTopic',
            title: '课程名称',
            align: 'center',
            valign: 'middle'
          }, {
            field: 'status',
            title: '课程状态',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span id='status-" + row.id + "'>上线</span>"
              } else if (value == '1') {
                return "<span  id='status-" + row.id + "' style='color:red'>下线</span>"
              }
            }
          }
          , {
            field: 'liveWay',
            title: '课程类型',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span>视频</span>"
              } else if (value == '1') {
                return "<span>语音</span>"
              }
            }
          },
          {
            field: 'chargeAmt',
            title: '课程费用',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'joinCount',
            title: '报名人数',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              var str = "<span onclick='showSpan(this)'>"
                      + "<span class='updateSpan' style='display:none '>"
                      + "<input type='text' title='增加人数' value='0'/><input type='button' value='增加' onclick='addJoinCount(this , " + row.id + ")'/></span>"
                      + "<span class='showSpan'>"
                      + value + "</span></span>";
              return str;
            }
          },
          {
            field: 'appUserName',
            title: '审核人',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'auditTime',
            title: '审核时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          }
        ]
      });
    }
    //传递的参数
    function queryParams(params) {

      var liveTopic = $("input[name='liveTopic']").val().trim();
      var beginTime = $("#beginTime").val();  //起始时间
      var endTime = $("#endTime").val();    //结束时间
      if (beginTime != "")beginTime = beginTime + " 00:00:00";
      if (endTime != "")endTime = endTime + " 23:59:59";
      var status = $("select[name='status'] option:selected").val();    //状态

      if (!params) {
        params = {};
      }
      params["liveTopic"] = liveTopic;
      params["beginTime"] = beginTime;
      params["endTime"] = endTime;
      params["status"] = status;
      return params;
    }
    function refresh(){
      $('#table-bootstrap').bootstrapTable('refresh', {query:{a:1,b:2}});
    }
    $(window).resize(function () {
      $('#table-bootstrap').bootstrapTable('resetView', {
        height: getHeight()
      });
    });
    function getHeight() {
      return $(window).height()-10 - $('#toolbar').outerHeight(true);
    }
    function detailFormatter(index, row) {
      var html = [];
      $.each(row, function (key, value) {
        html.push('<p><b>' + key + ':</b> ' + value + '</p>');
      });
      return html.join('');
    }

    /**
     *点击查询
     */
    function  doQuery(flag){
      if(flag){
        $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
      }else{
        $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
      }
    }
    

    function closeWindow(obj){
      if(obj.success){
        window.BSWINDOW.modal("hide");
        jbox_notice({content:obj.msg,autoClose:2000 ,className:"success"});
        doQuery();
      }else{
        window.BSWINDOW.modal("hide");
        jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
        doQuery();
      }
    }
    

  </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
  <h2>
    审核记录
  </h2>
  <div id="toolbar">
    <form class="form-horizontal" id="form1" name="form1">
      <label>开课时间:</label>
      <input type="text" class="Wdate form-control" id="beginTime"
             onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
             style="width: 120px;display: inline-block;"/>
      至<input type="text" class="Wdate form-control" id="endTime"
              onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
              style="width: 120px;display: inline-block;"/>
      <label>课程主题：</label>
      <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
             placeholder="" style="width: 220px;display: inline-block;">

      <label>课程状态:</label>
      <select class="form-control" id="status" name="status"
              style="display: inline-block;width: auto">
        <option value="">全部</option>
        <option value="0">上线</option>
        <option value="1">下线</option>
      </select>
      <button type="button" class="btn btn-info" onclick="doQuery(true);">
        <i class=" glyphicon glyphicon-search"></i> 查询
      </button>
    </form>
  </div>
  <table id="table-bootstrap"
         data-pagination="true"
         data-side-pagination="server"
         data-page-list="[10, 20, 50, 100,ALL]"
         data-show-refresh="true"
         data-show-toggle="true"
         data-show-columns="true"
         data-show-pagination-switch="true">
  </table>
</div>
</body>
<script>
  var firstDate = new Date();
  firstDate.setDate(1);
  var first=new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
  var endDate = new Date(firstDate);
  endDate.setMonth(firstDate.getMonth()+1);
  endDate.setDate(0);
  var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
  $("#createTimeBeginStr").val(first)  //获取当月第一天
  $("#createTimeEndStr").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
