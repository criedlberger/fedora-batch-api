/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Field.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlElement;
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

	@XmlElement(name = "dc-element")
	private String dcElement;

	@XmlElement(name = "dc-qualifier")
	private String dcQualifier;

	@XmlElement(name = "attribute")
	private Attribute attribute;

	@XmlElement(name = "repeatable")
	private Boolean repeatable;

	/**
	 * Number of repeatable field to display on the form, apply only for
	 * text field only.
	 * Ex: thesis form, Examining Committee Members and their Departments
	 * field (thesis:contributor, role=committeemember)
	 */
	@XmlElement(name = "display")
	private Integer display;

	@XmlElement(name = "label")
	private Label label;

	@XmlElement(name = "indent")
	private Boolean indent;

	@XmlElement(name = "style")
	private String style;

	@XmlElement(name = "input-type", required = true)
	private InputType inputType;

	@XmlElement(name = "hint")
	private Label hint;

	@XmlElement(name = "validate")
	private Validate validate;

	@XmlElement(name = "value")
	private Label value;

	/**
	 * The Field class constructor.
	 */
	public Field() {
		super();
	}

	/**
	 * The Field class constructor.
	 * 
	 * @param dcElement
	 * @param dcQualifier
	 * @param attribute
	 * @param repeatable
	 * @param label
	 * @param indent
	 * @param style
	 * @param inputType
	 * @param hint
	 * @param validate
	 * @param value
	 */
	public Field(String dcElement, String dcQualifier, Attribute attribute, Boolean repeatable, Label label,
		Boolean indent, String style, InputType inputType, Label hint, Validate validate, Label value) {
		super();
		this.dcElement = dcElement;
		this.dcQualifier = dcQualifier;
		this.attribute = attribute;
		this.repeatable = repeatable;
		this.label = label;
		this.indent = indent;
		this.style = style;
		this.inputType = inputType;
		this.hint = hint;
		this.validate = validate;
		this.value = value;
	}

	/**
	 * The Field class constructor.
	 * 
	 * @param dcElement
	 * @param dcQualifier
	 * @param attribute
	 * @param repeatable
	 * @param label
	 * @param indent
	 * @param inputType
	 * @param hint
	 * @param validate
	 * @param value
	 */
	public Field(String dcElement, String dcQualifier, Attribute attribute, Boolean repeatable, Label label,
		Boolean indent, InputType inputType, Label hint, Validate validate, Label value) {
		super();
		this.dcElement = dcElement;
		this.dcQualifier = dcQualifier;
		this.attribute = attribute;
		this.repeatable = repeatable;
		this.label = label;
		this.indent = indent;
		this.inputType = inputType;
		this.hint = hint;
		this.validate = validate;
		this.value = value;
	}

	public String getKey() {
		StringBuilder key = new StringBuilder();
		key.append(this.getDcElement());
		key.append((StringUtils.trimToNull(this.getDcQualifier()) != null ? "." + this.getDcQualifier() : ""));
		key.append((this.getAttribute() != null ? "." + this.getAttribute().getName() : ""));
		key.append((this.getAttribute() != null ? "." + this.getAttribute().getValue() : ""));
		return key.toString();
	}

	/**
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Field field) {
		int result = dcElement.compareTo(field.getDcElement());
		result = result == 0 ? StringUtils.trimToEmpty(dcQualifier).compareTo(
			StringUtils.trimToEmpty(field.getDcQualifier())) : result;
		result = result == 0 ? StringUtils.trimToEmpty(attribute == null ? null : attribute.getName()).compareTo(
			StringUtils.trimToEmpty(field.getAttribute() == null ? null : field.getAttribute().getName())) : result;
		result = result == 0 ? StringUtils.trimToEmpty(attribute == null ? null : attribute.getValue()).compareTo(
			StringUtils.trimToEmpty(field.getAttribute() == null ? null : field.getAttribute().getValue())) : result;
		return result;
	}

	/**
	 * The getDcElement getter method.
	 * 
	 * @return the dcElement
	 */
	public String getDcElement() {
		return dcElement;
	}

	/**
	 * The setDcElement setter method.
	 * 
	 * @param dcElement the dcElement to set
	 */
	public void setDcElement(String dcElement) {
		this.dcElement = dcElement;
	}

	/**
	 * The getDcQualifier getter method.
	 * 
	 * @return the dcQualifier
	 */
	public String getDcQualifier() {
		return dcQualifier;
	}

	/**
	 * The setDcQualifier setter method.
	 * 
	 * @param dcQualifier the dcQualifier to set
	 */
	public void setDcQualifier(String dcQualifier) {
		this.dcQualifier = dcQualifier;
	}

	/**
	 * The getValidate getter method.
	 * 
	 * @return the validate
	 */
	public Validate getValidate() {
		return validate;
	}

	/**
	 * The setValidate setter method.
	 * 
	 * @param validate the validate to set
	 */
	public void setValidate(Validate validate) {
		this.validate = validate;
	}

	/**
	 * The getRepeatable getter method.
	 * 
	 * @return the repeatable
	 */
	public Boolean getRepeatable() {
		return repeatable;
	}

	/**
	 * The setRepeatable setter method.
	 * 
	 * @param repeatable the repeatable to set
	 */
	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	/**
	 * The getInputType getter method.
	 * 
	 * @return the inputType
	 */
	public InputType getInputType() {
		return inputType;
	}

	/**
	 * The setInputType setter method.
	 * 
	 * @param inputType the inputType to set
	 */
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	/**
	 * The getLabel getter method.
	 * 
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * The setLabel setter method.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(Label label) {
		this.label = label;
	}

	/**
	 * The getHint getter method.
	 * 
	 * @return the hint
	 */
	public Label getHint() {
		return hint;
	}

	/**
	 * The setHint setter method.
	 * 
	 * @param hint the hint to set
	 */
	public void setHint(Label hint) {
		this.hint = hint;
	}

	/**
	 * The getIndent getter method.
	 * 
	 * @return the indent
	 */
	public Boolean getIndent() {
		return indent;
	}

	/**
	 * The setIndent setter method.
	 * 
	 * @param indent the indent to set
	 */
	public void setIndent(Boolean indent) {
		this.indent = indent;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Field [attribute=" + attribute + ", dcElement=" + dcElement + ", dcQualifier=" + dcQualifier
			+ ", hint=" + hint + ", indent=" + indent + ", inputType=" + inputType + ", label=" + label
			+ ", repeatable=" + repeatable + ", validate=" + validate + ", value=" + value + "]";
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
	 * The setAttribute setter method.
	 * 
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * The getValue getter method.
	 * 
	 * @return the value
	 */
	public Label getValue() {
		return value;
	}

	/**
	 * The setValue setter method.
	 * 
	 * @param value the value to set
	 */
	public void setValue(Label value) {
		this.value = value;
	}

	/**
	 * The getStyle getter method.
	 * 
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * The setStyle setter method.
	 * 
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * The getDisplay getter method.
	 * 
	 * @return the display
	 */
	public Integer getDisplay() {
		return display;
	}

	/**
	 * The setDisplay setter method.
	 * 
	 * @param display the display to set
	 */
	public void setDisplay(Integer display) {
		this.display = display;
	}
}
