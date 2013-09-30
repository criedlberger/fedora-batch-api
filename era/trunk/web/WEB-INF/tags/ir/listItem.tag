<%--
	$Id: listItem.tag 5427 2012-07-12 20:30:12Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)

	ListItem Taglib
	--------------
	Attributes: 
		- fld		: The search result field value map.
		- flds		: The search result map of field value collection.
		- status	: The search results forEach varStatus.
		- width		: The item description column width(default 324px).
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag produces table column for a list item." %>

<%@ attribute name="fld" type="java.util.Map" required="true" description="The search result field value map." %>
<%@ attribute name="flds" type="java.util.Map" required="true" description="The search result map of field value collection." %>
<%@ attribute name="status" type="javax.servlet.jsp.jstl.core.LoopTagStatus" required="true" description="The search results forEach varStatus." %>
<%@ attribute name="width" type="java.lang.Integer" required="false" description="The item description column width." %>

<c:set var="width" value="${width == 0 ? 324 : width}" />

	<a href="#" onclick="viewItem('${fld['PID']}'); return false;" class="result_title">${fld['dc.title']}</a>
	<div class="result_desc">
		By: ${fld['dc.creator']}
		<c:if test="${not empty fld['dc.description']}"><br />${fn:substring(fld['dc.description'], 0, 200)}${fn:length(fld['dc.description']) > 200 ? " ..." : ""}</c:if>
	</div>
	<div class="result_detail">
	<c:forEach items="${flds['dsm.ids']}" var="dsId" varStatus="sts">
		<a href="${actionBean.datastreamUrl}/get/${fld['PID']}/${dsId}" target="Download">${fn:substring(flds['dsm.labels'][sts.index], 0, 54)} <span style="font-weight: normal; margin-left: 2px;">[${flds['dsm.mimeTypes'][sts.index]}]</span></a>
	</c:forEach>
	</div>
	<div class="result_detail">
		<stripes:label for="search.license" />: <a href="${actionBean.datastreamUrl}/get/${fld['PID']}/LICENSE" target="Download">${fld['dsm.license']}</a>
	</div>
	<%-- 
	<c:if test="${not empty flds['rel.isMemberOfCollection'] || not empty flds['rel.isMemberOf']}">
		<script type="text/javascript">getMemberOfCollections('${fld["PID"]}', 'collections.${status.index}');</script>  
	</c:if>
	--%>
</td>
