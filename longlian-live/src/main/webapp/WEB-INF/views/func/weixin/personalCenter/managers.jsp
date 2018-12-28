<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>管理员列表</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/index.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;position: relative; background:#f7f7f7;">
<div class="bindingtap" style="display: none">
    <div class="bindbox">
        <div class="bindTitle">删除管理员</div>
        <p class="bindtext"></p>
        <ul class="bingthure">
            <li class="no">否</li>
            <li class="yes">是</li>
        </ul>
    </div>
</div>
<div id="wrapper" style="overflow-x: hidden;overflow-y: auto; height:100%; background:#f7f7f7;">
    <div id="cont_box" style="padding-bottom:2.7rem;background:#f7f7f7">
        <!--管理员列表 -->
        <div class="managersbox" id="managersbox" style="min-height:26rem">

        </div>
        <p class="pullUpLabel">上拉加载更多</p>
    </div>
</div>
<div class="addmanagersBox">
    <p class="addmanagers">新建管理员</p>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var offset = 0;
    $(function () {
        Load();
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
        if (oBok) {
            var result = ZAjaxRes({
                url: "/courseManager/findAllManagers.user?offset=" + offset,
            });
            if (result.code == "000000") {
                if (result.data.length > 0) {
                    offset = offset + result.data.length;
                    var allUser = result.data;//推荐
                    $.each(allUser, function (i, n) {
                        showManager(n);
                    });
                    if (result.data.length < 10) {
                        $('.pullUpLabel').hide();
                        oBok = false;
                    }
                } else {
                    $('.pullUpLabel').hide();
                    oBok = false;
                }
            } else {
                if (offset == 0) {
                    $('#managersbox').append('<div class="noData noDatas"></div>');
                    $('.pullUpLabel').hide();
                }
                oBok = false;
            }
            /****   ---DOM  改变后重新计数高度 --- ****/
        }
        else {
            if (!oBok) {
                if (offset != 0) {
                    $('.pullUpLabel').show();
                    $('.pullUpLabel').text('加载已经完毕');
                }
            }
        }
    }
    /**
     * 管理员列表展示
     * @param n
     */
    function showManager(n) {
        var el, li, i;
        el = document.querySelector("#managersbox");
        li = document.createElement('div');
        li.setAttribute("class", "managers");
        li.innerHTML = '<div class="overmanagers"><dl>\
            <dt style="background: url(' + n.photo + ') no-repeat center center;background-size: 100% 100%"></dt>\
            <dd>\
            <p>' + n.appUserName + '</p>' +
                '<i>ID:' + n.userId + '</i>' +
                '</dd></dl>' +
                '<p id="'+ n.id+'" class="managerDele"onclick="Dele($(this))" managerId="' + n.id + '" useName="' + n.appUserName + '"><img src="/web/res/image/cutof.png">删除</p></div>';
        el.appendChild(li, el.childNodes[0]);
    }

    var id = 0 ;
    function Dele(that) {
        $(".bindingtap").show();
        id = that.attr("managerId");
        var useName = that.attr("useName");
        $(".bindtext").html("你确定要将" + useName + "从管理员列表中移除吗?")
    }

    $(".yes").click(function () {
        $("#"+id+"").attr("disabled",true);
        var result = ZAjaxJsonRes({url: "/courseManager/delCourseManagerById.user?id=" + id});
        if (result.code == '000000') {
            $("#"+id+"").attr("disabled",false);
            $("#"+id+"").parent(".managers").remove();
            pop1({"content": "删除成功", "status": "normal", time: '1000'});
            window.location.reload()
        }else{
            $("#"+id+"").attr("disabled",false);
        }
        $(".bindingtap").hide();
    })
    $(".no").click(function () {
        $(".bindingtap").hide();
    })
    $(".addmanagers").click(function () {
        window.location.href = "/weixin/administrator"
    })
</script>
</html>
