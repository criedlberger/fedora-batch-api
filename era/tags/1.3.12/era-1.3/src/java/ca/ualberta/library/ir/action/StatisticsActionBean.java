/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: StatisticsActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The StatisticsActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/stats/{$event}/{subevent}/{pid}/{dsId}/{userId}")
public class StatisticsActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(StatisticsActionBean.class);

	private String subevent;
	private String pid;
	private String dsId;
	private int userId;
	private long count;

	/**
	 * The StatisticsActionBean class constructor.
	 */
	public StatisticsActionBean() {
		super();
	}

	@HandlesEvent("download")
	public Resolution download() {
		try {
			if (subevent.equals("getDownloadCountByPid")) {
				count = services.getDownloadCountByPid(pid);
			} else if (subevent.equals("getDownloadCountByDsId")) {
				count = services.getDownloadCountByDsId(pid, dsId);
			} else if (subevent.equals("getDownloadCountByUserId")) {
				count = services.getDownloadCountByUserId(userId);
			}
			return new ForwardResolution(uiPath + "/public/downloadStatsAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getSubaction getter method.
	 * 
	 * @return the subaction
	 */
	public String getSubevent() {
		return subevent;
	}

	/**
	 * The setSubaction setter method.
	 * 
	 * @param subevent the subaction to set
	 */
	public void setSubevent(String subevent) {
		this.subevent = subevent;
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

	/**
	 * The getLog getter method.
	 * 
	 * @return the log
	 */
	public static Log getLog() {
		return log;
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

}