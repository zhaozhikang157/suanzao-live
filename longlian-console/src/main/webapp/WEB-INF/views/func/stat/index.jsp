<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>用户统计 </title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/reset.css">
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/stat/index.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <style>
    </style>
</head>
<body onload="load()">
<div class="wrap">
    <div class="topbox">
        <p>用户统计</p>
        <table class="toptable">
            <tr>
                <th>时间</th>
                <th>新增用户</th>
                <th>新增老师</th>
                <th>pv</th>
                <th>活跃用户</th>
                <th>新增付费人数</th>
                <th>付费金额</th>
                <th>付费率</th>
                <th>次留存</th>
            </tr>

            <tr>
                <td>今日</td>
                <td id="dayNewUserCount">0</td>
                <td id="dayNewTeacherCount">0</td>
                <td id="dayPVCount">0</td>
                <td id="dayActiveCount">0</td>
                <td id="dayPayCount">0</td>
                <td id="dayPayPmtCount">0</td>
                <td id="dayPayRet">0</td>
                <td id="dayretentionRet">0</td>
            </tr>
            <tr>
                <td>昨日</td>
                <td id="yestNewUserCount">0</td>
                <td id="yestNewTeacherCount">0</td>
                <td id="yestPVCount">0</td>
                <td id="yestActiveCount">0</td>
                <td id="yestPayCount">0</td>
                <td id="yestPayAmtCount">0</td>
                <td id="yestPayRet">0</td>
                <td id="yestRetentionRet">0</td>
            </tr>
            <tr>
                <td>本周</td>
                <td id="weekNewUserCount">0</td>
                <td id="weekNewTeacherCount">0</td>
                <td id="weekPVCount">0</td>
                <td id="weekActiveCount">0</td>
                <td id="weekPayCount">0</td>
                <td id="weekPayAmtCount">0</td>
                <td id="weekPayRet">0</td>
                <td id="weekRetentionRet">0</td>
            </tr>
            <tr>
                <td>本月</td>
                <td id="monthNewUserCount">0</td>
                <td id="monthNewTeacherCount">0</td>
                <td id="monthPVCount">0</td>
                <td id="monthActiveCount">0</td>
                <td id="monthPayCount">0</td>
                <td id="monthPayAmtCount">0</td>
                <td id="monthPayRet">0</td>
                <td id="monthRetentionRet">0</td>
            </tr>
            <tr>
                <td>本年</td>
                <td id="yearNewUserCount">0</td>
                <td id="yearNewTeacherCount">0</td>
                <td id="yearPVCount">0</td>
                <td id="yearActiveCount">0</td>
                <td id="yearPayCount">0</td>
                <td id="yearPayAmtCount">0</td>
                <td id="yearPayRet">0</td>
                <td id="yearRetentionRet">0</td>
            </tr>
        </table>
    </div>


    <div class="topbox">
        <p>课程统计</p>
        <table class="toptable">
            <tr>
                <th>时间</th>
                <th>新增单节课</th>
                <th>新增付费单节课</th>
                <th>平台开课数</th>
                <th>付费率</th>
            </tr>
            <tr>

                <td>今日</td>
                <td id="dayNewCourseCounts">0</td>
                <td id="dayAllCoursePayCounts">0</td>
                <td id="dayAllPlatformCourseCounts">0</td>
                <td id="daykechengfufeilv">0</td>

            </tr>
            <tr>
                <td>昨日</td>
                <td id="yestNewCourseCount">0</td>
                <td id="yestAllCoursePayCount">0</td>
                <td id="yestAllPlatformCourseCounts">0</td>
                <td id="yestNewCoursePayRet">0</td>

            </tr>
            <tr>
                <td>本周</td>
                <td id="weekNewCourseCount">0</td>
                <td id="weekAllCoursePayCount">0</td>
                <td id="weekAllPlatformCourseCounts">0</td>
                <td id="weekNewCoursePayRet">0</td>

            </tr>
            <tr>
                <td>本月</td>
                <td id="monthNewCourseCount">0</td>
                <td id="monthAllCoursePayCount">0</td>
                <td id="monthAllPlatformCourseCounts">0</td>
                <td id="monthNewCoursePayRet">0</td>

            </tr>
            <tr>
                <td>本年</td>
                <td id="yearNewCourseCount">0</td>
                <td id="yearAllCoursePayCount">0</td>
                <td id="yearAllPlatformCourseCounts">0</td>
                <td id="yearNewCoursePayRet">0</td>

            </tr>


        </table>

<%--
        <div id="leftbox">

        </div>--%>

    </div>

        <%--  <form class="form-horizontal" id="form1" name="form1">
                <label>时间:</label>
                <input type="text" class="Wdate form-control" id="beginTime"
                       onclick="WdatePicker({dateFmt:'HH:mm',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="endTime"
                        onclick="WdatePicker({dateFmt:'HH:mm',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <button type="button" class="btn btn-info" onclick="doQueryDate(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
         </form>--%>
    <!--增加  查询所有 或者指定周期列表-->
    <script src="${requestContext.contextPath}/web/res/js/func/stat/function.js"></script>
    <form class="form-horizontal" id="form1" name="form1">
    <label>时间：</label>
    <input type="text" class="Wdate form-control" id="beginTime"
           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
           style="width: 120px;display: inline-block;"/>
    至<input type="text" class="Wdate form-control" id="endTime"
            onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
            style="width: 120px;display: inline-block;"/>
        <button type="button" class="btn btn-info" onclick="doQuery(true);">
            <i class=" glyphicon glyphicon-search"></i> 查询
        </button>
        <button type="button" class="btn btn-info" onclick="importExcel();">
            <i class=" glyphicon glyphicon-search"></i> 导出
        </button>
    </form>
    <div>
        <table style="" id="table-bootstrap"
               data-pagination="true"
               data-side-pagination="server"
               data-page-list="[10, 20, 50, 100,ALL]"
               data-show-refresh="true"
               data-show-toggle="true"
               data-show-columns="true"
               data-show-pagination-switch="true" class="table table-hover">
        </table>
    </div>
    <style>
       /* .fixed-table-container{height: 400px;}*/
    </style>
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

<script>
    //doInit();
    /**
     * 获取统计数据
     * @returns {*}
     */
    function doQueryDate() {
        var count;
        var dayTime = $("select[name='dayTime'] option:selected").val();
        var beginTime = $("#beginTime").val();  //起始时间
        var endTime = $("#endTime").val();    //结束时间
        $.ajax({
            type: "POST",
            async: false,
            dataType: 'json',
            url: "/stat/getcountMap",
            data:{
                dayTime:dayTime,beginTime:beginTime,endTime:endTime
            },
            success: function (obj) {
                if (obj.success) {
                    var option = myChart.getOption();
                    option.series[0].data = obj.data.valueList;
                    option.xAxis[0].data = obj.data.keyList;
                    myChart.setOption(option, true);
                } else {
                    jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                }
            }
        });
        return count;

    }


</script>


<script src="${requestContext.contextPath}/web/res/js/func/stat/echarts.common.min.js"></script>
<script src="${requestContext.contextPath}/web/res/js/func/stat/index.js"></script>


</html>
