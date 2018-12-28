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

  <script >
    $(document).ready(function() {
      doInit();
    });
    function doInit(){
      getBankNameList();   //获取开户银行下拉选条件

      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/ordersController/getWithdrawDepositCheckList",
        classes: 'table table-hover',
        height:getHeight(),
        toolbar:"#toolbar",
        pageSize:20,
        queryParams: queryParams,
        columns: [
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
            title: '提现申请',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'llChargePercent',
            title: '平台手续费率',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'llCharge',
            title: '平台手续费',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'realAmount',
            title: '实际到账',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'mobile',
            title: '申请人手机号',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'appName',
            title: '申请人',
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
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            formatter:function(value,row){
                return [
                  '<a href="javascript:void(0)" onclick="audit(' + row.id + ','+row.amount+');" >'+'提现</a>&nbsp;&nbsp;&nbsp;',
                ].join('');
            }
          }
        ]
      });
      var css = { height: "94%"};
      $("#indexiframe" , parent.document).css(css);
    }
    setTimeout(function(){
      css = { height: "93%"};
      $("#indexiframe" , parent.document).css(css);
    },500);
    var flag=true;
    var flagNum=0;
    //传递的参数
    function queryParams(params) {
      if(!flag && flagNum==1){  //  点击查询所有按钮  flagNum=1  记录上一步是在操作  查询所有数据 flag：false  flagNum=1
        params["bankName"] = null;
        params["createTimeBegin"] = null;
        params["createTimeEnd"] = null;
        flagNum==1;
      }else if(flag &&flagNum==1){  //点击 bootstrap刷新按钮  flagNum=1  flag 默认恢复 true
        params["bankName"] = null;
        params["createTimeBegin"] = null;
        params["createTimeEnd"] = null;
        flagNum==0;
      } else {   //点击查询  （条件查询）
        var bankName=$("#bankName option:selected").val();  //开户银行
        var createTimeBeginStr = $("#createTimeBeginStr").val();
        var createTimeEndStr = $("#createTimeEndStr").val();
        if(!params){
          params = {};
        }
        params["bankName"] = bankName;
        params["createTimeBegin"] = createTimeBeginStr;
        params["createTimeEnd"] = createTimeEndStr;
        flagNum=0;
      }
     /* if(flag && flagNum==0){   //根据条件查询
      }else{   //查询所有
      }*/
      flag=true;
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
      return $(window).height()-6 - $('h2').outerHeight(true);
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
      flagNum=0;  //  查询 设置  flagNum==0;
      if(flag){
        $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
      }else{
        $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
      }
    }
    /**  ----查询所有待审核记录   function     start ----  */
    function queryAll(){
      flag=false;
      flagNum=1;
      $('#table-bootstrap').bootstrapTable('refresh');
    }
    /**   ------查询所有   function    end   -----  */
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
    * 审核
     */
    function bankOutCheck(){
      var id =  $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
        return row.id
      });
      if(id.length!=1){
        jbox_notice({content:"请选择一条记录审核",autoClose:2000 ,className:"warning"});
        return false;
      }
      audit(id,0);
    }

    function audit(id,amount){

      var preCash=${meramt };
      if(true){
        var title = "会员提现审核";
        var url = "/ordersController/toAudit?id=" + id;
        bsWindow(url,title,{height:450,width:600,buttons:[
        ]});
      }else{
        //   提现判断  备付金是否支付提现额度   start
        jbox_notice({content:"备付金不足以支付提现金额",autoClose:2000 ,className:"warning"});
        //--------------------------------------end
      }
    }

    function closeWindow(obj){
      if(obj.success){
        window.BSWINDOW.modal("hide");
        jbox_notice({content:"审核完毕",autoClose:2000 ,className:"success"});
       // doQuery();
        flag=false;
        $('#table-bootstrap').bootstrapTable('refresh');
      }else{
        window.BSWINDOW.modal("hide");
        jbox_notice({content:"系统错误",autoClose:2000 ,className:"warning"});
        doQuery();
      }

    }

    //导出
    function exportExcel(){
      var bankName=$("#bankName option:selected").val();  //开户银行
      var createTimeBegin = $("#createTimeBeginStr").val();
      var createTimeEnd = $("#createTimeEndStr").val();
      window.location.href="/ordersController/exportExcelBankOutCheck?createTimeBegin="+createTimeBegin+"&createTimeEnd="+createTimeEnd+"&bankName="+bankName;
    }

  </script>
</head>
<body onload="doInit();">
<div class="container" style="width: 86%;float: left;">
  <h2>
    提现审核 <font color="red" size="3">备付金余额: ${meramt }元</font>
    </form>
  </h2>
  <div id="toolbar">
    <form class="form-horizontal" id="form1" name="form1">
      <label>开户银行:</label>
      <select id="bankName" name="bankName">
        <option selected value="">全部</option>
      </select>

      <label>申请时间：</label>
      <input type="text" class="Wdate form-control" id="createTimeBeginStr"
             onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
             style="width: 120px;display: inline-block;"/>
      至<input type="text" class="Wdate form-control" id="createTimeEndStr"
              onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
              style="width: 120px;display: inline-block;"/>

      <!--     查询所有  html   start       -->
      <button type="button" class="btn btn-info"  onclick="queryAll();">
        <i class=" glyphicon glyphicon-search"></i> 查询所有
      </button>
      <!--     查询所有  html   end       -->

      <button type="button" class="btn btn-info"  onclick="doQuery(true);">
        <i class=" glyphicon glyphicon-search"></i> 查询
      </button>
      <button type="button" class="btn btn-success"  onclick="exportExcel();">
        <i class=" glyphicon glyphicon-export"></i> 导出
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
         data-show-pagination-switch="true"
         class="table table-striped table_list table-bordered">
  </table>
</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
<script src="${ctx}/web/res/my97/calendar.js"></script>
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<script src="${ctx}/web/res/my97/xdate.dev.js"></script>
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
