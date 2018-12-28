<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="course">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程编辑</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/upload.css"/>
    <link href="${ctx}/web/res/cropper/dist/cropper.css" rel="stylesheet">
    <link href="${ctx}/web/res/cropper/css/main.css" rel="stylesheet">
    <script src="${ctx}/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-process.js"></script>

    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-validate.js"></script>
    <script src="${ctx}/web/res/fileupload/upload.js"></script>
    <script src="${ctx}/web/res/js/func/course/app.js?id=11"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <script src="${ctx}/web/res/cropper/dist/cropper.js"></script>

    <script>
        var id = ${id};
        $(function () {
            //bindImageUpload("uploadCoverssAddress", 100 * 1024, uploadCoverssAddressCallBack)
            // bindImageUpload("uploadColImageAddress", 100 * 1024, uploadColImageAddressCallBack)
        
//            $("#form1").validation({icon: true});

            $('#apply').change(function () {
                if ($(this).val() == "1") {
                    $("#word").show();
                    $("#image").hide();
                    $("#pushImgSrc").hide();
                } else if ($(this).val() == "2") {
                    $("#word").hide();
                    $("#image").show();
                    if($("#pushImgSrc").val().contents("http"))
                    {
                    $("#pushImgSrc").show();
                    }
                }
                else {
                    $("#word").hide();
                    $("#image").hide();
                    $("#pushImgSrc").hide();
                }
            })
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
    </script>

</head>
<link rel="stylesheet" href="${ctx}/web/res/style/css/course/course.css"/>

<body>
<div style="margin-top:10px; " id="formDiv">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%"
          ng-controller="task">

        <div class="form-group">
            <label for="liveTopic" class="col-sm-3 control-label">标题:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="liveTopic" name="liveTopic" ng-model="course.liveTopic"
                       autocomplete="off" placeholder="" check-type="required" maxlength="20">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">列表页图片:</label>

            <div class="col-sm-6 btn-upload">
                <div class="shade-box">本地上传</div>
                <span class="shade-text">请选择图片.png,.gif,.jpg , 图片大小100KB以内</span>
                <input type="file" class="file-btn" name="files[]" style="left:0;" id="uploadColImageAddress"
                       is_crop="true" img_address="colImageAddressImg" accept=".png,.gif,.jpg"
                       value="本地上传"/>
            </div>
        </div>
        <div class="form-group" id="colImageAddressSrc">
            <label class="col-sm-3 control-label"></label>

            <div class="col-sm-6">
                <img class="colImageAddressImg" ng-src="{{colImageAddressImgSrc}}" height="50px"/>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">课程封面图片:</label>

            <div class="col-sm-6 btn-upload">
                <div class="shade-box">本地上传</div>
                <span class="shade-text">请选择图片.png,.gif,.jpg , 图片大小100KB以内</span>
                <input type="file" class="file-btn" name="files[]" style="left:0;" id="uploadCoverssAddress"
                       is_crop="true" img_address="coverssAddressImg" accept=".png,.gif,.jpg"
                       value="本地上传"/>
            </div>

        </div>
        <div class="form-group" id="coverssAddressSrc">
            <label class="col-sm-3 control-label"></label>

            <div class="col-sm-6">
                <img class="coverssAddressImg" ng-src="{{coverssAddressImgSrc}}" height="50px"/>
            </div>
        </div>
        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label">课程类型:</label>

            <div class="col-sm-6">
                <select class="form-control" ng-model="course.courseType" ng-options="item.id as item.name for item in courseTypes" >
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label">课程介绍:</label>

            <div class="col-sm-6">
                <textarea class="form-control ng-pristine ng-valid ng-valid-maxlength ng-touched" style="resize: none;height: 100px" id="remark" name="remark"  autocomplete="off" ng-model="course.remark"
                            placeholder=""></textarea>
            
            </div>
        </div>
        <%--<div class="form-group">--%>
            <%--<label class="col-sm-3 control-label">介绍图片:</label>--%>

            <%--<div class="col-sm-6">--%>
                <%--<div class="wrap" id="imgStatus">--%>
                    <%--<div class="add_imgs">--%>
                        <%--<input type="file" id="file2" style="width: 110px; height:110px; opacity: 0.1;" name="file" --%>
                               <%--onchange="dads(this)" accept=".png,.gif,.jpg"/>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="form-group">--%>
            <%--<label for="apply" class="col-sm-3 control-label">推送方式:</label>--%>

            <%--<div class="col-sm-6">--%>
                <%--<select name="" class="form-control sex" check-type="" id="apply" ng-model="course.pushType">--%>
                <%--&lt;%&ndash;    <option ng-if="course.pushType==1" selected>文字</option>--%>
                    <%--<option ng-if="course.pushType==2" selected>图片</option>&ndash;%&gt;--%>

                    <%--<option value="0" ng-model="course.pushType">请选择</option>--%>
                    <%--<option ng-model="course.pushType" value="1">文字</option>--%>
                    <%--<option ng-model="course.pushType" value="2">图片</option>--%>
                <%--</select>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="form-group">--%>
            <%--<label for="startTime" class="col-sm-3 control-label">关注后推送内容:</label>--%>

            <%--<div class="col-sm-5" id="word">--%>
                <%--<textarea class="form-control ng-pristine ng-valid ng-valid-maxlength ng-touched" style="resize: none;height: 100px" name="pushContent"  autocomplete="off" ng-model="course.pushContent"--%>
                          <%--placeholder="" ></textarea>--%>
            <%--</div>--%>
            <%----%>
            <%----%>
        <%--</div>--%>
        <%--<div class="form-group" id="image">--%>
        <%--<label class="col-sm-3 control-label"></label>--%>

        <%--<div class="col-sm-6 btn-upload">--%>
            <%--<input type="hidden" name="MAX_FILE_SIZE" value="100000" />--%>
            <%--<div class="shade-box">本地上传</div>--%>
            <%--<span class="shade-text">请选择图片.png,.gif,.jpg , 图片大小100KB以内</span>--%>
            <%--<input type="file" class="file-btn" name="files[]" style="left:0;" id="uploadPushimg" onchange="check()"--%>
                   <%--accept=".png,.gif,.jpg"--%>
                   <%--value="本地上传"/>--%>
        <%--</div>--%>
    <%--</div>--%>
        <%--<div class="form-group" id="pushImgSrc">--%>
            <%--<label class="col-sm-3 control-label"></label>--%>

            <%--<div class="col-sm-6">--%>
                <%--<img class="pushImg" ng-src="{{pushImgSrc}}" height="50px"/>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div class="form-group">
            <label for="startTime" class="col-sm-3 control-label">开课时间:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="startTime" name="startTime"
                       ng-model="course.startTime | date:'yyyy-MM-dd HH:mm:ss'"
                       autocomplete="off" placeholder=""
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'twoer',charset:'gb2312'})"
                       onchange="setDate(this);"/>
            </div>
        </div>

    </form>
