<%--
	$Id: handle.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Handle Taglib
	--------------
	Attributes: 
		- pid			: The object PID.
		- type			: The object type: community, collection, item and author.
		- var			: The request scope variable for the Handle URL.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates an object handle URL from object PID and type." %>
<%@ tag import="ca.ualberta.library.ir.service.ServiceFacade" %>
<%@ tag import="ca.ualberta.library.ir.domain.Handle" %>
<%@ tag import="ca.ualberta.library.ir.action.BaseActionBean" %>

<%@ attribute name="pid" type="java.lang.String" required="true" description="The object PID." %>
<%@ attribute name="type" type="java.lang.String" required="true" description="The object type: community, collection, item and author." %>
<%@ attribute name="var" type="java.lang.String" description="The request scope variable for the Handle URL." %>

<c:set var="handleEnabled" value="${properties['handle.enabled']}" />
<c:set var="handleServer" value="${properties['handle.server']}" />
<c:set var="handlePrefix" value="${properties['handle.prefix']}" />
<c:set var="irPrefix" value="${properties['handle.ir.prefix']}" />
<c:set var="serverUrl" value="${properties['http.server.url']}${ctx}" />
<c:if test="${handleEnabled}">
	<% 
		String pid = (String) jspContext.getAttribute("pid");
		ServiceFacade services = (ServiceFacade) session.getAttribute("services");
		Handle handle = services.getHandleByPid(pid);
		jspContext.setAttribute("handle", handle);
		
		BaseActionBean actionBean = (BaseActionBean) request.getAttribute("actionBean");
		jspContext.setAttribute("handleUrl", actionBean.getHandleURL(pid));
	%>
	<c:choose>
	<c:when test="${empty handle}">
		<c:choose>
		<c:when test="${type == 'author'}">
			<c:set var="url" value="${serverUrl}/public/author/${pid}" />
		</c:when>
		<c:when test="${type == 'item'}">
			<c:set var="url" value="${handleUrl}" />
		</c:when>
		<c:otherwise>
			<c:set var="url" value="${serverUrl}/public/view/${type}/${pid}" />
		</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="url" value="${handleServer}/${handlePrefix}/${irPrefix}${empty irPrefix ? '' : '.'}${handle.id}" />
	</c:otherwise>
	</c:choose>
	<c:if test="${empty var}">${url}</c:if>
	<c:if test="${not empty var}">
		<%
			String var = (String) jspContext.getAttribute("var");
			String url = (String) jspContext.getAttribute("url");
			request.setAttribute(var, url);
		%>
	</c:if>
</c:if>
<c:if test="${not handleEnabled}">
	<c:choose>
	<c:when test="${type == 'author'}">
		<c:set var="url" value="${serverUrl}/public/view/author/${pid}" />
	</c:when>
	<c:otherwise>
		<c:set var="url" value="${serverUrl}/public/view/${type}/${pid}" />
	</c:otherwise>
	</c:choose>
	<c:if test="${empty var}">${url}</c:if>
	<c:if test="${not empty var}">
		<%
			String var = (String) jspContext.getAttribute("var");
			String url = (String) jspContext.getAttribute("url");
			request.setAttribute(var, url);
		%>
	</c:if>
</c:if>
