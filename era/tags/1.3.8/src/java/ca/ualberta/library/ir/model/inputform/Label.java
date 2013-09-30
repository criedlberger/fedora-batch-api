/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Label.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * The Label class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Label {

	@XmlAttribute(name = "id")
	private String id;

	@XmlValue
	private String value;

	/**
	 * The Label class constructor.
	 */
	public Label() {
		super();
	}

	/**
	 * The Label class constructor.
	 * 
	 * @param value
	 */
	public Label(String value) {
		super();
		this.value = value;
	}

	/**
	 * The Label class constructor.
	 * 
	 * @param id
	 * @param value
	 */
	public Label(String id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * The getId getter method.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * The setId setter method.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
		return "Label [id=" + id + ", value=" + value + "]";
	}

}
