/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: MyInformationActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.SystemPermissions;

/**
 * The UserActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/myaccount/information/{$event}")
public class MyInformationActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(MyInformationActionBean.class);

	@ValidateNestedProperties( { @Validate(field = "firstName", required = true, maxlength = 50),
		@Validate(field = "lastName", required = true, maxlength = 50),
		@Validate(field = "email", required = true, maxlength = 50, converter = EmailTypeConverter.class) })
	private User user;
	private String ccid;
	private boolean associated;

	/**
	 * The UserActionBean class constructor.
	 */
	public MyInformationActionBean() {
		super();
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		User usr = services.getUserByEmail(user.getEmail());
		if (usr != null && usr.getId().intValue() != context.getUser().getId().intValue()) {
			errors.add("user.email", new LocalizableError("user.emailExisted"));
		}
	}

	@DefaultHandler
	@HandlesEvent("view")
	@DontValidate
	public Resolution view() {
		try {
			user = context.getUser();
			ccid = user.getCcid();
			return new ForwardResolution(uiPath + "/protected/myInformation.jsp");
		} catch (Exception e) {
			log.error("Could not view this item!", e);
			return forwardExceptionError("Could not view this item!", e);
		}
	}

	@HandlesEvent("edit")
	@DontValidate
	@Secure(roles = "/user/information/update")
	public Resolution edit() {
		try {
			user = context.getUser();
			ccid = user.getCcid();
			associated = ccid != null ? true : false;
			return new ForwardResolution(uiPath + "/protected/editMyInformation.jsp");
		} catch (Exception e) {
			log.error("Could not delete this item!", e);
			return forwardExceptionError("Could not delete this item!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/user/information/update")
	public Resolution save() {
		try {
			// log.debug("saving my information...");
			User usr = services.getUser(context.getUser().getId());
			usr.setFirstName(user.getFirstName());
			usr.setLastName(user.getLastName());
			usr.setEmail(user.getEmail());
			if (isUserInRoles(usr, SystemPermissions.USER_CCID_ASSOCIATION.getPermission())) {
				if (!associated) {

					// dissociate ccid
					usr.setCcid(null);

					// logout ccid
					context.setCCIDUser(null);
				}
			}
			usr.setLanguage(user.getLanguage());
			services.saveOrUpdateUser(usr);

			// update context user (logged in user)
			user = services.getUser(usr.getId());
			context.setUser(user);
			ccid = user.getCcid();

			// reset language to default
			context.setLanguage(user.getLanguage());

			// log.debug("CCIDUser: " + session.getAttribute("CCIDUser"));

			context.getMessages().add(new LocalizableMessage("user.modifySuccess"));
			return new ForwardResolution(uiPath + "/protected/myInformation.jsp");
		} catch (Exception e) {
			log.error("Could not save the user information!", e);
			return forwardExceptionError("Could not save the user information!", e);
		}
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
	 * The isAssociated getter method.
	 * 
	 * @return the associated
	 */
	public boolean isAssociated() {
		return associated;
	}

	/**
	 * The setAssociated setter method.
	 * 
	 * @param associated the associated to set
	 */
	public void setAssociated(boolean associated) {
		this.associated = associated;
	}

}
