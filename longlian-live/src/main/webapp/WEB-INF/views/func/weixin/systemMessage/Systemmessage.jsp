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
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch; background:#FFF;">
<div id="wrapper" style="overflow-x: hidden;overflow-y: auto; height:100%; background:#FFF;">
    <div id="cont_box" style="padding-bottom:1rem; background:#FFF;">
        <!--系统消息 -->
        <div class="systemmessige" id="selected" style="min-height:38rem">

        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var pageNum = 0, pageSize = 20;
    $(function(){
        Load();
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
            var result = ZAjaxRes({
                url: "/appMsg/getAppMsgList.user?pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: "GET"
            });
            if (result.code == "000000") {
                if (result.data.length > 0) {
                    var messageList = result.data;//推荐
                    $.each(messageList, function (i, n) {
                        addLive(n);
                    });
                    pageNum = pageNum + result.data.length;
                    if(result.data.length < 20){
                        oBok = false;
                    }
                } else {
                    oBok = false;
                }
            } else {
                oBok = false;
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        }
    }
    /**
     * 系统消息展示
     * @param n
     */
    function addLive(n) {
        var el, li, i;
        el = document.querySelector("#selected");
        li = document.createElement('div');
        li.setAttribute("class","messlist");
        li.innerHTML ='\
        <div class="messlistbox" onclick="jumphref(this)" jumpHref=" '+ n.cAct+' ">\
        <p class="uesrpic" style="background: url(/web/res/image/newsicon@2x.png) no-repeat center center;background-size: 100% 100%"></p>\
            <div class="rightmessbox">\
            <p><i class="messtitle"> '+ n.msgType+'</i><span class="messtime"> '+ n.createTime+'</span></p>' +
                '<p class="messtext">' + n.content + ' </p>' +
                '</div>' +
                '</div><div class="line-btn-delete" liveRoomId=' + n.id + '  onclick="deletebtn(this)"><i>删除</i></div>'
        ;
        el.appendChild(li, el.childNodes[0]);
          xuanran();
    }

    function xuanran(){
        // 设定每一行的宽度=屏幕宽度+按钮宽度
        $(".messlist").width($(".systemmessige").width() + $(".line-btn-delete").width());
        // 设定常规信息区域宽度=屏幕宽度
        $(".messlistbox").width($(".systemmessige").width());

        // 获取所有行，对每一行设置监听
        var lines = $(".messlist");
        var len = lines.length;
        var lastX, lastXForMobile;

        // 用于记录被按下的对象
        var pressedObj;  // 当前左滑的对象
        var lastLeftObj; // 上一个左滑的对象

        // 用于记录按下的点
        var start;

        // 网页在移动端运行时的监听
        for (var i = 0; i < len; ++i) {
            lines[i].addEventListener('touchstart', function(e){
                lastXForMobile = e.changedTouches[0].pageX;
                pressedObj = this; // 记录被按下的对象

                // 记录开始按下时的点
                var touches = event.touches[0];
                start = {
                    x:touches.pageX, // 横坐标
                    y:touches.pageY  // 纵坐标
                };
            });

            lines[i].addEventListener('touchmove',function(e){
                // 计算划动过程中x和y的变化量
                var touches = event.touches[0];
                start = {
                    x:touches.pageX, // 横坐标
                    y:touches.pageY  // 纵坐标
                };
                delta = {
                    x: touches.pageX - start.x,
                    y: touches.pageY - start.y
                };

                //横向位移大于纵向位移，阻止纵向滚动
                if (Math.abs(delta.x) > Math.abs(delta.y)) {
                    event.preventDefault();
                }
            });

            lines[i].addEventListener('touchend', function(e){
                if (lastLeftObj && pressedObj != lastLeftObj) { // 点击除当前左滑对象之外的任意其他位置
                    $(lastLeftObj).animate({marginLeft:"0"}, 50); // 右滑
                    lastLeftObj = null; // 清空上一个左滑的对象
                }
                var diffX = e.changedTouches[0].pageX - lastXForMobile;
                if (diffX < -150) {
                    $(pressedObj).animate({marginLeft:"-3.3rem"}, 50); // 左滑
                    lastLeftObj && lastLeftObj != pressedObj &&
                    $(lastLeftObj).animate({marginLeft:"0"}, 50); // 已经左滑状态的按钮右滑
                    lastLeftObj = pressedObj; // 记录上一个左滑的对象
                } else if (diffX > 150) {
                    if (pressedObj == lastLeftObj) {
                        $(pressedObj).animate({marginLeft:"0"}, 50); // 右滑
                        lastLeftObj = null; // 清空上一个左滑的对象
                    }
                }
            });
        }

        // 网页在PC浏览器中运行时的监听
//        for (var i = 0; i < len; ++i) {
//            $(lines[i]).bind('mousedown', function(e){
//                lastX = e.clientX;
//                pressedObj = this; // 记录被按下的对象
//            });
//
//            $(lines[i]).bind('mouseup', function(e){
//                if (lastLeftObj && pressedObj != lastLeftObj) { // 点击除当前左滑对象之外的任意其他位置
//                    $(lastLeftObj).animate({marginLeft:"0"}, 500); // 右滑
//                    lastLeftObj = null; // 清空上一个左滑的对象
//                }
//                var diffX = e.clientX - lastX;
//                if (diffX < -150) {
//                    $(pressedObj).animate({marginLeft:"-132px"}, 500); // 左滑
//                    lastLeftObj && lastLeftObj != pressedObj &&
//                    $(lastLeftObj).animate({marginLeft:"0"}, 500); // 已经左滑状态的按钮右滑
//                    lastLeftObj = pressedObj; // 记录上一个左滑的对象
//                } else if (diffX > 150) {
//                    if (pressedObj == lastLeftObj) {
//                        $(pressedObj).animate({marginLeft:"0"}, 500); // 右滑
//                        lastLeftObj = null; // 清空上一个左滑的对象
//                    }
//                }
//            });
//        }
    }
//    $(document).ready(function(e){
//        xuanran();
//    })
    function deletebtn(that){
        var liveRoomId = $(that).attr("liveRoomId");
        var result = ZAjaxJsonRes({url: "/appMsg/deleteAppMsg.user?id=" + liveRoomId, type: "GET"});
            $(that).parents(".messlist").remove();
    }

    function jumphref(that){
        var h = $(that).attr("jumpHref");
            h = h.replace('+', '%2B');
        if(h == null || h == "" || h == undefined){

        }else{
            window.location.href = h;
        }
    }
</script>
</html>
