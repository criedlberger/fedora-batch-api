<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/myPendingItems.jsp $
   $Id: myPendingItems.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Pending Items" active="${navbarAdmin}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${myPendingItems}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="myPendingItems.header" /></h2>
		<div class="subheader"><fmt:message key="myPendingItems.description" /></div>
		<stripes:messages /><stripes:errors />
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
				<stripes:form id="sortForm" action="/action/myaccount" style="display: inline;" method="post">
					<span style="margin-left: 10px;"><stripes:label for="sort.label" />:</span>
					<stripes:select name="sortBy" onchange="$('sortForm').submit();">
						<%@ include file="/jsp/protected/workflowSortBy.jspf" %>
					</stripes:select>
					<input type="hidden" name="pendingitems" />
				</stripes:form>
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/pendingitems" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<%@ include file="/jsp/protected/itemListInfo.jspf" %>
					<div class="itemlist_actions">
						<c:choose>
						<c:when test="${fld['era.workflowState'] == 'Submit'}">
							<stripes:label for="WorkflowState.${fld['era.workflowState']}" />
						</c:when>
						<c:otherwise>
							<stripes:label for="Item.WorkflowState.${fld['era.workflowState']}" />
						</c:otherwise>
						</c:choose>
						<c:if test="${fld['era.workflowState'] != 'Review'}">
						<br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['era.workflowDate_dt']}" />
						</c:if>
						<security:secure roles="/item/update">
						<c:if test="${fld['era.workflowState'] == 'Reject'}">
						<a href="${ctx}/action/submit/editrejected/${fld['era.formName']}/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</c:if>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/pendingitems" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>