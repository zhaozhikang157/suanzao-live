<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>设置分享时长</title>
    <%@include file="/WEB-INF/views/func/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap/js/bootstrap3-validation.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/pc/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.js"></script>
    <script src="${ctx}/web/res/js/tools.js"></script>
    <style>
        .titles {
            width: 250px;
            text-align: right;
        }

        .much {
            display: inline-block;
        }

        .texry {
            width: 250px;
            height: 70px;
            font-size: 10px;
            resize: none;
            font-size: 14px;
        }

        .footerBtn {
            clear: both;
            text-align: center;
            margin-top: 80px;
        }
    </style>
</head>
<body>
<div style="margin-top:10px; ">
    <form class="form-horizontal form-horizontal-my-from" id="form1" name="myform" style="" ng-controller="task">
        <div class="form-group">
            <label for="mustShareTime" class="col-sm-3 control-label">分享时长:</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="mustShareTime"
                       autocomplete="off" placeholder="" maxlength="10" check-type="required" id="mustShareTime">
            </div>
        </div>
        <div class="footerBtn">
            <button type="button" class="btn btn-success passBtn" onclick="setShareTime()">
                <i class=" glyphicon glyphicon-ok"></i> 保存
            </button>
        </div>
    </form>
</div>
        <script>
            var id = ${id};
            $(function(){
                $.ajax({
                    type: "GET",
                    data: {id: id},
                    url: "/pcCourse/findByIdForEdit",
                    success: function (obj) {
                        if (obj.success) {
                            if (obj.data.course.mustShareTime == 0) {
                                $("#mustShareTime").val(obj.data.course.recTime);
                            }else{
                                $("#mustShareTime").val(obj.data.course.mustShareTime);
                            }
                        } else {
                            jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                        }
                    }
                })
            })


            function setShareTime() {
                var mustShareTime = $("#mustShareTime").val();
                if (mustShareTime == "") {
                    alert("请输入分享时长！");
                    return;
                }
                if (isNaN(mustShareTime)) {
                    alert("分享时长必须为数字！");
                    return;
                }
                if (mustShareTime <=0) {
                    alert("分享时长必须大于0！");
                    return;
                }
                $(".footerBtn button").attr("disabled", true);   //让按钮失去点击事件
                $.ajax({
                    type: "POST",
                    data: {id: id, mustShareTime: mustShareTime},
                    url: "/pcCourse/setShareTime.user",
                    success: function (obj) {
                        $(".footerBtn button").attr("disabled", false);  //还原点击事件
                        if (obj.success) {
                            if (obj.code == "1001") {
                                alert(obj.msg)
                                return;
                            }
                            window.parent.closeWindows(obj);
                        } else {
                            jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                        }
                    }
                })
            }
        </script>

</body>
</html>
