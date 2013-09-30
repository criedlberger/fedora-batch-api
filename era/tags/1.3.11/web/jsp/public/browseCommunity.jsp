<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/public/browseCommunity.jsp $
   $Id: browseCommunity.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Community - View Communities" active="${navbarCommunity}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			function subscribeInfo(element, pid, type) {
				<c:choose>
				<c:when test="${empty user}">
					var next = '/public/browse/community';
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
			<h2><fmt:message key="community.header" /></h2>
			<div class="subheader">
				<c:if test="${actionBean.resultRows > 0}">
					<b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b>${' '}
					<fmt:message key="of" />${' '}<b>${actionBean.numFound}</b> <fmt:message key="community.unit"/>
					<br /><fmt:message key="community.subheader" />
				</c:if>
				<c:if test="${actionBean.resultRows == 0}">
					<stripes:label for="searchResult.noDataFound" class="no_result" />
				</c:if>
			</div>
			<stripes:errors /><stripes:messages />

			<div class="subheader">
				<security:secure roles="/community/create">
			 		<a href="${ctx}/action/community/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcommunity.label" /></a>
				</security:secure>
				<security:secure roles="/collection/create">
		 			<a href="${ctx}/action/collection/preCreate" class="add_button auto_width"><fmt:message key="toolbar.addcollection.label" /></a>
				</security:secure>
	 		</div>
			
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
				<div class="a_community">
					<div class="title">
						<fmt:message key="browse.community" var="title" />
	                    <a id="${fn:toUpperCase(fn:substring(fld['dc.title'], 0, 1))}" href="${ctx}/public/view/community/${fld['PID']}" title="${title}">${fld['dc.title']}</a>
                    </div>
                   	<div class="toolbar">
	                    <div id="subcription.${status.index}" class="subscription"></div>
	                    <script type="text/javascript">
	                    	getSubscriptionInfo('subcription.${status.index}', '${fld["PID"]}', <%= SubscriptionType.COMMUNITY.getValue() %>);
	                    </script>
	                    <a href="${ctx}/public/feed/communityitems/${feedType}/${fld['PID']}" target="_blank" class="rssbutton">
	                    	<fmt:message key="toolbar.feed.label" />
	                    </a>
	                    <link rel="alternate" type="application/rss+xml" title="${fld['dc.title']}" href="${ctx}/public/feed/communityitems/${feedType}/${fld["PID"]}" /> 
                    </div>
                </div>
			</c:forEach>
			<div style="clear: both;" />
		</div>
    </stripes:layout-component>
</stripes:layout-render>
