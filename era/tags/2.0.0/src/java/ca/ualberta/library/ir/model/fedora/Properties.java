/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: Properties.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.fedora;

import java.util.List;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.AccessType;

/**
 * The Properties class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class Properties extends Model {

	// relationship constants
	public static final String OBJECT_TYPE = "FedoraObject";

	private String pid;
	private String label;
	private String contentModel;
	private String ownerId;
	private List<User> owners;
	private String state;
	private String modifiedDate;
	private String createdDate;
	private AccessType accessType;
	private boolean manualApproval;
	private boolean embargoed;
	private boolean hasMetadata;
	private String embargoedDate;
	private String workflowState;
	private String workflowDate;
	private String userId;
	private String submitterId;
	private String formName;

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
	 * 
	 * @return the contentModel
	 */
	public String getContentModel() {
		return contentModel;
	}

	/**
	 * The setContentModel setter method.
	 * 
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
	 * 
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * The setState setter method.
	 * 
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * The isDark getter method.
	 * 
	 * @return the dark
	 */
	public boolean isEmbargoed() {
		return embargoed;
	}

	/**
	 * The setDark setter method.
	 * 
	 * @param dark the dark to set
	 */
	public void setEmbargoed(boolean embargoed) {
		this.embargoed = embargoed;
	}

	/**
	 * The getModifiedDate getter method.
	 * 
	 * @return the modifiedDate
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * The setModifiedDate setter method.
	 * 
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * The getCreatedDate getter method.
	 * 
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * The setCreatedDate setter method.
	 * 
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * The getEmbargoDate getter method.
	 * 
	 * @return the embargoDate
	 */
	public String getEmbargoedDate() {
		return embargoedDate;
	}

	/**
	 * The setEmbargoDate setter method.
	 * 
	 * @param embargoDate the embargoDate to set
	 */
	public void setEmbargoedDate(String embargoedDate) {
		this.embargoedDate = embargoedDate;
	}

	/**
	 * The getOwners getter method.
	 * 
	 * @return the owners
	 */
	public List<User> getOwners() {
		return owners;
	}

	/**
	 * The setOwners setter method.
	 * 
	 * @param owners the owners to set
	 */
	public void setOwners(List<User> owners) {
		this.owners = owners;
	}

	/**
	 * The isManualApproval getter method.
	 * 
	 * @return the manualApproval
	 */
	public boolean isManualApproval() {
		return manualApproval;
	}

	/**
	 * The setManualApproval setter method.
	 * 
	 * @param manualApproval the manualApproval to set
	 */
	public void setManualApproval(boolean manualApproval) {
		this.manualApproval = manualApproval;
	}

	/**
	 * The getAccessType getter method.
	 * 
	 * @return the accessType
	 */
	public AccessType getAccessType() {
		return accessType;
	}

	/**
	 * The setAccessType setter method.
	 * 
	 * @param accessType the accessType to set
	 */
	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Properties [accessType=" + accessType + ", contentModel=" + contentModel + ", createdDate="
			+ createdDate + ", embargoed=" + embargoed + ", embargoedDate=" + embargoedDate + ", label=" + label
			+ ", manualApproval=" + manualApproval + ", modifiedDate=" + modifiedDate + ", ownerId=" + ownerId
			+ ", owners=" + owners + ", pid=" + pid + ", state=" + state + "]";
	}

	/**
	 * The getUserId getter method.
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * The setUserId setter method.
	 * 
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * The getWorkflowDate getter method.
	 * 
	 * @return the workflowDate
	 */
	public String getWorkflowDate() {
		return workflowDate;
	}

	/**
	 * The setWorkflowDate setter method.
	 * 
	 * @param workflowDate the workflowDate to set
	 */
	public void setWorkflowDate(String workflowDate) {
		this.workflowDate = workflowDate;
	}

	/**
	 * The getWorkflowState getter method.
	 * 
	 * @return the workflowState
	 */
	public String getWorkflowState() {
		return workflowState;
	}

	/**
	 * The setWorkflowState setter method.
	 * 
	 * @param workflowState the workflowState to set
	 */
	public void setWorkflowState(String workflowState) {
		this.workflowState = workflowState;
	}

	/**
	 * The getSubmitterId getter method.
	 * 
	 * @return the submitterId
	 */
	public String getSubmitterId() {
		return submitterId;
	}

	/**
	 * The setSubmitterId setter method.
	 * 
	 * @param submitterId the submitterId to set
	 */
	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}

	/**
	 * The getFormName getter method.
	 * 
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * The setFormName setter method.
	 * 
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}

	/**
	 * The isHasMetadata getter method.
	 * 
	 * @return the hasMetadata
	 */
	public boolean getHasMetadata() {
		return hasMetadata;
	}

	/**
	 * The setHasMetadata setter method.
	 * 
	 * @param hasMetadata the hasMetadata to set
	 */
	public void setHasMetadata(boolean hasMetadata) {
		this.hasMetadata = hasMetadata;
	}

}
