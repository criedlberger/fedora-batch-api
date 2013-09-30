<%@ include file="/jsp/layout/taglibs.jspf"%>
<h2><a href="#">Account Statistics</a></h2>
<table style="width: 100%">
<tr>
	<td style="border-bottom: 1px dotted #ccc;">
		<span style="float: left;">
			<img src="${ctx}/images/bookmark.gif" style="vertical-align: text-bottom; padding-right: 4px;" /> Bookmarks
		</span>
		<span style="float: right; padding-right: 3px;">${actionBean.bookmarkStats}</span>
	</td>
</tr> 
<tr>
	<td style="border-bottom: 1px dotted #ccc;">
		<span style="float: left;">
			<img src="${ctx}/images/favorite_on.gif" style="vertical-align: text-bottom; padding-right: 4px;" /> Favorites
		</span>
		<span style="float: right; padding-right: 3px;">${actionBean.favoriteStats}</span>
	</td>
</tr> 
<tr>
	<td style="border-bottom: 1px dotted #ccc;">
		<span style="float: left;">
			<img src="${ctx}/images/subscription.gif" style="vertical-align: text-bottom; padding-right: 4px;" /> Subscriptions
		</span>
		<span style="float: right; padding-right: 3px;">${actionBean.subscriptionStats}</span>
	</td>
</tr> 
</table>
