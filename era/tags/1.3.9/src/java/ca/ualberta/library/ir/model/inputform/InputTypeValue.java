/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: InputTypeValue.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * The InputTypeValue class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlEnum
public enum InputTypeValue {

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
	textarea

	; // close declaration
}
