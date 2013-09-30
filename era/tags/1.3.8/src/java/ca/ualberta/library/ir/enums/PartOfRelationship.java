/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: PartOfRelationship.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import fedora.common.Constants;

/**
 * The PartOf class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum PartOfRelationship {
	CCID_AUTH, DARK_REPOSITORY, MANUAL_APPROVAL, EMBARGOED;

	public String getPid() {
		return ContentModel.NAMESPACE + ":" + this.toString();
	}

	public String getValue() {
		return this.toString();
	}

	public String getURI() {
		return Constants.FEDORA.uri + getPid();
	}

}
