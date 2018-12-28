<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>超级管理员列表</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>

    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/appUser/loadData",
                classes: 'table table-hover',
                height: getHeight(),
                pageSize: 20,
                queryParams: null,
                columns: [
                    {
                        field: 'id',
                        title: 'ID',
                        align: 'center',
                        width:200,
                        valign: 'middle'
                    }, {
                        field: 'adminId',
                        title: '管理员ID',
                        align: 'center',
                        valign: 'middle'

                    },
                    {
                        field: 'adminName',
                        title: '管理员名称',
                        align: 'center',
                        valign: 'middle'

                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        width:360,
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss');
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            var arra = new Array();
                            arra.push(' <a href="javascript:void(0)" onclick="deleteById(' + row.id + ');">' + '删除</a>');
                            return arra.join('');
                        }
                    }]
            });
            var css = { height: "94%"};
            $("#indexiframe" , parent.document).css(css);
          }

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        function addOrUpdate(){
            window.location.href = '/appUser/to_add_system_admin';
        }

        function deleteById(id) {
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "id=" + id;
                    var obj = tools.requestRs("/appUser/deleteSystemAdmin", param, 'post');
                    if (obj.code=='000000') {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        window.location.reload(true);
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
    </script>
</head>
<body  onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">

    <div id="toolbar">

            <button type="button" class="btn btn-success" onclick="addOrUpdate();">
                <i class=" glyphicon glyphicon-button">添加</i>
            </button>
    </div>
    <table id="table-bootstrap"
           border="0" cellpadding="4" cellspacing="0"
           data-pagination="false"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           style="">
    </table>

</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">

</html>
