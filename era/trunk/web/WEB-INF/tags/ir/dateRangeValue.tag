<%--
	$Id: dateRangeValue.tag 5600 2012-10-05 18:40:05Z pcharoen $
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
--%>
<%@ include file="taglibs.tagf" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.io.*" %>
<%@ tag import="org.apache.commons.logging.*" %>
<%@ tag import="ca.ualberta.library.ir.model.inputform.*" %>
<%@ tag description="This tag uses current date to look up in date-range then return value pair." %>

<%@ attribute name="valuePairs" type="ca.ualberta.library.ir.model.inputform.ValuePairs" required="true" description="The username for getting full user name." %>
<%@ attribute name="var" type="java.lang.String" required="true" description="The request scope variable name to store user object." %>
<%@ attribute name="value" type="java.lang.String" required="false" description="The field value." %>
<%! static final Log log = LogFactory.getLog(DateRange.class); %>
<%! Pair pair; %>
<%
	try {
		if (value != null) {
			for (Pair valuePair : valuePairs.getPairs()) {
				if (valuePair.getStoredValue().equals(value)) {
					pair = valuePair;
					break;
				}
			}
		} else {
			Date date = new Date();
			for (Pair valuePair : valuePairs.getPairs()) {
				if (date.compareTo(valuePair.getDateRange().getDateStart()) >= 0 
					&& date.compareTo(valuePair.getDateRange().getDateEnd()) < 0) {
					pair = valuePair;
					break;
				}
			}
		}
	} catch (Throwable t) {
		log.error("Could not get date range value pair!", t);
	}
%>

<jsp:doBody var="body" />
<% request.setAttribute(var, pair); %>
