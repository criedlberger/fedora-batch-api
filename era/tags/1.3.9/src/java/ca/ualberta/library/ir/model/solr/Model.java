/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Model.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.solr;

import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The Model class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public abstract class Model {
	private static final Log log = LogFactory.getLog(Model.class);

	protected static String solrUrl = MessageFormat.format("{0}://{1}:{2}{3}",
		ApplicationProperties.getString("solr.protocol"), ApplicationProperties.getString("solr.host"),
		ApplicationProperties.getString("solr.port"), ApplicationProperties.getString("solr.service"));

	/**
	 * The initSolrServer method.
	 * 
	 * @return
	 */
	protected SolrServer initSolrServer() {
		SolrServer server = null;
		try {
			server = new CommonsHttpSolrServer(solrUrl);
			((CommonsHttpSolrServer) server).setConnectionTimeout(86400000);
		} catch (MalformedURLException e) {
			log.error("Could not connect to Solr Server!", e);
		}
		return server;
	}

	/**
	 * The Model class constructor.
	 */
	public Model() {
		super();
	}

}
