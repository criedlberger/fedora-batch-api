/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Definitions.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.enums;

/**
 * The ERARelationship predicate enums.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum Definitions {
	EMBARGOED_DATE("embargoedDate"), // embargoed date
	WORKFLOW_STATE("workflowState"), // workflow state
	WORKFLOW_DATE("workflowDate"), // workflow date
	COMMENTS("comments"), // comments
	SUBMITTER_ID("submitterId"), // submitter user id
	USER_ID("userId"), // workflow user id
	SORT("sort"), // collection items sort(ex: sort.desc date)
	META_DESCRIPTION("metaDescription"), // item view meta description tag on html header(true/false)
	FORM_NAME("formName"), // collection deposit form name
	PROQUEST_UPLOAD("proquestUpload") // upload item in this collection to ProQuest
	; // end declaration

	public static final String uri = "http://era.library.ualberta.ca/schema/definitions.xsd#";
	private String localName;

	private Definitions(String localName) {
		this.localName = localName;
	}

	public String getURI() {
		return uri + localName;
	}
}
