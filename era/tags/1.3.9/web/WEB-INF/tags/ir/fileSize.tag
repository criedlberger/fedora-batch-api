<%--
	$Id: fileSize.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	File Size Taglib
	--------------
	Attributes: 
		- size			: The file size in bytes.
		- var			: The request scope variable for the file size display format.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates a file size display format(xG xM xK xB) from bytes." %>

<%@ attribute name="size" type="java.lang.Long" required="true" description="The file size in bytes." %>
<%@ attribute name="var" type="java.lang.String" description="The request scope variable for file size display format." %>
<% 
	long size = ((Long) jspContext.getAttribute("size")).longValue();
	long g = 0, m = 0, k = 0, b = 0;
	if (size > 1024) {
		k = size / 1024;
		b = size % 1024;
		if (k > 1024) {
			m = k / 1024;
			k = k % 1024;
		}
		if (m > 1024) {
			g = m / 1024;
			m = m % 1024;
		}
	} else {
		b = size;
	}
	jspContext.setAttribute("fileSize", (g > 0 ? g + "G " : "") + (m > 0 ? m + "M " : "") + (k > 0 ? k + "K " : "") + b + "B");
%>
<c:if test="${empty var}">${fileSize}</c:if>
<c:if test="${not empty var}">
	<%
		String var = (String) jspContext.getAttribute("var");
		String fileSize = (String) jspContext.getAttribute("fileSize");
		request.setAttribute(var, fileSize);
	%>
</c:if>
