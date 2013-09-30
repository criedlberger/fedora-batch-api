/*----------------------------------------------------------------------------*/
// University of Alberta Libraries
// Information Technology and Services
// Project: Instition Repository 
// Author: Piyapong Charoenwattana
// Version: $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
// $Id: application.js 5427 2012-07-12 20:30:12Z pcharoen $
// $HeadURL: https://code.library.ualberta.ca/svn/repos/era/tags/1.3.9/web/js/application.js $
/*----------------------------------------------------------------------------*/

function bigWaiting() {
	window.scrollTo(0,0);
	$(document.body).setStyle({'overflow': 'hidden'}); 
	$(document.body).startWaiting('bigWaiting');
}

/*-- start submit form --*/
/* add metadata input from */
function addMetadataField(addmore) {
	var fld = $(addmore).previous().clone(true);
	var id = parseInt(fld.id.split('.')[1]);
	if ($('field.' + String(id + 1))) {
		alert(tooManyFields);
		return;
	}
	fld.setAttribute('id', 'field.' + String(id + 1));
	var hint = $(fld).select('div.hint');
	if (hint.length > 0) {
		$(hint[0]).remove();
	}
	var error = $(fld).select('div span.field_error');
	if (error.length > 0) {
		$(error[0]).remove();
	}
	var descs = $(fld).descendants();
	for (i = 0; i < descs.length; i++) {
		var ele = descs[i];
		if (ele.name) {
			ele.name = ele.name.replace(String(id), String(id + 1));
			if (ele.name.indexOf('].fieldName') > -1) {
				ele.value = ele.value.replace(String(id), String(id + 1));
			} else if (ele.name.indexOf('].value') > -1) {
				ele.value = '';
			}
		}
	}
	if ($(fld).select('a.remove').length == 0) {
		$(fld).insert({ 
			bottom: '<a href="" onclick="$(this).up().remove(); return false;" class="remove">' + removeDublinCoreLabel + '</a>' 
		});
	}
	$(addmore).previous().insert({ after: $(fld) });
}
/*-- end submit form --*/

/*-- start deposit/submit form --*/
/* add/delete dublincore for old deposit form */
function insertDublinCoreField(element, i) {
	$(element).previous().insert({ after: 
		'<div id="dublinCore.' + i + '">' +
			'<input type="text" name="dublinCore.fields[' + i + '].values" />&nbsp;' + 
			'<a href="#" class="addmore" onclick="deleteDublinCoreField(this); return false;" />' + removeDublinCoreLabel + '</a>' +
		'</div>'
	});
	$(element).previous().down().focus();
}

function deleteDublinCoreField(element) {
	$(element).up().remove();
}

var dsId = 0;
function insertDatastream(element) {
	dsId++;
	$(element).previous().insert({ after: 
		'<div id="datastreams">' +
			'<input type="file" name="files[' + dsId + ']" class="file" />&nbsp;' +
			'<a href="#" class="remove" onclick="deleteDatastream(this); return false;">' + removeDatastreamLabel + '</a>' +
		'</div>'
	});
	$(element).previous().down().focus();
}

function deleteDatastream(element) {
	$(element).up().remove();
}

function setFileLabel(element) {
	// set datastream.file
	$(element).previous(0).value = $(element).value;
	var s = $(element).value.replace('/', '\\').split('\\');
	// set datastream.label
	$(element).previous(1).value = s[s.length -1];
}

function setLicenseListTitle() {
	var ops = $('licenseList').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('licenseTitle').value = ops[i].innerHTML;
		}
	}			
	$('licenseFile').value = '';
	$('licenseText').value = '';
}

function setLicenseFileTitle() {
	$('licenseTitle').value = $('licenseFile').value;
	$('licenseList').value = '0';
	$('licenseText').value = '';
}

function setLicenseTextTitle() {
	$('licenseTitle').value = $('licenseText').value.substring(0, 40) + '...';
	$('licenseList').value = '0';
	$('licenseFile').value = '';
}

function submitDepositForm() {
	$('properties.label').value = $('dublinCore.fields[1].values[0]').value;
	$(document.body).startWaiting('bigWaiting'); 
}

