/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApplicationAccessControlManager.java 5615 2012-10-16 18:20:47Z pcharoen $
 */
package ca.ualberta.library.ir.security.controller;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.integration.spring.SpringHelper;
import net.sourceforge.stripes.security.controller.StripesSecurityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fcrepo.server.types.gen.ObjectFields;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.action.ApplicationActionBeanContext;
import ca.ualberta.library.ir.action.BaseActionBean;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.GroupType;
import ca.ualberta.library.ir.enums.ObjectPermissions;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The ApplicationACLSecurityManager class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
 */
public class ApplicationAccessControlManager implements StripesSecurityManager {
	private static final Log log = LogFactory.getLog(ApplicationAccessControlManager.class);

	protected static final Pattern pidPattern = Pattern.compile("/");
	private ServiceFacade services;

	/**
	 * The ApplicationACLSecurityManager class constructor.
	 */
	public ApplicationAccessControlManager() {
		super();
	}

	/**
	 * The injectServiceFacade method.
	 * 
	 * @param serviceFacade
	 */
	@SpringBean("services")
	public void injectServiceFacade(ServiceFacade serviceFacade) {
		// log.trace("injecting services bean...");
		this.services = serviceFacade;
	}

	/**
	 * The init method.
	 */
	protected void init() {
		if (services == null) {
			SpringHelper.injectBeans(this, StripesFilter.getConfiguration().getServletContext());
		}
	}

	/*
	 * This method is for ActionBean to do the ACL check on user. This method will be called by ActionBeans with
	 * @Secure(roles = ...) annotation.
	 */
	public boolean isUserInRole(List<String> roles, ActionBeanContext context) {
		try {
			ApplicationActionBeanContext ctx = ((ApplicationActionBeanContext) context);
			services = ctx.getServices();

			User user = ctx.getUser();
			if (user == null) {
				// get guest user
				user = services.getUser(0);
			}

			// check permission
			return isPermittedUser(user, roles);

		} catch (Exception e) {
			log.error("Access denied!", e);
			return false;
		}
	}

	/*
	 * This method is for Stripes Sucurity Tag, <security:secure> to do ACL check on user. This method will be called by
	 * JSP pages.
	 */
	public boolean isUserInRole(List<String> roles, HttpServletRequest request, HttpServletResponse response) {
		try {
			// log.trace("checking jsp access control...");
			// log.trace("request uri: " + request.getRequestURI());
			// init();
			BaseActionBean actionBean = (BaseActionBean) request.getAttribute("actionBean");
			// log.trace("actionBean: " + actionBean);
			if (actionBean == null) {
				return false;
			}

			services = actionBean.getServices();
			User user = actionBean.getContext().getUser();
			if (user == null) {
				// get guest user
				user = services.getUser(0);
			}

			// check /object/owner permission
			if (roles.contains(ObjectPermissions.OBJECT_OWNER.getPermission())) {
				String pid = actionBean.getObjectPID();
				if (pid != null) {
					if (user.getGroup().getId() == GroupType.ADMIN.getValue()) {
						return true;
					}
					ObjectFields object = actionBean.getObject(pid);
					String[] ownerIds = ActionConstants.commaPattern.split(object.getOwnerId().getValue());
					if (!Arrays.asList(ownerIds).contains(user.getUsername())) {
						return false;
					}
				}
			}
			return isPermittedUser(user, roles);

		} catch (Exception e) {
			log.error("Access denied!", e);
			return false;
		}
	}

	/**
	 * The isPermittedUser method.
	 * 
	 * @param user
	 * @param roles
	 * @return
	 */
	private boolean isPermittedUser(User user, List<String> roles) {

		// check group/user permissions
		try {
			boolean allowed = services.isUserPermissionAllowed(user == null ? 0 : user.getId(), roles);
			return allowed;
		} catch (Exception e) {
			try {
				if (services.isGroupPermissionAllowed(user == null ? 0 : user.getGroup().getId(), roles)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e2) {
				return false;
			}
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
	 * The setServices setter method.
	 * 
	 * @param services the services to set
	 */
	public void setServices(ServiceFacade services) {
		this.services = services;
	}
}