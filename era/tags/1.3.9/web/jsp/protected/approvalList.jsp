<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/protected/approvalList.jsp $
   $Id: approvalList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<fmt:message key="search.task.hint" var="hint" />
<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Tasks in the Pool" active="0">
	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				onLoad();
				// $('autocomplete').focus();
			});
			function onLoad() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/approval/getItemsByTitleAuthor", {
						paramName: "query", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								bigWaiting();
								location.href =  '${ctx}/public/view/item/' + selected.id;
							} else {
								clearHint();
							}
						}
					});
				}
			}
			function clearHint() {
				$('autocomplete').clear();
			}
			function showHint() {
				$('autocomplete').value = '${hint}';
			}
		</script>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/autocomplete.css" />
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><stripes:label for="admin.approval.header" /></h2>
		<div class="subheader"><fmt:message key="admin.approval.description" /></div>
		<stripes:messages /><stripes:errors />
   		<div style="clear: both; margin-bottom: 0.5em;">
			<stripes:form action="/action/approval">
	           	<stripes:label for="search.label" />:
				<stripes:text name="name" id="autocomplete" class="input_text" style="width: 40em;" value="${hint}" onfocus="clearHint();" onblur="showHint();" />
				<div id="autocomplete_choices" class="autocomplete"></div>
			</stripes:form>
		</div>
		<div class="results_subheader" style="padding-bottom: 0.5em; margin-bottom: 0.2em; background-color: #F5F4DF;">
			<stripes:label for="searchResult.total" />: <b>${actionBean.totalItemCount}</b> <stripes:label for="item.unit" style="font-weight: normal; margin-right: 1em; margint-left: 0.3em;" />
			<stripes:label for="WorkflowState.Submit" />: <b>${actionBean.submittedItemCount}</b> <stripes:label for="item.unit" style="font-weight: normal; margin-left: 0.3em;" />, 
			<stripes:label for="WorkflowState.Review" />: <b>${actionBean.reviewingItemCount}</b> <stripes:label for="item.unit" style="font-weight: normal; margin-left: 0.3em;" />, 
			<stripes:label for="WorkflowState.Reject" />: <b>${actionBean.rejectedItemCount}</b> <stripes:label for="item.unit" style="font-weight: normal; margin-left: 0.3em;" />
		</div>
		<div class="results_subheader" style="padding-bottom: 0.5em;">
			<stripes:label for="searchResult.label" />: 
			<b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b> of <b>${actionBean.numFound}</b>
			<fmt:message key="item.unit" />
			<stripes:form id="sortForm" action="/action/admin/approval" style="display: inline;" method="post">
				<input type="hidden" name="approval" />
				<span style="margin-left: 1em;"><stripes:label for="sort.label" />:</span>
				<stripes:select name="sortBy" onchange="$('sortForm').submit();">
					<%@ include file="/jsp/protected/workflowSortBy.jspf" %>
				</stripes:select>
				<span style="margin-left: 1em;"><stripes:label for="filterBy.label" />:</span>
				<stripes:select name="filter" onchange="$('sortForm').submit();">
					<%@ include file="/jsp/protected/workflowState.jspf" %>
				</stripes:select>
				
				<div style="margin-top: 0.5em;">
				<stripes:label for="department.label" />:</span>
				<stripes:select name="department" onchange="$('sortForm').submit();">
					<stripes:option value=""><fmt:message key="department.all" /></stripes:option>
					<c:forEach items="${actionBean.departments}" var="department">
					<stripes:option value="${department.name}">${department.name}</stripes:option>
					</c:forEach>
				</stripes:select>
				</div>
			</stripes:form>
			<c:if test="${actionBean.resultRows == 0}">
				<br style="margin-bottom: 1em;" /><stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
			<c:if test="${actionBean.numFound > 0}">
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}/${actionBean.filter}/${actionBean.department}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/approval" pagesClass="pagesbot" />
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
							<stripes:label for="WorkflowState.${fld['era.workflowState']}" />${' '}<fmt:message key="by" />${' '}<br />
							<ir:user username="${fld['era.submitterId']}" var="usr">
								<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
							</ir:user>
						</c:when>
						<c:otherwise>
							<stripes:label for="item.submittedBy" /><br />
							<ir:user username="${fld['era.submitterId']}" var="usr">
								<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a><br />
							</ir:user>
							<stripes:label for="WorkflowState.${fld['era.workflowState']}" />${' '}<fmt:message key="by" />${' '}<br />
							<ir:user username="${fld['era.userId']}" var="usr">
								<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
							</ir:user>
						</c:otherwise>
						</c:choose>
						<br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${fld['era.workflowDate_dt']}" />
						<security:secure roles="/admin/approve">
						<c:if test="${fld['era.workflowState'] == 'Submit'}">
						<a href="${ctx}/action/approval/review/${fld['PID']}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.takeTask" /></a>
						</c:if>
						<a href="${ctx}/public/view/item/${fld['PID']}/workflow" class="add_button auto_width" id="mltImg"><fmt:message key="item.view.transactions" /></a>
						</security:secure>
					</div>
				</li>
			</c:forEach>
		</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}/${actionBean.filter}/${actionBean.department}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/admin/approval" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
