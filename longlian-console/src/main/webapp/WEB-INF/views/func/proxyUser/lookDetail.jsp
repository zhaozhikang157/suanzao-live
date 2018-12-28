<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>代理用户详情</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="/web/res/style/css/course/courseDetails.css"/>
    <script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
   
</head>
<body  >
<div class="wrap">
    <h3 class="detailstitle">
        代理用户详情
    </h3>

    <div class="detailscont">
        <p><label>编号:</label><i>${proxyUser.id}</i></p>

        <p><label>用户姓名:</label><i>${proxyUser.name}</i></p>
        <c:if test="${proxyUser.gender=='1'}">
        <p><label>性别:</label><i>男</i></p>
            </c:if>
        <c:if test="${proxyUser.gender=='0'}">
            <p><label>性别:</label><i>女</i></p>
        </c:if>

        <p><label>用户图像:</label><img id="fm_pic" src="${proxyUser.PHOTO}"></p>

        <c:if test="${proxyUser.STATUS=='1'}">
        <p><label>状态:</label><i >撤销</i></p>
        </c:if>
        <c:if test="${proxyUser.STATUS=='0'}">
            <p><label>状态:</label><i >启用</i></p>
        </c:if>
        <p><label>创建时间:</label><i>${proxyUser.startTimeStr}</i></p>
    </div>
</div>
<script>
    function doInit() {
        $('#table-bootstrap').bootstrapTable({
            method: 'get',
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            url: "/accountTrack/getReDetailPage",
            classes: 'table table-hover',
            height: getHeight(),
            toolbar: "#toolbar",
            pageSize: 20,
            queryParams: queryParams,
            columns: [
                {
                    field: 'createTime',
                    title: '交易日期',
                    align: 'center',
                    valign: 'middle',
                    formatter: function (value, row) {
                        return getFormatDateTimeStr(value, "yyyy-MM-dd HH:mm:ss")
                    }
                },
                {
                    field: 'orderNo',
                    title: '交易订单号',
                    align: 'center',
                    valign: 'middle'
                },
                {
                    field: 'userName',
                    title: '户名',
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
                    field: 'remark',
                    title: '收入类型',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'income',
                    title: '收入金额',
                    align: 'center',
                    valign: 'middle'
                },
                {
                    field: 'expenditure',
                    title: '提现金额',
                    align: 'center',
                    valign: 'middle'
                },
                {
                    field: 'currBalance',
                    title: '余额',
                    align: 'center',
                    valign: 'middle'
                }
            ]
        });
    }


</script>
</body>
</html>

