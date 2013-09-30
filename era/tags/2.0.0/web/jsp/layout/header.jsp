<%-- header layout component --%>
<jsp:useBean id="now" class="java.util.Date" />

<div class="header">
<div class="account_info"><a href="${httpServerUrl}${ctx}/action/myaccount" class="myeratag" id="myera"><span>myERA</span></a>
<ul id="welcome" class="user_links">
	<c:choose>
	<c:when test="${not empty context.user}">
		<li><a href="${httpServerUrl}${ctx}/action/myaccount" class="name">${context.user.firstName}${" "}${context.user.lastName}</a></li>
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
	<li>
		<a href="${httpServerUrl}${ctx}/public/cart" class="cart" id="mycart" onmouseover="updateCart(); return false;"><fmt:message key="header.cart" /> &nbsp;<span id="cartItemCount"><script type="text/javascript">getCartItemCount();</script></span></a>
	</li>
</ul>
<security:secure roles="/admin/menu">
	<fmt:message key="admin.home.title" var="title" />
	<stripes:link id="admin_home" class="admin_button" href="/action/admin" style="float: left;"><fmt:message key="admin.home.label" /></stripes:link>
</security:secure>
<%-- 
<stripes:form id="langForm" action="/public/home">
	<stripes:select name="lang" onchange="$('langForm').submit();" style="margin-top: 5px;">
		<stripes:options-enumeration enum="ca.ualberta.library.ir.enums.Language" />
	</stripes:select>
	<input type="hidden" name="language" />
</stripes:form>
--%>
</div>
<div class="top_info"></div>
<div class="eraheader"><a href="${httpServerUrl}${ctx}/"><img src="${ctx}/images/eraheader.png" alt="ERA Banner"
	title="<fmt:message key="application.name"/>" border="0" /></a></div>
<fmt:message key="header.uofa.libraries" var="title" />
<div class="logo"><a href="http://www.library.ualberta.ca/"><img src="${ctx}/images/liblogo.png" title="${title}" /></a></div>
<div class="slogan"></div>
<security:secure roles="/admin/menu">
	<c:set var="adminMenu" value="true" />
</security:secure> 
<ir:menubar name="home.navbar" active="${active}" menubarClass="navbar">
	<jsp:attribute name="firstItem">
		<li class="slogan" style="${adminMenu ? '' : ''}"></li>
	</jsp:attribute>
</ir:menubar>
<div class="search_field">
	<ul class="help_contact">
		<li class="line"><a href="${httpsServerUrl}${ctx}/jsp/public/contactUs.jsp"><fmt:message key="header.contactEra" /></a></li>
		<li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648971" class="help"><fmt:message key="header.getHelp" /></a></li>
	</ul>
	<%@ include file="/jsp/layout/searchForm.jspf"%> 
	<a href="#" class="advanced_link" onclick="toggleAdvancedSearch(); return false;" id="advImg"><fmt:message key="header.advanced" /></a>
</div>

<div id="messages"></div>
<script type="text/javascript">
	new Ajax.PeriodicalUpdater({ success: 'messages' }, '${ctx}/public/home/message', { method: 'get', frequency: 60 });
</script> 
<%@ include file="/jsp/layout/advancedSearchForm.jspf" %> 
<c:if test="${event == 'simple'}">
	<div><stripes:messages /><stripes:errors /></div>
</c:if>
</div>

<div id="mycart_popup" class="popup" style="display: none;"></div>
<script type="text/javascript">
	//<![CDATA[
   	function updateCart() {
	   	var x = $('myera').viewportOffset().left;
	   	var y = $('myera').viewportOffset().top + $('myera').getHeight();
	   	new Popup('mycart_popup','mycart', { position: x + ',' + y, show_delay: 100 });
	   	new Ajax.Updater({ success: 'mycart_popup' }, '${ctx}/public/cart/getCartDetails');
	   	$('mycart_popup').popup.show();
   	}
   	//]]>
</script>

<c:if test="${not empty context.user}">
	<div id="myera_popup" class="popup" style="display: none;">
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
				<br /><fmt:message key="myaccount.sidebar.${i}.title" />
			</li>
			</security:secure> 
		</c:forEach>
		</ul>
	</div>
	<script type="text/javascript">
    	new Popup('myera_popup','myera', {position: 'below', show_delay: 100});
	</script>
</c:if>

<security:secure roles="/admin/menu">
	<div id="admin_popup" class="popup" style="display: none;">
		<h2><fmt:message key="admin.home.header" /></h2>
		<p><fmt:message key="admin.home.description" /></p>
		<ul>
		<fmt:message key="admin.sidebar.count" var="count" />
		<c:forEach begin="1" end="${count}" var="i" varStatus="status">
			<fmt:message key="admin.sidebar.${i}.roles" var="roles" />
			<security:secure roles="${roles}">
			<li>
				<fmt:message key="admin.sidebar.${i}.url" var="url" />
				<fmt:message key="admin.sidebar.${i}.id" var="id" />
				<a id="${id}_popup" href="${ctx}${url}" onclick="${id == 'reload' || id == 'facebook' ? 'return false;' : 'return true;'}">
					<fmt:message key="admin.sidebar.${i}.name" />
				</a>
				<br /><fmt:message key="admin.sidebar.${i}.title" />
			</li>
			</security:secure>
		</c:forEach>
		</ul>
	</div>
	<script type="text/javascript">
    	new Popup('admin_popup','admin_home', { position: 'below', show_delay: 100 });
	</script>
	<%@ include file="/jsp/protected/adminHomePopups.jspf" %>		
</security:secure>