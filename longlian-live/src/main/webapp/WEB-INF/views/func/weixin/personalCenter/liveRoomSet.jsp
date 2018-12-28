<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="Keywords" content=""/>
    <meta name="Description" content=""/>
    <title>直播间设置</title>
    <script src="/web/res/js/access-meta.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="/web/res/css/initialize.css?nd=<%=current_version%>"/>
    <link rel="stylesheet" type="text/css" href="/web/res/css/personal.css?nd=<%=current_version%>"/>
    <script src="/web/res/js/jquery-1.7.2.js?nd=<%=current_version%>" type="text/javascript" charset="utf-8"></script>
</head>
<body style="-webkit-overflow-scrolling: touch;">
<!--直播间设置 -->
<div class="broadcast">
    <ul class="broadcastnav">
        <li>
            <span>用户头像</span>
            <p class="rightcont">
                <span style="display: block;" class="userpic" id="img"></span>
                <input type="file" name="file" value="" class="liveroom"id="doc"
                       onchange="selectFileImage(this);"  accept="image/*"/>
            </p>
        </li>
    </ul>
    <div class="liveRoom_name">
        <span>直播间名称</span>
        <p class="rightcont" id="name">
        </p>
    </div>
    <div class="alone">
        <span>直播间介绍</span>
        <textarea id="remark" name="remark" placeholder="请介绍下你的直播间吧" maxlength="200" class="introduction"></textarea>
    </div>

    <div class="liveRoom_cover">
        <span>直播间封面</span>
        <p class="rightcont">
            <img src="" class="pic" id="coverss"/>
            <label for="coverssAddress" class="liveroom"></label>
            <input type="file" name="file" id="coverssAddress" value="" style="opacity:0;position: absolute;"
                   onchange="xmTanUploadImg(this,1)"  accept="image/*"/>
        </p>
        <%--<i class="jytext">(建议上传16:9的图片)</i>--%>
    </div>
    <div class="broadcastnav secret">
        <span>直播间邀请卡</span>
        <a onclick="getInviCard()">
            <p class="rightcont">
                <em >分享我的直播间邀请卡</em>
                <span class="jump"></span>
            </p>
        </a>
    </div>
  <span class="bcktv">
    完成
  </span>
</div>
</body>
<script src="/web/res/js/zepto.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/zepto.mdatetimer.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/tools.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/pop.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/jquery.min.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/form.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/uploadImage.js?nd=<%=current_version%>"></script>
<script src="/web/res/js/exif.js?nd=<%=current_version%>"></script>
<script>
    var id = '${id}';
    var uploadStatus = 0;
    var uploadStatus1 = 0;
    var JQuery = $.noConflict();
    var result = ZAjaxJsonRes({url: "/liveRoom/getLiveRoomInfo.user?", type: "GET"});
    if (result.code == "000000") {
        $('#coverss').attr("src", result.data.coverssAddress);
//    $('#bg').attr("src", result.data.bgAddress);
        $('#name').text(result.data.name);
        $('#remark').val(result.data.remark);

    } else {
        sys_pop(result);
    }

    function getInviCard() {
        statisticsBtn({'button':'007','referer':'007001','roomId':id})
        window.location.href = "/weixin/inviCard?roomId=" + id;
    }
    var result = ZAjaxJsonRes({url: "/user/userInfo.user", type: "GET"});
    if (result.code == "000000") {
        $("#img").css({
            background:'url('+result.data.headPhoto +') no-repeat',
            backgroundSize:'cover',
            backgroundPosition: 'center'
        })
    }
    //选择图片，马上预览
    function xmTanUploadImg(obj, type) {
        var file = obj.files[0];
        var size = file.size;
        if (size > 83886080) {
            alert('图片大小不能超过10M！');
            return false;
        }
        var img = $(obj).prev().prev('.pic')[0];
        var reader = new FileReader();
        reader.onload = function (e) {
            if (!/gif|jpg|jpeg|png|bmp|GIF|JPG|PNG|BMP$/.test(e.target.result)) {
                alert("图片类型必须是.gif,jpeg,jpg,png中的一种")
                return false;
            }
            if (type == 2) {
                if (uploadStatus == 1) {
                    alert("图片上传中，请稍等！！");
                    return;
                }
                uploadStatus = 1;
                JQuery.ajaxFileUpload({
                    url: '/file/upload',
                    secureuri: false,
                    fileElementId: 'bgAddress',//file标签的id
                    dataType: 'json',//返回数据的类型
                    data: {},//一同上传的数据
                    success: function (data, status) {
                        uploadStatus = 0;
                        var picc =  data.data.url;
                        if(isEmptyPic(picc)){
                            alert("图片上传失败，请重新上传！！");
                            return;
                        }
                        img.src = picc;
                    },
                    error: function (data, status, e) {
                        uploadStatus = 0;
                        alert("图片上传失败，请重新上传！！");
                        alert(e);
                    }
                });
            } else {
                if (uploadStatus1 == 1) {
                    alert("图片上传中，请稍等！！");
                    return;
                }
                uploadStatus1 = 1;
                JQuery.ajaxFileUpload({
                    url: '/file/upload',
                    secureuri: false,
                    fileElementId: 'coverssAddress',//file标签的id
                    dataType: 'json',//返回数据的类型
                    data: {},//一同上传的数据
                    success: function (data, status) {
                        uploadStatus1 = 0;
                        var picc =  data.data.url;
                        if(isEmptyPic(picc)){
                            alert("图片上传失败，请重新上传！！");
                            return;
                        }
                        var param={"id":id,"coverssAddress":picc}
                        var result = ZAjaxRes({url: "/liveRoom/setLiveRoom.user", param:param, type: "POST" });
                        if(result.code=="000000"){
                            img.src = picc;
                        }
                    },
                    error: function (data, status, e) {
                        alert("图片上传失败，请重新上传！！");
                        uploadStatus1 = 0;
                        alert(e);
                    }
                });
            }
        };
        reader.readAsDataURL(file);
        pop1({"content": "提交成功" , "status": "normal", time: '2500'});
    }
    //用户
    function phototUpload(src) {
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/file/avatar.user",
            data: {"baseUrl": src},
            async: false,
            success: function (data) {
                console.log(data)
                if (data.code == '000000') {
                    $("#img").css({
                        background:'url('+data.data.photo +') no-repeat',
                        backgroundSize:'cover',
                        backgroundPosition: 'center'
                    })
                    pop1({"content": "提交成功", "status": "normal", time: '2500'});
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {

            }
        });
    }


