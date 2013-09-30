<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/item.jsp $
   $Id: item.jsp 5603 2012-10-05 18:51:26Z pcharoen $
   $Revision: 5603 $ $Date: 2012-10-05 12:51:26 -0600 (Fri, 05 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<fmt:message key="application.name" var="appname" />

<stripes:layout-render name="/jsp/layout/dublinCore.jsp" title="${actionBean.htmlTitle}" active="0">

	<stripes:layout-component name="html-head">
		<ir:dublinCore inputForms="${actionBean.inputForms}" fields="${actionBean.fields}" item="${actionBean.item}" />
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				getDownloadCount();
			});
			function getDownloadCount() {
				new Ajax.Updater('download.1', '${ctx}/public/datastream/getDownloadCountByPid/${actionBean.pid}');
			}
			function toggleMoreLikeThis() {
				if ($('moreLikeThis').visible()) {
					Effect.SlideUp('moreLikeThis');
				} else {
    				if ($('transactions').visible()) {
						Effect.DropOut('transactions');
    				}
					new Ajax.Updater('moreLikeThis', '${ctx}/public/search/getMoreLikeThis', {
						parameters: { 
							pid: '${actionBean.item.properties.pid}'
						},
						onComplete: function(transport) {
							Effect.SlideDown('moreLikeThis');
						} 
					}); 
				}
			}
			<security:secure roles="/admin/approve">
			function toggleTransactions() {
				if ($('transactions').visible()) {
					Effect.SlideUp('transactions');
				} else {
    				if ($('moreLikeThis').visible()) {
						Effect.DropOut('moreLikeThis');
    				}
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
			</security:secure>
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<div class="title_actions">
		<div class="item_title">
			<div id="download.1" class="download_div"></div>
			<div><stripes:errors /><stripes:messages /></div>
			<c:if test="${actionBean.item.properties.state == 'A'}">
				<ir:handle type="item" pid="${actionBean.pid}" var="uri" />
				<p><label><fmt:message key="item.uri" />:</label><a href="${uri}">${uri}</a></p>
			</c:if>
		</div>
		
		<div class="item_toolbar">
		<c:set var="i" value="0" />
		<c:forEach items="${actionBean.item.datastreams}" var="dsm" varStatus="sts">
			<c:if test="${fn:startsWith(dsm.dsId, 'DS')}">
				<c:if test="${properties['google.analytics.enabled'] && (fn:endsWith(fn:toLowerCase(flds['dsm.labels'][sts.index]), '.pdf')||fn:contains(fn:toLowerCase(flds['dsm.mimeTypes'][sts.index]), 'pdf'))}">
					<c:set var="onclick" value="_gaq.push(['_trackEvent', 'File Download', 'download', 'pdf']); return true;" />
				</c:if>
				<c:choose>
					<c:when test="${sts.index == 0}">
						<a href="${ctx}/public/datastream/get/${actionBean.pid}/${dsm.dsId}" target="Download"
							onclick="${onclick}" class="download"><fmt:message key="checkout.header" /></a>
					</c:when>
					<c:otherwise>
						<c:set var="i" value="${i + 1}" />
						<a href="${ctx}/public/datastream/get/${actionBean.pid}/${dsm.dsId}" target="Download" 
							onclick="${onclick}" class="download"><fmt:message key="attached.item.header" />${' '}${i}</a>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
		<%-- 
		<c:if test="${actionBean.item.properties.accessType.value != 1}">
		<c:set var="j" value="0" />
		<c:forEach items="${actionBean.item.datastreams}" var="dsm" varStatus="sts">
			<c:if test="${fn:startsWith(dsm.dsId, 'DS') && (fn:contains(dsm.mimeType, 'pdf') || fn:contains(dsm.mimeType, 'word') || fn:contains(dsm.mimeType, 'ms-excel') || fn:contains(dsm.mimeType, 'spreadsheet') || fn:contains(dsm.mimeType, 'presentation') || fn:contains(dsm.mimeType, 'ms-powerpoint') || fn:contains(dsm.mimeType, 'tiff') || fn:contains(dsm.mimeType, 'zip'))}">
				<c:choose>
					<c:when test="${sts.index == 0}">
						<a href="https://docs.google.com/viewer?url=${httpServerUrl}${ctx}/public/datastream/get/${actionBean.pid}/${dsm.dsId}" target="_blank" class="read"><fmt:message key="item.read.document" /></a>
					</c:when>
					<c:otherwise>
						<c:set var="j" value="${j + 1}" />
						<a href="https://docs.google.com/viewer?url=${httpServerUrl}${ctx}/public/datastream/get/${actionBean.pid}/${dsm.dsId}" target="_blank" class="read"><fmt:message key="item.read.attached" />${' '}${j}</a>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
		</c:if> 
		--%>
		<c:if test="${actionBean.item.properties.state != 'D'}">
		<security:secure roles="/admin/approve">
			<c:choose>
			<c:when test="${actionBean.item.properties.workflowState == 'Submit'}">
				<a href="${ctx}/action/approval/review/${actionBean.pid}" class="edit_button" onclick="bigWaiting(); return true;"><fmt:message key="button.takeTask" /></a>
			</c:when>
			<c:when test="${actionBean.item.properties.workflowState == 'Review'}">
				<a href="${ctx}/action/approval/review/${actionBean.pid}" class="edit_button"  onclick="bigWaiting(); return true;"><fmt:message key="button.review" /></a>
			</c:when>
			</c:choose>
		</security:secure>
		<c:if test="${actionBean.item.properties.state != 'I'}">
		<span class="cartbutton"> <ir:addToCart pid="${actionBean.pid}" itemIndex="0" /> </span> 
		<span id="bookmark.0" class="favorite"></span>
		<c:set var="url" value="/public/view/item/${actionBean.pid}" />
		<script type="text/javascript">
			new Ajax.Updater('bookmark.0', '${ctx}/action/bookmark/getBookmarkStatus', { 
				parameters: { 
					pid: '${actionBean.pid}', 
					next: '${fnx:encodeUrl(url)}' 
				} 
			});
		</script> 
		</c:if>
		<c:choose>
		<c:when test="${actionBean.item.properties.state == 'A'}">
			<security:secure roles="/item/update,/object/dark,/object/ccid">
				<c:choose>
				<c:when test="${actionBean.item.properties.manualApproval}">
					<security:secure roles="/admin/item">
						<a href="${ctx}/action/submit/edit/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
					</security:secure>
				</c:when>
				<c:otherwise>
					<a href="${ctx}/action/submit/edit/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
				</c:otherwise>
				</c:choose>
			</security:secure>
			<security:secure roles="/item/delete,/object/dark,/object/ccid">
				<a id="deleteItem" href="${ctx}/action/item/delete/${actionBean.pid}" class="delete_button"
					onclick="Modalbox.show($('confirmDelete'), {title: '${appname}', width: 400}); return false;"><fmt:message key="button.delete" /></a>
			</security:secure>
		</c:when>
		<c:when test="${actionBean.item.properties.state == 'I' && actionBean.item.properties.embargoed && actionBean.item.properties.workflowState != 'Initial'}">
			<c:choose>
			<c:when test="${actionBean.item.properties.manualApproval}">
				<security:secure roles="/admin/embargoed">
				<security:secure roles="/item/update">
					<a href="${ctx}/action/submit/edit/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
				</security:secure>
				<security:secure roles="/item/delete">
				<a id="deleteItem" href="${ctx}/action/item/delete/${actionBean.pid}" class="delete_button" 
					onclick="Modalbox.show($('confirmDelete'), {title: '${appname}', width: 400}); return false;"><fmt:message key="button.delete" /></a>
				</security:secure>
				</security:secure>
			</c:when>
			<c:otherwise>
				<security:secure roles="/item/update">
					<a href="${ctx}/action/submit/edit/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
				</security:secure>
				<security:secure roles="/item/delete">
				<a id="deleteItem" href="${ctx}/action/item/delete/${actionBean.pid}" class="delete_button" 
					onclick="Modalbox.show($('confirmDelete'), {title: '${appname}', width: 400}); return false;"><fmt:message key="button.delete" /></a>
				</security:secure>
			</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${actionBean.item.properties.state == 'I' && actionBean.item.properties.accessType == 'NOONE'}">
			<security:secure roles="/item/delete,/object/owner,/object/dark,/object/ccid">
				<a id="deleteItem" href="${ctx}/action/item/delete/${actionBean.pid}" class="delete_button" 
					onclick="Modalbox.show($('confirmDelete'), {title: '${appname}', width: 400}); return false;"><fmt:message key="button.delete" /></a>
			</security:secure>
			<security:secure roles="/admin/dark">
				<a href="${ctx}/action/submit/edit/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
			</security:secure>
		</c:when>
		</c:choose>
		</c:if>

		<security:secure roles="/item/create,/object/owner">
		<c:if test="${actionBean.item.properties.state == 'I' && actionBean.item.properties.accessType != 'NOONE'}">
			<c:choose>
			<c:when test="${actionBean.item.properties.workflowState == 'Initial'}">
				<a href="${ctx}/action/submit/editsaved/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
				<a id="removeItem" href="${ctx}/action/submit/remove/${actionBean.item.properties.formName}/${actionBean.pid}" class="delete_button" title="${appname}" 
					onclick="Modalbox.show($('confirmRemove'), {title: this.title, width: 400}); return false;"><fmt:message key="button.remove" /></a>
			</c:when>
			<c:when test="${actionBean.item.properties.workflowState == 'Submit'}">
				<security:secure roles="/admin/purge">
				<a id="removeItem" href="${ctx}/action/submit/remove/${actionBean.item.properties.formName}/${actionBean.pid}" class="delete_button" title="${appname}" 
					onclick="Modalbox.show($('confirmRemove'), {title: this.title, width: 400}); return false;"><fmt:message key="button.remove" /></a>
				</security:secure>
			</c:when>
			<c:when test="${actionBean.item.properties.workflowState == 'Reject'}">
				<a href="${ctx}/action/submit/editrejected/${actionBean.item.properties.formName}/${actionBean.pid}" class="edit_button"><fmt:message key="button.edit" /></a>
				<security:secure roles="/admin/approve">
				<a id="removeItem" href="${ctx}/action/submit/removerejected/${actionBean.item.properties.formName}/${actionBean.pid}" class="delete_button" title="${appname}" 
					onclick="Modalbox.show($('confirmRemove'), {title: this.title, width: 400}); return false;"><fmt:message key="button.remove" /></a>
				</security:secure>
			</c:when>
			<c:otherwise>
				<a id="removeItem" href="${ctx}/action/submit/remove/${actionBean.item.properties.formName}/${actionBean.pid}" class="delete_button" title="${appname}" 
					onclick="Modalbox.show($('confirmRemove'), {title: this.title, width: 400}); return false;"><fmt:message key="button.remove" /></a>
			</c:otherwise>
			</c:choose>
		</c:if>
		
		</security:secure>
		<security:secure roles="/admin/email">
		<c:choose>
		<c:when test="${actionBean.item.properties.workflowState == 'Submit'}">
			<fmt:message key="item.submit.email" var="emailTitle" />
			<a href="${ctx}/action/submit//email/${actionBean.item.properties.pid}" class="email" title="${emailTitle}"><fmt:message key="item.email" /></a>
		</c:when>
		<c:when test="${actionBean.item.properties.workflowState == 'Reject'}">
			<fmt:message key="item.reject.email" var="emailTitle" />
			<a href="${ctx}/action/approval/email/${actionBean.item.properties.pid}" class="email" title="${emailTitle}"><fmt:message key="item.email" /></a>
		</c:when>
		<c:when test="${actionBean.item.properties.workflowState == 'Archive'}">
			<fmt:message key="item.archive.email" var="emailTitle" />
			<a href="${ctx}/action/approval/email/${actionBean.item.properties.pid}" class="email" title="${emailTitle}"><fmt:message key="item.email" /></a>
		</c:when>
		</c:choose>
		</security:secure>

		<c:if test="${actionBean.item.properties.state == 'D'}">
		<security:secure roles="/admin/purge">
			<a id="purgeItem" href="${ctx}/action/item/purge/${actionBean.pid}" class="delete_button" title="${appname}" 
				onclick="Modalbox.show($('confirmPurge'), {title: this.title, width: 400}); return false;"><fmt:message key="button.purge" /></a>
		</security:secure>
		<security:secure roles="/admin/deleted">
			<a id="restoreItem" href="${ctx}/action/item/restore/${actionBean.pid}" class="edit_button" title="${appname}" 
				onclick="Modalbox.show($('confirmRestore'), {title: this.title, width: 400}); return false;"><fmt:message key="button.restore" /></a>
		</security:secure>
		</c:if>
		
		<c:if test="${actionBean.item.properties.state == 'A'}">
		<span>
			<ir:addThis url="${uri}" title="${fnx:escapeHtml(actionBean.item.properties.label)}" /> 
			<ir:addtoany url="${uri}" title="${fnx:escapeHtml(actionBean.item.properties.label)}" /> 
		</span>
		</c:if>
		</div>
		</div>
		
		<c:if test="${actionBean.item.properties.state == 'A'}">
			<a onclick="toggleMoreLikeThis(); return false;" class="add_button auto_width" id="mltImg"><fmt:message key="item.view.more" /></a>
		</c:if>
		<security:secure roles="/admin/approve">
		<c:if test="${actionBean.noOfTransactions > 0}">
			<a onclick="toggleTransactions(); return false;" class="add_button auto_width" id="mltImg"><fmt:message key="item.view.transactions" /></a>
        </c:if>
		</security:secure>
		<div id="moreLikeThis" style="clear: both; display: none;"></div>
		<div id="transactions" style="clear: both; display: none;"></div>


		<div class="full_info">
		<ul class="item_info" style="width: 30em; min-height: 40em;">
			<li><img id="logo" name="logo" src="${ctx}/public/datastream/get/${actionBean.pid}/THUMBNAIL" /></li>
			<security:secure roles="/admin/depositor">
    		<c:if test="${not empty actionBean.item.properties.owners}">
    		<ul>
    			<li>
    				<label><fmt:message key="item.owner.name" />:</label>
    				<c:forEach items="${actionBean.item.properties.owners}" var="owner" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="mailto:${owner.email}">${owner.firstName}${' '}${owner.lastName}</a></c:forEach>
    			</li>
    		</ul>
    		</c:if>
    		</security:secure>
			<ir:metadata inputForms="${actionBean.inputForms}" fields="${actionBean.fields}" item="${actionBean.item}" />
		</ul>

		<c:if test="${!empty actionBean.item.license}"> 
		<div class="view_box">
			<h3 class="bigbold"><stripes:label for="license.label" /></h3>
			<p><a href="${ctx}/public/datastream/get/${actionBean.pid}/LICENSE" target="Download" style="text-decoration: none;">${actionBean.item.license.label}</a></p>
			<a href="${ctx}/public/datastream/get/${actionBean.pid}/LICENSE" target="Download" class="download"><fmt:message key="item.license.download" /></a>
		</div>
		</c:if>

		<c:if test="${!empty actionBean.item.communities}">
		<div class="view_box">
			<stripes:label for="community.label" /> 
			<c:forEach items="${actionBean.item.communities}" var="community">
				<a href="${ctx}/public/view/community/${community.id}" style="text-decoration: none;">${community.title}</a>
				<br />
			</c:forEach> 
			<c:if test="${not empty actionBean.item.collections}">
			<stripes:label for="collection.label" /> 
			<c:forEach items="${actionBean.item.collections}" var="collection">
				<a href="${ctx}/public/view/collection/${collection.id}" style="text-decoration: none;">${collection.title}</a>
				<br />
			</c:forEach> 
			</c:if>
			<c:set var="departments" value="${actionBean.item.metadata.fieldMap['thesis:degree.thesis:discipline']}" />
			<c:if test="${not empty departments}">
			<stripes:label for="department.label" /> 
			<c:forEach items="${departments}" var="department">
				<a href='${ctx}/public/search?fq=facet.department:"${department.value}"' style="text-decoration: none;">${department.value}</a>
				<br />
			</c:forEach> 
			</c:if>
		</div>
        </c:if>
        <ir:qrcode url="${uri}" />
		<c:if test="${actionBean.item.properties.state != 'A' && !empty actionBean.item.properties.workflowState}">
			<div class="view_box">
				<strong><fmt:message key="Item.WorkflowState.${actionBean.item.properties.workflowState}" /></strong>
				<c:choose>
				<c:when test="${actionBean.item.properties.workflowState == 'Submit' || actionBean.item.properties.workflowState == 'Initial'}">
					${' '}<fmt:message key="by" />${' '}
					<ir:user username="${actionBean.item.properties.submitterId}" var="usr" />
					<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
				</c:when>
				<c:when test="${actionBean.item.properties.workflowState == 'Review'}">
                </c:when>
				<c:when test="${actionBean.item.properties.workflowState == 'Reject'}">
                </c:when>
				<c:otherwise>
					${' '}<fmt:message key="by" />${' '}
					<ir:user username="${actionBean.item.properties.userId}" var="usr" />
					<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
				</c:otherwise>
				</c:choose> 
				<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.item.properties.workflowDate}" var="workflowDate" timeZone="GMT" /> 
				<br /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${workflowDate}" />
				<c:if test="${not empty actionBean.item.comments}">
					<br /><stripes:label for="admin.review.comments" /> 
					<textarea style="width: 100%; height: 26em;" readonly="readonly">${actionBean.item.comments}</textarea>
				</c:if>
				<c:if test="${actionBean.item.properties.accessType == 'NOONE'}">
					<br /><stripes:label for="item.dark" />
				</c:if>
				<c:if test="${actionBean.item.properties.embargoed}">
					<br /><stripes:label for="properties.embargoed" /> ${actionBean.item.properties.embargoedDate}
				</c:if>
			</div>
		</c:if>
		<security:secure roles="/admin/item">
		<div class="view_box">
    		<p>
    			<stripes:label for="properties.modifiedDate" />
    			<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.item.properties.modifiedDate}" var="modifiedDate" timeZone="GMT" /> 
    			<fmt:formatDate pattern="${actionBean.dateFormat}" value="${modifiedDate}" />
    		</p>
    		<p>
    			<stripes:label for="properties.state" />
    			<fmt:message key="State.${actionBean.item.properties.state}" />
    		</p>
    		<c:if test="${not empty actionBean.item.properties.workflowDate}">
    		<p>
    			<stripes:label for="properties.workflowDate" />
    			<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.item.properties.workflowDate}" var="workflowDate" timeZone="GMT" /> 
    			<fmt:formatDate pattern="${actionBean.dateFormat}" value="${workflowDate}" />
    		</p>
    		</c:if>
    		<c:if test="${not empty actionBean.item.properties.workflowState}">
    		<p>
    			<stripes:label for="properties.workflowState" />
    			<fmt:message key="WorkflowState.${actionBean.item.properties.workflowState}" />
    		</p>
    		</c:if>
    		<c:if test="${not empty actionBean.item.properties.userId}">
    		<p>
    			<stripes:label for="properties.userId" />
				<ir:user username="${actionBean.item.properties.userId}" var="usr" />
				<a href="mailto:${usr.email}">${usr.firstName}${' '}${usr.lastName}</a>
    		</p>
    		</c:if>
		</div>
    	</security:secure>
		</div>
		</div>
		
		<!-- confirmation dialog box -->
		<fmt:message key="button.yes" var="yes" />
		<fmt:message key="button.no" var="no" />
		<div id="confirmDelete" class="popup_box" style="display: none;">
			<h2><fmt:message key="item.delete.header" /></h2>
			<p>
				<fmt:message key="confirm.delete.prompt">
					<fmt:param value="${actionBean.item.properties.label}" />
				</fmt:message>
			</p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('deleteItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
		<div id="confirmRestore" class="popup_box" style="display: none;">
			<h2><fmt:message key="item.restore.header" /></h2>
			<p>
				<fmt:message key="confirm.restore.prompt">
					<fmt:param value="${actionBean.item.properties.label}" />
				</fmt:message>
			</p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('restoreItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
		<div id="confirmPurge" class="popup_box" style="display: none;">
			<h2><fmt:message key="item.purge.header" /></h2>
			<p>
				<fmt:message key="confirm.purge.prompt">
					<fmt:param value="${actionBean.item.properties.label}" />
				</fmt:message>
			</p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('purgeItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
		<div id="confirmRemove" class="popup_box" style="display: none;">
			<h2><fmt:message key="item.remove.header" /></h2>
			<p>
				<fmt:message key="confirm.remove.prompt">
					<fmt:param value="${actionBean.item.properties.label}" />
				</fmt:message>
			</p>
			<div class="actions">
				<input type="button" class="save_button" onclick="Modalbox.hide(); bigWaiting(); location.href = $('removeItem').href;" value="${yes}" /> 
				<input type="button" class="save_button" onclick="Modalbox.hide();" value="${no}" /> 
			</div>
		</div>
		<div style="clear: both;" />
		<security:secure roles="/admin/approve">
			<c:if test="${event == 'workflow' && actionBean.noOfTransactions > 0}">
			<script type="text/javascript">
				new Ajax.Updater('transactions', '${ctx}/action/item/getTransactionList', {
					parameters: { 
						'item.properties.pid': '${actionBean.item.properties.pid}'
					},
					onComplete: function(transport) {
						Effect.SlideDown('transactions');
					} 
				}); 
			</script>
			</c:if>
		</security:secure>
	</stripes:layout-component>

</stripes:layout-render>
