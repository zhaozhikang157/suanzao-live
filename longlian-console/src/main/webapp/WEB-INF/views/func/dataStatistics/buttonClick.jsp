<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>按钮点击次数统计</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
    <style>
        #leftbox{
            width: 1000px;
            height: 500px;
            padding: 20px;
            float: left;
            background: white !important;
        }
        #main{
            width: 500px;
            height: 500px;
            padding: 20px;
            float: left;
            background: white !important;
        }
    </style>
</head>


<body >

    <div class="container" style="width:97%;margin-top:10px">
        <h2>访按钮点击次数统计</h2>
        <div id="toolbar" style="padding-top: 5px">
            <form class="form-horizontal" id="form1" name="form1">
                <label>时间:</label>
                <input type="text" class="Wdate form-control" id="beginTime" value="${beginTime}"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                       style="width: 120px;display: inline-block;"/>
                至<input type="text" class="Wdate form-control" id="endTime" value="${endTime}"
                        onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                        style="width: 120px;display: inline-block;"/>
                <div>
                <label>按钮:</label>
                    <c:forEach  var="button" items="${buttons}">
                        <button type="button" class="btns" value="${button.type}" onclick="doQueryDate('${button.type}', ${button.hasChild});">
                        <i class=" glyphicon glyphicon-search"></i> ${button.name}
                    </button>
                     </c:forEach>
                    </div>
            </form>
        </div>
        <div id="leftbox">

        </div>
        <div id="main" style="display: none">

        </div>
    </div>


</body>
<script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/echarts.common.min.js"></script>
<script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/buttonClick.js"></script>
<script>
    var dom1 = document.getElementById("main");
    var dom = document.getElementById("leftbox");
    var myChart = echarts.init(dom);
    var objs = [];
    var option = {
        title: {
            text: '按钮点击次数统计',
            subtext: '按钮点击次数'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: []
        },
        toolbox: {
            show: true,
            feature: {
                magicType: {show: true, type: ['stack', 'tiled']},
                saveAsImage: {show: true}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: [],
            triggerEvent: true
        },
        yAxis: {
            type: 'value'

        },

        series: []
    };
    /**
     * 获取统计数据
     * @returns {*}
     */
    function doQueryDate(objectValue , hasChild) {
        dom1.style.display = 'none';
        var count;
        var beginTime = $("#beginTime").val();  //起始时间
        var endTime = $("#endTime").val();    //结束时间
        $.ajax({
            type: "POST",
            async: false,
            dataType: 'json',
            url: "/dataStatistics/getButtonClick",
            data:{
                objectValue:objectValue,beginTime:beginTime,endTime:endTime
            },
            success: function (obj) {
                if (obj.success) {
                      datas =  obj.data.datas;
                    objs = [];
                    option.xAxis.data = obj.data.keys;
                    option.legend.data = [];
                    option.series = [];

                    for (var i = 0 ;i < datas.length ;i++) {
                        var buttonRef = datas[i];
                        var objKey = buttonRef.objKey;
                        var tip  = buttonRef.name;
                        if (objKey == objectValue) {
                            tip += "合计";
                        }
                        objs[i] = objKey;
                        option.legend.data[i] = tip;
                        option.series[i] =  {
                            name: tip,
                            type: 'line',
                            smooth: true,
                            data: buttonRef.values,
                            button:objKey
                        };
                    }
                    myChart.setOption(option, true);
                } else {
                    jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                }
            }
        });
        return count;

    }
    this.doQueryDate('${first.type}', ${first.hasChild});
  
    

    var myChart1 = echarts.init(dom1);
    var option1 = {
        title : {
            text: '当天点击次数详情',
            subtext: '',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: []
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    myChart.on('click', function (params) {
        dom1.style.display = 'none';
        if (params.value == '0') {
            return ;
        }
        $.ajax({
            type: "POST",
            async: false,
            dataType: 'json',
            url: "/dataStatistics/getButtonClickDetail",
            data:{
                objectValue:objs[params.seriesIndex] , dateStr:params.name
            },
            success: function (obj) {
                if (obj.success) {
                    option1.series[0].data = [];
                    option1.legend.data = [];
                    option1.title.subtext = obj.msg;

                    var list = obj.data;

                    for (var i = 0 ;i < list.length ;i++) {
                        var obj2 = list[i];
                        option1.series[0].data[i] = obj2;
                        option1.legend.data[i] = obj2.name;
                    }
                    myChart1.setOption(option1, true);
                    dom1.style.display = '';
                } else {
                    jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                }
            }
        });
    });
</script>

</html>
