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
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/courseComment/getList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [

                    {
                        field: 'courseId',
                        title: '课程ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'liveTopic',
                        title: '课程主题',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'appName',
                        title: '评论人',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'content',
                        title: '评论内容',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
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
                            return [
                                '<a href="javascript:void(0)" onclick="deleteById(' + row.id + ');">' + '删除</a>'
                            ].join('');
                        }
                    }]
            });
        }
        //传递的参数
        function queryParams(params) {
            var liveTopic = $("input[name='liveTopic']").val().trim();
            var appName = $("input[name='appName']").val().trim();
            var beginTime = $("#beginTime").val();  //起始时间
            var endTime = $("#endTime").val();    //结束时间
            if(beginTime!="")beginTime = beginTime +" 00:00:00";
            if(endTime!="")endTime=endTime +" 23:59:59";
            if (!params) {
                params = {};
            }
            params["liveTopic"] = liveTopic;
            params["appName"] = appName;
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
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
         * 设置禁用
         * */
        var jboxObj;
        function deleteById(id) {
            var option = {
                width: 400, content: "确定要删除该评论？", confrimFunc: function () {
                    toDeleteById(id);
                }
            };
            jboxObj = jbox_confirm(option);
        }

        /**
         * 根据ID设置禁用
         */
        function toDeleteById(id) {
            var param = {id: id};
            var obj = tools.requestRs("/courseComment/toDeleteById", param, 'POST');
            if (obj.success) {
                jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                jboxObj.close();
                $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
            } else {
                jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                jboxObj.close();
                $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
            }
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        课程评论
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">

            <label>课程主题：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>评论人：</label>
            <input type="text" class="form-control" name="appName" autocomplete="off" placeholder="评论人"
                   placeholder="" style="width: 220px;display: inline-block;">

            <label>按时间查询：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
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
           data-show-pagination-switch="true">
    </table>
</div>
</body>
</html>
