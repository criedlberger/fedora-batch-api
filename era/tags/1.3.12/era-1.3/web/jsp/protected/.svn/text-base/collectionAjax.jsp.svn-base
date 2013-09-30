<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<c:choose>
	<c:when test="${event == 'getCollectionsByName'}">
		<ul>
		<c:forEach items="${actionBean.collections}" var="col" varStatus="status">
			<li id="${col.id}">${col.title}</li>
		</c:forEach>
		<c:if test="${fn:length(actionBean.collections) == 0}">
			<li><fmt:message key="searchResult.noDataFound" /></li>
		</c:if>
		</ul>	
	</c:when>
</c:choose>
