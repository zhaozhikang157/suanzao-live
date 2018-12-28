<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/style/css/orders/coursePrint.css"/>
    <script src="${ctx}/web/res/jquery/jquery-1.4.4.min.js"></script>
    <script src="${ctx}/web/res/jquery/jquery.jqprint-0.3.js"></script>
</head>
<body>
<div id="billPrint" class="wrapper">
    <button type="button" class="btn btn-info" onclick="billPrint()" style="display: block; float: right">
        <i class="glyphicon glyphicon-print"></i> 打印
    </button>
    <table class="billTable billTableInfo" border="" cellspacing="" cellpadding="">
        <thead class="billTitle">
        <tr>
            <td colspan="6" style="border: none;"><h1>电子回单(充值)</h1></td>
        </tr>
        <tr>
            <td colspan="6" style="border: none;">
                <div>
                    <p >NO:${map.tranNo}</p>

                    <p >交易日期:<fmt:formatDate value='${map.createTime}' pattern='yyyy-MM-dd HH:mm:ss'/></p>

                    <p >订单号:${map.orderNo}</p>
                </div>
            </td>
        </tr>
        </thead>
        <tbody>
        <tr align="center">
            <td colspan="2"><span>业务类型</span></td>
            <td><span>代收</span></td>
            <td colspan="2"><span>业务明细</span></td>
            <td><span>充值</span></td>
        </tr>
        <tr align="center">
            <td colspan="2"><span>支付渠道</span></td>
            <td><span>${map.bankType}</span></td>
            <td colspan="2"><span>币种</span></td>
            <td><span>人民币</span></td>
        </tr>
        <tr align="center">
            <td class="otherTd addbg" rowspan="2"><span>收款方</span></td>
            <td class="otherTd"><span>户名</span></td>
            <td><span class="spanMargin">北京龙链科技有限公司</span></td>
            <td class="otherTd addbg" rowspan="2"><span>付款方</span></td>
            <td class="otherTd"><span>户名</span></td>
            <td><span>${map.mobile}</span></td>
        </tr>
        <tr align="center">
            <td class="otherTd"><span>账户</span></td>
            <td><span >${map.mchId}</span></td>
            <td style="text-align: center">用户ID</td>
            <td><span >${map.appId}</span></td>
        </tr>
        <%--<tr align="center">--%>
            <%--<td rowspan="2" class="otherTd"><span>开户行</span></td>--%>
            <%--<td rowspan="2"><span > </span></td>--%>
            <%--<td class="otherTd"><span>开户行</span></td>--%>
            <%--<td><span ></span></td>--%>
        <%--</tr>--%>
        <%--<tr align="center">--%>
            <%--<td class="otherTd"><span>用户ID</span></td>--%>
            <%--<td><span>${map.appId}</span></td>--%>
        <%--</tr>--%>
        <tr align="center">
            <td colspan="2"><span>交易金额(小写)</span></td>
            <td><span >${map.oAmount}</span></td>
            <td colspan="2"><span>交易金额(大写)</span></td>
            <td><span>${map.capitalAmount}</span></td>
        </tr>
        <tr align="center">
            <td colspan="2"><span>商户手续费</span></td>
            <td><span>0</span></td>
            <td colspan="2"><span>第三方手续费</span></td>
            <td><span >${map.charge}</span></td>
        </tr>
        <tr align="center">
            <td colspan="2"><span>客户钱包支出</span></td>
            <td><span >0</span></td>
            <td colspan="2"><span>银联备付金支出</span></td>
            <td><span>0</span></td>
        </tr>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="6">
                <span class="textColor">备注：充值</span>
            </td>
        </tr>
        </tfoot>
    </table>
</div>
</body>
<script src="js/jquery-1.8.2.min.js"></script>
<script src="js/jquery.jqprint-0.3.js"></script>
<script>
    function billPrint() {
        $(".billTableInfo").jqprint();
    }
</script>
</html>
