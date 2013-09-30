<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/home.jsp $
   $Id: home.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:useActionBean binding="/public/home" var="actionBean" event="start" />

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home" active="${navbarHome}">

	<stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				stats();
			});
		</script>
		<script type="text/javascript">
			function toggleFavorite(element, pid, act) {
			<c:if test="${empty user}">
				var next = '/public/home';
				location.href = '${ctx}/action/favorite/' + act + '/' + pid + '?next=' + next;
			</c:if>
			<c:if test="${not empty user}">
				new Ajax.Updater($(element), '${ctx}/action/favorite/' + act + '/' + pid, {
					onComplete: function(transport) {
						new Ajax.Updater('accountSummary', ctx + '/action/myaccount/getAccountSummary');
					}
				});
			</c:if>
			}

			function subscribe(element, pid, type) {
			<c:if test="${empty user}">
				var next = '/public/home';
				location.href = '${ctx}/action/subscription/subscribe/' + pid + '/' + type + '?next=' + next;
			</c:if>
			<c:if test="${not empty user}">
				new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
					parameters: { 'subscription.pid': pid, 'subscription.type': type },
					onComplete: function(transport) {
						new Ajax.Updater('accountSummary', '${ctx}/action/myaccount/getAccountSummary');
					}
				});
			</c:if>
			}	
			new SlideShow('listOfImages');
			
			function stats() {
				new Ajax.PeriodicalUpdater({ success: 'stats' }, '${ctx}/public/browse/stats', { method: 'get', frequency: 60 });
			}
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="home_subheader" style="clear: both;">
			<p>
				<fmt:message key="home.subheader" /><stripes:link href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648950"	
					class="read_more left_pad"><fmt:message key="home.readMore" /></stripes:link>
				<div id="stats"></div>
			</p>
		</div>

		<div class="right">
		<div class="featuredinfo">
		<ul id="listOfImages">
			<li><img src="${httpServerUrl}${ctx}/images/slides/collections.jpg" alt="collections" /></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/reports.jpg" alt="reports" /></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/articles.jpg" alt="articles" /></li>
			<li><a href="${httpServerUrl}${ctx}/public/community"> <img src="${httpServerUrl}${ctx}/images/slides/communities.jpg"
				alt="communities" /> </a></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/data.jpg" alt="data" /></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/images.jpg" alt="images" /></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/sound.jpg" alt="sound" /></li>
			<li><a href="${httpServerUrl}${ctx}/public/researcher"> <img src="${httpServerUrl}${ctx}/images/slides/researchers.jpg"
				alt="researchers" /> </a></li>
			<li><img src="${httpServerUrl}${ctx}/images/slides/movies.jpg" alt="movies" /></li>
		</ul>
		</div>
		<div class="howitworks"><img src="${ctx}/images/ERA-graphic.jpg" alt="How ERA works diagram" /></div>
		</div>
		<%--
		<br />
		<div id="stats" class="full_box" style="clear: both;"></div>
		--%>
	</stripes:layout-component>
</stripes:layout-render>
