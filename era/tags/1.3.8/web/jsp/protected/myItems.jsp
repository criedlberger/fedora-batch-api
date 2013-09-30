<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.8/web/jsp/protected/myItems.jsp $
   $Id: myItems.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Items" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="myItems.header" /></h2>
		<div class="subheader"><fmt:message key="myItems.description" /></div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
	
				<stripes:form id="sortForm" action="/action/myaccount" style="display: inline;" method="get">
					<span style="margin-left: 10px;"><stripes:label for="sort.label" />:</span>
					<stripes:select name="sortBy" onchange="$('sortForm').submit();">
						<%@ include file="/jsp/protected/myAccountSortBy.jspf"%>
					</stripes:select>
					<input type="hidden" name="items" />
				</stripes:form>
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/items" pagesClass="pagesbot" />
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
						<stripes:label for="WorkflowState.Archive" /><br />
						<c:choose>
						<c:when test="${not empty fld['era.workflowDate_dt']}">
							<fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['era.workflowDate_dt']}" />
						</c:when>
						<c:otherwise>
							<fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['fo.createdDate_dt']}" />
						</c:otherwise>
						</c:choose>
						<c:if test="${fld['era.embargoed_b']}">
							<br /><stripes:label for="properties.embargoed" />${' '}
							<fmt:formatDate pattern="${actionBean.dateFormatShort}" value="${fld['era.embargoedDate_dt']}" />
						</c:if>
						<security:secure roles="/item/update">
						<c:choose>
						<c:when test="${fld['era.approval_b']}">
						<security:secure roles="/admin/item">
							<a href="${ctx}/action/submit/edit/${fld['era.formName']}/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</security:secure>
						</c:when>
						<c:otherwise>
							<a href="${ctx}/action/submit/edit/${fld['era.formName']}/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</c:otherwise>
						</c:choose>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
		<c:if test="${actionBean.numFound > 0}">
		<div class="pages pages_bottom">
			<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
				numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/items" pagesClass="pagesbot" />
		</div>
		</c:if>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
