/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: State.java 5430 2012-07-12 22:30:19Z pcharoen $
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
public enum State {
	I("Inactive"), // inactive
	A("Active"), // active
	D("Deleted"), // deleted
	U("Uploaded"); // proquest uploaded

	private String name;

	private static final Map<Integer, State> valueMap;

	private static final Map<String, State> nameMap;

	static {

		// init valueMap and nameMap
		valueMap = new HashMap<Integer, State>();
		nameMap = new HashMap<String, State>();
		State[] values = State.values();
		for (State state : values) {
			valueMap.put(state.getValue(), state);
			nameMap.put(state.getName(), state);
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

	public static State getState(int value) {
		return valueMap.get(value);
	}

	public static State getState(String name) {
		return nameMap.get(name);
	}
}
