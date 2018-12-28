<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>我的收益</title>
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
<body style="overflow: hidden;background: #EDEDED;" onload="doInit()">
<div id="wrap">

    <header class="headStyle header-play">
        <span><a class="BackBtn" href="javascrpt:void(0)"></a></span>
        <p>我的收益</p>
        <span></span>
    </header>
    <div class="contact cont_moneybag">
        <form class="cont_moneyForm">
            <section class="mybalance">
                <%--<p class="mypic"></p>--%>
                <p>我的枣币</p>
                <div><span id="balance">0.00</span></div>
            </section>
            <section class="section money_section2">

                <a href="/account/findUserAccount.user?type=5">
                    <div>

                        <p><span>更换交易密码</span><span class="more"></span></p>
                    </div>
                </a>
                <a  href="/weixin/toMoneyList">
                    <div>
                        <p ><span>交易明细</span><span class="more"></span></p>
                    </div>
                </a>
            </section>
        </form>

        <div class="mylivwallet" style="margin-top: 1rem">
            <div class="myzq">
                <p><span class="sytap">我的课程收益(枣币)</span><i id="mylivmonry"></i></p>
                <a class="walletxq" onclick="mylive()">详情
                    <span class="more new_more"></span>
                </a>
            </div>
        </div>
        <div class="mylivwallet">
            <div class="myzq">
                <p><span class="sytap">我的分销收益(枣币)</span><i id="fxsymoney"></i></p>
                <a class="walletxq" onclick="dis()">详情
                    <span class="more new_more"></span>
                </a>
            </div>
        </div>
        <%--<div class="mylivwallet">--%>
            <%--<div class="myzq">--%>
                <%--<p><span class="sytap">我的平台收益(枣币)</span><i id="platmoney"></i></p>--%>
                <%--<a class="walletxq" onclick="plat()">详情--%>
                    <%--<span class="more new_more"></span>--%>
                <%--</a>--%>
            <%--</div>--%>
        <%--</div>--%>
        <%--<div class="mylivwallet">--%>
            <%--<div class="myzq">--%>
                <%--<p><span class="sytap">我的代理收益(枣币)</span><i id="proxymoney"></i></p>--%>
                <%--<a class="walletxq" onclick="proxy()">详情--%>
                    <%--<span class="more new_more"></span>--%>
                <%--</a>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div class="mylivwallet">
            <div class="myzq">
                <p><span class="sytap">我的打赏收益(枣币)</span><i id="rewardmoney"></i></p>
                <a class="walletxq"onclick="reward()">详情
                    <span class="more new_more"></span>
                </a>
            </div>
        </div>
        <div class="myMoeybtn" onclick="cash()">
            提现
        </div>
    </div>
</div>
</body>
</html>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script>
    function doInit(){
        var result = ZAjaxRes({url: "/accountTrack/findAllWallet.user"});
        if(result.code == '000000'){
            $("#platmoney").text((result.data.plaAmount).toFixed(2));
            $("#fxsymoney").text((result.data.disAmount).toFixed(2));
            $("#mylivmonry").text((result.data.livAmount).toFixed(2));
            $("#proxymoney").text((result.data.proAmount).toFixed(2));
            $("#rewardmoney").text((result.data.rewAmount).toFixed(2));
            $("#balance").text(result.data.balance);
        } else{
            sys_pop(result);
        }
    }

    //直播间收益
    function mylive(){
        window.location.href="/weixin/myLiveIncome?mylivmoney="+$("#mylivmonry").text();
    }

    //分销收益
    function dis(){
        window.location.href="/weixin/disIncome?disIncome="+$("#fxsymoney").text();
    }

    //平台收益
    function plat(){
        window.location.href="/weixin/platIncome?platIncome="+$("#platmoney").text();
    }
    //我的打赏收益
    function reward(){
        window.location.href="/weixin/reward";
    }
    function cash(){
        var result = ZAjaxRes({url: "/liveRoom/findFlow.user"});
        if(result.code == '000000' && result.data != '1'){
            $(".bindingtap").show();
            return;
        }
        var result = ZAjaxRes({url: "/accountTrack/getAccount.user"});
        if(result.code == '000000'){
           var isProxy = result.data;// 0:没有代理 1:代理
            window.location.href="/weixin/carryCash.user?type=cash&isProxy="+isProxy;
        }else{
            sys_pop(result);
        }
    }
    $(".no").click(function(){
        $(".bindingtap").hide();
    })
    $(".yes").click(function(){
        $(".bindingtap").hide();
        window.location.href="/weixin/flowRecharge";
    })
    //代理收益
    function proxy(){
        window.location.href="/weixin/proxyIncome?proxyIncome="+$("#proxymoney").text();
    }
</script>
