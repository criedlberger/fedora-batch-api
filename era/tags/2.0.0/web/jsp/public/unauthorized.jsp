<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/public/unauthorized.jsp $
   $Id: unauthorized.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Unauthorized">
    <stripes:layout-component name="contents">
		<div class="full_box global_error">
	     	<div class="errors"><h2><fmt:message key="errors.unauthorized.header" /></h2>
	     		<ul>
			    	<li><fmt:message key="errors.unauthorized.messages" /></li>
	     		</ul>
	     	</div>
			<br style="clear: both;" />
			<div style="margin-top: 1em;">
				<fmt:message key="errors.unauthorized.footer"><fmt:param>${adminEmail}</fmt:param></fmt:message>
			</div>
		</div>		
	</stripes:layout-component>
</stripes:layout-render>	
