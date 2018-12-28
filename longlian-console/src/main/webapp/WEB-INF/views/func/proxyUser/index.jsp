<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>代理人管理</title>
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
                url: "/proxyUser/getProxyUserList",
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
                        field: 'name',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'gender',
                        title: '性别',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:red'>男</span>"
                            } else {
                                return "<span style='color:green'>女</span>"
                            }
                        }
                    },
                    {
                        field: 'status',
                        title: '状态',
                        align: 'center',
                        valign: 'middle',  formatter: function (value, row) {
                        if (value == "0") {
                            return "<span style='color:red'>启用</span>"
                        } else {
                            return "<span style='color:green'>撤销</span>"
                        }
                    }},
                    {
                field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                    return [
                                '<a href="javascript:void(0)" onclick="lookDetail(' + row.id + ');">'+'查看详情</a>'
                    ].join('');
                }
                    }]
        });
        }
      
        function lookDetail(id) {
            var title = "查看详情";
            var url = "/proxyUser/toLookDetail?id=" + id;
            bsWindow(url, title, {
                height: 600, width: 800, buttons: [
                    {
                        classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                        window.BSWINDOW.modal("hide");
                        doQuery();
                    }
                    }
                ]
            });
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
        代理人管理
    </h2>
 <%--   <div id="toolbar">

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

    </div>--%>
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
