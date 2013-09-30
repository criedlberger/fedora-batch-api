/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SubscriptionActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.Date;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Subscription;

/**
 * The SubscriptionActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/subscription/{$event}/{subscription.pid}/{subscription.type}/{next}")
public class SubscriptionActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(SubscriptionActionBean.class);

	private Subscription subscription;
	private int numberOfSubscribers;
	private String next;

	/**
	 * The SubscriptionActionBean class constructor.
	 */
	public SubscriptionActionBean() {
		super();
	}

	@HandlesEvent("getSubscriptionInfo")
	public Resolution getSubscriptionInfo() {
		return getSubscriptionStatus();
	}

	@HandlesEvent("getSubscriptionStatus")
	public Resolution getSubscriptionStatus() {
		try {
			// log.debug("getting subscription...");
			if (user != null) {
				Subscription sub = services.getUserSubscriptionByPid(subscription.getPid(), user.getId(),
					subscription.getType());
				if (sub != null) {
					subscription = sub;
				}
			}
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid(), subscription.getType());
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	@HandlesEvent("getSubscriptionWithNotify")
	public Resolution getSubscriptionWithNotify() {
		return getSubscriptionStatus();
	}

	@HandlesEvent("subscribeWithNotify")
	public Resolution subscribeWithNotify() {
		return subscribe();
	}

	@HandlesEvent("unsubscribeWithNotify")
	public Resolution unsubscribeWithNotify() {
		return unsubscribe();
	}

	@HandlesEvent("subscribeInfo")
	public Resolution subscribeInfo() {
		return subscribe();
	}

	@HandlesEvent("unsubscribeInfo")
	public Resolution unsubscribeInfo() {
		return unsubscribe();
	}

	@HandlesEvent("subscribe")
	public Resolution subscribe() {
		try {
			// log.debug("subscibing...");
			Subscription sub = services.getUserSubscriptionByPid(subscription.getPid(), user.getId(),
				subscription.getType());
			if (sub != null) {
				subscription = sub;
			} else {
				subscription.setUser(user);
				subscription.setNotification(true);
				subscription.setCreatedDate(new Date());
			}
			services.saveOrUpdateSubscription(subscription);
			services.addSubscriptionIndex(subscription);
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid());
		} catch (Exception e) {
			log.error("Could not process this request!", e);
		}

		// log.debug("next: " + next);
		if (StringUtils.trimToNull(next) != null) {
			return new RedirectResolution(next);
		} else {
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	@HandlesEvent("unsubscribe")
	public Resolution unsubscribe() {
		try {
			// log.debug("unsubscibing...");
			subscription = services.getSubscription(subscription.getId());
			if (subscription.getUser().getId().intValue() == user.getId().intValue()) {
				services.deleteSubscription(subscription.getId());
				services.deleteSubscription(subscription);
			}
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid());
		} catch (Exception e) {
			log.error("Could not process this request!", e);
		}
		if (StringUtils.trimToNull(next) != null) {
			return new RedirectResolution(next);
		} else {
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	@HandlesEvent("enable")
	public Resolution enable() {
		try {
			// log.debug("enabling notification...");
			subscription = services.getSubscription(subscription.getId());
			if (subscription.getUser().getId().intValue() == user.getId().intValue()) {
				subscription.setNotification(true);
				services.saveOrUpdateSubscription(subscription);
				services.addSubscriptionIndex(subscription);
			}
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid());
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	@HandlesEvent("disable")
	public Resolution disable() {
		try {
			// log.debug("disabling notification...");
			subscription = services.getSubscription(subscription.getId());
			if (subscription.getUser().getId().intValue() == user.getId().intValue()) {
				subscription.setNotification(false);
				services.saveOrUpdateSubscription(subscription);
				services.addSubscriptionIndex(subscription);
			}
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid());
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	@HandlesEvent("getNoOfSubscribers")
	public Resolution getNoOfSubscribers() {
		try {
			// log.debug("getting no of subscriber... " + subscription.getPid());
			numberOfSubscribers = services.getNoOfSubscribers(subscription.getPid());
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return new ForwardResolution(uiPath + "/public/subscriptionAjax.jsp");
		}
	}

	/**
	 * The getSubscription getter method.
	 * 
	 * @return the subscription
	 */
	public Subscription getSubscription() {
		return subscription;
	}

	/**
	 * The setSubscription setter method.
	 * 
	 * @param subscription the subscription to set
	 */
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	/**
	 * The getNumberOfSubscribers getter method.
	 * 
	 * @return the numberOfSubscribers
	 */
	public int getNumberOfSubscribers() {
		return numberOfSubscribers;
	}

	/**
	 * The setNumberOfSubscribers setter method.
	 * 
	 * @param numberOfSubscribers the numberOfSubscribers to set
	 */
	public void setNumberOfSubscribers(int numberOfSubscribers) {
		this.numberOfSubscribers = numberOfSubscribers;
	}

	/**
	 * The getNext getter method.
	 * 
	 * @return the next
	 */
	public String getNext() {
		return next;
	}

	/**
	 * The setNext setter method.
	 * 
	 * @param next the next to set
	 */
	public void setNext(String next) {
		this.next = next;
	}
}
