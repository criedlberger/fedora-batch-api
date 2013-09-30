<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/protected/changePassword.jsp $
   $Id: changePassword.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/action/password/change" />
</c:if>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="My Account - Change Password" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="password.change.header" /></h2>
			<div class="subheader"><fmt:message key="password.change.subheader" /></div>
			<stripes:errors /><stripes:messages />
			<div class="edit_profile password">
				<stripes:form id="passwordForm" action="/action/password" focus="currentPassword">
					<ul>
						<li><stripes:label for="currentPassword" /><stripes:password name="currentPassword" repopulate="true" /></li>
						<li><stripes:label for="newPassword" /><stripes:password name="newPassword" repopulate="true" /></li>
						<li><stripes:label for="confirmNewPassword" /><stripes:password name="confirmNewPassword" repopulate="true" /><li>
					</ul>    
	            	<stripes:submit name="save" class="save_button" value="${btnSave}" /> 
					<stripes:link href="/action/myaccount" class="cancel_link"><fmt:message key="button.cancel" /></stripes:link>
	            </stripes:form>
			</div>
			<div style="clear: both;" />
		</div>
    </stripes:layout-component>
</stripes:layout-render>
