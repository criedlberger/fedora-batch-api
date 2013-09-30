<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/protected/myFavorites.jsp $
   $Id: myFavorites.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page import="ca.ualberta.library.ir.action.MyAccountActionBean" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage You Account" active="${navbarMyAccount}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			function toggleFavorite(element, pid, act) {
				new Ajax.Updater($(element), '${ctx}/action/favorite/' + act + '/' + pid);
			}
		</script>
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="myaccount.sidebar" active="6" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><a href="#"><stripes:label for="myFavorites.header" /></a></h2>
		<div class="subheader">
		<c:if test="${actionBean.resultRows > 0}">
			<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b> of <b>${actionBean.numFound}</b> <fmt:message key="item.unit"/>
			<span style="font-size: 11px;"> (<b>${actionBean.QTime / 1000}</b> seconds.)</span>
		</c:if>
		<c:if test="${actionBean.resultRows == 0}">
			<stripes:label for="searchResult.noDataFound" class="no_result" />
		</c:if>
		</div>

		<stripes:messages /><stripes:errors />

		<table class="display" style="width: 100%;">
		
			<ir:toolbarSecurity action="item" />
			
			<c:forEach items="${actionBean.favoriteList}" var="result" varStatus="status">
			<c:set var="fld" value="${result.fieldValueMap}" />
			<c:set var="flds" value="${result.fieldValuesMap}" />
			<c:set var="type" value="${fld['fo.contentModel'] == 'COMMUNITY' ? 'community' : fld['fo.contentModel'] == 'COLLECTION' ? 'collection' : 'item'}" />
			<tr class="${status.count mod 2 == 0 ? "even" : "odd"}">
				<ir:listItem status="${status}" fld="${fld}" flds="${flds}" />
				<td class="toolbox" style="width: 140px;">
					<div>
						<span id="favorite.${status.index}" class="toolbar" style="font-weight: normal;"></span>						
						<script type="text/javascript">new Ajax.Updater('favorite.${status.index}', '${ctx}/action/favorite/getFavoriteStatus', { parameters: { pid: '${fld["PID"]}' } });</script>
						<span id="bookmark.${status.index}"></span>					
						<script type="text/javascript">new Ajax.Updater('bookmark.${status.index}', '${ctx}/action/bookmark/getBookmarkStatus', { parameters: { pid: '${fld["PID"]}', mode: 'myaccount', next: '${fnx:encodeUrl("/action/myaccount/favorites/" + actionBean.start)}' } });</script>
					</div>
					<div style="font-size: 11px;"><fmt:message key="searchResult.createdDate" />: <fmt:formatDate pattern="MMM d, yyyy" value="${fld['fo.createdDate_dt']}" /></div>
				</td>
			</tr>
			</c:forEach>
		</table>
		<c:if test="${actionBean.numFound > 0}">
			<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}" numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/favorites" pagesClass="pages" />
		</c:if>
		</div>
		<div style="clear: both;" />
    </stripes:layout-component>
</stripes:layout-render>
