/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Item.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.solr;

import static ca.ualberta.library.ir.action.ActionConstants.SOLR_STANDARD;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.beans.Field;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.model.inputform.Form;

/**
 * The Item class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class Item extends Model {
	private static final Log log = LogFactory.getLog(Item.class);

	protected SolrServer server = initSolrServer();

	@Field("PID")
	private String pid;

	@Field("dc.contributor")
	private List<String> contributors;

	@Field("dc.coverage")
	private List<String> coverages;

	@Field("dc.creator")
	private List<String> creators;

	@Field("dc.date")
	private List<String> dates;

	@Field("dc.description")
	private List<String> descriptions;

	@Field("dc.format")
	private List<String> formats;

	@Field("dc.identifier")
	private List<String> ids;

	@Field("dc.language")
	private List<String> languages;

	@Field("dc.publisher")
	private List<String> publishers;

	@Field("dc.relation")
	private List<String> relations;

	@Field("dc.rights")
	private List<String> rights;

	@Field("dc.source")
	private List<String> sources;

	@Field("dc.subject")
	private List<String> subjects;

	@Field("dc.title")
	private List<String> titles;

	@Field("dc.type")
	private String type;

	@Field("dcterms.abstract")
	private List<String> abstracts;

	@Field("dcterms.alternative")
	private List<String> alternatives;

	@Field("dcterms.created")
	private String created;

	@Field("dcterms.dateaccepted_dt")
	private Date dateAccepted;

	@Field("dcterms.datesubmitted_dt")
	private Date dateSubmitted;

	@Field("dcterms.extent")
	private List<String> extents;

	@Field("dcterms.identifier")
	private List<String> identifiers;

	@Field("dcterms.isPartOf")
	private List<String> isPartOfs;

	@Field("dcterms.isversionof")
	private List<String> isVersionOfs;

	@Field("dcterms.spatial")
	private List<String> spatials;

	@Field("dcterms.temporal")
	private List<String> temporals;

	@Field("dsm.controlGroups")
	private List<String> dsmControlGroups;

	@Field("dsm.ids")
	private List<String> dsmIds;

	@Field("dsm.labels")
	private List<String> dsmLabels;

	@Field("dsm.license")
	private String license;

	@Field("dsm.mimeTypes")
	private List<String> dsmMimeTypes;

	@Field("dsm.thumbnail")
	private String thumbnail;

	@Field("dsm.urls")
	private List<String> dsmUrls;

	@Field("era.approval_b")
	private boolean approval;

	@Field("era.ccid_b")
	private boolean ccid;

	@Field("era.embargoed_b")
	private boolean embargoed;

	@Field("era.comments")
	private String comments;

	@Field("era.embargoedDate_dt")
	private Date embargoedDate;

	@Field("era.formName")
	private String formName;

	@Field("era.submitterId")
	private String submitterId;

	@Field("era.userId")
	private String userId;

	@Field("era.workflowDate_dt")
	private Date workflowDate;

	@Field("era.workflowState")
	private String workflowState;

	@Field("eraterms.graduationdate")
	private String graduationDate;

	@Field("eraterms.specialization")
	private List<String> specialization;

	@Field("fo.contentModel")
	private String contentModel;

	@Field("fo.createdDate_dt")
	private Date createdDate;

	@Field("fo.label")
	private String label;

	@Field("fo.lastModifiedDate_dt")
	private Date lastModifiedDate;

	@Field("fo.ownerId")
	private String ownerId;

	@Field("fo.state")
	private String state;

	@Field("fo.type")
	private String objecType;

	@Field("id.handle")
	private String handle;

	@Field("id.other")
	private List<String> otherIds;

	@Field("id.uuid")
	private String uuid;

	@Field("license.mimeType")
	private String licenseMimeType;

	@Field("license.url")
	private String licenseUrl;

	private List<String> isMemberOf;

	private List<String> isMemberOfCollection;

	@Field("rel.isPartOf")
	private List<String> isPartOf;

	@Field("thesis.contributor.advisor")
	private List<String> thesisContributorAdvisors;

	@Field("thesis.contributor.committeemember")
	private List<String> thesisContributorCommitteeMembers;

	@Field("thesis.degree.discipline")
	private List<String> thesisDegreeDisciplines;

	@Field("thesis.degree.grantor")
	private List<String> thesisDegreeGrantors;

	@Field("thesis.degree.level")
	private List<String> thesisDegreeLevels;

	@Field("thesis.degree.name")
	private List<String> thesisDegreeNames;

	@Field("timestamp")
	private Date timestamp;

	private List<Community> communities;

	private List<Collection> collections;

	/**
	 * The Item class constructor.
	 */
	public Item() {
		super();
	}

	@Field("rel.isMemberOf")
	public void setRelIsMemberOf(List<String> rels) {
		isMemberOf = rels;
		communities = new ArrayList<Community>();
		for (String rel : rels) {
			try {
				SolrQuery query = new SolrQuery();
				query.setQueryType(SOLR_STANDARD);
				query.setQuery(MessageFormat.format("PID:\"{0}\"", rel));
				List<Community> coms = server.query(query).getBeans(Community.class);
				communities.addAll(coms);
			} catch (Exception e) {
				log.error("Could not get communities!", e);
			}
		}
	}

	@Field("rel.isMemberOfCollection")
	public void setRelIsMemberOfCollection(List<String> rels) {
		isMemberOfCollection = rels;
		collections = new ArrayList<Collection>();
		for (String rel : rels) {
			try {
				SolrQuery query = new SolrQuery();
				query.setQueryType(SOLR_STANDARD);
				query.setQuery(MessageFormat.format("PID:\"{0}\"", rel));
				List<Collection> cols = server.query(query).getBeans(Collection.class);
				collections.addAll(cols);
			} catch (Exception e) {
				log.error("Could not get collections!", e);
			}
		}
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
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The getContributors getter method.
	 * 
	 * @return the contributors
	 */
	public List<String> getContributors() {
		return contributors;
	}

	/**
	 * The setContributors setter method.
	 * 
	 * @param contributors the contributors to set
	 */
	public void setContributors(List<String> contributors) {
		this.contributors = contributors;
	}

	/**
	 * The getCoverages getter method.
	 * 
	 * @return the coverages
	 */
	public List<String> getCoverages() {
		return coverages;
	}

	/**
	 * The setCoverages setter method.
	 * 
	 * @param coverages the coverages to set
	 */
	public void setCoverages(List<String> coverages) {
		this.coverages = coverages;
	}

	/**
	 * The getCreators getter method.
	 * 
	 * @return the creators
	 */
	public List<String> getCreators() {
		return creators;
	}

	/**
	 * The setCreators setter method.
	 * 
	 * @param creators the creators to set
	 */
	public void setCreators(List<String> creators) {
		this.creators = creators;
	}

	/**
	 * The getDates getter method.
	 * 
	 * @return the dates
	 */
	public List<String> getDates() {
		return dates;
	}

	/**
	 * The setDates setter method.
	 * 
	 * @param dates the dates to set
	 */
	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	/**
	 * The getDescriptions getter method.
	 * 
	 * @return the descriptions
	 */
	public List<String> getDescriptions() {
		return descriptions;
	}

	/**
	 * The setDescriptions setter method.
	 * 
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * The getFormats getter method.
	 * 
	 * @return the formats
	 */
	public List<String> getFormats() {
		return formats;
	}

	/**
	 * The setFormats setter method.
	 * 
	 * @param formats the formats to set
	 */
	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	/**
	 * The getIdentifier getter method.
	 * 
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}

	/**
	 * The setIdentifier setter method.
	 * 
	 * @param ids the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	/**
	 * The getLanguages getter method.
	 * 
	 * @return the languages
	 */
	public List<String> getLanguages() {
		return languages;
	}

	/**
	 * The setLanguages setter method.
	 * 
	 * @param languages the languages to set
	 */
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	/**
	 * The getPublishers getter method.
	 * 
	 * @return the publishers
	 */
	public List<String> getPublishers() {
		return publishers;
	}

	/**
	 * The setPublishers setter method.
	 * 
	 * @param publishers the publishers to set
	 */
	public void setPublishers(List<String> publishers) {
		this.publishers = publishers;
	}

	/**
	 * The getRelations getter method.
	 * 
	 * @return the relations
	 */
	public List<String> getRelations() {
		return relations;
	}

	/**
	 * The setRelations setter method.
	 * 
	 * @param relations the relations to set
	 */
	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	/**
	 * The getRights getter method.
	 * 
	 * @return the rights
	 */
	public List<String> getRights() {
		return rights;
	}

	/**
	 * The setRights setter method.
	 * 
	 * @param rights the rights to set
	 */
	public void setRights(List<String> rights) {
		this.rights = rights;
	}

	/**
	 * The getSources getter method.
	 * 
	 * @return the sources
	 */
	public List<String> getSources() {
		return sources;
	}

	/**
	 * The setSources setter method.
	 * 
	 * @param sources the sources to set
	 */
	public void setSources(List<String> sources) {
		this.sources = sources;
	}

	/**
	 * The getSubjects getter method.
	 * 
	 * @return the subjects
	 */
	public List<String> getSubjects() {
		return subjects;
	}

	/**
	 * The setSubjects setter method.
	 * 
	 * @param subjects the subjects to set
	 */
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	/**
	 * The getTitles getter method.
	 * 
	 * @return the titles
	 */
	public List<String> getTitles() {
		return titles;
	}

	/**
	 * The setTitles setter method.
	 * 
	 * @param titles the titles to set
	 */
	public void setTitles(List<String> titles) {
		this.titles = titles;
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
	 * The setType setter method.
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The getAbstracts getter method.
	 * 
	 * @return the abstracts
	 */
	public List<String> getAbstracts() {
		return abstracts;
	}

	/**
	 * The setAbstracts setter method.
	 * 
	 * @param abstracts the abstracts to set
	 */
	public void setAbstracts(List<String> abstracts) {
		this.abstracts = abstracts;
	}

	/**
	 * The getAlternatives getter method.
	 * 
	 * @return the alternatives
	 */
	public List<String> getAlternatives() {
		return alternatives;
	}

	/**
	 * The setAlternatives setter method.
	 * 
	 * @param alternatives the alternatives to set
	 */
	public void setAlternatives(List<String> alternatives) {
		this.alternatives = alternatives;
	}

	/**
	 * The getCreated getter method.
	 * 
	 * @return the created
	 */
	public String getCreated() {
		return created;
	}

	/**
	 * The setCreated setter method.
	 * 
	 * @param created the created to set
	 */
	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * The getDateAccepted getter method.
	 * 
	 * @return the dateAccepted
	 */
	public Date getDateAccepted() {
		return dateAccepted;
	}

	/**
	 * The setDateAccepted setter method.
	 * 
	 * @param dateAccepted the dateAccepted to set
	 */
	public void setDateAccepted(Date dateAccepted) {
		this.dateAccepted = dateAccepted;
	}

	/**
	 * The getDateSubmitted getter method.
	 * 
	 * @return the dateSubmitted
	 */
	public Date getDateSubmitted() {
		return dateSubmitted;
	}

	/**
	 * The setDateSubmitted setter method.
	 * 
	 * @param dateSubmitted the dateSubmitted to set
	 */
	public void setDateSubmitted(Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	/**
	 * The getExtents getter method.
	 * 
	 * @return the extents
	 */
	public List<String> getExtents() {
		return extents;
	}

	/**
	 * The setExtents setter method.
	 * 
	 * @param extents the extents to set
	 */
	public void setExtents(List<String> extents) {
		this.extents = extents;
	}

	/**
	 * The getIdentifiers getter method.
	 * 
	 * @return the identifiers
	 */
	public List<String> getIdentifiers() {
		return identifiers;
	}

	/**
	 * The setIdentifiers setter method.
	 * 
	 * @param identifiers the identifiers to set
	 */
	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	/**
	 * The getIsPartOfs getter method.
	 * 
	 * @return the isPartOfs
	 */
	public List<String> getIsPartOfs() {
		return isPartOfs;
	}

	/**
	 * The setIsPartOfs setter method.
	 * 
	 * @param isPartOfs the isPartOfs to set
	 */
	public void setIsPartOfs(List<String> isPartOfs) {
		this.isPartOfs = isPartOfs;
	}

	/**
	 * The getIsVersionOfs getter method.
	 * 
	 * @return the isVersionOfs
	 */
	public List<String> getIsVersionOfs() {
		return isVersionOfs;
	}

	/**
	 * The setIsVersionOfs setter method.
	 * 
	 * @param isVersionOfs the isVersionOfs to set
	 */
	public void setIsVersionOfs(List<String> isVersionOfs) {
		this.isVersionOfs = isVersionOfs;
	}

	/**
	 * The getSpatials getter method.
	 * 
	 * @return the spatials
	 */
	public List<String> getSpatials() {
		return spatials;
	}

	/**
	 * The setSpatials setter method.
	 * 
	 * @param spatials the spatials to set
	 */
	public void setSpatials(List<String> spatials) {
		this.spatials = spatials;
	}

	/**
	 * The getTemporals getter method.
	 * 
	 * @return the temporals
	 */
	public List<String> getTemporals() {
		return temporals;
	}

	/**
	 * The setTemporals setter method.
	 * 
	 * @param temporals the temporals to set
	 */
	public void setTemporals(List<String> temporals) {
		this.temporals = temporals;
	}

	/**
	 * The getDsmControlGroups getter method.
	 * 
	 * @return the dsmControlGroups
	 */
	public List<String> getDsmControlGroups() {
		return dsmControlGroups;
	}

	/**
	 * The setDsmControlGroups setter method.
	 * 
	 * @param dsmControlGroups the dsmControlGroups to set
	 */
	public void setDsmControlGroups(List<String> dsmControlGroups) {
		this.dsmControlGroups = dsmControlGroups;
	}

	/**
	 * The getDsmIds getter method.
	 * 
	 * @return the dsmIds
	 */
	public List<String> getDsmIds() {
		return dsmIds;
	}

	/**
	 * The setDsmIds setter method.
	 * 
	 * @param dsmIds the dsmIds to set
	 */
	public void setDsmIds(List<String> dsmIds) {
		this.dsmIds = dsmIds;
	}

	/**
	 * The getDsmLabels getter method.
	 * 
	 * @return the dsmLabels
	 */
	public List<String> getDsmLabels() {
		return dsmLabels;
	}

	/**
	 * The setDsmLabels setter method.
	 * 
	 * @param dsmLabels the dsmLabels to set
	 */
	public void setDsmLabels(List<String> dsmLabels) {
		this.dsmLabels = dsmLabels;
	}

	/**
	 * The getLicens getter method.
	 * 
	 * @return the licens
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * The setLicens setter method.
	 * 
	 * @param licens the licens to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * The getDsmMimeTypes getter method.
	 * 
	 * @return the dsmMimeTypes
	 */
	public List<String> getDsmMimeTypes() {
		return dsmMimeTypes;
	}

	/**
	 * The setDsmMimeTypes setter method.
	 * 
	 * @param dsmMimeTypes the dsmMimeTypes to set
	 */
	public void setDsmMimeTypes(List<String> dsmMimeTypes) {
		this.dsmMimeTypes = dsmMimeTypes;
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
	 * The getDsmUrls getter method.
	 * 
	 * @return the dsmUrls
	 */
	public List<String> getDsmUrls() {
		return dsmUrls;
	}

	/**
	 * The setDsmUrls setter method.
	 * 
	 * @param dsmUrls the dsmUrls to set
	 */
	public void setDsmUrls(List<String> dsmUrls) {
		this.dsmUrls = dsmUrls;
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
	 * The getComments getter method.
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * The setComments setter method.
	 * 
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * The getEmbargoedDate getter method.
	 * 
	 * @return the embargoedDate
	 */
	public Date getEmbargoedDate() {
		return embargoedDate;
	}

	/**
	 * The setEmbargoedDate setter method.
	 * 
	 * @param embargoedDate the embargoedDate to set
	 */
	public void setEmbargoedDate(Date embargoedDate) {
		this.embargoedDate = embargoedDate;
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
	public Date getWorkflowDate() {
		return workflowDate;
	}

	/**
	 * The setWorkflowDate setter method.
	 * 
	 * @param workflowDate the workflowDate to set
	 */
	public void setWorkflowDate(Date workflowDate) {
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
	 * The getGraduationDate getter method.
	 * 
	 * @return the graduationDate
	 */
	public String getGraduationDate() {
		return graduationDate;
	}

	/**
	 * The setGraduationDate setter method.
	 * 
	 * @param graduationDate the graduationDate to set
	 */
	public void setGraduationDate(String graduationDate) {
		this.graduationDate = graduationDate;
	}

	/**
	 * The getSpecialization getter method.
	 * 
	 * @return the specialization
	 */
	public List<String> getSpecialization() {
		return specialization;
	}

	/**
	 * The setSpecialization setter method.
	 * 
	 * @param specialization the specialization to set
	 */
	public void setSpecialization(List<String> specialization) {
		this.specialization = specialization;
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
	 * The getFoCreatedDate getter method.
	 * 
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * The setFoCreatedDate setter method.
	 * 
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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
	 * The getLastModifiedDate getter method.
	 * 
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * The setLastModifiedDate setter method.
	 * 
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	 * The getObjecType getter method.
	 * 
	 * @return the objecType
	 */
	public String getObjecType() {
		return objecType;
	}

	/**
	 * The setObjecType setter method.
	 * 
	 * @param objecType the objecType to set
	 */
	public void setObjecType(String objecType) {
		this.objecType = objecType;
	}

	/**
	 * The getHandle getter method.
	 * 
	 * @return the handle
	 */
	public String getHandle() {
		if (handle == null) {
			this.handle = MessageFormat.format("{0}/public/view/item/{1}", ActionConstants.httpServerUrl, this.pid);
		}
		return handle;
	}

	/**
	 * The setHandle setter method.
	 * 
	 * @param handle the handle to set
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 * The getOtherIds getter method.
	 * 
	 * @return the otherIds
	 */
	public List<String> getOtherIds() {
		return otherIds;
	}

	/**
	 * The setOtherIds setter method.
	 * 
	 * @param otherIds the otherIds to set
	 */
	public void setOtherIds(List<String> otherIds) {
		this.otherIds = otherIds;
	}

	/**
	 * The getUuid getter method.
	 * 
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * The setUuid setter method.
	 * 
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * The getLicenseMimeType getter method.
	 * 
	 * @return the licenseMimeType
	 */
	public String getLicenseMimeType() {
		return licenseMimeType;
	}

	/**
	 * The setLicenseMimeType setter method.
	 * 
	 * @param licenseMimeType the licenseMimeType to set
	 */
	public void setLicenseMimeType(String licenseMimeType) {
		this.licenseMimeType = licenseMimeType;
	}

	/**
	 * The getLicenseUrl getter method.
	 * 
	 * @return the licenseUrl
	 */
	public String getLicenseUrl() {
		return licenseUrl;
	}

	/**
	 * The setLicenseUrl setter method.
	 * 
	 * @param licenseUrl the licenseUrl to set
	 */
	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	/**
	 * The getIsMemberOf getter method.
	 * 
	 * @return the isMemberOf
	 */
	public List<String> getIsMemberOf() {
		return isMemberOf;
	}

	/**
	 * The setIsMemberOf setter method.
	 * 
	 * @param isMemberOf the isMemberOf to set
	 */
	public void setIsMemberOf(List<String> isMemberOf) {
		this.isMemberOf = isMemberOf;
	}

	/**
	 * The getIsMemberOfCollection getter method.
	 * 
	 * @return the isMemberOfCollection
	 */
	public List<String> getIsMemberOfCollection() {
		return isMemberOfCollection;
	}

	/**
	 * The setIsMemberOfCollection setter method.
	 * 
	 * @param isMemberOfCollection the isMemberOfCollection to set
	 */
	public void setIsMemberOfCollection(List<String> isMemberOfCollection) {
		this.isMemberOfCollection = isMemberOfCollection;
	}

	/**
	 * The getIsPartOf getter method.
	 * 
	 * @return the isPartOf
	 */
	public List<String> getIsPartOf() {
		return isPartOf;
	}

	/**
	 * The setIsPartOf setter method.
	 * 
	 * @param isPartOf the isPartOf to set
	 */
	public void setIsPartOf(List<String> isPartOf) {
		this.isPartOf = isPartOf;
	}

	/**
	 * The getThesisContributorAdvisors getter method.
	 * 
	 * @return the thesisContributorAdvisors
	 */
	public List<String> getThesisContributorAdvisors() {
		return thesisContributorAdvisors;
	}

	/**
	 * The setThesisContributorAdvisors setter method.
	 * 
	 * @param thesisContributorAdvisors the thesisContributorAdvisors to set
	 */
	public void setThesisContributorAdvisors(List<String> thesisContributorAdvisors) {
		this.thesisContributorAdvisors = thesisContributorAdvisors;
	}

	/**
	 * The getThesisContributorCommitteeMembers getter method.
	 * 
	 * @return the thesisContributorCommitteeMembers
	 */
	public List<String> getThesisContributorCommitteeMembers() {
		return thesisContributorCommitteeMembers;
	}

	/**
	 * The setThesisContributorCommitteeMembers setter method.
	 * 
	 * @param thesisContributorCommitteeMembers the thesisContributorCommitteeMembers to set
	 */
	public void setThesisContributorCommitteeMembers(List<String> thesisContributorCommitteeMembers) {
		this.thesisContributorCommitteeMembers = thesisContributorCommitteeMembers;
	}

	/**
	 * The getThesisDegreeDisciplines getter method.
	 * 
	 * @return the thesisDegreeDisciplines
	 */
	public List<String> getThesisDegreeDisciplines() {
		return thesisDegreeDisciplines;
	}

	/**
	 * The setThesisDegreeDisciplines setter method.
	 * 
	 * @param thesisDegreeDisciplines the thesisDegreeDisciplines to set
	 */
	public void setThesisDegreeDisciplines(List<String> thesisDegreeDisciplines) {
		this.thesisDegreeDisciplines = thesisDegreeDisciplines;
	}

	/**
	 * The getThesisDegreeGrantors getter method.
	 * 
	 * @return the thesisDegreeGrantors
	 */
	public List<String> getThesisDegreeGrantors() {
		return thesisDegreeGrantors;
	}

	/**
	 * The setThesisDegreeGrantors setter method.
	 * 
	 * @param thesisDegreeGrantors the thesisDegreeGrantors to set
	 */
	public void setThesisDegreeGrantors(List<String> thesisDegreeGrantors) {
		this.thesisDegreeGrantors = thesisDegreeGrantors;
	}

	/**
	 * The getThesisDegreeLevels getter method.
	 * 
	 * @return the thesisDegreeLevels
	 */
	public List<String> getThesisDegreeLevels() {
		return thesisDegreeLevels;
	}

	/**
	 * The setThesisDegreeLevels setter method.
	 * 
	 * @param thesisDegreeLevels the thesisDegreeLevels to set
	 */
	public void setThesisDegreeLevels(List<String> thesisDegreeLevels) {
		this.thesisDegreeLevels = thesisDegreeLevels;
	}

	/**
	 * The getThesisDegreeNames getter method.
	 * 
	 * @return the thesisDegreeNames
	 */
	public List<String> getThesisDegreeNames() {
		return thesisDegreeNames;
	}

	/**
	 * The setThesisDegreeNames setter method.
	 * 
	 * @param thesisDegreeNames the thesisDegreeNames to set
	 */
	public void setThesisDegreeNames(List<String> thesisDegreeNames) {
		this.thesisDegreeNames = thesisDegreeNames;
	}

	/**
	 * The getTimestamp getter method.
	 * 
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * The setTimestamp setter method.
	 * 
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * The getCommunities getter method.
	 * 
	 * @return the communities
	 */
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setCommunities setter method.
	 * 
	 * @param communities the communities to set
	 */
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}

	/**
	 * The getCollections getter method.
	 * 
	 * @return the collections
	 */
	public List<Collection> getCollections() {
		return collections;
	}

	/**
	 * The setCollections setter method.
	 * 
	 * @param collections the collections to set
	 */
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	/**
	 * The isEmbargoed getter method.
	 * 
	 * @return the embargoed
	 */
	public boolean isEmbargoed() {
		return embargoed;
	}

	/**
	 * The setEmbargoed setter method.
	 * 
	 * @param embargoed the embargoed to set
	 */
	public void setEmbargoed(boolean embargoed) {
		this.embargoed = embargoed;
	}

}