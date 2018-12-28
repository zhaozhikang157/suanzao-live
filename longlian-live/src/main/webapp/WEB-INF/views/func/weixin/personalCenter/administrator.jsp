<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>添加管理员</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style="overflow-x: hidden;overflow-y: auto; height:100%; background:#f7f7f7;">
    <div id="cont_box" style="padding-bottom:1rem;">
        <%--搜索--%>
        <div class="searchAllbox" style="margin-bottom: 0">
            <%--<div class="search_boxpic"></div>--%>
            <div class="searchNew">
                <div class="searchInput"
                     style="display: -webkit-box;-webkit-box-align: center;-webkit-box-pack: center;padding-right: 0">
                    <input type="number" id="searchFn" class="newsearchFn" autofocus placeholder="请输入添加人ID"
                           oninput="searchFn()"/>

                    <p class="cancel">取消</p>
                    <span class="searclose" style="top:0.9rem;"></span>
                </div>
            </div>
        </div>
        <!--管理员列表 -->
        <div class="managersNewbox">
            <div class="managersbox" id="managersbox" style="min-height:32rem;padding-left: 0">
                <%--<div class="managers managersNew">--%>
                    <%--<dl>--%>
                        <%--<dt style="background: url('/web/res/image/messpic.png') no-repeat center center;background-size: 100% 100%"></dt>--%>
                        <%--<dd>--%>
                            <%--<p>就是不爱吃饭</p>--%>
                            <%--<i>ID:<em class="seachId">2329382</em><em class="seachIdcolor">2329382</em></i>--%>
                        <%--</dd>--%>
                    <%--</dl>--%>
                    <%--<p class="addTo on">添加</p>--%>
                <%--</div>--%>
            </div>
            <p class="pullUpLabel" style="border-top: none">上拉加载更多</p>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0, pageSize = 10;
    $(function () {
        /*直播 精选 下拉加载*/
        $('#wrapper').scroll(function () {
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if (scrollTop + windowHeight >= scrollHeight - 1) {
                Load();
            }
        });
    });
    var oBok = true;
    function Load() {
        if (offset < pageSize) {
            oBok = false;
        }
        var userId = $("#searchFn").val();
        if (oBok) {
            var result = ZAjaxRes({
                url: "/courseManager/findAppUserById.user?offset=" + offset + "&userId=" + userId +
                "&pageSize=" + pageSize
            });
            if (result.code == "000000") {
                if (result.data.length > 0) {
                    offset = offset + result.data.length;
                    var messageList = result.data;//推荐
                    $.each(messageList, function (i, n) {
                        addLive(n);
                    });
                    if (result.data.length < pageSize) {
                        $('.pullUpLabel').show();
                        $('.pullUpLabel').text('加载已经完毕');
                        oBok = false;
                    }
                } else {
                    $('.pullUpLabel').show();
                    $('.pullUpLabel').text('加载已经完毕');
                    oBok = false;
                }
            } else {
                $('.pullUpLabel').show();
                $('.pullUpLabel').text('加载已经完毕');
                oBok = false;
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        }
        else {
            if (!oBok) {
                $('.pullUpLabel').show();
                $('.pullUpLabel').text('加载已经完毕');
            }
        }
    }
    /**
     * 管理员列表展示
     * @param n
     */
    function addLive(n) {
        var el, li, i;
        var userIdValue = $("#searchFn").val();
        el = document.querySelector("#managersbox");
        li = document.createElement('div');
        li.setAttribute("class", "managers managersNew");
        var isManagerSpan = '<p class="addTo" onclick="createbtn(this)" userId="' + n.userId + '">添加</p>';
        if(n.isManager == '1'){
            isManagerSpan = '<p class="addTo on" userId="' + n.userId + '">已添加</p>'
        }
        var userId = (n.userId).toString();
        var spanId = '';
        if(userId.indexOf(userIdValue.toString()) >= 0){
            spanId= '<i>ID:<em class="seachId">'+userIdValue+'</em><em class="seachIdcolor">'+userId.substring(userIdValue.length,userId.length)+'</em></i>';
        }
        li.innerHTML = '<dl>\
            <dt style="background: url(' + n.photo + ') no-repeat center center;background-size: 100% 100%"></dt>\
            <dd>\
            <p>' + n.appUserName + '</p>' +
                 spanId +
                '</dd></dl>' +
                isManagerSpan;
        el.appendChild(li, el.childNodes[0]);
    }
    function createbtn(that) {
        $(that).attr("disabled",true);
        var userId = $(that).attr("userId");
        var result = ZAjaxRes({url: "/courseManager/createCourseManager.user?userId=" + userId});
        if (result.code == '000000') {
            pop1({"content": "添加成功", "status": "normal", time: '1000'});
            $(that).attr("disabled",false);
            setTimeout(function(){
                window.location.href = '/weixin/managers';
            },1000);
        } else {
            sys_pop(result);
        }
        statisticsBtn({'button':'018','referer':''});
    }
//    $(".search_boxpic").click(function () {
//        $(this).hide();
//        $(".searchNew").show();
//        $(".managersNewbox").show();
//    })

    function searchFn() {
        $("#managersbox").empty();
        var userId = $("#searchFn").val();
        console.log(userId)
        if (userId == null || userId == '') {
            return;
        }
        var test = /^\+?[1-9][0-9]*$/;
        if (!test.test(userId)) {
//            pop({"content": "请输入非零正整数!", "status": "error", time: '1500'});
            $('.managersbox').append('<div class="zdata">很抱歉，没有搜索到此用户</div>');
            return;
        }
        var result = ZAjaxRes({url: "/courseManager/findAppUserById.user?userId=" + userId + "&pageSize=" + pageSize});
        if (result.code == '000000' && result.data.length > 0) {
            $(".searclose").show();
            $.each(result.data, function (i, n) {
                addLive(n);
            });
            offset = offset + result.data.length;
        } else {
            $('.managersbox').append('<div class="zdata">很抱歉，没有搜索到此用户</div>')
        }
    }

    $(".searclose").click(function () {
        $("#searchFn").val("");
        $(this).hide();
        $("#managersbox").empty();
    })
    $(".cancel").click(function () {
//        $(".searchNew").hide();
//        $(".search_boxpic").show();
//        $(".managersNewbox").hide();
        $(".managersbox").empty();
        $("#searchFn").val("");
        window.location.href = "/weixin/managers"
    })
    //    document.addEventListener("touchstart",function(e){
    //        document.getElementById("searchFn").focus();
    //    })
</script>
</html>
