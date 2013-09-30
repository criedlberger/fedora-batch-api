<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/collection.jsp $
   $Id: collection.jsp 5606 2012-10-10 16:45:09Z pcharoen $
   $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Collection - View Collection" active="${modeNavbar}">

	<stripes:layout-component name="html-comment">
	<!-- $Id: collection.jsp 5606 2012-10-10 16:45:09Z pcharoen $ -->	
	</stripes:layout-component>

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2>${actionBean.collection.title}</h2>
			<stripes:messages /><stripes:errors />
			<ul class="item_info" style="width: 50em;">
				<li><img id="logo" class="logo" src="${ctx}/public/datastream/get/${actionBean.collection.id}/THUMBNAIL" /></li>
				<li>
					<ir:toolbarSecurity action="collection" ownerId="${actionBean.collection.ownerId}" />
					<fmt:message key="confirm.collection" var="type" />
					<ir:toolbar confirmDelete="confirmDelete('${actionBean.collection.title}', '${type}')" pid="${actionBean.collection.id}" title="${actionBean.collection.title}" action="collection" 
						createRole="${createRole}" updateRole="${updateRole}" deleteRole="${deleteRole}" toolbarClass="toolbar" />
				</li>
				<li>
					<div id="subcription" class="subscription" style="float: left;"></div>
					<script type="text/javascript">getSubscription('subcription', '${actionBean.collection.id}', <%= SubscriptionType.COLLECTION.getValue() %>);</script>
					<div style="float: left;">
						<a href="${ctx}/public/feed/collectionitems/${feedType}/${actionBean.collection.id}" target="RSS Feed" class="rssbutton"><fmt:message key="toolbar.feed.label" /></a>
						<a href="${ctx}/public/view/collection/${actionBean.collection.id}" class="browsebutton"><fmt:message key="toolbar.browse.label" /></a>
					</div>
					<div class="item_toolbar" style="float: left; padding-top: 0; margin-top: 0;">
						<ir:addThis url="${uri}" title="${actionBean.collection.title}" />
						<ir:addtoany url="${uri}" title="${actionBean.collection.title}" />
					</div>
				</li>
				<li>
					<div style="clear: left; padding-top: 1em;">
						<stripes:label for="collection.description" />${actionBean.collection.description}
					</div>
				</li>
				<li>
					<stripes:label for="collection.uri" />
					<ir:handle type="collection" pid="${actionBean.collection.id}" var="uri" /><a href="${uri}" style="text-decoration: none;">${uri}</a>
				</li>
				<li>
					<stripes:label for="collection.community" />
					<c:forEach items="${actionBean.memberOfCommunities}" var="community" varStatus="sts">
						<a href="${ctx}/public/view/community/${community.id}" style="text-decoration: none;">${community.title}</a>
					</c:forEach>
				</li>
			    <li>
				    <stripes:label for="collection.formName" />${actionBean.collection.formName}
					<p><fmt:message key="collection.formName.hint" /></p>
			    </li>
				<li>
					<img src="${ctx}/images/checkbox${actionBean.collection.ccid ? 'on' : 'off' }.gif" class="checkbox" />
					<stripes:label for="collection.ccid" />
					<p><fmt:message key="collection.ccid.hint" /></p>
				</li>
				<li>
					<img src="${ctx}/images/checkbox${actionBean.collection.approval ? 'on' : 'off' }.gif" class="checkbox" />
					<stripes:label for="collection.approval" />
					<p><fmt:message key="collection.approval.hint" /></p>
				</li>
			    <li>
					<img src="${ctx}/images/checkbox${actionBean.collection.metaDescription ? 'on' : 'off' }.gif" class="checkbox" />
				    <stripes:label for="collection.metaDescription" />
					<p><fmt:message key="collection.metaDescription.hint" /></p>
			    </li>
			    <%-- 
			    <li>
					<img src="${ctx}/images/checkbox${actionBean.collection.sortSER ? 'on' : 'off' }.gif" class="checkbox" />
				    <stripes:label for="collection.sortSER" />
					<p><fmt:message key="collection.sortSER.hint" /></p>
			    </li>
			    --%>
			    <li>
				    <stripes:label for="collection.sort" />
					<c:if test="${not empty actionBean.collection.sort}">
					    <fmt:message key="${fn:split(actionBean.collection.sort, ' ')[0]}" />
					</c:if>				    
					<p><fmt:message key="collection.sort.hint" /></p>
			    </li>
			    <li>
					<img src="${ctx}/images/checkbox${actionBean.collection.proquestUpload ? 'on' : 'off' }.gif" class="checkbox" />
				    <stripes:label for="collection.proquestUpload" />
					<p><fmt:message key="collection.proquestUpload.hint" /></p>
			    </li>
			    <li>
				    <stripes:label for="community.owner" />
					<ir:user username="${actionBean.collection.ownerId}" var="usr">
						<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a><br />
					</ir:user>
				</li>
				<li>
					<stripes:label for="collection.createdDate" />
					<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.collection.createdDate}" var="cdate" timeZone="GMT" />
					<fmt:formatDate pattern="${actionBean.dateFormat}" value="${cdate}" />
				</li>
				<li>
					<stripes:label for="collection.modifiedDate" />
					<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.collection.modifiedDate}" var="mdate" timeZone="GMT" />
					<fmt:formatDate pattern="${actionBean.dateFormat}" value="${mdate}" />
				</li>
			</ul>		
			<div style="clear: both;" />
		</div>
	</stripes:layout-component>

</stripes:layout-render>
