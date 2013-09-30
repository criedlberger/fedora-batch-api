/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UserState.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The State class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum UserState {
	I("Inactive"), A("Active"), D("Deleted"), B("Blocked");
	private String name;
	private static final Map<Integer, UserState> map;
	static {
		map = new HashMap<Integer, UserState>();
		UserState[] values = UserState.values();
		for (UserState state : values) {
			map.put(state.getValue(), state);
		}
	}

	private UserState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return this.ordinal();
	}

	public static UserState getState(int state) {
		return map.get(state);
	}
}
