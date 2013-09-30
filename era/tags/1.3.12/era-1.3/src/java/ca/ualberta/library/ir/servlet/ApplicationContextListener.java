/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApplicationContextListener.java 5646 2012-10-29 17:25:39Z pcharoen $
 */
package ca.ualberta.library.ir.servlet;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBContext;

import net.sourceforge.stripes.integration.spring.SpringHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.model.inputform.InputForms;
import ca.ualberta.library.ir.model.metadata.Metadata;
import ca.ualberta.library.ir.service.ServiceFacade;
import ca.ualberta.library.ir.utils.ApplicationProperties;
import ca.ualberta.library.ir.utils.ConnectionPoolMonitor;
import ca.ualberta.library.ir.utils.ThreadPool;

/**
 * The ApplicationContextListener class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong
 *         Charoenwattana</a>
 * @version $Revision: 5646 $ $Date: 2012-07-10 12:44:44 -0600 (Tue, 10 Jul
 *          2012) $
 */
public class ApplicationContextListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(ApplicationContextListener.class);
	public static JAXBContext metadataContext = initMetadataContext();
	public static JAXBContext inputFormsContext = initInputFormsContext();
	private static ApplicationContext context;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final ConnectionPoolMonitor monitor = new ConnectionPoolMonitor();

	private static JAXBContext initMetadataContext() {
		try {
			return JAXBContext.newInstance(Metadata.class);
		} catch (Exception e) {
			log.error("Could not initialize Metadata JAXBContext!", e);
			return null;
		}
	}

	private static JAXBContext initInputFormsContext() {
		try {
			return JAXBContext.newInstance(InputForms.class);
		} catch (Exception e) {
			log.error("Could not initialize InputForms JAXBContext!", e);
			return null;
		}
	}

	private ServletContext servletContext;
	private ServiceFacade services;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {

			log.info("initializing application context...");

			// set context variables
			servletContext = event.getServletContext();
			servletContext.setAttribute("properties", ApplicationProperties.PROPERTIES); // application.properties
			servletContext.setAttribute("httpServerUrl", ApplicationProperties.getString("http.server.url"));
			servletContext.setAttribute("httpsServerUrl", ApplicationProperties.getString("https.server.url"));
			servletContext.setAttribute("adminEmail", ApplicationProperties.getString("mail.admin"));
			servletContext.setAttribute("feedType", ApplicationProperties.getString("feed.type"));
			try {
				FileUtils.forceDelete(new File((String) servletContext.getAttribute("tempPath")));
			} catch (Exception e) {
				return;
			}

			// get application context
			context = (ApplicationContext) servletContext
					.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

			// initialize facebook page publisher
			services = (ServiceFacade) context.getBean("services");
			services.getFacebookService().getPage();
			services.setServletContext(servletContext);

			// check temp directory
			File tempDir = new File(ActionConstants.tempPath);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}

			// monitor connection pool
			startConnectionPoolMonitoring();

			log.info(servletContext.getServletContextName() + " Initialized.");

		} catch (Exception e) {
			log.error("Could not initialize servlet context!", e);
			contextDestroyed(event);
		}
	}

	private void startConnectionPoolMonitoring() {

		// inject dataSource bean
		SpringHelper.injectBeans(monitor, servletContext);

		// start schedule
		scheduler.scheduleAtFixedRate(monitor, 0, 1, TimeUnit.MINUTES);
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();

		log.info("running housekeeping process...");

		// shutdown thread pool
		ThreadPool.shutdown();

		log.info(servletContext.getServletContextName() + " Destroyed.");
	}
}
