<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/userList.jsp $
   $Id: userList.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Manage Users" active="0">

    <stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				onLoad();
				$('autocomplete').focus();
			});
			function onLoad() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/admin/user/getUserIdsByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								bigWaiting();
								location.href =  '${ctx}/action/admin/user/edit/' + selected.id;
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
		<h2><stripes:label for="admin.user.header" /></h2>
		<div class="subheader">
			<fmt:message key="admin.user.description" />
			<a href="${ctx}/action/admin/user/preCreate" class="add_button itemlist_add_button"><fmt:message key="toolbar.adduser.label" /></a>
		</div>
		<stripes:messages /><stripes:errors />
   		<div style="clear: both; margin-bottom: 0.5em;">
			<stripes:form action="/action/admin/user">
	           	<stripes:label for="search.name" />:
				<stripes:text name="name" id="autocomplete" class="input_text" style="width: 30em;" />
				<div id="autocomplete_choices" class="autocomplete"></div>
			</stripes:form>
		</div>
		<div class="results_subheader">
			<c:if test="${actionBean.resultRows > 0}">
				<stripes:label for="searchResult.label" />: <b>${actionBean.numFound > 0 ? actionBean.start + 1 : 0}</b> - <b>${actionBean.start + actionBean.resultRows}</b>
				${' '}<fmt:message key="of" />${' '}<b>${actionBean.numFound}</b> <fmt:message key="user.unit" />
				<div class="pages">
					<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
						numPages="${actionBean.numPages}" path="${ctx}/action/admin/users" pagesClass="pagesbot" />
				</div>
			</c:if>
			<c:if test="${actionBean.resultRows == 0}">
				<stripes:label for="searchResult.noDataFound" class="no_result" />
			</c:if>
		</div>
		
		<security:secure roles="/admin/group">
			<c:set var="editGroup" value="true" />
		</security:secure>
		<security:secure roles="/admin/user">
			<c:set var="editUser" value="true" />
		</security:secure>
		<div class="itemlist">
        	<ol>
			<c:forEach items="${actionBean.users}" var="user" varStatus="status">
				<li class="record">
					<div class="itemlist_info">
                 		<h2><a href="${ctx}/action/admin/user/edit/${user.id}" class="result_title">${user.firstName}${' '}${user.lastName}</a></h2>	
						<p class="result_others"><stripes:label for="user.email" />: <a href="mailto:${user.email}">${user.email}</a></p>    
						<p class="result_others"><stripes:label for="user.createdDate" />: <fmt:formatDate pattern="${actionBean.dateFormat}" value="${user.createdDate}" /></p>
					</div>
					<div class="itemlist_collection">
						<stripes:label for="user.username" />: ${user.username}<br />
						<stripes:label for="user.group" />:
						<c:choose>
						<c:when test="${editGroup}">
							<a href="${ctx}/action/admin/group/permission/list/${user.group.id}">${user.group.name}</a>
						</c:when>
						<c:otherwise>
							${user.group.name}
						</c:otherwise>
						</c:choose> 
					</div>
					<div class="itemlist_actions">
						<stripes:label for="user.state" />: <fmt:message key="UserState.${user.state}" /><br />
						<c:if test="${editGroup}">
							<a href="${ctx}/action/admin/user/edit/${user.id}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.edit" /></a>
						</c:if>
					</div>
				</li>
			</c:forEach>
			</ol>
			<c:if test="${actionBean.numFound > 0}">
			<div class="pages pages_bottom">
				<ir:pages numFound="${actionBean.numFound}" start="${actionBean.start}" sortBy="${actionBean.sortBy}" rows="${actionBean.rows}"
					numPages="${actionBean.numPages}" path="${ctx}/action/admin/users" pagesClass="pagesbot" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
