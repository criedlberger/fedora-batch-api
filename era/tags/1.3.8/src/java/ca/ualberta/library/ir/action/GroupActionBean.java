/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: GroupActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

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

import ca.ualberta.library.ir.domain.Group;

/**
 * The GroupActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/group/{$event}/{group.id}")
public class GroupActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(GroupActionBean.class);

	@ValidateNestedProperties( { @Validate(field = "name", required = true) })
	private Group group;

	/**
	 * The GroupActionBean class constructor.
	 */
	public GroupActionBean() {
		super();
	}

	@HandlesEvent("add")
	@Secure(roles = "/admin/group")
	@DontValidate
	public Resolution add() {
		try {
			group = new Group();
			return new ForwardResolution(uiPath + "/protected/editGroup.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("edit")
	@Secure(roles = "/admin/group")
	@DontValidate
	public Resolution edit() {
		try {
			group = services.getGroup(group.getId());
			return new ForwardResolution(uiPath + "/protected/editGroup.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/admin/group")
	public Resolution save() {
		try {
			services.saveOrUpdateGroup(group);
			context.setAllGroups(services.getAllGroups());
			context.getMessages().add(new LocalizableMessage("group.saveSuccess", group.getName()));
			return new RedirectResolution("/action/admin/group/permission/list/" + group.getId());
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getGroup getter method.
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * The setGroup setter method.
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
}
