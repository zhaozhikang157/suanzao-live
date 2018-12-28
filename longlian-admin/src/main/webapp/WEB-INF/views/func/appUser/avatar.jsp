<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1;text/html; charset=utf-8"/>
    <title>bootstrap table</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${ctx}/web/res/bootstrap-table/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/web/res/style/css/style.css">
    <script src="${ctx}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${ctx}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script>
        $(document).ready(function() {
            doInit();
        });
    </script>
    <script>
        var courseId = "${courseId}";
        function doInit() {
            var obj = {
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/avatar/findAllAvatar?courseId=${courseId}",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 10,
                queryParams: queryParams,
                //onLoadSuccess:onLoadSuccessFunc,
                columns: [{
                    field: 'id',
                    title: 'ID',
                    align: 'center',
                    valign: 'middle',
                },
                    {
                        field: 'name',
                        title: '姓名',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        field: 'gender',
                        title: '性别',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "男";
                            } else {
                                return "女";
                            }
                        }
                    },
                    {
                        field: 'photo',
                        title: '头像',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return "<img width='40' height='40' src='" + value + "'/>";
                        }
                    }
                ]
            };

            if (courseId) {
                obj.columns[obj.columns.length] = {
                    field: 'isInRoom',
                    title: '是否在聊天室里',
                    align: 'center',
                    valign: 'middle',
                    formatter: function (value, row) {
                        if (value == '0') {
                            return "<span id='isInRoomSpan-" + row.id + "'>否</span>";
                        } else if (value == '1') {
                            return "<span id='isInRoomSpan-" + row.id + "'>是</span>";
                        }
                    }
                }
            }
            //增加操作列
            obj.columns[obj.columns.length] = {
                field: '_opt_',
                title: '操作',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    var arra = new Array();
                    if (courseId) {
                        if (row.isInRoom == '1') {
                            arra.push('<a href="javascript:void(0)" onclick="removeRobot(' + row.id + ', ${courseId});">' + '退出</a>');
                        } else {
                            //arra.push('<span id="opt-'+  row.id +'><a href="javascript:void(0)" onclick="openWindow(' + row.id + ', ${courseId}, \'' + row.name + '\', \''+row.isInRoom+'\');">' + '聊天</a></span>');
                            arra.push('<a href="javascript:void(0)" onclick="openWindow(' + row.id + ', ${courseId}, \'' + row.name + '\', \'' + row.isInRoom + '\');">' + '聊天</a>');
                        }
                    } else {
                        arra.push('<a href="javascript:void(0)" onclick="edit(' + row.id + ');">' + '修改</a>');
                    }
                    return arra.join('');
                }
            };

            $('#table-bootstrap').bootstrapTable(obj);
        }
        //传递的参数
        function queryParams(params) {
            var name = $("input[name='name']").val();
            var isInRoom = $("#isInRoom").find("option:selected").val();
            if (!params) {
                params = {};
            }
            params["name"] = name;
            params["isInRoom"] = isInRoom;
            return params;
        }

        function onLoadSuccessFunc(data) {
            console.log(data);
            data = data.rows;
            for (var i = 0; i < data.length; i++) {
                var d = data[i];
                $("#table-bootstrap").bootstrapTable("checkBy", {field: "id", values: [d.id]});
            }

        }

        function refresh() {
            $('#table-bootstrap').bootstrapTable('refresh', {query: {a: 1, b: 2}});
        }

        $(window).resize(function () {
            $('#table-bootstrap').bootstrapTable('resetView', {
                height: getHeight()
            });
        });

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }

        /**
         *点击查询
         */
        function doQuery(flag) {
            if (flag) {
                $('#table-bootstrap').bootstrapTable('refreshOptions', {pageNumber: 1});
            } else {
                $('#table-bootstrap').bootstrapTable('refresh', {query: queryParams});
            }
        }

        function openWindow(userId, courseId, name, isInRoom) {
            <%--$("#isInRoomSpan-" + userId).text("是");--%>
            <%--$("#opt-" + userId).html('<a href="javascript:void(0)" onclick="openWindow(' + userId + ', ${courseId}, \'' + name + '\', \'1\');">' + '聊天</a>');--%>
            var url = "/course/virtualUser?id=" + courseId + "&userId=" + userId + "&name=" + encodeURIComponent(name) + "&isInRoom=" + isInRoom;
            window.open(url, '_blank');
        }

        function openUsers() {
            var ids = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.id
            });
            var names = $.map($('#table-bootstrap').bootstrapTable('getSelections'), function (row) {
                return row.name
            });

            for (var i = 0; i < ids.length; i++) {
                (function (_index) {
                    var id = ids[_index];
                    openWindow(id, courseId, names[_index]);
                })(i);
            }
        }

        function addUsers() {
            var count = $("input[name='count']").val();
            if (!count) {
                alert("添加用户数量不能为空")
                return;
            }
            if (count > 100) {
                alert("添加用户数量不能超过100")
                return;
            }
            $.getJSON("/avatar/addRoboot?courseId=" + courseId + "&count=" + count
                    , function (data) {
                        if (data.success) {
                            alert(data.data)
                            doQuery();
                        }
                    });
        }

        function removeUsers() {
            var count = $("input[name='count']").val();
            if (!count) {
                alert("移出用户数量不能为空")
                return;
            }
            if (count > 100) {
                alert("移出用户数量不能超过100")
                return;
            }
            $.getJSON("/avatar/removeRoboot?courseId=" + courseId + "&count=" + count
                    , function (data) {
                        if (data.success) {
                            alert(data.data)
                            doQuery();
                        }
                    });
        }

        function removeRobot(userId, courseId) {
            $.getJSON("/avatar/removeRobootByUserId?courseId=" + courseId + "&userId=" + userId
                    , function (data) {
                        if (data.success) {
                            alert(data.data)
                            doQuery();
                        }
                    });
        }

        /**
         * 导入页面
         */
        function importPhoto() {
            var title = "图片导入";
            var url = "/avatar/importPhoto";
            bsWindow(url, title, {
                height: 200, width: 430, submit: function (v, h) {
                    form3.submit();
                    window.BSWINDOW.modal("hide");
                    jbox_notice({content: "导入成功", autoClose: 2000, className: "success"});
                    $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                }
            });
        }

        /**
         * 导入页面
         */
        function importUser() {
            var title = "用户导入";
            var url = "/avatar/importUser";
            bsWindow(url, title, {
                height: 200, width: 430, submit: function (v, h) {
                    form4.submit();
                    window.BSWINDOW.modal("hide");
                    jbox_notice({content: "导入成功", autoClose: 2000, className: "success"});
                    $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                }
            });
        }

        function edit(id) {
            var title = "添加用户"
            if (id) title = "修改用户";
            var url = "/avatar/toAddOrUpdate?id=" + id;
            bsWindow(url, title, {
                height: 400, width: 550, submit: function (v, h) {
                    var cw = h[0].contentWindow;
                    cw.doSave(function (json) {
                        if (json.success) {
                            window.BSWINDOW.modal("hide");
                            jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                            $('#table-bootstrap').bootstrapTable('refresh', {silent: true});
                        } else {
                            alert(json.msg || "保存出错");
                        }
                        console.log(json);
                    });
                }
            });
        }
    </script>
