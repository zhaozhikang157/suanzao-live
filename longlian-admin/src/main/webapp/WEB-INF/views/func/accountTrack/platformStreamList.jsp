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
    <link rel="stylesheet" href="${ctx}/web/res/style/css/common/btable.css">
    <script>
        $(document).ready(function() {
            doInit();
        });
    </script>
    <script>
        var courseId=${courseId};
        function doInit() {
            initProfit();
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/course/getPlatformStreamList?courseId"+courseId,
                classes: 'table table-hover',
                //height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 15,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'appId',
                        title: '用户ID',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'appId',
                        title: '课程购买者id',
                        align: 'center',
                        visible:false
                    },
                    {
                        field: 'appName',
                        title: '课程购买者',
                        align: 'center',
                        visible:true
                    },
                    {
                        field: 'orderId',
                        title: 'orderId',   //分销人id
                        align: 'center',
                        visible:false
                    },
                    {
                        field: 'trackId',
                        title: 'trackId',   //分销人id
                        align: 'center',
                        visible:false
                    },
                    {
                        field: 'orderNo',
                        title: '订单号',
                        align: 'center',
                        valign: 'middle'
                    },

                    {
                        field: 'appMobile',
                        title: '联系电话',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'toTAmount',
                        title: '老师收益金额',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'trackAppName',
                        title: '分销人',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if(value=="" || value==null){
                                return row.trackRealName;
                            }else{
                                return row.trackAppName;
                            }
                        }
                    },
                    {
                        field: 'trackAmount',
                        title: '分销金额',
                        align: 'center',
                        valign: 'middle'
                     /*   formatter: function (value, row) {
                            return row.chargeAmt-row.toTAmount;
                        }*/
                    },
                    {
                        field: 'chargeAmt',
                        title: '课程价格',
                        align: 'center',
                        valign: 'middle'
                    },

                    {
                        field: 'successTime',
                        title: '购买时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    }/*,
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        width:'360',
                        valign: 'middle',
                        formatter: function (value, row) {
                            var arra = new Array();
                            arra.push('<a href="javascript:void(0)" onclick="getTrackInfo(' + row.trackId + ');">' + '分销人</a>');
                            return arra.join('');
                        }
                    }*/]
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

        function getTrackInfo(id) {
            var title = "分销人信息";
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

        function doQuery() {
            $('#table-bootstrap').bootstrapTable('refresh');
        }
        //传递的参数
        function queryParams(params) {
            if (!params) {
                params = {};
            }
            params["courseId"] = courseId;
            var trackName = $("input[name='trackName']").val().trim();
            var beginTime = $("#beginTime").val();  //起始时间
            var endTime = $("#endTime").val();    //结束时间
            params["trackName"] = trackName;
            params["endTime"] = endTime;
            params["beginTime"] = beginTime;
            params["mobile"] = $("input[name='mobile']").val().trim();   //下单人联系电话
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
        //导出Excel
        function importExcel() {
            var params = queryParams();
            var str = "trackName=" + params.trackName + "&courseId=" + params.courseId + "&beginTime=" + params.beginTime +
                    "&endTime="+params.endTime+"&mobile=" + params.mobile;
            window.location.href = "/course/platformStreamImportExcel?" + str;
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <script>
        function initProfit(){
            var totalProfit=parseFloat(${data.profit})+parseFloat(${data.trackProfit});
            var val = totalProfit.toFixed(2);
            $("#totalProfit").text(val);
        }
    </script>
    <h3>
        该课程 总收益：
        <label id="totalProfit"></label>
        &nbsp; 老师收益：${data.profit} &nbsp;分销人收益：${data.trackProfit}
    </h3>
    <h3>

    </h3>
    <h3>

    </h3>
    <div id="toolbar">

        <form class="form-horizontal" id="form1" name="form1">
            <label>购买时间：</label>
            <input type="text" class="Wdate form-control" id="beginTime"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'endTime\')}'})"
                   style="width: 120px;display: inline-block;"/>
            至<input type="text" class="Wdate form-control" id="endTime"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'beginTime\')}'})"
                    style="width: 120px;display: inline-block;"/>
            <label>分销人：</label>
            <input type="text" class="form-control" name="trackName" autocomplete="off" placeholder="分销人用户名或姓名"
                   placeholder="" style="width: 220px;display: inline-block;">
            <label>联系电话：</label>
            <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder="下单人联系电话"
                   placeholder="" style="width: 220px;display: inline-block;">

            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-success" onclick="importExcel();">
                <i class=" glyphicon glyphicon-export"></i> 导出
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
           data-show-pagination-switch="true" class="table table-hover" style="width: 1800px;">
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
   // $("#beginTime").val(first)  //获取当月第一天
   // $("#endTime").val(lastDay);//默认时间为 当月 最后一天
</script>
</html>
