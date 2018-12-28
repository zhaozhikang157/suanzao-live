<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>用户购买课程订单</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
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
                url: "/course/getCourseOrdersList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'orderNo',
                        title: '订单编号',
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
                        title: '购买人',
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
                        field: 'mobile',
                        title: '手机号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'liveTopic',
                        title: '课程名称',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'startTime',
                        title: '开课时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                        }
                    },
                    {
                        field: 'endTime',
                        title: '结束时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
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
                        title: '购买时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
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
            var liveTopic = $("input[name='liveTopic']").val();
            var cstartTime = $("input[name='cstartTime']").val();
            var cendTime = $("input[name='cendTime']").val();
            var uname = $("input[name='uname']").val();
            var mobile = $("input[name='mobile']").val();
            var startTime = $("input[name='startTime']").val();
            var endTime = $("input[name='endTime']").val();
            if (!params) {
                params = {};
            }
            params["liveTopic"] = liveTopic;
            params["cstartTime"] = cstartTime;
            if(cendTime!=""){cendTime+=" 23:59:59"}
            params["cendTime"] = cendTime;
            params["uname"] = uname;
            params["mobile"] = mobile;
            params["startTime"] = startTime;
            if(endTime!=""){endTime+=" 23:59:59"}
            params["endTime"] = endTime;
            return params;
        }


        function getHeight() {
            return $(window).height() - $('#H1').outerHeight(true) - 20;
        }

        /**
         *点击查询
         */
        function doQuery(flag) {
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
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
            用户购课明细
        </h2>

        <form class=" form-inline" id="form1" name="form1">
            <div class="form-group" style="margin-top:10px ;padding: 0;">
                <label class="control-label">课程名称：</label>
                <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">

                <label class="control-label">购买人：</label>
                <input type="text" class="form-control" name="uname" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
                <label class="control-label">电话：</label>
                <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder=""
                       style="width: 180px;display: inline-block;">
            </div>


            <div class="form-group" style="margin-top:10px;padding: 0;">
                <label class="control-label">开课时间：</label>
                <input type="text" class="Wdate form-control" id="cstartTime" name="cstartTime"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="cendTime" name="cendTime"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <label class="control-label">购买时间：</label>
                <input type="text" class="Wdate form-control" id="startTime" name="startTime"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="endTime" name="endTime"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'startTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <button type="button" class="btn btn-info" onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
               <%-- <button type="button" class="btn btn-success" onclick="importExcel();">
                    <i class=" glyphicon glyphicon-export"></i> 导出
                </button>--%>
            </div>
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
  <%--  <script>
        var firstDate = new Date();
        firstDate.setDate(1);
        var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
        var endDate = new Date(firstDate);
        endDate.setMonth(firstDate.getMonth() + 1);
        endDate.setDate(0);
        var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
        $("#startTime").val(first)  //获取当月第一天
        $("#endTime").val(lastDay);//默认时间为 当月 最后一天
    </script>--%>
</div>
</body>

</html>
