<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>电子回单</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/default/datepicker.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/my97/skin/whyGreen/datepicker.css"/>

    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/ordersController/getOrderElectronic",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [

                    {
                        field: 'mobile',
                        title: '用户手机',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'orderNo',
                        title: '订单编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'orderType',
                        title: '服务类型',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '1') {
                                return "提现";
                            } else if (value == '2') {
                                return "充值";
                            } else if(value == '009'){
                                return "钱包购买课程";
                            }else if(value == '014'){
                                return "微信购买课程";
                            }
                        }
                    },
                    {
                        field: 'amount',
                        title: '金额',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'successTime',
                        title: '日期',
                        align: 'center',
                        valign: 'middle',
                        sortable: true,
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a href="javascript:void(0)" onclick="printOrder(' + row.id + "," + row.orderType + ');">详情</a>&nbsp;&nbsp;&nbsp;',
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
        //传递的参数
        function queryParams(params) {
            var orderNo = $("input[name='orderNo']").val();//平台流水编号
            var orderType = $("#orderType option:selected").val();  //开户银行
            var createTimeBeginStr = $("#createTimeBeginStr").val();
            var createTimeEndStr = $("#createTimeEndStr").val();
            var appMobile = $("input[name='mobile']").val();//用户手机
            if (!params) {
                params = {};
            }
            params["orderNo"] = orderNo;
            params["orderType"] = orderType;
            params["startDate"] = createTimeBeginStr;
            params["endDate"] = createTimeEndStr;
            params["mobile"] = appMobile;
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
            return $(window).height() - 30 - $('#toolbar').outerHeight(true);
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
        /**
         *返钱
         * @param id
         */
        function printOrder(id, orderType) {
            if(orderType == "0009" || orderType == "014"){
                orderType = "0";
            }
            var title = "电子回单打印";
            var url = "/ordersController/orderElectronicPrint?id=" + id + "&orderType=" + orderType;
            bsWindow(url, title, {
                height: 700, width: 1000, buttons: [
//        {classStyle:"btn btn-primary" ,name:"关闭",clickFun:function(name ,bs){
//          window.BSWINDOW.modal("hide");
//        }}
                ]
            });
        }

        function closeWindow(obj) {
            if (obj.success) {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: obj.msg, autoClose: 2000, className: "success"});
                doQuery();
            } else {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                doQuery();
            }
        }


        //导出
        function exportExcel() {
            var orderNo = $("input[name='orderNo']").val();//平台流水编号
            var orderType = $("#orderType option:selected").val();
            if(orderType == "2"){
                orderType == "0";
            }
            var startDate = $("#createTimeBeginStr").val();
            var endDate = $("#createTimeEndStr").val();
            var appMobile = $("input[name='mobile']").val();//用户手机
            window.location.href = "/ordersController/exportExcelorderElectronic?orderNo=" + orderNo + "&orderType=" + orderType + "&startDate=" + startDate + "&endDate=" + endDate + "&appMobile=" + appMobile;
        }

        //批量打印
        function batchPrint() {
            var orderNo = $("input[name='orderNo']").val();//平台流水编号
            var orderType = $("#orderType option:selected").val();  //开户银行
            var createTimeBeginStr = $("#createTimeBeginStr").val();
            var createTimeEndStr = $("#createTimeEndStr").val();
            var appMobile = $("input[name='mobile']").val();//用户手机
            window.open("/ordersController/batchPrint?orderNo="+orderNo+"&orderType="+orderType+"&createTimeBeginStr="
            +createTimeBeginStr+"&createTimeEndStr="+createTimeEndStr+"&appMobile="+appMobile);
        }

        //电子回单导出word
        function exportWord() {
            var orderNo = $("input[name='orderNo']").val();//平台流水编号
            var orderType = $("#orderType option:selected").val();  //开户银行
            var createTimeBeginStr = $("#createTimeBeginStr").val();
            var createTimeEndStr = $("#createTimeEndStr").val();
            var appMobile = $("input[name='mobile']").val();//用户手机
            window.location.href = "/ordersController/exportWord?orderNo="+orderNo+"&orderType="+orderType+"&createTimeBeginStr="
                    +createTimeBeginStr+"&createTimeEndStr="+createTimeEndStr+"&appMobile="+appMobile;
        }



    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        电子回单
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label>订单编号:</label>
            <input type="text" class="form-control" name="orderNo" autocomplete="off" placeholder="订单编号"
                   placeholder="" style="width: 160px;display: inline-block;">
            <label>用户手机:</label>
            <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder="用户手机"
                   placeholder="" style="width: 160px;display: inline-block;">
            <label>服务类型:</label>
            <select id="orderType" class="form-control" style="display: inline-block;width: 120px;" name="orderType">
                <option selected value="">全部</option>
                <option value="0">钱包购买课程</option>
                <option value="2">微信购买课程</option>
                <option value="1">提现</option>
            </select>

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
            <button type="button" class="btn btn-info" onclick="exportExcel();">
                <i class=" glyphicon glyphicon-search"></i> 导出
            </button>
            <button type="button" class="btn btn-info" onclick="batchPrint();">
                <i class=" glyphicon glyphicon-search"></i> 批量打印预览
            </button>
            <button type="button" class="btn btn-info" onclick="exportWord();">
                <i class=" glyphicon glyphicon-search"></i> 电子回单导出word
            </button>
            <input type="reset" class="btn btn-warning" value="重置"/>
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
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<script src="${ctx}/web/res/my97/calendar.js"></script>
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<script src="${ctx}/web/res/my97/xdate.dev.js"></script>
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
</html>
