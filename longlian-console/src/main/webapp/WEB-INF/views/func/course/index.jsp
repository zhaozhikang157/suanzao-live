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
    <link rel="stylesheet" href="${ctx}/web/res/dataTable/dataTables.min.css"/>
    <script src="${ctx}/web/res/dataTable/dataTables.min.js" />
    <script>

    </script>
</head>
<script src="${ctx}/web/res/js/func/course/courseList.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/course/course.css"/>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        单节课列表
    </h2>

    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <label>开课时间：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>创建时间：</label>
            <input type="text" class="Wdate form-control" id="cbeginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'cendTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="cendTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'cbeginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>课程/直播名称：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程/直播名称"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>课程ID：</label>
            <input type="text" class="form-control" name="id" autocomplete="off" placeholder="讲课ID"
                   placeholder="" style="width: 110px;display: inline-block;">
            <label>课程状态：</label>
            <select class="form-control" id="status" name="status"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">上线</option>
                <option value="1">下线</option>
            </select>
            <label>课程分类：</label>
            <select class="form-control" id="courseType" name="courseType"
                    style="display: inline-block;width: auto">
            </select>
            <label>直播方式：</label>
            <select class="form-control" id="liveWay" name="liveWay"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">视频直播</option>
                <option value="1">语音直播</option>
            </select>
            <label>是否是录播：</label>
            <select class="form-control" id="isRecorded" name="isRecorded"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">不是</option>
                <option value="1">是</option>
            </select>
            <label>是否是系列课：</label>
            <select class="form-control" id="isSerier" name="isSerier"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">不是</option>
                <option value="1">是</option>
            </select>
           <%-- <label>排序：</label>
            <select class="form-control" id="sortType" name="sortType"
                    style="display: inline-block;width: auto">
                <option value="">-请选择-</option>
                <option value="1">创建时间</option>
                <option value="2">人工评分</option>
            </select>--%>
            <label>收费类型：</label>
            <select class="form-control" id="isFree" name="isFree"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="1">收费</option>
                <option value="0">免费</option>
            </select>
            <label>显示状态：</label>
            <select class="form-control" id="isHide" name="isHide"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="1">已隐藏</option>
                <option value="0">已显示</option>
            </select>
            <label>转播课类型：</label>
            <select class="form-control" id="isRelayCourse" name="isRelayCourse"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="1">转播课</option>
                <option selected value="0">原始课</option>
            </select>
            <label>转播市场显示状态：</label>
            <select class="form-control" id="isRelayHide" name="isRelayHide"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="1">隐藏</option>
                <option value="0">显示</option>
            </select>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
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
           data-show-pagination-switch="true" style="width: 2000px;"
           class="table table-striped table_list table-bordered"
            >
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
