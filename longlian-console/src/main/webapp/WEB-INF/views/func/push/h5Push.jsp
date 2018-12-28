<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>推广渠道数据统计</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <style>
        .bootstrap-table{clear: both}
    </style>


</head>
<body>
<div class="container" style="width:97%;margin-top:10px">
    <h2 style="margin-bottom: 20px;margin-top: 15px">
        消息推送
    </h2>
    <div id="toolbar" >
        <form class="form-horizontal" id="form1" name="form1">
            <div class="col-sm-6" >
                <span style="color:#f00">*</span><span id="inputText" style="position: absolute;bottom: 20px;right: 100px; color:#ccc">0/200</span> <textarea name="content" class="form-control content" rows="9" onKeyDown="LimitTextArea(this)" onKeyUp="LimitTextArea(this)" onkeypress="LimitTextArea(this)" placeholder="请输入推送消息,推送消息将会在通知栏及应用内弹窗显示"></textarea>
            </div>
            <div class="col-sm-6" style="width:50%;margin-top:20px">
                <textarea name="pushUrl" class="form-control pushUrl" rows="9" placeholder="请输入带有http://或https://开头的链接"></textarea>
            </div>
            <div class="col-sm-12" style="margin-top:100px"><button type="button" class="btn btn-primary center-block" id="pushButton">提交</button></div>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100 ,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true">
    </table>
</div>
<script>
    //提交
    function LimitTextArea(field) {     
        maxlimit = 200;
        $("#inputText").text(field.value.length+"/200");
        if (field.value.length > maxlimit) {
            field.value = field.value.substring(0, maxlimit);
            //alert("内容不得超过200字!");
        }
    }

    $("#pushButton").click(function (){
        if($.trim($(".content").val())==""){
            return;
        }

        var data={"content":$.trim($(".content").val()),"pushUrl":$.trim($(".pushUrl").val())};
        $.ajax({
            type: "POST",
            url: "/pushMsg/insertH5PushMsg.user",
            data:data,
            beforeSend:function(){
            $("#pushButton").prop('disabled',true)
        },
        success: function (data) {
            if (data.success) {
                jbox_notice({content: "提交成功", autoClose: 2000, className: "success"});
                window.location.replace("/pushMsg/toH5MsgPushPage")
            } else {
                jbox_notice({content: "提交失败", autoClose: 2000, className: "error"});
                $("#pushButton").prop('disabled',false)
            }
        }
    })
    });
</script>

</body>
</html>
