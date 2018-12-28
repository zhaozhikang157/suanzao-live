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
        $(document).ready(function() {
            doInit();
        });
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/flowManagementController/getFlowManagements",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                columns: [
                    {
                        field: 'publishUrl',
                        title: '推流地址',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'appName',
                        title: '直播间ID',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'streamName',
                        title: '课程ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'publishTime',
                        title: '推流时间',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'domainName',
                        title: '流加速域名',
                        align: 'center',
                        valign: 'middle'
                    }, {
                        field: 'userNum',
                        title: '在线人数',
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
                                '<a href="javascript:void(0)" onclick="liveAddBlacklist(' + row.appName +','+ row.streamName + ');">' + '加入黑名单</a>&nbsp;&nbsp;&nbsp;',
                                '<a href="javascript:void(0)" onclick="geturls();">' + '直播地址</a>'
                            ].join('');
                        }
                    }
                ]
            });
        }
        function geturls(ss){
            alert("敬请期待")
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
         * 设置禁用
         * */
        var jboxObj;
        function liveAddBlacklist(appName,streamName) {
            var option = {
                width: 400, content: "确定要加入黑名单吗？", confrimFunc: function () {
                    var param = {appName: appName,streamName:streamName}
                    var obj = tools.requestRs("/flowManagementController/liveAddBlacklist", param, 'post');
                    if (obj.success) {
                        jbox_notice({content: "成功加入黑名单!", autoClose: 2000, className: "success"});
                        jboxObj.close();
                        $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                    }else{
                        jbox_notice({content: "加入黑名单失败!", autoClose: 2000, className: "error"});
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
<div class="container" style="width:86%;float: left;">
    <h2>
        正在推流
    </h2>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
            >
    </table>
</div>
</body>
</html>
