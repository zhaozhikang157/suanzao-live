function doInit() {
    getCourseTypes();
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
                valign: 'middle'
            }, {
                field: 'startTime',
                title: '开课时间',
                align: 'center',
                valign: 'middle',
                width:'220',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            },
            {
                field: 'endTime',
                title: '结束时间',
                align: 'center',
                width:'220',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            },
            {
                field: 'createTime',
                title: '创建时间',
                align: 'center',
                width:'220',
                valign: 'middle',
                formatter: function (value, row) {
                    return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                }
            },
            {
                field: 'liveTopic',
                title: '课程名称',
                align: 'center',
                valign: 'middle'
            },{
                field: 'liveRoomName',
                title: '直播间名称',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'appUserName',
                title: '讲课老师',
                align: 'center',
                width:'300',
                valign: 'middle'
            }, {
                field: 'status',
                title: '课程状态',
                align: 'center',
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
                field: 'liveWay',
                title: '课程类型',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        return "<span>视频</span>"
                    } else if (value == '1') {
                        return "<span>语音</span>"
                    }
                }
            }, {
                field: 'isHide',
                title: '显示状态',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == '0') {
                        return "<span id='hideShow-" + row.id + "'>显示</span>"
                    } else if (value == '1') {
                        return "<span  id='hideShow-" + row.id + "' style='color:red'>隐藏</span>"
                    }
                }
            },{
                field: 'isRelayHide',
                title: '转播市场显示状态',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                	if(!row.isRelay == "1"){
                		return "<span id='relayHideShow-" + row.id + "'>--</span>"
                	}
                    if (value == '0') {
                        return "<span id='relayHideShow-" + row.id + "'>显示</span>"
                    } else if (value == '1') {
                        return "<span  id='relayHideShow-" + row.id + "' style='color:red'>隐藏</span>"
                    }
                }
            }, {
                field: 'chargeAmt',
                title: '课程费用',
                align: 'center',
                sortable : true,
                valign: 'middle'
            },
            {
                field: 'autoCloseTime',
                title: '自动关闭时间',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    var str = "<span onclick='showSpan(this)'>"
                        + "<span class='updateSpan' style='display:none '>"
                        + "<input type='text' title='增加人数' value='0'/><input type='button' value='修改' onclick='updateAutoCloseTime(this , " + row.id + ")'/>" +
                        "</span>"
                        + "<span class='showSpan'>"
                        + value + "</span></span>";
                    return str;
                }
            },
            {

                field: 'visitCount',
                title: '访问人数',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    var str = "<span onclick='showSpan(this)'>"
                        + "<span class='updateSpan' style='display:none '>"
                        + "<input style='width: 40px;' type='text' title='增加人数' value='0'/><input type='button' value='增加' onclick='addVisitCount(this , " + row.id + ")'/></span>"
                        + "<span class='showSpan'>"
                        + value + "</span></span>";
                    return str;
                }
            },
            {
                field: 'joinCount',
                title: '报名人数',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    var str = "<span onclick='showSpan(this)'>"
                        + "<span class='updateSpan' style='display:none '>"
                        + "<input style='width: 40px;' type='text' title='增加人数' value='0'/><input type='button' value='增加' onclick='addJoinCount(this , " + row.id + ")'/></span>"
                        + "<span class='showSpan'>"
                        + value + "</span></span>";
                    return str;
                }
            },
            {
                field: 'recoSort',
                title: '推荐评分',
                align: 'center',
                valign: 'middle',
                sortable : true,
                formatter: function (value, row) {
                    var str = "<span onclick='showSpan(this)'>"
                        + "<span class='updateSpan' style='display:none '>"
                        + "<input style='width: 40px;' type='text' title='增加评分' value='0'/><input type='button' value='增加' onclick='addRecoSort(this , " + row.id + ")'/></span>"
                        + "<span class='showSpan'>"
                        + value + "</span></span>";
                    return str;
                }
            },
            {
                field: 'typeSort',
                title: '分类评分',
                align: 'center',
                valign: 'middle',
                sortable : true,
                formatter: function (value, row) {
                    var str = "<span onclick='showSpan(this)'>"
                        + "<span class='updateSpan' style='display:none '>"
                        + "<input style='width: 40px;' type='text' title='增加评分' value='0'/>" +
                        "<input type='button' value='增加' onclick='addTypeSort(this , " + row.id + ")'/>" +
                        "</span>"
                        + "<span class='showSpan'>"
                        + value + "</span></span>";
                    return str;
                }
            },
            {
                field: '_opt_',
                title: '操作',
                align: 'center',
                width:'415',
                valign: 'middle',
                formatter: function (value, row) {
                    var arra = new Array();
                    arra.push('<a href="javascript:void(0)" onclick="edit(' + row.id + ');">' + '编辑</a>');
                    arra.push('<a href="javascript:void(0)" onclick="getDetails(' + row.id + ');">' + '详情</a>');
                    var startTime = new Date(row.startTime);
                    var endTime = row.endTime;
                    var n = new Date();
                    //未结束，且已经开始
                    if (!endTime && startTime.getTime() <= n.getTime()) {
                        arra.push('<a href="javascript:void(0)" onclick="live(' + row.id + ');">' + '直播</a>');
                    }
                    if (endTime) {
                        arra.push('<a href="javascript:void(0)" onclick="live(' + row.id + ');">' + '回播</a>');
                    } else {
                        if (row.isSeriesCourse =='0') {
                            arra.push('<a href="javascript:void(0)" onclick="end(' + row.id + ');">' + '结束</a>');
                        }
                    }
                    if (row.status == '0') {
                        arra.push('<a id="aDown-' + row.id + '" href="javascript:void(0)"  onclick="updateDown(this , ' + row.id + ');">' + '下线</a>');
                        arra.push('<a id="aUp-' + row.id + '"  href="javascript:void(0)"  style=" display:none"  onclick="updateUp(this , ' + row.id + ');">' + '上线</a>');
                    } else {
                        arra.push('<a id="aDown-' + row.id + '"  href="javascript:void(0)"  style=" display:none"  onclick="updateDown(this , ' + row.id + ');">' + '下线</a>');
                        arra.push('<a id="aUp-' + row.id + '"  href="javascript:void(0)" onclick="updateUp(this , ' + row.id + ');">' + '上线</a>');
                    }
                    if (row.isDelete == '0') {
                        arra.push('<span id="del-' + row.id + '"><a href="javascript:void(0)" onclick="del(' + row.id + ' , 1);">' + '删除</a></span>');
                    } else {
                        arra.push('<span id="del-' + row.id + '"><a href="javascript:void(0)" onclick="del(' + row.id + ' , 0);">' + '恢复</a></span>');
                    }
                    if (!endTime && row.isDelete == '0' && row.status == '0') {
                        arra.push('<a href="javascript:void(0)" onclick="exportUserAvatar(' + row.id + ');">' + '虚拟用户</a>');
                    }
                    if (row.seriesCourseId=="0") {
                        arra.push('<a href="javascript:void(0)" onclick="recommendTo(' + row.id + ');">' + '推荐到</a>');
                    }
                    /* arra.push(' <a href="javascript:void(0)" onclick="platformStream(' + row.id + ');">' + '平台本节课流水</a>');*/
                    if (row.isHide == '1') {
                        arra.push('<a id="show-' + row.id + '" href="javascript:void(0)"  onclick="isShow(this , ' + row.id + ');">' + '显示</a>');
                        //arra.push('<a id="hide-' + row.id + '"  href="javascript:void(0)"  style=" display:none"  onclick="isHide(this , ' + row.id + ');">' + '隐藏</a>');
                    } else {
                        //arra.push('<a id="hide-' + row.id + '"  href="javascript:void(0)"  style=" display:none"  onclick="isHide(this , ' + row.id + ');">' + '隐藏</a>');
                        arra.push('<a id="hide-' + row.id + '"  href="javascript:void(0)" onclick="isHide(this , ' + row.id + ');">' + '隐藏</a>');
                    }
                    if(row.isRelay == "1"){
                		if (row.isRelayHide == '1') {
                			arra.push('<a id="relayShow-' + row.id + '" href="javascript:void(0)"  onclick="isRelayShow(this , ' + row.id + ');">' + '转播显示</a>');
                		} else {
                			arra.push('<a id="relayHide-' + row.id + '"  href="javascript:void(0)" onclick="isRelayHide(this , ' + row.id + ');">' + '转播隐藏</a>');
                		}
                	}
                    return arra.join('');
                }
            }]
    });
    var css = { height: "94%"};
    $("#indexiframe" , parent.parent.document).css(css);
}
setTimeout(function(){
    css = { height: "93%"};
    $("#indexiframe" , parent.parent.document).css(css);
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
function getHeight() {
    return $(window).height() - 6 - $('h2').outerHeight(true);
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
    var id = $("input[name='id']").val().trim();
    var liveTopic = $("input[name='liveTopic']").val().trim();
    var appUserName = $("input[name='appUserName']").val().trim();
    var beginTime = $("#beginTime").val();  //起始时间
    var endTime = $("#endTime").val();    //结束时间
    if (beginTime != "")beginTime = beginTime + " 00:00:00";
    if (endTime != "")endTime = endTime + " 23:59:59";

    var cbeginTime = $("#cbeginTime").val();  //起始时间
    var cendTime = $("#cendTime").val();    //结束时间
    if (cbeginTime != "")cbeginTime = cbeginTime + " 00:00:00";
    if (cendTime != "")cendTime = cendTime + " 23:59:59";

    var status = $("select[name='status'] option:selected").val();    //状态
    var courseType = $("select[name='courseType'] option:selected").val();    //课程类型
    var liveWay = $("select[name='liveWay'] option:selected").val();    //直播类型
    var isRecorded = $("select[name='isRecorded'] option:selected").val();    //直播类型
    var isSerier = $("select[name='isSerier'] option:selected").val();    //是否是系列课
    var sortType = $("select[name='sortType'] option:selected").val();
    var isFree = $("select[name='isFree'] option:selected").val();    //课程是否免费
    var isHide = $("select[name='isHide'] option:selected").val();  //隐藏或显示
    var isRelayCourse = $("select[name='isRelayCourse'] option:selected").val();  //是否是转播课
    var isRelayHide = $("select[name='isRelayHide'] option:selected").val();  //转播市场隐藏或显示
    
    if (!params) {
        params = {};
    }
    params["id"] = id;
    params["liveTopic"] = liveTopic;
    params["appUserName"] = appUserName;

    params["beginTime"] = beginTime;
    params["endTime"] = endTime;

    params["cbeginTime"] = cbeginTime;
    params["cendTime"] = cendTime;

    params["status"] = status;
    params["courseType"] = courseType;
    params["liveWay"] = liveWay;
    params["isRecorded"] = isRecorded;
    params["isSerier"] = isSerier;
    params["sortType"] = sortType;
    params["isFree"] = isFree;
    params["isHide"] = isHide;
    params["isRelayCourse"] = isRelayCourse;
    params["isRelayHide"] = isRelayHide;
    return params;
}
function getCourseTypes() {
    var obj = tools.requestRs("/courseType/getCourseTypesList", 'get');
    if (obj.success) {
        $("#courseType").html("<option value=''>全部</option>");
        $.each(obj.data, function (idxs, items) {
            $("#courseType").append("<option value=" + items.id + ">" + items.name + "</option>");
        });
    }
}
function exportUserAvatar(courseId) {
    var title = "虚拟用户";
    var url = "/avatar/index?courseId=" + courseId;
    bsWindow(url, title, {
        height: 1300, width: 1300, buttons: [
            {
                classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                window.BSWINDOW.modal("hide");
                //doQuery();
            }
            }
        ]
    });
}
/**
 * 上线
 * @param a
 * @param id
 */
function updateUp(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/updateUp?id=" + id,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "上线成功", autoClose: 2000, className: "success"});
                a.style.display = "none";
                $('#aDown-' + id).css("display", "");
                $('#status-' + id).text("上线");
                $('#status-' + id).css("color", "");
                //doQuery();
                $('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "上线失败", autoClose: 2000, className: "error"});
            }
        }
    })
}
function edit(id) {
    var title = "课程编辑";
    var url = "/course/editCourse?id=" + id;
    bsWindow(url, title, {
        height: 700, width: 1000, submit: function (v, h) {
            var cw = h[0].contentWindow;
            cw.doSave(function (json) {
                if (json.success) {
                    window.BSWINDOW.modal("hide");
                    jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                    doQuery();
                } else {
                    jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                }
            });
        }
    });
}

