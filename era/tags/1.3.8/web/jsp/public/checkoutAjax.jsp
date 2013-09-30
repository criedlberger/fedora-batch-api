<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${event == 'process'}">${actionBean.filename}</c:when>
</c:choose>
