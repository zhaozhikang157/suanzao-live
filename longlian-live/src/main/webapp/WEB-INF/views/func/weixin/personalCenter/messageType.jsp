<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>消息</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-2.1.0.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body>
<!-- 主体部分 -->
<div id="wrapper" style="overflow: hidden; height:100%; -webkit-overflow-scrolling: touch;">
  <div id="cont_box"class="new_index" style="padding-bottom: 0;height: 100%">
    <div class="messBon">

    </div>
    <%--暂时隐藏该功能--%>
    <div class="yjqk" style="display: none;">一键清空</div>
  </div>
</div>
</body>
<%--<style>--%>
<%--#__vconsole{top:0;z-index:11199;position: absolute}#__vconsole .vc-switch{bottom:5rem;right:60%;}--%>
<%--</style>--%>
<%--<script src="/web/res/js/vconsole.min.js"></script>--%>
<script>
  //  var vConsole = new VConsole();
  var result = ZAjaxRes({url: "/appMsg/getNewAppMsgTypeInfo.user"});
  var classTy = ""
  if(result.code == "000000"){
      var result = result.data;
    console.log(result)
      $.each(result,function(i,n){
        if(n.tip=="0"){
          classTy ="messImg"
        }else if(n.tip=="1"){
          classTy="classTap"
        }else if(n.tip=="2"){
          classTy="syTap"
        }else if(n.tip=="3"){
          classTy="jytAP"
        }else if(n.tip=="4"){
          classTy="qttip"
        }else if(n.tip=="103"){
          classTy="hdtz"
        }
        var run ='';
        if(n.acount!="0"){
          $(".run").show();
          run = "<i class='run'>"+n.acount+"</i>"
        }
        var Mse = "<div class='messageType' data-istip ='"+n.tip+"' ><em class='"+classTy+"'></em>" +
        "<div class='rigBox'><p><i class='messTitle'>"+ n.title+"</i> <i class='messTime'>"+n.createTime+"</i></p>" +
        "<p> <i class='messCon'>"+n.content+"</i>"+run+" </p></div></div>";
        $(".messBon").append(Mse);
      });
  }

</script>
<script>
  $(function(){
    var result=ZAjaxRes({url: "/appMsg/checkIsHaveMsg.user"});
    if(result.code!= "000000"){
      $(".yjqk").addClass("on");
    }else{
      $(".yjqk").removeClass("on");
    };
  });

  $(".yjqk").click(function(){
    if( $(".yjqk").hasClass("on")){
      return;
    }
    pop2({"content": "加载中" , "status": "loadboxpoc",toggle:'true'});
    var results = ZAjaxRes({url: "/appMsg/emptyAppMsg.user"});
    if (results.code == "000000") {
      pop2({"content": "加载中" , "status": "loadboxpoc",toggle:'false'});
      pop1({"content": "清空成功" , "status": "normal", time: '2500'});
      $(".messTime,.run").empty();
      $(".run").hide();
      $(".messCon").html("暂无新消息");
      $(".yjqk").addClass("on");
    }else{
      pop2({"content": "加载中" , "status": "loadboxpoc",toggle:'false'});
      $(".yjqk").addClass("on");
    }
  })
  $(".messageType").click(function(){
    var istip  = $(this).attr("data-istip");
    toCourseInfo(istip);
  })
  /**
   *
   *  跳转至消息详情页面
   * @param id
   */
  function toCourseInfo(istip) {
      window.location.href = "/weixin/detailsMess?type=" + istip;
  };
</script>
</html>
