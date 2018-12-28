<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程渠道统计</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/sys.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/jboxTools.js"></script>
    <style>
        .bootstrap-table{clear: both}
    </style>
    <script>
        var courseId = '${id}';
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/shareChannel/getChannelRecordList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: 'id',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '渠道名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'newChannelVisit',
                        title: '新增',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'allChannelVisit',
                        title: '渠道点击量',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'status',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',  formatter: function (value, row) {
                        return [
                            '<a href="javascript:void(0)" id="proxyUser" onclick="generateChannelLink(' + row.id + ');">生成渠道二维码</a>'
                        ].join('');
                    }
                    }]
        });
        }
        //传递的参数
        function queryParams(params) {
//            var status=0;  //终端类型
//            var createTimeEndStr = $("#createTimeEndStr").val();
            var name = $("input[name='name']").val();
            if(!params){
                params = {};
            }
            params["courseId"] = courseId;
//            params["endDate"] = createTimeEndStr;
            params["name"] = name;
           
            return params;
        }
        /**
         *点击查询
         */
        function  doQuery(flag){
            if(flag){
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
            }else{
                $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
            }
        }
        function onLoadSuccessFunc(){
            //按字段选中
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"userId", values:["admin"]});
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"id", values:[LOGIN_ID]});
        }
        function getHeight() {
            return $(window).height()-6 - $('h2').outerHeight(true);
        }
        //生成二维码
        function generateChannelLink(id){
           // window.location.href = "/shareChannel/generateWeiXinQRCode?courseId=" + courseId + "&channelId=" + id;
            var param = {courseId:courseId ,channelId: id };
            var obj = tools.requestRs("/shareChannel/generateWeiXinQRCode", param, 'post');
            if(obj.success){
                var url = obj.data;
                window.open(url);
            }else{
                alert(obj.msg);
            }
        }
    </script>

</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        课程渠道统计
    </h2>
  <div id="toolbar">

            <form class="form-horizontal" id="form1" name="form1">
                <div class="left" style="float: left">
                    <input type="text" class="form-control" name="name" autocomplete="off" placeholder="请输入渠道名称"
                           placeholder="" style="width: 220px;display: inline-block;">
                </div>
                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
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
    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true">
    </table>
</div>


</body>
</html>
