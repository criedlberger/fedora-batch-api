/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: FedoraRelationship.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import fedora.common.Constants;

/**
 * The Relationship class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum FedoraRelationship {
	IS_MEMBER_OF_COLLECTION("isMemberOfCollection"), // collection relationship
	IS_MEMBER_OF("isMemberOf"), // community relationship
	IS_PART_OF("isPartOf"); // partOf dark and ccid relationships

	private static final String uri = Constants.RELS_EXT.uri;
	private static final String prefix = "rel.";

	private String localName;

	private FedoraRelationship(String localName) {
		this.localName = localName;
	}

	public String getLocalName() {
		return this.localName;
	}

	public String getFieldName() {
		// solr index field name
		return prefix + localName;
	}

	public String getURI() {
		return uri + localName;
	}

}
