<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/public/exceptionError.jsp $
   $Id: exceptionError.jsp 5615 2012-10-16 18:20:47Z pcharoen $
   $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<stripes:layout-render name="/jsp/layout/standard.jsp" title="Application Errors" active="0">
    <stripes:layout-component name="contents">
		<div class="full_box global_error">
			<stripes:errors globalErrorsOnly="true">
			     <stripes:errors-header>
			     	<div class="errors"><h2><fmt:message key="errors.exception.header" /></h2><ul>
			     </stripes:errors-header>
			     <li><stripes:individual-error /></li>
			     <stripes:errors-footer>
			     	</ul></div>
					<br style="clear: both;" />
					<div style="margin-top: 1em;"><fmt:message key="errors.exception.footer" /></div>
			     </stripes:errors-footer>
			</stripes:errors>
		</div>		
	</stripes:layout-component>
</stripes:layout-render>	
