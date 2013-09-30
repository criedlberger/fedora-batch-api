/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UserActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.enums.UserState;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The UserActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/user/{$event}/{user.id}")
public class UserActionBean extends SearchBaseActionBean {
	private static final Log log = LogFactory.getLog(UserActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "firstName", required = true, maxlength = 50),
		@Validate(field = "lastName", required = true, maxlength = 50),
		@Validate(field = "email", required = true, maxlength = 50, converter = EmailTypeConverter.class),
		@Validate(field = "username", required = true, minlength = 3, maxlength = 30) })
	private User user;
	private UserState state;
	private boolean associated;
	private String ccid;

	private String name;
	private List<User> users;

	/**
	 * The UserActionBean class constructor.
	 * 
	 */
	public UserActionBean() {
		super();
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		User usr = services.getUserByEmail(user.getEmail());
		if (context.getEventName().equals("save") && usr != null && usr.getId().intValue() != user.getId().intValue()) {
			errors.add("user.email", new LocalizableError("user.emailExisted"));
		}
		if (context.getEventName().equals("create") && usr != null) {
			errors.add("user.email", new LocalizableError("user.emailExisted"));
		}
	}

	@HandlesEvent("preCreate")
	@DontValidate
	@Secure(roles = "/admin/user")
	public Resolution preCreate() {
		try {
			user = new User();
			return new ForwardResolution(uiPath + "/protected/editUser.jsp");
		} catch (Exception e) {
			log.error("Could not create this user!", e);
			return forwardExceptionError("Could not create this user!", e);
		}
	}

	@HandlesEvent("create")
	@Secure(roles = "/admin/user")
	public Resolution create() {
		try {
			Group grp = services.getGroup(user.getGroup().getId());
			user.setGroup(grp);
			user.setPassword(DigestUtils.md5Hex(ApplicationProperties.getString("default.password")));
			user.setState(state.getValue());
			user.setCreatedDate(new Date());
			services.saveOrUpdateUser(user);
			context.getMessages().add(new LocalizableMessage("user.createSuccess", user.getUsername()));
			return new ForwardResolution(uiPath + "/protected/editUser.jsp");
		} catch (Exception e) {
			log.error("Could not create user!", e);
			return forwardExceptionError("Could not create user!", e);
		}
	}

	@HandlesEvent("edit")
	@DontValidate
	@Secure(roles = "/admin/user")
	public Resolution edit() {
		try {
			user = services.getUser(user.getId());
			state = UserState.getState(user.getState());
			ccid = user.getCcid();
			associated = ccid != null ? true : false;
			return new ForwardResolution(uiPath + "/protected/editUser.jsp");
		} catch (Exception e) {
			log.error("Could not edit the user!", e);
			return forwardExceptionError("Could not edit the user!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/admin/user")
	public Resolution save() {
		try {
			Group grp = services.getGroup(user.getGroup().getId());
			user.setGroup(grp);
			User usr = services.getUser(user.getId());
			user.setUsername(usr.getUsername());
			user.setPassword(usr.getPassword());
			user.setState(state.getValue());
			user.setCreatedDate(usr.getCreatedDate());
			// log.debug("language: " + user.getLanguage());
			if (isUserInRoles(usr, SystemPermissions.USER_CCID_ASSOCIATION.getPermission())) {
				if (!associated && usr.getCcid() != null) {
					user.setCcid(null);
				} else {
					user.setCcid(usr.getCcid());
				}
			}
			services.saveOrUpdateUser(user);

			// update context user (logged in user)
			if (user.getId().intValue() == context.getUser().getId()) {
				context.setUser(user);
			}
			context.getMessages().add(new LocalizableMessage("user.saveSuccess", user.getUsername()));
			return new ForwardResolution(uiPath + "/protected/editUser.jsp");
		} catch (Exception e) {
			log.error("Could not save user!", e);
			return forwardExceptionError("Could not save user!", e);
		}
	}

	@HandlesEvent("deleteprofile")
	@Secure(roles = "/admin/user")
	@DontValidate
	public Resolution deleteProfile() {
		try {
			User usr = services.getUser(user.getId());
			if (usr.getAuthor() != null) {
				services.deleteAuthor(usr.getAuthor());
			}
			context.getMessages().add(new LocalizableMessage("user.deleteProfileSuccess", usr.getUsername()));
			return edit();
		} catch (Exception e) {
			log.error("Could not delete user profile!", e);
			return forwardExceptionError("Could not delete user profile!", e);
		}
	}

	@HandlesEvent("getUsersByName")
	@Secure(roles = "/admin/user,/admin/depositor,/admin/login")
	@DontValidate
	public Resolution getUsersByName() {
		try {
			users = services.getUsersByName(name);
			return new ForwardResolution(uiPath + "/protected/userAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getUserIdsByName")
	@Secure(roles = "/admin/user,/admin/depositor,/admin/login")
	@DontValidate
	public Resolution getUserIdsByName() {
		return getUsersByName();
	}

	/**
	 * The getUser getter method.
	 * 
	 * @return the user
	 */
	@Override
	public User getUser() {
		return user;
	}

	/**
	 * The setUser setter method.
	 * 
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * The getState getter method.
	 * 
	 * @return the state
	 */
	public UserState getState() {
		return state;
	}

	/**
	 * The setState setter method.
	 * 
	 * @param state the state to set
	 */
	public void setState(UserState state) {
		this.state = state;
	}

	/**
	 * The isCcid getter method.
	 * 
	 * @return the ccid
	 */
	public boolean isAssociated() {
		return associated;
	}

	/**
	 * The setCcid setter method.
	 * 
	 * @param ccid the ccid to set
	 */
	public void setAssociated(boolean ccid) {
		this.associated = ccid;
	}

	/**
	 * The getCcid getter method.
	 * 
	 * @return the ccid
	 */
	public String getCcid() {
		return ccid;
	}

	/**
	 * The setCcid setter method.
	 * 
	 * @param ccid the ccid to set
	 */
	public void setCcid(String ccid) {
		this.ccid = ccid;
	}

	/**
	 * The getName getter method.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The setName setter method.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getUsers getter method.
	 * 
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * The setUsers setter method.
	 * 
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

}
