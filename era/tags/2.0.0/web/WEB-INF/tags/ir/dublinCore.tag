<%--
	$Id: dublinCore.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag produces item embedded Dublin Core metadata record." %>
<%@ attribute name="inputForms" type="ca.ualberta.library.ir.model.inputform.InputForms" required="true" description="The item input forms configuration." %>
<%@ attribute name="fields" type="java.util.List" required="true" description="The metadata fields to display." %>
<%@ attribute name="item" type="ca.ualberta.library.ir.model.fedora.Item" required="true" description="The item to display." %>

<!-- item embedded Dublin Core metadata record -->
<link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />
<link rel="schema.dcterms" href="http://purl.org/dc/terms/" />
<link rel="schema.eratrems" href="http://era.library.ualberta.ca/eraterms" />
<link rel="schema.thesis" href="http://www.ndltd.org/standards/metadata/etdms/1.0/" />
<c:choose>
	<c:when test="${not empty item.metadata}">
		<%-- DCQ metadata record --%>
		<c:forEach items="${item.metadata.fields}" var="field" varStatus="sts">
			<c:choose>
				<%-- look up type of item field	--%>
				<c:when test="${fld == 'dc:type'}">
					<fmt:message var="value" key="ContentModel.${field.value}" />
				</c:when>
				<%-- look up graduation date field --%>
				<c:when test="${fld == 'eraterms:graduationdate'}">
					<ir:label label="${inputForms.valuePairsMap['thesis-graduation-dates'].pairMap[field.value].displayedValue}" var="displayedValue" />
					<c:set var="value" value="${displayedValue}" />
				</c:when>
				<%-- set field value --%>
				<c:otherwise>
					<c:set var="value" value="${field.value}" />
				</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${not empty field.attribute && field.attribute.name == 'xsi:type'}">
				<meta name="${fn:replace(field.name, ':', '.')}" scheme="${fn:replace(field.attribute.value, ':', '.')}" content="${fnx:escapeHtml(value)}" />
			</c:when>
			<c:otherwise>
				<meta name="${fn:replace(field.name, ':', '.')}" content="${fnx:escapeHtml(value)}" />
			</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<%-- DC metadata record --%>
		<c:forEach items="${item.dublinCore.fields}" var="field" varStatus="sts">
			<c:forEach items="${field.values}" var="value" varStatus="status">
				<meta name="${fn:replace(field.name, ':', '.')}" content="${fnx:escapeHtml(value)}" />
			</c:forEach>
		</c:forEach>
	</c:otherwise>
</c:choose>