</head>
<body onload="doInit();">
<div class="container" style="width:86%;float: left;">
    <h2>
        用户管理
    </h2>

    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            姓名:
            <input type="text" class="form-control" name="name" autocomplete="off"
                   style="width: 220px;display: inline-block;">
            <c:if test="${not empty courseId}">
                是否在聊天室里:
                <select id="isInRoom">
                    <option selected value="">全部</option>
                    <option value="0">否</option>
                    <option value="1">是</option>
                </select>
            </c:if>
            <button type="button" class="btn btn-info" onclick="doQuery(true);">
                <i class=" glyphicon glyphicon-search"></i> 查询
            </button>

            <c:if test="${ empty courseId}">
                <br>
                <button type="button" class="btn btn-info" onclick="importPhoto();">
                    <i class=" glyphicon"></i> 导入图片
                </button>
                <button type="button" class="btn btn-info" onclick="importUser();">
                    <i class=" glyphicon"></i> 导入用户
                </button>

            </c:if>
            <c:if test="${not empty courseId}">
                <br>
                <input type="text" class="form-control" name="count" id="count" autocomplete="off"
                       style="width: 220px;display: inline-block;">
                <button type="button" class="btn btn-info" onclick="addUsers();">
                    <i class=" glyphicon"></i> 添加
                </button>
                <button type="button" class="btn btn-info" onclick="removeUsers();">
                    <i class=" glyphicon"></i> 移出
                </button>
            </c:if>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"

    <%--        data-detail-view="true"
            data-detail-formatter="detailFormatter"--%>
           data-show-pagination-switch="true"
           class="table table-striped table_list table-bordered"
            >
    </table>
</div>


</body>
</html>
