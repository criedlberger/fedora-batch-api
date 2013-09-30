/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: DatastreamID.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import ca.ualberta.library.ir.action.ActionConstants;

/**
 * The DatastreamID class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum DatastreamID {
	DS, // Documents (Prefix: DS1, DS2, ...)
	DC, // Dublin Core Metadata
	DCQ, // Dublin Core Qualified Metadata
	RELS_EXT, // Relationships
	LICENSE, // License
	THUMBNAIL, // Thumbnail Image
	POLICY; // XACML Policy

	@Override
	public String toString() {
		if (this.equals(DCQ)) {
			return ActionConstants.metadataId;
		}
		return super.toString().replace('_', '-');
	}
}
