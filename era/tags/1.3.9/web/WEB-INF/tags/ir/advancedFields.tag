<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates advanced field drop-down list." %>
<%@ attribute name="index" type="java.lang.String" required="true" description="The index of advance field." %>
<%@ attribute name="value" type="java.lang.String" required="false" description="The default option." %>

<fmt:message key="advanced.field.all" var="all" />
<fmt:message key="dublinCore.title" var="title" />
<fmt:message key="dublinCore.creator" var="creator" />
<fmt:message key="dublinCore.subject" var="subject" />
<fmt:message key="dublinCore.description" var="description" />
<fmt:message key="dublinCore.publisher" var="publisher" />
<fmt:message key="dublinCore.contributor" var="contributor" />
<fmt:message key="dublinCore.date" var="date" />
<fmt:message key="dublinCore.type" var="type" />
<fmt:message key="dublinCore.format" var="format" />
<fmt:message key="dublinCore.source" var="source" />
<fmt:message key="dublinCore.language" var="language" />
<fmt:message key="dublinCore.relation" var="relation" />
<fmt:message key="dublinCore.coverage" var="coverage" />
<fmt:message key="dublinCore.rights" var="rights" />

<stripes:select name="fields[${index}]" value="${value}">
	<stripes:option value="dc.all" label="${all}" />
	<stripes:option value="dc.title" label="${title}" />
	<stripes:option value="dc.creator" label="${creator}" />
	<stripes:option value="dc.subject" label="${subject}" />
	<stripes:option value="dc.description" label="${description}" />
	<stripes:option value="dc.publisher" label="${publisher}" />
	<stripes:option value="dc.contributor" label="${contributor}" />
	<stripes:option value="dc.date" label="${date}" />
	<stripes:option value="dc.type" label="${type}" />
	<stripes:option value="dc.format" label="${format}" />
	<stripes:option value="dc.source" label="${source}" />
	<stripes:option value="dc.language" label="${language}" />
	<stripes:option value="dc.relation" label="${relation}" />
	<stripes:option value="dc.coverage" label="${coverage}" />
	<stripes:option value="dc.rights" label="${rights}" />
</stripes:select>
