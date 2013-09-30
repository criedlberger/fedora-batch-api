<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<c:choose>
	<c:when test="${event == 'getCommunitiesByName'}">
		<ul>
		<c:forEach items="${actionBean.communities}" var="com" varStatus="status">
			<li id="${com.id}">${com.title}</li>
		</c:forEach>
		<c:if test="${fn:length(actionBean.communities) == 0}">
			<li><fmt:message key="searchResult.noDataFound" /></li>
		</c:if>
		</ul>	
	</c:when>
</c:choose>
