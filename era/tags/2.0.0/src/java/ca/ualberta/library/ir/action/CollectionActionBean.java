/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: CollectionActionBean.java 5615 2012-10-16 18:20:47Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.THUMBNAIL;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.fcrepo.server.types.gen.ObjectFields;
import org.fcrepo.server.types.gen.RelationshipTuple;
import org.openarchives.oai.x20.oaiDc.DcDocument;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.ContentModel;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.exception.FedoraAPIException;
import ca.ualberta.library.ir.exception.ServiceException;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.DublinCore;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.inputform.InputForms;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.FileUtils;

/**
 * The CommunityActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
 */
@UrlBinding("/action/collection/{$event}/{collection.id}/{mode}")
public class CollectionActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(CollectionActionBean.class);

	@ValidateNestedProperties(@Validate(field = "title", required = true))
	private Collection collection;

	private List<Community> communities;

	private List<Community> memberOfCommunities;

	@Validate(required = true)
	private List<String> memberOf;

	private String filename;

	private String imagePath;

	private ObjectFields object;

	private String ownerName;

	private String name;

	private List<Collection> collections;

	/**
	 * The CommunityActionBean class constructor.
	 */
	public CollectionActionBean() {
		super();
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return collection == null ? null : collection.getId();
	}

	@After(on = { "create", "save" }, stages = LifecycleStage.BindingAndValidation)
	public void restoreCommunityList() {
		try {
			if (context.getValidationErrors().size() > 0) {
				communities = services.getAllCommunities();
				memberOfCommunities = new ArrayList<Community>();
				if (memberOf != null) {
					for (String mem : memberOf) {
						memberOfCommunities.add(communities.remove(communities.indexOf(new Community(mem))));
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not restore community list!", e);
		}
	}

	@HandlesEvent("view")
	@DontValidate
	@Secure(roles = "/collection/read,/object/ccid")
	public Resolution view() {
		try {
			try {
				object = getObject(collection.getId());
			} catch (Exception e) {
				context.getValidationErrors().addGlobalError(new LocalizableError("collectionNotFound"));
				return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
			}

			getCollectionDetails(object);
			initProperties(collection.getId());
			if (hasThumbnail(collection.getId())) {
				imagePath = datastreamUrl + "/get/" + collection.getId() + "/" + THUMBNAIL.toString();
			}
			return new ForwardResolution(uiPath + "/protected/collection.jsp");
		} catch (Exception e) {
			log.error("Could not view this collection!", e);
			return forwardExceptionError("Could not view a collection!", e);
		}
	}

	@HandlesEvent("edit")
	@DontValidate
	@Secure(roles = "/collection/update,/admin/collection,/object/ccid,/object/owner")
	public Resolution edit() {
		try {
			try {
				object = getObject(collection.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCollectionMessage.jsp");
			}

			getCollectionDetails();
			initProperties(object.getPid().getValue());

			if (hasThumbnail(collection.getId())) {
				imagePath = datastreamUrl + "/get/" + collection.getId() + "/" + THUMBNAIL.toString();
			}
			return new ForwardResolution(uiPath + "/protected/editCollection.jsp");
		} catch (Exception e) {
			log.error("Could not edit this collection!", e);
			return forwardExceptionError("Could not edit a collection!", e);
		}
	}

	private void initProperties(String pid) throws SolrServerException, FedoraAPIException {
		RelationshipTuple[] colRels = services.getRelationships(pid, FedoraRelationship.IS_PART_OF.getURI());
		if (colRels != null) {
			for (RelationshipTuple rel : colRels) {
				if (rel.getObject().equals(PartOfRelationship.CCID_AUTH.getURI())) {
					collection.setCcid(true);
				} else if (rel.getObject().equals(PartOfRelationship.MANUAL_APPROVAL.getURI())) {
					collection.setApproval(true);
				}
			}
		}
		RelationshipTuple[] sortRels = services.getRelationships(pid, Definitions.SORT.getURI());
		collection.setSort(ArrayUtils.isEmpty(sortRels) ? null : sortRels[0].getObject());
		RelationshipTuple[] metaRels = services.getRelationships(pid, Definitions.META_DESCRIPTION.getURI());
		collection.setMetaDescription(ArrayUtils.isEmpty(metaRels) ? false : BooleanUtils.toBoolean(metaRels[0]
			.getObject()));
		RelationshipTuple[] formNameSERRels = services.getRelationships(pid, Definitions.FORM_NAME.getURI());
		collection.setFormName(ArrayUtils.isEmpty(formNameSERRels) ? null : formNameSERRels[0].getObject());
		RelationshipTuple[] proquestUploadRels = services.getRelationships(pid, Definitions.PROQUEST_UPLOAD.getURI());
		collection.setProquestUpload(ArrayUtils.isEmpty(proquestUploadRels) ? false : BooleanUtils
			.toBoolean(proquestUploadRels[0].getObject()));
	}

	@HandlesEvent("create")
	@Secure(roles = "/collection/create,/admin/collection")
	public Resolution create() {
		try {
			String pid = createObject();
			collection.setId(pid);

			// add object relationships
			services.modifyObjectRelationships(pid, "Collection Relationships", memberOf, null, null, "Created by "
				+ user.getUsername());

			// add relationships
			addRelationships();
			services.commit(false);

			String handle = buildHandle(pid, HandleType.COLLECTION);
			context.getMessages().add(
				new LocalizableMessage("collection.createSuccess", trimTitle(collection.getTitle()), handle));
			return new RedirectResolution("/action/collection/preCreate");

		} catch (Exception e) {
			log.error("Could not create the collection!", e);
			return forwardExceptionError("Could not save this collection!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/collection/update,/admin/collection,/object/ccid,/object/owner")
	public Resolution save() {
		try {
			// log.debug("saving properties...");
			try {
				object = getObject(collection.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
			}
			saveObject();

			// save object relationships
			services.modifyObjectRelationships(collection.getId(), "Collection Relationships", memberOf, null, null,
				"Modified by " + user.getUsername());

			// add relationships
			addRelationships();
			services.commit(false);

			context.getMessages().add(
				new LocalizableMessage("collection.modifySuccess", trimTitle(object.getTitle().get(0))));
			return new RedirectResolution("/action/collection/view/" + collection.getId());

		} catch (Exception e) {
			log.error("Could not save this collection!", e);
			return forwardExceptionError("Could not save a collection!", e);
		}
	}

	private void addRelationships() throws FedoraAPIException {

		// add content model
		services.addContentModel(collection.getId(), ContentModel.NAMESPACE + ":" + Collection.CONTENT_MODEL);

		// add form name
		if (collection.getFormName() != null) {
			services.addRelationship(collection.getId(), Definitions.FORM_NAME.getURI(), collection.getFormName(),
				true, null);
		}

		// add ccid auth
		if (collection.isCcid()) {
			services.addRelationship(collection.getId(), FedoraRelationship.IS_PART_OF.getURI(),
				PartOfRelationship.CCID_AUTH.getURI(), false, null);
		}

		// add manual approval
		if (collection.isApproval()) {
			services.addRelationship(collection.getId(), FedoraRelationship.IS_PART_OF.getURI(),
				PartOfRelationship.MANUAL_APPROVAL.getURI(), false, null);
		}

		// add item meta description html header
		if (collection.isMetaDescription()) {
			services.addRelationship(collection.getId(), Definitions.META_DESCRIPTION.getURI(),
				BooleanUtils.toStringTrueFalse(collection.isMetaDescription()), true, null);
		}

		// add collection item sort literal
		if (collection.getSort() != null) {
			services.addRelationship(collection.getId(), Definitions.SORT.getURI(), collection.getSort(), true, null);
		}

		// add proquest upload boolean
		if (collection.isProquestUpload()) {
			services.addRelationship(collection.getId(), Definitions.PROQUEST_UPLOAD.getURI(),
				BooleanUtils.toStringTrueFalse(collection.isProquestUpload()), true, null);
		}
	}

	private String createObject() throws Exception {
		// ingest object
		// log.debug("creating object...");
		Properties properties = new Properties();
		properties.setState(State.A.toString());
		properties.setLabel(collection.getTitle());
		properties.setContentModel(Collection.CONTENT_MODEL);
		properties.setOwnerId(user.getUsername());
		String pid = services.ingest(properties, "Created by " + user.getUsername());

		// create handle
		saveHandle(pid, HandleType.COLLECTION);
		String handle = buildHandle(pid, HandleType.COLLECTION);

		// modify dublin core
		DublinCore dublinCore = new DublinCore();
		// set identifier
		dublinCore.getFields().get(0).setValues(Arrays.asList(pid, handle));
		// set title
		dublinCore.getFields().get(1).setValues(Arrays.asList(collection.getTitle()));
		// set creator
		dublinCore.getFields().get(2).setValues(Arrays.asList(user.getFirstName() + " " + user.getLastName()));
		// set description
		dublinCore.getFields().get(4).setValues(Arrays.asList(StringUtils.trimToEmpty(collection.getDescription())));
		// log.debug(dublinCore.parse().toString());
		services.modifyDublinCore(pid, collection.getTitle(), dublinCore.parse().toString().trim(), "Created by "
			+ user.getUsername());

		if (StringUtils.trimToNull(filename) != null) {

			// add thumbnail datastream
			File thumbnail = new File(tempPath + "/" + filename);
			services.addDatastream(pid, THUMBNAIL.toString(), "Collection Logo", FileUtils.getContentType(thumbnail),
				new FileInputStream(thumbnail), "Created by " + user.getUsername());
		}
		return pid;
	}

	private void saveObject() throws Exception {
		// log.debug("saving object...");

		// modify the object
		Properties properties = new Properties();
		properties.setPid(collection.getId());
		properties.setState(collection.getState());
		properties.setLabel(collection.getTitle());
		properties.setContentModel(Collection.CONTENT_MODEL);
		properties.setOwnerId(object.getOwnerId().getValue());
		// log.debug(properties.toString());
		services.modifyObject(properties, "Modified by " + user.getUsername());

		// save handle
		saveHandle(collection.getId(), HandleType.COLLECTION);
		String handle = buildHandle(collection.getId(), HandleType.COLLECTION);

		// modify dublin core
		DublinCore dublinCore = new DublinCore();
		dublinCore.getFields().get(0).setValues(Arrays.asList(collection.getId(), handle)); // identifier
		dublinCore.getFields().get(1).setValues(Arrays.asList(collection.getTitle())); // title
		User usr = services.getUser(object.getOwnerId().getValue());
		dublinCore.getFields().get(2).setValues(Arrays.asList(usr.getFirstName() + " " + usr.getLastName())); // creator
		dublinCore.getFields().get(4).setValues(Arrays.asList(collection.getDescription())); // description
		// log.debug(dublinCore.parse().toString());
		services.modifyDublinCore(collection.getId(), collection.getTitle(), dublinCore.parse().toString().trim(),
			"Modified by " + user.getUsername());

		if (StringUtils.trimToNull(filename) != null) {

			// add thumbnail datastream
			File thumbnail = new File(tempPath + "/" + filename);
			if (hasThumbnail(collection.getId())) {
				Datastream dsm = new Datastream();
				dsm.setPid(collection.getId());
				dsm.setDsId(THUMBNAIL.toString());
				dsm.setMimeType(FileUtils.getContentType(thumbnail));
				dsm.setData(new FileInputStream(thumbnail));
				services.modifyDatastreamByReference(dsm, "Modified by " + user.getUsername());
			} else {
				services.addDatastream(collection.getId(), THUMBNAIL.toString(), "Collection Logo",
					FileUtils.getContentType(thumbnail), new FileInputStream(thumbnail),
					"Created by " + user.getUsername());
			}
		}
	}

	private void getCollectionDetails() throws SolrServerException, FedoraAPIException, ServiceException {
		collection.setId(object.getPid().getValue());
		collection.setState(object.getState().getValue());
		DcDocument dc = services.getDublinCore(object.getPid().getValue());
		DublinCore dublinCore = new DublinCore(dc);
		collection.setTitle(dublinCore.getFields().get(1).getValues().get(0));
		collection.setDescription(dublinCore.getFields().get(4).getValues().get(0));
		collection.setModifiedDate(object.getMDate().getValue());

		// get community list
		communities = services.getAllCommunities();

		// get member of community
		memberOfCommunities = new ArrayList<Community>();
		RelationshipTuple[] rels = services.getRelationships(collection.getId(),
			FedoraRelationship.IS_MEMBER_OF.getURI());
		for (RelationshipTuple rel : rels) {
			String comPid = slashPattern.split(rel.getObject())[1];
			Community com = services.getCommunity(comPid);
			memberOfCommunities.add(com);
			communities.remove(com);
		}
	}

	@HandlesEvent("delete")
	@DontValidate
	@Secure(roles = "/collection/delete,/admin/collection,/object/ccid,/object/owner")
	public Resolution delete() {
		try {
			try {
				object = getObject(collection.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCollectionMessage.jsp");
			}
			QueryResponse resp = services.findMemberObjects(collection.getId(),
				FedoraRelationship.IS_MEMBER_OF_COLLECTION.getFieldName(), 1);
			SolrDocumentList results = resp.getResults();
			if (results.getNumFound() > 0) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("collection.collectionNotEmpty", trimTitle(object.getTitle().get(0))));
			}

			// check subscription before delete
			int sub = services.getSubscriptionCountByPid(collection.getId());
			if (sub > 0) {
				services.deleteSubscriptionByPid(collection.getId());
				services.deleteByQuery(MessageFormat.format("sub.pid:\"{0}\"", collection.getId()));
				// context.getValidationErrors().addGlobalError(
				// new LocalizableError("collection.subscriptionError", new Object[] { sub }));
			}
			if (context.getValidationErrors().size() > 0) {
				return new ForwardResolution(uiPath + "/protected/editCollectionMessage.jsp");
			}
			services.purgeObject(collection.getId(), "Deleted by " + user.getUsername());
			services.commit(false);

			// delete handle
			deleteHandle(collection.getId());

			context.getMessages().add(
				new LocalizableMessage("collection.deleteSuccess", trimTitle(object.getTitle().get(0))));
			return new ForwardResolution(uiPath + "/protected/editCollectionMessage.jsp");

		} catch (Exception e) {
			log.error("Could not delete this collection!", e);
			return forwardExceptionError("Could not delete a collection!", e);
		}
	}

	@DefaultHandler
	@HandlesEvent("preCreate")
	@DontValidate
	@Secure(roles = "/admin/collection,/collection/create")
	public Resolution preCreate() {
		try {

			// get community list
			communities = services.getAllCommunities();

			return new ForwardResolution(uiPath + "/protected/editCollection.jsp");
		} catch (Exception e) {
			log.error("Could not create a collection!", e);
			return forwardExceptionError("Could not create a collection!", e);
		}
	}

	private void getCollectionDetails(ObjectFields object) throws FedoraAPIException, SolrServerException {
		collection = new Collection();
		collection.setId(object.getPid().getValue());
		collection.setState(object.getState().getValue());
		collection.setTitle(object.getLabel().getValue());
		DcDocument dc = services.getDublinCore(object.getPid().getValue());
		// log.debug(dc.toString());
		DublinCore dublinCore = new DublinCore(dc);
		collection.setDescription(dublinCore.getFields().get(4).getValues().get(0));
		collection.setModifiedDate(object.getMDate().getValue());
		collection.setCreatedDate(object.getCDate().getValue());
		collection.setOwnerId(object.getOwnerId().getValue());

		// get item relationships
		memberOfCommunities = services.getMemberOfCommunities(object.getPid().getValue());

		// get relationships
		initProperties(object.getPid().getValue());

		User owner = services.getUser(collection.getOwnerId());
		ownerName = owner == null ? "" : owner.getFirstName() + " " + owner.getLastName();
	}

	@HandlesEvent("getCollectionsByName")
	@DontValidate
	public Resolution getCollectionsByName() {
		try {
			QueryResponse response = services.findCollectionsByName(0, ActionConstants.suggestionCount, name);
			collections = response.getBeans(Collection.class);
		} catch (Exception e) {
			collections = new ArrayList<Collection>();
		}
		return new ForwardResolution(uiPath + "/protected/collectionAjax.jsp");
	}

	/**
	 * The getCollection getter method.
	 * 
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * The setCollection setter method.
	 * 
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * The getMemberOf getter method.
	 * 
	 * @return the memberOf
	 */
	public List<String> getMemberOf() {
		return memberOf;
	}

	/**
	 * The setMemberOf setter method.
	 * 
	 * @param memberOf the memberOf to set
	 */
	public void setMemberOf(List<String> memberOf) {
		this.memberOf = memberOf;
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
	 * The getMemberOfCommunities getter method.
	 * 
	 * @return the memberOfCommunities
	 */
	public List<Community> getMemberOfCommunities() {
		return memberOfCommunities;
	}

	/**
	 * The setMemberOfCommunities setter method.
	 * 
	 * @param memberOfCommunities the memberOfCommunities to set
	 */
	public void setMemberOfCommunities(List<Community> memberOfCommunities) {
		this.memberOfCommunities = memberOfCommunities;
	}

	/**
	 * The getFilename getter method.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * The setFilename setter method.
	 * 
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * The getImagePath getter method.
	 * 
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * The setImagePath setter method.
	 * 
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * The getOwnerName getter method.
	 * 
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * The setOwnerName setter method.
	 * 
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * The getInputforms getter method.
	 * 
	 * @return the inputforms
	 */
	@Override
	public InputForms getInputForms() {
		return inputForms;
	}

	/**
	 * The getName getter method.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The setName setter method.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getCollections getter method.
	 * 
	 * @return the collections
	 */
	public List<Collection> getCollections() {
		return collections;
	}

}
