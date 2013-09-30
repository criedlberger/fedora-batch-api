/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: PasswordActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The UserActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/password/{$event}")
public class PasswordActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(PasswordActionBean.class);

	@Validate(required = true, minlength = 5, maxlength = 32)
	private String currentPassword;

	@Validate(required = true, minlength = 5, maxlength = 32, expression = "this != currentPassword")
	private String newPassword;

	@Validate(required = true, minlength = 5, maxlength = 32, expression = "this == newPassword")
	private String confirmNewPassword;

	/**
	 * The UserActionBean class constructor.
	 */
	public PasswordActionBean() {
		super();
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		if (!DigestUtils.md5Hex(currentPassword).equals(context.getUser().getPassword())) {
			errors.add("currentPassword", new LocalizableError("incorrectPassword"));
		}
	}

	@DefaultHandler
	@HandlesEvent("change")
	@DontValidate
	@Secure(roles = "/user/password/update")
	public Resolution change() {
		try {
			if (ApplicationProperties.getBoolean("https.enabled") && !request.getScheme().equals("https")) {
				return new RedirectResolution(httpsServerUrl + request.getContextPath() + "/action/password/change",
					false);
			}
			user = context.getUser();
			return new ForwardResolution(uiPath + "/protected/changePassword.jsp");
		} catch (Exception e) {
			log.error("Could not delete this item!", e);
			return forwardExceptionError("Could not delete this item!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/user/password/update")
	public Resolution save() {
		try {
			if (ApplicationProperties.getBoolean("https.enabled") && !request.getScheme().equals("https")) {
				return new RedirectResolution(httpsServerUrl + request.getContextPath() + "/action/password/change",
					false);
			}
			user.setPassword(DigestUtils.md5Hex(newPassword));
			services.saveOrUpdateUser(user);
			context.getMessages().add(new LocalizableMessage("password.changePasswordSuccess"));
			return new RedirectResolution(httpServerUrl + request.getContextPath() + "/action/password/change");
		} catch (Exception e) {
			log.error("Could not save the password!", e);
			return forwardExceptionError("Could not save the password!", e);
		}
	}

	/**
	 * The getCurrentPassword getter method.
	 * 
	 * @return the currentPassword
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/**
	 * The setCurrentPassword setter method.
	 * 
	 * @param currentPassword the currentPassword to set
	 */
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	/**
	 * The getNewPassword getter method.
	 * 
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * The setNewPassword setter method.
	 * 
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * The getConfirmNewPassword getter method.
	 * 
	 * @return the confirmNewPassword
	 */
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	/**
	 * The setConfirmNewPassword setter method.
	 * 
	 * @param confirmNewPassword the confirmNewPassword to set
	 */
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
}
