/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: State.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The State class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public enum State {
	I("Inactive"), A("Active"), D("Deleted");
	private String name;
	private static final Map<Integer, State> map;
	static {
		map = new HashMap<Integer, State>();
		State[] values = State.values();
		for (State state : values) {
			map.put(state.getValue(), state);
		}
	}

	private State(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return this.ordinal();
	}

	public static State getState(int state) {
		return map.get(state);
	}
}
