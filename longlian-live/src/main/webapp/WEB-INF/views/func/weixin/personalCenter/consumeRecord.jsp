<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>流量消耗记录</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;background: #f7f7f7">
<div id="wrapper" style="overflow: auto;height: 100%">
    <div id="cont_box" style="padding-bottom: 0;min-height: 35rem;overflow: hidden">

    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>

    var nowTime = new Date();
    var nowMonth = nowTime.getMonth() + 1;
    var nowYear = nowTime.getFullYear();
    var nowT = nowYear +""+ nowMonth ;

    /*下拉加载*/
    $(function () {
        Load();
        /*直播 精选 下拉加载*/
        $('#wrapper').scroll(function () {
            var scrollTop = $(this).scrollTop();
            var windowHeight = $(this).height();
            var scrollHeight = $('#cont_box').height();
            if (scrollTop + windowHeight >= scrollHeight - 10) {
                Load();
            }
        });
    });
    var oBok = true;
    var offSet = 0;
    function Load() {
        if (oBok) {
            var currentMonth = $(".recordBox:last").find(".recordT").text();
            var param = {"offset": offSet, "pageSize": 10};
            var result = ZAjaxRes({url: "/cdnVisit/getAllCourseFlow.user", param: param, type: "GET"});
            if (result.code == "000000") {
                var data = result.data;
                var ext = result.ext;
                if (ext == '1') {
                    oBok = false;
                }
                var arry = [];
                for (var key in data) {
                    arry.push(key);
                }
                arry = quickSort(arry); //快速排序,从大到小
                for(var i = 0 ; i < arry.length ; i++){
                    var key = arry[i];
                    var itemData = data[key];
                    var dbYear = key.substring(0,4); //数据库查出来的日期 201711
                    var dbMonth = key.substring(4,6);
                    var keyShow = dbYear + "年" + dbMonth + "月";
                    if(key == nowT) {   //当前时间 201711
                        keyShow = "本月";
                    }else if(dbYear == nowYear) { //当前年 2017
                        if(dbMonth < 10){
                            dbMonth = dbMonth.substring(1,dbMonth.length);
                        }
                        keyShow = dbMonth + "月";
                    }
                    if (itemData != '1') {
                        var p = "";
                        if(key != currentMonth){
                            p = "<p class='recordT' style='display: none'>"+key+"</p>" +
                                    "<p class='recordTitle'>" + keyShow + "</p>";
                        }else{
                            p = "<p class='recordT' style='display: none'>"+key+"</p>" +
                                    "<p class='recordTitle' style='display: none'>" + keyShow + "</p>";
                        }
                        var div = '';
                        $.each(itemData, function (index, item) {
                            div += "<div class='recharRecordbox'> " +
                                    "<p> " +
                                    "<span class='classTtitleshow'>"+item.liveTopic+"</span> " +
                                    "<span>"+item.s+"</span> " +
                                    "</p>" +
                                    " <p> " +
                                    "<i>"+item.st+ " ~ " +item.et+"</i> " +
                                    "</p>" +
                                    " </div>";
                        });
                        $("#cont_box").append("<div class='recordBox'>" + p + div + "</div>")
                        offSet += itemData.length;
                    }
                }
            } else {
                oBok = false;
                if (offSet == 0) {
                    $('#cont_box').append('<div class="noData_new"></div>')
                }
            }
        }
    };

    var quickSort = function(arr) {
        if (arr.length <= 1) { return arr; }
        var pivotIndex = Math.floor(arr.length / 2);
        var pivot = arr.splice(pivotIndex, 1)[0];
        var left = [];
        var right = [];
        for (var i = 0; i < arr.length; i++){
            if (arr[i] < pivot) {
                right.push(arr[i]);
            } else {
                left.push(arr[i]);
            }
        }
        return quickSort(left).concat([pivot], quickSort(right));
    };
</script>
</html>