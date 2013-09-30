<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/public/tagBookmarks.jsp $
   $Id: tagBookmarks.jsp 5615 2012-10-16 18:20:47Z pcharoen $
   $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Bookmark" active="${0}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			function toggleFavorite(element, pid, act) {
				new Ajax.Updater($(element), '${ctx}/action/favorite/' + act + '/' + pid);
			}
		</script>
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="myBookmarks.header" /></h2>
		<div class="subheader"><fmt:message key="myBookmarks.description" /></div>
		<stripes:messages /><stripes:errors />
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />${' '}
				<fmt:message key="for" />${' '}<fmt:message key="bookmark.tag" />${' '}<em>"${actionBean.tag}"</em>${' '}
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/bookmarks" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<li class="record">
					<ir:item pid="${result['bm.pid']}" field="ifld" fields="iflds">
					<div class="itemlist_info">
                 		<h2><a href="${ctx}/public/view/item/${iresult['PID']}" class="result_title">${iresult['dc.title'][0]}</a></h2>	
						<c:if test="${not empty iresult['dc.creator']}"><p class="result_author"><stripes:label for="by" />${' '}${iresult['dc.creator'][0]}</p></c:if>
						<c:if test="${not empty result['bm.notes']}"><p class="result_others"><stripes:label for="bookmark.notes" />${' '}${fnx:trim(result['bm.notes'], 100)}</p></c:if>     
                   		<c:if test="${not empty result['bm.tags']}"><p class="result_others"><stripes:label for="bookmark.tags" /> <c:forEach items="${result['bm.tags']}" var="tag" varStatus="sts">${sts.index > 0 ? ", " : ""}<a href="${ctx}/public/tag/get/${tag}"> ${tag}</a></c:forEach></p></c:if>
					</div>
					<div class="itemlist_collection">
						<stripes:label for="item.submittedTo" /><br />
						<stripes:label for="community.label" />:
						<c:forEach items="${iresult['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
						<br /><stripes:label for="collection.label" />: 
						<c:forEach items="${iresult['rel.isMemberOfCollection']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/collection/${pid}"><ir:collection pid="${pid}" /></a></c:forEach>
					</div>
					<div class="itemlist_actions">
						<stripes:label for="bookmark.on" /><br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${result['bm.createdDate_dt']}" /><br />
						<span id="count.${status.index}"></span>
						<script type="text/javascript">
							new Ajax.Updater('count.${status.index}', '${ctx}/public/tag/getTagCountByPid', { 
								parameters: { pid: '${result["bm.pid"]}' } 
							});
						</script>
						<c:set var="next" value="${ctx}/action/myaccount/bookmarks/${actionBean.start}/${actionBean.sortBy}" />
						<a href="${ctx}/action/bookmark/edit/${result['bm.id']}/${result['bm.pid']}?next=${fnx:encodeUrl(next)}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a><br />
						<div id="bookmark.${status.index}" class="favorite" style="clear: both; margin-left: 0.2em;"></div>
						<script type="text/javascript">
							new Ajax.Updater('bookmark.${status.index}', '${ctx}/action/bookmark/getBookmarkStatus', { 
								parameters: { pid: '${result['bm.pid']}' } 
							});
						</script> 
					</div>
					</ir:item>
				</li>
			</c:forEach>
		</ol>
		<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/bookmarks" pagesClass="pagesbot" />
			</div>
		</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
