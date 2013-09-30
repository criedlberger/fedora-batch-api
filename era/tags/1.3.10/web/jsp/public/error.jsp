<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/public/error.jsp $
   $Id: error.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ page isErrorPage="true" %>
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:set var="statusCode" value="${pageContext.errorData.statusCode}" />
<c:set var="throwable" value="${pageContext.errorData.throwable}" />

<html>
<head>
	<title>Application Error!</title>
</head>
<body>
	<div class="full_box global_error">
		<div class="errors">
			<h2><fmt:message key="error${statusCode}.header" /></h2>
			<ul>
				<li><fmt:message key="error${statusCode}.message" /></li>
				<%-- <li>${throwable}</li> --%>
			</ul>
		</div>
		<br style="clear: both;" />
		<div style="margin-top: 1em;">
			<fmt:message key="errors.unauthorized.footer"><fmt:param>${adminEmail}</fmt:param></fmt:message>
		</div>
	</div>		
</body>
</html>
		
