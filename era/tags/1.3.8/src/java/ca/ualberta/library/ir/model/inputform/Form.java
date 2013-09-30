/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Form.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * The Form class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Form extends Model {

	@XmlEnum
	public enum Name {

		@XmlEnumValue("default")
		DEFAULT,

		@XmlEnumValue("thesis")
		THESIS,

		@XmlEnumValue("ser")
		SER,

		@XmlEnumValue("tr")
		TR;

		/**
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static Name getValue(String value) {
			return valueOf(value.toUpperCase());
		}

	}

	@XmlAttribute(name = "name", required = true)
	private String name;

	@XmlAttribute(name = "enabled", required = true)
	private boolean enabled;

	@XmlElement(name = "field")
	private List<Field> fields;

	/**
	 * The Form class constructor.
	 */
	public Form() {
		super();
	}

	/**
	 * The Form class constructor.
	 * 
	 * @param name
	 * @param fields
	 */
	public Form(String name, List<Field> fields) {
		super();
		this.name = name;
		this.fields = fields;
	}

	public Map<String, Field> getFieldMap() {
		Map<String, Field> map = new HashMap<String, Field>();
		if (fields == null || fields.isEmpty()) {
			return map;
		}
		for (Field fld : fields) {
			map.put(fld.getKey(), fld);
		}
		return map;
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
	 * The setName setter method.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * The setFields setter method.
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Form [fields=" + fields + ", name=" + name + "]";
	}

	/**
	 * The isEnabled getter method.
	 * 
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * The setEnabled setter method.
	 * 
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
