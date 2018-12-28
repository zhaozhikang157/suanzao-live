<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>选择银行</title>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <meta name=”renderer” content=”webkit”>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimal-ui"/>
    <meta name="format-detection" content="telephone=no"/>
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="yes"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/reset.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/myYention.css?nd=<%=current_version%>"/>
</head>
<body onload="doInit()">
<div id="wrap">
    <div class="contact cont_addCard">
        <form class="cont_addCardForm">

        </form>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js"></script>
<script src="/web/res/js/banckyz.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var userName = '${userName}';
    var cash = '${cash}';
    var isProxy = ${isProxy};
    function doInit() {
        var result = ZAjaxRes({url: "/bankCard/getAllBank", type: "GET"});
        if (result.code == '000000') {
            var html = '';
            $.each(result.data, function (i, item) {
                html += '<div class="add_section2 select_section2" onclick="selectBank('+item.id+')"> ' +
                        '<dl> ' +
                        '<dt><img src="'+item.picAddress+'"/></dt> ' +
                        '<dd>'+item.name+'</dd> </dl> ' +
                        '</div>';
            })
            $(".cont_addCardForm").append(html);
        }
    }

    function selectBank(bankId){
        window.location.href="/weixin/add_bankCard?bankId="+bankId+"&userName="+userName+"&cash="+cash+"&isProxy="+isProxy;
    }
</script>
</html>
