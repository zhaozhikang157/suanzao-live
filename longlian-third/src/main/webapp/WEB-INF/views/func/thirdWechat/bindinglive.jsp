<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.huaxin.util.spring.CustomizedPropertyConfigurer" %>
<%@ page import="java.net.URLEncoder" %>
<%@include file="/WEB-INF/views/common/taglibs.jsp" %>
<%
    String suanzao_website = CustomizedPropertyConfigurer.getContextProperty("suanzao.website");
    String wxMenuBindUrl = CustomizedPropertyConfigurer.getContextProperty("wechate.menu.bind.suanzao.url");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>PC微信公众号</title>
    <%@include file="/WEB-INF/views/include/header.jsp" %>
    <link rel="stylesheet" href="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.css">
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/bootstrap-table.js"></script>
    <script src="${requestContext.contextPath}/web/res/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/sys.js"></script>
    <script src="${requestContext.contextPath}/web/res/js/jboxTools.js"></script>
</head>

<div class="wrapp">
    <script>
        function doInit() {
            $('#table-bootstrap').bootstrapTable({
                method: 'post',
                contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                url: "/wechat/selectWechatOfficialRoomList.user",
                classes: 'table table-hover',
                height: getHeight(),
                toolbar: "#toolbar",
                pageSize: 20,
                // queryParams: queryParams,
                columns: [
                    {
                        field: 'id',
                        title: 'ID',
                        align: 'center',
                        valign: 'middle'
                    }, {
                        field: 'bindTime',
                        title: '绑定时间',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return getFormatDateTimeStr(value, 'yyyy-MM-dd HH:mm:ss')
                        }
                    },
                    {
                        field: 'liveName',
                        title: '绑定直播间名称',
                        align: 'center',
                        valign: 'middle'
                    },
                    {
                        field: 'liveRoomNo',
                        title: '微信菜单绑定路径',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (row.auditStatus == '1') {
                                return '<%=suanzao_website%><%=wxMenuBindUrl%>?liveRoomNo=' + value;
                            }else{
                                return '';
                            }
                        }
                    },
                    {
                        field: 'auditStatus',
                        title: '审核状态',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            if (value == '0') {
                                return "<span id='status-" + row.id + "'>待审核</span>"
                            } else if (value == '1') {
                                return "<span  id='status-" + row.id + "'>审核通过</span>"
                            } else {
                                return '<a data-toggle="tooltip" data-placement="top"  title="' + row.auditRemark + '"><font color="red">审核未通过?</font></a>'
                            }
                        }
                    },
                    {
                        field: '_opt_',
                        title: '操作',
                        align: 'center',
                        valign: 'middle',
                        formatter: function (value, row) {
                            return [
                                '<a href="javascript:void(0)" onclick="unBindLiveRoom(' + row.id + ');">' + '解绑</a>'
                            ].join('');
                        }
                    }
                ]
            })
        };

        function getHeight() {
            return $(window).height() - 6 - $('h2').outerHeight(true);
        }
    </script>
    <body onload="doInit()">
    <div id="toolbar">
        <form class="form-horizontal" id="form1" name="form1">
            <button type="button" class="btn btn-primary" onclick="bindLiveRoom();">
                <i class="glyphicon glyphicon-plus"></i> 绑定直播间
            </button>
            <button type="button" class="btn btn-primary" onclick="editMessageTemplet();">
                修改公众号消息模板
            </button>
        </form>
    </div>
    <table id="table-bootstrap"
           data-pagination="true"
           data-side-pagination="server"
           data-page-list="[10, 20, 50, 100,ALL]"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-pagination-switch="true">
    </table>
    <%--   <div class="publcont">
           <div class="contcecret">
               <span class="leftspan" onclick="bindLiveRoom()"></span>
               <span class="rightspan" id="bind" onclick="bindLiveRoom()">绑定直播间</span>
           </div>
           <table class="pubictab" border="" cellspacing="" cellpadding="">
               <thead>
               <th align="center" width="30%">绑定时间</th>
               <th align="center" width="30%">绑定直播间名称</th>
               <th align="center" width="60%">绑定直播间ID</th>
               &lt;%&ndash;<th align="center">预约提醒ID</th>&ndash;%&gt;
               &lt;%&ndash;<th align="center">预约提前提醒ID</th>&ndash;%&gt;
               <th align="center" width="70%">微信菜单绑定路径</th>
               <th align="center" width="70%">审核状态</th>
               <th align="center" width="10%">操作</th>
               </thead>
               <tbody>
               <tr>
                   <td id='bindTime'></td>
                   <td id="liveName"></td>
                   <td id="liveId"></td>
                   &lt;%&ndash;<td id="reserveReminderId"></td>&ndash;%&gt;
                   &lt;%&ndash;<td id="reservePreReminderId"></td>&ndash;%&gt;
                   <td id="menuBindUrl"></td>
                   <td id="auditStatus"></td>
                   <td id="caozuo"></td>
               </tr>
               </tbody>
           </table>
       </div>--%>
