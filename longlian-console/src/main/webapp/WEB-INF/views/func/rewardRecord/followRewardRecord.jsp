<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>关注奖明细表</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/rewardRecord/getFollowRewardRecordList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                onLoadSuccess:getCountAccount,
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'createTime',
                        title: '交易日期',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'name',
                        title: '讲师户名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'appId',
                        title: '讲师ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobile',
                        title: '讲师手机号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'remark',
                        title: '关注量',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'amount',
                        title: '关注奖',
                        align: 'center',
                        valign: 'middle'
                    }

                ]
            });
        }

        function getCountAccount(){
            var str = queryParams();
            $.ajax({
                type: "POST",
                url: "/rewardRecord/getFollowRewardRecordAccount",
                data: str,
                async: false,
                success: function (data) {
                    $("#amont").text(data.data);
                }
            })
        }
        //传递的参数
        function queryParams(params) {
            var appId = $("#appId").val().trim();
            var name = $("#name").val().trim();
            var startTime = $("#startTime").val().trim();
            if (startTime != "") {
                startTime += " 00:00:00"
            }
            var endTime = $("#endTime").val().trim();
            if (endTime != "") {
                endTime += " 23:59:59"
            }
            var startAmount = $("#startAmount").val().trim();
            var endAmount = $("#endAmount").val().trim();
            if (!params) {
                params = {};
            }

            params["appId"] = appId;
            params["name"] = name;
            params["startTime"] = startTime;
            params["endTime"] = endTime;
            params["startAmount"] = startAmount;
            params["endAmount"] = endAmount;
            return params;
        }

        function exportExcel() {
            var appId = $("#appId").val().trim();
            var name = $("#name").val().trim();
            var startTime = $("#startTime").val().trim();
            if (startTime != "") {
                startTime += " 00:00:00"
            }
            var endTime = $("#endTime").val().trim();
            if (endTime != "") {
                endTime += " 23:59:59"
            }
            var startAmount = $("#startAmount").val().trim();
            var endAmount = $("#endAmount").val().trim();
            window.location.href="/rewardRecord/exportExcelFollowRewardRecord?appId="+appId+"&name="+name+"&startTime="+startTime+"&endTime="+endTime+"&startAmount="+startAmount+"&endAmount="+endAmount;

        }
        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2> 关注奖明细 </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label class="control-label">用户ID:</label>
            <input type="text" class="form-control" name="appId" id="appId" autocomplete="off"
                   placeholder="用户ID"
                   placeholder="" style="width: 70px;display: inline-block;">

            <label class="control-label">用户名:</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off"
                   placeholder="用户名"
                   placeholder="" style="width: 150px;display: inline-block;">
            <label class="control-label">金额:</label>
            <input type="text" class="form-control" name="startAmount" id="startAmount" autocomplete="off"
                   placeholder="金额"
                   placeholder="" style="width: 60px;display: inline-block;">
            至
            <input type="text" class="form-control" name="endAmount" id="endAmount" autocomplete="off" placeholder="金额"
                   placeholder="" style="width: 60px;display: inline-block;">

            <label>申请时间：</label>
            <input type="text" class="Wdate form-control" id="startTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                    style="width: 120px;display: inline-block;"/>

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-success" onclick="exportExcel();">
                <i class=" glyphicon glyphicon-export"></i> 导出
            </button>
            <label class="control-label">奖励总金额：<span id="amont"></span>元</label>
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
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first=new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth()+1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#startTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
