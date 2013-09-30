/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: Collection.java 5615 2012-10-16 18:20:47Z pcharoen $
 */
package ca.ualberta.library.ir.model.solr;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.model.inputform.Form;

/**
 * The Collection class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
 */
public class Collection extends Model {
	public static final String CONTENT_MODEL = "COLLECTION";

	@Field("PID")
	private String id;

	@Field("fo.state")
	private String state;

	@Field("dsm.thumbnail")
	private String thumbnail;

	@Field("dc.title")
	private List<String> titles;

	private String title;

	@Field("dc.description")
	private List<String> descriptions;

	private String description;

	@Field("fo.ownerId")
	private String ownerId;

	private String modifiedDate;

	@Field("fo.lastModifiedDate_dt")
	private Date modified;

	private String createdDate;

	@Field("fo.createdDate_dt")
	private Date created;

	@Field("rel.isMemberOf")
	private List<String> memberOfs;

	@Field("rel.isPartOf")
	private List<String> partOfs;

	@Field("era.sort")
	private String sort;

	@Field("era.metaDescription_b")
	private boolean metaDescription;

	@Field("era.ccid_b")
	private boolean ccid;

	@Field("era.approval_b")
	private boolean approval;

	// @Field("era.sortSER_b")
	// private boolean sortSER;

	@Field("era.formName")
	private String formName;

	@Field("era.proquestUpload_b")
	private boolean proquestUpload;

	@Field("id.handle")
	private String handle;

	/**
	 * The Community class constructor.
	 */
	public Collection() {
		super();
	}

	public Collection(String id) {
		this.id = id;
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
	 * The setId setter method.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The getTitle getter method.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return titles.get(0);
	}

	/**
	 * The setTitle setter method.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		titles = Arrays.asList(title);
	}

	/**
	 * The getDescription getter method.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return descriptions.get(0);
	}

	/**
	 * The setDescription setter method.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		descriptions = Arrays.asList(description);
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.id.equals(((Collection) obj).getId());
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Collection [approval=" + approval + ", ccid=" + ccid + ", created=" + created + ", createdDate="
			+ createdDate + ", description=" + description + ", id=" + id + ", memberOfs=" + memberOfs
			+ ", metaDescription=" + metaDescription + ", modified=" + modified + ", modifiedDate=" + modifiedDate
			+ ", ownerId=" + ownerId + ", partOfs=" + partOfs + ", sort=" + sort + ", state=" + state + ", title="
			+ title + "]";
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
	 * The getPartOfs getter method.
	 * 
	 * @return the partOfs
	 */
	public List<String> getPartOfs() {
		return partOfs;
	}

	/**
	 * The setPartOfs setter method.
	 * 
	 * @param partOfs the partOfs to set
	 */
	public void setPartOfs(List<String> partOfs) {
		this.partOfs = partOfs;
	}

	/**
	 * The getMemberOfs getter method.
	 * 
	 * @return the memberOfs
	 */
	public List<String> getMemberOfs() {
		return memberOfs;
	}

	/**
	 * The setMemberOfs setter method.
	 * 
	 * @param memberOfs the memberOfs to set
	 */
	public void setMemberOfs(List<String> memberOfs) {
		this.memberOfs = memberOfs;
	}

	/**
	 * The getModified getter method.
	 * 
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * The setModified setter method.
	 * 
	 * @param modified the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * The getCreated getter method.
	 * 
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * The setCreated setter method.
	 * 
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * The getSort getter method.
	 * 
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * The setSort setter method.
	 * 
	 * @param sort the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * The isMetaDescription getter method.
	 * 
	 * @return the metaDescription
	 */
	public boolean isMetaDescription() {
		return metaDescription;
	}

	/**
	 * The setMetaDescription setter method.
	 * 
	 * @param metaDescription the metaDescription to set
	 */
	public void setMetaDescription(boolean metaDescription) {
		this.metaDescription = metaDescription;
	}

	/**
	 * The getFormName getter method.
	 * 
	 * @return the formName
	 */
	public String getFormName() {
		return formName == null ? Form.Name.DEFAULT.toString() : formName;
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
	 * The isProquestUpload getter method.
	 * 
	 * @return the proquestUpload
	 */
	public boolean isProquestUpload() {
		return proquestUpload;
	}

	/**
	 * The setProquestUpload setter method.
	 * 
	 * @param proquestUpload the proquestUpload to set
	 */
	public void setProquestUpload(boolean proquestUpload) {
		this.proquestUpload = proquestUpload;
	}

	/**
	 * The getThumbnail getter method.
	 * 
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * The setThumbnail setter method.
	 * 
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * The getHandle getter method.
	 * 
	 * @return the handle
	 */
	public String getHandle() {
		if (handle == null) {
			this.handle = MessageFormat
				.format("{0}/public/view/collection/{1}", ActionConstants.httpServerUrl, this.id);
			return handle;
		} else {
			return handle;
		}
	}

	/**
	 * The setHandle setter method.
	 * 
	 * @param handle the handle to set
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

}
