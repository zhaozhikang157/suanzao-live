<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>菜单管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/ztree/css/zTreeStyle/zTreeStyle.css">
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.core-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.excheck-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/js/jquery.ztree.exedit-3.5.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/ZTreeSync.js"></script>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/ztree/sort.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
        var zTreeObj;
        var selectedId = "";
        var selectedLevel = 0;
        var selectedpId = "";
        var oldName = "";
        $(document).ready(function() {
            doInit();
        });
        function doInit(){
            var url = "${requestContext.contextPath}/mRes/getAllCatalog";
            var config = {
                zTreeId: "ztree",
                requestURL: url,
                async:false,
                onClickFunc:setSelectedId,
                zTreeOnRename:zTreeOnRename,
                zTreeOnDblClick:onDblClick,
            };
            zTreeObj = ZTreeTool.config(config);
        }

        function setSelectedId(event, treeId, treeNode){
            selectedId = treeNode.id;
            selectedLevel = treeNode.level;
            selectedpId = treeNode.pId;
            if(selectedLevel==2){
                $("#tianjia").attr("disabled" , true);
            }else{
                $("#tianjia").attr("disabled" , false);
            }
            //加载排序菜单
            if(treeNode.isParent){
            	loadMenuSort(treeNode.id);
            }
        }

        function createOrUpdate(i) {
            var url;
            var title = "添加菜单";
            if(selectedId==""||selectedId==null ){
                url = "/mRes/createOrUpdateCatalog?id="+0+"&pId="+selectedId;
            } else {
            	title = "修改菜单";
            }
            if(i==0){   //添加
                url = "/mRes/createOrUpdateCatalog?id="+0+"&pId="+selectedId;
            }else{      //修改
                if(selectedpId==""||selectedId==null || selectedId == 0){
                    jbox_notice({content: "请不要选择根节点进行修改", autoClose: 2000, className: "error"});
                    return;
                }
                url = "/mRes/createOrUpdateCatalog?id="+selectedId+"&pId="+selectedpId;
            }
            bsWindow(url, title, {
                height: 400, width: 600, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            var newNodes = json.data;
                            window.BSWINDOW.modal("hide");
                            if(i==0){
                                addNode(newNodes);
                                jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            }else{
                                doInit();
                            }
                        } else {
                            alert(json.msg || "保存出错");
                        }
                    });
                }
            });
        }

        function removeNode(){
            var obj=  $.fn.zTree.getZTreeObj("ztree");
            var nodes = obj.getSelectedNodes();
            if(nodes.length==0){
                alert("请选择要移除的菜单!");
                return;
            }
            for (var i=0, l=nodes.length; i < l; i++) {
                obj.removeNode(nodes[i]);
            }
        }

        function deleteById(){
            if(selectedpId=="" ||selectedId==null || selectedId == 0){
                jbox_notice({content: "请选择非根节点进行删除!", autoClose: 2000, className: "error"});
                return;
            }
            var option = {
                width: 400, content: "确定要删除所选中的菜单，删除后将不可恢复？", confrimFunc: function (jboxObj) {
                    var param = "id=" + selectedId;
                    var obj = tools.requestRs("/mRes/deleteCalatlog", param, 'post');
                    if (obj.success) {
                        removeNode();
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        function addNode(newNodes){
            var treeObj = $.fn.zTree.getZTreeObj("ztree");
            var parentNodes = treeObj.getSelectedNodes();
            treeObj.addNodes(parentNodes[0], newNodes);
        }

        function editName(){
            var treeObj = $.fn.zTree.getZTreeObj("ztree");
            var nodes = treeObj.getSelectedNodes();
            oldName = nodes[0].name;
            treeObj.editName(nodes[0]);
        }

        function zTreeOnRename(event, treeId, treeNode, isCancel) {
            var id = treeNode.id ;
            var newName = treeNode.name;
            var param = "id="+id+"&newName="+newName;
            if(oldName!=newName){
                var obj = tools.requestRs("/mRes/updateCatalogName", param, 'post');
                if(!obj.success){
                    jbox_notice({content: "修改名字失败!", autoClose: 2000, className: "error"});
                }
            }
        }

        function onDblClick(event, treeId, treeNode) {
            editName();
        };

    </script>
</head>
<body onload="doInit()">
	<div class="container" style="width: 74%; float: left">
		<h2 style="margin-bottom: 20px; margin-top: 15px">菜单管理</h2>
		<form class="form-horizontal" id="form1" name="form1">
			<button type="button" class="btn btn-primary"
				onclick="createOrUpdate(0);" id="tianjia">
				<i class="glyphicon glyphicon-plus"></i> 添加菜单
			</button>
			<button type="button" class="btn btn-primary"
				onclick='createOrUpdate(1)'>
				<i class="glyphicon glyphicon-pencil"></i> 修改菜单
			</button>
			<button type="button" class="btn btn-danger" onclick='deleteById()'>
				<i class="glyphicon glyphicon-remove"></i> 删除菜单
			</button>
		</form>
		<br>
		<ul id="ztree" class="ztree"
			style="overflow: auto; background: #ffffff; min-height: 200px; width: 50%; float: left"></ul>

		<!-- 菜单排序 -->

		<div style="float: left; width: 50%">

			<TABLE id="Table1" height="100%" cellSpacing="0" cellPadding="0"
				width="100%" border="0">
				<TR>
					<TD vAlign="top">
						<TABLE id="Table4" cellSpacing="0" cellPadding="0" width="100%"
							border="0">
							<TR>
								<TD>

                                    <button class="btn btn-info upBtn" type="button" onmousedown="setTimeStart('up');" onmouseup="clearTimeout(x);"
                                            onclick="listObj=document.getElementById('forder');upListItem();clearTimeout(x);"
                                            ID="Button1" NAME="Button1">
                                        <i class="glyphicon glyphicon-arrow-up"></i> 向上
                                    </button>
                                    <button class="btn btn-info downBtn" type="button" onmousedown="setTimeStart('down');"
                                            onmouseup="clearTimeout(x);"
                                            onclick="listObj=document.getElementById('forder');downListItem();clearTimeout(x);"
                                            ID="Button2" NAME="Button2">
                                        <i class="glyphicon glyphicon-arrow-down"></i> 向下
                                    </button>
                                    <button class="btn btn-success" type="button" onclick="saveSortMenu();">
                                        <i class="glyphicon glyphicon-save"></i> 保存
                                    </button>
								</TD>
							</TR>
							<TR>
								<TD><SELECT id="forder" style="WIDTH: 304px; HEIGHT: 240px;margin-top: 20px;" size="15">
								<!-- 菜单顺序显示 -->
								</SELECT></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
		</div>
	</div>
</body>
</html>

