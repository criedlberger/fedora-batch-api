/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SystemPermissions.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The Permission class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum SystemPermissions {

	// admin permissions
	ADMIN_MENU("/admin/menu"), //
	ADMIN_DARK("/admin/dark"), // manage dark repository
	ADMIN_EMBARGO("/admin/embargoed"), // manage embragoed item
	ADMIN_APPROVE("/admin/approve"), // review and approve submitted item
	ADMIN_DELETED("/admin/deleted"), // delete item
	ADMIN_PURGE("/admin/purge"), // purge item and remove submitted item
	ADMIN_ITEM("/admin/item"), // manage item and for reviewer to edit item
	ADMIN_COLLECTION("/admin/collection"), // manage collection
	ADMIN_COMMUNITY("/admin/community"), // manage community
	ADMIN_USER("/admin/user"), // manage user
	ADMIN_GROUP("/admin/group"), // manage group
	ADMIN_GROUP_PERMISSION("/admin/group/permission"), // manage group permission
	ADMIN_DEPOSITOR("/admin/depositor"), // deposit item on behalf of
	ADMIN_LOGIN("/admin/login"), // login as other user
	ADMIN_MESSAGE("/admin/message"), // post, remove admin message

	// admin schedule permissions
	// ADMIN_EMBARGOED_PUBLISHER("/admin/embargoed/publisher"), //
	// ADMIN_SUBSCRIPTION_NOTIFIER("/admin/subscription/notifier"), //
	// ADMIN_INDEX_BUILDER("/admin/index/builder"), //
	// ADMIN_PROQUEST_UPLOAD("/admin/proquest/upload"), //

	// developer permissions
	// DEV_MAIN("/dev/main"), //
	// DEV_TEST("/dev/test"), //

	// item permissions
	ITEM_CREATE("/item/create"), //
	ITEM_READ("/item/read"), //
	ITEM_UPDATE("/item/update"), // edit archive item
	ITEM_DELETE("/item/delete"), //
	// ITEM_EMBARGOED("/item/embargoed"), // edit embargoed item

	// collection permissions
	COLLECTION_CREATE("/collection/create"), //
	COLLECTION_READ("/collection/read"), //
	COLLECTION_UPDATE("/collection/update"), //
	COLLECTION_DELETE("/collection/delete"), //

	// community permissions
	COMMUNITY_CREATE("/community/create"), //
	COMMUNITY_READ("/community/read"), //
	COMMUNITY_UPDATE("/community/update"), //
	COMMUNITY_DELETE("/community/delete"), //

	// user/password permissions
	USER_PASSWORD_UPDATE("/user/password/update"), //
	USER_INFORMATION_UPDATE("/user/information/update"), //
	USER_PROFILE_UPDATE("/user/profile/update"), //
	USER_CCID_ASSOCIATION("/user/ccid/association"); //

	private String permission;
	private static Map<String, String> permMap;
	static {
		permMap = new HashMap<String, String>();
		SystemPermissions[] perms = values();
		for (SystemPermissions p : perms) {
			permMap.put(p.getPermission(), p.toString());
		}
	}

	private SystemPermissions(String permission) {
		this.permission = permission;
	}

	public static String getName(String permission) {
		return permMap.get(permission);
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
