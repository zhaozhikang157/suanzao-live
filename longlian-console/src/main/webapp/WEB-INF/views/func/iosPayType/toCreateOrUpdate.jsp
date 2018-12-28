<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="iosPayType">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
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
    <script src="${requestContext.contextPath}/web/res/js/func/iosPayType/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            bindImageUpload("upload", 5 * 1024 * 1024, myCallBack)
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
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <input type="hidden" class="form-control" name="id" ng-model="iosPayType.id" autocomplete="off" placeholder="">

        <div class="form-group">
            <label for="orderSort" class="col-sm-3 control-label">金额:</label>

            <div class="col-sm-5">
                <input type="text" class="form-control" id="orderSort" name="orderSort" ng-model="iosPayType.amount"
                       autocomplete="off" placeholder="" check-type="required">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">龙链实收金额：</label>

            <div class="col-sm-5">
                <input type="text" class="form-control" name="name" ng-model="iosPayType.llReallyAmount"
                       autocomplete="off"
                       placeholder="" check-type="required">
            </div>
        </div>

        <div class="form-group">
            <label for="remark" class="col-sm-3 control-label">用户实收金额:</label>

            <div class="col-sm-5">
                <input type="text" class="form-control" id="remark" name="remark" ng-model="iosPayType.userReallyAmount"
                       autocomplete="off" placeholder="" check-type="required">
            </div>
        </div>

        <div class="form-group">
            <label for="iosCommodityId" class="col-sm-3 control-label">支付商品ID:</label>

            <div class="col-sm-5">
                <input type="text" class="form-control" id="iosCommodityId"
                       ng-model="iosPayType.iosCommodityId" autocomplete="off" placeholder="" check-type="required"
                       maxlength="10">
            </div>
        </div>

        <div class="form-group">
            <label for="iosCommodityName" class="col-sm-3 control-label">支付商品名称:</label>

            <div class="col-sm-5">
                <input type="text" class="form-control" id="iosCommodityName"
                       ng-model="iosPayType.iosCommodityName" check-type="required"
                       maxlength="10">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">备注:</label>

            <div class="col-sm-5">
                <textarea class="form-control" ng-model="iosPayType.remark" maxlength="50">

                </textarea>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">类型:</label>

            <div class="col-sm-5">
                <div class="checkbox">
                    <label>
                        <input type="radio" value="0" name="type" ng-model="iosPayType.type"/> &nbsp;IOS
                    </label>
                    <label>
                        <input type="radio" value="1" name="type" ng-model="iosPayType.type"/> &nbsp;安卓
                    </label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-3 control-label">状态:</label>

            <div class="col-sm-5">
                <div class="checkbox">
                    <label>
                        <input type="radio" value="0" name="status" ng-model="iosPayType.status" />
                        &nbsp;启用
                    </label>
                    <label>
                        <input type="radio" value="1" name="status" ng-model="iosPayType.status"/> &nbsp;禁用
                    </label>
                </div>
            </div>
        </div>

    </form>
</div>
</body>
</html>
</div>
</body>
</html>
