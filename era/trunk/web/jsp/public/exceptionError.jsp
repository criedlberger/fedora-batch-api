<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/public/exceptionError.jsp $
   $Id: exceptionError.jsp 5585 2012-09-27 22:11:57Z pcharoen $
   $Revision: 5585 $ $Date: 2012-09-27 16:11:57 -0600 (Thu, 27 Sep 2012) $
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
