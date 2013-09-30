<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/2.0.0/web/jsp/public/featuresAjax.jsp $
   $Id: featuresAjax.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<input type="hidden" id="feedCount" value="${fn:length(actionBean.featureFeeds)}" />
<table width="100%" style="padding-bottom: 10px;">
<c:forEach items="${actionBean.featureFeeds}" var="feed" varStatus="status">
<tr>
	<td>
		<link rel="alternate" type="application/rss+xml" title="${feed.title}" href="${ctx}/public/feed/feature/rss_2.0/${stauts.index}" />
		<h3>
			<img id="fo.${status.index}" src="${ctx}/images/outline_collapse.gif" onclick="toggleFeed('${status.index}'); return false;" style="vertical-align: text-top; display: inline; float: left;" />
			<a href="${ctx}/public/feed/feature/rss_2.0/${stauts.index}" target="RSS Feed" style="text-decoration: none;"><img id="sub_rss" src="${ctx}/images/rss_small.gif" style="margin: 2px 4px 0 0; display: inline; float: right; border: 0;" title="${feed.title} RSS 2.0 Feed" /></a>
			<a href="" onclick="toggleFeed(${status.index}); return false;">${feed.title}</a>
		</h3>
		<div id="feed.${status.index}" style="display: none;">
		<c:forEach items="${feed.entries}" var="entry" end="${actionBean.feedItems - 1}">
		<div style="margin: 6px 0 0 4px; border-bottom: solid 1px #eee;">
			<a href="${entry.link}" target="Feature Feeds"><b>${entry.title}</b></a> <span style="font-size: 11px; color: #808080; margin-left: 2px; letter-spacing: -0.3px;"><fmt:formatDate pattern="MMM d, yyyy h:mm a" value="${entry.publishedDate}" /></span>
			<c:forEach items="${entry.contents}" var="content">
				<div style="margin: 4px 0 4px 4px;">${content.value}</div>
			</c:forEach>
			<div style="margin: 4px 0 4px 4px;">${entry.description.value}</div>
		</div>
		</c:forEach>
		</div>
	</td>
</tr>
</c:forEach>
</table>