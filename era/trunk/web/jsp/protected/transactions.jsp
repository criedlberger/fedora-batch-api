<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/transactions.jsp $
   $Id: transactions.jsp 5585 2012-09-27 22:11:57Z pcharoen $
   $Revision: 5585 $ $Date: 2012-09-27 16:11:57 -0600 (Thu, 27 Sep 2012) $
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
