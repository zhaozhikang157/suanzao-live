<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>数据统计</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/swiper.min.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/swiper.min.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
    <script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
</head>
<body style=" -webkit-overflow-scrolling: touch;">
<!--数据统计 -->
<div id="wrapper" style="background:#FFF; overflow: auto; height:100%;">
    <div id="cont_box"class="new_index" style="padding-bottom:0; background:#FFF;">
        <div id="addli">
            <ul class="dataUl">
                <li class="on">已结束
                  </li>
                <li>已下架
                  </li>
                <li>直播中</li>
            </ul>
            <div id="dataTobox" class="dataTobox">
                <div class="dataItems coursisend" style="display: block">
                    <ul class="isend publicdata">

                    </ul>
                </div>
                <div class="dataItems courshelf">
                    <ul class="isshelf publicdata">

                    </ul>
                </div>
                <div class="dataItems  coursliving">
                    <ul class="living publicdata">

                    </ul>
                </div>
            </div>
        </div>
        </div>
    </div>
</div>
</body>
<script>
    var status =0;
        //  初始每个列表数据
        var listInit = {
            //直播中
            'coursliving':{
                'name' : 'coursliving',
                'ajaxInow' : true,
                'pageNum':1
            },
            //已结束
            'coursisend':{
                'name' : 'coursisend',
                'ajaxInow' : true,
                'pageNum':1
            },
            //已下架
            'courshelf':{
                'name' : 'courshelf',
                'ajaxInow' : true,
                'pageNum':1
            }
        };
        //页面加载
        Load(listInit.coursisend,0);
        Load(listInit.courshelf,1);
        Load(listInit.coursliving,2);
        //点击切换
        $('.dataUl li').on('click',function(){
            var index =$(this).index();
            $(this).addClass("on").siblings().removeClass("on");
           $("#dataTobox >.dataItems").eq(index).show().siblings().hide();
            status = index;
        });

        //  已结束
        $('.coursisend').scroll(function(){
            listScroll($(this),listInit.coursisend,status);
        });

        //    已下架
        $('.courshelf').scroll(function(){
            listScroll($(this),listInit.courshelf,status);
        });

        //   直播中
        $('.coursliving').scroll(function(){
            listScroll($(this),listInit.coursliving,status);
        });

        function listScroll(_this,json,status) {
            var thisScrollTop = _this.scrollTop();
            var thisScrollHeight = _this.height()+$(".dataUl").height();
            var bottomScrollTop = _this.find('ul').height();
            //下拉到底部加载
            if (thisScrollTop + thisScrollHeight > bottomScrollTop && json.ajaxInow) {
                Load(json,status);
            }
        };
        //加载
        function Load(json,status){
            var pageNum;
            if(!json.ajaxInow){return};
            if(status=="1"){
                pageNum = listInit.coursisend.pageNum;
            }else if(status=="2"){
                pageNum = listInit.coursliving.pageNum;
            }else{
                pageNum = listInit.courshelf.pageNum;
            }
            var result = ZAjaxRes({url: "/course/getCourseList",param:{pageSize:20,pageNum:pageNum,status:status,roomId:${id}}});
            if (result.code == "000000") {
                var coursisend = result.data;
                console.log(coursisend)
                $('#dataTobox>.dataItems>ul').eq(status).append(listsHtmlRendering(coursisend));
                if(status=="1"){
                    listInit.coursisend.pageNum++;
                }else if(status=="2"){
                    listInit.coursliving.pageNum++;
                }else{
                    listInit.courshelf.pageNum++;
                }

            }else if(result.code == "000110"){
                if($('#dataTobox >.dataItems>ul').eq(status).children('li').length < 1&& $('#dataTobox >.dataItems>ul').eq(status).children('.noData').length < 1){
                    $('#dataTobox >.dataItems>ul').eq(status).append('<div class="noData"></div>');
                }
            }
        }
        //  公共渲染lists列表数据转换成html格式
        function listsHtmlRendering(statistics){
            var arrHtml = [];
            $.each(statistics, function (i, n) {
                arrHtml.push('<li onclick=login('+n.id+')>' +
                        '<p>' +
                        '<span>'+n.liveTopic+'</span>' +
                        '<span>'+n.createTime+'</span>' +
                        '</p>' +
                        '<i class="newmore"></i>' +
                        '</li>');
            });
            return arrHtml.join('');
        };
    //跳转数据统计页面
    function login(id){
        window.location.href="/weixin/courseStatistics.user?id=" + id;
    }
</script>
</html>