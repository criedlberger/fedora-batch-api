/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: BaseActionBean.java 5602 2012-10-05 18:48:09Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.velocity.app.VelocityEngine;
import org.openarchives.oai.x20.oaiDc.DcDocument;
import org.springframework.ui.velocity.VelocityEngineUtils;

import fedora.common.Constants;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RelationshipTuple;

import ca.ualberta.library.ir.domain.GroupPermission;
import ca.ualberta.library.ir.domain.Handle;
import ca.ualberta.library.ir.domain.Proquest;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.AccessType;
import ca.ualberta.library.ir.enums.ContentModel;
import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.FedoraRelationship;
import ca.ualberta.library.ir.enums.HandleType;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.exception.FedoraAPIException;
import ca.ualberta.library.ir.exception.HandleClientException;
import ca.ualberta.library.ir.exception.UnauthorizedException;
import ca.ualberta.library.ir.localization.LocaleResources;
import ca.ualberta.library.ir.mail.MailServiceManager;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.DublinCore;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.inputform.InputForms;
import ca.ualberta.library.ir.model.inputform.InputFormsXmlBinder;
import ca.ualberta.library.ir.model.metadata.Field.Element;
import ca.ualberta.library.ir.model.metadata.Metadata;
import ca.ualberta.library.ir.model.metadata.MetadataTransformer;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.service.ServiceFacade;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.FileUtils;
import ca.ualberta.library.ir.utils.ThreadPool;
import ca.ualberta.library.ir.utils.UIProperties;

/**
 * The BaseActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5602 $ $Date: 2012-10-05 12:48:09 -0600 (Fri, 05 Oct 2012) $
 */
public abstract class BaseActionBean implements ActionBean, ActionConstants {
	private static final Log log = LogFactory.getLog(BaseActionBean.class);

	// url pattern for resuming to home page
	private static final Pattern urlPattern = Pattern.compile("/activation/confirm|/register|/search");

	protected static final SimpleDateFormat ISODateFormat;

	// resources.properties
	protected ResourceBundle applicationResources;

	protected ApplicationActionBeanContext context;
	protected ServletContext servletContext;
	protected HttpServletRequest request;
	protected HttpSession session;
	protected ServiceFacade services;
	protected MailServiceManager mailServiceManager;
	protected VelocityEngine velocityEngine;
	protected java.util.Properties templates;

	// datestream download url (with contextPath)
	protected String datastreamUrl;

	// current user
	protected User user;

	protected String tempPath;
	protected String targetUrl;
	protected String uiPath;

	// fedora object fields
	protected ObjectFields object;

	// search form fields
	protected String q; // query
	protected String fq; // filter query
	protected String sort; // sort field
	protected String query; // query string

	// stripes flash scope id
	protected Integer flashScopeId;

	protected String urlBinding;

	protected static String durationFormat;

	// metadata input forms config
	protected static InputForms inputForms;

	static {

		// initialize iso date format
		ISODateFormat = new SimpleDateFormat(ISODatePattern);
		ISODateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		// sort supported mimetypes
		Arrays.sort(supportedFileTypes);

		try {
			initInputForms();
		} catch (Exception e) {
			log.error("Could not initialize InputForms!", e);
		}
	}

	/**
	 * The initInputForms method.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void initInputForms() throws Exception {
		// log.debug("loading input-forms.xml...");
		inputForms = InputFormsXmlBinder.unmarshal();
	}

	/**
	 * The BaseActionBean class constructor.
	 */
	public BaseActionBean() {
		super();
	}

	@SpringBean("services")
	public void injectServiceFacade(ServiceFacade services) {
		// log.trace("injecting services..." + services);
		this.services = services;
		this.mailServiceManager = this.services.getMailServiceManager();
		this.velocityEngine = this.services.getVelocityEngine();
		this.templates = this.services.getTemplates();
	}

