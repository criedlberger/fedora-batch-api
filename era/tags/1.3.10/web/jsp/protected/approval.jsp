<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/approval.jsp $
   $Id: approval.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ page import="net.sourceforge.stripes.controller.StripesConstants"%>
<fmt:message key="application.name" var="appname" />
<fmt:message key="accept.restrictedDate.error" var="acceptRestrictedDateError" />
<fmt:message key="reject.restrictedDate.error" var="rejectRestrictedDateError" />

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Administrator - Review and Approve Submitted Item">

	<stripes:layout-component name="html-head">
		<%@ include file="/jsp/layout/calendarPopup.jspf" %>
		<script type="text/javascript">
			function toggleTransactions() {
				if ($('transactions').visible()) {
					Effect.SlideUp('transactions');
				} else {
					new Ajax.Updater('transactions', '${ctx}/action/item/getTransactionList', {
						parameters: { 
							'item.properties.pid': '${actionBean.item.properties.pid}'
						},
						onComplete: function(transport) {
							Effect.SlideDown('transactions');
						} 
					}); 
				}
			}
			function getExplanation(rejectId) {
				new Ajax.Updater('comments', '${ctx}/action/approval/getExplanation', {
					parameters: { 
						'rejectId': rejectId
					}
				});
			}
			function setEmbargoedDate() {
 				if ($('embargoed').checked) {
 					$('reject').disable();
 					$('release').disable();
 					$('reject').setOpacity(0.5);
 					$('release').setOpacity(0.5);
 				} else {
 					$('embargoedDate').clear();
 					$('reject').enable();
 					$('release').enable();
 					$('reject').setOpacity(1);
 					$('release').setOpacity(1);
 				}
			}
			function setEmbargoed() {
				if ($('embargoedDate').value.length > 0) {
					$('embargoed').checked = true;
				} else {
					$('embargoed').checked = false;
				}
			}
			function afterDateSelected(y, m, d) {
				$('embargoed').checked = true;
				$('embargoedDate').value = y + '/' + m + '/' + d; 
			}
			function validateAccept() {
				if ($('embargoed').checked && $('embargoedDate').value == '') {
					$('message').innerHTML = '${acceptRestrictedDateError}';
					Modalbox.show($('popupMessage'), {title: '${appname}', width: 400});
					$('embargoedDate').focus();
					return false;
				} else {
					bigWaiting();
					return true;
				}
			}
			function validateRejectAndReturn() {
				if ($('embargoed').checked || $('embargoedDate').value.length > 0) {
					$('message').innerHTML = '${rejectRestrictedDateError}';
					Modalbox.show($('popupMessage'), {title: '${appname}', width: 400}); 
					$('embargoed').focus();
					return false;
				} else {
					bigWaiting();
					return true;
				}
			}
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><fmt:message key="admin.approve.header" /></h2>
		<div class="title_actions" style="margin-top: 1em;">
		<div class="item_title">
			<div id="download.1" class="download_div"></div>
			<script type="text/javascript">new Ajax.Updater('download.1', '${ctx}/public/datastream/getDownloadCountByPid', { parameters: { pid: '${actionBean.item.properties.pid}'}});</script>
			<div><stripes:errors globalErrorsOnly="true" /><stripes:messages /></div>
		</div>
		<div class="item_toolbar">
		<c:forEach items="${actionBean.item.datastreams}" var="dsm" varStatus="sts">
			<c:if test="${fn:startsWith(dsm.dsId, 'DS')}">
				<c:choose>
					<c:when test="${dsm.dsId == 'DS1'}">
						<a href="#" onclick="window.open('${ctx}/public/datastream/get/${actionBean.item.properties.pid}/${dsm.dsId}', 'Download');" class="download" style="width: 12em;" />
						<fmt:message key="admin.approval.download" />
						</a>
					</c:when>
					<c:otherwise>
						<a href="#" onclick="window.open('${ctx}/public/datastream/get/${actionBean.item.properties.pid}/${dsm.dsId}', 'Download');" class="download" />
						<fmt:message key="attached.item.header" />
						</a>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
		</div>
		</div>
		
		<a onclick="toggleTransactions(); return false;" class="add_button auto_width" id="mltImg"><fmt:message key="item.view.transactions" /></a>
		<div id="transactions" style="clear: both; display: none;"></div>

		<div class="full_info">
		<ul class="item_info" style="width: 30em;">
			<c:if test="${!empty actionBean.item.thumbnail}">
				<li><img id="logo" name="logo" src="${ctx}/public/datastream/get/${actionBean.item.properties.pid}/THUMBNAIL" /></li>
			</c:if>
			<security:secure roles="/admin/depositor">
			<c:if test="${not empty actionBean.item.properties.owners}">
			<li>
				<label><fmt:message key="item.owner.name" />:</label>
				<c:forEach items="${actionBean.item.properties.owners}" var="owner" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="mailto:${owner.email}">${owner.firstName}${' '}${owner.lastName}</a></c:forEach>
			</li>
			</c:if>
			</security:secure>
			<ir:metadata inputForms="${actionBean.inputForms}" fields="${actionBean.fields}" item="${actionBean.item}" />
			<li>
				<label><fmt:message key="item.submitter.email" />:</label>
				<c:forEach items="${actionBean.item.properties.owners}" var="owner" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="mailto:${owner.email}">${owner.email}</a></c:forEach>
			</li>
		</ul>
		
		<stripes:form action="/action/approval" name="approval">
		<div class="view_box">
			<h3 class="bigbold"><stripes:label for="license.label" /></h3>
			<p><a href="${ctx}/public/datastream/get/${actionBean.item.properties.pid}/LICENSE" target="Download" style="text-decoration: none;">${actionBean.item.license.label}</a></p>
			<a href="${ctx}/public/datastream/get/${actionBean.item.properties.pid}/LICENSE" target="Download" class="download"><fmt:message key="item.license.download" /></a>
    		<p>
    			<stripes:label for="properties.modifiedDate" />
    			<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.item.properties.modifiedDate}" var="modifiedDate" timeZone="GMT" /> 
    			<fmt:formatDate pattern="${actionBean.dateFormat}" value="${modifiedDate}" />
    		</p>
		</div>
		<div class="view_box">
			<stripes:label for="community.label" /> 
			<c:forEach items="${actionBean.item.communities}" var="community">
				<a href="${ctx}/public/view/community/${community.id}" style="text-decoration: none;">${community.title}</a>
				<br />
			</c:forEach> 
			<stripes:label for="collection.label" /> 
			<c:forEach items="${actionBean.item.collections}" var="collection">
				<a href="${ctx}/public/view/collection/${collection.id}" style="text-decoration: none;">${collection.title}</a>
				<br />
			</c:forEach> 
		</div>
		<div class="view_box">
			<!-- item emabargoed date -->
			<div>
    			<stripes:label for="item.embargoed" /> 
    			<stripes:checkbox id="embargoed" name="embargoed" class="check" onclick="setEmbargoedDate(); return true;" />
    			<fmt:message key="properties.embargoed" />
    			<c:if test="${stripes:hasErrors(actionBean, 'item.properties.embargoedDate')}"><br /><stripes:errors field="item.properties.embargoedDate" /></c:if>
    			<br /><%= BaseActionBean.dublinCoreDatePattern %>
    			<br /><stripes:text name="embargoedDate" id="embargoedDate" class="smaller" onchange="setEmbargoed(); return true;" />
    			<!-- popup calendar -->
				<a href="#" style="font-weight: bold;" onClick="cal1x.select($('embargoedDate'),'anchor1x','yyyy/MM/dd'); return false;" title="Select Date from Calendar" name="anchor1x" id="anchor1x"><fmt:message key="calendar.select" /></a>
			</div>
		</div>
		<div style="clear: both; float: left; width: 55em;" class="view_box">
			<!-- comments -->
			<stripes:label for="admin.review.comments" />
			<fmt:message key="reject.count" var="count" />
			<select name="rejectId" id="rejectId" onchange="getExplanation($('rejectId').value); return true" class="reject">
				<c:forEach begin="0" end="${count}" var="i">
					<option value="${i}"><fmt:message key="reject.${i}.title" /></option>
				</c:forEach>
			</select>
			<div id="comments">
			<stripes:textarea name="comments" class="comments">${actionBean.item.comments}</stripes:textarea>
			</div>
			<div class="approve_buttons" style="float: none;">
				<input type="hidden" name="<%= StripesConstants.URL_KEY_FLASH_SCOPE_ID %>" value="${actionBean.flashScopeId}" />
				<stripes:submit id="accept" name="accept" class="save_button" title="Accept" onclick="return validateAccept();"><fmt:message key="button.accept" /></stripes:submit>
				<stripes:submit id="reject" name="reject" class="save_button" title= "Reject" onclick="return validateRejectAndReturn();"><fmt:message key="button.reject" /></stripes:submit>
				<stripes:submit id="release" name="release" class="save_button" style="width: 8.5em;" title="Return to the task pool" onclick="return validateRejectAndReturn();"><fmt:message key="button.release" /></stripes:submit>
				<c:choose>
				<c:when test="${event == 'review'}">
					<a class="cancel_link" style="margin-left: 0.2em;" href="${ctx}/action/admin/review"><fmt:message key="button.cancel" /></a>
				</c:when>
				<c:otherwise>
					<a class="cancel_link" style="margin-left: 0.2em;" href="${ctx}/action/admin/approval"><fmt:message key="button.cancel" /></a>
				</c:otherwise>
				</c:choose>
			</div>
		</div>		
		</stripes:form>
		</div>
		</div>
		<!--  popup message  -->
		<div id="popupMessage" class="popup_box" style="display: none;">
			<h2><fmt:message key="admin.approve.message.header" /></h2>
			<p id="message"></p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${btnClose}" /> 
			</div>
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="html-foot">
		<div id="caldiv" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white; line-height: 1em;"></div>
	</stripes:layout-component>
</stripes:layout-render>
