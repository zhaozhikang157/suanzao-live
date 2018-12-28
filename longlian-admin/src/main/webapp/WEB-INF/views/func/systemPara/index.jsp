<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
    <script >
        $(document).ready(function(){
            doInit();
        });
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/systemParaController/getList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '名称',
                        align: 'center',
                        valign: 'middle',
                        sortable:true
                    },
                    {
                        field: 'code',
                        title: '代码',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'value',
                        title: '参数值',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'describe',
                        title: '描述',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter:function(value,row){
                            return [
                                '<a href="javascript:void(0)" onclick="createOrUpdate(' + row.id + ');">'+'编辑</a>&nbsp;&nbsp;&nbsp;',
                                '<a href="javascript:void(0)" onclick="deleteById(' + row.id + ');">'+'删除</a>'
                            ].join('');
                        }
                    }]
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
            var name = $("input[name='name']").val().trim();
            if(!params){
                params = {};
            }
            params["name"] = name;
            return params;
        }
        function refresh(){
            $('#table-bootstrap').bootstrapTable('refresh', {query:{a:1,b:2}});
        }
        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });
        function getHeight() {
            return $(window).height()-6 - $('h2').outerHeight(true);
        }
        function detailFormatter(index, row) {
            var html = [];
            $.each(row, function (key, value) {
                html.push('<p><b>' + key + ':</b> ' + value + '</p>');
            });
            return html.join('');
        }

        /**
         *  批量删除
         */
        function getIdSelections() {
            var ids =  $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            if(ids.length<1){
                jbox_notice({content:"请选择删除项",autoClose:2000 ,className:"warning"});
                return false;
            }

            var option = {width:400,content:"确定要删除所选中的数据，删除后将不可恢复？",confrimFunc:function(){
                deleteSelectionsById(ids);
            },
                draggable: 'title',
                closeButton: 'box',
                animation: {
                    open: 'slide:left',
                    close: 'slide:right'
                }
            };
            jboxObj=   jbox_confirm(option);
        }

        /**
         * 获取所有选中的，并删除
         * */
        function deleteSelectionsById(ids){
           // 删除
            var param={ids:ids.join(",")};
            var obj = tools.requestRs("/systemParaController/deleteById" , param , 'post');
            if(obj.success){
                        jbox_notice({content:"删除成功",autoClose:2000 ,className:"success"});
                        jboxObj.close();
                        $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
            }else{
                jbox_notice({content:"请选择删除项",autoClose:2000 ,className:"success"});
            }
        }


        /**
         *点击查询
         */
        function  doQuery(flag){
        	if(flag){
        		$('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
        	}
            $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
        }
        /**
        * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id){
            var title = "添加资源";
            if(id) title = "编辑资源";
            var url = "/systemParaController/toAddOrUpdate?id=" + id;
            bsWindow(url,title,{height:330,width:550,submit:function(v,h){
                var cw = h[0].contentWindow;
                cw.doSave(function(json){
                    if(json.success){
                        window.BSWINDOW.modal("hide");
                        jbox_notice({content:"保存成功",autoClose:2000 ,className:"success"});
                        $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
                    }else{
                        alert( json.msg || "保存出错");
                    }
                    console.log(json);
                });
            }});
        }


        /**
         * 删除选中项
         * */
        var jboxObj;
        function deleteById(id){
            var option = {width:400,content:"确定要删除所选中的数据，删除后将不可恢复？",confrimFunc:function(){
                delById(id);
            },
                draggable: 'title',
                closeButton: 'box',
                animation: {
                    open: 'slide:left',
                    close: 'slide:right'
                }
            };
          jboxObj=   jbox_confirm(option);

        }

        /**
        * 根据ID删除
         */
        function delById(id){
            var param={ids:id};
            var obj = tools.requestRs("/systemParaController/deleteById" , param , 'post');
            if(obj.success){
                jbox_notice({content:"删除成功",autoClose:2000 ,className:"success"});
                jboxObj.close();
                $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
            }
        }

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:86%;float: left;">
    <h2>
        系统参数
    </h2>
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button"   class="btn btn-primary"  onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"  ></i> 新增
            </button>
            <button type="button"  class="btn btn-danger" onclick="getIdSelections();">
                <i class="glyphicon glyphicon-remove"></i> 批量删除
            </button>
            <input type="text" class="form-control" name="name" autocomplete="off" placeholder="名称" placeholder="" style="width: 220px;display: inline-block;">
            <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>
</body>
</html>
