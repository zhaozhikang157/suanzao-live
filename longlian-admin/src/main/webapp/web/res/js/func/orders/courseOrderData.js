/*** ----------------------start--------------------*/
var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);
    function refreshData(data){
        if(!myChart){
            return;
        }
        //更新数据
        var option = myChart.getOption();
        option.series[0].data = data;
        myChart.setOption(option);
    }

    var app = {};
    option = {
        title: {
            text: '课程收入统计',
            subtext: ''
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['课程收入（元）']
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
            data: ['单节课','序列课']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '课程收入',
                barMaxWidth:30,//最大宽度
                type: 'bar',
                smooth: true,
                color:'#74aef9',
                data: [20,30]
            }
        ]
    };
    if (option && typeof option === "object") {

        myChart.setOption(option, true);
    }
    $(".selectDate").change(function(){
        getCourseDatas({"selectDate":$(this).val()})

    });
function getCourseDatas(param){
    $.ajax({
        type: 'POST',
        url: '/course/getCourseDataIncome',
        data: param,
        dataType: "json",
        success: function (data) {
            if(data.data){
                $(".singleCourseIncome").text(data.data.singleAmount);
                $(".seriesCourseIncome").text(data.data.seriesAmount);
                var datav = [];
                datav.push(data.data.singleAmount);
                datav.push(data.data.seriesAmount);
                //更新数据
                var option = myChart.getOption();
                option.series[0].data = datav;
                myChart.setOption(option);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            jsonObj = {code: 0,success:false ,data:"" , msg: "请求出错！"};
        }
    });
}