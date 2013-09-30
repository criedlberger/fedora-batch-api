<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/collection.jsp $
   $Id: collection.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ page import="java.net.URLEncoder" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Collection - ${actionBean.collection.title}" active="${navbarCommunity}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				// getDownloadCount();
			});
			function getDownloadCount() {
				new Ajax.Updater($('download'), ctx + '/public/datastream/getDownloadCountByCollection/${actionBean.collection.id}');
			}
			function subscribe(element, pid, type) {
				<c:choose>
				<c:when test="${empty user}">
					var next = '/public/view/collection/${actionBean.collection.id}';
					location.href = '${ctx}/action/subscription/subscribe/' + pid + '/' + type + '?next=' + next;
				</c:when>
				<c:otherwise>
					new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
						parameters: { 'subscription.pid': pid, 'subscription.type': type }
					});
				</c:otherwise>
				</c:choose>
			}		
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2 class="collections">${actionBean.collection.title}</h2>
        	<div class="collections_toolbar">
				<div id="subscription.collection" class="subscription"></div>
				<script type="text/javascript">
					getSubscription('subscription.collection', '${actionBean.collection.id}', <%= SubscriptionType.COLLECTION.getValue() %>);
				</script>
				<a href="${ctx}/public/feed/collectionitems/${feedType}/${actionBean.collection.id}" target="_blank" class="rssbutton">
					<fmt:message key="toolbar.feed.label" />
				</a>
				<link rel="alternate" type="application/rss+xml" title="${actionBean.collection.title}" href="${ctx}/public/feed/collectionitems/${feedType}/${actionBean.collection.id}" /> 
				<c:set var="title" value='"${actionBean.collection.title}"' />
				<a href="${ctx}/public/search?fq=facet.collection:${fnx:encodeUrl(title)}" title="Browse Collection Items" class="browsebutton">
					<fmt:message key="toolbar.browse.label" />
				</a>
				<c:if test="${actionBean.collection.approval}">
				<security:secure roles="/admin/approve">
				<br />
				<div id="subscription.reviewer" class="subscription reviewer"></div>
				<script type="text/javascript">
					getSubscription('subscription.reviewer', '${actionBean.collection.id}', <%= SubscriptionType.TASK.getValue() %>);
				</script>
				</security:secure>
				</c:if>
				<div class="item_toolbar">
					<ir:handle var="uri" type="community" pid="${actionBean.collection.id}" />
					<ir:addThis url="${uri}" title="${actionBean.collection.title}" />
				</div>
			</div>
			<div class="description">
				<div class="thumb">
					<img src="${ctx}/public/datastream/get/${actionBean.collection.id}/THUMBNAIL" title="Browse Collection Items" onclick="location.href = '${ctx}/public/search?fq=facet.collection:${com}';" />
				</div>
				${actionBean.collection.description}
			</div>
			<div class="description">
				<div id="download" style="height: 1em; margin-bottom: 0.5em;"></div>
				<stripes:label for="collection.uri" />${': '}<ir:handle type="item" pid="${actionBean.collection.id}" var="uri" /><a href="${uri}">${uri}</a><br />
				<stripes:label for="community.label" />:
				<strong>
					<c:forEach items="${actionBean.communities}" var="com" varStatus="sts">
						${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${com.id}">${com.title}</a>
					</c:forEach>
				</strong>
			</div>
			<div class="subheader">
				<security:secure roles="/community/create,/collection/create">
			 		<a href="${ctx}/action/community/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcommunity.label" /></a>
		 			<a href="${ctx}/action/collection/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcollection.label" /></a>
				</security:secure>
				<security:secure roles="/item/create">
					<c:if test="${not empty actionBean.collection.formName}">
			 			<a href="${ctx}/action/submit/init/${actionBean.collection.formName}/${actionBean.collection.id}" 
			 				class="add_button auto_width"><fmt:message key="${actionBean.collection.approval ? 'toolbar.submitto.label' : 'toolbar.depositto.label'}" /></a>
					</c:if>
		 		</security:secure>
	 		</div>
			 <h2 class="collectionlist">
				<fmt:message key="collection.items.in" />${' '}<em>${actionBean.collection.title}</em>
			</h2>
		</div>
		<c:set var="q" value="${fnx:encodeUrl(actionBean.q)}" />
		<c:set var="fq" value="${fnx:encodeUrl(actionBean.fq)}" />
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: 
				<strong>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</strong> - <strong>${actionBean.start + actionBean.resultRows}</strong>
				${' '}<fmt:message key="of" />${' '}<strong>${actionBean.numFound}</strong>
			</c:if>
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			${' '}<ir:formatQuery q="${actionBean.q}" fq="${actionBean.fq}" />
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:form id="sortForm" action="/public/collection" style="display: inline;">
					<span class="sorter"><stripes:label for="sort.label" />:
					<stripes:select id="sort" name="sort" onchange="location.href = '${ctx}/public/view/collection/${actionBean.collection.id}?q=${q}&fq=${fq}&sort=' + $('sort').value;" value="${actionBean.sort}">
						<%@ include file="/jsp/public/searchSortBy.jspf" %>
						<c:if test="${fn:startsWith(actionBean.collection.sort, 'sort.ser')}">
							<stripes:option value="sort.ser asc"><fmt:message key="sort.ser" /></stripes:option>
						</c:if>
						<c:if test="${fn:startsWith(actionBean.collection.sort, 'sort.trid')}">
							<stripes:option value="sort.trid asc"><fmt:message key="sort.trid" /></stripes:option>
						</c:if>
					</stripes:select>
					</span>
				</stripes:form>
			</c:if>
		  	<c:if test="${actionBean.numFound > 0}">
		  		<div class="pages">
					<ir:searchPages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sort}" rows="${actionBean.rows}"	
						numPages="${actionBean.numPages}" path="?q=${q}&fq=${fq}" pagesClass="pagesbot" />
				</div>
			</c:if>
        </div>
		<c:if test="${event == 'search'}">
			<div><stripes:messages /><stripes:errors /></div>
		</c:if>
		<%@ include file="/jsp/public/searchResults.jspf" %>
	</stripes:layout-component>
</stripes:layout-render>
