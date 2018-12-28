<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
		<div id="navbar" class="navbar navbar-default">

			<div class="navbar-container " id="navbar-container">
				 
				<div class="navbar-header pull-left" role="navigation">
					<!-- #section:basics/navbar.layout.brand -->
					<%--<a href="javascript:void(0);" class="navbar-brand dropdown-toggle" data-toggle="dropdown">--%>
					<a href="javascript:void(0);" class="navbar-brand">
						<i class="fa fa-leaf"></i>
						酸枣直播后台管理系统
					</a>
					<%--<c:if test="${userType eq 'admin'}">--%>
					<%--<ul class="dropdown-menu dropdown-blue dropdown-caret dropdown-close system-type" style="margin-left: 45px;">--%>
						<%--<li><a href="javascript:void(0);" data-type="zhibo">华章</a></li>--%>
						<%--<li class="divider"></li>--%>
						<%--<li><a href="javascript:void(0);" data-type="caidao">和讯培训</a></li>--%>
					<%--</ul>--%>
					<%--</c:if>--%>
				</div>
				<div class="navbar-buttons navbar-header pull-right" role="navigation">
					<ul class="nav ace-nav">
						 
						<!-- #section:basics/navbar.user_menu -->
						<li class="light-blue">
							<a data-toggle="dropdown" href="#" class="dropdown-toggle">
								<img class="nav-user-photo" src="" alt="" />
								<span class="user-info">
									<small>你好,</small>
									${loginName }
								</span>
								<i class="ace-icon fa fa-caret-down"></i>
							</a>
							<ul class="user-menu dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
								<li>
									<a target="_blank" href="">
										<i class="ace-icon fa fa-cog"></i>
										修改密码
									</a>
								</li>
								<li class="divider"></li>
								<li>
									<a href="javascript:void(0);" onclick="logouts();">
										<i class="ace-icon fa fa-power-off"></i>
										退出登录
									</a>
								</li>
							</ul>
						</li>
					</ul>
				</div>
			</div> 
		</div>
		<script>
			function logouts(){
                layer.confirm('确认退出么？', {icon: 4, title:'提示'}, function (index, ele) {
                    $.ajax({
                        url :'<%=basePath%>logout',
                        type : "post",
                        //async : false,
                        dataType : "json",
                        success : function(data) {
							window.location.href="/login"
                        },
                        error : function() {
                            //layer.msg('添加失败！', {time: 3000,icon: 0});
                            return;
                        }
                    });
                });
            }
		</script>