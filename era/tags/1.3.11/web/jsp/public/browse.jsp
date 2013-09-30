<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/public/browse.jsp $
   $Id: browse.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Browse - Browse Items" active="${navbarBrowse}">
	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><fmt:message key="browse.header" /></h2>
			<div class="subheader"><fmt:message key="browse.subheader" /></div>
			<stripes:errors /><stripes:messages />
			
			<div class="browse">
				<%-- total and download --%>
				<ul class="browse_field" style="min-height: 3em; width: 64em;">
					<h3><fmt:message key="browse.total" /><span class="browse_count">(${actionBean.itemCount})</span></h3>
				</ul>
				
				<%-- browse by alphabet --%>
				<c:forEach items="${actionBean.alphaFields}" var="field" varStatus="status">
				<c:if test="${field.valueCount > 0}">
				<ul id="${field.name}.list" class="browse_field ${field.name}">
					<h3><fmt:message key="field.${field.name}" /><span class="browse_count">(${actionBean.alphaFieldMap[field.name]})</span></h3>
					<li>
					<c:forEach items="${field.values}" var="value" varStatus="sts">
						<p>
							<a href="${ctx}/public/browse/initial/facet.${fn:split(field.name, '.')[1]}/${fn:toUpperCase(value.name)}">${fn:toUpperCase(value.name)}</a>
							<span>(${value.count})</span>
						</p>
					</c:forEach>
					</li>
				</ul>
				</c:if>
				</c:forEach>

				<%-- browse by field --%>
				<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
				<c:if test="${field.valueCount > 0 && field.name != 'facet.author' && field.name != 'facet.subject'}">
				<ul id="${field.name}.list" class="browse_field ${field.name}">
					<h3><fmt:message key="field.${field.name}" /><span class="browse_count">(${actionBean.fieldMap[field.name]})</span></h3>
					<c:forEach items="${field.values}" var="value" end="${actionBean.browseItemCount - 1}" varStatus="sts">
					<li>
						<c:set var="fieldName" value="\"${value.name}\"" />
						<a href="${ctx}/public/search?fq=${field.name}:${fnx:encodeUrl(fieldName)}">${empty value.name ? '???' : fnx:trim(value.name, 40)}</a>
						<span>(${value.count})</span>
					</li>
					</c:forEach>
					<c:if test="${fn:length(field.values) > actionBean.browseItemCount}">
						<div id="${field.name}.0" class="more_narrow ${field.name}"><a href="#" onclick="moreBrowse(this, '${field.name}', $('${field.name}.list'), ${actionBean.browseItemCount}); return false;" class="morebutton">&#43; more</a></div>
					</c:if>
				</ul>
				</c:if>
				</c:forEach>
				<p class="bottom"><fmt:message key="browse.message" /></p>
            </div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
