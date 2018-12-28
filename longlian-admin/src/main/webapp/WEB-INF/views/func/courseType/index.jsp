<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html ng-app="courseTypev">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>课程类型</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${requestContext.contextPatg}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/angular/angular.min.js"></script>
    <script src="${ctx}/web/res/angular/angular-messages.min.js"></script>
    <style>
        .tabBox {
            margin-top: 20px;
        }
        .tabBox th {
            text-align: center;
        }
    </style>

</head>
<body>
<script src="${ctx}/web/res/js/func/courseType/index.js"></script>
<div class="container" style="width:86%;float: left;" ng-controller="task">
    <h3 style="margin: 15px 0"> 课程类型 </h3>
    <div id="toolbar">
        <form class="form-inline" id="form1" name="form1">
            <label class="control-label">名称:</label>
            <input type="text" class="form-control" name="name" autocomplete="off" placeholder="请输入姓名"
                   placeholder="" style="width: 120px;display: inline-block;">


            <button type="button" class="btn btn-info" ng-click="query()">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>
            <button type="button" class="btn btn-primary" onclick="createOrUpdate(0);">
                <i class="glyphicon glyphicon-plus"></i> 新增
            </button>
            <button type="button" class="btn btn-info" id="sort" onclick="sorts($(this))">
                <i class=" glyphicon glyphicon-search"></i> 排序
            </button>
        </form>
    </div>
    <table class="table table-bordered tabBox">
        <thead align="center" class="tableHead">
            <th>序号</th>
            <th>名称</th>
        <%--    <th>主题简介</th>--%>
            <th>状态</th>
           <%-- <th>图标地址</th>--%>
            <th>排序</th>
            <th>操作</th>
        </thead>
  <c:forEach items="${courseTypes}" var="courseType">
        <tr align="center" ng-repeat="courseType in courseTypeList">
            <td class="sortBtnBox" dto_id="${courseType.id}" len="{{courseTypeList.length}}">
                ${courseType.id}
            </td>
            <td>${courseType.name}</td>
            <td>${courseType.status}</td>
            <td>${courseType.orderSort}</td>
            <td>
                <span><a class="like" href="javascript:void(0) " dto_id="${courseType.id}"
                         onclick="createOrUpdate($(this));">编辑</a></span>
                <span><a class="like" href="javascript:void(0) " dto_id="${courseType.id}"
                         onclick="deleteById($(this));">删除</a></span>
            </td>
        </tr>
    </c:forEach>

    </table>
</div>
</body>

<script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
<script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<script src="${ctx}/web/res/bootstrap/js/bootstrap3-validation.js"></script>
<script>
    function createOrUpdate(id) {
        if (id != 0) {
            id = id.attr("dto_id");
        }
        var title = "添加课程类型";
        if (id) title = "编辑课程类型";
        var url = "/courseType/toAddOrUpdate?id=" + id;
        bsWindow(url, title, {
            height: 400, width: 550, submit: function (v, h) {
                var cw = h[0].contentWindow;
                cw.doSave(function (json) {
                    if (json.success) {
                        window.BSWINDOW.modal("hide");
                        jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                        location.reload();
                    } else {
                        jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                    }
                });
            }
        });
    }

    function deleteById(obj) {
        var id = $(obj).attr("dto_id");
        var option = {
            width: 500, content: "确定删除吗？", confrimFunc: function (jboxObj) {
                var param = {id: id};
                var obj = tools.requestRs("/courseType/deleteById", param, 'get');
                if (obj.success) {
                    jboxObj.close();
                    jbox_notice({content: "删除成功", autoClose: 2000, className: "success"});
                    location.reload();
                } else {
                    jbox_notice({content: "删除失败", autoClose: 2000, className: "error"});
                }
            }
        };
        jbox_confirm(option);
    }


    var i = 1;
    function sorts(that) {
        var html = '', html2 = '', html3 = '';
        html += '<button class="prev" onclick="prev($(this))">上移</button><button class="next" onclick="next($(this))">下移</button>';
        html2 += '<i class=" glyphicon glyphicon-save"></i> 保存';
        html3 += '<i class=" glyphicon glyphicon-search"></i> 排序';


        if (i == 0) { //保存
            that.html(html3)
            var len = $(".sortBtnBox").attr("len");

            var str = "";
            for (var j = 0; j < len; j++) {
                var id = $(".sortBtnBox").eq(j).attr("dto_id");
                str = str + id + ",";
            }
            $.ajax({
                url: "/courseType/toOrder",
                dataType: "json",
                type: "POST",
                async: false,
                data: {ids: str},
                success: function (json) {
                   if(json.success) {
                       jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                   }else{
                       jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                   }
                }
            });
            window.location.reload();
            i++;
        } else {//排序
            that.html(html2);
            $(".sortBtnBox").html(html);
            i--;
        }
    }
    function prev(that) {//上移
        var _tr = that.parent().parent();
        if (_tr.index() == 1) {
            return;
        } else {
            _tr.after(_tr.prev())
        }
    }
    function next(that) {//下移
        var _tr = that.parent().parent();
        _tr.before(_tr.next());
    }
</script>
</html>
