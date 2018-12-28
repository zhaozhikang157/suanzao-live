<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/liveRoom/getLiveRoomList",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '名称',
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
                        field: 'appName',
                        title: '用户名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'liveRoomNo',
                        title: '直播间编号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'autoCloseTime',
                        title: '自动关闭时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            var str = "<span onclick='showSpan(this)'>"
                                    + "<span class='updateSpan' style='display:none '>"
                                    + "<input type='text' title='增加人数' value='0'/><input type='button' value='修改' onclick='updateAutoCloseTime(this , " + row.id + ")'/></span>"
                                    + "<span class='showSpan'>"
                                    + value + "</span></span>";
                            return str;
                        }
                    },
                    {
                        field: 'dataCounts',
                        title: '总流量数',
                        align: 'center',
                        valign: 'middle',
                        sortable:true
                    },
                    {
                        field: 'reviewCounts',
                        title: '回看流量数',
                        align: 'center',
                        valign: 'middle',
                        sortable:true
                    },
                    {
                        field: 'reduceDataCounts',
                        title: '欠流量费',
                        align: 'center',
                        valign: 'middle',
                        sortable:true
                    },
                    {
                        field: 'isShow',
                        title: '是否允许显示',
                        align: 'center',
                        valign: 'middle',
                        sortable:true,
                        formatter:function(value, row){
                            if(value=='0'){return "允许";}else{
                                return '禁止';
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        width:'800',
                        formatter: function (value, row) {
                            if (row.status == 1) {
                                return [
                                    '<a class="like" href="javascript:void(0)" title="打印" onclick="print(' + row.appId + ');">打印</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="流量充值详情" onclick="buyFlowInfo(' + row.id + ');">流量充值详情</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="流量消耗详情" onclick="useFlowInfo(' + row.id + ');">流量消耗详情</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="授权" onclick="authorize(' + row.id + ');">授权</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="禁用" onclick="disable(' + row.id + ');">禁用</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="OSS管理" onclick="ossdelobj(' + row.id + ');">OSS管理</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="OSS管理" onclick="isShowNo(' + row.id + ');">允许显示直播</a>  ',
                                    '<a class="like" href="javascript:void(0)" title="OSS管理" onclick="isShowOff(' + row.id + ');">禁止显示直播</a>  '
                                ].join('');
                            }
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

        function buyFlowInfo(roomId){
            window.location.href = "/liveRoom/buyFlowInfo?roomId="+roomId;
        }
        
        function useFlowInfo(roomId){
            window.location.href = "/liveRoom/useFlowInfo?roomId="+roomId;
        }
        function ossdelobj(roomId){
            window.location.href = "/liveRoom/ossPage?roomId="+roomId;
        }
        
        function disable(roomId) {
            var title = "直播间禁用";
            var url = "/liveRoom/toDisableRoom?id=" + roomId;
            bsWindow(url, title, {
                height: 300, width: 700, buttons: []
            });
        }
        function authorize(roomId) {
            var title = "授权功能列表";
            var url = "/liveRoom/toAuthorize?id="+roomId;
            bsWindow(url, title, {
                height: 500, width: 700, buttons: []
            });
        }
        function showSpan(obj) {
            $(obj).find("span").eq(0).show();
        }

        function updateAutoCloseTime(obj, id) {
            var input = $(obj).prev();
            var updateValue = input.val();
            //判断是不是数字
            if (isNaN(updateValue) || parseInt(updateValue) < 0) {
                jbox_notice({content: "输入的不是正整数或小于0!", autoClose: 2000, className: "error"});
                return;
            }
            $.ajax({
                type: "GET",
                url: "/liveRoom/updateAutoCloseTime?id=" + id + "&updateValue=" + updateValue,
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

        function isShowNo(id){
            isShow(id,0);
        }
        function isShowOff(id){
            isShow(id,1);
        }

        function isShow(id, isShow) {
            $.ajax({
                type: "GET",
                url: "/liveRoom/updateIsShow?roomId=" + id + "&isShow=" + isShow,
                success: function (data) {
                    if (data.success) {
                        jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                        refresh();
                    } else {
                        jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
                        refresh();
                    }
                }
            })
        }

        //传递的参数
        function queryParams(params) {

            var name = $("#name").val().trim();  //名称
            var roomid = $("#roomid").val().trim();  //名称
            var mobile = $("#mobile").val().trim();    //手机号
            var sortType = $("select[name='sortType'] option:selected").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            params["id"] = roomid;
            params["mobile"] = mobile;
            params["sortType"] = sortType;
            return params;
        }
        function refresh() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
        }
        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });
        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }
        function detailFormatter(index, row) {
            var html = [];
            $.each(row, function (key, value) {
                html.push('<p><b>' + key + ':</b> ' + value + '</p>');
            });
            return html.join('');
        }
        //打印
        function print(id) {
            var title = "打印";
            var url = "/liveRoom/print?id=" + id;
            bsWindow(url,title,{height:550,width:600,buttons:[
                {classStyle:"btn btn-default" ,name:"关闭",clickFun:function(name ,bs){
                    window.BSWINDOW.modal("hide");
                }}
            ]});
        }

        function closeWindows(obj) {
            if (obj.success) {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: "授权成功", autoClose: 2000, className: "success"});
                doQuery();
            } else {
                window.BSWINDOW.modal("hide");
                jbox_notice({content: "系统错误", autoClose: 2000, className: "warning"});
                doQuery();
            }

        }
        /**
         *点击查询
         */
        function doQuery(flag) {
            if (flag) {
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
            } else {
                $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
            }
        }

        /**
         * 清空OSS 批量
         */
        function delOSSAll(){
            var yf = $("select[id=yuefen]").val();
            if(yf==""||yf==undefined){
                jbox_notice({content: "请选择清空日期!", autoClose: 2000, className: "error"});
                return;
            }
            var yuefen = {yuefen:yf};
            var obj = tools.requestRs("/course/delOSSAll",yuefen,'post');
            if(obj.success){
                jbox_notice({content: "清除成功", autoClose: 2000, className: "success"});
            }
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        直播间列表
    </h2>

    <div id="toolbar">
        <form class="" id="form1" name="form1">
            <label>直播间ID：</label>
            <input type="text" class="form-control" name="roomid" id="roomid" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <label>名称：</label>
            <input type="text" class="form-control" name="name" id="name" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <label>手机号：</label>
            <input type="text" class="form-control" name="mobile" id="mobile" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <select class="form-control" id="sortType" name="sortType"
                    style="display: inline-block;width: auto">
                <option value="">-请选择-</option>
                <option value="1">总流量数</option>
                <option value="2">回看流量数</option>
                <option value="3">欠流量费</option>
            </select>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <select class="form-control" id="yuefen" name="yuefen"
                    style="display: inline-block;width: auto">
                <option value="">-请选择-</option>
                <option value="2">2个月前</option>
                <option value="3">3个月前</option>
                <option value="6">6个月前</option>
            </select>
            <button type="button" class="btn btn-info" onclick="delOSSAll();">
                <i class=" glyphicon glyphicon-search"></i> 清空OSS
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
           data-show-pagination-switch="true"
            style="width: 2000px;">
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
    $("#beginTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
