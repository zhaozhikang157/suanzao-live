<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>设置</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <script src="/web/res/js/iscroll.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>

</head>
<body>
<div id="wrapper">
    <div class="vxbox">
        <div class="vxcont">
            <i class="vxclosebtn"></i>
            <dl class="vxtitle">
                <dt></dt>
                <dd>
                    <p>酸枣售后<img src="/web/res/image/peopleuse.png"></p>
                    <i>微信号:LLsuanzao</i>
                </dd>
            </dl>
            <img class="wetwer" src="/web/res/image/setewr.png" alt=""/>
            <p class="vxbottomtext">长按识别名片</p>
        </div>
    </div>
    <div id="cont_box">
        <div class="division">
            <div class="xzbox" id="binding">
                <a href="javascript:void(0);">
                    <dl>
                        <dd class="firstd" style="text-indent: 0.4rem"><i>账号与安全</i></dd>
                    </dl>
                    <p class="btell">
                        <i class="telldisplay">${mobile}</i>
                        <span></span>
                    </p>
                </a>
            </div>

            <%--<div class="xzbox" id="systemMessage">--%>
                <%--<a href="javascript:void(0);">--%>
                    <%--<dl>--%>
                        <%--<dd class="firstd" style="text-indent: 0.4rem"><i>系统消息</i> <i id="appMsg"></i></dd>--%>
                    <%--</dl>--%>
                    <%--<span></span>--%>
                <%--</a>--%>
            <%--</div>--%>

            <div class="xzbox">
                <a href="/weixin/aboutUs">
                    <dl>
                        <dd style="text-indent: 0.4rem">关于我们</dd>
                    </dl>
                    <span></span>
                </a>
            </div>
            <div class="xzbox">
                <a href="/weixin/feedBack">
                    <dl>
                        <dd style="text-indent: 0.4rem">意见反馈</dd>
                    </dl>
                    <span></span>
                </a>
            </div>
        </div>
    </div>
    <ul class="tellcall">
        <li><img src="/web/res/image/tellCall.png"><a href="tel:400-116-9269">客服热线</a></li>
        <p class="telline"></p>
        <li class="serviceCus"><img src="/web/res/image/vxpic.png"><a href="javascript:void(0);">微信客服</a></li>
    </ul>
</div>
<script>
    var mobile = '${mobile}';
    var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
    if (result.code == "000000") {
        //判断是否有未读消息
        if (result.data.isAppMsg > 0) {
            $("#appMsg").addClass("ju");
        } else {
//      pop(result);
        }
    }
    //读取系统消息并跳转
    $("#systemMessage").click(function () {
//        var result = ZAjaxJsonRes({url: "/appMsg/readAllMessage.user", type: "GET"});
        $(".ju").hide();
        window.location.href = "/weixin/messageType.user";
    });

    $("#binding").click(function () {
        window.location.href = "/weixin/resetPassword?mobile="+mobile;
    })
    $(".vxclosebtn").click(function(){
        $(".vxbox").hide();
    });
    $(".serviceCus").click(function(){
        $(".vxbox").show();
    });
    $(".vxbox").bind("click",function(e){
        var target  = $(e.target);
        if(target.closest(".vxcont").length == 0){
            $(".vxbox").hide();
        }
    });

    $(function(){
        if(mobile == ''){
            $(".telldisplay").empty();
            return;
        }else{
            $(".telldisplay").html(mobile.substring(0,3)+"****"+mobile.substring(7,11));
        }
        var location = window.location.href;
        if(location.indexOf("shareOK") > 0){
            pop({"content": "设置成功" ,width:"6rem",  "status": "normal", time: '2500'});
        }
    })
</script>
</body>
</html>
