<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>访问数统计</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
    <style>
        #leftbox{
            width: 800px;
            height: 500px;
            padding: 20px;
            float: left;
            background: white !important;
        }
    </style>
</head>
<body >

<div class="container" style="">
    <h2>访问数统计</h2>
    <div id="toolbar" style="padding-top: 5px">
        <form class="form-horizontal" id="form1" name="form1">
            <label>时间:</label>
            <input type="text" class="Wdate form-control" id="beginTime" value="${beginTime}"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime" value="${endTime}"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>页面:</label>
            <select class="form-control" id="objectId" name="objectId"
                    style="display: inline-block;width: auto">
                <option value="">站点</option>
                <c:forEach  var="url" items="${urls}"><option value="${url.id}">
                    <c:if test="${not empty url.pageName}">${url.pageName}</c:if>
                    <c:if test="${empty url.pageName}">${url.pageUrl}</c:if>
                </option></c:forEach>
            </select>
            <button type="button" class="btn btn-info" onclick="doQueryDate();">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>
    <div id="leftbox">

    </div>
</div>

</body>
<script>

    /**
     * 获取统计数据
     * @returns {*}
     */
    function doQueryDate() {
        var count;
        var objectId = $("select[name='objectId'] option:selected").val();
        var beginTime = $("#beginTime").val();  //起始时间
        var endTime = $("#endTime").val();    //结束时间
        $.ajax({
            type: "POST",
            async: false,
            dataType: 'json',
            url: "/dataStatistics/getVisits",
            data:{
                objectId:objectId,beginTime:beginTime,endTime:endTime
            },
            success: function (obj) {
                if (obj.success) {
                    var option = myChart.getOption();
                    option.series[0].data = obj.data.pvMap.pvValues;
                    option.series[1].data = obj.data.uvMap.uvValues;
                    option.xAxis[0].data = obj.data.pvMap.pvKeys;
                    myChart.setOption(option, true);
                } else {
                    jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                }
            }
        });
        return count;

    }


</script>

<script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/echarts.common.min.js"></script>
<script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/visit.js"></script>


</html>
