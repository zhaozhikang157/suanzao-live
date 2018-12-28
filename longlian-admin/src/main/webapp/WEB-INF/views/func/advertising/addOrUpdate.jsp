<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="advertising">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>广告管理</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/reset.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/upload.css"/>
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-process.js"></script>
    <script src="${ctx}/web/res/fileupload/js/jquery.fileupload-validate.js"></script>
    <script src="${ctx}/web/res/fileupload/upload.js"></script>
    <script src="${ctx}/web/res/js/func/advertising/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            bindImageUpload("upload", 200 * 1024, myCallBack)
            $("#form1").validation({icon: true});
        });
        function myCallBack(data) {
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $("#src").show();
                $scope.setPicAddress(data.data.url);
                $('.Img').attr("src", data.data.url);
            } else {
                jbox_notice({content: "选择图片失败", autoClose: 2000, className: "error"});
            }
        }

    </script>

</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="width: 94%"
          ng-controller="task">

        <div class="form-group">
            <label for="name" class="col-sm-3 control-label">广告名称:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="name" name="name" ng-model="advertising.name"
                       autocomplete="off" placeholder="" check-type="required" maxlength="20">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">广告类型:</label>

            <div class="col-sm-6">
                <select class="form-control" ng-model="advertising.advertType"
                        ng-options="item.value  as item.text for item in advertTypes"
                        check-type="select_required">
                    <option value="">--请选择--</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">类型:</label>

            <div class="col-sm-6">
                <select class="form-control" ng-model="advertising.type" check-type="select_required">
                    <option value="">--请选择--</option>
                    <option value="0">首页</option>
                    <option value="1">讲师轮播图</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">状态:</label>

            <div class="col-sm-6">
                <select class="form-control" ng-model="advertising.status" check-type="select_required">
                    <option value="">--请选择--</option>
                    <option value="0">启用</option>
                    <option value="1">禁用</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">图片路径:</label>

            <div class="col-sm-6 btn-upload">
                <div class="shade-box">本地上传</div>
                <span class="shade-text">请选择图片.png,.gif ,.jpg 图片大小200KB以内 ， 建议尺寸：750*388</span>
                <input type="file" class="file-btn" name="files[]" style="left:0;" id="upload" accept=".png,.gif,.jpg"
                       value="本地上传"/>

            </div>
        </div>
        <input type="hidden" id="picAddress" name="picAddress" ng-model="advertising.picAddress">

        <div class="form-group" style="display:none;" id="src">
            <label class="col-sm-3 control-label"></label>

            <div class="col-sm-6">
                <img class="Img" ng-src="{{imgSrc}}" height="50px"/>
            </div>
        </div>

        <div class="form-group">
            <label for="sortOrder" class="col-sm-3 control-label">排序:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="sortOrder" name="sortOrder" ng-model="advertising.sortOrder"
                       autocomplete="off" placeholder="" check-type="integeZero">
            </div>
        </div>

        <div class="form-group">
            <label for="openUrl" class="col-sm-3 control-label">打开地址:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="openUrl" name="openUrl" ng-model="advertising.openUrl"
                       autocomplete="off" placeholder="">
            </div>
        </div>

        <div class="form-group">
            <label for="courseId" class="col-sm-3 control-label">课程ID/直播间ID:</label>

            <div class="col-sm-6">
                <input type="text" class="form-control" id="courseId" name="courseId" ng-model="advertising.courseId"
                       autocomplete="off" placeholder="" check-type="number">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">备注:</label>

            <div class="col-sm-6">
                <textarea class="form-control" style="resize: none" name="remarks" autocomplete="off"
                          ng-model="advertising.remarks" placeholder="" maxlength="80"></textarea>
            </div>
        </div>
    </form>
</div>
</body>
<script>
    $(".file-btn").change(function () {
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

    })
</script>
</html>
