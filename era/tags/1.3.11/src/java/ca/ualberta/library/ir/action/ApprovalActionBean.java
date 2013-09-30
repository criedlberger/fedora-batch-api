/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApprovalActionBean.java 5602 2012-10-05 18:48:09Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.common.Constants;

import ca.ualberta.library.ir.domain.Handle;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.exception.ServiceException;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.metadata.Attribute;
import ca.ualberta.library.ir.model.metadata.Field.Element;
import ca.ualberta.library.ir.model.metadata.MetadataTransformer;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.ThreadPool;

/**
 * The ApprovalActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5602 $ $Date: 2012-10-05 12:48:09 -0600 (Fri, 05 Oct 2012) $
 */
@UrlBinding("/action/approval/{$event}/{item.properties.pid}")
public class ApprovalActionBean extends BaseActionBean {

	private static final Log log = LogFactory.getLog(ApprovalActionBean.class);

	// private final Date currentDate = new Date();

	private Item item;

	private String comments;

	private List<String> fields;

	private boolean embargoed;

	private String embargoedDate;

	private int rejectId;

	private List<ca.ualberta.library.ir.model.solr.Item> items;

	private String isoDate;

	private String workflowState;

	private String workflowDate;

	private String workflowComments;

	/**
	 * The ApprovalActionBean class constructor.
	 */
	public ApprovalActionBean() {
		super();
	}

	@Override
	public String getObjectPID() {
		return item.getProperties().getPid();
	}

	@ValidationMethod(on = { "accept", "reject", "release" })
	public void validate(ValidationErrors errors) {
		try {
			// get flash scope item
			item = (Item) request.getAttribute("item");
			if (item == null) {
				errors.addGlobalError(new LocalizableError("itemNotFound"));
			} else if (!WorkflowState.Review.toString().equals(item.getProperties().getWorkflowState())) {
				errors.addGlobalError(new LocalizableError("invalidWorkflowState", item.getProperties()
					.getWorkflowState()));
			}
		} catch (Exception e) {
			log.error("Validation errors!", e);
			errors.addGlobalError(new LocalizableError("errors.validationError"));
		}
	}

	@ValidationMethod(on = { "accept" })
	public void validateAccept(ValidationErrors errors) {
		try {

			// validate embargoed
			if (embargoed) {
				try {
					Date edate = embargoedDateFormat.parse(embargoedDate);
					if (edate.before(new Date())) {
						errors.addGlobalError(new LocalizableError("displayDateInvalid"));
						errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
					}
				} catch (Exception e) {
					errors.addGlobalError(new LocalizableError("displayDateInvalid"));
					errors.add("item.properties.embargoedDate", new LocalizableError("displayDateInvalid"));
				}
			} else if (embargoedDate != null) {
				errors.addGlobalError(new LocalizableError("displayDateInvalid"));
				errors.addGlobalError(new LocalizableError("displayDateInvalid"));
				errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
			}
		} catch (Exception e) {
			log.error("Validation errors!", e);
			errors.addGlobalError(new LocalizableError("errors.validationError"));
		}
	}

	@ValidationMethod(on = { "reject", "release" })
	public void validateRejectAndReturn(ValidationErrors errors) {
		try {

			// validate embargoed
			if (embargoed || embargoedDate != null) {
				errors.addGlobalError(new LocalizableError("displayDateInvalid"));
				errors.add("item.properties.embargoedDate", new LocalizableError("invalid"));
			}
		} catch (Exception e) {
			log.error("Validation errors!", e);
			errors.addGlobalError(new LocalizableError("errors.validationError"));
		}
	}

	@Before(stages = LifecycleStage.ResolutionExecution)
	public void restoreFields() {
		if (!context.getValidationErrors().isEmpty() && item != null) {

			// set flashscope item
			getFlashScope().put("item", item);

			// set field list for metadata display format
			fields = Arrays.asList(commaPattern.split(ApplicationProperties.getString(item.getProperties()
				.getFormName() + ".metadata.fields")));
		}
	}

