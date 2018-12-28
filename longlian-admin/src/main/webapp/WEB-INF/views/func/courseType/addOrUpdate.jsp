<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="courseType">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程类型</title>
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
    <script src="${ctx}/web/res/js/func/courseType/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            $("#form1").validation({icon: true});
            bindImageUpload("upload", 100 * 1024, myCallBack)
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
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="overflow: hidden"
          ng-controller="task">

        <div class="form-group">
            <label class="col-sm-3 control-label">名称:</label>

            <div class="col-sm-7 btn-upload">
                <input type="text" class="form-control" id="name" name="name" ng-model="courseType.name"
                       autocomplete="off" placeholder="" check-type="required" maxlength="20">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">上级分类:</label>
            <div class="col-sm-5">
                <select class="form-control" ng-model="courseType.parentId" ng-options="item.id as item.name for item in parentMenu" >
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">状态:</label>

            <div class="col-sm-7">
                <select class="form-control" ng-model="courseType.status" check-type="select_required">
                    <option value="">--请选择--</option>
                    <option value="0">正常</option>
                    <option value="1">禁用</option>
                </select>
            </div>
        </div>

       <%-- <div class="form-group">
            <label class="col-sm-3 control-label">图片路径:</label>

            <div class="col-sm-7 btn-upload">
                <div class="shade-box">本地上传</div>
                <span class="shade-text">请选择图片.png/.gif</span>
                <input type="file" class="file-btn" name="files[]" style="left:0;" id="upload" accept=".png,.gif"
                       value="本地上传"/>

            </div>
        </div>
        <input type="hidden" id="picAddress" name="picAddress" ng-model="courseType.picAddress">

        <div class="form-group" style="display:none;" id="src">
            <label class="col-sm-3 control-label"></label>

            <div class="col-sm-7">
                <img class="Img" ng-src="{{imgSrc}}" height="50px"/>
            </div>
        </div>
--%>

      <%--  <div class="form-group">
            <label class="col-sm-3 control-label">主题简介:</label>

            <div class="col-sm-7">
                <textarea class="form-control" style="resize: none;height: 100px" name="remark" autocomplete="off"
                          ng-model="courseType.remark" placeholder="限80字" maxlength="80"></textarea>
            </div>
        </div>--%>
    </form>
</div>
</body>
<script>
    $(".file-btn").change(function () {
        var urlVal = $(this).val();
        var filtName = getFileName(urlVal);
        if (!(/(?:png|gif)$/i.test(filtName))) {
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