//    function phototUpload(obj) {
//        if (uploadStatus == 1) {
//            alert("图片上传中，请稍等！！");
//            return;
//        }
//        uploadStatus = 1;
//        var file = obj.files[0];
//        var size = file.size;
//        if (size > 83886080) {
//            alert('图片大小不能超过10M！');
//            return false;
//        }
//        var img = $(obj).prev('.userpic')[0];
//        var reader = new FileReader();
//        reader.onload = function (e) {
//            if (!/gif|jpg|jpeg|png|bmp|GIF|JPG|PNG|BMP$/.test(e.target.result)) {
//                alert("图片类型必须是.gif,jpeg,jpg,png中的一种")
//                return false;
//            }
//            JQuery.ajaxFileUpload({
//                url: '/file/avatar.user',
//                secureuri: false,
//                fileElementId: 'doc',//file标签的id
//                dataType: 'json',//返回数据的类型
//                data: {},//一同上传的数据
//                success: function (data, status) {
//                    uploadStatus = 0;
//                    var picc = data.data.url;
//                    if(isEmptyPic(picc)){
//                        alert("图片上传失败，请重新上传！！");
//                        return;
//                    }
//                    img.src = picc;
//                },
//                error: function (data, status, e) {
//                    alert("图片上传失败，请重新上传！！");
//                    uploadStatus = 0;
//                    alert(e);
//                }
//            });
//        };
//        reader.readAsDataURL(file);
//        pop1({"content": "提交成功" , "status": "normal", time: '2500'});
//    }









    $(".livename").focus(function () {
        $(this).addClass("on");
    });
    $(".livename").blur(function () {
        $(this).removeClass("on");
    });

    $(".bcktv").on('click', function () {
        var name = $("#name").text();
        var remark = $("#remark").val();
        var param = {"id": id, "name": name, "remark": remark};
        if(name==""){
            alert("请输入昵称");
            return;
        }
        if(remark==""){
            alert("请输入直播间介绍");
            return;
        }
        var result = ZAjaxRes({url: "/liveRoom/setLiveRoom.user", param: param, type: "POST"});
        if (result.code == "000000") {
            pop1({"content": "修改成功" , "status": "normal", time: '2500'});
            setTimeout(function () {
                window.location.href = "/weixin/liveBackstage.user?id=" + id;
            }, 1000);
        } else {
            alert(result.message);
        }

    });
</script>
<script src="/web/res/js/ajaxfileupload.js"></script>
</html>