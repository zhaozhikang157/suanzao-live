<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="course">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>创建系列课</title>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/pc/css/upload.css"/>
    <link href="${ctx}/web/res/pc/cropper/dist/cropper.css" rel="stylesheet">
    <link href="${ctx}/web/res/pc/cropper/css/main.css" rel="stylesheet">
    <script src="${ctx}/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-process.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-validate.js"></script>
    <script src="${ctx}/web/res/fileupload/upload.js"></script>
    <script src="${ctx}/web/res/pc/js/seriesApp.js?nd=<%=current_version%>"></script>
    <script src="${ctx}/web/res/pc/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/pc/my97/xdate.dev.js"></script>
    <script src="${ctx}/web/res/pc/cropper/dist/cropper.js"></script>

    <script>
        $(function () {
            $("#form1").validation({icon: true});
         
        });
        var isSaved = false
        function onloadCheck(e) {
            if (isSaved) {
                return ;
            }
            e = e || window.event;
            // 兼容IE8和Firefox 4之前的版本
            if (e) {
                e.returnValue = '您的内容尚未保存，确定要离开本页吗？';
//            e.returnValue = false;
            }
            // Chrome, Safari, Firefox 4+, Opera 12+ , IE 9+
            return '您的内容尚未保存，确定要离开本页吗？';
//        return false;
        }
    </script>
    <style>
        .add_imgs {
            width: 289px;
            height: 190px;
            float: left;
            background: url("/web/res/image/backpicnew.png") no-repeat center center;
            cursor: pointer;
        }
        .picss {
            width: 289px;
            height: 190px;
            float: left;
            position: relative;
            margin: 0 20px 20px 0;
        }

        .closes {
            position: absolute;
            right: -12px;
            top: -12px;
            width: 32px;
            height:32px;
            background: url("/web/res/image/closeBtn.png") no-repeat center center;
            border-radius: 20px;
            text-align: center;
            line-height: 20px;
            cursor: pointer;
            z-index: 111;
        }
        #file2{
            width: 289px;
            height: 190px;
            opacity: 0;
        }
        .videonew{
            width: 289px;
            height: 190px;
            opacity: 0;
        }
        .pic{
            display: block;
            width: 100%;
            height: 100%;
        }
        .theite{
            display: none;
        }
        .liveForm{
            display: -webkit-box;
            padding-left: 18px;
            background: white;
            margin-top: 20px;
            height:440px;
        }
        .liveForm li{
            width: 226px;
            height: 440px;
            margin-right: 12px;
            position: relative;
        }
        .liveForm li span{
            position: absolute;
            display: block;
            width: 40px;
            height: 40px;
            right: 2px;
            bottom: 2px;
            background: url("/web/res/image/myxzpic.png") no-repeat center center;
            background-size: 100%;
        }
        .liveForm li span.on{
            background: url("/web/res/image/xzlpic.png") no-repeat center center;
            background-size: 100%;
        }
        .liveForm li:last-child{
            margin-right: 0;
        }
        .liveForm li img{
            width:100%;
        }
        .liveForm li p{
            font-size: 18px;
            color: #ababab;
            margin-bottom: 0.75rem;
            text-align: center;
        }
        .liveForm li p.on{
            color: #000;
        }
        .pubBlic{
            display: none;
        }
        .wordBox{
            float: left;
        }
        .wordCont{
            border: 1px solid #ccc;
            -webkit-border-radius: 8px;
            -moz-border-radius: 8px;
            border-radius: 8px;
        }
        .contBoxall{
            display: -webkit-box;
            display: -moz-box;
            margin-bottom: 30px;
            position: relative;
        }
        .wordCont dl{
            padding: 18px;
            display: -webkit-box;
            display: -moz-box;
            margin-bottom: 0;
            width: 646px;
        }
        .wordCont dl dt{
            width: 160px;
            height: 160px;
            margin-right: 18px;
        }
        .wordCont dl dt img{
            display: block;
            width: 100%;
            height: 100%;
        }
        .wordCont dl dd{
            -webkit-box-flex: 1;
            -moz-box-flex: 1;
            height: 164px;

        }
        .wordCont dl dd textarea{
            width: 100%;
            height: 100%;
            resize: none;
            border: none;
            overflow: auto;
        }
        .rightBox{
            padding:40px 22px;
            -webkit-box-flex: 1;
            display: -webkit-box;
            display: -moz-box;
            -webkit-box-orient: vertical;
            -moz-box-orient: vertical;
            -moz-box-align: center;
            -webkit-box-align: center;
        }
        .rightBox p{
            display: block;
            width: 0.7rem;
            height: 0.7rem;
            -webkit-box-flex: 1;
            -moz-box-flex: 1;
        }
        .oPrev{
            width: 30px;
            height: 30px;
            background: url("/web/res/image/upnew.png") no-repeat center center;
            display: block;
        }
        .oNext{
            width: 30px;
            height: 30px;
            background: url("/web/res/image/downnew.png") no-repeat center center;
            display: block;
        }
        .rightBox .oPrev{
            margin-bottom: 58px;
        }
        .wordBox .contBoxall:first-child .rightBox .oPrev{
            width: 30px;
            height: 30px;
            background: url("/web/res/image/upwite.png") no-repeat center center;
            display: block;
            cursor: pointer;
        }
        .wordBox .contBoxall:last-child .rightBox .oNext{
            width: 30px;
            height: 30px;
            background: url("/web/res/image/downwite.png") no-repeat center center;
            display: block;
            cursor: pointer;
        }
        .lowbtn{
            width:600px;
            float: left;
        }
        .lowbtn li{
            width: 184px;
            height: 48px;
            line-height: 48px;
            border-radius: 8px;
            color:#3366b7 ;
            border: 1px solid #3366b7;
            text-align: center;
            float: left;
            text-indent: 28px;
            cursor: pointer;
        }
        .lowbtn li:first-child{
            background: url("/web/res/image/picsmial.png") no-repeat 50px center;
            margin-right: 40px;
        }
        .lowbtn li:last-child {
            background: url("/web/res/image/textsmile.png") no-repeat 50px center;
            margin-right: 40px;
        }
        .seveBtn{
            float: left;
            width: 185px;
            height: 48px;
            line-height: 48px;
            text-align: center;
            background:#3366b7 ;
            color: white;
            margin: 20px 0;
            border-radius: 8px;
            clear: both;
            cursor: pointer;
        }
        #file3{
            width: 184px;
            height: 48px;
            position: absolute;
            opacity: 0;
            cursor: pointer;
        }
        .newcloss{
            width: 32px;
            height: 32px;
            background: url("/web/res/image/closeBtn.png") no-repeat center center;
            position: absolute;
            left: -15px;
            top: -12px;
        }
        .noMuch{
            margin-right: 14px;
        }
        .asterisk{
            color: red;
            vertical-align: middle;
        }
    </style>
