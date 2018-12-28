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
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        var sumIncome = 0;
        var sumExpenditure = 0;
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'get',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/accountTrack/getReDetailPage",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
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

        //传递的参数
        function queryParams(params) {
            var toAccountId = $("input[name='appId']").val();
            var mobile = $("input[name='mobile']").val();
            var startTime = $("#startTime").val();
            var endTime = $("#endTime").val();
            var returnMoneyLevel = $("#returnMoneyLevel").find("option:selected").val();
            var isShow = $("#isShow").find("option:selected").val();
            if (!params) {
                params = {};
            }
            params["toAccountId"] = toAccountId;
            params["mobile"] = mobile;
            params["startTime"] = startTime;
            params["endTime"] = endTime;
            params["returnMoneyLevel"] = returnMoneyLevel;
            params["isShow"] = isShow;
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

        function getDetail(appId){
            window.location.href="/accountTrack/showDetailed?appId="+appId+
                    "&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val();
        }

        //导出
        function importExcel(){
            var param = queryParams();
            window.location.href = "/accountTrack/importExcelReDetail?toAccountId="+param.toAccountId+"&mobile="+param.mobile
            +"&startTime="+param.startTime+"&endTime="+param.endTime+"&returnMoneyLevel="+param.returnMoneyLevel+"&isShow="+param.isShow;
        }

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        收支明细
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <%--查询条件--%>
            <label>查询时间：</label>
            <input type="text" class="Wdate form-control" id="startTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>用户ID:</label>
            <input type="text" class="form-control" name="appId" autocomplete="off"
                   placeholder="" style="width: 60px;display: inline-block;">
            <label>手机号:</label>
            <input type="text" class="form-control" name="mobile" autocomplete="off"
                   placeholder="请输入手机号" style="width: 150px;display: inline-block;">
            <label>收入类型：</label>
            <select class="form-control" id="returnMoneyLevel" style="width: 180px;display: inline-block;">
                <option value="-1">全部</option>
                <option value="0">直播间收益</option>
                <option value="1">分销奖励</option>
                <option value="2">推荐老师平台奖励</option>
                <option value="3">老师课程授课奖励</option>
                <option value="4">老师粉丝关注奖励</option>
            </select>
            <label>是否显示提现：</label>
            <select class="form-control" id="isShow" style="width: 70px;display: inline-block;">
                <option value="0">是</option>
                <option value="1">否</option>
            </select>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-info" onclick="importExcel();">
                <i class=" glyphicon glyphicon-search"></i> 导出
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

    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
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
    $("#startTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
