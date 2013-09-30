<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/approvalMessage.jsp $
   $Id: approvalMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Review and Approve Pending Submited Items" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="admin.approve.header" /></h2>
		<div class="edit_message">
		<div class="global_error"><stripes:errors>
			<stripes:errors-header>
				<div class="errors"><h2><fmt:message key="errors.process.header" /></h2>
				<ul>
			</stripes:errors-header>
			<li><stripes:individual-error /></li>
			<stripes:errors-footer>
				</ul></div>
			</stripes:errors-footer>
		</stripes:errors>
		</div>
		<stripes:messages />
		</div>
	</stripes:layout-component>

</stripes:layout-render>
