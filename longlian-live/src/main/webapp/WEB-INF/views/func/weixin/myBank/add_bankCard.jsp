<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>新增银行卡</title>
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
<body>
<div id="wrap">
    <div class="contact cont_addCard">
        <p class="addcardTitle">请绑定持卡人本人银行卡</p>
        <form class="cont_addCardForm">
            <section class="sec_addCard">
                <label>持卡人</label>
                <input type="text" value="${userName}" id="userName"/>
            </section>
            <section class="sec_addCard">
                <label>选择银行</label>
                <a onclick="selectBank()">
                    <div class="add_section2">
                        <dl>
                            <dt><img src=${url}></dt>
                            <dd>${name}</dd>
                        </dl>
                        <span class="iconBtn"></span>
                    </div>
                </a>
            </section>
            <section class="sec_addCard">
                <label>卡号</label>
                <input type="tel" maxlength="30" id="cardNo"
                       onkeyup="formatBankNo(this)" onkeydown="formatBankNo(this)" value=""
                       placeholder="请输入银行卡号"/>
            </section>
            <section class="sec_addCard">
                <label>身份证号</label>
                <input type="text" id="legalIdCard" maxlength="18" onblur="checkIdCard('legalIdCard',$(this))" value=""
                       placeholder="请输入身份证号"/>
            </section>
            <div class="promptText">
                <p>提醒：请仔细核对银行卡信息，如因输入错误造成的提现失败，本公司不承担任何责任。</p>
            </div>
            <div>
                <p class="btnstyle okAddBankCard"><a onclick="sureInfo()"><span>确认</span></a></p>
            </div>
        </form>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/banckyz.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
    var cash  = '${cash}';
    var isProxy  = ${isProxy};
    function sureInfo(){
        var bankId = ${id};
        if(bankId == null || bankId == undefined || bankId == '' || bankId < 1){
            pop({"content": "请选择银行!" , "status": "normal", time: '2500'});
            return;
        }
        var bankName = '${name}';
        if(bankName == null || bankName == undefined || bankName == ''){
            pop({"content": "请选择银行!" , "status": "normal", time: '2500'});
            return;
        }
        var bankCard = {
            idCard:$("#legalIdCard").val(),
            cardNo:$("#cardNo").val(),
            bankId:bankId,
            name:$("#userName").val(),
            bankName:bankName
        };
        if(bankCard.idCard == '' || bankCard.idCard == undefined || bankCard.idCard == null){
            pop({"content": "请填写身份证号!" , "status": "normal", time: '2500'});
            return;
        }
        if(bankCard.cardNo == '' || bankCard.cardNo == undefined || bankCard.cardNo == null){
            pop({"content": "请填写银行卡号!" , "status": "normal", time: '2500'});
            return;
        }
        if(bankCard.name == '' || bankCard.name == undefined || bankCard.name == null){
            pop({"content": "请填写持卡人姓名!" , "status": "normal", time: '2500'});
            return;
        }
        var ss = removeAllSpace(bankCard.cardNo);
//        if(ss.length < 16){
//            pop({"content": "请填写正确的银行卡号!" , "status": "normal", time: '2500'});
//            return;
//        }
        var result = ZAjaxJsonRes({url: "/bankCard/insertBankCard.user" , type: "POST" , param:bankCard});
        sys_pop(result);
        if(result.code == '000000'){
            window.setTimeout(window.location.href='/weixin/bankCard_manage?type=1&cash='+cash+'&isProxy='+isProxy , 2000);
        }
    }

    function removeAllSpace(str) {
        return str.replace(/\s+/g, "");
    }

    function selectBank(){
        window.location.href="/weixin/selectedBank?userName="+$("#userName").val()+"&cash="+cash+"&isProxy="+isProxy;
    }
</script>
</html>
