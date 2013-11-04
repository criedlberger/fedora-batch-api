<%--
	$Id: hint.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag create input form hint HTML fragment." %>
<%@ attribute name="label" type="ca.ualberta.library.ir.model.inputform.Label" required="true" description="The input form Lebel object." %>
<c:choose>
<c:when test="${not empty label.id}">
	<fmt:message var="message" key="${label.id}"  />
</c:when>
<c:otherwise>
	<c:set var="message" value="${label.value}" />
</c:otherwise>
</c:choose>
<c:if test="${not empty message}">
	<div class="hint"><p>${message}</p></div>
</c:if>