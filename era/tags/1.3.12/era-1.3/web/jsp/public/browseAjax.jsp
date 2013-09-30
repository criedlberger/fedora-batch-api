<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>

	<%-- more browse title --%>
	<c:when test="${event == 'more'}">
		<c:if test="${fn:length(actionBean.facetFields) > 0}">
			<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
			<c:if test="${field.valueCount > 0}">
				<c:forEach items="${field.values}" var="value" end="${actionBean.moreBrowseItemCount - 1}" varStatus="sts">
					<li>
						<c:set var="valueName">"${value.name}"</c:set>
						<c:set var="fieldValue" value="${fnx:encodeUrl(valueName)}" />
						<a href="${ctx}/public/search?fq=${field.name}:${fieldValue}">${value.name}</a>
						<span>(${value.count})</span>
					</li>
				</c:forEach>
				<c:if test="${fn:length(field.values) > actionBean.moreBrowseItemCount}">
					<div id="${field.name}.${actionBean.offset}" class="more_narrow ${field.name}"><a href="#" onclick="moreBrowse(this, '${field.name}', $('${field.name}.list'), ${actionBean.offset} + ${actionBean.browseItemCount}); return false;" class="morebutton">&#43; more</a></div>
				</c:if>
			</c:if>
			</c:forEach>
		</c:if>
	</c:when>
	
	<%-- more browse initial --%>
	<c:when test="${event == 'moreInitial'}">
		<c:if test="${fn:length(actionBean.facetFields) > 0}">
			<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
			<c:if test="${field.valueCount > 0}">
				<c:forEach items="${field.values}" var="value" end="${actionBean.moreBrowseItemCount - 1}" varStatus="sts">
					<li>
						<c:set var="valueName">"${value.name}"</c:set>
						<c:set var="fieldValue" value="${fnx:encodeUrl(valueName)}" />
						<a href="${ctx}/public/search?fq=${field.name}:${fieldValue}"><b>${fn:substring(value.name, 0, 1)}</b>${fn:substring(value.name, 1, -1)}</a>
						<span>(${value.count})</span>
					</li>
				</c:forEach>
				<c:if test="${fn:length(field.values) > actionBean.moreBrowseItemCount}">
					<div id="${field.name}.${actionBean.offset}" class="more_narrow ${field.name}"><a href="#" onclick="moreInitial(this, '${field.name}', $('${field.name}.list'), '${actionBean.prefix}', ${actionBean.offset} + ${actionBean.moreBrowseItemCount}); return false;" class="morebutton">&#43; more</a></div>
				</c:if>
			</c:if>
			</c:forEach>
		</c:if>
	</c:when>

	<%-- stats --%>
	<c:when test="${event == 'stats'}">
		<fmt:message key="browse.total" /><span class="browse_count" style="font-size: 1em;">(<fmt:formatNumber pattern="###,###,###" value="${actionBean.itemCount}" />)</span>
		<%-- 
		<h3><fmt:message key="browse.download" /><span class="browse_count">(${actionBean.downloadCount})</span></h3>
		<c:forEach items="${actionBean.alphaFields}" var="field" varStatus="status">
		<c:if test="${field.valueCount > 0}">
			<h3><fmt:message key="field.${field.name}" /><span class="browse_count">(${actionBean.alphaFieldMap[field.name]})</span></h3>
		</c:if>
		</c:forEach>
		<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
		<c:if test="${field.valueCount > 0 && field.name != 'facet.author' && field.name != 'facet.subject'}">
			<h3><fmt:message key="field.${field.name}" /><span class="browse_count">(${actionBean.fieldMap[field.name]})</span></h3>
		</c:if>
		</c:forEach>
		--%>
	</c:when>
</c:choose>
