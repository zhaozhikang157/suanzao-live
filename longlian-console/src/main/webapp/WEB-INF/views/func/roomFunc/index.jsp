<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>功能管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/roomFunc/getFuncList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'funcName',
                        title: '功能名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'funcCode',
                        title: '功能代码',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'funcDisc',
                        title: '功能说明',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '状态',
                        title: 'status',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (row.status == "0") {
                                return "<span style='color:green'>未启用</span>"
                            } else {
                                return "<span style='color:red'>启用</span>"
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a class="like" href="javascript:void(0)" title="修改" onclick="createOrUpdate(' + row.id + ');">修改</a>  ',
                                '<a class="like" href="javascript:void(0)" title="删除" onclick="isDelete(' + row.id + ');">删除</a>  '
                            ].join('');
                        }
                    }]
            });
        }
        /**
         * 创建或者更新角色名称，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加功能"
            if (id) title = "修改功能";
            var url = "/roomFunc/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 200, width: 550, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            alert(json.msg || "保存出错");
                        }
                    });
                }
            });
        }
        
        //传递的参数
        function queryParams(params) {
            var name = $("input[name='name']").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            return params;
        }


        function getHeight() {
            return $(window).height()-6 - $('h2').outerHeight(true);
        }

        function isDelete(ids) {
            var option = {
                width: 400, content: "确定要删除？", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids;
                    var obj = tools.requestRs("/roomFunc/deleteByIds", param, 'post');
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
        /**
         *点击查询
         */
        function doQuery(flag) {
        	if(flag){
        		$('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
        	}
            $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
        }
       

        //获取选中ID
        function getselectedId(){
            var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            return ids;
        }


    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-top: 15px">
        功能管理
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新建功能
            </button>
            <input type="text" class="form-control" name="name" autocomplete="off" placeholder="输入名称"
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
