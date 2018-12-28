<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="application/msword; charset=utf-8" %>
<%@ page pageEncoding="utf-8" %>
<style>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/dayin.css"/>
</style>
<%
    String fileName = "jsp导出word.doc";
    //对中文文件名编码
    //fileName = URLEncoder.encode(fileName, "utf-8");
    byte[] yte = fileName.getBytes("GB2312");
    String unicoStr = new String(yte, "ISO-8859-1");
    response.setHeader("Content-disposition", "attachment; filename=" + unicoStr);
%>
<c:forEach items="${list}" var="order" varStatus="list">
    <c:if test="${order.orderType == 0}">
        <div id="page_${list.index}" class="wrapper">

        <table class="billTable billTableInfo" border="" cellspacing="" cellpadding="">
            <thead class="billTitle">
                <tr>
                    <td colspan="6" style="border: none;">
                        <h1>电子回单(代收)</h1>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" style="border: none;">
                        <div>
                            <p>NO: ${order.tranNo}</p>

                            <p>交易日期:<fmt:formatDate value='${order.successTime}'
                                                    pattern='yyyy-MM-dd HH:mm:ss'/></p>

                            <p>订单号:${order.orderNo}</p>
                        </div>
                    </td>
                </tr>
            </thead>
            <tbody>
                <tr align="center">
                    <td colspan="3">
                        <span>业务类型:代收</span>
                    </td>
                    <td colspan="3">
                        <span>业务明细:课时费</span>
                    </td>
                </tr>
                <tr align="center">
                    <td colspan="3">
                        <span>支付渠道:${order.bankType}</span>
                    </td>
                    <td colspan="3">
                        <span>币种:人民币</span>
                    </td>
                </tr>
                <tr align="center">
                    <td class="otherTd addbg" rowspan="2" colspan="2">
                        <span>付款方</span>
                    </td>
                    <td class="otherTd" colspan="1">
                        <span>户名</span>
                    </td>
                    <td colspan="3">
                        <span class="spanMargin">${order.mobile} </span>
                    </td>

                </tr>
                <tr align="center">
                    <td class="otherTd" colspan="1">
                        <span>用户ID</span>
                    </td>
                    <td colspan="3">
                        <span>${order.appId}</span>
                    </td>
                </tr>
                <tr align="center">
                    <td colspan="3">
                        <span>付款金额:${order.realAmount}</span>
                    </td>
                    <td colspan="3">
                        <span>付款金额(大写):${order.capitalRealAmount}</span>
                    </td>
                </tr>

                <tr>
                    <c:if test="${empty order.distributorId}">
                        <td class="otherTd addbg" rowspan="2" colspan="2">
                            <span>收款方</span>
                        </td>
                    </c:if>
                    <c:if test="${order.distributorId !=null}">
                        <td class="otherTd addbg" rowspan="4" colspan="2">
                            <span>收款方</span>
                        </td>
                    </c:if>
                        <%--<td class="otherTd addbg" rowspan="4" colspan="2"><span>收款方</span></td>--%>
                    <td class="otherTd">
                        <span>讲师户名</span>
                    </td>
                    <td>
                        <span style="margin-left: 17px">${order.teacherName}</span>
                    </td>
                    <td rowspan="2" colspan="2">
                        <span>讲师课时费:${order.teacherAmount}</span>
                    </td>
                </tr>
                <tr>
                    <td class="otherTd">
                        <span>讲师ID</span>
                    </td>
                    <td>
                        <span>${order.teacherId}</span>
                    </td>
                </tr>
                <c:if test="${order.distributorId !=null}">
                    <tr>
                        <td class="otherTd">
                            <span>分销人户名</span>
                        </td>
                        <td>
                            <span>${order.distributorName}</span>
                        </td>
                        <td rowspan="2" colspan="3">
                            <span style="margin-left: 18px;">分销人课时费:${order.distributorAmount}</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="otherTd">
                            <span>分销人ID</span>
                        </td>
                        <td>
                            <span>${order.distributorId}</span>
                        </td>
                    </tr>
                </c:if>
                <tr align="center">
                    <td colspan="3">
                        <span>收款金额:${order.realAmount}</span>
                    </td>
                    <td colspan="3">
                        <span>收款金额(大写):${order.capitalRealAmount}</span>
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="6">
                        <span class="textColor">摘要：课时费分销收入 分成比例：${order.divideScale}</span>
                    </td>
                </tr>
            </tfoot>
        </table>
    </c:if>
    </div>
    <c:if test="${order.orderType == 1}">
        <div id="page_${list.index}" class="wrapper">
            <table class="billTable billTableInfo" border="" cellspacing="" cellpadding="">
                <thead class="billTitle">
                    <tr>
                        <td colspan="6" style="border: none;">
                            <h1>电子回单(提现)</h1>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6" style="border: none;">
                            <div>
                                <p>NO: 第三方支付单号 ${order.tranNo}</p>

                                <p>交易日期:<fmt:formatDate value='${order.successTime}'
                                                        pattern='yyyy-MM-dd HH:mm:ss'/></p>

                                <p>订单号:${order.orderNo}</p>
                            </div>
                        </td>
                    </tr>
                </thead>
                <tbody>
                    <tr align="center">
                        <td colspan="2">
                            <span>业务类型</span>
                        </td>
                        <td>
                            <span>代付</span>
                        </td>
                        <td colspan="2">
                            <span>业务明细</span>
                        </td>
                        <td>
                            <span>提现</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td colspan="2">
                            <span>支付渠道</span>
                        </td>
                        <td>
                            <span>${order.bankType}</span>
                        </td>
                        <td colspan="2">
                            <span>币种</span>
                        </td>
                        <td>
                            <span>人民币</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td class="otherTd addbg" rowspan="4">
                            <span>付款方</span>
                        </td>
                        <td class="otherTd">
                            <span>户名</span>
                        </td>
                        <td>
                            <span class="spanMargin">北京龙链科技有限公司</span>
                        </td>
                        <td class="otherTd addbg" rowspan="4">
                            <span>收款方</span>
                        </td>
                        <td class="otherTd">
                            <span>户名</span>
                        </td>
                        <td>
                            <span>${order.appMobile}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td class="otherTd">
                            <span>账户</span>
                        </td>
                        <td>
                            <span>808080211302864</span>
                        </td>
                        <td style="text-align: center">账户</td>
                        <td>
                            <span>${order.merId}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td rowspan="2" class="otherTd">
                            <span>开户行</span>
                        </td>
                        <td rowspan="2">
                            <span>${order.bankName}</span>
                        </td>
                        <td class="otherTd">
                            <span>开户行</span>
                        </td>
                        <td>
                            <span>${order.bankName}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td class="otherTd">
                            <span>用户ID</span>
                        </td>
                        <td>
                            <span>${order.appId}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td colspan="2">
                            <span>交易金额(小写)</span>
                        </td>
                        <td>
                            <span>${order.amount}</span>
                        </td>
                        <td colspan="2">
                            <span>交易金额(大写)</span>
                        </td>
                        <td>
                            <span>${order.capitalAmount}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td colspan="2">
                            <span>商户手续费</span>
                        </td>
                        <td>
                            <span>${order.llCharge}</span>
                        </td>
                        <td colspan="2">
                            <span>银行手续费</span>
                        </td>
                        <td>
                            <span>${order.charge}</span>
                        </td>
                    </tr>
                    <tr align="center">
                        <td colspan="2">
                            <span>客户钱包支出</span>
                        </td>
                        <td>
                            <span>${order.amount}</span>
                        </td>
                        <td colspan="2">
                            <span>银联备付金支出</span>
                        </td>
                        <td>
                            <span>${order.amount-order.charge}</span>
                        </td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="6">
                            <span class="textColor">备注：提现</span>
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>
    </c:if>
</c:forEach>
