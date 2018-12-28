<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/dataTable/dataTables.min.css"/>
    <script src="${ctx}/web/res/dataTable/dataTables.min.js" />
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>


    <script >
        $(getLogTypeAndTableType());
        $(document).ready(function(){
            doInit();
        });
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/systemLogController/getList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'systemType',
                        title: '消息类型',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if(value=="0"){
                                return "<span>APP端</span>"
                            }else if(value=="1"){
                                return "<span>console后台</span>"
                            }
                        }
                    },
                    {
                        field: 'userName',
                        title: '操作人员',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'ipAddress',
                        title: 'IP地址',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'logTypeStr',
                        title: '日志类型',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'object',
                        title: '操作对象',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'content',
                        title: '备注',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<span title='"+value+"' class='contentTex'>"+value+"</span>"
                        }
                    },
                    {
                        field: 'logTime',
                        title: '操作时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'tableTypeStr',
                        title: '表类型',
                        align: 'center',
                        valign: 'middle'
                    }

                ]
            });
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);
        //传递的参数
        function queryParams(params) {
            var userName = $("input[name='userName']").val();
            var createTimeBegin = $("#createTimeBeginStr").val();
            var createTimeEnd = $("#createTimeEndStr").val();
            var logType=$("#logType").find("option:selected").val()
            var tableType=$("#tableType").find("option:selected").val();
            var object = $("input[name='object']").val();
            var content = $("input[name='content']").val();
            if(!params){
                params = {};
            }
            params["userName"] = userName;
            params["createTimeBegin"] = createTimeBegin;
            params["createTimeEnd"] = createTimeEnd;
            params["logType"] = logType;
            params["tableType"] = tableType;
            params["object"] = object;
            params["content"] = content;
            return params;
        }
        function refresh(){
            $('#table-bootstrap').bootstrapTable('refresh', {query:{a:1,b:2}});
        }
        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });
        function getHeight() {
            return $(window).height()-10 - $('h2').outerHeight(true);
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
        function  doQuery(flag){
            if(flag){
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
            }else{
                $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
            }

        }

        /**
        *获取日志类型和表类型下拉选条件
         */
        function getLogTypeAndTableType(){
            $.ajax({
                url: "/systemLogController/getLogTypeAndTableType",
                dataType: "json",
                type: "POST",
                success: function (result) {
                    if(result.success){
                        var logTypeList=result.data.logTypeList;//日志类型
                        var logTableTypeList=result.data.logTableTypeList;//表类型

                        for(var i=0;i<logTypeList.length;i++){
                            $("#logType").append('<option value="'+logTypeList[i].type+'"  >'+logTypeList[i].name+'</option>');
                        }
                        for(var i=0;i<logTableTypeList.length;i++){
                            $("#tableType").append('<option value="'+logTableTypeList[i].val+'"  >'+logTableTypeList[i].name+'</option>');
                        }
                    }else{
                        jbox_notice({content:result.msg,autoClose:2000 ,className:"warning"});
                    }
                }
            })


            }


    </script>
</head>
<body onload="doInit();">
<div class="container" style="width: 86%;float: left;">
    <h2>
        系统日志
    </h2>
    <div id="toolbar">
        <form class="form-horizontal" id="form" name="form1">
            <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>操作人员</label>
            <input type="text" class="form-control" name="userName" autocomplete="off" placeholder="操作人员" placeholder="" style="width: 100px;display: inline-block;">
            <label>日志类型 </label>
            <select id="logType" class="form-control" style="width: 170px;display: inline-block;">
                <option value="" selected >全部</option>
            </select>
            <label>表类型 </label>
            <select id="tableType"class="form-control"style="width: 115px;display: inline-block;">
                <option value="" selected >全部</option>
            </select>
            <label>操作对象 </label>
            <input type="text" class="form-control" name="object" autocomplete="off" placeholder="操作对象" placeholder="" style="width: 100px;display: inline-block;">
            <label>备注 </label>
            <input type="text" class="form-control" name="content" autocomplete="off" placeholder="备注" placeholder="" style="width: 120px;display: inline-block;">
            <button type="button" class="btn btn-info"  onclick="doQuery(true);">
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
           class="table table-striped table_list table-bordered"
            >
    </table>
</div>


</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<%--<script src="${ctx}/web/res/my97/WdatePicker.js"></script>--%>
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
<%--<link rel="stylesheet" href="${ctx}/web/res/style/css/common/btable.css">--%>
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
