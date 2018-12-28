<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>用户记录管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <style>
        .bootstrap-table{clear: both}
    </style>
    <script>
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/userCount/getUserCountRecordlist",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'countTime',
                        title: '统计时间',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'type',
                        title: '统计类型',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "活跃用户数";
                            }
                            else if(value =="1")
                            {
                                return "新增用户数";
                            }
                            else if(value =="2")
                            {
                                return "启动数";
                            }
                            else if(value =="3") {
                                return "新增vip用户数";
                            }
                            else if(value =="3") {
                                return "新增vip用户数";
                            }
                            else if(value =="101") {
                                return "新增课程数";
                            }
                            else if(value =="102") {
                                return "新增付费课程数";
                            }
                            else if(value =="103") {
                                return "新增付费(新增的)课程数";
                            }
                            else if(value =="105") {
                                return "新增加用户";
                            }
                            else if(value =="106") {
                                return "新增加老师";
                            }
                            else if(value =="107") {
                                return "付费总用户数";
                            }
                            else if(value =="108") {
                                return "付费总金额";
                            }

                            else if(value =="109") {
                                return "新增付费(新增的)用户数";
                            }
                            else if(value =="110") {
                                return "活跃用户";
                            }
                            else if(value =="112") {
                                return "PV";
                            }
                            else if(value =="113") {
                                return "次留存率";
                            }
                            else if(value =="114") {
                                return "新增用户付费率";
                            }
                            else{
                                return "新增课程付费率";
                            }
                        }
                    },
                    {
                        field: 'count',
                        title: '统计数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                    return [
//                                '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">'+'编辑</a>&nbsp;&nbsp;&nbsp;',
                        '<a href="javascript:void(0)" onclick="deleteById(' + row.id + ');">'+'删除</a>'
                    ].join('');
                }
            }]
        });
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);

        function deleteById(id) {
            var option = {
                width: 500, content: "确定删除吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/userCount/deleteById", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        //传递的参数
        function queryParams(params) {
            var machineType=$("#machineType option:selected").val();  //终端类型
//            var createTimeBeginStr = $("#createTimeBeginStr").val();
//            var createTimeEndStr = $("#createTimeEndStr").val();
//            var appMobile = $("input[name='mobile']").val();//用户手机
            if(!params){
                params = {};
            }
            params["machineType"] = machineType;
//            params["startDate"] = createTimeBeginStr;
//            params["endDate"] = createTimeEndStr;
//            params["mobile"] = appMobile;
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
        员工记录管理
    </h2>
    <div id="toolbar">

            <form class="form-horizontal" id="form1" name="form1">
                <div class="left" style="float: left">
                    <label>终端类型:</label>
                    <select id="machineType" class="form-control" style="display: inline-block;width: 120px;" name="machineType">
                        <option selected value="">全部</option>
                        <option  value="android">android</option>
                        <option  value="ios">ios</option>
                    </select>
                </div>
                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
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
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>


</body>
</html>
