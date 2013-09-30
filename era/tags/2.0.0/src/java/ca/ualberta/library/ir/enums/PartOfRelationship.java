/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: PartOfRelationship.java 5615 2012-10-16 18:20:47Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import org.fcrepo.common.Constants;

/**
 * The PartOf class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5615 $ $Date: 2012-10-16 12:20:47 -0600 (Tue, 16 Oct 2012) $
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
