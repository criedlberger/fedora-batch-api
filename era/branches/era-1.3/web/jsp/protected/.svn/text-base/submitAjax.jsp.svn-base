<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<c:choose>
<c:when test="${event == 'getCollectionList'}">
	<select name="itemCols" id="item.cols" onchange="addCollection(this); return false;">
	<option value=""><fmt:message key="deposit.select" /></option>
	<c:forEach items="${actionBean.communities}" var="com">
		<option style="font-style: italic;" value="">${com.title}</option>
		<c:forEach items="${com.collections}" var="col">
			<option value="${col.id}">&nbsp;&nbsp;&nbsp;&nbsp;${col.title}</option>
		</c:forEach>
	</c:forEach>
	</select>
</c:when>
</c:choose>
