/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: GroupPermissionActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.GroupPermission;
import ca.ualberta.library.ir.enums.GroupType;
import ca.ualberta.library.ir.enums.SystemPermissions;

/**
 * The GroupPermissionActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/admin/group/permission/{$event}/{groupId}")
public class GroupPermissionActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(GroupPermissionActionBean.class);

	private Group group;
	private int groupId;
	private List<GroupPermission> permissions;
	private String[] perms;

	/**
	 * The GroupPermissionActionBean class constructor.
	 */

	public GroupPermissionActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("list")
	@Secure(roles = "/admin/group/permission")
	public Resolution list() {
		try {
			context.setAllGroups(services.getAllGroups());
			group = services.getGroup(groupId);
			permissions = new ArrayList<GroupPermission>();
			SystemPermissions[] perms = SystemPermissions.values();
			for (SystemPermissions perm : perms) {
				GroupPermission gp = new GroupPermission();
				gp.setPermission(perm.getPermission());
				GroupPermission g = services.getGroupPermissionByGroupId(groupId, perm.getPermission());
				gp.setAllowed(g == null ? false : g.isAllowed());
				permissions.add(gp);
			}
			return new ForwardResolution(uiPath + "/protected/editGroupPermission.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/admin/group/permission")
	public Resolution save() {
		try {
			List<String> permList = perms != null ? Arrays.asList(perms) : new ArrayList<String>();
			SystemPermissions[] perms = SystemPermissions.values();
			Group grp = services.getGroup(groupId);
			for (SystemPermissions perm : perms) {
				// log.debug("perm: " + perm);
				GroupPermission g = services.getGroupPermissionByGroupId(groupId, perm.getPermission());
				if (g == null) {
					g = new GroupPermission();
					g.setGroup(grp);
					g.setPermission(perm.getPermission());
				}

				// grant manage group, user permission and admin menu to admin
				if (groupId == GroupType.ADMIN.getValue()
					&& (perm.getPermission().equals(SystemPermissions.ADMIN_GROUP_PERMISSION.getPermission())
						|| perm.getPermission().equals(SystemPermissions.ADMIN_USER.getPermission()) || perm
						.getPermission().equals(SystemPermissions.ADMIN_MENU.getPermission()))) {
					g.setAllowed(true);
				} else {
					g.setAllowed(permList.contains(perm.getPermission()));
				}
				// log.debug(g);
				services.saveOrUpdateGroupPermission(g);
			}

			context.getMessages().add(new LocalizableMessage("group.permission.saveSuccess", grp.getName()));
			return new RedirectResolution("/action/admin/group/permission/list/" + groupId);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getGroupId getter method.
	 * 
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * The setGroupId setter method.
	 * 
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * The getPermissions getter method.
	 * 
	 * @return the permissions
	 */
	public List<GroupPermission> getPermissions() {
		return permissions;
	}

	/**
	 * The setPermissions setter method.
	 * 
	 * @param permissions the permissions to set
	 */
	public void setPermissions(List<GroupPermission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * The getPerms getter method.
	 * 
	 * @return the perms
	 */
	public String[] getPerms() {
		return perms;
	}

	/**
	 * The setPerms setter method.
	 * 
	 * @param perms the perms to set
	 */
	public void setPerms(String[] perms) {
		this.perms = perms;
	}

	/**
	 * The getGroup getter method.
	 * 
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * The setGroup setter method.
	 * 
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
}