function recommendTo(aid) {
    var title = "推荐课程";
    var url = "/course/recommendTo?id=" + aid;
    bsWindow(url, title, {
        height: 300, width: 700, buttons: []
    });
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

function getDetails(id) {
    var title = "课程详情";
    var url = "/course/details?id=" + id;
    bsWindow(url, title, {
        height: 1300, width: 1300, buttons: [
            {
                classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                window.BSWINDOW.modal("hide");
                doQuery();
            }
            }
        ]
    });
}
function live(id) {
    var url = "/course/live?id=" + id;
    window.open(url);
}

function updateDown(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/updateDown?id=" + id,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "下线成功", autoClose: 2000, className: "success"});
                a.style.display = "none";
                $('#status-' + id).text("下线");
                $('#status-' + id).css("color", "red");
                $('#aUp-' + id).css("display", "");
                //doQuery();
                $('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "下架失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

function end(id) {
    $.ajax({
        type: "GET",
        url: "/course/end?id=" + id,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "结束成功", autoClose: 2000, className: "success"});
                doQuery();
            } else {
                jbox_notice({content: "结束失败", autoClose: 2000, className: "error"});
            }
        }
    })
}
function del(id, flag) {
    if(confirm("确认要删除吗？")){
        $.ajax({
            type: "GET",
            url: "/course/del?id=" + id + "&isDel=" + flag,
            success: function (data) {
                if (data.success) {
                    jbox_notice({content: "操作成功", autoClose: 2000, className: "success"});
                    if (flag == 1) {
                        $('#del-' + id).html('<a href="javascript:void(0)" onclick="del(' + id + ' , 0);">恢复</a>');
                    } else {
                        $('#del-' + id).html('<a href="javascript:void(0)" onclick="del(' + id + ' , 1);">删除</a>');
                    }
                    doQuery();
                } else {
                    jbox_notice({content: "失败", autoClose: 2000, className: "error"});
                }
            }
        })
    }

}
function showSpan(obj) {
    $(obj).find("span").eq(0).show();
}

