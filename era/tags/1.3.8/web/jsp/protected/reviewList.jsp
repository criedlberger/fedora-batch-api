<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.8/web/jsp/protected/reviewList.jsp $
   $Id: reviewList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - My Tasks" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="admin.review.header" /></h2>
		<div class="subheader"><fmt:message key="admin.review.description" /></div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
				<stripes:form id="sortForm" action="/action/admin" style="display: inline;" method="post">
					<span style="margin-left: 10px;"><stripes:label for="sort.label" />:</span>
					<stripes:select name="sortBy" onchange="$('sortForm').submit();">
						<%@ include file="/jsp/protected/workflowSortBy.jspf" %>
					</stripes:select>
					<input type="hidden" name="review" />
				</stripes:form>
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/approval" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<stripes:messages /><stripes:errors />
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<%@ include file="/jsp/protected/itemListInfo.jspf" %>
					<div class="itemlist_actions">
						<stripes:label for="item.submittedBy" /><br />
						<ir:user username="${fld['era.submitterId']}" var="usr">
						<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a><br />
						</ir:user>
						<stripes:label for="WorkflowState.${fld['era.workflowState']}" />${' '}
						<br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['era.workflowDate_dt']}" />
						<security:secure roles="/admin/approve">
						<c:if test="${fld['era.workflowState'] == 'Review'}">
							<a href="${ctx}/action/approval/review/${fld['PID']}" class="edit_button"  onclick="bigWaiting(); return true;"><fmt:message key="button.review" /></a>
						</c:if>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages pages_bottom">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/approval" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
