<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>单节课收益统计</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
</head>
<body>
<!--单节课收益统计 -->
<div class="single">
  <ul class="singletap">
    <li class="on">直接付费</li>
    <li>间接付费</li>
  </ul>
  <div class="singtab">
    <div class="on">
      <div class="tableDiv">
        <table border="" cellspacing="" cellpadding="">
          <thead>
          <tr align="center">
            <td>付费人</td><td>付费额</td><td>购买时间</td><td>实际收入</td>
          </tr>
          </thead>
          <tbody>
          <tr align="center">
            <td><img src="/web/res/image/01.png"><span></span></td><td>￥10</td><td><i>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/01.png"><span></span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/01.png"><span></span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/01.png"><span></span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!--间接付费-->
    <div>
      <div class="tableDiv">
        <table border="" cellspacing="" cellpadding="">
          <thead>
          <tr align="center">
            <td>付费人</td><td>付费额</td><td>购买时间</td><td>实际收入</td>
          </tr>
          </thead>
          <tbody>
          <tr align="center">
            <td><img src="/web/res/image/timg.jpg"><span>我的世界1</span></td><td>￥10</td><td><i>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/timg.jpg"><span>我的世界1</span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/timg.jpg"><span>我的世界1</span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          <tr align="center">
            <td><img src="/web/res/image/timg.jpg"><span>我的世界1</span></td><td>￥10</td><td>2017-2-12</i> <i>14:22</i></td><td>￥5</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
</body>
<script>
  $(".singletap li").click(function(){
    var index = $(this).index();
    $(this).addClass("on").siblings().removeClass("on");
    $(".singtab>div").eq(index).addClass("on").siblings().removeClass("on");
  })
</script>
</html>