<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.12/era-1.3/web/jsp/public/read.jsp $
   $Id: read.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<stripes:layout-render name="/jsp/layout/standard.jsp" title="${actionBean.title} Page: ${actionBean.page}">
	<stripes:layout-component name="html-head">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="full_box" style="padding: 0 0 0 0; width: 62em;">
		<h2 style="text-align: center; padding: 1em 0 1em 0;">${actionBean.title}</h2>
		<div class="pages" style="float: left; width: 62em; text-align: center; background-color: #fff;">
		<div>
		<ir:readingPages numPages="20" rows="1" path="${ctx}/public/read/${actionBean.pid}/${actionBean.dsId}" numFound="${actionBean.noOfPages}" start="${actionBean.page}" />
		<a href="${ctx}/public/view/item/${actionBean.pid}">View Item &raquo;</a></div>
		</div>
		<div><stripes:messages /><stripes:errors /></div>
		<div style="float: left; padding-top: 2em;"><img src="${ctx}/tmp/${fn:replace(actionBean.pid, ':', '_')}_${actionBean.page}.png"
			style="width: 62em;" /></div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
