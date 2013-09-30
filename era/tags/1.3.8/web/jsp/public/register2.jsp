<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.8/web/jsp/public/register2.jsp $
   $Id: register2.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<c:if test="${empty actionBean}">
	<c:redirect url="${httpsServerUrl}${ctx}/public/register" />
</c:if>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Register" active="${navbarHome}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

    <stripes:layout-component name="contents">
		<div class="full_box">
		<h2><a href="#"><stripes:label for="register.title" /></a></h2>
		<div class="subheader">
			<p><fmt:message key="register.workflow.2.description"><fmt:param>${actionBean.user.firstName}</fmt:param></fmt:message>
			</p>
		</div>

       	<stripes:errors/>

        <stripes:form id="registerForm" action="/public/register" focus="user.password">
            <table>
                <tr>
                    <td class="input_label" style="width: 120px;"><stripes:label for="user.password" />:</td>
                    <td><stripes:password name="user.password" repopulate="true" style="width: 460px;"/></td>
                </tr>
                <tr>
                    <td class="input_label"><stripes:label for="confirmPassword" />:</td>
                    <td><stripes:password name="confirmPassword" style="width: 460px;"/></td>
                </tr>
                <tr>
                	<td align="left" colspan="2"><stripes:submit name="gotoStep1" value="« ${btnPrevious}" class="workflow_button"/></td>
                </tr>
                <tr>
                	<td align="right" colspan="2" style="height: 40px; vertical-align: bottom;"><stripes:submit id="register" name="register" value="${btnFinish}" class="button"/></td>
                </tr>
            </table>
        </stripes:form>

    	<ir:workflow name="register.workflow" active="2" itemCount="2" workflowClass="workflow" showTitle="true" titleClass="workflow_title" />
	    
	    <br />   	
	    <div style="text-align: right;">
   			<a class="cancel_link" href="${httpServerUrl}${ctx}/public/home"  onclick="return cancelWorkFlow();">&laquo; ${btnCancel}</a>
   		</div>
   		
       </div>

    </stripes:layout-component>

</stripes:layout-render>