var delDsId = 0;

function setFileLabel(i) {
	if ($('datastreams[' + i + '].label').value == '') {
		var s = $('files[' + i + ']').value.replace('/', '\\').split('\\'); 
		$('datastreams[' + i + '].label').value = s[s.length - 1];
	}
	$('datastreams[' + i + '].file').value = $('files[' + i + ']').value;
	$('datastreams[' + i + '].contentType').value = 'file';
}

function setUrlLabel(i) {
	if ($('datastreams[' + i + '].label').value == '') {
		var s = $('datastreams[' + i + '].url').value.split('/');
		$('datastreams[' + i + '].label').value = s[s.length - 1];
	}
	$('datastreams[' + i + '].contentType').value = 'url';
}

function showFile(i) {
	$('files[' + i + ']').value = '';
	$('fileinputs[' + i + ']').show();
	$('datastreams[' + i + '].url').value = '';
	$('datastreams[' + i + '].url').hide();
	$('datastreams[' + i + '].file').value = '';
	$('datastreams[' + i + '].contentType').value = 'file';
	// $('thumbnailDiv').show();
}

function showUrl(i) {
	$('files[' + i + ']').value = '';
	$('fileinputs[' + i + ']').hide();
	$('datastreams[' + i + '].file').value = '';
	$('datastreams[' + i + '].url').value = '';
	$('datastreams[' + i + '].url').show();
	$('datastreams[' + i + '].contentType').value = 'url';
	// $('thumbnail').value = false;
	// $('thumbnailDiv').hide();
}

function showLicenseList() {
	$('licenseFile').value = '';
	$('licenseFileName').value = '';
	$('licenseText').value = '';
	$('licenseListTable').show();
	$('licenseUploadTable').hide();
	$('licenseTextTable').hide();
}

function showLicenseUpload() {
	$('licenseFileName').value = $('licenseFile').value;
	$('licenseList').value = '0';
	$('licenseText').value = '';
	$('licenseListTable').hide();
	$('licenseUploadTable').show();
	$('licenseTextTable').hide();
}

function showLicenseText() {
	$('licenseList').value = '0';
	$('licenseFile').value = '';
	$('licenseFileName').value = '';
	$('licenseListTable').hide();
	$('licenseUploadTable').hide();
	$('licenseTextTable').show();
}

function setLicenseListTitle() {
	var ops = $('licenseList').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('licenseTitle').value = ops[i].innerHTML;
		}
	}			
	$('licenseFile').value = '';
	$('licenseText').value = '';
}

function setLicenseFileTitle() {
	$('licenseTitle').value = $('licenseFile').value;
	$('licenseList').value = '0';
	$('licenseText').value = '';
}

function setLicenseTextTitle() {
	$('licenseTitle').value = $('licenseText').value.substring(0, 40) + '...';
	$('licenseList').value = '0';
	$('licenseFile').value = '';
}

/*-- add community --*/
function addCommunity(element) {
	
	// check selected communities
	var coms = $('itemForm').elements['coms'];
	if (coms) {
		if (coms.value == $(element).value) {
			$(element).selectedIndex = 0;
			return;
		}
		for (var i = 0; i < coms.length; i++) {
			if (coms[i].value == $(element).value) {
				$(element).selectedIndex = 0;
				return;
			}
		}
	}
	
	// add selected community
	if ($(element).selectedIndex > 0) {
		$('community_list').insert({ bottom: 
			'<li class="floater3">' + 
				'<input type="hidden" name="coms" value="' + $(element).value + '" />' + 
				$(element).options[$(element).selectedIndex].innerHTML +
				' <a href="#" class="remove" onclick="removeCommunity($(this).up()); return false;">' + removeCommunityLabel + '</a> ' +
			'</li>'
		});
		$(element).selectedIndex = 0;
	}
	
	// get collection list
	getCollectionList();
}

function removeCommunity(element) {
	$(element).remove();
	getCollectionList();
}

