/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ContentModel.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

import fedora.common.Constants;

/**
 * The ContentModel class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum ContentModel {

	TYPE_OF_ITEM("TypeOfItem"), // Type of Item Content Model
	LICENSE("License"), // License Content Model
	FORM("Form") // License Content Model
	; // end declaration

	// object content model namespace
	public static final String NAMESPACE = "ir";

	// form content model namespace
	public static final String FORM_NS = "era-form";

	// resource prefix
	public static final String RESOURCE_PREFIX = "ContentModel.";

	private String id;

	private String pid;

	private ContentModel() {
	}

	private ContentModel(String id) {
		this.id = id;
		this.pid = NAMESPACE + ":" + id;
	}

	public String getPid() {
		return pid;
	}

	public static String getURIPrefix() {
		return Constants.FEDORA.uri + NAMESPACE + ":";
	}

	public String getURI() {
		return Constants.FEDORA.uri + pid;
	}

	@Override
	public String toString() {
		return pid;
	}

	/**
	 * The getId getter method.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}