<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="pageUrl">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title></title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <script type="text/javascript" src="${requestContext.contextPath}/web/res/jquery/jquery.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${requestContext.contextPath}/web/res/angular/angular-messages.js"></script>
    <script>
        var id = ${id};
        $(function () {
            //1. 简单写法：
            $("#form1").validation({icon: true});
        })
    </script>
    <script src="${requestContext.contextPath}/web/res/js/func/dataStatistics/pageUrl.js"></script>
    <script>
   /*     var userName = "";
        var oldName = "";
        var mUserName = "";
        function repeatName(){
            alert("222");
            userName = $("#shareChannelId").val();
            if(mUserName!=userName){
                alert("333")
                if(!userName==""||!userName==null){
                    var param = "userId="+userName;
                    var obj = tools.requestRs("/shareChannel/findUserId", param, 'post');
                    if(!obj.success){       //提交用户名重复
                        oldName = obj.data;
                    }
                }
            }
            alert("4444")
        }
        function getName(){
            mUserName = $("#shareChannelId").val();
            document.getElementById('shareChannelId').onclick=null;
        }*/
    </script>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label for="pageUrl" class="col-sm-3 control-label">url链接:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="pageUrl"
                       ng-model="pageUrl.pageUrl" autocomplete="off" placeholder="" check-type="required"  onblur="repeatName()" onclick="getName()"id="pageUrl">
            </div>
        </div>

        <div class="form-group">
            <label for="pageName" class="col-sm-3 control-label">url名称:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control"  name="pageName"
                       ng-model="pageUrl.pageName" autocomplete="off" placeholder=""  check-type="required"  onblur="repeatName()" onclick="getName()"id="pageName">
            </div>
        </div>
    </form>
</div>
</body>
</html>
