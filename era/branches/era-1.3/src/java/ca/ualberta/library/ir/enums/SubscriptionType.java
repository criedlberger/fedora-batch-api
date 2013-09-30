/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: SubscriptionType.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

/**
 * The SubscriptionType class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum SubscriptionType {
	COMMUNITY, COLLECTION, RESEARCHER, TASK;

	public int getValue() {
		return this.ordinal();
	}
}
