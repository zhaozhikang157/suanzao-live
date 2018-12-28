function doInit() {
    // getCourseTypes();
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/equipment/queryEquipmentList",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
            /*{
                field: 'id',
                title: '编号',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true
            },*/
            {
                field: 'appId',
                title: '用户id',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'name',
                title: '用户昵称',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'equipmentId',
                title: '设备id',
                align: 'center',
                valign: 'middle'
            },

            {
                field: 'equipmentType',
                title: '设备类型',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'createTime',
                title: '收集时间',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            }
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
    var name = $("input[name='name']").val().trim();
    var beginTime = $("#beginTime").val();  //起始时间
    var endTime = $("#endTime").val();    //结束时间
    if (beginTime != "")beginTime = beginTime + " 00:00:00";
    if (endTime != "")endTime = endTime + " 23:59:59";
    var equipmentType = $("select[name='equipmentType'] option:selected").val();


    if (!params) {
        params = {};
    }
    // params["id"] = id;
    params["name"] = name;
    params["equipmentType"] = equipmentType;
    params["beginTime"] = beginTime;
    params["endTime"] = endTime;

    return params;
}

//导出Excel
function importExcel() {
    var params = queryParams();
    var str = "name=" + params.name + "&equipmentType=" + params.equipmentType +"&startDate=" + params.beginTime + "&endDate=" + params.endTime ;
    window.location.href = "/equipment/importExcel?" + str;
}