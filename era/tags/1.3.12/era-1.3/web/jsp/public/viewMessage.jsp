<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/viewMessage.jsp $
   $Id: viewMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="View - Messages" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="view.message.header" /></h2>
		<div>
		<div class="global_error"><stripes:errors>
			<stripes:errors-header>
				<div class="errors"><h2><fmt:message key="view.errors.header" /></h2>
				<ul>
			</stripes:errors-header>
			<li><stripes:individual-error /></li>
			<stripes:errors-footer>
				</ul></div>
			</stripes:errors-footer>
		</stripes:errors>
		</div>
		<stripes:messages />
	</stripes:layout-component>

</stripes:layout-render>
