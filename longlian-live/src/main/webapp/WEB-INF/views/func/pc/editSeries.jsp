<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="course">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>编辑系列课</title>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/pc/css/upload.css"/>
    <link href="${ctx}/web/res/pc/cropper/dist/cropper.css" rel="stylesheet">
    <link href="${ctx}/web/res/pc/cropper/css/main.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/web/res/pc/css/boxImg.css"/>
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
    <script src="${ctx}/web/res/pc/js/seriesEdit.js?nd=<%=current_version%>"></script>
    <script src="${ctx}/web/res/pc/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/pc/my97/xdate.dev.js"></script>
    <script src="${ctx}/web/res/pc/cropper/dist/cropper.js"></script>
    <script src="${ctx}/web/res/js/plugins/boxImg.js"></script>
    <script>
        var id = ${id};
        $(function () {
           
            $("#form1").validation({icon: true});
        });
        function uploadCoverssAddressCallBack(data) {
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $("#coverssAddressSrc").show();
                $scope.setCoverssAddress(data.data.url);
                $('.coverssAddressImg').attr("src", data.data.url);
            } else {
                jbox_notice({content: "选择图片失败", autoClose: 2000, className: "error"});
            }
        }
        function uploadColImageAddressCallBack(data) {
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $("#colImageAddressSrc").show();
                $scope.setColImgAddress(data.data.url);
                $('.colImageAddressImg').attr("src", data.data.url);
            } else {
                jbox_notice({content: "选择图片失败", autoClose: 2000, className: "error"});
            }
        }
        function uploadPushimgCallBack(data) {
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $("#pushImgSrc").show();
                $scope.setPushContent(data.data.url);
                $('.pushImg').attr("src", data.data.url);
            } else {
                jbox_notice({content: "选择图片失败", autoClose: 2000, className: "error"});
            }

        }
        function uploadCourseImg(data) {
            var appElement = document.querySelector('[ng-controller=task]');
            var $scope = angular.element(appElement).scope();
            $scope.setCourseImg(data);
        }
        function check() {
            var imagSize = document.getElementById("uploadPushimg").files[0].size;
            if (imagSize > 1024 * 1024 * 2 )
            {
                alert("图片大小应在2M以内，为：" + 1024 * 1024 * 2 + "M");
                return false;
            }else
            {
                bindImageUpload("uploadPushimg", 100 * 1024, uploadPushimgCallBack)
                return true;
            }
           
        }
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
            border: 1px solid #ccc;
            background: url("/web/res/image/backpicnew.png") no-repeat center center;
            cursor: pointer;
        }
        .card_img {
            width: 190px;
            height: 289px;
            float: left;
            background: url("/web/res/image/backpic_rate.png") no-repeat center center;
            cursor: pointer;
        }
        #card_file{
            width: 190px;
            height: 289px;
            opacity: 0;
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
            color:#2e6da4 ;
            border: 1px solid #2e6da4;
            text-align: center;
            float: left;
            text-indent: 7px;
            cursor: pointer;
        }
        .lowbtn li:first-child{
            background: url("/web/res/image/picsmial.png") no-repeat 38px center;
            margin-right: 40px;
        }
        .lowbtn li:last-child {
            background: url("/web/res/image/textsmile.png") no-repeat 38px center;
            margin-right: 40px;
        }

        #file3{
            width: 184px;
            height: 48px;
            position: absolute;
            opacity: 0;
             }
        .newcloss{
            width: 32px;
            height: 32px;
            background: url("/web/res/image/closeBtn.png") no-repeat center center;
            position: absolute;
            left: -15px;
            top: -12px;
        }
        .seveBtn{
            float: left;
            width: 185px;
            height: 48px;
            line-height: 48px;
            text-align: center;
            background:#3366b7 ;
            color: white;
            margin: 20px 10px;
            border-radius: 8px;
            /*clear: both;*/
            cursor:pointer;
        }
        .asterisk{
            color: red;
            vertical-align: middle;
        }
        .back_btn{
            float: left;
            width: 185px;
            height: 48px;
            line-height: 48px;
            text-align: center;
            background: #3366b7;
            color: white;
            margin: 20px 1px;
            border-radius: 8px;
            cursor: pointer;
        }
        .file {
            position: relative;
            display: inline-block;
            background: #3366b7;
            border: 1px solid #3366b7;
            border-radius: 4px;
            overflow: hidden;
            text-decoration: none;
            text-indent: 0;
            float: left;
            height: 48px;
            line-height: 48px;
            text-align: center;
            background: #3366b7;
            color: white;
            margin: 20px 10px;
            border-radius: 8px;
            cursor: pointer;
        }
        .file input {
            position: absolute;
            font-size: 100px;
            right: 0;
            top: 0;
            opacity: 0;
            cursor: pointer;
        }
        #uploadPrivateCard a,#card_file{
            cursor: pointer;
        }
    </style>
