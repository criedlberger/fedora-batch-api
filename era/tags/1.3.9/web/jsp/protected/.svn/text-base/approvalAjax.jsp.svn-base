<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<c:choose>
	<c:when test="${event == 'getExplanation'}">
		<textarea name="comments" class="comments"><fmt:message key="reject.${actionBean.rejectId}.message" /></textarea>
	</c:when>
	<c:when test="${event == 'getItemsByTitleAuthor' }">
		<ul>
		<c:forEach items="${actionBean.items}" var="item" varStatus="status">
			<li id="${item.pid}" class="item" style="height: 3.5em;">
				<div style="width: 40em; height: 1.2em; overflow: hidden; font-weight: bold;">${item.titles[0]}</div>
				<fmt:message key="by" />:${' '} 
				<c:forEach items="${item.creators}" var="creator" varStatus="sts">
					${sts.index > 0 ? ', ' : ''}${creator}
				</c:forEach>
				<br /><fmt:message key="to" />:${' '} 
				<c:forEach items="${item.thesisDegreeDisciplines}" var="dept" varStatus="sts">
					${sts.index > 0 ? ', ' : ''}${dept}
				</c:forEach>
			</li>
		</c:forEach>
		<c:if test="${fn:length(actionBean.items) == 0}">
			<li><fmt:message key="searchResult.noDataFound" /></li>
		</c:if>
		</ul>	
	</c:when>
</c:choose>
