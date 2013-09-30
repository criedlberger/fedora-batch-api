/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UserActivationActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.Register;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The UserActivationActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/activation/{$event}/{user.id}/{register.activationKey}")
public class UserActivationActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(UserActivationActionBean.class);

	private User user;
	private Register register;

	/**
	 * The UserActivationActionBean class constructor.
	 */
	public UserActivationActionBean() {
		super();
	}

	@HandlesEvent("confirm")
	@DontValidate
	public Resolution confirm() {
		try {
			user = services.getUser(user.getId());

			switch (State.getState(user.getState())) {
			case I:
				Register reg = services.getRegisterByActivationKey(register.getActivationKey());
				if (reg == null) {
					context.getValidationErrors().addGlobalError(
						new LocalizableError("activation.confirmError", ActionConstants.mailAdmin));
					return new ForwardResolution(uiPath + "/public/activationMessage.jsp");
				}
				Group grp = services.getGroup(user.getGroup().getId());
				user.setGroup(grp);
				user.setState(State.A.getValue());
				services.deleteRegister(reg.getId());
				user.setRegisters(null);
				services.saveOrUpdateUser(user);

				// send account confirmation mail
				String subject = applicationResources.getString("mail.activation.subject");
				String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
					+ "/public/login";
				try {
					mailServiceManager.sendActivationMail(user, subject, url);
				} catch (Exception e) {
					log.warn("Could not send activation success email!", e);
				}
				context.getMessages().add(new LocalizableMessage("activation.confirmSuccess"));
				break;

			case A:
				context.getMessages().add(new LocalizableMessage("activation.confirmSuccess"));
				break;

			case D:
				context.getValidationErrors().addGlobalError(
					new LocalizableError("activation.deletedUser", ActionConstants.mailAdmin));
				break;

			default:
				context.getValidationErrors().addGlobalError(
					new LocalizableError("activation.invalidUserState", ActionConstants.mailAdmin));
				break;

			}

			return new ForwardResolution(uiPath + "/public/activationMessage.jsp");

		} catch (Exception e) {
			log.error("Could not activate this user!", e);
			context.getValidationErrors().addGlobalError(
				new LocalizableError("activation.confirmError", ActionConstants.mailAdmin));
			return new ForwardResolution(uiPath + "/public/activationMessage.jsp");
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
	 * The getRegister getter method.
	 * 
	 * @return the register
	 */
	public Register getRegister() {
		return register;
	}

	/**
	 * The setRegister setter method.
	 * 
	 * @param register the register to set
	 */
	public void setRegister(Register register) {
		this.register = register;
	}

}
