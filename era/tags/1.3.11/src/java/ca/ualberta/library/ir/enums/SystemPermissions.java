/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SystemPermissions.java 5602 2012-10-05 18:48:09Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The Permission class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5602 $ $Date: 2012-10-05 12:48:09 -0600 (Fri, 05 Oct 2012) $
 */
public enum SystemPermissions {

	// admin permissions
	ADMIN_MENU("/admin/menu"), // access admin menu
	ADMIN_GROUP("/admin/group"), // manage group
	ADMIN_GROUP_PERMISSION("/admin/group/permission"), // manage group permissions
	ADMIN_USER("/admin/user"), // manage user
	ADMIN_COMMUNITY("/admin/community"), // manage community
	ADMIN_COLLECTION("/admin/collection"), // manage collection
	ADMIN_ITEM("/admin/item"), // manage item
	ADMIN_DEPOSITOR("/admin/depositor"), // deposit on behalf
	ADMIN_APPROVE("/admin/approve"), // review and approval item
	ADMIN_EMAIL("/admin/email"), // send new task, rejected and archived notification emails
	ADMIN_DARK("/admin/dark"), // manage restricted item
	ADMIN_EMBARGOED("/admin/embargoed"), // manage emabargoed item
	ADMIN_DELETED("/admin/deleted"), // manage deleted item
	ADMIN_LOGIN("/admin/login"), // login as other user
	ADMIN_MESSAGE("/admin/message"), // post admin message
	ADMIN_PURGE("/admin/purge"), // purge item

	// user/password permissions
	USER_PASSWORD_UPDATE("/user/password/update"), //
	USER_INFORMATION_UPDATE("/user/information/update"), //
	USER_PROFILE_UPDATE("/user/profile/update"), //
	USER_CCID_ASSOCIATION("/user/ccid/association"), //

	// community permissions
	COMMUNITY_CREATE("/community/create"), //
	COMMUNITY_DELETE("/community/delete"), //
	COMMUNITY_READ("/community/read"), //
	COMMUNITY_UPDATE("/community/update"), //

	// collection permissions
	COLLECTION_CREATE("/collection/create"), //
	COLLECTION_READ("/collection/read"), //
	COLLECTION_UPDATE("/collection/update"), //
	COLLECTION_DELETE("/collection/delete"), //

	// item permissions
	ITEM_CREATE("/item/create"), //
	ITEM_READ("/item/read"), //
	ITEM_UPDATE("/item/update"), //
	ITEM_DELETE("/item/delete"), //

	// ITEM_EMBARGOED("/item/embargoed"), // edit embargoed item

	// admin schedule permissions
	// ADMIN_EMBARGOED_PUBLISHER("/admin/embargoed/publisher"), //
	// ADMIN_SUBSCRIPTION_NOTIFIER("/admin/subscription/notifier"), //
	// ADMIN_INDEX_BUILDER("/admin/index/builder"), //
	// ADMIN_PROQUEST_UPLOAD("/admin/proquest/upload"), //

	// developer permissions
	// DEV_MAIN("/dev/main"), //
	// DEV_TEST("/dev/test"), //

	; // end emum

	private static Map<String, String> permMap;
	static {
		permMap = new HashMap<String, String>();
		SystemPermissions[] perms = values();
		for (SystemPermissions p : perms) {
			permMap.put(p.getPermission(), p.toString());
		}
	}

	public static String getName(String permission) {
		return permMap.get(permission);
	}

	private String permission;

	private SystemPermissions(String permission) {
		this.permission = permission;
	}

	/**
	 * The getPermission getter method.
	 * 
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	public int getValue() {
		return this.ordinal();
	}
}
