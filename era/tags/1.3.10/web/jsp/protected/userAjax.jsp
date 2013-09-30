<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<c:choose>
	<c:when test="${event == 'getUsersByName'}">
		<ul>
		<c:forEach items="${actionBean.users}" var="user" varStatus="status">
			<c:if test="${user.state == 1}">
				<li id="${user.username}" class="user" style="width: 30em;">${user.firstName}${" "}${user.lastName} (${user.email})</li>
			</c:if>
		</c:forEach>
		<c:if test="${fn:length(actionBean.users) == 0}">
			<li class="user" style="width: 30em;"><fmt:message key="searchResult.noDataFound" /></li>
		</c:if>
		</ul>	
	</c:when>
	<c:when test="${event == 'getUserIdsByName'}">
		<ul>
		<c:forEach items="${actionBean.users}" var="user" varStatus="status">
			<li id="${user.id}" style="width: 27.5em;">${user.firstName}${" "}${user.lastName} (${user.email})</li>
		</c:forEach>
		<c:if test="${fn:length(actionBean.users) == 0}">
			<li style="width: 27.5em;"><fmt:message key="searchResult.noDataFound" /></li>
		</c:if>
		</ul>	
	</c:when>
</c:choose>
