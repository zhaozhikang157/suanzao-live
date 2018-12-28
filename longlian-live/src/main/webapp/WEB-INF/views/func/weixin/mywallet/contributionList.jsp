<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>贡献排行榜</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper"style=" overflow: auto; height:100%;background: white">
  <div id="cont_box" style="padding-bottom: 0">
    <div class="contrbutBox" style="min-height: 33rem">
          <ul class="contlistbox">

          </ul>
    </div>
    <div class="pullUpLabel">上拉加载更多</div>
  </div>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script>
  var courseId = ${courseId};
  var offset = 0;
  $(function(){
    getUserRewardSort();
    /*直播 精选 下拉加载*/
    $('#wrapper').scroll(function(){
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('#cont_box').height();
      if(scrollTop + windowHeight >= scrollHeight-1){
        Load();
      }
    });
  });

  var oBok = true;
  function Load() {
    if (oBok) {
          getUserRewardSort();
      /****   ---DOM  改变后重新计数高度 --- ****/
    } else {
      if (!oBok) {
        $('.pullUpLabel').show();
        $('.pullUpLabel').text('加载已经完毕！');
      }
    }
  }

  function getUserRewardSort(){
      var offset = $(".contlistbox li").length;
      var result = ZAjaxRes({url: "/userRewardRecord/getUserRewardSort" ,type:"POST" ,param:{courseId : courseId ,offset  : offset  } });
      if(result.code == "000000"){
        var data = result.data;
        if(result.data.length<1){
            $('.pullUpLabel').show();
            $('.pullUpLabel').text('加载已经完毕！');
            //如果没有了 oBok 设false
            oBok = false;
            if(offset=='0'){
                $('.contrbutBox').append('<div class="noData"></div>');
            }
            return;
        }
        $.each(result.data, function (i, item){
          var obj = data[i];
          var sort = offset + i + 1;
          var sortDesc = "";
          var temp = "";
         if(sort == 1){
            sortDesc = "goldmedal";
          } else if(sort == 2){
            sortDesc = "silvermedal";
          }else if(sort == 3){
            sortDesc = "bronzemedal";
          }
          if(sort > 3){
            temp = '<i class="goldmedal">' + sort + '</i>';
          }else{
            temp =  ' <i class="goldmedal" style="background: url(/web/res/image/' +sortDesc + '.png) no-repeat center center;background-size: 100% 100%"></i>';
          }
          var li ='<li> '+
                  temp +
                  '<i class="conuespic" style="background: url(' + obj.photo + ') no-repeat center center;background-size: 100% 100%"></i>'+
                  '<p>'+
                  '<span class="conusetitle">' + obj.name + '</span>'+
                  '<span class="contriBution">贡献值:' + obj.amount + '学币</span>'+
                  '</p>'+
                  '</li>';
          $(".contlistbox").append(li);
        });
      }else{
        alert(result.code);
      }
  }
</script>
</html>
