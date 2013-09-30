<%--
	$Id: pages.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Pages Taglib
	--------------
	Attributes: 
		- numFound		: The number of search results.
		- start			: The number of start row for first bookmark.
		- rows			: The number of rows per page.
		- path			: The action path: ${ctx}/action/{$action}/{$event}. 
						  Ex: /ir/action/myaccount/items
		- sortBy		: The sort field.
		- pagesClass	: The CSS class name for page bookmarks.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates a page bookmark navigator for search results." %>

<%@ attribute name="numFound" type="java.lang.Long" required="true" description="The number of search results." %>
<%@ attribute name="start" type="java.lang.Integer" required="true" description="The number of start row for first bookmark." %>
<%@ attribute name="rows" type="java.lang.Integer" required="true" description="The number of rows per page." %>
<%@ attribute name="numPages" type="java.lang.Integer" required="true" description="The number of pages to display." %>
<%@ attribute name="path" type="java.lang.String" required="true" description="The action path: ${ctx}/action/{$action}/{$event} Ex: /ir/action/myaccount/items" %>
<%@ attribute name="sortBy" type="java.lang.String" description="The sort field." %>
<%@ attribute name="pagesClass" type="java.lang.String" description="The CSS class name for page bookmarks." %>

<%
	// get tag attributes
	long numFound = ((Long)jspContext.getAttribute("numFound")).longValue();
	int start = ((Integer)jspContext.getAttribute("start")).intValue();
	int rows = ((Integer)jspContext.getAttribute("rows")).intValue();
	int numPages = ((Integer)jspContext.getAttribute("numPages")).intValue();
	
	// calculate pages variables
	int activePos = numPages / 2;
	int currentPage = start / rows + 1;
	int pages = (int)(numFound / rows == 0 ? 1 : numFound % rows == 0 ? numFound / rows : numFound / rows + 1);
	int startPage = 0;
	int endPage = currentPage + (numPages - activePos - 1);
	if (endPage > pages) {
		endPage = pages;
		startPage = endPage - numPages + 1;
	} else {
		endPage = currentPage + (numPages - activePos) > pages ? pages : currentPage + (numPages - activePos);	
		startPage = endPage - numPages + 1;
	}
	if (startPage < 2) {
		endPage = endPage - startPage + 1;
		endPage = endPage > pages ? pages : endPage;
		startPage = 1;
	}
	
	// set tag attributes
	jspContext.setAttribute("currentPage", currentPage); 
 	jspContext.setAttribute("pages", pages); 
 	jspContext.setAttribute("startPage", startPage); 
 	jspContext.setAttribute("endPage", endPage); 
%>
<c:if test="${pages > 1}">
<%-- DEBUG: numFound: ${numFound}/start: ${start}/rows: ${rows}/currentPage: ${currentPage}/pages: ${pages}/startPage: ${startPage}/endPage: ${endPage} --%>
<div class="${pagesClass}">
	<fmt:message key="pages.label" />:
	<c:if test="${currentPage > 1}">
		<span style="padding-left: 10px; font-size: 11px;"><a href="${path}/${start - rows}/${sortBy}">&laquo; <fmt:message key="pages.previous" /></a>
		<c:if test="${startPage > 1}">...</c:if></span>
	</c:if> 
	<c:forEach begin="${startPage}" end="${endPage}" var="page">
		<c:if test="${page == currentPage}">
			<span class="active">${page}</span>
		</c:if>
		<c:if test="${page != currentPage}">
			<span style="padding: 2px;"><a href="${path}/${page * rows - rows}/${sortBy}">${page}</a></span>
		</c:if>
	</c:forEach>
	<c:if test="${endPage < pages}"><span style="padding-right: 2px; font-size: 11px;">...</span><a href="${path}/${pages * rows - rows}/${sortBy}">${pages}</a></c:if>
	<c:if test="${currentPage < pages}">
		<span style="padding: 2px; font-size: 11px;"><a href="${path}/${start + rows}/${sortBy}"><fmt:message key="pages.next" /> &raquo;</a></span>
	</c:if> 
</div>
</c:if>
