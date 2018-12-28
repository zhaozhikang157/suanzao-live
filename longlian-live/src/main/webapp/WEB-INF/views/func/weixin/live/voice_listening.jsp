<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background:#fff;">
<head>
  <meta charset="UTF-8">
  <meta name="Keywords" content="" />
  <meta name="Description" content="" />
  <title>${liveTopic}</title>
  <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/liveAudioListening.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/animate.css?nd=<%=current_version%>"/>
  <script type="text/javascript" src="/web/res/js/jquery-1.7.2.js"></script>
  <script type="text/javascript" src="/web/res/js/jquery.cookie.js"></script>
  <script src="/web/res/js/handlerOutJs.js?nd=<%=current_version%>"></script>
  <script src='/web/res/js/filter.js?nd=<%=current_version%>'></script>
  <%--<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>--%>
  <script type="text/javascript">
    var courseId = '${courseId}';
    var isFollow = '${isFollow}';
    var _userId = '${userId}';
    var _userName = '${userName}';
    var _photo = '${photo}';
    var sourcesId = '${sourcesId}';
    var _coverssAddress = '${coverssAddress}';
    var _trySeeTime = '${trySeeTime}';
    var _appKey = '${appKey}';
    var _roomAddr = ${roomAddr};
    var _yunxinToken = '${yunxinToken}';
    var _chatRoomId = '${chatRoomId}';
    var _isEnd = '${isEnd}';
    var playEndAndLoadEnd = 0 ;
  </script>
</head>
<body style="-webkit-overflow-scrolling: touch">
<div class="content_h">
  <%--课件--%>
  <div class="brief_courseware ">
      <div class="courseware_bg"></div>
      <div class="swiper-container" id="brief_toppic">
        <div class="swiper-wrapper">
        </div>
        <div class="swiper-pagination toppic_pag" style="font-size:0.6rem;border-radius:5px; left:0.3rem;bottom:1.6rem; color:#FFF; background:rgba(0,0,0,0.48);width:1.8rem;height: 1.05rem;line-height: 1.05rem;"></div>
      </div>
  </div>
  <%--课程名称--%>
  <div class="brief_title">
    <span></span>
    <em></em>
  </div>
  <%--极简模式--%>
  <div class="brief_box">
    <%--进度条--%>
    <div class="audio_player_box">
      <div class="control-bar">
              <span class="audioBar">
                  <em style="width: 0.1%;" data-width="0">
                    <i></i>
                  </em>
              </span>
      </div>
      <div class="audio_times">
        <div>00:00:00</div>
        <div>00:00:00</div>
      </div>
    </div>
    <%--按钮--%>
    <div class="audio_player_btn">
      <span class="btn_course_list" data-name="课程列表"><img src="/web/res/image/newaudio/briefaudio/icon_list.png">听课列表</span>
      <div class="audio_player_main">
        <div class="btn-prev on" data-name="上一条"></div>
        <span class="btn-play " id="topic-audio-play" data-name="播放"></span>
        <div class="btn-next on" data-name="下一条"></div>
      </div>
      <span class="btn_tradition_list" data-name="上课模式"><img src="/web/res/image/newaudio/briefaudio/icon_con.png">上课模式</span>
    </div>
  </div>
  <div class="brief_coursenew">
      <div class="teacherxq" onclick="enters()">
        <div class="teacherzl">
          <div class="teacherlog"></div>
          <div class="thname">
            <p class="topteacherbox"><i></i></p>
            <%--<p class="btomteacherbox"></p>--%>
          </div>
          <span class="newMore"></span>
        </div>
      </div>
         <p class="pucmode">
        <span>课程简介</span>
      </p>
      <div class="logbox">
        <div class="livetext"></div>
        <div id="courseImgList"></div>
      </div>
  </div>
  <%--课程列表--%>
  <div class="course_portal_container">
    <div class="course_items">
      <div class="course_Tit">课程列表</div>
      <ul class="course_list">
        <li><i></i>最强大脑人气选手教你高效记单词</li>
        <li class="on">最强大脑人气选手教你高效记单词</li>
        <li>最强大脑人气选手教你高效记单词</li>
        <li class="on">最强大脑人气选手教你高效记单词</li>
        <li>最强大脑人气选手教你高效记单词</li>
      </ul>
      <div class="course_close">关闭</div>
    </div>

  </div>
</div>
<audio id="audio" preload="auto"  autoplay="autoplay"></audio>
<audio id="audioNext" preload="auto"></audio>
<%--<script src="/web/res/js/NIM_Web_SDK_v3.3.0.js?nd=<%=current_version%>"></script>--%>
<%--<script src='/web/res/js/NIM_Web_NIM_v3.3.0.js?nd=<%=current_version%>'></script>--%>
<%--<script src='/web/res/js/NIM_Web_Chatroom_v3.3.0.js?nd=<%=current_version%>'></script>--%>
<script src="/web/res/js/fastclick.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/base.js?nd=<%=current_version%>"></script>
<%--<script src="/web/res/js/bscroll.min.js?nd=<%=current_version%>"></script>--%>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/vconsole.min.js?nd=<%=current_version%>'></script>
<script src='/web/res/live/liveAudioListening.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/tools.js?nd=<%=current_version%>'></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?nd=<%=current_version%>"></script>
<script src='/web/res/js/weixin.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pay.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/pop.js?nd=<%=current_version%>'></script>
<script src='/web/res/js/form.js?nd=<%=current_version%>'></script>
</body>
</html>
