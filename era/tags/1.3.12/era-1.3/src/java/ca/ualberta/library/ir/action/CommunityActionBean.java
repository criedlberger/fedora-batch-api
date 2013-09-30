/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: CommunityActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.THUMBNAIL;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import fedora.server.types.gen.ObjectFields;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.ContentModel;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.DublinCore;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.FileUtils;

/**
 * The CommunityActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/community/{$event}/{community.id}")
public class CommunityActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(CommunityActionBean.class);

	private Community community;

	private String filename;

	private String imagePath;

	private ObjectFields object;

	private String mode;

	private List<String> properties;
	private HashMap<Object, Boolean> partOfs;
	private PartOfRelationship[] partOfList;

	private String ownerName;

	private String name;

	private List<Community> communities;

	/**
	 * The CommunityActionBean class constructor.
	 */
	public CommunityActionBean() {
		super();
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		// log.debug("validation method...");
		if (community == null || StringUtils.trimToNull(community.getTitle()) == null) {
			errors.add("community.title", new LocalizableError("titleRequired"));
		}
		if (errors.size() > 0) {
			try {
				if (context.getEventName().startsWith("create")) {
					partOfs = new HashMap<Object, Boolean>();
					partOfList = PartOfRelationship.values();
				} else {
					initProperties(community.getId());
				}
			} catch (SolrServerException e) {
				log.error("Initialize Properties Error!", e);
			}
		}
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return community == null ? null : community.getId();
	}

	@HandlesEvent("view")
	@Secure(roles = "/community/read,/object/ccid")
	@DontValidate
	public Resolution view() {
		try {
			try {
				object = getObject(community.getId());
			} catch (Exception e) {
				context.getValidationErrors().addGlobalError(new LocalizableError("communityNotFound"));
				return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
			}

			community = new Community();
			getCommunityDetails(object, community);

			// get part of relationship
			if (user != null
				&& services.getGroupPermissionByGroupId(user.getGroup().getId(),
					SystemPermissions.ADMIN_COMMUNITY.getPermission()).isAllowed()) {
				initProperties(object.getPid());
			}

			if (hasThumbnail(community.getId())) {
				imagePath = datastreamUrl + "/get/" + community.getId() + "/" + THUMBNAIL.toString();
			}

			User owner = services.getUser(community.getOwnerId());
			ownerName = owner == null ? "" : owner.getFirstName() + " " + owner.getLastName();
			return new ForwardResolution(uiPath + "/protected/community.jsp");
		} catch (Exception e) {
			log.error("Could not view this community!", e);
			return forwardExceptionError("Could not view this community!", e);
		}
	}

	@HandlesEvent("edit")
	@DontValidate
	@Secure(roles = "/community/update,/admin/community,/object/ccid,/object/owner")
	public Resolution edit() {
		try {
			try {
				object = getObject(community.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
			}

			getCommunityDetails(object, community);
			initProperties(community.getId());

			if (hasThumbnail(community.getId())) {
				imagePath = datastreamUrl + "/get/" + community.getId() + "/" + THUMBNAIL.toString();
			}
			return new ForwardResolution(uiPath + "/protected/editCommunity.jsp");
		} catch (Exception e) {
			log.error("Could not edit this community!", e);
			return forwardExceptionError("Could not edit this community!", e);
		}
	}

	private void initProperties(String pid) throws SolrServerException {
		partOfs = new HashMap<Object, Boolean>();
		List<String> rels = services.getRelationshipPids(pid, FedoraRelationship.IS_PART_OF.getFieldName());
		for (Object rel : rels) {
			// log.debug("partOfs: " + rel);
			partOfs.put(rel, Boolean.TRUE);
		}
		partOfList = PartOfRelationship.values();
	}

	@HandlesEvent("save")
	@Secure(roles = "/community/update,/admin/community,/object/ccid,/object/owner")
	public Resolution save() {
		try {
			// log.debug("saving object...");
			try {
				object = getObject(community.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
			}

			// modify the object
			Properties props = new Properties();
			props.setPid(community.getId());
			props.setState(State.A.toString());
			props.setLabel(community.getTitle());
			props.setContentModel(Community.CONTENT_MODEL);
			props.setOwnerId(object.getOwnerId());
			// log.debug(props.toString());
			services.modifyObject(props, "Modified by " + user.getUsername());

			// save handle
			saveHandle(community.getId(), HandleType.COMMUNITY);
			String handle = buildHandle(community.getId(), HandleType.COMMUNITY);

			// modify dublin core
			DublinCore dublinCore = new DublinCore();
			dublinCore.getFields().get(0).setValues(Arrays.asList(community.getId(), handle)); // identifier
			dublinCore.getFields().get(1).setValues(Arrays.asList(community.getTitle())); // title
			User usr = services.getUser(object.getOwnerId());
			dublinCore.getFields().get(2).setValues(Arrays.asList(usr.getFirstName() + " " // creator
				+ usr.getLastName()));
			dublinCore.getFields().get(4).setValues(Arrays.asList(community.getDescription())); // description
			// log.debug(dublinCore.parse().toString());
			services.modifyDublinCore(community.getId(), community.getTitle(), dublinCore.parse().toString().trim(),
				"Modified by " + user.getUsername());

			if (StringUtils.trimToNull(filename) != null) {

				// add thumbanil datastream
				File thumbnail = new File(tempPath + "/" + filename);
				if (hasThumbnail(community.getId())) {
					Datastream dsm = new Datastream();
					dsm.setPid(community.getId());
					dsm.setDsId(THUMBNAIL.toString());
					dsm.setMimeType(FileUtils.getContentType(thumbnail));
					dsm.setData(new FileInputStream(thumbnail));
					services.modifyDatastreamByReference(dsm, "Modified by " + user.getUsername());
				} else {
					services.addDatastream(community.getId(), THUMBNAIL.toString(), "Community Logo",
						FileUtils.getContentType(thumbnail), new FileInputStream(thumbnail),
						"Created by " + user.getUsername());
				}
			}

			// save relationships
			services.modifyObjectRelationships(community.getId(), "Community Relationships", null, null,
				this.properties, "Modified by " + user.getUsername());

			// add content model
			services.addContentModel(community.getId(), ContentModel.NAMESPACE + ":" + Community.CONTENT_MODEL);

			// save oai relationships
			if (proaiEnabled) {
				services.addRelationship(community.getId(), ApplicationProperties.getString("proai.fedora.setSpec"),
					community.getId(), true, null);
				services.addRelationship(community.getId(), ApplicationProperties.getString("proai.fedora.setName"),
					community.getTitle(), true, null);
			}

			services.commit(false);

			context.getMessages().add(new LocalizableMessage("community.modifySuccess", trimTitle(object.getTitle(0))));
			return new RedirectResolution("/action/community/view/" + community.getId());

		} catch (Exception e) {
			log.error("Could not save this community!", e);
			return forwardExceptionError("Could not save this community!", e);
		}
	}

	@DefaultHandler
	@HandlesEvent("preCreate")
	@DontValidate
	@Secure(roles = "/community/create,/admin/community")
	public Resolution preCreate() {
		try {
			community = null;
			partOfs = new HashMap<Object, Boolean>();
			partOfList = PartOfRelationship.values();
			return new ForwardResolution(uiPath + "/protected/editCommunity.jsp");

		} catch (Exception e) {
			log.error("Could not create a community!", e);
			return forwardExceptionError("Could not create this community!", e);
		}
	}

	@HandlesEvent("create")
	@Secure(roles = "/community/create,/admin/community")
	public Resolution create() {
		try {
			// ingest object
			Properties props = new Properties();
			props.setState(State.A.toString());
			props.setLabel(community.getTitle());
			props.setContentModel(Community.CONTENT_MODEL);
			props.setOwnerId(user.getUsername());
			String pid = services.ingest(props, "Created by " + user.getUsername());

			saveHandle(pid, HandleType.COMMUNITY);
			String handle = buildHandle(pid, HandleType.COMMUNITY);

			// modify dublin core
			DublinCore dublinCore = new DublinCore();

			dublinCore.getFields().get(0).setValues(Arrays.asList(pid, handle)); // identifier
			dublinCore.getFields().get(1).setValues(Arrays.asList(community.getTitle())); // title
			dublinCore.getFields().get(2).setValues(Arrays.asList(user.getFirstName() + " " + user.getLastName())); // creator
			dublinCore.getFields().get(4).setValues(Arrays.asList(StringUtils.trimToEmpty(community.getDescription()))); // description
			// log.debug(dublinCore.parse().toString());
			services.modifyDublinCore(pid, community.getTitle(), dublinCore.parse().toString().trim(), "Created by "
				+ user.getUsername());

			if (StringUtils.trimToNull(filename) != null) {

				// add thumbnail datastream
				File thumbnail = new File(tempPath + "/" + filename);
				services.addDatastream(pid, THUMBNAIL.toString(), "Community Logo",
					FileUtils.getContentType(thumbnail), new FileInputStream(thumbnail),
					"Created by " + user.getUsername());
			}

			// add object relationships
			if (properties != null) {
				services.modifyObjectRelationships(pid, "Community Relationships", null, null, properties,
					"Modified by " + user.getUsername());
			}

			// add content model
			services.addContentModel(pid, ContentModel.NAMESPACE + ":" + Community.CONTENT_MODEL);

			// save oai relationships
			if (proaiEnabled) {
				services.addRelationship(pid, ApplicationProperties.getString("proai.fedora.setSpec"), pid, true, null);
				services.addRelationship(pid, ApplicationProperties.getString("proai.fedora.setName"),
					community.getTitle(), true, null);
			}
			services.commit(false);

			context.getMessages().add(
				new LocalizableMessage("community.createSuccess", trimTitle(community.getTitle()), handle));
			return new RedirectResolution("/action/community/preCreate");

		} catch (Exception e) {
			log.error("Could not create this commnunity!", e);
			return forwardExceptionError("Could not create this community!", e);
		}
	}

	@DontValidate
	@HandlesEvent("delete")
	@Secure(roles = "/community/delete,/admin/community,/object/ccid,/object/owner")
	public Resolution delete() {
		try {
			// log.debug("deleting: " + community.getId());
			try {
				object = getObject(community.getId());
			} catch (Exception e) {
				return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
			}
			QueryResponse resp = services.findMemberObjects(community.getId(),
				FedoraRelationship.IS_MEMBER_OF.getFieldName(), 1);
			if (resp.getResults().getNumFound() > 0) {
				// log.debug("title: " + trimTitle(object.getTitle(0)));
				context.getValidationErrors().addGlobalError(
					new LocalizableError("community.communityNotEmpty", trimTitle(object.getTitle(0))));
			}

			// check subscription before delete
			int sub = services.getSubscriptionCountByPid(community.getId());
			if (sub > 0) {
				services.deleteSubscriptionByPid(community.getId());
				services.deleteByQuery(MessageFormat.format("sub.pid:\"{0}\"", community.getId()));
			}
			if (context.getValidationErrors().size() > 0) {
				return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
			}
			services.purgeObject(community.getId(), "Deleted by " + user.getUsername());
			services.commit(false);

			// delete handle
			deleteHandle(community.getId());

			context.getMessages().add(new LocalizableMessage("community.deleteSuccess", trimTitle(object.getTitle(0))));
			return new ForwardResolution(uiPath + "/protected/editCommunityMessage.jsp");
		} catch (Exception e) {
			log.error("Could not delete this community!", e);
			return forwardExceptionError("Could not delete this community!", e);
		}
	}

	@HandlesEvent("getCommunitiesByName")
	@DontValidate
	public Resolution getCommunitiesByName() {
		try {
			QueryResponse response = services.findCommunitiesByName(0, ActionConstants.suggestionCount, name);
			communities = response.getBeans(Community.class);
		} catch (Exception e) {
			communities = new ArrayList<Community>();
		}
		return new ForwardResolution(uiPath + "/protected/communityAjax.jsp");
	}

	/**
	 * The getCommunity getter method.
	 * 
	 * @return the community
	 */
	public Community getCommunity() {
		return community;
	}

	/**
	 * The setCommunity setter method.
	 * 
	 * @param community the community to set
	 */
	public void setCommunity(Community community) {
		this.community = community;
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
	 * The getMode getter method.
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * The setMode setter method.
	 * 
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * The getProperties getter method.
	 * 
	 * @return the properties
	 */
	public List<String> getProperties() {
		return properties;
	}

	/**
	 * The setProperties setter method.
	 * 
	 * @param properties the properties to set
	 */
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	/**
	 * The getPartOfs getter method.
	 * 
	 * @return the partOfs
	 */
	public HashMap<Object, Boolean> getPartOfs() {
		return partOfs;
	}

	/**
	 * The setPartOfs setter method.
	 * 
	 * @param partOfs the partOfs to set
	 */
	public void setPartOfs(HashMap<Object, Boolean> partOfs) {
		this.partOfs = partOfs;
	}

	/**
	 * The getPartOfList getter method.
	 * 
	 * @return the partOfList
	 */
	public PartOfRelationship[] getPartOfList() {
		return partOfList;
	}

	/**
	 * The setPartOfList setter method.
	 * 
	 * @param partOfList the partOfList to set
	 */
	public void setPartOfList(PartOfRelationship[] partOfList) {
		this.partOfList = partOfList;
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
	 * The getCommunities getter method.
	 * 
	 * @return the communities
	 */
	public List<Community> getCommunities() {
		return communities;
	}
}
