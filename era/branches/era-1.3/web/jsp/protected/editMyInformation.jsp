<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/editMyInformation.jsp $
   $Id: editMyInformation.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Edit My Information" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
		<ir:sidebar name="myaccount.sidebar" active="1" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="user.edit.header" /></h2>
			<div class="subheader"><fmt:message key="user.edit.subheader" /></div>
			<stripes:errors /><stripes:messages />
			<div class="edit_profile">
				<stripes:form id="userForm" action="/action/myaccount/information" focus="user.firstName">
				<ul>
					<li><stripes:label for="user.firstName" /><stripes:text name="user.firstName" /></li>
					<li><stripes:label for="user.lastName" /><stripes:text name="user.lastName" /></li>
					<li><stripes:label for="user.email" /><stripes:text name="user.email" /></li>
					<security:secure roles="/user/ccid/association">
						<c:if test="${not empty user.ccid}">
							<li><stripes:label for="user.ccid" /> <stripes:checkbox name="associated" class="checkbox" />${actionBean.ccid}
							<p><fmt:message key="user.ccid.help" /></p>
							</li>
						</c:if>
					</security:secure>
					<li><stripes:label for="user.language" /> <stripes:select name="user.language">
						<%-- 
						<option value="en">English</option>
						--%>
						<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.Language" />
					</stripes:select></li>
				</ul>
				<div style="clear: both;" />
				<stripes:submit name="save" class="save_button" value="${btnSave}" />
				<stripes:link href="/action/myaccount/information" class="cancel_link">
					<fmt:message key="button.cancel" />
				</stripes:link>
				</stripes:form>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
