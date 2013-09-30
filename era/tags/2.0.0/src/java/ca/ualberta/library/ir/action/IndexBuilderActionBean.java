/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: IndexBuilderActionBean.java 5615 2012-10-16 18:20:47Z pcharoen $
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
import ca.ualberta.library.ir.scheduling.IndexBuilder;

/**
 * The IndexBuilderActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
 */
@UrlBinding("/action/admin/index/builder/{$event}")
public class IndexBuilderActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(IndexBuilderActionBean.class);

	private IndexBuilder indexBuilder;
	private boolean deleteIndex = false;
	private boolean optimizeIndex = true;
	private boolean fedoraIndex = false;
	private boolean bookmarkIndex = false;
	private boolean favoriteIndex = false;
	private boolean subscriptionIndex = false;

	private Scheduler scheduler;

	/**
	 * The SubscriptionNotifierActionBean class constructor.
	 */
	public IndexBuilderActionBean() {
		super();
	}

	@SpringBean("indexBuilder")
	public void injectServiceFacade(IndexBuilder indexBuilder) {
		// log.trace("injecting indexBuilder bean...");
		this.indexBuilder = indexBuilder;
	}

	@DefaultHandler
	@HandlesEvent("confirm")
	@Secure(roles = "/admin/index")
	public Resolution confirm() {
		try {
			scheduler = services.getSchedulerByName(IndexBuilder.class.getName());
			return new ForwardResolution(uiPath + "/protected/indexBuilder.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("execute")
	@Secure(roles = "/admin/index")
	public Resolution execute() {
		try {
			indexBuilder.setDeleteIndex(deleteIndex);
			indexBuilder.setOptimizeIndex(optimizeIndex);
			indexBuilder.setFedoraIndex(fedoraIndex);
			indexBuilder.setBookmarkIndex(bookmarkIndex);
			indexBuilder.setFavoriteIndex(favoriteIndex);
			indexBuilder.setSubscriptionIndex(subscriptionIndex);
			indexBuilder.run();
			scheduler = services.getSchedulerByName(IndexBuilder.class.getName());
			context.getMessages().add(new LocalizableMessage("index.builderRunning"));
			return new ForwardResolution(uiPath + "/protected/indexBuilder.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The isDeleteIndex getter method.
	 * 
	 * @return the deleteIndex
	 */
	public boolean isDeleteIndex() {
		return deleteIndex;
	}

	/**
	 * The setDeleteIndex setter method.
	 * 
	 * @param deleteIndex the deleteIndex to set
	 */
	public void setDeleteIndex(boolean deleteIndex) {
		this.deleteIndex = deleteIndex;
	}

	/**
	 * The isOptimizeIndex getter method.
	 * 
	 * @return the optimizeIndex
	 */
	public boolean isOptimizeIndex() {
		return optimizeIndex;
	}

	/**
	 * The setOptimizeIndex setter method.
	 * 
	 * @param optimizeIndex the optimizeIndex to set
	 */
	public void setOptimizeIndex(boolean optimizeIndex) {
		this.optimizeIndex = optimizeIndex;
	}

	/**
	 * The isFedoraIndex getter method.
	 * 
	 * @return the fedoraIndex
	 */
	public boolean isFedoraIndex() {
		return fedoraIndex;
	}

	/**
	 * The setFedoraIndex setter method.
	 * 
	 * @param fedoraIndex the fedoraIndex to set
	 */
	public void setFedoraIndex(boolean fedoraIndex) {
		this.fedoraIndex = fedoraIndex;
	}

	/**
	 * The isBookmarkIndex getter method.
	 * 
	 * @return the bookmarkIndex
	 */
	public boolean isBookmarkIndex() {
		return bookmarkIndex;
	}

	/**
	 * The setBookmarkIndex setter method.
	 * 
	 * @param bookmarkIndex the bookmarkIndex to set
	 */
	public void setBookmarkIndex(boolean bookmarkIndex) {
		this.bookmarkIndex = bookmarkIndex;
	}

	/**
	 * The isFavoriteIndex getter method.
	 * 
	 * @return the favoriteIndex
	 */
	public boolean isFavoriteIndex() {
		return favoriteIndex;
	}

	/**
	 * The setFavoriteIndex setter method.
	 * 
	 * @param favoriteIndex the favoriteIndex to set
	 */
	public void setFavoriteIndex(boolean favoriteIndex) {
		this.favoriteIndex = favoriteIndex;
	}

	/**
	 * The isSubscriptionIndex getter method.
	 * 
	 * @return the subscriptionIndex
	 */
	public boolean isSubscriptionIndex() {
		return subscriptionIndex;
	}

	/**
	 * The setSubscriptionIndex setter method.
	 * 
	 * @param subscriptionIndex the subscriptionIndex to set
	 */
	public void setSubscriptionIndex(boolean subscriptionIndex) {
		this.subscriptionIndex = subscriptionIndex;
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