</head>
<body onbeforeunload="onloadCheck()">
<div style="margin-top:30px; " id="formDiv">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%"
          ng-controller="task">


        <div class="form-group">
            <label class="col-sm-3 control-label"><span class="asterisk">* </span>课程封面：</label>
            <div class="col-sm-6 btn-upload">
                <input type="file" class="file-btn" name="files[]"  id="uploadCoverssAddress" 
                       is_crop="true" img_address="coverssAddressImg" accept=".png,.gif,.jpg"
                        />
                <img class="coverssAddressImg" ng-src="{{coverssAddressImgSrc}}" height="50px"/>
            </div>
        </div>
        <div class="form-group">
            <label for="liveTopic" class="col-sm-3 control-label"><span class="asterisk">* </span>课程名称：</label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="liveTopic" name="liveTopic" ng-model="course.liveTopic"
                       autocomplete="off" placeholder="请输入课程名称"  maxlength="30">
            </div>
        </div>
        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label" style="margin-left: -10px"><span class="asterisk">* </span>课程分类:</label>
            <div class="col-sm-6" style="margin-left: 10px">
                <select class="form-control" ng-model="course.courseType" ng-options="item.id as item.name for item in courseTypes" >
                </select>
            </div>
        </div>
        <div class="form-group paymentType">
            <label class="col-sm-3 control-label"><span class="asterisk">* </span>付费类型：</label>
            <div class="col-sm-6">
                <button type="button" class="btn btn-primary noMuch">免费</button>
                <button type="button" class="btn btn-default getMuch">收费</button>
            </div>
        </div>
        <div class="form-group theite">
            <label class="col-sm-3 control-label">选择所属系列课：</label>
            <div class="col-sm-6">
                <select class="form-control" name="id"  ng-model="course.seriesCourseId" ng-options="item.id as item.LIVE_TOPIC  for item in seriesList">
                    <option value="" >选择系列课</option>
                </select>
            </div>
        </div>
        <div class="form-group pubBlic">
            <label class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
               <!-- 分销比例（前者为老师）：
               <label class="radio-inline"style="padding-top: 0">
                   <input type="radio" name="inlineRadioOptions"  value="0"  ng-model="course.divideScale" id="inlineRadioOptions">5：5
               </label>
                <label class="radio-inline"style="padding-top: 0">
                    <input type="radio" name="inlineRadioOptions" value="1" ng-model="course.divideScale">6：4
                </label>-->
                <input type="text" class="form-control" autocomplete="off" id="chargeAmt" ng-model="course.chargeAmt"  placeholder="请输入金额">
                学生所获分销比列（%）：
                <!--<input type="radio" name="inlineRadioOptions"  value="0"  ng-model="course.divideScale" id="inlineRadioOptions" />5：5-->
                <input type="text" ng-model="course.customDistribution" class="form-control" id="customDistribution" placeholder="请输入分销比列"/>
            </div>
        </div>
       <%-- <div class="form-group pubBlic">
            <label class="col-sm-3 control-label">试看时长:</label>
            <div class="col-sm-6">
                <input type="text" class="form-control" autocomplete="off" ng-model="course.trySeeTime"  placeholder="请输入试看时长">
                <p>试看时长单位为秒，请输入大于0且小于视频时长的整数</p>
            </div>
        </div>
