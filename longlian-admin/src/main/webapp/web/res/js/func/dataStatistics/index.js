var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);
var app = {};
option = {
    title: {
        text: '访问来源统计', subtext: '访问量'
    },
    tooltip: {  
        trigger: 'axis'
    },
    legend: {
        data: ['微信朋友圈', '微信朋友','总访问量']
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
            name: '微信朋友圈',
            type: 'bar',
            smooth: true,
            color:'#bd6728',
            data: []
        }, {
            name: '微信朋友',
            type: 'bar',
            smooth: true,
            data: []
        },
        {
            name: '总访问量',
            type: 'line',
            smooth: true,
           /* color:'#69bac3',*/
            data: []
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

