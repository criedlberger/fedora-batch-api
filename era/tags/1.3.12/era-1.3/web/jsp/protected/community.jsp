<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/protected/community.jsp $
   $Id: community.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ include file="/jsp/public/mode.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Community - View Community" active="${modeNavbar}">

	<stripes:layout-component name="html-comment">
	<!-- $Id: community.jsp 5427 2012-07-12 20:30:12Z pcharoen $ -->	
	</stripes:layout-component>

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2>${actionBean.community.title}</h2>
			<ul class="item_info" style="width: 50em;">
				<li><img id="logo" class="logo" src="${ctx}/public/datastream/get/${actionBean.community.id}/THUMBNAIL" /></li>
				<li>
					<ir:toolbarSecurity action="community" ownerId="${actionBean.community.ownerId}" />
					<fmt:message key="confirm.bookmark" var="type" />
					<ir:toolbar confirmDelete="confirmDelete('${actionBean.community.title}', '${type}')" pid="${actionBean.community.id}" action="community" title="${actionBean.community.title}" 
						createRole="${createRole}" updateRole="${updateRole}" deleteRole="${deleteRole}" toolbarClass="toolbar" urlSuffix="/${actionBean.mode}" />
				</li>
				<li>
					<div id="subcription" class="subscription" style="float: left;"></div>
					<script type="text/javascript">getSubscription('subcription', '${actionBean.community.id}', <%= SubscriptionType.COMMUNITY.getValue() %>);</script>
					<div style="float: left;">
						<a href="${ctx}/public/feed/communityitems/${feedType}/${actionBean.community.id}" target="RSS Feed" class="rssbutton"><fmt:message key="toolbar.feed.label" /></a>
						<a href="${ctx}/public/collection/view/${actionBean.community.id}" class="browsebutton"><fmt:message key="toolbar.browse.label" /></a>
					</div>
					<div class="item_toolbar" style="float: left; padding-top: 0; margin-top: 0;">
						<ir:addThis url="${uri}" title="${actionBean.community.title}" />
						<ir:addtoany url="${uri}" title="${actionBean.community.title}" />
					</div>
				</li>
				<li>
					<div style="clear: left; padding-top: 1em;">
						<stripes:label for="community.description" />${': '}${actionBean.community.description}
					</div>
				</li>
				<li>            	
		            <stripes:label for="community.uri" />${': '}
					<ir:handle type="community" pid="${actionBean.community.id}" var="uri" /><a href="${uri}">${uri}</a>
				</li>
				<security:secure roles="/admin/community">
			    <c:forEach items="${actionBean.partOfList}" var="partOf" varStatus="status">
				<c:if test="${status.index == 0}"><li><stripes:label for="community.properties" />:</li></c:if>
			    <c:if test="${partOf != 'DARK_REPOSITORY' && partOf != 'EMBARGOED'}">
			    <li>
					<img src="${ctx}/images/checkbox${actionBean.partOfs[partOf.value] ? 'on' : 'off' }.gif" style="margin-right: 2px; vertical-align: text-bottom;" />
					<fmt:message key="PartOfRelationship.${partOf}" />
			    </li>
				</c:if>
			    </c:forEach>
			    </security:secure>
			    <li>
				    <stripes:label for="community.owner" />${': '}
					<ir:user username="${actionBean.community.ownerId}" var="usr">
						<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a><br />
					</ir:user>
				</li>
				<li>
					<stripes:label for="community.createdDate" />${': '}
					<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.community.createdDate}" var="cdate" timeZone="GMT" />
					<fmt:formatDate pattern="${actionBean.dateFormat}" value="${cdate}" />
				</li>
				<li>
					<stripes:label for="community.modifiedDate" />${': '}
					<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.community.modifiedDate}" var="mdate" timeZone="GMT" />
					<fmt:formatDate pattern="${actionBean.dateFormat}" value="${mdate}" />
				</li>
			</ul>
			<div style="clear: both;" />
		</div>
	</stripes:layout-component>

</stripes:layout-render>
