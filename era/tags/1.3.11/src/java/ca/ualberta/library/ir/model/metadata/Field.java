/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Field.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.metadata;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * The Field class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Field extends Model implements Comparable<Field> {

	@XmlEnum
	public enum Element {

		@XmlEnumValue("dc:creator")
		dc_creator,

		@XmlEnumValue("dc:format")
		dc_format,

		@XmlEnumValue("dc:language")
		dc_language,

		@XmlEnumValue("dc:relation")
		dc_relation,

		@XmlEnumValue("dc:source")
		dc_source,

		@XmlEnumValue("dc:subject")
		dc_subject,

		@XmlEnumValue("dc:title")
		dc_title,

		@XmlEnumValue("dc:type")
		dc_type,

		@XmlEnumValue("dc:date")
		dc_date,

		@XmlEnumValue("dc:rights")
		dc_rights,

		@XmlEnumValue("dcterms:abstract")
		dcterms_abstract,

		@XmlEnumValue("dcterms:alternative")
		dcterms_alternative,

		@XmlEnumValue("dcterms:created")
		dcterms_created,

		@XmlEnumValue("dcterms:dateaccepted")
		dcterms_dateaccepted,

		@XmlEnumValue("dcterms:datesubmitted")
		dcterms_datesubmitted,

		@XmlEnumValue("dcterms:description")
		dcterms_description,

		@XmlEnumValue("dcterms:extent")
		dcterms_extent,

		@XmlEnumValue("dcterms:identifier")
		dcterms_identifier,

		@XmlEnumValue("dcterms:isversionof")
		dcterms_isversionof,

		@XmlEnumValue("dcterms:spatial")
		dcterms_spatial,

		@XmlEnumValue("dcterms:temporal")
		dcterms_temporal,

		@XmlEnumValue("eraterms:graduationdate")
		eraterms_graduationdate,

		@XmlEnumValue("eraterms:specialization")
		eraterms_specialization,

		@XmlEnumValue("eraterms:ser")
		eraterms_ser,

		@XmlEnumValue("thesis:contributor")
		thesis_contributor,

		@XmlEnumValue("thesis:degree")
		thesis_degree,

		@XmlEnumValue("thesis:grantor")
		thesis_grantor,

		@XmlEnumValue("thesis:level")
		thesis_level,

		@XmlEnumValue("thesis:name")
		thesis_name

		; // end

		/**
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return super.toString().replace('_', ':');
		}

	}

	/**
	 * The Key class contains enum fields for auto-generated metadata fields.
	 * 
	 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
	 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
	 */
	public enum Key {

		title("dc:title"), // title

		creator("dc:creator"), // creator

		type("dc:type"), // type

		date("dc:date"), // date

		description("dc:description"), // description

		format("dc:format.xsi:type.dcterms:IMT"), // file minetype

		extent("dcterms:extent"), // file size

		identifier("dcterms:identifier.xsi:type.dcterms:URI"), // handle

		uuid("dcterms:identifier.xsi:type.eraterms:local"), // uuid

		dateSubmitted("dcterms:datesubmitted.xsi:type.dcterms:W3CDTF"), // submitted date

		dateAccepted("dcterms:dateaccepted.xsi:type.dcterms:W3CDTF") // accepted date

		; //

		public static Map<String, Key> keyMap;

		static {
			keyMap = new HashMap<String, Key>();
			for (Key key : Key.values()) {
				keyMap.put(key.toString(), key);
			}
		}

		private String value;

		Key(String value) {
			this.value = value;
		}

		/**
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.value;
		}

		/**
		 * The getValueOf method gets Enum by field name value;
		 * 
		 * @param name
		 * @return
		 */
		public static Key getValueOf(String name) {
			return keyMap.get(name);
		}
	}

	@XmlElement(name = "attribute")
	private Attribute attribute;

	@XmlTransient
	private String fieldName;

	@XmlAttribute(name = "name", required = true)
	private String name;

	@XmlAttribute(name = "qualifier")
	private String qualifier;

	@XmlElement
	private String value;

	/**
	 * The Field class constructor.
	 */
	public Field() {
		super();
	}

	/**
	 * The Field class constructor.
	 * 
	 * @param fieldName
	 * @param name
	 * @param qualifier
	 * @param attribute
	 * @param value
	 */
	public Field(String fieldName, String name, String qualifier, Attribute attribute, String value) {
		super();
		this.fieldName = fieldName;
		this.name = name;
		this.qualifier = qualifier;
		this.attribute = attribute;
		this.value = value;
	}

	/**
	 * The getKey method creates a field mapping key from name, qualifier, attribute name and value.
	 * 
	 * @return The field mapping key
	 */
	public String getKey() {
		StringBuilder key = new StringBuilder();
		key.append(name);
		key.append((StringUtils.trimToNull(qualifier) != null ? "." + qualifier : ""));
		key.append((attribute != null ? "." + attribute.getName() : ""));
		key.append((attribute != null ? "." + attribute.getValue() : ""));
		return key.toString();
	}

	public String getResourceKey() {
		return this.getKey().replace(':', '-');
	}

	/**
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Field field) {
		int result = StringUtils.trimToEmpty(name).compareTo(StringUtils.trimToEmpty(field.getName()));
		result = result == 0 ? StringUtils.trimToEmpty(qualifier).compareTo(
			StringUtils.trimToEmpty(field.getQualifier())) : result;
		result = result == 0 ? StringUtils.trimToEmpty(attribute == null ? null : attribute.getName()).compareTo(
			StringUtils.trimToEmpty(field.getAttribute() == null ? null : field.getAttribute().getName())) : result;
		return result == 0 ? StringUtils.trimToEmpty(attribute == null ? null : attribute.getValue()).compareTo(
			StringUtils.trimToEmpty(field.getAttribute() == null ? null : field.getAttribute().getValue())) : result;
	}

	/**
	 * The getAttribute getter method.
	 * 
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * The getFieldName getter method.
	 * 
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * The getName getter method.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The getQualifier getter method.
	 * 
	 * @return the qualifier
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * The getValue getter method.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The setAttribute setter method.
	 * 
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * The setFieldName setter method.
	 * 
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * The setName setter method.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The setQualifier setter method.
	 * 
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * The setValue setter method.
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Field [attribute=" + attribute + ", fieldName=" + fieldName + ", name=" + name + ", qualifier="
			+ qualifier + ", value=" + value + "]";
	}
}
