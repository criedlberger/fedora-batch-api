/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: BrowseActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * The SearchActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/browse/{$event}/{browseField}/{prefix}")
public class BrowseActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(BrowseActionBean.class);

	private List<FacetField> facetFields;

	private String browseField;

	private int offset;

	private List<FacetField> alphaFields;

	private String prefix;

	private Map<Character, Boolean> initialMap;

	private List<Character> initialList;

	private HashMap<String, Integer> fieldMap;

	private HashMap<String, Long> alphaFieldMap;

	private long itemCount;

	private long downloadCount;

	/**
	 * The SearchActionBean class constructor.
	 */
	public BrowseActionBean() {
		super();
		// log.trace("creating action bean...");
	}

	@DefaultHandler
	@HandlesEvent("browse")
	public Resolution browse() {
		try {
			// log.debug("start browsing...");

			// browse by alphabet
			QueryResponse alphaResponse = services.browseAlphabet();
			alphaFields = alphaResponse.getFacetFields();
			alphaFieldMap = new HashMap<String, Long>();
			String name = null;
			long count = 0;
			for (FacetField fld : alphaFields) {
				if (!fld.getName().equals(name)) {
					if (name != null) {
						alphaFieldMap.put(name, count);
						count = 0;
					}
					name = fld.getName();
				}
				for (Count cnt : fld.getValues()) {
					count = count + cnt.getCount();
				}
			}
			alphaFieldMap.put(name, count);

			// browse by title
			q = "";
			fq = "";
			sort = "sort.title asc";
			QueryResponse response;

			// get field value count
			response = services.getNarrowSearch(q, fq, false, -1);
			facetFields = response.getFacetFields();
			fieldMap = new HashMap<String, Integer>();
			for (FacetField fld : facetFields) {
				fieldMap.put(fld.getName(), fld.getValueCount());
			}

			// get limited browse result
			response = services.getNarrowSearch(q, fq, false, browseItemCount + 1);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();

			// total and download
			// downloadCount = services.getDownloadCount();
			itemCount = services.getItemCount().getResults().getNumFound();
			// log.debug("end browsing...");
			return new ForwardResolution(uiPath + "/public/browse.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/browse.jsp");
		}
	}

	@HandlesEvent("stats")
	@DontValidate
	public Resolution stats() {
		try {
			// browse();
			itemCount = services.getItemCount().getResults().getNumFound();
			return new ForwardResolution(uiPath + "/public/browseAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("more")
	public Resolution more() {
		try {
			// log.debug("start more browsing...");
			QueryResponse response = services.moreNarrowSearch(q, fq, browseField, false, moreBrowseItemCount + 1,
				offset);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();
			// log.debug("end more browsing...");
			return new ForwardResolution(uiPath + "/public/browseAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/browseAjax.jsp");
		}
	}

	@HandlesEvent("initial")
	public Resolution initial() {
		try {
			// log.debug("start browse initial...");

			// browse by alphabet
			QueryResponse alphaResponse = services.browseAlphabet();
			alphaFields = alphaResponse.getFacetFields();

			// browse initail
			QueryResponse response = services.browseInitial(browseField, prefix, browseItemCount + 1, offset);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();
			// log.debug("end browse initial...");
		} catch (Exception e) {
			log.error("Could not search!", e);
		}
		return new ForwardResolution(uiPath + "/public/browseInitial.jsp");
	}

	@HandlesEvent("moreInitial")
	public Resolution moreInitial() {
		try {
			// log.debug("start browse initial...");
			QueryResponse response = services.browseInitial(browseField, prefix, moreBrowseItemCount + 1, offset);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();
			// log.debug("end browse initial...");
		} catch (Exception e) {
			log.error("Could not search!", e);
		}
		return new ForwardResolution(uiPath + "/public/browseAjax.jsp");
	}

	@HandlesEvent("community")
	public Resolution community() {
		try {
			QueryResponse response = services.findCommunities(0, Integer.MAX_VALUE, "sort.title asc");
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();

			// browse initial
			QueryResponse iresp = services.browseCommunityByAlphabet();
			alphaFields = iresp.getFacetFields();
			initialList = new ArrayList<Character>();
			for (char ch = 'A'; ch <= 'Z'; ++ch) {
				initialList.add(ch);
			}
			initialMap = new HashMap<Character, Boolean>();
			char ch = ' ';
			List<Count> initials = alphaFields.get(0).getValues();
			if (initials != null) {
				for (Count initial : initials) {
					char init = initial.getName().toUpperCase().charAt(0);
					if (ch != init) {
						initialMap.put(init, true);
						ch = init;
					}
				}
			}

			return new ForwardResolution(uiPath + "/public/browseCommunity.jsp");
		} catch (Exception e) {
			log.error("Could not view community!", e);
			return forwardExceptionError("Could not view community!", e);
		}
	}

	/**
	 * The getFacetFields getter method.
	 * 
	 * @return the facetFields
	 */
	public List<FacetField> getFacetFields() {
		return facetFields;
	}

	/**
	 * The setFacetFields setter method.
	 * 
	 * @param facetFields the facetFields to set
	 */
	public void setFacetFields(List<FacetField> facetFields) {
		this.facetFields = facetFields;
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
	 * The getBrowseField getter method.
	 * 
	 * @return the browseField
	 */
	public String getBrowseField() {
		return browseField;
	}

	/**
	 * The setBrowseField setter method.
	 * 
	 * @param browseField the browseField to set
	 */
	public void setBrowseField(String browseField) {
		this.browseField = browseField;
	}

	/**
	 * The getOffset getter method.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * The setOffset setter method.
	 * 
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * The getAlphaFields getter method.
	 * 
	 * @return the alphaFields
	 */
	public List<FacetField> getAlphaFields() {
		return alphaFields;
	}

	/**
	 * The getPrefix getter method.
	 * 
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * The setPrefix setter method.
	 * 
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * The getInitialMap getter method.
	 * 
	 * @return the initialMap
	 */
	public Map<Character, Boolean> getInitialMap() {
		return initialMap;
	}

	/**
	 * The getInitialList getter method.
	 * 
	 * @return the initialList
	 */
	public List<Character> getInitialList() {
		return initialList;
	}

	/**
	 * The getFieldMap getter method.
	 * 
	 * @return the fieldMap
	 */
	public HashMap<String, Integer> getFieldMap() {
		return fieldMap;
	}

	/**
	 * The getAlphaFieldMap getter method.
	 * 
	 * @return the alphaFieldMap
	 */
	public HashMap<String, Long> getAlphaFieldMap() {
		return alphaFieldMap;
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
	 * The getDownloadCount getter method.
	 * 
	 * @return the downloadCount
	 */
	public long getDownloadCount() {
		return downloadCount;
	}

}
