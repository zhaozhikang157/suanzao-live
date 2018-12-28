<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>版本管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script>
        $(document).ready(function(){
            doInit();
        });
        $(function() {
            $.ajax({
                type: "GET",
                url: "/mobileVersion/getVersionType",
                success: function (data) {
                    $.each(data.data.versionTypes, function (idxs, items) {
                        $("#versionType").append("<option value=" + items.value + ">" + items.text + "</option>");
                    });
                }
            })
        });

        $(function() {
            $.ajax({
                type: "GET",
                url: "/mobileVersion/getStateList",
                success: function (data) {
                    $.each(data.data.statuses, function (idxs, items) {
                        $("#status").append("<option value=" + items.id + ">" + items.name + "</option>");
                    });
                }
            })
        });
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/mobileVersion/findAll",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'name',
                        title: '版本名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'type',
                        title: '版本类型',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'versionNum',
                        title: '版本型号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'status',
                        title: '版本状态',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:green'>上线</span>"
                            } else {
                                return "<span style='color:red'>下线</span>"
                            }
                        }
                    },
                    {
                        field: 'userName',
                        title: '操作员',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'time',
                        title: '时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'isFoceUpdate',
                        title: '是否强制升级',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "1") {
                                return "<span style='color:red'>强制升级</span>"
                            } else {
                                return "<span style='color:green'>不强制升级</span>"
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a class="like" href="javascript:void(0)" title="编辑" onclick="createOrUpdate(' + row.id + ');">编辑</a>  ',
                            ].join('');
                        }
                    }]
            });
        }



        //传递的参数
        function queryParams(params) {
            var versionNum = $("input[name='versionNum']").val();
            var versionType = $("select[name='versionType'] option:selected").val();
            var status = $("select[name='status'] option:selected").val();
            if (!params) {
                params = {};
            }
            params["versionNum"] = versionNum;
            if(versionType!=""){
                params["versionType"] = versionType;
            }
            if(status!=""){
                params["status"] = status;
            }
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
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }
        /**
         * 创建或者更新员工，弹出框
         */
        function createOrUpdate(id) {
            var title = "添加版本";
            if (id) title = "编辑版本";
            var url = "/mobileVersion/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 600, width: 800, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    return cw.doSave(function (json) {
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

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:86%;float: left;">
    <h2> 版本管理 </h2>
    <div id="toolbar">
        <form class="form-inline" id="form1" name="form1">
            <div class="form-group">
                <label  class="control-label" >版本类型:</label>
                <select class="form-control" id="versionType" name="versionType">
                    <option value="">全部</option>
                </select>
            </div>
            <div class="form-group">
                <label  class="control-label" >版本状态:</label>
                <select class="form-control" id="status" name="status">
                    <option value="">全部</option>
                </select>
            </div>
            <div class="form-group">
                <label  class="control-label" >版本型号:</label>
                <input type="text" class="form-control" name="versionNum" autocomplete="off" placeholder="版本型号" placeholder="" style="width: 220px;display: inline-block;">
                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
                <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                    <i class="glyphicon glyphicon-plus"></i> 新建
                </button>
            </div>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>
</body>
</html>

