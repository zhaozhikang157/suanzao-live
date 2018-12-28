<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>

    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/inviCard/findAllInviCardPage",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'code',
                        title: '模板号',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'name',
                        title: '名称',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'address',
                        title: '图像',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return '<img src="' + value + '" height="20" width="20">';
                        }
                    },
                    {
                        field: 'xy',
                        title: '坐标',
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
                                '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">' + '编辑</a>&nbsp;&nbsp;&nbsp;',
                                '<a href="javascript:void(0)" onclick="delInviCard(' + row.id + ');">' + '删除</a>'
                            ].join('');
                        }
                    }
                ]
            });
        }
        //传递的参数
        function queryParams(params) {
            var name = $("input[name='name']").val();
            var mobile = $("input[name='mobile']").val();
            var userType = $("#userType").find("option:selected").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            params["userType"] = userType;
            return params;
        }

        function refresh() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
        }

        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery(flag) {
            if (flag) {
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
            } else {
                $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
            }
        }

        function createOrUpdate(id) {
            var title = "添加模本"
            if (id) title = "修改模本";
            var url = "/inviCard/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 400, width: 550, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                        } else {
                            alert(json.msg || "保存出错");
                        }
                        console.log(json);
                    });
                }
            });
        }

        /**
         * 设置禁用
         * */
        var jboxObj;
        function delInviCard(id) {
            var option = {
                width: 400, content: "确定要删除该模板吗？", confrimFunc: function () {
                    var param = {id: id}
                    var obj = tools.requestRs("/inviCard/delInviCardById", param, 'post');
                    if (obj.success) {
                        jbox_notice({content: "删除成功!", autoClose: 2000, className: "success"});
                        jboxObj.close();
                        $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                    }
                },
                draggable: 'title',
                closeButton: 'box',
                animation: {
                    open: 'slide:left',
                    close: 'slide:right'
                }
            };
            jboxObj = jbox_confirm(option);
        }

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        模板管理
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-info" onclick="createOrUpdate(0);">
                <i class=" glyphicon glyphicon-search"></i> 添加
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

    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true"
            >
    </table>
</div>
</body>
</html>
