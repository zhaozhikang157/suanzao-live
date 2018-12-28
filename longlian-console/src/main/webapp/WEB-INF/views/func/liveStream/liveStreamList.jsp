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
    function doInit() {
      $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/liveStream/getListData",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
          {
            field: 'courseId',
            title: '直播流key/课程ID',
            align: 'center',
            width:'120',
            valign: 'middle'
          },
          {
            field: 'liveTopic',
            title: '直播主题',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'remark',
            title: '课程介绍',
            align: 'center',
            valign: 'middle'
          },
          {
            field: 'startTime',
            title: '开课时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },
          /*{
            field: 'endTime',
            title: '结束时间',
            align: 'center',
            valign: 'middle',
            formatter: function (value, row) {
              return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
            }
          },*/
          {
            field: '_opt_',
            title: '操作',
            align: 'center',
            valign: 'middle',
            width:'100',
            formatter: function (value, row) {
              return [
                '<a href="javascript:void(0)" onclick="disableStream(' + row.courseId + ');" >' + '禁播</a>&nbsp;&nbsp;&nbsp;'
              ].join('');
            }
          }
        ]
      });
      function detailFormatter(index, row) {
        var html = [];
        $.each(row, function (key, value) {
          html.push('<p><b>' + key + ':</b> ' + value + '</p>');
        });
        return html.join('');
      }
    }
  </script>
  <script>
    $(window).resize(function () {
      $('#table-bootstrap').bootstrapTable('resetView', {
        height: getHeight()
      });
    });
    function getHeight() {
      return $(window).height() - 6 - $('h2').outerHeight(true);
    }
    /**
     * 禁播流  function
     *
     * */
    function disableStream(aid) {
      var url = "/liveStream/disableStream?courseId=" + aid;
      $.ajax({
        type: "GET",
        url: url,
        success: function (data) {
          if (data.success) {
            jbox_notice({content: "禁播流成功", autoClose: 2000, className: "success"});
            $('#table-bootstrap').bootstrapTable('refresh');
          } else {
            jbox_notice({content: "禁播流失败", autoClose: 2000, className: "error"});
          }
        }
      })
    }
    /*function closeWindows(obj) {
      if (obj.success) {
        window.BSWINDOW.modal("hide");
        jbox_notice({content: "审核完毕", autoClose: 2000, className: "success"});
        doQuery();
      } else {
        window.BSWINDOW.modal("hide");
        jbox_notice({content: "系统错误", autoClose: 2000, className: "warning"});
        doQuery();
      }

    }*/
    /**
     *点击查询
     */
   /* function doQuery(flag) {
      if (flag) {
        $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
      } else {
        $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
      }
    }*/
  </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">

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
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
<script src="${ctx}/web/res/my97/calendar.js"></script>
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<script src="${ctx}/web/res/my97/xdate.dev.js"></script>
   <!--js 自定义   function -->
<script src="${requestContext.contextPath}/web/res/js/func/liveStream/liveStreamList.js"></script>
</body>
</html>
