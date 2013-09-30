<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${event == 'getTagCountByPid'}">
		<c:if test="${actionBean.tagCount > 0}">
			<fmt:message key="bookmark.tagged.by" />: ${actionBean.tagCount} <fmt:message key="users" /><br />
		</c:if>
	</c:when>
	<c:when test="${event == 'getTagsByPid'}">
		<c:forEach items="${actionBean.tags.values}" var="tag" varStatus="status">${status.index > 0 ? ', ' : ''}<a href="${ctx}/public/tag/get/${tag.name}" style="text-decoration: underline;">${tag.name}</a></c:forEach>
	</c:when>
	<c:when test="${event == 'getRelatedTags'}">
		<div class="right_narrow">
			<div class="narrow_search"><fmt:message key="relatedTags.header" /></div>
			<ul class="narrow_field">
			<c:forEach items="${actionBean.tags.values}" var="tag" varStatus="status">
				<c:if test="${tag.name != actionBean.tag}">
				<li><span style="color: #808080;">${tag.count}</span> <a href="${ctx}/public/tag/get/${tag.name}">${tag.name}</a></li>
				</c:if>
			</c:forEach>
			</ul>
		</div>
	</c:when>
</c:choose>
