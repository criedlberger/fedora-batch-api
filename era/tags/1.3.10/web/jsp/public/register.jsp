<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/public/register.jsp $
   $Id: register.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/public/register" />
</c:if>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Register" active="${navbarHome}">

    <stripes:layout-component name="contents">
		<div class="full_box">
			<h2><stripes:label for="register.title" /></h2>
			<div class="subheader">
				<p><fmt:message key="register.info" /></p>
			</div>
		
        <stripes:errors />

        <div class="register">
            <stripes:form id="registerForm" action="/public/register" focus="user.firstName">
                <div><stripes:label for="user.firstName"/><stripes:text name="user.firstName" /></div>
                <div><stripes:label for="user.lastName"/><stripes:text name="user.lastName" /></div>
                <div><stripes:label for="user.username"/><stripes:text name="user.username" /></div>
                <div><stripes:label for="user.email"/><stripes:text name="user.email" /></div>
                <input type="hidden" name="authType" value="PASSWORD" />
                <div><stripes:label for="user.password" /><stripes:password name="user.password" repopulate="true" /></div>
                <div><stripes:label for="confirmPassword" /><stripes:password name="confirmPassword" /></div>
                <div class="registersubmit"><stripes:submit id="register" name="register" value="${btnFinish}" class="registerbutton"/></div>
            </stripes:form>
		</div>
	    <div style="text-align: right;">
   		</div>
   		
       </div>


    </stripes:layout-component>

</stripes:layout-render>
