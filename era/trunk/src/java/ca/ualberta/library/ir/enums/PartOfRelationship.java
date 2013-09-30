/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: PartOfRelationship.java 5606 2012-10-10 16:45:09Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import org.fcrepo.common.Constants;

/**
 * The PartOf class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
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
