/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ActivationActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.Register;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The ActivationActionBean class is for administator to activate user account.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/activation/{$event}/{user.id}")
public class ActivationActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ActivationActionBean.class);

	private User user;
	private Register register;

	private List<User> users;
	private int start;
	private int rows;
	private long numFound;
	private int resultRows;

	/**
	 * The ActivationActionBean class constructor.
	 */
	public ActivationActionBean() {
		super();
	}

	@HandlesEvent("edit")
	@Secure(roles = "/admin/activate")
	public Resolution edit() {
		try {
			user = services.getUser(user.getId());
			Set<Register> set = user.getRegisters();
			if (!set.isEmpty()) {
				register = set.iterator().next();
			}
			return new ForwardResolution(uiPath + "/protected/activation.jsp");
		} catch (Exception e) {
			log.error("Could not find this user!", e);
			return forwardExceptionError("Could not find this user!", e);
		}
	}

	@HandlesEvent("activate")
	@Secure(roles = "/admin/activate")
	public Resolution activate() {
		try {
			user = services.getUser(user.getId());
			Group grp = services.getGroup(user.getGroup().getId());
			user = services.getUser(user.getId());
			user.setGroup(grp);
			user.setState(State.A.getValue());
			for (Register reg : user.getRegisters()) {
				services.deleteRegister(reg.getId());
			}
			user.setRegisters(null);
			services.saveOrUpdateUser(user);

			// send account confirmation mail
			String subject = applicationResources.getString("mail.activation.subject");
			String url = ApplicationProperties.getString("http.server.url") + request.getContextPath()
				+ "/public/login";
			try {
				mailServiceManager.sendActivationMail(user, subject, url);
			} catch (Exception e) {
				log.error(e);
			}

			context.getMessages().add(
				new LocalizableMessage("activation.emailSuccess", user.getUsername(), user.getEmail()));
			return new ForwardResolution(uiPath + "/protected/activationMessage.jsp");

		} catch (Exception e) {
			log.error("Could not activate this user!", e);
			return forwardExceptionError("Could not activate this user!", e);
		}
	}

	/**
	 * The getStart getter method.
	 * 
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * The setStart setter method.
	 * 
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * The getRows getter method.
	 * 
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * The setRows setter method.
	 * 
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * The getActivationCount getter method.
	 * 
	 * @return the activationCount
	 */
	public long getNumFound() {
		return numFound;
	}

	/**
	 * The setActivationCount setter method.
	 * 
	 * @param activationCount the activationCount to set
	 */
	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	/**
	 * The getResultRows getter method.
	 * 
	 * @return the resultRows
	 */
	public int getResultRows() {
		return resultRows;
	}

	/**
	 * The setResultRows setter method.
	 * 
	 * @param resultRows the resultRows to set
	 */
	public void setResultRows(int resultRows) {
		this.resultRows = resultRows;
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

	/**
	 * The setNumFound setter method.
	 * 
	 * @param numFound the numFound to set
	 */
	public void setNumFound(long numFound) {
		this.numFound = numFound;
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
