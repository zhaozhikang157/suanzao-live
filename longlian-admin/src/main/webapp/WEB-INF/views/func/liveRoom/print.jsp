<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>打印</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/jquery/jquery-1.4.4.min.js"></script>
    <script src="${ctx}/web/res/jquery/jquery.jqprint-0.3.js"></script>
</head>
<body>
<div style="margin-top:10px;" id="print">
    <form class="form-horizontal form-horizontal-my-from" >

        <div class="form-group">
            <label class="col-sm-3 control-label">手机号:</label>

            <div class="col-sm-7">
                <span class="mobels"> ${appUser.mobile}</span>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">身份证正面:</label>
        </div>
        <div class="form-group picboxs">
            <div class="col-sm-6 tappic" style="padding-left: 166px"><img src=${appUser.idCardFront}  alt=""/></div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">身份证反面:</label>
        </div>
        <div class="form-group picboxs">
            <div class="col-sm-6 tappic" style="padding-left: 166px"><img src=${appUser.idCardRear}  alt=""/></div>
        </div>
    </form>
</div>
<button type="button" class="btn btn-info" onclick="billPrint()" style="display: block; float: right">
    <i class="glyphicon glyphicon-print"></i> 打印
</button>
</body>
<script>
    function billPrint() {
        $("#print").jqprint();
    }
</script>
</html>

