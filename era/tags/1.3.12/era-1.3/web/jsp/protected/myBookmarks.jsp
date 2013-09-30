<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/protected/myBookmarks.jsp $
   $Id: myBookmarks.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
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
				<fmt:message key="item.unit" />
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
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<ir:item pid="${fld['bm.pid']}" field="ifld" fields="iflds">
					<div class="itemlist_info">
                 		<h2><a href="${ctx}/public/view/item/${ifld['PID']}" class="result_title">${ifld['dc.title']}</a></h2>	
						<c:if test="${not empty ifld['dc.creator']}"><p class="result_author"><stripes:label for="by" />${' '}${ifld['dc.creator']}</p></c:if>
						<c:if test="${not empty fld['bm.notes']}"><p class="result_others"><stripes:label for="bookmark.notes" />${' '}${fnx:trim(fld['bm.notes'], 100)}</p></c:if>     
                   		<c:if test="${not empty fld['bm.tags']}"><p class="result_others"><stripes:label for="bookmark.tags" /> <c:forEach items="${flds['bm.tags']}" var="tag" varStatus="sts">${sts.index > 0 ? ", " : ""}<a href="${ctx}/public/tag/get/${tag}"> ${tag}</a></c:forEach></p></c:if>
					</div>
					<div class="itemlist_collection">
						<stripes:label for="item.submittedTo" /><br />
						<stripes:label for="community.label" />:
						<c:forEach items="${iflds['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
						<br /><stripes:label for="collection.label" />: 
						<c:forEach items="${iflds['rel.isMemberOfCollection']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/collection/${pid}"><ir:collection pid="${pid}" /></a></c:forEach>
					</div>
					<div class="itemlist_actions">
						<stripes:label for="bookmark.on" /><br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['bm.createdDate_dt']}" /><br />
						<span id="count.${status.index}"></span>
						<script type="text/javascript">
							new Ajax.Updater('count.${status.index}', '${ctx}/public/tag/getTagCountByPid', { 
								parameters: { pid: '${fld["bm.pid"]}' } 
							});
						</script>
						<c:set var="next" value="${ctx}/action/myaccount/bookmarks/${actionBean.start}/${actionBean.sortBy}" />
						<a href="${ctx}/action/bookmark/edit/${fld['bm.id']}/${fld['bm.pid']}?next=${fnx:encodeUrl(next)}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a><br />
						<div id="bookmark.${status.index}" class="favorite" style="clear: both; margin-left: 0.2em;"></div>
						<script type="text/javascript">
							new Ajax.Updater('bookmark.${status.index}', '${ctx}/action/bookmark/getBookmarkStatus', { 
								parameters: { pid: '${fld['bm.pid']}' } 
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
		<div style="clear: both;" />
    </stripes:layout-component>
</stripes:layout-render>
