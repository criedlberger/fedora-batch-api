/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MessageActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Message;
import ca.ualberta.library.ir.enums.State;

/**
 * The MessageActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/message/{$event}/{message.id}")
public class MessageActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(MessageActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "message", required = true),
		@Validate(field = "type", required = true) })
	private Message message;

	private List<Message> messages;

	/**
	 * The MessageActionBean class constructor.
	 */
	public MessageActionBean() {
		super();
	}

	@HandlesEvent("edit")
	@Secure(roles = "/admin/message")
	@DefaultHandler
	@DontValidate
	public Resolution edit() {
		try {
			message = new Message();
			message.setType(1);
			messages = services.getAllMessages();
			return new ForwardResolution(uiPath + "/protected/editMessage.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("post")
	@Secure(roles = "/admin/message")
	public Resolution save() {
		try {
			message.setState(State.A.getValue());
			message.setStartDate(new Date());
			message.setUser(user);
			services.saveOrUpdateMessage(message);
			context.getMessages().add(new LocalizableMessage("admin.message.saveSuccess"));
			return new RedirectResolution("/action/message");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("remove")
	@DontValidate
	@Secure(roles = "/admin/message")
	public Resolution remove() {
		try {
			message = services.getMessage(message.getId());
			message.setState(State.I.getValue());
			message.setEndDate(new Date());
			message.setUser(user);
			services.saveOrUpdateMessage(message);
			context.getMessages().add(new LocalizableMessage("admin.message.removeSuccess"));
			return new RedirectResolution("/action/message");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getMessage getter method.
	 * 
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * The setMessage setter method.
	 * 
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * The getMessages getter method.
	 * 
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}
}
