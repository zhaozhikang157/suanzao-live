<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>银行卡管理</title>
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
<div id="wrap" style="background: white;max-width:46.75rem;margin: 0 auto;">
    <header class="headStyle header-play">
        <span><a class="BackBtn"></a></span>

        <p>银行卡管理</p>
        <span><a href="/weixin/add_bankCard?cash=${cash}&isProxy=${isProxy}" style="color: white">新增</a></span>
    </header>
    <div class="contact cont_bankcard" style="top: 4rem;max-width: 46.75rem;margin: 0 auto;">
        <form class="cont_form">
            <section class="bankCard">

            </section>
        </form>
    </div>
</div>
</body>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/banckyz.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var type = ${type};
    var cash = '${cash}';
    var isProxy = ${isProxy}; //0:没有1:是
    function doInit(){
        var result = ZAjaxRes({url: "/bankCard/getMyBank.user" , type: "GET"});
        if(result.code == '000000'){
            var html = '';
            $.each(result.data,function(i,item){
                html+='<div> <a onclick="seleBankCard('+item.id+')"> <dl> ' +
                        '<dt>' +
                        '<img src="'+item.picAddress+'" /></dt> ' +
                        '<dd>'+item.bankName+'</dd> </dl> ' +
                        '<p>'+item.cardNo+'</p> ' +
                        '</a> </div>'
            })
            $(".bankCard").append(html);
        }else{
            pop({"content":result.message,"status":"error",time:'3000'});
        }
    }

    function seleBankCard(bankCardId){
        if("2" == type){
            window.location.href="/weixin/bankCard_details?bankCardId="+bankCardId+"&isProxy="+isProxy+"&type=cash";
        }else{
            window.location.href="/weixin/carryCash.user?bankCardId="+bankCardId+"&isProxy="+isProxy+"&type=cash";
        }
    }
</script>

</html>
