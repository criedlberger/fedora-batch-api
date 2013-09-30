<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/mySubscriptions.jsp $
   $Id: mySubscriptions.jsp 5606 2012-10-10 16:45:09Z pcharoen $
   $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Subscriptions" active="0">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			function toggleFavorite(element, pid, act) {
				new Ajax.Updater($(element), '${ctx}/action/favorite/' + act + '/' + pid);
			}
		</script>
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<h2><stripes:label for="mySubscriptions.header" /></h2>
		<div class="subheader"><fmt:message key="mySubscriptions.description" /></div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: 
				<b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/subscriptions" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<stripes:messages /><stripes:errors />
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.subscriptionList}" var="result" varStatus="status">
				<li class="record">
					<div class="itemlist_info record_info">
					<c:choose>
					<c:when test="${result['sub.type'] == 0}">
						<!-- community subscription -->
						<h2><a href="${ctx}/public/view/community/${result['sub.pid']}" class="result_title">${result['sub.title']}</a></h2>
                   		<c:if test="${not empty result['sub.description']}"><p class="result_subject">${fnx:trim(result['sub.description'][0], 120)}</p></c:if>
					</c:when>
					<c:when test="${result['sub.type'] == 1}">
						<!-- collection subscription -->
						<h2><a href="${ctx}/public/view/collection/${result['sub.pid']}" class="result_title">${result['sub.title']}</a></h2>
                   		<c:if test="${not empty result['sub.description']}"><p class="result_subject">${fnx:trim(result['sub.description'][0], 120)}</p></c:if>
					</c:when>
					<c:when test="${result['sub.type'] == 2}">
						<!-- researcher subscription -->
						<h2><a href="${ctx}/public/view/author/${result['sub.pid']}" class="result_title">${result['sub.title']}</a></h2>
                   		<c:if test="${not empty result['sub.description']}"><p class="result_subject">${fnx:trim(result['sub.description'][0], 120)}</p></c:if>
					</c:when>
					<c:when test="${result['sub.type'] == 3}">
						<!-- task subscription -->
						<h2><a href="${ctx}/public/view/collection/${result['sub.pid']}" class="result_title">${result['sub.title']}</a></h2>
                   		<c:if test="${not empty result['sub.description']}"><p class="result_subject">${fnx:trim(result['sub.description'][0], 120)}</p></c:if>
					</c:when>
					</c:choose>
					</div>
					<div class="itemlist_actions record_actions">
						<div><fmt:message key="mySubscriptions.type.label" />: <b><fmt:message key="mySubscriptions.type.${result['sub.type']}" /></b></div>
						<div id="subcription.${status.index}" class="subscription"></div>
						<script type="text/javascript">getSubscriptionWithNotify('subcription.${status.index}', '${result["sub.pid"]}', ${result["sub.type"]});</script>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages pages_bottom">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/subscriptions" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
