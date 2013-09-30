/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApplicationSessionListener.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.servlet;

import static ca.ualberta.library.ir.enums.ContentModel.TYPE_OF_ITEM;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.context.ApplicationContext;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.License;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.model.solr.ContentModel;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * ApplicationSessionListener
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ApplicationSessionListener implements HttpSessionListener {
	private static final Log log = LogFactory.getLog(ApplicationSessionListener.class);

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent event) {
		// log.trace("session created.");
		try {
			ApplicationContext context = ApplicationContextListener.getApplicationContext();
			HttpSession session = event.getSession();
			ServiceFacade services = (ServiceFacade) context.getBean("services");
			session.setAttribute("services", services);

			// set session attributes from services
			List<Group> groups = services.getAllGroups();
			session.setAttribute("groups", groups);

			List<ContentModel> contentModels = services.getContentModelsByType(TYPE_OF_ITEM.getId());
			session.setAttribute("contentModels", contentModels);

			List<Count> types = services.getAllTypes();
			session.setAttribute("types", types);

			List<Community> communities = services.getAllCommunities();
			session.setAttribute("communities", communities);

			List<Collection> collections = services.getAllCollections();
			session.setAttribute("collections", collections);

			List<License> licenses = services.getAllLicenses();
			session.setAttribute("licenses", licenses);

		} catch (Exception e) {
			log.error("Inititized Session Process Error!", e);
			sessionDestroyed(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		event.getSession().invalidate();
	}

	protected String printSession(HttpSession session) {
		return " id: " + session.getId() + " creationTime: " + new Date(session.getCreationTime())
			+ " lastAccessedTime: " + new Date(session.getLastAccessedTime()) + " maxInactiveInterval: "
			+ session.getMaxInactiveInterval() + " sec.";
	}

}
