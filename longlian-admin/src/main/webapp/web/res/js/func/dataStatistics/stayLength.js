
var dom = document.getElementById("leftbox");
var myChart = echarts.init(dom);


var app = {};
option = {
    title: {
        text: '停留时长统计',
        subtext: '停留时长'
    },
    tooltip: {  
        trigger: 'axis'
    },
    legend: {
        data: ['停留时长(秒)']
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
            name: '停留时长',
            type: 'line',
            color:'#2f4bdc',
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
                            if(params.value){
                                return  userTime(params.value);
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
function userTime(uTime){
    if (!uTime || uTime == '0.00'  || uTime == '0') {
        return '';
    }
    var result = "";
    var minutes = parseInt(uTime);
    if (uTime >= 60) {
        var sec = parseInt(minutes / 60);
        minutes =  minutes - sec * 60 ;
        result += sec + "分" + minutes + "秒";
        return result;
    }

    return  uTime+'秒';
}

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

