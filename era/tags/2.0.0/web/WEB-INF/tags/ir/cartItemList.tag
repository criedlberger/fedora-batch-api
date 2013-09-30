<%@ include file="taglibs.tagf" %>

<%@ tag description="This tag produces cart item list." %>
<%@ attribute name="results" type="org.apache.solr.common.SolrDocumentList" required="true" description="The cart items search results." %>
<%@ attribute name="type" type="java.lang.String" required="false" description="The cart items type (item, oversize, missing)." %>

<c:set var="type" value="${empty type ? 'item' : type}" />
<div class="itemlist cart_itemlist">
	<ol>
	<c:forEach items="${results}" var="result" varStatus="status">
		<li class="record">
			<div class="itemlist_info" ${type == 'missing' ? ' style="width: 39em;"': ''}>
              	<h2><a href="${ctx}/public/view/item/${result['PID']}" class="result_title">${result['dc.title'][0]}</a></h2>	
				<c:if test="${not empty result['dc.creator']}"><p class="result_author"><strong><fmt:message key="by" />${' '} </strong> ${result['dc.creator'][0]}</p></c:if>    
                <c:if test="${not empty result['dc.description']}"><p class="result_others">${fnx:trim(result['dc.description'][0], 120)}</p></c:if>
			</div>
			<div class="itemlist_collection">
				<stripes:label for="item.submittingTo" /><br />
				<stripes:label for="community.label" />:
				<c:forEach items="${result['rel.isMemberOf']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/community/${pid}"><ir:community pid="${pid}" /></a></c:forEach>
				<br /><stripes:label for="collection.label" />: 
				<c:forEach items="${result['rel.isMemberOfCollection']}" var="pid" varStatus="sts">${sts.index > 0 ? ', ' : ''}<a href="${ctx}/public/view/collection/${pid}"><ir:collection pid="${pid}" /></a></c:forEach>
			</div>
			<div class="itemlist_actions cart_itemlist_actions">
				<c:if test="${type != 'missing'}">
					<ir:addToCart pid="${result['PID']}" itemIndex="${status.index}" />
					<c:if test="${type == 'oversize'}">
						<c:forEach items="${result['dsm.ids']}" var="dsId" varStatus="sts">
            				${sts.index > 0 ? "" : ""}
							<c:set var="controlGroup" value="dsm.${sts.count}.controlGroup" />
							<c:set var="location" value="dsm.${sts.count}.location" />
							<c:choose>
								<c:when test="${dsId == 'DS1'}">
										<a href="${actionBean.datastreamUrl}/get/${result['PID']}/${dsId}" target="Download" class="download"><fmt:message key="checkout.header" /></a>
								</c:when>
								<c:otherwise>
									<a href="${actionBean.datastreamUrl}/get/${result['PID']}/${dsId}" target="Download" class="download"><fmt:message key="attached.item.header" /></a>
								</c:otherwise>
							</c:choose>
          				</c:forEach>
					</c:if>
				</c:if>
			</div>
		</li>
	</c:forEach>
	</ol>
</div>