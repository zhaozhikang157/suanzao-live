<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>直播流管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveChannel/getList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'code',
                        title: '通道编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'pushAddr',
                        title: '通道推送地址',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr1',
                        title: '通道直播地址rtmp',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr2',
                        title: '通道直播地址flv',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'playAddr3',
                        title: '通道直播地址m3u8',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a>  '
                                , '<a class="remove" href="javascript:void(0)" title="删除" onclick="deleteById(' + row.id + ');">删除</a>  '
                            ].join('');
                        }
                    }]
            });
        }

        //传递的参数
        function queryParams(params) {
            var code = $("#code").val().trim();    //类型
            if (!params) {
                params = {};
            }

            params["code"] = code;
            return params;
        }

        function deleteById(id) {
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param ={"id":id};
                    var obj = tools.requestRs("/liveChannel/deleteById", param, 'GET');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: obj.msg, autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery() {
            if(flag){
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
            }
            $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
        }
        /**
         * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加直播流通道";
            if (id) title = "编辑直播流通道";
            var url = "/liveChannel/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 400, width: 600, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: json.msg, autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            jbox_notice({content: json.msg, autoClose: 2000, className: "error"});
                        }
                    });
                }
            });
        }
        /**
         *  通道池管理
         */
        function poolManage() {
            document.location.href = "/liveChannel/getPoolPage";
        }

        //获取选中ID
        function getselectedId() {
            var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            return ids;
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2> 直播流通道管理 </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">

            <label class="control-label">通道编号:</label>
            <input type="text" class="form-control" name="code" id="code" autocomplete="off" placeholder="通道编号"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>

            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新建
            </button>

            <button type="button" class="btn btn-primary" onclick="poolManage();">
                <i class="glyphicon glyphicon-plus"></i> 通道池管理
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
           data-show-pagination-switch="true">
    </table>
</div>
</body>
</html>
