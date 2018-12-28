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
        var courseId=${courseId};
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/course/getPlatformStreamList?courseId"+courseId,
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'appId',
                        title: '用户ID',
                        align: 'center',
                        width: '220',
                        valign: 'middle'
                    },
                    {
                        field: 'appName',
                        title: '课程购买者',
                        align: 'center',
                        hidden:true
                    },
                    {
                        field: 'orderNo',
                        title: '订单号',
                        align: 'center',
                        width: '220',
                        valign: 'middle'
                    },
                    {
                        field: 'appName',
                        title: '课程购买者',
                        align: 'center',
                        width: '220',
                        valign: 'middle'
                    },
                    {
                        field: 'appMobile',
                        title: '联系电话',
                        align: 'center',
                        width: '220',
                        valign: 'middle'
                    },
                    {
                        field: 'toTAmount',
                        title: '老师收益金额',
                        align: 'center',
                        width:'300',
                        valign: 'middle'
                    },
                    {
                        field: 'chargeAmt',
                        title: '课程价格',
                        align: 'center',
                        width:'300',
                        valign: 'middle'
                    },

                    {
                        field: 'successTime',
                        title: '成功下单时间',
                        align: 'center',
                        valign: 'middle',
                        width:'360',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        width:'360',
                        valign: 'middle',
                        formatter: function (value, row) {
                            var arra = new Array();
                            arra.push('<a href="javascript:void(0)" onclick="edit(' + row.id + ');">' + '编辑</a>');
                            arra.push('<a href="javascript:void(0)" onclick="getDetails(' + row.id + ');">' + '详情</a>');
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

        //传递的参数
        function queryParams(params) {
            if (!params) {
                params = {};
            }
            params["courseId"] = courseId;
            return params;
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
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2>
        课程 总收益：
    </h2>

    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true" style="width: 1800px;">
    </table>
</div>
</body>

</html>
