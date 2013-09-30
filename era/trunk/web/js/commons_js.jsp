<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/javascript; charset=utf-8" pageEncoding="UTF-8"%>
<fmt:setLocale value="${language}" />

// export page variables to javascript
var httpServerUrl = '${httpServerUrl}';
var httpsServerUrl = '${httpsServerUrl}';
var ctx = '${pageContext.request.contextPath}';
var jar = new CookieJar({expires: '', path: ctx });

// export StripesResources.properties to javascript
var btnBrowse = '<fmt:message key="button.browse" />';
var datastreamFile = '<fmt:message key="datastream.type.file" />';
var datastreamUrl = '<fmt:message key="datastream.type.url" />';
var appName = '<fmt:message key="application.name"/>';

var restoreDatastreamLabel = '<fmt:message key="item.datastream.restore"/>';
var removeDublinCoreLabel = '<fmt:message key="item.dublinCore.remove"/>';
var removeDatastreamLabel = '<fmt:message key="item.datastream.remove"/>';
var removeOwnerNameLabel = '<fmt:message key="item.owner.remove"/>';
var removeCommunityLabel = '<fmt:message key="item.community.remove"/>';
var removeCollectionLabel = '<fmt:message key="item.collection.remove"/>';
var removeLicenseLabel = '<fmt:message key="item.license.remove"/>';
var restoreLicenseLabel = '<fmt:message key="item.license.restore"/>';
var tooManyFields = '<fmt:message key="item.metadata.tooManyFields"/>';
var collectionError = ' (<fmt:message key="item.collectionError" />)'

function confirmDelete(title, type) {
	var msg = new Template('<fmt:message key="confirm.delete" />');
	var vals = { title: title.truncate(40, '...'), type: type };
	if (confirm(msg.evaluate(vals))) {
		return true;
	} else {
		return false;
	}
}

function confirmRemove(title, type) {
	var msg = new Template('<fmt:message key="confirm.remove" />');
	var vals = { title: title.truncate(40, '...'), type: type };
	if (confirm(msg.evaluate(vals))) {
		return true;
	} else {
		return false;
	}
}

function confirmCancel() {
	if (confirm('<fmt:message key="confirm.cancel" />')) {
		return true;
	} else {
		return false;
	}
}

function cancelWorkflow() {
	if (confirm('<fmt:message key="confirm.cancel.workflow" />')) {
		return true;
	} else {
		return false;
	}
}
