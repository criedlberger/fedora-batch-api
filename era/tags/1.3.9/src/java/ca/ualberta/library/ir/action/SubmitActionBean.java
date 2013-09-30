/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SubmitActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import static ca.ualberta.library.ir.enums.DatastreamID.LICENSE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.thumbnail.ThumbnailGenerator;

import fedora.common.Constants;
import fedora.utilities.Foxml11Document.State;

import ca.ualberta.library.ir.domain.Handle;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.AccessType;
import ca.ualberta.library.ir.enums.ContentModel;
import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.SubscriptionType;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.exception.FedoraAPIException;
import ca.ualberta.library.ir.exception.ServiceException;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.inputform.Field;
import ca.ualberta.library.ir.model.inputform.Form;
import ca.ualberta.library.ir.model.inputform.Form.Name;
import ca.ualberta.library.ir.model.inputform.ValuePairs;
import ca.ualberta.library.ir.model.metadata.Attribute;
import ca.ualberta.library.ir.model.metadata.Field.Element;
import ca.ualberta.library.ir.model.metadata.Field.Key;
import ca.ualberta.library.ir.model.metadata.Metadata;
import ca.ualberta.library.ir.model.metadata.MetadataTransformer;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.PDFUtils;
import ca.ualberta.library.ir.utils.ThreadPool;

