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
      getBankNameList();   //获取开户银行下拉选条件

      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/ordersController/getCheckRecordList?auditStatus=1",   //auditStatus：审核状态 0-待审核 1-通过 2-不通过
        classes: 'table table-hover',
        height:getHeight(),
        toolbar:"#toolbar",
        pageSize:20,
        queryParams: queryParams,
        columns: [
          {
            field: 'state',
            checkbox: true,
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'createTime',
            title: '申请日期',
            align: 'center',
            valign: 'middle',
            sortable:true,
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'amount',
            title: '提现金额',
            align: 'center',
            valign: 'middle'
          },{
            field: 'orderNo',
            title: '订单编号',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'appName',
            title: '申请人',
            align: 'center',
            valign: 'middle'
          },{
            field: 'appMobile',
            title: '用户手机',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'bankCaidOpenName',
            title: '开户人',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'bankName',
            title: '开户银行',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'bankCardNo',
            title: '收款银行卡号',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'optStatus',
            title: '操作状态',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if(value=="1"){
                return "<span>成功</span>";
              }else if(value=="2"){
                return "<span>失败</span>";
              }else if(value=="0"){
                return "<span>进行中</span>";
              }else if(value=="3"){
                return "<span>已返钱</span>";
              }
            }
          },
          {
            field: 'successTime',
            title: '成功时间',
            align: 'center',
            valign: 'middle',
            sortable:true,
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'auditorId',
            title: ' 审核人ID',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'auditTime',
            title: '审核日期',
            align: 'center',
            valign: 'middle',
            sortable:true,
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'auditAgreed',
            title: '描述',
            align: 'center',
            valign: 'middle'
          },
          {
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            formatter:function(value,row){
              if(row.optStatus!="3"){
                return [
                  '<a href="javascript:void(0)" onclick="rollback(' + row.id + ');" style="display:none;">'+'返钱</a>&nbsp;&nbsp;&nbsp;',
                ].join('');
              }
            }
          }
        ]
      });
      var css = { height: "94%"};
      $("#indexiframe" , parent.parent.document).css(css);
    }
    setTimeout(function(){
      css = { height: "93%"};
      $("#indexiframe" , parent.parent.document).css(css);
    },500);
    //传递的参数
    function queryParams(params) {
      var orderNo=$("input[name='orderNo']").val();//平台流水编号
      var bankName=$("#bankName option:selected").val();  //开户银行
      var createTimeBeginStr = $("#createTimeBeginStr").val();
      if(createTimeBeginStr!=""){
        createTimeBeginStr+=" 00:00:00";
      }
      var createTimeEndStr = $("#createTimeEndStr").val();
      if(createTimeEndStr!=""){
        createTimeEndStr+=" 23:59:59";
      }
      var appMobile = $("input[name='appMobile']").val();//用户手机
      var optStatus=$("#optStatus option:selected").val();  //审核状态
      if(!params){
        params = {};
      }
      params["orderNo"] = orderNo;
      params["bankName"] = bankName;
      params["createTimeBegin"] = createTimeBeginStr;
      params["createTimeEnd"] = createTimeEndStr;
      params["appMobile"] = appMobile;
      params["optStatus"] = optStatus;
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

    /**
     *获取开户银行下拉选
     */
    function getBankNameList(){
      $.ajax({
        type: "GET",
        url: "/bankController/getBankNameList",
        success: function (data) {
          if(data.success){
            var bankNameList=data.data;
            for(var i=0;i<bankNameList.length;i++){
              $("#bankName").append("<option value=" + bankNameList[i].name + ">" + bankNameList[i].name + "</option>");
            }
          }
        }
      })
    }

    /**
    *返钱
* @param id
     */
    function rollback(id){
      var title = "返钱"
      var url = "/ordersController/toRollback?id=" + id;
      bsWindow(url,title,{height:250,width:600,buttons:[
//        {classStyle:"btn btn-primary" ,name:"关闭",clickFun:function(name ,bs){
//          window.BSWINDOW.modal("hide");
//        }}
      ]});
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


    //导出
    function exportExcel(){
      var bankName=$("#bankName option:selected").val();  //开户银行
      var createTimeBegin = $("#createTimeBeginStr").val();
      var createTimeEnd = $("#createTimeEndStr").val();
      var orderNo=$("input[name='orderNo']").val();//平台流水编号
      var appMobile = $("input[name='appMobile']").val();//用户手机
      var optStatus=$("#optStatus option:selected").val();  //审核状态
      var optStatusSrt=$("#optStatus option:selected").text();  //审核状态
      window.location.href="/ordersController/exportExcelBankOutCheckRecord?auditStatus=1&createTimeBegin="+createTimeBegin+"&createTimeEnd="+createTimeEnd+"&bankName="+bankName+"&orderNo="+orderNo+"&appMobile="+appMobile+"&optStatus="+optStatus+"&optStatusSrt="+optStatusSrt;
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
      <label>订单编号:</label>
      <input type="text" class="form-control" name="orderNo" autocomplete="off" placeholder="订单编号"
             placeholder="" style="width: 160px;display: inline-block;">
      <label>开户银行:</label>
      <select id="bankName" class="form-control" style="display: inline-block;width: 120px;" name="bankName">
        <option selected value="">全部</option>
      </select>

      <label>申请时间：</label>
      <input type="text" class="Wdate form-control" id="createTimeBeginStr"
             onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
             style="width: 120px;display: inline-block;"/>
      至<input type="text" class="Wdate form-control" id="createTimeEndStr"
              onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
              style="width: 120px;display: inline-block;"/>

      <label>用户手机:</label>
      <input type="text" class="form-control" name="appMobile" autocomplete="off" placeholder="用户手机"
             placeholder="" style="width: 160px;display: inline-block;">
      <label>操作状态:</label>
      <select id="optStatus" style="display: inline-block;width: 100px;" class="form-control" name="optStatus">
        <option selected value="">全部</option>
        <option value="1">成功</option>
        <option value="2">失败</option>
      </select>
      <button type="button" class="btn btn-info"  onclick="doQuery(true);">
        <i class=" glyphicon glyphicon-search"></i> 查询
      </button>
      <button type="button" class="btn btn-info"  onclick="exportExcel();">
        <i class=" glyphicon glyphicon-search"></i> 导出
      </button>
      <input type="reset"class="btn btn-warning"  value="重置"/>
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
