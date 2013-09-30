<%@ include file="taglibs.tagf"%>
<%@ tag description="This tag creates a QR Code for the item handle URL."%>
<%@ attribute name="url" type="java.lang.String" required="true" description="The item handle URL."%>
<c:if test="${actionBean.item.properties.state == 'A'}">
	<div class="view_box">
		<p style="font-size: 1em;">
			<img style="float: left; margin-right: 10px;"
				src="http://chart.apis.google.com/chart?chs=120x120&cht=qr&chld=|0&chl=${fnx:encodeUrl(url)}" width="120" height="120"
				alt="QRCode: ${url}" title="QRCode: ${url}" />
				<a href="http://en.wikipedia.org/wiki/QR_Code" target="_blank">QR Code</a> for this page URL 
		</p>
		<%-- 
		<br />
		<p>
			<script src="http://connect.facebook.net/en_US/all.js#xfbml=1"></script><fb:like href="${url}" layout="standard" show_faces="true" width="450"></fb:like> 
		</p>
		--%>
	</div>
</c:if>
