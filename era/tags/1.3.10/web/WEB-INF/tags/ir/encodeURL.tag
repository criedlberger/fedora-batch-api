<%--
	$Id: encodeURL.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
	
	@deprecated: Use function fnx:encodeUrl() instead, to be remove.

	Encode URL Taglib
	-----------------
	Attributes: 
		- url			: The URL to encode.
		- var			: The request scope variable for the encoded URL.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag encodes url value." %>
<%@ attribute name="url" type="java.lang.String" description="The URL to encode." %>
<%@ attribute name="var" type="java.lang.String" description="The request scope variable for the encoded URL." %>
<%
	String var = (String) jspContext.getAttribute("var");
	String url = (String) jspContext.getAttribute("url");
	String eUrl = URLEncoder.encode(url, "ISO-8859-1");
	if (var == null) {
		out.print(eUrl);
	} else {
		request.setAttribute(var, eUrl);
	}
%>
