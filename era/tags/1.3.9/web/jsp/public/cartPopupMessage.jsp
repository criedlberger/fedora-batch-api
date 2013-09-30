<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/cartPopupMessage.jsp $
   $Id: cartPopupMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/popup.jsp" title="My Cart - Empty Cart">

    <stripes:layout-component name="contents">
		<div class="popup_box">
			<h2><fmt:message key="cart.header" /></h2>
			<div class="subheader">
				<fmt:message key="cart.subheader"><fmt:param value="${fn:length(cart)}" /></fmt:message>
			</div>
			<div>
				<stripes:errors /><stripes:messages />
			</div>
		</div>
       	<script type="text/javascript">window.setTimeout('Modalbox.hide()', 8000);</script>
    </stripes:layout-component>
    
</stripes:layout-render>
