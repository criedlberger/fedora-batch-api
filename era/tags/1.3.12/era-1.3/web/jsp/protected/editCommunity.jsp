<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/protected/editCommunity.jsp $
   $Id: editCommunity.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ include file="/jsp/public/mode.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Community - ${event == 'edit' || event == 'save' ? 'Edit' : 'Create' } Collection" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="menubar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div class="full_box">
		<c:if test="${event == 'preCreate' || event == 'create'}">
			<h2><stripes:label for="community.create.header" /></h2>
			<div class="subheader">
				<p><fmt:message key="community.create.subheader"></fmt:message></p>
			</div>
			<stripes:errors /><stripes:messages />
		</c:if>
		<c:if test="${event == 'edit' || event == 'save'}">
			<h2><stripes:label for="community.edit.header" /></h2>
			<div class="subheader">
				<p><fmt:message key="community.edit.subheader"></fmt:message></p>
			</div>
			<stripes:errors /><stripes:messages />
		</c:if>

		<div class="edit_profile">
			<form id="uploadForm" action="${ctx}/ajax/thumbnail/put" method="post" enctype="multipart/form-data" target="fileUpload">
				<ul>
					<li>
						<stripes:label for="community.picture" />
						<iframe id="fileUpload" name="fileUpload" src="" style="display: none;"></iframe>
						<div class="fileinputs" id="fileinputs">
							<input type="file" name="file" id="file" class="file" onchange="uploadPicture();" />
							<div class="fakefile" id="fakefile"><input type="text" name="path" id="path" value="${actionBean.imagePath}" />
							<input type="button" class="file_button" value="${btnBrowse}" /></div>
						</div>
					</li>
				</ul>
			</form>
			<stripes:form id="communityForm" action="/action/community" focus="community.title">
				<stripes:hidden name="community.id" id="community.id" />
				<stripes:hidden name="filename" id="filename" />
				<stripes:hidden name="imagePath" id="imagePath" />
				<stripes:hidden name="start" />
				<stripes:hidden name="sortBy" />
				<stripes:hidden name="mode" />
				
				<c:if test="${event == 'preCreate' || event == 'create'}">
				<input type="hidden" name="create" />
				</c:if>
				<c:if test="${event == 'edit' || event == 'save'}">
				<input type="hidden" name="save" />
				</c:if>

				<ul>
					<li style="height: 1em;">
						<stripes:label for="community.title" /><stripes:text name="community.title" />
						<c:set var="imageUrl" value="${ctx}/public/datastream/get/${actionBean.community.id}/THUMBNAIL" />
						<c:if test="${empty actionBean.community.id}">
							<c:set var="imageUrl" value="${ctx}/images/space.gif" />
						</c:if>
						<img id="logo" name="logo" src="${imageUrl}" class="logo" />
					</li>
					<li><stripes:label for="community.description" /><stripes:textarea name="community.description" /></li>
				
					<!-- community properties -->
					<security:secure roles="/admin/community">
				    <c:forEach items="${actionBean.partOfList}" var="partOf" varStatus="status">
				    <c:if test="${partOf != 'DARK_REPOSITORY' && partOf != 'EMBARGOED'}">
						<li>
							<stripes:label for="PartOfRelationship.${partOf}" />
							<input type="checkbox" name="properties" class="checkbox" value="${partOf.pid}" ${actionBean.partOfs[partOf.value] ? 'checked="checked"' : ''} />
						</li>
					</c:if>
				    </c:forEach>
					</security:secure>
				</ul>
			</stripes:form>
			
			<div>
			<c:if test="${event == 'preCreate' || event == 'create'}">
			<input type="button" name="create" value="${btnCreate}" class="save_button" onclick="submitCommunityForm(); return false;" />
			</c:if>
			<c:if test="${event == 'edit' || event == 'save'}">
			<input type="button" name="save" value="${btnSave}" class="save_button" onclick="submitCommunityForm(); return false;" />
			</c:if>
			<a class="cancel_link" href="${ctx}/action/admin/communities">${btnCancel}</a>
			
			</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>
