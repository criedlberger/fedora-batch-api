<%--
	$Id: metadata.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag produces item metadata fields." %>
<%@ attribute name="inputForms" type="ca.ualberta.library.ir.model.inputform.InputForms" required="true" description="The item input forms configuration." %>
<%@ attribute name="fields" type="java.util.List" required="true" description="The metadata fields to display." %>
<%@ attribute name="item" type="ca.ualberta.library.ir.model.fedora.Item" required="true" description="The item to display." %>

<!-- item metadata fields -->
<c:choose>
	<c:when test="${not empty item.metadata}">
		<%-- DCQ metadata fields --%>
		<c:forEach items="${fields}" var="fld">
			<li>
				<%-- get metadata field from metadata fieldMap --%>
				<c:set var="fields" value="${item.metadata.fieldMap[fld]}" />
				<c:forEach items="${fields}" var="field" varStatus="sts">
					<c:choose>
						<%-- look up type of item field	--%>
						<c:when test="${fld == 'dc:type'}">
							<fmt:message var="value" key="ContentModel.${field.value}" />
							<c:if test="${fn:startsWith(value, '???')}">
								<c:set var="value" value="${field.value}" />
							</c:if>
						</c:when>
						<%-- format submitted, accepted date fields --%>
						<c:when test="${fld == 'dcterms:datesubmitted.xsi:type.dcterms:W3CDTF' || fld == 'dcterms:dateaccepted.xsi:type.dcterms:W3CDTF'}">
							<fmt:parseDate pattern="${W3CDTFDatePattern}" value="${field.value}" var="date" />
							<fmt:formatDate value="${date}" var="value" pattern="${dateFormat}" />
						</c:when>
						<%-- look up graduation date field --%>
						<c:when test="${fld == 'eraterms:graduationdate'}">
							<ir:label label="${inputForms.valuePairsMap['thesis-graduation-dates'].pairMap[field.value].displayedValue}" var="displayedValue" />
							<c:set var="value" value="${displayedValue}" />
						</c:when>
						<%-- look up graduation date field --%>
						<c:when test="${fld == 'dc:format.xsi:type.dcterms:IMT'}">
							<fmt:message var="value" key="${field.value}.name" />
							<c:if test="${fn:startsWith(value, '???')}">
								<c:set var="value" value="${field.value}" />
							</c:if>
						</c:when>
						<%-- look up language field --%>
						<c:when test="${fld == 'dc:language.xsi:type.dcterms:ISO639-3'}">
							<fmt:message var="value" key="dc-language.${field.value}" />
							<c:if test="${fn:startsWith(value, '???')}">
								<c:set var="value" value="${field.value}" />
							</c:if>
						</c:when>
						<%-- set field value --%>
						<c:otherwise>
							<c:set var="value" value="${field.value}" />
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${sts.index == 0}">
							<label><fmt:message key="${fn:replace(fld, ':', '-')}" />:</label>${value}
						</c:when>
						<c:otherwise><br />${value}</c:otherwise>
					</c:choose>
				</c:forEach>
			</li>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<%-- DC metadata fields --%>
		<c:forEach items="${item.dublinCore.fields}" var="field" varStatus="sts">
			<li>
				<c:forEach items="${field.values}" var="value" varStatus="status">
				<c:choose>
					<c:when test="${status.index == 0}">
						<label><fmt:message key="dublinCore.${field.name}" />:</label>${value}<br />
					</c:when>
					<c:otherwise>${value}<br /></c:otherwise>
				</c:choose>
				</c:forEach>
			</li>
		</c:forEach>
	</c:otherwise>
</c:choose>
