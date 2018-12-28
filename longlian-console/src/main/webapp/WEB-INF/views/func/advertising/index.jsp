<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>广告管理</title>
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
                url: "/advertising/findAllAdvertising",
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
                        field: 'advertType',
                        title: '广告类型',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:green'>链接</span>"
                            } else {
                                return "<span style='color:red'>普通</span>"
                            }
                        }
                    },
                    {
                        field: 'type',
                        title: '类型',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "<span style='color:green'>首页</span>"
                            } else {
                                return "<span style='color:red'>讲师轮播图</span>"
                            }
                        }
                    },
                    {
                        field: 'picAddress',
                        title: '图片',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<img style='max-width:100px;max-height:100px;' src="+value+" />";

                        }
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
                        field: 'sortOrder',
                        title: '排序',
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
                        field: 'remarks',
                        title: '备注',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'courseId',
                        title: '课程ID',
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
                                '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a>  ',
                                '<a class="remove" href="javascript:void(0)" title="删除" onclick="deleteById(' + row.id + ');">删除</a>  '
                            ].join('');
                        }
                    }]
            });
        }

        //传递的参数
        function queryParams(params) {
            var type = $("select[name='type'] option:selected").val();    //类型
            if (!params) {
                params = {};
            }

            params["type"] = type;
            return params;
        }

        function deleteById(ids) {
            if (ids == null || ids == "") {
                alert("请选择要删除的营业类型!");
                return;
            }
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "id=" + ids;
                    var obj = tools.requestRs("/advertising/deleteById", param, 'post');
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
            var title = "添加广告";
            if (id) title = "编辑广告";
            var url = "/advertising/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 500, width: 600, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            jbox_notice({content: "保存失败:"+json.msg, autoClose: 2000, className: "error"});
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
    <h2> 广告管理 </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">

            <label class="control-label">类型:</label>
            <select class="form-control" id="type" name="type"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">首页</option>
                <option value="1">讲师轮播图</option>
            </select>
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
