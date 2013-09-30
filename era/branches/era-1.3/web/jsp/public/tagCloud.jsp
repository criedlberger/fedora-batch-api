<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/tagCloud.jsp $
   $Id: tagCloud.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf" %>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home - Tag Cloud" active="${navbarHome}">

	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="full_box">
			<c:if test="${event == 'tagCloud'}">
				<h2><a href="${ctx}/public/browse"><stripes:label for="tagCloud.header" /></a></h2>
			</c:if>
			<c:if test="${event == 'tagList'}">
				<h2><a href="${ctx}/public/browse"><stripes:label for="tagList.header" /></a></h2>
			</c:if>
			<div class="subheader"><fmt:message key="tagCloud.subheader" /></div>
	
			<stripes:errors /><stripes:messages />

			<div class="browse">
				<div class="tagcloud_type">
					<stripes:label for="tagCloud.view" />:
					<span class="${event == 'tagCloud' ? 'active' : '' }">
						<a href="${ctx}/public/home/tagCloud"${event == 'tagCloud' ? ' onclick="return false;"' : ''}><fmt:message key="tagCloud.cloud" /></a>
					</span> 
					<span class="${event == 'tagList' ? 'active' : '' }">
						<a href="${ctx}/public/home/tagList"${event == 'tagList' ? ' onclick="return false;"' : ''}><fmt:message key="tagCloud.list" /></a>
					</span>
				</div>
				<c:if test="${event == 'tagCloud'}">
				<p>
					<c:forEach items="${actionBean.tagCloud.values}" var="tag" varStatus="status"><c:set var="fontSize" value="${(tag.count / actionBean.maxTagCount.count * 100) + 80}" />${status.index > 0 ? ", " : ""}<a href="${ctx}/public/tag/get/${tag.name}" class="${actionBean.userTags[tag.name] != null ? 'tag_link' : 'tag_link'}" style="font-size: ${fontSize}%;">${tag.name}</a></c:forEach>
				</p>				
				</c:if>
				<c:if test="${event == 'tagList'}">
				<p>
					<ul style="margin-left: 10px;">
					<c:forEach items="${actionBean.tagCloud.values}" var="tag" varStatus="status">
						<li><span style="color: #808080;">${tag.count}</span> <a href="${ctx}/public/tag/get/${tag.name}" class="${actionBean.userTags[tag.name] != null ? 'tag_link' : 'tag_link'}" style="margin-left: 2px;">${tag.name}</a></li>
					</c:forEach>
					</ul>
				</p>				
				</c:if>
			</div>
		</div>
    </stripes:layout-component>
</stripes:layout-render>
