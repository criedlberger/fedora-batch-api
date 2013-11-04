/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ActionBeanThread.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import java.util.regex.Pattern;

import ca.ualberta.library.ir.enums.WorkflowState;

/**
 * The ActionBeanThread class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 * @deprecated To be removed using concurrency instead.
 */
@Deprecated
public class ActionBeanThread extends Thread {

	private static final Pattern namePattern = Pattern.compile("\\.|\\$");
	protected WorkflowState state;

	/**
	 * The ActionBeanThread class constructor.
	 */
	public ActionBeanThread() {
		super();
	}

	public ActionBeanThread(Thread parent) {
		String[] names = namePattern.split(this.getClass().getName());
		StringBuilder name = new StringBuilder();
		name.append(parent.getName());
		name.append(".");
		name.append(names[names.length - 1]);
		name.append("-");
		name.append(this.getId());
		this.setName(name.toString());
	}

	public ActionBeanThread(Thread parent, WorkflowState state) {
		this(parent);
		this.state = state;
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param target
	 */
	public ActionBeanThread(Runnable target) {
		super(target);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param name
	 */
	public ActionBeanThread(String name) {
		super(name);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param group
	 * @param target
	 */
	public ActionBeanThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param group
	 * @param name
	 */
	public ActionBeanThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param target
	 * @param name
	 */
	public ActionBeanThread(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param group
	 * @param target
	 * @param name
	 */
	public ActionBeanThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	/**
	 * The ActionBeanThread class constructor.
	 * 
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	public ActionBeanThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}
}