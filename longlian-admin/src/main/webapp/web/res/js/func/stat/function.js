function queryParams(params) {
    if (!params) {
        params = {};
    }
    var beginTime = $("#beginTime").val();  //起始时间
    var endTime = $("#endTime").val();    //结束时间
    if (beginTime != "")beginTime = beginTime;
    if (endTime != "")endTime = endTime + " 23:59:59";
    params["beginTime"] = beginTime;
    params["endTime"] = endTime;
    /* params["courseId"] = courseId;
     var trackName = $("input[name='trackName']").val().trim();
     params["trackName"] = trackName;
     params["mobile"] = $("input[name='mobile']").val().trim();   //下单人联系电话*/
    return params;
}
function doInit() {
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/stat/getCountList",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 30,
        queryParams: queryParams,
        columns: [
            {
                field: 'countTime',
                title: '日期',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'ucount',
                title: '新增用户',
                align: 'center',
            },
            {
                field: 'tcount',
                title: '新增老师',
                align: 'center',
                visible:true
            },
            {
                field: 'pvcount',
                title: 'pv',
                align: 'center',
            },

            {
                field: 'account',
                title: '活跃用户',
                align: 'center',
                valign: 'middle'
            },

            {
                field: 'apcount',
                title: '新增付费人数',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'tpcount',
                title: '付费金额',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'auplRate',
                title: '付费率',
                align: 'center',
                valign: 'middle',
            },
            {
                field: 'clRate',
                title: '次留存率',
                align: 'center',
                valign: 'middle'

            }]
    });
    var css = { height: "94%"};
    $("#indexiframe" , parent.parent.document).css(css);
}
setTimeout(function(){
    css = { height: "93%"};
    $("#indexiframe" , parent.parent.document).css(css);
},500);
//传递的参数
function getHeight() {
    return $(window).height() - 6 - $('h2').outerHeight(true);
}
function doQuery() {
    $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
}