<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #eeeeee">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>我的收益</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body onload="doInit()">
<div class="wallet">
    <!--我的钱包 -->
    <div class="wallettitle new_wallettitle">
        <i class="allwallemoney">历史总收益( 枣币 )</i>
        <span class="walletmoney" id="walletmoney">0.00</span>
    </div>
    <div class="mylivwallet">
        <div class="myzq">
            <p><span class="sytap">我的课程收益(元)</span><i id="mylivmonry"></i></p>
            <a class="walletxq" onclick="mylive()">详情
                <img class="walljt" src="/web/res/image/more.png">
            </a>
        </div>
    </div>
    <div class="mylivwallet">
        <div class="myzq">
            <p><span class="sytap">我的分销收益(元)</span><i id="fxsymoney"></i></p>
            <a class="walletxq" onclick="dis()">详情
                <img class="walljt" src="/web/res/image/more.png">
            </a>
        </div>
    </div>
    <div class="mylivwallet">
        <div class="myzq">
            <p><span class="sytap">我的平台收益(元)</span><i id="platmoney"></i></p>
            <a class="walletxq" onclick="plat()">详情
                <img class="walljt" src="/web/res/image/more.png">
            </a>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    function doInit(){
        var result = ZAjaxRes({url: "/accountTrack/findAllWallet.user"});
        if(result.code == '000000'){
            $("#platmoney").text((result.data.plaAmount).toFixed(2));
            $("#fxsymoney").text((result.data.disAmount).toFixed(2));
            $("#mylivmonry").text((result.data.livAmount).toFixed(2));
            $("#walletmoney").text((parseFloat(result.data.livAmount)+parseFloat(result.data.disAmount)+parseFloat(result.data.plaAmount)).toFixed(2));
        }else{
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
</script>
</html>
