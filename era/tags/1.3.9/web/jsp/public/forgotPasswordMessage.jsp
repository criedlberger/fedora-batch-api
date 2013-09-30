<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/forgotPasswordMessage.jsp $
   $Id: forgotPasswordMessage.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Forgot ${actionBean.type == 'username' ? 'Username' : 'Password'}" active="${navbarHome}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>

	<stripes:layout-component name="menubar">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<div class="full_box">
			<c:if test="${actionBean.type == 'username'}">
				<h2><stripes:label for="forgot.username.header" /></h2>
			</c:if>
			<c:if test="${actionBean.type == 'password'}">
				<h2><stripes:label for="forgot.password.header" /></h2>
			</c:if>
			<div class="subheader"><fmt:message key="forgot.message.subheader" /></div>
			<div class="edit_message">
				<div class="global_error">
					<stripes:errors>
						<stripes:errors-header>
							<div class="errors"><h2><fmt:message key="errors.validataion.header" /></h2>
							<ul>
						</stripes:errors-header>
						<li><stripes:individual-error /></li>
						<stripes:errors-footer>
							</ul></div>
						</stripes:errors-footer>
					</stripes:errors>
				</div>
				<stripes:messages />
			</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>
