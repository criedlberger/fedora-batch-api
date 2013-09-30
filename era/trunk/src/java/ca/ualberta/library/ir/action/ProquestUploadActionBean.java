/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ProquestUploadActionBean.java 5606 2012-10-10 16:45:09Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Scheduler;
import ca.ualberta.library.ir.scheduling.ProquestUpload;
import ca.ualberta.library.ir.scheduling.Schedule;

/**
 * The ProquestUploadActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
 */
@UrlBinding("/action/admin/proquest/{$event}")
public class ProquestUploadActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ProquestUploadActionBean.class);
	private Schedule proquestUpload;
	private Scheduler scheduler;

	/**
	 * The ProquestUploadActionBean class constructor.
	 */
	public ProquestUploadActionBean() {
		super();
	}

	@SpringBean("proquestUpload")
	public void injectServiceFacade(Schedule proquestUpload) {
		// log.trace("injecting proquestUpload bean...");
		this.proquestUpload = proquestUpload;
	}

	@HandlesEvent("confirm")
	@Secure(roles = "/admin/proquest")
	@DefaultHandler
	public Resolution confirm() {
		try {
			scheduler = services.getSchedulerByName(ProquestUpload.class.getName());
			return new ForwardResolution(uiPath + "/protected/proquestUpload.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("execute")
	@Secure(roles = "/admin/proquest")
	public Resolution execute() {
		try {
			proquestUpload.run();
			scheduler = services.getSchedulerByName(ProquestUpload.class.getName());
			context.getMessages().add(new LocalizableMessage("proquest.uploadRunning"));
			return new ForwardResolution(uiPath + "/protected/proquestUpload.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getScheduler getter method.
	 * 
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
}