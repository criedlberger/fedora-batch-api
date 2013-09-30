<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/protected/editGroup.jsp $
   $Id: editGroup.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - ${event == 'add' ? 'Add ' : 'Edit '}Group" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2>${event == 'add' ? 'Add ' : 'Edit '}<fmt:message key="group.header" /></h2>
		<div class="subheader"><fmt:message key="group.subheader" /></div>

		<stripes:errors /><stripes:messages />
		
		<div class="edit_profile">
			<stripes:form action="/action/admin/group" focus="group.name">
				<stripes:hidden name="group.id" />
				<ul>
					<li><stripes:label for="group.name" /><stripes:text name="group.name" /></li>
					<li><stripes:label for="group.description" /><stripes:text name="group.description" /></li>
				</ul>
               	<stripes:submit name="save" class="save_button" value="${btnSave}" /> 
               	<stripes:link href="/action/admin/group/permission" class="cancel_link">${btnCancel}</stripes:link> 
	         </stripes:form>
		</div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
