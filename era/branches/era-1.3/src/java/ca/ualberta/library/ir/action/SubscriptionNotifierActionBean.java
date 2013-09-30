/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: SubscriptionNotifierActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
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
import ca.ualberta.library.ir.scheduling.Schedule;
import ca.ualberta.library.ir.scheduling.SubscriptionNotifier;

/**
 * The SubscriptionNotifierActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/subscription/notifier/{$event}")
public class SubscriptionNotifierActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(SubscriptionNotifierActionBean.class);

	private Schedule notifier;

	private Scheduler scheduler;

	/**
	 * The SubscriptionNotifierActionBean class constructor.
	 */
	public SubscriptionNotifierActionBean() {
		super();
	}

	@SpringBean("subscriptionNotifier")
	public void injectServiceFacade(Schedule notifier) {
		// log.trace("injecting subscriptionNotifier bean...");
		this.notifier = notifier;
	}

	@DefaultHandler
	@HandlesEvent("confirm")
	@Secure(roles = "/admin/subscription/notifier")
	public Resolution confirm() {
		try {
			scheduler = services.getSchedulerByName(SubscriptionNotifier.class.getName());
			return new ForwardResolution(uiPath + "/protected/subscriptionNotifier.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("execute")
	@Secure(roles = "/admin/subscription/notifier")
	public Resolution execute() {
		try {
			// log.debug("executing subscription notifier...");
			notifier.run();
			scheduler = services.getSchedulerByName(SubscriptionNotifier.class.getName());
			context.getMessages().add(new LocalizableMessage("subscription.notifierRunning"));
			return new ForwardResolution(uiPath + "/protected/subscriptionNotifier.jsp");
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