	@HandlesEvent("getExplanation")
	@DontValidate
	public Resolution getExplanation() {
		try {
			return new ForwardResolution(uiPath + "/protected/approvalAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("review")
	@Secure(roles = "/admin/approve")
	public Resolution review() {
		try {
			item = getItemByPid(item.getProperties().getPid());

			// set field list for metadata display format
			fields = Arrays.asList(commaPattern.split(ApplicationProperties.getString(item.getProperties()
				.getFormName() + ".metadata.fields")));

			if (item == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("itemNotFound"));
				return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");
			}

			if (WorkflowState.Review.toString().equals(item.getProperties().getWorkflowState())) {
				if (!item.getProperties().getUserId().equals(user.getUsername())) {
					User user = services.getUser(item.getProperties().getUserId());
					context.getValidationErrors().addGlobalError(
						new LocalizableError("itemTaken", user.getFirstName() + " " + user.getLastName(), user
							.getEmail()));
					return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");
				}
				getFlashScope().put("item", item);
				return new ForwardResolution(uiPath + "/protected/approval.jsp");
			}

			if (!State.I.toString().equals(item.getProperties().getState())) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("invalidState", item.getProperties().getState()));
			}
			if (!WorkflowState.Submit.toString().equals(item.getProperties().getWorkflowState())
				&& !WorkflowState.Review.toString().equals(item.getProperties().getWorkflowState())) {
				context.getValidationErrors().addGlobalError(
					new LocalizableError("invalidWorkflowState", item.getProperties().getWorkflowState()));
			}

			if (!context.getValidationErrors().isEmpty()) {
				return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");
			}

			// store current item properties
			isoDate = ISODateFormat.format(new Date());
			workflowState = item.getProperties().getWorkflowState();
			workflowDate = item.getProperties().getWorkflowDate();

			// purge workflow state
			services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
				workflowState, true, null);
			services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(), workflowDate,
				true, Constants.RDF_XSD.DATE_TIME.uri);

