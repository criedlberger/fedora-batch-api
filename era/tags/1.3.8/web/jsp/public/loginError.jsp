<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.8/web/jsp/public/loginError.jsp $
   $Id: loginError.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<stripes:layout-render name="/jsp/layout/standard.jsp" title="Login Error">

    <stripes:layout-component name="contents">

		<div class="full_box">
			<div class="error_box">
				<h2 class="global_error"><fmt:message key="login.error.header" /></h2>
				<stripes:errors globalErrorsOnly="true" />
				<br />
				<div class="global_error">
					<p><a href="${httpsServerUrl}${ctx}/public/login"><b>${httpsServerUrl}${ctx}/public/login</b></a></p>
				</div>
			</div>
		</div>		

	</stripes:layout-component>
	
</stripes:layout-render>	
