<%--
	$Id: item.tag 5427 2012-07-12 20:30:12Z pcharoen $
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
<%@ attribute name="fields" type="java.lang.String" required="true" rtexprvalue="false" description="The item field values map." %>
<%@ variable name-from-attribute="field" alias="fld" variable-class="java.util.Map" scope="NESTED" description="The tag nested scope variable to store item field value map." %>
<%@ variable name-from-attribute="fields" alias="flds" variable-class="java.util.Map" scope="NESTED" description="The tag nested scope variable to store item field values map." %>
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
				fld = doc.getFieldValueMap();
				flds = doc.getFieldValuesMap();
				jspContext.setAttribute("fld", fld);
				jspContext.setAttribute("flds", flds);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<jsp:doBody />
