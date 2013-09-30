<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>

<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<%@ include file="/jsp/layout/head.jspf" %>
	<stripes:layout-component name="html-head" />
</head>
<body>
	<%@ include file="/jsp/layout/noscript.jspf" %>
	<div class="wrapper">
		<div class="headerwrap">
			<stripes:layout-component name="header">
				<%@ include file="/jsp/layout/header.jsp" %>
			</stripes:layout-component> 
       	</div>
   		<div class="main">
			<stripes:layout-component name="menubar" />
			<stripes:layout-component name="contents" />
   		</div>
		<%-- <div class="push"></div> --%>
   	</div>
	<stripes:layout-component name="footer">
		<%@ include file="/jsp/layout/footer.jsp" %>
	</stripes:layout-component>	
</body>
</html>
</stripes:layout-definition>