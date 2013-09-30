<%@ include file="taglibs.tagf"%>
<%@ tag description="This tag creates a QR Code for the item handle URL."%>
<%@ attribute name="url" type="java.lang.String" required="true" description="The item handle URL."%>
<c:if test="${actionBean.item.properties.state == 'A' && properties['item.qrcode.enabled']}">
	<div class="view_box">
		<p style="font-size: 1em;">
			<img style="float: left; margin-right: 10px;"
				src="http://chart.apis.google.com/chart?chs=120x120&cht=qr&chld=|0&chl=${fnx:encodeUrl(url)}" width="100" height="100"
				alt="QRCode for: ${url}" title="QRCode for: ${url}" />
			<span style="font-size: 0.9em;">
				<em><a href="http://en.wikipedia.org/wiki/QR_Code" target="_blank">QR Code</a></em> for this page URL: <a href="${url}">${url}</a> 
			</span>
		</p>
	</div>
</c:if>
