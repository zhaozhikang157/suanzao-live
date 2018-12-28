<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<%
    String website  = CustomizedPropertyConfigurer.getContextProperty("website");
%>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <title>邀请码列表</title>
    <link rel="stylesheet" href="${ctx}/web/res/pc/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/css/style.css">
    <script src="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script  src="${ctx}/web/res/pc/ZeroClipboard.js"></script>

    <style>

    </style>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'get',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/inviCode/getAllInviCodePage.user",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
//                onLoadSuccess:onLoadSuccessFunc,
                columns: [
                    {
                        field: '',
                        title: '批次',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a href="javascript:void(0)" onclick="jumpItem(' + row.id + ')">' +row.id+ '</a>'
                            ].join('');
                        }
                    },
                    {
                        field: 'liveTopic',
                        title: '课程名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'courseStartTime',
                        title: '开课时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm")
                        }
                    },
                    {
                        field: 'theUseOfNum',
                        title: '邀请码使用数量',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'termOfValidity',
                        title: '有效期',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'copyHref',
                        title: '复制链接',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: '',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<button style="width:138px;margin:10px" type="button" class="btn btn-primary primarys" onclick="importExcel(' + row.id + ');">导出邀请码为表格</button>'
                            ].join('');


                        }
                    }
                ]
            });
        }

//        function onLoadSuccessFunc(data){
//            for(var i = 0 ; i < data.rows.length ; i ++){
//               initCopyToClipboard(data.rows[i].id);
//            }
//
//        }

        //传递的参数
        function queryParams(params) {
            var liveTopic = $("input[name='courseName']").val();
            var id = $("input[name='id']").val();
            if (!params) {
                params = {};
            }
            params["liveTopic"] = liveTopic;
            params["codeUnion"] = id;
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
         *初始化复制链接
       * @param id
         */
        <%--function initCopyToClipboard(id){--%>
            <%--var clip = new ZeroClipboard.Client();--%>
            <%--clip.setHandCursor(true);--%>
            <%--clip.setText("<%=website%>/weixin/inviCode?inviCodeId=" + id);--%>
            <%--clip.glue("copyToClipboard_"  + id);--%>
            <%--clip.addEventListener( "complete", function(){--%>
                <%--alert("复制成功！");--%>
            <%--});--%>
        <%--}--%>

        //导出
        function importExcel(id){
            window.location.href = "/inviCode/importExcelInviCodeItem?id="+id;
        }

        function jumpInsertCode(){
            window.location.href = "/inviCode/insertCode";
        }

        function jumpItem(id){
            window.location.href = "/inviCode/jumpItem?id="+id;
        }

    </script>
    <style>

    </style>
</head>
<body onload="doInit();" >
<div class="container" style="width:97%;margin-top:30px">
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label>课程名称：</label>
            <input type="text" class="form-control" name="courseName" autocomplete="off"
                   placeholder="课程名称" style="width: 180px;display: inline-block;margin-right: 20px">
            <label>批次：</label>
            <input type="text" class="form-control" name="id" autocomplete="off"
                   placeholder="批次" style="width: 180px;display: inline-block;margin-right: 20px">

            <button style="margin-right: 18px" type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>

        <div style="margin-top: 15px;margin-bottom: 10px">
            <button type="button" class="btn btn-primary" onclick="jumpInsertCode();">
                生成邀请码
            </button>
        </div>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           <%--data-show-columns="true"--%>

    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true"
            >
    </table>
</div>


</body>

</html>
