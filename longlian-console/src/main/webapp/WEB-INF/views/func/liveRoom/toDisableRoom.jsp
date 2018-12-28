<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>禁用直播间</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <script src="${ctx}/web/res/angular/angular.js"></script>
  <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.js"></script>
  <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
  <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
  <script src="${ctx}/web/res/js/tools.js"></script>
  <style>
    .titles{
      width: 250px;
      text-align: right;
    }
    .much{
        display: inline-block;
    }
    .texry{
      width: 250px;
      height: 70px;
      font-size: 10px;
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

      <div class="form-group" >
        <label class="col-sm-3 control-label"><span style="color: red"></span>直播间状态：</label>
        <div class="col-sm-6">
          <select ng-model="roomStatus" id="roomStatus" onchange="changeRoomStatu()">
            <option value="">--请选择--</option>
            <option value="0">正常</option>
            <option value="1">禁用</option>
          </select>
        </div>
      </div>
    
      <div class="form-group" id="disableRooomRemark">
        <label class="col-sm-3  control-label"><span style="color: red"></span>直播间禁用备注：</label>
        <div class="col-sm-6">
          <textarea class="texry" id="disableRemark" name="disableRemark"   autocomplete="off" placeholder="" maxlength="100" ></textarea>
        </div>
      </div>
  </form>
</div>
<div class="footerBtn">
  <button type="button" class="btn btn-success passBtn"  onclick="toCheck()">
    <i class=" glyphicon glyphicon-ok"></i> 确定
  </button>
</div>
<script>
  var id = ${id};
  $(function(){
    var liveRoomParam = {id:id}
    var obj = tools.requestRs("/liveRoom/findById",liveRoomParam,'get');
    if(obj.success){
      $("#roomStatus").val(obj.data.roomStatus);
      if(obj.data.roomStatus==0){
        $("#disableRooomRemark").hide();
      }else if(obj.data.roomStatus==1){
        $("#disableRooomRemark").show();
        $("#disableRemark").val(obj.data.disableRemark);
      }
    }
  })

  
  function changeRoomStatu(){
    var roomStatus =$('#roomStatus option:selected') .val();//选中的值
    if(roomStatus==1){
      $("#disableRooomRemark").show();
    }else{
      $("#disableRooomRemark").hide();
      $("#disableRemark").val("")
    }
  }
  function toCheck(){
    var disableRemark=$("#disableRemark").val();  //审核意见 
    var roomStatus =$('#roomStatus option:selected') .val();//选中的值
    if(roomStatus==""){
      alert("请选择禁用状态！");
      return;
    }
    $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
      $.ajax({
        type: "POST",
        data:{roomId:id,disableRemark:disableRemark,roomStatus:roomStatus},
        url: "/liveRoom/disableRoom",
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
