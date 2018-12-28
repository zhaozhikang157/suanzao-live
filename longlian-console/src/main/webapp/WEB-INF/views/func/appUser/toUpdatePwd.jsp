<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="course">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>修改密码</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <script src="${ctx}/web/res/angular/angular.js"></script>
  <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script>
    var id = ${id};
    $(function(){
      //1. 简单写法：
      $("#form1").validation({icon:true});
    });
  </script>
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
    <div class="form-group">
      <label class="col-sm-3 control-label">新密码：</label>
      <div class="col-sm-6" >
        <input type="text" class="form-control" autocomplete="off" placeholder="" id="pwd" />
      </div>
    </div>

  </form>
</div>
<div class="footerBtn">
  <button type="button" class="btn btn-success passBtn"  onclick="toCheck()">
    <i class=" glyphicon glyphicon-ok"></i>确定
  </button>
</div>
<script>
    function toCheck(auditStatus){
        var pwd=$("#pwd").val();
        $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
        if(pwd.trim().length>0){
            $.ajax({
                type: "POST",
                data:{id:id,pwd:pwd},
                url: "/appUser/resetOrUpdatePwd",
                success: function (obj) {
                    $(".footerBtn button").attr("disabled",false);  //还原点击事件
                    if(obj.success){
                        window.parent.closeWindows(obj); //调用父页面方法关闭窗口
                    }else{
                        jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
                    }
                }
            })
        }else{
            jbox_notice({content:"请输入新密码",autoClose:2000 ,className:"warning"});
            $(".footerBtn button").attr("disabled",false);  //还原点击事件
        }
    }
</script>

</body>
</html>
