<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/myCommunities.jsp $
   $Id: myCommunities.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Account - Manage Your Communities" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="myCommunities.header" /></h2>
		<div class="subheader">
			<fmt:message key="myCommunities.description" />
			<security:secure roles="/community/create">
			<a href="${ctx}/action/community/preCreate" class="add_button auto_width" style="float: right; margin-right: 3em;"><fmt:message key="toolbar.addcommunity.label" /></a>
			</security:secure>
		</div>
		<stripes:messages /><stripes:errors />
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="community.unit" />
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/communities" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<c:set var="fld" value="${result.fieldValueMap}" />
				<c:set var="flds" value="${result.fieldValuesMap}" />
				<li class="record">
					<div class="itemlist_info record_info">
                 		<h2><a href="${ctx}/action/community/view/${fld['PID']}" class="result_title">${fld['dc.title']}</a></h2>	
                   		<c:if test="${not empty fld['dc.description']}"><p class="result_subject">${fnx:trim(fld['dc.description'], 200)}</p></c:if>
					</div>
					<div class="itemlist_actions record_actions">
						<stripes:label for="community.owner" /><br />
						<ir:user username="${fld['fo.ownerId']}" var="usr">
							<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
						</ir:user>
						<fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['fo.createdDate_dt']}" />
						<security:secure roles="/community/update">
						<a href="${ctx}/action/community/edit/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
		<c:if test="${actionBean.numFound > 0}">
		<div class="pages pages_bottom">
			<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
				numPages="${actionBean.numPages}" path="${ctx}/action/myaccount/communities" pagesClass="pagesbot" />
		</div>
		</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
