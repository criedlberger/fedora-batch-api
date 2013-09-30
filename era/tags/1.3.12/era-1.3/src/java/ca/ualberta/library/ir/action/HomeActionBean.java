/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: HomeActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import ca.ualberta.library.ir.domain.Message;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The HomeActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/home/{$event}/{pid}")
public class HomeActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(HomeActionBean.class);

	private String imgUrl;

	private FacetField tagCloud;

	private SolrDocumentList newItems;

	private SolrDocumentList newCollections;

	private SolrDocumentList newCommunities;

	private SolrDocumentList newSubscriptions;

	private Count maxTagCount;

	private Map<String, Long> userTags;

	private QueryResponse resp;

	private boolean expand;

	private List<SyndFeed> featureFeeds;

	private List<String> featureFeedUrls;

	private String pid;

	private String lang;

	private List<Message> messages;

	/**
	 * The HomeActionBean class constructor.
	 */
	public HomeActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("start")
	public Resolution start() {
		try {

			// log.debug("starting home page...");

			// get new items
			resp = services.findNewItems(newItemRows == 0 ? defaultNewItemRows : newItemRows);
			newItems = resp.getResults();

			// get new collections
			if (newCollectionRows > 0) {
				resp = services.findNewCollections(newCollectionRows);
				newCollections = resp.getResults();
			}

			// get new communities
			if (newCommunityRows > 0) {
				resp = services.findNewCommunities(newCommunityRows);
				newCommunities = resp.getResults();
			}

			// get new subscription
			if (user != null && newSubscriptionRows > 0) {
				resp = services.findNewSubscriptions(user.getUsername(), newSubscriptionRows);
				if (resp != null) {
					newSubscriptions = resp.getResults();
				}
			}

			// get home tag cloud
			resp = services.getTagCloud(false, homeTagCloudLimit);
			tagCloud = resp.getFacetField("tags");
			processUserTags(homeTagCloudLimit);

			return new ForwardResolution(uiPath + "/public/home.jsp");
		} catch (Exception e) {
			log.error("Could not initialize home page!", e);
			return new RedirectResolution(uiPath + "/public/home.jsp");
		}
	}

	@HandlesEvent("language")
	public Resolution language() {
		try {
			context.setLanguage(lang);
			return context.getSourcePageResolution();
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("message")
	public Resolution message() {
		try {
			messages = services.getApplicationMessages();
			return new ForwardResolution(uiPath + "/public/message.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("whatsNew")
	public Resolution whatsNew() {
		try {
			// get new items
			resp = services.findNewItems(newItemRows == 0 ? defaultNewItemRows : newItemRows);
			newItems = resp.getResults();
			return new ForwardResolution(uiPath + "/public/whatsNew.jsp");
		} catch (Exception e) {
			log.error("Could not initialize home page!", e);
			return new RedirectResolution(uiPath + "/public/home.jsp");
		}
	}

	@HandlesEvent("getFeatures")
	public Resolution getFeatures() {
		try {
			if (context.getFeatureFeeds() == null) {
				featureFeeds = new ArrayList<SyndFeed>();
				featureFeedUrls = new ArrayList<String>();
				for (int i = 1; i <= feedCount; i++) {
					String url = ApplicationProperties
						.getString("features.feed.url." + i + "_" + context.getLanguage());
					// log.debug("url: " + url);
					featureFeedUrls.add(url);

					URL feedUrl = new URL(url);
					SyndFeedInput input = new SyndFeedInput();
					SyndFeed feed = input.build(new XmlReader(feedUrl));
					featureFeeds.add(feed);
				}
				context.setFeatureFeeds(featureFeeds);
				context.setFeatureFeedUrls(featureFeedUrls);
			} else {
				featureFeeds = context.getFeatureFeeds();
				featureFeedUrls = context.getFeatureFeedUrls();
			}

			return new ForwardResolution(uiPath + "/public/featuresAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("tagCloud")
	public Resolution tagCloud() {
		try {

			// get tags
			resp = services.getTagCloud(false, bookmarkTagCloudLimit);
			tagCloud = resp.getFacetField("tags");
			processUserTags(bookmarkTagCloudLimit);

			return new ForwardResolution(uiPath + "/public/tagCloud.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("tagList")
	public Resolution tagList() {
		try {
			// get tags
			resp = services.getTagCloud(true, bookmarkTagCloudLimit);
			tagCloud = resp.getFacetField("tags");
			processUserTags(bookmarkTagCloudLimit);

			return new ForwardResolution(uiPath + "/public/tagCloud.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("checkCCIDLogin")
	public Resolution checkCCIDLogin() {
		// log.debug("checking ccid login...");
		try {
			if (context.getCCIDUser() != null) {
				return new StreamingResolution(TEXT_HTML, "true");
			}
			if (isCCIDAuth(pid, true)) {
				return new StreamingResolution(TEXT_HTML, "false");
			} else {
				return new StreamingResolution(TEXT_HTML, "true");
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("checkLogin")
	public Resolution checkLogin() {
		// log.debug("checking login...");
		try {
			if (context.getUser() == null) {
				return new StreamingResolution(TEXT_HTML, "false");
			} else {
				return new StreamingResolution(TEXT_HTML, "true");
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getWelcome")
	public Resolution getWelcome() {
		// log.debug("getting welcome message...");
		try {
			return new ForwardResolution(uiPath + "/public/homeAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("version")
	public Resolution version() {
		try {
			context.context.setAttribute(ActionConstants.appVersion, NumberUtils.createInteger(pid) == null ? 1
				: NumberUtils.createInteger(pid));
			return new StreamingResolution("text/html", "Version: " + ActionConstants.appVersion);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	private void processUserTags(int limit) throws SolrServerException {

		// get max tag count for font size calculation
		resp = services.getTagCloud(true, 1);
		List<Count> lst = resp.getFacetField("tags").getValues();
		if (lst != null && lst.size() > 0) {
			maxTagCount = lst.get(0);
			// log.debug("maxTagCount.name: " + maxTagCount.getName() + ", " + maxTagCount.getCount());
		}

		if (user != null) {
			// get user tag for share tag font color
			List<String> ft = new ArrayList<String>();
			ft.add("bm.donotShare:false");
			resp = services.getTagsByOwnerId(user.getUsername(), ft, false, -1);
			List<Count> tl = resp.getFacetField("tags").getValues();
			if (tl != null) {
				userTags = new HashMap<String, Long>();
				for (Count cnt : tl) {
					userTags.put(cnt.getName(), cnt.getCount());
				}
			}
		}
	}

	/**
	 * The getImgUrl getter method.
	 * 
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * The setImgUrl setter method.
	 * 
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * The getTagCloud getter method.
	 * 
	 * @return the tagCloud
	 */
	public FacetField getTagCloud() {
		return tagCloud;
	}

	/**
	 * The setTagCloud setter method.
	 * 
	 * @param tagCloud the tagCloud to set
	 */
	public void setTagCloud(FacetField tagCloud) {
		this.tagCloud = tagCloud;
	}

	/**
	 * The getNewItems getter method.
	 * 
	 * @return the newItems
	 */
	public SolrDocumentList getNewItems() {
		return newItems;
	}

	/**
	 * The setNewItems setter method.
	 * 
	 * @param newItems the newItems to set
	 */
	public void setNewItems(SolrDocumentList newItems) {
		this.newItems = newItems;
	}

	/**
	 * The getNewCollections getter method.
	 * 
	 * @return the newCollections
	 */
	public SolrDocumentList getNewCollections() {
		return newCollections;
	}

	/**
	 * The setNewCollections setter method.
	 * 
	 * @param newCollections the newCollections to set
	 */
	public void setNewCollections(SolrDocumentList newCollections) {
		this.newCollections = newCollections;
	}

	/**
	 * The getNewCommunities getter method.
	 * 
	 * @return the newCommunities
	 */
	public SolrDocumentList getNewCommunities() {
		return newCommunities;
	}

	/**
	 * The setNewCommunities setter method.
	 * 
	 * @param newCommunities the newCommunities to set
	 */
	public void setNewCommunities(SolrDocumentList newCommunities) {
		this.newCommunities = newCommunities;
	}

	/**
	 * The getMaxTagCount getter method.
	 * 
	 * @return the maxTagCount
	 */
	public Count getMaxTagCount() {
		return maxTagCount;
	}

	/**
	 * The setMaxTagCount setter method.
	 * 
	 * @param maxTagCount the maxTagCount to set
	 */
	public void setMaxTagCount(Count maxTagCount) {
		this.maxTagCount = maxTagCount;
	}

	/**
	 * The getHomeTagCloudLimit getter method.
	 * 
	 * @return the homeTagCloudLimit
	 */
	public int getHomeTagCloudLimit() {
		return homeTagCloudLimit;
	}

	/**
	 * The getUserTags getter method.
	 * 
	 * @return the userTags
	 */
	public Map<String, Long> getUserTags() {
		return userTags;
	}

	/**
	 * The setUserTags setter method.
	 * 
	 * @param userTags the userTags to set
	 */
	public void setUserTags(Map<String, Long> userTags) {
		this.userTags = userTags;
	}

	/**
	 * The getNewSubscriptions getter method.
	 * 
	 * @return the newSubscriptions
	 */
	public SolrDocumentList getNewSubscriptions() {
		return newSubscriptions;
	}

	/**
	 * The setNewSubscriptions setter method.
	 * 
	 * @param newSubscriptions the newSubscriptions to set
	 */
	public void setNewSubscriptions(SolrDocumentList newSubscriptions) {
		this.newSubscriptions = newSubscriptions;
	}

	/**
	 * The isExpand getter method.
	 * 
	 * @return the expand
	 */
	public boolean isExpand() {
		return expand;
	}

	/**
	 * The setExpand setter method.
	 * 
	 * @param expand the expand to set
	 */
	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	/**
	 * The getFeedItems getter method.
	 * 
	 * @return the feedItems
	 */
	public int getFeedItems() {
		return feedItems;
	}

	/**
	 * The getFeedCount getter method.
	 * 
	 * @return the feedCount
	 */
	public int getFeedCount() {
		return feedCount;
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
	 * The getFeatureFeedUrls getter method.
	 * 
	 * @return the featureFeedUrls
	 */
	public List<String> getFeatureFeedUrls() {
		return featureFeedUrls;
	}

	/**
	 * The setFeatureFeedUrls setter method.
	 * 
	 * @param featureFeedUrls the featureFeedUrls to set
	 */
	public void setFeatureFeedUrls(List<String> featureFeedUrls) {
		this.featureFeedUrls = featureFeedUrls;
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
	 * The getLang getter method.
	 * 
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * The setLang setter method.
	 * 
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * The getMessages getter method.
	 * 
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}

}
