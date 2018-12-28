<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>流量管理管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>

    <script>
        $(document).ready(function() {
            doInit();
        });
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/dataChargeLevel/getDataChargeLevelList",
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
                        field: 'amount',
                        title: '流量',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value >= 1024) {
                                value = value/1024;
                                value = value.toFixed(2);
                                return "<span style='color:red'>"+value+"T</span>"
                            } else {
                                return "<span style='color:green'>"+value +"GB</span>"
                            }
                        }
                    },
                    {
                        field: 'origPrice',
                        title: '原价',
                        align: 'center',
                        valign: 'middle'
                    }, 
                    {
                        field: 'prefPrice',
                        title: '优惠价',
                        align: 'center',
                        valign: 'middle'
                    }, 
                    {
                        field: 'invalidDateUnit',
                        title: '失效周期',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (row.invalidDate == "0") {
                                return "<span style='color:red'>永久</span>"
                            } else {
                                var temp = "天"
                                if (value == '1') {
                                    temp = '月';
                                } else if (value == '2') {
                                    temp = '年';
                                }

                                return "<span style='color:green'>"+row.invalidDate + temp +"</span>"
                            }
                        }
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
                        field: 'isRetail',
                        title: '零售',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "<span style='color:red'>不是</span>"
                            } else {
                                return "<span style='color:green'>是</span>"
                            }
                        }
                    },
                    {
                        field: 'isHot',
                        title: '热门',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "<span style='color:red'>不是</span>"
                            } else {
                                return "<span style='color:green'>是</span>"
                            }
                        }
                    },
                    {
                field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                            formatter:function(value,row){
                                return [
                                    '<a class="like" href="javascript:void(0)" title="修改" onclick="createOrUpdate(' + row.id + ');">修改</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="修改" onclick="deleteById(' + row.id + ');">删除</a>  '
                                ].join('');
                            }
                    }]
        });
        }
      
        function deleteById(ids) {
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "ids=" + ids;
                    var obj = tools.requestRs("/dataChargeLevel/deleteByIds", param, 'post');
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
            var machineType=$("#machineType option:selected").val();  //终端类型
//            var createTimeBeginStr = $("#createTimeBeginStr").val();
//            var createTimeEndStr = $("#createTimeEndStr").val();
//            var appMobile = $("input[name='mobile']").val();//用户手机
            if(!params){
                params = {};
            }
            params["machineType"] = machineType;
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
        function createOrUpdate(id) {
            var title = "添加流量级别";
            if (id) title = "编辑流量级别";
            var url = "/dataChargeLevel/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 400, width: 600, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: json.msg, autoClose: 2000, className: "success"});
                            doQuery();
                        } else {
                            jbox_notice({content: json.msg, autoClose: 2000, className: "error"});
                        }
                    });
                }
            });
        }
    </script>

</head>
<body onload="doInit();">
<div class="container" style="width:86%;float: left;">
    <h2>
        流量管理
    </h2>
  <div id="toolbar">

            <form class="form-horizontal" id="form1" name="form1">
              <%--  <div class="left" style="float: left">
                    <label>终端类型:</label>
                    <select id="machineType" class="form-control" style="display: inline-block;width: 120px;" name="machineType">
                        <option selected value="">全部</option>
                        <option  value="android">android</option>
                        <option  value="ios">ios</option>
                    </select>
                </div>
                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>--%>
                  <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                      <i class="glyphicon glyphicon-plus"></i> 新建
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
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered">
    </table>
</div>


</body>
</html>
