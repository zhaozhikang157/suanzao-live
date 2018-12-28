<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html class="indHtml">
<head>
  <meta name="Keywords" content=""/>
  <meta name="Description" content=""/>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <title>编辑单节课</title>
  <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>" />
  <link rel="stylesheet" type="text/css" href="/web/res/pc/bootstrap/css/bootstrap.min.css?nd=<%=current_version%>"/>
  <link rel="stylesheet" type="text/css" href="/web/res/css/pcIndex.css?nd=<%=current_version%>"/>
</head>
<body>
<div class="singleBox" style="margin-top: 30px">
  <form class="form-inline">
    <div class="singleCont">
      <div class="publicBox coverFigure">
        <label><span class="coverTap">*</span>封面图 :</label>
        <div class="picBox">
          <img class="pic" id="img2" src="/web/res/image/timg.jpg"/>
        </div>
      </div>
      <div class="publicBox coverName">
        <label><span class="coverTap">*</span>课程名称 :</label>
        <input type="text" class="form-control" id="" placeholder="请填写课程名称">
      </div>
      <div class="publicBox timeBox">
        <label><span class="coverTap">*</span>开课时间 :</label>
        <input type="text" class="form-control"><span class="midText">至</span><input type="text" class="form-control">
        <p><label><span class="coverTap"></span></label>注意与直播开始时间之差要大于上传内容时长; 审核后方可开播，为了能顺利开播，建议开播时间设置在12小时以后。</p>
      </div>
      <div class="publicBox loadBox">
        <label>上传课件 :</label>
        <div class="imgBox">
          <div class="addImg">
            <div class="pic">
              <img src="/web/res/image/timg.jpg" alt=""/>
              <span class="closebtn"></span>
            </div>
            <p class="btn" id="addBtn"></p>
          </div>
        </div>
      </div>
      <div class="publicBox introduction">
        <label>课程介绍 :</label>
        <div class="texreBox">
          <textarea class="form-control"placeholder="请输入课程介绍"></textarea>
        </div>
      </div>
      <div class="publicBox">
        <label></label>
        <div class="wordBox">
          <div class="contBoxall">
            <div class="wordCont">
              <dl>
                <dt><img src="/web/res/image/timg.jpg"></dt>
                <dd>
                  <textarea placeholder="请输入图片介绍"></textarea>
                </dd>
              </dl>
            </div>
            <div class="rightBox">
              <span class="oPrev"></span>
              <span class="oNext"></span>
            </div>
          </div>
          <div class="contBoxall">
            <div class="wordCont">
              <dl>
                <dd>
                  <textarea placeholder="请输入图片介绍"></textarea>
                </dd>
              </dl>
            </div>
            <div class="rightBox">
              <span class="oPrev"></span>
              <span class="oNext"></span>
            </div>
          </div>

        </div>
      </div>
      <div style="clear: both"></div>
      <div class="publicBox">
        <label></label>
        <ul class="lowbtn">
          <li>上传图片</li>
          <li>上传文字</li>
        </ul>
      </div>
      <div style="clear: both"></div>
      <div class="publicBox">
        <label></label>
        <p class="seveBtn">保存</p>
      </div>
    </div>
  </form>
</div>
</body>
<script src="/web/res/js/jquery-1.7.2.js"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
</html>