//传递的参数
function queryParams(params) {
    /* var liveTopic = $("input[name='liveTopic']").val().trim();
     var beginTime = $("#beginTime").val();  //起始时间
     var endTime = $("#endTime").val();    //结束时间
     if (beginTime != "")beginTime = beginTime + " 00:00:00";
     if (endTime != "")endTime = endTime + " 23:59:59";
     var status = $("select[name='status'] option:selected").val();    //状态
     if (!params) {
     params = {};
     }
     params["liveTopic"] = liveTopic;
     params["beginTime"] = beginTime;
     params["endTime"] = endTime;
     params["status"] = status;*/
    return params;
}
function refresh() {
    $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
}

$(window).resize(function () {
    $('#table-bootstrap').bootstrapTable('resetView', {
        height: getHeight()
    });
});
function getHeight() {
    return $(window).height() - 6 - $('h2').outerHeight(true);
}
/**
 * 禁播流  function
 *
 * */
function disableStream(aid) {
    var url = "/liveStream/disableStream?courseId=" + aid;
    $.ajax({
        type: "GET",
        url: url,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "禁播流成功", autoClose: 2000, className: "success"});
                var opt = {
                    url: "/liveStream/getListData",
                    silent: true,
                    query:{}
                };
                $('#table-bootstrap').bootstrapTable('refresh',opt);
            } else {
                jbox_notice({content: "禁播流失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

/**
 *点击查询
 */
/* function doQuery(flag) {
 if (flag) {
 $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
 } else {
 $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
 }
 }*/