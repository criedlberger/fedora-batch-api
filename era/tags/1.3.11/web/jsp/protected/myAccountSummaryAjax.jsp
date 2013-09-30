<%@ include file="/jsp/layout/taglibs.jspf"%>
<h2><fmt:message key="myaccount.home.header" /></h2>
<p><fmt:message key="myaccount.home.description" /></p>
<ul>
<fmt:message key="myaccount.sidebar.count" var="count" />
<c:forEach begin="1" end="${count}" var="i" varStatus="status">
	<fmt:message key="myaccount.sidebar.${i}.roles" var="roles" /> 
	<security:secure roles="${roles}">
	<li>
		<fmt:message key="myaccount.sidebar.${i}.url" var="url" />
		<a href="${ctx}${url}"><fmt:message key="myaccount.sidebar.${i}.name" /></a>
		<fmt:message key="myaccount.sidebar.${i}.id" var="id" />
		<span class="count">${actionBean.itemCounts[id] > 0 ? fn:replace('(x)', 'x', actionBean.itemCounts[id]) : ''}</span>
		<br /><fmt:message key="myaccount.sidebar.${i}.title" />
	</li>
	</security:secure> 
</c:forEach>
</ul>