/*-- add collection --*/
function addCollection(element) {
	
	// check selected collections
	var cols = $('itemForm').elements['cols'];
	if (cols) {
		// check single value
		if (cols.value == $(element).value) {
			$(element).selectedIndex = 0;
			return;
		}
		// check value array
		for (var i = 0; i < cols.length; i++) {
			if (cols[i].value == $(element).value) {
				$(element).selectedIndex = 0;
				return;
			}
		}
	}

	if ($(element).value == '') {
		$('collectionError').innerHTML = collectionError;
		$(element).selectedIndex = 0;
		return;
	}
	
	// add selected collecion
	if ($(element).selectedIndex > 0) {
		$('collectionError').innerHTML = '';
		$('collection_list').insert({ bottom: 
			'<li class="floater3">' + 
				'<input type="hidden" name="cols" value="' + $(element).value + '" />' + 
				$(element).options[$(element).selectedIndex].innerHTML.replace('&nbsp;&nbsp;&nbsp;&nbsp;', '') +
				' <a href="#" class="remove" onclick="removeCollection($(this).up()); return false;">' + removeCollectionLabel + '</a> ' +
			'</li>'
		});
	}
	$(element).selectedIndex = 0;
}

function removeCollection(element) {
	$(element).remove();
}

function getCollectionsByCommunity(element) {
	new Ajax.Updater('collections', ctx + '/ajax/deposit/getCollectionList/' + $(element).value);
}
/*-- end deposit/submit form --*/

/*-- add dublinCore in Edit Item Metadata page --*/
function addDublinCoreField(element, i) {
	$(element).previous().insert({ after: 
		'<li id="dublinCore.' + i + '">' +
			'<label for="empty">&nbsp;</label>' +
			'<input type="text" name="dublinCore.fields[' + i + '].values" />&nbsp;' +
			'<a href="#" class="addmore" onclick="$(this).up().remove(); return false;" />' + removeDublinCoreLabel + '</a>' +
		'</li>'
	});
	$(element).previous().down().next().focus();
}

function addDublinCore(element, i) {
	$(element).previous().insert({ after: 
		'<div id="dublinCore.' + i + '">' +
			'<input type="text" name="item.dublinCore.fields[' + i + '].values" />&nbsp;' + 
			'<a href="#" class="addmore" onclick="$(this).up().remove(); return false;" />' + removeDublinCoreLabel + '</a>' +
		'</div>'
	});
	$(element).previous().down().focus();
}

function removeDatastream(element) {
	$(element).previous(1).addClassName('removed');
	var dsId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="removeDsIds" value="' + dsId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="restoreDatastream(this); return false;">' + restoreDatastreamLabel + '</a>'
	});
	$(element).remove();
}

function restoreDatastream(element) {
	$(element).previous(1).removeClassName('removed');
	var dsId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="dsIds" value="' + dsId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="removeDatastream(this); return false;">' + removeDatastreamLabel + '</a>'
	});
	$(element).remove();
}

function removeFile(element) {
	$(element).previous(1).addClassName('removed');
	var fileId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="removeFileIds" value="' + fileId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="restoreFile(this); return false;">' + restoreDatastreamLabel + '</a>'
	});
	$(element).remove();
}

function restoreFile(element) {
	$(element).previous(1).removeClassName('removed');
	var fileId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="fileIds" value="' + fileId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="removeFile(this); return false;">' + removeDatastreamLabel + '</a>'
	});
	$(element).remove();
}


function removeLicense(element) {
	$(element).previous(1).addClassName('removed');
	var dsId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="removeLcId" value="' + dsId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="restoreLicense(this); return false;">' + restoreLicenseLabel + '</a>'
	});
	$(element).remove();
}

function restoreLicense(element) {
	$(element).previous(1).removeClassName('removed');
	var dsId = $(element).previous().value;
	$(element).previous().remove();
	$(element).insert({ before:
		'<input type="hidden" name="lcId" value="' + dsId + '" />&nbsp;' +
		'<a href="#" class="remove" onclick="removeLicense(this); return false;">' + removeLicenseLabel + '</a>'
	});
	$(element).remove();
}


/**
 * Toggle communities and collections field in deposit5.jsp
 * @deprecated don't use anymore to allow user to enter collections and communities
 */
function toggleResearcher() {
	if ($('researcher').checked) {
		$('relationships').hide();
	} else {
		$('relationships').show();
	}
}

