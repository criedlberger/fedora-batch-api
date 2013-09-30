<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/protected/collectionList.jsp $
   $Id: collectionList.jsp 5615 2012-10-16 18:20:47Z pcharoen $
   $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Manage Collections" active="0">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				onLoad();
				$('autocomplete').focus();
			});
			function onLoad() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/collection/getCollectionsByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								bigWaiting();
								location.href =  '${ctx}/action/collection/view/' + selected.id;
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
		<h2><stripes:label for="admin.collection.header" /></h2>
		<div class="subheader">
			<fmt:message key="admin.collection.description" />
			<security:secure roles="/admin/collection">
			<a href="${ctx}/action/collection/preCreate" class="add_button itemlist_add_button"><fmt:message key="toolbar.addcollection.label" /></a>
            </security:secure>
		</div>
		<stripes:messages /><stripes:errors />
   		<div style="clear: both; margin-bottom: 0.5em;">
			<stripes:form action="/action/admin/collections">
	           	<stripes:label for="search.title" />:
				<stripes:text name="name" id="autocomplete" class="input_text" style="width: 30em;" />
				<div id="autocomplete_choices" class="autocomplete"></div>
			</stripes:form>
		</div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start +
				actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
				<fmt:message key="collection.unit" />
			</c:if> 
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/collections" pagesClass="pagesbot" />
				</div>
			</c:if>
		</div>
		<div class="itemlist">
        <ol>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
				<li class="record">
					<div class="itemlist_info record_info">
                 		<h2><a href="${ctx}/action/collection/view/${result['PID']}" class="result_title">${result['dc.title'][0]}</a></h2>	
                   		<c:if test="${not empty result['dc.description']}"><p class="result_subject">${fnx:trim(result['dc.description'][0], 200)}</p></c:if>
                   		<p class="result_others">
							<stripes:label for="community.label" />:
							<c:forEach items="${result['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
                   		</p>
					</div>
					<div class="itemlist_actions record_actions">
						<stripes:label for="collection.owner" /><br />
						<ir:user username="${result['fo.ownerId']}" var="usr">
							<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
						</ir:user>
						<fmt:formatDate pattern="${actionBean.dateFormat}" value="${result['fo.createdDate_dt']}" />
						<security:secure roles="/admin/collection">
						<a href="${ctx}/action/collection/edit/${result['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/admin/collections" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
