<%@ include file="/jsp/layout/taglibs.jspf"%>
<h2><fmt:message key="search.advanced.header" /></h2>
<div class="subheader">
	<p><fmt:message key="search.advanced.subheader"></fmt:message></p>
</div>
<stripes:form action="${httpServerUrl}${ctx}/public/search" id="advancedForm" focus="advTerms">
	<ul class="adv_search">
	<li class="first"><input type="hidden" name="operators" value="-" />
	<%@ include file="/jsp/public/advancedFields.jspf" %>
	<input id="advTerms" type="text" name="advancedTerms"/></li>
	<stripes:select name="state">
		<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.State" />
	</stripes:select>
	<li><%@ include file="/jsp/public/advancedOperators.jspf" %>
	<%@ include file="/jsp/public/advancedFields.jspf" %>
	<input type="text" name="advancedTerms"/></li>
	<li><%@ include file="/jsp/public/advancedOperators.jspf" %>
	<%@ include file="/jsp/public/advancedFields.jspf" %>
	<input type="text" name="advancedTerms" /></li>
	<li><%@ include file="/jsp/public/advancedOperators.jspf" %>
	<%@ include file="/jsp/public/advancedFields.jspf" %>
	<input type="text" name="advancedTerms" /></li>
	<div style="margin-top: 12px;"><h2 class="advanced_filters"><fmt:message key="advanced.filter.header" /></h2></div>
	<li>
		<div style="float: left; margin-right: 5px;">
			<div><stripes:label for="advanced.filter.contentModel" style="margin-left: 2px;" /></div>
			<div id="allContentModels">
				<select name="contentModelFilter" id="contentModelFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="true">
				<c:forEach items="${actionBean.types}" var="type" varStatus="status">
					<option value="${type.name}">${type.name}</option>
				</c:forEach>
				</select>
			</div>
			<div><a href="#" class="cancel_link" onclick="clearSelectedElements($('contentModelFilter'));" style="margin-left: 2px;">${btnClear} &raquo;</a></div>
		</div>
		<div style="float: left; margin-right: 5px;">
			<div><stripes:label for="advanced.filter.collection" style="margin-left: 2px;" /></div>
			<div id="allCollections">
				<select id="collectionFilter" name="collectionFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="multiple">
				<c:forEach items="${actionBean.collections}" var="collection" varStatus="status">
					<option value="${collection.title}">${collection.title}</option>
				</c:forEach>
				</select>
			</div>
			<div><a href="#" class="cancel_link" onclick="clearSelectedElements($('collectionFilter'));" style="margin-left: 2px;">${btnClear} &raquo;</a></div>
		</div>
		<div style="float: left;">
			<div><stripes:label for="advanced.filter.community" style="margin-left: 2px;" /></div>
			<div id="allCommunities">
				<select id="communityFilter" name="communityFilter" style="width: 192px; height: 120px; overflow: auto;" multiple="multiple">
				<c:forEach items="${actionBean.communities}" var="community" varStatus="status">
					<option value="${community.title}">${community.title}</option>
				</c:forEach>
				</select>
			</div>
		
		</div>
		<br clear="all"/>
</li>
<input type="submit" name="advanced" value="${btnSearch}" class="save_button" />
</stripes:form>
<a href="#" class="cancel_link" onclick="toggleAdvancedSearch();">&laquo; ${btnClose}</a>
