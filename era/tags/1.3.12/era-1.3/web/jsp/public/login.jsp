<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/login.jsp $
   $Id: login.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/public/login" />
</c:if>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Login" active="${navbarHome}">

	<stripes:layout-component name="html-comment">
	<!-- $Id: login.jsp 5427 2012-07-12 20:30:12Z pcharoen $ -->
	</stripes:layout-component>

    <stripes:layout-component name="html-head">
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				onLoad();
			});
			function onLoad() {
				if ($('autocomplete')) {
					new Ajax.Autocompleter("autocomplete", "autocomplete_choices", ctx +"/action/admin/user/getUsersByName", {
						paramName: "name", 
						minChars: 2,
						afterUpdateElement: function(autocomplete, selected) {
							if (selected.id) {
								$('asUsername').value = selected.id;
							} else {
								$('autocomplete').clear();
							}
						}
					});
				}
			}
		</script>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/autocomplete.css" />
	</stripes:layout-component>

    <stripes:layout-component name="contents">
		<div class="full_box">
			<h2><fmt:message key="login.${actionBean.mode}.header" /></h2>
			<div class="subheader"><fmt:message key="login.${actionBean.mode}.subheader"><fmt:param value="${ctx}" /></fmt:message></div>
			<stripes:errors action="/public/login"/>
	   		<div class="edit_profile" style="margin-top: 2em;">
	        <stripes:form action="/public/login" focus="username">
	     		<div class="username">
                	<stripes:label for="${actionBean.mode == 'ccid' ? 'ccid' : 'username'}" />
	           		<stripes:text name="username" value="${user.username}" class="input_text" tabindex="1" />
	           		<c:choose>
	                 <c:when test="${actionBean.mode == 'ccid'}">
						<a href="http://helpdesk.ualberta.ca/ccid/trouble.html" class="login_link" target="CCID"><fmt:message key="login.forgot.ccid" /></a>
					</c:when>
					<c:otherwise>
						<a href="${httpServerUrl}${ctx}/public/account/request/username" class="login_link"><fmt:message key="login.forgot.username" /></a>
					</c:otherwise>
	           		</c:choose>
              	</div>
				<div class="pass"><stripes:label for="password" />
					<stripes:password name="password" class="input_text" tabindex="2" />
	           		<c:choose>
	                 <c:when test="${actionBean.mode == 'ccid'}">
						<a href="http://helpdesk.ualberta.ca/ccid/maintenance.html" class="login_link" target="CCID"><fmt:message key="login.forgot.password" /></a>
					</c:when>
					<c:otherwise>
	                    <a href="${httpServerUrl}${ctx}/public/account/request/password" class="login_link"><fmt:message key="login.forgot.password" /></a>
					</c:otherwise>
	           		</c:choose>
                </div>
                <c:if test="${actionBean.mode == 'password' && not empty context.user}">
                <security:secure roles="/admin/login">
	     		<div class="username">
                	<stripes:label for="asUsername" />
	           		<input type="hidden" name="asUsername" id="asUsername" />
					<stripes:text name="name" id="autocomplete" class="input_text" style="width: 30em;" tabindex="3" />
					<div id="autocomplete_choices" class="autocomplete"></div>
              	</div>
                </security:secure>
                </c:if>
				<stripes:hidden name="url" value="${param['url']}"/>
				<stripes:hidden name="mode" />
				<stripes:submit name="login" class="login_button" value="${btnLogin}" tabindex="4" />
			</stripes:form>
          	</div>
		</div>
    </stripes:layout-component>
    
</stripes:layout-render>
