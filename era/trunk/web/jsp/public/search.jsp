<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/public/search.jsp $
   $Id: search.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ page import="java.net.URLEncoder" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Search Results for &quot;${actionBean.q}&quot;">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<c:set var="q" value="${fnx:encodeUrl(actionBean.q)}" />
		<c:set var="fq" value="${fnx:encodeUrl(actionBean.fq)}" />
			
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: 
				<strong>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</strong> - <strong>${actionBean.start + actionBean.resultRows}</strong> 
				${' '}<fmt:message key="of" />${' '}<strong>${actionBean.numFound}</strong>
			</c:if>
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			${' '}<ir:formatQuery q="${actionBean.q}" fq="${actionBean.fq}" />
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:form id="sortForm" action="/public/search" style="display: inline;">
					<span class="sorter"><stripes:label for="sort.label" />:
					<stripes:select id="sort" name="sort" onchange="location.href = '${ctx}/public/search?q=${q}&fq=${fq}&sort=' + $('sort').value;">
						<%@ include file="/jsp/public/searchSortBy.jspf" %>
					</stripes:select>
					</span>
				</stripes:form>
			</c:if>
		  	<c:if test="${actionBean.numFound > 0}">
		  		<div class="pages">
					<ir:searchPages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sort}" rows="${actionBean.rows}"	
						numPages="${actionBean.numPages}" path="${ctx}/public/search?q=${q}&fq=${fq}" pagesClass="pagesbot" />
				</div>
			</c:if>
        </div>
		<c:if test="${event == 'search'}">
			<div><stripes:messages /><stripes:errors /></div>
		</c:if>
		<%@ include file="/jsp/public/searchResults.jspf" %>
	</stripes:layout-component>
</stripes:layout-render>
