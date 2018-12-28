<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>用户钱包</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/accountTrack/getAppUserAccounts",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: '用户id',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'name',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'mobile',
                        title: '手机号',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'balance',
                        title: '钱包余额',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: '_opt_',
                        title: '查看',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a class="like" href="javascript:void(0)" title="银行卡" onclick="targetPageFunc(' + row.id + ','+row.balance+')">银行卡</a>  ',
                                '<a class="remove" href="javascript:void(0)" title="提现记录" onclick="withdrawDeposit(' + row.id + ');">提现记录</a>',
                                '<a class="remove" href="javascript:void(0)" title="收益记录" onclick="rebate(' + row.id + ');">奖励记录</a>'
                            ].join('');
                        }
                    }]
            });
        }
        //传递的参数
        function queryParams(params) {
            var name = $("input[name='name']").val();
            var mobile = $("input[name='mobile']").val();
            var appId = $("input[name='appId']").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            params["mobile"] = mobile;
            params["appId"] = appId;
            return params;
        }
        //导出Excel
        function importExcel() {
            var params = queryParams();
            var str = "name="+params.name+"&mobile="+params.mobile;
            window.location.href = "/accountTrack/importExcelUserAccounts?" + str;
        }

        function getHeight() {
            return $(window).height()-14 - $('#toolbar').outerHeight(true);
        }

        //银行卡详情
        function targetPageFunc(id,balance){
            var title = ""
            var url = "/appUser/bankinfo?id="+id+"&balance="+balance;
            bsWindow(url,title,{height:600,width:1000,buttons: [{
                classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
            }]});
        }

        //提现记录
        function withdrawDeposit(id){
            var title = ""
            var url = "/appUser/withdrawDepositPage?id="+id;
            bsWindow(url,title,{height:600,width:1000,buttons: [
                {
                    classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
                }
            ]
            });
        }

        //奖励记录
        function rebate(id){
            var title = ""
            var url = "/appUser/membershiprebatePage?id="+id;
            bsWindow(url,title,{height:600,width:1000,buttons: [
                {
                    classStyle: "btn btn-primary", name: "关闭", clickFun: function (name, bs) {
                    window.BSWINDOW.modal("hide");
                }
                }
            ]
            });
        }

        function doQuery(flag) {
        	if(flag){
        		$('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber:  1});
        	}
            $('#table-bootstrap').bootstrapTable('refresh',{silent: true});
        }
    </script>
</head>

<body onload="doInit();">
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-top: 15px;">
        用户钱包
    </h2>
    <div id="toolbar">
        <form class=" form-inline" id="form1" name="form1">
            <div class="form-group" style="margin-top:10px ;padding: 0;">
                <label  class="control-label">姓名：</label>
                <input type="text" class="form-control" name="name" autocomplete="off" placeholder="" style="width: 100px;display: inline-block;">
                <label  class="control-label">手机号：</label>
                <input type="text" class="form-control" name="mobile" autocomplete="off" placeholder="" style="width: 180px;display: inline-block;">
                <label  class="control-label">用户id：</label>
                <input type="text" class="form-control" name="appId" autocomplete="off" placeholder="" style="width: 70px;display: inline-block;">
            </div>
            <div class="form-group" style="margin-top:10px;padding: 0;">
                <button type="button" class="btn btn-info" onclick="doQuery(true);">
                    <i class=" glyphicon glyphicon-search"></i> 查询
                </button>
                <button type="button" class="btn btn-success" onclick="importExcel();">
                    <i class=" glyphicon glyphicon-export"></i> 导出
                </button>
            </div>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true">
    </table>
</div>
</body>
</html>
