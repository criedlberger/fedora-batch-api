<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.10/web/jsp/protected/editBookmark.jsp $
   $Id: editBookmark.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<%@ include file="/jsp/public/mode.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Bookmark - Edit Bookmark" active="0">

	<stripes:layout-component name="html-head">
		</script>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div class="full_box">
		<h2><stripes:label for="bookmark.edit.header" /></h2>
		<div class="subheader">
			<p><fmt:message key="bookmark.edit.subheader" /></p>
		</div>
		<stripes:errors /><stripes:messages />
		<div class="edit_profile">
			<stripes:form id="bookmarkForm" action="/action/bookmark" focus="bookmark.title">
				<stripes:hidden name="bookmark.id" />
				<stripes:hidden name="bookmark.pid" />
				<stripes:hidden name="bookmark.createdDate" />
				<stripes:hidden name="bookmark.title" />
				<stripes:hidden name="next" />
				<ul class="bookmark">
					<li><stripes:label for="bookmark.title" /><div style="display: block; width: 30em; text-align: left; float: left;">${actionBean.bookmark.title}</div></li>
					<li><stripes:label for="bookmark.url" /><ir:handle pid="${actionBean.bookmark.pid}" type="item" var="uri" /><div><a href="${uri}" style="text-decoration: none;">${uri}</a></div></li>
					<li><stripes:label for="bookmark.on" /><fmt:formatDate pattern="${actionBean.dateFormat}" value="${actionBean.bookmark.createdDate}" /></li>
					<li><stripes:label for="bookmark.notes" /><stripes:textarea name="bookmark.notes" /></li>
					<li><stripes:label for="bookmark.tags" /><stripes:text name="tags" /><p><fmt:message key="bookmark.tags.help" /></p></li>
				</ul>
				<stripes:submit name="save" value="${btnSave}" class="save_button" />
				<stripes:link href="/action/myaccount/bookmarks" class="cancel_link"><fmt:message key="button.cancel" /></stripes:link>
			</stripes:form>
		</div>
		</div>
		<div style="clear: both;" />
	</stripes:layout-component>

</stripes:layout-render>
