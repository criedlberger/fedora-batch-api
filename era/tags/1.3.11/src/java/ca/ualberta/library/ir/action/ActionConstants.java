/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ActionConstants.java 5602 2012-10-05 18:48:09Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.UIProperties;

/**
 * The ActionConstants class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5602 $ $Date: 2012-10-05 12:48:09 -0600 (Fri, 05 Oct 2012) $
 */
public interface ActionConstants {

	// application version
	public static final String appVersion = "1.3.11";

	// applicaton temp path
	public static final String tempPath = ApplicationProperties.getString("app.temp.path");

	// megabyte factor
	public static final int MB = 1048576;

	// bytes wording
	public static final String BYTES = "bytes";

	// content type
	public static final String TEXT_PLAIN = "text/plain";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_XML = "text/xml";

	// template type
	public static final String HTML = "html";
	public static final String TEXT = "text";

	// date format use in jsp to format date from search result
	public static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
	public static final String dateFormat = "MMM d, yyyy h:mm a";
	public static final String dateFormatShort = "MMM d, yyyy";

	public static final String ISODatePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String W3CDTFDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	// public static final String solrDatePattern = "EEE MMM d HH:mm:ss zzz yyyy"; // Tue Sep 23 10:35:59 MDT 2008
	public static final String embargoedDatePattern = "yyyy/MM/dd";
	public static final String timestampPattern = "yyyyMMdd'T'HHmmss";
	public static final String dublinCoreDatePattern = "yyyy/MM/dd";

	public static final SimpleDateFormat embargoedDateFormat = new SimpleDateFormat(embargoedDatePattern);
	public static final SimpleDateFormat W3CDTFDateFormat = new SimpleDateFormat(W3CDTFDatePattern);

	// result fields for Fedora findObject
	public static final String[] resultFields = new String[] { "pid", "label", "state", "ownerId", "cDate", "mDate",
		"dcmDate", "title", "description", "type" };

	// pattern constants
	public static final Pattern slashPattern = Pattern.compile("/");
	public static final Pattern commaPattern = Pattern.compile(",");
	public static final Pattern colonPattern = Pattern.compile(":");
	public static final Pattern tabPattern = Pattern.compile("\t");
	public static final Pattern hashPattern = Pattern.compile("#");
	public static final Pattern spacePattern = Pattern.compile("\\s");
	public static final Pattern webcrawlerFilter = Pattern.compile(
		ApplicationProperties.getString("webcrawler.filter"), Pattern.CASE_INSENSITIVE);

	// Solr Query Type
	public static final String SOLR_STANDARD = "solr-standard";
	public static final String SOLR_STANDARD_FACETS = "solr-standard-facets";
	public static final String SOLR_STANDARD_FREE_FACETS = "solr-standard-free-facets";
	public static final String STANDARD_FREE_FACETS = "standard-free-facets";

	public static final String fedoraServerUrl = ApplicationProperties.getString("fedora.protocol") + "://"
		+ ApplicationProperties.getString("fedora.host") + ":" + ApplicationProperties.getString("fedora.port");
	public static final String fedoraRestServiceUrl = ApplicationProperties.getString("fedora.rest.service");
	public static final String fedoraAccessServiceUrl = ApplicationProperties.getString("fedora.access.service");
	public static final String fedoraManageServiceUrl = ApplicationProperties.getString("fedora.management.service");
	public static final String fedoraUsername = ApplicationProperties.getString("fedora.username");
	public static final String fedoraPassword = ApplicationProperties.getString("fedora.password");
	public static final String fedoraHost = ApplicationProperties.getString("fedora.host");

	public static final String mailServer = ApplicationProperties.getString("mail.server");
	public static final String mailAdmin = ApplicationProperties.getString("mail.admin");

	// compile supported content types pattern
	public static final Pattern supportedContentTypes = Pattern.compile(ApplicationProperties
		.getString("supported.contentTypes"));
	public static final String[] supportedFileTypes = ApplicationProperties.getString("supported.contentTypes").split(
		"\\|");

