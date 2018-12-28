<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <%@include file="/WEB-INF/views/func/include/header.jsp" %>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>bootstrap table</title>
  <link rel="stylesheet" href="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.css">
  <link rel="stylesheet" href="${ctx}/web/res/css/style.css">
  <link rel="stylesheet" href="${ctx}/web/res/pc/my97/skin/WdatePicker.css"/>
  <script src="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.js"></script>
  <script src="${ctx}/web/res/pc/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
  <script src="${ctx}/web/res/pc/my97/WdatePicker.js"></script>
  <script src="${ctx}/web/res/pc/my97/xdate.dev.js"></script>
  <script src="${ctx}/web/res/angular/angular.min.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.min.js"></script>
  <script>
    function doInit() {
//      getCourseTypes();
      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/pcCourse/getList.user",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
          {
            field: 'id',
            title: 'id',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'coverssAddress',
            title: '封面图',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return   "<img src=" +value + " style='width:196px;height:120px;'></img>"
            }
          },{
            field: 'liveTopic',
            title: '课程名称',
            align: 'center',
            valign: 'middle'
          }, {
            field: 'startTime',
            title: '开课时间',
            align: 'center',
            valign: 'middle',
             formatter: function (value, row) {
               if(row.isSeriesCourse==1){
                 return ''
               }else{
                 return row.startTime
               }
             }
          },{
            field: 'chargeAmt',
            title: '价格',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
                if (value == 0 || value == "0" || value == '0.00') {
                    return "免费";
                } else{
                    return value;
                }
            }
          },
          {
            field: 'courseType',
            title: '课程类型',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'courseTyperName',
            title: '课程分类',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'seriesLiveTopic',
            title: '所属系列课',
            align: 'center',
            valign: 'middle'
          },{
            field: 'isVerticalScreen',
            title: '直播方式',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span>横屏</span>"
              } else if (value == '1') {
                return "<span>竖屏</span>"
              }else{
                return "<span></span>"
              }
            }
          },{
            field: 'isRelay',
            title: '是否转播',
            align: 'center',
            valign: 'middle',
            formatter:function(value,row){
              if (value == '0') {
                return "<span>否</span>"
              } else if (value == '1') {
                return "<span>是</span>"
              }else{
                return "<span></span>"
              }
            }
          },
          {
            field: 'time',
            title: '直播状态',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (row.isSeriesCourse == 1) {
                return "<span></span>"
              } else {
                if (row.auditStatus != 0 && row.auditStatus != -1) {
                  if (value == '0') {
                    return "<span>未开播</span>"
                  } else if (value == '1') {
                    return "<span>正在直播</span>"
                  } else if (value == '2') {
                    return "<span>已结束</span>"
                  } else if (value == '3') {
                    return "<span>已下架</span>"
                  } else {
                    return "<span></span>"
                  }
                } else {
                  return "<span></span>"
                }
              }
            }
          },{
            field: 'auditStatus',
            title: '审核状态',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              if (value == '0') {
                return "<span>审核中</span>"
              } else if (value == '1') {
                return "<span>已通过</span>"
              }else{
                return '<span><a data-toggle="tooltip" data-placement="top" title="'+row.remark+'">未通过?</a></span>'
              }
            }
          },
          {
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              var arra = new Array();
              arra.push('<button style="margin-bottom:4px" class="btn btn-primary" onclick="edit(' + row.id + ');">' + '编辑</button>');
                arra.push('<select style="margin-top:10px;" class="form-control downClass" onchange="deal(' + row.id + ','+row.isJoinStatus+')" id="deal'+''+String(row.id)+'">')
               arra.push('<option value="0" >更多操作</option>')
              if (row.auditStatus != 0 &&  row.auditStatus!=-1) {
                if (row.courseStatus == 0) {
                  arra.push('<option value="1" >下架</option>')
                } else {
                  arra.push('<option value="2" >上架</option>')
                }
                  arra.push('<option value="5" >设置分享时长</option>')
              }
              if (row.isSeriesCourse != 1 && row.auditStatus!=0 &&  row.auditStatus!=-1 && row.time != '2') {
                arra.push('<option value="3" >结束</option>')
              }
                arra.push('<option value="4" >删除</option>')

              return arra.join('');

            }
          }]
      });
    }
    Date.prototype.format = function (format) {
      var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
      };
      if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
      }
      for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
          format = format.replace(RegExp.$1, RegExp.$1.length == 1
                  ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
      }
      return format;
    };
    function deal(id,isJoinStatus) {
      var deal = $("#deal"+id).val();
      $('#deal'+id).prop('selectedIndex',0);
      if (deal == "1") {
        if(isJoinStatus=="1")  {
          alert("该课程已经被购买无法下架！");
        }else{
          if(confirm("确定要下架该课程吗？")) {
              updateDown(id);
          }
        }
      } else if (deal == "2") {
        if(confirm("确定要上架该课程吗？")) {
          updateUp(id);
        }
      } else if (deal == "3"){
        var result = ZAjaxJsonRes({url:"/pcCourse/getCourseStatus?courseid="+id, type: "GET"});
        if(result.data=="0"){
          alert("该课程已经结束！");
          return;
        }else if(result.data=="1"){
          return;
        }
        if(confirm("确定要结束该课程吗？")) {
          endLive(id);
        }
      } else if (deal == "4"){
        if(isJoinStatus=="1")  {
          alert("该课程已经被购买无法删除！");
        }else{
          if(confirm("确定要删除该课程吗？"))
          {
            del(id);
          }
        }
      }
      else if(deal == "5"){
          setShareTime(id);
      }
    }
    function closeWindows(obj) {
        if (obj.success) {
            window.BSWINDOW.modal("hide");
            jbox_notice({content: "设置完毕", autoClose: 2000, className: "success"});
            doQuery();
        } else {
            window.BSWINDOW.modal("hide");
            jbox_notice({content: "系统错误", autoClose: 2000, className: "warning"});
            doQuery();
        }

    }
    function setShareTime(id) {
        var title = "设置分享时长";
        var url = "/pcCourse/toSetShareTime.user?id=" + id;
        bsWindow(url, title, {
            height: 300, width: 700, buttons: []
        });
    }
    
    function edit(id) {
      var title = "课程编辑";
      var url = "/pcCourse/toEditCourse?id=" + id;
      window.location.href = url;
    }
    
