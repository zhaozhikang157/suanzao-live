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
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/userReward/findrewardTypeInfoPage",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'amount',
                        title: '金额',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss");
                        }
                    },
                    {
                        field: 'remark',
                        title: '备注',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'picAddress',
                        title: '图标',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<img src='"+value+"' style='width:50px;hight:50px'>";
                        }
                    },
                    {
                        field: 'chatPicAddress',
                        title: '聊天室图标',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<img src='"+value+"' style='width:50px;hight:50px'>";
                        }
                    },
                    {
                        field: 'status',
                        title: '状态',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "<span>启用</span>"
                            } else if (value == '1') {
                                return "<span>禁用</span>"
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (row.status == "0") {
                                return [
                                    '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">' + '编辑</a>&nbsp;&nbsp;&nbsp;',
                                    '<a href="javascript:void(0)" onclick="setStatusForbidden(' + row.id + ',' + row.status + ');">' + '禁用</a>'
                                ].join('');
                            } else if (row.status == "1") {
                                return [
                                    '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">' + '编辑</a>&nbsp;&nbsp;&nbsp;',
                                    '<a href="javascript:void(0)" onclick="setStatusForbidden (' + row.id + ',' + row.status + ');">' + '启用</a>'
                                ].join('');
                            }


                        }
                    }]
            });
        }
        //传递的参数
        function queryParams(params) {
            var status = $("#status").find("option:selected").val();
            if (!params) {
                params = {};
            }
            params["status"] = status;
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
        function detailFormatter(index, row) {
            var html = [];
            $.each(row, function (key, value) {
                html.push('<p><b>' + key + ':</b> ' + value + '</p>');
            });
            return html.join('');
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

        /**
         * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加参数"
            if (id) title = "编辑";
            var url = "/userReward/toCreateOrUpdate?id=" + id;
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
        function setStatusForbidden(id, status) {
            var content = "确定要禁用该数据?";
            if (status == '1') {
                content = "确定要启用该数据?";
                status = '0';
            }else{
                status = '1';
            }
            var option = {
                width: 400, content: content, confrimFunc: function () {
                    toSetStatusForbidden(id, status);
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

        /**
         * 根据ID设置禁用
         */
        function toSetStatusForbidden(id, status) {
            var obj = tools.requestRs("/userReward/updateStatus?id=" + id + "&status=" + status, 'post');
            if (obj.success) {
                jbox_notice({content: "操作成功", autoClose: 2000, className: "success"});
                jboxObj.close();
                $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
            }
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        打赏管理
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新增
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
