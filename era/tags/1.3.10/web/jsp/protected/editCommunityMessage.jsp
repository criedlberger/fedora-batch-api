<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editCommunityMessage.jsp $
   $Id: editCommunityMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ include file="/jsp/public/mode.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Community - ${modeNavbar == 3 ? 'Create' : 'Edit' } Community" active="${modeNavbar}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<c:if test="${modeNavbar == 3}">
	<stripes:layout-component name="menubar">
		<ir:menubar name="deposit.menubar" active="3" itemCount="3" menubarClass="menubar" showTitle="true" titleClass="menu_title" />
	</stripes:layout-component>
	</c:if>

	<stripes:layout-component name="contents">
		<div class="full_box">

		<h2><stripes:label for="community.${event}.header" /></h2>
		<div class="subheader">
		<p><fmt:message key="community.${event}.subheader"></fmt:message></p>
		</div>

		<div class="edit_message">
			<div class="global_error">
			<stripes:errors>
			     <stripes:errors-header><h2 class="global_error"><fmt:message key="errors.process.header" /></h2><ul style="list-style-type: circle; padding-left: 20px;"></stripes:errors-header>
			     <li><stripes:individual-error /></li>
			     <stripes:errors-footer></ul></stripes:errors-footer>
			</stripes:errors>
			</div>
			<stripes:messages />
		</div>
		</div>

	</stripes:layout-component>

</stripes:layout-render>
