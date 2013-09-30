/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SearchActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

import ca.ualberta.library.ir.domain.Bookmark;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;

/**
 * The SearchActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/search/{$event}/{narrowField}/{start}/{sort}")
public class SearchActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(SearchActionBean.class);

	private String terms;

	private String narrowField;

	private List<String> advancedTerms;

	private List<String> operators;

	private List<String> fields;

	private List<String> communityFilter;

	private List<String> collectionFilter;

	private List<String> contentModelFilter;

	private String filters;

	private List<FacetField> facetFields;

	private int index;

	private boolean favorite;

	private Bookmark bookmark;

	private Map<String, List<Collection>> mltCollections;

	private Map<String, List<Community>> mltCommunities;

	private int offset;

	private List<Count> types;

	private State state;

	/**
	 * The SearchActionBean class constructor.
	 */
	public SearchActionBean() {
		super();
		rows = rows == 0 ? defaultRows : rows;
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		q = StringUtils.trimToEmpty(q);
		sort = StringUtils.trimToNull(sort) == null ? "sort.title asc" : StringUtils.trimToEmpty(sort);
	}

	@DefaultHandler
	@HandlesEvent("search")
	public Resolution search() {
		try {
			QueryResponse response = services.search(q, fq, start, rows, sort);
			results = response.getResults();
			numFound = results.getNumFound();
			maxScore = results.getMaxScore();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/public/search.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/search.jsp");
		}
	}

	@HandlesEvent("simple")
	public Resolution simple() {
		if (StringUtils.trimToNull(q) == null) {
			context.getValidationErrors().addGlobalError(new LocalizableError("keywordError"));
		}
		if (q.startsWith("*")) {
			context.getValidationErrors().addGlobalError(new LocalizableError("wildcardError"));
		}
		query = q;
		if (!context.getValidationErrors().isEmpty()) {
			return new ForwardResolution(uiPath + "/public/search.jsp");
		}
		return search();
	}

	@HandlesEvent("getNarrowSearch")
	public Resolution getNarrowSearch() {
		try {
			// log.debug("start narrow searching...");
			QueryResponse response = services.getNarrowSearch(q, fq, true, narrowItemCount + 1);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();
			// log.debug("end narrow searching...");
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		}
	}

	@HandlesEvent("narrow")
	public Resolution narrow() {
		// update filter query
		// log.debug("fq: " + fq);
		fq = (StringUtils.trimToNull(fq) == null ? "" : fq + " ") + narrowField;
		return search();
	}

	@HandlesEvent("moreNarrowSearch")
	public Resolution moreNarrowSearch() {
		try {
			// log.debug("start more narrow searching...");
			QueryResponse response = services.moreNarrowSearch(q, fq, narrowField, true, moreNarrowItemCount + 1,
				offset);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			facetFields = response.getFacetFields();
			// log.debug("end more narrow searching...");
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		}
	}

	@HandlesEvent("advanced")
	public Resolution advanced() {
		try {
			if (advancedTerms == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("keywordError"));
			} else {
				q = StringUtils.trimToEmpty(advancedTerms.get(0));
				if (StringUtils.trimToNull(q) == null) {
					context.getValidationErrors().addGlobalError(new LocalizableError("keywordError"));
				}
				for (String term : advancedTerms) {
					if (StringUtils.trimToEmpty(term).startsWith("*")) {
						context.getValidationErrors().addGlobalError(new LocalizableError("wildcardError"));
					}
				}
			}
			query = q;
			if (!context.getValidationErrors().isEmpty()) {
				return new ForwardResolution(uiPath + "/public/search.jsp");
			}

			// build query
			StringBuffer qs = new StringBuffer();
			if (advancedTerms != null) {
				int i = 0;
				for (String t : advancedTerms) {
					if (StringUtils.trimToNull(t) != null) {
						String op = i == 0 ? "" : " " + operators.get(i) + " ";
						String fl = fields.get(i);
						qs.append(op);
						if (fl.equals("dc.all")) {
							qs.append(t);
						} else {
							qs.append(fl).append(":(").append(t).append(")");
						}
					}
					i++;
				}
			}
			if (state != null) {
				qs.append(" AND fo.state:").append(state.getName());
			}
			// log.debug("query: " + qs);
			this.q = qs.toString();

			// build filters
			StringBuffer ft = new StringBuffer();
			boolean first;
			if (contentModelFilter != null && contentModelFilter.size() > 0) {

				// build contentModel filters
				first = true;
				ft.append(" facet.type:\"");
				for (String cm : contentModelFilter) {
					if (first) {
						ft.append(cm);
						first = false;
					} else {
						ft.append("\" OR \"").append(cm);
					}
				}
				ft.append("\"");
			}
			if (collectionFilter != null && collectionFilter.size() > 0) {

				// build collection filters
				first = true;
				ft.append(" facet.collection:\"");
				for (String col : collectionFilter) {
					if (first) {
						ft.append(col);
						first = false;
					} else {
						ft.append("\" OR \"").append(col);
					}
				}
				ft.append("\"");
			}
			if (communityFilter != null && communityFilter.size() > 0) {
				first = true;

				// build community filters
				first = true;
				ft.append(" facet.community:\"");
				for (String com : communityFilter) {
					if (first) {
						ft.append(com);
						first = false;
					} else {
						ft.append("\" OR \"").append(com);
					}
				}
				ft.append("\"");
			}
			// log.debug("filters: " + ft);
			fq = ft.toString().trim();
			sort = "sort.title asc";
			QueryResponse response = services.search(q, fq, start, rows, sort);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/public/search.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/search.jsp");
		}
	}

	@HandlesEvent("getSuggestions")
	@SuppressWarnings({ "rawtypes" })
	public Resolution getSuggestions() {
		try {
			// log.debug("getting suggestion for: " + query);

			QueryResponse response = services.getSuggestions(query, suggestionCount);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();

			// TODO: parse spellcheck results
			NamedList spellcheck = (NamedList) response.getResponse().get("spellcheck");
			NamedList suggestions = (NamedList) spellcheck.get("suggestions");
			for (Iterator iterator = suggestions.iterator(); iterator.hasNext();) {
				// log.debug(iterator.next());
			}
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		}
	}

	@HandlesEvent("getMoreLikeThis")
	public Resolution getMoreLikeThis() {
		try {
			// log.debug("moreLikeThisCount: " + moreLikeThisItemCount);

			mltCollections = new HashMap<String, List<Collection>>();
			mltCommunities = new HashMap<String, List<Community>>();

			QueryResponse response = services.getMoreLikeThis(pid, moreLikeThisItemCount);
			results = response.getResults();
			if (results != null && results.size() > 0) {
				numFound = results.getNumFound();
				resultRows = results.size();
				qTime = response.getQTime();
				elapsedTime = response.getElapsedTime();
			}

			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");

		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/searchAjax.jsp");
		}
	}

	/**
	 * The getTerms getter method.
	 * 
	 * @return the terms
	 */
	public String getTerms() {
		return terms;
	}

	/**
	 * The setTerms setter method.
	 * 
	 * @param terms the terms to set
	 */
	public void setTerms(String terms) {
		this.terms = terms;
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
	 * The getFilters getter method.
	 * 
	 * @return the filters
	 */
	public String getFilters() {
		return filters;
	}

	/**
	 * The setFilters setter method.
	 * 
	 * @param filters the filters to set
	 */
	public void setFilters(String filters) {
		this.filters = filters;
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
	 * The getAdvancedTerms getter method.
	 * 
	 * @return the advancedTerms
	 */
	public List<String> getAdvancedTerms() {
		return advancedTerms;
	}

	/**
	 * The setAdvancedTerms setter method.
	 * 
	 * @param advancedTerms the advancedTerms to set
	 */
	public void setAdvancedTerms(List<String> advancedTerms) {
		this.advancedTerms = advancedTerms;
	}

	/**
	 * The getOperators getter method.
	 * 
	 * @return the operators
	 */
	public List<String> getOperators() {
		return operators;
	}

	/**
	 * The setOperators setter method.
	 * 
	 * @param operators the operators to set
	 */
	public void setOperators(List<String> operators) {
		this.operators = operators;
	}

	/**
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<String> getFields() {
		return fields;
	}

	/**
	 * The setFields setter method.
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	/**
	 * The getCommunityFilter getter method.
	 * 
	 * @return the communityFilter
	 */
	public List<String> getCommunityFilter() {
		return communityFilter;
	}

	/**
	 * The setCommunityFilter setter method.
	 * 
	 * @param communityFilter the communityFilter to set
	 */
	public void setCommunityFilter(List<String> communityFilter) {
		this.communityFilter = communityFilter;
	}

	/**
	 * The getCollectionFilter getter method.
	 * 
	 * @return the collectionFilter
	 */
	public List<String> getCollectionFilter() {
		return collectionFilter;
	}

	/**
	 * The setCollectionFilter setter method.
	 * 
	 * @param collectionFilter the collectionFilter to set
	 */
	public void setCollectionFilter(List<String> collectionFilter) {
		this.collectionFilter = collectionFilter;
	}

	/**
	 * The getContentModelFilter getter method.
	 * 
	 * @return the contentModelFilter
	 */
	public List<String> getContentModelFilter() {
		return contentModelFilter;
	}

	/**
	 * The setContentModelFilter setter method.
	 * 
	 * @param contentModelFilter the contentModelFilter to set
	 */
	public void setContentModelFilter(List<String> contentModelFilter) {
		this.contentModelFilter = contentModelFilter;
	}

	/**
	 * The getNarrowField getter method.
	 * 
	 * @return the narrowField
	 */
	public String getNarrowField() {
		return narrowField;
	}

	/**
	 * The setNarrowField setter method.
	 * 
	 * @param narrowField the narrowField to set
	 */
	public void setNarrowField(String narrowField) {
		this.narrowField = narrowField;
	}

	/**
	 * The getIndex getter method.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * The setIndex setter method.
	 * 
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * The setFavorite setter method.
	 * 
	 * @param favorite the favorite to set
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	/**
	 * The isFavorite getter method.
	 * 
	 * @return the favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * The getBookmark getter method.
	 * 
	 * @return the bookmark
	 */
	public Bookmark getBookmark() {
		return bookmark;
	}

	/**
	 * The setBookmark setter method.
	 * 
	 * @param bookmark the bookmark to set
	 */
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * The getMltCollections getter method.
	 * 
	 * @return the mltCollections
	 */
	public Map<String, List<Collection>> getMltCollections() {
		return mltCollections;
	}

	/**
	 * The setMltCollections setter method.
	 * 
	 * @param mltCollections the mltCollections to set
	 */
	public void setMltCollections(Map<String, List<Collection>> mltCollections) {
		this.mltCollections = mltCollections;
	}

	/**
	 * The getMltCommunities getter method.
	 * 
	 * @return the mltCommunities
	 */
	public Map<String, List<Community>> getMltCommunities() {
		return mltCommunities;
	}

	/**
	 * The setMltCommunities setter method.
	 * 
	 * @param mltCommunities the mltCommunities to set
	 */
	public void setMltCommunities(Map<String, List<Community>> mltCommunities) {
		this.mltCommunities = mltCommunities;
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
	 * The getTypes getter method.
	 * 
	 * @return the types
	 */
	public List<Count> getTypes() {
		return types;
	}

	/**
	 * The setTypes setter method.
	 * 
	 * @param types the types to set
	 */
	public void setTypes(List<Count> types) {
		this.types = types;
	}

	/**
	 * The getState getter method.
	 * 
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * The setState setter method.
	 * 
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
}
