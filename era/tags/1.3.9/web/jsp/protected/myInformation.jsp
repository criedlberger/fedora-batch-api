<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/protected/myInformation.jsp $
   $Id: myInformation.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage You Account" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
		<ir:sidebar name="myaccount.sidebar" active="1" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="myInformation.header" /></h2>
		<div class="subheader"><fmt:message key="myInformation.description" /></div>

		<stripes:errors /><stripes:messages />
		<c:set var="user" value="${actionBean.context.user}" />
		<ul class="item_info" style="width: 50em;">
			<security:secure roles="/user/information/update">
			<li>
				<a href="${ctx}/action/myaccount/information/edit" class="edit_button auto_width"><fmt:message key="toolbar.edit.information.label" /></a>
				<br style="clear: both;" />
			</li>
			</security:secure>
			<li><stripes:label for="user.firstName" />${': '}${user.firstName}</li>
			<li><stripes:label for="user.lastName" />${': '}${empty user.lastName ? ' ' : user.lastName}</li>
			<li><stripes:label for="user.username" />${': '}${user.username}</li>
			<li><stripes:label for="user.email" />${': '}${user.email}</li>
			<security:secure roles="/user/ccid/association">
				<c:if test="${not empty user.ccid}">
					<li>
						<stripes:label for="user.ccid" />${': '}${actionBean.ccid} 
						<p><fmt:message key="user.ccid.help" /></p>
					</li>
				</c:if>
			</security:secure>
			<li><stripes:label for="user.language" />:&nbsp;<fmt:message key="Language.${actionBean.user.language}" /></li>
		</ul>
		</div>
		<div style="clear: both;" />
	</stripes:layout-component>
</stripes:layout-render>
