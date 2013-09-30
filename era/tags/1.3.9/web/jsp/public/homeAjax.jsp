<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${event == 'getWelcome'}">
		<ul id="welcome" class="user_links">
			<c:choose>
				<c:when test="${not empty user}">
					<li><a href="${httpServerUrl}${ctx}/action/myaccount" class="name">${user.firstName}${" "}${user.lastName}</a></li>
					<li><a href="${httpServerUrl}${ctx}/public/logout"><fmt:message key="header.logout" /></a></li>
				</c:when>
				<c:when test="${not empty CCIDUser}">
					<li><a href="">${CCIDUser.firstName}${" "}${CCIDuser.lastName}</a></li>
					<li><a href="${httpsServerUrl}${ctx}/public/login"><fmt:message key="header.login" /></a></li>
					<li><a href="${httpServerUrl}${ctx}/public/logout"><fmt:message key="header.logout" /></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="${httpsServerUrl}${ctx}/public/login"><fmt:message key="header.login" /></a></li>
					<li><a href="${httpsServerUrl}${ctx}/public/register"><fmt:message key="header.register" /></a></li>
				</c:otherwise>
			</c:choose>
			<li><a href="${httpServerUrl}${ctx}/public/cart" class="cart"><fmt:message key="header.cart" /> &nbsp;<span id="cartItemCount">
			<script type="text/javascript">getCartItemCount();</script></span></a></li>
		</ul>
	</c:when>
</c:choose>
