<%--
	$Id: sidebar.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Sidebar Taglib
	--------------
	Attributes: 
		- name			: The sidebar name in properties file. Ex: admin.sidebar
		- active		: The number of active menu.
	Properties:
		# Admin Sidebar
		admin.sidebar.count		= 2
		admin.sidebar.name		= Administrator
		admin.sidebar.title		= Administrator Sidebar
		admin.sidebar.url		= /action/admin
		admin.sidebar.1.name 	= Dark Repository
		admin.sidebar.1.title	= Manage Dark Repository Items
		admin.sidebar.1.url 	= /action/admin/dark/items
		admin.sidebar.1.image	= /images/dark_repository.gif
		admin.sidebar.1.roles	= /admin/dark
		admin.sidebar.2.name 	= Deposit Approval
		admin.sidebar.2.title 	= Approve Deposit Items
		admin.sidebar.2.url 	= /action/admin/approval/list
		admin.sidebar.2.image	= /images/item_approval.gif
		admin.sidebar.2.roles	= /admin/approve
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates a sidebar for My Account and Administrator menu depending on user roles." %>

<%@ attribute name="name" type="java.lang.String" required="true" description="The sidebar name in properties file. Ex: admin.sidebar" %>
<%@ attribute name="active" type="java.lang.Integer" description="The number of active menu." %>

<div id="${name}" style="margin: 10px 0 10px 0;">
	<fmt:message key="${name}.url" var="url" />
	<fmt:message key="${name}.count" var="count" />
	<fmt:message key="${name}.title" var="title" />
	<h2><a href="${ctx}${url}" title="${title}"><fmt:message key="${name}.name" /></a></h2>
	<table style="width: 100%; margin: 0 0 10px 0;" align="center">
		<c:forEach begin="1" end="${count}" var="i" varStatus="status">
		<fmt:message key="${name}.${i}.title" var="title" />
		<fmt:message key="${name}.${i}.url" var="url" />
		<fmt:message key="${name}.${i}.image" var="image" />
		<fmt:message key="${name}.${i}.roles" var="roles" />
		<c:set var="enabled" value="false" />
		<security:secure roles="${roles}">
			<c:set var="enabled" value="true" />
		</security:secure>
		<c:if test="${empty roles || enabled}">
		<tr>
			<td style="border-bottom: 1px dotted green;">
				<span style="float: left;">
					<img src="${ctx}${image}" style="vertical-align: text-bottom; padding-right: 4px;" />
					<a href="${ctx}${url}" style="text-decoration: none;${i == active ? ' font-weight: bold; color: #6EAB23;' : ''}" title="${title}"><fmt:message key="${name}.${i}.name" /></a>
				</span>
			</td>
		</tr>
		</c:if>
		</c:forEach>
	</table>
</div>
