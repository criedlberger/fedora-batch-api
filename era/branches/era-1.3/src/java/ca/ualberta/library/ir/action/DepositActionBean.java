/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: DepositActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.LICENSE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.thumbnail.ThumbnailGenerator;

import fedora.common.Constants;
import fedora.server.types.gen.DatastreamDef;

import ca.ualberta.library.ir.domain.Handle;
import ca.ualberta.library.ir.domain.License;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.AccessType;
import ca.ualberta.library.ir.enums.ContentModel;
import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.DublinCore;
import ca.ualberta.library.ir.model.fedora.DublinCoreField;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.PDFUtils;

/**
 * The CreateActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/deposit/{$event}/{item.properties.pid}")
public class DepositActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(DepositActionBean.class);

	// the item
	private Item item;

	// selected owners
	private List<String> usernames;

	// selected communities
	private List<String> coms;

	private List<Community> comList;

	// selected collections
	private List<String> cols;

	// remove datastream ids
	private List<String> removeDsIds;

	// remove license id
	private String removeLcId;

	// selected license id
	private int licenseId;

	// uploaded license file
	private FileBean licenseFile;

	// license text
	private String licenseText;

	// uploaded files
	private List<FileBean> files;

	// remove file ids
	private List<String> removeFileIds;

	private Item oldItem;

	private List<FileBean> uploadedFiles;

	private List<String> dsIds;

	private FileBean uploadedLicense;

	private String url;

	private String pid;

	private String name;

	private List<User> owners;

	private List<Community> communities;

	/**
	 * The CreateActionBean class constructor.
	 */
	public DepositActionBean() {
		super();
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return item.getProperties().getPid();
	}

	@Before(on = { "init", "edit" }, stages = LifecycleStage.HandlerResolution)
	public void setDropDownLists() {
		try {

			// refresh communities and collection list
			context.setAllCommunities(services.getAllCommunities());
			context.setAllCollections(services.getAllCollections());

		} catch (Exception e) {
			log.error("Could not get all communities add/or collection!", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Before(on = { "save", "submit" }, stages = LifecycleStage.HandlerResolution)
	public void setFlashScopeVariables() {
		try {

			// get previous flash scope variables from request
			oldItem = (Item) request.getAttribute("item");
			uploadedFiles = (List<FileBean>) request.getAttribute("files");
			uploadedLicense = (FileBean) request.getAttribute("license");

		} catch (Exception e) {
			log.error("Could not get flash scope variable", e);
		}
	}

	@ValidationMethod(on = { "save", "submit" })
	public void validate(ValidationErrors errors) {
		try {

			// validate owners
			if (usernames != null) {
				List<User> users = new ArrayList<User>();
				boolean err = false;
				for (String username : usernames) {
					User user = services.getUser(username);
					if (user != null) {
						users.add(services.getUser(username));
					} else {
						errors.addGlobalError(new LocalizableError("ownerInvalid", username));
						if (!err) {
							errors.add("owner", new LocalizableError("invalid"));
							err = true;
						}
					}
				}
				item.getProperties().setOwners(users);
				context.setOwners(users);
			} else {
				context.setOwners(null);
			}

		} catch (Exception e) {
			log.error("Valication process error!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}
	}

	@ValidationMethod(on = { "submit" })
	public void validateSubmit(ValidationErrors errors) {
		// log.debug("validating submit event...");
		try {

			// validate flash scope item
			if (oldItem == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
				return;
			}

			// validate item metadata
			if (item.getProperties().getContentModel() == null) {
				errors.addGlobalError(new LocalizableError("typeRequired"));
				errors.add("item.properties.contentModel", new LocalizableError("required"));
			}
			if (item.getDublinCore().getFields().get(1).getValues() == null) {
				errors.addGlobalError(new LocalizableError("titleRequired"));
				errors.add("item.dublinCore.fields[1].values[0]", new LocalizableError("required"));
			}
			if (item.getDublinCore().getFields().get(2).getValues() == null) {
				errors.addGlobalError(new LocalizableError("authorRequired"));
				errors.add("item.dublinCore.fields[2].values", new LocalizableError("required"));
			}
			if (item.getDublinCore().getFields().get(3).getValues() == null) {
				errors.addGlobalError(new LocalizableError("subjectRequired"));
				errors.add("item.dublinCore.fields[3].values", new LocalizableError("required"));
			}
			if (item.getDublinCore().getFields().get(12).getValues() != null) {
				if (item.getDublinCore().getFields().get(12).getValues().get(1) != null) {
					if (!isValidUrl(item.getDublinCore().getFields().get(12).getValues().get(1))) {
						errors.addGlobalError(new LocalizableError("relatedItemLinkInvalid"));
						errors.add("item.dublinCore.fields[12].values[1]", new LocalizableError("invalid"));
					}
				}
			}

			// validate community
			if (coms == null) {
				errors.addGlobalError(new LocalizableError("communityRequired"));
				errors.add("item.dublinCore.fields[12].values[0]", new LocalizableError("invalid"));
				errors.add("coms", new LocalizableError("required"));
			}

			// validate community list
			List<Community> comList = new ArrayList<Community>();
			if (coms != null) {
				boolean err = false;
				for (String id : coms) {
					try {
						Community com = services.getCommunity(id);
						comList.add(com);
						if (com.isApproval()) {
							item.getProperties().setManualApproval(true);
						}
					} catch (Exception e) {
						log.error("Could not find community!", e);
						errors.addGlobalError(new LocalizableError("communityInvalid", id));
						if (!err) {
							errors.add("coms", new LocalizableError("invalid"));
							err = true;
						}
					}
				}
			}
			item.setCommunities(comList);

			// validate collection list screen input
			List<Collection> colList = new ArrayList<Collection>();
			if (cols != null) {
				boolean err = false;
				for (String id : cols) {
					try {
						Collection col = services.getCollection(id);
						colList.add(col);
						if (col.isApproval()) {
							item.getProperties().setManualApproval(true);
						}
					} catch (Exception e) {
						log.error("Could not find collection!", e);
						errors.addGlobalError(new LocalizableError("collectionInvalid", id));
						if (!err) {
							errors.add("cols", new LocalizableError("invalid"));
							err = true;
						}
					}
				}
			}
			item.setCollections(colList);

			// check selected license
			if (licenseId > 0) {
				licenseFile = null;
				licenseText = null;
			} else if (licenseFile != null) {
				licenseId = 0;
				licenseText = null;
			} else if (licenseText != null) {
				licenseId = 0;
				licenseFile = null;
			}

			// remove uploaded license
			if (removeLcId != null) {
				uploadedLicense = null;
			}

			// restore licenseFile from flash scope
			if (licenseFile == null && licenseId == 0 && licenseText == null) {
				licenseFile = uploadedLicense;
			}

			// check license
			if (licenseFile == null && licenseId == 0 && licenseText == null && oldItem.getLicense() == null) {
				errors.addGlobalError(new LocalizableError("licenseRequired"));
				errors.add("licenseId", new LocalizableError("required"));
			}

			// check licenseFile file type
			if (licenseFile != null && !supportedContentTypes.matcher(licenseFile.getContentType()).matches()
				&& !PDFUtils.isPDFDocument(licenseFile.getInputStream())) {
				errors.addGlobalError(new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile
					.getContentType()));
				errors.add("licenseFile", new LocalizableError("fileTypeInvalid", licenseFile.getFileName(),
					licenseFile.getContentType()));
			}

			// validate embargoed
			if (item.getProperties().isEmbargoed()) {
				try {
					Date edate = embargoedDateFormat.parse(item.getProperties().getEmbargoedDate());
					if (edate.before(new Date())) {
						errors.addGlobalError(new LocalizableError("displayDateInvalid"));
						errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
					}
				} catch (Exception e) {
					errors.addGlobalError(new LocalizableError("displayDateInvalid"));
					errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
				}
			}

			// validate file type
			if (files != null) {
				List<FileBean> fbs = new ArrayList<FileBean>();
				for (FileBean file : files) {
					if (file != null) {

						// check file size
						if (file.getSize() > uploadFileSize * 1048576) {
							errors.addGlobalError(new LocalizableError("uploadFileTooBig", file.getFileName(),
								ActionConstants.uploadFileSize));
							errors.add("files", new LocalizableError("uploadFileTooBig", file.getFileName(),
								ActionConstants.uploadFileSize));
						} else

						// check file contentType
						if (!supportedContentTypes.matcher(file.getContentType()).matches()
							&& !PDFUtils.isPDFDocument(file.getInputStream())) {
							errors.addGlobalError(new LocalizableError("fileTypeInvalid", file.getFileName(), file
								.getContentType()));
							errors.add("files", new LocalizableError("fileTypeInvalid", file.getFileName(), file
								.getContentType()));
						} else {
							fbs.add(file);
						}
					}
				}
				files = fbs;
			}

			// restore file uploaded from flash scope
			List<FileBean> undeleteds = new ArrayList<FileBean>();
			if (removeFileIds != null) {
				for (int i = 0; i < uploadedFiles.size(); i++) {
					if (!removeFileIds.contains(String.valueOf(i))) {
						undeleteds.add(uploadedFiles.get(i));
					}
				}
			} else if (uploadedFiles != null) {
				undeleteds = uploadedFiles;
			}

			if (files == null || files.isEmpty()) {
				files = undeleteds;
			} else {
				files.addAll(undeleteds);
			}

			if ((files == null || files.isEmpty()) && dsIds == null) {
				errors.addGlobalError(new LocalizableError("uploadFileRequired"));
				errors.add("file", new LocalizableError("required"));
			}

			if (!errors.isEmpty()) {
				restoreFormFields();
			}
		} catch (Exception e) {
			log.error("Valication process error!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}

	}

	@ValidationMethod(on = { "save" })
	public void validateSave(ValidationErrors errors) {
		try {

			// log.debug("validating save event...");

			// validate flash scope item
			if (oldItem == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
				return;
			}

			if (item.getDublinCore().getFields().get(1).getValues() == null) {
				errors.addGlobalError(new LocalizableError("titleRequired"));
				errors.add("item.dublinCore.fields[1].values[0]", new LocalizableError("required"));
			}

			// check selected license
			if (licenseId > 0) {
				licenseFile = null;
				licenseText = null;
			} else if (licenseFile != null) {
				licenseId = 0;
				licenseText = null;
			} else if (licenseText != null) {
				licenseId = 0;
				licenseFile = null;
			}

			// remove uploaded license
			if (removeLcId != null) {
				uploadedLicense = null;
			}

			// restore licenseFile from flash scope
			if (licenseFile == null && licenseId == 0 && licenseText == null) {
				licenseFile = uploadedLicense;
			}

			// check licenseFile file type
			if (licenseFile != null && !supportedContentTypes.matcher(licenseFile.getContentType()).matches()
				&& !PDFUtils.isPDFDocument(licenseFile.getInputStream())) {
				errors.addGlobalError(new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile
					.getContentType()));
				errors.add("licenseFile", new LocalizableError("fileTypeInvalid", licenseFile.getFileName(),
					licenseFile.getContentType()));
			}

			// validate file type
			if (files != null) {
				List<FileBean> fbs = new ArrayList<FileBean>();
				for (FileBean file : files) {
					if (file != null) {

						// check file size
						if (file.getSize() > uploadFileSize * 1048576) {
							errors.addGlobalError(new LocalizableError("uploadFileTooBig", file.getFileName(),
								ActionConstants.uploadFileSize));
							errors.add("files", new LocalizableError("uploadFileTooBig", file.getFileName(),
								ActionConstants.uploadFileSize));
						} else

						// check file contentType
						if (!supportedContentTypes.matcher(file.getContentType()).matches()
							&& !PDFUtils.isPDFDocument(file.getInputStream())) {
							errors.addGlobalError(new LocalizableError("fileTypeInvalid", file.getFileName(), file
								.getContentType()));
							errors.add("files", new LocalizableError("fileTypeInvalid", file.getFileName(), file
								.getContentType()));
						} else {
							fbs.add(file);
						}
					}
				}
				files = fbs;
			}

			// restore file uploaded from flash scope
			List<FileBean> undeleteds = new ArrayList<FileBean>();
			if (removeFileIds != null) {
				for (int i = 0; i < uploadedFiles.size(); i++) {
					if (!removeFileIds.contains(String.valueOf(i))) {
						undeleteds.add(uploadedFiles.get(i));
					}
				}
			} else if (uploadedFiles != null) {
				undeleteds = uploadedFiles;
			}

			if (files == null || files.isEmpty()) {
				files = undeleteds;
			} else {
				files.addAll(undeleteds);
			}

			if (!errors.isEmpty()) {
				restoreFormFields();
			}

		} catch (Exception e) {
			log.error("Valication process error!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}

	}

	public void restoreFormFields() {
		try {

			// restore item license
			if (licenseId == 0 && licenseFile == null && licenseText == null) {
				item.setLicense(oldItem.getLicense());
			}

			// set datastream screen input
			item.setDatastreams(oldItem.getDatastreams());
			if (removeDsIds != null) {
				for (Datastream ds : item.getDatastreams()) {
					if (removeDsIds.contains(ds.getDsId())) {
						ds.setState(State.D.toString());
					} else {
						ds.setState(State.A.toString());
					}
				}
			}

			// store flash variables for next request
			FlashScope flash = getFlashScope();
			flash.put("item", oldItem);
			flash.put("license", licenseFile);
			flash.put("files", files);

		} catch (Exception e) {
			log.error("Could not restore form fields!", e);
		}
	}

	@DefaultHandler
	@DontValidate
	@HandlesEvent("init")
	@Secure(roles = "/item/create")
	public Resolution init() {
		try {

			// initialize item
			item = new Item();
			Properties properties = new Properties();
			properties.setOwners(context.getOwners());
			properties.setAccessType(AccessType.PUBLIC);
			item.setProperties(properties);
			item.setDublinCore(new DublinCore());
			item.setDatastreams(new ArrayList<Datastream>());
			item.setCollections(new ArrayList<Collection>());
			item.setCommunities(new ArrayList<Community>());

			// save to flash scope
			getFlashScope().put("item", item);

			return new ForwardResolution(uiPath + "/protected/deposit.jsp");
		} catch (Exception e) {
			return forwardExceptionError("Could not edit the item!", e);
		}
	}

	@HandlesEvent("getOwnersByName")
	@DontValidate
	public Resolution getOwnersByName() {
		try {
			owners = services.getUsersByName(name);
			return new ForwardResolution(uiPath + "/protected/depositAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@DontValidate
	@HandlesEvent("getCollectionList")
	public Resolution getCollectionList() {
		try {
			if (coms != null) {
				communities = services.getCommunitiesByPids(coms).getBeans(Community.class);
				for (Community com : communities) {
					com.setCollections(services.getCommunityMemberCollections(com.getId()));
				}
			}
			return new ForwardResolution(uiPath + "/protected/depositAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@DontValidate
	@HandlesEvent("edit")
	@Secure(roles = "/item/create,/object/owner")
	public Resolution edit() {
		try {

			// get saved item details
			item = getItemByPid(item.getProperties().getPid());

			// check item state
			if (State.I.toString().equals(item.getProperties().getState())
				&& (WorkflowState.Initial.toString().equals(item.getProperties().getWorkflowState()) || WorkflowState.Reject
					.toString().equals(item.getProperties().getWorkflowState()))) {

				// save item to flash scope
				getFlashScope().put("item", item);
				return new ForwardResolution(uiPath + "/protected/deposit.jsp");

			} else {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("itemStatusError", item.getProperties().getLabel(), State.valueOf(
						item.getProperties().getState()).getName(),
						item.getProperties().getWorkflowState() == null ? "" : item.getProperties().getWorkflowState()
							.toString()));
				return new ForwardResolution(uiPath + "/protected/depositMessage.jsp");
			}

		} catch (Exception e) {
			return forwardExceptionError("Could not edit the item!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/item/create")
	public Resolution save() {
		try {
			// log.debug("saving item...");
			pid = item.getProperties().getPid();

			// set label
			item.getProperties().setLabel(
				item.getDublinCore().getFields().get(1).getValues() != null ? item.getDublinCore().getFields().get(1)
					.getValues().get(0) : "");

			// set owners
			if (usernames == null) {
				item.getProperties().setOwnerId(user.getUsername());
			} else if (isUserInRoles(user, SystemPermissions.ADMIN_DEPOSITOR.getPermission())) {
				StringBuilder ownerIds = new StringBuilder();
				for (String username : usernames) {
					ownerIds.append(ownerIds.length() > 0 ? "," : "").append(username);
				}
				item.getProperties().setOwnerId(ownerIds.toString());
			} else {
				item.getProperties().setOwnerId(user.getUsername());
			}

			// set item state
			item.getProperties().setState(State.I.toString());

			if (pid != null) {
				// modified object
				services.modifyObject(item.getProperties(), formatLogMessage(WorkflowState.Initial));
			} else {
				// create object
				pid = services.ingest(item.getProperties(), formatLogMessage(WorkflowState.Initial));
			}
			// log.debug("pid: " + pid);

			// save contentModel
			if (item.getProperties().getContentModel() != null
				&& !item.getProperties().getContentModel().equals(oldItem.getProperties().getContentModel())) {
				if (oldItem.getProperties().getContentModel() != null) {
					services.purgeRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
						+ oldItem.getProperties().getContentModel(), false, null);
				}
				services.addRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
					+ item.getProperties().getContentModel(), false, null);
			}

			if (oldItem.getLicense() != null && (licenseId > 0 || licenseFile != null || licenseText != null)) {
				// purge old license
				services.purgeDatastream(pid, LICENSE.toString(), formatLogMessage(WorkflowState.Initial));
			}

			if (licenseId > 0) {

				// add license datastram from database
				License license = services.getLicense(licenseId);
				ByteArrayInputStream bis = new ByteArrayInputStream(license.getContents());
				services.addDatastream(pid, LICENSE.toString(), license.getTitle(), license.getMimeType(), bis,
					formatLogMessage(WorkflowState.Initial));

			} else if (licenseFile != null) {

				// add license datastream from file
				services
					.addDatastream(pid, LICENSE.toString(), licenseFile.getFileName(), licenseFile.getContentType(),
						licenseFile.getInputStream(), formatLogMessage(WorkflowState.Initial));
				licenseFile.delete();

			} else if (licenseText != null) {

				// add license datastream from text
				ByteArrayInputStream bis = new ByteArrayInputStream(licenseText.getBytes());
				services.addDatastream(pid, LICENSE.toString(), trimTitle(licenseText), TEXT_PLAIN, bis,
					formatLogMessage(WorkflowState.Initial));
			}

			// remove datastreams
			if (removeDsIds != null) {
				for (String dsId : removeDsIds) {
					services.purgeDatastream(pid, dsId, formatLogMessage(WorkflowState.Initial));
				}
			}

			// add new datastreams
			boolean thumbnail = oldItem.getThumbnail() != null;
			if (files != null) {
				for (FileBean file : files) {
					if (file != null) {
						Datastream datastream = new Datastream();
						datastream.setPid(pid);
						datastream.setLabel(file.getFileName());
						datastream.setData(file.getInputStream());
						datastream.setMimeType(file.getContentType());
						datastream.setSize(file.getSize());
						services.addDatastream(datastream, formatLogMessage(WorkflowState.Initial));
						if (!thumbnail && ThumbnailGenerator.canGenerate(file.getContentType())) {
							generateThumbnail(file, pid, WorkflowState.Submit);
							thumbnail = true;
						} else {
							file.delete();
						}
					}
				}
			}

			// restore dublin core fields from old item
			item.getDublinCore().getFields().set(0, oldItem.getDublinCore().getFields().get(0)); // identifier
			item.getDublinCore().getFields().set(5, oldItem.getDublinCore().getFields().get(5)); // publisher
			item.getDublinCore().getFields().set(14, oldItem.getDublinCore().getFields().get(14)); // rights

			// set dublin core: format
			DatastreamDef[] defs = services.listDatastreams(pid);
			for (DatastreamDef def : defs) {
				if (def.getID().startsWith(DatastreamID.DS.toString())) {
					item.getDublinCore().getFields().set(9,
						new DublinCoreField("format", Arrays.asList(def.getMIMEType()))); // format
					break;
				}
			}

			if (item.getProperties().getContentModel() != null) {
				// set dublin core: type of item
				String typeOfItem = applicationResources.getString("ContentModel."
					+ item.getProperties().getContentModel());
				item.getDublinCore().getFields().get(8).setValues(Arrays.asList(typeOfItem));
			}

			// save dublin core
			// log.debug(item.getDublinCore().parse().toString());
			services.modifyDublinCore(pid, item.getProperties().getLabel(), item.getDublinCore().parse().toString()
				.trim(), formatLogMessage(WorkflowState.Initial));

			// purge communities
			for (Community com : oldItem.getCommunities()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(), Constants.FEDORA.uri
					+ com.getId(), false, null);
			}

			// purge collections
			for (Collection col : oldItem.getCollections()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
					Constants.FEDORA.uri + col.getId(), false, null);
			}

			// purge part of relationships
			if (oldItem.getProperties().getAccessType().equals(AccessType.CCID_PROTECTED)) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.CCID_AUTH
					.getURI(), false, null);
			}
			if (oldItem.getProperties().getAccessType().equals(AccessType.NOONE)) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
			}
			if (oldItem.getProperties().isEmbargoed()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.EMBARGOED
					.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(oldItem.getProperties()
					.getEmbargoedDate()));
				services.purgeRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);
			}
			if (coms != null) {
				// update community
				for (String com : coms) {
					services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(), Constants.FEDORA.uri + com,
						false, null);
				}
			}

			if (cols != null) {
				// update collections
				for (String col : cols) {
					services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
						Constants.FEDORA.uri + col, false, null);
				}
			}

			// update ccid protected relationship
			if (item.getProperties().getAccessType().equals(AccessType.CCID_PROTECTED)) {
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.CCID_AUTH
					.getURI(), false, null);
			}

			// update dark repository relationship
			if (item.getProperties().getAccessType().equals(AccessType.NOONE)) {
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
			}

			// update embargoed relationship
			if (item.getProperties().isEmbargoed()) {
				// add embargoed relationship
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.EMBARGOED
					.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(item.getProperties()
					.getEmbargoedDate()));
				services.addRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);
			}

			if (oldItem.getProperties().getWorkflowState() == null) {

				// add workflow state (Initial)
				services.addRelationship(pid, Definitions.WORKFLOW_STATE.getURI(),
					WorkflowState.Initial.toString(), true, null);

				// add workflow date
				services.addRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), ISODateFormat.format(new Date()),
					true, Constants.RDF_XSD.DATE_TIME.uri);

				// add user id
				services.addRelationship(pid, Definitions.SUBMITTER_ID.getURI(), user.getUsername(), true, null);

			} else if (oldItem.getProperties().getWorkflowState().equals(WorkflowState.Initial.toString())) {

				// update workflow date
				services.purgeRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), oldItem.getProperties()
					.getWorkflowDate(), true, Constants.RDF_XSD.DATE_TIME.uri);
				services.addRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), ISODateFormat.format(new Date()),
					true, Constants.RDF_XSD.DATE_TIME.uri);

				// update submitter id
				if (!user.getUsername().equals(oldItem.getProperties().getSubmitterId())) {
					services.purgeRelationship(pid, Definitions.SUBMITTER_ID.getURI(), oldItem.getProperties()
						.getUserId(), true, null);
					services
						.addRelationship(pid, Definitions.SUBMITTER_ID.getURI(), user.getUsername(), true, null);
				}
			}

			// commit solr index in background
			services.commit(true);

			if (WorkflowState.Reject.toString().equals(oldItem.getProperties().getWorkflowState())) {
				context.getMessages().add(
					new LocalizableMessage("deposit.rejectedItemMessage", trimTitle(item.getProperties().getLabel()),
						pid));
			} else {
				context.getMessages()
					.add(
						new LocalizableMessage("deposit.savedItemMessage", trimTitle(item.getProperties().getLabel()),
							pid));
			}
			return new ForwardResolution(uiPath + "/protected/depositMessage.jsp");

		} catch (Exception e) {
			log.error("Could not save this item!", e);
			return forwardExceptionError("Could not save this item!", e);
		} finally {
			deleteTempFiles();
		}
	}

	@HandlesEvent("submit")
	@Secure(roles = "/item/create")
	public Resolution submit() {
		try {
			// log.debug("submitting item...");
			pid = item.getProperties().getPid();

			// set label
			item.getProperties().setLabel(item.getDublinCore().getFields().get(1).getValues().get(0));

			// set owners
			if (usernames == null) {
				item.getProperties().setOwnerId(user.getUsername());
			} else if (isUserInRoles(user, SystemPermissions.ADMIN_DEPOSITOR.getPermission())) {
				StringBuilder ownerIds = new StringBuilder();
				for (String username : usernames) {
					ownerIds.append(ownerIds.length() > 0 ? "," : "").append(username);
				}
				item.getProperties().setOwnerId(ownerIds.toString());
			} else {
				item.getProperties().setOwnerId(user.getUsername());
			}

			// set item state
			if (item.getProperties().isEmbargoed()) {
				item.getProperties().setState(State.I.toString());
			} else if (item.getProperties().getAccessType().equals(AccessType.NOONE)) {
				item.getProperties().setState(State.I.toString());
			} else if (item.getProperties().isManualApproval()) {
				item.getProperties().setState(State.I.toString());
			} else {
				item.getProperties().setState(State.A.toString());
			}

			if (pid != null) {
				// modified object
				services.modifyObject(item.getProperties(), formatLogMessage(WorkflowState.Submit));
			} else {
				// create object
				pid = services.ingest(item.getProperties(), formatLogMessage(WorkflowState.Submit));
			}
			// log.debug("pid: " + pid);

			// create handle
			Handle handle = saveHandle(pid, HandleType.ITEM);
			if (handle != null) {
				url = new StringBuilder(handleServer).append("/").append(buildHandle(handle.getId())).toString();
			} else {
				url = new StringBuilder(httpServerUrl).append(request.getContextPath()).append("/public/view/item/")
					.append(pid).toString();
			}

			// save contentModel
			if (!item.getProperties().getContentModel().equals(oldItem.getProperties().getContentModel())) {
				services.purgeRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
					+ oldItem.getProperties().getContentModel(), false, null);
				services.addRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
					+ item.getProperties().getContentModel(), false, null);
			}

			// check old license
			if (oldItem.getLicense() != null && (licenseId > 0 || licenseFile != null || licenseText != null)) {
				// purge old license
				services.purgeDatastream(pid, LICENSE.toString(), formatLogMessage(WorkflowState.Initial));
			}

			if (licenseId > 0) {

				// add license datastram from database
				License license = services.getLicense(licenseId);
				ByteArrayInputStream bis = new ByteArrayInputStream(license.getContents());
				services.addDatastream(pid, LICENSE.toString(), license.getTitle(), license.getMimeType(), bis,
					"Modified by " + user.getUsername());

			} else if (licenseFile != null) {

				// add license datastream from file
				services.addDatastream(pid, LICENSE.toString(), licenseFile.getFileName(),
					licenseFile.getContentType(), licenseFile.getInputStream(), formatLogMessage(WorkflowState.Submit));

			} else if (licenseText != null) {

				// add license datastream from text
				ByteArrayInputStream bis = new ByteArrayInputStream(licenseText.getBytes());
				services.addDatastream(pid, LICENSE.toString(), trimTitle(licenseText), TEXT_PLAIN, bis,
					"Modified by " + user.getUsername());
			}

			// remove datastreams
			if (removeDsIds != null) {
				for (String dsId : removeDsIds) {
					services.purgeDatastream(pid, dsId, formatLogMessage(WorkflowState.Submit));
				}
			}

			// add new datastreams
			boolean thumbnail = oldItem.getThumbnail() != null;
			if (files != null) {
				for (FileBean file : files) {
					if (file != null) {
						Datastream datastream = new Datastream();
						datastream.setPid(pid);
						datastream.setLabel(file.getFileName());
						datastream.setData(file.getInputStream());
						datastream.setMimeType(file.getContentType());
						datastream.setSize(file.getSize());
						services.addDatastream(datastream, formatLogMessage(WorkflowState.Submit));
						if (!thumbnail && ThumbnailGenerator.canGenerate(file.getContentType())) {
							generateThumbnail(file, pid, WorkflowState.Submit);
							thumbnail = true;
						} else {
							file.delete();
						}
					}
				}
			}

			// set dublin core identifier
			item.getDublinCore().getFields().set(0, new DublinCoreField("identifier", Arrays.asList(url)));

			// set dublin core: format
			String format = null;
			fedora.server.types.gen.Datastream[] dstms = services.getDatastreams(pid);
			for (fedora.server.types.gen.Datastream dstm : dstms) {
				if (dstm.getID().startsWith("DS")) {
					format = dstm.getMIMEType();
					break;
				}
			}
			item.getDublinCore().getFields().set(9, new DublinCoreField("format", Arrays.asList(format))); // format

			// set dublin core: type of item
			String typeOfItem = applicationResources
				.getString("ContentModel." + item.getProperties().getContentModel());
			item.getDublinCore().getFields().get(8).setValues(Arrays.asList(typeOfItem));

			// save dublin core
			// log.debug(item.getDublinCore().parse().toString());
			services.modifyDublinCore(pid, item.getProperties().getLabel(), item.getDublinCore().parse().toString()
				.trim(), formatLogMessage(WorkflowState.Submit));

			// purge communities
			for (Community com : oldItem.getCommunities()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(), Constants.FEDORA.uri
					+ com.getId(), false, null);
			}

			// purge collections
			for (Collection col : oldItem.getCollections()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
					Constants.FEDORA.uri + col.getId(), false, null);
			}

			// purge part of relationships
			if (oldItem.getProperties().getAccessType().equals(AccessType.CCID_PROTECTED)) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.CCID_AUTH
					.getURI(), false, null);
			}
			if (oldItem.getProperties().getAccessType().equals(AccessType.NOONE)) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
			}
			if (oldItem.getProperties().isEmbargoed()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.EMBARGOED
					.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(oldItem.getProperties()
					.getEmbargoedDate()));
				services.purgeRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);
			}
			if (oldItem.getProperties().isManualApproval()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.MANUAL_APPROVAL.getURI(), false, null);
			}

			// update community
			for (String com : coms) {
				services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(), Constants.FEDORA.uri + com,
					false, null);
			}

			// update collections
			if (cols != null) {
				for (String col : cols) {
					services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
						Constants.FEDORA.uri + col, false, null);
				}
			}

			// update ccid protected relationship
			if (item.getProperties().getAccessType().equals(AccessType.CCID_PROTECTED)) {
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.CCID_AUTH
					.getURI(), false, null);
			}

			// update dark repository relationship
			if (item.getProperties().getAccessType().equals(AccessType.NOONE)) {
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
			}

			// update embargoed relationship
			if (item.getProperties().isEmbargoed()) {
				// add embargoed relationship
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.EMBARGOED
					.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(item.getProperties()
					.getEmbargoedDate()));
				services.addRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);
			}

			// update manual approval relationship
			if (item.getProperties().isManualApproval()) {
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.MANUAL_APPROVAL.getURI(), false, null);
			}

			if (proaiEnabled) {
				if (oldItem.getProperties().getWorkflowState() == null
					|| WorkflowState.Initial.equals(oldItem.getProperties().getWorkflowState())) {
					services.addRelationship(pid, ApplicationProperties.getString("proai.fedora.itemID"), MessageFormat
						.format(proaiItemId, pid), true, null);
				}
			}

			// update workflow state
			if (oldItem.getProperties().getWorkflowState() != null) {

				// purge workflow state and date
				services.purgeRelationship(pid, Definitions.WORKFLOW_STATE.getURI(), oldItem.getProperties()
					.getWorkflowState(), true, null);
				services.purgeRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), oldItem.getProperties()
					.getWorkflowDate(), true, Constants.RDF_XSD.DATE_TIME.uri);
			}

			// add workflow state (Submit)
			services.addRelationship(pid, Definitions.WORKFLOW_STATE.getURI(), item.getProperties()
				.isManualApproval() ? WorkflowState.Submit.toString() : WorkflowState.Archive.toString(), true, null);

			// update workflow date
			services.addRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), ISODateFormat.format(new Date()),
				true, Constants.RDF_XSD.DATE_TIME.uri);

			// update user id
			if (oldItem.getProperties().getUserId() != null) {
				if (!user.getUsername().equals(oldItem.getProperties().getUserId())) {
					services.purgeRelationship(pid, Definitions.USER_ID.getURI(), oldItem.getProperties()
						.getUserId(), true, null);
					services.addRelationship(pid, Definitions.USER_ID.getURI(), user.getUsername(), true, null);
				}
			} else {
				services.addRelationship(pid, Definitions.USER_ID.getURI(), user.getUsername(), true, null);
			}

			// update submitter id
			if (oldItem.getProperties().getSubmitterId() != null) {
				if (!user.getUsername().equals(oldItem.getProperties().getSubmitterId())) {
					services.purgeRelationship(pid, Definitions.SUBMITTER_ID.getURI(), oldItem.getProperties()
						.getUserId(), true, null);
					services
						.addRelationship(pid, Definitions.SUBMITTER_ID.getURI(), user.getUsername(), true, null);
				}
			} else {
				services.addRelationship(pid, Definitions.SUBMITTER_ID.getURI(), user.getUsername(), true, null);
			}

			// commit item index and send deposit success email in backgroud process
			sendMail();

			// set response message
			if (item.getProperties().isManualApproval()) {
				context.getMessages().add(
					new LocalizableMessage("deposit.pendingMessage", trimTitle(item.getProperties().getLabel()), url));
			} else {
				context.getMessages().add(
					new LocalizableMessage("deposit.successMessage", trimTitle(item.getProperties().getLabel()), url));
			}

			// log.debug("finish! successfully deposited.");
			return new ForwardResolution(uiPath + "/protected/depositMessage.jsp");

		} catch (Exception e) {
			log.error("Could not save this item!", e);
			return forwardExceptionError("Could not process this request!", e);
		} finally {
			deleteTempFiles();
		}
	}

	@HandlesEvent("remove")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid")
	public Resolution remove() {
		try {

			item = getItemByPid(item.getProperties().getPid());
			if (item.getProperties().getState().equals(State.I.toString())
				&& (item.getProperties().getWorkflowState().equals(WorkflowState.Initial.toString()) || item
					.getProperties().getWorkflowState().equals(WorkflowState.Reject.toString()))) {

				// purge object
				services.purgeObject(item.getProperties().getPid(), formatLogMessage(WorkflowState.Initial));
				services.commit();
				context.getMessages().add(
					new LocalizableMessage("item.removeSuccess", trimTitle(item.getProperties().getLabel())));
			} else {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("removeItemError", item.getProperties().getLabel(), item.getProperties()
						.getWorkflowState()));
			}
			return new ForwardResolution(uiPath + "/protected/depositMessage.jsp");
		} catch (Exception e) {
			log.error("Could not delete this item!", e);
			return forwardExceptionError("Could not delete this item!", e);
		}
	}

	private void sendMail() {

		// send email to user in a new thread
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {

					// commit solr index
					try {
						services.commit(false);
					} catch (Exception ex) {
						log.error("Could not commit item index!", ex);
					}

					if (item.getProperties().isManualApproval()) {

						// get submission mails to reviewers
						List<User> users = services.getUsersByGroupPermission(SystemPermissions.ADMIN_APPROVE
							.getPermission());

						String subject = applicationResources.getString("mail.submit.subject");
						String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
							+ "/action/admin/approval";
						Map<String, Object> itm = new HashMap<String, Object>();
						itm.put("doc", services.findObjectByPid(pid).getResults().get(0));
						itm.put("collections", item.getCollections());
						itm.put("communities", item.getCommunities());
						mailServiceManager.sendSubmissionMail(users, user, subject, itm, url);

					} else {

						// send deposit success mail
						String subject = applicationResources.getString("mail.deposit.subject");
						Map<String, Object> itm = new HashMap<String, Object>();
						itm.put("doc", services.findObjectByPid(pid).getResults().get(0));
						itm.put("collections", item.getCollections());
						itm.put("communities", item.getCommunities());
						mailServiceManager.sendDepositMail(user, subject, itm, url);
					}
				} catch (Exception e) {
					log.error("Could not send email!", e);
				}
			}
		});
		thread.start();
	}

	private void deleteTempFiles() {

		// clean up temp files
		if (licenseFile != null) {
			try {
				licenseFile.delete();
			} catch (IOException e) {
				log.warn("Could not delete license temp file! (" + licenseFile.toString() + ")", e);
			}
		}
		if (files != null) {
			for (FileBean file : files) {
				try {
					file.delete();
				} catch (IOException e) {
					log.warn("Could not delete license temp file! (" + file.toString() + ")", e);
				}
			}
		}
	}

	/**
	 * The getItem getter method.
	 * 
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * The setItem setter method.
	 * 
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * The getUsernames getter method.
	 * 
	 * @return the usernames
	 */
	public List<String> getUsernames() {
		return usernames;
	}

	/**
	 * The setUsernames setter method.
	 * 
	 * @param usernames the usernames to set
	 */
	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	/**
	 * The getRemoveDsIds getter method.
	 * 
	 * @return the removeDsIds
	 */
	public List<String> getRemoveDsIds() {
		return removeDsIds;
	}

	/**
	 * The setRemoveDsIds setter method.
	 * 
	 * @param removeDsIds the removeDsIds to set
	 */
	public void setRemoveDsIds(List<String> removeDsIds) {
		this.removeDsIds = removeDsIds;
	}

	/**
	 * The getComs getter method.
	 * 
	 * @return the coms
	 */
	public List<String> getComs() {
		return coms;
	}

	/**
	 * The setComs setter method.
	 * 
	 * @param coms the coms to set
	 */
	public void setComs(List<String> coms) {
		this.coms = coms;
	}

	/**
	 * The getCols getter method.
	 * 
	 * @return the cols
	 */
	public List<String> getCols() {
		return cols;
	}

	/**
	 * The setCols setter method.
	 * 
	 * @param cols the cols to set
	 */
	public void setCols(List<String> cols) {
		this.cols = cols;
	}

	/**
	 * The getLicenseId getter method.
	 * 
	 * @return the licenseId
	 */
	public int getLicenseId() {
		return licenseId;
	}

	/**
	 * The setLicenseId setter method.
	 * 
	 * @param licenseId the licenseId to set
	 */
	public void setLicenseId(int licenseId) {
		this.licenseId = licenseId;
	}

	/**
	 * The getLicenseFile getter method.
	 * 
	 * @return the licenseFile
	 */
	public FileBean getLicenseFile() {
		return licenseFile;
	}

	/**
	 * The setLicenseFile setter method.
	 * 
	 * @param licenseFile the licenseFile to set
	 */
	public void setLicenseFile(FileBean licenseFile) {
		this.licenseFile = licenseFile;
	}

	/**
	 * The getLicenseText getter method.
	 * 
	 * @return the licenseText
	 */
	public String getLicenseText() {
		return licenseText;
	}

	/**
	 * The setLicenseText setter method.
	 * 
	 * @param licenseText the licenseText to set
	 */
	public void setLicenseText(String licenseText) {
		this.licenseText = licenseText;
	}

	/**
	 * The getFiles getter method.
	 * 
	 * @return the files
	 */
	public List<FileBean> getFiles() {
		return files;
	}

	/**
	 * The setFiles setter method.
	 * 
	 * @param files the files to set
	 */
	public void setFiles(List<FileBean> files) {
		this.files = files;
	}

	/**
	 * The getRemoveFileIds getter method.
	 * 
	 * @return the removeFileIds
	 */
	public List<String> getRemoveFileIds() {
		return removeFileIds;
	}

	/**
	 * The setRemoveFileIds setter method.
	 * 
	 * @param removeFileIds the removeFileIds to set
	 */
	public void setRemoveFileIds(List<String> removeFileIds) {
		this.removeFileIds = removeFileIds;
	}

	/**
	 * The getComList getter method.
	 * 
	 * @return the comList
	 */
	public List<Community> getComList() {
		return comList;
	}

	/**
	 * The setComList setter method.
	 * 
	 * @param comList the comList to set
	 */
	public void setComList(List<Community> comList) {
		this.comList = comList;
	}

	/**
	 * The getRemoveLcId getter method.
	 * 
	 * @return the removeLcId
	 */
	public String getRemoveLcId() {
		return removeLcId;
	}

	/**
	 * The setRemoveLcId setter method.
	 * 
	 * @param removeLcId the removeLcId to set
	 */
	public void setRemoveLcId(String removeLcId) {
		this.removeLcId = removeLcId;
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
	 * The getOwners getter method.
	 * 
	 * @return the owners
	 */
	public List<User> getOwners() {
		return owners;
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
	 * The getUploadedDsIds getter method.
	 * 
	 * @return the dsIds
	 */
	public List<String> getDsIds() {
		return dsIds;
	}

	/**
	 * The setUploadedDsIds setter method.
	 * 
	 * @param dsIds the dsIds to set
	 */
	public void setDsIds(List<String> dsIds) {
		this.dsIds = dsIds;
	}
}