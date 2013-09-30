<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/researcherList.jsp $
   $Id: researcherList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page import="ca.ualberta.library.ir.domain.AuthorProfile" %>
<%@ page import="java.util.regex.Pattern" %>
<%! static Pattern pattern = Pattern.compile("\\n"); %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Researcher - Author List" active="${navbarResearcher}">
	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2 class="collections"><fmt:message key="researcher.header" /></h2>
			<security:secure roles="/user/profile/update">
            	<stripes:link href="/action/myaccount/profile/edit" class="create_button"><fmt:message key="header.start.yours" /></stripes:link>
			</security:secure>
			<div class="subheader"><fmt:message key="researcher.subheader" /></div>
			<stripes:errors /><stripes:messages />
			<div class="browse">
				<div class="researcher_link" style="clear: both;">
				<stripes:label for="researcher.lastname.label" />
				<c:forEach items="${actionBean.nameList}" var="name" varStatus="status">
					<c:if test="${actionBean.nameMap[name]}">
						<a href="#${name}">${name}</a>
					</c:if>
					<c:if test="${!actionBean.nameMap[name]}">
						<span>${name}</span>
					</c:if>
				</c:forEach>
				</div>
            	<ul class="researcher_list">
    			<c:forEach items="${actionBean.authors}" var="author" varStatus="status">
    			<li>
    				<img id="picture.${status.index}" src="${ctx}/public/researcher/getPicture/${author.id}" onclick="location.href = '${ctx}/public/author/${author.user.username}';" />
    				<h2><a id="${fn:toUpperCase(fn:substring(author.user.lastName, 0, 1))}" href="${ctx}/public/view/author/${author.user.username}">${author.user.firstName}${" "}${author.user.lastName}</a></h2>
    				<p><em>${author.institution}</em></p>
    				<c:forEach items="${author.authorProfiles}" var="profile" varStatus="sts" begin="0" end="1">
    					<c:if test="${not empty profile.description}">
    						<% pageContext.setAttribute("description", pattern.matcher(((AuthorProfile) pageContext.getAttribute("profile")).getDescription()).replaceAll(", ")); %>
    						<p>${description}</p>
    					</c:if>
    				</c:forEach>
    			</li>
    			</c:forEach>
    			</ul>
			</div>
			<div style="clear: both;" />
		</div>
    </stripes:layout-component>
</stripes:layout-render>


