/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ReadActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;

import ca.ualberta.library.ir.model.solr.Item;

/**
 * The ReadActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/read/{pid}/{dsId}/{page}")
public class ReadActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ReadActionBean.class);
	private String pid;
	private int page;
	private int noOfPages;
	private String title;
	private String dsId;

	/**
	 * The ReadActionBean class constructor.
	 */
	public ReadActionBean() {
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

	@HandlesEvent("start")
	@DefaultHandler
	public Resolution start() {
		try {
			Item item = services.findObjectByPid(pid).getBeans(Item.class).get(0);
			title = item.getTitles().get(0);

			PDDocument doc;
			page = page == 0 ? 1 : page;

			// check doc file in cache
			String docFilename = MessageFormat.format("{0}/{1}.pdf", tempPath, pid.replace(":", "_"), page);
			File docFile = new File(docFilename);
			if (docFile.exists()) {
				// log.debug("found document: " + docFilename + " in cache");
				doc = PDDocument.load(docFilename);
			} else {
				// log.debug("loading document: " + pid + " from server...");
				doc = PDDocument.load(new URL(MessageFormat
					.format("{0}/fedora/get/{1}/{2}", fedoraServerUrl, pid, dsId)));
				doc.save(docFilename);
				generatePageImages(docFilename);
			}
			noOfPages = doc.getNumberOfPages();
			out.println("noOfpages: " + doc.getNumberOfPages());

			// check page image in cache
			String pageFilename = MessageFormat.format("{0}/{1}_{2}.png", tempPath, pid.replace(":", "_"), page);
			File pageFile = new File(pageFilename);
			if (pageFile.exists()) {
				// log.debug("found page image: " + pageFilename + " in cache");
				return new ForwardResolution(uiPath + "/public/read.jsp");
			} else {
				// log.debug("generating page image: " + pid + " page: " + page + " from document...");
				PDFImageWriter writer = new PDFImageWriter();
				writer.writeImage(doc, "png", null, page, page, MessageFormat.format("{0}/{1}_", tempPath, pid.replace(
					":", "_")));
				return new ForwardResolution(uiPath + "/public/read.jsp");
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The generatePageImages method.
	 * 
	 * @param doc
	 */
	private void generatePageImages(final String docFilename) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				PDDocument doc;
				try {
					doc = PDDocument.load(docFilename);
					PDFImageWriter writer = new PDFImageWriter();
					for (int i = 2; i < doc.getNumberOfPages(); i++) {
						try {
							String pageFilename = MessageFormat.format("{0}/{1}_{2}.png", tempPath, pid.replace(":",
								"_"), i);
							File pageFile = new File(pageFilename);
							if (!pageFile.exists()) {
								// log.debug("generating page image: " + pid + " page: " + i + " from document...");
								writer.writeImage(doc, "png", null, i, i, MessageFormat.format("{0}/{1}_", tempPath,
									pid.replace(":", "_")));
							}
						} catch (Exception e) {
							log.error("Could not generate page: " + i + "!", e);
						}
					}
				} catch (IOException ex) {
					log.error("Could not load the document: " + docFilename + "!", ex);
				}
			}
		});
		thread.start();
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
	 * The getPage getter method.
	 * 
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * The setPage setter method.
	 * 
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * The getNoOfPages getter method.
	 * 
	 * @return the noOfPages
	 */
	public int getNoOfPages() {
		return noOfPages;
	}

	/**
	 * The getTitle getter method.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
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
	 * The getDsId getter method.
	 * 
	 * @return the dsId
	 */
	public String getDsId() {
		return dsId;
	}

}
