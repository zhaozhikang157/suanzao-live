<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <link rel="stylesheet" href="/web/res/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/web/res/bootstrap-table/bootstrap-table.css">
    <script src="/web/res/jquery/jquery.min.js"></script>
    <script src="/web/res/bootstrap/js/bootstrap.min.js"></script>
    <script src="/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
<%--    <script src="/web/res/js/tools.js"></script>--%>
    <script >
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/systemLog/getLogList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                queryParams: queryParams,
                columns: [
                    {
                        field: 'userId',
                        title: '用户名',
                        align: 'center',
                        valign: 'middle',
                        sortable:true
                    },
                    {
                        field: 'userName',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle'
                    }, {
                        field: 'sex',
                        title: '性别',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobilePhone',
                        title: '手机',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'status',
                        title: '状态',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                            if(value=="OPEN"){
                                return "<span style='color:green'>启用</span>"
                            }else{
                                return "<span style='color:red'>关闭</span>"
                            }
                        }
                    },{
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                            return "<a href='javascript:void(0)' onclick='edit("+row.sid+")'>编辑</a>&nbsp;&nbsp;<a href='javascript:void(0)' onclick='del("+row.sid+")'>删除</a>";
                        }
                    }]
            });
        }
        //传递的参数
        function queryParams(params) {
            params["test2"] = "sss";
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
            return $(window).height() - $('h1').outerHeight(true);
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:90%;">
    <h1>
        <form class="form-horizontal" id="form1">
            <label class="control-label" style="padding-right: 15px; " >用户:</label>
            <input type="text" class="form-control" id="deptName" name="deptName" autocomplete="off" placeholder="输入用户名/用户姓名" placeholder="" style="width: 200px;display: inline-block;">
            <input type="hidden" class="form-control" id="parentId"  name="parentId" placeholder=""  >
            <button class="btn btn-info btn-sm" onclick="add()" >
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </h1>
    <div id="toolbar">
        <button id="remove" class="btn btn-danger" disabled>
            <i class="glyphicon glyphicon-remove"></i> Delete
        </button>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-export="true"
           data-show-columns="true"
        <%--   data-show-footer="true"--%>
           data-detail-view="true"
           data-detail-formatter="detailFormatter"
           data-show-pagination-switch="true"
           data-pagination="true">
    </table>
</div>
</body>
</html>
