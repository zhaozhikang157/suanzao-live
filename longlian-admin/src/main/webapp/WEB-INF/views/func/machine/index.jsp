<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>机器管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>

    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/my97/skin/default/datepicker.css"/>
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/my97/skin/WdatePicker.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPatg}/web/res/my97/skin/whyGreen/datepicker.css"/>
    <%--<link rel="stylesheet" href="${requestContext.contextPatg}web/res/style/css/common/btable.css">--%>

    <script>
        $(document).ready(function() {
            doInit();
        });
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/machine/getList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'machineCode',
                        title: '机器编码',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userId',
                        title: '用户ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userName',
                        title: '用户名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'machineType',
                        title: '机器类型',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'loginTime',
                        title: '登录时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'version',
                        title: '机器版本',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'connotUpdateVersion',
                        title: '不升级版本',
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
                                '<a class="like" href="javascript:void(0)" title="发送" onclick="sendUpdateMsg(' + row.id + ');">发送更新消息</a>  '
                                //, '<a class="remove" href="javascript:void(0)" title="删除" onclick="deleteById(' + row.id + ');">删除</a>  '
                            ].join('');
                        }
                    }]
            });

        }

        //传递的参数
        function queryParams(params) {
            var machineType = $("#machineType").val().trim();    //类型
            var version = $("#version").val().trim();    //类型
            var userName = $("#userName").val().trim();    //类型
            var userId = $("#userId").val().trim();    //类型
            var createTimeBeginStr = $("#createTimeBeginStr").val();
            var createTimeEndStr = $("#createTimeEndStr").val();

            if (!params) {
                params = {};
            }
            params["createTimeBegin"] = createTimeBeginStr;
            params["createTimeEnd"] = createTimeEndStr;
            params["machineType"] = machineType;
            params["version"] = version;
            params["userName"] = userName;
            params["userId"] = userId;

            return params;
        }
        function sendUpdateMsg(id) {
            var option = {
                width: 400, content: "确定要发送？发送之后数据不能撤销!", confrimFunc: function (jboxObj) {
                    var param ={"id":id};
                    var obj = tools.requestRs("/machine/sendUpdateMsg", param, 'POST');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "发送成功", autoClose: 2000, className: "success"});
                        //doQuery();
                    } else {
                        if (obj.msg)
                            jbox_notice({content: obj.msg, autoClose: 2000, className: "error"});
                        else
                            jbox_notice({content: "发送失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
        function sendSelect() {
            var option = {
                width: 400, content: "确定要发送？发送之后数据不能撤销!", confrimFunc: function (jboxObj) {
                    var param ={"ids":getselectedId()};
                    var obj = tools.requestRs("/machine/sendSelect", param, 'POST');
                    if (obj.success) {
                        jboxObj.close();
                        if (obj.msg)
                            jbox_notice({content: obj.msg, autoClose: 2000, className: "success"});
                        else
                            jbox_notice({content: "发送成功", autoClose: 2000, className: "success"});
                    } else {
                        if (obj.msg)
                            jbox_notice({content: obj.msg, autoClose: 2000, className: "error"});
                        else
                            jbox_notice({content: "发送失败", autoClose: 2000, className: "error"});
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
        function doQuery(flag) {
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
        //获取选中ID
        function getselectedId() {
            var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            var id = "";
            for (var i = 0 ;i < ids.length;i++) {
                id+= ids[i] + ",";
            }
            return id;
        }
    </script>
</head>
<body>
<div class="container" style="width:86%;float: left">
    <h2 > 机器管理 </h2>
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label class="control-label">机器类型:</label>
            <select   name="machineType" id="machineType">
                <option value=""></option>
                <option value="android">android</option>
                <option value="ios">ios</option>
            </select>
            <label class="control-label">版本号:</label>
            <input type="text" class="form-control" name="version" id="version" autocomplete="off" placeholder="版本号"
                   placeholder="" style="width: 220px;display: inline-block;">

            <label class="control-label">用户ID:</label>
            <input type="text" class="form-control" name="userId" id="userId" autocomplete="off" placeholder="用户ID"
                   placeholder="" style="width: 220px;display: inline-block;">

            <label class="control-label">用户名称:</label>
            <input type="text" class="form-control" name="userName" id="userName" autocomplete="off" placeholder="用户名称"
                   placeholder="" style="width: 220px;display: inline-block;">

            <label>登录时间：</label>
            <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'createTimeEndStr\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'createTimeBeginStr\')}'})"
                    style="width: 120px;display: inline-block;"/>

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-info" onclick="sendSelect();">
                <i class=" glyphicon glyphicon-search"></i> 发送
            </button>
        </form>
    </div>
    <table id="table-bootstrap"
           border="0" cellpadding="4" cellspacing="0"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
</html>
