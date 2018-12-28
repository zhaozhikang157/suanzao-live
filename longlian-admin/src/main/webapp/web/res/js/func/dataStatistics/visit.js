
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
    },grid: {
        left: '8%',
        right: '0',
        bottom: '1%',
        containLabel: true
    },

    toolbox: {
        show : true,
        feature : {
            dataView : {show: true, readOnly: false},
            magicType : {show: true, type: ['line', 'bar']},
            restore : {show: true},
            saveAsImage : {show: true}
        }
    },
    xAxis: [
        {
            type: 'category',
            data: []
        }
    ],

    yAxis: {
        type: 'value'
    },

    series: [
        {
            name: 'pv',
            type: 'bar',
            smooth: true,
            data: [],
            markPoint : {
            data : [
                {type : 'max', name: '最大值'},
                {type : 'min', name: '最小值'}
            ]
        },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
            //data: [liveChannelUsingCount]  //用户活跃量数据
        }, {
            name: 'uv',
            type: 'bar',
            color:'#2f4bdc',
            smooth: true,
            data: [],
            markPoint : {
                data : [
                    {type : 'max', name: '最大值'},
                    {type : 'min', name: '最小值'}
                ]
            },
            markLine : {
                data : [
                    {type : 'average', name: '平均值'}
                ]
            }
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

