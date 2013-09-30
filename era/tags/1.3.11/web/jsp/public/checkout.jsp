<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.11/web/jsp/public/checkout.jsp $
   $Id: checkout.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Checkout Process" active="${navbarHome}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() { 
				onLoad(); 
			});
			
			function onLoad() {
				getCartItemCount();
				location.href = '${httpServerUrl}${ctx}${actionBean.url}';
			}
		</script>
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		
		<div class="full_box">
			<h2><stripes:label for="checkout.header" /></h2>
			<div class="subheader">
			<%-- 
			<fmt:message key="checkout.subheader"><fmt:param value="${actionBean.downloadFileSize}" />
			</fmt:message>
			--%>
			</div>
			<stripes:messages /><stripes:errors />
			<div style="clear: both; text-align: center; padding-bottom: 20px;">
				<fmt:message key="checkout.completed"><fmt:param value="${httpServerUrl}${ctx}${actionBean.url}" /></fmt:message>
			</div>

			<c:if test="${fn:length(actionBean.oversizeItems) > 0}">
			<div>
				<h2><stripes:label for="checkout.oversize" /></h2>
				<ir:cartItemList results="${actionBean.oversizeItems}" type="oversize" />
			</div>
			</c:if>

			<c:if test="${fn:length(actionBean.missingItems) > 0}">
			<div>
				<h2><stripes:label for="checkout.missingItems" /></h2>
				<ir:cartItemList results="${actionBean.missingItems}" type="missing" />
			</div>
			</c:if>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
