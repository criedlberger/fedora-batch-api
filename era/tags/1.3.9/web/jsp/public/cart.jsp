<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/cart.jsp $
   $Id: cart.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="My Cart">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
		<h2><stripes:label for="cart.header" /></h2>
		<div class="subheader"><fmt:message key="cart.subheader" /></div>
		<stripes:messages /><stripes:errors />
		<c:if test="${actionBean.resultRows > 0}">
			<ir:cartItemList results="${actionBean.results}" />
			<div style="clear: both;" />
			<div class="cart_form_actions">
				<a href="${ctx}/public/cart/removeAll" class="delete_button auto_width"><fmt:message key="cart.removeall.label" /></a>
				<a href="${ctx}/public/checkout" class="download" onclick="bigWaiting(); return true;" style="float: none;"><fmt:message key="cart.downloadall.label" /></a>
			</div>
		</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
