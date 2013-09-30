<%--
	$Id: addThis.tag 5606 2012-10-10 16:45:09Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	AddThis Taglib
	--------------
	Attributes: 
		- url			: The URL to bookmark.
		- title			: The bookmark title.
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag creates an AddThis bookmark icon." %>

<%@ attribute name="url" type="java.lang.String" required="true" description="The URL to bookmark." %>
<%@ attribute name="title" type="java.lang.String" required="true" description="The bookmark title." %>

<c:if test="${properties['addthis.bookmark']}">
<!-- AddThis Button BEGIN -->
<a href="http://www.addthis.com/bookmark.php" class="addthis addthis_button" addthis:url="${url}" addthis:title="${title}"><fmt:message key="button.addthis" /></a>
<script type="text/javascript">var addthis_config = { username:'${properties["addthis.username"]}' };</script>
<script type="text/javascript" src="https://s7.addthis.com/js/250/addthis_widget.js"></script>
<!-- AddThis Button END -->
</c:if>
