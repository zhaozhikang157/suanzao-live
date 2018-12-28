<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>龙链369后台管理系统</title>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/bootstrap/css/bootstrap.min.css"/>
    <link title="blue" rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/default.css" id="cssfile"/>
    <link rel="stylesheet" type="text/css" href ="${requestContext.contextPath}/web/res/jbox/css/jBox.css">
    <link rel="stylesheet"type="text/css"  href ="${requestContext.contextPath}/web/res/jbox/css/jbox-customer.css">
    <link rel="stylesheet" href="${cxt}/web/res/srcollbar/perfect-scrollbar.css">
</head>
<body onload="doInit()" style="overflow: hidden;">
<div class="default-main-layout indextop">
    <div class="comLogo"><img alt="龙链369" title="龙链369" src="${requestContext.contextPath}/web/res/style/img/pic_wodezhuye.png" /></div>
    <div>|&nbsp;&nbsp;<span id="mobile"></span>&nbsp;&nbsp;你好，欢迎使用龙链369后台管理系统&nbsp;&nbsp;&nbsp;</div>
    <ul>
        <li class="timeclick"><span></span></li>
        <li><span id="loginOut" class="cancellation" title="注销"></span></li>
    </ul>
</div>
<div class="default-main-layout indexleft">
    <ul class="navigationleft">
        <li>
            <p><a id="main" href="javascript:void(0)">首页</a></p>
        </li>
    </ul>
</div>
<div  class="default-main-layout indexcon">
    <iframe name="myiframe" id="indexiframe" class="indexiframe" frameborder=0 src="showPage.html"></iframe>
</div>
</body>
<script src="/web/res/jquery/jquery.min.js"></script>
<script src="/web/res/jquery/jquery.cookie.js"></script>
<script src="/web/res/jquery/ui/jquery-ui.js"></script>
<script src="/web/res/jbox/js/jbox.js"></script>
<script src="/web/res/js/jboxTools.js"></script>
<script src="/web/res/js/tools.js"></script>
<script src="${cxt}/web/res/srcollbar/perfect-scrollbar.jquery.js"></script>
<script src="${cxt}/web/res/srcollbar/perfect-scrollbar.js"></script>
<script src="/web/res/js/func/new_index.js"></script>

<script>
    function setFrame(url){
        $("#indexiframe")[0].src = url;
    }
    /**
     *初始化菜单用户
     */
    function doInit(){
        showDT();
        //setStyle();
        var url  =  "/main/getUserInfoAndEmpRes";
        var json =  tools.requestRs(url);
        var  myStyle;
        if(json.success){
            var  emp = json.data.user;
            var  resList = json.data.resList;
            var str = JSON.stringify(resList);
            var  navigationleft = $(".navigationleft");
            $("#mobile").text(emp.name  );
            $.each(resList , function (i , resource){
                if(resource.parentId > 0){//子级别
                    var ol =  $("li[item_id='item_id_"+resource.parentId+"']").children("ol");
                    if(ol.length <= 0){
                        ol = $('<ol class="NextOl">');
                        $("li[item_id='item_id_"+resource.parentId+"']").append(ol);
                    }
                    var childLi  = $('<li><a href="javascript:void(0)" onclick="setFrame(\''+ resource.url + '\')">' + resource.name + '</a></li>');
                    ol.append(childLi);
                }else{
                    var li =  '<li item_id="item_id_' + resource.id + '"><p><a href="javascript:void(0)">'+'<span class="spgl"></span>' + resource.name+ '</a></p>';
                    if(resource.url && resource.url != ''){
                        li =   '<li><p><a href="javascript:void(0);" onclick="setFrame(\''+ resource.url + '\')">'+'<span class="spgl"></span>' + resource.name+ '</a></p>';
                    }
                    navigationleft.append(li);
                }
            });

            $(".navigationleft li:first-child p").addClass("OnNext");
            $(".navigationleft li p").on("click",function(){
                if($(this).find("span").hasClass("on")){
                    $(this).find("span").removeClass("on");
                }else{
                    $(this).find("span").addClass("on").parents().siblings().find("span").removeClass("on");
                }
                if($(this).hasClass("OnFirst")){
                    $(this).removeClass("OnFirst");
                }else{
                    $(this).addClass("OnFirst").parent().siblings().find("p").removeClass("OnFirst");
                }
                $(this).next().slideToggle().parent().siblings().find("ol").slideUp();
                $(this).parent().siblings().find("span").removeClass("on").parents("p").next().slideUp();

            });
            $(".NextOl li").on("click",function(){
                $(this).addClass("OnNext").siblings().removeClass("OnNext");
            });

        }else{
            alert(json.msg);
        }
        $("#main").click(function(){
            setFrame("/stat");
        });
        $("#loginOut").click(function(){
            var jobx ;
            var option = {width:400,content:"欢迎您使用龙链369后台管理系统<br>确定要退出吗？",confrimFunc:function(){
                jobx.close();
                logout();
            }
            };
            jobx = jbox_confirm(option);
        })

    }
    function logout() {
        location.href = "/logout";
    }
    $(function(){
        setFrame("/stat");
    });
    $('.indexleft').perfectScrollbar();
</script>
</html>
