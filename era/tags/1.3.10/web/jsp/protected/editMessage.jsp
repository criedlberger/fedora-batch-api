<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editMessage.jsp $
   $Id: editMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/sidebar.jsp" title="Administrator - Message" active="0">

	<stripes:layout-component name="html-head">
		<style type="text/css">
			table.message {width: auto;}
			table.message tr {border: 1px solid #ccc;}
			table.message th {background-color: #E9E6D3;}
			table.message th, td {padding: 0 0.5em 0 0.5em; border: 1px solid #ccc; line-height: 1.4em;}
		</style>
	</stripes:layout-component>

	<stripes:layout-component name="sidebar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="admin.message.header" /></h2>
			<div class="subheader"><fmt:message key="admin.message.subheader" /></div>
	
			<stripes:errors /><stripes:messages />
	
			<br style="clear: both;" />
			<div style="margin-top: 0.5em; margin-left: 5em; line-height: 2em; width: 50em;">
				<stripes:form action="/action/message">
					<stripes:hidden name="message.id" />
					<ul>
						<li>
							<stripes:label for="admin.message.type" style="margin-right: 0.5em; width: 6em; float: left;" />
							<c:forEach begin="0" end="1" var="type">
								<stripes:radio value="${type}" name="message.type" /><stripes:label for="admin.message.${type}" style="margin: 0 0.5em 0 0.5em;" />
							</c:forEach>
						</li>
						<li>
							<stripes:label for="admin.message.text" style="vertical-align: top; width: 6em; float: left;" />
							<stripes:textarea name="message.message" style="width: 46em; height: 5em; font-size: 1.1em; margin: 0 0.5em 0 0.5em;" />
						</li>
					</ul>
					<div style="text-align: center;">
					<stripes:submit name="post" class="save_button" style="margin-left: 0; height: 2em; width: 5em; text-align: center; clear: both;"><fmt:message key="button.post" /></stripes:submit>
					<stripes:link href="/action/admin" class="cancel_link">
						<fmt:message key="button.cancel" />
					</stripes:link>
					</div>
				</stripes:form>
			</div>
			<div style="margin-top: 1em; margin-left: 5em; line-height: 2em; width: 50em;">
				<h3><fmt:message key="admin.message.history" /></h3>
				<div style="height: 20em; overflow-y: scroll; border: thin solid #ccc;">
				<table class="message" style="width: 100%;">
					<tr>
						<th colspan="4" style="text-align: left;"><fmt:message key="admin.message.text" /></th>
						<th rowspan="2" style="width: 4em;">Action</th>
					</tr>
					<tr>
						<th><fmt:message key="admin.message.type" /></th>
						<th><fmt:message key="admin.message.start" /></th>
						<th><fmt:message key="admin.message.end" /></th>
						<th><fmt:message key="admin.message.user" /></th>
					</tr>
					<c:forEach items="${actionBean.messages}" var="message">
						<tr>
							<td colspan="4">${message.message}</td>
							<td rowspan="2" style="width: 4em; vertical-align: bottom;">
							<c:if test="${message.state == 1}">
								<a href="${ctx}/action/message/remove/${message.id}" class="delete_button" style="width: 5em;"><fmt:message key="button.remove" /></a>
							</c:if>
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message key="admin.message.${message.type}" />
							</td>
							<td>${message.startDate}</td>
							<td>${message.endDate}</td>
							<td>${message.user.username}</td>
						</tr>
					</c:forEach>
				</table>
				</div>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
