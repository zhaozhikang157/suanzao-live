function doInit() {
    // getCourseTypes();
    $('#table-bootstrap').bootstrapTable({
        method: 'post',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/course/getList",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 20,
        queryParams: queryParams,
        columns: [
            {
                field: 'id',
                title: 'ID',
                align: 'center',
                width:200,
                valign: 'middle'
            }, {
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
                field: 'endTime',
                title: '结束时间',
                align: 'center',
                valign: 'middle',
                width:460,
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            },
            {
                field: 'liveTopic',
                title: '课程名称',
                align: 'center',
                width:460,
                valign: 'middle'
            },
            {
                field: 'divideScale',
                title: '分销比例',
                align: 'center',
                width:200,
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0' || value=="" || value==null) {
                        return "-";
                    } else {
                        return value;

                    }
                }

            },
            {
                field: 'appUserName',
                title: '讲课老师',
                align: 'center',
                width:220,
                valign: 'middle'
            }, {
                field: 'courseTypeName',
                title: '课程分类',
                align: 'center',
                width:220,
                valign: 'middle'
            },{
                field: 'status',
                title: '课程状态',
                align: 'center',
                width:200,
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        return "<span id='status-" + row.id + "'>上线</span>"
                    } else if (value == '1') {
                        return "<span  id='status-" + row.id + "' style='color:red'>下线</span>"
                    }
                }
            }, {
                field: 'isSeriesCourse',
                title: '是否是系列课',
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
            }
            , {
                field: 'isRelayCourse',
                title: '是否转播课',
                align: 'center',
                width:180,
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        return "<span>否</span>"
                    } else if (value == '1') {
                        return "<span>是</span>"
                    }
                }
            },{
                field: 'liveWay',
                title: '课程类型',
                align: 'center',
                width:200,
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        return "<span>视频</span>"
                    } else if (value == '1') {
                        return "<span>语音</span>"
                    }
                }
            },

            {
                field: 'chargeAmt',
                title: '课程费用',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true
            },
            {
                field: 'joinCount',
                title: '报名人数',
                align: 'center',
                width:180,
                valign: 'middle'
                // sortable : true
            },
            {
                field: 'buyCount',
                title: '购买人数',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true
            },
            {
                field: 'realBuyCount',
                title: '实际购买人数',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true
            },
            {
                field: 'totalCourseAmount',
                title: '课程收益',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true
            },
            {
                field: 'totalRelayCourseAmount',
                title: '转播收益',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true,
                formatter: function (value, row) {
                    if (value=="" || value==null) {
                        return "0";
                    } else {
                        return value;

                    }
                }
            },
            {
                field: 'totalAmount',
                title: '课程总收益',
                align: 'center',
                width:180,
                valign: 'middle',
                sortable : true,
                formatter: function (value, row) {
                    if (value=="" || value==null) {
                        return "0";
                    } else {
                        return value;

                    }
                }
            },
            {
                field: '_opt_',
                title: '操作',
                align: 'center',
                width:400,
                valign: 'middle',
                formatter: function (value, row) {
                    var arra = new Array();
                    arra.push(' <a href="javascript:void(0)" onclick="platformStream(' + row.id + ');">' + '平台本节课流水</a>');
                    return arra.join('');
                }
            }]
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
 * 查看平台本节课流水
 *
 * */
function platformStream(id){
    var title = "本节课平台流水";
    //id:课程id
    var url = "/course/getPlatformStream?id=" + id;
    bsWindow(url, title, {
        height: 700, width: 1400, buttons: [
            {
                classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                window.BSWINDOW.modal("hide");
                doQuery();
            }
            }
        ]
    });
}
function showSpan(obj) {
    $(obj).find("span").eq(0).show();
}

function closeWindows(obj) {
    if (obj.success) {
        window.BSWINDOW.modal("hide");
        jbox_notice({content: "审核完毕", autoClose: 2000, className: "success"});
        doQuery();
    } else {
        window.BSWINDOW.modal("hide");
        jbox_notice({content: "系统错误", autoClose: 2000, className: "warning"});
        doQuery();
    }

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
    var beginTime = $("#beginTime").val();  //起始时间
    var endTime = $("#endTime").val();    //结束时间
    if (beginTime != "")beginTime = beginTime + " 00:00:00";
    if (endTime != "")endTime = endTime + " 23:59:59";
    var status = $("select[name='status'] option:selected").val();    //状态
    // var courseType = $("select[name='courseType'] option:selected").val();    //课程类型
    var isSerier = $("select[name='isSerier'] option:selected").val();    //是否是系列课
    var isFree = $("select[name='isFree'] option:selected").val();    //课程是否免费
    var isRelayCourse = $("select[name='isRelayCourse'] option:selected").val();    //课程是否是转播课
    var courseType = $("select[name='courseType'] option:selected").val();    //课程分类

    if (!params) {
        params = {};
    }
    // params["id"] = id;
    params["liveTopic"] = liveTopic;
    params["appUserName"] = appUserName;
    params["beginTime"] = beginTime;
    params["endTime"] = endTime;
    params["status"] = status;
    //   params["courseType"] = courseType;
    params["isSerier"] = isSerier;
    params["isFree"] = isFree;
    params["isRelayCourse"] = isRelayCourse;
    params["courseType"] = courseType;
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
    var str = "liveTopic=" + params.liveTopic + "&appUserName=" + params.appUserName + "&status=" + params.status +
        "&isSerier="+params.isSerier+"&isFree=" + params.isFree +
        "&startDate=" + params.beginTime + "&endDate=" + params.endTime + "&isRelay=" + params.isRelayCourse ;
    window.location.href = "/course/importExcel?" + str;
}