	@Before(stages = LifecycleStage.HandlerResolution)
	public void setUrlBinding() {
		// get action binding path
		urlBinding = StripesFilter.getConfiguration().getActionResolver().getUrlBinding(this.getClass())
			+ (context.getEventName() == null ? "" : "/" + context.getEventName());
		// log.trace("urlBinding: " + urlBinding);

	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.stripes.action.ActionBean#getContext()
	 */
	public ApplicationActionBeanContext getContext() {
		return this.context;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.stripes.action.ActionBean#setContext(net.sourceforge.stripes.action.ActionBeanContext)
	 */
	public void setContext(ActionBeanContext ctx) {
		// log.trace(">>> initializing actionBean: " + this.getClass().getName());
		// log.trace("setting context...");

		// initialize variables
		context = (ApplicationActionBeanContext) ctx;
		servletContext = context.getServletContext();
		request = context.getRequest();
		session = context.getRequest().getSession();
		applicationResources = LocaleResources.getResourceBundle(context.getLanguage());
		user = context.getUser();
		tempPath = ApplicationProperties.getString("app.temp.path");
		datastreamUrl = request.getContextPath() + dsmUrl;
		String userAgent = StringUtils.trimToEmpty(request.getHeader("User-Agent"));
		// log.trace("run: " + context.context.getAttribute("run"));
		// log.trace("remoteAddr: " + request.getRemoteAddr());
		// log.trace("remoteHost: " + request.getRemoteHost());
		// log.trace("User-Agent: " + userAgent);
		if (webcrawlerFilter.matcher(userAgent).find() || webcrawlerFilter.matcher(request.getRemoteAddr()).find()) {
			session.setMaxInactiveInterval(60); // secounds
		}

		// get uiPath from request
		UIProperties ui = getUIProperties(request);
		uiPath = ui == null ? defaultUIPath : ui.getPath();
		context.getResponse().setContentType(ui == null ? defaultContentType : ui.getContentType());
		// log.trace("uiPath: " + uiPath);

		try {

			// setting encoding targetUrl for redirecting back to previous page before login
			if (request.getPathInfo() == null) {
				targetUrl = "";
			} else if (urlPattern.matcher(request.getPathInfo()).find()) {
				targetUrl = URLEncoder.encode(request.getContextPath(), "UTF-8");
			} else {
				targetUrl = URLEncoder.encode(
					request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo())
						+ (request.getQueryString() == null ? "" : "?" + request.getQueryString()), "UTF-8");
			}
		} catch (Exception e) {
			log.error("Could not create target URL!", e);
			targetUrl = "";
		}

		// duration format for server process
		durationFormat = applicationResources.getString("duration.format");

		// if (log.isTraceEnabled()) {
		// log.trace("------------------- request info -------------------");
		// log.trace("remote host: [" + request.getRemoteHost() + "] port: [" + request.getRemotePort()
		// + "] address: [" + request.getRemoteAddr() + "] User-Agent: [" + request.getHeader("User-Agent") + "]");
		// log.trace("url: " + request.getRequestURL() + " query: " + request.getQueryString());
		// log.trace("header:");
		// Enumeration<String> hds = request.getHeaderNames();
		// while (hds.hasMoreElements()) {
		// String hd = hds.nextElement();
		// log.trace("- " + hd + ": " + request.getHeader(hd));
		// }
		// log.trace("cookies:");
		// Cookie[] cookies = request.getCookies();
		// for (Cookie cookie : cookies) {
		// log.trace(cookie.getName() + ", domain: " + cookie.getDomain() + ", path: " + cookie.getPath()
		// + ", value: " + cookie.getValue());
		// }
		// log.trace("----------------------------------------------------");
		// }
	}

	/**
	 * The getObjectPID method provides a current object PID for object access control interceptor (with role /object/*)
	 * to grant or deny access by checking on object relationships. Override this method in child class to provide the
	 * object PID to the interceptor. Otherwise, the exception will be thrown.
	 * 
	 * @return the object PID.
	 */
	public String getObjectPID() {
		return null;
	}

	public String getDatastreamID() {
		return null;
	}

	public boolean isUserInRoles(User user, String... roles) {
		if (user == null || roles.length == 0) {
			return false;
		}

		try {
			return services.isUserPermissionAllowed(user, roles);
		} catch (UnauthorizedException e) {
			if (services.isGroupPermissionAllowed(user.getGroup(), roles)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public Resolution foxml(String pid) {
		try {
			byte[] objXml = services.getObjectXML(pid);
			return new StreamingResolution("text/xml", new String(objXml));
		} catch (Exception e) {
			log.error("Could not get object xml!", e);
			return forwardExceptionError("Could not get object xml!", e);
		}
	}

	public Resolution solrxml(String pid) {
		try {
			byte[] solrXml = services.findItemXml(pid);
			return new StreamingResolution("text/xml", new String(solrXml));
		} catch (Exception e) {
			log.error("Could not get object xml!", e);
			return forwardExceptionError("Could not get object xml!", e);
		}
	}

	protected void generateThumbnail(final FileBean file, final String pid, final WorkflowState state) {
		// log.debug("start: generate thumbnail...");
		if (file != null) {
			File tmp = null;
			File thumbnail = null;
			try {
				tmp = File.createTempFile("upload_", ".tmp", new File(tempPath));
				file.save(tmp);

				// generate thumbnail
				thumbnail = services.generateThumbnail(file.getContentType(), ActionConstants.thumbnailWidth,
					ActionConstants.thumbnailHeight, tmp);

				// add thumbnail datastream
				services.addDatastream(pid, DatastreamID.THUMBNAIL.toString(), "Item Thumbnail",
					FileUtils.getContentType(thumbnail), new FileInputStream(thumbnail), formatLogMessage(state));
			} catch (Exception e) {
				log.warn("Could not generate thumbnail!", e);
			} finally {
				if (tmp != null) {
					tmp.delete();
				}
				if (thumbnail != null) {
					thumbnail.delete();
				}
			}
		}
		// log.debug("end: generate thumbnail.");
	}

	public String formatLogMessage(WorkflowState state) {
		return formatLogMessage(state, state.name());
	}

	public String formatLogMessage(WorkflowState state, String messages) {
		return logMessageFormat.format(new String[] { urlBinding, user.getUsername(), state.toString(), messages });
	}

	protected String mergeTemplate(String template, String type, String language, Map<String, Object> model) {
		String file = MessageFormat.format(templates.getProperty(template), type, language);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, file, model);
		return text;
	}

	public Item getItemByPid(String pid) throws Exception {

		// log.trace("start: getting item pid: " + pid);
		Item item = new Item();
		ObjectFields obj = services.findObjectByPid(resultFields, pid);

		// get item properties
		Properties properties = new Properties();
		properties.setPid(pid);
		properties.setLabel(obj.getLabel());
		properties.setOwnerId(obj.getOwnerId());
		properties.setState(obj.getState());
		properties.setModifiedDate(obj.getMDate());
		properties.setCreatedDate(obj.getCDate());

		// set item owners
		List<User> owners = new ArrayList<User>();
		String[] ownerIds = ActionConstants.commaPattern.split(obj.getOwnerId());
		for (String owner : ownerIds) {
			owners.add(services.getUser(owner));
		}
		properties.setOwners(owners);

		// set item relationships properties
		List<Community> coms = new ArrayList<Community>();
		List<ca.ualberta.library.ir.model.solr.Collection> cols = new ArrayList<ca.ualberta.library.ir.model.solr.Collection>();

		// get all object relationships
		RelationshipTuple[] rels = services.getRelationships(pid, null);
		for (RelationshipTuple rel : rels) {
			// log.debug("rel.predicate: " + rel.getPredicate());
			if (rel.getPredicate().equals(Constants.MODEL.HAS_MODEL.uri)
				&& rel.getObject().startsWith(ContentModel.getURIPrefix())) {
				// set content model
				properties.setContentModel(rel.getObject().split(ContentModel.NAMESPACE + ":")[1]);
			} else if (rel.getPredicate().equals(FedoraRelationship.IS_PART_OF.getURI())) {
				if (rel.getObject().equals(PartOfRelationship.EMBARGOED.getURI())) {
					// embargoed
					properties.setEmbargoed(true);
				} else if (rel.getObject().equals(PartOfRelationship.CCID_AUTH.getURI())) {
					// ccid protected
					properties.setAccessType(AccessType.CCID_PROTECTED);
				} else if (rel.getObject().equals(PartOfRelationship.DARK_REPOSITORY.getURI())) {
					// dark item
					properties.setAccessType(AccessType.NOONE);
				} else if (rel.getObject().equals(PartOfRelationship.MANUAL_APPROVAL.getURI())) {
					// manual approval
					properties.setManualApproval(true);
				}
			} else if (rel.getPredicate().equals(Definitions.EMBARGOED_DATE.getURI())) {
				try {
					// set embargoed date
					// SimpleDateFormat isoFormat = new SimpleDateFormat(ISODatePattern);
					SimpleDateFormat dateFormat = new SimpleDateFormat(embargoedDatePattern);
					Date emDate = ISODateFormat.parse(rel.getObject());
					properties.setEmbargoedDate(dateFormat.format(emDate));
				} catch (Exception e) {
					log.error("Could not set embargoed item properties!", e);
				}
			} else if (rel.getPredicate().equals(FedoraRelationship.IS_MEMBER_OF.getURI())) {

				// get community from solr index
				String comPid = slashPattern.split(rel.getObject())[1];
				try {
					coms.add(services.getCommunity(comPid));
				} catch (Exception e) {
					log.warn("Could not find community: " + comPid + "!", e);
				}

			} else if (rel.getPredicate().equals(FedoraRelationship.IS_MEMBER_OF_COLLECTION.getURI())) {

				// get community from solr index
				String colPid = slashPattern.split(rel.getObject())[1];
				try {
					cols.add(services.getCollection(colPid));
				} catch (Exception e) {
					log.warn("Could not find collection: " + colPid + "!", e);
				}

			} else if (rel.getPredicate().equals(Definitions.WORKFLOW_STATE.getURI())
			// fix wrong uri for old item
				|| rel.getPredicate().equals("http://era.library.ualbertaca/schema/definitions.xsd#workflowState")) {
				// item workflow state
				properties.setWorkflowState(rel.getObject());
			} else if (rel.getPredicate().equals(Definitions.WORKFLOW_DATE.getURI())) {
				// item workflow modified date
				properties.setWorkflowDate(rel.getObject());
			} else if (rel.getPredicate().equals(Definitions.COMMENTS.getURI())) {
				// item comments
				item.setComments(rel.getObject());
			} else if (rel.getPredicate().equals(Definitions.USER_ID.getURI())) {
				// item modified user id
				properties.setUserId(rel.getObject());
			} else if (rel.getPredicate().equals(Definitions.SUBMITTER_ID.getURI())) {
				// item submitter user id
				properties.setSubmitterId(rel.getObject());
			} else if (rel.getPredicate().equals(Definitions.FORM_NAME.getURI())) {
				// item input form name
				properties.setFormName(rel.getObject());
			}
		}
		if (properties.getAccessType() == null) {
			properties.setAccessType(AccessType.PUBLIC);
		}

		// set item properties
		item.setProperties(properties);

		// set item communities
		item.setCommunities(coms);

		// set item collections
		item.setCollections(cols);

		// set datestreams: metadata, license, files
		ArrayList<Datastream> datastreams = new ArrayList<Datastream>();
		fedora.server.types.gen.Datastream[] dstms = services.getDatastreams(pid);
		for (fedora.server.types.gen.Datastream dstm : dstms) {
			// log.trace("id: " + dstm.getID() + " location: " + dstm.getLocation() + " mimeType: " +
			// dstm.getMIMEType());

			if (dstm.getID().equals(DatastreamID.DCQ.toString())) {

				// create dcq metadata
				InputStream is = services.getDatastreamContent(pid, dstm.getID());
				Metadata metadata = MetadataTransformer.datastream2metadata(is);
				item.setMetadata(metadata);
				item.getProperties().setHasMetadata(true);

			} else if (dstm.getID().equals(DatastreamID.THUMBNAIL.toString())) {

				// thumbnail
				Datastream thumbnail = new Datastream();
				thumbnail.setPid(pid);
				thumbnail.setDsId(dstm.getID());
				thumbnail.setLabel(new String(dstm.getLabel()));
				thumbnail.setMimeType(dstm.getMIMEType());
				thumbnail.setSize(dstm.getSize());
				thumbnail.setLocation(dstm.getLocation());
				thumbnail.setExternalLocation(dstm.getLocation());
				thumbnail.setControlGroup(dstm.getControlGroup().getValue());
				thumbnail.setCreatedDate(dstm.getCreateDate());
				thumbnail.setState(dstm.getState());
				item.setThumbnail(thumbnail);

			} else if (dstm.getID().equals(DatastreamID.LICENSE.toString())) {

				// license datastream
				Datastream license = new Datastream();
				license.setPid(pid);
				license.setDsId(dstm.getID());
				license.setLabel(new String(dstm.getLabel()));
				license.setMimeType(dstm.getMIMEType());
				license.setLocation(dstm.getLocation());
				license.setExternalLocation(dstm.getLocation());
				license.setControlGroup(dstm.getControlGroup().getValue());
				license.setCreatedDate(dstm.getCreateDate());
				license.setState(dstm.getState());
				item.setLicense(license);

			} else if (dstm.getID().startsWith(DatastreamID.DS.toString())) {

				// datastreams
				Datastream datastream = new Datastream();
				datastream.setPid(pid);
				datastream.setDsId(dstm.getID());
				datastream.setLabel(new String(dstm.getLabel()));
				datastream.setMimeType(dstm.getMIMEType());
				datastream.setSize(dstm.getSize());
				datastream.setLocation(dstm.getLocation());
				datastream.setExternalLocation(dstm.getLocation());
				datastream.setControlGroup(dstm.getControlGroup().getValue());
				datastream.setCreatedDate(dstm.getCreateDate());
				datastream.setState(dstm.getState());
				datastreams.add(datastream);
			}
		}

		// set item contents
		item.setDatastreams(datastreams);

		// backward complatible with DC
		if (item.getMetadata() == null) {

			// transform dc to metadata
			InputStream is = services.getDatastreamContent(pid, DatastreamID.DC.toString());
			Metadata metadata = MetadataTransformer.datastream2metadata(is);
			item.setMetadata(metadata);
		}
		// log.trace("end: getting item pid: " + pid);
		return item;
	}

	public Handle saveHandle(String pid, HandleType type) {
		if (handleEnabled) {
			Handle handle = services.getHandleByPid(pid);
			if (handle == null) {

				// create handle
				handle = new Handle();
				handle.setPid(pid);
				handle.setType(type.getValue());
				services.saveOrUpdateHandle(handle);
				String url = httpServerUrl + (request.getContextPath().equals("/") ? "" : request.getContextPath())
					+ "/public/view/" + type.getName() + "/" + pid;
				try {
					services.createHandle(url, buildHandle(handle.getId()));
				} catch (HandleClientException e) {
					log.error("Could not save the handle!", e);
					return null;
				}
			}
			return handle;
		} else {
			return null;
		}
	}

	public void deleteHandle(String pid) {
		if (handleEnabled) {
			Handle handle = services.getHandleByPid(pid);
			if (handle != null) {
				try {
					services.deleteHandle(buildHandle(handle.getId()));
				} catch (HandleClientException e) {
					log.error("Could not delete the handle!", e);
				}
				services.deleteHandle(handle.getId());
			}
		}
	}

	public String getHandleURL(String pid) {
		String url = null;
		Handle handle = services.getHandleByPid(pid);
		if (handle != null) {
			url = new StringBuilder(handleServer).append("/").append(buildHandle(handle.getId())).toString();
		} else {
			try {
				ca.ualberta.library.ir.model.solr.Item item = services.findObjectByPid(pid)
					.getBeans(ca.ualberta.library.ir.model.solr.Item.class).iterator().next();
				url = item.getHandle();
			} catch (Exception e) {
				url = new StringBuilder(httpServerUrl).append(request.getContextPath()).append("/public/view/item/")
					.append(pid).toString();
			}
		}
		return url;
	}

	private static UIProperties getUIProperties(HttpServletRequest request) {
		String rui = request.getParameter("_ui");
		if (rui != null) {
			request.getSession().setAttribute("_ui", rui);
		}
		rui = (String) request.getSession().getAttribute("_ui");
		if (rui != null) {
			for (UIProperties ui : BaseActionBean.uiList) {
				if (ui.getId().equals(rui)) {
					return ui;
				}
			}
		} else {
			String userAgent = request.getHeader("User-Agent");
			// // log.trace("userAgent: " + userAgent);
			for (UIProperties ui : BaseActionBean.uiList) {
				// // log.trace("ui: " + ui.getUserAgent());
				if (ui.getUserAgent().matcher(userAgent).find()) {
					return ui;
				}
			}
		}
		return null;
	}

	public String buildHandle(String pid, HandleType type) {
		String handle = buildHandleUriFromPid(pid);
		if (handle == null) {
			return httpServerUrl + request.getContextPath() + "/public/view/" + type.getName() + "/" + pid;
		} else {
			return handle;
		}
	}

	public String buildHandleUriFromPid(String pid) {
		Handle handle = services.getHandleByPid(pid);
		if (handle != null) {
			return handleServer + "/" + buildHandle(handle.getId());
		} else {
			return null;
		}
	}

	public String buildHandle(int handleId) {
		return new StringBuilder(handlePrefix).append("/")
			.append(StringUtils.trimToNull(irPrefix) == null ? "" : irPrefix + ".").append(String.valueOf(handleId))
			.toString();
	}

	public boolean canAccessDarkRepository(String pid) {
		GroupPermission perm = null;
		if (context.getUser() != null) {
			perm = services.getGroupPermissionByGroupId(user.getGroup().getId(),
				SystemPermissions.ADMIN_DARK.getPermission());
		}
		if (perm == null) {
			return false;
		} else {
			return perm.isAllowed();
		}
	}

	public boolean canAccessEmbargoed(String pid) {
		GroupPermission perm = null;
		if (context.getUser() != null) {
			perm = services.getGroupPermissionByGroupId(user.getGroup().getId(),
				SystemPermissions.ADMIN_EMBARGOED.getPermission());
		}
		if (perm == null) {
			return false;
		} else {
			return perm.isAllowed();
		}
	}

	public Resolution forwardUnauthorized() {
		return new ForwardResolution(uiPath + "/public/unauthorized.jsp");
	}

	public Resolution redirectCCIDLogin() throws UnsupportedEncodingException {
		String url = httpsServerUrl + request.getContextPath() + "/public/login?mode=ccid&url="
			+ URLEncoder.encode(httpServerUrl + request.getContextPath(), "UTF-8") + targetUrl;
		// log.trace("redirecting to ccid login url: " + url);
		return new RedirectResolution(url, false);
	}

	public ObjectFields getObject(String pid) throws Exception, UnauthorizedException {
		if (object != null) {
			return object;
		}

		// find fedora object
		try {
			// log.trace("getting object: " + pid + "...");
			object = services.findObjectByPid(resultFields, pid);
			if (object == null) {
				context.getValidationErrors().addGlobalError(new LocalizableError("errors.objectNotFound"));
				throw new Exception("Object Not Found!");
			}
		} catch (FedoraAPIException e) {
			context.getValidationErrors().addGlobalError(new LocalizableError("errors.objectNotFound"));
			throw e;
		}
		return object;
	}

	public Resolution forwardExceptionError(String message, Exception e) {
		try {
			context.getValidationErrors().addGlobalError(
				new LocalizableError("errors.exception.messages", new Object[] { message, e.getLocalizedMessage() }));
		} catch (Exception ex) {
			log.error("Forward to global exception error page error!", ex);
		}
		return new ForwardResolution(uiPath + "/public/exceptionError.jsp");
	}

	public Resolution forwardErrorMessage(Object... messages) {
		try {
			context.getValidationErrors().addGlobalError(new LocalizableError("errors.message.messages", messages));
		} catch (Exception ex) {
			log.error("Forward to global exception error page error!", ex);
		}
		return new ForwardResolution(uiPath + "/public/errorMessage.jsp");
	}

	public Resolution forwardMessage(String key, Object... params) {
		try {
			context.getMessages().add(new LocalizableMessage(key, params));
		} catch (Exception ex) {
			log.error("Forward to application message page error!", ex);
		}
		return new ForwardResolution(uiPath + "/public/applicationMessage.jsp");
	}

	public ServiceFacade getServices() {
		return this.services;
	}

	public static String trimTitle(String title) {
		return trim(title, 32);
	}

	public static String trim(String title, int length) {
		String[] str = StringUtils.trimToEmpty(title).split("\\t|\\r\\n");
		return str[0].length() <= length ? str[0] : WordUtils.abbreviate(str[0], length, -1, "...");
	}

	public void getCommunityDetails(ObjectFields object, Community community) throws FedoraAPIException {
		community.setId(object.getPid());
		community.setState(object.getState());
		community.setTitle(object.getLabel());
		DcDocument dc = services.getDublinCore(object.getPid());
		// log.trace(dc.toString());
		DublinCore dublinCore = new DublinCore(dc);
		community.setDescription(dublinCore.getFields().get(4).getValues().get(0));
		community.setModifiedDate(object.getMDate());
		community.setCreatedDate(object.getCDate());
		community.setOwnerId(object.getOwnerId());
	}

	public boolean hasThumbnail(String pid) {
		boolean hasThumbnail = false;
		try {
			DatastreamDef[] defs = services.listDatastreams(pid);
			for (DatastreamDef def : defs) {
				if (def.getID().equals(DatastreamID.THUMBNAIL.toString())) {
					hasThumbnail = true;
					break;
				}
			}
		} catch (Exception e) {
			hasThumbnail = false;
		}
		// log.trace("hasThumbnail: " + hasThumbnail);
		return hasThumbnail;
	}

	public boolean isValidUrl(String url) {
		try {
			// log.trace("validating url... " + url);
			@SuppressWarnings("unused")
			URLConnection con = new URL(url).openConnection();
			return true;
		} catch (Exception e) {
			log.warn("Invalid url!", e);
			return false;
		}
	}

	/**
	 * The addProquestRecord method.
	 * 
	 * @param item2
	 */
	public void addProquestRecord(final Item item) throws Exception {
		ThreadPool.getCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				// log.debug("adding proquest upload record...");
				String author = item.getMetadata().getFieldMap().get(Element.dc_creator.toString()).get(0).getValue();
				Proquest proquest = new Proquest();
				proquest.setAuthor(author);
				proquest.setPid(item.getProperties().getPid());
				proquest.setState(ca.ualberta.library.ir.enums.State.A.getValue());
				proquest.setCreatedDate(new Date());
				services.saveOrUpdateProquest(proquest);
			}
		});
	}

