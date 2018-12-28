<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>直播室课程消息管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <style>
        .bootstrap-table{clear: both}
    </style>
    <script>
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/course/getCourseMessageList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [

                    {
                        field: 'courseId',
                        title: '课程编号',
                        align: 'center',
                        valign: 'middle'
                    },

                    {
                        field: 'liveTopic',
                        title: '课程名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'chatRoomId',
                        title: '聊天室id',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'fromNick',
                        title: '发送人昵称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'msgTime',
                        title: '发送消息时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'replyName',
                        title: '回复人姓名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'msgType',
                        title: '消息类型',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'attach',
                        title: '消息内容',
                        align: 'center',
                        valign: 'middle'
                    },
//                    {
//                field: '_opt_',
//                        title: '操作',
//                        align: 'center',
//                        valign: 'middle',
//                        formatter:function(value,row){
//                    return [
//                                '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">'+'编辑</a>&nbsp;&nbsp;&nbsp;',
//                        '<a href="javascript:void(0)" onclick="deleteById(' + row.courseId + ');">'+'删除</a>'
//                    ].join('');
//                }
//            }
 ]
        });
        }

        function deleteById(id) {
            var option = {
                width: 500, content: "确定删除吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    alert(id)
                    var obj = tools.requestRs("/course/deleteById", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        //传递的参数
        function queryParams(params) {
            var courseId = $("input[name='courseId']").val().trim();
            var liveTopic = $("input[name='liveTopic']").val().trim();
            var attach = $("input[name='attach']").val().trim();
            if(!params){
                params = {};
            }

            params["courseId"] = courseId;
            params["liveTopic"] = liveTopic;
            params["attach"] = attach;
//            params["mobile"] = appMobile;
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

    </script>

</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        直播室课程消息管理
    </h2>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <div class="left" style="float: left ;margin-top: -4px">

                <label  class="control-label">课程编号：</label>
                <input type="text" class="form-control" name="courseId" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">

                <label  class="control-label">课程名称：</label>
                <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">

                <label  class="control-label">消息内容：</label>
                <input type="text" class="form-control" name="attach" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">
            </div>

            <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                <i class="glyphicon glyphicon-search"></i> 查询
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
