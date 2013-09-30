<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates operator drop-down list." %>
<%@ attribute name="index" type="java.lang.String" required="true" description="The index of operator." %>

<stripes:select name="operators[${index}]">
	<stripes:option value="AND" label="AND" />
	<stripes:option value="OR" label="OR" />
	<stripes:option value="NOT" label="NOT" />
</stripes:select>