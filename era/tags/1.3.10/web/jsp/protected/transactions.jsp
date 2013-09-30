<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/transactions.jsp $
   $Id: transactions.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<div class="itemlist morelikethis" style="background-color: #E6EBF4 !important;">
<ol>
	<c:forEach items="${actionBean.transactions}" var="trans" varStatus="status">
		<li class="record">
		<div class="itemlist_info" style="font-weight: bold; width: 12em;"><fmt:formatDate pattern="${actionBean.dateFormat}"
			value="${trans.workflowDate}" /></div>
		<div class="itemlist_collection" style="width: 20em;"><stripes:label for="WorkflowState.${trans.workflowState}" />${' '}<fmt:message key="by" />${' '}<br />
		<a href="mailto:${trans.user.email}">${trans.user.firstName}${' '}${trans.user.lastName}</a><br />
		</div>
		<div class="itemlist_actions" style="width: 30em;">
			<stripes:label for="admin.review.comments" />: ${fnx:trim(trans.comments, 256)}
		</div>
		</li>
	</c:forEach>
</ol>
</div>
<br style="clear: both;" />
