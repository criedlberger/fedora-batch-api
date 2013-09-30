<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/protected/myAccountHome.jsp $
   $Id: myAccountHome.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Home" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><fmt:message key="myaccount.home.header" /></h2>
		<p><fmt:message key="myaccount.home.description" /></p>
		<ul class="myaccount_info">
			<fmt:message key="myaccount.sidebar.count" var="count" />
			<c:set var="n" value="0" />
			<c:forEach begin="1" end="${count}" var="i" varStatus="status">
				<fmt:message key="myaccount.sidebar.${i}.roles" var="roles" />
				<c:set var="enabled" value="false" />
				<security:secure roles="${roles}">
					<c:set var="enabled" value="true" />
				</security:secure>
				<c:if test="${empty roles || enabled}">
					<c:set var="n" value="${n + 1}" />
					${n % 2 == 1 ? '<li>' : ''}
						<div>
							<fmt:message key="myaccount.sidebar.${i}.id" var="id" />
							<span class="count">${actionBean.itemCounts[id] > 0 ? actionBean.itemCounts[id] : ''}</span>
							<span class="desc">
								<fmt:message key="myaccount.sidebar.${i}.url" var="url" />
								<h3><a href="${ctx}${url}"><fmt:message key="myaccount.sidebar.${i}.name" /></a></h3>
								<fmt:message key="myaccount.sidebar.${i}.title" />
							</span>
						</div>
					${n % 2 == 0 ? '</li>' : ''}
				</c:if>
			</c:forEach>
			${n % 2 == 1 ? '</li>' : ''}
			<li><div class="last"></div></li>
		</ul>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
