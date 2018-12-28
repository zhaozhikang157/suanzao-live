<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<%
    String website  = CustomizedPropertyConfigurer.getContextProperty("website");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>邀请码列表</title>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/css/style.css">
    <script src="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script  src="${ctx}/web/res/pc/ZeroClipboard.js"></script>
    <script>
        var id = ${id};
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'get',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/inviCode/getInviCodeItem.user?id="+id,
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'inviCode',
                        title: '邀请码',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'useTime',
                        title: '使用状态',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if(value == null || value == ''){
                                return "未使用";
                            }else{
                                return "已使用";
                            }
                        }
                    },
                    {
                        field: 'useName',
                        title: '使用人',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'useTime',
                        title: '使用时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if(value == null || value == ''){
                                return '';
                            }
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm")
                        }
                    }
                ]
            });
//            initCopyToClipboard();
        }

        //传递的参数
        function queryParams(params) {
            if (!params) {
                params = {};
            }
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

        //导出
        function importExcel(){
            window.location.href = "/inviCode/importExcelInviCodeItem?id="+id;
        }

        /**
         *初始化复制链接
         * @param id
         */

        <%--function initCopyToClipboard(){--%>
            <%--var clip = new ZeroClipboard.Client();--%>
            <%--clip.setHandCursor(true);--%>
            <%--clip.setText("<%=website%>/weixin/inviCode?inviCodeId=" + id);--%>
            <%--clip.glue("copyToClipboard_");--%>
            <%--clip.addEventListener( "complete", function(){--%>
                <%--alert("复制成功！");--%>
            <%--});--%>
        <%--}--%>
    </script>
    <style>
        #form1 label{
            margin-right: 70px;
        }
    </style>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:30px">
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label>课程名称:${inviCodeDto.liveTopic}</label>
            <label>开课时间:${inviCodeDto.remark}</label>
            <label>批次:${id}</label>
            <label>邀请码使用数量:${inviCodeDto.theUseOfNum}</label>
            <label>有效期:${inviCodeDto.termOfValidity}</label>
            <label>复制链接:${inviCodeDto.copyHref}</label>
            <button type="button" class="btn btn-info" onclick="importExcel();" style="margin-right: 18px">
                导出邀请码为表格
            </button>
            <%--<button type="button" class="btn btn-info"  id="copyToClipboard_">--%>
                 <%--复制邀请码链接--%>
            <%--</button>--%>
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
<script>

</script>
</html>
