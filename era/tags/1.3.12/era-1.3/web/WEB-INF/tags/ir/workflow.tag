<%--
	$Id: workflow.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	Workflow Taglib
	--------------
	Attributes: 
		- name			: The workflow name in properties file. Ex: deposit.workflow.
		- workflowClass	: The CSS class name for navigator bar.
		- active		: The number of active step.
		- itemCount		: The number of steps.
		- showTitle		: The boolean value (true/false) for showing step title.
		- titleClass	: The step title CSS class name.
	Properties:
		# Deposit Workflow
		deposit.workflow.0.name 	= Home
		deposit.workflow.0.title	= Cancel this submission and return to home page.
		deposit.workflow.1.name 	= Step 1
		deposit.workflow.1.title 	= Enter a new item information.
		...
		deposit.workflow.9.name 	= Finish
		deposit.workflow.9.title 	= Finished.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates workflow navigator bar for wizard pages." %>

<%@ attribute name="name" type="java.lang.String" required="true" description="The workflow name in properties file. Ex: deposit.workflow." %>
<%@ attribute name="workflowClass" type="java.lang.String" description="The CSS class name for workflow navigator bar." %>
<%@ attribute name="active" type="java.lang.Integer" description="The number of active step." %>
<%@ attribute name="itemCount" type="java.lang.Integer" required="true" description="The number of steps." %>
<%@ attribute name="showTitle" type="java.lang.Boolean" description="The boolean value (true/false) for showing step title." %>
<%@ attribute name="titleClass" type="java.lang.String" description="The step title CSS class name." %>

<c:set var="active" value="${active == 0 ? 1 : active}" />
<div class="${workflowClass}">
<ul>
<fmt:message key="${name}${'.'}${active}${'.title'}" var="activeTitle"/>
<c:forEach var="i" begin="0" end="${itemCount + 1}">
	<fmt:message var="stepName" key="${name}${'.'}${i}${'.name'}" />
	<c:if test="${!empty stepName}">
		<fmt:message key="${name}${'.'}${i}${'.title'}" var="title"/>
		<c:choose>
		<c:when test="${active == i}">
			<li><span class="active">${stepName}</span><span class="seperater">
		</c:when>
		<c:otherwise>
			<li>
			<c:choose>
			<c:when test="${showTitle}">
				<a href="#" onclick="return false;" onmouseover="showStepTitle('${title}');" onmouseout="hideStepTitle('${activeTitle}');">${stepName}</a>
			</c:when>
			<c:otherwise>
				<a href="#" onclick="return false;">${stepName}</a>
			</c:otherwise>
			</c:choose>
			
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${i == 0 || i == itemCount}">
			<span class="seperater">&bull;</span>
		</c:when>
		<c:when test="${i == itemCount + 1}">
		</c:when>
		<c:otherwise>
			<span class="seperater">&raquo;</span>
		</c:otherwise>
		</c:choose>
		</li>
	</c:if>
</c:forEach>
</ul>
</div>
<c:if test="${showTitle}">
<div id="stepTitle" class="${titleClass}">${activeTitle}</div><div style="height: 14px;"></div>
<script type="text/javascript">
	function showStepTitle(title) {
		document.getElementById('stepTitle').innerHTML = title;
	}
	function hideStepTitle(title) {
		document.getElementById('stepTitle').innerHTML = title;
	}
</script>
</c:if>
