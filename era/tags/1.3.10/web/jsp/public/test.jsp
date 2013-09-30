<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/public/test.jsp $
   $Id: test.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@page import="java.net.InetAddress"%>
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>ERA: Test Page</title>
<style type="text/css">
label {
	font-weight: bold;
	padding-right: 0.5em;
}
</style>
</head>
<body>
<h2>ERA: Test Page</h2>
<div>
<h3>Events:</h3>
<ul>
	<c:forEach items="${actionBean.events}" var="value">
		<li><a href="/dev/test/${value}">${value}</a></li>
	</c:forEach>
</ul>
</div>

<div>
<h3>OAI: GetRecord</h3>
<stripes:form action="/dev/test">
	<ul type="none">
		<li><label>metadataPrefix:</label><stripes:select name="metadataPrefix">
			<stripes:option label="oai_dc" />
			<stripes:option label="oai_etdms" />
		</stripes:select></li>
		<li><label>pid:</label><stripes:text name="pid" style="width: 22em;" /></li>
		<li><stripes:submit name="oaiGetRecord" /></li>
	</ul>
</stripes:form></div>

<h3>readDocument</h3>
<stripes:form action="/dev/test#doc">
	<ul type="none">
		<li><label>pid:</label><stripes:text name="pid" style="width: 22em;" /></li>
		<li><label>page:</label><stripes:select name="page">
			<c:forEach begin="1" end="${actionBean.noOfPages}" varStatus="sts">
				<stripes:option label="${sts.count}" />
			</c:forEach>
		</stripes:select></li>
		<li><stripes:submit name="readDocument" /></li>
	</ul>
</stripes:form>
</div>

<div>
<h3>Results:</h3>
<h4>${empty event ? 'init' : event}</h4>
<ul type="none">
	<c:choose>
		<c:when test="${event == 'postFacebookMessage'}">
			<li>messageId: ${actionBean.messageId}</li>
			<li>picture: ${actionBean.picture}</li>
		</c:when>
		<c:when test="${event == 'removeAllFacebookPosts'}">
			<li>noOfPosts: ${actionBean.noOfPosts}</li>
		</c:when>
		<c:when test="${event == 'postRandomFacebookMessage'}">
			<li>noOfPosts: ${actionBean.handle}</li>
		</c:when>
		<c:when test="${event == 'postFacebookTodayItem'}">
			<li>item has been posted.</li>
		</c:when>
		<c:when test="${event == 'readDocument'}">
			<li>
			<h3 id="doc" style="text-align: center;">${actionBean.title}</h3>
			<center>
			<div style="text-align: center; font-size: 0.8em; width: 800px;"><label id="doc">Page:</label><c:forEach begin="1"
				end="${actionBean.noOfPages}" varStatus="sts">
				<c:choose>
					<c:when test="${sts.count == actionBean.page}">
						<b>${sts.count}</b>
					</c:when>
					<c:otherwise>
						<a name="p${sts.count}" href="/dev/test/readDocument?pid=${actionBean.pid}&page=${sts.count}#doc">${sts.count}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach></div>
			</center>
			</li>
			<li>
			<div align="center"><img src="/tmp/${fn:replace(actionBean.pid, ':', '_')}_${actionBean.page}.png" /></div>
			</li>
		</c:when>
	</c:choose>
</ul>
<h4>IP Address: <%= InetAddress.getLocalHost().getHostAddress() %></h4>
</div>
</body>
</html>