function addCols() {
	var ops = $('item.cols').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('item.memberOfCols').insert(ops[i]);
		}
	}			
}

function removeCols() {
	var ops = $('item.memberOfCols').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('item.cols').insert(ops[i]);
		}
	}			
}

function addComs() {
	var ops = $('item.coms').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('item.memberOfComs').insert(ops[i]);
		}
	}			
}

function removeComs() {
	var ops = $('item.memberOfComs').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('item.coms').insert(ops[i]);
		}
	}			
}

function markMemberOf(element) {
	if ($(element)) {
		var ops = $(element).childElements();
		for (var i = 0; i < ops.length;  i++) {
			ops[i].selected = true;
		}
	}			
}

function getCollections() {
	$(document.body).startWaiting('bigWaiting');
	new Ajax.Updater('collectionList', ctx + '/ajax/deposit/getCollectionList/' + $('item.communities').value, {
		onComplete: function(transport) {
			var mems = $('item.memberOfCols').childElements();
			var cols = $('item.cols').childElements();
			for (var i = 0; i < cols.length; i++) {
				for (var j = 0; j < mems.length; j++) {
					if (cols[i].value == mems[j].value) {
						$(cols[i]).remove();
					}
				}			
			}
			$(document.body).stopWaiting();
		}
	});
}
    		
function submitStep1() {
	$('depositForm').submit();
}

function submitStep5() {
	markMemberOf('item.memberOfComs');
	markMemberOf('item.memberOfCols');
	$(document.body).startWaiting('bigWaiting'); 
	return true;
}

function submitDepositForm() {
	$('properties.label').value = $('dublinCore.fields[1].values[0]').value;
	$(document.body).startWaiting('bigWaiting'); 
	return true;
}

/*-- thumbnail --*/
var intervalId;

function uploadPicture() {
	$('path').value = $('file').value;
	$('imagePath').value = $('file').value;
	$('uploadForm').submit();
	intervalId = window.setInterval(showPicture, 500);
	$(document.body).startWaiting('bigWaiting'); 
}

function showPicture() {
	var fn = window.frames['fileUpload'].document.getElementById('filename');
	if (fn) {
		var filename = fn.innerHTML;
		if (filename != '') {
			$('logo').src = ctx + '/ajax/thumbnail/get/' + filename;
			$('filename').value = filename;
			window.clearInterval(intervalId);
			$(fn).innerHTML = '';
			$(document.body).stopWaiting();
		}
	}
}

/*-- collection --*/
function addCommunities() {
	var ops = $('collection.communities').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('collection.memberOf').insert(ops[i]);
		}
	}			
}

function removeCommunities() {
	var ops = $('collection.memberOf').childElements();
	for (var i = 0; i < ops.length;  i++) {
		if (ops[i].selected) {
			$('collection.communities').insert(ops[i]);
		}
	}			
}

function submitCollectionForm() {
	bigWaiting();
	markMemberOf('collection.memberOf');
}			

function submitCommunityForm() {
	bigWaiting();
	$('communityForm').submit();
}			

/*-- search and myaccount --*/
function getMemberOfCollections(pid, elementId) {
	new Ajax.Updater($(elementId),  ctx + '/public/search/getMemberOfCollections', {
		parameters: { pid: pid }
	});
}

function getMemberOfCommunities(communityIds, elementId) {
	new Ajax.Updater($(elementId),  ctx + '/public/search/getMemberOfCommunities', {
		parameters: { communityIds: communityIds.substring(1, communityIds.length - 1) }
	}); 
}

/**
 * @deprecated Use img.src load image directly
 */
function getImageUrl(img, pid) {
	new Ajax.Request(ctx + '/public/search/getImageUrl', {
		parameters: { pid: pid },
		onComplete: function(transport) {
			$(img).src = transport.responseText.strip();
		}
	}); 
}

/*-- itemInformation --*/
function submitInformationForm() {
	$('informationForm').submit();
	$(document.body).startWaiting('bigWaiting');
}			

/*-- itemProperties --*/
function submitPropertiesForm() {
	$('propertiesForm').submit();
	$(document.body).startWaiting('bigWaiting');
}			