/*    function toCreateSingleCourse() {
      var title = "创建单节课";
      var url = "/pcCourse/toCreateSingleCourse.user";
      bsWindow(url, title, {
        height: 1300, width: 1000, submit: function (v, h) {
          var cw = h[0].contentWindow;
          cw.doSave(function (json) {
            if (json.code=="000000") {
              window.BSWINDOW.modal("hide");
              jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
              doQuery();
            } else {
              jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
            }
          });
        }
      });
    }*/
  /*  function toCreateSeriesCourse() {
      var title = "创建系列课";
      var url = "/pcCourse/toCreateSeriesCourse.user";
      bsWindow(url, title, {
        height: 1300, width: 1000, submit: function (v, h) {
          var cw = h[0].contentWindow;
          cw.doSave(function (json) {
            if (json.code=="000000") {
              window.BSWINDOW.modal("hide");
              jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
              doQuery();
            } else {
              jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
            }
          });
        }
      });
    }*/
    /**
     *点击查询
     */
    function doQuery() {
      $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
    }
    function getHeight() {
      return $(window).height() - 6 - $('h2').outerHeight(true);
    }
    function refresh() {
      $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
    }
    $(window).resize(function () {
      $('#table-bootstrap').bootstrapTable('resetView', {
        height: getHeight()
      });
    });
    //传递的参数
    function queryParams(params) {
      var liveTopic = $("input[name='liveTopic']").val().trim();
      var beginTime = $("#beginTime").val();  //起始时间
//      var endTime = $("#endTime").val();    //结束时间
      var endTime = $("#endTime").val();    //结束时间
//      if (beginTime != "")beginTime = beginTime + " 00:00:00";
//      if (endTime != "")endTime = endTime + " 23:59:59";
      var status = $("select[name='status'] option:selected").val();    //状态
      var liveWay = $("select[name='liveWay'] option:selected").val();    //直播类型
      var auditStatus = $("select[name='auditStatus'] option:selected").val();    //状态
      var isSeriesCourse = $("select[name='isSeriesCourse'] option:selected").val();    //状态
      var chargeAmt = $("select[name='chargeAmt'] option:selected").val();
      var isVerticalScreen = $("select[name='isVerticalScreen'] option:selected").val();
      var zhiboStatus = $("select[name='zhiboStatus'] option:selected").val();

      if (!params) { 
        params = {};
      } 
      params["liveTopic"] = liveTopic;
      params["beginTime"] = beginTime;
//      params["endTime"] = endTime;
      params["status"] = status;
      params["liveWay"] = liveWay;
      params["auditStatus"] = auditStatus;
      params["isSeriesCourse"] = isSeriesCourse;
      params["chargeAmt"] = chargeAmt;
      params["isVerticalScreen"] = isVerticalScreen;
      params["zhiboStatus"] = zhiboStatus;
      return params;
    }
    
    function updateUp(id) {
      $.ajax({
        type: "GET",
        url: "/pcCourse/updateUp.user?id=" + id,
        success: function (data) {
          if (data.success) {
            jbox_notice({content: "上线成功", autoClose: 2000, className: "success"});
            window.location.reload();
          } else {
            jbox_notice({content: "上线失败", autoClose: 2000, className: "error"});
          }
        }
      })
    }
    function del(id) {
      $.ajax({
        type: "GET",
        url: "/pcCourse/del.user?id=" + id,
        success: function (data) {
          if (data.success) {
            jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
            window.location.reload();
          } else {
            jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
          }
        }
      })
    }
    function updateDown(id) {
      $.ajax({
        type: "GET",
        url: "/course/setCourseDown.user?id=" + id,
        success: function (data) {
          if (data.code=="000000") {
            jbox_notice({content: "下线成功", autoClose: 2000, className: "success"});
            window.location.reload();
          } else {
            jbox_notice({content: "下架失败", autoClose: 2000, className: "error"});
          }
        }
      })
    }
    function endLive(id) {
      $.ajax({
        type: "GET",
        url: "/teacher/live/endLive.user?courseId=" + id,
        success: function (data) {
          if (data.code=="000000") {
            jbox_notice({content: "该课程已结束", autoClose: 2000, className: "success"});
            window.location.reload();
          } else {
            jbox_notice({content: "结束失败", autoClose: 2000, className: "error"});
          }
        }
      })
    }
  </script>
  <style>
    .downClass{
      width: 110px;
      margin-left: 10px;
      display: inline-block;
    }
    #toolbar span{
      display: inline-block;
      margin-bottom: 15px;
    }
  </style>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:30px">
  <div id="toolbar">
    <form class="form-horizontal" id="form1" name="form1">
      <span>
        <label>课程名称:</label>
        <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
               placeholder="" style="width: 220px;display: inline-block; margin-right: 20px;"  onblur="doQuery(true)">
        <label>开课时间:</label>
        <input type="text" class="Wdate form-control" id="beginTime"
               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312'})"
               style="width: 120px;display: inline-block;margin-right: 20px;"   onfocus="doQuery(true)"/>
        <%--  至<input type="text" class="Wdate form-control" id="endTime"
                  onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                  style="width: 120px;display: inline-block;"/>--%>
      </span>
      <span>
        <label>课程类型:</label>
        <select class="form-control" id="isSeriesCourse" name="isSeriesCourse"
                style="display: inline-block;width: auto;margin-right: 20px;"  onchange="doQuery(true)">
          <option value="">-请选择-</option>
          <option value="0">单节课</option>
          <option value="1">系列课</option>
          <option value="2">系列课单节课</option>
        </select>
      </span>
      <span>
        <label>付费类型:</label>
        <select class="form-control" id="chargeAmt" name="chargeAmt"
                style="display: inline-block;width: auto; margin-right: 20px;"  onchange="doQuery(true)">
          <option value="">-请选择-</option>
          <option value="0">收费</option>
          <option value="1">免费</option>
        </select>
      </span>
      <span>
        <label>直播方式:</label>
        <select class="form-control" id="isVerticalScreen" name="isVerticalScreen"
                style="display: inline-block;width: auto; margin-right: 20px;"  onchange="doQuery(true)">
          <option value="">-请选择-</option>
          <option value="0">横屏</option>
          <option value="1">竖屏</option>
        </select>
      </span>

      <span>
        <label>直播状态:</label>
        <select class="form-control" id="zhiboStatus" name="zhiboStatus"
                style="display: inline-block;width: auto; margin-right: 20px;"  onchange="doQuery(true)">
          <option value="">-请选择-</option>
          <option value="0">未开播</option>
          <option value="1">正在直播</option>
          <option value="2">已结束</option>
          <option value="3">已下架</option>
        </select>
      </span>

      <span>
        <label>审核状态:</label>
        <select class="form-control" id="auditStatus" name="auditStatus"
                style="display: inline-block;width: auto; margin-right: 20px;" onchange="doQuery(true)">
          <option value="">-请选择-</option>
          <option value="1">已通过</option>
          <option value="0">正在审核</option>
          <option value="-1">未通过</option>
        </select>
      <button type="button" class="btn btn-info" onclick="doQuery(true);"style=" margin-right: 20px;float:right">
        <i class=" glyphicon glyphicon-search"></i> 查询
      </button>
      </span>
    </form>

    <div>
      <div class="btn-group"style="float: left">
        <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          创建课程<i class="caret"></i>
        </button>
        <ul class="dropdown-menu">
          <li><a href="javascript:void(0)" onclick="toCreateSingleCourse()">创建单节课</a></li>
          <li><a href="javascript:void(0)" onclick="toCreateSeriesCourse()">创建系列课</a></li>
        </ul>
      </div>
    </div>
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
  var result = ZAjaxJsonRes({url: "/liveRoom/isForbiddenRoom.user", type: "GET"});
  function toCreateSingleCourse(){
    if( result.code=="100057"){
      alert(result.message);
      return;
    }
    window.location.href = "/pcCourse/toCreateSingleCourse.user";
  }
  function toCreateSeriesCourse(){
    if( result.code=="100057"){
      alert(result.message);
      return;
    }
    window.location.href = "/pcCourse/toCreateSeriesCourse.user";
  }
  $(function () {
    setTimeout(function(){
      $("[data-toggle='tooltip']").tooltip();
    },300);
  });
  var firstDate = new Date();
  firstDate.setDate(1);
  var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
  var endDate = new Date(firstDate);
  endDate.setMonth(firstDate.getMonth() + 1);
  endDate.setDate(0);
  var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
//  $("#beginTime").val(first)  //获取当月第一天
//  $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
