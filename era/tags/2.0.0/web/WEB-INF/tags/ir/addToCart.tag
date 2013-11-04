<%--
	$Id: addToCart.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	AddToCart Taglib
	----------------
	Attributes: 
		- pid			: The Item PID.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates an AddToCart link." %>
<%@ tag import="java.util.*" %>

<%@ attribute name="pid" type="java.lang.String" required="true" description="The Item PID." %>
<%@ attribute name="itemIndex" type="java.lang.Integer" required="false" description="The Item Index." %>
<%--
	Boolean added = Boolean.FALSE;
	List<String> cart = (List<String>) session.getAttribute("cart");
	if (cart != null && cart.contains(jspContext.getAttribute("pid"))) {
		added = Boolean.TRUE;		
	}
	jspContext.setAttribute("added", added);		
--%>
<fmt:message key="cart.add.title" var="add" />
<fmt:message key="cart.remove.title" var="remove" />
<c:if test="${properties['google.analytics.enabled']}">
	<c:set var="onclick" value="_gaq.push(['_trackEvent', 'Shopping Cart', 'Click', 'Add to cart']);" />
</c:if>
<c:choose>
<c:when test="${not empty cart && fn:contains(cart, pid)}">
	<span id="addToCart.${itemIndex}" class="cartbutton">
		<a href="#" onclick="removeFromCart(this.parentNode, '${pid}'); return false;" title="${remove}"><fmt:message key="cart.remove.label" /></a>
	</span>
</c:when>
<c:otherwise>
	<span id="addToCart.${itemIndex}" class="cartbutton">
		<a href="#" onclick="${onclick} addToCart(this.parentNode, '${pid}'); return false;" title="${add}"><fmt:message key="cart.add.label" /></a>
	</span>
</c:otherwise>
</c:choose>