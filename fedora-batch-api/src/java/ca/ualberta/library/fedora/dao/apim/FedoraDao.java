/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: RepositoryFedoraDao.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.dao.apim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.fedora.dao.BaseDao;

/**
 * The FedoraAPIFedoraDao class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public abstract class FedoraDao implements BaseDao {
	private static final Log log = LogFactory.getLog(FedoraDao.class);

	protected String protocol;
	protected String host;
	protected int port;
	protected String username;
	protected String password;
	protected String serviceAPIMPath;
	protected String serviceAPIAPath;
	protected String restServicePath;

	/**
	 * The FedoraAPIFedoraDao class constructor.
	 */
	public FedoraDao() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.BaseDao#init()
	 */
	public void init() {
		log.debug("Initializing bean...");
	}

	public String getRestServiceUrl() {
		return protocol + "://" + host + ":" + port + "/" + restServicePath;
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.BaseDao#destroy()
	 */
	public void destroy() {
		log.debug("destroying bean...");
	}

	/**
	 * The getProtocol getter method.
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * The setProtocol setter method.
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * The getHost getter method.
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * The setHost setter method.
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * The getPort getter method.
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * The setPort setter method.
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * The getUsername getter method.
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The setUsername setter method.
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * The getPassword getter method.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * The setPassword setter method.
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * The getServicePath getter method.
	 * @return the servicePath
	 */
	public String getAPIMServicePath() {
		return serviceAPIMPath;
	}

	/**
	 * The setServicePath setter method.
	 * @param servicePath the servicePath to set
	 */
	public void setAPIMServicePath(String servicePath) {
		this.serviceAPIMPath = servicePath;
	}

	/**
	 * The getServicePath getter method.
	 * @return the servicePath
	 */
	public String getAPIAServicePath() {
		return serviceAPIAPath;
	}

	/**
	 * The setServicePath setter method.
	 * @param servicePath the servicePath to set
	 */
	public void setAPIAServicePath(String servicePath) {
		this.serviceAPIAPath = servicePath;
	}

	/**
	 * The getRestServicePath getter method.
	 * @return the restServicePath
	 */
	public String getRestServicePath() {
		return restServicePath;
	}

	/**
	 * The setRestServicePath setter method.
	 * @param restServicePath the restServicePath to set
	 */
	public void setRestServicePath(String restServicePath) {
		this.restServicePath = restServicePath;
	}

}
