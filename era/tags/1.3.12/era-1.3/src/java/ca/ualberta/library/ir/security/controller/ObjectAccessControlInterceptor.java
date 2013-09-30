/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: ObjectAccessControlInterceptor.java 5603 2012-10-05 18:51:26Z pcharoen $
 */
package ca.ualberta.library.ir.security.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.exception.StripesServletException;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.integration.spring.SpringHelper;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.security.controller.StripesSecurityFilter;
import net.sourceforge.stripes.security.exception.StripesAuthorizationException;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.action.ApplicationActionBeanContext;
import ca.ualberta.library.ir.action.BaseActionBean;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.DatastreamID;
import ca.ualberta.library.ir.enums.GroupType;
import ca.ualberta.library.ir.enums.ObjectPermissions;
import ca.ualberta.library.ir.enums.WorkflowState;
import ca.ualberta.library.ir.model.solr.Item;
import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The ObjectAccessControlInterceptor class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5603 $ $Date: 2012-10-05 12:51:26 -0600 (Fri, 05 Oct 2012) $
 */
@Intercepts(LifecycleStage.EventHandling)
public class ObjectAccessControlInterceptor implements Interceptor, ConfigurableComponent {
	private static final Log log = LogFactory.getLog(ObjectAccessControlInterceptor.class);
	private ServiceFacade services;

	/**
	 * The ObjectAccessControlInterceptor class constructor.
	 */
	public ObjectAccessControlInterceptor() {
		super();
	}

	/**
	 * 
	 * @see net.sourceforge.stripes.config.ConfigurableComponent#init(net.sourceforge.stripes.config.Configuration)
	 */
	@Override
	public void init(Configuration configuration) throws Exception {
		SpringHelper.injectBeans(this, configuration.getServletContext());
	}

	@SpringBean("services")
	public void injectServiceFacade(ServiceFacade serviceFacade) {
		// log.trace("injecting services bean...");
		this.services = serviceFacade;
	}

