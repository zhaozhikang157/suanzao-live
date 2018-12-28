<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>银行卡详情</title>
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
    <div class="contact cont_bankcard">
        <form class="cont_form">
            <section class="bankCard">
                <div>
                    <dl>
                        <dt><img id="bankImg"/></dt>
                        <dd id="bankName"></dd>
                    </dl>
                    <p id="bankNo"></p>
                </div>
            </section>
            <section class="sec_addCard sec_details">
                <label>持卡人</label>
                <input type="text" value="" id="userName" disabled="disabled"/>
            </section>
            <section class="sec_addCard sec_details">
                <label>卡号</label>
                <input type="text" value="" id="cardNo" disabled="disabled"/>
            </section>
            <div>
                <p class="btnstyle okAddBankCard"><a onclick="delBankCard()"><span>解绑银行卡</span></a></p>
            </div>
        </form>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/banckyz.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var bankCardId = ${bankCardId};
    var isProxy = ${isProxy};
    var appId = 0;
    function doInit(){
        var result = ZAjaxJsonRes({url: "/bankCard/findBankCardInfo.user?id="+ bankCardId});
        if(result.code == '000000'){
            var data = result.data;
            $("#bankImg").attr("src",data.picAddress);
            $("#bankName").text(data.bankName);
            $("#bankNo").text(data.cardNo);
            $("#cardNo").val(data.cardNo);
            $("#userName").val(data.name);
            appId = data.appId;
        }
    }

    function delBankCard(){
        var result = ZAjaxJsonRes({url: "/bankCard/delBankCardById.user?id="+ bankCardId});
        sys_pop(result)
        if(result.code == '000000'){
            var CardId = window.localStorage.getItem(appId);
            if(CardId==null || CardId == "" || CardId == undefined){

            }else{
                if(CardId == bankCardId){
                    window.localStorage.removeItem(appId);
                }
            }
            window.setTimeout( jumpIndex(), 2000);
        }
    }

    function jumpIndex(){
        window.location.href="/weixin/carryCash.user?isProxy="+isProxy+"&type=2&cash=cash";
    }
</script>
</html>
