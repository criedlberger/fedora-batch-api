/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: SaveDownloadThread.java 5603 2012-10-05 18:51:26Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.action.ActionConstants.webcrawlerFilter;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Download;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.model.solr.Item;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The SaveDownloadThread class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5603 $ $Date: 2012-10-05 12:51:26 -0600 (Fri, 05 Oct 2012) $
 */

public class SaveDownloadThread extends Thread {
	private static final Log log = LogFactory.getLog(SaveDownloadThread.class);
	private final ApplicationActionBeanContext context;
	private final ServiceFacade services;
	private final Integer id;
	private final String pid;
	private final String dsId;
	private final Date date;

	public SaveDownloadThread(ApplicationActionBeanContext context, String pid, String dsId) {
		this.context = context;
		this.services = context.getServices();
		this.id = null;
		this.pid = pid;
		this.dsId = dsId;
		this.date = new Date();
	}

	public SaveDownloadThread(ApplicationActionBeanContext context, Download download) {
		this.context = context;
		this.services = context.getServices();
		this.id = download.getId();
		this.pid = download.getPid();
		this.dsId = download.getDsId();
		this.date = download.getDownloadedDate();
	}

	@Override
	public void run() {
		try {

			// googlebot, bingbot filter
			String userAgent = context.getRequest().getHeader("User-Agent");
			if (webcrawlerFilter.matcher(userAgent).find()
				|| webcrawlerFilter.matcher(context.getRequest().getRemoteAddr()).find()) {
				return;
			}

			// update download statictics
			Download dl = new Download();
			dl.setId(id);
			dl.setPid(pid);
			dl.setDsId(dsId);
			dl.setDownloadedDate(date);
			User usr = context.getUser();
			if (usr == null) {
				usr = services.getUser(0); // guest user
			}
			dl.setUser(usr);
			// get collection pids
			List<Item> items = services.findObjectByPid(pid).getBeans(Item.class);
			if (!items.isEmpty()) {
				Item item = items.get(0);
				StringBuilder sb = new StringBuilder();
				List<Community> coms = item.getCommunities();
				if (coms != null) {
					for (Community com : coms) {
						sb.append(sb.length() > 0 ? "," : "").append(com.getId());
					}
				}
				List<Collection> cols = item.getCollections();
				if (cols != null) {
					for (Collection col : cols) {
						sb.append(sb.length() > 0 ? "," : "").append(col.getId());
					}
				}
				dl.setCollectionPids(sb.toString());
				dl.setIp(context.getRequest().getRemoteAddr());
				dl.setUserAgent(userAgent);
				services.saveOrUpdateDownload(dl);
			}
		} catch (Exception e) {
			log.warn("Could not save download record! (" + pid + ")", e);
		}
	}
}
