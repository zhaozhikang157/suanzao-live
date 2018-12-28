<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>老师查询</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script src="${ctx}/web/res/my97/calendar.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        var form3;
        var form4;
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/appUser/getTeacherList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: 'id',
                        align: 'center',
                        width:'260',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '姓名',
                        align: 'center',
                        width:'260',
                        valign: 'middle'
                    },
                    {
                        field: 'weixin',
                        title: '微信号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobile',
                        title: '手机号',
                        align: 'center',
                        width:'260',
                        valign: 'middle'
                    }, {
                        field: 'realName',
                        title: '真实姓名',
                        align: 'center',
                        valign: 'middle'
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
                    /*},
                    {
                        field: 'userType',
                        title: '身份',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == "0") {
                                return "<span style='color:green'>学生</span>"
                            } else if (value == "1") {
                                return "<span style='color:red'>老师</span>"
                            } else if (value == "2") {
                                return "<span style='color:red'>机构</span>"
                            }
                        }*/
                    },
                    {
                        field: 'city',
                        title: '所属城市',
                        align: 'center',
                        valign: 'middle'
                    }, {
                        field: 'openid',
                        title: '微信openID',
                        align: 'center',
                        width:'300',
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        valign: 'middle',
                        width:'300',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'teacherCreateTime',
                        title: '老师注册时间',
                        align: 'center',
                        valign: 'middle',
                        width:'300',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                        }
