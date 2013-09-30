/**
 * Information Technology Services
 * University of Alberta Libraries
 * Project: ir
 * $Id: RegisterActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.Date;
import java.util.UUID;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.action.Wizard;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Register;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.AuthType;
import ca.ualberta.library.ir.enums.GroupType;
import ca.ualberta.library.ir.enums.Language;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The RegisterActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@Wizard(startEvents = "start")
@UrlBinding("/public/register/{$event}")
public class RegisterActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(RegisterActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "firstName", required = true, maxlength = 50),
		@Validate(field = "lastName", required = true, maxlength = 50),
		@Validate(field = "username", required = true, mask = "^[a-zA-Z0-9_-]{3,20}$", minlength = 3, maxlength = 20),
		@Validate(field = "email", required = true, maxlength = 50, converter = EmailTypeConverter.class),
		@Validate(field = "password", required = false, minlength = 5, maxlength = 20) })
	private User user;

	private AuthType authType;

	@Validate(required = false, minlength = 5, maxlength = 32, expression = "this == user.password")
	private String confirmPassword;

	private String note;

	/**
	 * The RegisterActionBean class constructor.
	 */
	public RegisterActionBean() {
		super();
	}

	/**
	 * Validates that the two passwords entered match each other, and that the username entered is not already taken in
	 * the system.
	 */
	@ValidationMethod
	public void validate(ValidationErrors errors) {
		if (services.getUser(this.user.getUsername()) != null) {
			errors.add("user.username", new LocalizableError("usernameTaken"));
		}
		if (services.getUserByEmail(user.getEmail()) != null) {
			errors.add("user.email", new LocalizableError("emailExisted"));
		}
	}

	@DefaultHandler
	@HandlesEvent("start")
	@DontValidate
	public Resolution start() {
		if (ApplicationProperties.getBoolean("https.enabled") && !request.getScheme().equals("https")) {
			return new RedirectResolution(httpsServerUrl + request.getContextPath() + "/public/register", false);
		}
		return new ForwardResolution(uiPath + "/public/register.jsp");
	}

	@HandlesEvent("gotoStep1")
	@DontValidate
	public Resolution gotoStep1() throws Exception {
		return new ForwardResolution(uiPath + "/public/register.jsp");
	}

	@HandlesEvent("gotoStep2")
	public Resolution gotoStep2() throws Exception {
		return new ForwardResolution(uiPath + "/public/register2.jsp");
	}

	/**
	 * Registers a new user, logs them in, and redirects them to the bug list page.
	 */
	@HandlesEvent("register")
	public Resolution register() {
		try {
			if (ApplicationProperties.getBoolean("https.enabled") && !request.getScheme().equals("https")) {
				return new RedirectResolution(httpsServerUrl + request.getContextPath() + "/public/register", false);
			}

			user.setState(State.I.getValue());
			user.setGroup(services.getGroup(GroupType.USER.getValue())); // user group
			user.setCreatedDate(new Date());
			user.setPassword(DigestUtils.md5Hex(user.getPassword()));
			user.setLanguage(Language.en.toString());
			services.saveOrUpdateUser(user);

			Register register = new Register();
			register.setUser(user);
			register.setActivationKey(UUID.randomUUID().toString());
			register.setNote(note);
			services.saveOrUpdateRegister(register);

			// send register confirmation mail
			String subject = applicationResources.getString("mail.register.subject");
			String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
				+ "/public/activation/confirm/" + user.getId() + "/" + register.getActivationKey();
			try {
				mailServiceManager.sendRegisterMail(user, register, subject, url);
			} catch (Exception e) {
				log.error(e);
			}

			// context.setUser(this.user);
			context.getMessages().add(
				new LocalizableMessage("register.successMessage", this.user.getFirstName(), this.user.getUsername()));

			return new ForwardResolution(uiPath + "/public/registerMessage.jsp");
		} catch (Exception e) {
			log.error("Could not register!", e);
			return forwardExceptionError("Could not register!", e);
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
	 * The getConfirmPassword getter method.
	 * 
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * The setConfirmPassword setter method.
	 * 
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * The getAuthType getter method.
	 * 
	 * @return the authType
	 */
	public AuthType getAuthType() {
		return authType;
	}

	/**
	 * The setAuthType setter method.
	 * 
	 * @param authType the authType to set
	 */
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	/**
	 * The getNote getter method.
	 * 
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * The setNote setter method.
	 * 
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
}
