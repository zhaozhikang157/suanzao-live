<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<style type="text/css">
	.pageLink {
		border: 1px solid #dddddd;
		padding: 4px 12px;
		text-decoration: none;
		border-radius: 5px;
	}
	
	.selectPageLink {
		border: 1px solid #1ab394;
		padding: 4px 12px;
		color: #1ab394;
		background-color: #ffefef;
		text-decoration: none;
		border-radius: 5px;
	}
</style>
</head>

<body>
    <input type="hidden" name="pageNo" id="pageNo">
	<!-- 分页标签 -->
	<div style="text-align: center; border: 0;height: 20px;line-height: 20px;font-size: 12px;margin-bottom: 20px;" class="pageDiv mt20">
		<pg:pager url="#" items="${pageView.totalRecord}" maxPageItems="${pageView.pageSize}" maxIndexPages="1000" isOffset="true">
		          总共：${pageView.totalRecord}条	共:${pageView.totalPage}页	
			<pg:first>
				<a id="first" href="#" class="pageLink">首页</a>
			</pg:first>
			<c:if test="${pageView.pageNo != 1 && pageView.totalPage > 0 }">
			  <a id="prev" href="#" class="pageLink">上一页</a>
			</c:if>
			<pg:pages>
				<c:choose>
					<c:when test="${pageView.pageNo==pageNumber}">
						<span class="selectPageLink">${pageNumber}</span>
					</c:when>
					<c:otherwise>
						<c:if test="${(pageNumber-pageView.pageNo lt 10) and (pageNumber-pageView.pageNo gt -10)}">
							<a href="javascript:pageAction('${pageNumber}')" class="pageLink">${pageNumber}</a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</pg:pages>
			<c:if test="${pageView.pageNo != pageView.totalPage  && pageView.totalPage > 0 }">
			   <a id="next" href="#" class="pageLink">下一页</a>
			</c:if>
			<pg:last>
			   <a id="last" href="#" class="pageLink">尾页</a>
			</pg:last>
		</pg:pager>
	</div>
<script type="text/javascript">
	$(function(){
		var pageNo = ${pageView.pageNo};
		var totalPage = ${pageView.totalPage };
		$("#first").bind("click",function(event){
			pageAction(1);
		});
		$("#prev").bind("click",function(event){
			if(pageNo>1){
				pageNo--;
			}else{
				pageNo = 1;
			}
			pageAction(pageNo);
		});
		$("#next").bind("click",function(event){
			if(totalPage>pageNo){
				pageNo++;
			}else{
				pageNo=totalPage;
			}
			pageAction(totalPage);
		});
		$("a[name='doNumberPage']").bind("click",function(event){
			pageAction(pageNo);
		});
		$("#last").bind("click",function(event){
			pageAction(totalPage);
		});
	});
	function pageAction(pageNo){
		$("#pageNo").val(pageNo);
		$("#queryForm").submit();
	}
</script>
</body>
</html>
