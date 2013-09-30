<%--
	$Id: menubar.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Menubar Taglib
	--------------
	Attributes: 
		- name			: The menubar name in properties file. Ex: deposit.menubar.
		- menubarClass	: The CSS class name for menubar.
		- active		: The number of active menu.
		- itemCount		: @deprecated: The number of menu items.
		- showTitle		: The boolean value (true/false) for showing menu title.
		- titleClass	: The menu title CSS class name.
	Properties:
		# Deposit Menubar
		deposit.menubar.count	= 2
		deposit.menubar.1.name 	= Submit Item
		deposit.menubar.1.title	= Upload new item to repository
		deposit.menubar.1.url 	= /jsp/protected/deposit.jsp
		deposit.menubar.2.name 	= Create Community
		deposit.menubar.2.title = Create new community
		deposit.menubar.2.url 	= /jsp/protected/editCommunity.jsp
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates a menubar depending on user roles." %>

<%@ attribute name="name" type="java.lang.String" required="true" description="The menubar name in properties file. Ex: deposit.menubar." %>
<%@ attribute name="menubarClass" type="java.lang.String" description="The CSS class name for menubar." %>
<%@ attribute name="active" type="java.lang.Integer" description="The number of active menu." %>

<%-- @deprecated: use ${name}.count property instead --%> 
<%@ attribute name="itemCount" type="java.lang.String" required="false" description="The number of menu items." %>

<%@ attribute name="showTitle" type="java.lang.String" description="The boolean value (true/false) for showing menu title." %>
<%@ attribute name="titleClass" type="java.lang.String" description="The menu title CSS class name." %>
<%@ attribute name="firstItem" fragment="true" description="First list item element to center menubar." %>

<div class="${menubarClass}">
<ul>
<c:if test="${!empty firstItem}"><jsp:invoke fragment="firstItem"/></c:if>
<fmt:message key="${name}${'.'}${active}${'.title'}" var="activeTitle"/>
<fmt:message key="${name}${'.count'}" var="itemCount"/>
<c:forEach var="i" begin="1" end="${itemCount}">
	<fmt:message var="menuName" key="${name}${'.'}${i}${'.name'}" />
	<c:if test="${!empty menuName}">
		<fmt:message key="${name}${'.'}${i}${'.title'}" var="title"/>
		<fmt:message key="${name}${'.'}${i}${'.url'}" var="url"/>
		<fmt:message key="${name}${'.'}${i}${'.roles'}" var="roles"/>

		<%-- check user roles to enable/disable menu item --%>
		<c:set var="enabled" value="false" />
		<security:secure roles="${roles}">
			<c:set var="enabled" value="true" />
		</security:secure>
		
		<c:choose>
		<c:when test="${empty roles || enabled}">
			<c:choose>
			<c:when test="${active == i}">
				<li class="active"><a href="${httpServerUrl}${ctx}${url}">${menuName}</a></li>
			</c:when>
			<c:otherwise>
				<li>
				<c:choose>
				<c:when test="${showTitle}">
					<a href="${httpServerUrl}${ctx}${url}" onmouseover="showMenuTitle('${title}');" onmouseout="hideMenuTitle('${activeTitle}');">${menuName}</a>
				</c:when>
				<c:otherwise>
					<a href="${httpServerUrl}${ctx}${url}">${menuName}</a>
				</c:otherwise>
				</c:choose>
				</li>
			</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<%-- <li class="disabled">${menuName}</li> --%>
		</c:otherwise>
		</c:choose>
	</c:if>
</c:forEach>
</ul>
</div>
<c:if test="${showTitle}">
<div id="menuTitle" class="${titleClass}">${activeTitle}</div><div style="height: 14px;"></div>
<script type="text/javascript">
	function showMenuTitle(title) {
		document.getElementById('menuTitle').innerHTML = title;
	}
	function hideMenuTitle(title) {
		document.getElementById('menuTitle').innerHTML = title;
	}
</script>
</c:if>
