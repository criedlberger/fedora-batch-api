<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${actionBean.count > 0 }">
	<c:choose>
		<c:when test="${event == 'getDownloadCountByPid'}">
			<fmt:message key="item.downloaded" /><em style="margin: 0 0.5em 1em 0.5em;"><fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></em><em><fmt:message key="download.unit" />.</em>
		</c:when>
		<c:when test="${event == 'getDownloadCountByCommunity'}">
			<label><fmt:message key="community.downloaded" /></label><em><span style="margin: 0 0.5em 1em 0.5em;"><fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></span></em>
		</c:when>
		<c:when test="${event == 'getDownloadCountByCollection'}">
			<label><fmt:message key="collection.downloaded" /></label><em><span style="margin: 0 0.5em 1em 0.5em;"><fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></span></em>
		</c:when>
	</c:choose>
</c:if>
