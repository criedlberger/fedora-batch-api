<%--
	$Id: collection.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag gets collection name from service facade using collection PID." %>
<%@ tag import="ca.ualberta.library.ir.service.ServiceFacade" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>
<%@ tag import="ca.ualberta.library.ir.model.solr.Collection" %>

<%@ attribute name="pid" type="java.lang.String" required="true" description="The collection PID." %>
<%@ attribute name="var" type="java.lang.String" required="false" description="The request scope variable name to store collection object." %>
<%@ variable name-given="collection" variable-class="ca.ualberta.library.ir.domain.Collection" scope="NESTED" description="The tag nested scope variable to store collection object." %>
<% 
	String pid = (String) jspContext.getAttribute("pid");
	String var = (String) jspContext.getAttribute("var");
	Collection collection = new Collection();
	if (StringUtils.trimToNull(pid) != null) {
		try {
			ServiceFacade services = (ServiceFacade) session.getAttribute("services");
			collection = services.getCollection(pid);
			jspContext.setAttribute("collection", collection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<jsp:doBody var="body" />
<c:choose>
<c:when test="${!empty var}">
	<% request.setAttribute(var, collection); %>
</c:when>
<c:when test="${empty body}">
	${collection.title}
</c:when>
<c:otherwise>
	${body}
</c:otherwise>
</c:choose>
