<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/communityList.jsp $
   $Id: communityList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Manage Community" active="0">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				onLoad();
				$('autocomplete').focus();
			});
			function onLoad() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/community/getCommunitiesByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								bigWaiting();
								location.href =  '${ctx}/action/community/view/' + selected.id;
							} else {
								$('autocomplete').clear();
							}
						}
					});
				}
			}
		</script>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/autocomplete.css" />
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="admin.community.header" /></h2>
		<div class="subheader">
			<fmt:message key="admin.community.description" />
			<security:secure roles="/admin/community">
			<a href="${ctx}/action/community/preCreate" class="add_button auto_width" style="float: right; margin-right: 3em;"><fmt:message key="toolbar.addcommunity.label" /></a>
			</security:secure>
		</div>
		<stripes:messages /><stripes:errors />
   		<div style="clear: both; margin-bottom: 0.5em;">
			<stripes:form action="/action/admin/communities">
	           	<stripes:label for="search.title" />:
				<stripes:text name="name" id="autocomplete" class="input_text" style="width: 30em;" />
				<div id="autocomplete_choices" class="autocomplete"></div>
			</stripes:form>
		</div>
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
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/communities" pagesClass="pagesbot" />
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
						<security:secure roles="/admin/community">
						<a href="${ctx}/action/community/edit/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/admin/communities" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
