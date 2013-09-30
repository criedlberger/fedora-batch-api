<%@ include file="/jsp/layout/taglibs.jspf" %>

<div id="admin">
	<h2><a href="#" class="adminButton" onclick="toggleAdminSummary(); return false;"><fmt:message key="admin.sidebar.name" /></a></h2>
	<ul id="adminMenu">
		<c:set var="name" value="admin.sidebar" />
		<fmt:message key="admin.sidebar.count" var="count" />
		<c:forEach begin="1" end="${count}" var="i" varStatus="status">
		<fmt:message key="${name}.${i}.title" var="title" />
		<fmt:message key="${name}.${i}.url" var="url" />
		<fmt:message key="${name}.${i}.image" var="image" />
		<fmt:message key="${name}.${i}.roles" var="roles" />
		<c:set var="enabled" value="false" />
		<security:secure roles="${roles}">
			<c:set var="enabled" value="true" />
		</security:secure>
		<c:if test="${empty roles || enabled}">
		
				<li>
					
					<a href="${ctx}${url}"><fmt:message key="${name}.${i}.name" /></a>
			
				<c:choose>
				<c:when test="${roles == '/admin/subscription/notifier'}">
				<fmt:formatDate value="${actionBean.subscription.startTime}" pattern="MMM d h:mm a" /> (${actionBean.subscription.hours}:${actionBean.subscription.minutes}:${actionBean.subscription.seconds})
				</c:when>
				<c:when test="${roles == '/admin/embargoed/publisher'}">
				<fmt:formatDate value="${actionBean.embargoed.startTime}" pattern="MMM d h:mm a" /> (${actionBean.embargoed.hours}:${actionBean.embargoed.minutes}:${actionBean.embargoed.seconds})
				</c:when>
				<c:when test="${roles == '/admin/index/builder'}">
				<fmt:formatDate value="${actionBean.index.startTime}" pattern="MMM d h:mm a" /> (${actionBean.index.hours}:${actionBean.index.minutes}:${actionBean.index.seconds})</span></li>
				</c:when>
				<c:otherwise>
					${actionBean.count[i-1]}
				</c:otherwise>
				</c:choose>
				
		</c:if>
	</li>
		</c:forEach>
	</ul>
</div>

