<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>直播鉴黄详情</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <style>
        /*.bootstrap-table{clear: both}*/
    </style>
    <script>
        $(document).ready(function() {
            doInit();
        });
        function doInit(){
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/yellowResult/getYellowResultList",
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
                        field: 'rate',
                        title: '分值',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'greenLabel',
                        title: '绿网建议值',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "<span>正常</span>"
                            } else if (value == '1') {
                                return "<span style='color:red'>暴恐敏感</span>"
                            }else{
                                return "<span style='color:red'>需要review</span>"
                            }
                        }
                    },
                    {
                        field: 'url',
                        title: '图片地址',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<img src='" + value + "' width='300' height='180'/>"
                        }
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                        }
                    }
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
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);

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
            var smallRate = $("input[name='smallRate']").val().trim();
            var bigRate = $("input[name='bigRate']").val().trim();

            var greenLabel = $("#greenLabel").find("option:selected").val();
            if(!params){
                params = {};
            }
//
            params["courseId"] = courseId;
            params["liveTopic"] = liveTopic;
            params["greenLabel"] = greenLabel;
            params["smallRate"] = smallRate;
            params["bigRate"] = bigRate;
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
<div class="container" style="width:86%;float: left;">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        直播鉴黄详情
    </h2>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <div class="left" style="float: left ;margin-top: -4px">

                <label  class="control-label">课程编号：</label>
                <input type="text" class="form-control" name="courseId" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">

                <label  class="control-label">课程名称：</label>
                <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">
                <label>绿网建议值:</label>
                <select id="greenLabel" class="form-control" style="display: inline-block;width: 120px;" name="machineType">
                    <option selected value="">全部</option>
                    <option  value="0">正常</option>
                    <option  value="1">暴恐敏感</option>
                    <option  value="2">需要review</option>
                </select>
                <label  class="control-label">分值：</label>
                <input type="text" class="form-control" name="smallRate" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">
                至<input type="text" class="form-control" name="bigRate" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">

                <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                    <i class="glyphicon glyphicon-search"></i> 查询
                </button>
            </div>



        </form>

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
