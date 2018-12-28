<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>今日收益</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<div id="wrapper" style=" height:100%;background: white;">
    <div id="cont_box" style="overflow: hidden;height: 100%;padding-bottom: 0">
        <div class="learncoinTitle newlearncoinTitle">
            <span class="alllearMoney newalllearMoney">今日总收益(枣币)</span>
            <span id="balance"></span>
        </div>
        <ul class="earUl">
            <li class="on">
                <i>课程收益</i>
                <i class="clad"></i>
            </li>
            <li>
                <i>分销收益</i>
                <i class="fex"></i>
            </li>
            <li>
                <i>打赏收益</i>
                <i class="dad"></i>
            </li>
            <li>
                <i>转播收益</i>
                <i class="reelay"></i>
            </li>
        </ul>
        <div class="earAllbox">
            <%--课程收益--%>
            <div class="wcBox on" id="onewcBox">
                <div class="clasyBox" id="claList">

                </div>
            </div>
            <div class="wcBox" id="twowcBox">
                <%--分销收益--%>
                <div class="clasyBox" id="fesList">

                </div>
            </div>
            <div class="wcBox" id="threewcBox">
                <%--打赏收益--%>
                <div class="clasyBox" id="dasList">

                </div>
            </div>
            <div class="wcBox" id="fourwcBox">
                <%--转播收益--%>
                <div class="clasyBox" id="relayBox">

                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script>
    var result = ZAjaxRes({url: "/userRewardRecord/getTodayIncomeNew.user"});
        if(result.code == '000000') {
            var dayMon = result.data;
            $(".clad").text((dayMon.courseIncome).toFixed(2));
            $(".fex").text((dayMon.disIncome).toFixed(2));
            $(".dad").text((dayMon.rewardIncome).toFixed(2));
            $("#balance").text((dayMon.totalIncome).toFixed(2));
            $(".reelay").text((dayMon.relayIncome).toFixed(2));
        }
    $(".earUl li").click(function(){
        var index = $(this).index();
        $(this).addClass("on").siblings().removeClass("on");
        $(".earAllbox>div").eq(index).show().siblings().hide();
    });


    var status =0;
    //  初始每个列表数据
    var newlistInit = {
        //课程收益
        'claList':{
            'name' : 'claList',
            'ajaxInow' : true,
            'offset':0
        },
        //分销收益
        'fesList':{
            'name' : 'fesList',
            'ajaxInow' : true,
            'offset':0
        },
        //打赏收益
        'dasList':{
            'name' : 'dasList',
            'ajaxInow' : true,
            'offset':0
        },
        //转播收益
        'relayBox':{
            'name' : 'relayBox',
            'ajaxInow' : true,
            'offset':0
        }
    };
    //页面加载
    Load(newlistInit.claList);
    Load(newlistInit.fesList);
    Load(newlistInit.dasList);
    Load(newlistInit.relayBox);
    //点击切换
    $('.dataUl li').on('click',function(){
        var index =$(this).index();
        $(this).addClass("on").siblings().removeClass("on");
        $("#dataTobox >.wcBox").eq(index).show().siblings().hide();
    });

    //  课程收益
    $('#onewcBox').scroll(function(){
        listScroll($(this),newlistInit.claList);
    });

    //    分销收益
    $('#twowcBox').scroll(function(){
        listScroll($(this),newlistInit.fesList);
    });

    //   打赏收益
    $('#threewcBox').scroll(function(){
        listScroll($(this),newlistInit.dasList);
    });
    //   转播收益
    $('#fourwcBox').scroll(function(){
        listScroll($(this),newlistInit.relayBox);
    });

    function listScroll(_this,json) {
        var thisScrollTop = _this.scrollTop();
        var thisScrollHeight = _this.height();
        var bottomScrollTop = _this.find('.clasyBox').height();
        //下拉到底部加载
        if (thisScrollTop + thisScrollHeight >= bottomScrollTop-10) {
            Load(json);
        }
    };
    //加载
    function Load(json) {
        switch (json.name) {
            //课程收益
            case 'claList':
                if(!json.ajaxInow){return};
                newlistInit.claList.ajaxInow = false;
                var result = ZAjaxRes({url: "/liveRoom/courseIncomeTodayDetailsPage.user", type: "GET",param:{pageSize:20,offset:json.offset}});
                if (result.code == "000000"){
                    var claList = result.data;
                    console.log(claList);
                    var size = claList.length;
                    $('#claList').append(pubicHtml(claList));
                    if(size<10){
                        newlistInit.claList.ajaxInow = false;
                    }else{
                        newlistInit.claList.offset=$('#claList div.claList').length;
                        newlistInit.claList.ajaxInow = true;
                    }
                }else if(result.code == "000110") {
                if ($('#claList div').length < 1 && $('#claList .noData').length < 1) {
                    $('#claList').append('<div class="noData"></div>');
                }
                    newlistInit.claList.ajaxInow = false;
            } else{
                    newlistInit.claList.ajaxInow = true;
                }
                break;
            //分销收益
            case 'fesList':
                if(!json.ajaxInow){return};
                newlistInit.fesList.ajaxInow = false;
                var result = ZAjaxRes({url: "/liveRoom/disIncomeTodayDetailsPage.user", type: "GET",param:{pageSize:20,offset:json.offset}});
                if (result.code == "000000") {
                    var fesList = result.data;//直播
                    var size = fesList.length;
                    $('#fesList').append(pubicHtml(fesList));
                    if(size<10){
                        newlistInit.fesList.ajaxInow = false;
                    }else{
                        newlistInit.fesList.offset=$('#fesList div.claList').length;
                        newlistInit.fesList.ajaxInow = true;
                    }
                }else if(result.code == "000110"){
                    if($('#fesList div').length < 1 && $('#fesList .noData').length < 1){
                        $('#fesList').append('<div class="noData"></div>');
                    }
                    newlistInit.fesList.ajaxInow = false;
                }else{
                    newlistInit.fesList.ajaxInow = true;
                }
                break;
            //打赏收益
            case 'dasList':
                if(!json.ajaxInow){return};
                newlistInit.dasList.ajaxInow = false;
                var result = ZAjaxRes({url: "/userRewardRecord/findTodayRewInfoPage.user", type: "GET",param:{pageSize:20,offset:json.offset}});
                if (result.code == "000000") {
                    var dasList = result.data;//直播
                    var size = dasList.length;
                    $('#dasList').append(pubicHtml(dasList));
                    if(size<10){
                        newlistInit.dasList.ajaxInow = false;
                    }else{
                        newlistInit.dasList.offset=$('#dasList div.claList').length;
                        newlistInit.dasList.ajaxInow = true;
                    }
                }else if(result.code == "000110"){
                    if($('#dasList div').length < 1 && $('#dasList .noData').length < 1){
                        $('#dasList').append('<div class="noData"></div>');
                    }
                    newlistInit.dasList.ajaxInow = false;
                }else{
                    newlistInit.dasList.ajaxInow = true;
                }
                break;
            //转播收益
            case 'relayBox':
                if(!json.ajaxInow){return};
                newlistInit.relayBox.ajaxInow = false;
                var result = ZAjaxRes({url: "/userRewardRecord/income/today/relay/course/list.user", type: "GET",param:{pageSize:10,offset:json.offset}});
                if (result.code == "000000") {
                    var relayBox = result.data;//直播
                    var size = relayBox.length;
                    $('#relayBox').append(pubicHtml(relayBox));
                    if(size<10){
                        newlistInit.relayBox.ajaxInow = false;
                    }else{
                        newlistInit.relayBox.offset=$('#relayBox div.claList').length;
                        newlistInit.relayBox.ajaxInow = true;
                    }
                }else if(result.code == "000110"){
                    if($('#relayBox div').length < 1 && $('#relayBox .noData').length < 1){
                        $('#relayBox').append('<div class="noData"></div>');
                    }
                    newlistInit.relayBox.ajaxInow = false;
                }else{
                    newlistInit.relayBox.ajaxInow = true;
                }
            default:
                return false;
        }
    };



    //  公共渲染lists列表数据转换成html格式
    function pubicHtml(mycourse){
        var arrHtml = [];
        var bg ="";
        var ag ="";
        var fu ="";
        var img ="";
        var liveway ="";
        $.each(mycourse, function (i, n) {
            if(n.liveWay=="1"||n.live_way=="1"){
                liveway="livewaya"
            }else if(n.liveWay=="0"||n.live_way=="0"){
                liveway="livewayv"
            }else{
                liveway=""
            }

            if(n.visitCount||n.visitCount=="0"){
                if(n.IsRelay=="0"){
                    img="";
                }else{
                    img = "<img src='/web/res/image/zbke.png'>"
                }
//                fu = "findCourseInfo("+n.courseId+")"
                bg='<i class="earbot"> <span>想学人数:<em class="xcmoney">'+ n.visitCount+'</em></span> <span>付费人数:<em class="fumoney">'+n.joinCount+'</em></span></i>';
            }else if(n.courseCount||n.courseCount=="0"){
                if(n.IsRelay=="0"){
                    img="";
                }else{
                    img = "<img src='/web/res/image/zbke.png'>"
                }
//                fu = "findRewardbyCouseId("+n.courseId+")"
                bg= '<em class="xcmoney">打赏人数:'+n.courseCount+'</em>';
            }else if(n.INCOME_TOTAL||n.INCOME_TOTAL=="0"){
                bg='<i class="earbot"> <span>总收益:<em class="xcmoney">'+(n.INCOME_TOTAL).toFixed(2)+'枣币</em></span> <span>转播人数:<em class="fumoney">'+n.RELAYER_CNT+'</em></span></i>';
                fu = "findCrealyuseId("+n.ID+")";
                img=""
            }
            else{
                fu = ""
                bg='<em class="xcmoney newxcmo">'+n.creteTime+'</em>';
                img =""
            };
            if(n.totalAmount){
                ag='<p class="llM">'+n.totalAmount+'枣币</p>';
            }else if(n.INCOME_TOTAL||n.INCOME_TOTAL=="0"){
                ag='<p class="llM"></p>';
            }else{
                ag='<p class="llM">'+n.amount+'枣币</p>';
            }
            arrHtml.push('<div class="newPubicsize claList" c_id='+n.courseId+' onclick="'+fu+'">' +
                    '<i class="clasPic" style="background:url(' + n.address + ') no-repeat center center; background-size:cover;" ><em class="'+liveway+'"></em></i>'+
                    ' <div class="claRightbox">' +
                    '<div class="reversionTi"><i class="earTitle">'+img+ n.topic+'</i>'+ag+'</div>' +
                    ''+bg+'</div>' +
                    '</div>');
        });
        return arrHtml.join('');
    };

    //课程收益
//    function findCourseInfo(cId){
//        window.location.href = "/weixin/courseRevenuedetails?courseId="+cId;
//    }
    //打赏收益
//    function findRewardbyCouseId(cId){
//        window.location.href = "/weixin/rewardFordetails?courseId="+cId;
//    };
    //转播收益收益
    function findCrealyuseId(cId){
        window.location.href = "/weixin/detailsReturn?courseId="+cId+"&zb=1";
    };


</script>
</html>
