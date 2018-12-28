<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="wechatOfficialRoom">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>公众号审核</title>
    <%@include file="/WEB-INF/views/include/header.jsp"%>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/default/datepicker.css"/>
    <link rel="stylesheet" href="${ctx}/web/res/my97/skin/WdatePicker.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/web/res/my97/skin/whyGreen/datepicker.css"/>
    <script src="${ctx}/web/res/angular/angular.min.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.min.js"></script>
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
        var id = ${id};
        $(function(){
            //1. 简单写法：
            $("#form1").validation({icon:true});
        });

    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/wechatOfficial/app.js"></script>
    <style>
        .titles{
            width: 250px;
            text-align: right;
        }
        .much{
            display: inline-block;
        }
        .texry{
            position: absolute;
            left: 252px;
            top: 0;
            width: 250px;
            height: 66px;
            font-size: 10px;
            margin-left: 4px;
            resize: none;
            font-size: 14px;
        }
        .footerBtn{
            clear: both;
            text-align: center;
            margin-top: 80px;
        }
    </style>
</head>
<body>
<div  style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from"  id="form1" name="myform" style="" ng-controller="task">

        <div class="form-group" style="position:relative;">
            <label class="col-sm-3 control-label">联系方式：</label>
            <div class="col-sm-6">
                <input type="text" class="form-control" id="mobile" name="mobile" ng-model="wechatOfficialRoom.mobile"
                       autocomplete="off" placeholder=""  maxlength="20">
            </div>
        </div>
    </form>
</div>
<div class="footerBtn">

    <button type="button" class="btn btn-success passBtn"  onclick="save()">
        <i class=" glyphicon glyphicon-ok"></i> 保存
    </button>
</div>
<script>

    function save(){
        var mobile=$("#mobile").val();  //审核意见
        if(mobile.trim().length==0){
            alert("手机号码不能为空")
            return ;
        }
        if(!(/^1[3|4|5|8][0-9]\d{4,8}$/.test(mobile))){
            alert("不是完整的11位手机号或者正确的手机号前七位");
            return ;
        }
        $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
        $.ajax({
            type: "POST",
            data:{id:id,mobile:mobile},
            url: "/wechatOfficial/updateMobile",
            success: function (obj) {
                $(".footerBtn button").attr("disabled",false);  //还原点击事件
                if(obj.success){
                    window.parent.closeWindows(obj); //调用父页面方法关闭窗口
                }else{
                    jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
                }
            }
        })
    }

</script>
</body>
</html>
