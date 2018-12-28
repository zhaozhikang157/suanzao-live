<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>--%>
<%@include file="/WEB-INF/views/common/taglibs.jsp"%>
<style>
    .nav-list{
        padding-bottom: 40px;
    }
</style>
<div id="sidebar" class="sidebar responsive">

    <ul class="nav nav-list">
        <li class="">
            <a href="javascript:void(0);" onclick="goContent('/stat')">
                <i class="menu-icon fa fa-indent"></i>
                <span class="menu-text">首页</span>
                <b class="arrow fa fa-angle-down"></b>
            </a>
        </li>
        <c:forEach items="${menus.data.resList}" var="tag">
            <li class="">
                <a href="javascript:void(0);" class="dropdown-toggle">
                    <i class="menu-icon fa fa-indent"></i>
                    <span class="menu-text">${tag.name}</span>
                    <b class="arrow fa fa-angle-down"></b>
                </a>
                <c:if test="${not empty tag.children}">
                    <b class="arrow"></b>
                    <ul class="submenu">
                        <c:forEach items="${tag.children}" var="menu">
                            <li class="">
                                <a data-url="javascript:void(0);" href="#" onclick="goContent('${menu.url}')">
                                    <i class="menu-icon fa fa-caret-right"></i> ${menu.name}
                                </a>
                                <b class="arrow"></b>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </li>
        </c:forEach>
    </ul>
</div>
<script>
    var path='${basePath}';
    function goContent(url){
      //  $('#myFrame').attr('src',url);
        var oriUrl = url;
        if('/' == url.substr(0,1)){
            oriUrl = url.substr(1,url.length) ;
        }
        setStorageSync('origin_url',path+oriUrl,15*1000);
        $("#contents").load(url);
       // $("#breadcrumbs").hide();
    }

    $(function(){
        var url_cookie = getStorageSync('origin_url');
        if(url_cookie && url_cookie != null && url_cookie != ''){
        } else {
            $("#contents").load("/stat");
        }
    });
</script>