<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>推广渠道数据统计</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <style>
        .bootstrap-table{clear: both}
    </style>
    <script>
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/course/getListChannelSimplePage",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: 'ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'liveTopic',
                        title: '标题',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'startTime',
                        title: '开播时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                            return [
                                '<a class="like" href="javascript:(void)" title="统计" onclick="courseChannelCount(' + row.id + ');">统计</a>  '
                            ].join('');
                        }
                    }]
            });
        }
        function courseChannelCount(id){
            var title = ""
            var url = "/shareChannel/toCourseChannelCount?id="+id;
            bsWindow(url,title,{height:600,width:1000,buttons: [
                {
                    classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
                }
            ]
            });
        }
        //传递的参数
        function queryParams(params) {
            var liveTopic = $("input[name='liveTopic']").val();
            var beginTime = $("#beginTime").val();  //起始时间
            var endTime = $("#endTime").val();    //结束时间
            if (beginTime != "")beginTime = beginTime + " 00:00:00";
            if (endTime != "")endTime = endTime + " 23:59:59";
            if(!params){
                params = {};
            }
            params["liveTopic"] = liveTopic;
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
            return params;
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
        function onLoadSuccessFunc(){
            //按字段选中
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"userId", values:["admin"]});
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"id", values:[LOGIN_ID]});
        }


        function getHeight() {
            return $(window).height()-6 - $('h2').outerHeight(true);
        }

    </script>

</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        推广渠道数据统计
    </h2>
    <div id="toolbar" >
        <form class="form-horizontal" id="form1" name="form1">
            <label>标题:</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder=""
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>开课时间:</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            
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
    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true">
    </table>
</div>


</body>
</html>
