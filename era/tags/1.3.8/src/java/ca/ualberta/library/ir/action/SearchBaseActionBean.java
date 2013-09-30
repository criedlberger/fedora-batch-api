/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SearchBaseActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.THUMBNAIL;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The SearchBaseActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public abstract class SearchBaseActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(SearchBaseActionBean.class);

	protected long numFound;
	protected Float maxScore;
	protected int start;
	protected String sortBy;
	protected int rows;
	protected int resultRows;
	protected SolrDocumentList results;
	protected int qTime;
	protected long elapsedTime;

	protected String imgUrl;
	protected String pid;
	protected String collectionIds;
	protected String communityIds;
	protected List<Community> communities;
	protected List<Collection> collections;

	/**
	 * The SearchBaseActionBean class constructor.
	 */
	public SearchBaseActionBean() {
		super();
	}

	public Resolution getAllCommunities() {
		try {
			communities = services.getAllCommunities();
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	public Resolution getAllCollections() {
		try {
			collections = services.getAllCollections();
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	public Resolution getAllContentModels() {
		try {
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	public Resolution getMemberOfCommunities() {
		try {
			communities = new ArrayList<Community>();
			String[] ids = commaPattern.split(communityIds);
			for (String id : ids) {
				QueryResponse response = services.findObjectByPid(id.trim());
				SolrDocument result = response.getResults().get(0);
				Community com = new Community();
				com.setId(id.trim());
				com.setTitle((String) result.getFieldValue("fo.label"));
				communities.add(com);
			}
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	public Resolution getMemberOfCollections() {
		try {
			// log.debug("pid: " + pid);
			communities = services.getMemberOfCommunities(pid);
			collections = services.getMemberOfCollections(pid);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		}
	}

	public Resolution getImageUrl() {
		try {
			imgUrl = ApplicationProperties.getString("fedora.rest.service") + "/get/" + pid + "/"
				+ THUMBNAIL.toString();
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this step!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getNumFound getter method.
	 * 
	 * @return the numFound
	 */
	public long getNumFound() {
		return numFound;
	}

	/**
	 * The setNumFound setter method.
	 * 
	 * @param numFound the numFound to set
	 */
	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	/**
	 * The getStart getter method.
	 * 
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * The setStart setter method.
	 * 
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * The getSortBy getter method.
	 * 
	 * @return the sortBy
	 */
	public String getSortBy() {
		return sortBy;
	}

	/**
	 * The setSortBy setter method.
	 * 
	 * @param sortBy the sortBy to set
	 */
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * The getRows getter method.
	 * 
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * The setRows setter method.
	 * 
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * The getResultRows getter method.
	 * 
	 * @return the resultRows
	 */
	public int getResultRows() {
		return resultRows;
	}

	/**
	 * The setResultRows setter method.
	 * 
	 * @param resultRows the resultRows to set
	 */
	public void setResultRows(int resultRows) {
		this.resultRows = resultRows;
	}

	/**
	 * The getResults getter method.
	 * 
	 * @return the results
	 */
	public SolrDocumentList getResults() {
		return results;
	}

	/**
	 * The setResults setter method.
	 * 
	 * @param results the results to set
	 */
	public void setResults(SolrDocumentList results) {
		this.results = results;
	}

	/**
	 * The getQTime getter method.
	 * 
	 * @return the qTime
	 */
	public int getQTime() {
		return qTime;
	}

	/**
	 * The setQTime setter method.
	 * 
	 * @param time the qTime to set
	 */
	public void setQTime(int time) {
		qTime = time;
	}

	/**
	 * The getElapsedTime getter method.
	 * 
	 * @return the elapsedTime
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * The setElapsedTime setter method.
	 * 
	 * @param elapsedTime the elapsedTime to set
	 */
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	/**
	 * The getDefaultRows getter method.
	 * 
	 * @return the defaultRows
	 */
	public int getDefaultRows() {
		return defaultRows;
	}

	/**
	 * The getNumPages getter method.
	 * 
	 * @return the numPages
	 */
	public int getNumPages() {
		return numPages;
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
	 * The getCollectionIds getter method.
	 * 
	 * @return the collectionIds
	 */
	public String getCollectionIds() {
		return collectionIds;
	}

	/**
	 * The setCollectionIds setter method.
	 * 
	 * @param collectionIds the collectionIds to set
	 */
	public void setCollectionIds(String collectionIds) {
		this.collectionIds = collectionIds;
	}

	/**
	 * The getCommunityIds getter method.
	 * 
	 * @return the communityIds
	 */
	public String getCommunityIds() {
		return communityIds;
	}

	/**
	 * The setCommunityIds setter method.
	 * 
	 * @param communityIds the communityIds to set
	 */
	public void setCommunityIds(String communityIds) {
		this.communityIds = communityIds;
	}

	/**
	 * The getCommunities getter method.
	 * 
	 * @return the communities
	 */
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setCommunities setter method.
	 * 
	 * @param communities the communities to set
	 */
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}

	/**
	 * The getCollections getter method.
	 * 
	 * @return the collections
	 */
	public List<Collection> getCollections() {
		return collections;
	}

	/**
	 * The setCollections setter method.
	 * 
	 * @param collections the collections to set
	 */
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	/**
	 * The getNarrowItemCount getter method.
	 * 
	 * @return the narrowItemCount
	 */
	public int getNarrowItemCount() {
		return narrowItemCount;
	}

	/**
	 * The getBrowseItemCount getter method.
	 * 
	 * @return the browseItemCount
	 */
	public int getBrowseItemCount() {
		return browseItemCount;
	}

	/**
	 * The getMaxScore getter method.
	 * 
	 * @return the maxScore
	 */
	public Float getMaxScore() {
		return maxScore;
	}

	/**
	 * The setMaxScore setter method.
	 * 
	 * @param maxScore the maxScore to set
	 */
	public void setMaxScore(Float maxScore) {
		this.maxScore = maxScore;
	}

	/**
	 * The getMoreLikeThisItemCount getter method.
	 * 
	 * @return the moreLikeThisItemCount
	 */
	public int getMoreLikeThisItemCount() {
		return moreLikeThisItemCount;
	}

	/**
	 * The getMoreNarrowItemCount getter method.
	 * 
	 * @return the moreNarrowItemCount
	 */
	public int getMoreNarrowItemCount() {
		return moreNarrowItemCount;
	}

	/**
	 * The getMoreBrowseItemCount getter method.
	 * 
	 * @return the moreBrowseItemCount
	 */
	public int getMoreBrowseItemCount() {
		return moreBrowseItemCount;
	}

}
