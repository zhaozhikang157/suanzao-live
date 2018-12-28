<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>

<html class="indHtml">
<head>
  <%@include file="/WEB-INF/views/common/meta.jsp" %>
  <title>测试你的子女力组成</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/composs.css?nd=<%=current_version%>" />
  <script src="/web/res/js/jquery-2.1.0.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="wrap">
  <header class="head"></header>
  <div class="con_top">
    <ul>
      <li>
        <p  date-id="1"><img src="/web/res/image/01_03.png">你会经常想家吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="2"><img src="/web/res/image/01_06.png">你会陪爸妈参加聚会吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="3"><img src="/web/res/image/01_08.png">你会给他们准备生日礼物吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="4"><img src="/web/res/image/01_10.png">你经常和父母意见不同吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="5"><img src="/web/res/image/01_12.png">你能独自承担生活的压力吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
    </ul>
  </div>

  <div class="con_top con_bottom">
    <ul>
      <li>
        <p date-id="6"><img src="/web/res/image/01_15.png">你会主动和父母说自己的感情/事业吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="7"><img src="/web/res/image/01_18.png">你会把家里人的事情放在第一位吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="8"><img src="/web/res/image/01_20.png">你会经常回家吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p  date-id="9"><img src="/web/res/image/01_23.png">你会对父母说“我爱你”吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <li>
        <p date-id="10"><img src="/web/res/image/10.png">你会欣然接受父母的建议吗？</p>
        <span date-orgin="0">是</span>
        <span date-orgin="1">否</span>
      </li>
      <span class="upBtn"></span>
    </ul>
    <div class="lefpic"></div>
    <div class="rightpic"></div>
  </div>
</div>
</body>
<script>
  var err = "";
  var arr =[];
  $("ul li span").click(function(){
    $(this).addClass("on").siblings("span").removeClass("on");

    if($(this).hasClass("on")){
      var cont = $(this).attr("date-orgin");
      var title = $(this).parent("li").find("p").attr("date-id");
      arr.push({'title':title,'cont':cont});
      err += title+":"+cont+",";
    }
  });
  $(".upBtn").click(function(){
    if(arr.length<10){
      pop({"content": "有未完成课题" , "status": "normal", time: '2000'});
      return;
    }else{
      window.location.href = '/festival/doAnswer?ans='+decodeURIComponent(err);
    }
  })
</script>
</html>


