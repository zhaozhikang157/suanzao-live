<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html >
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>龙链369后台管理系统</title>
    <link rel="stylesheet" type="text/css" href="${requestContext.contextPath}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href ="${requestContext.contextPath}/web/res/jbox/css/jBox.css">
    <link rel="stylesheet" href ="${requestContext.contextPath}/web/res/jbox/css/jbox-customer.css">
    <link rel="stylesheet" href="${request.contextPath}/web/res/jquery/ui/css/jquery-ui.min.css">
    <link  rel="stylesheet" href="${requestContext.contextPath}/web/res/style/css/main.css" >

</head>
<body onload="doInit();" style="overflow: hidden;">
<div class="default-main-layout indextop">
	<div style="float:left"> 
    <img style="margin-left:26px;margin-top:10px" src="/web/res/style/img/logo.png" alt=""><p class="titletext">欢迎登陆龙链369后台管理系统</p>
   </div>
   
    <ul>
       <li><span class="numtel"  id="user_name"></span>
        </li>
    	<li><img title="主页" style="cursor:pointer"  id="main" src="${requestContext.contextPath}/web/res/style/img/home.png"></li>
    	<li><img  title="退出"  style="cursor:pointer" id="loginOut" src="${requestContext.contextPath}/web/res/style/img/out.png"></li>
    </ul>

    
   
</div>


<div class="default-main-layout indexleft">
    <ul class="navigationleft">
         
    </ul>
</div>
<div  class="default-main-layout indexcon">
    <%--<iframe name="myiframe" id="indexiframe" class="indexiframe" frameborder=0 src="">

    </iframe>--%>
</div>
 
<div class="ui-widget-content  system_message_window" style="display: none;"></div>
<audio style="display: none;" id="__audition_sound1" autoplay="true"></audio>
</body>
<script src="/web/res/jquery/jquery.min.js"></script>
<script src="/web/res/jquery/jquery.cookie.js"></script>
<script src="/web/res/jquery/ui/jquery-ui.js"></script>
<script src="/web/res/jbox/js/jbox.js"></script>
<script src="/web/res/js/jboxTools.js"></script>
<script src="/web/res/js/tools.js"></script>
<script src="/web/res/js/frame/main.js"></script>
<script >
    function setFrame(url){
        $("#indexiframe")[0].src = url;
    }

    function setStyle() {
    	/*导航*/
    	
    	$(".navigationleft li p").click(function(){
    		if($(this).find(".updown").hasClass("on")){
    			$(this).find(".updown").removeClass("on");
    		}else{
    			$(this).find(".updown").addClass("on").parents().siblings().find(".updown").removeClass("on");
    		}
    		$(this).next().slideToggle().parent().siblings().find("ol").slideUp();
    	})
    		$(".nextol li a").click(function(){
    			$(this).addClass("on").parent().siblings().find("a").removeClass("on");
    			$(this).prev().addClass("on").parent().siblings().find(".circular").removeClass("on")
    		})
    }
    /**
     *初始化菜单用户
     */
    function doInit(){
        //setStyle();
        var url  =  "/main/getUserInfoAndEmpRes";
        var json =  tools.requestRs(url);
        var  myStyle;
        if(json.success){
            var  emp = json.data.user;
            $("#user_name").html("用户：" + emp.name);
            var  resList = json.data.resList;
            var str = JSON.stringify(resList);  
            var  navigationleft = $(".navigationleft");

            $.each(resList , function (i , resource){
                if(resource.parentId > 0){//子级别
                   var ol =  $("li[item_id='item_id_"+resource.parentId+"']").children("ol");
                    if(ol.length <= 0){
                        ol = $('<ol class="nextol">');
                        $("li[item_id='item_id_"+resource.parentId+"']").append(ol);
                    }
                    var childLi  = $('<li><a href="javascript:void(0)" onclick="setFrame(\''+ resource.url + '\')">' + resource.name + '</a></li>');
                    ol.append(childLi);
                }else{
                    var li =  '<li item_id="item_id_' + resource.id + '"><p><img src="${requestContext.contextPath}/web/res/style/img/jb.png">' + resource.name+ '<span class="updown"></span></p>';
                    if(resource.url && resource.url != ''){
                        li =   '<li><p><a href="javascript:void(0);" onclick="setFrame(\''+ resource.url + '\')"><img src="${requestContext.contextPath}/web/res/style/img/jd.png">' + resource.name+ '</a></p>';
                    }
                    navigationleft.append(li);
                }
            });
            
            
            
            $(".navigationleft li p").click(function(){
                if($(this).find(".updown").hasClass("on")){
                    $(this).find(".updown").removeClass("on");
                }else{
                    $(this).find(".updown").addClass("on").parents().siblings().find(".updown").removeClass("on");
                }
                $(this).next().slideToggle().parent().siblings().find("ol").slideUp();
                $(this).parent("li").siblings().find(".updown").removeClass("on").parents("li").find("ol").slideUp();
            });
            $(".nextol li a").click(function(){
                $(this).addClass("on").parent().siblings().find("a").removeClass("on");
                $(this).parent().addClass("on").siblings().removeClass("on");
                var url = $(this).attr("item_url");
                if(url){
                  //  setFrame(url);
                }
            });
            
            $(".navigationleft li").first().find("ol").show();
        }else{
            alert(json.msg);
        }
        $("#main").click(function(){
        	setFrame("/stat");
        });
        $("#loginOut").click(function(){
            var jobx ;
            var option = {width:400,content:"欢迎您使用L3龙链店务后台管理系统<br>确定要退出吗？",confrimFunc:function(){
            	jobx.close();
            	logout();
               }
            };
            jobx = jbox_confirm(option);
        })
        //showDT();
    }
	function logout() {
	  location.href = "/logout";
	}
	
    $(function(){
       //$( ".system_message_window" ).draggable();
     /*   var option = {width:400,content:"确定要删除所选中的数据，删除后将不可恢复？",confrimFunc:confrimFuncTest,
            draggable: 'title',
            closeButton: 'box',
            animation: {
                open: 'slide:left',
                close: 'slide:right'
            }
        };
        jbox_confirm(option);

      */
    	setFrame("/stat");
    });

</script>
</html>
