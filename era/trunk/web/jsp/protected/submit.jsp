<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/submit.jsp $
   $Id: submit.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<c:set var="state" value="${actionBean.item.properties.workflowState}" />
<c:set var="step" value="1" />

<stripes:layout-render name="/jsp/layout/standard.jsp" title="${state == 'Archive' || !empty item.properties.pid ? 'Edit Item' : actionBean.form == 'thesis' ? 'Submit' : 'Deposit'}" active="3">

    <stripes:layout-component name="html-head">
		<%@ include file="/jsp/layout/calendarPopup.jspf" %>
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				getOwnerNameAutoComplete();
				getCollectionList();
				$('itemForm').observe('keypress', function(event){
				    if (event.keyCode == Event.KEY_RETURN  || event.which == Event.KEY_RETURN) {
				        Event.stop(event);
				    }
				});
			});
			function getOwnerNameAutoComplete() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/admin/user/getUsersByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								addOwner(selected);
								$('autocomplete').value = '';
								$('autocomplete').focus();
							} else {
								$('autocomplete').clear();
							}
						}
					});
				}
			}
			function getCollectionList() {
				var comIds = new Array();
				var coms = $('itemForm').elements['coms'];
				if (coms) {
					if (coms.value) {
						comIds = coms.value;
					} else {
						for (var i = 0; i < coms.length; i++) {
							comIds[i] = coms[i].value
						}
					}
				}
				new Ajax.Updater('collections', ctx + '/action/submit/getCollectionList', {
					parameters: {
						coms: comIds 
					}
				});
			}
			function gotoThesisSubmissionForm() {
				location.href = '${ctx}/submit/thesis';
			}
			function setEmbargoedDate() {
 				if ($('embargoed').checked) {
 				} else {
 					$('embargoedDate').clear();
 				}
			}
			function setEmbargoed() {
				if ($('embargoedDate').value.length > 0) {
					$('embargoed').checked = true;
				} else {
					$('embargoed').checked = false;
				}
			}
			function showEmbargoedFields(ele) {
				if (ele.value == 'NOONE') {
					$('embargoed').checked = false;
					$('embargoedDate').value = '';
					$('embargo').hide();
				} else {
					$('embargo').show();
				}
			}
			function afterDateSelected(y, m, d) {
				$('embargoed').checked = true;
				$('embargoedDate').value = y + '/' + m + '/' + d; 
			}
		</script>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/autocomplete.css" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<c:set var="formHeader" value="${actionBean.form}.header" />
			<c:set var="formSubheader" value="${actionBean.form}.subheader" />
			<h2><stripes:label for="${state == 'Archive' || !empty item.properties.pid ? 'item.edit.header' : formHeader}" /></h2>
			<div class="subheader">
				<p>
					<fmt:message key="${state == 'Archive' || !empty item.properties.pid ? 'item.edit.subheader' : formSubheader}" />
					<c:set var="formMessageId" value="${actionBean.inputForm.enabled ? 'form-enabled-message' : 'form-disabled-message'}" />
					${actionBean.inputForm.fieldMap[formMessageId].value.value}
				</p>
			</div>
			<c:if test="${state == 'Reject'}">
			<div class="errors">
				<h2>
				<fmt:message key="submit.itemRejected">
				<fmt:param>
					<ir:user username="${actionBean.item.properties.userId}" var="user" />
					<a href="mailto:${user.email}">${user.firstName}${' '}${user.lastName}</a>
				</fmt:param>
				<fmt:param>
					<fmt:parseDate pattern="${actionBean.datePattern}" value="${actionBean.item.properties.workflowDate}" var="workflowDate" timeZone="GMT" /> 
					<fmt:formatDate pattern="${actionBean.dateFormat}" value="${workflowDate}" />
				</fmt:param>
				</fmt:message>
				</h2>
				<c:if test="${not empty actionBean.item.comments}">
					<label><fmt:message key="admin.review.comments" />:</label><br /> 
					<textarea style="width: 99%; height: 18em; background-color: #FFEDED;" readonly="readonly">${actionBean.item.comments}</textarea>
				</c:if>
			</div>
			</c:if>
			<stripes:errors globalErrorsOnly="true" />
			
			<div class="deposit_metadata">
			<stripes:form id="itemForm" action="/action/submit">
				<stripes:hidden name="form" />
				<stripes:hidden name="item.properties.pid" />
				<stripes:hidden name="item.properties.workflowState" />
				<stripes:hidden name="item.properties.workflowDate" />
				<stripes:hidden name="item.comments" />

				<!-- item owner input form -->
				<security:secure roles="/admin/depositor">
				<fieldset class="what">    
					<h3><fmt:message key="item.owner.header" /></h3><stripes:errors field="owner" />
					<p class="regular"><fmt:message key="item.owner.subheader" /></p>
					<ul style="margin-bottom: 0;">
						<li class="floater1" style="margin-bottom: 0;">
							<stripes:label for="item.owner.name" />
							<input type="hidden" id="username" />
							<div class="menus">
								<stripes:text name="owner" id="autocomplete" style="width: 30em;" />
								<div id="autocomplete_choices" class="autocomplete"></div>
							</div>
						</li>
					</ul>
					<ul id="owner_list" style="clear: both;">
						<c:forEach items="${actionBean.item.properties.owners}" var="owner" varStatus="sts">
						<li class="floater3">
							<stripes:label for="empty"/>
							<input type="hidden" name="usernames" value="${owner.username}"  />${owner.firstName}${' '}${owner.lastName} (${owner.email}) 
                   			<a href="#" class="remove" onclick="removeOwner(this); return false;"><fmt:message key="item.owner.remove" /></a>
						</li>
						</c:forEach>
					</ul>
				</fieldset>
				</security:secure>

				<!-- step 1: metadata input form -->
				<ir:inputForm />
				
				<!-- step 2: community/collection input form -->
				<c:choose>
				<c:when test="${actionBean.form == 'thesis'}">
					<c:forEach items="${actionBean.item.communities}" var="com" varStatus="sts">
						<input type="hidden" name="coms" value="${com.id}" />
					</c:forEach>
					<c:forEach items="${actionBean.item.collections}" var="col">
						<input type="hidden" name="cols" value="${col.id}" />
					</c:forEach>
				</c:when>
				<c:otherwise>
				<fieldset class="where">
					<h3>
						<c:set var="step" value="${step + 1}" />
						<fmt:message key="item.workflow.where">
							<fmt:param value="${step}" />
						</fmt:message>
					</h3> 
					<security:secure roles="/item/create">
					<c:if test="${(state == 'Initial' || event == 'init' || empty item.properties.pid) || state == 'Reject'}">
						<stripes:submit name="save" class="save_button" style="margin-left: 0em; height: 1.2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
						<br />
					</c:if>
					</security:secure>
					<ul>
						<li> 
							<stripes:label for="item.community" class="required"/>
							<c:if test="${stripes:hasErrors(actionBean, 'coms')}"><br /><stripes:errors field="coms" /></c:if>
							<div class="menus">
								<stripes:select name="itemComs" id="item.coms" onchange="addCommunity(this); return false;">
									<option value=""><fmt:message key="deposit.select" /></option>
									<stripes:options-collection collection="${actionBean.communityList}" label="title" value="id"/>
								</stripes:select>
							</div>
							<a href="mailto:erahelp@ualberta.ca?subject=Community Request" class="addmore" style="margin-bottom: 0.5em;"><fmt:message key="item.community.request" /></a>
							<div class="list">
							<ul id="community_list">
							<c:forEach items="${actionBean.item.communities}" var="com" varStatus="sts">
								<li class="floater3"> 
									<input type="hidden" name="coms" value="${com.id}" />${com.title}&nbsp;
									<a href="#" class="remove" onclick="removeCommunity($(this).up()); return false;"><fmt:message key="item.community.remove" /></a>
								</li>
							</c:forEach>
							</ul>
							</div>
						</li>
					</ul>
					<br style="clear: left;" />
					<ul>
						<li>
							<stripes:label for="item.collection" />
							<stripes:errors field="cols" />
							<span id="collectionError" class="field_error"></span>
							<div id="collections" class="menus">
								<stripes:select name="itemCols" id="item.cols" onchange="addCollection(this); return false;">
									<option value=""><fmt:message key="deposit.select" /></option>
								</stripes:select>
							</div>
							<a href="mailto:erahelp@ualberta.ca?subject=Collection Request" class="addmore" style="margin-bottom: 0.5em;"><fmt:message key="item.collection.request" /></a>
							<div class="list">
								<ul id="collection_list">
								<c:forEach items="${actionBean.item.collections}" var="col">
									<li class="floater3"> 
										<input type="hidden" name="cols" value="${col.id}" />${col.title}&nbsp;
										<a href="#" class="remove" onclick="removeCollection($(this).up()); return false;"><fmt:message key="item.collection.remove" /></a>
									</li>
								</c:forEach>
								</ul>
							</div>
						</li>
					</ul>
				</fieldset>
				</c:otherwise>
				</c:choose>

				<!-- step 3: license input form -->
				<fieldset class="who">
					<h3>
						<c:set var="step" value="${step + 1}" />
						<fmt:message key="item.workflow.license">
							<fmt:param value="${step}" />
						</fmt:message>
					</h3> 
					<security:secure roles="/item/create">
					<c:if test="${(state == 'Initial' || event == 'init' || empty item.properties.pid) || state == 'Reject'}">
						<stripes:submit name="save" class="save_button" style="margin-left: 0em; height: 1.2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
					</c:if>
					</security:secure>
					<p class="regular"><fmt:message key="license.description" /></p>
					<ul>
						<c:choose>
						<c:when test="${actionBean.form == 'thesis'}">
							<stripes:hidden name="item.properties.accessType" value="PUBLIC" />
							
							<!-- item emabargoed date -->
							<security:secure roles="/admin/embargoed">
								<c:set var="adminEmbargoed" value="true" />
							</security:secure>
							<c:choose>
							<c:when test="${adminEmbargoed && event == 'edit'}">
								<div id="embargo" class="embargo" style="float: none; font-size: 1em; margin-left: auto; width: 100%; margin-right: auto; margin-bottom: 1em;">
									<stripes:checkbox name="item.properties.embargoed" id="embargoed" class="check" onchange="setEmbargoedDate(); return true;" />
									<fmt:message key="properties.embargoed" />
									<div style="margin: 0.5em 0 0.5em 0;"><p><%= BaseActionBean.dublinCoreDatePattern %></p></div>
									<c:if test="${stripes:hasErrors(actionBean, 'item.properties.embargoedDate')}"><br /><stripes:errors field="item.properties.embargoedDate" /><br /></c:if>
									<stripes:text name="item.properties.embargoedDate" id="embargoedDate" class="smaller" style="width: 9em;" onchange="setEmbargoed(); return true;" />
					    			<!-- popup calendar -->
									<a href="#" style="font-weight: bold;" onClick="cal1x.select($('embargoedDate'),'anchor1x','yyyy/MM/dd'); return false;" title="Select Date from Calendar" name="anchor1x" id="anchor1x"><fmt:message key="calendar.select" /></a>
								</div>
							</c:when>
							<c:otherwise>
								<stripes:hidden name="item.properties.embargoed" />
								<stripes:hidden name="item.properties.embargoedDate" />
							</c:otherwise>
							</c:choose>

							<!-- thesis license input form -->
							<h4 class="license_choose" style="width: 100%;"><fmt:message key="license.thesis.title" /></h4>
							<li class="indented">
								<stripes:hidden name="licenseId" value="${actionBean.licenses[0].id}" />
								<div class="hint" style="float: none;">
									<p id="licenseText" style="font-size: 1em; width: 40em; background-color: #fff"></p>
								</div>
								<script type="text/javascript">
									new Ajax.Request('${ctx}/public/datastream/get/${actionBean.licenses[0].id}/LICENSE', {
										onComplete: function(transport) {
											$('licenseText').innerHTML = transport.responseText.replace('\n', '<br /><br />');
										}	
									});
								</script>
								<div style="text-align: center; padding: 1em 0 0 0; width: 40em;">
									<c:if test="${stripes:hasErrors(actionBean, 'accepted')}"><stripes:errors field="accepted" /><br /></c:if>
									<stripes:checkbox name="accepted" class="check" style="margin-right: 0.2em;" />
									<stripes:label for="license.acceptance.label" class="required" />
								</div>
							</li>

						</c:when>
						<c:otherwise>
						
							<!-- item access type -->
							<li>
								<stripes:label for="license.prompt.label" class="required" />
								<ul class="checkboxes">
									<li><label for="everyone"><stripes:radio name="item.properties.accessType" value="PUBLIC" checked="PUBLIC" onclick="showEmbargoedFields(this); return true;" />&nbsp;<fmt:message key="license.prompt.anyone" /></label></li>
									<li><label for="uofaonly"><stripes:radio name="item.properties.accessType" value="CCID_PROTECTED" checked="CCID_PROTECTED" onclick="showEmbargoedFields(this); return true;" />&nbsp;<fmt:message key="license.prompt.yes" /></label></li>
									<li><label for="noone"><stripes:radio name="item.properties.accessType" value="NOONE" checked="NOONE" onclick="showEmbargoedFields(this); return true;" />&nbsp;<fmt:message key="license.prompt.no" /></label></li>
								</ul>
							</li>
							
							<!-- item emabargoed date -->
							<div id="embargo" class="embargo"${actionBean.item.properties.accessType == 'NOONE' ? ' style="display: none;"' : ''}>
								<stripes:checkbox name="item.properties.embargoed" id="embargoed" class="check" onclick="setEmbargoedDate(); return true;" />
								<fmt:message key="properties.embargoed" />
								<div style="margin: 0.5em 0 0.5em 0;"><p><%= BaseActionBean.dublinCoreDatePattern %></p></div>
								<c:if test="${stripes:hasErrors(actionBean, 'item.properties.embargoedDate')}"><br /><stripes:errors field="item.properties.embargoedDate" /><br /></c:if>
								<stripes:text name="item.properties.embargoedDate" id="embargoedDate" class="smaller" style="width: 9em;" onchange="setEmbargoed(); return true;" />
				    			<!-- popup calendar -->
								<a href="#" style="font-weight: bold;" onClick="cal1x.select($('embargoedDate'),'anchor1x','yyyy/MM/dd'); return false;" title="Select Date from Calendar" name="anchor1x" id="anchor1x"><fmt:message key="calendar.select" /></a>
							</div>

							<!-- default license input form -->
							<h4 class="license_choose required"><fmt:message key="license.title" /></h4>
							<li class="indented">
							<stripes:errors field="licenseId" />
							<stripes:errors field="licenseFile" />
							<c:choose>
							<c:when test="${not empty actionBean.licenseFile}">
								<div>
									<span>${actionBean.licenseFile.fileName}${' '}[${actionBean.licenseFile.contentType}]</span>&nbsp;
									<input type="hidden" value="${actionBean.licenseFile.fileName}" />
									<a href="#" class="addmore" onclick="removeLicense(this); return false;"><fmt:message key="item.license.remove" /></a>
								</div>
							</c:when>
							<c:otherwise>
								<c:if test="${not empty actionBean.item.license}">
								<div>
									<span>${actionBean.item.license.label}${' '}[${actionBean.item.license.mimeType}]</span>
								</div>
								</c:if>
							</c:otherwise>
							</c:choose>
							</li>
							<li class="indented">
								<p><fmt:message key="license.list.label" /></p>
								<div class="license_menu">
									<stripes:select name="licenseId" id="licenseList" onchange="$('licenseFile').value = ''; $('licenseText').value = '';">
										<stripes:option value=""></stripes:option>
										<stripes:options-collection collection="${actionBean.licenses}" label="title" value="id"/>
									</stripes:select>
								</div>
							</li>
							<li class="indented">
								<p class="morepad"><fmt:message key="license.file.label" /></p><br/>
		                   		<stripes:file name="licenseFile" id="licenseFile" class="file" onchange="$('licenseList').selectedIndex = 0; $('licenseText').value = '';"  />
							</li>
							<a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=658303" class="licenceHelp" target="_blank"><fmt:message key="license.help" /></a>
							<li class="indented">
								<p class="morepad"><fmt:message key="license.text.label" /></p><br/>
								<stripes:textarea name="licenseText" id="licenseText" class="license_text" onchange="$('licenseList').selectedIndex = 0; $('licenseFile').value = '';" />
							</li>
						</c:otherwise>
						</c:choose>
					</ul>  
				</fieldset>

				<!-- step 4: file upload input form -->
				<fieldset class="upload_item">
					<h3>
						<c:set var="step" value="${step + 1}" />
						<fmt:message key="item.workflow.file">
							<fmt:param value="${step}" />
						</fmt:message>
					</h3> 
					<security:secure roles="/item/create">
					<c:if test="${(state == 'Initial' || event == 'init' || empty item.properties.pid) || state == 'Reject'}">
						<stripes:submit name="save" class="save_button" style="margin-left: 0em; height: 1.2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
					</c:if>
					</security:secure>
					<p class="regular"><fmt:message key="${actionBean.form}.datastream.description" /></p>
					<ul>
						<stripes:errors field="files" />
						<c:forEach items="${actionBean.item.datastreams}" var="ds" varStatus="sts">
						<li>
							<div>
							<c:if test="${ds.state == 'A'}">
								<span>${ds.label}${' '}[${ds.mimeType}]</span>&nbsp;
								<input type="hidden" name="dsIds" value="${ds.dsId}" />
								<a href="#" class="remove" onclick="removeDatastream(this); return false;"><fmt:message key="item.datastream.remove" /></a>
							</c:if>
							<c:if test="${ds.state == 'D'}">
								<span class="removed">${ds.label}${' '}[${ds.mimeType}]</span>&nbsp;
								<input type="hidden" name="removeDsIds" value="${ds.dsId}" />
								<a href="#" class="remove" onclick="restoreDatastream(this); return false;"><fmt:message key="item.datastream.restore" /></a>
							</c:if>
							</div>
						</li>
						</c:forEach>
						<c:forEach items="${actionBean.files}" var="file" varStatus="sts">
						<li>
							<div>
								<span>${file.fileName}${' '}[${file.contentType}]</span>&nbsp;
								<input type="hidden" value="${sts.index}" />
								<a href="#" class="remove" onclick="removeFile(this); return false;"><fmt:message key="item.datastream.remove" /></a>
							</div>
						</li>
						</c:forEach>
						<li>
							<stripes:label for="deposit.upload.label" />
							<c:if test="${stripes:hasErrors(actionBean, 'file')}"><br /><stripes:errors field="file" /></c:if>
	                    	<div id="datastream">
		                    	<stripes:file name="files[0]" id="files" class="file" />
	                    	</div>
							<c:if test="${actionBean.inputForm.fieldMap['uploadfile'].repeatable}">
								<a href="#" id="addDs" class="addmore" onclick="insertDatastream(this); return false;"><fmt:message key="deposit.add.file" /></a>
							</c:if>
						</li>
					</ul>
				</fieldset>

				<!-- footer message -->
				<fieldset class="save_item">
					<c:choose>
					<c:when test="${event == 'edit'}">
						<p class="regular"><fmt:message key="${actionBean.form}.item.edit" /></p>
					</c:when>
					<c:otherwise>
						<c:set var="formFooterId" value="${actionBean.inputForm.enabled ? 'form-enabled-footer' : 'form-disabled-footer'}" />
						${actionBean.inputForm.fieldMap[formFooterId].value.value}
					</c:otherwise>
					</c:choose>
				</fieldset>
				<!-- TODO: check form change before cancel -->
				<input type="hidden" name="<%= StripesConstants.URL_KEY_FLASH_SCOPE_ID %>" value="${actionBean.flashScopeId}" />
				<c:choose>
				<c:when test="${state == 'Initial' || event == 'init' || empty item.properties.pid}">
					<security:secure roles="/item/create">
					<c:choose>
					<c:when test="${actionBean.form == 'thesis'}">
						<c:choose>
						<c:when test="${actionBean.inputForm.enabled}">
							<stripes:submit name="submit" class="save_button" style="margin-left: 14em; height: 2em; border: 0;" onclick="bigWaiting(); return true;"><fmt:message key="button.submit" /></stripes:submit>
						</c:when>
						<c:otherwise>
							<span style="margin-left: 16em;" />
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
						<c:when test="${actionBean.inputForm.enabled}">
							<stripes:submit name="submit" class="deposit_button" style="margin-left: 12em; border: 0;" onclick="bigWaiting(); return true;"><fmt:message key="button.deposit" /></stripes:submit>
						</c:when>
						<c:otherwise>
							<span style="margin-left: 16em;" />
						</c:otherwise>
						</c:choose>
					</c:otherwise>
					</c:choose>
					<stripes:submit name="save" class="save_button" style="margin-left: 1em; height: 2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
					</security:secure>
					<a class="cancel_link" href="${ctx}${event == 'edit' ? '/action/myaccount/saveditems' : '/public/home'}"><fmt:message key="button.cancel" /></a>
				</c:when>
				<c:when test="${state == 'Reject'}">
					<security:secure roles="/item/create">
					<c:choose>
					<c:when test="${actionBean.form == 'thesis'}">
						<c:choose>
						<c:when test="${actionBean.inputForm.enabled}">
						<stripes:submit name="submit" class="save_button" style="margin-left: 14em; height: 2em; border: 0;" onclick="bigWaiting(); return true;"><fmt:message key="button.submit" /></stripes:submit>
						</c:when>
						<c:otherwise>
							<span style="margin-left: 16em;" />
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<stripes:submit name="submit" class="deposit_button" style="margin-left: 12em; border: 0;" onclick="bigWaiting(); return true;"><fmt:message key="button.deposit" /></stripes:submit>
					</c:otherwise>
					</c:choose>
					<stripes:submit name="save" class="save_button" style="margin-left: 1em; height: 2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
					</security:secure>
					<a class="cancel_link" href="${ctx}/action/myaccount/pendingitems"><fmt:message key="button.cancel" /></a>
				</c:when>
				<c:when test="${state == 'Archive' || !empty item.properties.pid}">
					<c:choose>
					<c:when test="${item.properties.embargoed}">
						<security:secure roles="/admin/embargoed">
						<stripes:submit name="modify" class="save_button" style="margin-left: 20em; height: 2em; width: 4em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save" /></stripes:submit>
						</security:secure>
					</c:when>
					<c:otherwise>
						<security:secure roles="/item/update">
						<stripes:submit name="modify" class="save_button" style="margin-left: 20em; height: 2em; width: 4em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save" /></stripes:submit>
						</security:secure>
					</c:otherwise>
					</c:choose>
					<a class="cancel_link" href="${ctx}/action/myaccount/items"><fmt:message key="button.cancel" /></a>
				</c:when>
				</c:choose>
			</stripes:form>
			</div>
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="html-foot">
		<div id="caldiv" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white; line-height: 1em;"></div>
	</stripes:layout-component>
</stripes:layout-render>
