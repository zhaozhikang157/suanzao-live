<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        function doInit() {
            var roomId = ${roomId}
            getCourseTypes();
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/course/getListByRoomId?roomId="+roomId,
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
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'endTime',
                        title: '结束时间',
                        align: 'center',
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
                    }, {
                        field: 'appUserName',
                        title: '讲课老师',
                        align: 'center',
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
                    },
                    {
                        field: 'autoCloseTime',
                        title: '自动关闭时间',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'chargeAmt',
                        title: '课程费用',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'visitCount',
                        title: '访问人数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'joinCount',
                        title: '报名人数',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'recoSort',
                        title: '人工评分',
                        align: 'center',
                        valign: 'middle'
                    }
                    ,
                    {
                        field: 'distributionMap.distributionAmount',
                        title: '收益',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        width:'400',
                        formatter: function (value, row) {
                            var arra = new Array();
                            arra.push('<a href="javascript:void(0)" onclick="del(' + row.id + ');">' + '清空OSS并删除课程</a>');
                            return arra.join('');
                        }
                    }]
            });
        }
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
        function del(id) {
            $.ajax({
                type: "GET",
                url: "/course/OSSdel?id=" + id,
                success: function (data) {
                    if (data.success) {
                        jbox_notice({content: "操作成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "下架失败", autoClose: 2000, className: "error"});
                    }
                }
            })
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
                    } else {
                        jbox_notice({content: "上线失败", autoClose: 2000, className: "error"});
                    }
                }
            })
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
            var status = $("select[name='status'] option:selected").val();    //状态
            var courseType = $("select[name='courseType'] option:selected").val();    //课程类型
            var liveWay = $("select[name='liveWay'] option:selected").val();    //直播类型
            var isRecorded = $("select[name='isRecorded'] option:selected").val();    //直播类型
            var isSerier = $("select[name='isSerier'] option:selected").val();    //是否是系列课
            var sortType = $("select[name='sortType'] option:selected").val();
            if (!params) {
                params = {};
            }
            params["id"] = id;
            params["liveTopic"] = liveTopic;
            params["appUserName"] = appUserName;
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
            params["status"] = status;
            params["courseType"] = courseType;
            params["liveWay"] = liveWay;
            params["isRecorded"] = isRecorded;
            params["isSerier"] = isSerier;
            params["sortType"] = sortType;
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

    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        单节课列表
    </h2>

    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <label>开课时间：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>课程主题：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>课程ID：</label>
            <input type="text" class="form-control" name="id" autocomplete="off" placeholder="讲课ID"
                   placeholder="" style="width: 110px;display: inline-block;">
            <label>课程状态：</label>
            <select class="form-control" id="status" name="status"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">上线</option>
                <option value="1">下线</option>
            </select>
            <label>课程分类：</label>
            <select class="form-control" id="courseType" name="courseType"
                    style="display: inline-block;width: auto">
            </select>
            <label>直播方式：</label>
            <select class="form-control" id="liveWay" name="liveWay"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">视频直播</option>
                <option value="1">语音直播</option>
            </select>
            <label>是否是录播：</label>
            <select class="form-control" id="isRecorded" name="isRecorded"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">不是</option>
                <option value="1">是</option>
            </select>
            <label>是否是系列课：</label>
            <select class="form-control" id="isSerier" name="isSerier"
                    style="display: inline-block;width: auto">
                <option value="">全部</option>
                <option value="0">不是</option>
                <option value="1">是</option>
            </select>
            <label>排序：</label>
            <select class="form-control" id="sortType" name="sortType"
                    style="display: inline-block;width: auto">
                <option value="">-请选择-</option>
                <option value="1">创建时间</option>
                <option value="2">人工评分</option>
            </select>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true" style="width: 2000px;">
    </table>
</div>
</body>
<script>
    var firstDate = new Date();
    firstDate.setDate(1);
    var first = new XDate(firstDate).toString('yyyy-MM-dd'); //第一天
    var endDate = new Date(firstDate);
    endDate.setMonth(firstDate.getMonth() + 1);
    endDate.setDate(0);
    var lastDay = new XDate(endDate).toString('yyyy-MM-dd');
    $("#beginTime").val(first)  //获取当月第一天
    $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
