/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ObjectPermissions.java 5602 2012-10-05 18:48:09Z pcharoen $
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
public enum ObjectPermissions {
	PREFIX("/object"), // object permission prefix
	OBJECT_DARK("/object/dark"), // dark repository object
	OBJECT_CCID("/object/ccid"), // ccid authenticaton object
	OBJECT_APPROVE("/object/approve"), // approval object
	OBJECT_OWNER("/object/owner"), // object owner
	OBJECT_EMBARGOED("/object/embargoed"); // object embargoed
	private String permission;
	private static Map<String, ObjectPermissions> permMap;
	static {
		permMap = new HashMap<String, ObjectPermissions>();
		ObjectPermissions[] perms = values();
		for (ObjectPermissions p : perms) {
			permMap.put(p.getPermission(), p);
		}
	}

	private ObjectPermissions(String permission) {
		this.permission = permission;
	}

	public static ObjectPermissions getObjectPermissions(String permission) {
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
