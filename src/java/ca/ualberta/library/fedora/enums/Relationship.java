/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: Relationship.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.enums;

/**
 * The Relationship class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public enum Relationship {
	IS_MEMBER_OF_COLLECTION("isMemberOfCollection"), // collection relationship
	IS_MEMBER_OF("isMemberOf"), // community relationship
	IS_PART_OF("isPartOf"); // partOf dark and ccid relationships

	private String id;

	private Relationship(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getFieldName() {
		return "rel." + id;
	}
}
