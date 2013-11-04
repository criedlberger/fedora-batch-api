/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: DublinCore.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.fedora;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openarchives.oai.x20.oaiDc.DcDocument;
import org.openarchives.oai.x20.oaiDc.OaiDcType;
import org.purl.dc.elements.x11.ElementType;

/**
 * The DublinCore class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class DublinCore extends Model {

	private List<DublinCoreField> fields;
	private String location;
	private String createdDate;

	/**
	 * The DublinCore class constructor.
	 */
	public DublinCore() {
		super();
		this.fields = new ArrayList<DublinCoreField>();
		this.fields.add(new DublinCoreField("identifier", null)); // 0
		this.fields.add(new DublinCoreField("title", null)); // 1
		this.fields.add(new DublinCoreField("creator", null)); // 2
		this.fields.add(new DublinCoreField("subject", null)); // 3
		this.fields.add(new DublinCoreField("description", null)); // 4
		this.fields.add(new DublinCoreField("publisher", null)); // 5
		this.fields.add(new DublinCoreField("contributor", null)); // 6
		this.fields.add(new DublinCoreField("date", null)); // 7
		this.fields.add(new DublinCoreField("type", null)); // 8
		this.fields.add(new DublinCoreField("format", null)); // 9
		this.fields.add(new DublinCoreField("source", null)); // 10
		this.fields.add(new DublinCoreField("language", null)); // 11
		this.fields.add(new DublinCoreField("relation", null)); // 12
		this.fields.add(new DublinCoreField("coverage", null)); // 13
		this.fields.add(new DublinCoreField("rights", null)); // 14

		// initialize with empty string
		// this.fields.add(new DublinCoreField("identifier", Arrays.asList(""))); // 0
		// this.fields.add(new DublinCoreField("title", Arrays.asList(""))); // 1
		// this.fields.add(new DublinCoreField("creator", Arrays.asList(""))); // 2
		// this.fields.add(new DublinCoreField("subject", Arrays.asList(""))); // 3
		// this.fields.add(new DublinCoreField("description", Arrays.asList(""))); // 4
		// this.fields.add(new DublinCoreField("publisher", Arrays.asList(""))); // 5
		// this.fields.add(new DublinCoreField("contributor", Arrays.asList(""))); // 6
		// this.fields.add(new DublinCoreField("date", Arrays.asList(""))); // 7
		// this.fields.add(new DublinCoreField("type", Arrays.asList(""))); // 8
		// this.fields.add(new DublinCoreField("format", Arrays.asList(""))); // 9
		// this.fields.add(new DublinCoreField("source", Arrays.asList(""))); // 10
		// this.fields.add(new DublinCoreField("language", Arrays.asList(""))); // 11
		// this.fields.add(new DublinCoreField("relation", Arrays.asList(""))); // 12
		// this.fields.add(new DublinCoreField("coverage", Arrays.asList(""))); // 13
		// this.fields.add(new DublinCoreField("rights", Arrays.asList(""))); // 14
	}

	public DublinCore(DcDocument dcDocument) {
		OaiDcType oai = dcDocument.getDc();
		this.fields = new ArrayList<DublinCoreField>();
		this.fields.add(new DublinCoreField("identifier", toList(oai.getIdentifierArray())));
		this.fields.add(new DublinCoreField("title", toList(oai.getTitleArray())));
		this.fields.add(new DublinCoreField("creator", toList(oai.getCreatorArray())));
		this.fields.add(new DublinCoreField("subject", toList(oai.getSubjectArray())));
		this.fields.add(new DublinCoreField("description", toList(oai.getDescriptionArray())));
		this.fields.add(new DublinCoreField("publisher", toList(oai.getPublisherArray())));
		this.fields.add(new DublinCoreField("contributor", toList(oai.getContributorArray())));
		this.fields.add(new DublinCoreField("date", toList(oai.getDateArray())));
		this.fields.add(new DublinCoreField("type", toList(oai.getTypeArray())));
		this.fields.add(new DublinCoreField("format", toList(oai.getFormatArray())));
		this.fields.add(new DublinCoreField("source", toList(oai.getSourceArray())));
		this.fields.add(new DublinCoreField("language", toList(oai.getLanguageArray())));
		this.fields.add(new DublinCoreField("relation", toList(oai.getRelationArray())));
		this.fields.add(new DublinCoreField("coverage", toList(oai.getCoverageArray())));
		this.fields.add(new DublinCoreField("rights", toList(oai.getRightsArray())));
	}

	public DublinCore(List<DublinCoreField> fields) throws XmlException {
		this.fields = fields;
	}

	public DcDocument parse() throws XmlException {
		DcDocument dcDocument = DcDocument.Factory.parse(
			"<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" "
				+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"></oai_dc:dc>", new XmlOptions()
				.setCharacterEncoding("UTF-8"));
		OaiDcType oai = dcDocument.getDc();
		if (fields != null && fields.size() > 0) {
			for (int i = 0; fields.get(0).getValues() != null && i < fields.get(0).getValues().size(); i++) {
				// if (fields.get(0).getValues().get(0).trim().length() > 0)
				oai.addNewIdentifier().setStringValue(fields.get(0).getValues().get(i));
			}
			for (int i = 0; fields.get(1).getValues() != null && i < fields.get(1).getValues().size(); i++) {
				oai.addNewTitle().setStringValue(fields.get(1).getValues().get(i));
			}
			for (int i = 0; fields.get(2).getValues() != null && i < fields.get(2).getValues().size(); i++) {
				oai.addNewCreator().setStringValue(fields.get(2).getValues().get(i));
			}
			for (int i = 0; fields.get(3).getValues() != null && i < fields.get(3).getValues().size(); i++) {
				oai.addNewSubject().setStringValue(fields.get(3).getValues().get(i));
			}
			for (int i = 0; fields.get(4).getValues() != null && i < fields.get(4).getValues().size(); i++) {
				oai.addNewDescription().setStringValue(fields.get(4).getValues().get(i));
			}
			for (int i = 0; fields.get(5).getValues() != null && i < fields.get(5).getValues().size(); i++) {
				oai.addNewPublisher().setStringValue(fields.get(5).getValues().get(i));
			}
			for (int i = 0; fields.get(6).getValues() != null && i < fields.get(6).getValues().size(); i++) {
				oai.addNewContributor().setStringValue(fields.get(6).getValues().get(i));
			}
			for (int i = 0; fields.get(7).getValues() != null && i < fields.get(7).getValues().size(); i++) {
				oai.addNewDate().setStringValue(fields.get(7).getValues().get(i));
			}
			for (int i = 0; fields.get(8).getValues() != null && i < fields.get(8).getValues().size(); i++) {
				oai.addNewType().setStringValue(fields.get(8).getValues().get(i));
			}
			for (int i = 0; fields.get(9).getValues() != null && i < fields.get(9).getValues().size(); i++) {
				oai.addNewFormat().setStringValue(fields.get(9).getValues().get(i));
			}
			for (int i = 0; fields.get(10).getValues() != null && i < fields.get(10).getValues().size(); i++) {
				oai.addNewSource().setStringValue(fields.get(10).getValues().get(i));
			}
			for (int i = 0; fields.get(11).getValues() != null && i < fields.get(11).getValues().size(); i++) {
				oai.addNewLanguage().setStringValue(fields.get(11).getValues().get(i));
			}
			for (int i = 0; fields.get(12).getValues() != null && i < fields.get(12).getValues().size(); i++) {
				oai.addNewRelation().setStringValue(fields.get(12).getValues().get(i));
			}
			for (int i = 0; fields.get(13).getValues() != null && i < fields.get(13).getValues().size(); i++) {
				oai.addNewCoverage().setStringValue(fields.get(13).getValues().get(i));
			}
			for (int i = 0; fields.get(14).getValues() != null && i < fields.get(14).getValues().size(); i++) {
				oai.addNewRights().setStringValue(fields.get(14).getValues().get(i));
			}
		}
		return dcDocument;
	}

	private List<String> toList(ElementType[] elementTypes) {
		List<String> list = new ArrayList<String>();
		if (elementTypes == null || elementTypes.length == 0) {
			return list;
		}
		for (int i = 0; i < elementTypes.length; i++) {
			list.add(elementTypes[i].getStringValue());
		}
		return list;
	}

	/**
	 * The getFields getter method.
	 * @return the fields
	 */
	public List<DublinCoreField> getFields() {
		return fields;
	}

	/**
	 * The setFields setter method.
	 * @param fields the fields to set
	 */
	public void setFields(List<DublinCoreField> fields) {
		this.fields = fields;
	}

	/**
	 * The getLocation getter method.
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * The setLocation setter method.
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("fields", fields).append("location", location).toString();
	}

	/**
	 * The getCreatedDate getter method.
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * The setCreatedDate setter method.
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}