--%>

        <div class="form-group paymentType">
            <label for="" class="col-sm-3 control-label" style="margin-left:-10px"><span class="asterisk">* </span>是否转播:</label>
            <div class="col-sm-6" style="margin-left: 10px">
                <select required class="form-control" id = "isRelay" ng-model="course.isRelay" >
                    <option value="1">是</option>
                    <option value="0">否</option>
                </select>
            </div>
        </div>
        <div class="form-group paymentType" id="relay_div">
            <label class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="relayCharge"  autocomplete="off" ng-model="course.relayCharge"  placeholder="不输入即为免费转播">
                <p  id="relayScale_ts" hidden="hidden">买课分成比例（%）：</p>
                <input type="text" id="relayScale" class="form-control" ng-model="course.relayScale" placeholder="请输入0~100之间的整数"/>
            </div>
        </div>

        <div class="form-group" style="margin-bottom: 30px">
            <label for="remark" class="col-sm-3 control-label">课程简介：</label>
            <div class="col-sm-6">
                <textarea class="form-control ng-pristine ng-valid ng-valid-maxlength ng-touched" style="resize: none;width:646px;height: 100px" id="remark" name="remark"  autocomplete="off" ng-model="course.remark"
                          placeholder="请输入课程简介" maxlength="800"></textarea>
            </div>
        </div>

        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
                <div class="wordBox" id='detailsList'>
                    <%--上传图文--%>


                </div>
            </div>
        </div>
        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
                <ul class="lowbtn">
                    <li id="uploadLi"><input type="file"  id="file3" name="file"  onchange="dads2(this)" accept=".png,.gif,.jpg"/>上传图片</li>
                    <li onClick="detailsText()">上传文字</li>
                </ul>
                <div class="errorMessages"style="padding-top: 25px; color: #FF0000;  width: 100%; float: left; font-size: 16px;"></div>
                <p class="seveBtn" ng-click="sub()">保存</p>
            </div>
        </div>
    </form>
</div>


<div id="crop" style="padding: 10px 15px; display: none;">
    <div style="width: 760px;float: left;">
        <div style="max-height: 580px;max-width: 760px;">
            <img alt="Picture" id="shop_photo">
        </div>

    </div>
    <div style="width: 180px;float: left; padding: 30px 0px 0px 28px;text-align: center;">
        <div class="img-preview preview-lg">
        </div>
        <div>系列课</div>
        <div style="margin-top:38px;">
            <p style="margin-bottom: 15px">滑动鼠标进行缩放</p>
            <input type="button" class="btn btn-primary" style="padding: 6px 60px" name="add" value="确定"
                   data-method="getCroppedCanvas" data-option="{ &quot;width&quot;: 664, &quot;height&quot;: 373 }"
                   cropper_type=""/>
            <input type="button" class="btn" style="padding: 6px 60px ;margin-top:10px ;" name="close" value="取消"/>
        </div>
    </div>
    <div style="clear: both;"></div>
    <div id="colImageAddressImg-canvas-file" style="display: none;"></div>
    <div id="coverssAddressImg-canvas-file" style="display: none;"></div>
