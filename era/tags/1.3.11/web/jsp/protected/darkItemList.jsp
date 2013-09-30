<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/protected/darkItemList.jsp $
   $Id: darkItemList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Dark Items" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="admin.dark.header" /></h2>
		<div class="subheader"><fmt:message key="admin.dark.description" /></div>
		<stripes:messages /><stripes:errors />
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="item.unit" />
				<stripes:form id="sortForm" action="/action/admin" style="display: inline;" method="post">
					<span style="margin-left: 10px;"><stripes:label for="sort.label" />:</span>
					<stripes:select name="sortBy" onchange="$('sortForm').submit();">
						<%@ include file="/jsp/public/searchSortBy.jspf" %>
					</stripes:select>
					<input type="hidden" name="dark" />
				</stripes:form>
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/dark" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<div class="itemlist_info">
                 		<h2><a href="${ctx}/public/view/item/${fld['PID']}" class="result_title">${fld['dc.title']}</a></h2>	
						<c:if test="${not empty fld['dc.creator']}"><p class="result_author"><strong><fmt:message key="by" />${' '} </strong> ${fld['dc.creator']}</p></c:if>    
                   		<c:if test="${not empty fld['dc.description']}"><p class="result_others">${fnx:trim(fld['dc.description'], 120)}</p></c:if>
					</div>
					<div class="itemlist_collection">
						<stripes:label for="item.submittingTo" /><br />
						<stripes:label for="community.label" />:
						<c:forEach items="${flds['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
						<c:if test="${not empty flds['rel.isMemberOfCollection']}">
						<br /><stripes:label for="collection.label" />: 
						<c:forEach items="${flds['rel.isMemberOfCollection']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/collection/${pid}"><ir:collection pid="${pid}" /></a></c:forEach>
						</c:if>
						<c:if test="${not empty flds['thesis.degree.discipline']}">
						<br /><stripes:label for="department.label" />:
						<c:forEach items="${flds['thesis.degree.discipline']}" var="dept" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href='${ctx}/public/search?fq=facet.department:"${dept}"'>${dept}</a></c:forEach>
						</c:if>
					</div>
					<div class="itemlist_actions">
						<stripes:label for="item.submittedBy" /><br />
						<ir:user username="${fld['era.submitterId']}" var="usr">
							<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a><br />
						</ir:user>
						<fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['era.workflowDate_dt']}" />
						<security:secure roles="/admin/dark">
						<a href="${ctx}/action/submit/edit/${fld['era.formName']}/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
		<c:if test="${actionBean.numFound > 0}">
		<div class="pages pages_bottom">
			<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
				numPages="${actionBean.numPages}" path="${ctx}/action/admin/dark" pagesClass="pagesbot" />
		</div>
		</c:if>
		</div>
		<div style="clear: both;" />
    </stripes:layout-component>
</stripes:layout-render>
