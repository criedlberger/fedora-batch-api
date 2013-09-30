<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates item metadata input form fields." %>
<c:set var="state" value="${actionBean.item.properties.workflowState}" />

<!-- metadata input form start -->
<fieldset class="what">    
	<h3><fmt:message key="item.workflow.step1" /></h3>
	<security:secure roles="/item/create">
	<c:if test="${(state == 'Initial' || event == 'init' || empty item.properties.pid) || state == 'Reject'}">
		<stripes:submit name="save" class="save_button" style="margin-left: 0em; height: 1.2em; width: 12em;" onclick="bigWaiting(); return true;"><fmt:message key="button.save.thesis" /></stripes:submit>
	</c:if>
	</security:secure>
	<ul>
	<c:forEach items="${actionBean.inputFields}" var="fld" varStatus="status">
		<li style="clear: both;${fld.inputType.value == 'hidden' ? ' display: none;' : ''}" class="${fld.indent ? 'indented' : ''}">
			<c:set var="fields" value="${actionBean.fieldMap[fld.key]}" />
			<c:if test="${not empty fld.label.value}">
			<label class="${fld.validate.required ? 'required' : ''}${fld.inputType.value == 'hidden' ? ' hidden' : ''}"><ir:label label="${fld.label}" /></label>
			</c:if>
			<c:choose>
			<c:when test="${fld.inputType.value == 'textarea'}">
				<c:choose>
				<c:when test="${empty fields}">
				<div id="field.${status.index}00" class="field">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:textarea name="fields[${status.index}00].value" value="${value}" style="${fld.style}" />
					<ir:hint label="${fld.hint}" />
				</div>
				</c:when>
				<c:otherwise>
				<c:forEach items="${fields}" var="field" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.index}" />
				<div id="field.${status.index}${i}" class="field">
					<div><stripes:errors field="fields[${status.index}${i}].value" /></div>
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.value" value="${fld.attribute.value}" />
					<stripes:textarea name="fields[${status.index}${i}].value" value="${field.value}" style="${fld.style}" />
					<c:if test="${sts.index == 0}"><ir:hint label="${fld.hint}" /></c:if>
					<c:if test="${sts.index > 0}">
						<a href="" onclick="$(this).up().remove(); return false;" class="remove">
							<fmt:message key="item.dublinCore.remove" />
						</a>
					</c:if>
				</div>
				</c:forEach>
				</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${fld.inputType.value == 'dropdown'}">
				<c:choose>
				<c:when test="${empty fields}">
				<div id="field.${status.index}00" class="menus field">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:select name="fields[${status.index}00].value" value="${value}">
					<c:forEach items="${actionBean.valuePairsMap[fld.inputType.valuePairsName].pairs}" var="pair">
						<stripes:option value="${pair.storedValue}"><ir:label label="${pair.displayedValue}" /></stripes:option>
					</c:forEach>
					</stripes:select>
					<ir:hint label="${fld.hint}" />
				</div>
				</c:when>
				<c:otherwise>
				<c:forEach items="${fields}" var="field" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.index}" />
				<div id="field.${status.index}${i}" class="menus field">
					<div><stripes:errors field="fields[${status.index}${i}].value" /></div>
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.value" value="${fld.attribute.value}" />
					<stripes:select name="fields[${status.index}${i}].value" value="${field.value}">
					<c:forEach items="${actionBean.valuePairsMap[fld.inputType.valuePairsName].pairs}" var="pair">
						<stripes:option value="${pair.storedValue}"><ir:label label="${pair.displayedValue}" /></stripes:option>
					</c:forEach>
					</stripes:select>
					<c:if test="${sts.index == 0}"><ir:hint label="${fld.hint}" /></c:if>
					<c:if test="${sts.index > 0}">
						<a href="" onclick="$(this).up().remove(); return false;" class="remove">
							<fmt:message key="item.dublinCore.remove" />
						</a>
					</c:if>
				</div>
				</c:forEach>
				</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${fld.inputType.value == 'qualdrop_value'}">
				<c:choose>
				<c:when test="${empty fields}">
				<div id="field.${status.index}00" class="field">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${actionBean.valuePairsMap[fld.inputType.valuePairsName].dcTerm}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:select style="margin-top: 0;" name="fields[${status.index}00].attribute.value" value="${value}">
					<c:forEach items="${actionBean.valuePairsMap[fld.inputType.valuePairsName].pairs}" var="pair">
						<stripes:option value="${pair.storedValue}"><ir:label label="${pair.displayedValue}" /></stripes:option>
					</c:forEach>
					</stripes:select>
					<div><stripes:text style="margin-left: 3px;" name="fields[${status.index}00].value" /></div>
					<ir:hint label="${fld.hint}" />
				</div>
				</c:when>
				<c:otherwise>
				<c:forEach items="${fields}" var="field" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.index}" />
				<div id="field.${status.index}${i}" class="field">
					<div><stripes:errors field="fields[${status.index}${i}].value" /></div>
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${actionBean.valuePairsMap[fld.inputType.valuePairsName].dcTerm}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:select style="margin-top: 0;" name="fields[${status.index}${i}].attribute.value" value="${value}">
					<c:forEach items="${actionBean.valuePairsMap[fld.inputType.valuePairsName].pairs}" var="pair">
						<stripes:option value="${pair.storedValue}"><ir:label label="${pair.displayedValue}" /></stripes:option>
					</c:forEach>
					</stripes:select>
					<div><stripes:text style="margin-left: 3px;" name="fields[${status.index}${i}].value" /></div>
					<ir:hint label="${fld.hint}" />
				</div>
				</c:forEach>
				</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${fld.inputType.value == 'hidden'}">
				<c:choose>
				<c:when test="${empty fields}">
				<div id="field.${status.index}00">
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:hidden name="fields[${status.index}00].value" value="${value}" />
				</div>
				</c:when>
				<c:otherwise>
				<c:forEach items="${fields}" var="field" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.index}" />
				<div id="field.${status.index}${i}">
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.value" value="${fld.attribute.value}" />
					<stripes:hidden name="fields[${status.index}${i}].value" value="${field.value}" />
				</div>
				</c:forEach>
				</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${fld.inputType.value == 'readonly'}">
				<div id="field.${status.index}00">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:hidden name="fields[${status.index}00].value" value="${value}" />${value}
					<ir:hint label="${fld.hint}" />
				</div>
			</c:when>
			<c:when test="${fld.inputType.value == 'daterange_value'}">
				<div id="field.${status.index}00">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:dateRangeValue var="pair" valuePairs="${actionBean.valuePairsMap[fld.inputType.valuePairsName]}" />
					<c:if test="${actionBean.item.properties.workflowState == 'Archive'}">
						<security:secure roles="/item/update">
						<stripes:select name="fields[${status.index}00].value" value="${pair.storedValue}">
						<c:forEach items="${actionBean.valuePairsMap[fld.inputType.valuePairsName].pairs}" var="pair">
							<stripes:option value="${pair.storedValue}"><ir:label label="${pair.displayedValue}" /></stripes:option>
						</c:forEach>
						</stripes:select>
						<c:set var="dateOptions" value="true" />
						</security:secure>
					</c:if>
					<c:if test="${!dateOptions}">
						<stripes:hidden name="fields[${status.index}00].value" value="${pair.storedValue}" />
						<ir:label label="${pair.displayedValue}" />
					</c:if>
					<c:set var="dateOptions" value="false" />
					<ir:hint label="${fld.hint}" />
				</div>
			</c:when>
			<c:when test="${fld.inputType.value == 'text'}">
				<c:choose>
				<c:when test="${empty fields}">
				<div id="field.${status.index}00" class="field">
					<div><stripes:errors field="fields[${status.index}00].value" /></div>
					<stripes:hidden name="fields[${status.index}00].fieldName" value="fields[${status.index}00].value" />
					<stripes:hidden name="fields[${status.index}00].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}00].qualifier" value="${fld.dcQualifier}" />
					<stripes:hidden name="fields[${status.index}00].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}00].attribute.value" value="${fld.attribute.value}" />
					<ir:label label="${fld.value}" var="value" />
					<stripes:text name="fields[${status.index}00].value" value="${value}" class="${fld.indent ? 'small' : ''}" style="${fld.style}" />
					<ir:hint label="${fld.hint}" />
				</div>
				<c:if test="${fld.display > 0}">
				<c:forEach begin="1" end="${fld.display - 1}" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.count}" />
				<div id="field.${status.index}${i}" class="field">
					<div><stripes:errors field="fields[${status.index}${i}].value" /></div>
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.value" value="${fld.attribute.value}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:text name="fields[${status.index}${i}].value" value="${value}" class="${fld.indent ? 'small' : ''}" style="${fld.style}" />
					<a href="" onclick="$(this).up().remove(); return false;" class="remove">
						<fmt:message key="item.dublinCore.remove" />
					</a>
				</div>
				</c:forEach>
				</c:if>
				</c:when>
				<c:otherwise>
				<c:forEach items="${fields}" var="field" varStatus="sts">
				<fmt:formatNumber var="i" pattern="00" value="${sts.index}" />
				<div id="field.${status.index}${i}" class="field">
					<div><stripes:errors field="fields[${status.index}${i}].value" /></div>
					<stripes:hidden name="fields[${status.index}${i}].fieldName" value="fields[${status.index}${i}].value" />
					<stripes:hidden name="fields[${status.index}${i}].name" value="${fld.dcElement}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.name" value="${fld.attribute.name}" />
					<stripes:hidden name="fields[${status.index}${i}].attribute.value" value="${fld.attribute.value}" />
					<stripes:hidden name="fields[${status.index}${i}].qualifier" value="${fld.dcQualifier}" />
					<stripes:text name="fields[${status.index}${i}].value" value="${field.value}" class="${fld.indent ? 'small' : ''}" style="${fld.style}" />
					<c:if test="${sts.index == 0}"><ir:hint label="${fld.hint}" /></c:if>
					<c:if test="${sts.index > 0}">
						<a href="" onclick="$(this).up().remove(); return false;" class="remove">
							<fmt:message key="item.dublinCore.remove" />
						</a>
					</c:if>
				</div>
				</c:forEach>
				</c:otherwise>
				</c:choose>
			</c:when>
			</c:choose>
			<c:choose>
			<c:when test="${fld.inputType.value == 'label' || fld.inputType.value == 'file' || fld.inputType.value == 'message'	
				|| fld.inputType.value == 'hidden' || fld.inputType.value == 'readonly'}">
			</c:when>
			<c:otherwise>
				<c:if test="${fld.repeatable}">
					<a href="" id="addmore" class="addmore" onclick="addMetadataField(this); return false;">
						<fmt:message key="inputform.${fn:replace(fld.key, ':', '_')}.more" var="label" />
						<c:choose>
						<c:when test="${fn:startsWith(label, '???')}"><fmt:message key="item.editDublinCore.add" /></c:when>
						<c:otherwise>${label}</c:otherwise>
						</c:choose>
					</a>
				</c:if>
			</c:otherwise>
			</c:choose>
		</li>
	</c:forEach>
	</ul>
</fieldset>
<!-- metadata input form end -->
