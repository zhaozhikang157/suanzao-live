<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>手机号抽奖</title>
    <link rel="stylesheet" type="text/css" href="/web/res/annualMeetingDraw/css/animate.min.css"/>
    <link rel="stylesheet" type="text/css" href="/web/res/annualMeetingDraw/css/css.css"/>
    <script type="text/javascript" src="/web/res/annualMeetingDraw/js/jquery-1.7.2-min.js"></script>
    <script type="text/javascript" src="/web/res/annualMeetingDraw/js/easing.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/tools.js"></script>
</head>
<body>
<audio id="audio1" src="/web/res/annualMeetingDraw/images/y3.m4r" loop='loop' ></audio>
<audio id="audio2" src="/web/res/annualMeetingDraw/images/2.mp3" ></audio>
<div class="main">
    <div class="img"><img src="/web/res/annualMeetingDraw/images/2017top.png" width="1236" height="428" /></div>
<div class="left">
    <div class="reset"><img src="/web/res/annualMeetingDraw/images/reset.png"/></div>
    <ol>
        <li vue="0">特等奖</li>
        <li vue="1">一等奖</li>
        <li vue="2">二等奖</li>
        <li vue="3">三等奖</li>
        <li vue="4">四等奖</li>
        <li vue="5">五等奖</li>
        <li class="on" vue="6">六等奖</li>
    </ol>
    <div class="box1">
        <h1><span class="total">总人数：50</span>幸运号码池</h1>

        <div id="main_bg">
            <div class="box_mb"></div>
            <div class="box_mb1"></div>
            <div class="box_mb2"></div>
            <ul id="box">

            </ul>
        </div>
        <div class="btn1">开始</div>
    </div>
    <div class="box2">
        <h1>六等奖 获奖号码</h1>

        <div id="ms">13611109738</div>
        <div class="btn not">停止</div>
    </div>
</div>
<div class="right" id="winList">
    <h1><img src="/web/res/annualMeetingDraw/images/hj.png" /></h1>
    <h3 style="font-size:22px">特等奖(1名)</h3>
    <ul class="win_0">
    </ul>
    <h3>一等奖(2名)</h3>
    <ul class="win_1">
    </ul>
    <h3>二等奖(3名)</h3>
    <ul class="win_2">
    </ul>
    <h3>三等奖(4名)</h3>
    <ul class="win_3">
    </ul>
    <h3>四等奖(5名)</h3>
    <ul class="win_4">
    </ul>
    <h3>五等奖(6名)</h3>
    <ul class="win_5">
    </ul>
    <h3>六等奖(7名)</h3>
    <ul class="win_6">
    </ul>
</div>
</div>
</body>
</html>
<script>
    //用户数组
    var arr;
    $(document).ready(function () {
        var obj = tools.requestRs("/annualMeetingDraw/getPhone", "GET");
        arr = obj.data.phoneList;
        $(".total").html("总人数：" + obj.data.number);
        setTimeout(function(){
            $('.img').addClass('animated flip').show()
        },1000);
    });

    //中奖用户
    var winName;
    //每行的高度
    var u = 100;
    //插入到n行
    var n = 15;
    //定时器
    var timer = null;

    //几等奖
    var grade = 6;
    var gradeTetx = '六等奖';

    $(function () {
        //初始化中奖用户
        if (localStorage.getItem("sequence")) {
            var ssHtml = localStorage.getItem("sequence");
            $("#winList").html(ssHtml);
        }
        //初始化创建手机号
        createLi();
        //选择几等奖
        $('.left>ol>li').click(function () {
            var _this = $(this);
            _this.addClass('on').siblings('li').removeClass('on');
            grade = _this.attr('vue');
            gradeTetx = _this.html();
            $('.box2 h1').html(gradeTetx + '&nbsp;获奖号码')
        });

        //开始按钮
        $('.btn1').click(function () {
            var obj = tools.requestRs("/annualMeetingDraw/draw", "GET");
            if (obj.data.selectPhone != "") {
                $('#audio2')[0].pause();
                $('#audio1')[0].play();
                winName = obj.data.selectPhone;
                $(".total").html("总人数：" + obj.data.phoneNumber);
                $('#ms').remove('animated tada').hide().html(winName);
                //插入中奖号码
                arr.splice(n + 1, 0, winName);
                //重新创建手机号
                createLi();
                //抽奖开始滚动
                start();
                //开始停止按钮状态
                $(this).addClass('not');
                $('.btn').removeClass('not');
            } else {
                alert("网路异常！！");
            }

        });

        //停按钮
        $('.btn').click(function () {
            //去掉中奖号
            arr.splice(n + 1, 1);
            //重新打乱数组
            arr = randomArr(arr);
            //抽奖停止滚动
            end();
            //开始停止按钮状态
            $(this).addClass('not');
        });

        //重置
        $('.reset').click(function () {
            localStorage.removeItem("sequence");
            window.location.reload();
            var obj = tools.requestRs("/annualMeetingDraw/reset", "GET");
        });

        /** 随机排列数组里的顺序 */
        function randomArr(arr) {
            var _arr = [];
            var length = arr.length;
            for (i = 0; i < length; i++) {
                var ran = Math.random() * arr.length;
                _arr.push((arr.splice(ran, 1)).toString());
            }
            return _arr;
        };

        //生成手机号标签函数
        function createLi() {
            $('#box').html('');
            $.each(arr, function () {
                var _this = this;
                var oLi = $('<li>');
                var arr2 = _this.split('');
                $.each(arr2, function () {
                    var oSpan = $('<span>' + this + '</span>')
                    oLi.append(oSpan);
                });
                $('#box').append(oLi);
            });
        };

        //抽奖开始滚动函数
        function start() {
            clearInterval(timer);
            $('#box').stop(false, true).css({'top': 0})
            timer = setInterval(function () {
                var a = parseInt($('#box').css('top'));
                if (a < -2400) {
                    $('#box').stop(false, true).css({'top': 0});
                }
                $('#box').stop(false, true).animate({'top': "-=100"}, 100, 'linear');
            }, 100);
        };

        //抽奖停止滚动函数
        function end() {
            clearInterval(timer);
            $('#box').stop(false, true).css({'top': 0}).animate({'top': -u * n}, {
                duration: 3000,
                easing: "swing",
                complete: function () {
                    $('#audio1')[0].pause();
                    $('#audio2')[0].currentTime = 0;
                    $('#audio2')[0].play();
                    setTimeout(function () {
                        var ms = $('#ms');
                        ms.addClass('animated tada').show();
                        var aLi = $('<li>' + ms.html() + '</li>');
                        $('#winList').find('.win_' + grade).append(aLi);
                        localStorage.setItem("sequence", $("#winList").html());
                        $('.btn1').removeClass('not');
                    }, 100);
                }
            });
        };

        //清除错误中奖号
        $('#winList li').live('mousedown', '#winList', function (event, a) {
            if (event.which == 3 || a == 'right') {
                var wintext = $(this).html();
                var result = confirm('是否删除' + wintext + '该号码！');
                if (result) {
                    $(this).remove();
                    localStorage.setItem("sequence", $("#winList").html());
                }
            }
        });
    });


</script>
</div>