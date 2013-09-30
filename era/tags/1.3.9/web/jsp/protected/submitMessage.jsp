<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/protected/submitMessage.jsp $
   $Id: submitMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<stripes:layout-render name="/jsp/layout/standard.jsp" title="Submit" active="3">
	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	<stripes:layout-component name="menubar">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<c:set var="formHeader" value="${actionBean.form}.header" />
		<div class="full_box">
			<h2><stripes:label for="${formHeader}" /></h2>
			<div class="edit_message">
			<div class="global_error">
				<stripes:errors>
					<c:choose>
					<c:when test="${actionBean.form == 'thesis'}">
						<stripes:individual-error />
					</c:when>
					<c:otherwise>
						<stripes:errors-header>
							<div class="errors">
							<h2><fmt:message key="submit.${actionBean.form}.errors.header" /></h2>
							<ul>
						</stripes:errors-header>
						<li><stripes:individual-error /></li>
						<stripes:errors-footer>
							</ul></div>
						</stripes:errors-footer>
					</c:otherwise>
					</c:choose>
				</stripes:errors>
			</div>
			<stripes:messages />
		</div>
		<c:if test="${fn:length(context.validationErrors) == 0}">
			<fmt:message key="${actionBean.form}.submit.another" var="label" />
			<c:choose>
			<c:when test="${actionBean.form == 'thesis'}">
			</c:when>
			<c:otherwise>
				<c:set var="url" value="${ctx}/action/submit/init/${actionBean.form}" />
				<input type="button" class="another_button" style="margin-top; 1em; margin-left: 14em;" value="${label}" onclick="location.href = '${url}'" />
			</c:otherwise>
			</c:choose>
		</c:if>
	</stripes:layout-component>
</stripes:layout-render>
