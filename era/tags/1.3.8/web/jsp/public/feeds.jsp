<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.8/web/jsp/public/feeds.jsp $
   $Id: feeds.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - RSS Feeds" active="${navbarHome}">

	<stripes:layout-component name="html-head">
		<link rel="alternate" type="application/rss+xml" title="<fmt:message key="feed.item.title" />" href="${httpServerUrl}${ctx}/public/feed/item/${feedType}">
		<link rel="alternate" type="application/rss+xml" title="<fmt:message key="feed.collection.title" />" href="${httpServerUrl}${ctx}/public/feed/collection/${feedType}">
		<link rel="alternate" type="application/rss+xml" title="<fmt:message key="feed.community.title" />" href="${httpServerUrl}${ctx}/public/feed/community/${feedType}">
		<c:if test="${not empty context.user}">
		<link rel="alternate" type="application/rss+xml" title="<fmt:message key="feed.subscription.title" />" href="${httpServerUrl}${ctx}/public/feed/subscription/${feedType}/${context.user.username}">
		</c:if>
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><a href="${ctx}/public/community"><stripes:label for="feeds.header" /></a></h2>
			<div class="subheader"><fmt:message key="feeds.subheader" /></div>
	
			<stripes:errors /><stripes:messages />

			<div class="full_box">
				<table cellpadding="4" style="margin: 10px 0 10px 0;">
				<tr>
					<th style="width: 300px;">We currently offer the following feeds</th>
					<th style="text-align: center;"><img src="${ctx}/images/feed-atom.gif" style="vertical-align: text-bottom;" /></th>
					<th style="text-align: center;"><img src="${ctx}/images/feed-atom.gif" style="vertical-align: text-bottom;" /></th>
					<th style="text-align: center;"><img src="${ctx}/images/feed-rss.gif" style="vertical-align: text-bottom;" /></th>
					<th style="text-align: center;"><img src="${ctx}/images/feed-rdf.gif" style="vertical-align: text-bottom;" /></th>
					<th style="text-align: center;"><img src="${ctx}/images/feed-xml.gif" style="vertical-align: text-bottom;" /></th>
				</tr>
				<c:forEach items="${actionBean.featureFeeds}" var="feed" varStatus="status">
				<tr>
					<td>${feed.title}</td>
					<td><a href="${ctx}/public/feed/feature/atom_1.0/${status.index}" target="${feed.title}">Atom 1.0</a></td>
					<td><a href="${ctx}/public/feed/feature/atom_0.3/${status.index}" target="${feed.title}">Atom 0.3</a></td>
					<td><a href="${ctx}/public/feed/feature/rss_2.0/${status.index}" target="${feed.title}">RSS 2.0</a></td>
					<td><a href="${ctx}/public/feed/feature/rss_1.0/${status.index}" target="${feed.title}">RSS 1.0</a></td>
					<td><a href="${ctx}/public/feed/feature/rss_0.92/${status.index}" target="${feed.title}">RSS 0.92</a></td>
				</tr>
				</c:forEach>
				<tr>
					<td><fmt:message key="feed.item.title" /></td>
					<td><a href="${ctx}/public/feed/item/atom_1.0" target="<fmt:message key="feed.item.title" />">Atom 1.0</a></td>
					<td><a href="${ctx}/public/feed/item/atom_0.3" target="<fmt:message key="feed.item.title" />">Atom 0.3</a></td>
					<td><a href="${ctx}/public/feed/item/rss_2.0" target="<fmt:message key="feed.item.title" />">RSS 2.0</a></td>
					<td><a href="${ctx}/public/feed/item/rss_1.0" target="<fmt:message key="feed.item.title" />">RSS 1.0</a></td>
					<td><a href="${ctx}/public/feed/item/rss_0.92" target="<fmt:message key="feed.item.title" />">RSS 0.92</a></td>
				</tr>
				<tr>
					<td><fmt:message key="feed.collection.title" /></td>
					<td><a href="${ctx}/public/feed/collection/atom_1.0" target="<fmt:message key="feed.collection.title" />">Atom 1.0</a></td>
					<td><a href="${ctx}/public/feed/collection/atom_0.3" target="<fmt:message key="feed.collection.title" />">Atom 0.3</a></td>
					<td><a href="${ctx}/public/feed/collection/rss_2.0" target="<fmt:message key="feed.collection.title" />">RSS 2.0</a></td>
					<td><a href="${ctx}/public/feed/collection/rss_1.0" target="<fmt:message key="feed.collection.title" />">RSS 1.0</a></td>
					<td><a href="${ctx}/public/feed/collection/rss_0.92" target="<fmt:message key="feed.collection.title" />">RSS 0.92</a></td>
				</tr>
				<tr>
					<td><fmt:message key="feed.community.title" /></td>
					<td><a href="${ctx}/public/feed/community/atom_1.0" target="<fmt:message key="feed.community.title" />">Atom 1.0</a></td>
					<td><a href="${ctx}/public/feed/community/atom_0.3" target="<fmt:message key="feed.community.title" />">Atom 0.3</a></td>
					<td><a href="${ctx}/public/feed/community/rss_2.0" target="<fmt:message key="feed.community.title" />">RSS 2.0</a></td>
					<td><a href="${ctx}/public/feed/community/rss_1.0" target="<fmt:message key="feed.community.title" />">RSS 1.0</a></td>
					<td><a href="${ctx}/public/feed/community/rss_0.92" target="<fmt:message key="feed.community.title" />">RSS 0.92</a></td>
				</tr>
				<c:if test="${not empty context.user}">
				<tr>
					<td><fmt:message key="feed.subscription.title" /></td>
					<td><a href="${ctx}/public/feed/subscription/atom_1.0/${context.user.username}" target="<fmt:message key="feed.subscription.title" />">Atom 1.0</a></td>
					<td><a href="${ctx}/public/feed/subscription/atom_0.3/${context.user.username}" target="<fmt:message key="feed.subscription.title" />">Atom 0.3</a></td>
					<td><a href="${ctx}/public/feed/subscription/rss_2.0/${context.user.username}" target="<fmt:message key="feed.subscription.title" />">RSS 2.0</a></td>
					<td><a href="${ctx}/public/feed/subscription/rss_1.0/${context.user.username}" target="<fmt:message key="feed.subscription.title" />">RSS 1.0</a></td>
					<td><a href="${ctx}/public/feed/subscription/rss_0.92/${context.user.username}" target="<fmt:message key="feed.subscription.title" />">RSS 0.92</a></td>
				</tr>
				</c:if>
				</table>
			</div>

		</div>
    </stripes:layout-component>
</stripes:layout-render>
