/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: DepositAjaxBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.model.solr.Collection;

/**
 * The DepositAjaxBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class DepositAjaxBean extends BaseAjaxBean {
	private static final Log log = LogFactory.getLog(DepositAjaxBean.class);

	/**
	 * The DepositAjaxBean class constructor.
	 */
	public DepositAjaxBean() {
		super();
	}

	public void getCollectionList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String communityId = params == null ? null : params[0];
			List<Collection> collections = null;
			if (communityId == null) {
				collections = services.getAllCollections();
			} else {
				collections = new ArrayList<Collection>();
				QueryResponse resp = services.findMemberObjects(communityId,
					FedoraRelationship.IS_MEMBER_OF.getFieldName());
				SolrDocumentList results = resp.getResults();
				for (SolrDocument doc : results) {
					String cModel = (String) doc.getFieldValue("fo.contentModel");
					if (cModel.equals(Collection.CONTENT_MODEL)) {
						Collection col = new Collection();
						col.setId((String) doc.getFieldValue("PID"));
						col.setTitle((String) doc.getFieldValue("fo.label"));
						collections.add(col);
					}
				}
			}
			request.setAttribute("collections", collections);
			request.getRequestDispatcher("/jsp/protected/depositAjax.jsp").forward(request, response);
		} catch (Exception e) {
			log.error("Could not find collection object!", e);
			throw new ServletException(e);
		}
	}

	public void findOwnerByName(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String name = request.getParameter("name");
			List<User> owners = services.getUsersByName(name);
			// log.debug("finding name: " + name);
			// log.debug("owners: " + owners + " size: " + owners.size());
			// log.debug("event: " + request.getAttribute("event"));
			request.setAttribute("owners", owners);
			request.getRequestDispatcher("/jsp/protected/depositAjax.jsp").forward(request, response);
		} catch (Exception e) {
			log.error("Could not find collection object!", e);
			throw new ServletException(e);
		}
	}
}
