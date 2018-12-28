<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>员工管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
        var LOGIN_ID = ${loginId};
        $(document).ready(function() {
            doInit();
        });
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/user/findAllUser",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                onLoadSuccess:onLoadSuccessFunc,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'state',
                        checkbox: true,
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'userId',
                        title: '用户名',
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
                        field: 'idCard',
                        title: '角色',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'status',
                        title: '是否授权登录',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:green'>启用</span>"
                            } else {
                                return "<span style='color:red'>禁用</span>"
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if(row.userId=='admin'){
                                return [

                                ].join('');
                            }
                            if(row.id==LOGIN_ID){
                                return[
//                                    '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a> '
                                ].join('');
                            }
                            if(row.userId!='admin'){
                                return [
                                    '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a>  ',
                                    '<a class="remove" href="javascript:void(0)" title="删除" onclick="deleteById(' + row.id + ');">删除</a>  '
                                ].join('');
                            }
                        }
                    }]
            });
        }

        function onLoadSuccessFunc(){
            //按字段选中
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"userId", values:["admin"]});
            $("#table-bootstrap").bootstrapTable("disabledCheckBy", {field:"id", values:[LOGIN_ID]});
        }

        function deleteById(ids) {
            if(ids==null || ids==""){
                jbox_notice({content: "请选择要删除的用户!", autoClose: 2000, className: "error"});
                return;
            }
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids + "&status=2";
                    var obj = tools.requestRs("/user/deleteByIds", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
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

        /*
         * 禁止登录操作
         * */
        function prohibitLogin() {
            var ids = getselectedId();
            var status = getStatus();
            if(ids==null || ids==""){
                jbox_notice({content: "请选择要禁止登录的用户!", autoClose: 2000, className: "error"});
                return;
            }
            if(status=='0'){
                jbox_notice({content: "该用户已被禁止登录!", autoClose: 2000, className: "error"});
                return;
            }
            var option = {
                width: 300, content: "确定要禁止登录？", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids + "&status=0";
                    var obj = tools.requestRs("/user/deleteByIds", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "禁止成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "禁止失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
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
        /**
         * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加员工"
            if (id) title = "编辑员工";
            var url = "/user/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 500, width: 550, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                        }
                    });
                }
            });
        }

        //获取选中ID
        function getselectedId(){
            var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            return ids;
        }

        //获取选中的用户名
        function getUserId(){
            var userId = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.userId
            });
            return userId;
        }

        //获取选中用户的状态情况
        function getStatus(){
            var status = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.status
            });
            return status;
        }

        /*
         * 密码重置操作
         * */
        function passwordReset() {
            var ids = getselectedId();
            if(ids==""||ids==null){
                jbox_notice({content: "请选择要重置密码的用户!", autoClose: 3000, className: "error"});
                return;
            }
            var option = {
                width: 400, content: "确定要重置密码？", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids;
                    var obj = tools.requestRs("/user/passwordReset", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "重置密码成功!", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "重置密码失败!", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }

        /*
         * 授权登录
         * */
        function authorizationLogin() {
            var ids = getselectedId();
            var status = getStatus();
            if(ids==null || ids==""){
                jbox_notice({content: "请选择要授权登录的用户!", autoClose: 3000, className: "error"});
                return;
            }
            if(status=='1'){
                jbox_notice({content: "该用户已授权登录!", autoClose: 5000, className: "error"});
                return;
            }
            var option = {
                width: 300, content: "确定授权登录？", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids + "&status=1";
                    var obj = tools.requestRs("/user/deleteByIds", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "授权成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "授权失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
    </script>

</head>
<body onload="doInit();">
<div class="container" style="width:86%;float: left;">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        员工管理
    </h2>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <div class="left" style="float: left">
                <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);" >
                    <i class="glyphicon glyphicon-plus"></i> 新建员工
                </button>
                <input type="text" class="form-control" name="name" autocomplete="off" placeholder="请输入姓名/用户名"
                       placeholder="" style="width: 220px;display: inline-block;">
                <button type="button" class="btn btn-info" onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
                <button type="button" class="btn btn-success" onclick='authorizationLogin()'>
                    <i class="glyphicon glyphicon-ok"></i> 授权登录
                </button>
            </div>
        </form>
        <button type="button" class="btn btn-info" onclick="passwordReset();">
            <i class="glyphicon glyphicon-refresh"></i> 密码重置
        </button>
        <button type="button" class="btn btn-danger" onclick='prohibitLogin()'>
            <i class="glyphicon glyphicon-alert"></i> 禁止登陆
        </button>
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
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>


</body>
</html>
