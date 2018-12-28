$(document).ready(function() {
    doInit();
    getCourseDatas({"selectDate":$(".getCourseDatas").val()});
});

/**  ------------------ end --------------**/
function doInit() {
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/course/getCourseOrdersList",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
            {
                field: 'orderNo',
                title: '订单编号',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'realName',
                align: 'center',
                visible:false,
                valign: 'middle'
            },
            {
                field: 'uname',
                title: '购买人',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    if(value==null || value==""){
                        return row.realName
                    }else{
                        return value;
                    }
                }
            },
            {
                field: 'mobile',
                title: '手机号',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'liveTopic',
                title: '课程名称',
                align: 'center',
                valign: 'middle',
            },
            {
                field: 'startTime',
                title: '开课时间',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                }
            },
            {
                field: 'endTime',
                title: '结束时间',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                }
            },
            {
                field: 'amount',
                title: '金额',
                align: 'center',
                valign: 'middle'
            },

            {
                field: 'successTime',
                title: '购买时间',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
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

//传递的参数
function queryParams(params) {
    var liveTopic = $("input[name='liveTopic']").val();
    var cstartTime = $("input[name='cstartTime']").val();
    var cendTime = $("input[name='cendTime']").val();
    var uname = $("input[name='uname']").val();
    var mobile = $("input[name='mobile']").val();
    var startTime = $("input[name='startTime']").val();
    var endTime = $("input[name='endTime']").val();
    if (!params) {
        params = {};
    }
    params["liveTopic"] = liveTopic;
    params["cstartTime"] = cstartTime;
    if (cendTime != ""){
        cendTime+=" 23:59:59"
    }
    params["cendTime"] = cendTime;
    params["uname"] = uname;
    params["mobile"] = mobile;
    params["startTime"] = startTime;
    if(endTime!=""){
        cendTime+=" 23:59:59"
    }
    params["endTime"] = endTime;
    return params;
}
function getHeight() {
    return $(window).height() - $('#H1').outerHeight(true) - 20;
}
/**
 *点击查询
 */
function doQuery(flag) {
    $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
}