</div>
<script>
    /*    function load() {
            var url = "/wechat/selectWechatOfficialRoomList";
            var json = tools.requestRs(url);
            if (json.success) {
                var roomList = json.data;
                if (roomList.length> 0) {
                    for (var i = 0; i < roomList.length; i++) {
                        if (roomList[i].liveId != 0) {
                            $("#bindTime").text(getFormatDateTimeStr(roomList[i].bindTime, 'yyyy-MM-dd HH:mm:ss'));
                            $("#liveName").text(roomList[i].liveName);
                            $("#liveId").text(roomList[i].liveRoomNo);
                            var auditStatuStr = ""
                            if (roomList[i].auditStatus == 0) {
                                auditStatuStr = "待审核";
                            } else if (roomList[i].auditStatus == 1) {
                                auditStatuStr = "审核通过";
                            } else {
                                auditStatuStr = '<a data-toggle="tooltip" data-placement="top"  title="' + roomList[i].auditRemark + '">审核未通过?</a>';
                            }
                            $("#auditStatus").html(auditStatuStr);
<%--$("#menuBindUrl").text("<%=suanzao_website%><%=wxMenuBindUrl%>?liveRoomNo=" + roomList[i].liveRoomNo);--%>
                        var arra = new Array();
                        arra.push('<a href="javascript:void(0)" onclick="unBindLiveRoom(' + roomList[i].id + ');">' + '解绑</a>');
                        $("#caozuo").html(arra);
                    }
                }
            }
        }
    }*/
    /*    function bindLiveRoom(aid) {
            var title = "绑定直播间"
            var url = "/wechat/toBindLiveRoom";
            bsWindow(url, title, {
                height: 400, width: 700, buttons: []
            });
        }*/
    function bindLiveRoom(aid) {
        window.open('/wechat/toBindLiveRoom', 'newwindow', 'height=400,width=700,top=200,left=450,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no')
    }

    function editMessageTemplet() {
        var title = "编辑消息模板"
        var url = "/wechat/editMessageTemplet";
        bsWindow(url, title, {
            height: 300, width: 650, submit: function (v, h) {
                var cw = h[0].contentWindow;
                cw.doSave(function (json) {
                    if (json.success) {
                        window.BSWINDOW.modal("hide");
                        jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                        window.location.href="${requestContext.contextPath}/wechat/bindinglive"
                    } else {
                        jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
                    }
                });
            }
        });
    }

    function unBindLiveRoom(id) {
        var msg = confirm("您确定要解绑吗?");
        if (msg == true) {
            $.ajax({
                url: "/wechat/unBindLiveRoom.user?id=" + id,
                dataType: "json",
                type: "GET",
                async: false,
                success: function (result) {
                    window.location.href = "${requestContext.contextPath}/wechat/bindinglive";
                }
            });
            return true;
        } else {

            return false;
        }
    }

    function closeWindows() {
        window.BSWINDOW.modal("hide");
        jbox_notice({content: "完毕", autoClose: 2000, className: "success"});
    }


    $(function () {
        setTimeout(function () {
            $("[data-toggle='tooltip']").tooltip();
        }, 300);
    });
</script>
</body>
</html>
