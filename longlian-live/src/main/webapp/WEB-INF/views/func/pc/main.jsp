<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <link rel="stylesheet" type="text/css" href="/web/res/css/general.css">
  <script type="text/javascript" src="/web/res/jquery/jquery.min.js"></script>
  <script type="text/javascript" src="/web/res/js/index.js"></script>

  <title>酸枣</title>
</head>

<body>
<!-- 头部 -->
<div id="header">
  <div class="logo" title="">酸枣</div>
  <!-- 用户信息 -->
  <div class="userInfo">
    <img src="${user.photo}">
    <span>${user.name}</span>
    <div class="SignOut">退出登录</div>
  </div>
</div>
<!-- 主体 -->
<div id="main">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <!-- 左侧栏 开始 -->
      <td id="sidebar" >
        <div class="sidebar_nav">
          <ul>
            <li class="on" onclick="tabs('/pcCourse/index.user' ,this)"><span></span>课程列表</li>
            <li  onclick="tabs('/inviCode/index.user' ,this)"><span></span>邀请码管理</li>
          </ul>
        </div>
      </td>
      <!-- 左侧栏 结束 -->
      <!-- 主体中心区域 -->
      <td valign="top">
        <div id="main_conetnt">
          <iframe id="iframe" style="height: 100%; width: 100%; display: inline;" frameborder="0"  src="/pcCourse/index.user"></iframe>
        </div>
      </td>
    </tr>
  </table>
</div>
</body>
<script>
  function tabs(url ,obj){
      $(".sidebar_nav li").removeClass('on');
      $(obj).addClass("on");
      $('#iframe').attr('src',url);
  };
  $(function(){
    $('.userInfo').click(function(){
       $('.SignOut').show();
    });
    $(document).bind("click",function(e){  //点击对象
      var target  = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
      if(target.closest(".userInfo").length == 0){
        $('.SignOut').hide();
      }
    });
  });

  $('.SignOut').click(function(){
      $.get("/teacher/loginOut.user",function(data,status){
          window.location.href='/pc/login';
      });

  });
</script>
</html>