/*-- home --*/
/**
 * @deprecated Use img.src load image directly
 */
function getPublicImageUrl(img, pid) {
	new Ajax.Request(ctx + '/public/home/getImageUrl', {
		parameters: { pid: pid },
		onComplete: function(transport) {
			$(img).src = transport.responseText.strip();
		}
	}); 
}

function getAdminSummary() {
	new Ajax.Updater('adminSummary', ctx + '/action/admin/getAdminSummary', { 
		onComplete: function(transport) {
			var admin = jar.get('admin');
			if (admin) {
				if (admin.visible) {
					toggleAdminSummary();
				}
			} else {
				toggleAdminSummary();
			}
		}
	});
}

function toggleAdminSummary() {
	if ($('adminMenu').visible()) {
		$('adminButton');
		$('adminMenu').toggle();
		var admin = { visible: false };
		jar.put('admin', admin);
	} else {
		$('adminButton'); 
		$('adminMenu').toggle(); 
		var admin = { visible: true };
		jar.put('admin', admin);
	}
}

/*-- itemDatastreams --*/
function purgeDatastream(i) {
	$('datastream[' + i + ']').hide();
	$('purgeDatastreams').insert('<input type="hidden" name="purgeDatastreams[' + i + ']" value="' + $('datastreams[' + i + '].dsId').value + '"/>');
}


/*-- itemProperties --*/
function submitPropertiesForm() {
	$('propertiesForm').submit();
	$(document.body).startWaiting('bigWaiting');
}

/*-- itemLicense --*/
function showLicenseType() {
	$('licenseListTable').hide();
	$('licenseUploadTable').hide();
	$('licenseTextTable').hide();
	if ($('licenseType').value == 'LIST') {
		$('licenseListTable').show();
		//$('licensePrompt').hide();
	} else if ($('licenseType').value == 'FILE') {
		$('licenseUploadTable').show();
		//$('licensePrompt').show();
	} else if ($('licenseType').value == 'TEXT') {
		$('licenseTextTable').show();
		//$('licensePrompt').show();
	} 
}

/*-- itemCollection --*/
function submitItemCollectionForm() {
	markMemberOf('item.memberOfComs');
	markMemberOf('item.memberOfCols');
	$('collectionForm').submit();
	$(document.body).startWaiting('bigWaiting');
}			

/*-- search --*/

/**
 * Get more narrow search result for a filed.
 */
function moreNarrowSearch(ele, fn, fl, offset) {
	new Ajax.Request(ctx + '/public/search/moreNarrowSearch', {
		parameters: {
			q: $('_q').value,
			fq: $('_fq').value,
			sort: $('_sort').value,
			offset: offset,
			narrowField: fn
		},
		onComplete: function(transport) {
			$(fl).insert({ bottom: transport.responseText });
			$(ele).up().remove();
		}
	});
}

function moreBrowse(ele, fn, fl, offset) {
	new Ajax.Request(ctx + '/public/browse/more', {
		parameters: {
			browseField: fn,
			offset: offset
		},
		onComplete: function(transport) {
			$(fl).insert({ bottom: transport.responseText });
			$(ele).up().remove();
		}
	});
}

function moreInitial(ele, fn, fl, prefix, offset) {
	new Ajax.Request(ctx + '/public/browse/moreInitial', {
		parameters: {
			browseField: fn,
			prefix: prefix,
			offset: offset
		},
		onComplete: function(transport) {
			$(fl).insert({ bottom: transport.responseText });
			$(ele).up().remove();
		}
	});
}

function toggleNarrowSearch(fld) {
	if ($(fld).visible()) {
		$(fld + '.more').innerHTML = '&#43; more'; 
		Effect.BlindUp(fld); 
	} else {
		$(fld + '.more').innerHTML = '&#45; less'; 
		Effect.BlindDown(fld); 
	}
}
			
function toggleBrowseMore(fld) {
	if ($(fld).visible()) {
		$(fld + '.more').innerHTML = '&#43; more';
		$(fld).hide(); 
		//Effect.BlindUp(fld); 
	} else {
		$(fld + '.more').innerHTML = '&#45; less';
		$(fld).show(); 
		//Effect.BlindDown(fld); 
	}
}

