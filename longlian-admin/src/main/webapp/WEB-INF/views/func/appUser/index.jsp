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
    </script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/appUser/getAllAppUserInfoPage",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'index',
                        title: '序号',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'photo',
                        title: '头像',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return '<img src="'+value+'" height="20" width="20">';
                        }
                    },
                    {
                        field: 'createTime',
                        title: '注册时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss');
                        }
                    },
                    {
                        field: 'userName',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobile',
                        title: '手机号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'gender',
                        title: '性别',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "男";
                            } else {
                                return "女";
                            }
                        }
                    },
                    {
                        field: 'city',
                        title: '地区',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userType',
                        title: '身份',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "学生";
                            } else if (value == '1') {
                                return "老师";
                            } else {
                                return "机构";
                            }
                        }
                    },
                    {
                        field: 'appUserType ',
                        title: '注册渠道',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "微信";
                            } else {
                                return "机构";
                            }
                        }
                    },
                    {
                        field: 'followCount',
                        title: '被关注数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'courseCount',
                        title: '课程数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'distributionNumber',
                        title: '分销人数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'payNumber',
                        title: '付费人数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'distributionIncome',
                        title: '分销收益',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'conversionRate',
                        title: '转化率',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'authentication',
                        title: '申请/认证',
                        align: 'center',
                        valign: 'middle'
                    },
                ]
            });
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);
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

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        用户管理
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            姓名:
            <input type="text" class="form-control" name="name" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            手机号:
            <input type="text" class="form-control" name="mobile" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            身份:
            <select id="userType">
                <option selected value="">全部</option>
                <option value="0">学生</option>
                <option value="1">老师</option>
                <option value="2">机构</option>
            </select>

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
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
           class="table table-striped table_list table-bordered"
            >
    </table>
</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
<script src="${ctx}/web/res/my97/calendar.js"></script>
<script src="${ctx}/web/res/my97/WdatePicker.js"></script>
<script src="${ctx}/web/res/my97/xdate.dev.js"></script>
</html>
