<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/activationMessage.jsp $
   $Id: activationMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Register" active="${navbarHome}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="menubar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="activation.header" /></h2>
			<%-- 
			<div class="subheader">
				<p><fmt:message key="activation.subheader" /></p>
			</div>
			--%>
			<div class="edit_message">
				<div class="global_error">
					<stripes:errors>
					<stripes:errors-header>
						<div class="errors"><h2><fmt:message key="activation.error.header" /></h2><ul>
						<ul>
							</stripes:errors-header>
							<li><stripes:individual-error /></li>
							<stripes:errors-footer>
						</ul>
					</stripes:errors-footer>
					</stripes:errors>
				</div>
				<stripes:messages />
			</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>
