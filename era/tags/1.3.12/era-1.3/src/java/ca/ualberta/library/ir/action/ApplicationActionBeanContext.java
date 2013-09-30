/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApplicationActionBeanContext.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.syndication.feed.synd.SyndFeed;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.integration.spring.SpringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.localization.ApplicationLocalePicker;
import ca.ualberta.library.ir.model.fedora.ContentModel;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The ApplicationActionBeanContext class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ApplicationActionBeanContext extends ActionBeanContext {
	private static final Log log = LogFactory.getLog(ApplicationActionBeanContext.class);

	protected static final ApplicationLocalePicker localePicker = (ApplicationLocalePicker) StripesFilter
		.getConfiguration().getLocalePicker();;

	protected ServiceFacade services;
	protected ServletContext context;
	protected HttpSession session;
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	/**
	 * The ApplicationActionBeanContext class constructor.
	 */
	public ApplicationActionBeanContext() {
		super();
		// log.trace("initilizing actionbean context...");
		// log.trace("injecting beans...");
		SpringHelper.injectBeans(this, StripesFilter.getConfiguration().getServletContext());
	}

	@SpringBean("services")
	public void injectServiceFacade(ServiceFacade serviceFacade) {
		try {
			// log.trace("injecting services bean...");
			this.services = serviceFacade;
		} catch (Exception e) {
			log.error("Could not inject service facade bean!", e);
		}
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		try {
			// log.trace("setting servlet request..");
			super.setRequest(request);
			this.request = getRequest();
			this.session = getRequest().getSession();
			this.context = getRequest().getSession().getServletContext();

			// if (log.isTraceEnabled()) {
			// log.trace("remote host: [" + request.getRemoteHost() + "] port: [" + request.getRemotePort()
			// + "] address: [" + request.getRemoteAddr() + "] User-Agent: [" + request.getHeader("User-Agent")
			// + "]");
			// log.trace("url: " + request.getRequestURL() + " query: " + request.getQueryString());
			// Enumeration<String> hds = request.getHeaderNames();
			// while (hds.hasMoreElements()) {
			// String hd = hds.nextElement();
			// log.trace("Header - " + hd + ": " + request.getHeader(hd));
			// }
			// log.trace("session lastAccessdTime: " + new Date(request.getSession().getLastAccessedTime())
			// + " creationTime: " + new Date(request.getSession().getCreationTime()) + " maxInactiveInterval: "
			// + request.getSession().getMaxInactiveInterval());
			// }
		} catch (Exception e) {
			log.error("Could not set servlet request!", e);
		}
	}

	/**
	 * 
	 * @see net.sourceforge.stripes.action.ActionBeanContext#setResponse(javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void setResponse(HttpServletResponse response) {
		try {
			// log.trace("setting servlet response...");
			super.setResponse(response);
			this.response = getResponse();
		} catch (Exception e) {
			log.error("Could not set servlet response!", e);
		}
	}

	/**
	 * The getServices getter method.
	 * 
	 * @return the services
	 */
	public ServiceFacade getServices() {
		return services;
	}

	/**
	 * Logs the user out by invalidating the session.
	 */
	public void logout() {
		if (getFromUser() != null) {
			setUser(getFromUser());
			setFromUser(null);
		} else {
			session.invalidate();
			// reset language to default
			setLanguage(Locale.getDefault().getLanguage());
		}
	}

	/**
	 * The setUser method.
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		session.setAttribute("user", user);
	}

	public User getUser() {
		return (User) session.getAttribute("user");
	}

	@SuppressWarnings("unchecked")
	public List<ContentModel> getAllContentModels() {
		return (List<ContentModel>) session.getAttribute("contentModels");
	}

	public String getTag() {
		return (String) session.getAttribute("tag");
	}

	public void setTag(String tag) {
		session.setAttribute("tag", tag);
	}

	public void setTagQuery(String query) {
		session.setAttribute("tagQuery", query);
	}

	public String getTagQuery() {
		return (String) session.getAttribute("tagQuery");
	}

	/**
	 * The setFeatureFeeds method.
	 * 
	 * @param featureFeeds
	 */
	public void setFeatureFeeds(List<SyndFeed> featureFeeds) {
		session.setAttribute("featureFeeds", featureFeeds);
	}

	/**
	 * The setFeatureFeedUrls method.
	 * 
	 * @param featureFeedUrls
	 */
	public void setFeatureFeedUrls(List<String> featureFeedUrls) {
		session.setAttribute("featureFeedUrls", featureFeedUrls);
	}

	/**
	 * The getFeatureFeeds method.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SyndFeed> getFeatureFeeds() {
		return (List<SyndFeed>) session.getAttribute("featureFeeds");
	}

	/**
	 * The getFeatureFeedUrls method.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getFeatureFeedUrls() {
		return (List<String>) session.getAttribute("featureFeedUrls");
	}

	/**
	 * The setLanguage method.
	 * 
	 * @param language
	 */
	public void setLanguage(String language) {
		if (!localePicker.getLocales().contains(new Locale(language))) {
			language = localePicker.pickLocale(request).getLanguage();
		}
		Cookie cookie = new Cookie("lang", language);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public String getLanguage() {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("lang")) {
					String language = cookie.getValue();
					if (!localePicker.getLocales().contains(new Locale(language))) {
						language = localePicker.pickLocale(request).getLanguage();
					}
					return language;
				}
			}
		}
		return localePicker.pickLocale(request).getLanguage();
	}

	@SuppressWarnings("unchecked")
	public List<Group> getAllGroups() {
		return (List<Group>) session.getAttribute("groups");
	}

	public void setAllGroups(List<Group> groups) {
		session.setAttribute("groups", groups);
	}

	/**
	 * The getCart method.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCart() {
		List<String> cart = (List<String>) session.getAttribute("cart");
		if (cart == null) {
			cart = new ArrayList<String>();
			session.setAttribute("cart", cart);
		}
		return cart;
	}

	/**
	 * The setCart method.
	 * 
	 * @param cart
	 */
	public void setCart(List<String> cart) {
		session.setAttribute("cart", cart == null ? new ArrayList<String>() : cart);
	}

	/**
	 * The setMessages method.
	 * 
	 * @param messages
	 */
	public void setAdminMessage(String message) {
		context.setAttribute("adminMessage", message);
	}

	public void setSystemMessage(String message) {
		context.setAttribute("systemMessage", message);
	}

	public String getAdminMessage() {
		return (String) context.getAttribute("adminMessage");
	}

	public String getSystemMessage() {
		return (String) context.getAttribute("systemMessage");
	}

	/**
	 * The setCCIDUser method.
	 * 
	 * @param ccidUser
	 */
	public void setCCIDUser(User ccidUser) {
		session.setAttribute("CCIDUser", ccidUser);
	}

	public User getCCIDUser() {
		return (User) session.getAttribute("CCIDUser");
	}

	@SuppressWarnings("unchecked")
	public List<User> getOwners() {
		return (List<User>) (session.getAttribute("owners") == null ? new ArrayList<User>() : session
			.getAttribute("owners"));
	}

	public void setOwners(List<User> owners) {
		session.setAttribute("owners", owners);
	}

	/**
	 * The setAllCommunities method.
	 * 
	 * @param allCommunities
	 */
	public void setAllCommunities(List<Community> allCommunities) {
		session.setAttribute("allCommunities", allCommunities);
	}

	/**
	 * The setAllCollections method.
	 * 
	 * @param allCollections
	 */
	public void setAllCollections(List<Collection> allCollections) {
		session.setAttribute("allCollections", allCollections);
	}

	@SuppressWarnings("unchecked")
	public List<Community> getAllCommunities() {
		return (List<Community>) session.getAttribute("allCommunities");
	}

	@SuppressWarnings("unchecked")
	public List<Community> getCommunities() {
		return (List<Community>) session.getAttribute("communities");
	}

	@SuppressWarnings("unchecked")
	public List<Collection> getAllCollections() {
		return (List<Collection>) session.getAttribute("allCollections");
	}

	@SuppressWarnings("unchecked")
	public List<Collection> getCollections() {
		return (List<Collection>) session.getAttribute("collections");
	}

	public void setFromUser(User user) {
		session.setAttribute("fromUser", user);
	}

	public User getFromUser() {
		return (User) session.getAttribute("fromUser");
	}
}
