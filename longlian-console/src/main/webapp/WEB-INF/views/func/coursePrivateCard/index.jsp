<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>

    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/coursePrivateCard/getCoursePrivateCardPage",
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
                        field: 'cid',
                        title: '课程ID',
                        align: 'center',
                        valign: 'middle'

                    },
                    {
                        field: 'liveTopic',
                        title: '课程',
                        align: 'center',
                        valign: 'middle'

                    },
                    {
                        field: 'appUserName',
                        title: '老师',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        align: 'center',
                        width:360,
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            var arra = new Array();
                            arra.push(' <a href="javascript:void(0)" onclick="addOrUpdate(' + row.id + ');">' + '编辑</a>');
                            arra.push(' <a href="javascript:void(0)" onclick="deleteById(' + row.id + ');">' + '删除</a>');
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
        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }
        /**
         * 查看平台本节课流水
         *
         * */
        function addOrUpdate(id){
            var title = "新增或修改邀请卡设置";
            //id:课程id
            var url = "/coursePrivateCard/addOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 500, width: 600, submit: function (v, h) {
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

        function deleteById(id) {
            var option = {
                width: 400, content: "确定要删除？删除之后数据不能再恢复!", confrimFunc: function (jboxObj) {
                    var param = "id=" + id;
                    var obj = tools.requestRs("/coursePrivateCard/deleteById", param, 'post');
                    if (obj.success) {
                        jboxObj.close();
                        jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                        doQuery();
                    } else {
                        jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                    }
                }
            };
            jbox_confirm(option);
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
            var liveTopic = $("input[name='liveTopic']").val().trim();
            var appUserName = $("input[name='appUserName']").val().trim();
            var beginTime = $("#beginTime").val();  //起始时间
            var endTime = $("#endTime").val();    //结束时间
            if (beginTime != "")beginTime = beginTime + " 00:00:00";
            if (endTime != "")endTime = endTime + " 23:59:59";
            if (!params) {
                params = {};
            }
           // params["id"] = id;
            params["liveTopic"] = liveTopic;
            params["appUserName"] = appUserName;
            params["beginTime"] = beginTime;
            params["endTime"] = endTime;
            return params;
        }

        //导出Excel
        function importExcel() {
            var params = queryParams();
            var str = "liveTopic=" + params.liveTopic + "&appUserName=" + params.appUserName + "&status=" + params.status +
                    "&isSerier="+params.isSerier+"&isFree=" + params.isFree +
                    "&startDate=" + params.beginTime + "&endDate=" + params.endTime ;
            window.location.href = "/course/importExcel?" + str;
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        邀请卡渠道设置:
    </h2>

    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <label>创建时间：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>课程名称：</label>
            <input type="text" class="form-control" name="liveTopic" autocomplete="off" placeholder="课程主题"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>讲课老师：</label>
            <input type="text" class="form-control" name="appUserName" autocomplete="off" placeholder="讲课老师"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-success" onclick="addOrUpdate();">
                <i class=" glyphicon glyphicon-button">添加</i>
            </button>

        </form>
    </div>
    <table id="table-bootstrap"
           border="0" cellpadding="4" cellspacing="0"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true"
           style="">
    </table>

</div>
</body>
<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
<link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
<link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">

</html>