/**
 * The CreateActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/submit/{$event}/{form}/{pid}")
public class SubmitActionBean extends BaseActionBean {

	private static final Log log = LogFactory.getLog(SubmitActionBean.class);

	// input form name
	private String form;

	// item pid, collection pid to submit
	private String pid;

	// metadata input form variables
	private Form inputForm;

	private List<Field> inputFields;

	private Map<String, ValuePairs> valuePairsMap;

	private List<ca.ualberta.library.ir.model.metadata.Field> fields;

	private Map<String, List<ca.ualberta.library.ir.model.metadata.Field>> fieldMap;

	// licenses
	private List<ca.ualberta.library.ir.model.solr.License> licenses;

	// the item
	private Item item;

	// selected owners
	private List<String> usernames;

	// selected communities
	private List<String> coms;

	// selected collections
	private List<String> cols;

	// remove datastream ids
	private List<String> removeDsIds;

	// remove license id
	private String removeLcId;

	// selected license id
	private String licenseId;

	// uploaded license file
	private FileBean licenseFile;

	// license text
	private String licenseText;

	// accept licence
	private boolean accepted;

	// uploaded files
	private List<FileBean> files;

	// remove file ids
	private List<String> removeFileIds;

	// item datastream ids
	private List<String> dsIds;

	// item url or handle
	private String url;

	// item owners
	private List<User> owners;

	// communities form field
	private List<Community> communities;

	// init community list
	private List<Community> communityList;

	// item metadata
	private Metadata metadata;

	// flash scope item
	private Item flashItem;

	// flash scope uploaded file
	private List<FileBean> flashFiles;

	// flash scope uploaded license
	private FileBean flashLicense;

	private boolean proquestUpload;

	private Date currentDate;

	private FileBean thumbnailFile;

	/**
	 * The CreateActionBean class constructor.
	 */
	public SubmitActionBean() {
		super();
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return pid;
	}

	@Before(on = { "remove", "removerejected" }, stages = LifecycleStage.EventHandling)
	public void initForm() {
		form = form == null ? Name.DEFAULT.toString() : form;
	}

	@Before(on = { "init", "edit", "editsaved", "editrejected", "editembargoed" }, stages = LifecycleStage.EventHandling)
	public void initCommunityList() {
		try {
			// log.debug("@before state: " + LifecycleStage.HandlerResolution);
			form = form == null ? Name.DEFAULT.toString() : form;

			// init communities and collection list
			if (context.getCCIDUser() == null) {
				communityList = services.getPublicCommunities();
			} else {
				communityList = services.getCommunitiesByFormName(form);
			}

			// init license list
			licenses = services.getLicensesByFromName(form);

		} catch (Exception e) {
			log.error("Could not get all communities add/or collection!", e);
		}
	}

	@Before(on = { "init" }, stages = { LifecycleStage.EventHandling })
	public void initInputForm() {
		// log.debug("@before on: " + context.getEventName() + " state: " + LifecycleStage.EventHandling);
		try {
			initMetadataInputForm();
		} catch (Exception e) {
			log.error("Could not initialize metadata input form!", e);
		}
	}

	@Before(on = { "edit", "editsaved", "editrejected", "editembargoed" }, stages = { LifecycleStage.EventHandling })
	public void initItem() {
		// log.debug("@before on: " + context.getEventName() + " state: " + LifecycleStage.EventHandling);
		try {
			item = getItemByPid(pid);
		} catch (Exception e) {
			log.error("Could not initialize metadata input form!", e);
		}
	}

	/**
	 * The initTestItem method.
	 * XXX: Comment @After annotation for production deployment.
	 */

	// @After(on = { "init" }, stages = { LifecycleStage.EventHandling })
	public void initTestItem() {
		try {
			// XXX: TEST THESIS ITEM
			// log.debug("@after on: " + context.getEventName() + " state: " + LifecycleStage.EventHandling);

			// transform DCQ to Metadata object
			metadata = MetadataTransformer
				.datastream2metadata(servletContext.getResourceAsStream(form.equals("thesis") ? "/era_test_ethesis.xml"
					: "/era_test_basic.xml"));

			// get fieldMap and inputForm for jsp
			fieldMap = metadata.getFieldMap();

		} catch (Exception e) {
			log.error("Could not initailize test item!", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Before(on = { "save", "submit", "modify" }, stages = LifecycleStage.CustomValidation)
	public void initFlashVariables() {
		try {
			// log.debug("@before on: " + context.getEventName() + " state: " + LifecycleStage.HandlerResolution);

			// init current date
			currentDate = new Date();

			// init flash scope variables from request
			flashItem = (Item) request.getAttribute("item");
			flashFiles = (List<FileBean>) request.getAttribute("files");
			flashLicense = (FileBean) request.getAttribute("license");

		} catch (Exception e) {
			log.error("Could not get flash scope variable", e);
		}
	}

	@Before(on = { "save", "submit", "modify" }, stages = { LifecycleStage.CustomValidation })
	public void initItemForm() {
		// log.debug("@before on: " + context.getEventName() + " state: " + LifecycleStage.CustomValidation);
		try {
			// init metadata input form
			initMetadataInputForm();

			// remove all null fields
			CollectionUtils.filter(fields, NotNullPredicate.getInstance());

			// create metadata input form
			metadata = new Metadata(fields);

			// get fieldMap for form
			fieldMap = metadata.getFieldMap();

			// set item metadata
			item.setMetadata(metadata);

			// set item communities
			List<Community> comList = new ArrayList<Community>();
			if (coms != null) {
				for (String id : coms) {
					try {
						Community com = services.getCommunity(id);
						comList.add(com);
						if (com.isApproval()) {
							item.getProperties().setManualApproval(true);
						}
					} catch (Exception e) {
						log.error("Could not find community: " + id + "!", e);
					}
				}
			}
			item.setCommunities(comList);

			// set item collections
			List<Collection> colList = new ArrayList<Collection>();
			if (cols != null) {
				for (String id : cols) {
					try {
						Collection col = services.getCollection(id);
						colList.add(col);
						if (col.isApproval()) {
							item.getProperties().setManualApproval(true);
						}
					} catch (Exception e) {
						log.error("Could not find collection: " + id + "!", e);
					}
				}
			}
			item.setCollections(colList);
		} catch (Exception e) {
			log.error("Could not initialize item form!", e);
		}
	}

	@Before(on = { "save", "submit", "modify" }, stages = LifecycleStage.ResolutionExecution)
	public void restoreFormFields() {
		try {
			// log.debug("@before on: " + context.getEventName() + " state: " + LifecycleStage.ResolutionExecution);

			// init communities and collection list
			if (context.getCCIDUser() == null) {
				communityList = services.getPublicCommunities();
			} else {
				communityList = services.getCommunitiesByFormName(form);
			}

			if (!context.getValidationErrors().isEmpty()) {

				// restore item license
				licenses = services.getLicensesByFromName(form == null ? Name.DEFAULT.toString() : form);

				if (licenseId == null && licenseFile == null && licenseText == null) {
					item.setLicense(flashItem.getLicense());
				}

				// set datastream screen input
				item.setDatastreams(flashItem.getDatastreams());
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
				flash.put("item", flashItem);
				flash.put("license", licenseFile);
				flash.put("files", files);
			}
		} catch (Exception e) {
			log.error("Could not restore form fields!", e);
		}
	}

	@ValidationMethod(on = { "save", "submit", "modify" })
	public void validateOwners(ValidationErrors errors) {
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

	@ValidationMethod(on = { "submit", "modify" })
	public void validateWorkflowState(ValidationErrors errors) {
		try {
			if (flashItem.getProperties().getWorkflowState() != null) {
				WorkflowState state = WorkflowState.valueOf(flashItem.getProperties().getWorkflowState());
				switch (state) {
				case Initial:
				case Reject:
					if (context.getEventName().equals("modify")) {
						errors.addGlobalError(new LocalizableError("modifyItemError"));
					}
					break;

				case Archive:
					if (!context.getEventName().equals("modify")) {
						errors.addGlobalError(new LocalizableError("submitItemError"));
					}
					break;
				}
			}
		} catch (Exception e) {
			log.error("Could not validate item workflow state!", e);
		}
	}

	@ValidationMethod(on = { "submit", "modify" })
	public void validateSubmit(ValidationErrors errors) {
		// log.debug("validating submit event...");
		try {

			// validate flash scope item
			if (flashItem == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
				return;
			}

			// validate metadata fields
			MetadataValidator validator = new MetadataValidator(context);
			validator.validate(fields, inputForm);

			// validate metadata transformation
			try {
				MetadataTransformer.metadata2datastream(metadata);
			} catch (Exception e) {
				errors.addGlobalError(new LocalizableError("metadataFieldsInvalid"));
			}

			// validate community
			if (coms == null) {
				errors.addGlobalError(new LocalizableError("communityRequired"));
				errors.add("coms", new LocalizableError("communityRequired"));
			}

			// validate embargoed
			if (item.getProperties().isEmbargoed()) {
				try {
					Date edate = embargoedDateFormat.parse(item.getProperties().getEmbargoedDate());
					if (edate.before(currentDate)) {
						errors.addGlobalError(new LocalizableError("displayDateInvalid"));
						errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
					}
				} catch (Exception e) {
					errors.addGlobalError(new LocalizableError("displayDateInvalid"));
					errors.add("item.properties.embargoedDate", new LocalizableError("displayDateInvalid"));
				}
			}

			if (form.equals(Name.THESIS.toString())) {

				// validate collection
				if (cols == null) {
					errors.addGlobalError(new LocalizableError("collectionRequired"));
				}

				// thesis form: license validation
				validateThesisLicense(errors);

			} else {

				// default form: license validation
				validateDefaultLicense(errors);

				// check required license
				if (licenseFile == null && licenseId == null && licenseText == null && flashItem.getLicense() == null) {
					errors.addGlobalError(new LocalizableError("licenseRequired"));
					errors.add("licenseId", new LocalizableError("licenseRequired"));
				}
			}

			// validate file form
			validateFiles(errors);

			// restore file uploaded from flash scope
			restoreFlashFiles(errors);

			// validate required file
			if ((files == null || files.isEmpty()) && dsIds == null) {
				errors.addGlobalError(new LocalizableError("uploadFileRequired"));
				errors.add("file", new LocalizableError("uploadFileRequired"));
			}

		} catch (Exception e) {
			log.error("Valication process error!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}

	}

	/**
	 * The restoreFlashFiles method.
	 * 
	 * @param errors
	 */
	private void restoreFlashFiles(ValidationErrors errors) {
		List<FileBean> undeleteds = new ArrayList<FileBean>();
		if (removeFileIds != null) {
			for (int i = 0; i < flashFiles.size(); i++) {
				if (!removeFileIds.contains(String.valueOf(i))) {
					undeleteds.add(flashFiles.get(i));
				}
			}
		} else if (flashFiles != null) {
			undeleteds = flashFiles;
		}

		if (files == null || files.isEmpty()) {
			files = undeleteds;
		} else {
			files.addAll(undeleteds);
		}
	}

	/**
	 * The validateFiles method.
	 * 
	 * @param errors
	 * @throws IOException
	 */
	private void validateFiles(ValidationErrors errors) throws IOException {
		if (files != null) {

			// remove all null fileBean
			CollectionUtils.filter(files, NotNullPredicate.getInstance());

			List<FileBean> fbs = new ArrayList<FileBean>();
			for (FileBean file : files) {

				// check file size
				if (file.getSize() > uploadFileSize * ActionConstants.MB) {
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
					errors.add("files",
						new LocalizableError("fileTypeInvalid", file.getFileName(), file.getContentType()));
				} else {
					fbs.add(file);
				}
			}
			files = fbs;
		}
	}

	/**
	 * The licenseValidation method.
	 * 
	 * @param errors
	 * @throws IOException
	 */
	private void validateDefaultLicense(ValidationErrors errors) throws IOException {

		// default form: validate license
		if (licenseId != null) {
			licenseFile = null;
			licenseText = null;
		} else if (licenseFile != null) {
			licenseId = null;
			licenseText = null;
		} else if (licenseText != null) {
			licenseId = null;
			licenseFile = null;
		}

		// remove uploaded license
		if (removeLcId != null) {
			flashLicense = null;
		}

		// restore licenseFile from flash scope
		if (licenseFile == null && licenseId == null && licenseText == null) {
			licenseFile = flashLicense;
		}

		// check licenseFile file form
		if (licenseFile != null && !supportedContentTypes.matcher(licenseFile.getContentType()).matches()
			&& !PDFUtils.isPDFDocument(licenseFile.getInputStream())) {
			errors.addGlobalError(new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile
				.getContentType()));
			errors.add("licenseFile",
				new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile.getContentType()));
		}
	}

	@ValidationMethod(on = { "save" })
	public void validateSave(ValidationErrors errors) {
		try {
			// log.debug("validating save event...");

			// validate flash scope item
			if (flashItem == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
				return;
			}

			// validate title metadata field
			ca.ualberta.library.ir.model.metadata.Field title = fieldMap.get(Key.title.toString()).get(0);
			if (title.getValue() == null) {
				Field fld = inputForm.getFieldMap().get(title.getKey());
				String label = fld.getLabel().getId() == null ? fld.getLabel().getValue() : applicationResources
					.getString(fld.getLabel().getId());
				ValidationError error = new LocalizableError("metadata.required.valueNotPresent", label);
				errors.addGlobalError(error);
				errors.add(title.getFieldName(), error);
			}

			// validate license
			switch (Name.getValue(form)) {
			case DEFAULT:
				validateDefaultLicense(errors);
				break;
			}

			// validate file form
			validateFiles(errors);

			// restore file uploaded from flash scope
			restoreFlashFiles(errors);

		} catch (Exception e) {
			log.error("Valication process error!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}

	}

	/**
	 * The validateThesisLicense method.
	 * 
	 * @param errors
	 */
	private void validateThesisLicense(ValidationErrors errors) {
		if (!accepted) {
			errors.addGlobalError(new LocalizableError("acceptanceRequired"));
			errors.add("accepted", new LocalizableError("acceptanceRequired"));
		}
	}

	@HandlesEvent("init")
	@Secure(roles = "/item/create")
	@DefaultHandler
	@DontValidate
	public Resolution init() {
		try {

			// initialize item
			item = new Item();
			Properties properties = new Properties();
			properties.setOwners(context.getOwners());
			properties.setAccessType(AccessType.PUBLIC);

			// set submit to community and collection
			List<Community> coms = new ArrayList<Community>();
			List<Collection> cols = new ArrayList<Collection>();

			// set init collection
			if (pid != null) {
				try {
					Collection col = services.getCollection(pid);
					cols.add(col);

					// check ccid protected collection
					if (col.isCcid()) {
						if (context.getCCIDUser() == null) {
							return redirectCCIDLogin();
						} else {
							properties.setAccessType(AccessType.CCID_PROTECTED);
						}
					}

					// set init communities
					List<String> comIds = col.getMemberOfs();
					for (String comId : comIds) {
						Community com = services.getCommunity(comId);
						coms.add(com);

						// check ccid protected community
						if (com.isCcid()) {
							if (context.getCCIDUser() == null) {
								return redirectCCIDLogin();
							} else {
								properties.setAccessType(AccessType.CCID_PROTECTED);
							}
						}
					}
				} catch (Exception e) {
					context.getValidationErrors().addGlobalError(new LocalizableError("initializeCollectionError"));
					return forwardExceptionError("Could not initialize submit form!", e);
				}
			}
			item.setProperties(properties);
			item.setCommunities(coms);
			item.setCollections(cols);
			item.setDatastreams(new ArrayList<Datastream>());

			// init metadata field map
			fieldMap = new HashMap<String, List<ca.ualberta.library.ir.model.metadata.Field>>();

			// save to flash scope
			getFlashScope().put("item", item);

			return new ForwardResolution(uiPath + "/protected/submit.jsp");
		} catch (Exception e) {
			log.error("Could not initialize submit form!", e);
			return forwardExceptionError("Could not initialize submit form!", e);
		}
	}

	private void initMetadataInputForm() throws Exception {
		// log.debug("initializing input form variables...");
		form = form == null ? Form.Name.DEFAULT.toString() : form;
		inputForm = inputForms.getFormByItemType(form);
		inputFields = inputForm.getFields();
		valuePairsMap = inputForms.getValuePairsMap();
	}

	@DontValidate
	@HandlesEvent("getCollectionList")
	public Resolution getCollectionList() {
		// log.debug("getting collection list...");
		try {
			if (coms != null) {
				communities = services.getCommunitiesByPids(coms).getBeans(Community.class);
				for (Community com : communities) {
					if (context.getCCIDUser() == null) {
						com.setCollections(services.getPublicCommunityMemberCollections(com.getId()));
					} else {
						com.setCollections(services.getCommunityMemberCollections(com.getId()));
					}
				}
			}
			return new ForwardResolution(uiPath + "/protected/submitAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@DontValidate
	@HandlesEvent("editsaved")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid")
	public Resolution editSaved() {
		if (WorkflowState.Initial.toString().equals(item.getProperties().getWorkflowState())) {
			return edit();
		} else {
			return forwardUnauthorized();
		}
	}

	@DontValidate
	@HandlesEvent("editrejected")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid")
	public Resolution editRejected() {
		if (WorkflowState.Reject.toString().equals(item.getProperties().getWorkflowState())) {
			accepted = true;
			return edit();
		} else {
			return forwardUnauthorized();
		}
	}

	@DontValidate
	@HandlesEvent("edit")
	@Secure(roles = "/item/update,/object/owner,/object/dark,/object/ccid")
	public Resolution edit() {
		try {

			if (item.getProperties().getWorkflowState() == null
				|| item.getProperties().getWorkflowState().equals(WorkflowState.Archive.toString())) {
				accepted = true;
			}

			// set metadata
			metadata = item.getMetadata();

			// set fieldMap
			fieldMap = metadata.getFieldMap();

			form = form == null ? (item.getProperties().getFormName() == null ? Form.Name.DEFAULT.toString() : item
				.getProperties().getFormName()) : form;
			initMetadataInputForm();

			WorkflowState state = item.getProperties().getWorkflowState() == null
				&& item.getProperties().getPid() != null ? WorkflowState.Archive : WorkflowState.valueOf(item
				.getProperties().getWorkflowState());

			switch (state) {
			case Initial:
			case Reject:

				// save item to flash scope for edit submitted and rejected item
				getFlashScope().put("item", item);
				return new ForwardResolution(uiPath + "/protected/submit.jsp");

			case Archive:

				// save item to flash scope for edit archived item
				getFlashScope().put("item", item);
				return new ForwardResolution(uiPath + "/protected/submit.jsp");

			default:
				context.getValidationErrors().addGlobalError(
					new LocalizableError("itemStatusError", item.getProperties().getLabel(),
						ca.ualberta.library.ir.enums.State.valueOf(item.getProperties().getState()).getName(), item
							.getProperties().getWorkflowState() == null ? "" : item.getProperties().getWorkflowState()
							.toString()));
				return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
			}
		} catch (Exception e) {
			log.error("Could not edit this item!", e);
			return forwardExceptionError("Could not edit the item!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid")
	public Resolution save() {
		try {
			// log.debug("saving item...");

			// check workflow state
			if (flashItem.getProperties().getWorkflowState() == null) {
				if (flashItem.getProperties().getPid() == null) {

					// save new item
					saveOrUpdateItem(WorkflowState.Initial);
				} else {
					context.getValidationErrors().addGlobalError(
						new LocalizableError("itemStatusError", item.getProperties().getLabel(), "",
							WorkflowState.Initial.toString()));
					return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
				}
			} else {

				// save saved item
				saveOrUpdateItem(WorkflowState.valueOf(flashItem.getProperties().getWorkflowState()));
			}

			if (WorkflowState.Reject.toString().equals(flashItem.getProperties().getWorkflowState())) {
				context.getMessages().add(
					new LocalizableMessage("submit.rejectedItemMessage", trimTitle(item.getProperties().getLabel()),
						pid));
			} else {
				context.getMessages().add(
					new LocalizableMessage("submit.savedItemMessage", trimTitle(item.getProperties().getLabel()), pid));
			}
			return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");

		} catch (Exception e) {
			log.error("Could not save this item!", e);
			return forwardExceptionError("Could not save this item!", e);
		}
	}

	@HandlesEvent("submit")
	@Secure(roles = "/item/create,/object/owner,/object/ccid")
	public Resolution submit() {
		try {

			// log.debug("submitting item...");
			if (!inputForm.isEnabled()) { // form disabled
				context.getValidationErrors().addGlobalError(new LocalizableError(form + ".formDisabled"));
				return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
			}

			// check workflow state
			if (flashItem.getProperties().getWorkflowState() == null && flashItem.getProperties().getPid() != null) {
				context.getValidationErrors()
					.addGlobalError(
						new LocalizableError("itemStatusError", item.getProperties().getLabel(),
							ca.ualberta.library.ir.enums.State.valueOf(flashItem.getProperties().getState()).getName(),
							""));
				return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
			}

			// save item details
			saveOrUpdateItem(WorkflowState.Submit);

			// set response message
			if (item.getProperties().isManualApproval()) {
				context.getMessages().add(
					new LocalizableMessage("submit.pendingMessage", trimTitle(item.getProperties().getLabel()), url));
			} else {
				context.getMessages().add(
					new LocalizableMessage("submit.successMessage", trimTitle(item.getProperties().getLabel()), url));
			}

			// log.debug("finish! successfully deposited.");
			return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");

		} catch (Exception e) {
			log.error("Could not submit this item!", e);
			return forwardExceptionError("Could not submit this request!", e);
		}
	}

	@HandlesEvent("modify")
	@Secure(roles = "/item/update,/object/dark,/object/ccid")
	public Resolution modify() {
		try {

			// log.debug("modifying item...");

			// check item pid
			if (flashItem.getProperties().getPid() == null) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("itemStatusError", item.getProperties().getLabel(), "", flashItem
						.getProperties().getWorkflowState() == null ? "" : flashItem.getProperties().getWorkflowState()
						.toString()));
				return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
			}

			// check workflow state
			WorkflowState state = flashItem.getProperties().getWorkflowState() == null
				&& flashItem.getProperties().getPid() != null ? WorkflowState.Archive : WorkflowState.valueOf(flashItem
				.getProperties().getWorkflowState());
			if (!WorkflowState.Archive.equals(state)) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("itemStatusError", item.getProperties().getLabel(),
						ca.ualberta.library.ir.enums.State.valueOf(flashItem.getProperties().getState()).getName(),
						item.getProperties().getWorkflowState() == null ? "" : flashItem.getProperties()
							.getWorkflowState().toString()));
				return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
			}

			// save item details
			saveOrUpdateItem(WorkflowState.Archive);

			// set response message
			context.getMessages().add(
				new LocalizableMessage("submit.modifiedMessage", trimTitle(item.getProperties().getLabel()), url));

			// log.debug("finish! successfully deposited.");
			return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");

		} catch (Exception e) {
			log.error("Could not modify this item!", e);
			return forwardExceptionError("Could not modify this item!", e);
		}
	}

	/**
	 * The saveItem method.
	 * 
	 * @throws FedoraAPIException
	 * @throws ServiceException
	 * @throws IOException
	 */
	private void saveOrUpdateItem(final WorkflowState state) throws Exception {
		// log.debug("save or update item...");

		// update item properties
		updateItem(state);

		// update handle
		updateHandle(state);

		// update data in background using concurrency
		ThreadPool.getCachedThreadPool().execute(new UpdateRunable(state));
	}

	private void updateItem(final WorkflowState state) throws Exception {
		try {

			// set item label from metadata dc:title
			item.getProperties().setLabel(fieldMap.get(Key.title.toString()).get(0).getValue());

			// set item contentModel from metadata dc:type
			item.getProperties().setContentModel(fieldMap.get(Key.type.toString()).get(0).getValue());

			// set item owners
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
			switch (state) {
			case Initial:

			case Reject:
				item.getProperties().setState(State.I.toString());
				break;

			case Submit:
				if (item.getProperties().isEmbargoed()) {
					item.getProperties().setState(State.I.toString());
				} else if (item.getProperties().getAccessType().equals(AccessType.NOONE)) {
					item.getProperties().setState(State.I.toString());
				} else if (item.getProperties().isManualApproval()) {
					item.getProperties().setState(State.I.toString());
				} else {
					item.getProperties().setState(State.A.toString());
				}
				break;

			default:
				if (item.getProperties().isEmbargoed()) {
					item.getProperties().setState(State.I.toString());
				} else if (item.getProperties().getAccessType().equals(AccessType.NOONE)) {
					item.getProperties().setState(State.I.toString());
				} else {
					item.getProperties().setState(State.A.toString());
				}
			}

			if (item.getProperties().getPid() != null) {
				// modified object
				services.modifyObject(item.getProperties(), formatLogMessage(state));
				pid = item.getProperties().getPid();
			} else {
				// create object
				pid = services.ingest(item.getProperties(), formatLogMessage(state));
				item.getProperties().setPid(pid);
			}
			// log.debug("pid: " + pid);
		} catch (Exception e) {
			// log.debug("Could not update item: " + pid + "!", e);
			throw new Exception("Could not update item: " + pid + "!", e);
		}
	}

	private void updateHandle(final WorkflowState state) throws Exception {

		url = new StringBuilder(httpServerUrl).append(request.getContextPath()).append("/public/view/item/")
			.append(pid).toString();

		switch (state) {
		case Submit:
			// update item handle (deposit)
			if (!item.getProperties().isManualApproval()) {
				Handle handle = saveHandle(pid, HandleType.ITEM);
				if (handle != null) {
					url = new StringBuilder(handleServer).append("/").append(buildHandle(handle.getId())).toString();
				}
			}
			break;

		case Archive:
			// update item handle (edit archived item)
			Handle handle = saveHandle(pid, HandleType.ITEM);
			if (handle != null) {
				url = new StringBuilder(handleServer).append("/").append(buildHandle(handle.getId())).toString();
			}
			break;
		}
	}

	/**
	 * The updateMetadata method.
	 * 
	 * @param state
	 * @throws Exception
	 */
	private void updateMetadata(WorkflowState state) throws Exception {
		try {
			Map<String, List<ca.ualberta.library.ir.model.metadata.Field>> flashFieldMap;
			switch (state) {

			// submit item
			case Submit:

				// add identifier uuid field
				metadata.getFields().add(
					new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_identifier.toString(), null,
						new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.eraterms_local.toString()),
						pid));

				// add datesubmited field
				metadata.getFields().add(
					new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_datesubmitted.toString(),
						null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_W3CDTF
							.toString()), W3CDTFDateFormat.format(currentDate)));

				if (!item.getProperties().isManualApproval()) {

					// add identifier handle field (submit)
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_identifier.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_URI
								.toString()), url));

					// add accepted date field
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_dateaccepted.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_W3CDTF
								.toString()), W3CDTFDateFormat.format(currentDate)));
				}
				break;

			// edit rejected item
			case Reject:

				// restore auto-generated fields from flash scope item metadata
				flashFieldMap = flashItem.getMetadata().getFieldMap();
				metadata.getFields().add(flashFieldMap.get(Key.uuid.toString()).get(0));
				metadata.getFields().add(flashFieldMap.get(Key.dateSubmitted.toString()).get(0));
				break;

			// edit archived item
			case Archive:

				// restore auto-generated fields from flash scope item metadata
				flashFieldMap = flashItem.getMetadata().getFieldMap();

				// handle
				metadata.getFields()
					.add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_identifier.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_URI
								.toString()), url));

				// uuid
				metadata.getFields().add(
					new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_identifier.toString(), null,
						new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.eraterms_local.toString()),
						pid));

				// dateSubmited
				if (flashFieldMap.get(Key.dateSubmitted.toString()) == null) {
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_datesubmitted.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_W3CDTF
								.toString()), W3CDTFDateFormat.format(ISODateFormat.parse(flashItem.getProperties()
								.getCreatedDate()))));
				} else {
					metadata.getFields().add(flashFieldMap.get(Key.dateSubmitted.toString()).get(0));
				}

				// dateAccecpted
				if (flashFieldMap.get(Key.dateAccepted.toString()) == null) {
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_dateaccepted.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_W3CDTF
								.toString()), W3CDTFDateFormat.format(ISODateFormat.parse(flashItem.getProperties()
								.getCreatedDate()))));
				} else {
					metadata.getFields().add(flashFieldMap.get(Key.dateAccepted.toString()).get(0));
				}
				break;
			}

			// transform metadata to dcq
			byte[] dcq = MetadataTransformer.metadata2datastream(metadata);

			// save or update item metadata
			if (flashItem.getProperties().getHasMetadata()) {
				services.modifyDatastreamByValue(pid, DatastreamID.DCQ.toString(), "Item Metadata", dcq,
					formatLogMessage(state));
			} else {
				services.addDatastream(pid, DatastreamID.DCQ.toString(), "Item Metadata", dcq, formatLogMessage(state));
			}

		} catch (Throwable e) {
			log.error("Could not update item metadata: " + pid + "!", e);
			throw new Exception("Could not update item metadata: " + pid + "!", e);
		}
	}

	/**
	 * The updateCommunity method.
	 * 
	 * @param state
	 */
	private void updateCommunity(WorkflowState state) throws Exception {
		// log.debug("start: update communties...");
		try {

			// purge communities
			for (Community com : flashItem.getCommunities()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(),
					Constants.FEDORA.uri + com.getId(), false, null);
			}

			// purge collections
			for (Collection col : flashItem.getCollections()) {
				services.purgeRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
					Constants.FEDORA.uri + col.getId(), false, null);
			}

			// update community
			if (coms != null) {
				for (String com : coms) {
					services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF.getURI(), Constants.FEDORA.uri + com,
						false, null);
				}
			}

			// update collections
			if (cols != null) {
				for (String col : cols) {
					services.addRelationship(pid, FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI(),
						Constants.FEDORA.uri + col, false, null);
					Collection collection = services.getCollection(col);
					if (collection.isProquestUpload()) {
						proquestUpload = true;
					}
				}
			}
		} catch (Throwable e) {
			log.error("Could not update item community: " + pid + "!", e);
			throw new Exception("Could not update item community: " + pid + "!", e);
		}
		// log.debug("end: update communities.");
	}

	/**
	 * The updateLicense method.
	 * 
	 * @param state
	 * @throws Exception
	 */
	private void updateLicense(WorkflowState state) throws Exception {
		// log.debug("start: update license...");
		try {

			// check old license
			if (flashItem.getLicense() != null && (licenseId != null || licenseFile != null || licenseText != null)) {

				// purge old license
				services.purgeDatastream(pid, LICENSE.toString(), formatLogMessage(state));
			}

			if (licenseId != null) {

				// add license datastram
				ca.ualberta.library.ir.model.solr.License license = services.getLicenseById(licenseId);
				services.addDatastream(pid, LICENSE.toString(), license.getTitle(), license.getMimeType(),
					license.getUrl(), formatLogMessage(state));

			} else if (licenseFile != null) {

				// add license datastream from file
				services.addDatastream(pid, LICENSE.toString(), licenseFile.getFileName(),
					licenseFile.getContentType(), licenseFile.getInputStream(), formatLogMessage(state));

			} else if (licenseText != null) {

				// add license datastream from text
				ByteArrayInputStream bis = new ByteArrayInputStream(licenseText.getBytes());
				services.addDatastream(pid, LICENSE.toString(), trimTitle(licenseText), TEXT_PLAIN, bis,
					formatLogMessage(state));
			}
		} catch (Throwable e) {
			log.error("Could not update item license: " + pid + "!", e);
			throw new Exception("Could not update item license: " + pid + "!", e);
		}
		// log.debug("end: update license.");
	}

	/**
	 * The updateDatastream method.
	 * 
	 * @param state
	 */
	private void updateDatastream(WorkflowState state) throws Exception {
		// log.debug("start: update datastreams...");
		try {

			// remove datastreams
			if (removeDsIds != null) {
				for (String dsId : removeDsIds) {
					try {
						// remove item metadata: format, extent
						List<ca.ualberta.library.ir.model.metadata.Field> formats = fieldMap.get(Key.format.toString());
						List<ca.ualberta.library.ir.model.metadata.Field> extents = fieldMap.get(Key.extent.toString());
						List<Datastream> ds = flashItem.getDatastreams();
						for (int i = 0; i < ds.size(); i++) {
							if (ds.get(i).getDsId().equals(dsId)) {
								fields.remove(formats.get(i));
								fields.remove(extents.get(i));
								break;
							}
						}
					} catch (Exception e) {
						log.warn("Could not remove format and extent metadata for: " + pid + "/" + dsId + "!", e);
					}

					// purge datastream
					services.purgeDatastream(pid, dsId, formatLogMessage(state));
				}
			}

			// add new datastreams
			boolean thumbnail = flashItem.getThumbnail() != null;
			if (files != null) {
				for (FileBean file : files) {

					// add file datastream
					// log.debug("adding datastream: " + file.getFileName());
					Datastream datastream = new Datastream();
					datastream.setPid(pid);
					datastream.setLabel(file.getFileName());
					datastream.setData(file.getInputStream());
					datastream.setMimeType(file.getContentType());
					datastream.setSize(file.getSize());
					services.addDatastream(datastream, formatLogMessage(state));

					// generate thumbnail
					if (!thumbnail && ThumbnailGenerator.canGenerate(file.getContentType())) {
						thumbnailFile = file;
						thumbnail = true;
					}

					// add file format metadata field
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dc_format.toString(), null,
							new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_IMT.toString()),
							file.getContentType()));

					// add file size metadata field
					metadata.getFields().add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_extent.toString(), null,
							null, file.getSize() + " " + BYTES));
				}
			}
		} catch (Throwable e) {
			log.error("Could not update item datastream: " + pid + "!", e);
			throw new Exception("Could not update item datastream: " + pid + "!", e);
		}
		// log.debug("end: update datastreams.");
	}

	/**
	 * The updateContentModel method.
	 * 
	 * @param state
	 * @throws Exception
	 */
	private void updateContentModel(WorkflowState state) throws Exception {
		// log.debug("start: update contentModel...");
		try {

			// save contentModel
			if (item.getProperties().getContentModel() == null) {
				if (flashItem.getProperties().getContentModel() != null) {
					services.purgeRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
						+ flashItem.getProperties().getContentModel(), false, null);
				}
			} else if (!item.getProperties().getContentModel().equals(flashItem.getProperties().getContentModel())) {
				services.purgeRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
					+ flashItem.getProperties().getContentModel(), false, null);
				services.addRelationship(pid, Constants.MODEL.HAS_MODEL.uri, ContentModel.getURIPrefix()
					+ item.getProperties().getContentModel(), false, null);
			}
		} catch (Exception e) {
			log.error("Could not update item content model: " + pid + "!", e);
			throw new Exception("Could not update item content model: " + pid + "!", e);
		}
		// log.debug("end: update contentModel.");
	}

	/**
	 * The updateRelationships method.
	 * 
	 * @param state
	 * @throws Exception
	 */
	private void updateRelationships(WorkflowState state) throws Exception {
		// log.debug("start: update relationships...");
		try {

			// purge item access type
			switch (flashItem.getProperties().getAccessType()) {
			case CCID_PROTECTED:
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.CCID_AUTH.getURI(), false, null);
				break;

			case NOONE:
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
				break;
			}

			// add item access type
			switch (item.getProperties().getAccessType()) {
			case CCID_PROTECTED:
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.CCID_AUTH.getURI(), false, null);
				break;

			case NOONE:

				// clear embargoed
				item.getProperties().setEmbargoed(false);
				item.getProperties().setEmbargoedDate(null);

				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.DARK_REPOSITORY.getURI(), false, null);
				break;
			}

			// update embargoed relationships update
			if (flashItem.getProperties().isEmbargoed()) {

				// purge embargoed relationship and date
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.EMBARGOED.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(flashItem.getProperties()
					.getEmbargoedDate()));
				services.purgeRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);

			}
			if (item.getProperties().isEmbargoed()) {

				// add embargoed relationship and date
				services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.EMBARGOED.getURI(), false, null);
				String embargoedDate = ISODateFormat.format(embargoedDateFormat.parse(item.getProperties()
					.getEmbargoedDate()));
				services.addRelationship(pid, Definitions.EMBARGOED_DATE.getURI(), embargoedDate, true,
					Constants.RDF_XSD.DATE_TIME.uri);

			}

			// update manual approval relationship
			if (item.getProperties().isManualApproval()) {
				if (!flashItem.getProperties().isManualApproval()) {

					// add manual approval relationship
					services.addRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
						PartOfRelationship.MANUAL_APPROVAL.getURI(), false, null);
				}

			} else if (flashItem.getProperties().isManualApproval()) {

				// purge manual approval relationship
				services.purgeRelationship(pid, FedoraRelationship.IS_PART_OF.getURI(),
					PartOfRelationship.MANUAL_APPROVAL.getURI(), false, null);
			}

			// update form name
			if (flashItem.getProperties().getFormName() == null) {
				services.addRelationship(pid, Definitions.FORM_NAME.getURI(), form, true, null);
			}

			// update submitter id
			if (!user.getUsername().equals(flashItem.getProperties().getSubmitterId())) {
				if (flashItem.getProperties().getSubmitterId() != null) {
					services.purgeRelationship(flashItem.getProperties().getPid(), Definitions.SUBMITTER_ID.getURI(),
						flashItem.getProperties().getUserId(), true, null);
				}
				services.addRelationship(pid, Definitions.SUBMITTER_ID.getURI(), user.getUsername(), true, null);
			}

			// purge workflow state and date
			if (flashItem.getProperties().getWorkflowState() != null) {
				services.purgeRelationship(pid, Definitions.WORKFLOW_STATE.getURI(), flashItem.getProperties()
					.getWorkflowState(), true, null);
			}
			if (flashItem.getProperties().getWorkflowDate() != null) {
				services.purgeRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), flashItem.getProperties()
					.getWorkflowDate(), true, Constants.RDF_XSD.DATE_TIME.uri);
			}

			// update workflow date
			services.addRelationship(pid, Definitions.WORKFLOW_DATE.getURI(), ISODateFormat.format(currentDate), true,
				Constants.RDF_XSD.DATE_TIME.uri);

			switch (state) {

			case Submit:

				// add workflow state
				if (item.getProperties().isManualApproval()) {
					item.getProperties().setWorkflowState(WorkflowState.Submit.toString());
				} else {
					item.getProperties().setWorkflowState(WorkflowState.Archive.toString());
				}
				services.addRelationship(pid, Definitions.WORKFLOW_STATE.getURI(), item.getProperties()
					.getWorkflowState(), true, null);

				// oaiprovider relationships
				if (proaiEnabled) {
					if (flashItem.getProperties().getWorkflowState() == null
						|| WorkflowState.Initial.equals(flashItem.getProperties().getWorkflowState())) {
						services.addRelationship(pid, ApplicationProperties.getString("proai.fedora.itemID"),
							MessageFormat.format(proaiItemId, pid), true, null);
					}
				}

				// add proquest upload record
				if (proquestUpload && !item.getProperties().isManualApproval() && !item.getProperties().isEmbargoed()) {
					addProquestRecord(item);
				}
				break;

			default:

				// add workflow state
				item.getProperties().setWorkflowState(state.toString());
				services.addRelationship(pid, Definitions.WORKFLOW_STATE.getURI(), state.toString(), true, null);
				break;
			}
		} catch (Exception e) {
			log.error("Could not update object relationships!", e);
			throw e;
		}
		// log.debug("end: update relationships.");
	}

	protected class UpdateRunable implements Runnable {

		private final WorkflowState state;

		/**
		 * The UpdateRunable class constructor.
		 * 
		 * @param state
		 */
		public UpdateRunable(WorkflowState state) {
			this.state = state;
		}

		/**
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// log.debug("start: update object thread...");
			try {

				// update datastream
				updateDatastream(state);

				// update metadata
				updateMetadata(state);

				// update community/collection
				updateCommunity(state);

				// update license
				updateLicense(state);

				// update content model
				updateContentModel(state);

				// update object relationships
				updateRelationships(state);

				// generate thumbnail
				generateThumbnail(thumbnailFile, pid, state);

				// commit solr index
				services.commit(false);

				if (WorkflowState.Submit.equals(state)) {

					// send submission success email
					sendSubmitMail();

					// post facebook message
					postFacebookMessage(item);
				}

				// update transaction
				if (context.getEventName().equals("submit")) {
					services.updateTransaction(item, user);
				}

			} catch (Throwable t) {
				log.error("Could not update object: " + pid + "!", t);
			} finally {
				cleanTempFiles();
			}
			// log.debug("end: update object thread.");
		}

	}

	@Deprecated
	protected class UpdateObjectThread extends ActionBeanThread {

		/**
		 * The UpdateObjectThread class constructor.
		 * 
		 * @param parent
		 * @param state
		 */
		public UpdateObjectThread(Thread parent, WorkflowState state) {
			super(parent, state);
		}

		/**
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// log.debug("start: update object thread...");
			try {

				// update datastream
				updateDatastream(state);

				// update metadata
				updateMetadata(state);

				// update community/collection
				updateCommunity(state);

				// update license
				updateLicense(state);

				// update content model
				updateContentModel(state);

				// update object relationships
				updateRelationships(state);

				// generate thumbnail
				generateThumbnail(thumbnailFile, pid, state);

				// commit solr index
				// services.commit(false);

				if (WorkflowState.Submit.equals(state)) {

					// send submission success email
					sendSubmitMail();

					// post facebook message
					postFacebookMessage(item);
				}

				// update transaction
				if (context.getEventName().equals("submit")) {
					services.updateTransaction(item, user);
				}

			} catch (Throwable t) {
				log.error("Could not update object: " + pid + "!", t);
			} finally {
				cleanTempFiles();
			}
			// log.debug("end: update object thread.");
		}
	}

	@HandlesEvent("remove")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid")
	public Resolution remove() {
		try {
			item = getItemByPid(pid);
			if (State.I.toString().equals(item.getProperties().getState())
				&& (WorkflowState.Initial.toString().equals(item.getProperties().getWorkflowState())
					|| WorkflowState.Submit.toString().equals(item.getProperties().getWorkflowState()) || item
					.getProperties().getWorkflowState() == null)) {

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
			return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
		} catch (Exception e) {
			log.error("Could not purge this item!", e);
			return forwardExceptionError("Could not purge this item!", e);
		}
	}

	@HandlesEvent("removerejected")
	@Secure(roles = "/item/create,/object/owner,/object/dark,/object/ccid,/admin/approve")
	public Resolution removeRejected() {
		try {
			item = getItemByPid(pid);
			if (State.I.toString().equals(item.getProperties().getState())
				&& WorkflowState.Reject.toString().equals(item.getProperties().getWorkflowState())) {

				// purge object
				services.purgeObject(item.getProperties().getPid(), formatLogMessage(WorkflowState.Reject));
				services.commit();
				context.getMessages().add(
					new LocalizableMessage("item.removeSuccess", trimTitle(item.getProperties().getLabel())));
			} else {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("removeItemError", item.getProperties().getLabel(), item.getProperties()
						.getWorkflowState()));
			}
			return new ForwardResolution(uiPath + "/protected/submitMessage.jsp");
		} catch (Exception e) {
			log.error("Could not purge this item!", e);
			return forwardExceptionError("Could not purge this item!", e);
		}
	}

	private void sendSubmitMail() {

		// send email to user in background process
		Thread thread = new Thread(new Runnable() {

			public void run() {
				try {
					if (item.getProperties().isManualApproval()) {

						// collection new task notification mail
						List<User> users = new ArrayList<User>();
						List<Collection> cols = item.getCollections();
						for (Collection col : cols) {
							List<User> subUsers = services.getSubscriptionUsers(col.getId(),
								SubscriptionType.TASK.getValue());
							if (subUsers != null) {
								users.addAll(subUsers);
							}
						}
						if (!users.isEmpty()) {
							String subject = applicationResources.getString("mail.submit.subject");
							String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
								+ "/action/admin/approval";
							String viewItemUrl = ApplicationProperties.getString("http.server.url")
								+ request.getContextPath() + "/public/view/item/" + pid;
							Map<String, Object> model = new HashMap<String, Object>();
							model.put("item",
								services.findObjectByPid(pid).getBeans(ca.ualberta.library.ir.model.solr.Item.class)
									.get(0));
							model.put("viewItemUrl", viewItemUrl);
							mailServiceManager.sendSubmissionMail(users, user, subject, model, url);
						}
					} else {

						// send deposit success mail
						String subject = applicationResources.getString("mail.deposit.subject");
						Map<String, Object> model = new HashMap<String, Object>();
						model
							.put("item",
								services.findObjectByPid(pid).getBeans(ca.ualberta.library.ir.model.solr.Item.class)
									.get(0));
						mailServiceManager.sendDepositMail(user, subject, model, url);
					}
				} catch (Exception e) {
					log.error("Could not send email!", e);
				}
			}
		});
		thread.start();
	}

	private void cleanTempFiles() {

		// clean up temp file in background process
		Thread thread = new Thread(new Runnable() {
			public void run() {
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
							log.warn("Could not delete uploaded temp file! (" + file.toString() + ")", e);
						}
					}
				}
			}
		});
		thread.start();
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
	public String getLicenseId() {
		return licenseId;
	}

	/**
	 * The setLicenseId setter method.
	 * 
	 * @param licenseId the licenseId to set
	 */
	public void setLicenseId(String licenseId) {
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

	/**
	 * The getType getter method.
	 * 
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * The setType setter method.
	 * 
	 * @param form the form to set
	 */
	public void setForm(String form) {
		this.form = form;
	}

	/**
	 * The getValuePairsMap getter method.
	 * 
	 * @return the valuePairsMap
	 */
	public Map<String, ValuePairs> getValuePairsMap() {
		return valuePairsMap;
	}

	/**
	 * The getFieldMap getter method.
	 * 
	 * @return the fieldMap
	 */
	public Map<String, List<ca.ualberta.library.ir.model.metadata.Field>> getFieldMap() {
		return fieldMap;
	}

	/**
	 * The getLicenses getter method.
	 * 
	 * @return the licenses
	 */
	public List<ca.ualberta.library.ir.model.solr.License> getLicenses() {
		return licenses;
	}

	/**
	 * The getInputFields getter method.
	 * 
	 * @return the inputFields
	 */
	public List<Field> getInputFields() {
		return inputFields;
	}

	/**
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<ca.ualberta.library.ir.model.metadata.Field> getFields() {
		return fields;
	}

	/**
	 * The setFields setter method.
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(List<ca.ualberta.library.ir.model.metadata.Field> fields) {
		this.fields = fields;
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
	 * The setCommunityIds setter method.
	 * 
	 * @param communityIds the communityIds to set
	 */
	public void setCommunityIds(String communityIds) {
	}

	/**
	 * The getCommunityList getter method.
	 * 
	 * @return the communityList
	 */
	public List<Community> getCommunityList() {
		return communityList;
	}

	/**
	 * The isAccepted getter method.
	 * 
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * The setAccepted setter method.
	 * 
	 * @param accepted the accepted to set
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	/**
	 * The getInputForm getter method.
	 * 
	 * @return the inputForm
	 */
	public Form getInputForm() {
		return inputForm;
	}
}
