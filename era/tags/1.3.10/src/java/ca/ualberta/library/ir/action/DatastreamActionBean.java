/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: DatastreamActionBean.java 5560 2012-09-17 19:58:11Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.DS;
import static ca.ualberta.library.ir.enums.DatastreamID.LICENSE;
import static ca.ualberta.library.ir.enums.DatastreamID.THUMBNAIL;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.exception.FedoraAPIException;

/**
 * The DatastreamActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5560 $ $Date: 2012-09-17 13:58:11 -0600 (Mon, 17 Sep 2012) $
 */
@UrlBinding("/public/datastream/{$event}/{pid}/{dsId}/{filename}")
public class DatastreamActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(DatastreamActionBean.class);

	@Validate(required = true)
	private String pid;

	private String dsId;

	private String filename;

	private long count;

	private int userId;

	/**
	 * The DatastreamActionBean class constructor.
	 */
	public DatastreamActionBean() {
		super();
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return pid;
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getDatastreamID()
	 */
	@Override
	public String getDatastreamID() {
		return dsId;
	}

	@HandlesEvent("get")
	@Secure(roles = "/item/read,/object/dark,/object/ccid,/object/embargoed")
	public Resolution get() {
		try {
			// log.debug("download starting...");

			// create httpClient
			HttpClient http = new HttpClient();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(fedoraUsername, fedoraPassword);
			AuthScope authScope = new AuthScope(fedoraHost, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
			http.getState().setCredentials(authScope, credentials);
			http.getParams().setAuthenticationPreemptive(true);

			// craete get method
			String url = fedoraServerUrl + fedoraRestServiceUrl + "/get/" + pid + "/" + dsId;
			GetMethod get = new GetMethod(url);
			get.setDoAuthentication(true);
			get.setFollowRedirects(true);

			InputStream is = null;
			try {
				// log.debug("getting datastream: " + pid + "/" + dsId);
				int status = http.executeMethod(get);
				if (status != HttpServletResponse.SC_OK) {
					throw new FedoraAPIException("Get Datastream Content Error Status: " + status);
				}
				String contentType = get.getResponseHeader("Content-Type").getValue();
				// log.debug("Content-Type: " + contentType + " contentLength: " + get.getResponseContentLength());
				is = get.getResponseBodyAsStream();

				// log.debug("got datastream...");
				if (dsId.startsWith(DS.toString()) || dsId.equals(LICENSE.toString())) {

					// get datastream label as a filename
					fedora.server.types.gen.Datastream dstm = services.getDatastream(pid, dsId);
					try {
						filename = URLEncoder.encode(
							dstm.getLabel()
								+ (StringUtils.substringAfterLast(dstm.getLabel(), ".").length() == 3
									|| StringUtils.substringAfterLast(dstm.getLabel(), ".").length() == 4 ? "" : "."
									+ applicationResources.getString(dstm.getMIMEType() + ".ext").split(",")[0]),
							"UTF-8");

					} catch (Exception e) {
						log.warn("Could not encode filename!", e);
						filename = URLEncoder.encode(dstm.getLabel(), "UTF-8");
					}
				}
				if (dsId.startsWith(DS.toString())) {
					// save download statisitic
					new SaveDownloadThread(context, pid, dsId).start();
				}
				// log.debug("passing datastream through stripes...");
				return new StreamingResolution(contentType, is).setFilename(filename == null ? dsId : filename);
			} catch (Exception e) {
				// log.warn("Could not get datastream content! (" + dsId + ")");
				if (dsId.equals(THUMBNAIL.toString())) {
					FileInputStream space = new FileInputStream(context.getServletContext().getRealPath(
						"/images/era-thumbnail-lite.png"));
					return new StreamingResolution(null, space);
				} else {
					throw new FedoraAPIException("Could not get datastream content!", e);
				}
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getDownloadCountByPid")
	public Resolution getDownloadCountByPid() {
		try {
			count = services.getDownloadCountByPid(pid);
			return new ForwardResolution(uiPath + "/public/downloadAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getDownloadCountByDsId")
	public Resolution getDownloadCountByDsId() {
		try {
			count = services.getDownloadCountByDsId(pid, dsId);
			return new ForwardResolution(uiPath + "/public/downloadAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getDownloadCountByUserId")
	@DontValidate
	public Resolution getDownloadCountByUserId() {
		try {
			count = services.getDownloadCountByUserId(userId);
			return new ForwardResolution(uiPath + "/public/downloadAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getDownloadCountByCollection")
	public Resolution getDownloadCountByCollection() {
		try {
			count = services.getDownloadCountByCollection(pid);
			return new ForwardResolution(uiPath + "/public/downloadAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getDownloadCountByCommunity")
	public Resolution getDownloadCountByCommunity() {
		try {
			count = services.getDownloadCountByCollection(pid);
			return new ForwardResolution(uiPath + "/public/downloadAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
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
	 * The getDsId getter method.
	 * 
	 * @return the dsId
	 */
	public String getDsId() {
		return dsId;
	}

	/**
	 * The setDsId setter method.
	 * 
	 * @param dsId the dsId to set
	 */
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	/**
	 * The getCount getter method.
	 * 
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * The setCount setter method.
	 * 
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * The getUserId getter method.
	 * 
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * The setUserId setter method.
	 * 
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
