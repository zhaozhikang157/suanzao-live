<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="roomFunc">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title> 功能列表</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
   
    <script>
        var roomId = ${id};
        var liveRoomParam = {roomId:roomId}
        var obj ="";
        var funcStr = [];
        $(function(){
             obj = tools.requestRs("/liveRoom/getRoomFuncList",liveRoomParam,'get');
            if(obj.success){
                if (obj.data.length > 0){
                    $.each(obj.data, function (index, item) {
                        funcStr.push(item.funcCode);
                    })
                }
            }else{
                jbox_notice({content: obj.msg, autoClose: 2000, className: "error"});
            }
        })
        
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveRoom/getAuthorizeList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle',
                        formatter : stateFormatter
                    },
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'funcName',
                        title: '功能名',
                        align: 'center',
                        valign: 'middle'
            }]
            });
            //对应的函数进行判断；
            function stateFormatter(value, row, index) {
                        if (contains(funcStr,row.funcCode)){
                            return {
                                disabled : false,//设置是否可用
                                checked : true//设置选中
                            };
                  
                }
                return value;
            }
        }
        function contains(arr, obj) {
            var i = arr.length;
            while (i--) {
                if (arr[i] === obj) {
                    return true;
                }
            }
            return false;
        }
        //传递的参数
        function queryParams(params) {
            var name = $("input[name='name']").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            return params;
        }
        function getHeight() {
            return $(window).height()-6 - $('h2').outerHeight(true);
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
    </script>
</head>
<script>
  
    //获取选中ID
    function getselectedId(){
        var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
            return row.funcCode;
        });
        return ids;
    }

    var isSaving = false;
    function sureAuthorize() {
        if (isSaving) {
            return false;
        }
        isSaving = true;
        var funcCodes = getselectedId();
//        if(funcCodes==null || funcCodes==""){
//            jbox_notice({content: "请选择要授权的功能", autoClose: 2000, className: "error"});
//            return;
//        }
        var option = {
            width: 400, content: "确定要授权吗?", confrimFunc: function (jboxObj) {
                var param = "funcCodes=" + funcCodes+"&roomId="+roomId ;
                var obj = tools.requestRs("/liveRoom/sureAuthorize", param, 'post');
                if (obj.success) {
                    window.parent.closeWindows(obj); //调用父页面方法关闭窗口
                } else {
                    jbox_notice({content: "授权失败", autoClose: 2000, className: "error"});
                }
            }
        };
        jbox_confirm(option);
    }
</script>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-top: 15px">
        功能列表
    </h2>

    <div id="toolbar">
        <div class="footerBtn">
            <button type="button" class="btn btn-success passBtn"  onclick="sureAuthorize()">
                <i class=" glyphicon glyphicon-ok"></i> 通过
            </button>
        </div>
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
