<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>导入用户</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/style/css/reset.css"/>
    <style>
        .shade-box{
            display: inline-block;
            position: relative;
            width: 120px;
            height: 32px;
            text-align: center;
            line-height: 32px;
            color: #FF5500;
            background: transparent;
            border: 1px solid #FF5500;
            border-radius: 4px;
        }
        .shade-text{
            position: absolute;
            left: 140px;
            top: 30px;
            width: 225px;
            height: 32px;
            line-height: 32px;
            color: #747474;
            font-size: 12px;
        }
        .file-btn{
            position: absolute;
            top: 30px;
            left: 0;
            opacity: 0;
            width: 120px;
            height: 32px;
            cursor: pointer;
        }
    </style>
    <script type="text/javascript" src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script type="text/javascript" src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js" ></script>
    <script src="${ctx}/web/res/angular/angular.min.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.min.js"></script>
    <script>
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        });
    </script>
</head>
<body>
<div>
    <div class="contact-tab" style="margin-top:30px; " >
        <form id="form3" class="form3" method="post" enctype="multipart/form-data" action="/avatar/uploadUser">
            <div class="shade-box">本地上传</div>
            <span class="shade-text">文件格式为.xls,.xlsx</span>
            <input type="file" id="fileField" class="file-btn file" name="file" id="businessLicensePIC" value="本地上传" accept=".xls,.xlsx" />
        </form>
    </div>
</div>
</body>
<script>

    $(".file-btn").change(function(){
        var urlVal = $(this).val();
        var filtName = getFileName(urlVal);

        function getFileName(o){
            var pos=o.lastIndexOf("\\");
            return o.substring(pos+1);
        }
        $(this).prev().text(filtName);
        window.parent.form4=$(".form3");
    })
</script>
</html>
