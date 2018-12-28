<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>url管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>

    <script>
        $(function(){
            doInit();
        });
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/dataStatistics/getPageUrlList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'pageUrl',
                        title: 'URL',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'pageName',
                        title: 'URL名称',
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
                                '<a class="like" href="javascript:void(0)" title="修改" onclick="createOrUpdate(' + row.id + ');">修改</a>  ',
                                '<a class="like" href="javascript:void(0)" title="删除" onclick="deleteById(' + row.id + ');">删除</a>  '
                            ].join('');
                        }
                    }]
            });
        }
        /**
         * 创建或者更新，弹出框
         */
        function createOrUpdate(id) {
            var title = "创建页面Url"
            if (id) title = "编辑页面Url";
            var url = "/dataStatistics/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 500, width: 550, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                        }
                    });
                }
            });
        }
        function deleteById(ids) {
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "id=" + ids;
                    var obj = tools.requestRs("/dataStatistics/deleteById", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
        //传递的参数
        function queryParams(params) {
            var pageName = $("input[name='pageName']").val();
            if(!params){
                params = {};
            }
            params["pageName"] = pageName;
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
<div class="container" style="width: 86%;float: left;">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        url管理
    </h2>
    <div id="toolbar" >
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新建Url
            </button>
            <input type="text" class="form-control" name="pageName" autocomplete="off" placeholder="输入url名称"
                   placeholder="" style="width: 220px;display: inline-block;">
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
