<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/protected/editCollectionMessage.jsp $
   $Id: editCollectionMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Collection - ${event == 'edit' || event == 'save' ? 'Edit' : 'Create' } Collection" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<c:if test="${modeNavbar == 3}">
	<stripes:layout-component name="menubar">
		<ir:menubar name="deposit.menubar" active="2" itemCount="3" menubarClass="menubar" showTitle="true" titleClass="menu_title" />
	</stripes:layout-component>
	</c:if>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="collection.${event}.header" /></h2>
			<div class="edit_message">
			<div class="global_error">
				<stripes:errors>
					<stripes:errors-header>
						<div class="errors"><h2><fmt:message key="errors.process.header" /></h2>
						<ul>
					</stripes:errors-header>
					<li><stripes:individual-error /></li>
					<stripes:errors-footer>
						</ul></div>
					</stripes:errors-footer>
				</stripes:errors>
			</div>
			<stripes:messages />
		</div>
	</stripes:layout-component>

</stripes:layout-render>
