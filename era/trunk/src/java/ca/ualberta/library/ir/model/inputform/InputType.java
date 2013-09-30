/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: InputType.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * The InputType class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class InputType {

	@XmlEnum
	public enum Value {

		/**
		 * TODO: implement checkbox input-type
		 * A checkbox input field.
		 */
		@XmlEnumValue("checkbox")
		checkbox,

		/**
		 * A current date mapped value from value-pairs as a read only field.
		 */
		@XmlEnumValue("daterange_value")
		daterange_value,

		/**
		 * One selected item dropdown list if field repeatable is false.
		 */
		@XmlEnumValue("dropdown")
		dropdown,

		/**
		 * A hidden input with value.
		 */
		@XmlEnumValue("hidden")
		hidden,

		/**
		 * A label and description.
		 */
		@XmlEnumValue("label")
		label,

		/**
		 * One dropdown list and one input text box.
		 */
		@XmlEnumValue("qualdrop_value")
		qualdrop_value,

		/**
		 * A read only value.
		 */
		@XmlEnumValue("readonly")
		readonly,

		/**
		 * A input text box.
		 */
		@XmlEnumValue("text")
		text,

		/**
		 * A input textarea box.
		 */
		@XmlEnumValue("textarea")
		textarea,

		/**
		 * A file input box.
		 */
		@XmlEnumValue("file")
		file,

		/**
		 * A message in form message area.
		 */
		@XmlEnumValue("message")
		message

		; // close declaration
	}

	@XmlAttribute(name = "value-pairs-name")
	private String valuePairsName;

	@XmlValue
	private Value value;

	/**
	 * The InputType class constructor.
	 */
	public InputType() {
		super();
	}

	/**
	 * The InputType class constructor.
	 * 
	 * @param valuePairsName
	 * @param value
	 */
	public InputType(String valuePairsName, Value value) {
		super();
		this.valuePairsName = valuePairsName;
		this.value = value;
	}

	/**
	 * The getValuePairsName getter method.
	 * 
	 * @return the valuePairsName
	 */
	public String getValuePairsName() {
		return valuePairsName;
	}

	/**
	 * The setValuePairsName setter method.
	 * 
	 * @param valuePairsName the valuePairsName to set
	 */
	public void setValuePairsName(String valuePairsName) {
		this.valuePairsName = valuePairsName;
	}

	/**
	 * The getValue getter method.
	 * 
	 * @return the value
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * The setValue setter method.
	 * 
	 * @param value the value to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InputType [value=" + value + ", valuePairsName=" + valuePairsName + "]";
	}
}