	// server url
	public static final String httpServerUrl = ApplicationProperties.getString("http.server.url");
	public static final String httpsServerUrl = ApplicationProperties.getString("https.server.url");

	// thumnail size
	public static final int thumbnailWidth = NumberUtils.toInt(ApplicationProperties.getString("thumbnail.width"));
	public static final int thumbnailHeight = NumberUtils.toInt(ApplicationProperties.getString("thumbnail.height"));

	// handle system
	public static final boolean handleEnabled = BooleanUtils.toBoolean(ApplicationProperties
		.getString("handle.enabled"));
	public static final String handleServer = ApplicationProperties.getString("handle.server");
	public static final String handlePrefix = ApplicationProperties.getString("handle.prefix");
	public static final String irPrefix = StringUtils.trimToEmpty(ApplicationProperties.getString("handle.ir.prefix"));

	// oai provider properties
	public static final boolean proaiEnabled = BooleanUtils.toBoolean(ApplicationProperties.getString("proai.enabled"));
	public static final String proaiItemId = ApplicationProperties.getString("proai.item.id");

	// metadata transformation
	public static final String metadataId = ApplicationProperties.getString("metadata.datastream.id");
	public static final String datastream2metadataXsl = ApplicationProperties
		.getString("metadata.datastream2metadata.xsl");
	public static final String metadata2datastreamXsl = ApplicationProperties
		.getString("metadata.metadata2datastream.xsl");

	public static final List<UIProperties> uiList = UIProperties.getUIList();

	public static final String defaultUIPath = ApplicationProperties.getString("ui.default.path");
	public static final String defaultContentType = ApplicationProperties.getString("ui.default.contentType");

	// initialize maximum download cart file size
	public static final float downloadFileSize = NumberUtils.toFloat(ApplicationProperties
		.getString("download.file.size"));

	// initialize maximum upload item file size
	public static final float uploadFileSize = NumberUtils.toFloat(ApplicationProperties.getString("upload.file.size"));

	// initialize fedora log message format
	public static final MessageFormat logMessageFormat = new MessageFormat(
		ApplicationProperties.getString("fedora.logMessagePattern"));

	public static final String dsmUrl = "/public/datastream";

	public static final int defaultNewItemRows = 3;
	public static final int newItemRows = ApplicationProperties.getInt("new.item.rows");
	public static final int newCollectionRows = ApplicationProperties.getInt("new.collection.rows");
	public static final int newCommunityRows = ApplicationProperties.getInt("new.community.rows");
	public static final int newSubscriptionRows = ApplicationProperties.getInt("new.subscription.rows");
	public static final int homeTagCloudLimit = ApplicationProperties.getInt("home.tagCloud.limit");
	public static final int bookmarkTagCloudLimit = ApplicationProperties.getInt("bookmark.tagCloud.limit");
	public static final int feedItems = ApplicationProperties.getInt("features.feed.items");
	public static final int feedCount = ApplicationProperties.getInt("features.feed.count");

	public static final int defaultRows = ApplicationProperties.getInt("default.rows");
	public static final int numPages = ApplicationProperties.getInt("bookmark.page.pages");
	public static final int moreLikeThisItemCount = ApplicationProperties.getInt("mlt.item.count");
	public static final int narrowItemCount = ApplicationProperties.getInt("narrow.item.count");
	public static final int browseItemCount = ApplicationProperties.getInt("browse.item.count");
	public static final int suggestionCount = ApplicationProperties.getInt("search.suggestion.count");
	public static final int moreNarrowItemCount = ApplicationProperties.getInt("narrow.more.count");
	public static final int moreBrowseItemCount = ApplicationProperties.getInt("browse.more.count");

	public static final int communityRows = ApplicationProperties.getInt("community.rows");

	public static final String itemPattern = ApplicationProperties.getString("download.item.filename.pattern");
	public static final String zipPattern = ApplicationProperties.getString("download.zip.filename.pattern");

	public static final int bookmarkTagsLimit = ApplicationProperties.getInt("bookmark.tags.limit");
	public static final int relatedTagsLimit = ApplicationProperties.getInt("realated.tags.limit");

}
