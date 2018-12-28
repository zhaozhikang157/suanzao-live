
//数据统计
//var count = getCountMap();  //统计数据a
//var liveChannelUsingTime = getData(count.keyList);
//var liveChannelUsingCount=getData(count.valueList);
var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);


var app = {};
option = {
    title: {
        text: '直播流统计',
        subtext: '正在使用直播流数量'
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: [  '直播流使用量']
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
        data: []
    },
    yAxis: {
        type: 'value'
    },
    
    series: [ 
        {
            name: '直播流使用量',
            type: 'line',
            smooth: true,
            data: []
            //data: [liveChannelUsingCount]  //用户活跃量数据
        }]
    
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
this.doQueryDate();
//这里用setTimeout代替ajax请求进行演示
//window.setInterval(function () {
//    $.get("/stat/getcountMap",function(responseTxt,statusTxt,xhr)
//    {
//        test=responseTxt;
//        if (obj.success) {
//            //更新数据
//            var option = myChart.getOption();
//            option.series[0].data = obj.data.liveChannelUsingCount;
//            myChart.setOption(option);
//        } else {
//            jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
//        }
//        if (statusTxt == "error")
//            alert("Error: " + xhr.status + ": " + xhr.statusText);
//    });
//},3000);

function refreshData(data){
    if(!myChart){
        return;
    }
    //更新数据
    var option = myChart.getOption();
    option.series[0].data = data;
    myChart.setOption(option);
}

/*

function getCountMap() {
    var count;
    $.ajax({
        type: "POST",
        async: false,
        dataType: 'json',
        url: "/stat/getcountMap",
        success: function (obj) {
            if (obj.success) {
                count=obj.data;
            } else {
                jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
            }
        }
    });
    return count;

}
*/



function getData(dataValue) {//获取统计数组
    
    var data = [];
    for (var i = 0; i < dataValue.length; i++) {
        data.push(dataValue[i] == undefined ? 0 : dataValue[i]);
    }
    return data;
}

function load() {
    doInit();
    $.ajax({
        url: "/stat/count",
        dataType: "json",
        type: "GET",
        async: false,
        success: function (result) {
            $("#dayNewUserCount").text(result.data.dayMap.dayNewUserCount);
            $("#dayNewTeacherCount").text(result.data.dayMap.dayNewTeacherCount);
            $("#dayPayCount").text(result.data.dayMap.daynewUserPayCounts);
            $("#dayActiveCount").text(result.data.dayMap.dayActiveCount);
            $("#dayPVCount").text(result.data.dayMap.dayPVCount);
            $("#dayPayPmtCount").text(result.data.dayMap.dayPayPmtCount);
            //当天用户付费率
            var daynewUserPayCounts = result.data.dayMap.daynewUserPayCounts;
            var dayNewUserCount = result.data.dayMap.dayNewUserCount;
            $("#dayPayRet").text(result.data.dayMap.dayPayRet)
            //当天用户留存率
            $("#dayretentionRet").text(result.data.dayMap.dayretentionRetPercent);

            $("#yestNewUserCount").text(result.data.oldMap.yestNewUserCount);
            $("#yestNewTeacherCount").text(result.data.oldMap.yestNewTeacherCount);
            $("#yestPVCount").text(result.data.oldMap.yestPVCount);
            $("#yestActiveCount").text(result.data.oldMap.yestActiveCount);
            $("#yestPayCount").text(result.data.oldMap.yestPayCount);
            $("#yestPayAmtCount").text(result.data.oldMap.yestPayAmtCount);

            var yestPayCount = result.data.oldMap.yestPayCount;
            var yestNewUserCount = result.data.oldMap.yestNewUserCount;

            $("#yestPayRet").text(result.data.oldMap.yestPayRet);
            $("#yestRetentionRet").text(result.data.oldMap.yestRetentionRetPercent);

            $("#weekNewUserCount").text(result.data.oldMap.weekNewUserCount);
            $("#weekNewTeacherCount").text(result.data.oldMap.weekNewTeacherCount);
            $("#weekPVCount").text(result.data.oldMap.weekPVCount);
            $("#weekActiveCount").text(result.data.oldMap.weekActiveCount);
            $("#weekPayCount").text(result.data.oldMap.weekPayCount);
            $("#weekPayAmtCount").text(result.data.oldMap.weekPayAmtCount);



            $("#weekPayRet").text(result.data.oldMap.weekPayRet);
            $("#weekRetentionRet").text(result.data.oldMap.weekRetentionRetPercent);


            $("#monthNewUserCount").text(result.data.oldMap.monthNewUserCount);
            $("#monthNewTeacherCount").text(result.data.oldMap.monthNewTeacherCount);
            $("#monthPVCount").text(result.data.oldMap.monthPVCount);
            $("#monthActiveCount").text(result.data.oldMap.monthActiveCount);
            $("#monthPayCount").text(result.data.oldMap.monthPayCount);
            $("#monthPayAmtCount").text(result.data.oldMap.monthPayAmtCount);

            $("#monthPayRet").text( result.data.oldMap.monthPayRet);
            $("#monthRetentionRet").text(result.data.oldMap.monthRetentionRetPercent);

            $("#yearNewUserCount").text(result.data.oldMap.yearNewUserCount);
            $("#yearNewTeacherCount").text(result.data.oldMap.yearNewTeacherCount);
            $("#yearPVCount").text(result.data.oldMap.yearPVCount);
            $("#yearActiveCount").text(result.data.oldMap.yearActiveCount);
            $("#yearPayCount").text(result.data.oldMap.yearPayCount);
            $("#yearPayAmtCount").text(result.data.oldMap.yearPayAmtCount);

            $("#yearPayRet").text(result.data.oldMap.yearPayRet);
            $("#yearRetentionRet").text(result.data.oldMap.yearRetentionRetPercent);



            //当天课程付费率
            var daynewCoursePayCount = result.data.dayMap.daynewCoursePayCount;
            var allCouseCount = result.data.dayMap.dayNewCourseCounts;

            $("#dayNewCourseCounts").text(result.data.dayMap.dayNewCourseCounts);
            $("#dayAllCoursePayCounts").text(result.data.dayMap.dayAllCoursePayCounts);
            $("#daykechengfufeilv").text(result.data.dayMap.coursePayRet);


            //开课数
            $("#dayAllPlatformCourseCounts").text(result.data.oldMap.dayAllPlatformCourseCounts);
            $("#yestAllPlatformCourseCounts").text(result.data.oldMap.yestAllPlatformCourseCounts);
            $("#weekAllPlatformCourseCounts").text(result.data.oldMap.weekAllPlatformCourseCounts);
            $("#monthAllPlatformCourseCounts").text(result.data.oldMap.monthAllPlatformCourseCounts);
            $("#yearAllPlatformCourseCounts").text(result.data.oldMap.yearAllPlatformCourseCounts);

            $("#yestNewCourseCount").text(result.data.oldMap.yestNewCourseCount);
            $("#yestAllCoursePayCount").text(result.data.oldMap.yestAllCoursePayCount);

            var yestAllCoursePayCount = result.data.oldMap.yestAllCoursePayCount;
            var yestNewCourseCount = result.data.oldMap.yestNewCourseCount;
            $("#yestNewCoursePayRet").text(result.data.oldMap.yestNewCoursePayRet);

            $("#weekNewCourseCount").text(result.data.oldMap.weekNewCourseCount);
            $("#weekAllCoursePayCount").text(result.data.oldMap.weekAllCoursePayCount);

            var weekAllCoursePayCount = result.data.oldMap.weekAllCoursePayCount;
            var weekNewCourseCount = result.data.oldMap.weekNewCourseCount;
            $("#weekNewCoursePayRet").text(result.data.oldMap.weekNewCoursePayRet);


            $("#monthNewCourseCount").text(result.data.oldMap.monthNewCourseCount);
            $("#monthAllCoursePayCount").text(result.data.oldMap.monthAllCoursePayCount);

            var monthAllCoursePayCount = result.data.oldMap.monthAllCoursePayCount;
            var monthNewCourseCount = result.data.oldMap.monthNewCourseCount;

            $("#monthNewCoursePayRet").text(result.data.oldMap.monthNewCoursePayRet);

            $("#yearNewCourseCount").text(result.data.oldMap.yearNewCourseCount);
            $("#yearAllCoursePayCount").text(result.data.oldMap.yearAllCoursePayCount);

            var yearAllCoursePayCount = result.data.oldMap.yearAllCoursePayCount;
            var yearNewCourseCount = result.data.oldMap.yearNewCourseCount;

            $("#yearNewCoursePayRet").text(result.data.oldMap.yearNewCoursePayRet);
        }
    });

}