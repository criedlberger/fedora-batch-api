<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editGroupPermission.jsp $
   $Id: editGroupPermission.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Group Permissions" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminGroup}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
				<div class="admin_options">
		<h2><stripes:label for="group.permission.header" /></h2>
		<div class="subheader"><fmt:message key="group.permission.subheader" /></div>
		<stripes:errors /><stripes:messages />
		<table class="display">
		<stripes:form id="permissionForm" action="/action/admin/group/permission">
			<table class="display">
				<tr>
					<td>
						<stripes:label for="group.label" />:
						<stripes:select name="groupId" onchange="location.href = '${ctx}/action/admin/group/permission/list/' + this.value;">
							<stripes:options-collection collection="${context.allGroups}" value="id" label="name" />
						</stripes:select>
					</td>
					<td>
					<security:secure roles="/admin/group">
				 		<a href="${ctx}/action/admin/group/add" class="add_button auto_width"><fmt:message key="group.add" /></a>
			 			<a href="${ctx}/action/admin/group/edit/${actionBean.groupId}" class="edit_button"><fmt:message key="group.edit" /></a>
					</security:secure>
					</td>
				</tr>
				<tr><td><stripes:label for="group.description" />: ${actionBean.group.description}</td></tr>
				<tr>
					<td style="vertical-align: top;">
						<table class="display">
						<tr>
							<th style="height: 20px;"><fmt:message key="permission.admin.label" /></th>
							<th style="text-align: center; height: 20px;"><fmt:message key="permission.allowed" /></th>
						</tr>
						<c:set var="count" value="0" />
						<c:forEach items="${actionBean.permissions}" var="permission" varStatus="status">
						<c:if test="${fn:startsWith(permission.permission, '/admin')}">
						<c:set var="perm" value="${permission.permission}" />
						<c:set var="count" value="${count + 1}" />
						<% pageContext.setAttribute("permName", SystemPermissions.getName((String)pageContext.getAttribute("perm"))); %>
						<tr class="${count mod 2 == 0 ? "even" : "odd"}">
							<td style="font-size: 11px; font-weight: bold; height: 20px;">
								<fmt:message key="permission.admin.task" var="title" /><img src="${ctx}/images/admin.gif" title="${title}" style="vertical-align: text-bottom; padding-right: 2px;" />
								<fmt:message key="Permissions.${permName}" />
							</td>
							<td  style="font-size: 11px; font-weight: bold; height: 20px; text-align: center;"><input type="checkbox" name="perms" ${permission.allowed ? 'checked' : ''} value="${perm}" /></td>
						</tr>
						</c:if>
						</c:forEach>
						</table>
					</td>
					<td style="vertical-align: top;">
						<table class="display">
						<tr>
							<th style="height: 20px;"><fmt:message key="permission.user.label" /></th>
							<th style="text-align: center; height: 20px;"><fmt:message key="permission.allowed" /></th>
						</tr>
						<c:set var="count" value="0" />
						<c:forEach items="${actionBean.permissions}" var="permission" varStatus="status">
						<c:if test="${!fn:startsWith(permission.permission, '/admin')}">
						<c:set var="perm" value="${permission.permission}" />
						<c:set var="count" value="${count + 1}" />
						<% pageContext.setAttribute("permName", SystemPermissions.getName((String)pageContext.getAttribute("perm"))); %>
						<tr class="${count mod 2 == 0 ? "even" : "odd"}">
							<td style="font-size: 11px; font-weight: bold; height: 20px;">
								<fmt:message key="permission.user.task" var="title" /><img src="${ctx}/images/user.gif" title="${title}" style="vertical-align: text-bottom; padding-right: 2px;"  />
								<fmt:message key="Permissions.${permName}" />
							</td>
							<td  style="font-size: 11px; font-weight: bold; height: 20px; text-align: center;"><input type="checkbox" name="perms" ${permission.allowed ? 'checked' : ''} value="${perm}" /></td>
						</tr>
						</c:if>
						</c:forEach>
						</table>
					</td>
				</tr>
               	</table>
         
           		<stripes:submit name="save" class="save_button" value="${btnSave}" /> 
				<stripes:link href="/action/admin/home" class="cancel_link"><fmt:message key="button.cancel" /></stripes:link>
		</stripes:form>
		</div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