</head>
<body onbeforeunload="onloadCheck()">
<div style="margin-top:30px; " id="formDiv">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%"
          ng-controller="task">

        
        <div class="form-group">
            <label class="col-sm-3 control-label"><span class="asterisk">* </span>课程封面图片：</label>
            <div class="col-sm-6 btn-upload">
                <input type="file" class="file-btn" name="files[]"  id="uploadCoverssAddress" 
                       is_crop="true" img_address="coverssAddressImg" accept=".png,.gif,.jpg"
                       />
                <img class="coverssAddressImg" ng-src="{{coverssAddressImgSrc}}" height="50px"/>
                <input type="hidden" name="coverssAddress"  ng-model="course.coverssAddress"   id="coverssAddress"/>
            </div>
        </div>
       
        <div class="form-group">
            <label for="liveTopic" class="col-sm-3 control-label"><span class="asterisk">* </span>课程名称：</label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="liveTopic" name="liveTopic" ng-model="course.liveTopic"
                       autocomplete="off" placeholder="" maxlength="20">
            </div>
        </div>
        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label">课程分类:</label>

            <div class="col-sm-6">
                <select class="form-control" ng-model="course.courseType" ng-options="item.id as item.name for item in courseTypes" >
                </select>
            </div>
        </div>
        <div class="form-group paymentType">
            <label for="" class="col-sm-3 control-label" style="margin-left:-10px"><span class="asterisk">* </span>是否转播:</label>
            <div class="col-sm-6" style="margin-left: 10px">
                <select required class="form-control" id = "isRelay">
                    <option></option>
                    <option value="1">是</option>
                    <option value="0">否</option>
                </select>
            </div>
        </div>
        <div class="form-group paymentType" id="relay_div">
            <label class="col-sm-3 control-label"></label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="relayCharge"  autocomplete="off" ng-model="course.relayCharge"  placeholder="不输入即为免费转播">
                <p id="relayScale_tx">买课分成比例（%）：</p>
                <input type="text" id="relayScale" class="form-control" ng-model="course.relayScale" placeholder="请输入0~100之间的整数"/>

            </div>
        </div>
        <div class="form-group" style="margin-bottom: 30px">
            <label for="remark" class="col-sm-3 control-label">课程简介：</label>
            <div class="col-sm-6">
                <textarea class="form-control ng-pristine ng-valid ng-valid-maxlength ng-touched" style="resize: none;height: 100px" id="remark" name="remark"  autocomplete="off" ng-model="course.remark"
                          placeholder="请输入课程简介" maxlength="800"></textarea>

            </div>
        </div>
        <c:if test="${isPrivateCard > 0}">
            <div class="form-group" id="uploadPrivateCard">
                <label class="col-sm-3 control-label">上传邀请卡模板：</label>
                <div style="float: left;width: 50%;" >
                    <a href="javascript:;" class="file" style="width: 30%;" >上传
                        <input type="file" class="file" name="card_file" id="card_file" onchange="addCard(this)" />
                    </a>
                    <p class="seveBtn" style="width: 30%;" value="" id="deleteModel">删除</p>
                    <p class="seveBtn" style="width: 30%;" value="" id="preview">预览</p>

                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label"></label>
                <div style="float: left;width: 50%; height: 300px;" id="previewDiv">
                    <div class="">
                        <input type="hidden" value="${courseCard.modelUrl}" name="modelUrl" id="modelUrl"/>
                        <input type="hidden" value="${courseCard.cardUrl}" name="cardUrl"  id="cardUrl">
                    </div>
                    <c:if test="${courseCard.modelUrl != null and courseCard.modelUrl != ''}">
                        <div class='picss'>
                            <img src="${courseCard.modelUrl}" width="190px" height="289px"/><span class="closes" onclick="clocs(this)"></span>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:if>
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
                <div style="float: left;width: 100%;">
                    <div class="errorMessages"style="padding-top: 25px; color: #FF0000;  width: 100%; float: left; font-size: 16px;"></div>
                    <p class="seveBtn" ng-click="sub()">保存</p>
                    <p class="back_btn" onclick="javascript:history.go(-1)">返回</p>
                </div>
                <%--<p class="seveBtn" ng-click="sub()">保存</p>--%>
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
        <div>APP实际展示效果</div>
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
<div class="mask-layer" style="display:none;">
    <div class="mask-layer-black"></div>
    <div class="mask-layer-container" style="height: 790px;width: 420px;">
        <div class="mask-layer-container-operate" style="height:42px">
            <button class="mask-close btn-default-styles">关闭</button>
        </div>
        <div class="mask-layer-imgbox auto-img-center" style="height: 711px;width: 400px;">
            <p>
                <img src="" alt="" style="width:400px;height: 711px;margin-top:15px;" id="cardPic">
            </p>
        </div>
    </div>
