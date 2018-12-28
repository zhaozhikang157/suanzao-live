<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>收支详情</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/common/btable.css">
    <script>
        $(document).ready(function() {
            doInit();
        });
    </script>
    <script>
        var startTime = '${startTime}';
        var endTime = '${endTime}';
        var appId = '${appId}';
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/accountTrack/getDetailedPage?startTime="+startTime+"&endTime="+endTime+"&appId="+appId,
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                columns: [
                    {
                        field: 'createTime',
                        title: '交易日期',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                        }
                    },
                    {
                        field: 'orderNo',
                        title: '交易订单号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userName',
                        title: '户名',
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
                        field: 'remark',
                        title: '收入类型',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'income',
                        title: '收入金额',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'expenditure',
                        title: '提现金额',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'currBalance',
                        title: '余额',
                        align: 'center',
                        valign: 'middle'
                    }
                ]
            });
        }

        function getHeight() {
            return $(window).height() - $('#H1').outerHeight(true) - 20;
        }

        function importExcel(){
            window.location.href="/accountTrack/importDetails?startTime="+startTime+"&endTime="+endTime+"&appId="+appId;
        }

    </script>
</head>

<body onload="doInit();">
<div class="container" style="width:97%;">
    <div id="H1">
        <h2 style="margin-top: 15px;">
            收支详情
        </h2>
        <div class="form-group" style="margin-top:10px;padding: 0;">
           <%-- <a class="btn btn-info glyphicon glyphicon-share-alt" href="javascript:history.back(-1);">返回</a>--%>
            <button type="button" class="btn btn-success" onclick="importExcel();">
                <i class=" glyphicon glyphicon-export"></i> 导出
            </button>
        </div>
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
