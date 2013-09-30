<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/supportedFileTypes.jsp $
   $Id: supportedFileTypes.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:useActionBean binding="/public/home" var="actionBean" />
<stripes:layout-render name="/jsp/layout/popup.jsp">
    <stripes:layout-component name="contents">
		
		<div class="popup_box">
			<fmt:message key="supportedFileTypes.description" />
			<table class="display">
				<tr>
					<th><fmt:message key="supportedFileTypes.fileName" /></th>
					<th><fmt:message key="supportedFileTypes.fileExt" /></th>
					<th><fmt:message key="supportedFileTypes.contentType" /></th>
				</tr>
				<c:forEach items="${actionBean.supportedFileTypes}" var="contentType" varStatus="sts">
				<tr class="${sts.count % 2 == 1 ? 'odd' : 'even'}">
					<td><fmt:message key="${contentType}.name" /></td>
					<td><fmt:message key="${contentType}.ext" /></td>
					<td>${contentType}</td>
				</tr>
				</c:forEach>
			</table>
		</div>

    </stripes:layout-component>
</stripes:layout-render>
