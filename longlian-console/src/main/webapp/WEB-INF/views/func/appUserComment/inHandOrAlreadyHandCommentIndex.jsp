<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>bootstrap table</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
  <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
  <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
  <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
  <script src="${ctx}/web/res/my97/xdate.dev.js"></script>  <script >
    var handStatus=${handStatus};
    function doInit(){
      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/appUserCommentController/getInHandOrAlreadyHandCommentList?handStatus="+handStatus,
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
            title: '处理时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          {
            field: 'appName',
            title: '姓名',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'mobile',
            title: '手机号',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'remarks',
            title: '反馈内容',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'handRemarks',
            title: '处理内容',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'handUserId',
            title: '处理人ID',
            align: 'center',
            valign: 'middle'
          }
          ,
          {
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            formatter:function(value,row){
              if(row.handStatus=="2"){   //已处理状态，没有处理选项
                return [
                '<a href="javascript:void(0)" onclick="showComment(' + row.id + ');">'+'查看处理意见</a>&nbsp;&nbsp;&nbsp;',
                ].join('');
              } else{
                return [
                  '<a href="javascript:void(0)" onclick="setHandComment(' + row.id + ','+row.handStatus+');">'+'处理</a>&nbsp;&nbsp;&nbsp;',
                ].join('');
              }
            }
          }]
      });
    }
    //传递的参数
    function queryParams(params) {
      var beginTime = $("#beginTime").val();  //起始时间
      var endnTime = $("#endnTime").val();    //结束时间
      if(!params){
        params = {};
      }
      params["beginTime"] = beginTime;
      params["endnTime"] = endnTime;
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
      if(flag){
        $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
      }else{
        $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
      }
    }
    /**
     * 创建或者更新员工，弹出框
     */
    function setHandComment(id,handStatus){
      var title = "反馈处理";
      var url = "/appUserCommentController/hand?id=" + id+"&handStatus="+handStatus;
      bsWindow(url, title, {
        height: 500, width: 600, submit: function (v, h) {
          var cw = h[0].contentWindow;
          cw.doSave(function (json) {
            if (json) {
              window.BSWINDOW.modal("hide");
              jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
              doQuery();
            } else {
              jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
            }
          });
        }
      });
    }

    /**
    *查看处理意见
     */
    function showComment(id){
      var title = "查看审核意见";
      var url = "/appUserCommentController/toShowComment?id=" + id;
      bsWindow(url,title,{height:300,width:500,buttons:[
        {classStyle:"btn btn-primary" ,name:"关闭",clickFun:function(name ,bs){
          window.BSWINDOW.modal("hide");
        }}
      ]});
    }

  </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
  <h2>
    意见反馈
  </h2>
  <div id="toolbar">
    <form class="" id="form1" name="form1">
      <label>按时间查询：</label>
      <input  type="text" class="Wdate form-control" id="beginTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endnTime\')}'})" style="width: 150px;display: inline-block;"/>
      至<input  type="text" class="Wdate form-control" id="endnTime"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})" style="width: 150px;display: inline-block;"/>
      <button type="button" class="btn btn-info"  onclick="doQuery(true);">
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
         data-show-pagination-switch="true" >
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
  $("#beginTime").val(first);  //获取当月第一天
  $("#endnTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
