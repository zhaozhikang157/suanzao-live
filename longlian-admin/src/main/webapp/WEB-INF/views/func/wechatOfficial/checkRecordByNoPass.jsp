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
        url:"/wechatOfficial/getCheckRecordList?auditStatus=-1",   //auditStatus：审核状态 0-待审核 1-通过 -1-不通过",
        classes: 'table table-hover',
        height:getHeight(),
        toolbar:"#toolbar",
        pageSize:10,
        queryParams: queryParams,
        columns: [
          {
            field: 'id',
            title: 'id',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'nickName',
            title: '授权方昵称',
            align: 'center',
            width:'260',
            valign: 'middle'
          },
          {
            field: 'liveId',
            title: '直播间id',
            align: 'center',
            valign: 'middle'
          },    {
            field: 'liveName',
            title: '直播间名称',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'reserveReminderId',
            title: '预约提醒ID',
            align: 'center',
            width:'660',
            valign: 'middle'
          },
          {
            field: 'contactWechat',
            title: '联系微信号',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'contactMobile',
            title: '联系手机号',
            align: 'center',
            valign: 'middle'
          }, {
            field: 'status',
            title: '认证状态',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span id='status-" + row.id + "'>已授权</span>"
              } else if (value == '1') {
                return "<span  id='status-" + row.id + "' style='color:red'>取消</span>"
              }
            }
          },
          {
            field: 'serviceType',
            title: '授权方公众号类型',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span id='status-" + row.id + "'>订阅号</span>"
              } else if (value == '1') {
                return "<span  id='status-" + row.id + "' >历史老帐号升级后的订阅号</span>"
              }else if (value == '2') {
                return "<span  id='status-" + row.id + "' >服务号</span>"
              }
            }
          },
          {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            width:'300',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'bindTime',
            title: '绑定时间',
            align: 'center',
            valign: 'middle',
            width:'300',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'auditUserName',
            title: '审核人姓名',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'auditTime',
            title: '审核时间',
            align: 'center',
            width:'300',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },{
            field: 'auditRemark',
            title: '审核备注',
            align: 'center',
            width:'300',
            valign: 'middle'
          },{
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            width:'460',
            formatter: function (value, row) {
              return [
                '<a href="javascript:void(0)" onclick="unBindLiveRoom(' + row.id+ ');" >' + '解绑</a>'
              ].join('');
            }
          }]
      });
    }
    function unBindLiveRoom(id) {
      var option = {
        width: 400, content: "确定要要解绑吗", confrimFunc: function (jboxObj) {
          var param ={"id":id};
          var obj = tools.requestRs("/wechatOfficial/unBindLiveRoom", param, 'GET');
          if (obj.success) {
            jboxObj.close();
            jbox_notice({content: "解绑成功", autoClose: 2000, className: "success"});
            doQuery();
          } else {
            jbox_notice({content: obj.msg, autoClose: 2000, className: "error"});
          }
        }
      };
      jbox_confirm(option);
    }

    function dataRecharge(aid,liveId) {
      var title = "流量充值";
      var url = "/wechatOfficial/toDataRecharge?id=" + aid+"&&liveId="+liveId;
      bsWindow(url, title, {
        height: 300, width: 700, buttons: []
      });
    }

    function updateManager(aid) {
      var title = "修改负责人";
      var url = "/wechatOfficial/toUpdateManager?id=" + aid;
      bsWindow(url, title, {
        height: 300, width: 700, buttons: []
      });
    }
    //传递的参数
    function queryParams(params) {
      var nickName = $("input[name='nickName']").val();
      var createTimeBegin = $("#createTimeBegin").val();  //起始时间
      var createTimeEnd = $("#createTimeEnd").val();    //结束时间
      if (!params) {
        params = {};
      }
      params["nickName"] = nickName;
      params["createTimeBegin"] = createTimeBegin;
      params["createTimeEnd"] = createTimeEnd;
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
<div class="container" style="width:86%;float: left;">
  <h2>
    审核记录
  </h2>
  <div id="toolbar">
    <form class="form-horizontal" id="form1" name="form1">
      <label>授权方昵称:</label>
      <input type="text" class="form-control" name="nickName" autocomplete="off" placeholder="授权方昵称"
             placeholder="" style="width: 220px;display: inline-block;">

      <label>创建时间:</label>
      <input type="text" class="Wdate form-control" id="createTimeBegin"
             onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'})"
             style="width: 120px;display: inline-block;"/>
      至<input type="text" class="Wdate form-control" id="createTimeEnd"
              onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBegin\')}'})"
              style="width: 120px;display: inline-block;"/>
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
         data-show-pagination-switch="true" style="width: 2400px;"
         class="table table-striped table_list table-bordered">
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