</div>
</body>
<script src="${ctx}/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
<script>

    window.onload = function () {
        $("#relayCharge").hide();
        $("#relayScale").hide();
        $("#relayScale").val('');
        $("#isRelay").val(0);
    }

    $(".add_imgs").off("click").on("click", function () {
        var _index = $('#imgStatus').children('.picss').length;
        if(_index>=5){
            $("#file2").prop("disabled",true);
            alert('课程图片最多为5张！');
            $('.add_imgs').hide();
            return;
        }
    });
    var coursePhoto = [];
    var JQuery = $.noConflict();
    function uploadCoursePhoto(data) {
        var appElement = document.querySelector('[ng-controller=task]');
        var $scope = angular.element(appElement).scope();
        $scope.setCourseImg(data);
    }
    function dads(input) {
        var file = input.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        ajaxFileUpload();
        reader.onload = function (e) {
            var html = "";
            html += "<div class='picss'>";
            html += '<img class="pic" src="' + this.result + '"/>';
            html += '<span class="closes" onclick="clocs(this)"></span>'
            html += "</div>";
            $(".add_imgs").before(html);
        }
    }
    function ajaxFileUpload() {
        JQuery.ajaxFileUpload({
            url: '/file/upload',
            secureuri: false,
            async: false,
            fileElementId: 'file2',//file标签的id
            dataType: 'json',//返回数据的类型
            data: {},//一同上传的数据
            success: function (data, status) {
                coursePhoto.push(data.data.url);
                uploadCoursePhoto(coursePhoto.join("&&"));
            },
            error: function (data, status, e) {

                alert(e);
            }
        });
    }
    function clocs(that) {
        var index = $(that).parent('.picss').index();
        $(that).parent('.picss').remove();
        if (index == 0) {
            coursePhoto.splice(index, index + 1);
        } else {
            coursePhoto.splice(index, index);
        }
        var _index = $('#imgStatus').children('.picss').length;
        if(_index<5){
            $('.add_imgs').show();
            $("#file2").prop("disabled",false);
        }
        uploadCoursePhoto(coursePhoto.join("&&"));
        console.log(coursePhoto);
    }
