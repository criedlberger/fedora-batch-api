<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/protected/activation.jsp $
   $Id: activation.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - User Activativation" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminActivation}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><a href="#"><stripes:label for="admin.activation.header" /></a></h2>
		<div class="subheader"><fmt:message key="admin.activation.subheader" /></div>

		<stripes:errors /><stripes:messages />

		<stripes:form action="/action/admin/activation">
			<stripes:hidden name="user.id" />
			<c:set var="user" value="${actionBean.user}" />
			<table>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.firstName" />:</td>
					<td><div class="input_view" style="width: 460px;">${user.firstName}</div></td>
				</tr>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.lastName" />:</td>
					<td><div class="input_view" style="width: 460px;">${empty user.lastName ? '&nbsp;' : user.lastName}</div></td>
				</tr>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.username" />:</td>
					<td><div class="input_view" style="width: 460px;">${user.username}</div></td>
				</tr>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.email" />:</td>
					<td><div class="input_view" style="width: 460px;"><a href="mailto:${user.email}" style="text-decoration: none;">${user.email}</a></div></td>
				</tr>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.authType" />:</td>
					<td><div class="input_view" style="width: 460px;"><fmt:message key="AuthType.${user.authType}" /></div></td>
				</tr>
				<c:forEach items="${actionBean.user.registers}" var="register">
				<tr>
					<td class="input_label" style="width: 120px; vertical-align: top;"><stripes:label for="register.note" />:</td>
					<td><div class="input_view" style="width: 460px; height: 120px; overflow: auto;">${register.note}</div></td>
				</tr>
				</c:forEach>
				<tr>
					<td class="input_label" style="width: 120px;"><stripes:label for="user.group" />:</td>
					<td>
						<stripes:select name="user.group.id">
							<stripes:options-collection collection="${context.allGroups}" value="id" label="name" />
						</stripes:select>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="right" style="text-align: right;">
						<stripes:submit name="activate" class="button" value="${btnActivate}" />
					</td>
				</tr>
			</table>
		</stripes:form>

		</div>
    </stripes:layout-component>
</stripes:layout-render>