function doInit() {
    // getCourseTypes();
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/dataStatistics/getCourseDetailDataStatistics",
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
                field: 'totalStayTime',
                title: '停留时长',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle',
                formatter: function (value, row) {
                    return row.totalStayTimeStr

                }
            },
            {
                field: 'pCount',
                title: '访问人数',
                align: 'center',
                width:200,
                sortable : true,
                valign: 'middle',
                formatter: function (value, row) {
                    if(value!=null && value!=""){
                        console.log(row);
                        var str="<span class='showSpan' onclick='getPortData("+JSON.stringify(row)+")'>"+value+"</span>"
                        return str;
                    }else{
                        return "-";
                    }
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
            {
                field: 'avgStayTimeStr',
                title: '人均停留时长',
                align: 'center',
                width:200,
                valign: 'middle'
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
    var dateRange=$(this).val();
    getDateRange(dateRange,$("#pageUrl").val())

}
//传递的参数
function queryParams(params) {
    // var id = $("input[name='id']").val().trim();
    var liveTopic = $("input[name='liveTopic']").val().trim();
    var appUserName = $("input[name='appUserName']").val().trim();
    var beginDate = $("#beginDate").val();  //起始时间
    var endDate = $("#endDate").val();    //结束时间
    var pageUrl = $("select[name='pageUrl'] option:selected").val();    //状态
    var portType=$("select[name='portType'] option:selected").val();
    if (!params) {
        params = {};
    }
    params["liveTopic"] = liveTopic;
    params["appUserName"] = appUserName;
    params["beginDate"] = beginDate;
    params["endDate"] = endDate;
    params["pageUrl"] = pageUrl;
    params["portType"]=portType;
    if(pageUrl==0){
        $(".courseTitle").text("课程访问统计");
    }else{
        $(".courseTitle").text("直播间访问统计");
    }
    $(".courseTitle")
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
        "&endDate="+params.endDate+"&pageUrl=" + params.pageUrl +
        "&portType=" + params.portType ;
    window.location.href = "/dataStatistics/importCoursePageCountExcel?" + str;
}

function getTotalVisitCount(courseId){

    // $(".totalVisitCount").show();
}
function getDateRange(dateRange,pageUrl){
    $.ajax({
        type:"POST",
        url:"/dataStatistics/getDateRangeMap",
        data:{"dateRange":dateRange,"pageUrl":pageUrl},
        datatype: "json",//"xml", "html", "script", "json", "jsonp", "text".
        success:function(data){
            var data=data.data;
            var str="";
            if(dateRange==0){
                str="<div style='color: #404096;'><pr class='subStay1'>日停留时长:</pr>"+data.totalStayTime+"<pr class='subStay1'>日访问人数:</pr>"+data.pCount+"<pr class='subStay1'>日访问次数:</pr>"+data.vCount+"</div>"
            }else if(dateRange==1){
                str="<div style='color: #404096;'><pr class='subStay1'>周停留时长:</pr>"+data.totalStayTime+"<pr class='subStay1'>周访问人数:</pr>"+data.pCount+"<pr class='subStay1'>周访问次数:</pr>"+data.vCount+"</div>"
            }else{
                str="<div style='color: #404096;'><pr class='subStay1'>月停留时长:</pr>"+data.totalStayTime+"<pr class='subStay1'>月访问人数:</pr>"+data.pCount+"<pr class='subStay1'>月访问次数:</pr>"+data.vCount+"</div>"
            }
            $(".totalVisite").empty();
            $(".totalVisite").append(str);
        }   ,
        error: function(e){ //请求出错处理
           // alert(e);
        }
    });
}
function getPortData(obj){
    $(".visitDialog").show();
    $(".liveTopic-p").text(obj.liveTopic);
    $(".rangeDate-p").text($("#beginDate").val()+"至"+$("#endDate").val());
    $(".vount-p").text(obj.pCount);
    if($("#portType").val()==0){
        $(".weixinCountp").text(obj.weixinCountp);
        $(".androidCountp").text(obj.androidCountp);
        $(".iosCountp").text(obj.iosCountp);
    }else if($("#portType").val()==1){  //ios
        $(".weixinCountp").text(0);
        $(".androidCountp").text(0);
        $(".iosCountp").text(obj.iosCountp);
    }else if($("#portType").val()==1){   //android
        $(".weixinCountp").text(obj.weixinCountp);
        $(".androidCountp").text(0);
        $(".iosCountp").text(0);
    }
}
function closev(){
    $(".visitDialog").hide();
}
$(function(){
    getDateRange(0,$("#pageUrl").val())
    $(".close").click(function(){
        $(this).parents(".totalVisitCount").hide();
    });

    $("#dateRange").change(function () {
        var dateRange=$(this).val();
        getDateRange(dateRange,$("#pageUrl").val())

    })



});