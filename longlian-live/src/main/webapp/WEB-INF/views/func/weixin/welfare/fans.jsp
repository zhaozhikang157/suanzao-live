<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>粉丝奖励</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/weixin.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
</head>
<body onload="load()">
<div class="paymentmethod" style="display: none">
    <div class="mask">
        <div class="bsf"><img src="/web/res/image/covers.png"></div>
        <p>请点击右上角</p>

        <p>通过【发送给朋友】</p>

        <p>邀请好友参与</p>
    </div>
</div>
<div id="wrapper">
    <div id="cont_box" style="padding-bottom:0">
        <!--福利 -->
        <div class="rulesbox">
            <img src="/web/res/image/gzyjpic.png" alt=""/>
            <ul class="fxtype">
                <li class="wxhy">
                    <a>
                        微信好友
                    </a>
                </li>

                <li class="friends">
                    <a>
                        朋友圈
                    </a>
                </li>
                <%--<li class="mdmfx">--%>
                <%--<a >--%>
                <%--面对面--%>
                <%--</a>--%>
                <%--</li>--%>
                <%--<li class="wbfx">--%>
                <%--<a >--%>
                <%--微博--%>
                <%--</a>--%>
                <%--</li>--%>
            </ul>
            <%--<p class="gdyqfs">--%>
            <%--更多邀请方式--%>
            <%--</p>--%>
            <%--<div class="morboxs">--%>

            <%--</div>--%>
        </div>
    </div>
</div>
</body>
<script>
    var json =null;
    function load() {
        setTimeout(function () {
            var myscroll = new iScroll("wrapper");
        }, 500);
        var para = {
            systemType: "CREATR_LIVE_ROOM",
        };
        share_h5(para);
        json = {
            'share_title':share_title,
            'share_desc':share_desc,
            'share_link':share_link,
            'imgUrl':imgUrl
        };
    }
    function goBack() {
        history.back(-1);
    }
    //    分享
    var flug = 0;
    $(function(){
        $(".wxhy,.friends").click(function () {
            if(isWeiXin()){
                $(".paymentmethod").show();
            }
        });
        $(".mask").click(function () {
            $(".paymentmethod").hide();
        })
        //调用原生方法；
        $(".wxhy").click(function(){
            json.share_type = 0;
            var jsonstr = JSON.stringify(json);
            myObj.shareToWx(jsonstr);
            return false;
        });
        $(".friends").click(function(){
            json.share_type = 1;
            var jsonstr = JSON.stringify(json);
            myObj.shareToPyq(jsonstr);
            return false;
        });
    });

</script>
</html>
