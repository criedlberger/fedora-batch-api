<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/mySavedItems.jsp $
   $Id: mySavedItems.jsp 5606 2012-10-10 16:45:09Z pcharoen $
   $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Saved Items" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="mySavedItems.header" /></h2>
		<div class="subheader"><fmt:message key="mySavedItems.description" /></div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
	
				<stripes:form id="sortForm" action="/action/myaccount" style="display: inline;">
					<span style="margin-left: 10px;"><stripes:label for="sort.label" />:</span>
					<stripes:select name="sortBy" onchange="$('sortForm').submit();" value="${actionBean.sortBy}">
						<%@ include file="/jsp/public/searchSortBy.jspf" %>
					</stripes:select>
					<input type="hidden" name="saveditems" />
				</stripes:form>
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/saveditems" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<stripes:messages /><stripes:errors />
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<li class="record">
					<%@ include file="/jsp/protected/itemListInfo.jspf" %>
					<div class="itemlist_actions">
						<stripes:label for="item.createdDate" /><br />
						<fmt:formatDate pattern="${actionBean.dateFormat}" value="${result['fo.createdDate_dt']}" /><br />
						<a href="${ctx}/action/submit/editsaved/${result['era.formName']}/${result['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/saveditems" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
		<fmt:message key="button.yes" var="yes" />
		<fmt:message key="button.no" var="no" />
		<div id="confirmRemove" style="display: none; text-align: center;">
			<div id="prompt" style="margin: 1em 1em 1em 1em;">
			</div>
			<div>
				<input type="button" class="save_button" style="margin-left: 0em; height: 2em; width: 3em;" onclick="Modalbox.hide(); bigWaiting(); location.href = $('removeItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" style="margin-left: 1em; height: 2em; width: 3em;" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
