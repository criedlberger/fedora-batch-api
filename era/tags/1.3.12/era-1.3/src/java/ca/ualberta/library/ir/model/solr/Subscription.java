/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: Subscription.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.solr;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.solr.client.solrj.beans.Field;

/**
 * The Subscription class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class Subscription extends Model {
	public static final String TYPE = "subscription";

	@Field("PID")
	private String id;

	@Field("ir.type")
	private String type = TYPE;

	@Field("sub.type")
	private int subtype;

	@Field("sub.ownerId")
	private String username;

	@Field("sub.pid")
	private String pid;

	@Field("sub.notification_b")
	private boolean notification;

	@Field("sub.createdDate_dt")
	private Date createdDate;

	@Field("sub.title")
	private String title;

	@Field("sub.description")
	private String description;

	@Field("sub.creator")
	private String creator;

	@Field("sub.subject")
	private String subject;

	@Field("sub.authorId")
	private String authorId;

	/**
	 * The Subscription class constructor.
	 */
	public Subscription() {
		super();
	}

	/**
	 * The Subscription class constructor.
	 */
	public Subscription(ca.ualberta.library.ir.domain.Subscription subscription) {
		this.username = subscription.getUser().getUsername();
		this.id = subscription.getPid() + "." + this.username + "." + TYPE + "." + subscription.getType();
		this.subtype = subscription.getType();
		this.pid = subscription.getPid();
		this.notification = subscription.isNotification();
		this.createdDate = subscription.getCreatedDate();
	}

	/**
	 * The getId getter method.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * The getUsername getter method.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
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
	 * The isNotification getter method.
	 * 
	 * @return the notification
	 */
	public boolean isNotification() {
		return notification;
	}

	/**
	 * The getType getter method.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
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
	 * The setTitle setter method.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The getCreatedDate getter method.
	 * 
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * The getSubtype getter method.
	 * 
	 * @return the subtype
	 */
	public int getSubtype() {
		return subtype;
	}

	/**
	 * The getDescription getter method.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The setDescription setter method.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The getCreator getter method.
	 * 
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * The setCreator setter method.
	 * 
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * The getSubject getter method.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * The setSubject setter method.
	 * 
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * The getAuthorId getter method.
	 * 
	 * @return the authorId
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * The setAuthorId setter method.
	 * 
	 * @param authorId the authorId to set
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id).append("type", type)
			.append("subtype", subtype).append("username", username).append("pid", pid)
			.append("notification", notification).append("createdDate", createdDate).append("title", title)
			.append("description", description).append("creator", creator).append("subject", subject)
			.append("authorId", authorId).toString();
	}

	/**
	 * The setId setter method.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The setType setter method.
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The setSubtype setter method.
	 * 
	 * @param subtype the subtype to set
	 */
	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	/**
	 * The setUsername setter method.
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * The setNotification setter method.
	 * 
	 * @param notification the notification to set
	 */
	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	/**
	 * The setCreatedDate setter method.
	 * 
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
