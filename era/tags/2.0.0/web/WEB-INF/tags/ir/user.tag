<%--
	$Id: user.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag gets user full name from service facade using username." %>
<%@ tag import="ca.ualberta.library.ir.service.ServiceFacade" %>
<%@ tag import="ca.ualberta.library.ir.domain.User" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>

<%@ attribute name="username" type="java.lang.String" required="true" description="The username for getting full user name." %>
<%@ attribute name="var" type="java.lang.String" required="false" description="The request scope variable name to store user object." %>
<% 
	String username = (String) jspContext.getAttribute("username");
	String var = (String) jspContext.getAttribute("var");
	User user = new User();
	if (StringUtils.trimToNull(username) != null) {
		try {
			ServiceFacade services = (ServiceFacade) session.getAttribute("services");
			user = services.getUser(username);
			jspContext.setAttribute("user", user);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<c:choose>
<c:when test="${!empty var}">
	<% request.setAttribute(var, user); %>
	<jsp:doBody />
</c:when>
<c:otherwise>
	${user.firstName}${' '}${user.lastName}
</c:otherwise>
</c:choose>
