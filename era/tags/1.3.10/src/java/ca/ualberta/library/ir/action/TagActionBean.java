/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: TagActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.List;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;

/**
 * The TagActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/tag/{$event}/{tag}/{start}")
public class TagActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(TagActionBean.class);

	private String query;
	private String filters;
	private String tag;
	private String pid;
	private long tagCount;
	private FacetField tags;

	/**
	 * The TagActionBean class constructor.
	 */
	public TagActionBean() {
		super();
	}

	@HandlesEvent("get")
	public Resolution get() {
		try {
			query = "ir.type:bookmark AND bm.tags:\"" + tag + "\"";
			// log.debug("query: " + query);
			context.setTagQuery(query);
			context.setTag(tag);
			search();
			return new ForwardResolution(uiPath + "/public/tagBookmarks.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/tagBookmarks.jsp");
		}
	}

	@HandlesEvent("getRelatedTags")
	public Resolution getRelatedTags() {
		try {
			QueryResponse response = services.getRelatedTags(tag, true, relatedTagsLimit);
			tags = response.getFacetField("tags");
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		}
	}

	public void search() throws SolrServerException {
		rows = rows == 0 ? defaultRows : rows;
		QueryResponse response = services.query("solr-standard", query, filters, start, rows, sortBy);
		results = response.getResults();
		numFound = results.getNumFound();
		resultRows = results.size();
		qTime = response.getQTime();
		elapsedTime = response.getElapsedTime();
	}

	@HandlesEvent("page")
	public Resolution page() {
		try {
			query = context.getTagQuery();
			search();
			return new ForwardResolution(uiPath + "/public/tagBookmarks.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/tagBookmarks.jsp");
		}
	}

	@HandlesEvent("getTagCountByPid")
	public Resolution getTagCountByPid() {
		try {
			QueryResponse resp = services.getTagCountByPid(pid);
			FacetField fld = resp.getFacetField("bm.pid");
			List<Count> count = fld.getValues();
			if (count != null) {
				tagCount = count.get(0).getCount();
			}
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		}
	}

	@HandlesEvent("getTagsByPid")
	public Resolution getTagsByPid() {
		try {
			QueryResponse resp = services.getTagsByPid(pid, true, bookmarkTagsLimit);
			tags = resp.getFacetField("tags");
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/tagBookmarksAjax.jsp");
		}
	}

	/**
	 * The getTag getter method.
	 * 
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * The setTag setter method.
	 * 
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
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
	 * The getTagCount getter method.
	 * 
	 * @return the tagCount
	 */
	public long getTagCount() {
		return tagCount;
	}

	/**
	 * The setTagCount setter method.
	 * 
	 * @param tagCount the tagCount to set
	 */
	public void setTagCount(long tagCount) {
		this.tagCount = tagCount;
	}

	/**
	 * The getTags getter method.
	 * 
	 * @return the tags
	 */
	public FacetField getTags() {
		return tags;
	}

	/**
	 * The setTags setter method.
	 * 
	 * @param tags the tags to set
	 */
	public void setTags(FacetField tags) {
		this.tags = tags;
	}
}
