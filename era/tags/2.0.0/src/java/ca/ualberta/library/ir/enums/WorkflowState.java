/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: WorkflowState.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.enums;

/**
 * The WorkflowState class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum WorkflowState {
	Initial, // An item has been saved.
	Submit, // The item has been submitted.
	Cancel, // The submission has been cancelled.
	Review, // The item is being reviewed.
	Release, // The item has been returned back to task pool.
	Reject, // The item has been rejected.
	Revise, // The item is being revised.
	Archive; // The item has been approved and archived.
}
