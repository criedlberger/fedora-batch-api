/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: MyAccountActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;

/**
 * The MyAccountActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/myaccount/{$event}/{start}/{sortBy}")
public class MyAccountActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(MyAccountActionBean.class);

	private Map<String, Long> itemCounts;

	private long itemCount;
	private long collectionCount;
	private long communityCount;
	private long favoriteCount;
	private long bookmarkCount;
	private long subscriptionCount;
	private long pendingItemCount;
	private long downloadCount;

	private long bookmarkStats;
	private long favoriteStats;
	private long subscriptionStats;

	private List<SolrDocument> subscriptionList;
	private List<SolrDocument> favoriteList;

	/**
	 * The MyAccountActionBean class constructor.
	 */
	public MyAccountActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("home")
	public Resolution home() {
		getMyAccountSummary();
		return new ForwardResolution(uiPath + "/protected/myAccountHome.jsp");
	}

	@HandlesEvent("getAccountSummary")
	public Resolution getAccountSummary() {
		getMyAccountSummary();
		return new ForwardResolution(uiPath + "/protected/myAccountSummaryAjax.jsp");
	}

	/**
	 * The getMyAccountSummary method.
	 */
	private void getMyAccountSummary() {
		try {

			itemCounts = new HashMap<String, Long>();
			QueryResponse resp = null;

			resp = services.findManualApprovalItems(0, 0, null);
			itemCounts.put("approval", resp.getResults().getNumFound());

			resp = services.findReviewItems(user.getUsername(), 0, 0, null);
			itemCounts.put("review", resp.getResults().getNumFound());

			resp = services.findMySavedItems(user.getUsername(), 0, 0, null);
			itemCounts.put("saved", resp.getResults().getNumFound());

			resp = services.findMyPendingItems(user.getUsername(), 0, 0, null);
			itemCounts.put("pending", resp.getResults().getNumFound());

			resp = services.findMyItems(user.getUsername(), 0, 0, null);
			itemCounts.put("item", resp.getResults().getNumFound());

			// resp = services.findMyFavorites(user.getUsername(), 0, 0, null);
			// itemCounts.put("favorite", resp.getResults().getNumFound());

			resp = services.findMyBookmarks(user.getUsername(), 0, 0, null);
			itemCounts.put("favorite", resp.getResults().getNumFound());

			resp = services.findMySubscriptions(user.getUsername(), 0, 0, null);
			itemCounts.put("subscription", resp.getResults().getNumFound());

			resp = services.findMyCollections(user.getUsername(), 0, 0, null);
			itemCounts.put("collection", resp.getResults().getNumFound());

			resp = services.findMyCommunities(user.getUsername(), 0, 0, null);
			itemCounts.put("community", resp.getResults().getNumFound());

			long downloadCount = services.getDownloadCountByUserId(user.getId());
			itemCounts.put("download", downloadCount);

		} catch (Exception e) {
			log.error("Could not process this request!", e);
		}
	}

	@HandlesEvent("getAccountStats")
	public Resolution getAccountStats() {
		try {
			QueryResponse resp = null;
			resp = services.getItemPidsByOwnerId(user.getUsername());
			SolrDocumentList docs = resp.getResults();
			if (docs.size() > 0) {
				StringBuilder pids = new StringBuilder();
				for (SolrDocument doc : docs) {
					pids.append("\"").append(doc.getFieldValue("PID")).append("\" ");
				}
				resp = services.getBookmarkUserStats("+bm.pid:(" + pids.toString() + ")");
				bookmarkStats = resp.getResults().getNumFound();
				resp = services.getFavoriteUserStats("+fav.pid:(" + pids.toString() + ")");
				favoriteStats = resp.getResults().getNumFound();
			}
			resp = services.getSubscriptionUserStats(user.getUsername());
			if (resp != null) {
				subscriptionStats = resp.getResults().getNumFound();
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
		}
		return new ForwardResolution(uiPath + "/protected/myAccountStatsAjax.jsp");
	}

	@HandlesEvent("items")
	@Secure(roles = "/item/read")
	public Resolution items() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.dateaccepted desc" : sortBy;
			QueryResponse response = services.findMyItems(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/myItems.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("saveditems")
	@Secure(roles = "/item/create")
	public Resolution savedItems() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.createdDate desc" : sortBy;
			QueryResponse response = services.findMySavedItems(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/mySavedItems.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("communities")
	@Secure(roles = "/community/read")
	public Resolution communities() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.title asc" : sortBy;
			QueryResponse response = services.findMyCommunities(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/myCommunities.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("collections")
	@Secure(roles = "/collection/read")
	public Resolution collections() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.title asc" : sortBy;
			QueryResponse response = services.findMyCollections(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/myCollections.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("favorites")
	public Resolution favorites() {
		try {
			sortBy = sortBy == null ? "fav.title asc" : sortBy;
			rows = rows == 0 ? defaultRows : rows;
			QueryResponse response = services.findMyFavorites(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();

			// query item
			if (numFound > 0) {
				SolrDocumentList rs = response.getResults();
				StringBuffer fq = new StringBuffer("+PID:(");
				for (SolrDocument doc : rs) {
					// log.debug("fav.pid: " + doc.getFieldValue("fav.pid"));
					fq.append("\"").append((String) doc.getFieldValue("fav.pid")).append("\" ");
				}
				fq.append(")");
				response = services.query("solr-standard", "*:*", fq.toString(), 0, rows, "");

				// sort item
				Map<String, SolrDocument> map = new HashMap<String, SolrDocument>();
				for (SolrDocument doc : response.getResults()) {
					map.put((String) doc.getFieldValue("PID"), doc);
				}
				favoriteList = new ArrayList<SolrDocument>();
				for (SolrDocument doc : rs) {
					favoriteList.add(map.get(doc.getFieldValue("fav.pid")));
				}
			}

			return new ForwardResolution(uiPath + "/protected/myFavorites.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("subscriptions")
	public Resolution subscriptions() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sub.title asc" : sortBy;
			QueryResponse response = services.findMySubscriptions(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			subscriptionList = response.getResults();

			return new ForwardResolution(uiPath + "/protected/mySubscriptions.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("bookmarks")
	public Resolution bookmarks() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "bm.title asc" : sortBy;
			QueryResponse response = services.findMyBookmarks(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/myBookmarks.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("pendingitems")
	public Resolution pendingItems() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.datesubmitted desc" : sortBy;
			QueryResponse response = services.findMyPendingItems(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/myPendingItems.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getCommunityIds getter method.
	 * 
	 * @return the communityIds
	 */
	@Override
	public String getCommunityIds() {
		return communityIds;
	}

	/**
	 * The setCommunityIds setter method.
	 * 
	 * @param communityIds the communityIds to set
	 */
	@Override
	public void setCommunityIds(String communityIds) {
		this.communityIds = communityIds;
	}

	/**
	 * The getPictureUrl getter method.
	 * 
	 * @return the pictureUrl
	 */
	@Override
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * The setPictureUrl setter method.
	 * 
	 * @param pictureUrl the pictureUrl to set
	 */
	@Override
	public void setImgUrl(String pictureUrl) {
		this.imgUrl = pictureUrl;
	}

	/**
	 * The getPid getter method.
	 * 
	 * @return the pid
	 */
	@Override
	public String getPid() {
		return pid;
	}

	/**
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	@Override
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The getCommunities getter method.
	 * 
	 * @return the communities
	 */
	@Override
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setCommunities setter method.
	 * 
	 * @param communities the communities to set
	 */
	@Override
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}

	/**
	 * The getCollectionIds getter method.
	 * 
	 * @return the collectionIds
	 */
	@Override
	public String getCollectionIds() {
		return collectionIds;
	}

	/**
	 * The setCollectionIds setter method.
	 * 
	 * @param collectionIds the collectionIds to set
	 */
	@Override
	public void setCollectionIds(String collectionIds) {
		this.collectionIds = collectionIds;
	}

	/**
	 * The getCollections getter method.
	 * 
	 * @return the collections
	 */
	@Override
	public List<Collection> getCollections() {
		return collections;
	}

	/**
	 * The setCollections setter method.
	 * 
	 * @param collections the collections to set
	 */
	@Override
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	/**
	 * The getItemCount getter method.
	 * 
	 * @return the itemCount
	 */
	public long getItemCount() {
		return itemCount;
	}

	/**
	 * The setItemCount setter method.
	 * 
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * The getCollectionCount getter method.
	 * 
	 * @return the collectionCount
	 */
	public long getCollectionCount() {
		return collectionCount;
	}

	/**
	 * The setCollectionCount setter method.
	 * 
	 * @param collectionCount the collectionCount to set
	 */
	public void setCollectionCount(long collectionCount) {
		this.collectionCount = collectionCount;
	}

	/**
	 * The getCommunityCount getter method.
	 * 
	 * @return the communityCount
	 */
	public long getCommunityCount() {
		return communityCount;
	}

	/**
	 * The setCommunityCount setter method.
	 * 
	 * @param communityCount the communityCount to set
	 */
	public void setCommunityCount(long communityCount) {
		this.communityCount = communityCount;
	}

	/**
	 * The getFavoriteCount getter method.
	 * 
	 * @return the favoriteCount
	 */
	public long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * The setFavoriteCount setter method.
	 * 
	 * @param favoriteCount the favoriteCount to set
	 */
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * The getBookmarkCount getter method.
	 * 
	 * @return the bookmarkCount
	 */
	public long getBookmarkCount() {
		return bookmarkCount;
	}

	/**
	 * The setBookmarkCount setter method.
	 * 
	 * @param bookmarkCount the bookmarkCount to set
	 */
	public void setBookmarkCount(long bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}

	/**
	 * The getSubscriptionCount getter method.
	 * 
	 * @return the subscriptionCount
	 */
	public long getSubscriptionCount() {
		return subscriptionCount;
	}

	/**
	 * The setSubscriptionCount setter method.
	 * 
	 * @param subscriptionCount the subscriptionCount to set
	 */
	public void setSubscriptionCount(long subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}

	/**
	 * The getSubscriptionList getter method.
	 * 
	 * @return the subscriptionList
	 */
	public List<SolrDocument> getSubscriptionList() {
		return subscriptionList;
	}

	/**
	 * The setSubscriptionList setter method.
	 * 
	 * @param subscriptionList the subscriptionList to set
	 */
	public void setSubscriptionList(List<SolrDocument> subscriptionList) {
		this.subscriptionList = subscriptionList;
	}

	/**
	 * The getFavoriteList getter method.
	 * 
	 * @return the favoriteList
	 */
	public List<SolrDocument> getFavoriteList() {
		return favoriteList;
	}

	/**
	 * The setFavoriteList setter method.
	 * 
	 * @param favoriteList the favoriteList to set
	 */
	public void setFavoriteList(List<SolrDocument> favoriteList) {
		this.favoriteList = favoriteList;
	}

	/**
	 * The getBookmarkStats getter method.
	 * 
	 * @return the bookmarkStats
	 */
	public long getBookmarkStats() {
		return bookmarkStats;
	}

	/**
	 * The setBookmarkStats setter method.
	 * 
	 * @param bookmarkStats the bookmarkStats to set
	 */
	public void setBookmarkStats(long bookmarkStats) {
		this.bookmarkStats = bookmarkStats;
	}

	/**
	 * The getFavoriteStats getter method.
	 * 
	 * @return the favoriteStats
	 */
	public long getFavoriteStats() {
		return favoriteStats;
	}

	/**
	 * The setFavoriteStats setter method.
	 * 
	 * @param favoriteStats the favoriteStats to set
	 */
	public void setFavoriteStats(long favoriteStats) {
		this.favoriteStats = favoriteStats;
	}

	/**
	 * The getSubscriptionStats getter method.
	 * 
	 * @return the subscriptionStats
	 */
	public long getSubscriptionStats() {
		return subscriptionStats;
	}

	/**
	 * The setSubscriptionStats setter method.
	 * 
	 * @param subscriptionStats the subscriptionStats to set
	 */
	public void setSubscriptionStats(long subscriptionStats) {
		this.subscriptionStats = subscriptionStats;
	}

	/**
	 * The getPendingItemCount getter method.
	 * 
	 * @return the pendingItemCount
	 */
	public long getPendingItemCount() {
		return pendingItemCount;
	}

	/**
	 * The setPendingItemCount setter method.
	 * 
	 * @param pendingItemCount the pendingItemCount to set
	 */
	public void setPendingItemCount(long pendingItemCount) {
		this.pendingItemCount = pendingItemCount;
	}

	/**
	 * The getDownloadCount getter method.
	 * 
	 * @return the downloadCount
	 */
	public long getDownloadCount() {
		return downloadCount;
	}

	/**
	 * The setDownloadCount setter method.
	 * 
	 * @param downloadCount the downloadCount to set
	 */
	public void setDownloadCount(long downloadCount) {
		this.downloadCount = downloadCount;
	}

	/**
	 * The getItemCounts getter method.
	 * 
	 * @return the itemCounts
	 */
	public Map<String, Long> getItemCounts() {
		return itemCounts;
	}

}
