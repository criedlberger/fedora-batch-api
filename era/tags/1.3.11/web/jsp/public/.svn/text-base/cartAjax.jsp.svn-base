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
			<c:set var="fld" value="${result.fieldValueMap}" />
			<c:set var="flds" value="${result.fieldValuesMap}" />
			<li style="padding-top: 1em;">
 				<a href="${ctx}/public/view/item/${fld['PID']}">${fld['dc.title']}</a>	
				<c:if test="${not empty fld['dc.creator']}"><br /><fmt:message key="by" />${' '} ${fld['dc.creator']}</c:if>    
           		<c:if test="${not empty fld['dc.description']}"><br />${fnx:trim(fld['dc.description'], 60)}</c:if>
           		<%-- 
          		<div class="cartbutton" style="float: right;"><ir:addToCart pid="${fld['PID']}" itemIndex="0" /></div> 
           		--%>
			</li>
		</c:forEach>
		</c:if>
		</ul>
		<%-- 
		<c:if test="${fn:length(context.cart) > 0}">
		<a href="" class="delete_button auto_width" style="clear: both; float: right; margin-top: 0.5em;" onclick="removeAllCartItems(); return false;">
			<fmt:message key="cart.removeall.label" />
		</a>
		</c:if>
		--%>
	</c:when>
</c:choose>
