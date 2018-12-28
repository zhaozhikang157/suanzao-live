<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>权限管理</title>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/jquery/jquery.js"></script>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jBox.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jbox-customer.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/ztree/css/zTreeStyle/zTreeStyle.css">
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.core-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.excheck-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.exedit-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/ZTreeSync.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${requestContext.contextPath}/web/res/jbox/js/jbox.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/jboxTools.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/sys.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/tools.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/menu/findAllRole.js"></script>
    <script>
        $(document).ready(function(){
            doInit();
        });
        var zTreeObj;
        var oldName = "";
        var roleId = "";
        function selectTree(id){
            roleId = id ;
            var url = "${requestContext.contextPath}/mRes/getAllMenu?roleId="+id;
            var config = {
                zTreeId: "ztree",
                requestURL: url,
                checkController:{
                    enable : true,
                    chkboxType: { "Y": "ps", "N": "s" }
                },
                async:false,
            };
            zTreeObj = ZTreeTool.config(config);
        };

        function getSelectIds(){
            var id = ZTreeTool.getSelectCheckedNodeIds(true,true);
            return id;
        }

        function saveRoleRes(){
            var ids = getSelectIds();
            if(ids==null || ids==""){
                jbox_notice({content: "请至少赋予一个菜单管理!", autoClose: 3000, className: "error"});
                return;
            }
            if(roleId=='0'){
                jbox_notice({content: "请选择角色!", autoClose: 3000, className: "error"});
                return;
            }
            var option = {
                width: 400, content: "确定要为该角色赋予所选择的菜单?", confrimFunc: function (jboxObj) {
                    var param = "Resids=" + ids + "&roleId=" + roleId;
                    var obj = tools.requestRs("/mRes/saveRoleRes", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                        selectTree(roleId);
                    } else {
                        jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        /*
        * 全选
        * */
        function checkAll(){
            var treeObj = $.fn.zTree.getZTreeObj("ztree");
            treeObj.checkAllNodes(true);
        }

        function checkNoutAll(){
            var treeObj = $.fn.zTree.getZTreeObj("ztree");
            treeObj.checkAllNodes(false);
        }
    </script>
    <style>
        .on{
            background: #E8E8E8;
        }
    </style>
</head>
<body onload="doInit()">
<div class="container" style="width:86%;float: left;">
<h2 style="margin-top: 15px;">
    权限管理
</h2>
    <div style="float: right;width:47%;margin-bottom: 20px;margin-right: 10px">
        <form style="float: left;">
            <button type="button" class="btn btn-primary" onclick="checkAll()">
                <i class="glyphicon glyphicon-ok-sign"></i>全勾选</button>
            <button type="button" class="btn btn-danger" onclick="checkNoutAll()">
                <i class="glyphicon glyphicon-remove-sign"></i>全取消</button>
        </form>
        <form style="float: right;margin-right: 4px" class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-primary" onclick="saveRoleRes();" id="tianjia">
                <i class="glyphicon glyphicon-ok"></i> 保存
            </button>
        </form>
    </div>
    <div style="clear: both"></div>
    <div class="leftbox" style="width: 48%;float: left;border: 1px solid #cccccc">
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
    <div>
        <ul id="ztree" class="ztree" style="overflow:auto ;width:48%; background:#ffffff;min-height: 200px;float: right;border: 1px solid #cccccc"></ul>
    </div>
    </div>
</body>
</html>

