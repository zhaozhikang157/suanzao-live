<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>购买课程回单</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/style/css/orders/print.css"/>
    <script src="${ctx}/web/res/jquery/jquery-1.4.4.min.js"></script>
    <script src="${ctx}/web/res/jquery/jquery.jqprint-0.3.js"></script>
</head>
<body>
<div id="billPrint" class="wrapper">
    <table class="billTable billTableInfo" border="" cellspacing="" cellpadding="">
        <thead class="billTitle">
        <tr>
            <td colspan="6" style="border: none;"><h1>电子回单(代收)</h1></td>
        </tr>
        <tr>
            <td colspan="6" style="border: none;">
                <div>
                    <p >NO: ${order.tranNo}</p>

                    <p >交易日期:<fmt:formatDate value='${order.successTime}' pattern='yyyy-MM-dd HH:mm:ss'/></p>

                    <p >订单号:${order.orderNo}</p>
                </div>
            </td>
        </tr>
        </thead>
        <tbody>
        <tr align="center">
            <td colspan="3"><span>业务类型:代收</span></td>
            <td colspan="3"><span>业务明细:课时费</span></td>
        </tr>
        <tr align="center">
            <td colspan="3"><span>支付渠道:${order.bankType}</span></td>
            <td colspan="3"><span>币种:人民币</span></td>
        </tr>
        <tr align="center">
            <td class="otherTd addbg" rowspan="2" colspan="2"><span>付款方</span></td>
            <td class="otherTd" colspan="1"><span>户名</span></td>
            <td colspan="3"><span class="spanMargin">${order.mobile} </span></td>

        </tr>
        <tr align="center">
            <td class="otherTd" colspan="1"><span>用户ID</span></td>
            <td colspan="3"><span >${order.appId}</span></td>
        </tr>
        <tr align="center">
            <td colspan="3"><span>付款金额:${order.realAmount}</span></td>
            <td colspan="3"><span>付款金额(大写):${order.capitalRealAmount}</span></td>
        </tr>

        <tr>
        <c:if test="${empty order.distributorId}">
            <td class="otherTd addbg" rowspan="2" colspan="2"><span>收款方</span></td>
        </c:if>
        <c:if test="${order.distributorId !=null}">
            <td class="otherTd addbg" rowspan="4" colspan="2"><span>收款方</span></td>
        </c:if>
            <%--<td class="otherTd addbg" rowspan="4" colspan="2"><span>收款方</span></td>--%>
            <td class="otherTd"><span>讲师户名</span></td>
            <td ><span style="margin-left: 17px">${order.teacherName}</span></td>
            <td rowspan="2" colspan="2"><span>讲师课时费:${order.teacherAmount}</span></td>
        </tr>
        <tr>
            <td class="otherTd"><span>讲师ID</span></td>
            <td><span>${order.teacherId}</span></td>
        </tr>
        <c:if test="${order.distributorId !=null}">
        <tr >
            <td class="otherTd"><span>分销人户名</span></td>
            <td><span>${order.distributorName}</span></td>
            <td rowspan="2" colspan="3"><span style="margin-left: 18px;">分销人课时费:${order.distributorAmount}</span></td>
        </tr>
        <tr >
            <td class="otherTd"><span>分销人ID</span></td>
            <td><span>${order.distributorId}</span></td>
        </tr>
         </c:if>
        <tr align="center">
            <td colspan="3"><span>收款金额:${order.realAmount}</span></td>
            <td colspan="3"><span>收款金额(大写):${order.capitalRealAmount}</span></td>
        </tr>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="6">
                <span class="textColor">摘要：课时费分销收入  分成比例：${order.divideScale}</span>
            </td>
        </tr>
        </tfoot>
    </table>
</div>
<button type="button" class="btn btn-info" onclick="billPrint()" style="position: fixed;top: 0;right:0">
    <i class="glyphicon glyphicon-print"></i> 打印
</button>
<script>
    function billPrint() {
        $(".billTableInfo").jqprint();
    }
</script>
</body>
</html>