</script>
<script>

    $(".file-btn").unbind("change").on("change", function () {
        var urlVal = $(this).val();
        var filtName = getFileName(urlVal);
        if (!(/(?:png|gif|jpg)$/i.test(filtName))) {
            return;
        }
        function getFileName(o) {
            var pos = o.lastIndexOf("\\");
            return o.substring(pos + 1);
        }

        $(this).prev().text(filtName);
        if ($(this).attr("is_crop")) {
            uploadImageChange(this);
        }
    })
    $(function () {
        $("input[name='close']").click(function () {
            // this.startCropper();
            hide_btn();
        });
    });
    /**
     * 隐藏并销毁
     */
    function hide_btn() {
        $("#crop").hide();
        $("#formDiv").show();
        $('#shop_photo').cropper('destroy');//销毁
        $(".file-btn").val("");
    }
    //改变附件事件
    function uploadImageChange(event) {
        var files = event.files;
        if (files.length > 0) {
            file = files[0];
            if (!/image\/\w+/.test(file.type)) {
                jbox_notice({content: "文件必须为图片！", autoClose: 1500, className: "error"});
                return false;
            }
            if (this.url) {
                URL.revokeObjectURL(this.url); // Revoke the old one
            }
            event.url = URL.createObjectURL(file);
            $("#shop_photo").attr("src", event.url);
            // this.startCropper();
            $("#formDiv").hide();
            $("#crop").show();
            $("input[name='add']").attr("cropper_type", $(event).attr("img_address"));
            initCrop();
        }
    }
    var cropper;
    function initCrop() {
        cropper = $('#shop_photo').cropper({
            aspectRatio: 16 / 9,
            dragCrop: false,
            resizable: false,
            preview: '.img-preview',
            crop: function (data) {
                /*    $("#dataX").val(Math.round(data.x));
                 $("#dataY").val(Math.round(data.y));
                 $("#dataHeight").val(Math.round(data.height));
                 $("#dataWidth").val(Math.round(data.width));
                 $("#dataRotate").val(Math.round(data.rotate));*/
            }
        });
    }

    // Methods,保存方法
    $(document.body).on('click', '[data-method]', function () {
        var data = $(this).data(),
                $target,
                result;
        if (data.method) {
            data = $.extend({}, data); // Clone a new one
            if (typeof data.target !== 'undefined') {
                $target = $(data.target);

                if (typeof data.option === 'undefined') {
                    try {
                        data.option = JSON.parse($target.val());
                    } catch (e) {
                        console.log(e.message);
                    }
                }
            }
            result = $('#shop_photo').cropper(data.method, data.option);
            if (data.method === 'getCroppedCanvas') {
                if ($(this).attr("name") == 'save') {
                    var data = new Array();
                } else if ($(this).attr("name") == 'add') {
                    hide_btn();
                    var cropper_type = $(this).attr("cropper_type");
                    addPic(result, cropper_type);
                } else {
                    $('#getCroppedCanvasModal').modal().find('.modal-body').html(result);
                }
            }
            if ($.isPlainObject(result) && $target) {
                try {
                    $target.val(JSON.stringify(result));
                } catch (e) {
                    console.log(e.message);
                }
            }
        }
    }).on('keydown', function (e) {
        switch (e.which) {
            case 37:
                e.preventDefault();
                $image.cropper('move', -1, 0);
                break;

            case 38:
                e.preventDefault();
                $image.cropper('move', 0, -1);
                break;
            case 39:
                e.preventDefault();
                $image.cropper('move', 1, 0);
                break;

            case 40:
                e.preventDefault();
                $image.cropper('move', 0, 1);
                break;
        }
    });
    function addPic(canvas, cropper_type) {
        var appElement = document.querySelector('[ng-controller=task]');
        var $scope = angular.element(appElement).scope();
        $("#" + cropper_type + "-canvas-file").empty();//先清空，再添加
        var add = $("#" + cropper_type + "-canvas-file").append(canvas);
        $("#" + cropper_type + "Src").show();
        //$scope.setColImgAddress(data.data.url);
        $('.' + cropper_type).attr("src", canvas.toDataURL("image/jpeg"));
    }



    $(".single").click(function(){
        $(".paymentType").show();
        $(".theite").hide();
    })
    $(".series").click(function(){
        $(".paymentType").hide();
        $(".theite").show();
        $(".pubBlic").hide()
    });
    $(".liveForm li").click(function(){
        $(this).find("p,span").addClass("on").parents().siblings().find("p,span").removeClass("on");
        var appElement = document.querySelector('[ng-controller=task]');
        var $scope = angular.element(appElement).scope();
        $scope.setLiveWay ($('#liveWay li p.on').attr('liveWay'));
    });
    $(".getMuch").click(function(){
        $(".pubBlic").show();
        $(this).removeClass('btn-default').addClass('btn-primary');
        $('.noMuch').removeClass('btn-primary').addClass('btn-default');
        $('#inlineRadioOptions').attr("checked",true);
        $('#inlineRadioOptions').val("0");
        $("#relayScale_ts").show();
        $("#relayScale").show();
    });
    $(".noMuch").click(function(){
        $(".pubBlic").hide();
        $(this).removeClass('btn-default').addClass('btn-primary');
        $('.getMuch').removeClass('btn-primary').addClass('btn-default');
        $('[name="inlineRadioOptions"]').attr("checked",false);
        $("#chargeAmt").val("");
        $('#customDistribution').val('');
        $("#relayScale_ts").hide();
        $("#relayScale").hide();
        $("#relayScale").val('');
    });

    $("#isRelay").change(function(){
        var isRelay = $("#isRelay").val();
        if(!isRelay || isRelay == '0'){
            $("#relayCharge").val('');
            $("#relay_div").hide();
            $("#relayScale").val('');
        }else{
            $("#relay_div").show();
            $("#relayCharge").show();
            if($(".getMuch").hasClass("btn-primary")) {
                $("#relayScale").show();
                $("#relayScale_ts").show();
            }
        }
    });
