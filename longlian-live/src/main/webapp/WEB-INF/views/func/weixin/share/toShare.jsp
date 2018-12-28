<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>分享达人</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/pullToRefresh.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pullToRefresh.js?nd=<%=current_version%>"></script>
</head>
<body>
<div class="paymentmethod" style="display: none">
    <div class="mask" style="display: none">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>
        <p>通过【发送给朋友】</p>
        <p>邀请好友参与</p>
    </div>
</div>
<!--分享达人 -->
<div class="sharers">
    <div class="sharebox">
        <dl>
            <dt id="headPhoto"
                style="background: url(/web/res/image/01.png) no-repeat center center;background-size:100% 100% ;"></dt>
            <dd>
                <p><i class="data">我</i><i class="noph">未进入排行</i></p>

                <p>推荐了<i class="tjnub" id="count"></i>个朋友来听课</p>
            </dd>
        </dl>
        <p class="gofx">去分享</p>
    </div>
</div>
<div id="wrapper" style="background: white">
    <div id="cont_box">
        <div id="firstbox">

        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
<script>
    var id = '${id}';
    toshare();
    var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
    if (result.code = "000000") {
        $("#headPhoto").attr("style", "background: url(" + result.data.headPhoto + ") no-repeat center center;background-size:100% 100% ;");
        $("#count").text(result.data.recommendNum);
    }
    $(function () {
        loaded();
    });
    /*下拉加载*/
    refresher.init({
        id: "wrapper",
        pullUpAction: loaded
    });
    var pageNum = 0;
    var pageSize = 10;
    var oBok = true;
    function loaded() {
        if (oBok) {
            $.ajax({
                url: "/accountTrack/getDistributionMasterList.user?courseId=" + '${id}' + "&pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: 'POST',
                dataType: 'json',
                success: function (result) {
                    if (result.code == "000000") {
                        if (result.data.length > 0) {
                            $.each(result.data, function (index, item) {
                                $("#firstbox").append(" <div class='isgood'><p class='leftsbox'><span class='tx' style='background: url(" + item.photo + ") no-repeat center center;background-size:100% 100% ;'></span></p>" +
                                        " <div class='isbox'><p>" + item.name + "</p><p class='rightsbox'><span>推荐了</span> <span><i class='pepoles'>" + item.count + "</i>个朋友来听课</span></p></div></div>");
                            });
                            pageNum += 10;
                        } else {
                            $('.pullUpLabel').text('没有更多内容了！');
                            $('.pullUp').removeClass('loading');
                            oBok = false;
                        }
                    }
                }
            });
            wrapper.refresh();
        }
        else {
            $('.pullUpLabel').text('没有更多内容了！');
            $('.pullUp').removeClass('loading')
        }
    }

    //分享
    $(".gofx").click(function () {
        $(".paymentmethod").show();
        $(".mask").show();
        toshare();
    });

    $(".paymentmethod").bind("click", function (e) {  //点击对象
        var target = $(e.target);//表示当前对象，切记，如果没有e这个参数，即表示整个BODY对象
        if (target.closest(".dister,.contnt").length == 0) {
            $(".paymentmethod").hide();
            $(".mask").hide();
        }
    });
    //获取邀请卡路径
    function getUrl() {
        var result = ZAjaxRes({url: "/user/invitationCard.user?courseId=" + id, type: "GET"});
        return result.data.url;
    }

    function toshare(){
        var param = {
            liveRoomId: id,
            systemType: "COURSE",
            photoUrl: getUrl()
        };
        share_h5(param);//分享
    }

</script>
</html>