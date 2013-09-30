<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/contactUs.jsp $
   $Id: contactUs.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Contact Us" active="${navbarHelp}">
	<stripes:layout-component name="contents">
		<div class="full_box explanations">
			<h2>
				<fmt:message key="contactUs.header" />
			</h2>
			<div style="margin: 2em 0 2em 2em;">
				<fmt:message key="contactUs.description" />
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
