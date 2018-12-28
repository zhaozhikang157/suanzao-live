<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>提现</title>
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
    <link rel="stylesheet" type="text/css" href="/web/res/css/settingPwd.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/reset.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/myYention.css?nd=<%=current_version%>"/>
</head>
<body onload="doinit()">

<div class="bigDealPwdDiv" style="display: none">
    <div class="dealPwdDiv">
        <div class="d_header">
            <h1>交易密码</h1>
            <span onclick="closeBigDealFunc()"></span>
        </div>
        <div class="d_contact">
            <div class="inputBoxContainer" id="inputBoxContainer">
                <input autofocus type="text" class="realInput" onkeydown="IsLength($(this))"/>
                <div class="bogusInput">
                    <input type="password" maxlength="6" disabled/>
                    <input type="password" maxlength="6" disabled/>
                    <input type="password" maxlength="6" disabled/>
                    <input type="password" maxlength="6" disabled/>
                    <input type="password" maxlength="6" disabled/>
                    <input type="password" maxlength="6" disabled/>
                </div>
            </div>
            <p onclick="bankOutforgetPwd()">忘记密码</p>
        </div>
    </div>
</div>

<div id="wrap" style="   ">
    <header class="headStyle header-play">
        <span><a class="BackBtn"></a></span>

        <p>提现</p>
        <span onclick="manageBank()">银行卡管理</span>
    </header>
    <div class="contact cont_addCard"style="top:4rem">
        <form class="cont_addCardForm" >
            <section class="sec_addCard sec_Cash">
                <label>提现银行卡</label>
                <input type="text" value="" readonly="" id="selectBankCard" onclick="selectBank()"/>
            </section>
            <section class="sec_addCard sec_Cash">
                <label>金额</label>
                <input class="importMoney" onfocus="showMoneyIconFunc(this)" type="number" placeholder="提现金额"/>
                <span>￥</span>
            </section>
            <p class="allCash"><span>枣币余额：￥${data.balance}</span><span onclick="showAll()">全部提现</span></p>

            <div class="promptText" id="pro">

            </div>
            <div>
                <p class="btnstyle okAddBankCard"><a onclick="sureBankOutMoney()"><span>确认提现</span></a></p>
            </div>
        </form>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/banckyz.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script>
    var bankCardId = ${bankCardId};
    var appId = ${appId};
    var type = '${type}';
    var isProxy = ${isProxy}; // 0:没有代理 1:代理
    function doinit(){
        var bankOut = ZAjaxRes({url: "/withdrawals/getBankOutRemark.user"});
        if(isProxy == '0'){
            $("#pro").append("<p>"+bankOut.data.bankOutRemark+"</p>");
        }else{
            $("#pro").append("<p>"+bankOut.data.bankOutRemarkProxy+"</p>")
        }
        var bankCard = window.localStorage;
        if(bankCard == null || bankCard == '' || bankCard == undefined){
            $("#selectBankCard").val("请选择银行卡");
            return;
        }
        var bankId = bankCard.getItem(appId);
        if((bankId == null || bankId == '')&&(bankCardId == "")){
            $("#selectBankCard").val("请选择银行卡");
            return;
        }
        var cardId = '';
        if(bankCardId!='' && bankCardId!='0'){
            cardId = bankCardId;
        }else{
            cardId = bankId;
        }
        if(cardId!=''){
            var result = ZAjaxRes({url: "/bankCard/findBankCardInfo.user?id="+cardId, type: "GET"});
            if(result.code == '000000'){
                var data = result.data;
                var bankName =data.bankName;
                var cardNo = data.cardNo;
                bankCardId = data.id;
                cardNo = cardNo.substring(cardNo.length - 4 , cardNo.length);
                $("#selectBankCard").val(bankName+"("+cardNo+")");
                window.localStorage.setItem(data.appId , bankCardId);
            }
        }
    }

    function manageBank(){
        var cash = type;    //银行卡管理
        window.location.href='/weixin/bankCard_manage?type=2&cash='+cash+"&isProxy="+isProxy;
    }

    function selectBank(){
        var cash = type;    //银行卡提现
        window.location.href='/weixin/bankCard_manage?type=1&cash='+cash+"&isProxy="+isProxy;
    }

    function showAll() {
        $(".importMoney").val(${data.balance});
    }

    function closeBigDealFunc() {
        $(".bigDealPwdDiv").hide()
        $("#inputBoxContainer input").val("");
    }

    function sureBankOutMoney() {
        var money = $(".importMoney").val();
        if(bankCardId =="" || bankCardId < 1 || bankCardId == null || bankCardId == undefined){
            pop({"content": "请选择银行卡!", "status": "error", time: '2500'});
            return;
        }
        if (!/^\d+(\.\d{1,2})?$/.test(money)) {
            pop({"content": "请输入正确的提现金额!", "status": "error", time: '2500'});
            return;
        }
        if (parseFloat(money) >${data.balance}) {
            pop({"content": "您的提现金额大于账户的余额!", "status": "error", time: '2500'});
            return;
        }
        $(".bigDealPwdDiv").show();
        $(".bigDealPwdDiv").find(".realInput").attr("autofocus","autofocus")
    }

    boxInput.init(function(){
        var value =  boxInput.getBoxInputValue();
        sureMoney(value);
    });

    function sureMoney(pwdVal) {
        var cardId = bankCardId;
        if (pwdVal != null && pwdVal != "") {
            $(".bigDealPwdDiv").hide();
            var money = $(".importMoney").val();
            var result = ZAjaxRes({
                url: "/withdrawals/option.user?amount=" + money + "&cardId=" + cardId + "&tradePassword=" + pwdVal + "&type=1"
                , type: "POST"
            });
            sys_pop(result)
            if(result.code == '000000'){
                var store = window.localStorage;
                store.setItem(appId,cardId);
                setTimeout(function () {window.location.href= '/weixin/new_learncoinAccount';}, 2000);
            }else{
                $("#inputBoxContainer input").val("");
            }
        }
    }

    /**
     * 忘记密码
     */
    function bankOutforgetPwd(){
        window.location.href='/weixin/forgetTradePwd.user?isProxy='+isProxy;
    }

</script>
</html>
