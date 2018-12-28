<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>关注的直播间</title>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
</head>

<body style="-webkit-overflow-scrolling:touch; ">
<div id="wrapper" style=" overflow: auto; height:100%;">
  <div id="cont_box" style="padding-bottom:.2rem; overflow:hidden; background:#FFF; ">
    <!--关注的直播间 -->
    <div class="reporMain" style="overflow:hidden; background:#FFF;">
      <div style="overflow: auto">
        <ul class="reportbox" id="selected">

        </ul>
      </div>
    </div>
  </div>
</div>
</body>

<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
var pageNum = 1, pageSize = 20;
var oBok = true;
var newfollows;
var repottext;
function clickFollow(that){
  var liveRoomId = $(that).attr("liveRoomId");
  var number = parseFloat( $(that).prev('dd').find('.repred').html());
  if($(that).hasClass("on")){
    var result = ZAjaxJsonRes({url: "/userFollow/cancelFollow.user?liveRoomId=" + liveRoomId, type: "GET"});
    if (result.code == "000000") {
      $(that).removeClass("on");
      $(that).text("关注")
      pop1({"content": "取消关注成功" , "status": "normal", time: '2500'});
      number--;
      $(that).prev('dd').find('.repred').html(number);
    }
  }else{
    var result = ZAjaxJsonRes({url: "/userFollow/follow.user?liveRoomId=" + liveRoomId, type: "GET"});
      if (result.code == "000000" || result.code == "000111") {
      $(that).addClass("on");
        $(that).text("已关注")
        pop1({"content": "关注成功" , "status": "normal", time: '2500'});
        number++;
        $(that).prev('dd').find('.repred').html(number);
    }
  }
}
  $(function(){
    Load();
    $('#wrapper').scroll(function(){
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('#cont_box').height();
      if(scrollTop + windowHeight >= scrollHeight-1){
        Load();
      }
    });
  });

function Load() {
  if(oBok){
    var result = ZAjaxRes({url:"/userFollow/followLiveRoom.user?pageNum="+pageNum+"&pageSize="+pageSize , type:"GET"});
    if(result.code == "000000") {
      console.log(result.data);
      if(result.data.length>0){
        $.each(result.data, function(i, n){
          //获取直播间关注人数
          $("#selected").append("<li><p class='reportpic'style='background: url("+ n.photo+") no-repeat center center;background-size:100% 100% '></p>" +
                  "<dl>" +
                  "<dd onclick='toliveRoom("+ n.id+")'>" +
                  "<span class='livenames'>"+n.name+"</span>" +
                  "<span class='concernnumber'><i class='repred'>"+n.count+"</i>人关注</span>" +
//                  "<i class='repotlivetext'>"+ n.remark+"</i>" +
                  "</dd>" +
                  "<dt class='newfollows on' liveRoomId=" + n.id + " onclick='clickFollow(this)'>已关注</dt>"+
                  "</dl>" +
                  "</li>");
        });
        if(result.data.length<10){
          oBok = false;
        }
        pageNum++;
      }else{
        if(pageNum==1){
          $("#selected").append('<div class="noData"></div>');
        }
        //如果没有了 oBok 设false
        oBok = false;
      }
    }
  }
}

  function toliveRoom(id){
    window.location.href="/weixin/liveRoom?id="+id;
  }
</script>
</html>
