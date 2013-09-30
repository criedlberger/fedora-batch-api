<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/aboutFaq.jsp $
   $Id: aboutFaq.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<stripes:useActionBean binding="/public/home" var="actionBean" />
<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - About &amp; FAQ" active="${navbarHelp}">
    <stripes:layout-component name="contents">

		<div class="full_box explanations">
			<h2><fmt:message key="aboutFaq.header" /></h2> 
			<fmt:message key="aboutFaq.description" />
		</div>

    </stripes:layout-component>
</stripes:layout-render>