</script>
<script>
    //图文混排数组
    var courseContent = [];
    var isRemarkImageUploading = false;
    function dads2(input) {
        if (isRemarkImageUploading) {
            alert("正在上传中，请稍后！");
            return
        }
        isRemarkImageUploading = true;

        var file = input.files[0];
        if (!file) {
            isRemarkImageUploading = false;
            return ;
        }
        var reader = new FileReader();
        reader.readAsDataURL(file);
        ajaxFileUpload2();
        reader.onload = function (e) {
            $("#uploadLi").hide();
            var html ='<div class="contBoxall">\
                        <div class="wordCont">\
                            <dl>\
                                <dt><img src="' + this.result + '"><span id="uploadTip">正在上传中...</span></dt>\
                                <dd>\
                                    <textarea class="datext" placeholder="请输入图片简介" maxlength="800"></textarea>\
                                </dd>\
                            </dl>\
							<span class="newcloss" onclick="colssesWx(this)"></span>\
                        </div>\
                        <div class="rightBox">\
                            <span class="oPrev" onclick="oPrev(this)"></span>\
                            <span class="oNext" onclick="oNext(this)"></span>\
                        </div>\
                    </div>'
            $("#detailsList").append(html);
        }
    };
    function ajaxFileUpload2() {
        JQuery.ajaxFileUpload({
            url: '/file/upload',
            secureuri: false,
            async: false,
            fileElementId: 'file3',//file标签的id
            dataType: 'json',//返回数据的类型
            data: {},//一同上传的数据
            success: function (data, status) {
                var json = {"img":data.data.url, "content":''};
                courseContent.push(json);
                isRemarkImageUploading = false;
                $("#uploadLi").show();
                $("#uploadTip").remove();
            },
            error: function (data, status, e) {
                alert(e);
                isRemarkImageUploading = false;
                $("#uploadLi").show();
                $("#uploadTip").remove();
            }
        });
    }
    //上传文字
    function detailsText(){
        var json = {"img":"", "content":""};
        courseContent.push(json);
        var oDiv ='<div class="contBoxall">\
                        <div class="wordCont">\
                            <dl>\
                                <dd>\
                                    <textarea  class="datext" placeholder="请输入文字简介"maxlength="800"></textarea>\
                                </dd>\
                            </dl>\
							<span class="newcloss" onclick="colssesWx(this)"></span>\
                        </div>\
                        <div class="rightBox">\
                            <span class="oPrev" onclick="oPrev(this)"></span>\
                            <span class="oNext"  onclick="oNext(this)"></span>\
                        </div>\
                    </div>'
        $('#detailsList').append(oDiv);
    };

    //数组位置调换
    function change(arr,index,upDow){
        var temp;
        temp = arr[index];
        if(upDow=='up'){
            arr[index] = arr[index-1];
            arr[index-1] = temp;
        }else if(upDow=='dow'){
            arr[index] = arr[index+1];
            arr[index+1] = temp;
        }
        return arr;
    };
    //上移
    function oPrev(that){
        var _this = $(that).parents('.contBoxall');
        var index = _this.index();
        if(index==0){
            alert('到头了');
            return;
        }
        var oPrev = _this.prev('.contBoxall');
        _this.insertBefore(oPrev);
        change(courseContent,index,'up');
    };
    //下移动
    function oNext(that){
        var _this = $(that).parents('.contBoxall');
        var index = _this.index();
        if(index==($('.contBoxall').length-1)){
            alert('到底了');
            return;
        }
        var oNext = _this.next('.contBoxall');
        oNext.insertBefore(_this);
        change(courseContent,index,'dow');
    };
    //删除图片
    function colssesWx(that) {
        var index = $(that).parents('.contBoxall').index();
        $(that).parents('.contBoxall').remove();
        courseContent.splice(index,1);
    };
    //文字加进数组
    function addDatext(){
        $.each(courseContent, function (i, n) {
            n.content = $('.datext').eq(i).val();
        });
    }

</script>
<script src="/web/res/js/ajaxfileupload.js"></script>
</html>
