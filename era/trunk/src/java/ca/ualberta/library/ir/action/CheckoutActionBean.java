/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: CheckoutActionBean.java 5606 2012-10-10 16:45:09Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.ObjectFields;

import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.exception.FedoraAPIException;
import ca.ualberta.library.ir.utils.ZipUtils;

/**
 * The CheckoutActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
 */
@UrlBinding("/public/checkout/{$event}/{filename}")
public class CheckoutActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(CheckoutActionBean.class);

	private static final SimpleDateFormat timestampFormat;
	private String filename;
	private String url;

	private SolrDocumentList cartItems;
	private SolrDocumentList downloadedItems;
	private SolrDocumentList oversizeItems;
	private SolrDocumentList missingItems;

	static {
		timestampFormat = new SimpleDateFormat(timestampPattern);
	}

	/**
	 * The CheckoutActionBean class constructor.
	 */
	public CheckoutActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("process")
	public Resolution process() {
		try {

			// create temporary directory for downloaded item datastreams
			String zipDir = tempPath
				+ "/"
				+ MessageFormat.format(zipPattern, "checkout", user == null ? "guest" : user.getUsername(),
					timestampFormat.format(new Date()));
			// log.debug("zip dir: " + zipDir);
			File tmpDir = new File(zipDir);
			tmpDir.mkdir();

			// get item datastreams and write to tmpdir
			List<String> osPids = new ArrayList<String>();
			List<String> dlPids = new ArrayList<String>();
			List<String> msPids = new ArrayList<String>();

			List<String> cart = context.getCart();
			if (cart.isEmpty()) {
				return new RedirectResolution("/public/cart");
			}
			int n = 1;
			for (String pid : cart) {
				ObjectFields results = services.findObjectByPid(resultFields, pid);
				Datastream[] dstms = services.getDatastreams(pid);
				int i = 0;

				// get content datastream
				for (Datastream dstm : dstms) {
					// log.debug("dstm.size: " + dstm.getSize());
					if (dstm.getID().startsWith(DatastreamID.DS.toString())) {
						try {
							if (getDownloadDatastreamContent(n++, pid, dstm.getID(), tmpDir, itemPattern, results
								.getTitle().get(0))) {
								dlPids.add(pid);
								// update download statistics
								new SaveDownloadThread(context, pid, dstm.getID()).start();
								i++;
							} else {
								osPids.add(pid);
							}
						} catch (FedoraAPIException e) {
							msPids.add(pid);
						}
					}
				}

				// get license datastream
				if (i > 0) {
					for (Datastream dstm : dstms) {
						if (dstm.getID().equals(DatastreamID.LICENSE.toString())) {
							try {
								getDownloadDatastreamContent(n++, pid, dstm.getID(), tmpDir, itemPattern, results
									.getTitle().get(0));
							} catch (FedoraAPIException e) {
								log.warn("Could not get license datastream!");
							}
						}
					}
				}
			}

			// remove all items from cart
			context.setCart(null);

			// get item details for checkout page
			// cartItems = cart.isEmpty() ? null : services.findObjectsByPids(cart).getResults();
			downloadedItems = dlPids.isEmpty() ? null : services.findObjectsByPids(dlPids).getResults();
			oversizeItems = osPids.isEmpty() ? null : services.findObjectsByPids(osPids).getResults();
			missingItems = msPids.isEmpty() ? null : services.findObjectsByPids(msPids).getResults();

			// compress tmpdir
			String tmpDirPath = tmpDir.getPath();
			// log.debug("compressing: " + tmpDirPath + "...");
			File zip = ZipUtils.zipDirectory(tmpDirPath, tmpDirPath + ".zip");
			zip.deleteOnExit();

			// delete tmpdir on exit
			tmpDir.deleteOnExit();

			this.filename = zip.getName();
			url = "/tmp/" + filename;
			// log.debug("filename: " + filename + " url: " + url);
			return new ForwardResolution(uiPath + "/public/checkout.jsp");

		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("download")
	public Resolution download() {
		try {

			// download url
			url = "/tmp/" + this.filename;
			// log.debug("url: " + url);

			// create oversize item list
			List<String> cart = context.getCart();
			if (cart.size() > 0) {
				QueryResponse response = services.findObjectsByPids(cart);
				results = response.getResults();
				numFound = results.getNumFound();
				resultRows = results.size();
				qTime = response.getQTime();
				elapsedTime = response.getElapsedTime();
			}
			return new ForwardResolution(uiPath + "/public/checkout.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getUrl getter method.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The getFilename getter method.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * The setFilename setter method.
	 * 
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * The getDownloadedItems getter method.
	 * 
	 * @return the downloadedItems
	 */
	public SolrDocumentList getDownloadedItems() {
		return downloadedItems;
	}

	/**
	 * The getOversizeItems getter method.
	 * 
	 * @return the oversizeItems
	 */
	public SolrDocumentList getOversizeItems() {
		return oversizeItems;
	}

	/**
	 * The getMissingItems getter method.
	 * 
	 * @return the missingItems
	 */
	public SolrDocumentList getMissingItems() {
		return missingItems;
	}

	/**
	 * The getCartItems getter method.
	 * 
	 * @return the cartItems
	 */
	public SolrDocumentList getCartItems() {
		return cartItems;
	}

}
