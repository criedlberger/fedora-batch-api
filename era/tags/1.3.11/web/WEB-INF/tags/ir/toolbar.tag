<%--
	$Id: toolbar.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Edit Object Toolbar Taglib
	--------------
	Attributes: 
		- action 		: The action name.
		- pid			: The object PID.
		- title			: The object title.
		- toolbarClass	: The toolbar CSS class name.
		- confirmDelete	: The confirm delete JavaScript function and parameters with return true/false.		
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates toolbar for view and edit pages." %>

<%@ attribute name="action" type="java.lang.String" required="true" description="The action name." %>
<%@ attribute name="pid" type="java.lang.String" required="false" description="The object PID." %>
<%@ attribute name="title" type="java.lang.String" required="false" description="The object title." %>
<%@ attribute name="toolbarClass" type="java.lang.String" required="false" description="The toolbar CSS class name." %>
<%@ attribute name="style" type="java.lang.String" required="false" description="The toolbar style attribute value(HTML pass through)." %>
<%@ attribute name="confirmDelete" type="java.lang.String" required="false" description="The confirm delete JavaScript function and parameters with return true/false.	" %>
<%@ attribute name="urlSuffix" type="java.lang.String" required="false" description="The action URL suffix." %>

<%@ attribute name="add" type="java.lang.Boolean" required="false" description="The flag to turn on/off add button." %>
<%@ attribute name="edit" type="java.lang.Boolean" required="false" description="The flag to turn on/off edit button." %>
<%@ attribute name="delete" type="java.lang.Boolean" required="false" description="The flag to turn on/off delete button." %>

<%@ attribute name="createRole" type="java.lang.Boolean" required="false" description="The flag to turn on/off create role." %>
<%@ attribute name="updateRole" type="java.lang.Boolean" required="false" description="The flag to turn on/off update role." %>
<%@ attribute name="deleteRole" type="java.lang.Boolean" required="false" description="The flag to turn on/off delete role." %>

<fmt:message key="application.name" var="appname" />

<%-- check security access control --%>
<c:if test="${empty createRole}">
	<c:set var="createRole" value="false" />
	<security:secure roles="/${action}/create">
	<c:set var="createRole" value="true" />
	</security:secure>
</c:if>
<c:if test="${empty updateRole}">
	<c:set var="updateRole" value="false" />
	<security:secure roles="/${action}/update">
	<c:set var="updateRole" value="true" />
	</security:secure>
</c:if>
<c:if test="${empty deleteRole}">
	<c:set var="deleteRole" value="false" />
	<security:secure roles="/${action}/delete">
	<c:set var="deleteRole" value="true" />
	</security:secure>
</c:if>


	<c:if test="${empty edit || edit}">
		<c:if test="${updateRole}">
			<a href="${ctx}/action/${action}/edit/${pid}${urlSuffix}" class="edit_button"><fmt:message key="toolbar.edit.label" /></a> 
		</c:if>
	</c:if>
	<c:if test="${empty add || add}">
		<c:if test="${createRole}">
 			<a href="${ctx}/action/${action}/preCreate/${pid}${urlSuffix}" class="add_button"><fmt:message key="toolbar.add.label" /></a>
		</c:if>
	</c:if> 
	<c:if test="${empty delete || delete}">
		<c:if test="${deleteRole}">
		 	<a id="delete" href="${ctx}/action/${action}/delete/${pid}${urlSuffix}" class="delete_button" title="${appname}"
				onclick="Modalbox.show($('confirmDelete'), {title: this.title, width: 400}); return false;">
		 		<fmt:message key="toolbar.delete.label" />
		 	</a>
		</c:if>
	</c:if>
	&nbsp;

	<fmt:message key="button.yes" var="yes" />
	<fmt:message key="button.no" var="no" />
	<div id="confirmDelete" class="popup_box" style="display: none;">
		<h2><fmt:message key="${action}.delete.header" /></h2>
		<p>
			<fmt:message key="confirm.delete.prompt">
				<fmt:param value="${title}" />
			</fmt:message>
		</p>
		<div class="actions">
			<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('delete').href;" value="${yes}" /> 
			<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
		</div>
	</div>
