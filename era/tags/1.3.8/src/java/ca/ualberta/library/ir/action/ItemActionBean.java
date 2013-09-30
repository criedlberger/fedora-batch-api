/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ItemActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.After;
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
import org.apache.solr.client.solrj.SolrServerException;

import fedora.server.types.gen.ObjectProfile;

import ca.ualberta.library.ir.domain.Transaction;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.PDFUtils;

/**
 * The CreateActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/item/{$event}/{item.properties.pid}")
public class ItemActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ItemActionBean.class);

	// all communities drop-down list
	private List<Community> communities;

	// all collections drop-down list
	private List<Collection> collections;

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

	private Item flashItem;

	private List<Transaction> transactions;

	/**
	 * The CreateActionBean class constructor.
	 */
	public ItemActionBean() {
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

	@SuppressWarnings("unchecked")
	@ValidationMethod
	public void validate(ValidationErrors errors) {
		// log.debug("validation method...");
		try {

			// get previous flash scrope variables from request
			flashItem = (Item) request.getAttribute("item");
			List<FileBean> uploadedFiles = (List<FileBean>) request.getAttribute("files");
			FileBean uploadedLicense = (FileBean) request.getAttribute("license");

			if (flashItem == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
				return;
			}

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
				for (String pid : coms) {
					try {
						ObjectProfile obj = services.getObjectProfile(pid);
						Community com = new Community();
						com.setId(obj.getPid());
						com.setTitle(obj.getObjLabel());
						comList.add(com);
					} catch (Exception e) {
						errors.addGlobalError(new LocalizableError("communityInvalid", pid));
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
				for (String pid : cols) {
					try {
						ObjectProfile obj = services.getObjectProfile(pid);
						Collection col = new Collection();
						col.setId(obj.getPid());
						col.setTitle(obj.getObjLabel());
						colList.add(col);
					} catch (Exception e) {
						errors.addGlobalError(new LocalizableError("collectionInvalid", pid));
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

			// check licenseFile file type
			if (licenseFile != null && !supportedContentTypes.matcher(licenseFile.getContentType()).matches()
				&& !PDFUtils.isPDFDocument(licenseFile.getInputStream())) {
				errors.addGlobalError(new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile
					.getContentType()));
				errors.add("license",
					new LocalizableError("fileTypeInvalid", licenseFile.getFileName(), licenseFile.getContentType()));
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

			// validate uploaded files
			int removeDsCount = removeDsIds == null ? 0 : removeDsIds.size();
			int dsCount = flashItem.getDatastreams() == null ? 0 : flashItem.getDatastreams().size();
			if (files == null && uploadedFiles == null && dsCount == removeDsCount) {
				errors.addGlobalError(new LocalizableError("uploadFileRequired"));
				errors.add("file", new LocalizableError("required"));
			} else if (files != null) {
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
							errors.add("files",
								new LocalizableError("fileTypeInvalid", file.getFileName(), file.getContentType()));
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
			log.error("Could not process the request!", e);
			errors.addGlobalError(new LocalizableError("validationErrors"));
		}

	}

	@After(on = { "edit", "save" }, stages = LifecycleStage.HandlerResolution)
	public void afterHandlerResolution() {
		try {
			communities = services.getAllCommunities();
			collections = services.getAllCollections();
		} catch (SolrServerException e) {
			log.error("Could not get all communities add/or collection!", e);
		}
	}

	public void restoreFormFields() {
		try {

			// restore item license
			if (licenseId == 0 && licenseFile == null && licenseText == null) {
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

		} catch (Exception e) {
			log.error("Could not restore form fields!", e);
		}
	}

	@HandlesEvent("getTransactionList")
	@DontValidate
	@Secure(roles = "/admin/approve")
	public Resolution getTransactionList() {
		try {
			transactions = services.getTransactionsByPid(item.getProperties().getPid());
			return new ForwardResolution(uiPath + "/protected/transactions.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@DontValidate
	@HandlesEvent("delete")
	@Secure(roles = "/item/delete,/object/owner,/object/dark,/object/ccid")
	public Resolution delete() {
		try {
			item = getItemByPid(item.getProperties().getPid());
			if (item == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("itemNotFound"));
			}
			if (State.D.toString().equals(item.getProperties().getState())) {
				context.getMessages().add(
					new LocalizableMessage("item.deleteSuccess", trimTitle(item.getProperties().getLabel())));
				return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
			}

			if (!context.getValidationErrors().isEmpty()) {
				return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
			}
			item.getProperties().setState(State.D.toString());
			services.modifyObject(item.getProperties(),
				formatLogMessage(WorkflowState.Archive, "Modify object state to Delete."));
			if (item.getProperties().getUserId() != null
				&& !user.getUsername().equals(item.getProperties().getUserId())) {
				services.purgeRelationship(item.getProperties().getPid(), Definitions.USER_ID.getURI(), item
					.getProperties().getUserId(), true, null);
			}
			services.addRelationship(item.getProperties().getPid(), Definitions.USER_ID.getURI(), user.getUsername(),
				true, null);
			services.commit();

			context.getMessages().add(
				new LocalizableMessage("item.deleteSuccess", trimTitle(item.getProperties().getLabel())));
			return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
		} catch (Exception e) {
			log.error("Could not delete this item!", e);
			return forwardExceptionError("Could not delete this item!", e);
		}
	}

	@DontValidate
	@HandlesEvent("restore")
	@Secure(roles = "/admin/deleted,/item/delete,/object/owner,/object/dark,/object/ccid")
	public Resolution restore() {
		try {
			item = getItemByPid(item.getProperties().getPid());
			if (item == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("itemNotFound"));
			}
			if (!State.D.toString().equals(item.getProperties().getState())) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("notDeleted", item.getProperties().getWorkflowState()));
			}
			if (!context.getValidationErrors().isEmpty()) {
				return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
			}
			item.getProperties().setState(item.getProperties().isEmbargoed() ? State.I.toString() : State.A.toString());
			services.modifyObject(item.getProperties(),
				formatLogMessage(WorkflowState.Archive, "Modify object state to Active."));
			services.commit();

			context.getMessages().add(
				new LocalizableMessage("item.restoreSuccess", trimTitle(item.getProperties().getLabel())));
			return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
		} catch (Exception e) {
			log.error("Could not undelete this item!", e);
			return forwardExceptionError("Could not undelete this item!", e);
		}
	}

	@DontValidate
	@HandlesEvent("purge")
	@Secure(roles = "/admin/purge,/object/owner,/object/dark,/object/ccid")
	public Resolution purge() {
		try {
			item = getItemByPid(item.getProperties().getPid());
			if (item == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("itemNotFound"));
			}

			// check item state
			if (!State.D.toString().equals(item.getProperties().getState())) {
				context.getValidationErrors()
					.addGlobalError(
						new LocalizableError("item.invalidState", State.valueOf(item.getProperties().getState())
							.getName()));
			}

			// check favorite and bookmark before delete
			int bm = services.getBookmarkCountByPid(item.getProperties().getPid());
			if (bm > 0) {
				services.deleteBookmarkByPid(item.getProperties().getPid());
				services.deleteByQuery(MessageFormat.format("bm.pid:\"{0}\" AND ir.type:bookmark", item.getProperties()
					.getPid()));
			}
			int fav = services.getFavoriteCountByPid(item.getProperties().getPid());
			if (fav > 0) {
				services.deleteFavoriteByPid(item.getProperties().getPid());
				services.deleteByQuery(MessageFormat.format("fav.pid:\"{0}\" AND ir.type:bookmark", item
					.getProperties().getPid()));
			}

			// return if there is an error.
			if (!context.getValidationErrors().isEmpty()) {
				return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
			}

			services.purgeObject(item.getProperties().getPid(),
				formatLogMessage(WorkflowState.Archive, "Purge object."));
			services.commit();

			// delete handle
			deleteHandle(item.getProperties().getPid());

			context.getMessages().add(
				new LocalizableMessage("item.purgeSuccess", trimTitle(item.getProperties().getLabel())));
			return new ForwardResolution(uiPath + "/protected/editItemMessage.jsp");
		} catch (Exception e) {
			log.error("Could not purge this item!", e);
			return forwardExceptionError("Could not purge this item!", e);
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
	 * The getCommnuites getter method.
	 * 
	 * @return the commnuites
	 */
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setCommnuites setter method.
	 * 
	 * @param commnuites the commnuites to set
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
	 * The getTransactions getter method.
	 * 
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}
}
