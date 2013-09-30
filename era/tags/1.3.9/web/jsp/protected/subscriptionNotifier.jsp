<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/protected/subscriptionNotifier.jsp $
   $Id: subscriptionNotifier.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/action/password/change" />
</c:if>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Subscription Notifier" active="0">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="sidebar">
		<ir:sidebar name="admin.sidebar" active="${adminSubscriptionNotifier}" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="subscription.notifier.header" /></h2>
			<div class="subheader">
				<fmt:message key="subscription.notifier.subheader" /><br />
				<stripes:label for="last.updated" />: ${actionBean.scheduler.startTime} 
				<c:if test="${not empty actionBean.scheduler.stopTime}"> - ${actionBean.scheduler.stopTime} 
				 (${actionBean.scheduler.hours > 0 ? actionBean.scheduler.hours : ''} ${actionBean.scheduler.hours > 0 ? ' hrs' : ''} 
				 ${actionBean.scheduler.minutes > 0 ? actionBean.scheduler.minutes : ''} ${actionBean.scheduler.minutes > 0 ? ' mins' : ''} 
				 ${actionBean.scheduler.seconds > 0 ? actionBean.scheduler.seconds : ''} ${actionBean.scheduler.seconds > 0 ? ' secs' : ''}) 
				</c:if>
			</div>
			<stripes:errors /><stripes:messages />
			<c:if test="${event == 'confirm'}">
				<stripes:form id="subscriptionForm" action="/action/admin/subscription/notifier">
					<table>
		                <tr>
		                	<td align="left" colspan="2" style="height: 40px; vertical-align: bottom; width: 580px;">
		                		<input type="hidden" name="execute" value="Execute" />
		                		<stripes:submit name="execute" class="button" value="${btnExecute}" onclick="$(document.body).startWaiting('bigWaiting'); return true;" />
		                	</td>
		                </tr>
					</table>
				</stripes:form>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
