<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/editCollection.jsp $
   $Id: editCollection.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%-- 
<%@ include file="/jsp/public/mode.jspf"%>
--%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Collection - ${event == 'edit' || event == 'save' ? 'Edit' : 'Create' } Collection" active="0">

	<stripes:layout-component name="contents">
		<div class="full_box">
		<c:if test="${event == 'preCreate' || event == 'create'}">
			<h2><stripes:label for="collection.create.header" /></h2>	
			<div class="subheader">
				<p><fmt:message key="collection.create.subheader"></fmt:message></p>
			</div>
		</c:if>
		<c:if test="${event == 'edit' || event == 'save'}">
			<h2><stripes:label for="collection.edit.header" /></h2>
			<div class="subheader">
				<p><fmt:message key="collection.edit.subheader"></fmt:message></p>
			</div>
		</c:if>
		<stripes:errors /><stripes:messages />
		<div class="edit_profile">
			<ul>
				<li>
					<stripes:label for="collection.picture" />
					<iframe id="fileUpload" name="fileUpload" src="" style="display: none;"></iframe>
					<form id="uploadForm" action="${ctx}/ajax/thumbnail/put" method="post" enctype="multipart/form-data" target="fileUpload">
					<div class="fileinputs" id="fileinputs">
						<input type="file" name="file" id="file" class="file" onchange="uploadPicture();" />
						<div class="fakefile" id="fakefile"><input type="text" name="path" id="path" style="width: 376px;" value="${actionBean.imagePath}" />
							<input type="button" class="file_button" value="${btnBrowse}" />
						</div>
					</div>
					</form>
				</li>
			</ul>
			<stripes:form id="collectionForm" action="/action/collection" focus="collection.title" onsubmit="submitCollectionForm(); return true;">
			<stripes:hidden name="filename" id="filename" />
			<stripes:hidden name="imagePath" id="imagePath" />
			<stripes:hidden name="collection.id" />
			<stripes:hidden name="start" />
			<stripes:hidden name="sortBy" />
			<ul>
				<li style="height: 1em;">
					<stripes:label for="collection.title" /><stripes:text name="collection.title" />
					<c:set var="imageUrl" value="${ctx}/public/datastream/get/${actionBean.collection.id}/THUMBNAIL" />
					<c:if test="${empty actionBean.collection.id}">
						<c:set var="imageUrl" value="${ctx}/images/space.gif" />
					</c:if>
					<img id="logo" name="logo" src="${imageUrl}" class="logo" />
				</li>
				<li>
					<stripes:label for="collection.description" />
					<stripes:textarea name="collection.description" />
				</li>
			    <li>
				    <stripes:label for="collection.formName" />
					<stripes:select name="collection.formName">
						<stripes:option></stripes:option>
						<stripes:options-collection collection="${actionBean.inputForms.nameMaps}" value="formName" label="formName" />
					</stripes:select>
					<p><fmt:message key="collection.formName.hint" /></p>
			    </li>
				<li>
					<stripes:label for="collection.ccid" />
					<stripes:checkbox name="collection.ccid" class="checkbox" />
					<p><fmt:message key="collection.ccid.hint" /></p>
				</li>
				<li>
					<stripes:label for="collection.approval" />
					<stripes:checkbox name="collection.approval" class="checkbox" />
					<p><fmt:message key="collection.approval.hint" /></p>
				</li>
			    <li>
				    <stripes:label for="collection.metaDescription" />
				    <stripes:checkbox name="collection.metaDescription" class="checkbox" />
					<p><fmt:message key="collection.metaDescription.hint" /></p>
			    </li>
			    <%-- 
			    <li>
				    <stripes:label for="collection.sortSER" />
				    <stripes:checkbox name="collection.sortSER" class="checkbox" />
					<p><fmt:message key="collection.sortSER.hint" /></p>
			    </li>
			    --%>
			    <li>
				    <stripes:label for="collection.sort" />
					<stripes:select name="collection.sort">
						<stripes:option></stripes:option>
						<%@ include file="/jsp/protected/sortBy.jspf" %>
					</stripes:select>
					<p><fmt:message key="collection.sort.hint" /></p>
			    </li>
			    <li>
				    <stripes:label for="collection.proquestUpload" />
				    <stripes:checkbox name="collection.proquestUpload" class="checkbox" />
					<p><fmt:message key="collection.proquestUpload.hint" /></p>
			    </li>
				<li>
					<table style="margin-left: 5em;">
					<tr>
						<td style="text-align: center; font-weight: bold;"><fmt:message key="collection.communities" /></td>
						<td></td>
						<td style="text-align: center; font-weight: bold;"><fmt:message key="collection.memberOf" /></td>
					</tr>
					<tr>
						<td align="right">
							<stripes:select name="communities" id="collection.communities" style="width: 25em;" size="8" multiple="true">
								<stripes:options-collection collection="${actionBean.communities}" label="title" value="id" />
							</stripes:select>
						</td>
	                    <td align="center" style="vertical-align: middle;">
							<div><img src="${ctx}/images/forward.gif" onclick="addCommunities();" style="cursor: pointer;" /></div>
							<div><img src="${ctx}/images/back.gif" onclick="removeCommunities();" style="cursor: pointer;" /></div>
						</td>
						<td align="left">
							<stripes:select name="memberOf" id="collection.memberOf" style="width: 25em;" size="8" multiple="true">
								<stripes:options-collection collection="${actionBean.memberOfCommunities}" label="title" value="id" />
							</stripes:select>
						</td>
					</tr>
					</table>
			    </li>
			</ul>
			<div sytle="clear: both; display: block;">
				<c:if test="${event == 'preCreate' || event == 'create'}">
					<stripes:submit name="create" value="${btnCreate}" class="save_button" />
				</c:if>
				<c:if test="${event == 'edit' || event == 'save'}">
					<stripes:submit name="save" value="${btnSave}" class="save_button" />
				</c:if>
				<a class="cancel_link" href="${ctx}/action/admin/collections">${btnCancel}</a>
			</div>
			</stripes:form>
		</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>
