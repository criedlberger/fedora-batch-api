<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${actionBean.subevent == 'getDownloadCountByPid'}">Download: <fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></c:when>
	<c:when test="${actionBean.subevent == 'getDownloadCountByDsId'}">Download: <fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></c:when>
	<c:when test="${actionBean.subevent == 'getDownloadCountByUserId'}">Download: <fmt:formatNumber pattern="#,###,###,###" value="${actionBean.count}" /></c:when>
</c:choose>
