<%@ include file="/jsp/layout/taglibs.jspf" %>
<fmt:message key="favorite.add.title" var="add" />
<fmt:message key="favorite.remove.title" var="remove" />
<c:choose>
	<c:when test="${event == 'getBookmarkStatus'}">
		<a href="#" onclick="addBookmark(this.parentNode, ${!empty actionBean.bookmark ? actionBean.bookmark.id : 0}, '${actionBean.pid}', '${empty actionBean.bookmark ? "add" : "remove"}'); return false;" class="${empty context.user ? 'disabled_link' : ''}"
			title="${empty actionBean.bookmark ? add : remove}">
		<c:choose>
		<c:when test="${empty actionBean.bookmark}">
			<fmt:message key="toolbar.favorite.label" />
		</c:when>
		<c:otherwise>
			<fmt:message key="toolbar.favorite.on.label" />
		</c:otherwise>
		</c:choose>
		</a>
	</c:when>
	<c:when test="${event == 'add'}">
		<a href="#" title="${remove}" onclick="addBookmark(this.parentNode, ${!empty actionBean.bookmark ? actionBean.bookmark.id : 0}, '${actionBean.pid}', 'remove'); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="toolbar.favorite.on.label" /></a>
	</c:when>
	<c:when test="${event == 'remove'}">
		<a href="#" title="${add}" onclick="addBookmark(this.parentNode, ${!empty actionBean.bookmark ? actionBean.bookmark.id : 0}, '${actionBean.pid}', 'add'); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="toolbar.favorite.label" /></a>
	</c:when>
	<c:when test="${event == 'getBookmarkToolbar'}">
		<fmt:message key="confirm.bookmark" var="type" />
		<ir:toolbar confirmDelete="confirmDelete('${actionBean.bookmark.title}', '${type}')" pid="${actionBean.bookmark.id}/${actionBean.pid}" title="${actionBean.bookmark.title}" action="bookmark" toolbarClass="toolbar" style="font-weight: normal;"
			add="false" deleteRole="false" updateRole="true" urlSuffix="?next=${actionBean.next}&mode=myaccount" />
	</c:when>
</c:choose>