</div>
</body>
<script src="${ctx}/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
<script>

    
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
    })
    $(".noMuch").click(function(){
        $(".pubBlic").hide();
    })
    $(".relay").click(function(){
        $(this).removeClass('btn-default').addClass('btn-primary');
        $(".noRelay").removeClass("btn-primary").addClass("btn-default");
    })
    $(".noRelay").click(function(){
        $(this).removeClass('btn-default').addClass('btn-primary');
        $(".relay").removeClass("btn-primary").addClass("btn-default");
    })
    $("#isRelay").change(function(){
        var isRelay = $("#isRelay").val();
        if(!isRelay || isRelay == '0'){
            $("#relayCharge").val('');
            $("#relayCharge").parent().parent().hide();
            $("#relayScale").val('');
        }else{
            $("#relayCharge").parent().parent().show();
        }
    });
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
        uploadCoursePhoto(coursePhoto.join(";"));
        //$('#preview_img').attr('src','');
        $('#cardUrl').val('');
        $('#modelUrl').val('');
    }
    //邀请卡模板上传
    function ajaxCardUpload() {
        JQuery.ajaxFileUpload({
            url: '/file/upload',
            secureuri: false,
            async: false,
            fileElementId: 'card_file',//file标签的id
            dataType: 'json',//返回数据的类型
            data: {},//一同上传的数据
            success: function (data, status) {
                var modelUrl = data.data.url;
                $('#modelUrl').val(modelUrl);
            },
            error: function (data, status, e) {
                alert(e);
            }
        });
    }
    //上传邀请卡模板
    function addCard(inputs) {
        var file = inputs.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        ajaxCardUpload();
        reader.onload = function (e) {
            var html = "";
            html += "<div class='picss'>";
            html += '<img src="' + this.result + '" width="190px" height="289px"/>';
            html += '<span class="closes" onclick="clocs(this)"></span>'
            html += "</div>";
            $('.picss').remove();
            $("#previewDiv").append(html);
        }
    }
    $(function(){
        $('#preview').bind('click',function(){
            var modelUrl = $('#modelUrl').val();
            if(modelUrl == ''){
                alert('请先上传模板');
                return;
            } else {
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "/pcCourse/getPreviewCourseCard",
                    data:{'modelUrl':modelUrl,'courseId':window.id},
                    success: function (obj) {
                        if (obj.code=='000000') {
                            //$('#preview_img').attr('src','');
                            //$('#preview_img').attr('src',obj.data);
                            $('#cardUrl').val(obj.data);
                            $('#cardPic').attr('src','');
                            $('#cardPic').attr('src',obj.data);
                            $('.mask-layer').show();
                        } else {
                            alert('预览失败');
                        }
                    }
                });
            }
        });
        $('.mask-close').bind('click',function(){
            $('.mask-layer').hide();
        });
        $('#deleteModel').bind('click',function(){
            var modelUrl = $('#modelUrl').val();
            $.ajax({
                type: "POST",
                async: false,
                url: "/courseCard/delCourseCard",
                data:{'modelUrl':modelUrl,'courseId':window.id},
                success: function (obj) {
                    if (obj.code=='000000') {
                        $('.picss').remove();
                        $('#modelUrl').val('');
                        alert('删除成功');
                    } else {
                        alert('预览失败');
                    }
                }
            });
        });
    })
</script>
<script>
//图文混排数组
var courseContent = [];
var isRemarkImageUploading = false;
function dads2(input) {
    if (isRemarkImageUploading) {
        alert("正在上传中，请稍后！");
        return ;
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
                                    <textarea  class="datext" placeholder="请输入文字简介" maxlength="800"></textarea>\
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
