/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: EmbargoedPublisherActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
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
import ca.ualberta.library.ir.scheduling.EmbargoedItemPublisher;
import ca.ualberta.library.ir.scheduling.Schedule;

/**
 * The EmbargoedPublisherActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/embargoed/publisher/{$event}")
public class EmbargoedPublisherActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(EmbargoedPublisherActionBean.class);
	private Schedule publisher;
	private Scheduler scheduler;

	/**
	 * The SubscriptionNotifierActionBean class constructor.
	 */
	public EmbargoedPublisherActionBean() {
		super();
	}

	@SpringBean("embargoedItemPublisher")
	public void injectServiceFacade(Schedule publisher) {
		// log.trace("injecting embargoedItemPublisher bean...");
		this.publisher = publisher;
	}

	@DefaultHandler
	@HandlesEvent("confirm")
	@Secure(roles = "/admin/embargoed/publisher")
	public Resolution confirm() {
		try {
			scheduler = services.getSchedulerByName(EmbargoedItemPublisher.class.getName());
			return new ForwardResolution(uiPath + "/protected/embargoedPublisher.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("execute")
	@Secure(roles = "/admin/embargoed/publisher")
	public Resolution execute() {
		try {
			publisher.run();
			scheduler = services.getSchedulerByName(EmbargoedItemPublisher.class.getName());
			context.getMessages().add(new LocalizableMessage("embargoed.publisherRunning"));
			return new ForwardResolution(uiPath + "/protected/embargoedPublisher.jsp");
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
