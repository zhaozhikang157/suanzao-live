<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content="" />
    <meta name="Description" content="" />
    <title>收益明细</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>

<body style="-webkit-overflow-scrolling:touch; ">
<div id="wrapper" style=" overflow: hidden; height:100%;background: white">
    <div id="cont_box" style="overflow-y:auto;padding-bottom: 0;height: 100%;">
        <!--兑换明细-->
        <ul class="detailsForbox" id="detailsForbox">

        </ul>
    </div>
</div>
</body>

<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0, pageSize = 20;
    var oBok = true;
    var html="";
    $(function(){
        Load();
        $('#cont_box').scroll(function(){
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('.detailsForbox').height();
            if(scrollTop + windowHeight >= scrollHeight-10){
                Load();
            }
        });
    });

    function Load() {
        if(oBok){
            var result = ZAjaxRes({url: "/accountTrack/getWalletsPage.user?offset=" + offset + "&pageSize="+pageSize});
            if(result.code == "000000") {
                offset = offset + result.data.length;
                if(result.data.length>0){
                    $.each(result.data, function(i, n){
                        html+=" <li>" +
                                "<p><span>"+n.typeName+"</span><span>"+n.createTime +"</span></p>" +
                                "<p>"+ n.amount+"</p>" +
                                "</li>"
                    });
                    $("#detailsForbox").append(html);
                    if(result.data.length<10){
                        oBok = false;
                    }
                }else{
                    if(offset==0){
                        $("#detailsForbox").append('<div class="noData"></div>');
                    }
                    //如果没有了 oBok 设false
                    oBok = false;
                }
            }else if(result.code == "000110"){
                if(offset==0){
                    $("#detailsForbox").append('<div class="noData"></div>');
                }
            }
        }
    }
</script>
</html>
