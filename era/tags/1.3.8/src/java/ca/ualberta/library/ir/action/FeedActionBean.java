/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: FeedActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;

import ca.ualberta.library.ir.domain.Author;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.model.solr.Item;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The FeedActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/feed/{$event}/{feedType}/{param}")
public class FeedActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(FeedActionBean.class);

	private static final int feedRows = ApplicationProperties.getInt("feed.rows");

	/**
	 * ROME Feed Type: rss_0.9, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0,
	 * rss_2.0, atom_0.3, atom_1.0
	 */
	private String param;
	private String feedType;
	private String username;
	private String pid;
	private int feedNo;
	private List<SyndFeed> featureFeeds;

	/**
	 * The FeedActionBean class constructor.
	 */
	public FeedActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("feeds")
	public Resolution feeds() {
		featureFeeds = context.getFeatureFeeds();
		return new ForwardResolution(uiPath + "/public/feeds.jsp");
	}

	@HandlesEvent("feature")
	public Resolution feature() {
		try {
			featureFeeds = context.getFeatureFeeds();
			feedNo = NumberUtils.toInt(param);
			return createFeedResolution(featureFeeds.get(feedNo));
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("item")
	public Resolution item() {
		try {
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);
			feed.setTitle(applicationResources.getString("feed.item.title"));
			feed.setDescription(applicationResources.getString("feed.item.description"));
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/item/" + feedType);
			setFeedProperties(feed);

			feedNo = NumberUtils.toInt(param);
			QueryResponse resp = services.findNewItems(feedNo > 0 ? feedNo : feedRows);
			if (resp != null) {
				feed.setEntries(createItemEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("collection")
	public Resolution collection() {
		try {
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);
			feed.setTitle(applicationResources.getString("feed.collection.title"));
			feed.setDescription(applicationResources.getString("feed.collection.description"));
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/collection/" + feedType);
			setFeedProperties(feed);

			feedNo = NumberUtils.toInt(param);
			QueryResponse resp = services.findNewCollections(feedNo > 0 ? feedNo : feedRows);
			if (resp != null) {
				feed.setEntries(createCollectionEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The createCollectionEntries method.
	 * 
	 * @param resp
	 * @return
	 */
	private List<SyndEntry> createCollectionEntries(QueryResponse resp) {
		List<Collection> cols = resp.getBeans(Collection.class);
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		for (Collection col : cols) {
			SyndEntryImpl entry = new SyndEntryImpl();
			entry.setTitle(col.getTitle());
			entry.setLink(httpServerUrl + request.getContextPath() + "/public/view/collection/" + col.getId());
			entry.setPublishedDate(col.getCreated());
			entry.setUpdatedDate(col.getModified());
			User user = services.getUser(col.getOwnerId());
			if (user == null) {
				entry.setAuthor(col.getOwnerId());
			} else {
				entry.setAuthor(user.getLastName() + "," + user.getFirstName());
			}
			entry.setDescription(buildCollectionDescription(col));
			entry.setUri(buildHandle(col.getId(), HandleType.COLLECTION));
			entries.add(entry);
		}
		return entries;
	}

	@HandlesEvent("community")
	public Resolution community() {
		try {
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);
			feed.setTitle(applicationResources.getString("feed.community.title"));
			feed.setDescription(applicationResources.getString("feed.community.description"));
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/community/" + feedType);
			setFeedProperties(feed);

			feedNo = NumberUtils.toInt(param);
			QueryResponse resp = services.findNewCommunities(feedNo > 0 ? feedNo : feedRows);
			if (resp != null) {
				feed.setEntries(createCommunityEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The createCommunityEntries method.
	 * 
	 * @param resp
	 * @return
	 */
	private List<SyndEntry> createCommunityEntries(QueryResponse resp) {
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		List<Community> coms = resp.getBeans(Community.class);
		for (Community com : coms) {
			SyndEntryImpl entry = new SyndEntryImpl();
			entry.setTitle(com.getTitle());
			entry.setLink(httpServerUrl + request.getContextPath() + "/public/view/community/" + com.getId());
			entry.setPublishedDate(com.getCreated());
			entry.setUpdatedDate(com.getModified());
			User user = services.getUser(com.getOwnerId());
			if (user == null) {
				entry.setAuthor(com.getOwnerId());
			} else {
				entry.setAuthor(user.getLastName() + "," + user.getFirstName());
			}
			entry.setDescription(buildCommunityDescription(com));
			entry.setUri(buildHandle(com.getId(), HandleType.COMMUNITY));
			entries.add(entry);
		}
		return entries;
	}

	@HandlesEvent("communityitems")
	public Resolution communityItems() {
		try {
			QueryResponse resp = null;
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);

			pid = param;
			resp = services.findObjectByPid(pid);
			Community com = resp.getBeans(Community.class).get(0);
			feed.setTitle(MessageFormat.format(applicationResources.getString("feed.community.items.title"),
				com.getTitle()));
			feed.setDescription(com.getDescription());
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/communityitems/" + feedType);
			setFeedProperties(feed);

			resp = services.findCommunityNewItems(pid, feedRows);
			if (resp != null) {
				feed.setEntries(createItemEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("collectionitems")
	public Resolution collectionItems() {
		try {
			QueryResponse resp = null;
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);

			pid = param;
			resp = services.findObjectByPid(pid);
			Collection col = resp.getBeans(Collection.class).get(0);
			feed.setTitle(MessageFormat.format(applicationResources.getString("feed.collection.items.title"),
				col.getTitle()));
			feed.setDescription(col.getDescription());
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/collectionitems/" + feedType);
			setFeedProperties(feed);

			resp = services.findCollectionNewItems(pid, feedRows);
			if (resp != null) {
				feed.setEntries(createItemEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not create collection item feed! (" + pid + ")", e);
			return forwardExceptionError("Could not create collection item feed! (" + pid + ")", e);
		}
	}

	@HandlesEvent("subscription")
	public Resolution subscription() {
		try {
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);
			feed.setTitle(applicationResources.getString("feed.subscription.title"));
			feed.setDescription(applicationResources.getString("feed.subscription.description"));
			username = param;
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/subscription/" + feedType + "/"
				+ username);
			setFeedProperties(feed);

			QueryResponse resp = services.findNewSubscriptions(username, feedRows);
			if (resp != null) {
				feed.setEntries(createItemEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("author")
	public Resolution author() {
		try {
			username = param;
			// log.debug("username: " + username);
			User user = services.getUser(username);

			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);
			String title = applicationResources.getString("feed.author.title");
			String fullName = user.getFirstName() + " " + user.getLastName();
			feed.setTitle(MessageFormat.format(title, fullName));
			feed.setDescription(buildAuthorDescription(user));
			feed.setLink(httpServerUrl + request.getContextPath() + "/public/feed/author/" + feedType + "/" + username);
			setFeedProperties(feed);

			QueryResponse resp = services.findMyItems(username, 0, feedRows, "sort.createdDate desc");
			if (resp != null) {
				feed.setEntries(createItemEntries(resp));
			}
			return createFeedResolution(feed);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The createEntries method.
	 * 
	 * @param resp
	 * @return
	 */
	private List<SyndEntry> createItemEntries(QueryResponse resp) {
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		List<Item> items = resp.getBeans(Item.class);
		for (Item item : items) {
			SyndEntryImpl entry = new SyndEntryImpl();
			entry.setTitle(item.getTitles().get(0));
			entry.setLink(httpServerUrl + request.getContextPath() + "/public/view/item/" + item.getPid());
			entry.setPublishedDate(item.getCreatedDate());
			entry.setUpdatedDate(item.getLastModifiedDate());
			entry.setAuthor(item.getCreators() == null ? "" : item.getCreators().get(0));
			entry.setDescription(buildItemDescription(item));
			entry.setUri(item.getHandle());
			entries.add(entry);
		}
		return entries;
	}

	private Resolution createFeedResolution(SyndFeed feed) throws IOException, FeedException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bos, "UTF-8"));
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
		writer.close();
		StreamingResolution resolution = new StreamingResolution(TEXT_XML, new BufferedReader(new InputStreamReader(
			new ByteArrayInputStream(bos.toByteArray()), "UTF-8")));
		resolution.setCharacterEncoding("UTF-8");
		return resolution;
	}

	/**
	 * The buildAuthorDescription method.
	 * 
	 * @param user
	 * @return
	 */
	private String buildAuthorDescription(User user) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("DateFormatUtils", DateFormatUtils.class);
		model.put("user", user);
		Author author = user.getAuthor();
		if (author != null) {
			model.put("author", author);
			if (author.getPicture() != null) {
				model.put("picture", httpServerUrl + "/public/researcher/getPicture/" + user.getId());
			}
		}
		model.put("handle", buildHandle(user.getUsername(), HandleType.AUTHOR));
		String description = mergeTemplate("feed.author.header", HTML, context.getLanguage(), model);
		return description;
	}

	private SyndContent buildItemDescription(Item item) {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("DateFormatUtils", DateFormatUtils.class);
		model.put("item", item);
		if (item.getThumbnail() != null) {
			model.put("thumbnail", httpServerUrl + "/public/datastream/get/" + item.getPid() + "/THUMBNAIL");
		}
		SyndContentImpl description = new SyndContentImpl();
		description.setType(TEXT_HTML);
		description.setValue(mergeTemplate("feed.item", HTML, context.getLanguage(), model));
		return description;
	}

	private SyndContent buildCollectionDescription(Collection col) {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("DateFormatUtils", DateFormatUtils.class);
		model.put("collection", col);
		if (col.getThumbnail() != null) {
			model.put("thumbnail", httpServerUrl + "/public/datastream/get/" + col.getId() + "/THUMBNAIL");
		}
		User creator = services.getUser(col.getOwnerId());
		model.put("creator", creator);
		if (col.getHandle() == null) {
			col.setHandle(buildHandle(col.getId(), HandleType.COLLECTION));
		}
		SyndContentImpl description = new SyndContentImpl();
		description.setType(TEXT_HTML);
		description.setValue(mergeTemplate("feed.collection", HTML, context.getLanguage(), model));
		return description;
	}

	private SyndContent buildCommunityDescription(Community com) {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("DateFormatUtils", DateFormatUtils.class);
		model.put("community", com);
		if (com.getThumbnail() != null) {
			model.put("thumbnail", httpServerUrl + "/public/datastream/get/" + com.getId() + "/THUMBNAIL");
		}
		User creator = services.getUser(com.getOwnerId());
		model.put("creator", creator);
		if (com.getHandle() == null) {
			com.setHandle(buildHandle(com.getId(), HandleType.COMMUNITY));
		}
		SyndContentImpl description = new SyndContentImpl();
		description.setType(TEXT_HTML);
		description.setValue(mergeTemplate("feed.community", HTML, context.getLanguage(), model));
		return description;
	}

	private void setFeedProperties(SyndFeed feed) {
		feed.setAuthor(applicationResources.getString("feed.author"));
		feed.setCopyright(applicationResources.getString("feed.copyright"));
		SyndImage image = new SyndImageImpl();
		image.setTitle(applicationResources.getString("feed.image.title"));
		image.setUrl(httpServerUrl + request.getContextPath() + applicationResources.getString("feed.image.url"));
		image.setLink(httpServerUrl + request.getContextPath() + applicationResources.getString("feed.image.link"));
		image.setDescription(applicationResources.getString("feed.image.description"));
		feed.setImage(image);
	}

	/**
	 * The getType getter method.
	 * 
	 * @return the type
	 */
	public String getFeedType() {
		return feedType;
	}

	/**
	 * The setType setter method.
	 * 
	 * @param type the type to set
	 */
	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	/**
	 * The getOwnerId getter method.
	 * 
	 * @return the ownerId
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The setOwnerId setter method.
	 * 
	 * @param ownerId the ownerId to set
	 */
	public void setUsername(String ownerId) {
		this.username = ownerId;
	}

	/**
	 * The getFeatureFeeds getter method.
	 * 
	 * @return the featureFeeds
	 */
	public List<SyndFeed> getFeatureFeeds() {
		return featureFeeds;
	}

	/**
	 * The setFeatureFeeds setter method.
	 * 
	 * @param featureFeeds the featureFeeds to set
	 */
	public void setFeatureFeeds(List<SyndFeed> featureFeeds) {
		this.featureFeeds = featureFeeds;
	}

	/**
	 * The getFeedNo getter method.
	 * 
	 * @return the feedNo
	 */
	public int getFeedNo() {
		return feedNo;
	}

	/**
	 * The setFeedNo setter method.
	 * 
	 * @param feedNo the feedNo to set
	 */
	public void setFeedNo(int feedNo) {
		this.feedNo = feedNo;
	}

	/**
	 * The getPid getter method.
	 * 
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The getParam getter method.
	 * 
	 * @return the param
	 */
	public String getParam() {
		return param;
	}

	/**
	 * The setParam setter method.
	 * 
	 * @param param the param to set
	 */
	public void setParam(String param) {
		this.param = param;
	}

}
