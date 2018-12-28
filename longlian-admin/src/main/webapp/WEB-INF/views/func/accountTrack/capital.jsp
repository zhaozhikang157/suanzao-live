<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>资金管理</title>
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
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/accountTrack/trackAndOrders",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'orderNo',
                        title: '订单编号',
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
                        field: 'realName',
                        align: 'center',
                        visible:false,
                        valign: 'middle'
                    },
                    {
                        field: 'uname',
                        title: '用户名称',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                           if(value==null || value==""){
                               return row.realName
                           }else{
                                return value;
                            }
                        }
                    },
                    {
                        field: 'orderType',
                        title: '业务类型',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'liveTopic',
                        title: '课程名称',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'bankType',
                        title: '支付方式',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'oamount',
                        title: '收入/支出',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'tamount',
                        title: '实际收入/支出',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'llCharge',
                        title: '用户手续费',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'charge',
                        title: '银行手续费',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'currBalance',
                        title: '账户余额',
                        align: 'center',
                        valign: 'middle'
                    },
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
                        field: 'remark',
                        title: '备注',
                        align: 'center',
                        valign: 'middle'
                    },
                ]
            });
            getAllBanktype();
            getCountAccount();
            var css = { height: "94%"};
            $("#indexiframe" , parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.document).css(css);
        },500);

        function getCountAccount() {
            var params = queryParams();
            if (params.bankType == undefined) {
                params.bankType = '';
            }
            var str = "orderNo=" + params.orderNo + "&orderType=" + params.orderType + "&bankType=" + params.bankType +
                    "&startDate=" + params.startTime + "&endDate=" + params.endTime + "&mobile=" + params.mobile;
            $.ajax({
                type: "POST",
                url: "/accountTrack/getCountAccount",
                data: str,
                async: false,
                success: function (data) {
                    if (params.orderType == '0') {    //课时
                        $("#countOamount").text(data.countOamount + "元"); //总收入
                        $("#countTamount").text("0 元");              //总支出
                    } else {                          //提现
                        $("#countOamount").text("0 元");
                        $("#countTamount").text(data.countTamount + "元");
                    }
                    $("#countllCharge").text(data.countllCharge + "元");
                    $("#countCharge").text(data.countCharge + "元");

                    $("#yearOamount").text(data.yearOamount + "元");
                    $("#monthOamount").text(data.monthOamount + "元");
                    $("#weekOamount").text(data.weekOamount + "元");
                    $("#dayOamount").text(data.dayOamount + "元");


                }
            })
        }

        function getAllBanktype() {
            $.ajax({
                type: "get",
                url: "/ordersController/getAllBankType",
                success: function (data) {
                    var html = '<option value="" selected>全部</option>';
                    $.each(data.data, function (i, item) {
                        html += '<option value="' + item.value + '">' + item.text + '</option>';
                    })
                    $("#bankType").append(html);
                }
            })
        }

        //导出Excel
        function importExcel() {
            var params = queryParams();
            var str = "orderNo=" + params.orderNo + "&orderType=" + params.orderType + "&bankType=" + params.bankType +
                    "&startDate=" + params.startTime + "&endDate=" + params.endTime + "&mobile=" + params.mobile;
            window.location.href = "/accountTrack/importExcel?" + str;
        }
        //传递的参数
        function queryParams(params) {
            var orderNo = $("input[name='orderNo']").val();
            var orderType = $("select[name='orderType'] option:selected").val();
            var bankType = $("select[name='bankType'] option:selected").val();
            var startTime = $("input[name='startTime']").val();
            var endTime = $("input[name='endTime']").val();
            var mobile = $("input[name='mobile']").val();
            if (!params) {
                params = {};
            }
            params["orderNo"] = orderNo;
            params["orderType"] = orderType;
            params["bankType"] = bankType;
            params["startTime"] = startTime;
            params["endTime"] = endTime;
            params["mobile"] = mobile;
            return params;
        }


        function getHeight() {
            return $(window).height() - $('#H1').outerHeight(true) - 20;
        }

        /**
         *点击查询
         */
        function doQuery(flag) {
            getCountAccount();
            if (flag) {
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
            }
            $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
        }
    </script>
    <style>
        .fontweight {
            font-weight: bold;
            font-size: 16px;
        }

        .fontweight img {
            margin-right: 8px;
            vertical-align: middle;
        }
    </style>
</head>

<body onload="doInit();">
<div class="container" style="width:97%;">
    <div id="H1">
        <h2 style="margin-top: 15px;">
            资金统计
        </h2>

        <form class=" form-inline" id="form1" name="form1">
            <div class="form-group" style="margin-top:10px ;padding: 0;">
                <label class="control-label">订单编号：</label>
                <input type="text" class="form-control" name="orderNo" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
                <label class="control-label">手机号：</label>
                <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
                <label class="control-label">业务类型：</label>
                <select class="form-control" name="orderType">
                    <option value="0">课时</option>
                    <option value="1">提现</option>
                </select>
                <label class="control-label">支付类型：</label>
                <select class="form-control" name="bankType" id="bankType">

                </select>
            </div>
            <div class="form-group" style="margin-top:10px;padding: 0;">
                <label class="control-label">交易日期：</label>
                <input type="text" class="Wdate form-control" id="startTime" name="startTime"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="endTime" name="endTime"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <button type="button" class="btn btn-info" onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
                <button type="button" class="btn btn-success" onclick="importExcel();">
                    <i class=" glyphicon glyphicon-export"></i> 导出
                </button>
            </div>
        </form>
    </div>
    <div id="toolbar">
        <div class="form-group fontweight" style="display: inline-block;margin: 0;padding: 0;">
            <label class="control-label"><img src="${ctx}/web/res/style/img/getmuch.png">总收入：</label>
            <span style="margin-right: 20px;" id="countOamount"></span>
        </div>
        <!--  周期收入   start-->

        <div class="form-group fontweight" style="display: inline-block;margin: 0;padding: 0;">
            <label class="control-label"><img src="${ctx}/web/res/style/img/outmuch.png">总支出：</label>
            <span style="margin-right: 20px;" id="countTamount"></span>
        </div>
        <div class="form-group fontweight" style="display: inline-block;margin: 0;padding: 0;">
            <label class="control-label"><img src="${ctx}/web/res/style/img/sxfs.png">用户手续费：</label>
            <span style="margin-right: 20px;" id="countllCharge"></span>
        </div>
        <div class="form-group fontweight" style="display: inline-block;margin: 0;padding: 0;">
            <label class="control-label"><img src="${ctx}/web/res/style/img/sxfs.png">银行手续费：</label>
            <span style="margin-right: 20px;" id="countCharge"></span>
        </div>
            <label class="control-label">本年收入：</label>
            <span style="margin-right: 20px;" id="yearOamount"></span>
            <label class="control-label">本月收入：</label>
            <span style="margin-right: 20px;" id="monthOamount"></span>
            <label class="control-label">本周收入：</label>
            <span style="margin-right: 20px;" id="weekOamount"></span>
            <label class="control-label">本日收入：</label>
            <span style="margin-right: 20px;" id="dayOamount"></span>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
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
    $("#startTime").val('2017-01-01')  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天

</script>
</html>
