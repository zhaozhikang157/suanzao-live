<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>代理地区查询</title>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jBox.css">
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/jbox/css/jbox-customer.css">
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/areaquery.css"/>
</head>
<body>
<div class="wrap">
    <div class="head">
        <div class="headbox">
            <div class="headtop">
                <ul class="headul">
                    <li><img src="${requestContext.contextPath}/web/res/style/img/phionlogo.png" alt="龙巢App"/>龙巢App<p class="ermpic"><img src="${requestContext.contextPath}/web/res/style/img/lcapp.png"></p></li>
                    <li><img src="${requestContext.contextPath}/web/res/style/img/telllogo.png" alt="龙链咨询"/>龙链咨询<p class="ermpic"><img src="${requestContext.contextPath}/web/res/style/img/llkjewm.png"></p></li>
                    <li><img src="${requestContext.contextPath}/web/res/style/img/phionlogo.png" alt="联系电话"/>400-793-6688</li>
                    <li><img src="${requestContext.contextPath}/web/res/style/img/closelogo.png" alt="退出"/></li>
                </ul>
            </div>
            <div class="headbottom">
                <div class="headleft">
                       <div class="logobox"> <img src="${requestContext.contextPath}/web/res/style/img/llkejogo.png"></div>
                    <div class="headtext">
                        <p>您好，欢迎来到龙链科技！</p>
                        <p>编号：1101</p>
                    </div>
                </div>
                <div class="headright"></div>
            </div>
        </div>
    </div>

    <div class="con">
        <div class="conttop">
            <ul class="contul">
                <li><img src="${requestContext.contextPath}/web/res/style/img/dllogo.png">代理编号：<span class="num">1101</span></li>
                <li><img src="${requestContext.contextPath}/web/res/style/img/djlogo.png">等级：<span class="num">3</span>&nbsp;(高级代理)&nbsp;<img src="${requestContext.contextPath}/web/res/style/img/djgz.png"></li>
                <li><img src="${requestContext.contextPath}/web/res/style/img/matchmoney.png">总收入：<span class="num">1101</span>&nbsp;元</li>
            </ul>
            <div class="btnbox">
                <input type="button" value="查询" class="cx">
                <input type="button" value="收益提现" class="sytx">
            </div>
        </div>
        <div class="contbottom">
            <label>申请加入时间：</label><input type="text" class="Wdate" id="logTimeBeginStr" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',maxDate:'#F{$dp.$D(\'logTimeEndStr\')}'})">&nbsp; &nbsp;至&nbsp;&nbsp;
            <input type="text" class="Wdate" id="logTimeEndStr" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'twoer',charset:'gb2312',minDate:'#F{$dp.$D(\'logTimeBeginStr\')}'})">
            <span class="rightbox">
               <label>是否有保证金入账：</label><select class="selectbox"><option>-----全部-----</option></select>
            </span>
        </div>
    </div>
</div>
</body>
<script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${requestContext.contextPath}/web/res/js/jboxTools.js"></script>
<script type="text/javascript" src="${requestContext.contextPath}/web/res/jbox/js/jbox.js"></script>
<script src="${requestContext.contextPath}/web/res/my97/lang/zh-cn.js"></script>
<script src="${requestContext.contextPath}/web/res/my97/WdatePicker.js"></script>
<script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script type="text/javascript">
    $(".headul li").click(function(){
        $(this).find("p").fadeIn().parent().siblings().find("p").fadeOut();
    })
    $(".headul li").mouseleave(function(){
        $(".ermpic").fadeOut();
    })
</script>
</html>
