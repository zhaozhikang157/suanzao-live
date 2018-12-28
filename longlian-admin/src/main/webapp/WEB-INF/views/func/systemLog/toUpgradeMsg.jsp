<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>升级提醒</title>
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
    <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
    <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <%--<script src="${ctx}/web/res/js/func/systemParaController/toUpgradeMsg.js"></script>--%>
    <script>
       // var id = ${id};
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
<div style="margin-top:10px;width: 86%;float: left;" align="center">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="">
        <div class="form-group">
            <label class="col-sm-3 control-label">标题:</label>
            <div class="col-sm-5">
                 <textarea class="form-control" style="resize: none;height: 40px" id="first" check-type="required"
                           autocomplete="off"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label  class="col-sm-3 control-label">晋升类型:</label>
            <div class="col-sm-5">
                 <textarea class="form-control" style="resize: none;height: 40px" id="keyword1" check-type="required"
                           autocomplete="off"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">截止时间:</label>

            <div class="col-sm-5">
                 <textarea class="form-control" style="resize: none;height: 40px" id="keyword2" check-type="required"
                           autocomplete="off"></textarea>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">备注:</label>
            <div class="col-sm-5">
             <textarea class="form-control" style="resize: none;height: 100px" id="remark"
                       autocomplete="off" check-type="required"></textarea>
            </div>
        </div>
        <div class="form-group" align="center">
            <label class="col-sm-3 control-label"></label>
            <div class="col-sm-5">
           <%--<button id="submit" value="提交">提交</button>--%>
                <input type="button" value="提交" id="submit">
            </div>
        </div>
    </form>
</div>
</body>
<script>
    
  $("#submit").click(function(){
      var first = $("#first").val();
      var keyword1 = $("#keyword1").val();
      var keyword2 = $("#keyword2").val();
      var remark = $("#remark").val();
      if(first==""|| first==null){
          alert("标题不能为空！")
          return;
      }
      if(keyword1==""|| keyword1==null){
          alert("晋升类型不能为空！")
          return;
      }
      if(keyword2==""|| keyword2==null){
          alert("截止时间不能为空！")
          return;
      }
      if(remark==""|| remark==null){
          alert("备注不能为空！")
          return;
      }
      var data={"first":first,"keyword1":keyword1,"keyword2":keyword2,"remark":remark};
      $.ajax({
          type : 'POST',
          url : '${pageContext.request.contextPath}/systemLogController/sendUpgradeMsg',
          contentType: "application/json; charset=utf-8",
          data : JSON.stringify(data),
          dataType : 'json',
          error : function(data) {
              alert("请求失败，网络异常！")
              console.log(data);
          },
          success : function(data) {
              alert("升级消息发送成功！")
                  window.location.href = "${pageContext.request.contextPath}/systemLogController/toUpgradeMsg";
             
          }
      });
  })
</script>
</html>
