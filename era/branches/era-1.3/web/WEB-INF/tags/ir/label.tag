<%--
	$Id: label.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag gets message from resorces file using label id." %>
<%@ attribute name="label" type="ca.ualberta.library.ir.model.inputform.Label" required="true" description="The input form Lebel object." %>
<%@ attribute name="var" type="java.lang.String" required="false" description="The request scope variable name to store label." %>

<c:choose>
<c:when test="${not empty label.id}">
	<fmt:message var="labelValue" key="${label.id}"  />
</c:when>
<c:otherwise>
	<c:set var="labelValue" value="${label.value}" />
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${not empty var}">
	<% request.setAttribute(var, jspContext.getAttribute("labelValue")); %>
</c:when>
<c:otherwise>
	${labelValue}
</c:otherwise>
</c:choose>