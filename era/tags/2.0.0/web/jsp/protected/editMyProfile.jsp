<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/protected/editMyProfile.jsp $
   $Id: editMyProfile.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Edit My Profile" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="myaccount.sidebar" active="2" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><fmt:message key="profile.edit.header" /></h2>
	
		<div class="subheader profile_edit"><fmt:message key="profile.edit.subheader" /></div>
		<stripes:errors /><stripes:messages />
		<div class="edit_profile">
			<ul>
				<li style="height: 1em;">
					<stripes:label for="profile.picture" />
					<iframe id="fileUpload" name="fileUpload" src="" style="display: none;"></iframe>
					<form id="uploadForm" action="${ctx}/ajax/thumbnail/put" method="post" enctype="multipart/form-data" target="fileUpload">
						<div class="fileinputs" id="fileinputs"><input type="file" name="file" id="file" class="file" onchange="uploadPicture();" />
						<div class="fakefile" id="fakefile"><input type="text" name="path" id="path" style="width: 276px;" value="${actionBean.imagePath}" />
							<input type="button" class="file_button" value="${btnBrowse}" />
						</div>
					</form>
					<div class="object_logo">
						<img id="logo" name="logo" src="${ctx}/public/researcher/getPicture/${actionBean.author.id}" class="profile_thumb" />
					</div>
				</li>
			</ul>
			<ul style="clear: both;">
				<stripes:form id="profileForm" action="/action/myaccount/profile">
				<stripes:hidden name="author.id" />
				<stripes:hidden name="filename" id="filename" />
				<stripes:hidden name="imagePath" id="imagePath" />
				<li><stripes:label for="profile.remove.picture" /><stripes:checkbox name="removePicture" style="width: 1em;" /></li>
				<li><stripes:label for="profile.published" /><stripes:checkbox name="author.published" style="width: 1em;" /></li>
				<li><stripes:label for="profile.institution" /><stripes:text name="author.institution" /></li>
				<li><stripes:label for="profile.description" /><stripes:textarea name="author.description"/></li>
				<li><stripes:label for="profile.contact" /><stripes:textarea name="author.contact" /></li>
				<li>
					<stripes:label for="profile.cv" />
                    <stripes:file name="cv" id="cv" class="file" onchange="$('cvLabel').value = $('cv').value;" />
                    <div class="fakefile" id="fakefile">
                    	<input type="text" name="cvLabel" id="cvLabel" value="${actionBean.cvPath}" /><input type="button" class="file_button" value="${btnBrowse}" />	
                   	</div>
                </li>
                <li><stripes:label for="profile.remove.cv" /><stripes:checkbox name="removeCv" style="width: 1em;" /></li>
                <c:choose>
                <c:when test="${empty actionBean.author.authorProfiles}">
			    	<c:forEach items="${actionBean.profileList}" var="profile" varStatus="status">
						<li>
							<stripes:label for="AuthorProfileType.${profile.value}" />
							<stripes:hidden name="authorProfiles[${status.index}].id" value="" />
							<stripes:hidden name="authorProfiles[${status.index}].type" value="${profile.value}" />
							<stripes:textarea name="authorProfiles[${status.index}].description" value="${actionBean.profileMap[profile.value]}" />
							<p class="hint"><fmt:message key="AuthorProfileType.${status.index}.description" /></p>
						</li>		
			    	</c:forEach>
                </c:when>
                <c:otherwise>
			    	<c:forEach items="${actionBean.author.authorProfiles}" var="profile" varStatus="status">
						<li>
							<stripes:label for="AuthorProfileType.${profile.type}" />
							<stripes:hidden name="authorProfiles[${status.index}].id" value="${profile.id}" />
							<stripes:hidden name="authorProfiles[${status.index}].type" value="${profile.type}" />
							<stripes:textarea name="authorProfiles[${status.index}].description" value="${profile.description}" />
							<p class="hint"><fmt:message key="AuthorProfileType.${status.index}.description" /></p>
						</li>
			    	</c:forEach>
                </c:otherwise>
                </c:choose>
          		<stripes:submit name="save" class="save_button" value="${btnSave}" /> 
				<stripes:link href="/action/myaccount/profile" class="cancel_link"><fmt:message key="button.cancel" /></stripes:link>
				</stripes:form>
			</ul>
		</div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
