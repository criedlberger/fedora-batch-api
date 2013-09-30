/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: AdminActionBean.java 5606 2012-10-10 16:45:09Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.GroupType.ADMIN;

import java.text.MessageFormat;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import ca.ualberta.library.ir.domain.Scheduler;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.model.solr.Item;
import ca.ualberta.library.ir.scheduling.EmbargoedItemPublisher;
import ca.ualberta.library.ir.scheduling.FacebookPublisher;
import ca.ualberta.library.ir.scheduling.IndexBuilder;
import ca.ualberta.library.ir.scheduling.SubscriptionNotifier;

/**
 * The AdminActionBean class handles administrator navigator requests. The action bean also provides methods for
 * administrator summary sidebar Ajax request.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
 */
@UrlBinding("/action/admin/{$event}/{start}/{sortBy}/{filter}/{department}")
public class AdminActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(AdminActionBean.class);

	private String pid;

	private Long[] count;

	private boolean ccid;

	private boolean approval;

	private List<User> users;

	private long userCnt;

	private Scheduler subscription;

	private Scheduler embargoed;

	private Scheduler index;

	private String message;

	private String filter;

	private long submittedItemCount;

	private long reviewingItemCount;

	private long rejectedItemCount;

	private long totalItemCount;

	private String department;

	private List<Count> departments;

	private FacebookPublisher facebook;

	/**
	 * The AdminActionBean class constructor.
	 */
	public AdminActionBean() {
		super();
	}

	@SpringBean("facebookPublisher")
	public void injectServiceFacade(FacebookPublisher facebook) {
		this.facebook = facebook;
	}

	/**
	 * The home method forwards a request to administrator home page.
	 * 
	 * @return the ForwardResolution for administrator home page.
	 */
	@DefaultHandler
	@HandlesEvent("home")
	public Resolution home() {
		return new ForwardResolution(uiPath + "/protected/adminHome.jsp");
	}

	/**
	 * The getAdminSummary method produces summary data for administrator sidebar.
	 * 
	 * @return the ForwardResolution for administrator summary sidebar.
	 */
	@HandlesEvent("getAdminSummary")
	@Secure(roles = "/admin/menu")
	public Resolution getAdminSummary() {
		try {
			int i = 0;
			count = new Long[NumberUtils.toInt(applicationResources.getString("admin.sidebar.count"))];
			QueryResponse resp = null;

			// dark item count
			resp = services.getDarkItemCount();
			count[i++] = resp.getResults().getNumFound();

			// embargoed item count
			resp = services.getEmbargoedItemCount();
			count[i++] = resp.getResults().getNumFound();

			// pending for approval item count
			resp = services.getManualApprovalItemCount();
			count[i++] = resp.getResults().getNumFound();

			// deleted item count
			resp = services.getItemCountByState(State.D.getName());
			count[i++] = resp.getResults().getNumFound();

			// user activation count
			// count[i++] = services.getInactiveUserCount();

			// collection count
			resp = services.getAllCollectionsCount();
			count[i++] = resp.getResults().getNumFound();

			// community count
			resp = services.getAllCommunitiesCount();
			count[i++] = resp.getResults().getNumFound();

			// user count
			count[i++] = services.getUserCount();

			// group count
			count[i++] = services.getGroupCount();

			// scheduler process date
			subscription = services.getSchedulerByName(SubscriptionNotifier.class.getName());
			embargoed = services.getSchedulerByName(EmbargoedItemPublisher.class.getName());
			index = services.getSchedulerByName(IndexBuilder.class.getName());

			return new ForwardResolution(uiPath + "/protected/adminSummaryAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The darks method queries items in dark repository for dark repository item list page.
	 * 
	 * @return the Resolution for dark repository item list page.
	 */
	@HandlesEvent("dark")
	@Secure(roles = "/admin/dark")
	public Resolution dark() {
		try {
			// context.setItemPageParams(new PageParams(start, sortBy));
			rows = rows == 0 ? defaultRows : rows;
			QueryResponse response = services.findDarkRepositoryItems(start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/darkItemList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The approve method queries inactive items for approval item list page.
	 * 
	 * @return the ForwardResolution for approval item list page.
	 */
	@HandlesEvent("approval")
	@Secure(roles = "/admin/approve")
	public Resolution approval() {
		try {
			sortBy = sortBy == null ? "sort.workflowState asc, sort.workflowDate desc" : sortBy;
			rows = rows == 0 ? defaultRows : rows;
			QueryResponse response;

			// total item count
			response = services.findManualApprovalItems(start, rows, "", null, null);
			totalItemCount = response.getResults().getNumFound();

			// submitted item count
			response = services.findManualApprovalItems(start, rows, "", WorkflowState.Submit.toString(), null);
			submittedItemCount = response.getResults().getNumFound();

			// reviewing item count
			response = services.findManualApprovalItems(start, rows, "", WorkflowState.Review.toString(), null);
			reviewingItemCount = response.getResults().getNumFound();

			// rejected item count
			response = services.findManualApprovalItems(start, rows, "", WorkflowState.Reject.toString(), null);
			rejectedItemCount = response.getResults().getNumFound();

			// get departments
			response = services.getSubmittedDepartments();
			FacetField flds = response.getFacetField("facet.department");
			departments = flds.getValues();

			// task in the pool list
			response = services.findManualApprovalItems(start, rows, sortBy, filter, department);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/approvalList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@Secure(roles = "/admin/approve")
	public Resolution review() {
		try {
			sortBy = sortBy == null ? "sort.datesubmitted desc" : sortBy;
			rows = rows == 0 ? defaultRows : rows;
			QueryResponse response = services.findReviewItems(user.getUsername(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/reviewList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The embargo method queries data for embargoed item list page.
	 * 
	 * @return the ForwardResolution for embargoed item list page.
	 */
	@HandlesEvent("embargoed")
	@Secure(roles = "/admin/embargoed")
	public Resolution embargoed() {
		try {
			sortBy = sortBy == null ? "sort.title asc" : sortBy;
			rows = rows == 0 ? defaultRows : rows;
			QueryResponse response = user.getGroup().getId() == ADMIN.getValue() ? services.findEmbargoedItems(start,
				rows, sortBy) : services.findEmbargoedItemsBySubscription(start, rows, sortBy, user);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/embargoedItemList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The deleted method queries data for deleted item list page.
	 * 
	 * @return the ForwardResolution for deleted item list page.
	 */
	@HandlesEvent("deleted")
	@Secure(roles = "/admin/deleted")
	public Resolution deleted() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "fo.lastModifiedDate_dt desc" : sortBy;
			QueryResponse response = services.findItemsByState(State.D.getName(), start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/deletedItemList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("purgeall")
	@Secure(roles = "/admin/purge")
	public Resolution purgeAll() {
		try {
			rows = Integer.MAX_VALUE;
			sortBy = sortBy == null ? "fo.lastModifiedDate_dt desc" : sortBy;
			QueryResponse response = services.findItemsByState(State.D.getName(), start, rows, sortBy);
			List<Item> items = response.getBeans(Item.class);
			for (Item item : items) {

				// check fedora object
				ca.ualberta.library.ir.model.fedora.Item it = getItemByPid(item.getPid());
				if (it == null || !State.D.toString().equals(it.getProperties().getState())) {
					continue;
				}
				// log.debug("purging pid: " + item.getPid() + " - " + item.getTitles().get(0));

				// check favorite and bookmark before delete
				int bm = services.getBookmarkCountByPid(item.getPid());
				if (bm > 0) {
					services.deleteBookmarkByPid(item.getPid());
					services.deleteByQuery(MessageFormat.format("bm.pid:\"{0}\" AND ir.type:bookmark", item.getPid()));
				}
				int fav = services.getFavoriteCountByPid(item.getPid());
				if (fav > 0) {
					services.deleteFavoriteByPid(item.getPid());
					services.deleteByQuery(MessageFormat.format("fav.pid:\"{0}\" AND ir.type:bookmark", item.getPid()));
				}

				// purge object
				services.purgeObject(item.getPid(), formatLogMessage(WorkflowState.Archive, "Purge object."));
				services.commit();

				// delete handle
				deleteHandle(item.getPid());
			}
			context.getMessages().add(new LocalizableMessage("admin.purgeAllSuccess"));

			// return to deleted items page
			return deleted();

		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The activations method gets inactive users from database for user activation list page.
	 * 
	 * @return the ForwardResolution for user activation list page.
	 */
	@HandlesEvent("activations")
	@Secure(roles = "/admin/activate")
	public Resolution activations() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			users = services.getInactiveUsers(start, rows);
			resultRows = users.size();
			numFound = services.getInactiveUserCount();
			return new ForwardResolution(uiPath + "/protected/activationList.jsp");
		} catch (Exception e) {
			log.error("Could not list users!", e);
			return forwardExceptionError("Could not list users!", e);
		}
	}

	/**
	 * The collections method queries data for manage collections page.
	 * 
	 * @return the ForwardResolution for manage collections page.
	 */
	@HandlesEvent("collections")
	@Secure(roles = "/admin/collection")
	public Resolution collections() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.title asc" : sortBy;
			QueryResponse response = services.findAllCollections(start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/collectionList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The communities method queries data for manage communities page.
	 * 
	 * @return ForwardResolution for manage community page.
	 */
	@HandlesEvent("communities")
	@Secure(roles = "/admin/community")
	public Resolution communities() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			sortBy = sortBy == null ? "sort.title asc" : sortBy;
			QueryResponse response = services.findAllCommunities(start, rows, sortBy);
			results = response.getResults();
			numFound = results.getNumFound();
			resultRows = results.size();
			qTime = response.getQTime();
			elapsedTime = response.getElapsedTime();
			return new ForwardResolution(uiPath + "/protected/communityList.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The users method queries users from database for manage users page.
	 * 
	 * @return the ForwardResolution for manage users page.
	 */
	@HandlesEvent("users")
	@Secure(roles = "/admin/user")
	public Resolution users() {
		try {
			rows = rows == 0 ? defaultRows : rows;
			users = services.getUsers(start, rows);
			resultRows = users.size();
			numFound = services.getUserCount();
			return new ForwardResolution(uiPath + "/protected/userList.jsp");
		} catch (Exception e) {
			log.error("Could not list users!", e);
			return forwardExceptionError("Could not list users!", e);
		}
	}

	@HandlesEvent("reload")
	@Secure(roles = "/admin/reload")
	public Resolution reload() {
		try {
			BaseActionBean.initInputForms();
			return forwardMessage("admin.reload.success");
		} catch (Exception e) {
			log.error("Could not reload inputforms.xml!", e);
			return forwardErrorMessage("admin.reload.error");
		}
	}

	@HandlesEvent("facebook")
	@Secure(roles = "/admin/facebook")
	public Resolution facebook() {
		try {
			facebook.run();
			return forwardMessage("admin.facebook.success");
		} catch (Exception e) {
			log.error("Could not post facebook message!", e);
			return forwardErrorMessage("admin.facebook.error");
		}
	}

	/**
	 * The getStart getter method.
	 * 
	 * @return the start
	 */
	@Override
	public int getStart() {
		return start;
	}

	/**
	 * The setStart setter method.
	 * 
	 * @param start the start to set
	 */
	@Override
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * The getRows getter method.
	 * 
	 * @return the rows
	 */
	@Override
	public int getRows() {
		return rows;
	}

	/**
	 * The setRows setter method.
	 * 
	 * @param rows the rows to set
	 */
	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * The getNumFound getter method.
	 * 
	 * @return the numFound
	 */
	@Override
	public long getNumFound() {
		return numFound;
	}

	/**
	 * The setNumFound setter method.
	 * 
	 * @param numFound the numFound to set
	 */
	@Override
	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	/**
	 * The getResultRows getter method.
	 * 
	 * @return the resultRows
	 */
	@Override
	public int getResultRows() {
		return resultRows;
	}

	/**
	 * The setResultRows setter method.
	 * 
	 * @param resultRows the resultRows to set
	 */
	@Override
	public void setResultRows(int resultRows) {
		this.resultRows = resultRows;
	}

	/**
	 * The getNumPages getter method.
	 * 
	 * @return the numPages
	 */
	@Override
	public int getNumPages() {
		return numPages;
	}

	/**
	 * The getPid getter method.
	 * 
	 * @return the pid
	 */
	@Override
	public String getPid() {
		return pid;
	}

	/**
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	@Override
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The isCcid getter method.
	 * 
	 * @return the ccid
	 */
	public boolean isCcid() {
		return ccid;
	}

	/**
	 * The setCcid setter method.
	 * 
	 * @param ccid the ccid to set
	 */
	public void setCcid(boolean ccid) {
		this.ccid = ccid;
	}

	/**
	 * The isApproval getter method.
	 * 
	 * @return the approval
	 */
	public boolean isApproval() {
		return approval;
	}

	/**
	 * The setApproval setter method.
	 * 
	 * @param approval the approval to set
	 */
	public void setApproval(boolean approval) {
		this.approval = approval;
	}

	/**
	 * The getCount getter method.
	 * 
	 * @return the count
	 */
	public Long[] getCount() {
		return count;
	}

	/**
	 * The setCount setter method.
	 * 
	 * @param count the count to set
	 */
	public void setCount(Long[] count) {
		this.count = count;
	}

	/**
	 * The getUsers getter method.
	 * 
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * The setUsers setter method.
	 * 
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * The getUserCnt getter method.
	 * 
	 * @return the userCnt
	 */
	public long getUserCnt() {
		return userCnt;
	}

	/**
	 * The setUserCnt setter method.
	 * 
	 * @param userCnt the userCnt to set
	 */
	public void setUserCnt(long userCnt) {
		this.userCnt = userCnt;
	}

	/**
	 * The getSubscription getter method.
	 * 
	 * @return the subscription
	 */
	public Scheduler getSubscription() {
		return subscription;
	}

	/**
	 * The getEmbargoed getter method.
	 * 
	 * @return the embargoed
	 */
	public Scheduler getEmbargoed() {
		return embargoed;
	}

	/**
	 * The getIndex getter method.
	 * 
	 * @return the index
	 */
	public Scheduler getIndex() {
		return index;
	}

	/**
	 * The getMessages getter method.
	 * 
	 * @return the messages
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * The setMessages setter method.
	 * 
	 * @param message the messages to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * The getFilter getter method.
	 * 
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * The setFilter setter method.
	 * 
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * The getSubmittedItemCount getter method.
	 * 
	 * @return the submittedItemCount
	 */
	public long getSubmittedItemCount() {
		return submittedItemCount;
	}

	/**
	 * The getReviewingItemCount getter method.
	 * 
	 * @return the reviewingItemCount
	 */
	public long getReviewingItemCount() {
		return reviewingItemCount;
	}

	/**
	 * The getRejectedItemCount getter method.
	 * 
	 * @return the rejectedItemCount
	 */
	public long getRejectedItemCount() {
		return rejectedItemCount;
	}

	/**
	 * The getTotalItemCount getter method.
	 * 
	 * @return the totalItemCount
	 */
	public long getTotalItemCount() {
		return totalItemCount;
	}

	/**
	 * The getDepartment getter method.
	 * 
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * The setDepartment setter method.
	 * 
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * The getDepartments getter method.
	 * 
	 * @return the departments
	 */
	public List<Count> getDepartments() {
		return departments;
	}

}
