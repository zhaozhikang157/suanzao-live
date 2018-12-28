<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="mobileVersion">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>版本管理</title>
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
    <script src="${ctx}/web/res/js/func/mobileVersion/app.js"></script>
    <script>
        var finish = false;
        var id = ${id};
        $(function () {
            //1. 简单写法：
            bindFileUpload("upload" , "" , 50 *1024 *1024 , myCallBack , null , startCallBack , "useOriginName=1") ;
            $("#form1").validation({icon: true});
            finish = true;
        });
        function myCallBack(data) {
        	
            data = data.result;
            if (data.success) {
                var appElement = document.querySelector('[ng-controller=task]');
                var $scope = angular.element(appElement).scope();
                $scope.setAddress(data.data.url);
            } else {
                jbox_notice({content: "上传文件失败", autoClose: 2000, className: "error"});
            }
            isUploading = false;
        }
        var isUploading = false;
        function startCallBack(data , ctrlId) {
        	 isUploading = true;
        }
    </script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label for="name" class="col-sm-3 control-label">版本名称:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="name" name="name" ng-model="mobileVersion.name"
                       autocomplete="off" placeholder="" maxlength="20" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">版本类型:</label>

            <div class="col-sm-7">
                <select class="form-control" ng-model="mobileVersion.versionType"
                        ng-options="item.value  as item.text for item in versionTypes"
                        check-type="select_required">
                    <option value="">  请选择</option>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="versionNum" class="col-sm-3 control-label">版本型号:</label>

            <div class="col-sm-7">
                <input type="text" class="form-control" id="versionNum" name="versionNum" ng-model="mobileVersion.versionNum"
                       autocomplete="off" placeholder="" check-type="required" maxlength="20">
            </div>
        </div>


        <div class="form-group">
            <label class="col-sm-3 control-label">版本状态:</label>

            <div class="col-sm-7">
                <select class="form-control" ng-model="mobileVersion.status"
                        ng-options="item.id  as item.name for item in statuses" check-type="required">
                    <option value="">  请选择</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">上传版本:</label>

            <div class="col-sm-7 btn-upload">
                <div class="shade-box">本地上传</div>
                <span class="shade-text">请选择文件.apk/zip</span>
                <input type="file" class="file-btn" name="files[]" style="left:0;" id="upload" accept=".apk,.zip" value="本地上传" />
                <input type="text" class="form-control"  ng-model="mobileVersion.downloadAddress" maxlength="30">
            </div>
         
        </div>
        <input type="hidden" id="downloadAddress" name="downloadAddress" ng-model="mobileVersion.downloadAddress">
   


        <div class="form-group">
            <label class="col-sm-3 control-label">是否强制升级:</label>

            <div class="col-sm-7">
                <select class="form-control" ng-model="mobileVersion.isFoceUpdate"
                        ng-options="item.id  as item.name for item in isFoceUpdates" check-type="required">
                    <option value="">  请选择</option>
                </select>
            </div>
        </div>


        <div class="form-group">
            <label class="col-sm-3 control-label">版本概要:</label>

            <div class="col-sm-7">
                <textarea rows="10" class="form-control" style="resize: none" name="versionBrief" autocomplete="off"
                          ng-model="mobileVersion.versionBrief" placeholder="" maxlength="80"></textarea>
            </div>
        </div>
    </form>
</div>
</body>
<script>
    $(".file-btn").change(function(){
        var urlVal = $(this).val();
        var filtName = getFileName(urlVal);
        if(!(/(?:apk|zip)$/i.test(filtName))){
            alert("该类型文件不支持上传，请选择.zip格式");
            return;
        }
        function getFileName(o){
            var pos=o.lastIndexOf("\\");
            return o.substring(pos+1);
        }
        $(this).prev().text(filtName);

    })
</script>
</html>
