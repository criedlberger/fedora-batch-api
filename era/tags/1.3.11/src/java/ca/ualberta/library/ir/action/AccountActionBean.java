/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: AccountActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The AccountActionBean class handles user requests for forgot username and password.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/account/{$event}/{type}")
public class AccountActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(AccountActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "username", required = false, minlength = 5, maxlength = 32),
		@Validate(field = "email", required = true, maxlength = 50, converter = EmailTypeConverter.class) })
	private User user;

	private String type;

	/**
	 * The AccountActionBean class constructor.
	 */
	public AccountActionBean() {
		super();
	}

	/**
	 * The validate method validates username, email and password. If user requests for username, email will be
	 * validated. If user requests for password, username and email will be validated.
	 * 
	 * @param errors - the validation error messages
	 */
	@ValidationMethod
	public void validate(ValidationErrors errors) {
		if (context.getEventName().equals("password")) {
			User usr = services.getUser(user.getUsername());
			if (usr == null) {
				errors.add("user.username", new LocalizableError("incorrectUsername"));
			} else {
				if (!usr.getEmail().equals(user.getEmail())) {
					errors.add("user.email", new LocalizableError("incorrectEmail"));
				}
			}
		} else if (context.getEventName().equals("username")) {
			User usr = services.getUserByEmail(user.getEmail());
			if (usr == null) {
				errors.add("user.email", new LocalizableError("emailNotFound", user.getEmail()));
			}
		}
	}

	/**
	 * The request method forwords request to ForwardResolution to produce Forgot Username/Password form.
	 * 
	 * @return ForwardResolution to create forgot username or password form from /public/forgotPassword.jsp.
	 */
	@DefaultHandler
	@HandlesEvent("request")
	@DontValidate
	public Resolution request() {
		try {
			type = StringUtils.trimToNull(type) == null ? "password" : type;
			return new ForwardResolution(uiPath + "/public/forgotPassword.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The username method gets User Object using the user's email and creates and sends HTML email with the username.
	 * 
	 * @return ForwardResolution to create request username success message from /public/forgotPasswordMesssage.jsp
	 */
	@HandlesEvent("username")
	public Resolution username() {
		try {
			User user = services.getUserByEmail(this.user.getEmail());

			// send username mail
			String subject = applicationResources.getString("mail.forgotUsername.subject");
			String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
				+ "/public/login";
			try {
				mailServiceManager.sendUsernameMail(user, subject, url);
			} catch (Exception e) {
				log.error(e);
			}

			context.getMessages().add(new LocalizableMessage("forgot.usernameSuccess", user.getEmail()));
			return new ForwardResolution(uiPath + "/public/forgotPasswordMessage.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The password method gets User Object using username then initializes user's password with a generated password
	 * and sends email to the user.
	 * 
	 * @return ForwardResolution to create request password success message from /public/forgotPasswordMessage.jsp.
	 */
	@HandlesEvent("password")
	public Resolution password() {
		try {
			User user = services.getUser(this.user.getUsername());

			// set current user interface langauge
			// user.setLanguage(context.getLanguage());

			// reset password
			String pwd = RandomStringUtils.random(8, true, true);
			user.setPassword(DigestUtils.md5Hex(pwd));
			services.saveOrUpdateUser(user);

			// set new password mail
			String subject = applicationResources.getString("mail.forgotPassword.subject");
			String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
				+ "/public/login";
			try {
				mailServiceManager.sendPasswordMail(user, pwd, subject, url);
			} catch (Exception e) {
				log.error(e);
			}

			context.getMessages().add(new LocalizableMessage("forgot.passwordSuccess", user.getEmail()));
			return new ForwardResolution(uiPath + "/public/forgotPasswordMessage.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
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
}