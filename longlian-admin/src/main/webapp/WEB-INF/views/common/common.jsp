<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static" />
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>
<script src="${ctxStatic}/js/jquery-1.11.1.min.js"></script>
<script src="${ctx}/web/res/js/tools.js"></script>
<%--<script src="${ctxStatic}/assets/js/ajax.google.jquery.min.js"></script>--%>
<script src="${ctxStatic}/assets/js/bootstrap.min.js"></script>
<!-- bootstrap & fontawesome -->
<link rel="stylesheet" href="${ctxStatic}/assets/css/bootstrap.min.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/font-awesome.min.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/jquery-ui-1.10.3.full.min.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/datepicker.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/ui.jqgrid.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/css.css?family=Open+Sans:400,300" />
<link rel="stylesheet" href="${ctxStatic}/web/res/style/css/style.css"/>
<link rel="stylesheet" href="${ctxStatic}/font-awesome/css/font-awesome.css"/>

<!-- ace styles -->
<link rel="stylesheet" href="${ctxStatic}/assets/css/ace.min.css" id="main-ace-style" />

<!--[if lte IE 9]>
<link rel="stylesheet" href="${ctxStatic}/assets/css/ace-part2.min.css" />
<![endif]-->
<link rel="stylesheet" href="${ctxStatic}/assets/css/ace-skins.min.css" />
<link rel="stylesheet" href="${ctxStatic}/assets/css/ace-rtl.min.css" />
<%--<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/common.css" />--%>
<link rel="stylesheet" href="${ctxStatic}/common/css/common.css" />
<link   href="${ctxStatic}/assets/css/fullcalendar.css" rel="stylesheet"/>
<link   href="${ctxStatic}/assets/css/fullcalendar.print.css" rel="stylesheet"/>
<!--[if lte IE 9]>
<link rel="stylesheet" href="${ctxStatic}/assets/css/ace-ie.min.css" />
<![endif]-->

<link   href="${ctxStatic}/assets/layer/skin/layer.css" rel="stylesheet"/>

<!-- ace settings handler -->
<script src="${ctxStatic}/assets/js/ace-extra.min.js"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->

<!--[if lte IE 8]>
<script src="${ctxStatic}/assets/js/html5shiv.js"></script>
<script src="${ctxStatic}/assets/js/respond.min.js"></script>
<![endif]-->
<%--<script type="text/javascript" src="${ctxStatic}/js/My97DatePicker/WdatePicker.js"></script>--%>

<script type="text/javascript">
    if('ontouchstart' in document.documentElement){
        document.write("<script src='${ctxStatic}/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
    }
</script>

<script type="text/javascript">
    window.jQuery || document.write("<script src='${ctxStatic}/assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
</script>
<script type="text/javascript">
    if("ontouchend" in document) document.write("<script src='${ctxStatic}/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
</script>
<script src="${ctxStatic}/assets/js/typeahead-bs2.min.js"></script>

<!-- page specific plugin scripts -->

<%--<script src="${ctxStatic}/assets/js/date-time/bootstrap-datepicker.min.js"></script>--%>
<script src="${ctxStatic}/assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="${ctxStatic}/assets/js/jqGrid/i18n/grid.locale-en.js"></script>
<!-- ace scripts -->
<script src="${ctxStatic}/assets/js/ace-elements.min.js"></script>
<script src="${ctxStatic}/assets/js/ace.min.js"></script>
<script src="${ctxStatic}/assets/js/common.js"></script>
<script src="${ctxStatic}/assets/layer/layer.js"></script>
<script src="${ctxStatic}/assets/layer/color-ext.js"></script>
<script type="text/javascript">
    //Load content via ajax
    $.ajax({
        url: '${ctx}/check',
        type: "GET",   //请求方式
        async:false,
        beforeSend: function() {
        },
        success: function(data) {
            if (!data) {
                window.location.href = '${ctx}/login.jsp';
            }
        },
        complete: function() {
        },
        error: function() {
        }
    });

    jQuery(function($) {
        if('enable_ajax_content' in ace) {
            var options = {
                content_url: function(url) {
                    //this is for Ace demo only, you should change it
                    //please refer to documentation for more info

                    if(!url.match(/^page\//)) return false;

                    var path = document.location.pathname;

                    //for Ace HTML demo version, convert ajax.html#page/gallery to > gallery.html and load it
                    if(path.match(/ajax\.html/)) return path.replace(/ajax\.html/, url.replace(/^page\//, '')+'.html') ;

                    //for Ace PHP demo version convert "page/dashboard" to "?page=dashboard" and load it
                    return path + "?" + url.replace(/\//, "=");
                },
                default_url: ''//default url
            }
            ace.enable_ajax_content($, options)
        }
    })
</script>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>