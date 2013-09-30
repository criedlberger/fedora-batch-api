<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editItem.jsp $
   $Id: editItem.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Item - Edit Item" active="0">

    <stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				getOwnerNameAutoComplete();
				getCollectionList();
			});

			function getOwnerNameAutoComplete() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/admin/user/getUsersByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							addOwner(selected);
							$('autocomplete').value = '';
							$('autocomplete').focus();
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
				new Ajax.Updater('collections', ctx + '/action/deposit/getCollectionList', {
					parameters: { 
						coms: comIds 
					}
				});
			}
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="item.edit.header" /></h2>
			<div class="subheader">
				<p><fmt:message key="item.edit.subheader" /></p>
			</div>

			<stripes:errors globalErrorsOnly="true" />
	

			<div class="deposit_metadata">
			<stripes:form id="itemForm" action="/action/item" focus="owner">
				<stripes:hidden name="item.properties.pid" />
				<security:secure roles="/admin/depositor">
					<fieldset class="what">    
						<h3><fmt:message key="item.owner.header" /></h3><stripes:errors field="owner" />
						<p class="regular"><fmt:message key="item.owner.subheader" /></p>
						<ul style="margin-bottom: 0;">
							<li class="floater1" style="margin-bottom: 0;">
								<stripes:label for="item.owner.name" />
								<input type="hidden" id="username" />
								<div class="menus">
									<stripes:text name="owner" id="autocomplete" style="width: 22em;" />
									<div id="autocomplete_choices" class="autocomplete"></div>
								</div>
							</li>
						</ul>
						<ul id="owner_list" style="clear: both;">
							<c:forEach items="${actionBean.item.properties.owners}" var="owner" varStatus="sts">
							<li class="floater3">
								<stripes:label for="empty"/>
								<input type="hidden" name="usernames" value="${owner.username}"  />${owner.firstName}${' '}${owner.lastName}&nbsp;
	                   			<a href="#" class="addmore" onclick="removeOwner(this); return false;"><fmt:message key="item.owner.remove" /></a>
							</li>
							</c:forEach>
						</ul>
					</fieldset>
				</security:secure>
	
					<fieldset class="what">    
						<h3><fmt:message key="item.workflow.step1" /></h3>
						<p class="regular"><fmt:message key="item.workflow.step1.sub" /></p>
						<ul>
							<li class="floater1">
								<stripes:label for="dublinCore.type" class="required"/>
								<stripes:errors field="item.properties.contentModel" />
								<div class="menus">
									<stripes:select name="item.properties.contentModel">
										<option value=""><fmt:message key="deposit.select" /></option>
										<stripes:options-collection collection="${context.allContentModels}" value="contentModel" />
									</stripes:select>
								</div>   
							</li>
							<li class="floater2">
								<stripes:label for="dublinCore.language" class="required"/>
								<stripes:hidden value="language" name="item.dublinCore.fields[11].name" />
								<div class="menus">
									<stripes:select name="item.dublinCore.fields[11].values[0]">
				                   		<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.DublinCoreLanguage" />
									</stripes:select>
								</div>
							</li>
							<li>
								<stripes:label for="dublinCore.title" class="required"/>
								<stripes:errors field="item.dublinCore.fields[1].values[0]" />
								<div>
									<stripes:hidden value="title" name="item.dublinCore.fields[1].name" />
									<stripes:text name="item.dublinCore.fields[1].values[0]" id="dublinCore.fields[1].values[0]" />
								</div>
							</li>
							<li>
								<stripes:label for="dublinCore.creator" class="required"/>
								<stripes:errors field="item.dublinCore.fields[2].values" />
								<stripes:hidden value="creator" name="item.dublinCore.fields[2].name" />
								<c:choose>
								<c:when test="${empty actionBean.item.dublinCore.fields[2].values}">
									<div id="dublinCore.2">
										<input type="text" name="item.dublinCore.fields[2].values" />
										<p><fmt:message key="dublinCore.creator.description" /></p>
									</div>
									<a href="#" id="add2" class="addmore" onclick="addDublinCore(this, '2'); return false;"><fmt:message key="deposit.add.creator" /></a>
								</c:when>
								<c:otherwise>
	                				<c:forEach items="${actionBean.item.dublinCore.fields[2].values}" var="value" varStatus="sts">
									<div id="dublinCore.2">
										<input type="text" name="item.dublinCore.fields[2].values"  value="${actionBean.item.dublinCore.fields[2].values[sts.index]}" />
										<c:choose>
										<c:when test="${sts.index == 0}">
											<p><fmt:message key="dublinCore.creator.description" /></p> 
										</c:when>
										<c:otherwise>
											<a href="#" class="addmore" onclick="$(this).up().remove(); return false;" /><fmt:message key="item.dublinCore.remove" /></a>
										</c:otherwise> 
										</c:choose>
									</div>
									</c:forEach>
									<a href="#" id="add2" class="addmore" onclick="addDublinCore(this, '2'); return false;"><fmt:message key="deposit.add.creator" /></a>
								</c:otherwise>
								</c:choose>
							</li>
							<li>
								<stripes:label for="dublinCore.contributor"/>
								<stripes:hidden value="contributor" name="item.dublinCore.fields[6].name" />
								<c:choose>
								<c:when test="${empty actionBean.item.dublinCore.fields[6].values}">
									<div id="dublinCore.6">
										<input type="text" name="item.dublinCore.fields[6].values" />
										<p><fmt:message key="dublinCore.contributor.description" /></p>
									</div>
									<a href="#" id="add2" class="addmore" onclick="addDublinCore(this, '6'); return false;"><fmt:message key="deposit.add.contributor" /></a>
								</c:when>
								<c:otherwise>
	                				<c:forEach items="${actionBean.item.dublinCore.fields[6].values}" var="value" varStatus="sts">
									<div id="dublinCore.6">
										<input type="text" name="item.dublinCore.fields[6].values"  value="${actionBean.item.dublinCore.fields[6].values[sts.index]}" />
										<c:choose>
										<c:when test="${sts.index == 0}">
											<p><fmt:message key="dublinCore.contributor.description" /></p> 
										</c:when>
										<c:otherwise>
											<a href="#" class="addmore" onclick="$(this).up().remove(); return false;" /><fmt:message key="item.dublinCore.remove" /></a>
										</c:otherwise> 
										</c:choose>
									</div>
									</c:forEach>
									<a href="#" id="add6" class="addmore" onclick="addDublinCore(this, '6'); return false;"><fmt:message key="deposit.add.contributor" /></a>
								</c:otherwise>
								</c:choose>
							</li>
							<li>
								<stripes:label for="dublinCore.subject" class="required"/>
								<stripes:errors field="item.dublinCore.fields[3].values" />
								<stripes:hidden value="subject" name="item.dublinCore.fields[3].name" />
								<c:choose>
								<c:when test="${empty actionBean.item.dublinCore.fields[3].values}">
									<div id="dublinCore.3">
										<input type="text" name="item.dublinCore.fields[3].values" />
										<p><fmt:message key="dublinCore.subject.description" /></p>
									</div>
									<a href="#" id="add2" class="addmore" onclick="addDublinCore(this, '3'); return false;"><fmt:message key="deposit.add.subject" /></a>
								</c:when>
								<c:otherwise>
	                				<c:forEach items="${actionBean.item.dublinCore.fields[3].values}" var="value" varStatus="sts">
									<div id="dublinCore.3">
										<input type="text" name="item.dublinCore.fields[3].values"  value="${actionBean.item.dublinCore.fields[3].values[sts.index]}" />
										<c:choose>
										<c:when test="${sts.index == 0}">
											<p><fmt:message key="dublinCore.subject.description" /></p> 
										</c:when>
										<c:otherwise>
											<a href="#" class="addmore" onclick="$(this).up().remove(); return false;" /><fmt:message key="item.dublinCore.remove" /></a>
										</c:otherwise> 
										</c:choose>
									</div>
									</c:forEach>
									<a href="#" id="add3" class="addmore" onclick="addDublinCore(this, '3'); return false;"><fmt:message key="deposit.add.subject" /></a>
								</c:otherwise>
								</c:choose>
							</li>
							<p class="coverage_question"><fmt:message key="dublinCore.coverage.comment" /></p>
							<li class="indented">
								<stripes:label for="dublinCore.coverage.time" class="coverage_date"/>
								<div id="dublinCore.13[0]">
									<stripes:hidden value="coverage" name="item.dublinCore.fields[13].name" />
									<stripes:text name="item.dublinCore.fields[13].values[0]" id="dublinCore.fields[13].values[0]" class="small" />
									<p><fmt:message key="dublinCore.coverage.time.description" /></p>
								</div>
								<br /><br />
								<stripes:label for="dublinCore.coverage.place"/> 
								<div id="dublinCore.13[0]">
									<stripes:hidden value="coverage" name="item.dublinCore.fields[13].name" />
									<stripes:text name="item.dublinCore.fields[13].values[1]" id="dublinCore.fields[13].values[1]" class="small"/>
									<p><fmt:message key="dublinCore.coverage.place.description" /></p>
								</div>

							</li>
							<li>
								<stripes:label for="dublinCore.description"/>
								<div>
									<stripes:hidden value="description" name="item.dublinCore.fields[4].name" />
									<stripes:textarea name="item.dublinCore.fields[4].values[0]" id="dublinCore.fields[4].values[0]" />
									<p><fmt:message key="dublinCore.description.comment" /></p>
								</div>
							</li>
							<li>
								<stripes:label for="dublinCore.relation.citation"/>
								<div id="dublinCore.12[0]">
									<stripes:hidden value="relation" name="item.dublinCore.fields[12].name" />
									<stripes:textarea name="item.dublinCore.fields[12].values[0]" id="dublinCore.fields[12].values[0]" />
									<p><fmt:message key="dublinCore.relation.citation.description" /></p>
								</div>

							</li>
							<li>
								<stripes:label for="dublinCore.date"/>
								<div>
									<stripes:hidden value="date" name="item.dublinCore.fields[7].name" />
									<stripes:text name="item.dublinCore.fields[7].values[0]" id="dublinCore.fields[7].values[0]" />
									<p><fmt:message key="dublinCore.date.description" /></p>
								</div>
							</li>
							<li> 
								<stripes:label for="dublinCore.source"/>
								<div>
									<stripes:hidden value="source" name="item.dublinCore.fields[10].name" />
									<stripes:text name="item.dublinCore.fields[10].values[0]" id="dublinCore.fields[10].values[0]" />
									<p><fmt:message key="dublinCore.source.comment" /></p>
								</div>
							</li>
							<li>
								<stripes:label for="dublinCore.relation"/>
								<stripes:errors field="item.dublinCore.fields[12].values[1]" />
								<div  id="dublinCore.12[1]">
									<stripes:hidden value="relation" name="item.dublinCore.fields[12].name" />
									<stripes:text name="item.dublinCore.fields[12].values[1]" id="dublinCore.fields[12].values[1]" />
									<p><fmt:message key="dublinCore.relation.description" /></p>
								</div>
							</li>
						</ul>
					</fieldset>
					
					<fieldset class="where">
						<h3><fmt:message key="item.workflow.step2" /></h3> 
						<ul>
							<li> 
								<stripes:label for="item.community" class="required"/>
								<stripes:errors field="coms" />
								<div class="menus">
									<stripes:select name="itemComs" id="item.coms" onchange="addCommunity(this); return false;">
										<option value=""><fmt:message key="deposit.select" /></option>
										<stripes:options-collection collection="${actionBean.communities}" label="title" value="id"/>
									</stripes:select>
								</div>
								<a href="mailto:erahelp@ualberta.ca?subject=Community Request" class="addmore"><fmt:message key="item.community.request" /></a>
								<div class="list">
								<ul id="community_list">
								<c:forEach items="${actionBean.item.communities}" var="com" varStatus="sts">
									<li class="floater3"> 
										<input type="hidden" name="coms" value="${com.id}" />${com.title}&nbsp;
										<a href="#" class="addmore" onclick="removeCommunity($(this).up()); return false;"><fmt:message key="item.community.remove" /></a>
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
								<a href="mailto:erahelp@ualberta.ca?subject=Collection Request" class="addmore"><fmt:message key="item.collection.request" /></a>
								<div class="list">
									<ul id="collection_list">
									<c:forEach items="${actionBean.item.collections}" var="col">
										<li class="floater3"> 
											<input type="hidden" name="cols" value="${col.id}" />${col.title}&nbsp;
											<a href="#" class="addmore" onclick="removeCollection($(this).up()); return false;"><fmt:message key="item.collection.remove" /></a>
										</li>
									</c:forEach>
									</ul>
								</div>
							</li>
						</ul>
					</fieldset>

					<fieldset class="who">
						<h3><fmt:message key="item.workflow.step3" /></h3> 	 
						<p class="regular"><fmt:message key="license.description" /></p>
						<ul>
							<li>
								<stripes:label for="license.prompt.label" class="required" />
								<ul class="checkboxes">
									<li><label for="everyone"><stripes:radio name="item.properties.accessType" value="PUBLIC" checked="PUBLIC" />&nbsp;<fmt:message key="license.prompt.anyone" /></label></li>
									<li><label for="uofaonly"><stripes:radio name="item.properties.accessType" value="CCID_PROTECTED" checked="CCID_PROTECTED" />&nbsp;<fmt:message key="license.prompt.yes" /></label></li>
									<li><label for="noone"><stripes:radio name="item.properties.accessType" value="NOONE" checked="NOONE" />&nbsp;<fmt:message key="license.prompt.no" /></label></li>
								</ul>
							</li>
							<div class="embargo">
								<stripes:checkbox name="item.properties.embargoed" class="check" />
								<fmt:message key="properties.embargoed" />
								<stripes:errors field="item.properties.embargoedDate" />
								<br />
								<stripes:text name="item.properties.embargoedDate" class="smaller" />
								<p><%= BaseActionBean.dublinCoreDatePattern %></p>
							</div>
							<h4 class="license_choose"><fmt:message key="license.title" /></h4>
							<li class="indented">
							<stripes:errors field="license" />
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
										<option value="0"></option>
										<stripes:options-collection collection="${licenses}" label="title" value="id"/>
									</stripes:select>
								</div>
							</li>
							<li class="indented">
								<p class="morepad"><fmt:message key="license.file.label" /></p><br/>
		                   		<stripes:file name="licenseFile" id="licenseFile" class="file" onchange="$('licenseList').selectedIndex = 0; $('licenseText').value = '';"  />
							</li>
							<a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=658303" class="licenceHelp"><fmt:message key="license.help" /></a>
							<li class="indented">
								<p class="morepad"><fmt:message key="license.text.label" /></p><br/>
								<stripes:textarea name="licenseText" id="licenseText" class="license_text" onchange="$('licenseList').selectedIndex = 0; $('licenseFile').value = '';" />
							</li>
						</ul>  
					</fieldset>

					<fieldset class="upload_item">
						<h3><fmt:message key="item.workflow.step4" /></h3>
						<p class="regular"><fmt:message key="datastream.description" /></p>
						<ul>
							<stripes:errors field="files" />
							<c:forEach items="${actionBean.item.datastreams}" var="ds" varStatus="sts">
							<li>
								<div>
								<c:if test="${ds.state == 'A'}">
									<span>${ds.label}${' '}[${ds.mimeType}]</span>&nbsp;
									<input type="hidden" value="${ds.dsId}" />
									<a href="#" class="addmore" onclick="removeDatastream(this); return false;"><fmt:message key="item.datastream.remove" /></a>
								</c:if>
								<c:if test="${ds.state == 'D'}">
									<span class="removed">${ds.label}${' '}[${ds.mimeType}]</span>&nbsp;
									<input type="hidden" name="removeDsIds" value="${ds.dsId}" />
									<a href="#" class="addmore" onclick="restoreDatastream(this); return false;"><fmt:message key="item.datastream.restore" /></a>
								</c:if>
								</div>
							</li>
							</c:forEach>
							<c:forEach items="${actionBean.files}" var="file" varStatus="sts">
							<li>
								<div>
									<span>${file.fileName}${' '}[${file.contentType}]</span>&nbsp;
									<input type="hidden" value="${sts.index}" />
									<a href="#" class="addmore" onclick="removeFile(this); return false;"><fmt:message key="item.datastream.remove" /></a>
								</div>
							</li>
							</c:forEach>
							<li>
								<stripes:label for="deposit.upload.label" /><stripes:errors field="file" />
		                    	<div id="datastream">
			                    	<stripes:file name="files[0]" id="files" class="file" />
		                    	</div>
								<a href="#" id="addDs" class="addmore" onclick="insertDatastream(this); return false;"><fmt:message key="deposit.add.file" /></a>
							</li>
						</ul>
					</fieldset>
					<input type="hidden" name="<%= StripesConstants.URL_KEY_FLASH_SCOPE_ID %>" value="${actionBean.flashScopeId}" />
					<stripes:submit name="save" class="save_button" onclick="bigWaiting(); return true;"><fmt:message key="button.save" /></stripes:submit>
					<!-- TODO: check form change before cancel -->
					<a class="cancel_link" href="${ctx}/public/view/item/${actionBean.item.properties.pid}"><fmt:message key="button.cancel" /></a>
			</stripes:form>
			</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>
