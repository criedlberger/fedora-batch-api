<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/protected/activationList.jsp $
   $Id: activationList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - User Activativation" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminActivation}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><a href="#"><stripes:label for="admin.activation.header" /></a></h2>
		<div class="subheader">
			<c:if test="${actionBean.resultRows > 0}">
			<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b> of <b>${actionBean.numFound}</b> Users
			</c:if>
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
		</div>

		<stripes:messages /><stripes:errors />

		<c:if test="${actionBean.resultRows > 0}">
		<table class="display" style="width: 100%;">
		    <tr>
		    	<th><fmt:message key="user.username" /></th>
		    	<th><fmt:message key="user.email" /></th>
		    	<th><fmt:message key="user.createdDate" /></th>
		    	<th></th>
		    </tr>

			<c:forEach items="${actionBean.users}" var="user" varStatus="status">
			<tr class="${status.count mod 2 == 0 ? "even" : "odd"}">
				<fmt:message key="AuthType.${user.authType}" var="type" />
				<fmt:message key="user.authType" var="auth" />
				<td><img src="${ctx}/images/${user.authType == 0 ? 'password' : 'ccid'}.gif" title="${type }${' '}${auth}" style="vertical-align: text-bottom;" /> <a href="${ctx}/action/admin/activation/edit/${user.id}">${user.firstName}${" "}${user.lastName}</a></td>
				<td><a href="mailto:${user.email}">${user.email}</a></td>
				<td><fmt:formatDate pattern="MMM d, yyyy" value="${user.createdDate}" /></td>
				<td style="width: 60px; font-size: 11px;">
					<img src="${ctx}/images/user_activation.gif" style="vertical-align: text-bottom;" onclick="location.href = '${ctx}/action/admin/activation/edit/${user.id}';"> 
					<a href="${ctx}/action/admin/activation/edit/${user.id}"><fmt:message key="toolbar.activate.label" /></a>
				</td>
			</tr>
			</c:forEach>
		</table>
		</c:if>
		
		<c:if test="${actionBean.numFound > 0}">
			<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="" rows="${actionBean.rows}" numPages="${actionBean.numPages}" path="${ctx}/action/admin/activations" pagesClass="pages" />
		</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
