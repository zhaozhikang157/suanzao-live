
//数据统计
//var count = getCountMap();  //统计数据a
//var liveChannelUsingTime = getData(count.keyList);
//var liveChannelUsingCount=getData(count.valueList);
var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);


var app = {};
option = {
    title: {
        text: '访问数统计',
        subtext: '访问数'
    },
    tooltip: {  
        trigger: 'axis'
    },
    legend: {
        data: ['pv', 'uv']
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
            name: 'pv',
            type: 'line',
            smooth: true,
            data: []
            //data: [liveChannelUsingCount]  //用户活跃量数据
        }, {
            name: 'uv',
            type: 'line',
            smooth: true,
            data: []
            //data: [liveChannelUsingCount]  //用户活跃量数据
        }
    ]
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}
this.doQueryDate();

function refreshData(data){
    if(!myChart){
        return;
    }
    //更新数据
    var option = myChart.getOption();
    option.series[0].data = data;
    myChart.setOption(option);
}

function getData(dataValue) {//获取统计数组
    
    var data = [];
    for (var i = 0; i < dataValue.length; i++) {
        data.push(dataValue[i] == undefined ? 0 : dataValue[i]);
    }
    return data;
}

