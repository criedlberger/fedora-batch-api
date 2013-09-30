<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:choose>
	<c:when test="${event == 'subscribe'}">
		<a href="" class="subscribe" onclick="unsubscribe(this.parentNode, '${actionBean.subscription.id}'); return false;"><fmt:message key="subscription.unsubscribe.${actionBean.subscription.type}" /></a>
	</c:when>
	
	<c:when test="${event == 'unsubscribe'}">
		<a href="" class="subscribe" onclick="subscribe(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;"><fmt:message key="subscription.subscribe.${actionBean.subscription.type}" /></a>
	</c:when>

	<c:when test="${event == 'getSubscriptionStatus'}">
		<c:choose>
		<c:when test="${not empty actionBean.subscription.id}">
			<a href="" class="subscribe" onclick="unsubscribe(this.parentNode, '${actionBean.subscription.id}'); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="subscription.unsubscribe.${actionBean.subscription.type}" /></a>
		</c:when>
		<c:otherwise>
			<a href="" class="subscribe" onclick="subscribe(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="subscription.subscribe.${actionBean.subscription.type}" /></a>
		</c:otherwise>
		</c:choose>
	</c:when>
	
	<c:when test="${event == 'subscribeInfo'}">
		<%-- 
		<div style="height: 1.5em;">
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		--%>
		<a href="" class="subscribe" onclick="unsubscribeInfo(this.parentNode, '${actionBean.subscription.id}'); return false;"><fmt:message key="subscription.unsubscribe.${actionBean.subscription.type}" /></a>
	</c:when>
	
	<c:when test="${event == 'unsubscribeInfo'}">
		<%-- 
		<div style="height: 1.5em;">
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		--%>
		<a href="" class="subscribe" onclick="subscribeInfo(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;"><fmt:message key="subscription.subscribe.${actionBean.subscription.type}" /></a>
	</c:when>

	<c:when test="${event == 'getSubscriptionInfo'}">
		<%-- 
		<div style="height: 1.5em;">
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		--%>
		<c:choose>
		<c:when test="${not empty actionBean.subscription.id}">
			<a href="" class="subscribe" onclick="unsubscribeInfo(this.parentNode, '${actionBean.subscription.id}'); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="subscription.unsubscribe.${actionBean.subscription.type}" /></a>
		</c:when>
		<c:otherwise>
			<a href="" class="subscribe" onclick="subscribeInfo(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;" class="${empty context.user ? 'disabled_link' : ''}"><fmt:message key="subscription.subscribe.${actionBean.subscription.type}" /></a>
		</c:otherwise>
		</c:choose>
	</c:when>

	<c:when test="${event == 'getSubscriptionWithNotify'}">
		<div style="height: 3em;">
			<fmt:message key="subscription.subscribed" />: <fmt:formatDate pattern="${actionBean.dateFormatShort}" value="${actionBean.subscription.createdDate}" /><br/>
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		<c:if test="${empty actionBean.subscription.id}">
			<a href="" onclick="subscribeWithNotify(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;"><fmt:message key="subscription.subscribe" /></a>
		</c:if>
		<c:if test="${not empty actionBean.subscription.id}">
			<a href="" onclick="unsubscribeWithNotify(this.parentNode, '${actionBean.subscription.id}'); return false;"><fmt:message key="subscription.unsubscribe" /></a>
		</c:if>
	</c:when>
	
	<c:when test="${event == 'subscribeWithNotify'}">
		<div style="height: 3em;">
			<fmt:message key="subscription.subscribed" />: <fmt:formatDate pattern="${actionBean.dateFormatShort}" value="${actionBean.subscription.createdDate}" /><br/>
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		<c:if test="${empty actionBean.subscription.id}">
			<a href="" onclick="subscribeWithNotify(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;"><fmt:message key="subscription.subscribe" /></a>
		</c:if>
		<c:if test="${not empty actionBean.subscription.id}">
			<a href="" onclick="unsubscribeWithNotify(this.parentNode, '${actionBean.subscription.id}'); return false;"><fmt:message key="subscription.unsubscribe" /></a>
		</c:if>
	</c:when>

	<c:when test="${event == 'unsubscribeWithNotify'}">
		<div style="height: 3em;">
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
		<a href="" onclick="subscribeWithNotify(this.parentNode, '${actionBean.subscription.pid}', ${actionBean.subscription.type}); return false;"><fmt:message key="subscription.subscribe" /></a>
	</c:when>

	<c:when test="${event == 'getNoOfSubscribers'}">
		<div style="height: 1.5em;">
			<c:if test="${actionBean.numberOfSubscribers > 0}">
				${actionBean.numberOfSubscribers}${' '}<fmt:message key="subscription.subscribers" />
			</c:if>
		</div>
	</c:when>
</c:choose>
