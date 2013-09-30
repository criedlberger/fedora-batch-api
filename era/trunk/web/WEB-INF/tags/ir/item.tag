<%--
	$Id: item.tag 5606 2012-10-10 16:45:09Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag description="This tag gets item from service facade using item PID." %>
<%@ tag import="java.util.Map"%>
<%@ tag import="ca.ualberta.library.ir.service.ServiceFacade" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>
<%@ tag import="org.apache.solr.client.solrj.response.QueryResponse" %>
<%@ tag import="org.apache.solr.common.SolrDocument" %>

<%@ attribute name="pid" type="java.lang.String" required="true" description="The item PID." %>
<%@ attribute name="field" type="java.lang.String" required="true" rtexprvalue="false" description="The item field value map." %>
<%@ variable name-from-attribute="field" alias="fld" variable-class="java.util.Map" scope="NESTED" description="The tag nested scope variable to store item field value map." %>
<% 
	String pid = (String) jspContext.getAttribute("pid");
	SolrDocument doc;
	Map fld;
	Map flds;
	if (StringUtils.trimToNull(pid) != null) {
		try {
			ServiceFacade services = (ServiceFacade) session.getAttribute("services");
			QueryResponse resp = services.findObjectByPid(pid);
			if (!resp.getResults().isEmpty()) {
				doc = resp.getResults().get(0);
				jspContext.setAttribute("fld", doc);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<jsp:doBody />
