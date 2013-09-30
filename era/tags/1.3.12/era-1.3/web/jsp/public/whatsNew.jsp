<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/whatsNew.jsp $
   $Id: whatsNew.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - What's New" active="${navbarHome}">
    <stripes:layout-component name="contents">

		<div class="full_box">
			<img src="${ctx}/images/questionmark.gif" alt="Image" title="Image" class="image" /> <h2><a href="#">What's New?</a></h2>
			<a href="">Newest</a> | <a href="">This Week</a> | <a href="">This Month</a> | <a href="">Last Week</a> | <a href="">Last Month</a>
			<p><fmt:message key="whatsNew.description" /></p>
			<div style="text-align: right; margin-top: 20px;">
				<a href="${ctx}/public/home" class="back_link">&laquo; Back</a>
			</div>
		</div>

    </stripes:layout-component>
</stripes:layout-render>
