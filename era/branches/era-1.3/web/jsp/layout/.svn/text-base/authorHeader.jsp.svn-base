<%-- header layout component --%>
<jsp:useBean id="now" class="java.util.Date" />

<div class="header">
    <div class="account_info">
        <a href="${httpServerUrl}${ctx}/action/myaccount" class="myeratag"><span>ERA</span></a>
         <ul class="user_links">
			<c:choose>
			<c:when test="${not empty user}">
				<li><a href="${httpServerUrl}${ctx}/action/myaccount" class="name">${user.firstName}${" "}${user.lastName}</a></li>
				<li><a href="${httpServerUrl}${ctx}/public/logout"><fmt:message key="header.logout" /></a></li>
			</c:when>
			<c:when test="${not empty CCIDUser}">
				<li><a href="#" class="name">${CCIDUser.firstName}${" "}${CCIDUser.lastName}</a></li>
				<li><a href="${httpServerUrl}${ctx}/public/logout"><fmt:message key="header.logout" /></a></li>
			</c:when>
			<c:otherwise>
				<li><a href="${httpsServerUrl}${ctx}/public/login?url=${actionBean.targetUrl}"><fmt:message key="header.login" /></a></li>
				<li><a href="${httpsServerUrl}${ctx}/public/register"><fmt:message key="header.register" /></a></li>
			</c:otherwise>
			</c:choose>
            <li><a href="${httpServerUrl}${ctx}/public/cart" class="cart"><fmt:message key="header.cart" /><span id="cartItemCount">
                    <script type="text/javascript">getCartItemCount();</script></span></a> </li>
		</ul>
	 	<stripes:link href="/action/myaccount/profile/edit" class="create_button"><fmt:message key="header.start.yours" /></stripes:link>
		<security:secure roles="/admin/menu">
			<fmt:message key="admin.home.title" var="title" />
			<stripes:link class="admin_button" href="/action/admin" title="${title}"><fmt:message key="admin.home.label" /></stripes:link>
		</security:secure>
  	</div>
	<div class="top_info"></div>
	<div class="logo"><a href="http://www.library.ualberta.ca/"><img src="${ctx}/images/liblogo.png" title="${headerUofALibraries}" /></a></div>
	<div class="slogan"></div>
    <div  class="eraheader">
        <a href="${httpServerUrl}${ctx}/"><img src="${ctx}/images/eraheader.png" title="<fmt:message key="application.name"/>" /></a>
    </div>
    <ir:menubar name="home.navbar" active="${active}" menubarClass="navbar">
        <jsp:attribute name="firstItem">
            <li class="slogan" style="${adminMenu ? 'width: 50px;' : ''}"></li>
        </jsp:attribute>
    </ir:menubar>
   	<div class="search_field">
  		<ul class="help_contact">
    		<li class="line"><a href="${httpsServerUrl}${ctx}/jsp/public/contactUs.jsp">contact era</a></li>
        	<li><a href="${httpsServerUrl}${ctx}/jsp/public/howtoDeposit.jsp" class="help">get help</a></li>
     	</ul>
     	<%@ include file="/jsp/layout/searchForm.jspf" %>
		<a href="" class="advanced_link" onclick="toggleAdvancedSearch(); return false;" id="advImg" ><fmt:message key="header.advanced" /></a>
	</div>
   	<%@ include file="/jsp/layout/advancedSearchForm.jspf" %>
</div>