<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>我的账户</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
</head>
<body style="overflow: hidden;background: #EDEDED;" onload="doInit()">
<div id="wrap">
    <div class="contact">
        <%--<p class="mypic"></p>--%>
        <p class="contTitle">我的学币</p>
        <span id="balances"></span>
        <p class="convertible">
            <span>可兑换枣币</span>
            <span class="tibleMoney"></span>
            <img src="/web/res/image/you_@1.png" alt=""/>
        </p>
    </div>
    <ul class="contUl">
        <li>
            <a href="/weixin/recharge">
                <p>
                    <img src="/web/res/image/icon_@1_cz.png" alt=""/>
                    <em>充值</em>
                    <span class="more"></span>
                </p>
            </a>
        </li>
        <li>
            <a href="/weixin/toDetailsMoneyList">
                <p>
                    <img src="/web/res/image/icon_@1_czmx.png" alt=""/>
                    <em>交易明细</em>
                    <span class="more"></span>
                </p>
            </a>
        </li>
    </ul>
    </div>
</div>
</body>
</html>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    function doInit(){
        var result = ZAjaxRes({url: "/llaccount/getAccountBalance.user", type: "get"});
        if (result.code == "000000") {
            $("#balances").text(result.data.xb);
            $(".tibleMoney").text(result.data.zb);
        }
    }
    $(".convertible").click(function(){
        window.location.href="/weixin/exchangeCurrency.user";
    })
</script>