	/**
	 * The getCurrentFlashScope method.
	 * 
	 * @return
	 */
	public FlashScope getFlashScope() {

		// get current flash scope
		FlashScope flash = FlashScope.getCurrent(request, true);
		flashScopeId = flash.key();

		// set timeout to 5 mins
		flash.setTimeout(300);
		return flash;
	}

	public boolean isManualApproval(String pid) {
		// check collection relationship
		return isPartOfRelationship(PartOfRelationship.MANUAL_APPROVAL, pid, true);
	}

	public boolean isDarkRepository(String pid) {
		// check item relationship
		return isPartOfRelationship(PartOfRelationship.DARK_REPOSITORY, pid, false);
	}

	public boolean isCCIDAuth(String pid) {
		// check item relationship
		return isPartOfRelationship(PartOfRelationship.CCID_AUTH, pid, false);
	}

	public boolean isCCIDAuth(String pid, boolean deep) {
		// check item deep relationship, check collection and community relationship
		return isPartOfRelationship(PartOfRelationship.CCID_AUTH, pid, deep);
	}

	public boolean isPartOfRelationship(final PartOfRelationship partOfRelationship, final String pid,
		final boolean deep) {
		try {
			QueryResponse resp = services.findObjectByPid(pid);
			SolrDocumentList results = resp.getResults();
			if (results.isEmpty()) {
				return false;
			}
			SolrDocument doc = results.iterator().next();
			Collection<Object> values = doc.getFieldValues(FedoraRelationship.IS_PART_OF.getFieldName());

			// check object isPartOf relationship
			if (values != null && values.contains(partOfRelationship.getValue())) {
				return true;

			} else if (deep) {

				// check object isMemberOfCollection relationship (Collection)
				Collection<Object> cols = doc.getFieldValues(FedoraRelationship.IS_MEMBER_OF_COLLECTION.getFieldName());
				if (cols != null) {
					for (Object col : cols) {
						// log.trace("checking collection: " + col);
						resp = services.findObjectByPid((String) col);
						results = resp.getResults();
						for (SolrDocument result : results) {
							values = result.getFieldValues(FedoraRelationship.IS_PART_OF.getFieldName());
							// log.trace("isPartOf: " + values);
							if (values != null && values.contains(partOfRelationship.getValue())) {
								return true;
							}

							// check object isMemberOf relationship (Community)
							Collection<Object> coms = result.getFieldValues(FedoraRelationship.IS_MEMBER_OF
								.getFieldName());
							if (coms != null) {
								for (Object com : coms) {
									// log.trace("checking collection community: " + com);
									QueryResponse res = services.findObjectByPid((String) com);
									SolrDocumentList rs = res.getResults();
									for (SolrDocument sd : rs) {
										values = sd.getFieldValues(FedoraRelationship.IS_PART_OF.getFieldName());
										// log.trace("isPartOf: " + values);
										if (values != null && values.contains(partOfRelationship.getValue())) {
											return true;
										}
									}
								}
							}
						}
					}
				}

				// check object isMemberOf relationship (Community)
				Collection<Object> coms = doc.getFieldValues(FedoraRelationship.IS_MEMBER_OF.getFieldName());
				if (coms != null) {
					for (Object com : coms) {
						// log.trace("checking community: " + com);
						resp = services.findObjectByPid((String) com);
						results = resp.getResults();
						for (SolrDocument result : results) {
							values = result.getFieldValues(FedoraRelationship.IS_PART_OF.getFieldName());
							// log.trace("isPartOf: " + values);
							if (values != null && values.contains(partOfRelationship.getValue())) {
								return true;
							}
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error("Checking isPartOf Relationship Error!", e);
			return false;
		}
	}

	protected boolean getDownloadDatastreamContent(int n, String pid, String dsId, File tmpDir, String itemPattern,
		String title) throws FedoraAPIException {
		boolean ret = false;
		try {
			fedora.server.types.gen.Datastream ds = null;
			InputStream in = null;
			String ext = "";

			ds = services.getDatastream(pid, dsId);
			try {
				in = services.getDatastreamContent(pid, dsId);
			} catch (FedoraAPIException e) {
				log.error("Could not find datastream content!", e);
				throw e;
			}

			try {
				// add file extension from resources
				String rs = applicationResources.getString(ds.getMIMEType() + ".ext");
				String[] exts = commaPattern.split(rs);
				ext = exts[0].trim();
			} catch (Exception e) {
				log.warn("Could not find file extension: " + ds.getMIMEType() + ".ext!", e);
			}

			String stitle = StringUtils.abbreviate(FileUtils.toWindowsFilename(title), 30);
			String filename = MessageFormat.format(itemPattern, n, stitle, dsId,
				StringUtils.abbreviate(ds.getLabel(), 30), ext);
			File tmp = new File(tmpDir, filename);
			FileOutputStream out = new FileOutputStream(tmp);
			// log.trace("writing datastream: " + filename + "...");
			IOUtils.copy(in, out);
			in.close();
			out.flush();
			out.close();

			if (tmp.length() > downloadFileSize * 1048576) {
				// log.trace("oversize item: " + title + " size: " + (tmp.length() / 1048576) + "m.");
				tmp.delete();
				ret = false;
			} else {
				tmp.deleteOnExit();
				ret = true;
			}
		} catch (IOException e) {
			log.error("Get datastream content error!", e);
		}
		return ret;
	}

	protected void postFacebookMessage(final Item item) {

		// send email to user in background process
		ThreadPool.getCachedThreadPool().execute(new Runnable() {

			public void run() {
				try {
					if (item.getProperties().getWorkflowState().equals(WorkflowState.Archive.toString())
						&& !item.getProperties().isEmbargoed()) {
						String message = applicationResources.getString("facebook.new.item.message");
						ca.ualberta.library.ir.model.solr.Item itm = services
							.findObjectByPid(item.getProperties().getPid())
							.getBeans(ca.ualberta.library.ir.model.solr.Item.class).get(0);
						services.getFacebookService().postMessage(itm, message, Locale.getDefault().getLanguage());
					}
				} catch (Exception e) {
					log.error("Could not publisher facebook post item!", e);
				}
			}
		});
	}

	/**
	 * The getTmpPath getter method.
	 * 
	 * @return the tempPath
	 */
	public String getTempPath() {
		return tempPath;
	}

	/**
	 * The getFedoraRestServiceUrl getter method.
	 * 
	 * @return the fedoraRestServiceUrl
	 */
	public String getDatastreamUrl() {
		return datastreamUrl;
	}

	/**
	 * The getDatePattern getter method.
	 * 
	 * @return the datePattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * The getDateFormat getter method.
	 * 
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * The getMailAdmin getter method.
	 * 
	 * @return the mailAdmin
	 */
	public String getMailAdmin() {
		return mailAdmin;
	}

	/**
	 * The getServerUrl getter method.
	 * 
	 * @return the serverUrl
	 */
	public String getHttpServerUrl() {
		return httpServerUrl;
	}

	/**
	 * The getFedoraServerUrl getter method.
	 * 
	 * @return the fedoraServerUrl
	 */
	public String getFedoraServerUrl() {
		return fedoraServerUrl;
	}

	/**
	 * The getFedoraRestServiceUrl getter method.
	 * 
	 * @return the fedoraRestServiceUrl
	 */
	public String getFedoraRestServiceUrl() {
		return fedoraRestServiceUrl;
	}

	/**
	 * The getHttpsServerUrl getter method.
	 * 
	 * @return the httpsServerUrl
	 */
	public String getHttpsServerUrl() {
		return httpsServerUrl;
	}

	/**
	 * The getHandleServer getter method.
	 * 
	 * @return the handleServer
	 */
	public String getHandleServer() {
		return handleServer;
	}

	/**
	 * The getTargetUrl getter method.
	 * 
	 * @return the targetUrl
	 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * The getUiPath getter method.
	 * 
	 * @return the uiPath
	 */
	public String getUiPath() {
		return uiPath;
	}

	/**
	 * The getSupportedFileTypes getter method.
	 * 
	 * @return the supportedFileTypes
	 */
	public String[] getSupportedFileTypes() {
		return supportedFileTypes;
	}

	/**
	 * The getDownloadFileSize getter method.
	 * 
	 * @return the downloadFileSize
	 */
	public float getDownloadFileSize() {
		return downloadFileSize;
	}

	/**
	 * The getUser getter method.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * The getServletContext getter method.
	 * 
	 * @return the servletContext
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * The getRequest getter method.
	 * 
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * The getSession getter method.
	 * 
	 * @return the session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * The getContentModelTitle method.
	 * 
	 * @param cModel
	 * @return
	 */
	public String getContentModelTitle(String cModel) {
		try {
			return applicationResources.getString("ContentModel." + cModel);
		} catch (Exception e) {
			return cModel;
		}
	}

	/**
	 * The isProaiEnabled getter method.
	 * 
	 * @return the proaiEnabled
	 */
	public static boolean isProaiEnabled() {
		return proaiEnabled;
	}

	/**
	 * The getProaiItemId getter method.
	 * 
	 * @return the proaiItemId
	 */
	public static String getProaiItemId() {
		return proaiItemId;
	}

	/**
	 * The getQ getter method.
	 * 
	 * @return the q
	 */
	public String getQ() {
		return q;
	}

	/**
	 * The setQ setter method.
	 * 
	 * @param q the q to set
	 */
	public void setQ(String q) {
		this.q = q;
	}

	/**
	 * The getFq getter method.
	 * 
	 * @return the fq
	 */
	public String getFq() {
		return fq;
	}

	/**
	 * The setFq setter method.
	 * 
	 * @param fq the fq to set
	 */
	public void setFq(String fq) {
		this.fq = fq;
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
	 * The getQuery getter method.
	 * 
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * The setQuery setter method.
	 * 
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * The getFlashScopeId getter method.
	 * 
	 * @return the flashScopeId
	 */
	public Integer getFlashScopeId() {
		return flashScopeId;
	}

	/**
	 * The setFlashScopeId setter method.
	 * 
	 * @param flashScopeId the flashScopeId to set
	 */
	public void setFlashScopeId(Integer flashScopeId) {
		this.flashScopeId = flashScopeId;
	}

	/**
	 * The getDateformatshort getter method.
	 * 
	 * @return the dateformatshort
	 */
	public String getDateFormatShort() {
		return dateFormatShort;
	}

	/**
	 * The getMailServiceManager getter method.
	 * 
	 * @return the mailServiceManager
	 */
	public MailServiceManager getMailServiceManager() {
		return mailServiceManager;
	}

	/**
	 * The getUrlBinding getter method.
	 * 
	 * @return the urlBinding
	 */
	public String getUrlBinding() {
		return urlBinding;
	}

	/**
	 * The getInputforms getter method.
	 * 
	 * @return the inputforms
	 */
	public InputForms getInputForms() {
		return inputForms;
	}
}
