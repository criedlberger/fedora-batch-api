<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/community.jsp $
   $Id: community.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Community - ${actionBean.community.title}" active="${navbarCommunity}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				// getDownloadCount();
			});
			function getDownloadCount() {
				new Ajax.Updater($('download'), ctx + '/public/datastream/getDownloadCountByCommunity/${actionBean.community.id}');
			}
			function subscribeInfo(element, pid, type) {
				<c:choose>
				<c:when test="${empty user}">
					var next = '/public/view/community/${actionBean.community.id}';
					location.href = '${ctx}/action/subscription/subscribeInfo/' + pid + '/' + type + '?next=' + next;
				</c:when>
				<c:otherwise>
					new Ajax.Updater($(element), ctx + '/action/subscription/subscribeInfo', {
						parameters: { 'subscription.pid': pid, 'subscription.type': type }
					});
				</c:otherwise>
				</c:choose>
			}		
		</script>
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<stripes:errors /><stripes:messages />
			<h2 class="collections">${actionBean.community.title}</h2>
        	<div class="collections_toolbar">
				<div id="subcription.community" class="subscription"></div>
				<script type="text/javascript">
					getSubscription('subcription.community', '${actionBean.community.id}', <%= SubscriptionType.COMMUNITY.getValue() %>);
				</script>
				<a href="${ctx}/public/feed/communityitems/${feedType}/${actionBean.community.id}" target="_blank" class="rssbutton">
					<fmt:message key="toolbar.feed.label" />
				</a>
				<link rel="alternate" type="application/rss+xml" title="${actionBean.community.title}" href="${ctx}/public/feed/communityitems/${feedType}/${actionBean.community.id}" /> 
				<c:set var="title" value='"${actionBean.community.title}"' />
				<a href="${ctx}/public/search?fq=facet.community:${fnx:encodeUrl(title)}" title="Browse Community Items" class="browsebutton">
					<fmt:message key="toolbar.browse.label" />
				</a>
				<div class="item_toolbar">
					<ir:handle type="community" pid="${actionBean.community.id}" var="uri" />
					<ir:addThis url="${uri}" title="${actionBean.community.title}" />
				</div>
			</div>
			<div class="description">
				<div class="thumb">
					<img src="${ctx}/public/datastream/get/${actionBean.community.id}/THUMBNAIL" title="Browse Community Items" onclick="location.href = '${ctx}/public/search?fq=facet.community:${com}';" />
				</div>
				${actionBean.community.description}
			</div>
			<div class="description">
				<div id="download" style="height: 1em; margin-bottom: 0.5em;"></div>
				<stripes:label for="community.uri" />${': '}<ir:handle type="item" pid="${actionBean.community.id}" var="uri" /><a href="${uri}">${uri}</a>
			</div>

			<security:secure roles="/community/create,/collection/create">
			<div class="subheader">
		 		<a href="${ctx}/action/community/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcommunity.label" /></a>
	 			<a href="${ctx}/action/collection/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcollection.label" /></a>
	 		</div>
			</security:secure>

			<h2 class="collectionlist">
				<fmt:message key="community.collections.title" />${' '}<em>${actionBean.community.title}</em>
			</h2>
			
			<div class="initial_link" style="clear: both;">
			<c:forEach items="${actionBean.initialList}" var="name" varStatus="status">
				<c:if test="${actionBean.initialMap[name]}">
					<a href="#${name}">${name}</a>
				</c:if>
				<c:if test="${!actionBean.initialMap[name]}">
					<span>${name}</span>
				</c:if>
			</c:forEach>
			</div>

			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
			<c:set var="fld" value="${result.fieldValueMap}" />
			<c:set var="flds" value="${result.fieldValuesMap}" />
			<div class="a_collection">
				<div class="title">
					<a id="${fn:toUpperCase(fn:substring(fld['dc.title'], 0, 1))}" href="${ctx}/public/view/collection/${fld['PID']}" class="result_title">${fld['dc.title']}</a>
				</div>
				<div class="toolbar">
					<div id="subcription.${status.index}" class="subscription"></div>
					<script type="text/javascript">
						getSubscriptionInfo('subcription.${status.index}', '${fld["PID"]}', <%= SubscriptionType.COLLECTION.getValue() %>);
					</script>
					<div>
						<a href="${ctx}/public/feed/collectionitems/${feedType}/${fld['PID']}" target="_blank" class="rssbutton">
							<fmt:message key="toolbar.feed.label" />
						</a>
						<link rel="alternate" type="application/rss+xml" title="${fld['dc.title']}" href="${ctx}/public/feed/collectionitems/${feedType}/${fld["PID"]}" /> 
					</div>
				</div>
			</div>
			</c:forEach>
			<div style="clear: both;" />
		</div>
    </stripes:layout-component>
</stripes:layout-render>