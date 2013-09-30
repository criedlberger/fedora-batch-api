<%-- footer layout component --%>
<br style="clear: both;" />
<div class="footerwrap">
    <div class="footer">
        <div class="footerbox">
            <h2><fmt:message key="footer.quickLinks" /></h2>
            <ul>
                <li><a href="${httpServerUrl}${ctx}/public/login">Login</a></li>
				<li><a href="${httpServerUrl}${ctx}/public/browse">Browse</a></li>
				<li><a id="advImg" onclick="toggleAdvancedSearch(); return false;" href="">Search</a></li>
                <li><a href="${httpServerUrl}${ctx}/action/deposit">Deposit</a></li>
                <li><a href="${httpServerUrl}${ctx}/action/myaccount">MyERA</a></li>
            </ul>
        </div>
        <div class="footerbox middle">
            <h2><fmt:message key="footer.policies" /></h2>
            <ul>
               
                <li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648953">Content Policy</a></li>
                <li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648953">Mediated Deposit</a></li>
				<li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648953">Metadata Policy</a></li>
               
            </ul>
        </div>
        <div class="footerbox">
            <h2><fmt:message key="footer.need.help" /></h2>
            <ul>
               
                <li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648971"><fmt:message key="howtoDeposit.header" /></a></li>
                <li><a href="http://guides.library.ualberta.ca/content.php?pid=87240&sid=648971">FAQ</a></li>
                <li><a href="${httpServerUrl}${ctx}/jsp/public/contactUs.jsp"><fmt:message key="header.contact.us" /></a></li>
            </ul>
        </div>
	</div>
	<div class="ua_footer_wrap">      
		<div class="ua_footer">
	            <h2>
	                <a href="http://www.ualberta.ca/">University of Alberta</a></h2>
	                <ul>
	                    <li><a href="http://www.beartracks.ualberta.ca/">Bear Tracks</a></li>
	                    <li><a href="http://www.emergencies.ualberta.ca/">Emergency</a></li>
	                    <li><a href="http://www.library.ualberta.ca/">Libraries</a></li>
	                    <li><a href="http://www.campusmap.ualberta.ca/">Maps</a></li>
	               </ul>
					<ul>
						<li><a href="http://www.uofaweb.ualberta.ca/policies/">Policies</a></li>
	                    <li><a href="http://www.uofaweb.ualberta.ca/ualberta_about/privacy.html">Privacy</a></li>
	                    <li><a href="http://www.ualberta.ca/ELEARNING/">WebCT</a></li>
	                    <li><a href="https://webmail.ualberta.ca/">Webmail</a></li>
					</ul>
	        </div>

	<div class="copyright"><fmt:message key="footer.copyright" /></div>	
 </div>
</div>
<%@ include file="/jsp/layout/googleAnalytics.jspf" %>
