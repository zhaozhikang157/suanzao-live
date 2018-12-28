<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>日志管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script>
        $(function () {
            $.ajax({
                type: "GET",
                url: "/mSystemLog/getMSysLogTypeList",
                success: function (data) {
                    $.each(data.data.mSysLogTypes, function (idxs, items) {
                        $("#type").append("<option value=" + items.type + ">" + items.name + "</option>");
                    });
                }
            })
        });
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/mSystemLog/findAll",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userId',
                        title: '用户编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userName',
                        title: '用户名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'object',
                        title: 'object',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'comment',
                        title: '日志内容',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'logTime',
                        title: '日志时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'ip',
                        title: 'ip地址',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'typeName',
                        title: '日志类型',
                        align: 'center',
                        valign: 'middle'
                    }
                ]
            });
        }




        //传递的参数
        function queryParams(params) {
            var userName = $("input[name='userName']").val();
            var type = $("select[name='type'] option:selected").val();
            var createTimeBeginStr = $("#createTimeBeginStr").val();
            var createTimeEndStr = $("#createTimeEndStr").val();
            if (!params) {
                params = {};
            }
            if (type != "") {
                params["type"] = type;
            }
            params["userName"] = userName;
            params["createTimeBegin"] = createTimeBeginStr;
            params["createTimeEnd"] = createTimeEndStr;
            return params;
        }


        function getHeight() {
            return $(window).height() - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery(flag) {
        	if(flag){
        		$('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
        	}
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2> 日志管理 </h2>

    <div id="toolbar">
        <form class="" id="form1" name="form1">
            <div>
                <div class="form-group">
                    <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
                           style="width: 120px;display: inline-block;"/>
                    至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                            onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
                            style="width: 120px;display: inline-block;"/>

                    <label>日志类型：</label>
                    <select class="form-control" id="type" name="type" style="width: 160px;display: inline-block;">
                        <option value="">全部</option>
                    </select>

                    <label>用户名：</label>
                    <input type="text" class="form-control" name="userName" autocomplete="off" placeholder="用户名"
                           placeholder="" style="width: 160px;display: inline-block;">
                    <button type="button" class="btn btn-info" onclick="doQuery(true);">
                        <i class=" glyphicon glyphicon-search"></i> 查询
                    </button>

                </div>
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
    $("#createTimeBeginStr").val(first); //获取当月第一天
    $("#createTimeEndStr").val(lastDay);//默认时间为 当月 最后一天

</script>
</html>
