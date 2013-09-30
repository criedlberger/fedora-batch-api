<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/protected/transactions.jsp $
   $Id: transactions.jsp 5603 2012-10-05 18:51:26Z pcharoen $
   $Revision: 5603 $ $Date: 2012-10-05 12:51:26 -0600 (Fri, 05 Oct 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<div class="itemlist morelikethis" style="background-color: #E6EBF4 !important;">
<ol>
	<c:forEach items="${actionBean.transactions}" var="trans" varStatus="status">
		<li class="record">
		<div class="itemlist_info" style="font-weight: bold; width: 12em;"><fmt:formatDate pattern="${actionBean.dateFormat}"
			value="${trans.workflowDate}" /></div>
		<div class="itemlist_collection" style="width: 15em;"><stripes:label for="WorkflowState.${trans.workflowState}" />${' '}<fmt:message key="by" />${' '}<br />
		<a href="mailto:${trans.user.email}">${trans.user.firstName}${' '}${trans.user.lastName}</a><br />
		</div>
		<div class="itemlist_actions" style="width: 35em; height: 100%;">
		<c:if test="${not empty trans.comments}">
			<stripes:label for="admin.review.comments" />: 
			<textarea style="width: 100%; height: 8em; background-color: #FFEDED;" readonly="readonly">${trans.comments}</textarea>
		</c:if>
		</div>
		</li>
	</c:forEach>
</ol>
</div>
<br style="clear: both;" />
