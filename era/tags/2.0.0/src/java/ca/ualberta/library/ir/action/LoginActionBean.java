/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: LoginActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import javax.naming.NamingException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.enums.UserState;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The LoginActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/login/{url}")
public class LoginActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(LoginActionBean.class);

	@Validate(required = true, minlength = 3, maxlength = 32)
	private String username;

	@Validate(required = true, minlength = 3, maxlength = 32)
	private String password;

	private String asUsername;

	private String url;

	private String mode;

	private String type;

	private boolean httpsError;

	/**
	 * The LoginActionBean class constructor.
	 */
	public LoginActionBean() {
		super();
	}

	// @ValidationMethod
	public void validate(ValidationErrors errors) {
		mode = mode == null ? mode = "password" : mode;

		// check request protocol
		if (ApplicationProperties.getBoolean("https.enabled") && !request.getScheme().equals("https")) {
			errors.addGlobalError(new LocalizableError("login.error.message"));
		}
	}

	@DefaultHandler
	@HandlesEvent("start")
	@DontValidate
	public Resolution start() {

		mode = mode == null ? mode = "password" : mode;

		// check request protocol
		if (BooleanUtils.toBoolean(ApplicationProperties.getString("https.enabled"))
			&& !request.getScheme().equals("https")) {
			context.getValidationErrors().addGlobalError(new LocalizableError("login.error.message"));
			httpsError = true;
			return new ForwardResolution(uiPath
				+ ("popup".equals(type) ? "/public/loginPopup.jsp" : "/public/loginError.jsp"));
		}
		return new ForwardResolution(uiPath + ("popup".equals(type) ? "/public/loginPopup.jsp" : "/public/login.jsp"));
	}

	@HandlesEvent("login")
	public Resolution login() {
		// log.debug("logging in...");

		// check request protocol
		if (BooleanUtils.toBoolean(ApplicationProperties.getString("https.enabled"))
			&& !request.getScheme().equals("https")) {
			httpsError = true;
			context.getMessages().add(new LocalizableMessage("login.error.message"));
			return new ForwardResolution(uiPath
				+ ("popup".equals(type) ? "/public/loginPopup.jsp" : "/public/login.jsp"));
		}
		if (mode.equals("ccid")) {

			// ccid login
			try {

				services.authenticate(username, password);
				User ccidUser = services.findLdapUser(username);
				context.setCCIDUser(ccidUser);

				User user = context.getUser();
				if (user != null) {
					if (user.getCcid() == null) {

						// check user permission
						if (isUserInRoles(user, SystemPermissions.USER_CCID_ASSOCIATION.getPermission())) {

							// associate ccid to user
							// log.debug("associating user: " + user.getUsername() + " to ccid: " +
							// context.getCCIDUser().getUsername());
							user = services.getUser(user.getId());
							user.setCcid(context.getCCIDUser().getUsername());
							services.saveOrUpdateUser(user);
						}

						// update context user
						context.setUser(user);
					}
				} else {

					// get user using ccid
					user = services.getUserByCcid(username);
					if (user != null) {

						// user associated with ccid, log user in
						context.setUser(user);
					}
				}
			} catch (Exception e) {
				// log.debug("CCID Authentication Error!: " + e.getLocalizedMessage(), e);
				ValidationError error = new LocalizableError("authenticationError");
				getContext().getValidationErrors().addGlobalError(error);
				return getContext().getSourcePageResolution();
			}

			if ("popup".equals(type)) {
				context.getMessages().add(new LocalizableMessage("login.authenticationSuccess"));
				return new ForwardResolution(uiPath + "/public/loginPopup.jsp");
			}

			// redirect user using full url
			if (StringUtils.trimToNull(this.url) == null) {
				return new RedirectResolution(httpServerUrl + request.getContextPath() + "/public/home", false);
			} else {
				return new RedirectResolution(this.url, false);
			}

		} else {

			// password login
			User user = services.getUser(this.username);
			if (user == null) {
				ValidationError error = new LocalizableError("usernameDoesNotExist");
				getContext().getValidationErrors().add("username", error);
				return getContext().getSourcePageResolution();
			}
			if (user.getState() == UserState.I.getValue()) {
				ValidationError error = new LocalizableError("accountInactive", mailAdmin);
				getContext().getValidationErrors().addGlobalError(error);
				return getContext().getSourcePageResolution();
			} else if (user.getState() == UserState.D.getValue()) {
				ValidationError error = new LocalizableError("accountDeleted", mailAdmin);
				getContext().getValidationErrors().addGlobalError(error);
				return getContext().getSourcePageResolution();
			} else if (user.getState() == UserState.B.getValue()) {
				ValidationError error = new LocalizableError("accountBlocked", mailAdmin);
				getContext().getValidationErrors().addGlobalError(error);
				return getContext().getSourcePageResolution();
			} else if (user.getState() == State.A.getValue()) {

				// password validation
				if (!(user.getPassword().equals(DigestUtils.md5Hex(password)))) {
					ValidationError error = new LocalizableError("incorrectPassword");
					getContext().getValidationErrors().add("password", error);
					return getContext().getSourcePageResolution();
				}

				if (asUsername != null) {
					if (isUserInRoles(context.getUser(), SystemPermissions.ADMIN_LOGIN.getPermission())) {
						User asUser = services.getUser(asUsername);
						if (asUser != null) {
							context.setFromUser(context.getUser());
							context.setUser(asUser);
						} else {
							ValidationError error = new LocalizableError("usernameDoesNotExist");
							getContext().getValidationErrors().add("asUsername", error);
							return getContext().getSourcePageResolution();
						}
					}
				} else {
					context.setUser(user);
				}

				// check for ccid association
				String ccid = StringUtils.trimToNull(user.getCcid());
				if (context.getCCIDUser() != null) {
					if (ccid == null) {

						// associate user to ccid
						// log.debug("associating user: " + user.getUsername() + " to ccid: " +
						// context.getCCIDUser().getUsername());
						user.setCcid(ccid);
						services.saveOrUpdateUser(user);
					}
				} else {
					if (ccid != null) {
						try {

							// look up ccid on ldap server
							User ccidUser = services.findLdapUser(ccid);
							context.setCCIDUser(ccidUser);
						} catch (NamingException e) {
							log.warn("Could not find CCID!", e);
						}
					}
				}

				// update user language
				context.setLanguage(context.getUser().getLanguage());

				if ("popup".equals(type)) {
					context.getMessages().add(new LocalizableMessage("login.authenticationSuccess"));
					return new ForwardResolution(uiPath + "/public/loginPopup.jsp");
				}

				// redirect user using full url
				// log.debug("url: " + url);
				if (StringUtils.trimToNull(this.url) == null) {
					return new RedirectResolution(httpServerUrl + request.getContextPath() + "/public/home", false);
				} else {
					return new RedirectResolution(this.url, false);
				}
			} else {

				// invalid state
				ValidationError error = new LocalizableError("invalidState", mailAdmin);
				getContext().getValidationErrors().addGlobalError(error);
				return getContext().getSourcePageResolution();
			}
		}
	}

	/** The username of the user trying to log in. */
	public void setUsername(String username) {
		this.username = username;
	}

	/** The username of the user trying to log in. */
	public String getUsername() {
		return username;
	}

	/** The password of the user trying to log in. */
	public void setPassword(String password) {
		this.password = password;
	}

	/** The password of the user trying to log in. */
	public String getPassword() {
		return password;
	}

	/**
	 * The getMode getter method.
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * The setMode setter method.
	 * 
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * The getUrl getter method.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The setUrl setter method.
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * The getType getter method.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * The setType setter method.
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The isHttpsError getter method.
	 * 
	 * @return the httpsError
	 */
	public boolean isHttpsError() {
		return httpsError;
	}

	/**
	 * The getAsusername getter method.
	 * 
	 * @return the asusername
	 */
	public String getAsUsername() {
		return asUsername;
	}

	/**
	 * The setAsusername setter method.
	 * 
	 * @param asUsername the asusername to set
	 */
	public void setAsUsername(String asUsername) {
		this.asUsername = asUsername;
	}
}