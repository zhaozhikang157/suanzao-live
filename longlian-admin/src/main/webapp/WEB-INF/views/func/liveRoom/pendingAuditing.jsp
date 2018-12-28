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
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script >
        function doInit(){

            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveRoom/getPendingList",
                classes: 'table table-hover',
                height:getHeight(),
                toolbar:"#toolbar",
                pageSize:20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'createTime',
                        title: '时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'name',
                        title: '名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'appName',
                        title: '用户名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobile',
                        title: '手机',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'remark',
                        title: '备注',
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
                                '<a href="javascript:void(0)" onclick="setPending(' + row.id +');">'+'审核</a>&nbsp;&nbsp;&nbsp;',
                            ].join('');
                        }
                    }]
            });
        }
        //传递的参数
        function queryParams(params) {
            var name = $("#name").val().trim();  //名称
            var mobile = $("#mobile").val().trim();    //手机号
            var beginTime = $("#beginTime").val();  //起始时间
            var endTime = $("#endTime").val();    //结束时间
            if(!params){
                params = {};
            }
            params["name"] = name;
            params["mobile"] = mobile;
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
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
         *点击查询
         */
        function  doQuery(flag){
            if(flag){
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
            }else{
                $('#table-bootstrap').bootstrapTable('refresh', {query:  queryParams});
            }
        }
        /**
         * 创建或者更新员工，弹出框
         */
        function setPending(id){
            var title = "审核处理";
            var url = "/liveRoom/setPending?id=" + id;
            bsWindow(url, title, {
                height: 600, width: 700, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json) {
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
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        直播室审核
    </h2>
    <div id="toolbar">
        <form class="" id="form1" name="form1">
            <label>名称:：</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <label>手机号:：</label>
            <input type="text" class="form-control" name="mobile" id="mobile" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <label>按时间查询：</label>
            <input  type="text" class="Wdate form-control" id="beginTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})" style="width: 150px;display: inline-block;"/>
            至<input  type="text" class="Wdate form-control" id="endTime"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})" style="width: 150px;display: inline-block;"/>
            <button type="button" class="btn btn-info"  onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true">
    </table>
</div>
</body>
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first=new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth()+1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#beginTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
