<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${event == 'add'}">
		<img src="${ctx}/images/favorite_on.gif" style="vertical-align: text-bottom;" onclick="toggleFavorite(this.parentNode, '${actionBean.pid}', 'remove');"> 
		<a href="#" onclick="addFavorite(this.parentNode, '${actionBean.pid}', 'remove');"><fmt:message key="toolbar.favorite.label" /></a>
	</c:when>
	<c:when test="${event == 'remove'}">
		<img src="${ctx}/images/favorite_off.gif" style="vertical-align: text-bottom;" onclick="toggleFavorite(this.parentNode, '${actionBean.pid}', 'add');"> 
		<a href="#" onclick="addFavorite(this.parentNode, '${actionBean.pid}', 'add');"><fmt:message key="toolbar.favorite.label" /></a>
	</c:when>
	<c:when test="${event == 'getFavoriteStatus'}">
		<img src="${ctx}/images/${actionBean.favorite ? 'favorite_on.gif' : 'favorite_off.gif'}" style="vertical-align: text-bottom;" onclick="addFavorite(this.parentNode, '${actionBean.pid}', '${actionBean.favorite ? "remove" : "add"}'); return false;" /> 
		<a href="#" onclick="addFavorite(this.parentNode, '${actionBean.pid}', '${actionBean.favorite ? "remove" : "add"}'); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="toolbar.favorite.label" /></a>
	</c:when>
</c:choose>
