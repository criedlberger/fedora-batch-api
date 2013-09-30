<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/maintenance.jsp $
   $Id: maintenance.jsp 5485 2012-08-13 17:40:46Z pcharoen $
   $Revision: 5485 $ $Date: 2012-08-13 11:40:46 -0600 (Mon, 13 Aug 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<stripes:layout-render name="/jsp/layout/system.jsp">
	<stripes:layout-component name="contents">
		<h3>Actions:</h3>
		<ul>
			<security:secure roles="/dev/main/tools">
			<li style="margin-top: 2em;">
				<div class="left_col">
					<a style="font-weight: bold;" href="/dev/main/init">init</a>
				</div> 
				<div class="right_col">
					<fmt:message key="dev.main.${event}.description" />
				</div>
			</li>
			<%-- 
			<li style="margin-top: 2em;">
				<div class="left_col">
					<a style="font-weight: bold;" href="/monitoring" target="monitoring">monitoring</a>
				</div> 
				<div class="right_col">
					System monitoring
				</div>
			</li>
			<li style="margin-top: 2em;">
				<div class="left_col">
					<a style="font-weight: bold;" href="/fedora/monitoring" target="fedora_monitoring">fedoramonitoring</a>:
				</div> 
				<div class="right_col">
					Fedora monitoring
				</div>
			</li>
			--%>
			<li style="margin-top: 2em;">
				<div class="left_col">
					<a style="font-weight: bold;" href="/manager/html" target="manager">manager</a>
				</div> 
				<div class="right_col">
					Tomcat Manager
				</div>
			</li>
			<li style="margin-top: 2em;">
				<div class="left_col">
					<a style="font-weight: bold;" href="/solr-fedora/admin" target="solr-admin">solr admin</a>
				</div> 
				<div class="right_col">
					Solr Admin
				</div>
			</li>
			</security:secure>
			<c:forEach items="${actionBean.events}" var="event">
				<security:secure roles="/dev/main/${event}">
				<li style="margin-top: 2em; clear: both;">
					<div class="left_col">
						<a style="font-weight: bold;" href="/dev/main/${event}" 
							onclick="return confirm('Do you really want to process \'${event}\'?');">${event}</a> 
					</div>
					<div class="right_col">
						<fmt:message key="dev.main.${event}.description" />
						<c:if test="${not empty actionBean.messages[event]}">
							<br />${actionBean.messages[event]}
						</c:if>
					</div>
				</li>
				</security:secure>
			</c:forEach>
		</ul>
</stripes:layout-component>
</stripes:layout-render>
