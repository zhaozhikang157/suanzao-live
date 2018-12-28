<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap2').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveChannel/getPoolUsingList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams2,
                columns: [
                    {
                        field: 'code',
                        title: '通道编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'pushAddr',
                        title: '通道推送地址',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr1',
                        title: '通道直播地址rtmp',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr3',
                        title: '通道直播地址m3u8',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr3',
                        title: '通道直播地址m3u8',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'courseId',
                        title: '课程Id',
                        align: 'center',
                        valign: 'middle'
                    }]
            });

            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveChannel/getPoolCanUseList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'code',
                        title: '通道编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'pushAddr',
                        title: '通道推送地址',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr1',
                        title: '通道直播地址rtmp',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr3',
                        title: '通道直播地址m3u8',
                        align: 'center',
                        valign: 'middle'
                    }]
            });
        }

        function getHeight() {
            return (($(window).height() - 6 - $('h2').outerHeight(true)) / 2) - 50;
        }
        //传递的参数
        function queryParams(params) {
            var code = $("#code").val().trim();    //类型
            if (!params) {
                params = {};
            }

            params["code"] = code;
            return params;
        }
        /**
         *点击查询
         */
        function doQuery2() {
            $('#table-bootstrap2').bootstrapTable('refresh', {query: queryParams2});
        }
        //传递的参数
        function queryParams2(params) {
            var code = $("#code2").val().trim();    //类型
            if (!params) {
                params = {};
            }

            params["code"] = code;
            return params;
        }
        /**
         *点击查询
         */
        function doQuery() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }
    </script>
</head>
<body onload="doInit()">
<div  style="margin-top:10px; ">
    <h2> 正在用的直播通道 </h2>
    <div id="toolbar2">
        <form class="form-horizontal" id="form2" name="form2">

            <label class="control-label">通道编号:</label>
            <input type="text" class="form-control" name="code2" id="code2" autocomplete="off" placeholder="通道编号"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery2(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>
    <table id="table-bootstrap2"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true">
    </table>
</div>
    <div  style="margin-top:10px; ">
    <h2> 可用的直播通道 </h2>
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">

            <label class="control-label">通道编号:</label>
            <input type="text" class="form-control" name="code" id="code" autocomplete="off" placeholder="通道编号"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true">
    </table>
</div>
</body>
</html>

