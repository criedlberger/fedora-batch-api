/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MaintenanceActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fedora.server.types.gen.RelationshipTuple;

import ca.ualberta.library.ir.domain.Download;
import ca.ualberta.library.ir.domain.Scheduler;
import ca.ualberta.library.ir.enums.Definitions;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.model.solr.Item;
import ca.ualberta.library.ir.scheduling.EmbargoedItemPublisher;
import ca.ualberta.library.ir.scheduling.FacebookPublisher;
import ca.ualberta.library.ir.scheduling.IndexBuilder;
import ca.ualberta.library.ir.scheduling.ProquestUpload;
import ca.ualberta.library.ir.scheduling.SubscriptionNotifier;
import ca.ualberta.library.ir.service.ServiceFacade;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The MaintenanceActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/dev/main/{$event}")
@Secure(roles = "/dev/main")
public class MaintenanceActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(MaintenanceActionBean.class);
	private List<String> events;
	private Map<String, String> messages;
	private EmbargoedItemPublisher embargoedPublisher;
	private SubscriptionNotifier subscriptionNotifier;
	private IndexBuilder indexBuilder;
	private ProquestUpload proquestUpload;
	private FacebookPublisher facebookPublisher;
	private List<Item> items;
	private List<String> params;

	/**
	 * The MaintenanceActionBean class constructor.
	 */
	public MaintenanceActionBean() {
		super();
		try {
			events = new ArrayList<String>();
			messages = new HashMap<String, String>();
			Method[] methods = MaintenanceActionBean.class.getMethods();
			for (Method method : methods) {
				HandlesEvent event = method.getAnnotation(HandlesEvent.class);
				if (event != null) {
					events.add(event.value());
				}
			}
			Collections.sort(events);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
		}
	}

	@SpringBean("subscriptionNotifier")
	public void injectServiceFacade(SubscriptionNotifier notifier) {
		// log.trace("injecting subscriptionNotifier bean...");
		this.subscriptionNotifier = notifier;
	}

	@SpringBean("embargoedItemPublisher")
	public void injectServiceFacade(EmbargoedItemPublisher publisher) {
		// log.trace("injecting embargoedItemPublisher bean...");
		this.embargoedPublisher = publisher;
	}

	@SpringBean("indexBuilder")
	public void injectServiceFacade(IndexBuilder indexBuilder) {
		// log.trace("injecting indexBuilder bean...");
		this.indexBuilder = indexBuilder;
	}

	@SpringBean("proquestUpload")
	public void injectServiceFacade(ProquestUpload proquestUpload) {
		// log.trace("injecting proquestUpload bean...");
		this.proquestUpload = proquestUpload;
	}

	@SpringBean("facebookPublisher")
	public void injectServiceFacade(FacebookPublisher publisher) {
		// log.trace("injecting proquestUpload bean...");
		this.facebookPublisher = publisher;
	}

	@HandlesEvent("init")
	@DefaultHandler
	public Resolution init() {
		return new ForwardResolution(uiPath + "/protected/maintenance.jsp");
	}

	@Before(stages = LifecycleStage.ResolutionExecution)
	public void initialize() {
		// log.debug("initializing...");
		Scheduler subscriptionScheduler = services.getSchedulerByName(SubscriptionNotifier.class.getName());
		addSchedulerMessage("notifysubscriber", subscriptionScheduler);
		Scheduler embargoedScheduler = services.getSchedulerByName(EmbargoedItemPublisher.class.getName());
		addSchedulerMessage("publishembargoed", embargoedScheduler);
		Scheduler indexScheduler = services.getSchedulerByName(IndexBuilder.class.getName());
		addSchedulerMessage("buildindex", indexScheduler);
		Scheduler proquestScheduler = services.getSchedulerByName(ProquestUpload.class.getName());
		addSchedulerMessage("proquestupload", proquestScheduler);
		Scheduler facebookScheduler = services.getSchedulerByName(FacebookPublisher.class.getName());
		addSchedulerMessage("postfacebookmessage", facebookScheduler);
	}

	/**
	 * The addSchedulerMessage method.
	 * 
	 * @param scheduler
	 */
	private void addSchedulerMessage(String name, Scheduler scheduler) {
		String message = MessageFormat.format(
			"start: {0} stop: {1} process took: {2,number,##} hrs. {3,number,##} mins. {4,number,##.###} secs.",
			scheduler.getStartTime(), scheduler.getStopTime() == null ? "" : scheduler.getStopTime(),
			scheduler.getHours() == null ? 0 : scheduler.getHours(),
			scheduler.getMinutes() == null ? 0 : scheduler.getMinutes(),
			scheduler.getSeconds() == null ? 0 : scheduler.getSeconds());
		messages.put(name, message);
	}

	@HandlesEvent("purgeall")
	@Secure(roles = "/dev/main/purgeall")
	public Resolution purgeAll() {
		try {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						log.info("purging all deleted items...");
						QueryResponse response = services.findItemsByState(State.D.getName(), 0, Integer.MAX_VALUE,
							null);
						SolrDocumentList docs = response.getResults();
						for (SolrDocument doc : docs) {
							String pid = (String) doc.getFieldValue("PID");
							log.info("purging pid: " + pid);
							services.purgeObject(pid, "Purged by system maintenance");
						}
					} catch (Exception e) {
						log.error("Could not purge all deleted items!", e);
					}
				}
			});
			thread.start();
			context.getMessages().add(new SimpleMessage("Purging all deleted items..."));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("oaigetrecord")
	@Secure(roles = "/dev/main/oaigetrecord")
	public Resolution oaiGetRecord() {
		try {
			if (params == null) {
				context.getMessages().add(
					new SimpleMessage("Please choose metadataPrefix and enter object identifier."));
				return new ForwardResolution("/jsp/protected/maintenanceForms.jsp");
			}
			log.info("getting OAI record...");
			return new RedirectResolution(MessageFormat.format(
				"/oaiprovider?verb=GetRecord&metadataPrefix={0}&identifier=oai:era.library.ualberta.ca:{1}",
				params.get(0), params.get(1)));
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("updatedownloadcollectionpids")
	@Secure(roles = "/dev/main/updatedownloadcollectionpids")
	public Resolution updateDownloadCollectionPids() {
		try {
			log.info("updating download collection pids...");
			new UpdateDownloadThread(services).start();
			context.getMessages().add(new SimpleMessage("Update download collection pids is processing..."));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	class UpdateDownloadThread extends Thread {

		private final ServiceFacade services;

		public UpdateDownloadThread(ServiceFacade services) {
			this.services = services;
		}

		@Override
		public void run() {
			log.info("update download collection pids is processing...");
			List<Download> dls = services.getAllDownloads();
			for (Download dl : dls) {
				String dsId = dl.getDsId() == null || dl.getDsId().equals("DS") ? "DS1" : dl.getDsId();
				dl.setDsId(dsId);
				// log.debug("processing pid: " + dl.getPid() + "/" + dsId + "...");
				new SaveDownloadThread(context, dl).start();
			}
			log.info("update download collection pids success.");
		}
	}

	@HandlesEvent("getobjectxml")
	@Secure(roles = "/dev/main/getobjectxml")
	public Resolution getObjectXml() {
		try {
			log.info("getting object XML...");
			if (params == null) {
				context.getMessages().add(new SimpleMessage("Please enter object PID."));
				return new ForwardResolution("/jsp/protected/maintenanceForms.jsp");
			}
			byte[] objXml = services.getObjectXML(params.get(0));
			return new StreamingResolution("text/xml", new String(objXml));
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("reloadinputforms")
	@Secure(roles = "/dev/main/reloadinputforms")
	public Resolution reloadInputForms() {
		try {
			log.info("reloading input forms...");
			initInputForms();
			context.getMessages().add(new SimpleMessage("Submission input forms configuration reloaded."));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("updaterelationshipuri")
	@Secure(roles = "/dev/main/updaterelationshipuri")
	public Resolution updateRelationshipUri() {
		try {
			Thread thread = new Thread(new Runnable() {

				public void run() {
					try {
						log.info("updating relationship URI...");
						long n = services.getItemCount().getResults().getNumFound();
						log.info("item count: " + n);
						int rows = 10;
						for (int i = 0; i < n; i += rows) {
							log.info("start: " + i + " rows: " + rows);
							QueryResponse resp = services.search("", null, i, rows, null);
							items = resp.getBeans(Item.class);
							int j = i;
							for (Item item : items) {
								try {
									log.info("[" + j + "] processing: " + item.getPid() + "...");
									RelationshipTuple[] rels = services.getRelationships(item.getPid(), null);
									if (rels != null) {
										for (RelationshipTuple rel : rels) {
											if (rel.getPredicate().startsWith("http://era.library.ualbertaca")) {
												log.info("[" + j + "] modifying pid: " + item.getPid() + ", "
													+ rel.getPredicate() + ", " + rel.getObject());
												services.purgeRelationship(item.getPid(), rel.getPredicate(),
													rel.getObject(), rel.isIsLiteral(), rel.getDatatype());
												services.addRelationship(item.getPid(), Definitions.uri
													+ rel.getPredicate().split("#")[1], rel.getObject(), true, null);
											}
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								j++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
			context.getMessages().add(new SimpleMessage("Update item relationship uri is processing."));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("resetoaisetspec")
	@Secure(roles = "/dev/main/resetoaisetspec")
	public Resolution resetOaiSetSpec() {

		Thread thread = new Thread(new Runnable() {

			public void run() {
				try {
					log.info("processing: " + context.getEventName());
					log.info("purging collection setSpec...");
					// context.getMessages().add(new SimpleMessage("processing: " + context.getEventName()));
					// context.getMessages().add(new SimpleMessage("purging collection setSpec..."));
					for (Collection col : services.getAllCollections()) {
						log.info("collection: " + col.getId() + " - " + col.getTitle());
						try {
							services.purgeRelationship(col.getId(),
								ApplicationProperties.getString("proai.fedora.setSpec"), col.getId(), true, null);
							// context.getMessages()
							// .add(new SimpleMessage(col.getId() + " relationship has been purged."));
						} catch (Exception e) {
							log.error(
								"Could not purge: " + col.getId() + ", "
									+ ApplicationProperties.getString("proai.fedora.setSpec"), e);
							// context.getMessages().add(new SimpleMessage("could not purge setSpec " + col.getId()));
						}
						try {
							services.purgeRelationship(col.getId(),
								ApplicationProperties.getString("proai.fedora.setName"), col.getTitle(), true, null);
							// context.getMessages().add(
							// new SimpleMessage(col.getTitle() + " relationship has been purged."));
						} catch (Exception e) {
							log.error(
								"Could not purge: " + col.getTitle() + ", "
									+ ApplicationProperties.getString("proai.fedora.setName"), e);
							// context.getMessages().add(new SimpleMessage("could not purge setName " +
							// col.getTitle()));
						}
					}

					log.info("adding community setSpec...");
					// context.getMessages().add(new SimpleMessage("adding community setSpec..."));
					for (Community com : services.getAllCommunities()) {
						log.info("community: " + com.getId() + " - " + com.getTitle());
						try {
							services.purgeRelationship(com.getId(),
								ApplicationProperties.getString("proai.fedora.setSpec"), com.getId(), true, null);
							services.addRelationship(com.getId(),
								ApplicationProperties.getString("proai.fedora.setSpec"), com.getId(), true, null);
							// context.getMessages().add(new SimpleMessage(com.getId() +
							// " relationship has been added."));
						} catch (Exception e) {
							log.error(
								"Could not purge: " + com.getId() + ", "
									+ ApplicationProperties.getString("proai.fedora.setSpec"), e);
							// context.getMessages().add(new SimpleMessage("could not add setSpec " + com.getId()));
						}
						try {
							services.purgeRelationship(com.getId(),
								ApplicationProperties.getString("proai.fedora.setName"), com.getTitle(), true, null);
							services.addRelationship(com.getId(),
								ApplicationProperties.getString("proai.fedora.setName"), com.getTitle(), true, null);
							// context.getMessages().add(
							// new SimpleMessage(com.getTitle() + " relationship has been added."));
						} catch (Exception e) {
							log.error(
								"Could not purge: " + com.getTitle() + ", "
									+ ApplicationProperties.getString("proai.fedora.setName"), e);
							// context.getMessages().add(new SimpleMessage("could not add setName " + com.getTitle()));
						}
					}
					// context.getMessages().add(new SimpleMessage(context.getEventName() + " process success."));
					log.info(context.getEventName() + " process success.");
				} catch (Exception e) {
					log.error(context.getEventName() + " process error!", e);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					PrintWriter ps = new PrintWriter(bos);
					e.printStackTrace(ps);
					ps.flush();
					ps.close();
					// context.getMessages().add(
					// new SimpleMessage(context.getEventName() + " process error!\n" + bos.toString() + "\n"));
				}
			}
		});
		thread.start();
		context.getMessages().add(new SimpleMessage(context.getEventName() + " is process running..."));
		return new RedirectResolution("/dev/main/init");
	}

	@HandlesEvent("notifysubscriber")
	@Secure(roles = "/dev/main/notifysubscriber")
	public Resolution subscriptionNotification() {
		try {
			log.info("executing subscription notifier...");
			subscriptionNotifier.run();
			Scheduler scheduler = services.getSchedulerByName(SubscriptionNotifier.class.getName());
			addSchedulerMessage(context.getEventName(), scheduler);
			context.getMessages().add(new LocalizableMessage("subscription.notifierRunning"));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("subscriptionbydate")
	@Secure(roles = "/dev/main/notifysubscriber")
	public Resolution subscriptionByDate() {
		try {
			if (params == null || params.size() != 2) {
				context.getMessages().add(new SimpleMessage("Please enter accepted date and username."));
				return new ForwardResolution("/jsp/protected/maintenanceForms.jsp");
			}
			Date date = new SimpleDateFormat("yyyy/MM/dd").parse(params.get(0));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			QueryResponse resp = services.findNewSubscriptionsByDate(params.get(1), Integer.MAX_VALUE, cal.getTime(),
				true);
			if (resp == null || resp.getResults().isEmpty()) {
				context.getMessages().add(new SimpleMessage("No new item in subscribed community or collection!"));
			}
			SolrDocumentList results = resp.getResults();
			// log.debug("[" + user.getUsername() + "] number of new items found: " + results.getNumFound());
			if (results.getNumFound() > 0) {
				String subject = applicationResources.getString("mail.subscription.subject");
				List<Item> items = resp.getBeans(Item.class);
				mailServiceManager.sendSubscriptionMail(user, subject, items);
				context.getMessages().add(new SimpleMessage(results.getNumFound() + " new item(s) found."));
			}
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("publishembargoed")
	@Secure(roles = "/dev/main/publishembargoed")
	public Resolution publishEmbargoed() {
		try {
			log.info("executing embargoed item publisher...");
			embargoedPublisher.run();
			Scheduler scheduler = services.getSchedulerByName(EmbargoedItemPublisher.class.getName());
			addSchedulerMessage(context.getEventName(), scheduler);
			context.getMessages().add(new LocalizableMessage("embargoed.publisherRunning"));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("buildindex")
	@Secure(roles = "/dev/main/buildindex")
	public Resolution buildIndex() {
		try {
			log.info("executing index builder...");
			indexBuilder.setDeleteIndex(true);
			indexBuilder.setOptimizeIndex(true);
			indexBuilder.setFedoraIndex(true);
			indexBuilder.setBookmarkIndex(true);
			indexBuilder.setFavoriteIndex(true);
			indexBuilder.setSubscriptionIndex(true);
			indexBuilder.run();
			Scheduler scheduler = services.getSchedulerByName(IndexBuilder.class.getName());
			addSchedulerMessage(context.getEventName(), scheduler);
			context.getMessages().add(new LocalizableMessage("index.builderRunning"));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("proquestupload")
	@Secure(roles = "/dev/main/proquestupload")
	public Resolution proquestUpload() {
		try {
			log.info("executing proquest upload...");
			proquestUpload.run();
			Scheduler scheduler = services.getSchedulerByName(ProquestUpload.class.getName());
			addSchedulerMessage(context.getEventName(), scheduler);
			context.getMessages().add(new LocalizableMessage("proquest.uploadRunning"));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("postfacebookmessage")
	@Secure(roles = "/dev/main/postfacebookmessage")
	public Resolution postFacebookMessage() {
		try {
			log.info("executing facebook publisher...");
			facebookPublisher.run();
			Scheduler scheduler = services.getSchedulerByName(FacebookPublisher.class.getName());
			addSchedulerMessage(context.getEventName(), scheduler);
			context.getMessages().add(new LocalizableMessage("facebook.publisherRunning"));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("removefacebookposts")
	@Secure(roles = "/dev/main/removefacebookposts")
	public Resolution removeFacebookPosts() {
		try {
			int noOfPosts = services.getFacebookService().removeAllPosts();
			context.getMessages().add(new SimpleMessage(noOfPosts + " Facebook posts have been removed."));
			return new RedirectResolution("/dev/main/init");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getEvents getter method.
	 * 
	 * @return the events
	 */
	public List<String> getEvents() {
		return events;
	}

	/**
	 * The getMessages getter method.
	 * 
	 * @return the messages
	 */
	public Map<String, String> getMessages() {
		return messages;
	}

	/**
	 * The getParams getter method.
	 * 
	 * @return the params
	 */
	public List<String> getParams() {
		return params;
	}

	/**
	 * The setParams setter method.
	 * 
	 * @param params the params to set
	 */
	public void setParams(List<String> params) {
		this.params = params;
	}
}
