<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/loginPopup.jsp $
   $Id: loginPopup.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/popup.jsp" title="Login">

    <stripes:layout-component name="contents">
		<div class="popup_box">
			<h2><fmt:message key="login.${actionBean.mode}.header" /></h2>
			<stripes:errors action="/public/login">
				<stripes:errors-header>
					<div class="errors" style="width: 100%"><h2><fmt:message key="login.error.header" /></h2>
					<ul>
				</stripes:errors-header>
				<li><stripes:individual-error /></li>
				<stripes:errors-footer>
					</ul></div>
				</stripes:errors-footer>
			</stripes:errors>
	   		<stripes:messages />
			<c:if test="${not actionBean.httpsError && ((empty CCIDUser && actionBean.mode == 'ccid') || (empty actionBean.context.user && actionBean.mode == 'password'))}">
			<div class="popup_wrap">
		        <stripes:form id="loginForm" action="/public/login" focus="username" onsubmit="submitLoginPopup(); return false;" class="popup_form">
		        <input type="hidden" name="type" value="popup" />
		        <div style="float: left;">
				   	<div class="username">
						<stripes:label for="${actionBean.mode == 'ccid' ? 'ccid' : 'username'}" />:
						<stripes:text name="username" value="${user.username}" class="input_text" tabindex="1" />
					</div>
					<div class="pass">
						<stripes:label for="password" />:
						<stripes:password name="password" class="input_text" tabindex="2" />
					</div>
		        </div>
				<div style="float: right;">
					<div class="username">
				        <c:if test="${(empty CCIDUser && actionBean.mode == 'ccid') || (empty actionBean.context.user && actionBean.mode == 'password')}">
			        	<c:choose>
						<c:when test="${actionBean.mode == 'ccid'}">
							<a href="http://helpdesk.ualberta.ca/ccid/trouble.html" class="login_link" target="CCID" style="padding-right: 3em;"><fmt:message key="login.forgot.ccid" /></a> 
						</c:when>
						<c:otherwise>
							<a href="${httpServerUrl}${ctx}/public/account/request/username" class="login_link" style="padding-right: 3em;"><fmt:message key="login.forgot.username" /></a>
						</c:otherwise>
			        	</c:choose>
						</c:if>
					</div>
					<div class="password">
				        <c:if test="${(empty CCIDUser && actionBean.mode == 'ccid') || (empty actionBean.context.user && actionBean.mode == 'password')}">
			        	<c:choose>
						<c:when test="${actionBean.mode == 'ccid'}">
							<a href="http://helpdesk.ualberta.ca/ccid/maintenance.html" class="login_link" target="CCID"><fmt:message key="login.forgot.password" /></a>
						</c:when>
						<c:otherwise>
							<a href="${httpServerUrl}${ctx}/public/account/request/password" class="login_link"><fmt:message key="login.forgot.password" /></a>
						</c:otherwise>
			        	</c:choose>
						</c:if>
					</div>
				</div>
				<stripes:hidden name="url" value="${param['url']}"/>
				<stripes:hidden name="mode" />
				<stripes:submit name="login" class="login_button" value="${btnLogin}" tabindex="3" />
				</stripes:form>
			</div>
		    </c:if>
		</div>
        <c:if test="${(actionBean.mode == 'ccid' && not empty CCIDUser) || (actionBean.mode == 'password' && not empty actionBean.context.user)}">
        	<script type="text/javascript">window.setTimeout('Modalbox.hide()', 1500);</script>
        </c:if>
		
    </stripes:layout-component>
    
</stripes:layout-render>