</div>


<div id="crop" style="display: none;" style="padding: 10px 15px;">
    <div style="width: 760px;float: left;">
        <div style="max-height: 580px;max-width: 760px;">
            <img alt="Picture" id="shop_photo">
        </div>

    </div>
    <div style="width: 180px;float: left; padding: 30px 0px 0px 28px;text-align: center;">
        <div class="img-preview preview-lg">
        </div>
        <div>APP实际展示效果</div>
        <div style="margin-top:15px; ">
            <input type="button" class="btn btn-primary" style="padding: 6px 60px" name="add" value="确定"
                   data-method="getCroppedCanvas" data-option="{ &quot;width&quot;: 664, &quot;height&quot;: 373 }"
                   cropper_type=""/>
          <!--  data-option="{ &quot;width&quot;: 664, &quot;height&quot;: 373 }"-->
            <input type="button" class="btn" style="padding: 6px 60px ;margin-top:10px ;" name="close" value="取消"/>
        </div>
    </div>
    <div style="clear: both;"></div>
    <div id="colImageAddressImg-canvas-file" style="display: none;;"></div>
    <div id="coverssAddressImg-canvas-file" style="display: none;;"></div>
</div>
</body>
<script src="${ctx}/web/res/js/jquery-1.7.2.js" type="text/javascript" charset="utf-8"></script>
<script>
    $(".add_imgs").off("click").on("click", function () {
        var _index = $('#imgStatus').children('.picss').length;
//        alert(_index)
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
            html += '<span class="closes" onclick="clocs(this)">X</span>'
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
//        alert(_index)
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

            case 40:s
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


</script>
<script src="/web/res/js/ajaxfileupload.js"></script>
</html>