	/**
	 * 
	 * @see net.sourceforge.stripes.controller.Interceptor#intercept(net.sourceforge.stripes.controller.ExecutionContext)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Resolution intercept(ExecutionContext ctx) {
		final Configuration config = StripesSecurityFilter.getConfiguration();
		final ActionResolver resolver = config.getActionResolver();
		final ApplicationActionBeanContext context = (ApplicationActionBeanContext) ctx.getActionBeanContext();
		final BaseActionBean actionBean = (BaseActionBean) ctx.getActionBean();
		final Class beanClass = resolver.getActionBeanType(getRequestedPath(context.getRequest()));

		try {
			if (beanClass != null) {

				// process class level object access control
				final Secure beanSecure = getSecureAnnotationFromClass(beanClass);
				if (beanSecure != null) {
					Resolution resolution = process(beanClass, beanSecure, context, actionBean);
					if (resolution != null) {
						return resolution;
					}
				}

				// Then lookup the event name and handler method etc.
				String eventName = resolver.getEventName(beanClass, context);
				context.setEventName(eventName);

				final Method handler;
				if (eventName != null) {
					handler = resolver.getHandler(beanClass, eventName);
				} else {
					handler = resolver.getDefaultHandler(beanClass);
					if (handler != null) {
						context.setEventName(resolver.getHandledEvent(handler));
					}
				}

				// Insist that we have a handler
				if (handler == null) {
					throw new StripesServletException("No handler method found for request with  ActionBean ["
						+ beanClass.getSimpleName() + "] and eventName [ " + eventName + "]");
				}

				if (handler != null) {
					// log.trace("Checking the method " + handler.getName());

					// Check to see if we have a method level security annotation and authenticate
					// the user if we do.
					Secure methodSecure = handler.getAnnotation(Secure.class);
					if (methodSecure != null) {
						Resolution resolution = process(beanClass, methodSecure, context, actionBean);
						if (resolution != null) {
							return resolution;
						}
					}
				}
			}
			return ctx.proceed();

		} catch (Exception e) {
			log.error("Object access control validation process error!", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("errors.objectAccessDenied"));
			return actionBean.forwardUnauthorized();
		}

	}

	@SuppressWarnings("rawtypes")
	private Resolution process(final Class beanClass, final Secure secure, final ApplicationActionBeanContext context,
		final BaseActionBean actionBean) throws Exception {

		// allow to access thumbnail datastream
		if (context.getRequest().getRequestURI().endsWith(DatastreamID.THUMBNAIL.name())) {
			return null;
		}

		// Lets just say that if somebody sets the @Secure annotation on a class and doesn't set any
		// roles it defaults to unauthorized.
		if (secure.roles() == null || secure.roles().trim().length() < 1)
			throw new StripesAuthorizationException();

		// Now lets go through the any roles. If they have any of these roles and the above 2 have
		// succeeded, let them in.

		String pid = actionBean.getObjectPID();
		if (pid == null) {
			// skip checking if pid is null.
			return null;
		}
		Item item = services.findObjectByPid(pid).getBeans(Item.class).iterator().next();

		if (secure.roles() != null && secure.roles().trim().length() > 0) {

			// log.trace("Checking requires any Roles[" + secure.roles() + "]");
			List<String> roles = Arrays.asList(ActionConstants.commaPattern.split(secure.roles().trim()));
			for (String role : roles) {
				ObjectPermissions permission = ObjectPermissions.getObjectPermissions(role);
				if (permission == null) {
					// skip other roles, process only roles found in ObjectPermissions
					continue;
				}

				// log.trace("processing role: " + role + " pid: " + pid);
				switch (permission) {
				case OBJECT_DARK:

					// check dark repository
					if (actionBean.isDarkRepository(pid) && !actionBean.canAccessDarkRepository(pid)) {
						return actionBean.forwardUnauthorized();
					}
					break;

				case OBJECT_EMBARGOED:

					// check dark repository
					if (item.isEmbargoed() && !actionBean.canAccessEmbargoed(pid)) {
						return actionBean.forwardUnauthorized();
					}
					break;

				case OBJECT_CCID:

					// check CCID authentication
					// log.trace("CCIDUser: " + context.getCCIDUser());
					if (context.getCCIDUser() == null && actionBean.isCCIDAuth(pid, true)) {
						return actionBean.redirectCCIDLogin();
					}
					break;

				case OBJECT_OWNER:

					// check admin permissions
					List<String> owners = Arrays.asList(ActionConstants.commaPattern.split(item.getOwnerId()));
					User user = actionBean.getContext().getUser();
					if (user.getGroup().getId() == GroupType.ADMIN.getValue()) {

						// allow admin
						break;

					} else if (user.getGroup().getId().equals(GroupType.USER.getValue()) && item.isApproval()
						&& item.getWorkflowState().equals(WorkflowState.Archive.toString())) {

						// do not allow user to edit e-thesis
						return actionBean.forwardUnauthorized();

					} else if (owners.contains(user.getUsername())) {

						// allow edit own item
						break;

					} else {
						return actionBean.forwardUnauthorized();
					}
				}
			}
		}
		return null;
	}

	protected String getRequestedPath(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		return (servletPath == null ? "" : servletPath) + (pathInfo == null ? "" : pathInfo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Secure getSecureAnnotationFromClass(final Class clazz) {
		// log.trace("Checking the class " + clazz.getSimpleName());
		Secure beanSecure = (Secure) clazz.getAnnotation(Secure.class);
		if (beanSecure == null) {
			// log.trace("Checking the parent class " + clazz.getSuperclass().getSimpleName());
			Class parent = clazz.getSuperclass();
			if (ActionBean.class.isAssignableFrom(parent)) {
				return getSecureAnnotationFromClass(parent);
			} else {
				return null;
			}
		} else {
			return beanSecure;
		}
	}
}
