<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/exceptionError.jsp $
   $Id: exceptionError.jsp 5603 2012-10-05 18:51:26Z pcharoen $
   $Revision: 5603 $ $Date: 2012-10-05 12:51:26 -0600 (Fri, 05 Oct 2012) $
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
