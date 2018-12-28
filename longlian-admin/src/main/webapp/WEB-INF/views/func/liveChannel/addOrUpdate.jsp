<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="liveChannel">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>关注金额规则</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/func/liveChannel/app.js"></script>
    <script>
        var id = ${id};
        $(function () {
            $("#form1").validation({icon: true});
        });
    </script>
</head>
<body>
<div  style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group">
            <label class="col-sm-4 control-label">通道编号：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="code"  name="code"  ng-model="liveChannel.code" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>

        <div class="form-group" id="codeNum">
            <label class="col-sm-4 control-label">自动生成：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" id="num"  name="num"  autocomplete="off" placeholder=""  check-type="positivIntege" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label">通道推送地址：</label>
            <div class="col-sm-5">
        <input type="text" class="form-control"  name="pushAddr"  ng-model="liveChannel.pushAddr" autocomplete="off" placeholder=""  check-type="required" >
</div>
</div>

        <div class="form-group">
            <label class="col-sm-4 control-label">通道直播地址rtmp：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="playAddr1"  ng-model="liveChannel.playAddr1" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label">通道直播地址flv：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="playAddr2"  ng-model="liveChannel.playAddr2" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-4 control-label">通道直播地址m3u8：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="playAddr3"  ng-model="liveChannel.playAddr3" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">加速域名：</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="domain"  ng-model="liveChannel.domain" autocomplete="off" placeholder=""  check-type="required" >
            </div>
        </div>
    </form>
</div>
</body>
</html>

