<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/myProfile.jsp $
   $Id: myProfile.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="ca.ualberta.library.ir.domain.AuthorProfile"%>

<%! static Pattern pattern = Pattern.compile("\\n"); %>
<%! static Pattern linkPattern = Pattern.compile("\\|"); %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Edit My Profile" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="myaccount.sidebar" active="2" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="myProfile.header" /></h2>
		<div class="subheader">
			<c:if test="${empty actionBean.author.authorProfiles}"><fmt:message key="myProfile.noProfile" /></c:if>
			<c:if test="${not empty actionBean.author.authorProfiles}"><fmt:message key="myProfile.subheader" /></c:if>
		</div>
		<stripes:errors /><stripes:messages />
		<div style="float: right; min-width: 20em;">
		</div>
		<ul class="item_info" style="width: 50em;">
		    <security:secure roles="/user/profile/update">
		    <li>
				<a href="${ctx}/action/myaccount/profile/edit" class="edit_button edit_profile_button"><fmt:message key="toolbar.edit.profile.label" /></a>
				<br style="clear: both;" />
		    </li>
		    </security:secure>
			<li>
				<img id="logo" name="logo" src="${ctx}/public/researcher/getPicture/${actionBean.author.id}" class="profile_thumb" />
			</li>
			<li>
				<stripes:label for="profile.published" />
				<img src="${ctx}/images/checkbox${actionBean.author.published ? 'on' : 'off' }.gif" class="checkbox" />
			</li>
			<li>
				<stripes:label for="profile.institution" />${' '}${actionBean.author.institution}
			</li>
			<li><stripes:label for="profile.description" />${' '}${actionBean.author.description}</li>
			<li><stripes:label for="profile.contact" />${' '}
					<c:if test="${not empty actionBean.author.contact}">
						<c:set var="contact" value="${actionBean.author.contact}" />
						<% pageContext.setAttribute("contact", pattern.split((String)pageContext.getAttribute("contact"))); %>
						<c:forEach items="${contact}" var="ln" varStatus="status">${status.index == 0 ? "" : "<br/>"}${ln}</c:forEach>
						&nbsp;
					</c:if>
				</li>
			<li><stripes:label for="profile.cv" />${' '}
					<c:if test="${not empty actionBean.author.cv}">
						<a href="${ctx}/public/researcher/downloadCv/${actionBean.author.id}" target="Download" class="download">Download CV</a>
					</c:if>
				</li>
			
		    <c:forEach items="${actionBean.author.authorProfiles}" var="profile" varStatus="status">
				<li><stripes:label for="AuthorProfileType.${profile.type}" />${' '}
		
					<c:if test="${not empty profile.description}">
					<% 	
						AuthorProfile prof = (AuthorProfile) pageContext.getAttribute("profile");
						String[] desc = pattern.split(prof.getDescription());
						pageContext.setAttribute("desc", desc);
					%>
					<c:forEach items="${desc}" var="ln" varStatus="status">
						<c:if test="${profile.type != 4}">${status.index > 0 ? "<br />": ""}${ln}</c:if>
						<c:if test="${profile.type == 4}">
							<% pageContext.setAttribute("link", linkPattern.split((String)pageContext.getAttribute("ln"))); %>
							${status.index > 0 ? "<br />": ""}<a href="${link[0]}">${link[1]}</a>
						</c:if>
					</c:forEach>
					</c:if></li>
				
	
		    </c:forEach>
           	<li><stripes:label for="profile.uri" />${' '}<ir:handle type="author" pid="${user.username}" var="uri" /><a href="${uri}">${uri}</a></li>
			<li><stripes:label for="profile.createdDate" />${' '}<fmt:formatDate pattern="${actionBean.dateFormat}" value="${actionBean.author.createdDate}" /></li>
			<li><stripes:label for="profile.modifiedDate" />${' '}<fmt:formatDate pattern="${actionBean.dateFormat}" value="${actionBean.author.modifiedDate}" /></li>
		</ul>
		<div style="clear: both;" />
		</div>
    </stripes:layout-component>
</stripes:layout-render>
