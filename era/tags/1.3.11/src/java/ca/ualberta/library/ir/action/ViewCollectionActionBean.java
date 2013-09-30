/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ViewCollectionActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;

/**
 * The ViewCommunityActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/view/collection/{collection.id}/{$event}")
public class ViewCollectionActionBean extends SearchActionBean {
	private static final Log log = LogFactory.getLog(ViewCollectionActionBean.class);

	private Collection collection;
	private List<Community> communities;

	/**
	 * The ViewCommunityActionBean class constructor.
	 */
	public ViewCollectionActionBean() {
		super();
	}

	@Override
	public String getObjectPID() {
		return collection.getId();
	}

	@HandlesEvent("foxml")
	public Resolution foxml() {
		return foxml(collection.getId());
	}

	@HandlesEvent("solrxml")
	public Resolution solrxml() {
		return solrxml(collection.getId());
	}

	@HandlesEvent("view")
	@DefaultHandler
	@DontValidate
	@Secure(roles = "/collection/read")
	public Resolution view() {
		try {
			String pid = collection.getId();
			collection = services.getCollection(pid);
			communities = services.getMemberOfCommunities(collection.getId());

			// find items in this collection
			fq = "facet.collection:\"" + collection.getTitle() + "\"";
			getItems();

			return new ForwardResolution(uiPath + "/public/collection.jsp");

		} catch (Exception e) {
			log.error("Could not view collection!", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("collectionNotFound"));
			return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
		}
	}

	private void getItems() throws SolrServerException {
		if (StringUtils.trimToNull(sort) == null) {
			try {
				// use era.sort field from solr index
				sort = services.getCollection(collection.getId()).getSort();
			} catch (Exception e) {
				sort = "sort.title asc";
			}
		}
		rows = rows == 0 ? defaultRows : rows;
		QueryResponse response = services.search(q, fq, start, rows, sort);
		results = response.getResults();
		numFound = results.getNumFound();
		maxScore = results.getMaxScore();
		resultRows = results.size();
		qTime = response.getQTime();
		elapsedTime = response.getElapsedTime();
	}

	/**
	 * The getCollection getter method.
	 * 
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * The setCollection setter method.
	 * 
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
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
	 * The getMemberOfCommunities getter method.
	 * 
	 * @return the memberOfCommunities
	 */

}