//                    , {
//                    field: '_opt_',
//                    title: '操作',
//                    align: 'center',
//                    valign: 'middle'
//
//                        ,formatter:function(value,row){
//
//                        if (row.status == "0" && row.teacherStatus == "0") {
//                            return [
//                                '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyUser(' + row.id + ');">'+'取消代理用户</a>&nbsp;',
//                                '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyTeacher(' + row.id + ');">' + '解除代理人关系</a>'
//                            ].join('');
//                        }
//
//                        if (row.status == "1" && row.teacherStatus == "1") {
//                            return [
//                                '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyUser(' + row.id + ');">'+'确认为代理人</a>',
//                                '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyTeacher(' + row.id + ');">' + '设置代理人关系</a>'
//                            ].join('');
//                        }
//
//
//                        if (row.status == "0") {
//                            var teacherStatus="";
//                            var status= '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyUser(' + row.id + ');">'+'取消代理用户</a>&nbsp;';
//                            if(row.teacherStatus == "0")
//                            {
//                                teacherStatus=   '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyTeacher(' + row.id + ');">' + '解除代理人关系</a>';
//                            }else{
//                                teacherStatus=    '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyTeacher(' + row.id + ');">' + '设置代理人关系</a>';
//                            }
//                            return [
//                                status+teacherStatus
//                            ].join('');
//                        }
//
//
//                        if (row.status == "1") {
//                            var teacherStatus="";
//                            var status= '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyUser(' + row.id + ');">'+'确认为代理人</a>&nbsp;';
//                            if(row.teacherStatus == "0")
//                            {
//                                teacherStatus=   '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyTeacher(' + row.id + ');">' + '解除代理人关系</a>';
//                            }else{
//                                teacherStatus=    '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyTeacher(' + row.id + ');">' + '设置代理人关系</a>';
//                            }
//                            return [
//                                status+teacherStatus
//                            ].join('');
//                        }
//
//
//                        if (row.teacherStatus == "0") {
//                            var status="";
//                            var teacherStatus= '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyTeacher(' + row.id + ');">' + '解除代理人关系</a>&nbsp;';
//                            if(row.status == "0")
//                            {
//                                status=  '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyUser(' + row.id + ');">'+'取消代理用户</a>';
//                            }else{
//                                status=   '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyUser(' + row.id + ');">'+'确认为代理人</a>';
//                            }
//                            return [
//                                status+teacherStatus
//                            ].join('');
//                        }
//                        if (row.teacherStatus == "1") {
//                            var status="";
//                            var teacherStatus=  '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyTeacher(' + row.id + ');">' + '设置代理人关系</a>&nbsp;';
//                            if(row.status == "0")
//                            {
//                                status=  '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyUser(' + row.id + ');">'+'取消代理用户</a>';
//                            }else{
//                                status=   '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyUser(' + row.id + ');">'+'确认为代理人</a>';
//                            }
//                            return [
//                                status+teacherStatus
//                            ].join('');
//                        }
//
//
//                        /*
//                                                if (row.status == "0") {
//                                                    return [
//                                                        '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyUser(' + row.id + ');">'+'已设置为代理人</a>'
//                                                    ].join('');
//                                                }else
//                                                {
//                                                    return [
//                                                        '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyUser(' + row.id + ');">'+'设置为代理人</a>'
//                                                    ].join('');
//
//                                                }
//                                                if (row.teacherStatus == "0") {
//                                                    return [
//                                                        '<a href="javascript:void(0)" id="proxyUser" onclick="removeProxyTeacher(' + row.id + ');">' + '已设置为代理人老师</a>'
//                                                    ].join('');
//                                                }else
//                                                {
//                                                    return [
//                                                        '<a href="javascript:void(0)" id="proxyUser" onclick="setProxyTeacher(' + row.id + ');">' + '设置为代理人老师</a>'
//                                                    ].join('');
//
//                                                }*/
//                 }
//
//                    }
                        
                ]
            });
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);
        //设置老师代理
        function setProxyTeacher(id){
            var title = ""
            var url = "/proxyUser/toProxyTeacher?id="+id;
            bsWindow(url,title,{height:600,width:1000,buttons: [
                {
                    classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
                }
            ]
            });
        }
        function removeProxyTeacher(id) {
            var option = {
                width: 500, content: "确定解除代理人老师吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/proxyUser/removeProxyTeacher", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
//                        $("#proxyUser").html("已设置");
//                        $("#proxyUser").css(disabled="disabled");
                        jbox_notice({content: "解除成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "解除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
        //传递的参数
        function queryParams(params) {
            var mobile = $("#mobile").val().trim();
            var name = $("#name").val().trim();
            var userType = $("#userType option:selected").val();
            var startTime = $("#createTimeBeginStr").val();
            var endTime = $("#createTimeEndStr").val();
            var createStartTime = $("#createTimeBegin").val();
            var createEndTime = $("#createTimeEnd").val();
            if (!params) {
                params = {};
            }
          
                params["startTime"] = startTime;
                params["endTime"] = endTime;
          
                params["createStartTime"] = createStartTime;
                params["createEndTime"] = createEndTime;
         
            params["mobile"] = mobile;
            params["name"] = name;
            params["userType"] = userType;
            return params;
        }

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }
        function setProxyUser(id) {
            var option = {
                width: 500, content: "确定设置为代理人吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/proxyUser/setProxyUser", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
//                        $("#proxyUser").html("已设置");
//                        $("#proxyUser").css(disabled="disabled");
                        jbox_notice({content: "设置成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "设置失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
        function removeProxyUser(id) {
                var option = {
                width: 500, content: "确定解除代理人吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/proxyUser/removeProxyUser", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
//                        $("#proxyUser").html("已设置");
//                        $("#proxyUser").css(disabled="disabled");
                        jbox_notice({content: "解除成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "解除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
        }
        /**
         *点击查询
         */
        function doQuery() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
        }



    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>老师查询</h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <label class="control-label">用户名:</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off"
                   placeholder="用户名"
                   placeholder="" style="width: 150px;display: inline-block;">
            <label class="control-label">手机号:</label>
            <input type="text" class="form-control" name="mobile" id="mobile" autocomplete="off"
                   placeholder="用户ID"
                   placeholder="" style="width: 150px;display: inline-block;">
           
           <%--   <label>创建时间：</label>
                <input type="text" class="Wdate form-control" id="createTimeBegin"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="createTimeEnd"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                        style="width: 120px;display: inline-block;"/>--%>
                <label>老师注册时间：</label>
                <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                        style="width: 120px;display: inline-block;"/>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
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
           data-show-pagination-switch="true" style="width:1600;">
    </table>
</div>
</body>
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#createTimeBeginStr").val(first)  //获取当月第一天
    $("#createTimeEndStr").val(lastDay);//默认时间为 当月 最后一天
    $("#createTimeBegin").val(first)  //获取当月第一天
    $("#createTimeEnd").val(lastDay);//默认时间为 当月 最后一天
    $("#teacherCreateTime").hide();
    $("#createTime").show();
    function selectUserType(){
        var userType = $("#userType option:selected").val();
        if(userType == "1" ){
            $("#teacherCreateTime").show();
            $("#createTime").hide();
        }else{
            $("#teacherCreateTime").hide();
            $("#createTime").show();
        }
    }
</script>
</html>