/*-- feature feeds --*/	
function getFeatureFeeds() {
	var found = 0;
	new Ajax.Updater('features', ctx + '/public/home/getFeatures', { 
		onComplete: function(transport) { 
			for (var i = 0; i < $('feedCount').value; i++) { 
				var feed = jar.get('feed_' + i); 
				if (feed) {
					found++;
				 	if (feed.visible) {
				 		toggleFeed(i);
				 	} 
				} 
			} 
			if (found == 0) {
				toggleFeed(0);
			}
		}
	});
}

function toggleFeed(i) {
	if ($('feed.' + i).visible()) {
		$('fo.' + i).src = ctx + '/images/outline_collapse.gif';
		//Effect.SlideUp($('feed.' + i));
		$('feed.' + i).hide();
		var feed = { visible: false };
		jar.put('feed_' + i, feed); 
	} else {
		$('fo.' + i).src = ctx + '/images/outline_expand.gif';
		//Effect.SlideDown($('feed.' + i));
		$('feed.' + i).show(); 
		var feed = { visible: true };
		jar.put('feed_' + i, feed); 
	}
}

/*-- advanced search --*/
function toggleAdvancedSearch() {
	if ($('advanced').visible()) {
		Effect.BlindUp('advanced');
		$('q').focus();
	} else {
		Effect.BlindDown('advanced');
	}
}
			
function clearSelectedElements(element) {
	if ($(element)) {
		var ops = $(element).childElements();
		for (var i = 0; i < ops.length;  i++) {
			ops[i].selected = false;
		}
	}			
}

/*-- home left contents collapsable --*/
function toggleSubscriptions() {
	if ($('subscriptions').visible()) {
		$('subs').src = ctx + '/images/widget_closed.gif'; 
		$('subscriptions').hide();
		var subs = { visible: false };
		jar.put('subs', subs);
	} else {
		$('subs').src = ctx + '/images/widget_open.gif'; 
		$('subscriptions').show();
		var subs = { visible: true };
		jar.put('subs', subs);
	}
}

function toggleItems() {
	if ($('items').visible()) {
		$('itms').src = ctx + '/images/widget_closed.gif'; 
		$('items').hide();
		var itms = { visible: false };
		jar.put('itms', itms);
	} else {
		$('itms').src = ctx + '/images/widget_open.gif'; 
		$('items').show();
		var itms = { visible: true };
		jar.put('itms', itms);
	}
}

function toggleCollections() {
	if ($('collections').visible()) {
		$('cols').src = ctx + '/images/widget_closed.gif'; 
		$('collections').hide();
		var cols = { visible: false };
		jar.put('cols', cols);
	} else {
		$('cols').src = ctx + '/images/widget_open.gif'; 
		$('collections').show();
		var cols = { visible: true };
		jar.put('cols', cols);
	}
}

function toggleCommunities() {
	if ($('communities').visible()) {
		$('coms').src = ctx + '/images/widget_closed.gif'; 
		$('communities').hide();
		var coms = { visible: false };
		jar.put('coms', coms);
	} else {
		$('coms').src = ctx + '/images/widget_open.gif'; 
		$('communities').show();
		var coms = { visible: true };
		jar.put('coms', coms);
	}
}

/*-- subscription --*/
function getNoOfSubscribers(element, pid) {
	new Ajax.Updater($(element), ctx + '/action/subscription/getNoOfSubscribers', {
		parameters: { 'subscription.pid': pid }
	});
}		

function getSubscription(element, pid, type) {
	new Ajax.Updater($(element), ctx + '/action/subscription/getSubscriptionStatus', {
		parameters: { 'subscription.pid': pid, 'subscription.type': type }
	});
}		

function getSubscriptionInfo(element, pid, type) {
	new Ajax.Updater($(element), ctx + '/action/subscription/getSubscriptionInfo', {
		parameters: { 'subscription.pid': pid, 'subscription.type': type }
	});
}		

function getSubscriptionWithNotify(element, pid, type) {
	new Ajax.Updater($(element), ctx + '/action/subscription/getSubscriptionWithNotify', {
		parameters: { 'subscription.pid': pid, 'subscription.type': type }
	});
}		

