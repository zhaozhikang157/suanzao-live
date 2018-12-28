<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>关注奖励规则</title>
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
                url: "/followRewardRule/getList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'name',
                        title: '名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'status',
                        title: '状态',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "<span style='color:green'>启用</span>"
                            } else {
                                return "<span style='color:red'>禁用</span>"
                            }
                        }
                    },

                    {
                        field: 'menCount',
                        title: '人数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'amount',
                        title: '奖励金额',
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
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a>  ',
                                '<a class="remove" href="javascript:void(0)" title="禁用" onclick="deleteById(' + row.id + ',1);">禁用</a>  ',
                                '<a class="remove" href="javascript:void(0)" title="启用" onclick="deleteById(' + row.id + ',0);">启用</a>  '
                            ].join('');
                        }
                    }]
            });
        }

        //传递的参数
        function queryParams(params) {
            var name = $("#name").val().trim();    //类型
            if (!params) {
                params = {};
            }

            params["name"] = name;
            return params;
        }

        function deleteById(id,status) {
//            1： 禁用 0：启用
            var deleteStatus = "确定要禁用！";
            if(status==0)deleteStatus = "确定要启用！";
            var option = {
                width: 400, content: deleteStatus, confrimFunc: function (jboxObj) {
                    var obj = tools.requestRs("/followRewardRule/deleteById?id=" + id +"&status="+status, 'GET');
                    if (obj) {
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

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }
        /**
         * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加奖励规程";
            if (id) title = "编辑奖励规程";
            var url = "/followRewardRule/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 500, width: 600, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json) {
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
    <h2> 关注奖励规则 </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">

            <label>课程主题：</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off" placeholder="名称"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>

            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新建
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
