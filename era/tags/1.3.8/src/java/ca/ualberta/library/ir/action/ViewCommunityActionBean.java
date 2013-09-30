/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ViewCommunityActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import ca.ualberta.library.ir.model.solr.Community;

/**
 * The ViewCommunityActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/view/community/{community.id}/{$event}")
public class ViewCommunityActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(ViewCommunityActionBean.class);

	private Community community;
	private ArrayList<Character> initialList;
	private HashMap<Character, Boolean> initialMap;

	/**
	 * The ViewCommunityActionBean class constructor.
	 */
	public ViewCommunityActionBean() {
		super();
	}

	@Override
	public String getObjectPID() {
		return community.getId();
	}

	@HandlesEvent("foxml")
	public Resolution foxml() {
		return foxml(community.getId());
	}

	@HandlesEvent("solrxml")
	public Resolution solrxml() {
		return solrxml(community.getId());
	}

	@HandlesEvent("view")
	@DefaultHandler
	@Secure(roles = "/community/read")
	public Resolution view() {
		try {
			community = services.getCommunity(community.getId());
			QueryResponse response = services
				.findCollections(community.getId(), 0, Integer.MAX_VALUE, "sort.title asc");
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();

			// browse initial
			QueryResponse iresp = services.browseCollectionByAlphabet(community.getId());
			List<FacetField> alphaFields = iresp.getFacetFields();
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

			return new ForwardResolution(uiPath + "/public/community.jsp");
		} catch (Exception e) {
			log.error("Could not view community!", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("communityNotFound"));
			return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
		}
	}

	/**
	 * The getCommunity getter method.
	 * 
	 * @return the community
	 */
	public Community getCommunity() {
		return community;
	}

	/**
	 * The setCommunity setter method.
	 * 
	 * @param community the community to set
	 */
	public void setCommunity(Community community) {
		this.community = community;
	}

	/**
	 * The getInitialList getter method.
	 * 
	 * @return the initialList
	 */
	public ArrayList<Character> getInitialList() {
		return initialList;
	}

	/**
	 * The getInitialMap getter method.
	 * 
	 * @return the initialMap
	 */
	public HashMap<Character, Boolean> getInitialMap() {
		return initialMap;
	}
}
