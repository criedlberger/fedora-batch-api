<%--
	$Id: community.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag gets community name from service facade using community PID." %>
<%@ tag import="ca.ualberta.library.ir.service.ServiceFacade" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>
<%@ tag import="ca.ualberta.library.ir.model.solr.Community" %>

<%@ attribute name="pid" type="java.lang.String" required="true" description="The community PID." %>
<%@ attribute name="var" type="java.lang.String" required="false" description="The request scope variable name to store collection object." %>
<%@ variable name-given="community" variable-class="ca.ualberta.library.ir.domain.Community" scope="NESTED" description="The tag nested scope variable to store community object." %>
<% 
	String pid = (String) jspContext.getAttribute("pid");
	String var = (String) jspContext.getAttribute("var");
	Community community = new Community();
	if (StringUtils.trimToNull(pid) != null) {
		try {
			ServiceFacade services = (ServiceFacade) session.getAttribute("services");
			community = services.getCommunity(pid);
			jspContext.setAttribute("community", community);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<jsp:doBody var="body" />
<c:choose>
<c:when test="${!empty var}">
	<% request.setAttribute(var, community); %>
</c:when>
<c:when test="${empty body}">
	${community.title}
</c:when>
<c:otherwise>
	${body}
</c:otherwise>
</c:choose>
