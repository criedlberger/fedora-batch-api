<%--
	$Id: addtoany.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	addToAny Taglib
	--------------
	Attributes: 
		- url			: The URL to bookmark.
		- title			: The bookmark title.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates an addtoany bookmark icon." %>
<%@ tag import="java.net.URLEncoder" %>

<%@ attribute name="url" type="java.lang.String" required="true" description="The URL to bookmark." %>
<%@ attribute name="title" type="java.lang.String" required="true" description="The bookmark title." %>

<c:if test="${properties['addtoany.bookmark']}">
<a class="a2a_dd" href="http://www.addtoany.com/share_save?linkname=addtoany&amp;linkurl=${fnx:encodeUrl(url)}" target="_blank"><img src="https://http-s1.simplecdn.net/static.addtoany.com/buttons/share_save_171_16.gif" width="171" height="16" border="0" alt="Share/Save/Bookmark" /></a><script type="text/javascript">a2a_linkname="addtoany";a2a_linkurl="${url}";</script><script type="text/javascript" src="https://http-s1.simplecdn.net/static.addtoany.com/menu/page.js"></script>
</c:if>