			// update workflow state
			services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
				WorkflowState.Review.toString(), true, null);
			services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(), isoDate, true,
				Constants.RDF_XSD.DATE_TIME.uri);

			// update userId
			if (!user.getUsername().equals(item.getProperties().getUserId())) {
				if (item.getProperties().getUserId() != null) {
					services.purgeRelationship(item.getProperties().getPid(), Definitions.USER_ID.getURI(), item
						.getProperties().getUserId(), true, null);
				}
				services.addRelationship(item.getProperties().getPid(), Definitions.USER_ID.getURI(),
					user.getUsername(), true, null);
			}

			// update item before update transaction
			item.getProperties().setWorkflowState(WorkflowState.Review.toString());
			item.getProperties().setWorkflowDate(isoDate);

			// update transaction
			updateTransaction();

			// set item in flash scope
			getFlashScope().put("item", item);
			return new ForwardResolution(uiPath + "/protected/approval.jsp");

		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("reject")
	@Secure(roles = "/admin/approve")
	public Resolution reject() {
		try {

			// store current item properties
			isoDate = ISODateFormat.format(new Date());
			workflowState = item.getProperties().getWorkflowState();
			workflowDate = item.getProperties().getWorkflowDate();
			workflowComments = item.getComments();

			// update object in background process
			ThreadPool.getCachedThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					try {
						// log.debug("updating object in background...");

						// update workflow state
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							workflowState, true, null);
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							workflowDate, true, Constants.RDF_XSD.DATE_TIME.uri);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							WorkflowState.Reject.toString(), true, null);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							isoDate, true, Constants.RDF_XSD.DATE_TIME.uri);

						// update comments
						updateComments();

					} catch (Exception e) {
						log.error("Could not update object to reject the item! (" + item.getProperties().getPid(), e);
					}
				}
			});

			// update item
			item.getProperties().setWorkflowState(WorkflowState.Reject.toString());
			item.getProperties().setWorkflowDate(isoDate);
			item.setComments(comments);

			// send notification email
			sendRejectMail();

			// update transaction
			updateTransaction();

			String url = new StringBuilder(httpServerUrl).append(request.getContextPath()).append("/public/view/item/")
				.append(item.getProperties().getPid()).toString();
			context.getMessages().add(
				new LocalizableMessage("approval.rejectSuccess", trimTitle(item.getProperties().getLabel()), url));
			return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");

		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The sendRejectMail method.
	 */
	private void sendRejectMail() throws Exception {
		ThreadPool.getCachedThreadPool().execute(new Runnable() {
			public void run() {
				try {
					// log.debug("sending rejected email...");

					String subject = applicationResources.getString("mail.reject.subject");
					String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
						+ "/action/myaccount/pendingitems";
					String pid = item.getProperties().getPid();
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("item",
						services.findObjectByPid(pid).getBeans(ca.ualberta.library.ir.model.solr.Item.class).get(0));
					User user = services.getUser(item.getProperties().getSubmitterId());
					mailServiceManager.sendRejectMail(user, subject, model, url, comments);

				} catch (Exception e) {
					log.error("Could not send reject email!", e);
				}
			}
		});
	}

	@HandlesEvent("release")
	@Secure(roles = "/admin/approve")
	public Resolution release() {
		try {

			// store current item properties
			isoDate = ISODateFormat.format(new Date());
			workflowState = item.getProperties().getWorkflowState();
			workflowDate = item.getProperties().getWorkflowDate();

			// update item in background process
			ThreadPool.getCachedThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					try {
						// log.debug("updating object in background...");

						// update workflow state to Submit
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							workflowState, true, null);
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							workflowDate, true, Constants.RDF_XSD.DATE_TIME.uri);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							WorkflowState.Submit.toString(), true, null);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							isoDate, true, Constants.RDF_XSD.DATE_TIME.uri);

					} catch (Exception e) {
						log.error("Could not update object to release item! (" + item.getProperties().getPid() + ")", e);
					}
				}
			});

			// update workflow history
			item.getProperties().setWorkflowState(WorkflowState.Release.toString());
			item.getProperties().setWorkflowDate(isoDate);

			updateTransaction();

			String url = new StringBuilder(httpServerUrl).append(request.getContextPath()).append("/public/view/item/")
				.append(item.getProperties().getPid()).toString();
			context.getMessages().add(
				new LocalizableMessage("approval.releaseSuccess", trimTitle(item.getProperties().getLabel()), url));

			return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");

		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("accept")
	@Secure(roles = "/admin/approve")
	public Resolution accept() {
		try {

			// store current item properties
			isoDate = ISODateFormat.format(new Date());
			workflowState = item.getProperties().getWorkflowState();
			workflowDate = item.getProperties().getWorkflowDate();
			workflowComments = item.getComments();

			// create handle
			String url = null;
			Handle handle = saveHandle(item.getProperties().getPid(), HandleType.ITEM);
			if (handle != null) {

				// add identifier handle field (submit)
				url = new StringBuilder(handleServer).append("/").append(buildHandle(handle.getId())).toString();
				item.getMetadata()
					.getFields()
					.add(
						new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_identifier.toString(),
							null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_URI
								.toString()), url));
			}

			// add accepted date
			item.getMetadata()
				.getFields()
				.add(
					new ca.ualberta.library.ir.model.metadata.Field(null, Element.dcterms_dateaccepted.toString(),
						null, new Attribute(Attribute.Name.xsi_type.toString(), Attribute.Value.dcterms_W3CDTF
							.toString()), W3CDTFDateFormat.format(new Date())));

			// update item workflow state
			item.getProperties().setWorkflowState(WorkflowState.Archive.toString());
			item.getProperties().setWorkflowDate(isoDate);
			item.setComments(comments);

			// update object in background process
			ThreadPool.getCachedThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					try {
						// log.debug("updating object in background...");

						// update embargoed relationship
						if (embargoed) {

							// add embargoed relationship
							services.addRelationship(item.getProperties().getPid(),
								FedoraRelationship.IS_PART_OF.getURI(), PartOfRelationship.EMBARGOED.getURI(), false,
								null);

							// add embargoed date
							String eDate = ISODateFormat.format(embargoedDateFormat.parse(embargoedDate));
							services.addRelationship(item.getProperties().getPid(),
								Definitions.EMBARGOED_DATE.getURI(), eDate, true, Constants.RDF_XSD.DATE_TIME.uri);

						} else {

							// update item state
							item.getProperties().setState(State.A.toString());
							services.modifyObject(item.getProperties(), formatLogMessage(WorkflowState.Archive));

						}

						// update item metadata
						byte[] dcq = MetadataTransformer.metadata2datastream(item.getMetadata());
						services.modifyDatastreamByValue(item.getProperties().getPid(), DatastreamID.DCQ.toString(),
							null, dcq, formatLogMessage(WorkflowState.Archive));

						// update workflow state
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							workflowState, true, null);
						services.purgeRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							workflowDate, true, Constants.RDF_XSD.DATE_TIME.uri);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_STATE.getURI(),
							WorkflowState.Archive.toString(), true, null);
						services.addRelationship(item.getProperties().getPid(), Definitions.WORKFLOW_DATE.getURI(),
							isoDate, true, Constants.RDF_XSD.DATE_TIME.uri);

						// update comments
						updateComments();

					} catch (Exception e) {
						log.error("Could not update object to archive the item! (" + item.getProperties().getPid()
							+ ")", e);
					}
				}
			});

			// check proquest upload flag
			boolean proquestUpload = false;
			for (Collection col : item.getCollections()) {
				if (col.isProquestUpload()) {
					proquestUpload = true;
				}
			}

			// add proquest upload record
			if (proquestUpload && !embargoed) {
				addProquestRecord(item);
			}

			// commit solr index
			// services.commit(false);

			// send archvie email
			sendArchiveMail();

			// post facebook message
			postFacebookMessage(item);

			// update transaction
			updateTransaction();

			context.getMessages().add(
				new LocalizableMessage("approval.approveSuccess", trimTitle(item.getProperties().getLabel()), url));
			return new ForwardResolution(uiPath + "/protected/approvalMessage.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("email")
	@Secure(roles = "/admin/email")
	public Resolution email() {
		try {
			item = getItemByPid(item.getProperties().getPid());
			if (item.getProperties().getWorkflowState().equals(WorkflowState.Reject.toString())) {
				sendRejectMail();
				return forwardMessage("approval.rejected.email");
			} else if (item.getProperties().getWorkflowState().equals(WorkflowState.Archive.toString())) {
				sendArchiveMail();
				return forwardMessage("approval.archived.email");
			} else {
				return forwardErrorMessage("Could not send the email because the item has workflow state "
					+ item.getProperties().getWorkflowState() + "!");
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getItemsByTitleAuthor")
	@DontValidate
	public Resolution getItemsByTitleAuthor() {
		try {
			items = services.findManualApprovalItemsByTitleAuthor(query, 0, ActionConstants.suggestionCount).getBeans(
				ca.ualberta.library.ir.model.solr.Item.class);
		} catch (Exception e) {
			items = new ArrayList<ca.ualberta.library.ir.model.solr.Item>();
		}
		return new ForwardResolution(uiPath + "/protected/approvalAjax.jsp");
	}

	/**
	 * The updateComments method.
	 * 
	 * @throws Exception
	 */
	private void updateComments() throws Exception {
		try {
			// update comments
			if (workflowComments != null) {
				services.purgeRelationship(item.getProperties().getPid(), Definitions.COMMENTS.getURI(),
					workflowComments, true, null);
				// item.setComments(null);
			}
			if (StringUtils.trimToNull(comments) != null) {
				services.addRelationship(item.getProperties().getPid(), Definitions.COMMENTS.getURI(), comments, true,
					null);
				// item.setComments(comments);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void updateTransaction() {
		ThreadPool.getCachedThreadPool().execute(new Runnable() {
			public void run() {
				try {
					// log.debug("updating transaction...");
					services.updateTransaction(item, user);
				} catch (ServiceException e) {
					log.error("Could not update transaction!", e);
				}
			}
		});
	}

	private void sendArchiveMail() throws Exception {
		ThreadPool.getCachedThreadPool().execute(new Runnable() {
			public void run() {
				try {
					// log.debug("sending archived email...");

					String pid = item.getProperties().getPid();
					String url = getHandleURL(pid);

					User user = services.getUser(item.getProperties().getSubmitterId());
					String subject = applicationResources.getString("mail.archive.subject");
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("item",
						services.findObjectByPid(pid).getBeans(ca.ualberta.library.ir.model.solr.Item.class).get(0));
					if (embargoed) {
						model.put("embargoed", embargoed);
						model.put("embargoedDate", embargoedDateFormat.parse(embargoedDate));
					}
					mailServiceManager.sendArchiveMail(user, subject, model, url, comments);

				} catch (Exception e) {
					log.error("Could not send email!", e);
				}
			}
		});
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
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<String> getFields() {
		return fields;
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

	/**
	 * The getEmbargoedDate getter method.
	 * 
	 * @return the embargoedDate
	 */
	public String getEmbargoedDate() {
		return embargoedDate;
	}

	/**
	 * The setEmbargoedDate setter method.
	 * 
	 * @param embargoedDate the embargoedDate to set
	 */
	public void setEmbargoedDate(String embargoedDate) {
		this.embargoedDate = embargoedDate;
	}

	/**
	 * The setRejectId setter method.
	 * 
	 * @param rejectId the rejectId to set
	 */
	public void setRejectId(int rejectId) {
		this.rejectId = rejectId;
	}

	/**
	 * The getRejectId getter method.
	 * 
	 * @return the rejectId
	 */
	public int getRejectId() {
		return rejectId;
	}

	/**
	 * The getItems getter method.
	 * 
	 * @return the items
	 */
	public List<ca.ualberta.library.ir.model.solr.Item> getItems() {
		return items;
	}
}
