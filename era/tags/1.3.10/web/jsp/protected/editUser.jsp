<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editUser.jsp $
   $Id: editUser.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - ${actionBean.user.id > 0 ? 'Edit' : 'Create'} User"
	active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminUser}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="admin.user.${empty actionBean.user.id ? 'create' : 'edit'}.header" /></h2>
		<div class="subheader">
			<fmt:message key="admin.user.${empty actionBean.user.id ? 'create' : 'edit'}.subheader" />
		</div>
		<div>
			<stripes:errors /><stripes:messages />
		</div>
		<div style="clear: both;">
			<a id="deleteItem" href="${ctx}/action/admin/user/deleteprofile/${actionBean.user.id}" class="delete_button" style="width: 12em;" title="${appname}" 
				onclick="Modalbox.show($('confirmDelete'), {title: this.title, width: 400}); return false;"><fmt:message key="button.delete.profile" /></a>
		</div>
		<div class="edit_profile">
			<stripes:form action="/action/admin/user">
				<stripes:hidden name="user.id" />
				<c:set var="user" value="${actionBean.user}" />
				<ul>
					<li><stripes:label for="user.username" /> <stripes:text name="user.username" /></li>
					<li><stripes:label for="user.firstName" /><stripes:text name="user.firstName" /></li>
					<li><stripes:label for="user.lastName" /><stripes:text name="user.lastName" /></li>
					<li><stripes:label for="user.email" /><stripes:text name="user.email" /></li>
					<c:if test="${not empty user.ccid}">
						<li><fmt:message key="user.ccid.help" var="userCcidHelp" /> <stripes:label for="user.ccid" title="${userCcidHelp}" /><stripes:checkbox
							name="associated" class="checkbox" />${actionBean.ccid}</li>
					</c:if>
					<li><stripes:label for="user.language" /> <stripes:select name="user.language">
						<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.Language" />
					</stripes:select></li>
					<li><stripes:label for="user.group" /> <stripes:select name="user.group.id">
						<stripes:options-collection collection="${context.allGroups}" value="id" label="name" />
					</stripes:select></li>
					<li><stripes:label for="user.state" /> <stripes:select name="state">
						<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.UserState" />
					</stripes:select></li>
				</ul>
				<stripes:submit name="${actionBean.user.id > 0 ? 'save' : 'create'}" value="${btnSave}" class="save_button" />
				<stripes:link href="/action/admin/users" class="cancel_link">
					<fmt:message key="button.cancel" />
				</stripes:link>
			</stripes:form>
		</div>
		</div>
		<div style="clear: both;" />
		<fmt:message key="button.yes" var="yes" />
		<fmt:message key="button.no" var="no" />
		<div id="confirmDelete" class="popup_box" style="display: none;">
			<h2><fmt:message key="user.delete.profile.header" /></h2>
			<p>
				<fmt:message key="confirm.delete.profile.prompt">
					<fmt:param value="${actionBean.user.firstName}${' '}${actionBean.user.lastName}" />
				</fmt:message>
			</p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('deleteItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
