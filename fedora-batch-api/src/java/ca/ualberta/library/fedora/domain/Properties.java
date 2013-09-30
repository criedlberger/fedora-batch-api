/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: Properties.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The Properties class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public class Properties extends BaseDomain {

	// relationship constants
	public static final String OBJECT_TYPE = "FedoraObject";

	private String pid;
	private String label;
	private String contentModel;
	private String ownerId;
	private String namespace;
	private String state;
	private boolean embargo;
	private String embargoDate;
	private String modifiedDate;
	private String createdDate;

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
	 * The Properties class constructor.
	 */
	public Properties() {
		super();
	}

	/**
	 * The getNamespace getter method.
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * The setNamespace setter method.
	 * 
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * The getLabel getter method.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The setLabel setter method.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * The getContentModel getter method.
	 * @return the contentModel
	 */
	public String getContentModel() {
		return contentModel;
	}

	/**
	 * The setContentModel setter method.
	 * @param contentModel the contentModel to set
	 */
	public void setContentModel(String contentModel) {
		this.contentModel = contentModel;
	}

	/**
	 * The getOwnerId getter method.
	 * 
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * The setOwnerId setter method.
	 * 
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * The getState getter method.
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * The setState setter method.
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * The isDark getter method.
	 * @return the dark
	 */
	public boolean isEmbargo() {
		return embargo;
	}

	/**
	 * The setDark setter method.
	 * @param dark the dark to set
	 */
	public void setEmbargo(boolean embargo) {
		this.embargo = embargo;
	}

	/**
	 * The getModifiedDate getter method.
	 * @return the modifiedDate
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * The setModifiedDate setter method.
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * The getCreatedDate getter method.
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * The setCreatedDate setter method.
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("pid", pid).append("label", label)
			.append("contentModel", contentModel).append("ownerId", ownerId).append("namespace", namespace).append(
				"state", state).append("dark", embargo).append("modifiedDate", modifiedDate).append("createdDate",
				createdDate).toString();
	}

	/**
	 * The getEmbargoDate getter method.
	 * @return the embargoDate
	 */
	public String getEmbargoDate() {
		return embargoDate;
	}

	/**
	 * The setEmbargoDate setter method.
	 * @param embargoDate the embargoDate to set
	 */
	public void setEmbargoDate(String embargoDate) {
		this.embargoDate = embargoDate;
	}

}