function subscribeWithNotify(element, pid, type) {
	new Ajax.Updater($(element), ctx + '/action/subscription/subscribeWithNotify', {
		parameters: { 'subscription.pid': pid, 'subscription.type': type }
	});
}		

function unsubscribeWithNotify(element, id) {
	new Ajax.Updater($(element), ctx + '/action/subscription/unsubscribeWithNotify', {
		parameters: { 'subscription.id': id }
	});
}		

function subscribe(element, pid, type) {
	/*
	new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
		parameters: { 'subscription.pid': pid, 'subscription.type': type }
	});
	*/

	// popup login
	new Ajax.Request(ctx + '/public/home/checkLogin', {
		onComplete: function(transport) {
			if (transport.responseText == 'false') {
				
				// popup login
				Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: { type: 'popup' },
					afterHide: function() {
						new Ajax.Request(ctx + '/public/home/checkLogin', {
							onComplete: function(transport) {
								if (transport.responseText == 'true') {
									new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
										parameters: { 'subscription.pid': pid, 'subscription.type': type }
									});
									new Ajax.Updater($('welcome'), ctx + '/public/home/getWelcome');
								}
							}
						});
					}
				});
			} else if (transport.responseText == 'true') {
				new Ajax.Updater($(element), ctx + '/action/subscription/subscribe', {
					parameters: { 'subscription.pid': pid, 'subscription.type': type }
				});
			}
		}
	});
}		

function unsubscribe(element, id) {
	new Ajax.Updater($(element), ctx + '/action/subscription/unsubscribe', {
		parameters: { 'subscription.id': id }
	});
}		

function unsubscribeInfo(element, id) {
	new Ajax.Updater($(element), ctx + '/action/subscription/unsubscribeInfo', {
		parameters: { 'subscription.id': id }
	});
}		

function notify(element, id) {
	new Ajax.Updater($(element), ctx + '/action/subscription/enable', {
		parameters: { 'subscription.id': id }
	});
}		

function unnotify(element, id) {
	new Ajax.Updater($(element), ctx + '/action/subscription/disable', {
		parameters: { 'subscription.id': id }
	});
}

/*-- shopping cart --*/
function addToCart(element, pid) {
	
	// check if cart is full
	new Ajax.Request(ctx + '/public/cart/isCartFull', {
		onComplete: function(transport) {
			if (transport.responseText == 'true') {
				Modalbox.show(ctx + '/public/cart/getCartFullMessage', { title: appName, width: 450 });
			} else {
				
				// process add to cart
				// check for ccid protected object
				new Ajax.Request(ctx + '/public/home/checkCCIDLogin/' + pid, {
					onComplete: function(transport) {
						if (transport.responseText == 'false') {
							
							// popup ccid login
							Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: { mode: 'ccid', type: 'popup' },
								afterHide: function() {
									new Ajax.Request(ctx + '/public/home/checkCCIDLogin/' + pid, {
										onComplete: function(transport) {
											if (transport.responseText == 'true') {
												new Ajax.Updater($(element), ctx + '/public/cart/add/' + pid, {
													onComplete: function(transport) {
														getCartItemCount();
													}
												});
												new Ajax.Updater($('welcome'), ctx + '/public/home/getWelcome');
											}
										}
									});
								}
							});
						} else if (transport.responseText == 'true') {
							new Ajax.Updater($(element), ctx + '/public/cart/add/' + pid, {
								onComplete: function(transport) {
									getCartItemCount();
								}
							});
						}
					}
				});
			}
		}
	});
}

/*-- submit popup login --*/
function submitLoginPopup() {
	Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: Form.serialize('loginForm') });
}

function removeFromCart(element, pid) {
	new Ajax.Updater($(element), ctx + '/public/cart/remove/' + pid, {
		onComplete: function(transport) {
			getCartItemCount();
		}
	});
}

function getCartItemCount() {
	new Ajax.Updater({ success: 'cartItemCount' }, ctx + '/public/cart/getItemCount');
}

