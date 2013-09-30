<%--
	$Id: formatQuery.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Format Query Taglib
	-------------------
	Descripton:
		This tag is for formating query string and replace field names with descriptions.
	Attributes: 
		- f		: The query string to format.
		- fq	: The filter query to format.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag import="java.util.ArrayList"%>

<%@ tag description="This tag is for formating query string and replace field names with descriptions." %>
<%@ attribute name="q" type="java.lang.String" description="The query string to format." %>
<%@ attribute name="fq" type="java.lang.String" description="The filter query string to format." %>

<c:if test="${not empty q}">
	<fmt:message key="search.for" /> 
	<c:choose>
	<c:when test="${fn:contains(q, 'dc.')}">
		${' '}${fn:replace(q, 'dc.', '')}
	</c:when>
	<c:otherwise>
		${' '}${q}
	</c:otherwise>
	</c:choose>
</c:if>
<c:if test="${not empty fq}">
	${' '}<fmt:message key="field.facet.filterBy" />${' '}${fn:replace(fq, 'facet.', '')}
</c:if>
