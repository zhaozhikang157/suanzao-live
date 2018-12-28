function doInit() {
    // getCourseTypes();
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/dataStatistics/getCourseDetailClickDataStatistics",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
            {
                field: 'id',
                title: '课程ID',
                align: 'center',
                width:200,
                valign: 'middle'
            },
            {
                field: 'liveTopic',
                title: '课程名称',
                align: 'center',
                width:460,
                valign: 'middle'
            },
            {
                field: 'isSeriesCourse',
                title: '课程类型',
                align: 'center',
                width:200,
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        if (row.seriesCourseId != 0) {
                            return "系列课" + row.seriesCourseId + "的单节课"
                        }
                        return "单节课"
                    } else if (value == '1') {
                        return "系列课"
                    }
                }
            },
            {
                field: 'appUserName',
                title: '讲课老师',
                align: 'center',
                width:220,
                valign: 'middle'
            },
            {
                field: 'startTime',
                title: '开课时间',
                align: 'center',
                valign: 'middle',
                width:460,
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            },
            {
                field: 'vCount',
                title: '访问次数',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle'
            },
            /* {
             field: 'pClickCount',
             title: '访问人数',
             align: 'center',
             width:200,
             valign: 'middle',//pClickCount
             formatter: function (value, row) {
             var pcount=value+row.ppClickCount+row.peClickCount;
             return pcount;
             }
             },*/

            {
                field: 'visitClickCount',
                title: '购买课程点击次数(点击率)',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle',
                formatter: function (value, row) {
                    // if(value!=null && value!=""){
                    var str=value+"("+row.buyCourseClickRet+")"
                    return str;
                    // }

                }
            },
            {
                field: 'visitpClickCount',
                title: '观看点击次数(点击率)',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle',
                formatter: function (value, row) {
                    var str=value+"("+row.buyCoursepClickRet+")"
                    return str;

                }
            },
            {
                field: 'visiteClickCount',
                title: '返回点击次数(点击率)',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle',
                formatter: function (value, row) {
                    var str=value+"("+row.buyCourseeClickRet+")"
                    return str;

                }
            },

            /* {
             field: 'buyCount',
             title: '购买人数',
             align: 'center',
             width:200,
             valign: 'middle'
             },
             {
             field: 'realBuyCount',
             title: '实际购买人数',
             align: 'center',
             width:200,
             valign: 'middle'
             },*/

        ]
    });
    var css = { height: "94%"};
    $("#indexiframe" , parent.document).css(css);
}
setTimeout(function(){
    css = { height: "93%"};
    $("#indexiframe" , parent.document).css(css);
},500);
Date.prototype.format = function (format) {
    var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
};
$(window).resize(function () {
    $('#continuousAlarmTable').bootstrapTable('resetView');
});
function getHeight() {
    return $(window).height() - 6 - $('h2').outerHeight(true);
}
/**
 *点击查询
 */
function doQuery() {
    $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
}
//传递的参数
function queryParams(params) {
    // var id = $("input[name='id']").val().trim();
    var liveTopic = $("input[name='liveTopic']").val().trim();
    var appUserName = $("input[name='appUserName']").val().trim();
    var beginDate = $("#beginDate").val();  //起始时间
    var endDate = $("#endDate").val();    //结束时间
    var portType = $("select[name='portType'] option:selected").val();    //状态
    if (!params) {
        params = {};
    }
    params["liveTopic"] = liveTopic;
    params["appUserName"] = appUserName;
    params["beginDate"] = beginDate;
    params["endDate"] = endDate;
    params["portType"] = portType;
    return params;
}
/*function getCourseTypes() {
 var obj = tools.requestRs("/courseType/getCourseTypesList", 'get');
 if (obj.success) {
 $("#courseType").html("<option value=''>全部</option>");
 $.each(obj.data, function (idxs, items) {
 $("#courseType").append("<option value=" + items.id + ">" + items.name + "</option>");
 });
 }
 }*/
//导出Excel
function importExcel() {
    var params = queryParams();
    var str = "liveTopic=" + params.liveTopic + "&appUserName=" + params.appUserName + "&beginDate=" + params.beginDate +
        "&endDate="+params.endDate+"&portType=" + params.portType ;
    window.location.href = "/dataStatistics/importButtonCountExcel?" + str;
}

function getDateRange(dateRange){
    $.ajax({
        type:"POST",
        url:"/dataStatistics/getClickDateRangeMap",
        data:{"dateRange":dateRange},
        datatype: "json",//"xml", "html", "script", "json", "jsonp", "text".
        success:function(data){
            var data=data.data;
            var str="";
            if(dateRange==0){
                str="<div style='color: #404096;'><pr class='subStay1'>日购买课程点击次数（点击率）:</pr>"+data.courseButtonCountStr+"<pr class='subStay1'>日观看点击次数（点击率）:</pr>"+data.playButtonCountStr+"<pr class='subStay1'>日返回点击次数（点击率）:</pr>"+data.outButtonCountStr+"</div>"
            }else if(dateRange==1){
                str="<div style='color: #404096;'><pr class='subStay1'>周购买课程点击次数（点击率）:</pr>"+data.courseButtonCountStr+"<pr class='subStay1'>周观看点击次数（点击率）:</pr>"+data.playButtonCountStr+"<pr class='subStay1'>周返回点击次数（点击率）:</pr>"+data.outButtonCountStr+"</div>"
            }else{
                str="<div style='color: #404096;'><pr class='subStay1'>月购买课程点击次数（点击率）:</pr>"+data.courseButtonCountStr+"<pr class='subStay1'>月观看点击次数（点击率）:</pr>"+data.playButtonCountStr+"<pr class='subStay1'>月返回点击次数（点击率）:</pr>"+data.outButtonCountStr+"</div>"
            }
            $(".totalVisite").empty();
            $(".totalVisite").append(str);
        }   ,
        error: function(e){ //请求出错处理
            alert(e);
        }
    });
}
$(function(){

    getDateRange(0);
    $(".close").click(function(){
        $(this).parents(".totalVisitCount").hide();
    });
    $("#dateRange").change(function () {
        var dateRange=$(this).val();
        getDateRange(dateRange)

    })
});