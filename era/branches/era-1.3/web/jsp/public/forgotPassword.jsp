<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/forgotPassword.jsp $
   $Id: forgotPassword.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Forgot Username/Password" active="${navbarHome}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="menubar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
		<c:if test="${actionBean.type == 'username'}">
			<h2><fmt:message key="forgot.username.header" /></h2>
			<div class="subheader"><fmt:message key="forgot.username.subheader" /></div>
		</c:if>
		<c:if test="${actionBean.type == 'password'}">
			<h2><fmt:message key="forgot.password.header" /></h2>
			<div class="subheader"><fmt:message key="forgot.password.subheader" /></div>
		</c:if>

		<stripes:errors /><stripes:messages />
	<div class="edit_profile">
		<stripes:form id="forgotPasswordForm" action="/public/account">
			<stripes:hidden name="type" />
		
			<ul>
				<c:if test="${actionBean.type == 'password'}">
                <li><stripes:label for="user.username"/>
                    <stripes:text name="user.username"/>
				</li>
             
				</c:if>
				<li>
					<stripes:label for="user.email" /><stripes:text name="user.email"/>
				</li>
              
                		<input type="hidden" name="${actionBean.type}" />
                		<input type="submit" class="save_button" value="Submit" /> 
                
		</stripes:form>
	</div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
