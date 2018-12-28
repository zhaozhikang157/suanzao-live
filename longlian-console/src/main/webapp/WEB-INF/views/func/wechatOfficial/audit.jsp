<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<!DOCTYPE html>
<html ng-app="wechatOfficialRoom">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
  <title>公众号审核</title>
  <%@include file="/WEB-INF/views/include/header.jsp"%>
  <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
  <script src="${ctx}/web/res/angular/angular.js"></script>
  <script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
  <script src="${ctx}/web/res/angular/angular-messages.js"></script>
  <script src="${ctx}/web/res/my97/WdatePicker.js"></script>
  <script src="${ctx}/web/res/my97/xdate.dev.js"></script>
    <script>
    var id = ${id};
    $(function(){
      //1. 简单写法：
      $("#form1").validation({icon:true});
    });

    function getNowFormatDate() {
      var date = new Date();
      var seperator1 = "-";
      var seperator2 = ":";
      var month = date.getMonth() + 1;
      var strDate = date.getDate();
      if (month >= 1 && month <= 9) {
        month = "0" + month;
      }
      if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
      }
      var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
              + " " + date.getHours() + seperator2 + date.getMinutes()
              + seperator2 + date.getSeconds();
      return currentdate;
    }
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
    <%--<div class="form-group">
      <div class="centers">
          <label class="control-label titles">课程编号：</label>
          <span>{{course.id}}</span>
      </div>
    </div>
    <div class="form-group">
      <div class="centers">
        <label  class="control-label titles">课程名称：</label>
        <span class="much">{{course.liveTopic}}</span>
      </div>
    </div>
    --%>

      <div class="form-group" >
        <label class="col-sm-3 control-label"><span style="color: red"></span>负责人：</label>
        <div class="col-sm-6">
          <select ng-model="managerId" id="managerId">
            <option value="">--请选择--</option>
            <option ng-repeat=" item in managerList" value="{{item.id}}">{{item.name}}</option>
          </select>
        </div>
      </div>
        <div class="form-group">
            <label class="col-sm-3 control-label">有效期：</label>
            <div class="col-sm-6" id="startTimeInput">
                <input type="text" class="form-control" id="freeDates"
                       autocomplete="off" placeholder=""
                       onclick="WdatePicker({skin:'twoer',charset:'gb2312',isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:getNowFormatDate(),maxDate:'2088-03-10 20:59:30'})"
                       onchange="setDate(this);"/>
            </div>
        </div>
      <div class="form-group">
        <label class="col-sm-3  control-label"><span style="color: red"></span>审核意见：</label>
        <div class="col-sm-6">
          <textarea class="texry" id="remark" name="remark" ng-model="wechatOfficialRoom.remark"  autocomplete="off" placeholder="" maxlength="100" ></textarea>
        </div>
      </div>
  </form>
</div>
<div class="footerBtn">
  <button type="button" class="btn btn-danger" style="margin-right: 6px"  onclick="toCheck(-1)">
    <i class=" glyphicon glyphicon-remove"></i> 不通过
  </button>
  <button type="button" class="btn btn-success passBtn"  onclick="toCheck(1)">
    <i class=" glyphicon glyphicon-ok"></i> 通过
  </button>
</div>
</body>
</html>
