<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/protected/embargoedPublisher.jsp $
   $Id: embargoedPublisher.jsp 5615 2012-10-16 18:20:47Z pcharoen $
   $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/action/password/change" />
</c:if>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Embargoed Item Publisher" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminEmbargoedPublisher}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="embargoed.publisher.header" /></h2>
			<div class="subheader">
				<fmt:message key="embargoed.publisher.subheader" /><br />
				<stripes:label for="last.updated" />: ${actionBean.scheduler.startTime} 
				<c:if test="${not empty actionBean.scheduler.stopTime}"> - ${actionBean.scheduler.stopTime} 
				 (${actionBean.scheduler.hours > 0 ? actionBean.scheduler.hours : ''} ${actionBean.scheduler.hours > 0 ? ' hrs' : ''} 
				 ${actionBean.scheduler.minutes > 0 ? actionBean.scheduler.minutes : ''} ${actionBean.scheduler.minutes > 0 ? ' mins' : ''} 
				 ${actionBean.scheduler.seconds > 0 ? actionBean.scheduler.seconds : ''} ${actionBean.scheduler.seconds > 0 ? ' secs' : ''}) 
				</c:if>
			</div>
			<stripes:errors /><stripes:messages />
			<c:if test="${event == 'confirm'}">
			<stripes:form id="embargoedForm" action="/action/admin/embargoed/publisher">
				<table>
	                <tr>
	                	<td align="left" colspan="2" style="height: 40px; vertical-align: bottom; width: 60em;">
	                		<stripes:submit name="execute" class="button" style="margin-left: 28em;" value="${btnExecute}" onclick="$(document.body).startWaiting('bigWaiting'); return true;" />
	                		<input type="button" class="button" onclick="location.href = '${ctx}/action/admin'" value="${btnCancel}" />
	                	</td>
	                </tr>
				</table>
			</stripes:form>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
