<%--
	$Id: toolbarSecurity.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Toolbar Security Taglib
	--------------
	Attributes: 
		- action 		: The action name.
		- ownerId		: The object owner ID.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag sets toolbar user roles for the object." %>

<%@ attribute name="action" type="java.lang.String" required="true" description="The action name." %>
<%@ attribute name="ownerId" type="java.lang.String" required="false" description="The object owner ID." %>

<%@ variable name-given="createRole" scope="AT_BEGIN" %>
<%@ variable name-given="updateRole" scope="AT_BEGIN" %>
<%@ variable name-given="deleteRole" scope="AT_BEGIN" %>

<c:set var="createRole" value="false" />
<c:set var="updateRole" value="false" />
<c:set var="deleteRole" value="false" />

<security:secure roles="/${action}/create">
	<c:set var="createRole" value="true" />
</security:secure>

<security:secure roles="/${action}/update">
	<c:if test="${!empty ownerId}">
	<c:choose>
	<%-- TODO: change 4 to GroupType.DEPOSITOR --%>
	<c:when test="${ownerId == context.user.username || context.user.group.id == ADMIN_GROUP || context.user.group.id == DEPOSITOR_GROUP}">
		<c:set var="updateRole" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="updateRole" value="false" />
	</c:otherwise>
	</c:choose>
	</c:if>
	<c:if test="${empty ownerId}">
		<c:set var="updateRole" value="true" />
	</c:if>
</security:secure>

<security:secure roles="/${action}/delete">
	<c:if test="${!empty ownerId}">
	<c:choose>
	<c:when test="${ownerId == context.user.username || context.user.group.id == ADMIN_GROUP}">
		<c:set var="deleteRole" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="deleteRole" value="false" />
	</c:otherwise>
	</c:choose>
	</c:if>
	<c:if test="${empty ownerId}">
		<c:set var="deleteRole" value="true" />
	</c:if>
</security:secure>
