<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>会员详情</title>
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
                url: "/appUser/getMemberDetailsList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'NAME',
                        title: '姓名',
                        align: 'center',
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
                    },
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
                        }
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
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '用户创建时间',
                        align: 'center',
                        valign: 'middle',
                        width:'180',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'teacherCreateTime',
                        title: '老师创建日期',
                        align: 'center',
                        valign: 'middle',
                        width:'180',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                        },
                    {
                        field: 'proportion',
                        title: '提现比率',
                        align: 'center',
                        valign: 'middle',
                        width:'180',
                        formatter: function (value, row) {
                            var str = "<span onclick='showSpan(this)'>"
                                    + "<span class='updateSpan' style='display:none '>"
                                    + "<input style='width: 40px;' type='text' title='修改' value='0'/><input type='button' value='修改' onclick='updateProportion(this , " + row.id + ")'/></span>"
                                    + "<span class='showSpan'>"
                                    + value + "</span>%</span>";
                            return str;
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        width:'180',
                        formatter: function (value, row) {
                            return [
                                '<a href="javascript:void(0)" onclick="toUpdatePwd(' + row.id + ');" >' + '修改密码</a>&nbsp;&nbsp;&nbsp;',
                                '<a href="javascript:void(0)" onclick="resetPwd(' + row.id + ');" >' + '重置密码</a>&nbsp;&nbsp;&nbsp;'
                            ].join('');
                        }
                    }
                ]
            });
            var css = { height: "94%"};
            $("#indexiframe" , parent.parent.document).css(css);
        }
        setTimeout(function(){
            css = { height: "93%"};
            $("#indexiframe" , parent.parent.document).css(css);
        },500);
        function toUpdatePwd(id){
            var title = ""
            var url = "/appUser/toUpdatePwd?id="+id;
            bsWindow(url,title,{height:500,width:700,buttons: [
                {
                    classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
                }
            ]
            });
        }
        function showSpan(obj) {
            $(obj).find("span").eq(0).show();
        }
        function closeWindows(obj) {
            if (obj.success) {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: "审核完毕", autoClose: 2000, className: "success"});
                doQuery();
            } else {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: "系统错误", autoClose: 2000, className: "warning"});
                doQuery();
            }

        }

        function updateProportion(obj, id) {
            var input = $(obj).prev();
            var addValue = input.val();
            var r = /^\+?[0-9][0-9]*$/;　　//正整数
            //判断是不是数字
            if (isNaN(addValue) || !r.test(addValue)||parseInt(addValue)<0 || parseInt(addValue)>100) {
                jbox_notice({content: "必需输入0-100的整数!", autoClose: 2000, className: "error"});
                return;
            }
            $.ajax({
                type: "GET",
                url: "/appUser/updateProportion?id=" + id + "&addCount=" + addValue,
                success: function (data) {
                    if (data.success) {
                        jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                        input.val(0);
                        $(obj).parent().next().text(data.data);
                        $(obj).parent().hide();
                    } else {
                        jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
                    }
                }
            })
        }

        function resetPwd(id) {
            var option = {
                width: 500, content: "确定重置密码吗？", confrimFunc: function (jboxObj) {
                    var param = {id: id};
                    var obj = tools.requestRs("/appUser/resetOrUpdatePwd", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "重置成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "重置失败", autoClose: 2000, className: "error"});
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
            if(userType == "1"){
                params["startTime"] = startTime;
                params["endTime"] = endTime;
            }else{
                params["createStartTime"] = createStartTime;
                params["createEndTime"] = createEndTime;
            }
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
    <h2> 会员详情 </h2>

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
            <label>身份类型:</label>
            <select class="form-control" id="userType" name="userType"
                    style="display: inline-block;width: auto" onchange="selectUserType()">
                <option value="">全部</option>
                <option value="0">学生</option>
                <option value="1">老师</option>
                <option value="2">机构</option>
            </select>
            <div id="createTime">
                <label>创建时间：</label>
                <input type="text" class="Wdate form-control" id="createTimeBegin"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="createTimeEnd"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                        style="width: 120px;display: inline-block;"/>
            </div>
            <div id="teacherCreateTime">
                <label>老师注册时间：</label>
                <input type="text" class="Wdate form-control" id="createTimeBeginStr"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="createTimeEndStr"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312)}'})"
                        style="width: 120px;display: inline-block;"/>
            </div>
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
           data-show-pagination-switch="true">
    </table>
</div>
</body>
<script>
    /*var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#createTimeBeginStr").val(first)  //获取当月第一天
    $("#createTimeEndStr").val(lastDay);//默认时间为 当月 最后一天
    $("#createTimeBegin").val(first)  //获取当月第一天
    $("#createTimeEnd").val(lastDay);//默认时间为 当月 最后一天*/
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
<link rel="stylesheet" href="${ctx}/web/res/style/css/course/course.css"/>
</html>