function updateAutoCloseTime(obj, id) {
    var input = $(obj).prev();
    var updateValue = input.val();
    //判断是不是数字
    if (isNaN(updateValue) || parseInt(updateValue) < 0) {
        jbox_notice({content: "输入的不是正整数或小于0!", autoClose: 2000, className: "error"});
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/updateAutoCloseTime?id=" + id + "&updateValue=" + updateValue,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                input.val(0);
                $(obj).parent().next().text(data.data);
                $(obj).parent().hide();
            } else {
                jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
            }
        }
    })
}


function addVisitCount(obj, id) {
    var input = $(obj).prev();
    var addValue = input.val();
    //判断是不是数字
    if (isNaN(addValue) && parseInt(addValue) > 0) {
        jbox_notice({content: "输入的不是正整数且大于0!", autoClose: 2000, className: "error"});
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/addVisitCount?id=" + id + "&addCount=" + addValue,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                input.val(0);
                $(obj).parent().next().text(data.data);
                $(obj).parent().hide();
            } else {
                jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

function addJoinCount(obj, id) {
    var input = $(obj).prev();
    var addValue = input.val();
    //判断是不是数字
    if (isNaN(addValue) && parseInt(addValue) > 0) {
        jbox_notice({content: "输入的不是正整数且大于0!", autoClose: 2000, className: "error"});
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/addJoinCount?id=" + id + "&addCount=" + addValue,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                input.val(0);
                $(obj).parent().next().text(data.data);
                $(obj).parent().hide();
            } else {
                jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
            }
        }
    })
}
function addRecoSort(obj, id) {
    var input = $(obj).prev();
    var addValue = input.val();
    //判断是不是数字
    if (isNaN(addValue) && parseInt(addValue) > 0) {
        jbox_notice({content: "输入的不是正整数且大于0!", autoClose: 2000, className: "error"});
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/addRecoSort?id=" + id + "&addCount=" + addValue,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                input.val(0);
                $(obj).parent().next().text(data.data);
                $(obj).parent().hide();
            } else {
                jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

/**
 * 分类评分
 * @param obj
 * @param id
 */
function addTypeSort(obj, id) {
    var input = $(obj).prev();
    var addValue = input.val();
    //判断是不是数字
    //^((-)?\d+(\.\d+)?)?$
    var reg=/^((-)?\d+(\.\d+)?)?$/;
    if(!reg.test(addValue)){
        jbox_notice({content: "输入的是非法数字!", autoClose: 2000, className: "error"});
        return;
    }
    $.ajax({
        type: "GET",
        url: "/course/addTypeSort?id=" + id + "&addCount=" + addValue,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "修改成功", autoClose: 2000, className: "success"});
                input.val(0);
                $(obj).parent().next().text(data.data);
                $(obj).parent().hide();
            } else {
                jbox_notice({content: "修改失败", autoClose: 2000, className: "error"});
            }
        }
    })
}
//显示
function isShow(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/showHide?id=" + id+"&isHide="+0,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "显示成功", autoClose: 2000, className: "success"});
                a.style.display = "none";
                $('#hideShow-' + id).text("显示");
                $('#show-' + id).css("display", "");
                //doQuery();
                $('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "显示失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

//隐藏
function isHide(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/showHide?id=" + id+"&isHide="+1,
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "隐藏成功", autoClose: 2000, className: "success"});
                a.style.display = "none";
                $('#hideShow-' + id).text("隐藏");
                $('#hideShow-' + id).css("color", "red");
                $('#hide-' + id).css("display", "");
                //doQuery();
                $('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "隐藏失败", autoClose: 2000, className: "error"});
            }
        }
    })
}

//转播显示
function isRelayShow(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/showRelayHide?id=" + id+"&isRelayHide="+0,
        success: function (data) {
            if (data.success) {
            	jbox_notice({content: "设置转播市场显示成功", autoClose: 2000, className: "success"});
            	$('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "设置转播市场显示失败", autoClose: 2000, className: "error"});
            }
        }
    })
}
 //转播隐藏
function isRelayHide(a, id) {
    $.ajax({
        type: "GET",
        url: "/course/showRelayHide?id=" + id+"&isRelayHide="+1,
        success: function (data) {
            if (data.success) {
            	jbox_notice({content: "设置转播市场隐藏成功", autoClose: 2000, className: "success"});
            	$('#table-bootstrap').bootstrapTable(('refresh'));
            } else {
                jbox_notice({content: "设置转播市场隐藏失败", autoClose: 2000, className: "error"});
            }
        }
    })
} 