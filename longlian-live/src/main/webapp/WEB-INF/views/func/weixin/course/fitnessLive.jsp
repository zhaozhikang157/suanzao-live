<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html style="background: #eeeeee">
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>大美黄石 2017 全国健身健美公开赛高清直播</title>
    <script src="/web/res/js/access-meta.js" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/fit_live.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
</head>
<body style="overflow-x:hidden; -webkit-overflow-scrolling: touch;">
<!-- 课程详情 -->
<div id="wrapper" >
    <div id="cont_box">
		<div class="fit_big">
        	<ul id="fitul">

            </ul>
        </div>
        <div id="fit_min">
            <div class="swiper-wrapper">

            </div>
        </div>
        <div class="fitNumber "><span>观看人次：<em id="visitCount">1257</em></span>&nbsp;&nbsp;&nbsp;&nbsp; <span>铁粉人数：<em id="studyCount">1200</em></span></div>
        <div class="fitDL">
        	<dl>
            	<dt><img src="/web/res/image/fitImg/fitImg_4.jpg"></dt>
                <dd>
                	<span>2017-6-24  9:00</span>
                    <strong>健人传媒</strong>
                </dd>
            </dl>
        </div>
        <div class="fitXQ">
        	<img src="/web/res/image/fitImg/fitImg_xq.jpg">
        </div>
    </div>
</div>
<div class="fitbtn"></div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pay.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/setting_pwd.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
<script>
    var seriesid = 1333;
    var isSeries = 0;
	var invitationAppId = '${invitationAppId}';
    var courseId = "${courseId}";//课程ID
    var _this = null;
    $(function(){
//		share_h5({systemType: 'COURSE', liveRoomId: seriesid ,isSeries:1,seriesid: 0 });//分享
		share_h5({systemType: 'COURSE_FIXED', liveRoomId: seriesid ,isSeries:1,seriesid: seriesid});//分享
		//tab切换
		/*课程列表*/
		//课程切换
		$('#fit_min').on('click','.swiper-slide',function(){
			var _index = $(this).index();
			$('.swiper-slide').removeClass('on');
			$(this).addClass('on');
			$('#fitul li').hide();
			$('#fitul li').eq(_index).show();
		});
		
		$('.fitbtn').click(function(){
			if($(this).hasClass('on')){
				$(this).removeClass('on');
				$('#wrapper').scrollTop(0);
			}else{
				$(this).addClass('on');
				$('#wrapper').scrollTop( $('#wrapper')[0].scrollHeight );
			}
		});
		
		var result = ZAjaxRes({url: "/series/getSeriesCourseInfo?seriesid=" + seriesid, type: "GET"});
		if (result.code == "000000") {
			var data = result.data;
			var seriesCourse = data.seriesCourse;
			$("#visitCount").html(seriesCourse.visitCount);
			$("#studyCount").html(seriesCourse.studyCount);
		}
		  
		//课程详情
	  	var results = ZAjaxRes({
	  		url: "/series/getMyCourseList?offset=" + 0 + "&pageSize=" + 10 + "&seriesid=" + seriesid,
	  		type: "GET"
        });
        if (results.code == "000000") {
        	var courseList = results.data;
			console.log(results)
			if (courseList.length > 0) {
				$.each(courseList, function (i, n) {
					var liveTimeStatus = n.liveTimeStatus;//直播时间状态 0-预告 1-直播中 2-结束的

					if(liveTimeStatus == '1'){
                        var liveTimeStatusDesc = "正在直播";
                        _this = courseList.length-i-1;
                    }
					var liveTimeSta = "air";
					if (liveTimeStatus == '0') {
					    liveTimeSta = "notstarted";
					    liveTimeStatusDesc = "未开播";
					} else if (liveTimeStatus == '2') {
					    liveTimeSta = "isover";
					    liveTimeStatusDesc = "回放";
					}
					var oLi = ('<li c_id=' + n.id +'><img src="'+n.coverssAddress+'"><span></span></li>');
					var oDiv = ('<div class="swiper-slide" >\
									<img src="'+n.coverssAddress+'">\
									<span>'+n.startTime.substring(5)+'</span>\
									<i>'+liveTimeStatusDesc+'</i>\
								</div>');
					$('#fitul').prepend(oLi);
					$('.swiper-wrapper').prepend(oDiv);
				});
				if(_this!=null){
                    $('#fitul li').hide();
                    $('#fitul li').eq(_this).show();
                    $('.swiper-slide').removeClass('on');
                    $('.swiper-slide').eq(_this).addClass('on');
                }else{
                    $('#fitul li').hide();
                    $('#fitul li').eq(0).show();
                    $('.swiper-slide').removeClass('on');
                    $('.swiper-slide').eq(0).addClass('on');
                }

			}
		}
		$('#fitul').on('click','#fitul li',function(){
			var param = {payType: "07", password: "", amount: '0', courseId: seriesid, deviceNo: ""  , isBuy:'0'};
      		var result = paying(param);
			courseInfo($(this));
		});
		function courseInfo(obj) {
			courseId = $(obj).attr("c_id");
			window.location.href = "/weixin/index.user?courseId="+courseId;
		};
		var rewardList = new Swiper('#fit_min', {
			slidesPerView: 4,
			paginationClickable: true,
			spaceBetween: 0,
		});
	});
</script>
</html>
