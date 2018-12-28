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
  <script src="/web/res/js/echarts.simple.min.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
  <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div class="wrapp">
  <div class="hed"></div>
  <div class="con">
    <div class="topBox">
      <%--<span class="user"><img src="/web/res/image/01_03.png"/></span>--%>
      <%--<p class="userTitle"></p>--%>
      <p class="sizeCole">你的子女力组成部分</p>
      <div class="tj"></div>
    </div>
    <div id="echear">

    </div>
        <img src="/web/res/image/0er.jpg" class="ewm">
  </div>
</div>
</body>
<script>
  var res = '${m.result}';
  var nickName = '${m.nickName}';
  var imgUrl = '${m.imgUrl}';
  $(".userTitle").html(nickName);
  $(".user img").attr("src",imgUrl);
  var list = [];
  for(var i= 0; i < JSON.parse(res).length;i++){
    var value =JSON.parse(res)[i].value;
    var title =JSON.parse(res)[i].title;
    list.push(
            {
              value:value,
              name:title
            }
    );
  }
  var myChart = echarts.init(document.getElementById('echear'));
  window.onresize = myChart.resize;
  option = {
    textStyle: {
      fontSize :27
    },
    color:['#62d1de','#54d6b6','#a6db69','#ffd454','#ffa361','#d1d1d1','#fe4a49','#224273'],
    series : [
      {
        type: 'pie',
        radius : '55%',
        center: ['50%', '45%'],
        data:list,
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          },
          normal:{
            label:{
              show: true,
              formatter: '{b}:{d}%'
            },
            labelLine :{show:true}
          }
        }
      }
    ]
  };
  myChart.setOption(option);
</script>
</html>


