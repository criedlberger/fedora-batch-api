<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/branches/era-1.3/web/jsp/public/adminTemplate.jsp $
   $Id: adminTemplate.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>

<stripes:layout-render name="/jsp/layout/standard.jsp" title="Home">
	<stripes:layout-component name="contents">
		<div class="search_filters">
			<div class="right_narrow">
			<ul><h2>Sidebar</h2>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
				<li><a>Item 1</a></li>
			</ul>
			</div>
		</div>
		<div>
			<div style="float: left;">
        	<h2 style="margin: 1em 0 1em 1em;">My Items</h2>
			<div style="width: 100%; background-color: #fff;">
				Results for "my items"
			</div>
			</div>
	        <ol class="search_results">
				<li class="record">
					<div class="record_info">
	                 		<h2><a href="${ctx}/public/view/${type}/${fld['PID']}" class="result_title">TEST ITEM</a></h2>	
							<p class="result_author"><strong>by:</strong> Piyapong Charoenwattana </p>    
	                   		<p class="result_subject">
	                   			Item descriptions.
							</p>
					</div>
					<div class="record_actions">
					</div>
				</li>
	        </ol>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
