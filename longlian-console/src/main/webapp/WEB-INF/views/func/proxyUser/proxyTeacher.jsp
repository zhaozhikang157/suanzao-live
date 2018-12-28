<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>代理老师</title>
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
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/proxyUser/getProxyUserList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'gender',
                        title: '性别',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:red'>男</span>"
                            } else {
                                return "<span style='color:green'>女</span>"
                            }
                        }
                    },
                    {
                        field: 'status',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',  formatter: function (value, row) {
                        return [
                            '<a href="javascript:void(0)" id="proxyUser" onclick="sureProxyTeacher(' + row.appId + ');">'+'确认</a>'
                        ].join('');
                    }
                    }]
        });
        }

        function sureProxyTeacher(id) {
            var option = {
                width: 500, content: "确定设置为代理老师吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id,userId:${id}};
                    var obj = tools.requestRs("/proxyUser/sureProxyTeacher", param, 'post');
                    if (obj.success) {
                        if(obj.code=="1111") {
                            jboxObj.close();
                           alert(obj.msg);
                            window.parent.BSWINDOW.modal("hide");
                            window.parent.document.location.reload();
                        }else {
                            jboxObj.close();
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            window.parent.BSWINDOW.modal("hide");
                            window.parent.document.location.reload();
                        }
                      } 
                    else
                        {
                            jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                        }
                }
            };
            jbox_confirm(option);
            

        }

        //
//        if(json.success){
//            window.BSWINDOW.modal("hide");
//            jbox_notice({content:"保存成功",autoClose:2000 ,className:"success"});
//            $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
//        }else{
//            alert( json.msg || "保存出错");
        
        
        function deleteById(id) {
            var option = {
                width: 500, content: "确定删除吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/userCount/deleteById", param, 'post');
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
            var status=0;  //终端类型
//            var createTimeBeginStr = $("#createTimeBeginStr").val();
//            var createTimeEndStr = $("#createTimeEndStr").val();
//            var appMobile = $("input[name='mobile']").val();//用户手机
            if(!params){
                params = {};
            }
            params["status"] = status;
//            params["startDate"] = createTimeBeginStr;
//            params["endDate"] = createTimeEndStr;
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
        代理老师
    </h2>
 <%--   <div id="toolbar">

            <form class="form-horizontal" id="form1" name="form1">
                <div class="left" style="float: left">
                    <label>终端类型:</label>
                    <select id="machineType" class="form-control" style="display: inline-block;width: 120px;" name="machineType">
                        <option selected value="">全部</option>
                        <option  value="android">android</option>
                        <option  value="ios">ios</option>
                    </select>
                </div>
                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
        </form>

    </div>--%>
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
