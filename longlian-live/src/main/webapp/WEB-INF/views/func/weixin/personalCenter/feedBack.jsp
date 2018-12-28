<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>意见反馈</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>
<body>
<div class="complaint">
  <!--意见反馈 -->
  <form  method="post"  onsubmit="return false" >
    <div class="complbox">
      <textarea class="jbtext" id="content" placeholder="请输入你要反馈的内容（不支持emoji表情）"></textarea>
    </div>
    <p class="btnbox">
      <button class="tjbtn">确认提交</button>
      <%--<button class="qxbtn">取消</button>--%>
    </p>
  </form>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/filter.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/emoji.js?nd=<%=current_version%>"></script>
<script>
  var id = '${id}';
  $(".tjbtn").on('click',function(){
    var content = $("#content").val();
    if(content==""){
      alert("提交内容不能为空");
      return;
    }
    if(isEmojiCharacter(content)){
      alert("不支持emoji表情输入");
      return;
    }
    var result = ZAjaxRes({url: "/comment/createComment.user?comment="+filter(content), type: "GET"});
    if(result.code=="000000"){
      pop1({"content": "提交成功" , "status": "normal", time: '2500'});
      setTimeout(function () {
        window.location.href="/weixin/setUp";
      }, 1000);
    }else{
      alert("提交失败");
    }

  });
</script>
</html>
