<%@ include file="/jsp/layout/taglibs.jspf" %>
<fmt:message key="cart.add.title" var="add" />
<fmt:message key="cart.remove.title" var="remove" />
<c:choose>
	<c:when test="${event == 'add'}">
		<a href="#" onclick="removeFromCart(this.parentNode, '${actionBean.pid}'); return false;" title="${remove}"><fmt:message key="cart.remove.label" /></a>
	</c:when>
	<c:when test="${event == 'remove'}">
		<a href="#" onclick="addToCart(this.parentNode, '${actionBean.pid}'); return false;" title="${add}"><fmt:message key="cart.add.label" /></a>
	</c:when>
	<c:when test="${event == 'getItemCount'}">
		<c:if test="${fn:length(actionBean.cart) > 0}">(${fn:length(actionBean.cart)})</c:if>
	</c:when>
	<c:when test="${event == 'getCartDetails'}">
		<h2><fmt:message key="cart.header" /></h2>
		<p><fmt:message key="cart.subheader" /></p>
		<stripes:messages /><stripes:errors />
		<ul>
		<c:if test="${actionBean.resultRows > 0}">
		<c:forEach items="${actionBean.results}" var="result" varStatus="status">
			<li style="padding-top: 1em;">
 				<a href="${ctx}/public/view/item/${result['PID']}">${result['dc.title'][0]}</a>	
				<c:if test="${not empty result['dc.creator']}"><br /><fmt:message key="by" />${' '} ${result['dc.creator'][0]}</c:if>    
           		<c:if test="${not empty result['dc.description']}"><br />${fnx:trim(result['dc.description'][0], 60)}</c:if>
			</li>
		</c:forEach>
		</c:if>
		</ul>
	</c:when>
</c:choose>