function checkout() {
	new Ajax.Request(ctx + '/public/checkout', {
		onComplete: function(transport) {
			$(document.body).stopWaiting();
			location.href = ctx + '/public/checkout/download/' + transport.responseText.strip();
		}
	});
	$(document.body).startWaiting('bigWaiting');
}

/*-- supported file types popup --*/
function showSupportedFileTypes() {
	Modalbox.show(ctx + '/jsp/public/supportedFileTypes.jsp', { title: appName, width: 550, height: 600 });
}

/*-- add/remove favorite --*/
function addFavorite(element, pid, act) {
	new Ajax.Request(ctx + '/public/home/checkLogin', {
		onComplete: function(transport) {
			if (transport.responseText == 'false') {
				
				// popup login
				Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: { type: 'popup' },
					afterHide: function() {
						new Ajax.Request(ctx + '/public/home/checkLogin', {
							onComplete: function(transport) {
								if (transport.responseText == 'true') {
									new Ajax.Updater($(element), ctx + '/action/favorite/' + act + '/' + pid);
									new Ajax.Updater($('welcome'), ctx + '/public/home/getWelcome');
								}
							}
						});
					}
				});
			} else if (transport.responseText == 'true') {
				new Ajax.Updater($(element), ctx + '/action/favorite/' + act + '/' + pid);
			}
		}
	});
}

/*-- add/remove bookmark --*/
function addBookmark(element, id, pid, act) {
	new Ajax.Request(ctx + '/public/home/checkLogin', {
		onComplete: function(transport) {
			if (transport.responseText == 'false') {
				
				// popup login
				Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: { type: 'popup' },
					afterHide: function() {
						new Ajax.Request(ctx + '/public/home/checkLogin', {
							onComplete: function(transport) {
								if (transport.responseText == 'true') {
									new Ajax.Updater($(element), ctx + '/action/bookmark/' + act +'/' + id + '/' + pid);
									new Ajax.Updater($('welcome'), ctx + '/public/home/getWelcome');
								}
							}
						});
					}
				});
			} else if (transport.responseText == 'true') {
				new Ajax.Updater($(element), ctx + '/action/bookmark/' + act + '/' + id + '/' + pid);
			}
		}
	});
}

/*-- view item --*/
function viewItem(pid) {
	
	// check for ccid protected object
	new Ajax.Request(ctx + '/public/home/checkCCIDLogin/' + pid, {
		onComplete: function(transport) {
			if (transport.responseText == 'false') {
				
				// popup ccid login
				Modalbox.show(ctx + '/public/login', { title: appName, width: 450, params: { mode: 'ccid', type: 'popup' },
					afterHide: function() {
						new Ajax.Request(ctx + '/public/home/checkCCIDLogin/' + pid, {
							onComplete: function(transport) {
								if (transport.responseText == 'true') {
									location.href = ctx + '/public/view/item/' + pid;
								}
							}
						});
					}
				});
			} else if (transport.responseText == 'true') {
				location.href = ctx + '/public/view/item/' + pid;
			}
		}
	});
}

/*-- deposit on behalf of --*/
function addOwner(element) {
	
	// check selected username
	var usrs = $('itemForm').elements['usernames'];
	if (usrs) {
		if (usrs.value == element.id) {
			return;
		}
		for (var i = 0; i < usrs.length; i++) {
			if (usrs[i].value == element.id) {
				return;
			}
		}
	}
	$('owner_list').insert({ bottom: 
		'<li class="floater3">' +
			'<label for="empty">&nbsp;</label>' +
			'<input type="hidden" name="usernames" value="' + element.id + '" />' + 
			$('autocomplete').value +
			'<a href="#" class="remove" onclick="removeOwner(this); return false;">' + removeOwnerNameLabel + '</a>' +
		'</li>'
	});
}

/*-- remove dublinCore in Edit Item Metadata page --*/
function removeOwner(element) {
	$(element).up().remove();
	$('autocomplete').focus();
}

/*-- remove all cart items for cart popup box --*/
function removeAllCartItems() {
	new Ajax.Request(ctx + '/public/cart/removeAll', {
		onSuccess: function(transport) {
			getCartItemCount();
		}
	}); 
	$('mycart_popup').popup.hide(); 
}

