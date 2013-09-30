<%@ include file="/jsp/layout/taglibs.jspf" %>

<%-- SearchActionBean Ajax Response --%>
<c:choose>

	<%-- get all communities --%>
	<c:when test="${event == 'getAllCommunities'}">
		<select id="communityFilter" name="communityFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="multiple">
		<c:forEach items="${actionBean.communities}" var="community" varStatus="status">
			<option value="${community.id}">${community.title}</option>
		</c:forEach>
		</select>
	</c:when>

	<%-- get all colletions --%>
	<c:when test="${event == 'getAllCollections'}">
		<select id="collectionFilter" name="collectionFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="multiple">
		<c:forEach items="${actionBean.collections}" var="collection" varStatus="status">
			<option value="${collection.id}">${collection.title}</option>
		</c:forEach>
		</select>
	</c:when>

	<%-- get all contentModels --%>
	<c:when test="${event == 'getAllContentModels'}">
		<select id="contentModelFilter" name="contentModelFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="multiple">
		<c:forEach items="${context.allContentModels}" var="contentModel" varStatus="status">
			<c:if test="${contentModel.id > 0}">
			<option value="${contentModel.contentModel}">${contentModel.title}</option>
			</c:if>
		</c:forEach>
		</select>
	</c:when>

	<%-- get member of collections --%>
	<c:when test="${event == 'getMemberOfCollections'}">
	<c:if test="${fn:length(actionBean.communities) > 0}">
		<div>
			<stripes:label for="search.community" />: 
			<c:forEach items="${actionBean.communities}" var="community" varStatus="status">${status.index > 0 ? ", " : ""}<a href="${ctx}/public/view/community/${community.id}">${community.title}</a></c:forEach>
		</div>
	</c:if>
	<c:if test="${fn:length(actionBean.collections) > 0}">
		<div>
			<stripes:label for="search.collection" />: 
			<c:forEach items="${actionBean.collections}" var="collection" varStatus="status">${status.index > 0 ? ", " : ""}<a href="${ctx}/public/view/collection/${collection.id}">${collection.title}</a></c:forEach>
		</div>
	</c:if>
	</c:when>

	<%-- get member of communities --%>
	<c:when test="${event == 'getMemberOfCommunities'}">
		<c:forEach items="${actionBean.communities}" var="community" varStatus="status">${status.index > 0 ? ", " : ""}<a href="${ctx}/public/view/community/${community.id}">${community.title}</a></c:forEach>
	</c:when>

	<%-- @deplicated: use static url instead --%>
	<c:when test="${event == 'getImageUrl'}">
		${actionBean.imgUrl}
	</c:when>

	<%-- @deplicated: use favoriteAjax.jsp instead --%>
	<c:when test="${event == 'getFavoriteStatus'}">
		<img src="${ctx}/images/${actionBean.favorite ? 'favorite_on.gif' : 'favorite_off.gif'}" style="vertical-align: text-bottom;" onclick="toggleFavorite('${actionBean.pid}', '${actionBean.favorite ? "remove" : "add"}', ${actionBean.index}); return false;" /> 
		<a href="#" onclick="toggleFavorite('${actionBean.pid}', '${actionBean.favorite ? "remove" : "add"}', ${actionBean.index}); return false;"><fmt:message key="toolbar.favorite.label" /></a>
	</c:when>

	<%-- @deplicated: use bookmarkAjax.jsp instead --%>
	<c:when test="${event == 'getBookmarkStatus'}">
		<img src="${ctx}/images/${!empty actionBean.bookmark ? 'bookmark.gif' : 'bookmark_grey.gif'}" style="vertical-align: text-bottom;" onclick="location.href = '${ctx}/action/bookmark/edit/${!empty actionBean.bookmark ? actionBean.bookmark.id : 0}/${actionBean.pid}';"> 
		<a href="${ctx}/action/bookmark/edit/${!empty actionBean.bookmark ? actionBean.bookmark.id : 0}/${actionBean.pid}"><fmt:message key="toolbar.bookmark.label" /></a>
	</c:when>

	<%-- narrow search --%>
	<c:when test="${event == 'getNarrowSearch'}">
		<c:if test="${fn:length(actionBean.facetFields) > 0}">
		<c:set var="q" value="${fnx:encodeUrl(actionBean.q)}" />
		<c:set var="fq" value="${fnx:encodeUrl(actionBean.fq)}" />
		<div class="right_narrow">
			<div class="narrow_search"><h1><fmt:message key="search.narrow.header" /></h1></div>
			<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
			<c:if test="${field.valueCount > 0}">
			<ul id="${field.name}.list" class="${field.name}">
				<h2><fmt:message key="field.${field.name}" /></h2>
				<c:forEach items="${field.values}" var="value" end="${actionBean.narrowItemCount - 1}" varStatus="sts">
					<li>
						<c:set var="valueName">"${value.name}"</c:set>
						<c:set var="fieldValue" value="${fnx:encodeUrl(valueName)}" />
						<a href="${ctx}/public/search/narrow?q=${q}&fq=${fq}&sort=${actionBean.sort}&narrowField=${field.name}:${fieldValue}">${value.name}</a>
						<span>(${value.count})</span>
					</li>
				</c:forEach>
				<c:if test="${fn:length(field.values) > actionBean.narrowItemCount}">
					<div id="${field.name}.0" class="more_narrow ${field.name}"><a href="#" onclick="moreNarrowSearch(this, '${field.name}', $('${field.name}.list'), ${actionBean.narrowItemCount}); return false;" class="morebutton">&#43; more</a></div>
				</c:if>
			</ul>
			</c:if>
			</c:forEach>
			<p><fmt:message key="search.narrow.footer" /></p>
		</div>
		</c:if>
	</c:when>

	<%-- more narrow search --%>
	<c:when test="${event == 'moreNarrowSearch'}">
		<c:if test="${fn:length(actionBean.facetFields) > 0}">
		<c:set var="q" value="${fnx:encodeUrl(actionBean.q)}" />
		<c:set var="fq" value="${fnx:encodeUrl(actionBean.fq)}" />
			<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
			<c:if test="${field.valueCount > 0}">
				<c:forEach items="${field.values}" var="value" end="${actionBean.moreNarrowItemCount - 1}" varStatus="sts">
				<li class="more_${fn:substringAfter(field.name, "facet.")}_${actionBean.offset}">
					<c:set var="valueName">"${value.name}"</c:set>
					<c:set var="fieldValue" value="${fnx:encodeUrl(valueName)}" />
					<a href="${ctx}/public/search/narrow?q=${q}&fq=${fq}&sort=${actionBean.sort}&narrowField=${field.name}:${fieldValue}">${value.name}</a>
					<span>(${value.count})</span>
				</li>
				</c:forEach>
				<c:if test="${fn:length(field.values) > actionBean.narrowItemCount}">
					<div id="${field.name}.${actionBean.offset}" class="more_narrow ${field.name}"><a href="#" onclick="moreNarrowSearch(this, '${field.name}', $('${field.name}.list'), ${actionBean.offset} + ${actionBean.moreNarrowItemCount}); return false;" class="morebutton">&#43; more</a></div>
				</c:if>
			</c:if>
			</c:forEach>
		</c:if>
	</c:when>

	<%-- more like this --%>
	<c:when test="${event == 'getMoreLikeThis'}">
		<div class="itemlist morelikethis">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<div class="itemlist_info" style="width: 39em;">
                 		<h2><a href="${ctx}/public/view/item/${fld['PID']}" class="result_title">${fld['dc.title']}</a></h2>	
						<c:if test="${not empty fld['dc.creator']}"><p class="result_author"><strong><fmt:message key="by" />${' '} </strong> ${fld['dc.creator']}</p></c:if>    
                   		<c:if test="${not empty fld['dc.description']}"><p class="result_others">${fnx:trim(fld['dc.description'], 120)}</p></c:if>
                   		<c:if test="${not empty fld['era.comments']}"><p class="result_others"><stripes:label for="admin.review.comments" />: ${fnx:trim(fld['era.comments'], 100)}</p></c:if>
					</div>
					<div class="itemlist_collection">
						<stripes:label for="item.submittedTo" /><br />
						<stripes:label for="community.label" />:
						<c:forEach items="${flds['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
						<br /><stripes:label for="collection.label" />: 
						<c:forEach items="${flds['rel.isMemberOfCollection']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/collection/${pid}"><ir:collection pid="${pid}" /></a></c:forEach>
					</div>
				</li>
			</c:forEach>
		</ol>
		</div>
		<br style="clear: both;" />
	</c:when>

</c:choose>
