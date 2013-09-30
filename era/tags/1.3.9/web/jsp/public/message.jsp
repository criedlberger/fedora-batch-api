<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/message.jsp $
   $Id: message.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:forEach items="${actionBean.messages}" var="message">
	<ul class="admin_message ${message.type == 0 ? 'system' : 'admin'}">
		<li>${message.message}</li>
	</ul>
</c:forEach>
 
