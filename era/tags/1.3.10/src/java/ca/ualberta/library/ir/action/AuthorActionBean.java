/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: AuthorActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import ca.ualberta.library.ir.domain.Author;
import ca.ualberta.library.ir.domain.User;

/**
 * The AuthorActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/view/author/{username}")
public class AuthorActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(AuthorActionBean.class);

	private String username;
	private Author author;
	private SolrDocumentList results;

	/**
	 * The AuthorActionBean class constructor.
	 */
	public AuthorActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("home")
	public Resolution home() {
		try {

			// get author profile
			User user = services.getUser(username);
			author = user.getAuthor();
			if (author == null) {
				context.getMessages().add(new LocalizableMessage("researcher.notFound"));
				return new RedirectResolution("/public/researcher");
			}

			// get items order by subject
			QueryResponse resp = services.findMyItems(user.getUsername(), 0, Integer.MAX_VALUE,
				"sort.subject asc, sort.title asc");
			results = resp.getResults();

			return new ForwardResolution(uiPath + "/public/authorHome.jsp");
		} catch (Exception e) {
			log.error("Could not find author!", e);
			return forwardExceptionError("Could not find authors!", e);
		}
	}

	/**
	 * The getUsername getter method.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The setUsername setter method.
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * The getAuthor getter method.
	 * 
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * The setAuthor setter method.
	 * 
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
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

}
