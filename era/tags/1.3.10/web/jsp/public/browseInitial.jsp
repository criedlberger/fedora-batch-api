<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/public/browseInitial.jsp $
   $Id: browseInitial.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Browse - Browse Items" active="${navbarBrowse}">
	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><fmt:message key="browse.initial.header" />${' '}<fmt:message key="field.${actionBean.browseField}" /></h2>
			<div class="subheader"><fmt:message key="browse.initial.subheader" /></div>
			<stripes:errors /><stripes:messages />
			
			<div class="browse">
			
				<%-- browse by alphabet --%>
				<c:forEach items="${actionBean.alphaFields}" var="field" varStatus="status">
				<c:if test="${field.valueCount > 0 && fn:substringAfter(actionBean.browseField, '.') == fn:substringAfter(field.name, '.')}">
				<ul id="${field.name}.list" class="browse_field initial_list">
					<h3><fmt:message key="field.${field.name}" /></h3>
					<c:forEach items="${field.values}" var="value" varStatus="sts">
						<li>
							<a href="${ctx}/public/browse/initial/facet.${fn:split(field.name, '.')[1]}/${fn:toUpperCase(value.name)}">
								${fn:toLowerCase(actionBean.prefix) == value.name ? '<b>' : ''}${empty value.name ? '?' : fn:toUpperCase(value.name)}${fn:toLowerCase(actionBean.prefix) == value.name ? '</b>' : ''}
							</a>
							<span>(${value.count})</span>
						</li>
					</c:forEach>
				</ul>
				</c:if>
				</c:forEach>
			
				<%-- browse by initial --%>
				<c:forEach items="${actionBean.facetFields}" var="field" varStatus="status">
				<ul id="${field.name}.list" class="browse_field initial_title">
					<h3><fmt:message key="field.${field.name}" />: ${actionBean.prefix}</h3>
					<c:forEach items="${field.values}" var="value" end="${actionBean.browseItemCount - 1}" varStatus="sts">
					<li>
						<c:set var="fieldName" value="\"${value.name}\"" />
						<a href="${ctx}/public/search?fq=${field.name}:${fnx:encodeUrl(fieldName)}"><b>${fn:substring(value.name, 0, 1)}</b>${fn:substring(value.name, 1, -1)}</a>
						<span>(${value.count})</span>
					</li>
					</c:forEach>
					<c:if test="${fn:length(field.values) > actionBean.browseItemCount}">
						<div id="${field.name}.0" class="more_narrow ${field.name}"><a href="#" onclick="moreInitial(this, '${field.name}', $('${field.name}.list'), '${actionBean.prefix}', ${actionBean.browseItemCount}); return false;" class="morebutton">&#43; more</a></div>
					</c:if>
				</ul>
				</c:forEach>
				<div style="clear: both;" />
				<p class="bottom"><fmt:message key="browse.message" /></p>
            </div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
