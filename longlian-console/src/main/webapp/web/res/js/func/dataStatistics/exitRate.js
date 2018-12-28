
var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);


var app = {};
option = {
    title: {
        text: '退出率统计',
        subtext: '退出率'
    },
    tooltip: {  
        trigger: 'axis'
    },
    legend: {
        data: ['退出率']
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
            name: '退出率',
            type: 'line',
            smooth: true,
            data: [],
            itemStyle: {
                normal: {
                    label: {
                        show:true,
                        position: 'top',
                        textStyle: {
                            color: 'green'
                        },
                        formatter: function(params) {
                            if(params.value && params.value != 0 && params.value != '0'){
                                return params.value + '%'
                            }else{
                                return '';
                            }
                        }
                    },
                    color:'#ee8b3f'
                }
            }
        }
    ]
};
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

