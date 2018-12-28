<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/calendar.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveRoom/getAllUseFlowPage?roomId="+${roomId},
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'time',
                        title: '时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'courseName',
                        title: '课程名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 's',
                        title: '流量',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (row.status == 1) {
                                return [
                                    '<a class="like" href="javascript:void(0)" title="打印" onclick="print(' + row.appId + ');">打印</a>  '
                                ].join('');
                            }
                        }
                    }]
            });
        }


        //传递的参数
        function queryParams(params) {
            var beginTime = $("#createTimeBeginStr").val();
            if(beginTime != null || beginTime != "" || beginTime != undefined){
                beginTime = beginTime + " 00:00:00"
            }
            var endTime = $("#createTimeEndStr").val();
            if(endTime != null || endTime != "" || endTime != undefined){
                endTime = endTime + " 23:59:59"
            }
            var coureName = $("#name").val();
            if (!params) {
                params = {};
            }
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
            params["courseName"] = coureName;
            return params;
        }
        function refresh() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
        }
        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });
        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }
        function detailFormatter(index, row) {
            var html = [];
            $.each(row, function (key, value) {
                html.push('<p><b>' + key + ':</b> ' + value + '</p>');
            });
            return html.join('');
        }
        //打印
        function print(id) {
            var title = "打印";
            var url = "/liveRoom/print?id=" + id;
            bsWindow(url,title,{height:550,width:600,buttons:[
                {classStyle:"btn btn-default" ,name:"关闭",clickFun:function(name ,bs){
                    window.BSWINDOW.modal("hide");
                }}
            ]});
        }
        
        
        /**
         *点击查询
         */
        function doQuery(flag) {
            if (flag) {
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
            } else {
                $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
            }
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        直播间流量消耗记录
    </h2>

    <div id="toolbar">
        <form class="" id="form1" name="form1">
            <label>课程名称:</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <label>交易时间：</label>
            <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
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
           data-show-pagination-switch="true"
            >
    </table>
</div>
</body>
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#createTimeBeginStr").val(first)  //获取当月第一天
    $("#createTimeEndStr").val(lastDay);//默认时间为 当月 最后一天
</script>
</script>
</html>
