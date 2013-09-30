<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/jsp/public/authorHome.jsp $
   $Id: authorHome.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="ca.ualberta.library.ir.domain.AuthorProfile" %>

<%! static Pattern pattern = Pattern.compile("\\n"); %>
<%! static Pattern linkPattern = Pattern.compile("\\|"); %>

<c:set var="fullName" value="${actionBean.author.user.firstName}${' '}${actionBean.author.user.lastName}" />
<fmt:message key="feed.author.title" var="feedTitle">
	<fmt:param value="${fullName}" />
</fmt:message>

<stripes:layout-render name="/jsp/layout/author.jsp" title="${fullName} - Home" author="${fullName}">

	<stripes:layout-component name="html-head">
		<link rel="alternate" type="application/rss+xml" title="${feedTitle}" href="${httpServerUrl}${ctx}/public/feed/author/${feedType}/${actionBean.author.user.username}">
		<script type="text/javascript">
			function subscribe(element, pid, type) {
			<c:if test="${empty user}">
				<c:set var="next" value="/public/view/author/${actionBean.author.user.username}" />
				location.href = '${ctx}/action/subscription/subscribe/' + pid + '/' + type + '?next=${fnx:encodeUrl(next)}';
			</c:if>
			<c:if test="${not empty user}">
				new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
					parameters: { 'subscription.pid': pid, 'subscription.type': type }
				});
			</c:if>
			}
		</script>		
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<div class="title_actions">         	
				<h2>${actionBean.author.user.firstName}${" "}${actionBean.author.user.lastName}</h2>
           		<div class="collections_toolbar"> 
                	<div id="subcription" class="subscription" ></div>
                	<script type="text/javascript">getSubscription('subcription', '${actionBean.author.user.username}', <%= SubscriptionType.RESEARCHER.getValue() %>);</script>
                	<a href="${ctx}/public/feed/author/${feedType}/${actionBean.username}" title="${fullName} RSS 2.0 Feed" target="${fullName} RSS 2.0 Feed" class="rssbutton">RSS Feed</a>
					<div class="item_toolbar">
		                <ir:handle type="author" pid="${actionBean.username}" var="uri" />
	                	<ir:addThis url="${uri}" title="${actionBean.author.user.firstName}${' '}${actionBean.author.user.lastName}" />
	                	<ir:addtoany url="${uri}" title="${actionBean.author.user.firstName}${' '}${actionBean.author.user.lastName}" />
					</div>
				</div>
				<div class="description">
					<p>${actionBean.author.description}</p>
				</div>
			</div>
			<div class="author_box">
        		<img id="logo" name="logo" src="${ctx}/public/researcher/getPicture/${actionBean.author.id}" />
          		<h3>${fullName}</h3>
         		<p><em>${actionBean.author.institution}</em></p>
          		<c:if test="${not empty actionBean.author.contact}">
            	<h4><stripes:label for="profile.contact" /></h4>
               	<c:set var="contact" value="${actionBean.author.contact}" />
              	<% pageContext.setAttribute("contact", pattern.split((String)pageContext.getAttribute("contact"))); %>
             	<c:forEach items="${contact}" var="ln" varStatus="status">
                 	${status.index == 0 ? "" : ""}${ln}<br />
              	</c:forEach>
           	</c:if>
         	<c:if test="${not empty actionBean.author.cv}">
            	<a href="${ctx}/public/researcher/downloadCv/${actionBean.author.id}" target="Download" class="download"><fmt:message key="profile.cv" /></a>	
          	</c:if>
          	<c:forEach items="${actionBean.author.authorProfiles}" var="profile" varStatus="status">
           		<c:if test="${not empty profile.description}">
           			<h4><stripes:label for="AuthorProfileType.${profile.type}" /></h4>
                   		<ul>
                                <% 	
                                    AuthorProfile prof = (AuthorProfile) pageContext.getAttribute("profile");
                                    String[] desc = pattern.split(prof.getDescription());
                                    pageContext.setAttribute("desc", desc);
                                %>
                                <c:forEach items="${desc}" var="ln">
                                    <c:if test="${profile.type != 4}">
                                        <li>${ln}</li>
                                    </c:if>
                                    <c:if test="${profile.type == 4}">
                                        <% pageContext.setAttribute("link", linkPattern.split((String)pageContext.getAttribute("ln"))); %>
                                        <li><a href="${link[0]}">${link[1]}</a></li>
                                    </c:if>
                                </c:forEach>
            				</ul>
           				</c:if>
 					</c:forEach>
               	<h4><stripes:label for="profile.uri" /></h4><a href="${uri}">${uri}</a>
			</div>
			<div class="author_subjects">
                <h4><fmt:message key="profile.subjectlist.title" /></h4>
                <ul>	
                    <c:set var="subject" value="" />
                    <c:forEach items="${actionBean.results}" var="result" varStatus="status">
                        <c:set var="fld" value="${result.fieldValueMap}" />
                        <c:set var="flds" value="${result.fieldValuesMap}" />           
                        <c:if test="${not empty fld['dc.subject'] && subject != fld['dc.subject']}">
                            <c:set var="subject" value="${fld['dc.subject']}" />
                                 <li>
                                    <h3>${fld['dc.subject']}</h3>
                        </c:if>
                        <c:if test="${not empty fld['dc.subject']}">
                            <ul>
                                <li>
                                    <a href="${ctx}/public/view/item/${fld['PID']}" class="result_title">${fld['dc.title']}</a>
                                    <c:if test="${not empty fld['dc.description']}"><p>${fld['dc.description']}</p></c:if>
                                    <c:forEach items="${flds['dsm.ids']}" var="dsId" varStatus="sts">
                                        ${sts.index == 0 ? "" : ", "}<a href="${actionBean.datastreamUrl}/get/${fld['PID']}/${dsId}" target="Download" class="download">
                                            <fmt:message key="checkout.header" />
                                            </a>
                                    </c:forEach>
                                </li>
                          	</ul>
    					</c:if>  
                        </li>        
                  	</c:forEach>
					<c:set var="first" value="true"/>
					<c:forEach items="${actionBean.results}" var="result" varStatus="status">
						<c:set var="fld" value="${result.fieldValueMap}" />
						<c:set var="flds" value="${result.fieldValuesMap}" />
						<c:if test="${empty fld['dc.subject']}">
							<c:if test="${first}">
							<c:set var="first" value="false"/>
							<li>
								<h3>No Subject Area</h3>
							</c:if>
                                <ul>
                                    <li>
                                        <a href="${ctx}/public/view/item/${fld['PID']}" class="result_title">${fld['dc.title']}</a>
                                        <c:if test="${not empty fld['dc.description']}"><p>${fld['dc.description']}</p></c:if>
                                        <c:forEach items="${flds['dsm.ids']}" var="dsId" varStatus="sts">
                                            ${sts.index == 0 ? "" : ", "}<a href="${actionBean.datastreamUrl}/get/${fld['PID']}/${dsId}" target="Download" class="download">
                                                <fmt:message key="checkout.header" />
                                                </a>
                                        </c:forEach>
                                    </li>
                                </ul>
							</li>
						</c:if>
					</c:forEach>
            	</ul>
			</div>
        </div>
	</stripes:layout-component>

</stripes:layout-render>
