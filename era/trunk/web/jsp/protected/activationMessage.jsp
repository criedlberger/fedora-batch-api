<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/activationMessage.jsp $
   $Id: activationMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - User Activativation" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminActivation}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box" style="height: 280px;">

		<h2><a href="#"><stripes:label for="admin.activation.header" /></a></h2>
		<div class="subheader">
			<p><fmt:message key="admin.activation.subheader"></fmt:message></p>
		</div>

		<div class="edit_message">
			<div class="global_error">
			<stripes:errors>
			     <stripes:errors-header><h2 class="global_error"><fmt:message key="errors.validataion.header" /></h2><ul style="list-style-type: circle; padding-left: 20px;"></stripes:errors-header>
			     <li><stripes:individual-error /></li>
			     <stripes:errors-footer></ul></stripes:errors-footer>
			</stripes:errors>
			</div>
			<div style="margin-top: 50px; text-align: center;">
				<stripes:messages />
			</div>
		</div>
		</div>

	</stripes:layout-component>

</stripes:layout-render>
