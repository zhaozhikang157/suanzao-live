<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <title>系统消息</title>
  <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
  <script src="/web/res/js/jquery.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;background: #f7f7f7;">
<div id="wrapper" style="overflow: hidden; height:100%;">
  <div id="cont_box" style="overflow-y: auto;height: 100%;">
    <!--系统消息 -->
    <div class="systemmessige" id="selected" style="padding-bottom:2.45rem;">

    </div>
  </div>
</div>
<div class="bjBtn">编辑</div>
<div class="mesBj" >
  <p class="qxb"><span class="allgarden"></span><i>全选</i></p>
  <p class="sc"><i class="ca1">删除</i></p>
  <p class="qx"><i>取消</i></p>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
  var pageNum = 0, pageSize = 20;
  var typeUrl="";
  $(function(){
    Load();
    function getUrlParam(name) {
      var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
      var r = window.location.search.substr(1).match(reg);  // 匹配目标参数
      if (r != null) return unescape(r[2]); return null; // 返回参数值
    }
     typeUrl= getUrlParam('type');
    var results = ZAjaxRes({url: "/appMsg/updateStatusByMsgType.user?type="+typeUrl});

    /*直播 精选 下拉加载*/
    $('#cont_box').scroll(function(){
      var scrollTop = $(this).scrollTop();
      var windowHeight = $(this).height();
      var scrollHeight = $('.systemmessige').height();
      if(scrollTop + windowHeight >= scrollHeight-1){
        if($('.systemmessige .noData').length < 1){
          Load();
        }
      }
    });
  });
  var oBok = true;
  function Load() {
    if (oBok) {
      var result = ZAjaxRes({
        url: "/appMsg/getNewAppMsgTypeList.user?pageNum=" + pageNum + "&pageSize=" + pageSize+"&type=${msg}",
        type: "GET"
      });
      if (result.code == "000000") {
        if (result.data.length > 0) {
          var messageList = result.data;
          $.each(messageList, function (i, n) {
            if($(".allgarden").hasClass("on")){
              addLive(n,1);
            }else if($('.bjBtn').css('display') == 'none'){
              addLive(n,2);
            }else{
              addLive(n,0);
            }
          });
          if('103'=='${msg}'){   //暂时隐藏功能
            $('.bjBtn').hide();
          }
          pageNum = pageNum + result.data.length;
          if(result.data.length < 20){
            oBok = false;
          }
        } else {
          oBok = false;
        }
      }else{
        if($(".systemmessige>div").length < 1 && $('.systemmessige .noData').length < 1) {
          $('#selected').append('<div class="noData"></div>');
          $(".bjBtn").hide();
        };
      }
    }
  }
  /**
   * 系统消息展示
   * @param n
   */

  function addLive(n,k) {
    var kClass = '';
    var dis = '';
    var padd = '';
    var hbj = "";
    if(n.status=="0"){
      hbj= "<span class='hbj'></span>"
    }

    if(k == 1){
      kClass = ' on';
      dis = 'display:block;';
      padd = 'padding-left: 0px;'
    }else if(k ==2){
      dis = 'display:block;';
      padd = 'padding-left: 0px;'
    }else{
      kClass = '';
    }
    var mes = '<div class="sysnewBox'+kClass+'" data-id ="'+n.id+'" jumpHref=" '+ n.cAct+' "onclick="jumphref($(this))"><p class="garden'+kClass+'" style="'+ dis+'"></p><div class="sysRight" style="'+padd+'">' +
            '<p class="deMetop"><em class="deTitle">'+n.msgType+''+hbj+'</em><span class="deTime">'+n.createTime+'</span></p>' +
            '<i>'+n.content+'</i></div></div>';
          $("#selected").append(mes);
  }
  $(".bjBtn").click(function(){
    $(this).hide();
    $(".garden").show();
    $(".mesBj").css("display"," -webkit-box");
    $(".sysRight").css("padding-left",0);
  });
  $(".qx").click(function(){
    $(".mesBj,.garden").hide();
    $(".bjBtn").show();
    $(".allgarden,.sysnewBox,.ca1,.garden").removeClass("on");
  });
  $(".sc").click(function(){
    if($(".ca1").hasClass("on")){
      pop2({"content": "删除中" , "status": "loadboxpoc",toggle:'true'});
      var err=[];
      if($('.allgarden').hasClass('on')){
        $('.sysnewBox').hide();
        if($('.systemmessige .noData').length < 1 && $(".allgarden").hasClass("on")) {
          for(var i = 0;i <= $('.sysnewBox').length;i++){
            if($('.sysnewBox').eq(i).hasClass("on")){
              $('.sysnewBox').eq(i).hide();
              $(".ca1").removeClass("on");
              $(".mesBj").hide();
              $(".bjBtn").show();
              err.push($('.sysnewBox').eq(i).attr("data-id"));
            };
          }

        };
      }else{
        for(var i = 0;i <= $('.sysnewBox').length;i++){
          if($('.sysnewBox').eq(i).hasClass("on")){
            $('.sysnewBox').eq(i).hide();
            $(".ca1").removeClass("on");
            $(".mesBj,.garden").hide();
            $(".bjBtn").show();
            err.push($('.sysnewBox').eq(i).attr("data-id"));
          }
        }
      }
      var result = ZAjaxRes({url: "/appMsg/deleteAppMsgByIdsAndType.user", type: "POST",param:{ids:err.join(","),type:typeUrl}});
      if (result.code != "000000") {
        pop2({"content": "删除中" , "status": "loadboxpoc",toggle:'false'});
        pop1({"content": "删除成功" , "status": "normal", time: '2500'});
        $('#selected').append('<div class="noData"></div>');
        $(".bjBtn,.mesBj").hide();
      }else{
        pop2({"content": "删除中" , "status": "loadboxpoc",toggle:'false'});
        pop1({"content": "删除成功" , "status": "normal", time: '2500'});
      }
    }else{
      return;
    }
  });
  $(".qxb").click(function(){
    $(this).find(".allgarden").toggleClass("on");
    if($(".allgarden").hasClass("on")){
      $(".garden,.sysnewBox").addClass("on");
      if($(".garden,.sysnewBox").addClass("on")){
          $(".ca1").addClass("on");
      }
    }else{
      $(".garden,.sysnewBox").removeClass("on");
      $(".ca1").removeClass("on");
    }
  });

  function jumphref(that){
    if('103' == '${msg}'){return;}
    var display =$('.garden').css('display');
    if(display != 'none'){
      that.toggleClass("on").find(".garden").toggleClass("on");
    if(that,$(".garden").hasClass("on")){
      $(".ca1").addClass("on");
    }else{
      $(".ca1").removeClass("on");
    }
    }else{
      var h = that.attr("jumpHref");
      h = h.replace('+', '%2B');
      if(h == null || h == "" || h == undefined){
      }else{
        window.location.href = h;
      }
    }
    if(!that.hasClass("on")){
      $(".allgarden ").removeClass("on");
    }
  }
</script>
</html>
